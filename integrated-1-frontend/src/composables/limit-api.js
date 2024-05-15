import { useToast } from '@/stores/toast'

export const useLimitApi = () => {
    const myToast = useToast()
    const url = import.meta.env.VITE_BASE_URL

    async function getLimit() {
        try {
            const data = await fetch(`${url}/limit`)
            const result = await data.json()
            return result
        } catch (error) {
            console.log(`error: ${error}`)
        }
    }

    async function updateLimit(obj) {
        try {
            const response = await fetch(`${url}/limit`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ ...obj }),
            })
            if (response.status / 400 >= 1) {
                myToast.changeToast(false, 'The update was unsuccesful')
                return
            }
            const result = await response.json()
            myToast.changeToast(true, 'The setting has been updated')
            return result
        } catch (error) {
            myToast.changeToast(false, 'The update was unsuccesful')
            console.error(`Error updating setting: ${error}`)
        }
    }

    return { getLimit, updateLimit }
}
