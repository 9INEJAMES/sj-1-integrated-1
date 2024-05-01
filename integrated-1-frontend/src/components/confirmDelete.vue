<script setup>
import { defineProps, defineEmits, onUnmounted } from 'vue'
import { deleteTask } from '@/libs/FetchAPI.js'
import { useTasks } from '@/stores/task';


const props = defineProps({
  task: {
    type: Object,
  },
})


const myTasks = useTasks()

const emit = defineEmits(['closeModal']) 

const submitDelete = async () => {
  // Call deleteTask function with taskId
  const result = await deleteTask(props.task.id)
  myTasks.removeTask(result.id)
  emit('closeModal')
  
}

const cancelDelete = () => {
  emit('closeModal')
}

// Automatically set deleteModal to false when the component is unmounted

</script>


<template>
  <div class="py-[30vh] px-[10vh] fixed inset-0 flex justify-center bg-black bg-opacity-35 w-full z-50">
    <div class="bg-white  flex-col border rounded-md p-[2vh] w-fit h-fit">
      <p class="font-bold text-[4vh] py-[2vh]">Delete a Task</p>
      <hr />
      <p class="py-[3vh]">Do you want to delete the task "{{ task.title }}" ?</p>
      <div class="flex gap-[2vh] justify-end py-[2vh]">
        <button @click="submitDelete" class="bg-red-500 text-white rounded-md p-2">Delete</button>
        <button @click="cancelDelete" class="bg-blue-500 text-white rounded-md p-2">Cancel</button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
