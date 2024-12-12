<script setup>
import { ref } from 'vue'
import TaskTable from '@/components/TaskTable.vue'
import { useTasksStore } from '@/stores/task.js'
import VButton from '@/ui/VButton.vue'
import StatusSetting from '@/components/StatusSetting.vue'
import FilterModal from '@/components/FilterModal.vue'
import { useStatusesStore } from '@/stores/status'
import { useBoardStore } from '@/stores/board'
import { useAuthStore } from '@/stores/auth'
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useBoardApi } from '@/composables/board-api'
import VisibleModal from '@/components/VisibleModal.vue'

const route = useRoute()
const router = useRouter()
const base = import.meta.env.VITE_BASE
const taskStore = useTasksStore()
const statusStore = useStatusesStore()
const boardStore = useBoardStore()
const isSettingOpen = ref(false)
const isFilterOpen = ref(false)
const isCanEdit = ref(false)
const currentBoard = { value: { isPublic: true } }
const boardApi = useBoardApi()
const isViModal = ref(false)
const isOwner = ref(false)

const authStore = useAuthStore()
onMounted(async () => {
    currentBoard.value = await boardStore.getCurrentBoard()
    currentBoard.value.isPublic = currentBoard.value.visibility === 'PRIVATE' ? false : true
    // if (!authStore.checkToken() && boardStore.boards.length === 0) await boardStore.fetchBoard()
    if (taskStore.tasks.length === 0) await taskStore.fetchTasks(route.params.bid)
    if (statusStore.statuses.length === 0) await statusStore.fetchStatuses(route.params.bid)

    if (!currentBoard.value) {
        router.push({ name: 'boardView' })
    }

    // currentBoard.value = await boardStore.findBoard(route.params.bid)
    isCanEdit.value = authStore.checkToken() ? await authStore.isEditor(route.params.bid) : false
    isOwner.value = authStore.checkToken() ? await authStore.isOwner(route.params.bid) : false
})
const changeBoardVisibility = async (isConfirm) => {
    if (isConfirm) {
        currentBoard.value.isPublic = !currentBoard.value.isPublic

        currentBoard.value.visibility = currentBoard.value.isPublic ? 'PUBLIC' : 'PRIVATE'
        if (route.params.bid) {
            const updated = await boardApi.updateBoardVisibility(currentBoard.value)
            boardStore.updateBoard({
                ...updated,
            })
            if (updated)
                boardStore.updateBoard({
                    ...updated,
                })
        }
    }
    isViModal.value = false
}
const openVisibilityModal = () => {
    isViModal.value = true
}
const fetchFilter = async (filter) => {
    await taskStore.fetchTasks(route.params.bid, filter)
}
</script>

<template>
    <div class="flex justify-between pt-[5vh] pl-[5vh] pr-[5vh]">
        <div class="flex gap-2">
            <div :class="!isCanEdit ? 'tooltip tooltip-right' : ''" data-tip="You need to be board owner to perform this action">
                <VButton :disabled="!isCanEdit" @click="isSettingOpen = true" class="itbkk-status-setting" :iconurl="`${base ? base : ''}/settings.png`" />
            </div>
            <VButton msg="Manage Status" class="itbkk-manage-status" @click="$router.push({ name: 'statusView' })" />
        </div>
        <div class="flex gap-2">
            <VisibleModal v-if="isViModal" :msg="!currentBoard.value.isPublic" class="z-[45]" @closeModal="changeBoardVisibility"></VisibleModal>

            <div :class="!isOwner ? 'tooltip tooltip-left' : ''" data-tip="You need to be board owner to perform this action">
                <button class="btn flex items-center" :disabled="!isOwner" @click="openVisibilityModal()">
                    <svg width="24" height="24" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path
                            v-if="currentBoard?.value?.isPublic"
                            fill="currentColor"
                            d="M12 22q-2.075 0-3.9-.788t-3.175-2.137T2.788 15.9T2 12t.788-3.9t2.137-3.175T8.1 2.788T12 2t3.9.788t3.175 2.137T21.213 8.1T22 12t-.788 3.9t-2.137 3.175t-3.175 2.138T12 22m-1-2.05V18q-.825 0-1.412-.587T9 16v-1l-4.8-4.8q-.075.45-.137.9T4 12q0 3.025 1.988 5.3T11 19.95m6.9-2.55q.5-.55.9-1.187t.662-1.325t.4-1.413T20 12q0-2.45-1.363-4.475T15 4.6V5q0 .825-.587 1.413T13 7h-2v2q0 .425-.288.713T10 10H8v2h6q.425 0 .713.288T15 13v3h1q.65 0 1.175.388T17.9 17.4"
                        />
                        <path
                            v-else
                            fill="currentColor"
                            d="M13 0C9.155 0 6 3.155 6 7v1H5c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2h-1V7c0-3.845-3.155-7-7-7m0 2c2.755 0 5 2.245 5 5v1H8V7c0-2.755 2.245-5 5-5m0 9c2.8 0 5 2.2 5 5s-2.2 5-5 5s-5-2.2-5-5s2.2-5 5-5m0 2c-1.7 0-3 1.3-3 3s1.3 3 3 3s3-1.3 3-3c0-.3-.094-.606-.094-.906c-.3.5-.806.906-1.406.906c-.8 0-1.5-.7-1.5-1.5c0-.6.406-1.106.906-1.406c-.3 0-.606-.094-.906-.094"
                        /></svg
                    >{{ currentBoard?.value?.isPublic ? 'Public' : 'Private' }}
                </button>
            </div>

            <VButton @click="isFilterOpen = true" msg="Filter" class="itbkk-status-filter" :iconurl="`${base ? base : ''}/filter.png`" />
            <VButton msg="Manage Collaborator" class="itbkk-button-home mr-[1vh]" @click="router.push({ name: 'collabView' })" />
            <div :class="!isCanEdit ? 'tooltip tooltip-left' : ''" data-tip="You need to be board owner to perform this action">
                <VButton :disabled="!isCanEdit" class="itbkk-button-add" msg="Add Task" @click="router.push({ name: 'taskAdd' })" />
            </div>
        </div>
    </div>

    <RouterView class="z-30" />
    <div class="px-[5vh] pt-[1vh]">
        <div class="">
            <TaskTable :isCanEdit="isCanEdit"></TaskTable>
        </div>
    </div>
    <StatusSetting v-if="isSettingOpen" @close="isSettingOpen = false" class="z-[45]"></StatusSetting>
    <FilterModal v-if="isFilterOpen" @close="isFilterOpen = false" class="z-40" @applyFilter="fetchFilter"></FilterModal>
</template>

<style scoped></style>
