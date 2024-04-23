<script setup>
import { ref, onMounted } from 'vue'
import { getAllTasks } from '../libs/FetchAPI.js'
import TaskCard from '../components/TaskCard.vue'
import {  useTasks  } from '../stores/task.js'

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
</script>

<template>
  <div class="px-[5vh]">
  <p class="font-bold text-[3vh] pt-[4vh]"> All your task is Here</p>
  <div class="flex gap-[5vh] py-[3vh]">
    <div v-for="task in myTasks.getTasks()" :key="task.id" class=" flex-wrap" >
      <TaskCard :task="task" />
    </div>
  </div>
  </div>
</template>

<style scoped>



</style>