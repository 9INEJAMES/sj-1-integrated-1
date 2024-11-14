<script setup>
import { ref, onMounted, computed } from 'vue'
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
const maxDisplayCount = 4

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
const displayedCollaborators = computed(() => {
    return Array(Math.min(invite.value.collaborators, maxDisplayCount)).fill(null)
})
const getRandomInitial = () => {
    const alphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
    return alphabet[Math.floor(Math.random() * alphabet.length)]
}
const randomColor = () => {
    const colors = ['#EC4899', '#A78BFA', '#FB923C', '#F87171']
    return colors[Math.floor(Math.random() * colors.length)]
}
const goBack = () => {
    // router.push({
    //     name: 'boardView',
    // })
    router.back()
}
</script>

<template>
    <div v-if="invite.status === 'PENDING'" class="min-h-screen flex items-center justify-center bg-pink-100">
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
            <div>
                <div class=" items-center space-x-1">
                    <div class="flex justify-center pb-3 -space-x-2">
                        <div
                            v-for="(collaborator, index) in displayedCollaborators"
                            :key="index"
                            class="h-8 w-8 rounded-full flex items-center justify-center text-white font-medium"
                            :style="{ backgroundColor: randomColor() }"
                        >
                            {{ getRandomInitial() }}
                        </div>

                        <span v-if="invite.collaborators > maxDisplayCount" class="text-gray-500 text-sm"> +{{ invite.collaborators - maxDisplayCount }} </span>
                    </div>
                    <p class="text-gray-500 text-sm">{{ invite.collaborators }} members have already become collaborators on this board.</p>
                </div>
            </div>
            <div class="flex justify-center space-x-4 mt-4">
                <button class="w-1/3 py-2 rounded-lg border border-gray-300 text-gray-500 font-semibold hover:bg-gray-100" @click="replyInvitation(false)">Decline</button>
                <button class="w-1/3 py-2 rounded-lg bg-pink-500 text-white font-semibold hover:bg-pink-600" @click="replyInvitation(true)">Accept invitation</button>
            </div>
        </div>
    </div>

    <div v-else class="min-h-screen flex items-center justify-center bg-red-100">
        <div class="w-[36rem] bg-white p-12 rounded-lg shadow-md text-center space-y-6">
            <div class="flex justify-center text-red-500 text-7xl">
                <i class="fas fa-exclamation-circle"></i>
            </div>

            <h1 class="text-2xl font-bold text-red-600">Sorry, we couldn't find your active invitation to this board</h1>
            <p class="text-gray-600 text-lg">It looks like the invitation link might be outdated or invalid.</p>
            <p class="text-gray-600">If you believe this is an error, please check the link or contact the board owner to request a new invitation.</p>

            <div class="flex justify-center">
                <button @click="goBack" class="mt-4 bg-red-400 text-white px-6 py-3 rounded-full flex items-center justify-center hover:bg-red-500 transition duration-200 ease-in-out">
                    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
                    </svg>
                    Back
                </button>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
