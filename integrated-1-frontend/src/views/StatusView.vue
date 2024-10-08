<script setup>
import { useStatusApi } from '@/composables/status-api'
import StatusTable from '@/components/StatusTable.vue'
import { useTheme } from '@/stores/theme'
import VButton from '@/ui/VButton.vue'
import StatusSetting from '@/components/StatusSetting.vue'
import { ref } from 'vue'
import { useStatusesStore } from '@/stores/status'
import { useBoardStore } from '@/stores/board'
import { onMounted } from 'vue'
import { useTasksStore } from '@/stores/task'
import { useAuthStore } from '@/stores/auth'
import { useRoute, useRouter } from 'vue-router'
import BoardModal from '@/components/BoardModal.vue'
import { useBoardApi } from '@/composables/board-api'
import VisibleModal from '@/components/VisibleModal.vue'

const route = useRoute()
const router = useRouter()
const base = import.meta.env.VITE_BASE
const isSettingOpen = ref(false)
const themeStore = useTheme()
const statusApi = useStatusApi()
const statusStore = useStatusesStore()
const boardStore = useBoardStore()
const taskStore = useTasksStore()
const authStore = useAuthStore()
const isCanEdit = ref(false)
const currentBoard = {}
const boardApi = useBoardApi()
const isViModal = ref(false)

const chosenStatus = async (id) => {
    selectedStatus.value = await statusApi.getStatusById(route.params.bid, id)
    isSelectStatus.value = true
}
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
    isCanEdit.value = authStore.checkToken() ? await authStore.isOwnerOrCollab(route.params.bid) : false
})
const changeBoardVisibility = async (isConfirm) => {
    if (isConfirm) {
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
    } else {
        currentBoard.value.isPublic = !currentBoard.value.isPublic
    }
    isViModal.value = false
}
const openVisibilityModal = () => {
    currentBoard.value.isPublic = !currentBoard.value.isPublic
    isViModal.value = true
}
</script>

<template>
    <div class="flex justify-between pt-[5vh] pl-[5vh] pr-[5vh]">
        <div class="flex gap-2">
            <div :class="!isCanEdit ? 'tooltip tooltip-right' : ''" data-tip="You need to be board owner to perform this action">
                <VButton :disabled="!isCanEdit" @click="isSettingOpen = true" class="itbkk-status-setting" :iconurl="`${base ? base : ''}/settings.png`" />
            </div>
            <VButton msg="Manage Task" class="itbkk-button-home" @click="$router.push({ name: 'taskView' })" />
        </div>
        <div class="flex gap-2">
            <VisibleModal v-if="isViModal" class="z-[45]" @closeModal="changeBoardVisibility"></VisibleModal>

            <div class="flex items-center" v-if="isCanEdit">
                <label class="flex cursor-pointer items-center gap-3 p-2 bg-slate-500 bg-opacity-10 rounded-lg shadow">
                    private
                    <input
                        id="theme"
                        type="checkbox"
                        value="synthwave"
                        class="itbkk-board-visibility toggle theme-controller hidden"
                        @click="openVisibilityModal()"
                        v-model="currentBoard.value.isPublic"
                    />
                    <div class="relative inline-block w-12 h-6 transition duration-200 ease-in bg-gray-300 rounded-full">
                        <span
                            class="absolute left-0 w-6 h-6 transition-transform duration-200 ease-in bg-white rounded-full transform"
                            :class="{ 'translate-x-full': currentBoard.value.isPublic }"
                        ></span>
                    </div>
                    public
                </label>
            </div>
            <div :class="!isCanEdit ? 'tooltip tooltip-left' : ''" data-tip="You need to be board owner to perform this action">
                <VButton msg="Manage Collaborator" class="itbkk-button-home mr-[1vh]" @click="$router.push({ name: 'collab' })" />
                <VButton :disabled="!isCanEdit" class="itbkk-button-add" msg="Add Status" @click="router.push({ name: 'statusAdd' })" />
            </div>
        </div>
    </div>

    <RouterView class="z-30" />
    <div class="px-[5vh] pt-[1vh]">
        <StatusTable @get-Status="chosenStatus"></StatusTable>
    </div>
    <StatusSetting v-if="isSettingOpen" @close="isSettingOpen = false" class="z-40"></StatusSetting>
</template>

<style lang="scss" scoped></style>
