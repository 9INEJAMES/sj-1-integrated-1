import { useStatusApi } from '@/composables/status-api'
import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

export const useStatusesStore = defineStore('statuses', () => {
    const statuses = ref([])
    const statusApi = useStatusApi()
    const fetchStatuses = async (bid) => {
        const statusesData = await statusApi.getAllStatuses(bid)
        resetStatuses()
        addStatuses(statusesData)
    }

    function addStatuses(newStatuses) {
        newStatuses.forEach((newStatus) => addStatus(newStatus))
    }

    function addStatus(status) {
        statuses.value.push(status)
    }

    function updateStatus(updatedStatus) {
        const index = findIndexStatus(updatedStatus.id)
        if (index !== -1) {
            statuses.value.splice(index, 1, updatedStatus)
        }
    }

    function findStatusById(searchId) {
        return statuses.value.find((Status) => Status.id == searchId)
    }

    function findIndexStatus(searchId) {
        return statuses.value.findIndex((Status) => Status.id == searchId)
    }

    function removeStatus(removeId) {
        const index = findIndexStatus(removeId)
        if (index !== -1) {
            statuses.value.splice(index, 1)
        }
    }

    function getStatuses() {
        return statuses.value
    }

    function resetStatuses() {
        statuses.value = []
    }
    return {
        statuses,
        fetchStatuses,
        addStatuses,
        addStatus,
        updateStatus,
        findStatusById,
        findIndexStatus,
        removeStatus,
        getStatuses,
        resetStatuses,
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useStatusesStore, import.meta.hot))
}
