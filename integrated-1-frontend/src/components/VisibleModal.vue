<script setup>
import { ref } from 'vue'
const emit = defineEmits(['closeModal'])
const cancel= () => {
    emit('closeModal', false)
}
const confirm= () => {
    emit('closeModal', true)
}

</script>

<template>
    <div>
        <!-- Button to trigger modal -->

        <!-- Modal overlay -->
        <div class="fixed inset-0 bg-gray-900 bg-opacity-50 flex items-center justify-center z-50">
            <!-- Modal container -->
            <div class="bg-white p-10 rounded-2xl shadow-xl max-w-md w-full text-center relative">
                <!-- Modal header -->
                <h1 class="text-4xl font-bold text-pink-600 mb-4">Board visibility changed!</h1>

                <!-- Modal body -->
                <p class="text-gray-700 mb-4">In public, anyone can view the board, task list, and task details. Do you want to change the visibility to Public?</p>

                <!-- Modal actions -->
                <div class="flex justify-center space-x-4 mt-6">
                    <button class="btn bg-red-500 text-white hover:bg-red-700 px-6 py-2 rounded-lg" @click="cancel">Cancel</button>
                    <button class="btn bg-green-500 text-white hover:bg-green-700 px-6 py-2 rounded-lg" @click="confirm">Confirm</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
