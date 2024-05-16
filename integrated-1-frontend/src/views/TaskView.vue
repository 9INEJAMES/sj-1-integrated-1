<script setup>
import { ref } from 'vue'
import TaskTable from '@/components/TaskTable.vue'
import { useTasksStore } from '@/stores/task.js'
import VButton from '@/ui/VButton.vue'
import { useTaskApi } from '@/composables/task-api.js'
import { useTheme } from '@/stores/theme'
import router from '@/router'
import StatusSetting from '@/components/StatusSetting.vue'
import FilterModal from '@/components/FilterModal.vue'

const taskApi = useTaskApi()
const taskStore = useTasksStore()
const isSelectTask = ref(false)
const themeStore = useTheme()
const selectedTask = ref({})
const isSettingOpen = ref(false)
const isFilterOpen = ref(false)  

const chosenTask = async (id) => {
    selectedTask.value = await taskApi.getTaskById(id)
    isSelectTask.value = true
}

const filttedTasks = ref([])
</script>

<template>
    <div class="flex justify-between pt-[5vh] pl-[5vh] pr-[5vh]">
        <div class="flex gap-2">
            <VButton @click="isSettingOpen = true" class="itbkk-status-setting" iconurl="/settings.png" />
            <RouterLink to="/status">
                <VButton msg="Manage Status" class="itbkk-manage-status" />
            </RouterLink>
        </div>
        <div class="flex gap-2">
            <VButton @click="isFilterOpen  = true" msg="Filter" class="itbkk-filter" iconurl="/filter.png" />
            <RouterLink :to="{ name: 'taskAdd' }">
                <VButton class="itbkk-button-add" msg="Add Task" />
            </RouterLink>
        </div>
    </div>
    <RouterView class="z-30" />
    <div class="px-[5vh] pt-[1vh]">
        <div class="">
            <TaskTable @get-task="chosenTask"></TaskTable>
        </div>
    </div>
    <StatusSetting v-if="isSettingOpen" @close="isSettingOpen = false" class="z-40"></StatusSetting>
    <FilterModal :isVisible="isFilterOpen" @close="isFilterOpen = false"></FilterModal> <!-- Include FilterModal -->
</template>

<style scoped></style>
