<script setup>
import { ref, onMounted } from 'vue'
import { useAuthApi } from '@/composables/auth-api.js'

const authApi = useAuthApi()
authApi.azureCallback()
</script>

<template>
    <div></div>
</template>

<style scoped></style>
