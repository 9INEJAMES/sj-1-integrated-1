import { useToast } from '@/stores/toast'
import { useRouter } from 'vue-router'
import { useStatusesStore } from '@/stores/status'

export const useStatusApi = () => {
    const myToast = useToast()
    const router = useRouter()
    const statusesStore = useStatusesStore()
    const url = import.meta.env.VITE_BASE_STATUS_URL

    async function getAllStatuses() {
        try {
            const data = await fetch(`${url}`)
            const result = await data.json()
            return result
        } catch (error) {
            console.log(`error: ${error}`)
        }
    }

    async function getStatusById(id) {
        try {
            const data = await fetch(`${url}/${id}`)
            const result = await data.json()
            if (data.status == 404) {
                myToast.changeToast(false, 'The requested status does not exist')
                // router.push({ name: 'home' })
                return
            }
            return result
        } catch (error) {
            myToast.changeToast(false, 'The requested status does not exist')
            console.log(`error: ${error}`)
        }
    }

    async function addStatus(obj) {
        try {
            const response = await fetch(`${url}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ ...obj }),
            })
            const result = await response.json()
            myToast.changeToast(true, 'The status has been successfully added')
            return result
        } catch (error) {
            console.error(`Error adding user: ${error}`)
        }
    }

    async function updateStatus(obj) {
        try {
            const response = await fetch(`${url}/${obj.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ ...obj }),
            })
            if (response.status == 404) {
                myToast.changeToast(false, 'The update was unsuccesful')
                return
            }
            const updatedStatus = await response.json()
            myToast.changeToast(true, 'The stuatus has been updated')
            return updatedStatus
        } catch (error) {
            myToast.changeToast(false, 'The update was unsuccesful')
            console.error(`Error updating user: ${error}`)
        }
    }

    async function deleteStatus(id, newId) {
        try {
            const path = newId ? `${url}/${id}/${newId}` : `${url}/${id}`
            const response = await fetch(path, {
                method: 'DELETE',
            })
            if (response.status == 404) {
                myToast.changeToast(false, 'An error has occurred, the status does not exist.')
                statusesStore.fetchStatuses()
                console.log(statusesStore.tasks)
                return
            }
            const deleted = await response.json()
            myToast.changeToast(true, 'The status has been deleted')
            return deleted
        } catch (error) {
            myToast.changeToast(false, 'An error has occurred, the status does not exist.')
            statusesStore.fetchStatuses()
            console.error(`Error deleting user: ${error}`)
        }
    }

    return { getAllStatuses, getStatusById, addStatus, updateStatus, deleteStatus }
}
