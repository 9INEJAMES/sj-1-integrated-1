<script setup>
import router from '@/router'
import { ref, defineProps, defineEmits } from 'vue'
import { getTaskById } from '../libs/FetchAPI.js'

const props = defineProps({
    task: {
        type: Object
    }
})

const task = ref(props.task)

const getTask = async () => {
    await router.push({ path: `/${props.task.taskId}` })    

}
</script>

<template>
    <div class="flex-col justify-start gap-[2vh] rounded-md border-none shadow-lg p-[2vh] ดสำป">
        <button class="font-bold" @click="getTask()">{{ task.title }}</button>
        <p>{{ task.description }}</p>
        <p>{{ task.assignees }}</p>
        <p>{{ task.status }}</p>
        <p>{{ task.createdOn }}</p>
        <p>{{ task.updatedOn }}</p>
    </div>
</template>

<style lang="scss" scoped></style>
