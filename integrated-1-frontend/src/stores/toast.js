import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'
import { useTasksStore } from './task'
import { useStatusesStore } from './status'

export const useToast = defineStore('toast', () => {
    const currToast = ref({ style: '', msg: '' })
    const changeToast = async (isSuccess, msg) => {
        isSuccess ? (currToast.value.style = 'alert-success') : (currToast.value.style = 'alert-error')
        if (!isSuccess || msg.toLowerCase().includes('tranferred') || msg.toLowerCase().includes('the status has been updated.')) {
            if (msg.toLowerCase().includes('task') || msg.toLowerCase().includes('the status does not exist.') || msg.toLowerCase().includes('the status has been updated.')) {
                await useTasksStore().fetchTasks()
            }
            if (msg.toLowerCase().includes('status') && !msg.toLowerCase().includes('the status has been updated.')) {
                await useStatusesStore().fetchStatuses()
            }
        }

        currToast.value.msg = msg
        setTimeout(() => {
            resetToast()
        }, 2000)
    }
    const resetToast = () => {
        currToast.value = { style: '', msg: '' }
    }
    return { currToast, changeToast, resetToast }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useToast, import.meta.hot))
}
