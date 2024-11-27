import { useAuthStore } from '../stores/auth.js'
import { useToast } from '@/stores/toast'

export const useAuthApi = () => {
    const toastStore = useToast()
    const authStore = useAuthStore()

    const url = import.meta.env.VITE_BASE_URL

    async function signIn(user) {
        try {
            toastStore.displayLoading()
            const response = await fetch(`${url}/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(user),
            })
            await toastStore.resetToast()

            if (response.ok) {
                const token = await response.json()
                const userTokenObject = authStore.addToken(token.access_token, token.refresh_token, 'local') // Store token
                toastStore.changeToast('success', 'Success', 'You have successfully logged in')

                return userTokenObject
            } else if (response.status === 400 || response.status === 401) {
                toastStore.changeToast('error', 'Error', 'Username or Password is incorrect')
            } else {
                toastStore.changeToast('error', 'Error', 'There is a problem. Please try again later')
            }
        } catch (error) {
            console.error(`Error during sign in: ${error}`)
            throw error
        }
    }

    return { signIn }
}
