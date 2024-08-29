<script setup>
import { useStatusApi } from '@/composables/status-api'
import StatusTable from '@/components/StatusTable.vue'
import { useTheme } from '@/stores/theme'
import VButton from '@/ui/VButton.vue'
import StatusSetting from '@/components/StatusSetting.vue'
import { ref } from 'vue'
import { useStatusesStore } from '@/stores/status'
import { useLimitStore } from '@/stores/limitTask'
import { onMounted } from 'vue'
import { useTasksStore } from '@/stores/task'
import { useAuthStore } from '@/stores/auth'

const base = import.meta.env.VITE_BASE
const isSettingOpen = ref(false)
const themeStore = useTheme()
const statusApi = useStatusApi()
const statusStore = useStatusesStore()
const limitStore = useLimitStore()
const taskStore = useTasksStore()
const authStore = useAuthStore()

const chosenStatus = async (id) => {
    selectedStatus.value = await statusApi.getStatusById(id)
    isSelectStatus.value = true
}
onMounted(async () => {
    authStore.checkToken()
    if (taskStore.tasks.length <= 0) await taskStore.fetchTasks()
    if (statusStore.statuses.length <= 0) await statusStore.fetchStatuses()
    if (limitStore.limitTask.length <= 0) await limitStore.fetchLimit()
})
</script>

<template>
    <div class="flex justify-between pt-[5vh] pl-[5vh] pr-[5vh]">
        <div class="flex gap-2">
            <VButton @click="isSettingOpen = true" class="itbkk-status-setting" :iconurl="`${base ? base : ''}/settings.png`" />
            <RouterLink to="/task"><VButton msg="Manage Task" class="itbkk-button-home" /></RouterLink>
        </div>
        <RouterLink :to="{ name: 'statusAdd' }">
            <VButton class="itbkk-button-add" msg="Add Status" />
        </RouterLink>
    </div>

    <RouterView class="z-30" />
    <div class="px-[5vh] pt-[1vh]">
        <StatusTable @get-Status="chosenStatus"></StatusTable>
    </div>
    <StatusSetting v-if="isSettingOpen" @close="isSettingOpen = false" class="z-40"></StatusSetting>
</template>

<style lang="scss" scoped></style>
