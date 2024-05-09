import { useStatusApi } from '@/composables/status-api'
import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

export const useStatusesStore = defineStore('statuses', () => {
    const statuses = ref([])
    const statusApi = useStatusApi()
    const fetchStatuses = async () => {
        const statusesData = await statusApi.getAllStatuses()
        resetStatuses()
        addStatuses(statusesData)
    }

    function addStatuses(newStatuses) {
        newStatuses.forEach((newStatus) => addStatus(newStatus))
    }

    function addStatus(status) {
        status.name = status.name
            .split('_')
            .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
            .join(' ')
        statuses.value.push(status)
    }

    function updateStatus(updatedStatus) {
        const index = findIndexStatus(updatedStatus.id)
        if (index !== -1) {
            statuses.value.splice(index, 1, updatedStatus)
        }
    }

    function getIdOfStatus(searchId) {
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
        getIdOfStatus,
        findIndexStatus,
        removeStatus,
        getStatuses,
        resetStatuses,
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useStatusesStore, import.meta.hot))
}
