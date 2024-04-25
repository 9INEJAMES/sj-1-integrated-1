<script setup>
import { ref, onMounted , computed, watchEffect} from 'vue'
import { getAllTasks } from '../libs/FetchAPI.js'
import TaskCard from '../components/TaskCard.vue'
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
const chosenTask = (task) => {
  selectedTask.value = { ...task }
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
  <TaskDetails v-if="isSelectTask" :task="selectedTask" @updatedTask="handleUpdatedTask" />

  <div class="px-[5vh]">
    <p class="font-bold text-[3vh] pt-[4vh]">All your task is Here</p>

    <div class="flex gap-[5vh] py-[3vh] flex-wrap">
      <div v-for="task in taskList" :key="task.id" class=" ">
        <TaskCard :task="task" @get-task="chosenTask" />
      </div>
    </div>
  </div>
</template>

<style scoped></style>
