<script setup>
import { useStatusApi } from '@/composables/status-api'
import StatusTable from '@/components/StatusTable.vue'
import { useTheme } from '@/stores/theme'
import VButton from '@/components/VButton.vue'
import router from '@/router'

const themeStore = useTheme()
const statusApi = useStatusApi()
const chosenStatus = async (id) => {
    selectedStatus.value = await statusApi.getStatusById(id)
    isSelectStatus.value = true
}

const addTaskBtn = () => {
    router.push({
        name: 'statusAdd',
    })
}
</script>

<template>
    <div class="flex justify-between pt-[5vh] pl-[5vh] pr-[5vh]">
        <RouterLink to="/"><VButton msg="Manage Task" /></RouterLink>
        <VButton msg="Add Status" :class="themeStore.getAlterTheme()" @click="addTaskBtn" />
    </div>

    <RouterView class="z-40" />
    <div class="px-[5vh] pt-[1vh]">
        <StatusTable @get-Status="chosenStatus"></StatusTable>
    </div>
</template>

<style lang="scss" scoped></style>
