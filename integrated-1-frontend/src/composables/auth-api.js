import { useAuthStore } from "../stores/auth.js"
import { useToast } from "@/stores/toast"

export const useAuthApi = () => {
    const toastStore = useToast()
    const AuthStore = useAuthStore()

    const url = import.meta.env.VITE_BASE_URL2

    async function getToken() {
        try {
            const data = await fetch(`${url}/`)
            const result = await data.json()
            return result
        } catch (error) {
            console.log(`error: ${error}`)
        }
    }

    async function signIn(user) {
        try {
            console.log(user)
            const response = await fetch(`${url}/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ ...user }),
            })
            const token = await response.json()
            toastStore.changeToast(true, "You have successfully logged in")
            // Earn code here

            //Book code here
            AuthStore.user.value = [{...user},token]

            console.log(token)  
            return token
            
        } catch (error) {
            console.error(`Error adding task: ${error}`)
        }
    }



    return { signIn, getToken }
}
