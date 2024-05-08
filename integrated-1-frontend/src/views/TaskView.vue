<script setup>
import { ref } from 'vue'
import TaskTable from '@/components/TaskTable.vue'
import { useTasksStore } from '@/stores/task.js'
import VButton from '@/components/VButton.vue'
import { useTaskApi } from '@/composables/task-api.js'
import { useTheme } from '@/stores/theme'
import router from '@/router'

const taskApi = useTaskApi()
const taskStore = useTasksStore()
const isSelectTask = ref(false)
const themeStore = useTheme()
const selectedTask = ref({})

const chosenTask = async (id) => {
    selectedTask.value = await taskApi.getTaskById(id)
    isSelectTask.value = true
}
const addTaskBtn = () => {
    router.push({
        name: 'taskAdd',
    })
}
</script>

<template>
    <div class="flex justify-between pt-[5vh] pl-[5vh] pr-[5vh]">
        <RouterLink to="/status"> <VButton msg="Manage Status" /> </RouterLink>
        <VButton msg="Add task" :class="themeStore.getAlterTheme()" @click="addTaskBtn" />
    </div>
    <RouterView class="z-40" />
    <div class="px-[5vh] pt-[1vh]">
        <div class="">
            <TaskTable @get-task="chosenTask"></TaskTable>
        </div>
    </div>
</template>

<style scoped></style>
