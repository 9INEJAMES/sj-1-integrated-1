<script setup>
import { useToast } from '@/stores/toast.js'
const myToast = useToast()
</script>

<template>
    <div v-if="myToast.currToast.msg.length > 0" class="toast toast-start toast-top">
        <div class="alert text-white" :class="myToast.currToast.style">
            <span class="text-xl font-semibold">{{ myToast.currToast.style === 'alert-success' ? 'Success' : 'Error' }}:</span>
            <span class="itbkk-message">{{ myToast.currToast.msg }}</span>
            <button
                type="button"
                class="ms-auto -mx-1.5 -my-1.5 text-gray-200 hover:text-gray-900 rounded-lg p-1.5 inline-flex items-center justify-center h-8 w-8"
                aria-label="Close"
                @click="myToast.resetToast()"
            >
                <span class="sr-only">Close</span>
                <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6" />
                </svg>
            </button>
        </div>
    </div>
</template>

<style scoped></style>
