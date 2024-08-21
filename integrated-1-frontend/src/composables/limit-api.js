import { useToast } from '@/stores/toast'

export const useLimitApi = () => {
    const toastStore = useToast()
    const url = import.meta.env.VITE_BASE_URL

    async function fetchWithToken(endpoint, options = {}) {
        const auth = JSON.parse(localStorage.getItem("authData"))
        const token = auth ? auth.token : null

        const headers = {
            "Content-Type": "application/json",
            ...options.headers,
        }

        if (token) {
            headers["Authorization"] = `Bearer ${token}`
        }

        const response = await fetch(`${url}${endpoint}`, {
            ...options,
            headers,
        })

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`)
        }

        return response.json()
    }

    async function getLimit() {
        try {
            const data = await fetch(`${url}/limit`)
            const result = await data.json()
            return await fetchWithToken(`/limit${filter}`)
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
                toastStore.changeToast(false, 'The update was unsuccesful')
                return
            }
            const result = await response.json()
            obj.limit
                ? toastStore.changeToast(true, `The Kanban board now limits ${obj.limitMaximumTask} tasks in each status`)
                : toastStore.changeToast(true, `The Kanban board has disabled the task limit in each status`)

            return result
        } catch (error) {
            toastStore.changeToast(false, 'The update was unsuccesful')
            console.error(`Error updating setting: ${error}`)
        }
    }

    return { getLimit, updateLimit }
}
