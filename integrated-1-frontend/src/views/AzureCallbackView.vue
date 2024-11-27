<script setup>
import { ref, onMounted } from 'vue'
import { useAuthApi } from '@/composables/auth-api.js'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const authApi = useAuthApi()
// const res = await authApi.azureCallback(route.query.code)
// console.log(res)
</script>

<template>
    <div></div>
</template>

<style scoped></style>
