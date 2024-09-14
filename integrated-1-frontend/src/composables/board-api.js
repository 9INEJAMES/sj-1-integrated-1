import { useToast } from '@/stores/toast'
import { useAuthStore } from '@/stores/auth.js'
import router from '@/router'
import { useBoardStore } from '@/stores/board'
import { useRoute } from 'vue-router'

export const useBoardApi = () => {
    const authStore = useAuthStore()
    const toastStore = useToast()
    const url = import.meta.env.VITE_BASE_URL
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

        const response = await fetch(`${url}${endpoint}`, {
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

    async function getAllBoard() {
        try {
            return (await fetchWithToken(`/v3/boards`)).json()
        } catch (error) {
            console.error(`Error fetching boards: ${error}`)
        }
    }

    async function createBoard(board) {
        try {
            const result = await fetchWithToken(`/v3/boards`, {
                method: 'POST',
                body: JSON.stringify({ ...board }),
            })
            toastStore.changeToast(true, 'The board has been created.')
            return result.json()
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the board could not be created.')
            console.error(`Error adding board: ${error}`)
        }
    }

    async function updateBoard(board) {
        try {
            const updatedStatus = await fetchWithToken(`/v3/boards/${board.id}`, {
                method: 'PUT',
                body: JSON.stringify({ ...board }),
            })
            toastStore.changeToast(true, 'The board has been updated.')
            return updatedStatus.json()
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the board could not be updated.')
            console.error(`Error updating board: ${error}`)
        }
    }

    async function deleteBoard(id) {
        try {
            const response = await fetchWithToken(`/v3/boards/${id}`, {
                method: "DELETE",
            })
            if (response.ok) {
                toastStore.changeToast(true, "The board has been deleted.")
                return response.json()
            } else {
                toastStore.changeToast(false, "An error has occurred, the board could not be deleted.")
                console.error(`Error deleting board: ${response.statusText}`)
            }
        } catch (error) {
            toastStore.changeToast(false, "An error has occurred, the board could not be deleted.")
            console.error(`Error deleting board: ${error}`)
        }
    }

    async function getCurrentBoard() {
        try {
            return (await fetchWithToken(`/v3/boards/${route.params.bid}`)).json()
        } catch (error) {
            console.error(`Error fetching limit: ${error}`)
        }
    }
    
    async function updateBoardLimit(board) {
        try {
            const response = await fetchWithToken(`/v3/boards/${route.params.bid}`, {
                method: 'PUT',
                body: JSON.stringify({ ...obj }),
            })

            if (response.status >= 400) {
                toastStore.changeToast(false, 'The update was unsuccessful')
                return
            }

            const result = await response.json()

            if (board.limit) {
                toastStore.changeToast(true, `The Kanban board now limits ${board.limitMaximumTask} tasks in each status`)
            } else {
                toastStore.changeToast(true, `The Kanban board has disabled the task limit in each status`)
            }

            return result
        } catch (error) {
            toastStore.changeToast(false, 'The update was unsuccessful')
            console.error(`Error updating limit: ${error}`)
        }
    }

    return { getAllBoard, createBoard, deleteBoard, updateBoard, getCurrentBoard, updateBoardLimit }
}
