<script setup>
import { ref, onMounted, onUpdated, watch } from 'vue'
import TaskTable from '@/components/TaskTable.vue'
import { useTasksStore } from '@/stores/task.js'
import Addicon from '../../public/Addicon.vue'
import { useToast } from '@/stores/toast.js'
import { useTaskApi } from '@/composables/task-api.js'
import VToast from '@/ui/VToast.vue'
import { useTheme } from '@/stores/theme'
import router from '@/router'

const taskApi = useTaskApi()
const myTasks = useTasksStore()
const myToast = useToast()
const isSelectTask = ref(false)
const taskList = ref([])
const myTheme = useTheme()

onMounted(async () => {
  if (myTasks.getTasks().length <= 0) {
    const tasksData = await taskApi.getAllTasks()
    myTasks.addTasks(tasksData)
  }
  if (myToast.currToast.style === 'alert-error') {
    myTasks.resetTasks()
    const tasksData = await taskApi.getAllTasks()
    myTasks.addTasks(tasksData)
    isSelectTask.value = false
  }
  taskList.value = myTasks.getTasks()
})

watch(myToast.currToast, async () => {
  if (myToast.currToast.style === 'alert-error') {
    myTasks.resetTasks()
    const tasksData = await taskApi.getAllTasks()
    myTasks.addTasks(tasksData)
    taskList.value = tasksData
    isSelectTask.value = false
  }
})
const selectedTask = ref({})
const chosenTask = async (id) => {
  selectedTask.value = await taskApi.getTaskById(id)
  isSelectTask.value = true // Update isSelectTask when a task is chosen
}
const addTaskBtn = () => {
  router.push({
    name: 'taskAdd',
  })
}
</script>

<template>
  <VToast class="z-50" />
  <RouterView class="z-40" />
  <div class="px-[5vh] pt-[6vh]">
    <div class="">
      <TaskTable :taskList="taskList" @get-task="chosenTask"></TaskTable>
    </div>
  </div>

  <RouterLink v-show="$route.name == 'home'" to="/task/add" class="itbkk-button-add"
    ><Addicon v-show="$route.name == 'home'" :class="myTheme.getAlterTheme()" class="fixed bottom-0 right-0 w-16 h-16 rounded-full p-2 m-5 transition-all ease-in hover:cursor-pointer" style="color: #443ad8"
  /></RouterLink>
</template>

<style scoped></style>
