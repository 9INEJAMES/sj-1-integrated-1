<script setup>
import { ref, onMounted } from 'vue'
import { useStatusesStore } from '../stores/status.js'
import { useTheme } from '@/stores/theme'
import ConfirmDelete from './ConfirmDelete.vue'
import router from '../router/index.js'
import { useStatusApi } from '@/composables/status-api'
import { useRoute } from 'vue-router'
import { useTasksStore } from '../stores/task.js'

const statusesStore = useStatusesStore()
const themeStore = useTheme()
const deleteModal = ref(false)
const selectedIndex = ref(null)
const selectStatus = ref(null)
const statusApi = useStatusApi()
const base = import.meta.env.VITE_BASE
const route = useRoute()
const taskStore = useTasksStore()

const emit = defineEmits(['getStatus'])

onMounted(async () => {
    if (taskStore.tasks.length === 0) await taskStore.fetchTasks(route.params.bid)
    if (statusesStore.statuses.length === 0) await statusesStore.fetchStatuses(route.params.bid)
})
const toEditPage = (id) => {
    router.push({
        name: 'statusEdit',
        params: {
            id: id,
        },
    })
}

const deleteStatus = async (status, index) => {
    const status_1 = await statusApi.getStatusById(status.id)
    if (status_1) {
        selectStatus.value = status_1
        selectedIndex.value = index
        deleteModal.value = true
    }
}

const handleDeleteModal = () => {
    deleteModal.value = false
    selectStatus.value = null
    selectedIndex.value = null
}

</script>

<template>
    <div class="">
        <ConfirmDelete v-if="deleteModal" mode="status" :object="selectStatus" :number="selectedIndex"
            @closeModal="handleDeleteModal" />
        <table class="myTable table-pin-rows shadow-lg">
            <thead>
                <tr class="text-lg" :class="themeStore.getTableTheme()">
                    <th style="width: 5%"></th>
                    <th style="width: 25%">Name</th>
                    <th style="width: 45%">Description</th>
                    <th style="width: 25%" class="text-center">Action</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(status, index) in statusesStore.statuses" :key="status.id" class="itbkk-item">
                    <td>
                        <div class="flex">
                            {{ index + 1 }}
                        </div>
                    </td>
                    <td class="itbkk-status-name font-bold h-[30px] text-[2vh] break-all text-black">
                        <button class="rounded-2xl w-[100px] h-[30px] text-[2vh] font-bold cursor-default"
                            :style="{ backgroundColor: status.color }">
                            {{ status.name }}
                        </button>
                    </td>
                    <td :class="status.description ? '' : 'italic text-gray-500'"
                        class="itbkk-status-description break-all">
                        {{ status.description ? status.description : 'No description is provided' }}
                    </td>
                    <td>
                        <div class="flex justify-center gap-1">
                            <div class="itbkk-button-edit btn btn-sm" :class="themeStore.getButtonTheme()"
                                @click="toEditPage(status.id)">
                                Edit <img :src="`${base ? base : ''}/edit${themeStore.isLight ? '' : '2'}.png`"
                                    alt="edit picture" class="w-4 h-4" />
                            </div>
                            <div class="itbkk-button-delete btn btn-sm" :class="themeStore.getButtonTheme()"
                                @click="deleteStatus(status, index + 1)">
                                Delete <img :src="`${base ? base : ''}/delete${themeStore.isLight ? '' : '2'}.png`"
                                    alt="delete picture" class="w-4 h-4" />
                            </div>
                        </div>
                    </td>
                </tr>

            </tbody>
    </table>
</div></template>

<style lang="scss" scoped></style>
