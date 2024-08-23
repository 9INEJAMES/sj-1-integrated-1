<script setup>
import { ref } from 'vue'
import TaskTable from '@/components/TaskTable.vue'
import { useTasksStore } from '@/stores/task.js'
import VButton from '@/ui/VButton.vue'
import { useTaskApi } from '@/composables/task-api.js'
import { useTheme } from '@/stores/theme'
import StatusSetting from '@/components/StatusSetting.vue'
import FilterModal from '@/components/FilterModal.vue'
import { useStatusesStore } from '@/stores/status'
import { useLimitStore } from '@/stores/limitTask'
import { useAuthStore } from '@/stores/auth'
import { onMounted } from 'vue'

const base = import.meta.env.VITE_BASE
const taskApi = useTaskApi()
const taskStore = useTasksStore()
const isSelectTask = ref(false)
const themeStore = useTheme()
const selectedTask = ref({})
const isSettingOpen = ref(false)
const isFilterOpen = ref(false)
const statusStore = useStatusesStore()
const limitStore = useLimitStore()
const authStore = useAuthStore()

const chosenTask = async (id) => {
    selectedTask.value = await taskApi.getTaskById(id)
    isSelectTask.value = true
}
onMounted(async () => {
    if (taskStore.tasks.length <= 0) await taskStore.fetchTasks()
    if (statusStore.statuses.length <= 0) await statusStore.fetchStatuses()
    if (limitStore.limitTask.length <= 0) await limitStore.fetchLimit()
})
</script>

<template>
    <div class="flex justify-between pt-[5vh] pl-[5vh] pr-[5vh]">
        <div class="flex gap-2">
            <VButton @click="isSettingOpen = true" class="itbkk-status-setting" :iconurl="`${base ? base : ''}/settings.png`" />
            <RouterLink to="/status">
                <VButton msg="Manage Status" class="itbkk-manage-status" />
            </RouterLink>
        </div>
        <div class="flex gap-2">
            <VButton @click="isFilterOpen = true" msg="Filter" class="itbkk-status-filter" :iconurl="`${base ? base : ''}/filter.png`" />
            <RouterLink :to="{ name: 'taskAdd' }">
                <VButton class="itbkk-button-add" msg="Add Task" />
            </RouterLink>
        </div>
    </div>

    <RouterView class="z-30" />
    <div class="px-[5vh] pt-[1vh]">
        <div class="">
            <TaskTable></TaskTable>
        </div>
    </div>
    <StatusSetting v-if="isSettingOpen" @close="isSettingOpen = false" class="z-[45]"></StatusSetting>
    <FilterModal v-if="isFilterOpen" @close="isFilterOpen = false" class="z-40" @applyFilter="taskStore.fetchTasks"></FilterModal>
</template>

<style scoped></style>
