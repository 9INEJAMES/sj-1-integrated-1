import { useToast } from "@/stores/toast"

export const useStatusApi = () => {
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

        return response
    }

    async function getAllStatuses() {
        try {
            return (await fetchWithToken(`/statuses`)).json()
        } catch (error) {
            console.error(`Error fetching statuses: ${error}`)
        }
    }

    async function getStatusById(id) {
        try {
            return (await fetchWithToken(`/statuses/${id}`)).json()
        } catch (error) {
            toastStore.changeToast(false, "An error has occurred, the status does not exist.")
            console.error(`Error fetching status by ID: ${error}`)
        }
    }

    async function addStatus(status) {
        try {
            const result = await fetchWithToken(`/statuses`, {
                method: "POST",
                body: JSON.stringify({ ...status }),
            })
            toastStore.changeToast(true, "The status has been added.")
            return result.json()
        } catch (error) {
            toastStore.changeToast(false, "An error has occurred, the status could not be added.")
            console.error(`Error adding status: ${error}`)
        }
    }

    async function updateStatus(status) {
        try {
            const updatedStatus = await fetchWithToken(`/statuses/${status.id}`, {
                method: "PUT",
                body: JSON.stringify({ ...status }),
            })
            toastStore.changeToast(true, "The status has been updated.")
            return updatedStatus.json()
        } catch (error) {
            toastStore.changeToast(false, "An error has occurred, the status could not be updated.")
            console.error(`Error updating status: ${error}`)
        }
    }

    async function deleteStatus(id) {
        try {
            const deleted = await fetchWithToken(`/statuses/${id}`, {
                method: "DELETE",
            })
            toastStore.changeToast(true, "The status has been deleted.")
            return deleted.json()
        } catch (error) {
            toastStore.changeToast(false, "An error has occurred, the status does not exist.")
            console.error(`Error deleting status: ${error}`)
        }
    }

    async function deleteStatusAndTransfer(id, newStatus, tasks) {
        try {
            const deleted = await fetchWithToken(`/statuses/${id}/${newStatus.id}`, {
                method: "DELETE",
            })
            toastStore.changeToast(true, `${tasks} task${tasks > 1 ? "s" : ""} have been transferred and the status has been deleted.`)
            return deleted.json()
        } catch (error) {
            toastStore.changeToast(false, "An error has occurred, the status does not exist.")
            console.error(`Error deleting status and transferring tasks: ${error}`)
        }
    }

    return { getAllStatuses, getStatusById, addStatus, updateStatus, deleteStatus, deleteStatusAndTransfer }
}
