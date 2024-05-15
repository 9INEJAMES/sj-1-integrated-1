import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'
import { useLimitApi } from '@/composables/limit-api'

export const useLimitStore = defineStore('limitTask', () => {
    const limitTask = ref([])
    const limitApi = useLimitApi()
    const fetchLimit = async () => {
        const data = await limitApi.getLimit()
        resetLimit()
        limitTask.value.push(data)
    }

    function updateLimit(updatedLimt) {
        limitTask.value[0] = updatedLimt
    }

    function getLimit() {
        return limitTask.value[0]
    }
    function resetLimit() {
        limitTask.value = []
    }
    return { limitTask, fetchLimit, updateLimit, getLimit, resetLimit }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useLimitStore, import.meta.hot))
}
