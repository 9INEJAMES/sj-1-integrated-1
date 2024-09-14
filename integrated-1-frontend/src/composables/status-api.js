import { useToast } from '@/stores/toast'
import { useAuthStore } from '@/stores/auth.js'
import router from '@/router'
import { useRoute } from 'vue-router'

export const useStatusApi = () => {
    const toastStore = useToast()
    const url = import.meta.env.VITE_BASE_URL
    const authStore = useAuthStore()
    const route = useRoute()

    async function fetchWithToken(endpoint, options = {}) {
        const token = authStore.getToken()

        const headers = {
            'Content-Type': 'application/json',
            ...options.headers,
        }

        if (token) {
            headers['Authorization'] = `Bearer ${token}`
        }

        const response = await fetch(`${url}/v3/boards/${route.params.bid}${endpoint}`, {
            ...options,
            headers,
        })

        if (response.status === 401) {
            toastStore.changeToast(false, 'Your token is expired. Please log in again')
            localStorage.removeItem('authData')
            router.push('/login')
        }
        return response
    }

    async function getAllStatuses() {
        try {
            return (await fetchWithToken(`/statuses`)).json()
        } catch (error) {
            console.error(`Error fetching statuses: ${error}`)
        }
    }

    async function getStatusById(id) {
        try {
            return (await fetchWithToken(`/statuses/${id}`)).json()
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the status does not exist.')
            console.error(`Error fetching status by ID: ${error}`)
        }
    }

    async function addStatus(status) {
        try {
            const result = await fetchWithToken(`/statuses`, {
                method: 'POST',
                body: JSON.stringify({ ...status }),
            })
            if (result.status >= 400) {
                toastStore.changeToast(false, 'An error has occurred, the status could not be added.')
                return
            }
            toastStore.changeToast(true, 'The status has been added.')
            return result.json()
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the status could not be added.')
            console.error(`Error adding status: ${error}`)
        }
    }

    async function updateStatus(status) {
        try {
            const updatedStatus = await fetchWithToken(`/statuses/${status.id}`, {
                method: 'PUT',
                body: JSON.stringify({ ...status }),
            })
            toastStore.changeToast(true, 'The status has been updated.')
            return updatedStatus.json()
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the status could not be updated.')
            console.error(`Error updating status: ${error}`)
        }
    }

    async function deleteStatus(id) {
        try {
            const deleted = await fetchWithToken(`/statuses/${id}`, {
                method: 'DELETE',
            })
            toastStore.changeToast(true, 'The status has been deleted.')
            return deleted.json()
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the status does not exist.')
            console.error(`Error deleting status: ${error}`)
        }
    }

    async function deleteStatusAndTransfer(id, newStatus, tasks) {
        try {
            const deleted = await fetchWithToken(`/statuses/${id}/${newStatus.id}`, {
                method: 'DELETE',
            })
            toastStore.changeToast(true, `${tasks} task${tasks > 1 ? 's' : ''} have been transferred and the status has been deleted.`)
            return deleted.json()
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the status does not exist.')
            console.error(`Error deleting status and transferring tasks: ${error}`)
        }
    }

    return { getAllStatuses, getStatusById, addStatus, updateStatus, deleteStatus, deleteStatusAndTransfer }
}
