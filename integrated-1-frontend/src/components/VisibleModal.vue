<script setup>
import { ref } from 'vue'
const emit = defineEmits(['closeModal'])
const props = defineProps({
    msg: Boolean,
})
const cancel = () => {
    emit('closeModal', false)
}
const confirm = () => {
    emit('closeModal', true)
}
</script>

<template>
    <div class="itbkk-modal-alert">
        <div class="fixed inset-0 bg-gray-900 bg-opacity-50 flex items-center justify-center z-40">
            <div class="bg-white p-10 rounded-2xl shadow-xl max-w-md w-full text-center relative">
                <h1 class="text-4xl font-bold text-pink-600 mb-4">Board visibility changed!</h1>

                <p class="itbkk-message text-gray-700 mb-4">
                    In {{ msg ? 'public, anyone ' : 'private, only board owner and collaborators ' }}can view the board, task list, and task details. Do you want to change the visibility to
                    {{ msg ? 'Public' : 'Private' }}?
                </p>

                <div class="flex justify-center space-x-4 mt-6">
                    <button class="itbkk-button-cancel btn bg-red-500 text-white hover:bg-red-700 px-6 py-2 rounded-lg" @click="cancel">Cancel</button>
                    <button class="itbkk-button-confirm btn bg-green-500 text-white hover:bg-green-700 px-6 py-2 rounded-lg" @click="confirm">Confirm</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
