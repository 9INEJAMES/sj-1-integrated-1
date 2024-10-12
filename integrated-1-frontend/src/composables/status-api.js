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
        await authStore.checkToken()

        const token = authStore.getToken()

        const headers = {
            'Content-Type': 'application/json',
            ...options.headers,
        }

        if (token) {
            headers['Authorization'] = `Bearer ${token}`
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

    async function getAllStatuses(bid) {
        try {
            return (await fetchWithToken(`${bid}/statuses`)).json()
        } catch (error) {
            console.error(`Error fetching statuses: ${error}`)
        }
    }

    async function getStatusById(bid, id) {
        try {
            return (await fetchWithToken(`${bid}/statuses/${id}`)).json()
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the status does not exist.')
            console.error(`Error fetching status by ID: ${error}`)
        }
    }

    async function addStatus(bid, status) {
        try {
            const response = await fetchWithToken(`${bid}/statuses`, {
                method: 'POST',
                body: JSON.stringify({ ...status }),
            })
            // if (response.status >= 400) {
            //     toastStore.changeToast(false, 'An error has occurred, the status could not be added.')
            //     return
            // }
            if (response.ok) {
                toastStore.changeToast(true, 'The status has been added.')
                return response.json()
            }
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the status could not be added.')
            console.error(`Error adding status: ${error}`)
        }
    }

    async function updateStatus(bid, status) {
        try {
            const response = await fetchWithToken(`${bid}/statuses/${status.id}`, {
                method: 'PUT',
                body: JSON.stringify({ ...status }),
            })
            if (response.ok) {
                toastStore.changeToast(true, 'The status has been updated.')
                return response.json()
            }
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the status could not be updated.')
            console.error(`Error updating status: ${error}`)
        }
    }

    async function deleteStatus(bid, id) {
        try {
            const response = await fetchWithToken(`${bid}/statuses/${id}`, {
                method: 'DELETE',
            })
            if (response.ok) {
                toastStore.changeToast(true, 'The status has been deleted.')
                return response.json()
            }
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the status does not exist.')
            console.error(`Error deleting status: ${error}`)
        }
    }

    async function deleteStatusAndTransfer(bid, id, newStatus, tasks) {
        try {
            const response = await fetchWithToken(`${bid}/statuses/${id}/${newStatus.id}`, {
                method: 'DELETE',
            })
            if (response.ok) {
                toastStore.changeToast(true, `${tasks} task${tasks > 1 ? 's' : ''} have been transferred and the status has been deleted.`)
                return response.json()
            } else {
                toastStore.changeToast(false, 'can not move all taks to new status because its over limit.')
            }
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the status does not exist.')
            console.error(`Error deleting status and transferring tasks: ${error}`)
        }
    }

    return { getAllStatuses, getStatusById, addStatus, updateStatus, deleteStatus, deleteStatusAndTransfer }
}
