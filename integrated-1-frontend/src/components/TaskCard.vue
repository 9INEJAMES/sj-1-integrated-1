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

const emit = defineEmits(['getTask'])

const getTask = async () => {
    emit('getTask', props.task)
}

const localTimeZone = ref('')

const switchTimeZone = () => {
    localTimeZone.value = Intl.DateTimeFormat().resolvedOptions().timeZone
}

</script>

<template>
    <div class="flex-col justify-start gap-[2vh] rounded-md border-none shadow-lg p-[2vh] w-[50vh] h-[30vh]">
        <button class="font-bold pb-[2vh] hover:text-blue-500 " @click="getTask()">{{ task.title }}</button>
        <p v-if="task.description">{{ task.description }}</p>
        <p v-else class=" italic text-gray-500">No description provided</p>
        <p>{{ task.assignees }}</p>
        <p>{{ task.status }}</p>

    </div>
</template>

<style lang="scss" scoped></style>
