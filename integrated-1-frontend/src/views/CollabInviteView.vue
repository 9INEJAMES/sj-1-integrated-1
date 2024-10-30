<script setup>
import { ref, onMounted } from 'vue'
import { useCollabApi } from '@/composables/collab-api'
import { useCollabStore } from '@/stores/collab'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const collabApi = useCollabApi()
const route = useRoute()
const authStore = useAuthStore()
const collabStore = useCollabStore()
const invite = collabApi.getCollaboratorById(route.params.bid, authStore.getAuthData()?.oid)
console.log(invite)
</script>

<template>
    <div></div>
</template>

<style scoped></style>
