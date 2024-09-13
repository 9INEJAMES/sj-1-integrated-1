import { useToast } from '@/stores/toast'
import { useAuthStore } from '@/stores/auth.js'
import router from '@/router'

export const useBoardApi = () => {
    const authStore = useAuthStore()
    const toastStore = useToast()
    const url = import.meta.env.VITE_BASE_URL

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

        // if (!response.ok) {
        //     throw new Error(`HTTP error! status: ${response.status}`)
        // }
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
            const updatedStatus = await fetchWithToken(`/v3/boards/${board.id}`, {
                method: 'DELETE',
                body: JSON.stringify({ ...board }),
            })
            toastStore.changeToast(true, 'The board has been updated.')
            return updatedStatus.json()
        } catch (error) {
            toastStore.changeToast(false, 'An error has occurred, the board could not be updated.')
            console.error(`Error updating board: ${error}`)
        }
    }

    return { getAllBoard, createBoard,deleteBoard,updateBoard }
}
