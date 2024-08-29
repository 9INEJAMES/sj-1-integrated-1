import router from '@/router'
import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'
import VueJwtDecode from 'vue-jwt-decode'
import { useToast } from '@/stores/toast.js'
import { useTasksStore } from '@/stores/task.js'
import { useStatusesStore } from '@/stores/status.js'
import { useLimitStore } from '@/stores/limitTask'
export const useAuthStore = defineStore('auth', () => {
    const toastStore = useToast()
    const taskStore = useTasksStore()
    const statusStore = useStatusesStore()
    const limitStore = useLimitStore()

    const token = ref('')
    const checkToken = () => {
        if (!token.value) {
            const auth = JSON.parse(localStorage.getItem('authData'))
            token.value = auth ? auth.token : null
            if (!token.value) {
                localStorage.removeItem('authData')
                router.push('/login')
            } else {
                if (isTokenExpired()) {
                    toastStore.changeToast(false, 'Your token is expired. Please log in again')
                    localStorage.removeItem('authData')
                    router.push('/login')
                } else {
                    await taskStore.fetchTasks()
                    await statusStore.fetchStatuses()
                    await limitStore.fetchLimit()
                }
            }
        }
    }
    const isTokenExpired = () => {
        const decodedToken = getAuthData()
        return decodedToken.exp < Date.now() / 1000
    }
    const addToken = (newToken) => {
        token.value = newToken
        const userTokenObject = {
            username: getAuthData().username, // Assuming the token contains a field 'userName'
            token: newToken,
        }
        localStorage.setItem('authData', JSON.stringify(userTokenObject))
        return userTokenObject
    }
    const getToken = () => {
        checkToken()
        return token.value
    }
    const getAuthData = () => {
        checkToken()
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
