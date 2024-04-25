<script setup>
import { ref, onMounted, computed, watchEffect } from 'vue'
import { getAllTasks, getTaskById } from '../libs/FetchAPI.js'
import TaskCard from '../components/TaskCard.vue'
import TaskTable from '../components/TaskTable.vue'
import { useTasks } from '../stores/task.js'
import TaskDetails from '../components/TaskDetails.vue'
import { useVariables } from '../stores/store.js'

const myTasks = useTasks()
const myVariables = useVariables()
const isSelectTask = ref(false)
const taskList = ref([])

onMounted(async () => {
  isSelectTask.value = await myVariables.isSelectTask // Assign the value to isSelectTask.value
  if (myTasks.getTasks().length == 0) {
    const tasksData = await getAllTasks()
    myTasks.addTasks(tasksData)
    taskList.value = myTasks.getTasks()
  }
})

console.log(isSelectTask.value)
const selectedTask = ref({})
const chosenTask = async (id) => {
  selectedTask.value = await getTaskById(id)
  // selectedTask.value = { ...task }
  myVariables.isSelectTask = true
  isSelectTask.value = true // Update isSelectTask when a task is chosen
}

const handleUpdatedTask = (editedTask) => {
  myTasks.updateTask(editedTask)
  isSelectTask.value = false // Close the modal
  taskList.value = myTasks.getTasks()
  console.log(myTasks.getTasks())
}
</script>

<template>
  <TaskDetails v-if="isSelectTask" :task="selectedTask" @updatedTask="handleUpdatedTask" @cancel="isSelectTask = false" />

  <div class="px-[5vh]">
    <p class="font-bold text-[3vh] pt-[4vh]">All your task is Here</p>
    <div class="overflow-x-auto">
      <table class="myTable">
        <!-- head -->
        <thead>
          <tr>
            <th>Title</th>
            <th>Assignees</th>
            <th>Status</th>
          </tr>
        </thead>
        
        <TaskTable
          v-if="taskList.length > 0"
          :taskList="taskList"
          @get-task="chosenTask"
        ></TaskTable>
        <!-- <div v-else>No record</div> -->
      </table>
    </div>
  </div>
</template>

<style scoped>
</style>
