<script setup>
import { ref, onMounted } from 'vue'
import { useTheme } from '@/stores/theme.js'
import { useCollabStore } from '@/stores/collab'
import { useAuthStore } from '@/stores/auth'
import { useRoute } from 'vue-router'
import CollaboratorModal from '@/components/CollaboratorModal.vue'
import VButton from '@/ui/VButton.vue'
import router from '@/router'

const themeStore = useTheme()
const collabStore = useCollabStore()
const authStore = useAuthStore()
const route = useRoute()

const showModal = ref(false)
const modalAction = ref('')
const selectedCollaborator = ref(null) // Store the selected collaborator for deletion\
const isCanEdit = ref(false)

const openModal = (action, collaborator = null, accessRight = null) => {
    modalAction.value = action
    selectedCollaborator.value = collaborator // Set the selected collaborator for deletion
    showModal.value = true
}

const closeCollabModal = () => {
    // collabStore.fetchCollaborator(route.params.bid)

    showModal.value = false
    modalAction.value = ''
    selectedCollaborator.value = null // Clear the selected collaborator
}

onMounted(async () => {
    collabStore.fetchCollaborator(route.params.bid)
    isCanEdit.value = authStore.checkToken() ? await authStore.isOwner(route.params.bid) : false
})
</script>

<template>
    <div class="text-center text-3xl font-bold pt-[4vh]">Collaborator Management</div>
    <div class="flex justify-between pt-[2vh] pr-[5vh]">
        <div class="flex">
            <div class="pl-[5vh] pr-[1vh] text-2xl font-bold text-blue-500 hover:cursor-pointer hover:text-blue-800" @click="router.back">
                {{ authStore.getAuthData()?.name ? authStore.getAuthData()?.name : 'GUEST' }}
            </div>
            <div class="text-2xl font-bold">> Collaborator</div>
        </div>
        <!-- <button class="btn" :disabled="!isCanEdit" @click="openModal('add')">Add collaborator</button> -->
        <div :class="(themeStore.getButtonTheme(), !isCanEdit ? 'disabled tooltip tooltip-left' : '')" :data-tip="'You need to be board owner to perform this action'">
            <VButton :disabled="!isCanEdit" class="itbkk-button-add" msg="Add collaborator" @click="openModal('add')" />
        </div>
    </div>
    <div class="flex justify-between pt-[1vh] pl-[5vh] pr-[5vh]">
        <table class="myTable table-lg shadow-lg">
            <thead class="w-full">
                <tr class="text-lg" :class="themeStore.getTableTheme()">
                    <th style="width: 1%">No</th>
                    <th style="width: 25%">Name</th>
                    <th style="width: 20%">Email</th>
                    <th style="width: 20%" class="text-center">Status</th>
                    <th style="width: 20%" class="text-center">Access Right</th>
                    <th style="width: 14%" class="text-center">Action</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(collaborator, index) in collabStore.collaborators" :key="collaborator.oid">
                    <td>
                        <div class="flex">{{ index + 1 }}</div>
                    </td>
                    <td>{{ collaborator.name }}</td>
                    <td>{{ collaborator.email }}</td>
                    <td class="text-center h-full break-all text-black">
                        <button class="rounded-2xl w-[100px] h-[30px] font-semibold cursor-default" :class="collaborator.status == 'JOINED' ? 'bg-green-400' : 'bg-yellow-300'">
                            {{ collaborator.status }}
                        </button>
                    </td>
                    <td class="text-center">
                        <div class="dropdown">
                            <div :class="(themeStore.getButtonTheme(), !isCanEdit ? 'disabled tooltip tooltip-left' : '')" :data-tip="'You need to be board owner to perform this action'">
                                <button tabindex="0" :disabled="!isCanEdit" role="button" class="btn m-1">{{ collaborator.accessRight }} v</button>
                                <ul tabindex="0" class="dropdown-content menu bg-base-100 rounded-box z-[1] w-52 p-2 shadow">
                                    <li>
                                        <a>{{ collaborator.accessRight }}</a>
                                    </li>
                                    <li @click="openModal('update', collaborator)">
                                        <a>{{ collaborator.accessRight === 'WRITE' ? 'READ' : 'WRITE' }}</a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </td>
                    <td class="text-center">
                        <div
                            :class="(themeStore.getButtonTheme(), authStore.getAuthData()?.oid !== collaborator?.oid && !isCanEdit ? 'disabled tooltip tooltip-left' : '')"
                            :data-tip="'You need to be board owner to perform this action'"
                        >
                            <button
                                class="btn"
                                :disabled="authStore.getAuthData()?.oid !== collaborator?.oid && !isCanEdit"
                                @click="openModal(collaborator.status == 'JOINED' ? 'delete' : 'cancel', collaborator)"
                            >
                                {{ collaborator.status == 'JOINED' ? 'Remove' : 'Cancel' }}
                            </button>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <CollaboratorModal v-if="showModal" @close="closeCollabModal" :action="modalAction" :collaborator="selectedCollaborator" class="z-45" />
</template>

<style scoped></style>
