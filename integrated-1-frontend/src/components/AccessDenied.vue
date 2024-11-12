<script setup>
import { useToast } from '@/stores/toast.js'
import { onMounted } from 'vue'

const toastStore = useToast()
onMounted(() => {
    toastStore.changeToast(false, 'Accsess denied, you do not have permission to view this page')
})
</script>

<template>
    <div class="min-h-screen flex items-center justify-center bg-pink-200">
        <div class="bg-white p-16 rounded-2xl shadow-2xl max-w-2xl w-full text-center">
            <h1 class="text-6xl font-bold text-pink-600 mb-8">Access Denied</h1>
            <p class="text-2xl text-gray-700 mb-6">You do not have permission to view this page.</p>
            <p class="text-base text-gray-500">If you believe this is a mistake, please contact the administrator.</p>
        </div>
    </div>
</template>

<style scoped></style>
