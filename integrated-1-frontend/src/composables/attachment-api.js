import { useToast } from "@/stores/toast"
import { useAuthStore } from "@/stores/auth.js"
import router from "@/router"
import { useRoute } from "vue-router"

export const useAttachmentApi = () => {
    const toastStore = useToast()
    const url = import.meta.env.VITE_BASE_URL
    const authStore = useAuthStore()
    const route = useRoute()
    let filename;

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

        if (response.status == 401) {
            authStore.refreshAccessToken()
        }
        return response
    }

    async function downloadFile(bid, taskId, id , location) {
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

    async function updateTaskWithAttachments(boardId, taskId, taskData, attachmentFiles) {
        const url = `https://your-backend-url/v3/boards/${boardId}/tasks/${taskId}`

        // Prepare headers with the authorization token
        const headers = new Headers()
        headers.append("Authorization", "Bearer your-token-here")

        // Create a FormData object to handle both JSON and file data
        const formData = new FormData()

        // Append task data fields (as JSON) if any
        if (taskData) {
            formData.append("input", new Blob([JSON.stringify(taskData)], { type: "application/json" }))
        }

        // Append each attachment file
        if (attachmentFiles && attachmentFiles.length > 0) {
            attachmentFiles.forEach((file) => {
                formData.append("attachmentFiles", file)
            })
        }

        try {
            const response = await fetch(url, {
                method: "PUT",
                headers: headers,
                body: formData,
            })

            if (!response.ok) {
                throw new Error(`Failed to update task: ${response.statusText}`)
            }

            const result = await response.json()
            console.log("Task updated successfully:", result)
            return result // Return the result or handle it in your frontend
        } catch (error) {
            console.error("Error updating task:", error)
        }
    }



    

    return { downloadFile, updateTaskWithAttachments }
}
