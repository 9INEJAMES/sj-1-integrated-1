import { useToast } from '@/stores/toast'
import { useAuthStore } from '@/stores/auth.js'
import { useRoute, useRouter } from 'vue-router'

export const useBoardApi = () => {
    const authStore = useAuthStore()
    const toastStore = useToast()
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

        const response = await fetch(`${url}${endpoint}`, {
            ...options,
            headers,
        })

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
            const response = await fetchWithToken(`/v3/boards`, {
                method: 'POST',
                body: JSON.stringify({ ...board }),
            })
            if (response.ok) {
                toastStore.changeToast(true, 'The board has been created.')
                return response.json()
            }
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the board could not be created.')
            console.error(`Error adding board: ${error}`)
        }
    }

    async function updateBoard(board) {
        try {
            const response = await fetchWithToken(`/v3/boards/${board.id}`, {
                method: 'PUT',
                body: JSON.stringify({ ...board }),
            })
            if (response.ok) {
                toastStore.changeToast(true, 'The board has been updated.')
                return response.json()
            }
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the board could not be updated.')
            console.error(`Error updating board: ${error}`)
        }
    }

    async function deleteBoard(id) {
        try {
            const response = await fetchWithToken(`/v3/boards/${id}`, {
                method: 'DELETE',
            })
            if (response.ok) {
                toastStore.changeToast(true, 'The board has been deleted.')
                return response.json()
            }
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the board could not be deleted.')
            console.error(`Error deleting board: ${error}`)
        }
    }

    async function getCurrentBoard() {
        try {
            console.log(route.params.bid)
            const response = await fetchWithToken(`/v3/boards/${route.params.bid}`)
            if (response.status == 400) {
                toastStore.changeToast(false, 'Accsess denied, you do not have permission to view this page')
                return
            }
            if (response.ok) return response.json()
        } catch (error) {
            console.error(`Error fetching limit: ${error}`)
        }
    }
    async function getBoardById(bid) {
        try {
            const response = await fetchWithToken(`/v3/boards/${bid}`)
            if (response.status == 400) {
                toastStore.changeToast(false, 'Accsess denied, you do not have permission to view this page')
                return
            }
            if (response.ok) return response.json()
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

            if (board.limit) {
                toastStore.changeToast(true, `The Kanban board now limits ${board.limitMaximumTask} tasks in each status`)
            } else {
                toastStore.changeToast(true, `The Kanban board has disabled the task limit in each status`)
            }

            return response.json()
        } catch (error) {
            toastStore.changeToast(false, 'The update was unsuccessful')
            console.error(`Error updating limit: ${error}`)
        }
    }

    return { getAllBoard, createBoard, deleteBoard, updateBoard, getCurrentBoard, updateBoardLimit, getBoardById }
}
