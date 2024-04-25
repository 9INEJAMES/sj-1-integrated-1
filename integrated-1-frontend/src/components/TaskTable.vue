<script setup>
const props = defineProps({
  taskList: {
    type: Array
  }
})
const emit = defineEmits(['getTask'])
const getTask = (id) => {
  emit('getTask', id)
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
  <tr v-for="task in taskList" :key="task.id" class=" ">
    <td>
      <p
        class="font-bold pb-[2vh] hover:text-blue-500 break-all hover:cursor-pointer"
        @click="getTask(task.id)"
      >
        {{ task.title }}
    </p>
    </td>
    <td>
      <p v-if="task.assignees">
        {{ task.assignees }}
      </p>
      <p v-else class="italic text-gray-500">Unassigned</p>
    </td>
    <td>
      <button
        class="rounded-2xl w-[100px] h-[30px] text-[2vh] font-bold cursor-default"
        :class="colorStatus(task.status)"
      >
        {{ task.status }}
      </button>
    </td>
  </tr>
  
</template>

<style scoped></style>
