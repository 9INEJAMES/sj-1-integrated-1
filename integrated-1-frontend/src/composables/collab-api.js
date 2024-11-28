import { useRoute, useRouter } from 'vue-router'
import { useToast } from '@/stores/toast.js'
import { useAuthStore } from '@/stores/auth.js'

export const useCollabApi = () => {
    const authStore = useAuthStore()
    const toastStore = useToast()
    const route = useRoute()
    const url = import.meta.env.VITE_BASE_URL

    async function fetchWithToken(endpoint, options = {}) {
        await authStore.checkToken()
        const token = authStore.getToken()

        const headers = {
            'Content-Type': 'application/json',
            ...options.headers,
        }

        if (token) {
            headers['Authorization'] = `Bearer ${token}`
            headers['Auth-Type'] = await authStore.getTypeOfLogin()
        }

        toastStore.displayLoading()

        let response = await fetch(`${url}${endpoint}`, {
            ...options,
            headers,
        })

        await toastStore.resetToast()

        if (response.status == 401) {
            // authStore.refreshAccessToken()
            if (authStore.getToken) {
                response = await fetch(`${url}${endpoint}`, {
                    ...options,
                    headers,
                })
            }
        }
        return response
    }

    async function getAllCollabBoard() {
        try {
            return (await fetchWithToken(`/v3/boards/`)).json()
        } catch (error) {
            console.error(`Error fetching boards: ${error}`)
        }
    }

    async function deleteCollabBoard(bid, oid) {
        try {
            const response = await fetchWithToken(`/v3/boards/${bid}/collabs/${oid}`, {
                method: 'DELETE',
            })
            if (response.ok) {
                toastStore.changeToast('success', 'Success', 'The collab board has been deleted.')
                return response.json()
            }
        } catch (error) {
            toastStore.changeToast('error', 'Error', 'An error has occurred, the collab board could not be deleted.')
            console.error(`Error deleting board: ${error}`)
        }
    }

    async function getAllCollaborator() {
        try {
            return (await fetchWithToken(`/v3/boards/${route.params.bid}/collabs`)).json()
        } catch (error) {
            console.error(`Error fetching collaborators: ${error}`)
        }
    }
    async function addCollaborator(newCollaborator) {
        try {
            const response = await fetchWithToken(`/v3/boards/${route.params.bid}/collabs`, {
                method: 'POST',
                body: JSON.stringify({ ...newCollaborator }),
            })
            if (response.ok) {
                toastStore.changeToast('success', 'Success', 'The collaborator has been added.')
            } else if (response.status === 409) {
                toastStore.changeToast('error', 'Error', 'This user is already a collaborator or pending collaborator on this board.')
            } else if (response.status === 403) {
                toastStore.changeToast('error', 'Error', 'You do not have permission to add board collaborator.')
            } else if (response.status === 404 || newCollaborator.email.length > 50) {
                toastStore.changeToast('error', 'Error', 'The user does not exists.')
            } else if (response.status === 500) {
                toastStore.changeToast('error', 'Error', 'There is a problem. Please try again later.')
            }
            return response
        } catch (error) {
            toastStore.changeToast('error', 'Error', 'An error occurred, the collaborator could not be added.')
            console.error(`Error adding collaborator: ${error}`)
        }
        return { success: false }
    }

    async function deleteCollaborator(bid, oid) {
        try {
            const response = await fetchWithToken(`/v3/boards/${bid}/collabs/${oid}`, {
                method: 'DELETE',
            })
            if (response.ok) {
                toastStore.changeToast('success', 'Success', 'The collaborator has been deleted.')
                return { success: true, data: await response.json() }
            } else {
                toastStore.changeToast('error', 'Error', 'Failed to delete the collaborator.')
                return { success: false }
            }
        } catch (error) {
            toastStore.changeToast('error', 'Error', 'An error has occurred, the collaborator could not be deleted.')
            console.error(`Error deleting collaborator: ${error}`)
            return { success: false, error } // Return false with error info
        }
    }

    async function updateCollaborator(bid, oid, accessRight) {
        try {
            const response = await fetchWithToken(`/v3/boards/${bid}/collabs/${oid}`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' }, // Ensure JSON content type
                body: JSON.stringify({ accessRight }), // Properly serialize the body
            })

            if (response.ok) {
                // Check if the response is successful
                toastStore.changeToast('success', 'Success', 'Collaborator access right has been updated.')
                return { success: true, data: await response.json() } // Return success with data
            } else {
                toastStore.changeToast('error', 'Error', 'Failed to update collaborator access right.')
                return { success: false }
            }
        } catch (error) {
            toastStore.changeToast('error', 'Error', 'You do not have permission to change collaborator access right.')
            console.error(`Error updating collaborator access: ${error}`)
            return { success: false, error }
        }
    }

    async function getCollaboratorById(bid, oid) {
        try {
            return (await fetchWithToken(`/v3/boards/${bid}/collabs/${oid}`)).json()
        } catch (error) {
            console.error(`Error fetching collaborator: ${error}`)
        }
    }

    async function getCollaboratorInvitation(bid) {
        try {
            return (await fetchWithToken(`/v3/boards/${bid}/collabs/invitation`)).json()
        } catch (error) {
            console.error(`Error fetching collaborator: ${error}`)
        }
    }
    async function updateCollabStatus(bid, isAccept) {
        try {
            const response = await fetchWithToken(`/v3/boards/${bid}/collabs/invitation/${isAccept}`, {
                method: 'PATCH',
            })
        } catch (error) {
            console.error(`Error fetching collaborator: ${error}`)
        }
    }
    return { getAllCollabBoard, deleteCollabBoard, getAllCollaborator, addCollaborator, deleteCollaborator, updateCollaborator, getCollaboratorById, getCollaboratorInvitation, updateCollabStatus }
}
