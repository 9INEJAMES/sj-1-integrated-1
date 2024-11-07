import { useToast } from "@/stores/toast"
import { useAuthStore } from "@/stores/auth.js"
import { useRoute } from "vue-router"
import { useTasksStore } from "@/stores/task"

export const useAttachmentApi = () => {
    const toastStore = useToast()
    const url = import.meta.env.VITE_BASE_URL
    const authStore = useAuthStore()
    const tasksStore = useTasksStore()

    const route = useRoute()

    async function fetchWithToken(endpoint, options = {}) {
        await authStore.checkToken()
        const token = authStore.getToken()
        const headers = {
            "Content-Type": "application/json",
            ...options.headers,
        }

        if (token) {
            headers["Authorization"] = `Bearer ${token}`
        }

        const response = await fetch(`${url}/v3/boards/${endpoint}`, {
            ...options,
            headers,
        })

        if (response.status === 401) {
            await authStore.refreshAccessToken()
            return fetchWithToken(endpoint, options) // Retry with refreshed token
        }

        // Check if the response is JSON
        const contentType = response.headers.get("content-type")
        if (contentType && contentType.includes("application/json")) {
            return response.json()
        } else {
            return response.text() // Return as text if not JSON
        }
    }

    async function downloadFile(bid, taskId, id, location) {
        try {
            const response = await fetchWithToken(`${bid}/tasks/${taskId}/attachments/${id}`)

            if (!response.ok) {
                throw new Error("File not found")
            }

            const contentDisposition = response.headers.get("Content-Disposition")
            const filename = contentDisposition ? contentDisposition.split("filename=")[1].replace(/"/g, "") : `${location.split("/").pop()}`

            // Convert response to a Blob for downloading
            const blob = await response.blob()
            const url = window.URL.createObjectURL(blob)

            // Trigger file download
            const link = document.createElement("a")
            link.href = url
            link.setAttribute("download", filename) // Set the filename
            document.body.appendChild(link)
            link.click()

            // Clean up
            document.body.removeChild(link)
            window.URL.revokeObjectURL(url)

            return response
        } catch (error) {
            // Display error message
            toastStore.changeToast(false, "An error has occurred, the attachment does not exist.")
            console.error(`Error fetching attachment by ID: ${error}`)
        }
    }



    const deleteAttachmentFromTask = async (bid, taskId, attachmentId) => {
        try {
            const response = await fetchWithToken(`${bid}/tasks/${taskId}/attachments/${attachmentId}`, {
                method: "DELETE",
            })

            if (response.status >= 400) {
                const errorMessage = typeof response === "string" ? response : "An error occurred, the attachment could not be deleted."
                toastStore.changeToast(false, errorMessage)
                return
            }

            if (response.ok) {
                tasksStore.removeAttachmentFromTask(taskId, attachmentId)
                toastStore.changeToast(true, "The attachment has been deleted successfully")
            }
        } catch (error) {
            toastStore.changeToast(false, "An error occurred, the attachment could not be deleted.")
            console.error(`Error deleting attachment: ${error}`)
        }
    }

    return { downloadFile, deleteAttachmentFromTask }
}
