import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'
import { useTasksStore } from './task'
import { useStatusesStore } from './status'
import { useRoute } from 'vue-router'

export const useToast = defineStore('toast', () => {
    const route = useRoute()
    const currToast = ref({ style: '', msg: '' })
    let myTimeout = null
    const changeToast = async (isSuccess, msg) => {
        resetToast()
        clearTimeout(myTimeout)
        isSuccess ? (currToast.value.style = 'alert-success') : (currToast.value.style = 'alert-error')
        if (!isSuccess || msg.toLowerCase().includes('tranferred') || msg.toLowerCase().includes('the status has been updated.')) {
            if (msg.toLowerCase().includes('task') || msg.toLowerCase().includes('the status does not exist.') || msg.toLowerCase().includes('the status has been updated.')) {
                await useTasksStore().fetchTasks(route.params.bid)
            }
            if (msg.toLowerCase().includes('status') && !msg.toLowerCase().includes('the status has been updated.')) {
                await useStatusesStore().fetchStatuses(route.params.bid)
            }
        }
        currToast.value.msg = msg
        myTimeout = setTimeout(() => {
            resetToast()
        }, 5000)
    }
    const resetToast = () => {
        currToast.value = { style: '', msg: '' }
    }
    return { currToast, changeToast, resetToast }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useToast, import.meta.hot))
}
