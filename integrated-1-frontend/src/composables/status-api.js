import { useToast } from '@/stores/toast'

export const useStatusApi = () => {
    const myToast = useToast()
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
                myToast.changeToast(false, 'An error has occurred, the status does not exist.')
                return
            }
            const result = await data.json()
            return result
        } catch (error) {
            myToast.changeToast(false, 'An error has occurred, the status does not exist.')
            console.log(`error: ${error}`)
        }
    }

    async function addStatus(obj) {
        try {
            const response = await fetch(`${url}/statuses`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ ...obj }),
            })

            if (response.status / 400 >= 1) {
                myToast.changeToast(false, 'An error has occurred, the status could not be added.')

                return
            }
            const result = await response.json()
            myToast.changeToast(true, 'The status has been added.')
            return result
        } catch (error) {
            myToast.changeToast(false, 'An error has occurred, the status could not be added.')

            console.error(`Error adding status: ${error}`)
        }
    }

    async function updateStatus(obj) {
        try {
            const response = await fetch(`${url}/statuses/${obj.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ ...obj }),
            })
            if (response.status == 500) {
                // myToast.changeToast(false, `An error has occurred, the status can't use duplicated name.`)
                // return
            }
            if (response.status / 400 >= 1) {
                myToast.changeToast(false, 'An error has occurred, the status does not exist.')
                return
            }
            const updatedStatus = await response.json()
            myToast.changeToast(true, 'The status has been updated.')
            return updatedStatus
        } catch (error) {
            myToast.changeToast(false, 'An error has occurred, the status does not exist.')
            console.error(`Error updating status: ${error}`)
        }
    }

    async function deleteStatus(id) {
        try {
            const response = await fetch(`${url}/statuses/${id}`, {
                method: 'DELETE',
            })
            if (response.status / 400 >= 1) {
                myToast.changeToast(false, 'An error has occurred, the status does not exist.')
                return
            }
            const deleted = await response.json()
            myToast.changeToast(true, 'The status has been deleted.')
            return deleted
        } catch (error) {
            myToast.changeToast(false, 'An error has occurred, the status does not exist.')

            console.error(`Error deleting status: ${error}`)
        }
    }
    async function deleteStatusAndTransfer(id, newId, tasks) {
        try {
            const response = await fetch(`${url}/statuses/${id}/${newId}`, {
                method: 'DELETE',
            })
            if (response.status / 400 >= 1) {
                myToast.changeToast(false, 'An error has occurred, the status does not exist.')
                return
            }
            const deleted = await response.json()
            myToast.changeToast(true, `${tasks} task${tasks > 1 ? 's' : ''} have been tranferred and the status has been deleted.`)
            return deleted
        } catch (error) {
            myToast.changeToast(false, 'An error has occurred, the status does not exist.')
            console.error(`Error deleting status: ${error}`)
        }
    }

    return { getAllStatuses, getStatusById, addStatus, updateStatus, deleteStatus, deleteStatusAndTransfer }
}