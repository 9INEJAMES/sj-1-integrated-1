import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'
import VueJwtDecode from 'vue-jwt-decode'
import { useToast } from '@/stores/toast.js'
import { useBoardApi } from '@/composables/board-api'
import { msalInstance, graphScopes, state, loginRequest } from '@/configs/msalConfig'

export const useAuthStore = defineStore('auth', () => {
    const toastStore = useToast()
    const boardApi = useBoardApi()
    const accessToken = ref('')
    const refreshToken = ref('')
    const isLogin = ref(false)
    const typeLogin = ref('')

    // Initialize MSAL
    let msalInitialized = false
    const initializeMsal = async () => {
        if (!msalInitialized) {
            try {
                await msalInstance.initialize()
                msalInitialized = true
            } catch (error) {
                console.error('MSAL initialization error:', error)
                throw error
            }
        }
    }

    const getToken = () => {
        const auth = JSON.parse(localStorage.getItem('authData'))
        isLogin.value = auth ? true : false
        typeLogin.value = auth ? auth.typeLogin : ''
        accessToken.value = auth ? auth.access_token : null
        refreshToken.value = auth ? auth.refresh_token : null
        return accessToken.value
    }

    const addToken = (newAccessToken, newRefreshToken, newType) => {
        accessToken.value = newAccessToken
        refreshToken.value = newRefreshToken || refreshToken.value // Keep current refresh token if not passed
        typeLogin.value = newType || typeLogin.value
        let userTokenObject

        userTokenObject = {
            username: decodeToken(newAccessToken).username,
            access_token: accessToken.value,
            refresh_token: refreshToken.value,
            typeLogin: typeLogin.value,
        }

        localStorage.setItem('authData', JSON.stringify(userTokenObject))
        isLogin.value = true
        return userTokenObject
    }

    const getLoginStatus = async () => {
        getToken()
        return isLogin.value
    }

    const getTypeOfLogin = async () => {
        getToken()
        return typeLogin.value
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
            console.log('Refreshing token...')
            getToken() // test
            if (refreshToken.value) {
                const response = await fetch(`${import.meta.env.VITE_BASE_URL}/token`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${refreshToken.value}`,
                        'Auth-Type': typeLogin.value || 'LOCAL',
                    },
                })
                const data = await response.json()
                if (response.ok && data.access_token) {
                    addToken(data.access_token) // Keep the same refresh token
                    return true
                }
                return false
            }
        } catch (error) {
            console.error('Error refreshing token:', error)
            return false
        }
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

    const logout = async () => {
        localStorage.removeItem('authData')
        localStorage.removeItem('loginStatus')
        if (typeLogin.value == 'AZURE') await azureLogout()
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

    const loadAzureData = async () => {
        try {
            await initializeMsal()

            const currentAccounts = msalInstance.getAllAccounts()
            if (currentAccounts.length === 0) {
                // await azureLogin()
                return null
            }

            const account = currentAccounts[0]
            msalInstance.setActiveAccount(account)

            try {
                // Get both ID token and access token
                const response = await msalInstance.acquireTokenSilent({
                    ...loginRequest,
                    account: account,
                })

                // Store user info
                state.user = account
                state.isAuthenticated = true
                isLogin.value = true
                typeLogin.value = 'azure'

                // Store tokens securely
                // localStorage.setItem('msal.idToken', response.idToken)
                // localStorage.setItem('msal.accessToken', response.accessToken)
                const tokenKeys = JSON.parse(localStorage.getItem(`msal.token.keys.${import.meta.env.VITE_CLIENT_ID}`))
                const accessKey = JSON.parse(localStorage.getItem(`${tokenKeys.accessToken[0]}`))
                const refreshKey = JSON.parse(localStorage.getItem(`${tokenKeys.refreshToken[0]}`))
                const idKey = JSON.parse(localStorage.getItem(`${tokenKeys.idToken[0]}`))

                // console.log('access token ', accessKey.secret)
                // console.log('access token ', refreshKey.secret)
                // console.log('access token ', idKey.secret)

                addToken(accessKey.secret, refreshKey.secret, 'AZURE')
                localStorage.setItem('loginStatus', JSON.stringify({ isFirstLogin: true }))

                return account
            } catch (silentError) {
                console.error('Silent token acquisition failed:', silentError)
                if (silentError.name === 'InteractionRequiredAuthError') {
                    // await azureLogin()
                    return null
                }
                throw silentError
            }
        } catch (error) {
            console.error('Load Azure data error:', error)
            throw error
        }
    }

    const azureLogin = async () => {
        try {
            await initializeMsal()
            state.isAuthenticated = false
            state.user = null
            isLogin.value = false
            typeLogin.value = ''

            await msalInstance.loginRedirect(loginRequest)
            const response = await msalInstance.acquireTokenSilent({
                ...loginRequest,
                account: account,
            })

            await loadAzureData()
        } catch (error) {
            if (error.name === 'BrowserAuthError' && error.message.includes('interaction_in_progress')) {
                localStorage.removeItem('msal.interaction.status')
                return
            }
            console.error('Azure login error:', error)
            throw error
        }
    }

    const azureHandleRedirect = async () => {
        try {
            await initializeMsal()

            const response = await msalInstance.handleRedirectPromise()

            if (response) {
                const account = response.account
                msalInstance.setActiveAccount(account)

                // Store user info
                state.user = account
                state.isAuthenticated = true
                isLogin.value = true
                typeLogin.value = 'azure'

                return account
            } else {
                return await loadAzureData()
            }
        } catch (error) {
            console.error('Handle redirect error:', error)
            if (error.name === 'BrowserAuthError' && error.message.includes('interaction_in_progress')) {
                localStorage.removeItem('msal.interaction.status')
                return await loadAzureData()
            }
            throw error
        }
    }

    // Helper function to get MS Graph access token
    const getGraphToken = async () => {
        try {
            await initializeMsal()

            const account = msalInstance.getActiveAccount()
            if (!account) {
                throw new Error('No active account')
            }

            const response = await msalInstance.acquireTokenSilent({
                ...graphScopes,
                account: account,
            })

            return response.accessToken
        } catch (error) {
            console.error('Get Graph token error:', error)
            if (error.name === 'InteractionRequiredAuthError') {
                const response = await msalInstance.acquireTokenPopup(graphScopes)
                return response.accessToken
            }
            throw error
        }
    }

    const azureLogout = async () => {
        if (!msalInstance) {
            throw new Error('MSAL is not initialized')
        }

        await msalInstance.logoutRedirect()
    }

    const showSuccessLoginToast = () => {
        const loginStatus = JSON.parse(localStorage.getItem(`loginStatus`))
        if (loginStatus?.isFirstLogin) {
            toastStore.changeToast('success', 'Success', 'You have successfully logged in')
        }
        localStorage.setItem('loginStatus', JSON.stringify({ isFirstLogin: false }))
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
        getGraphToken,
        getTypeOfLogin,
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useAuthStore, import.meta.hot))
}
