import { useAuthStore } from '../stores/auth.js'
import { useToast } from '@/stores/toast'

export const useAuthApi = () => {
    const toastStore = useToast()
    const authStore = useAuthStore()

    const url = import.meta.env.VITE_BASE_URL2

    async function signIn(user) {
        try {
            const response = await fetch(`${url}/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(user),
            })

            if (response.ok) {
                const token = await response.json()
                const userTokenObject = authStore.addToken(token.access_token) // Store token
                toastStore.changeToast(true, 'You have successfully logged in')

                return userTokenObject
            } else if (response.status === 400 || response.status === 401) {
                toastStore.changeToast(false, 'Username or Password is incorrect')
            } else {
                toastStore.changeToast(false, 'There is a problem. Please try again later')
            }
        } catch (error) {
            console.error(`Error during sign in: ${error}`)
            throw error
        }
    }

    return { signIn }
}
