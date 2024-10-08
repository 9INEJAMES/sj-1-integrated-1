import { defineStore, acceptHMRUpdate } from "pinia"
import { ref } from "vue"
import { useAuthStore } from "@/stores/auth"
import { useBoardApi } from "@/composables/board-api"

export const useCollabStore = defineStore("collab", () => {
    const collabBoards = ref([])
    const authStore = useAuthStore()
    const boardApi = useBoardApi()

    const fetchCollabBoards = async () => {
        const response = await boardApi.getAllBoard() // Call the method correctly

        if (response && response.collab_boards) {
            resetCollabBoards()
            addCollabBoards(response.collab_boards) // Pass the collab_boards array
        } else {
            console.error("Expected response with collab_boards, but got:", response)
        }
    }

    const addCollabBoards = (newBoard) => {
        newBoard.forEach((newBoard) => addCollabBoard(newBoard))
    }

    async function addCollabBoard(newBoard) {
        collabBoards.value.push(newBoard)
    }

    const resetCollabBoards = () => {
        collabBoards.value = [] // Reset the array
    }

    return { collabBoards, fetchCollabBoards, resetCollabBoards }
})

// Hot module replacement support for the store
if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useCollabStore, import.meta.hot))
}
