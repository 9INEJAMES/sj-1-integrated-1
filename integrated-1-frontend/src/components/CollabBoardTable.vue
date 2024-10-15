<script setup>
import { useTheme } from '@/stores/theme.js'
import { useCollabStore } from '@/stores/collab'
import { useCollabApi } from '@/composables/collab-api'
import { useAuthStore } from '@/stores/auth'
import { ref } from 'vue'
import CollaboratorModal from '@/components/CollaboratorModal.vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const themeStore = useTheme()
const collabStore = useCollabStore()
const collabApi = useCollabApi()
const authStore = useAuthStore()

const showModal = ref(false)
const modalAction = ref('')
const selectedCollabBoard = ref(null)

const openModal = (action, cb = null) => {
    modalAction.value = action
    selectedCollabBoard.value = cb
    showModal.value = true
}

const closeCollabModal = () => {
    showModal.value = false
    modalAction.value = ''
    selectedCollabBoard.value = null
}

const boardSelector = async (bid) => {
    // boardStore.findBoard(bid)
    router.push({
        name: 'taskView',
        params: {
            bid: bid,
        },
    })
}
</script>

<template>
    <div>
        <table class="myTable table-lg table-pin-rows shadow-lg">
            <thead class="w-full">
                <tr class="text-lg" :class="themeStore.getTableTheme()">
                    <th style="width: 1%"></th>
                    <th style="width: 9%">No</th>
                    <th style="width: 30%">Name</th>
                    <th style="width: 20%">Owner</th>

                    <th style="width: 20%" class="text-center">Access Right</th>
                    <th style="width: 20%" class="text-center">Action</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(collabBoard, index) in collabStore.collabBoards" :key="collabBoard.id" class="itbkk-item">
                    <td>
                        <div class="flex"></div>
                    </td>
                    <td>
                        <div class="flex">
                            {{ index + 1 }}
                        </div>
                    </td>
                    <td :class="themeStore.isLight ? 'hover:text-pink-300' : 'hover:text-cyan-500'" class="itbkk-title h-[30px] break-all font-semibold" @click="boardSelector(collabBoard.id)">
                        {{ collabBoard.name }}
                    </td>
                    <td>{{ collabBoard.owner.name }}</td>

                    <td class="font-semibold text-center">
                        {{ collabBoard.accessRight }}
                    </td>
                    <td>
                        <div class="flex justify-center items-center h-full">
                            <button class="btn text-center" @click="openModal('leaveBoard', collabBoard)">Leave</button>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <CollaboratorModal v-if="showModal" @close="closeCollabModal" :action="modalAction" :collabBoard="selectedCollabBoard" class="z-[45]" />
</template>

<style lang="scss" scoped></style>
