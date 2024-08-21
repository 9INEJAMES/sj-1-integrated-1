import { useAuthStore } from "../stores/auth.js"
import { useToast } from "@/stores/toast"

export const useAuthApi = () => {
    const toastStore = useToast()
    const AuthStore = useAuthStore()

    const url = import.meta.env.VITE_BASE_URL2


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
            toastStore.changeToast(true, "You have successfully logged in")
            // Earn code here

            //Book code here
            if (response.ok) {
                const token = await response.text()

                const userTokenObject = {
                    username: user.userName,
                    token: token
                }

                localStorage.setItem("authData", JSON.stringify(userTokenObject))
                toastStore.changeToast(true, "You have successfully logged in")
                
                return userTokenObject
            } else if (response.status === 400) {
                toastStore.changeToast(false, "Username or password is incorrect")
                
            }else{
                toastStore.changeToast(false, "There is a problem. Please try again later")
            }
        } catch (error) {
            console.error(`Error during sign in: ${error}`)
            throw error
        }
    }



    return { signIn }
}
