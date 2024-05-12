<script setup>
import { ref, onMounted } from 'vue'
import { useStatusesStore } from '../stores/status.js'
import { useTheme } from '@/stores/theme'
import ConfirmDelete from './ConfirmDelete.vue'
import router from '../router/index.js'
import { useStatusApi } from '@/composables/status-api'

const statusesStore = useStatusesStore()
const themeStore = useTheme()
const deleteModal = ref(false)
const selectedIndex = ref(null)
const selectStatus = ref(null)
const statusApi = useStatusApi()
const base = import.meta.env.VITE_BASE

const emit = defineEmits(['getStatus'])

onMounted(async () => {
    if (statusesStore.statuses.length <= 0) {
        await statusesStore.fetchStatuses()
    }
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
        <ConfirmDelete v-if="deleteModal" mode="status" :object="selectStatus" :number="selectedIndex" @closeModal="handleDeleteModal" />
        <table class="myTable shadow-lg">
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
                    <td :class="$route.name != 'home' ? '' : 'itbkk-status-name'" class="font-bold h-[30px] text-[2vh] break-all text-black">
                        <button class="rounded-2xl w-[100px] h-[30px] text-[2vh] font-bold cursor-default" :style="{ backgroundColor: status.color }">
                            {{ status.name }}
                        </button>
                    </td>
                    <td :class="[$route.name !== 'home' ? '' : 'itbkk-status-description', status.description ? '' : 'italic text-gray-500']" class="break-all">
                        {{ status.description ? status.description : 'no description provide' }}
                    </td>
                    <td>
                        <div class="flex justify-center gap-1 overflow-scroll">
                            <div class="btn btn-sm itbkk-button-edit" :class="themeStore.getButtonTheme()" @click="toEditPage(status.id)">
                                Edit <img :src="`${base ? base : ''}/edit${themeStore.isLight ? '' : '2'}.png`" alt="edit picture" class="w-4 h-4" />
                            </div>
                            <div class="btn btn-sm itbkk-button-delete" :class="themeStore.getButtonTheme()" @click="deleteStatus(status, index + 1)">
                                Delete <img :src="`${base ? base : ''}/delete${themeStore.isLight ? '' : '2'}.png`" alt="delete picture" class="w-4 h-4" />
                            </div>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<style lang="scss" scoped></style>
