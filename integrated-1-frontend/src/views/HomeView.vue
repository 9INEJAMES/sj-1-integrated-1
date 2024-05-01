<script setup>
import { ref, onMounted, onUpdated, watch } from 'vue'
import TaskTable from '@/components/TaskTable.vue'
import { useTasksStore } from '@/stores/task.js'
import TaskDetailsPage from '@/components/TaskDetailsPage.vue'
import Addicon from '../../public/Addicon.vue'
import { useToast } from '@/stores/toast.js'
import { useTaskApi } from '@/composables/task-api.js'
import VToast from '@/ui/VToast.vue'
import { useTheme } from '@/stores/theme'

const taskApi = useTaskApi()
const myTasks = useTasksStore()
const myToast = useToast()
const isSelectTask = ref(false)
const taskList = ref([])
const myTheme = useTheme()

onMounted(async () => {
    if (myTasks.getTasks().length <= 0) {
        const tasksData = await taskApi.getAllTasks()
        myTasks.addTasks(tasksData)
    }
    if (myToast.currToast.style === 'alert-error') {
        myTasks.resetTasks()
        const tasksData = await taskApi.getAllTasks()
        myTasks.addTasks(tasksData)
        isSelectTask.value = false
    }
    taskList.value = myTasks.getTasks()
})

watch(myToast.currToast, async () => {
    console.log(myToast.currToast.style)
    if (myToast.currToast.style === 'alert-error') {
        myTasks.resetTasks()
        const tasksData = await taskApi.getAllTasks()
        myTasks.addTasks(tasksData)
        taskList.value = tasksData
        isSelectTask.value = false
    }
})
const selectedTask = ref({})
const chosenTask = async (id) => {
    selectedTask.value = await taskApi.getTaskById(id)
    isSelectTask.value = true // Update isSelectTask when a task is chosen
}

const handleUpdatedTask = () => {
    isSelectTask.value = false // Close the modal
    selectedTask.value = {}
    taskList.value = myTasks.getTasks()
}
</script>

<template>
    <TaskDetailsPage v-if="isSelectTask" :task="selectedTask" @closeModal="handleUpdatedTask" />
    <div class="px-[5vh] pt-[6vh]">
        <div class="">
            <TaskTable :taskList="taskList" @get-task="chosenTask"></TaskTable>
            <!-- <div v-else>No record</div> -->
        </div>
    </div>

    <RouterLink v-show="!isSelectTask" to="/task/add" class="itbkk-button-add"
        ><Addicon :class="myTheme.getAlterTheme()" class="fixed bottom-0 right-0 w-16 h-16 rounded-full p-2 m-5 transition-all ease-in hover:cursor-pointer" style="color: #443ad8"
    /></RouterLink>
    <VToast />
</template>

<style scoped></style>
