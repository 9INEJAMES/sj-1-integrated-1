<script setup>
import { defineProps, defineEmits } from 'vue'
import { useTasksStore } from '@/stores/task'
import { useTheme } from '@/stores/theme.js'
import { useTaskApi } from '@/composables/task-api.js'

const taskApi = useTaskApi()
const myTheme = useTheme()
const myTasks = useTasksStore()
const props = defineProps({
    task: {
        type: Object,
    },
})
const emit = defineEmits(['closeModal'])

const submitDelete = async () => {
    const result = await taskApi.deleteTask(props.task.id)
    if (result) myTasks.removeTask(result.id)
    emit('closeModal', true)
}
const cancelDelete = () => {
    emit('closeModal', false)
}
</script>

<template>
    <div class="py-[30vh] px-[10vh] fixed inset-0 flex justify-center bg-black bg-opacity-35 w-full z-50">
        <div class="flex-col border rounded-md p-[2vh] w-fit h-fit" :class="myTheme.getTheme()">
            <p class="font-bold text-[4vh] py-[2vh]">Delete a Task</p>
            <hr />
            <p class="itbkk-message py-[3vh]">Do you want to delete the task "{{ task.title }}" ?</p>
            <div class="flex gap-[2vh] justify-end py-[2vh]">
                <button @click="submitDelete" class="itbkk-button-confirm bg-red-500 text-white rounded-md p-2">Confirm</button>
                <button @click="cancelDelete" class="itbkk-button-cancel bg-blue-500 text-white rounded-md p-2">Cancel</button>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped></style>
