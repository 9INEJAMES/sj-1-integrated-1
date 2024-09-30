import router from '@/router'
import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'
import VueJwtDecode from 'vue-jwt-decode'
import { useToast } from '@/stores/toast.js'
import { useBoardStore } from '@/stores/board.js'
import { useTasksStore } from '@/stores/task.js'
import { useStatusesStore } from '@/stores/status.js'
import { useBoardApi } from '@/composables/board-api'

export const useAuthStore = defineStore('auth', () => {
    const toastStore = useToast()
    const boardStore = useBoardStore()
    const boardApi = useBoardApi()
    const accessToken = ref('')
    const refreshToken = ref('')
    const isLogin = ref(false)
    let currBid = ''

    const getToken = () => {
        const auth = JSON.parse(localStorage.getItem('authData'))
        isLogin.value = auth ? true : false
        accessToken.value = auth ? auth.access_token : null
        refreshToken.value = auth ? auth.refresh_token : null
        return accessToken.value
    }

    const checkToken = async () => {
        getToken()
        if (!accessToken.value) {
            // getToken()
            if (!accessToken.value) {
                logout()
            }
        }
        if (isTokenExpired()) {
            const success = await refreshAccessToken()
            if (!success) {
                toastStore.changeToast(false, 'Your token is expired. Please log in again')
                logout()
            } else {
            }
        }
        return isTokenExpired()
    }

    const isTokenExpired = () => {
        getToken()
        const decodedToken = getAuthData()
        if (decodedToken) return decodedToken.exp < Date.now() / 1000
    }

    const refreshAccessToken = async () => {
        try {
            const response = await fetch(`${import.meta.env.VITE_BASE_URL}/token`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${refreshToken.value}` },
            })
            const data = await response.json()
            if (response.ok && data.access_token) {
                addToken(data.access_token) // Keep the same refresh token
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
        isLogin.value = true
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
        isLogin.value = false
    }

    const isOwner = async (bid) => {
        let isO = false
        if (isLogin.value) {
            const board = await boardApi.getBoardById(bid)
            const authData = await getAuthData()
            if (board?.owner?.oid === authData.oid) isO = true
        }
        console.log('is owner: ', isO)
        return isO
    }

    return { isLogin, getAuthData, addToken, getToken, checkToken, isTokenExpired, logout, isOwner, refreshAccessToken }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useAuthStore, import.meta.hot))
}
