import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'
import { useBoardApi } from '@/composables/board-api'
import { useRoute } from 'vue-router'

export const useBoardStore = defineStore('board', () => {
    const route = useRoute()
    const boards = ref([])
    const boardApi = useBoardApi()

    const fetchBoard = async (bid) => {
        const data = await boardApi.getAllBoard()
        resetBoard()
        addBoards(data)
        findBoard(bid)
    }

    function updateBoard(updatedBoard) {
        const index = findBoardIndex(updatedBoard.id)
        if (index !== -1) {
            boards.value.splice(index, 1, updatedBoard)
        }
    }

    function getBoard() {
        return findBoard(route.params.bid)
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

    function removeBoard(removeId) {
        const index = findBoardIndex(removeId)
        if (index !== -1) {
            boards.value.splice(index, 1)
        }
    }

    const findBoard = (bid) => {
        return boards.value.find((board) => board.id == bid)
    }
    const findBoardIndex = (bid) => {
        return boards.value.findIndex((board) => board.id == bid)
    }
    const findBoardName = (bid) => {
        const board = findBoard(bid)
        return board.name
    }
    const resetBoards = () => {
        boards.value = []
    }
    return { boards, fetchBoard, addBoard, updateBoard, removeBoard, findBoard, findBoardIndex, resetBoards, getBoard }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useBoardStore, import.meta.hot))
}
