import { useStatusesStore } from '@/stores/status.js'
import { useToast } from '@/stores/toast'
import { useAuthStore } from '@/stores/auth.js'
import { useRoute, useRouter } from 'vue-router'

export const useTaskApi = () => {
    const toastStore = useToast()
    const statusesStore = useStatusesStore()
    const authStore = useAuthStore()
    const url = import.meta.env.VITE_BASE_URL
    const route = useRoute()
    const router = useRouter()

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

    async function getAllTasks(bid, filterStatuses) {
        try {
            let filter = ''
            if (filterStatuses && filterStatuses.length > 0) {
                filter = filterStatuses.reduce((acc, status, index) => {
                    const prefix = index === 0 ? '?filterStatuses=' : '&filterStatuses='
                    return acc + prefix + status
                }, '')
            }
            const response = await fetchWithToken(`${bid}/tasks${filter}`)
            return response.json()
        } catch (error) {
            console.error(`Error fetching tasks: ${error}`)
        }
    }

    async function getTaskById(bid, id) {
        try {
            const response = await fetchWithToken(`${bid}/tasks/${id}`)
            if (response.status >= 400) {
                toastStore.changeToast(false, 'The requested task does not exist')
                router.push({ name: 'notFound' })
                return
            }
            return response.json()
        } catch (error) {
            toastStore.changeToast(false, 'The requested task does not exist')
            console.error(`Error fetching task by ID: ${error}`)
        }
    }

    async function addTask(bid, task) {
        try {
            const response = await fetchWithToken(`${bid}/tasks`, {
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
            if (response.ok) {
                const result = await response.json()
                toastStore.changeToast(true, 'The task has been successfully added')
                return result
            }
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the task could not be added.')
            console.error(`Error adding task: ${error}`)
        }
    }

    async function updateTask(bid, task) {
        try {
            const response = await fetchWithToken(`${bid}/tasks/${task.id}`, {
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
            if (response.ok) {
                const updatedTask = await response.json()
                toastStore.changeToast(true, 'The task has been updated')
                return updatedTask
            }
        } catch (error) {
            toastStore.changeToast(false, 'The update was unsuccessful')
            console.error(`Error updating task: ${error}`)
        }
    }

    async function deleteTask(bid, id) {
        try {
            const response = await fetchWithToken(`${bid}/tasks/${id}`, {
                method: 'DELETE',
            })

            if (response.status >= 400) {
                toastStore.changeToast(false, 'An error has occurred, the task does not exist.')
                return
            }
            if (response.ok) {
                const deleted = await response.json()
                toastStore.changeToast(true, 'The task has been deleted')
                return deleted
            }
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the task does not exist.')
            console.error(`Error deleting task: ${error}`)
        }
    }

    return { getAllTasks, getTaskById, addTask, updateTask, deleteTask }
}
