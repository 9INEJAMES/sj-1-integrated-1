import { useStatusesStore } from '@/stores/status.js'
import { useToast } from '@/stores/toast'
import { useAuthStore } from '@/stores/auth.js'
import { useRoute, useRouter } from 'vue-router'
import { useTasksStore } from '@/stores/task'

export const useTaskApi = () => {
    const toastStore = useToast()
    const statusesStore = useStatusesStore()
    const tasksStore = useTasksStore()
    const authStore = useAuthStore()
    const url = import.meta.env.VITE_BASE_URL
    const route = useRoute()
    const router = useRouter()

    async function fetchWithToken(endpoint, options = {}) {
        await authStore.checkToken()

        const token = authStore.getToken()

        const headers = options.overrideHeaders || {
            'Content-Type': 'application/json',
            ...options.headers,
        }

        if (token) {
            headers['Authorization'] = `Bearer ${token}`
        }
        toastStore.displayLoading()
        const response = await fetch(`${url}/v3/boards/${endpoint}`, {
            ...options,
            headers,
        })
        await toastStore.resetToast()

        if (!response.ok) {
            tasksStore.fetchTasks(route.params.bid)
        }
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
                toastStore.changeToast('error', 'Error', 'The requested task does not exist')
                router.push({ name: 'notFound' })
            }
            return response.json()
        } catch (error) {
            toastStore.changeToast('error', 'Error', 'The requested task does not exist')
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
                toastStore.changeToast('error', 'Error', `The status ${status.name} will have too many tasks. Please make progress and update the status of existing tasks first.`)
                return
            }
            if (response.status >= 400) {
                toastStore.changeToast('error', 'Error', 'An error has occurred, the task could not be added.')
                return
            }
            if (response.ok) {
                const result = await response.json()
                toastStore.changeToast('success', 'Success', 'The task has been successfully added')
                return result
            }
        } catch (error) {
            toastStore.changeToast('error', 'Error', 'An error has occurred, the task could not be added.')
            console.error(`Error adding task: ${error}`)
        }
    }

    async function updateTask(bid, task, files) {
        try {
            const taskData = { ...task }
            delete taskData.attachments

            await authStore.checkToken()

            const token = authStore.getToken()

            const myHeaders = new Headers()
            myHeaders.append('Authorization', `Bearer ${token}`)

            const formdata = new FormData()

            const json = JSON.stringify(taskData)
            const blob = new Blob([json], {
                type: 'application/json',
            })

            formdata.append('input', blob)
            if (files && files.length > 0) {
                for (let i = 0; i < files.length; i++) {
                    formdata.append('attachmentFiles', files[i], files[i].name)
                }
            }
            const requestOptions = {
                method: 'PUT',
                headers: myHeaders,
                body: formdata,
                redirect: 'follow',
            }

            fetch(`${url}/v3/boards/${bid}/tasks/${task.id}`, requestOptions)
                .then((response) => response.text())
                .then((result) => console.log(result))
                .catch((error) => console.error(error))

            // if (response.ok) {
            //     const updatedTask = await response.json()
            //     toastStore.changeToast("success", "The task has been updated")
            //     return updatedTask
            // } else {
            //     const errorText = await response.text()
            //     console.error("Error details:", errorText)
            //     toastStore.changeToast("error", response.status >= 500 ? "Please make progress and update the status of existing tasks first." : "The update was unsuccessful")
            // }
        } catch (error) {
            toastStore.changeToast('error', 'Error', 'The update was unsuccessful')
            console.error(`Error updating task: ${error}`)
        }
    }
    async function deleteTask(bid, id) {
        try {
            const response = await fetchWithToken(`${bid}/tasks/${id}`, {
                method: 'DELETE',
            })

            if (response.status >= 400) {
                toastStore.changeToast('error', 'Error', 'An error has occurred, the task does not exist.')
                return
            }
            if (response.ok) {
                const deleted = await response.json()
                toastStore.changeToast('success', 'Success', 'The task has been deleted')
                return deleted
            }
        } catch (error) {
            toastStore.changeToast('error', 'Error', 'An error has occurred, the task does not exist.')
            console.error(`Error deleting task: ${error}`)
        }
    }

    return { getAllTasks, getTaskById, addTask, updateTask, deleteTask }
}
