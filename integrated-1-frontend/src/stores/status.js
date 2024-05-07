import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

export const useStatusesStore = defineStore('statuses', () => {
    const statuses = ref([])

    function addstatuses(newstatuses) {
        newstatuses.forEach((newTask) => addTask(newTask))
    }

    function addStatus(Status) {
        Status.status = Status.status
            .split('_')
            .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
            .join(' ')
        statuses.value.push(Status)
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

    function getstatuses() {
        return statuses.value
    }

    function resetstatuses() {
        statuses.value = []
    }
    return {
        addstatuses,
        addStatus,
        updateStatus,
        getIdOfStatus,
        findIndexStatus,
        removeStatus,
        getstatuses,
        resetstatuses,
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useStatusesStore, import.meta.hot))
}
