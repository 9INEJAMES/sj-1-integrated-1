import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useBoardApi } from '@/composables/board-api'
import { useCollabApi } from '@/composables/collab-api'

export const useCollabStore = defineStore('collab', () => {
    // Board is here
    const collabBoards = ref([])
    const authStore = useAuthStore()
    const boardApi = useBoardApi()
    const collabApi = useCollabApi()

    const fetchCollabBoards = async () => {
        const response = await boardApi.getAllBoard() // Call the method correctly
        resetCollabBoards()
        addCollabBoards(response.collab_boards)
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

    const removeCollabBoard = (boardId) => {
        const index = collabBoards.value.findIndex((board) => board.id === boardId)
        collabBoards.value.splice(index, 1)
    }

    // This is the collaborator

    const collaborators = ref([])

    const fetchCollaborator = async (bid) => {
        resetCollaborator()
        const data = await collabApi.getAllCollaborator(bid)
        addCollaborators(data)
    }

    function getCollaborator() {
        return findCollaborator(route.params.bid)
    }

    function resetCollaborator() {
        collaborators.value = []
    }

    function addCollaborators(newCollaborators) {
        newCollaborators.forEach((newCollaborator) => addCollaborator(newCollaborator))
    }

    async function addCollaborator(newCollaborator) {
        collaborators.value.push(newCollaborator)
    }

    function findCollaborator(oid) {
        return collaborators.value.find((collaborator) => collaborator.oid === oid)
    }

    function removeCollaborator(collaboratorId) {
        const index = findCollaboratorIndex(collaboratorId)
        if (index !== -1) {
            collaborators.value.splice(index, 1)
        }
    }

    const findCollaboratorIndex = (oid) => {
        return collaborators.value.findIndex((collaborator) => collaborator.oid === oid)
    }

    function updateCollaborator(updateCollaboratorId, updateCollaborator) {
        const index = findCollaboratorIndex(updateCollaboratorId)
        if (index !== -1) {
            collaborators.value.splice(index, 1, updateCollaborator)
        }
    }


    return {
        collabBoards,
        collaborators,
        fetchCollabBoards,
        resetCollabBoards,
        removeCollabBoard,
        fetchCollaborator,
        getCollaborator,
        resetCollaborator,
        addCollaborators,
        addCollaborator,
        removeCollaborator,
        findCollaboratorIndex,
        updateCollaborator
    }
})

// Hot module replacement support for the store
if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useCollabStore, import.meta.hot))
}
