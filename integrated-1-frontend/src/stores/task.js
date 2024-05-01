import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

export const useTasks = defineStore('tasks', () => {
  const tasks = ref([])

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
    addTasks,
    updateTask,
    getIdOfTask,
    findIndexTask,
    removeTask,
    getTasks,
    resetTasks,
  }
})

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useTasks, import.meta.hot))
}
