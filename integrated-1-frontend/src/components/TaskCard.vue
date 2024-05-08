<script setup>
import { computed } from 'vue'

const props = defineProps({
  task: {
    type: Object,
  },
})
const task = computed(() => props.task)
const emit = defineEmits(['getTask'])
const getTask = async () => {
  emit('getTask', props.task)
}
const colorStatus = (task) => {
  if (task == 'Done') {
    return 'bg-emerald-500'
  } else if (task == 'Doing') {
    return 'bg-sky-300'
  } else if (task == 'No Status') {
    return 'bg-slate-300'
  } else {
    return 'bg-amber-300'
  }
}
</script>

<template>
  <div class="flex flex-col justify-between border-none rounded-md shadow-lg p-[2vh] w-[40vh] h-[25vh] gap-[2vh]">
    <div class="flex-col justify-start">
      <button v-if="task.title.length < 40" class="font-bold pb-[2vh] hover:text-blue-500 break-all" @click="getTask()">
        {{ task.title }}
      </button>
      <button v-else class="font-bold pb-[2vh] hover:text-blue-500 break-all" @click="getTask()">{{ task.title.slice(0, 30) }}...</button>

      <p v-if="task.assignees"><span class="text-amber-600">Assignees : </span>{{ task.assignees }}</p>
      <p v-else class="italic text-gray-500">Unassigned</p>
    </div>
    <div class="flex justify-end">
      <button class="rounded-xl w-[100px] h-[35px] text-[2vh] font-bold cursor-default" :class="colorStatus(task.status)">
        {{ task.status }}
      </button>
    </div>
  </div>
</template>

<style scoped></style>
