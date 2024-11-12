<script setup>
import { ref, onMounted } from 'vue'
import { useCollabApi } from '@/composables/collab-api'
import { useCollabStore } from '@/stores/collab'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useTheme } from '@/stores/theme'

const themeStore = useTheme()
const collabApi = useCollabApi()
const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const collabStore = useCollabStore()
const invite = ref({})
const localTimeZone = ref('')
const currentDate = ref('')

onMounted(async () => {
    invite.value = await collabApi.getCollaboratorInvitation(route.params.bid)
    switchTimeZone(invite.value)
})

const switchTimeZone = (coll) => {
    localTimeZone.value = Intl.DateTimeFormat().resolvedOptions().timeZone
    currentDate.value = new Date(coll.updatedOn).toLocaleDateString('en-US', {
        timeZone: localTimeZone.value,
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric',
    })
}
const replyInvitation = async (isAccepted) => {
    await collabApi.updateCollabStatus(route.params.bid, isAccepted)
    if (isAccepted) {
        router.push({
            name: 'taskView',
            params: {
                bid: route.params.bid,
            },
        })
    } else {
        router.push({
            name: 'boardView',
        })
    }
}
</script>

<template>
    {{ invite.status == 'PENDING' ? 'you have invitation on this board' : 'you dont have an invitation now' }}
    {{ invite }}
    <div class="min-h-screen flex items-center justify-center bg-pink-100">
        <div class="w-[30rem] bg-white p-10 rounded-lg shadow-lg text-center space-y-6">
            <p class="text-gray-400 text-sm">{{ currentDate }}</p>

            <h2 class="text-3xl font-bold text-pink-500">Pending invite</h2>

            <div class="flex flex-col items-center space-y-2">
                <div class="h-16 w-16 rounded-full bg-pink-500 flex items-center justify-center text-white text-2xl font-semibold">{{ invite?.ownerName?.split(' ')[1].slice(0, 1) }}</div>
                <p class="text-xl font-semibold text-gray-800">
                    <span class="font-semibold text-pink-500">{{ invite.ownerName }}</span> has invited you to collaborate with <span class="font-semibold">{{ invite.accessRight }}</span> access right
                    on
                    <span class="text-pink-500 font-semibold">"{{ invite.boardName }}"</span>
                </p>
                <p class="text-gray-500 font-semibold">You can accept or decline this invitation.</p>
            </div>
            <div v-if="invite.collaborators > 0">
                <div class="flex justify-center items-center space-x-1">
                    <div class="flex -space-x-2">
                        <div class="h-8 w-8 rounded-full bg-pink-500 flex items-center justify-center text-white font-medium">S</div>
                        <div class="h-8 w-8 rounded-full bg-purple-500 flex items-center justify-center text-white font-medium">M</div>
                        <div class="h-8 w-8 rounded-full bg-orange-400 flex items-center justify-center text-white font-medium">E</div>
                        <div class="h-8 w-8 rounded-full bg-red-400 flex items-center justify-center text-white font-medium">A</div>
                    </div>
                    <span class="text-gray-500 text-sm">+5</span>
                </div>
                <p class="text-gray-500 text-sm">{{ invite.collaborators }} members have already become collaborators on this board.</p>
            </div>
            <div class="flex justify-center space-x-4 mt-4">
                <button class="w-1/3 py-2 rounded-lg border border-gray-300 text-gray-500 font-semibold hover:bg-gray-100" @click="replyInvitation(false)">Decline</button>
                <button class="w-1/3 py-2 rounded-lg bg-pink-500 text-white font-semibold hover:bg-pink-600" @click="replyInvitation(true)">Accept invitation</button>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
