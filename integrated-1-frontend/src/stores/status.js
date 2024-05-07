import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

export const useStatusesStore = defineStore('statuses', () => {
    const statuses = ref([])

    function addstatuses(newstatuses) {
        newstatuses.forEach((newTask) => addTask(newTask))
    }

    function addTask(task) {
        task.status = task.status
            .split('_')
            .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
            .join(' ')
        statuses.value.push(task)
    }

    function updateTask(updatedTask) {
        const index = findIndexTask(updatedTask.id)
        if (index !== -1) {
            statuses.value.splice(index, 1, updatedTask)
        }
    }

    function getIdOfTask(searchId) {
        return statuses.value.find((task) => task.id == searchId)
    }

    function findIndexTask(searchId) {
        return statuses.value.findIndex((task) => task.id == searchId)
    }

    function removeTask(removeId) {
        const index = findIndexTask(removeId)
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
        updateTask,
        getIdOfTask,
        findIndexTask,
        removeTask,
        getstatuses,
        resetstatuses,
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useTasksStore, import.meta.hot))
}
