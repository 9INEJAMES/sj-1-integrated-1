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

    return { getAllCollabBoard }
}
