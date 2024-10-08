import { defineStore, acceptHMRUpdate } from "pinia"
import { ref } from "vue"
import { useBoardApi } from "@/composables/board-api"
import { useRoute } from "vue-router"

export const useBoardStore = defineStore("board", () => {
    const route = useRoute()
    const boards = ref([]) 
    const boardApi = useBoardApi()
    const filterBoardList = ref([])

    const fetchBoard = async () => {
        const data = await boardApi.getAllBoard()
        resetBoard()
        addBoards(data.person_boards) 
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

    const filterBoards = (oid) => {
        filterBoardList.value = boards.value.filter((board) => board.owner.oid === oid)
        return filterBoardList.value
    }

    const findBoard = (bid) => {
        return boards.value.find((board) => board.id === bid)
    }

    const findBoardIndex = (bid) => {
        return boards.value.findIndex((board) => board.id === bid)
    }

    const resetBoards = () => {
        boards.value = []
    }

    const getCurrentBoard = async () => {
        resetBoard()
        const board = await boardApi.getCurrentBoard()
        if (board) await addBoard(board)
        return boards.value[0]
    }

    return {
        boards,
        filterBoardList,
        fetchBoard,
        addBoard,
        updateBoard,
        removeBoard,
        findBoard,
        findBoardIndex,
        resetBoards,
        getBoard,
        filterBoards,
        getCurrentBoard,
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useBoardStore, import.meta.hot))
}
