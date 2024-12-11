<script setup>
import { onMounted, ref } from 'vue'
import { useTheme } from '@/stores/theme.js'
import { useCollabStore } from '@/stores/collab'
import { useAuthStore } from '@/stores/auth'
import { useCollabApi } from '@/composables/collab-api'
import { useRoute } from 'vue-router'

const collabStore = useCollabStore()
const themeStore = useTheme()
const authStore = useAuthStore()
const ownerEmail = authStore.getUserEmail()
const collabApi = useCollabApi()
const route = useRoute()

const props = defineProps({
    action: {
        type: String,
        required: true,
    },
    collaborator: {
        type: Object,
        required: false,
    },
    collabBoard: {
        type: Object,
        required: false,
    },
})

const emit = defineEmits(['close'])

const closeModal = () => {
    emit('close')
}

const newCollaborator = ref({
    email: '',
    accessRight: 'READ',
})
const deleteCollaborator = async (collaboratorId) => {
    try {
        const response = await collabApi.deleteCollaborator(route.params.bid, collaboratorId)
        if (response.success) {
            collabStore.removeCollaborator(collaboratorId)
            closeModal()
        } else {
        }
    } catch (error) {}
}

const cancelCollaborator = async (collaboratorId) => {
    try {
        const response = await collabApi.cancelCollaborator(route.params.bid, collaboratorId)
        if (response.success) {
            collabStore.removeCollaborator(collaboratorId)
            closeModal()
        } else {
        }
    } catch (error) {}
}

const updateCollaborator = async (collaboratorId, newCol) => {
    const updatedAccessRight = newCol.accessRight === 'READ' ? 'WRITE' : 'READ'
    newCol.accessRight = updatedAccessRight
    const response = await collabApi.updateCollaborator(route.params.bid, collaboratorId, newCol.accessRight)

    if (response.success) {
        collabStore.updateCollaborator(collaboratorId, response.data)
    } else {
        console.error('Failed to update collaborator access right.')
    }
    closeModal()
}

const deleteCollabBoard = (collabBoard) => {
    collabApi.leaveCollabBoard(collabBoard.id, authStore.getAuthData().oid)
    collabStore.removeCollabBoard(collabBoard.id)

    closeModal()
}

const handleSubmit = async () => {
    if (props.action === 'add') {
        const response = await collabApi.addCollaborator(newCollaborator.value)
        if (response && response.ok) {
            collabStore.addCollaborator(await response.json()) // Add it to the store if success
        }
        if (response.status != 400 && response.status != 404 && response.status != 409) {
            closeModal()
        }
    }
}
const validateEmail = (email) => {
    const atIndex = email.indexOf('@')
    const dotIndex = email.lastIndexOf('.')

    return atIndex > 0 && dotIndex > atIndex + 1 && dotIndex < email.length - 1
}
onMounted(() => {
    if (props.action === 'update') {
        newCollaborator.value.name = props.collaborator.name
        newCollaborator.value.email = props.collaborator.email
        newCollaborator.value.accessRight = props.collaborator.accessRight
        newCollaborator.value.oid = props.collaborator.oid
    }
    if (props.action === 'leaveBoard') {
    }
})
</script>

<template>
    <div class="fixed inset-0 flex justify-center items-center bg-black bg-opacity-60 w-full">
        <div class="shadow-lg rounded-lg p-6 w-full max-w-xl mx-6" :class="themeStore.getTheme()">
            <div class="flex justify-between items-center mb-4">
                <p v-if="props.action === 'add'" class="text-2xl font-semibold" :class="themeStore.getTextHeaderTheme()">Add Collaborator</p>
                <p v-if="props.action === 'delete'" class="text-2xl font-semibold" :class="themeStore.getTextHeaderTheme()">Remove Collaborator</p>
                <p v-if="props.action === 'cancel'" class="text-2xl font-semibold" :class="themeStore.getTextHeaderTheme()">Cancel Pending Invited</p>
                <p v-if="props.action === 'update'" class="text-2xl font-semibold" :class="themeStore.getTextHeaderTheme()">Change access right</p>
                <p v-if="props.action === 'leaveBoard'" class="text-2xl font-semibold" :class="themeStore.getTextHeaderTheme()">Leave Board</p>
                <button class="text-gray-500 hover:text-gray-700 dark:text-gray-300 dark:hover:text-gray-100" @click="closeModal">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-6 h-6">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                </button>
            </div>
            <hr class="my-3 border-gray-300 dark:border-gray-600" />
            <div class="mb-5">
                <div class="grid grid-cols-6 gap-2" v-if="props.action === 'add'">
                    <div class="col-span-4">
                        <label for="title" class="block mb-1 font-medium"> Collaborator e-mail </label>

                        <input type="email" id="title" placeholder="Enter e-mail" v-model="newCollaborator.email" class="w-full p-2 border border-gray-300 rounded-md" maxlength="50" />
                        <p v-if="newCollaborator.email == ownerEmail" class="text-xs pl-1 pt-2 overflow-auto">Board owner cannot be collaborator of his/her own board</p>
                    </div>
                    <div class="col-span-2">
                        <label for="accessRight" class="block mb-1 font-medium"> Access Right</label>
                        <select class="itbkk-status select select-bordered" id="accessRight" v-model="newCollaborator.accessRight">
                            <option value="READ">READ</option>
                            <option value="WRITE">WRITE</option>
                        </select>
                    </div>
                </div>
                <div v-if="props.action === 'delete'" class="font-medium">Do you want to remove "{{ props.collaborator.name }}" from the board ?</div>
                <div v-if="props.action === 'update'" class="font-medium">
                    Do you want to change access right of "{{ props.collaborator.name }}" to "{{ props.collaborator.accessRight === 'WRITE' ? 'READ' : 'WRITE' }}" ?
                </div>
                <div v-if="props.action === 'cancel'" class="font-medium">Do you want to cancel invitation to "{{ props.collaborator.name }}" ?</div>
                <div v-if="props.action === 'leaveBoard'" class="font-medium">Do you want to leave this "{{ props.collabBoard.name }}" board?</div>
            </div>
            <hr class="my-3 border-gray-300 dark:border-gray-600" />
            <div class="flex justify-end gap-2">
                <button
                    v-if="props.action === 'add'"
                    @click="handleSubmit"
                    class="px-4 py-2 text-black rounded-md bg-pink-500 hover:text-white disabled:bg-slate-300"
                    :disabled="!validateEmail(newCollaborator.email) || !newCollaborator.email.length || newCollaborator.email == ownerEmail"
                >
                    Confirm
                </button>
                <button v-if="props.action === 'delete'" @click="deleteCollaborator(props.collaborator.oid)" class="px-4 py-2 text-black rounded-md bg-pink-500">Delete</button>
                <button v-if="props.action === 'cancel'" @click="cancelCollaborator(props.collaborator.oid)" class="px-4 py-2 text-black rounded-md bg-pink-500">Confirm</button>
                <button v-if="props.action === 'update'" @click="updateCollaborator(props.collaborator.oid, newCollaborator)" class="px-4 py-2 text-black rounded-md bg-pink-500">Confirm</button>
                <button v-if="props.action === 'leaveBoard'" @click="deleteCollabBoard(props.collabBoard)" class="px-4 py-2 text-black rounded-md bg-pink-500">Confirm</button>

                <button @click="closeModal" class="px-4 py-2 text-black rounded-md bg-slate-100">Cancel</button>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
