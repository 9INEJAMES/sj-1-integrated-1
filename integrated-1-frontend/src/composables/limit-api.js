import { useToast } from '@/stores/toast'
import { useAuthStore } from '@/stores/auth.js'
import router from '@/router'

export const useLimitApi = () => {
    const authStore = useAuthStore()
    const toastStore = useToast()
    const url = import.meta.env.VITE_BASE_URL

    const checkTokenExpired = (response) => {
        if (response.status === 401) {
            toastStore.changeToast(false, 'Your token is expired. Please log in again')
            localStorage.removeItem('authData')
            router.push('/login')
        }
    }
    async function fetchWithToken(endpoint, options = {}) {
        const token = authStore.getToken()

        const headers = {
            'Content-Type': 'application/json',
            ...options.headers,
        }

        if (token) {
            headers['Authorization'] = `Bearer ${token}`
        }

        const response = await fetch(`${url}${endpoint}`, {
            ...options,
            headers,
        })
        if (response.status === 401) {
            toastStore.changeToast(false, 'Your token is expired. Please log in again')
            localStorage.removeItem('authData')
            router.push('/login')
        }

        // if (!response.ok) {
        //     throw new Error(`HTTP error! status: ${response.status}`)
        // }
        return response
    }

    async function getLimit() {
        try {
            return (await fetchWithToken(`/limit`)).json()
        } catch (error) {
            console.error(`Error fetching limit: ${error}`)
        }
    }

    async function updateLimit(obj) {
        try {
            const response = await fetchWithToken(`/limit`, {
                method: 'PUT',
                body: JSON.stringify({ ...obj }),
            })

            if (response.status >= 400) {
                toastStore.changeToast(false, 'The update was unsuccessful')
                return
            }

            const result = await response.json()

            if (obj.limit) {
                toastStore.changeToast(true, `The Kanban board now limits ${obj.limitMaximumTask} tasks in each status`)
            } else {
                toastStore.changeToast(true, `The Kanban board has disabled the task limit in each status`)
            }

            return result
        } catch (error) {
            toastStore.changeToast(false, 'The update was unsuccessful')
            console.error(`Error updating limit: ${error}`)
        }
    }

    return { getLimit, updateLimit }
}
