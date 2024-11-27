import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'
import VueJwtDecode from 'vue-jwt-decode'
import { useToast } from '@/stores/toast.js'
import { useBoardApi } from '@/composables/board-api'
import { msalInstance, graphScopes, state } from '@/configs/msalConfig'

export const useAuthStore = defineStore('auth', () => {
    const toastStore = useToast()
    const boardApi = useBoardApi()
    const accessToken = ref('')
    const refreshToken = ref('')
    const isLogin = ref(false)
    const typeLogin = ref('')

    const getToken = () => {
        const auth = JSON.parse(localStorage.getItem('authData'))
        isLogin.value = auth ? true : false
        typeLogin.value = auth ? auth.typeLogin : ''
        accessToken.value = auth ? auth.access_token : null
        refreshToken.value = auth ? auth.refresh_token : null
        return accessToken.value
    }

    const checkToken = async () => {
        getToken()
        if (!accessToken.value) {
            if (refreshToken.value) await refreshAccessToken()
            // getToken()
            if (!accessToken.value) logout()
        }
        if (isTokenExpired()) {
            const success = await refreshAccessToken()
            if (!success) {
                toastStore.changeToast('error', 'Error', 'Your token is expired. Please log in again')
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
            getToken() // test
            const response = await fetch(`${import.meta.env.VITE_BASE_URL}/token`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${refreshToken.value}`,
                    'Auth-Type': typeLogin.value || undefined,
                },
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

    const addToken = (newAccessToken, newRefreshToken, type) => {
        accessToken.value = newAccessToken
        refreshToken.value = newRefreshToken || refreshToken.value // Keep current refresh token if not passed
        const userTokenObject = {
            username: decodeToken(newAccessToken).username,
            access_token: newAccessToken,
            refresh_token: refreshToken.value,
            type: type,
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
        typeLogin.value = ''
        // isLogin.value = false
    }

    const isEditor = async (bid) => {
        let isCol = false
        if (isLogin.value) {
            const access = await boardApi.getAccess(bid)
            if (access?.accessRight == 'WRITE') isCol = true
        }
        return isCol
    }

    const isOwner = async (bid) => {
        let isOwn = false
        if (isLogin.value) {
            const { response, status } = await boardApi.getBoardById(bid)
            if (status == '403') {
                return false
            }
            const authData = await getAuthData()
            if (authData.oid == response.owner.oid) isOwn = true
        }
        return isOwn
    }

    const getUserEmail = () => {
        const t = getToken()
        return decodeToken(t)?.email
    }

    const getLoginStatus = async () => {
        getToken()
        return isLogin.value
    }

    const loadAzureData = async () => {
        try {
            //Check if MSAL is intialized before using it.
            if (!msalInstance) {
                throw new Error('MSAL is not initialized')
            }

            //i store data in local storage
            const account = msalInstance.getAllAccounts()
            if (account.length > 0) {
                state.user = account[0]
                state.isAuthenticated = true
                isLogin.value = true
                typeLogin.value = 'azure'
            }
            const token = JSON.parse(localStorage.getItem(`msal.token.keys.${import.meta.env.VITE_CLIENT_ID}`))

            console.log(token.accessToken[0])
            console.log(token.refreshToken[0])
            toastStore.changeToast('success', 'Success', 'You have successfully logged in')

            return account
        } catch (error) {
            console.error(error)
        }
    }

    const azureLogin = async () => {
        try {
            //Check if MSAL is intialized before using it.
            if (!msalInstance) {
                throw new Error('MSAL is not initialized')
            }

            const loginResponse = await msalInstance.loginRedirect()
            state.user = loginResponse.account
            state.isAuthenticated = true
            isLogin.value = true
            typeLogin.value = 'azure'
        } catch (error) {
            console.error(error)
        }
    }

    const azureLogout = async () => {
        //Check if MSAL is intialized before using it.
        if (!msalInstance) {
            throw new Error('MSAL is not initialized')
        }

        await msalInstance.logoutRedirect()
        isLogin.value = false
        typeLogin.value = ''
    }

    const azureHandleRedirect = async () => {
        //Check if MSAL is intialized before using it.
        if (!msalInstance) {
            throw new Error('MSAL is not initialized')
        }

        const response = await msalInstance.handleRedirectPromise()
        if (response) {
            state.user = response.account
            isLogin.value = true
            typeLogin.value = 'azure'
        }
    }

    return {
        getLoginStatus,
        getAuthData,
        isOwner,
        addToken,
        getToken,
        checkToken,
        isTokenExpired,
        logout,
        isEditor,
        refreshAccessToken,
        getUserEmail,
        azureLogin,
        azureLogout,
        azureHandleRedirect,
        loadAzureData,
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useAuthStore, import.meta.hot))
}
