<script setup>
import { ref, onMounted, onUpdated } from 'vue'
import { getAllTasks, getTaskById } from '@/libs/FetchAPI.js'
import TaskTable from '@/components/TaskTable.vue'
import { useTasks } from '@/stores/task.js'
import TaskDetailsPage from '@/components/TaskDetailsPage.vue'
import Addicon from '../../public/Addicon.vue'

const myTasks = useTasks()

const isSelectTask = ref(false)
const taskList = ref([])

const closeModalAfterTimeout = () => {
  setTimeout(() => {
    isSelectTask.value = false // Change the value of isSelectedTask after 3 minutes
  }, 3000) // 3 minutes in milliseconds
}

onMounted(async () => {
  myTasks.resetTasks()
  const tasksData = await getAllTasks()
  myTasks.addTasks(tasksData)
  taskList.value = myTasks.getTasks()
  // closeModalAfterTimeout()
})
const selectedTask = ref({})
const chosenTask = async (id) => {
  selectedTask.value = await getTaskById(id)
  // selectedTask.value = { ...task }
  isSelectTask.value = true // Update isSelectTask when a task is chosen
}

const handleUpdatedTask = () => {
  isSelectTask.value = false // Close the modal
  selectedTask.value = {}
  taskList.value = myTasks.getTasks()
}
</script>

<template>
  <TaskDetailsPage
    v-if="isSelectTask"
    :task="selectedTask"
    @closeModal="handleUpdatedTask"
  />
  <div class="px-[5vh]">
    <p class="font-bold text-[3vh] pt-[4vh]">All your task is Here</p>
    <br />

    <div class="">
      <TaskTable
        v-if="taskList.length > 0"
        :taskList="taskList"
        @get-task="chosenTask"
      ></TaskTable>
      <!-- <div v-else>No record</div> -->
    </div>
  </div>

  <RouterLink to="/tasks/add"
    ><Addicon
      class="fixed bottom-0 right-0 bg-white w-14 h-14 rounded-full p-2 m-5 transition-all ease-in hover:bg-slate-600 hover:cursor-pointer"
      style="color: #443ad8"
  /></RouterLink>
</template>

<style scoped></style>
