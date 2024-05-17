import { useStatusesStore } from '../stores/status.js'
import { useToast } from '@/stores/toast'

export const useTaskApi = () => {
    const myToast = useToast()
    const statusesStore = useStatusesStore()

    const url = import.meta.env.VITE_BASE_URL

    async function getAllTasks(filterStatuses) {
        try {
            let filter = ''
            if (filterStatuses && filterStatuses.length > 0) {
                filter = filterStatuses.reduce((acc, status, index) => {
                    const prefix = index === 0 ? '?filterStatuses=' : '&filterStatuses='
                    return acc + prefix + status
                }, '')
            }
            const data = await fetch(`${url}/tasks${filter}`)
            const result = await data.json()
            return result
        } catch (error) {
            console.log(`error: ${error}`)
        }
    }

    async function getTaskById(id) {
        try {
            const data = await fetch(`${url}/tasks/${id}`)
            if (data.status / 400 >= 1) {
                myToast.changeToast(false, 'The requested task does not exist')
                return
            }
            const result = await data.json()
            return result
        } catch (error) {
            myToast.changeToast(false, 'The requested task does not exist')
            console.log(`error: ${error}`)
        }
    }

    async function addTask(task) {
        try {
            const response = await fetch(`${url}/tasks`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ ...task }),
            })
            if (response.status / 500 >= 1) {
                const status = statusesStore.findStatusById(task.status)
                myToast.changeToast(false, `The status ${status.name} will have too many tasks. Please make progress and update status of existing tasks first.`)
                return
            }
            if (response.status / 400 >= 1) {
                myToast.changeToast(false, 'An error has occurred, the task could not be added.')
                return
            }
            const result = await response.json()
            myToast.changeToast(true, 'The task has been successfully added')
            return result
        } catch (error) {
            console.error(`Error adding task: ${error}`)
        }
    }

    async function updateTask(task) {
        try {
            const response = await fetch(`${url}/tasks/${obj.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ ...task }),
            })
            if (response.status / 400 >= 1) {
                myToast.changeToast(false, 'The update was unsuccesful')
                return
            }
            const updatedTask = await response.json()
            myToast.changeToast(true, 'The task has been updated')
            return updatedTask
        } catch (error) {
            myToast.changeToast(false, 'The update was unsuccesful')
            console.error(`Error updating task: ${error}`)
        }
    }

    async function deleteTask(id) {
        try {
            const response = await fetch(`${url}/tasks/${id}`, {
                method: 'DELETE',
            })
            if (response.status / 400 >= 1) {
                myToast.changeToast(false, 'An error has occurred, the task does not exist.')
                return
            }
            const deleted = await response.json()
            myToast.changeToast(true, 'The task has been deleted')
            return deleted
        } catch (error) {
            myToast.changeToast(false, 'An error has occurred, the task does not exist.')
            console.error(`Error deleting task: ${error}`)
        }
    }

    return { getAllTasks, getTaskById, addTask, updateTask, deleteTask }
}
