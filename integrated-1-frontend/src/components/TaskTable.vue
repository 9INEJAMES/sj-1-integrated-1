<script setup>
const props = defineProps({
    taskList: {
        type: Array,
    },
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
    <tr v-for="(task, index) in taskList" :key="task.id" class="itbkk-item">
        <td>{{ index + 1 }}</td>
        <td class="itbkk-title">
            <p class="font-bold pb-[2vh] hover:text-blue-500 break-all hover:cursor-pointer" @click="getTask(task.id)">
                {{ task.title }}
            </p>
        </td>
        <td class="itbkk-assignees" :class="task.assignees ? '' : 'italic text-gray-500'">
            {{ task.assignees ? task.assignees : 'Unassigned' }}
        </td>
        <td class="itbkk-status">
            <button class="rounded-2xl w-[100px] h-[30px] text-[2vh] font-bold cursor-default" :class="colorStatus(task.status)">
                {{ task.status }}
            </button>
        </td>
    </tr>
</template>

<style scoped></style>
