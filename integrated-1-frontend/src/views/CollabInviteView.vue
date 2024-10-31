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
const invite = ref({})
onMounted(async () => {
    invite.value = collabApi.getCollaboratorById(route.params.bid, authStore.getAuthData()?.oid)
})
</script>

<template>
    <div>{{ invite ? 'you have invitation on thi board' : 'you dont have an invitation now' }}</div>
</template>

<style scoped></style>
