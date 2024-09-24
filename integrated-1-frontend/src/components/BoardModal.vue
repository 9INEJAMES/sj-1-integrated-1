<script setup>
import { useRoute, useRouter } from 'vue-router'
import { onMounted, ref, watch } from 'vue'
import { useTheme } from '@/stores/theme.js'
import { useBoardApi } from '@/composables/board-api'
import { useBoardStore } from '@/stores/board'
import { useAuthStore } from '@/stores/auth'
const themeStore = useTheme()

const route = useRoute()
const router = useRouter()
const boardApi = useBoardApi()
const boardStore = useBoardStore()
const isDisabled = ref(false)
const isChanged = ref(false)
const authStore = useAuthStore()
const user = authStore.getAuthData()
const isCanEdit = ref(true)

const newBoard = ref({
    boardId: '',
    name: user.name + ' personal board',
    isPublic: false,
})
const oldBoard = ref({
    boardId: '',
    name: user.name + ' personal board',
    isPublic: false,
})

const submitBoard = async (isSave) => {
    if (isSave) {
        if (route.params.bid) {
            const updated = await boardApi.updateBoard(newBoard.value)
            boardStore.updateBoard({
                ...updated,
            })
            if (updated)
                boardStore.updateBoard({
                    ...updated,
                })
        } else {
            const board = await boardApi.createBoard(newBoard.value)

            if (board) boardStore.addBoard(board)
        }
    }
    newBoard.value = {
        boardId: '',
        name: user.name + ' personal board',
        isPublic: false,
    }

    //onlyone board
    // if (boardStore.boards.length === 1) {
    //     router.push({ name: 'taskView', params: { bid: boardStore.boards[0].id } })
    // }
    router.back()
}
const changeBoardVisibility = () => {
    newBoard.value.isPublic = !newBoard.value.isPublic
    console.log(newBoard.value.isPublic)
}

onMounted(async () => {
    // authStore.checkToken()
    await boardStore.fetchBoard(route.params.bid)

    if (route.name !== 'boardAdd') {
        // if (boardStore.boards.length === 0) await boardStore.fetchBoard()
        oldBoard.value = { ...boardStore.findBoard(route.params.bid) }
        newBoard.value = { ...boardStore.findBoard(route.params.bid) }
        isCanEdit.value = await authStore.isOwner(route.params.bid)
    }

})

watch(
    newBoard,
    () => {
        if (JSON.stringify(newBoard.value) === JSON.stringify(oldBoard.value)) isChanged.value = false
        else isChanged.value = true
    },
    { deep: true }
)
</script>

<template>
    <div class="itbkk-modal-new fixed inset-0 flex justify-center items-center bg-black bg-opacity-60 w-full">
        <div class="itbkk-modal-task shadow-lg rounded-lg p-6 w-full max-w-lg mx-6" :class="themeStore.getTheme()">
            <div class="flex justify-between items-center mb-4">
                <p v-if="$route.name == 'boardAdd'" class="text-2xl font-semibold" :class="themeStore.getTextHeaderTheme()">Create New Board</p>
                <p v-if="$route.name == 'boardDelete'" class="text-2xl font-semibold" :class="themeStore.getTextHeaderTheme()">Delete Board</p>
                <p v-if="$route.name == 'boardEdit'" class="text-2xl font-semibold" :class="themeStore.getTextHeaderTheme()">Edit Board</p>
                <button class="text-gray-500 hover:text-gray-700 dark:text-gray-300 dark:hover:text-gray-100" @click="router.back()">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-6 h-6">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                </button>
            </div>

            <hr class="my-3 border-gray-300 dark:border-gray-600" />

            <div class="mb-5">
                <label for="title" class="block mb-1 font-medium"> {{ $route.name == 'boardAdd' ? 'Name' : 'Change board name' }}<span class="text-red-600">*</span> </label>
                <input
                    v-model="newBoard.name"
                    type="text"
                    id="title"
                    class="itbkk-board-name block w-full p-3 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring focus:ring-indigo-500 bg-gray-50 dark:bg-gray-700 dark:text-gray-200 dark:border-gray-600"
                    placeholder="Enter your board name"
                    :class="themeStore.getTheme()"
                    :disabled="isDisabled"
                />
            </div>

            <div class="flex items-center" v-if="isCanEdit">
                <label class="flex cursor-pointer items-center gap-3 p-2 bg-slate-500 bg-opacity-10 rounded-lg shadow">
                    private
                    <input id="theme" type="checkbox" value="synthwave" class="toggle theme-controller hidden" @click="changeBoardVisibility()" v-model="newBoard.isPublic" />
                    <div class="relative inline-block w-12 h-6 transition duration-200 ease-in bg-gray-300 rounded-full">
                        <span class="absolute left-0 w-6 h-6 transition-transform duration-200 ease-in bg-white rounded-full transform" :class="{ 'translate-x-full': newBoard.isPublic }"></span>
                    </div>
                    public
                </label>
            </div>

            <div class="flex justify-end space-x-4">
                <button
                    class="itbkk-button-cancel px-4 py-2 text-white bg-red-500 hover:bg-red-600 rounded-md transition duration-200 ease-in-out focus:outline-none focus:ring focus:ring-red-300 dark:focus:ring-red-500"
                    @click="submitBoard(false)"
                >
                    Cancel
                </button>
                <button
                    class="itbkk-button-ok px-4 py-2 text-white bg-green-500 hover:bg-green-600 rounded-md transition duration-200 ease-in-out focus:outline-none focus:ring focus:ring-green-300 dark:focus:ring-green-500 disabled:bg-slate-300"
                    :class="
                        newBoard.name.length <= 0 || ($route.name == 'boardEdit' && !isChanged) || (newBoard.name.trim().length <= 0 && $route.name == 'boardAdd') || !isCanEdit
                            ? 'disabled'
                            : ''
                    "
                    :disabled="newBoard.name.length <= 0 || ($route.name == 'boardEdit' && !isChanged) || !isCanEdit"
                    @click="submitBoard(true)"
                >
                    {{ $route.name == 'boardAdd' ? 'Create' : 'Save' }}
                </button>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
