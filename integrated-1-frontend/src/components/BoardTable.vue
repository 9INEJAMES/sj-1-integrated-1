<script setup>
import { ref, onMounted } from 'vue'
import ConfirmDelete from './ConfirmDelete.vue'
import router from '../router/index.js'
import { useBoardStore } from '../stores/board'
import { useTheme } from '@/stores/theme.js'
import { useAuthStore } from '@/stores/auth'

const themeStore = useTheme()
const deleteModal = ref(false)
const boardStore = useBoardStore()
const selectedBoard = ref(null)
const selectedIndex = ref(null)
const authStore = useAuthStore()
const props = defineProps({
    isCanEdit: Boolean,
})

onMounted(async () => {
    // if (authStore.checkToken()) await boardStore.fetchBoard()
})

const toEditPage = (bid) => {
    // authStore.checkToken()
    router.push({
        name: 'boardEdit',
        params: {
            bid: bid,
        },
    })
}

const toCollabPage = (bid) => {
    router.push({
        name: 'collab',
        params: {
            bid: bid,
        },
    })
}

const deleteBoard = (board, index) => {
    selectedBoard.value = board
    selectedIndex.value = index
    deleteModal.value = true
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

const handleDeleteModal = (confirmed) => {
    if (confirmed) {
        boardStore.removeBoard(selectedBoard.value.id)
        selectedBoard.value = null
        selectedIndex.value = null
    }
    deleteModal.value = false
}
</script>

<template>
    <div>
        <ConfirmDelete v-if="deleteModal" mode="board" :object="selectedBoard" :number="selectedIndex" @closeModal="handleDeleteModal" />
        <table class="myTable table-lg table-pin-rows shadow-lg">
            <thead class="w-full">
                <tr class="text-lg" :class="themeStore.getTableTheme()">
                    <th style="width: 1%"></th>
                    <th style="width: 9%">No</th>
                    <th style="width: 50%">Name</th>

                    <th style="width: 20%" class="text-center">Status</th>
                    <th style="width: 20%" class="text-center">Action</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(board, index) in boardStore.boards" :key="board.id" class="itbkk-item">
                    <td class="break-all"></td>
                    <td>
                        <div class="flex">
                            {{ index + 1 }}
                        </div>
                    </td>
                    <td
                        :class="themeStore.isLight ? 'hover:text-pink-300' : 'hover:text-cyan-500'"
                        class="itbkk-title font-semibold h-[30px] break-all hover:cursor-pointer"
                        @click="boardSelector(board.id)"
                    >
                        {{ board.name }}
                    </td>
                    <td class="flex justify-center items-center h-full font-semibold text-[2vh] break-all text-black">
                        <button class="rounded-2xl w-[100px] h-[30px] text-[2vh] font-semibold cursor-default" :class="board.visibility == 'PUBLIC' ? 'bg-green-400' : 'bg-red-400'">
                            {{ board.visibility }}
                        </button>
                    </td>
                    <td>
                        <div class="flex justify-center items-center h-full">
                            <div class="itbkk-button-action dropdown dropdown-hover">
                                <div tabindex="0" role="button">
                                    <img src="/element/dots.png" class="w-[3vh] h-[3vh]" alt="list img" />
                                </div>
                                <ul tabindex="0" class="dropdown-content z-[1] w-fit menu p-2 shadow bg-base-100 rounded-box" :class="themeStore.getAlterTheme()">
                                    <li>
                                        <p :disabled="!isCanEdit" class="itbkk-button-edit" @click="toEditPage(board.id)">Edit</p>
                                    </li>
                                    <li>
                                        <p :disabled="!isCanEdit" class="itbkk-button-delete" @click="deleteBoard(board, index + 1)">Delete</p>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<style scoped></style>
