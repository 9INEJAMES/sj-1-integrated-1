import { defineStore, acceptHMRUpdate } from "pinia"
import { ref } from "vue"
import { useTaskApi } from "@/composables/task-api"

export const useTasksStore = defineStore("tasks", () => {
    const tasks = ref([])
    const sortDirection = ref("default")
    const taskApi = useTaskApi()
    const uploadFiles = ref([]) 
    const deleteFiles = ref([])


    const fetchTasks = async (bid, filterStatuses) => {
        const tasksData = await taskApi.getAllTasks(bid, filterStatuses)
        resetTasks()
        addTasks(tasksData)
        sortedTasks()
    }

    const sortedTasks = () => {
        if (sortDirection.value === "default") {
            tasks.value.sort((a, b) => a.id - b.id)
        } else if (sortDirection.value === "asc") {
            tasks.value.sort((a, b) => a.status.name.localeCompare(b.status.name))
        } else {
            tasks.value.sort((a, b) => b.status.name.localeCompare(a.status.name))
        }
    }

    const switchSortOrder = () => {
        if (sortDirection.value === "default") {
            sortDirection.value = "asc"
        } else if (sortDirection.value === "asc") {
            sortDirection.value = "desc"
        } else {
            sortDirection.value = "default"
        }
        sortedTasks()
    }

    function addTasks(newTasks) {
        newTasks.forEach((newTask) => addTask(newTask))
    }

    async function addTask(task) {
        tasks.value.push(task)
        sortedTasks()
    }

    function updateTask(updatedTask) {
        const index = findIndexTask(updatedTask.id)
        if (index !== -1) {
            tasks.value.splice(index, 1, updatedTask)
        }
        sortedTasks()
    }

    function findTaskById(searchId) {
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
        sortedTasks()
    }

    function getTasks() {
        return tasks.value
    }


    function resetTasks() {
        tasks.value = []
    }


    function addAttachmentToTask(taskId, uploadedFile) {
        const task = findTaskById(taskId)
        if (task) {
            if (!task.attachments) {
                task.attachments = []
            }
            if (Array.isArray(uploadedFile)) {
                task.attachments.push(...uploadedFile)
            } else {
                task.attachments.push(uploadedFile)
            }
        }
    }

    return {
        tasks,
        sortDirection,
        getTasks,
        addTasks,
        updateTask,
        findTaskById,
        findIndexTask,
        removeTask,
        resetTasks,
        fetchTasks,
        addTask,
        switchSortOrder,
        addAttachmentToTask,
        uploadFiles,
        deleteFiles
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useTasksStore, import.meta.hot))
}
