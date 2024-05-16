<script setup>
import { useStatusApi } from '@/composables/status-api'
import StatusTable from '@/components/StatusTable.vue'
import { useTheme } from '@/stores/theme'
import VButton from '@/ui/VButton.vue'
import router from '@/router'

const themeStore = useTheme()
const statusApi = useStatusApi()
const chosenStatus = async (id) => {
    selectedStatus.value = await statusApi.getStatusById(id)
    isSelectStatus.value = true
}
</script>

<template>
    <div class="flex justify-between pt-[5vh] pl-[5vh] pr-[5vh]">
        <div class="flex gap-2">
            <VButton @click="isSettingOpen = true" class="itbkk-status-setting" iconurl="/settings.png" />
            <RouterLink to="/"><VButton msg="Manage Task" class="itbkk-button-home" /></RouterLink>
        </div>
        <RouterLink :to="{ name: 'statusAdd' }">
            <VButton class="itbkk-button-add" msg="Add Status" />
        </RouterLink>
    </div>

    <RouterView class="z-30" />
    <div class="px-[5vh] pt-[1vh]">
        <StatusTable @get-Status="chosenStatus"></StatusTable>
    </div>
</template>

<style lang="scss" scoped></style>
