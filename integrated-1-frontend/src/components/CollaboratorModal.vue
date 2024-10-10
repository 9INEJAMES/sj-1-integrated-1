<script setup>
import { defineProps, defineEmits, onMounted } from 'vue'
import { useTheme } from '@/stores/theme.js'
import { useCollabStore } from '@/stores/collab'
import { useAuthStore } from '@/stores/auth'
import { useCollabApi } from '@/composables/collab-api'
import { useRoute } from 'vue-router'

const collabStore = useCollabStore()
const themeStore = useTheme()
const authStore = useAuthStore()
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
})

const emit = defineEmits(['close'])

const closeModal = () => {
    emit('close')
}

const newCollaborator = {
    email: '',
    accessRight: 'READ',
}

const deleteCollabBoard = async (collaboratorId) => {
    try {
        const response = await collabApi.deleteCollaborator(route.params.bid, collaboratorId)
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

const handleSubmit = async () => {
    if (props.action === 'add') {
        try {
            const response = await collabApi.addCollaborator(newCollaborator)
            if (response && response.success) {
                collabStore.addCollaborator(response.data) // Add it to the store if success
            }
        } catch (error) {
            console.error('Error adding collaborator:', error)
        }
    }
    closeModal()
}

onMounted(() => {
    if (props.action === 'update') {
        newCollaborator.name = props.collaborator.name
        newCollaborator.email = props.collaborator.email
        newCollaborator.accessRight = props.collaborator.accessRight
        newCollaborator.oid = props.collaborator.oid
    }
})
</script>

<template>
    <div class="fixed inset-0 flex justify-center items-center bg-black bg-opacity-60 w-full">
        <div class="shadow-lg rounded-lg p-6 w-full max-w-xl mx-6" :class="themeStore.getTheme()">
            <div class="flex justify-between items-center mb-4">
                <p v-if="props.action === 'add'" class="text-2xl font-semibold" :class="themeStore.getTextHeaderTheme()">Add Collaborator</p>
                <p v-if="props.action === 'delete'" class="text-2xl font-semibold" :class="themeStore.getTextHeaderTheme()">Remove Collaborator</p>
                <p v-if="props.action === 'update'" class="text-2xl font-semibold" :class="themeStore.getTextHeaderTheme()">Change access right</p>
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
                        <input type="email" id="title" placeholder="Enter e-mail" v-model="newCollaborator.email" class="w-full p-2 border border-gray-300 rounded-md" />
                    </div>
                    <div class="col-span-2">
                        <label for="accessRight" class="block mb-1 font-medium"> Access Right</label>
                        <select class="itbkk-status select select-bordered" id="accessRight" v-model="newCollaborator.accessRight">
                            <option value="READ">READ</option>
                            <option value="WRITE">WRITE</option>
                        </select>
                    </div>
                </div>
                <div v-if="props.action === 'delete'" class="font-medium">Do you want to remove "{{ props.collaborator.name }}" from the board?</div>
                <div v-if="props.action === 'update'" class="font-medium">
                    Do you want to change access right of "{{ props.collaborator.name }}" to "{{ props.collaborator.accessRight === 'WRITE' ? 'READ' : 'WRITE' }}" ?
                </div>
            </div>
            <hr class="my-3 border-gray-300 dark:border-gray-600" />
            <div class="flex justify-end gap-2">
                <button v-if="props.action === 'add'" @click="handleSubmit" class="px-4 py-2 text-black rounded-md bg-pink-500">Confirm</button>
                <button v-if="props.action === 'delete'" @click="deleteCollabBoard(props.collaborator.oid)" class="px-4 py-2 text-black rounded-md bg-pink-500">Delete</button>
                <button v-if="props.action === 'update'" @click="updateCollaborator(props.collaborator.oid, newCollaborator)" class="px-4 py-2 text-black rounded-md bg-pink-500">Confirm</button>
                <button @click="closeModal" class="px-4 py-2 text-black rounded-md bg-slate-100">Cancel</button>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
