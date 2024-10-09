import { useRoute, useRouter } from "vue-router"
import { useToast } from "@/stores/toast.js"
import { useAuthStore } from "@/stores/auth.js"

export const useCollabApi = () => {
    const authStore = useAuthStore()
    const toastStore = useToast()
    const route = useRoute()
    const router = useRouter()
    const url = import.meta.env.VITE_BASE_URL

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

        const response = await fetch(`${url}${endpoint}`, {
            ...options,
            headers,
        })

        return response
    }

    async function getAllCollabBoard() {
        try {
            return (await fetchWithToken(`/v3/boards/`)).json()
        } catch (error) {
            console.error(`Error fetching boards: ${error}`)
        }
    }

    async function deleteCollabBoard(bid, oid) {
        try {
            const response = await fetchWithToken(`/v3/boards/${bid}/collabs/${oid}`, {
                method: "DELETE",
            })
            if (response.ok) {
                toastStore.changeToast(true, "The collab board has been deleted.")
                return response.json()
            }
        } catch (error) {
            toastStore.changeToast(false, "An error has occurred, the collab board could not be deleted.")
            console.error(`Error deleting board: ${error}`)
        }
    }

    async function getAllCollaborator(){
        try {
            return (await fetchWithToken(`/v3/boards/${route.params.bid}/collabs`)).json()
        } catch (error) {
            console.error(`Error fetching collaborators: ${error}`)
        }
    }
    async function addCollaborator(newCollaborator){
        try {
            const response = await fetchWithToken(`/v3/boards/${route.params.bid}/collabs`, {
                method: "POST",
                body: JSON.stringify({ ...newCollaborator }),
            })
            if (response.ok) {
                toastStore.changeToast(true, "The collaborator has been added.")
                return response.json()
            }
        } catch (error) {
            toastStore.changeToast(false, "An error has occurred, the collaborator could not be added.")
            console.error(`Error adding collaborator: ${error}`)
        }
    }

    async function deleteCollaborator(bid, oid) {
        try {
            const response = await fetchWithToken(`/v3/boards/${bid}/collabs/${oid}`, {
                method: "DELETE",
            })
            if (response.ok) {
                toastStore.changeToast(true, "The collaborator has been deleted.")
                return { success: true, data: await response.json() } // Return success with data
            } else {
                toastStore.changeToast(false, "Failed to delete the collaborator.")
                return { success: false }
            }
        } catch (error) {
            toastStore.changeToast(false, "An error has occurred, the collaborator could not be deleted.")
            console.error(`Error deleting collaborator: ${error}`)
            return { success: false, error } // Return false with error info
        }
    }




    

    return { getAllCollabBoard, deleteCollabBoard, getAllCollaborator, addCollaborator, deleteCollaborator }
}
