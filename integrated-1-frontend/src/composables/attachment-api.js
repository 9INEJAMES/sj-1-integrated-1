import { useToast } from '@/stores/toast'
import { useAuthStore } from '@/stores/auth.js'
export const useAttachmentApi = () => {
    const toastStore = useToast()
    const url = import.meta.env.VITE_BASE_URL
    const authStore = useAuthStore()

    async function fetchWithToken(endpoint, options = {}) {
        await authStore.checkToken()
        const token = authStore.getToken()
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers,
        }

        if (token) {
            headers['Authorization'] = `Bearer ${token}`
        }

        toastStore.displayLoading()
        const response = await fetch(`${url}/v3/boards/${endpoint}`, {
            ...options,
            headers,
        })
        await toastStore.resetToast()

        if (response.status === 401) {
            await authStore.refreshAccessToken()
            // return fetchWithToken(endpoint, options)
        }

        const contentType = response.headers.get('content-type')
        if (contentType && contentType.includes('application/json')) {
            return response.json()
        } else {
            return response
        }
    }

    async function downloadFile(bid, taskId, id, location) {
        try {
            const response = await fetchWithToken(`${bid}/tasks/${taskId}/attachments/${id}`)

            if (!response.ok) {
                throw new Error(`Error fetching attachment, status: ${response.status}`)
            }

            const contentDisposition = response.headers.get('Content-Disposition')
            const filename = contentDisposition ? contentDisposition.split('filename=')[1].replace(/"/g, '') : `${location.split('/').pop()}`

            const blob = await response.blob()
            const url = window.URL.createObjectURL(blob)

            const contentType = response.headers.get('Content-Type')
            const fileExtension = filename.split('.').pop().toLowerCase()

            if (contentType.startsWith('image') || ['png', 'jpeg', 'jpg', 'gif'].includes(fileExtension)) {
                const imageWindow = window.open('', '_blank')
                imageWindow.document.write(`<img src="${url}" alt="${filename}" style="max-width: 100%; height: auto;">`)

                const link = document.createElement('a')
                link.href = url
                link.setAttribute('download', filename)
                document.body.appendChild(link)
                link.click()
            } else if (contentType === 'application/pdf' || fileExtension === 'pdf') {
                const pdfWindow = window.open(url, '_blank')
                pdfWindow.document.title = filename
            } else {
                const link = document.createElement('a')
                link.href = url
                link.setAttribute('download', filename)
                document.body.appendChild(link)
                link.click()
                document.body.removeChild(link)
                window.URL.revokeObjectURL(url)
            }

            return response
        } catch (error) {
            toastStore.changeToast('error', 'Error', 'An error has occurred, the attachment does not exist.')
            console.error(`Error fetching attachment by ID: ${error}`)
        }
    }

    const deleteAttachmentFromTask = async (bid, taskId, attachmentId) => {
        try {
            const response = await fetchWithToken(`${bid}/tasks/${taskId}/attachments/${attachmentId}`, {
                method: 'DELETE',
            })

            if (response.status >= 400) {
                const errorMessage = typeof response === 'string' ? response : 'An error occurred, the attachment could not be deleted.'
                toastStore.changeToast('error', 'Error', errorMessage)
                return
            }

            if (response.ok) {
                toastStore.changeToast('success', 'Success', 'The attachment has been deleted successfully')
            }
        } catch (error) {
            toastStore.changeToast('error', 'Error', 'An error occurred, the attachment could not be deleted.')
            console.error(`Error deleting attachment: ${error}`)
        }
    }

    async function loadFileDisplay(bid, taskId, filename) {
        try {
            const response = await fetchWithToken(`${bid}/tasks/${taskId}/attachments/displays/${filename}`, {
                headers: {
                    Accept: 'application/octet-stream', // Make sure backend sends arraybuffer
                },
            })

            if (response.status >= 400) {
                toastStore.changeToast('error', 'Error', 'An error has occurred, the attachment does not exist.')
                return
            }

            const contentType = response.headers.get('Content-Type')
            const arrayBuffer = await response.arrayBuffer()

            let binary = ''
            const bytes = new Uint8Array(arrayBuffer)
            bytes.forEach((byte) => {
                binary += String.fromCharCode(byte)
            })
            const base64String = window.btoa(binary)

            const base64URL = `data:${contentType};base64,${base64String}`
            // const fileWindow = window.open(base64URL, "_blank")
            // if (fileWindow) {
            //     fileWindow.document.title = filename
            //     if (contentType.includes("image")) {
            //         fileWindow.document.body.innerHTML = `<img src="${base64URL}" alt="${filename}" />`
            //     } else if (contentType.includes("pdf")) {
            //         fileWindow.document.body.innerHTML = `<embed src="${base64URL}" width="100%" height="100%" type="application/pdf" />`
            //     } else {
            //         fileWindow.document.body.innerHTML = `<a href="${base64URL}" download="${filename}">Download ${filename}</a>`
            //     }
            // }

            return base64URL
        } catch (error) {
            toastStore.changeToast('error', 'Error', 'An error has occurred, the attachment does not exist.')
            console.error(`Error fetching attachment by filename: ${error}`)
        }
    }

    return { downloadFile, deleteAttachmentFromTask, loadFileDisplay }
}
