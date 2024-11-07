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



    

    return { downloadFile}
}
