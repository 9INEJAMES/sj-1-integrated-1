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

const chosenStatus = async (id) => {
    selectedStatus.value = await statusApi.getStatusById(id)
    isSelectStatus.value = true
}
onMounted(async () => {
    currentBoard.value = await boardStore.getCurrentBoard()
    // if (!authStore.checkToken() && boardStore.boards.length === 0) await boardStore.fetchBoard()
    if (taskStore.tasks.length === 0) await taskStore.fetchTasks(route.params.bid)
    if (statusStore.statuses.length === 0) await statusStore.fetchStatuses(route.params.bid)

    if (!currentBoard.value) {
        router.push({ name: 'boardView' })
    }

    // currentBoard.value = await boardStore.findBoard(route.params.bid)
    isCanEdit.value = authStore.checkToken() ? await authStore.isOwner(route.params.bid) : false
})
const changeBoardVisibility = async () => {
    if (route.params.bid) {
        currentBoard.value.isPublic = !currentBoard.value.isPublic
        const updated = await boardApi.updateBoard(currentBoard.value)
        boardStore.updateBoard({
            ...updated,
        })
        if (updated)
            boardStore.updateBoard({
                ...updated,
            })
    }
}
</script>

<template>
    <div class="flex justify-between pt-[5vh] pl-[5vh] pr-[5vh]">
        <div class="flex gap-2">
            <VButton :disabled="!isCanEdit" @click="isSettingOpen = true" class="itbkk-status-setting"
                :iconurl="`${base ? base : ''}/settings.png`" />
            <VButton msg="Manage Task" class="itbkk-button-home" @click="$router.push({ name: 'taskView' })" />
        </div>
        <div class="flex gap-2">
            <div class="flex items-center" v-if="isCanEdit">
                <label class="flex cursor-pointer items-center gap-3 p-2 bg-slate-500 bg-opacity-10 rounded-lg shadow">
                    private
                    <input id="theme" type="checkbox" value="synthwave" class="toggle theme-controller hidden"
                        @click="changeBoardVisibility()" v-model="currentBoard.value.isPublic" />
                    <div class="relative inline-block w-12 h-6 transition duration-200 ease-in bg-gray-300 rounded-full">
                        <span
                            class="absolute left-0 w-6 h-6 transition-transform duration-200 ease-in bg-white rounded-full transform"
                            :class="{ 'translate-x-full': currentBoard.value.isPublic }"></span>
                    </div>
                    public
                </label>
            </div>
            <VButton :disabled="!isCanEdit" class="itbkk-button-add" msg="Add Status"
                @click="router.push({ name: 'statusAdd' })" />
        </div>
    </div>

    <RouterView class="z-30" />
    <div class="px-[5vh] pt-[1vh]">
        <StatusTable @get-Status="chosenStatus"></StatusTable>
    </div>
    <StatusSetting v-if="isSettingOpen" @close="isSettingOpen = false" class="z-40"></StatusSetting>
</template>

<style lang="scss" scoped></style>
