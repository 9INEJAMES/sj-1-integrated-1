import router from '@/router'
import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'
import VueJwtDecode from 'vue-jwt-decode'
import { useToast } from '@/stores/toast.js'
export const useAuthStore = defineStore('auth', () => {
    const toastStore = useToast()

    const token = ref('')
    const checkToken = async () => {
        if (!token.value) {
            getToken()
            if (!token.value) {
                localStorage.removeItem('authData')
                router.push('/login')
            }
        }
        if (isTokenExpired()) {
            toastStore.changeToast(false, 'Your token is expired. Please log in again')
            localStorage.removeItem('authData')
            console.log('expired')
            router.push('/login')
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
        const auth = JSON.parse(localStorage.getItem('authData'))
        token.value = auth ? auth.token : null
        return token.value
    }
    const getAuthData = () => {
        if (!token.value) {
            return null
        } else {
            return VueJwtDecode.decode(token.value)
        }
    }
    // Return the store properties and methods
    return { getAuthData, addToken, getToken, checkToken, isTokenExpired }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useAuthStore, import.meta.hot))
}
