<script setup>
import { useToast } from '@/stores/toast.js'
const toastStore = useToast()
</script>

<template>
    <div v-if="toastStore.currToastArr.header.length > 0" class="fixed inset-0 flex items-start justify-center pt-6 transition-opacity z-50">
        <div class="alert text-white max-w-fit w-full mx-4 p-4 rounded-lg shadow-lg" :class="toastStore.currToastArr.style">
            <span class="text-xl font-semibold">{{ toastStore.currToastArr.header }}:</span>

            <!-- Loading spinner for 'Loading' state -->
            <div v-show="toastStore.currToastArr.header == 'Loading'">
                <div role="status" class="mx-auto">
                    <svg aria-hidden="true" class="w-6 h-6 text-gray-200 animate-spin dark:text-gray-600 fill-slate-600" viewBox="0 0 100 101" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <!-- Spinner paths -->
                    </svg>
                </div>
            </div>

            <!-- Message content -->
            <div class="itbkk-message mt-2 text-sm">
                <p v-for="msg in toastStore.currToastArr.msgArr" :key="msg">
                    <span class="text-red-400">{{ msg?.msg1 }}</span
                    >{{ msg?.msg2 }}<br />
                </p>
            </div>

            <!-- Close button -->
            <button
                type="button"
                class="ms-auto mt-2 text-gray-200 hover:text-gray-900 rounded-lg p-1.5 inline-flex items-center justify-center h-8 w-8"
                aria-label="Close"
                @click="toastStore.resetToast()"
            >
                <span class="sr-only">Close</span>
                <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6" />
                </svg>
            </button>
        </div>
    </div>
</template>

<style scoped>
.toast {
    transition: opacity 0.3s ease;
}
.toast-enter-active,
.toast-leave-active {
    opacity: 1;
}
.toast-enter,
.toast-leave-to {
    opacity: 0;
}
</style>
