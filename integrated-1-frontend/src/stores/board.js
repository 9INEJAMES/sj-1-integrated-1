import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'
import { useBoardApi } from '@/composables/board-api'

export const useBoardStore = defineStore('board', () => {
    const boards = ref([])
    const boardApi = useBoardApi()
    const currBid = ref('')

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

    const findBoard = (bid) => {
        currBid.value = bid
        return boards.value.find((board) => board.id == bid)
    }
    return { currBid, boards, fetchBoard, getBoard, updateBoard, resetBoard, addBoard, addBoards, findBoard }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useBoardStore, import.meta.hot))
}
