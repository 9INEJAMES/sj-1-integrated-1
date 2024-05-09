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
            if (data.status == 404) {
                myToast.changeToast(false, 'An error has occurred, the status does not exist.')
                await statusesStore.fetchStatuses()
                return
            }
            const result = await data.json()
            return result
        } catch (error) {
            myToast.changeToast(false, 'An error has occurred, the status does not exist.')
            await statusesStore.fetchStatuses()
            console.log(statusesStore.statuses)

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

            if (response.status / 400 >= 1) {
                myToast.changeToast(false, 'An error has occurred, the status could not be added.')
                await statusesStore.fetchStatuses()
                return
            }
            const result = await response.json()
            myToast.changeToast(true, 'The status has been added.')
            return result
        } catch (error) {
            myToast.changeToast(false, 'An error has occurred, the status could not be added.')
            await statusesStore.fetchStatuses()
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
            if (response.status == 406) {
                myToast.changeToast(false, `An error has occurred, the status can't use duplicated name.`)
                return
            } else if (response.status / 400 >= 1) {
                myToast.changeToast(false, 'An error has occurred, the status does not exist.')
                return
            }
            const updatedStatus = await response.json()
            myToast.changeToast(true, 'The stuatus has been updated.')
            return updatedStatus
        } catch (error) {
            myToast.changeToast(false, 'An error has occurred, the status does not exist.')
            console.error(`Error updating user: ${error}`)
        }
    }

    async function deleteStatus(id) {
        try {
            const response = await fetch(`${url}/${id}`, {
                method: 'DELETE',
            })
            if (response.status / 400 >= 1) {
                myToast.changeToast(false, 'An error has occurred, the status does not exist.')
                await statusesStore.fetchStatuses()
                return
            }
            const deleted = await response.json()
            myToast.changeToast(true, 'The status has been deleted.')
            return deleted
        } catch (error) {
            myToast.changeToast(false, 'An error has occurred, the status does not exist.')
            await statusesStore.fetchStatuses()
            console.error(`Error deleting user: ${error}`)
        }
    }
    async function deleteStatusAndTransfer(id, newId, isMany) {
        try {
            const path = newId ? `${url}/${id}/${newId}` : `${url}/${id}`
            const response = await fetch(path, {
                method: 'DELETE',
            })
            if (response.status / 400 >= 1) {
                myToast.changeToast(false, 'An error has occurred, the status does not exist.')
                await statusesStore.fetchStatuses()
                return
            }
            const deleted = await response.json()
            myToast.changeToast(true, `The task${isMany ? 's' : ''} have been tranferred and the status has been deleted.`)
            return deleted
        } catch (error) {
            myToast.changeToast(false, 'An error has occurred, the status does not exist.')
            await statusesStore.fetchStatuses()
            console.error(`Error deleting user: ${error}`)
        }
    }

    return { getAllStatuses, getStatusById, addStatus, updateStatus, deleteStatus, deleteStatusAndTransfer }
}
