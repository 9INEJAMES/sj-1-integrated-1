<script setup>
import { ref, onMounted } from 'vue'
import { getAllTasks } from '../libs/FetchAPI.js'
import TaskCard from '../components/TaskCard.vue'
import { useTasks } from '../stores/task.js'
import TaskDetails from '../components/TaskDetails.vue'

const myTasks = useTasks()

onMounted(async () => {
  // Await the promise returned by getAllTasks
  if (myTasks.getTasks().length == 0) {
    const tasksData = await getAllTasks()
    myTasks.addTasks(tasksData)
    console.log(tasksData)
  }
  // Assign the result to the reactive variable
})
const isSelectTask = ref(false)

const selectedTask = ref({})

const chosenTask = (task) => {
  selectedTask.value = { ...task }
  isSelectTask.value = true
}


</script>

<template>
  <TaskDetails v-if="isSelectTask" :task="selectedTask" />

  <div class="px-[5vh]">
    <p class="font-bold text-[3vh] pt-[4vh]">All your task is Here</p>

    <div class="flex gap-[5vh] py-[3vh] flex-wrap">
      <div v-for="task in myTasks.getTasks()" :key="task.id" class=" ">
        <TaskCard :task="task" @get-task="chosenTask" />
      </div>
    </div>
  </div>
</template>

<style scoped></style>
