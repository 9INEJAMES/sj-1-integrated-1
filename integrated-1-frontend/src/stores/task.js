import { defineStore, acceptHMRUpdate } from "pinia"
import { ref } from "vue"
import { useTaskApi } from "@/composables/task-api"

export const useTasksStore = defineStore("tasks", () => {
    const tasks = ref([])
    const sortDirection = ref("default")
    const taskApi = useTaskApi()
    const uploadFiles = ref([]) // Store multiple files

    // Fetch tasks
    const fetchTasks = async (bid, filterStatuses) => {
        const tasksData = await taskApi.getAllTasks(bid, filterStatuses)
        resetTasks()
        addTasks(tasksData)
        sortedTasks()
    }

    // Sort tasks based on the sort direction
    const sortedTasks = () => {
        if (sortDirection.value === "default") {
            tasks.value.sort((a, b) => a.id - b.id)
        } else if (sortDirection.value === "asc") {
            tasks.value.sort((a, b) => a.status.name.localeCompare(b.status.name))
        } else {
            tasks.value.sort((a, b) => b.status.name.localeCompare(a.status.name))
        }
    }

    // Switch sorting order
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

    // Add new tasks to the list
    function addTasks(newTasks) {
        newTasks.forEach((newTask) => addTask(newTask))
    }

    // Add a single task to the list
    async function addTask(task) {
        tasks.value.push(task)
        sortedTasks()
    }

    // Update an existing task
    function updateTask(updatedTask) {
        const index = findIndexTask(updatedTask.id)
        if (index !== -1) {
            tasks.value.splice(index, 1, updatedTask)
        }
        sortedTasks()
    }

    // Find task by ID
    function findTaskById(searchId) {
        return tasks.value.find((task) => task.id == searchId)
    }

    // Find task index by ID
    function findIndexTask(searchId) {
        return tasks.value.findIndex((task) => task.id == searchId)
    }

    // Remove a task from the list
    function removeTask(removeId) {
        const index = findIndexTask(removeId)
        if (index !== -1) {
            tasks.value.splice(index, 1)
        }
        sortedTasks()
    }

    // Get all tasks
    function getTasks() {
        return tasks.value
    }

    // Reset tasks to an empty list
    function resetTasks() {
        tasks.value = []
    }

    function removeAttachmentFromTask(taskId, attachmentId) {
        const task = findTaskById(taskId)
        if (task) {
            task.attachments = task.attachments.filter((attachment) => attachment.id !== attachmentId)
        }
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
        removeAttachmentFromTask,
        addAttachmentToTask,
        uploadFiles, // Expose the array of uploaded files
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useTasksStore, import.meta.hot))
}
