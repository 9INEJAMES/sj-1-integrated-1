import router from '@/router'
import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'
import VueJwtDecode from 'vue-jwt-decode'
import { useToast } from '@/stores/toast.js'
import { useBoardStore } from '@/stores/board.js'
import { useTasksStore } from '@/stores/task.js'
import { useStatusesStore } from '@/stores/status.js'

export const useAuthStore = defineStore('auth', () => {
    const toastStore = useToast()
    const boardStore = useBoardStore()
    const statusStore = useStatusesStore()
    const taskStore = useTasksStore()
    const isCanEdit = ref(false)
    const accessToken = ref('')
    const refreshToken = ref('')

    const getToken = () => {
        if (!accessToken.value || !refreshToken.value) {
            const auth = JSON.parse(localStorage.getItem('authData'))
            accessToken.value = auth ? auth.access_token : null
            refreshToken.value = auth ? auth.refresh_token : null
        }
        return accessToken.value
    }

    const checkToken = async () => {
        if (!accessToken.value) {
            getToken()
            if (!accessToken.value) {
                logout()
                return
            }
        }
        if (isTokenExpired()) {
            const success = await refreshAccessToken()
            if (!success) {
                toastStore.changeToast(false, 'Your token is expired. Please log in again')
                logout()
            }
        }
    }

    const isTokenExpired = () => {
        getToken()
        const decodedToken = getAuthData()
        if (decodedToken) return decodedToken.exp < Date.now() / 1000
    }

    const refreshAccessToken = async () => {
        try {
            const response = await fetch('/token', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${refreshToken.value}` },
            })
            const data = await response.json()
            if (response.ok && data.access_token) {
                addToken(data.access_token, refreshToken.value) // Keep the same refresh token
                return true
            }
            return false
        } catch (error) {
            console.error('Error refreshing token:', error)
            return false
        }
    }

    const addToken = (newAccessToken, newRefreshToken) => {
        accessToken.value = newAccessToken
        refreshToken.value = newRefreshToken || refreshToken.value // Keep current refresh token if not passed
        const userTokenObject = {
            username: decodeToken(newAccessToken).username,
            access_token: newAccessToken,
            refresh_token: refreshToken.value,
        }
        localStorage.setItem('authData', JSON.stringify(userTokenObject))
        return userTokenObject
    }

    const getAuthData = () => {
        if (!accessToken.value) {
            return null
        } else {
            return decodeToken(accessToken.value)
        }
    }

    const decodeToken = (token) => {
        return VueJwtDecode.decode(token)
    }

    const logout = () => {
        localStorage.removeItem('authData')
        accessToken.value = ''
        refreshToken.value = ''
        router.push('/login')
        boardStore.resetBoards()
        statusStore.resetStatuses()
        taskStore.resetTasks()
    }

    const isOwner = async (bid) => {
        if (boardStore.boards.length === 0) await boardStore.fetchBoard()
        const board = await boardStore.findBoard(bid)
        const authData = await getAuthData()
        if (!authData || !board) return false
        return (await board?.owner.oid) === authData.oid
    }

    return { getAuthData, addToken, getToken, checkToken, isTokenExpired, logout, isOwner, refreshAccessToken }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useAuthStore, import.meta.hot))
}
