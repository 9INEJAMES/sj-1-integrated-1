import { useToast } from '@/stores/toast'
import { useAuthStore } from '@/stores/auth.js'
export const useAttachmentApi = () => {
    const toastStore = useToast()
    const url = import.meta.env.VITE_BASE_URL
    const authStore = useAuthStore()
    let count = 0

    async function fetchWithToken(endpoint, options = {}) {
        await authStore.checkToken()
        const token = authStore.getToken()
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers,
        }

        if (token) {
            headers['Authorization'] = `Bearer ${token}`
            headers['Auth-Type'] = await authStore.getTypeOfLogin() || 'LOCAL'
        }

        toastStore.displayLoading()
        const response = await fetch(`${url}/v3/boards/${endpoint}`, {
            ...options,
            headers,
        })
        await toastStore.resetToast()

        if (response.status == 401) {
            count++
            await authStore.refreshAccessToken()
            if (authStore.getToken()) {
                response = await fetch(`${url}${endpoint}`, {
                    ...options,
                    headers,
                })
            }
        } else count = 0

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
            let filename = location.split('/').pop()
            const match = contentDisposition?.match(/filename\*?=(['"])?(.+?)\1(;|$)/)
            if (match) {
                filename = decodeURIComponent(match[2])
            }

            const blob = await response.blob()
            const url = window.URL.createObjectURL(blob)

            const contentType = response.headers.get('Content-Type')?.split(';')[0].trim()
            const fileExtension = filename.split('.').pop().toLowerCase()

            console.log(`Content-Type: ${contentType}, File Extension: ${fileExtension}`)

            if (fileExtension === 'pdf') {
                const pdfWindow = window.open(url, '_blank')
                if (pdfWindow) {
                    pdfWindow.document.title = filename
                    const downloadLink = pdfWindow.document.createElement('a')
                    downloadLink.href = url
                    downloadLink.download = filename
                    pdfWindow.document.body.appendChild(downloadLink)
                    downloadLink.click()
                }
            } else if (contentType.startsWith('image') || ['png', 'jpeg', 'jpg', 'gif'].includes(fileExtension.toLowerCase())) {
                const imageWindow = window.open('', '_blank')
                if (imageWindow) {
                    imageWindow.document.write(`<img src="${url}" alt="${filename}" style="max-width: 100%; height: auto;">`)
                    const link = document.createElement('a')
                    link.href = url
                    link.setAttribute('download', filename)
                    imageWindow.document.body.appendChild(link)
                    link.click()
                }
            } else {
                const link = document.createElement('a')
                link.href = url
                link.setAttribute('download', filename)
                document.body.appendChild(link)
                link.click()
                document.body.removeChild(link)
            }

            window.URL.revokeObjectURL(url)

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
                    Accept: 'application/octet-stream',
                },
            })

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
