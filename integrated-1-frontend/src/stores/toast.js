import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'
import { useTasksStore } from './task'
import { useStatusesStore } from './status'
import { useRoute } from 'vue-router'

export const useToast = defineStore('toast', () => {
    const route = useRoute()
    const currToast = ref({ style: '', header: '', msg: '' })
    const currToastArr = ref({style: '', header: '', msgArr: []})
    let myTimeout = null
    const changeToast = async (status, head, msg) => {
        resetToast()
        clearTimeout(myTimeout)
        currToast.value.header = head
        currToast.value.style = status == 'success' ? 'alert-success' : status == 'error' ? 'alert-error' : status == 'load' || 'warn' ? 'alert-warning' : ''
        if (!status || msg.toLowerCase().includes('tranferred') || msg.toLowerCase().includes('the status has been updated.')) {
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
    const changeToastArr = async (status, head, msgArr) => {
        resetToast()
        clearTimeout(myTimeout)
        currToastArr.value.header = head
        currToastArr.value.style = status == 'success' ? 'alert-success' : status == 'error' ? 'alert-error' : status == 'load' || 'warn' ? 'alert-warning' : ''
        currToastArr.value.msgArr = msgArr
        myTimeout = setTimeout(() => {
            resetToast()
        }, 5000)
    }
    const resetToast = async () => {
        currToast.value = { style: '', header: '', msg: '' }
        currToastArr.value = {style: '', header: '', msgArr: []}
    }
    const displayLoading = async () => {
        changeToast('load', 'Loading', '')
    }
    return { currToast, changeToast, resetToast, displayLoading, currToastArr, changeToastArr }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useToast, import.meta.hot))
}
