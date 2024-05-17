import { useToast } from '@/stores/toast'
import { useRouter } from 'vue-router'
import { useStatusesStore } from '../stores/status.js'

export const useStatusApi = () => {
    const toastStore = useToast()
    const router = useRouter()
    const statusesStore = useStatusesStore()

    const url = import.meta.env.VITE_BASE_URL

    async function getAllStatuses() {
        try {
            const data = await fetch(`${url}/statuses`)
            const result = await data.json()
            return result
        } catch (error) {
            console.log(`error: ${error}`)
        }
    }

    async function getStatusById(id) {
        try {
            const data = await fetch(`${url}/statuses/${id}`)
            if (data.status == 404) {
                toastStore.changeToast(false, 'An error has occurred, the status does not exist.')
                return
            }
            const result = await data.json()
            return result
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the status does not exist.')
            console.log(`error: ${error}`)
        }
    }

    async function addStatus(status) {
        try {
            const response = await fetch(`${url}/statuses`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ ...status }),
            })

            if (response.status / 400 >= 1) {
                toastStore.changeToast(false, 'An error has occurred, the status could not be added.')
                return
            }
            const result = await response.json()
            toastStore.changeToast(true, 'The status has been added.')
            return result
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the status could not be added.')

            console.error(`Error adding status: ${error}`)
        }
    }

    async function updateStatus(status) {
        try {
            const response = await fetch(`${url}/statuses/${status.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ ...status }),
            })
            if (response.status == 500) {
                // toastStore.changeToast(false, `An error has occurred, the status can't use duplicated name.`)
                // return
            }
            if (response.status / 400 >= 1) {
                toastStore.changeToast(false, 'An error has occurred, the status does not exist.')
                return
            }
            const updatedStatus = await response.json()
            toastStore.changeToast(true, 'The status has been updated.')
            return updatedStatus
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the status does not exist.')
            console.error(`Error updating status: ${error}`)
        }
    }

    async function deleteStatus(id) {
        try {
            const response = await fetch(`${url}/statuses/${id}`, {
                method: 'DELETE',
            })
            if (response.status / 400 >= 1) {
                toastStore.changeToast(false, 'An error has occurred, the status does not exist.')
                return
            }
            const deleted = await response.json()
            toastStore.changeToast(true, 'The status has been deleted.')
            return deleted
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the status does not exist.')

            console.error(`Error deleting status: ${error}`)
        }
    }
    async function deleteStatusAndTransfer(id, newStatus, tasks) {
        try {
            const response = await fetch(`${url}/statuses/${id}/${newStatus.id}`, {
                method: 'DELETE',
            })
            if (response.status / 500 >= 1) {
                toastStore.changeToast(false, `Cannot transfer to ${newStatus.name} status since it will exceed the limit, Please choose another status to transfer to.`)
                return
            }
            if (response.status / 400 >= 1) {
                toastStore.changeToast(false, 'An error has occurred, the status does not exist.')
                return
            }
            const deleted = await response.json()
            toastStore.changeToast(true, `${tasks} task${tasks > 1 ? 's' : ''} have been tranferred and the status has been deleted.`)
            return deleted
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the status does not exist.')
            console.error(`Error deleting status: ${error}`)
        }
    }

    return { getAllStatuses, getStatusById, addStatus, updateStatus, deleteStatus, deleteStatusAndTransfer }
}
