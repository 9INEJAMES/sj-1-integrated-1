import { useStatusesStore } from '../stores/status.js'
import { useToast } from '@/stores/toast'

export const useTaskApi = () => {
    const toastStore = useToast()
    const statusesStore = useStatusesStore()

    const url = import.meta.env.VITE_BASE_URL

    async function fetchWithToken(endpoint, options = {}) {
        const auth = JSON.parse(localStorage.getItem('authData'))
        const token = auth ? auth.token : null

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

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`)
        }

        return response
    }

    async function getAllTasks(filterStatuses) {
        try {
            let filter = ''
            if (filterStatuses && filterStatuses.length > 0) {
                filter = filterStatuses.reduce((acc, status, index) => {
                    const prefix = index === 0 ? '?filterStatuses=' : '&filterStatuses='
                    return acc + prefix + status
                }, '')
            }
            return (await fetchWithToken(`/tasks${filter}`)).json()
        } catch (error) {
            console.error(`Error fetching tasks: ${error}`)
        }
    }

    async function getTaskById(id) {
        try {
            const result = await fetchWithToken(`/tasks/${id}`)
            return result.json()
        } catch (error) {
            toastStore.changeToast(false, 'The requested task does not exist')
            console.error(`Error fetching task by ID: ${error}`)
        }
    }

    async function addTask(task) {
        try {
            const response = await fetchWithToken(`/tasks`, {
                method: 'POST',
                body: JSON.stringify({ ...task }),
            })

            if (response.status >= 500) {
                const status = statusesStore.findStatusById(task.status)
                toastStore.changeToast(false, `The status ${status.name} will have too many tasks. Please make progress and update the status of existing tasks first.`)
                return
            }
            if (response.status >= 400) {
                toastStore.changeToast(false, 'An error has occurred, the task could not be added.')
                return
            }

            const result = await response.json()
            toastStore.changeToast(true, 'The task has been successfully added')
            return result
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the task could not be added.')
            console.error(`Error adding task: ${error}`)
        }
    }

    async function updateTask(task) {
        try {
            const response = await fetchWithToken(`/tasks/${task.id}`, {
                method: 'PUT',
                body: JSON.stringify({ ...task }),
            })

            if (response.status >= 500) {
                const status = statusesStore.findStatusById(task.status)
                toastStore.changeToast(false, `The status ${status.name} will have too many tasks. Please make progress and update the status of existing tasks first.`)
                return
            }
            if (response.status >= 400) {
                toastStore.changeToast(false, 'The update was unsuccessful')
                return
            }

            const updatedTask = await response.json()
            toastStore.changeToast(true, 'The task has been updated')
            return updatedTask
        } catch (error) {
            toastStore.changeToast(false, 'The update was unsuccessful')
            console.error(`Error updating task: ${error}`)
        }
    }

    async function deleteTask(id) {
        try {
            const response = await fetchWithToken(`/tasks/${id}`, {
                method: 'DELETE',
            })

            if (response.status >= 400) {
                toastStore.changeToast(false, 'An error has occurred, the task does not exist.')
                return
            }

            const deleted = await response.json()
            toastStore.changeToast(true, 'The task has been deleted')
            return deleted
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the task does not exist.')
            console.error(`Error deleting task: ${error}`)
        }
    }

    return { getAllTasks, getTaskById, addTask, updateTask, deleteTask }
}
