<script setup>
import { ref, onMounted, onUpdated } from 'vue'
import { getAllTasks, getTaskById } from '@/libs/FetchAPI.js'
import TaskTable from '@/components/TaskTable.vue'
import { useTasks } from '@/stores/task.js'
import TaskDetailsPage from '@/components/TaskDetailsPage.vue'
import Addicon from '../../public/Addicon.vue'

const myTasks = useTasks()
const isToastOpen = ref(false)
const responseMsg = ref('test')
const isSelectTask = ref(false)
const taskList = ref([])

const closeModalAfterTimeout = () => {
  setTimeout(() => {
    isToastOpen.value = false // Change the value of isSelectedTask after 3 minutes
  }, 3000) // 3 minutes in milliseconds
}

onMounted(async () => {
  // myTasks.resetTasks()
  if (myTasks.getTasks().length <= 0) {
    const tasksData = await getAllTasks()
    myTasks.addTasks(tasksData)
  }
  taskList.value = myTasks.getTasks()
  // closeModalAfterTimeout()
})
const selectedTask = ref({})
const chosenTask = async (id) => {
  selectedTask.value = await getTaskById(id)
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
  <div class="px-[5vh] pt-[6vh]">
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
  <div v-if="!isToastOpen" class="toast toast-start toast-top">
    <div class="alert alert-success text-white">
      <span>{{ responseMsg }}</span>
      <button
        type="button"
        class="ms-auto -mx-1.5 -my-1.5 text-gray-200 hover:text-gray-900 rounded-lg p-1.5 inline-flex items-center justify-center h-8 w-8"
        aria-label="Close"
        @click="closeToast"
      >
        <span class="sr-only">Close</span>
        <svg
          class="w-3 h-3"
          aria-hidden="true"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 14 14"
        >
          <path
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"
          />
        </svg>
      </button>
    </div>
  </div>
</template>

<style scoped></style>
