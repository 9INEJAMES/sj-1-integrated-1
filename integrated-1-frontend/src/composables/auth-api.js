import { useAuthStore } from "../stores/auth.js"
import { useToast } from "@/stores/toast"
import VueJwtDecode from "vue-jwt-decode"

export const useAuthApi = () => {
    const toastStore = useToast()
    const AuthStore = useAuthStore()

    const url = import.meta.env.VITE_BASE_URL2

    async function signIn(user) {
        try {
            const response = await fetch(`${url}/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(user),
            })

            if (response.ok) {
                const token = await response.json()
                const decodedToken = VueJwtDecode.decode(token.access_token)

                const userTokenObject = {
                    username: decodedToken.userName, // Assuming the token contains a field 'userName'
                    token: token.access_token,
                }

                if (!AuthStore.user.value) {
                    AuthStore.user.value = {} // Initialize the user object if it doesn't exist
                }
                AuthStore.user.value = decodedToken // Store decoded token

                localStorage.setItem("authData", JSON.stringify(userTokenObject))

                toastStore.changeToast(true, "You have successfully logged in")

                return userTokenObject
            } else if (response.status === 400 || response.status === 401) {
                toastStore.changeToast(false, "Username or Password is incorrect")
            } else {
                toastStore.changeToast(false, "There is a problem. Please try again later")
            }
        } catch (error) {
            console.error(`Error during sign in: ${error}`)
            throw error
        }
    }

    return { signIn }
}
