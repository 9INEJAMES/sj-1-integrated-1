<script setup>

import { deleteTask } from '@/libs/FetchAPI';

const props = defineProps({
  task: {
    type: Object,
  },
})

const submitDelete = (id) => {
    deleteTask(id);
    closeModal();
}
</script>

<template>
  <div
    class="py-[10vh] px-[10vh] fixed inset-0 flex justify-center bg-black bg-opacity-35 w-full"
  >
    <div class="bg-white flex-col border rounded-md p-[3vh]">
      <p class="font-bold text-[4vh] py-[2vh]">Delete a Task</p>
      <hr>
      <p class=" py-[3vh]">Do you want to delete the task "{{ task.title }}" ?</p>
      <hr>
      <div class=" flex gap-[2vh] justify-end py-[2vh]">
        <button
          @click="submitDelete(task.id)"
          class="bg-red-500 text-white rounded-md p-2"
        >
          Delete
        </button>
        <button
          @click="closeModal"
          class="bg-blue-500 text-white rounded-md p-2"
        >
          Cancel
        </button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
