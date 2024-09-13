import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'
import { useBoardApi } from '@/composables/board-api'

export const useBoardStore = defineStore('board', () => {
    const boards = ref([])
    const boardApi = useBoardApi()

    const fetchBoard = async () => {
        const data = await boardApi.getAllBoard()
        resetBoard()
        addBoards(data)
    }

    function updateBoard(updatedLimt) {
        boards.value = updatedLimt
    }

    function getBoard() {
        return boards.value
    }
    function resetBoard() {
        boards.value = []
    }

    function addBoards(newBoards) {
        newBoards.forEach((newBoard) => addBoard(newBoard))
    }

    async function addBoard(newBoard) {
        boards.value.push(newBoard)
    }
    return { boards, fetchBoard, getBoard, updateBoard, resetBoard, addBoard, addBoards }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useBoardStore, import.meta.hot))
}
