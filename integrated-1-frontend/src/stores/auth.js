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

    const token = ref('')
    const checkToken = async () => {
        if (!token.value) {
            getToken()
            if (!token.value) {
                logout()
            }
        }
        if (isTokenExpired()) {
            toastStore.changeToast(false, 'Your token is expired. Please log in again')
            logout()
        }
    }
    const isTokenExpired = () => {
        const decodedToken = getAuthData()
        if (decodedToken) return decodedToken.exp < Date.now() / 1000
    }
    const addToken = (newToken) => {
        token.value = newToken
        const userTokenObject = {
            username: getAuthData().username,
            token: newToken,
        }
        localStorage.setItem('authData', JSON.stringify(userTokenObject))
        return userTokenObject
    }
    const getToken = () => {
        if (!token.value) {
            const auth = JSON.parse(localStorage.getItem('authData'))
            token.value = auth ? auth.token : null
        }
        return token.value
    }
    const getAuthData = () => {
        if (!token.value) {
            return null
        } else {
            return VueJwtDecode.decode(token.value)
        }
    }
    const logout = () => {
        localStorage.removeItem('authData')
        token.value = ''
        router.push('/login')
        boardStore.resetBoards()
        statusStore.resetStatuses()
        taskStore.resetTasks()
    }
    // Return the store properties and methods
    return { getAuthData, addToken, getToken, checkToken, isTokenExpired, logout }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useAuthStore, import.meta.hot))
}
