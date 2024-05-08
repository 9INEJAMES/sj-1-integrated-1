import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'
import { useTaskApi } from '@/composables/task-api'

export const useTasksStore = defineStore('taswks', () => {
    const tasks = ref([])
    const taskApi = useTaskApi()
    const fetchTasks = async () => {
        const tasksData = await taskApi.getAllTasks()
        resetTasks()
        addTasks(tasksData)
    }

    function addTasks(newTasks) {
        newTasks.forEach((newTask) => addTask(newTask))
    }

    function addTask(task) {
        task.status = task.status
            .split('_')
            .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
            .join(' ')
        tasks.value.push(task)
    }

    function updateTask(updatedTask) {
        const index = findIndexTask(updatedTask.id)
        if (index !== -1) {
            tasks.value.splice(index, 1, updatedTask)
        }
    }

    function getIdOfTask(searchId) {
        return tasks.value.find((task) => task.id == searchId)
    }

    function findIndexTask(searchId) {
        return tasks.value.findIndex((task) => task.id == searchId)
    }

    function removeTask(removeId) {
        const index = findIndexTask(removeId)
        if (index !== -1) {
            tasks.value.splice(index, 1)
        }
    }

    function getTasks() {
        return tasks.value
    }
    function resetTasks() {
        tasks.value = []
    }

    return {
        tasks,
        getTasks,
        addTasks,
        updateTask,
        getIdOfTask,
        findIndexTask,
        removeTask,
        resetTasks,
        fetchTasks,
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useTasksStore, import.meta.hot))
}
