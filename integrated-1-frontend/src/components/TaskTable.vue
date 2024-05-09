<script setup>
import { ref, onMounted } from 'vue'
import ConfirmDelete from './ConfirmDelete.vue'
import router from '../router/index.js'
import { useTasksStore } from '../stores/task.js'
import { useTheme } from '@/stores/theme.js'
import { useStatusesStore } from '@/stores/status'
const themeStore = useTheme()
const deleteModal = ref(false)
const updateModal = ref(false)
const taskStore = useTasksStore()
const selectedTask = ref(null)
const selectedIndex = ref(null)
const statusesStore = useStatusesStore()
const emit = defineEmits(['getTask'])

const getTask = (id) => {
    router.push({
        name: 'taskDetails',
        params: {
            taskId: id,
        },
    })
    // emit('getTask', id)
}

const colorStatus = (taskstatus) => {
    const status = statusesStore.getStatuses().find((s) => {
        return s.name.toUpperCase() == taskstatus.toUpperCase()
    })
    return status ? status.color : ''
}

const toEditPage = (id) => {
    router.push({
        name: 'taskEdit',
        params: {
            taskId: id,
        },
    })
    if (selectedTask.value === null) {
        selectedTask.value = taskStore.getIdOfTask(id)
    }
    updateModal.value = true
}

const deleteTask = (task, index) => {
    selectedTask.value = task
    selectedIndex.value = index
    deleteModal.value = true
}

const handleDeleteModal = () => {
    deleteModal.value = false
    selectedTask.value = null
    selectedIndex.value = null
}
</script>

<template>
    <div class="">
        <ConfirmDelete v-if="deleteModal" mode="task" :object="selectedTask" :number="selectedIndex" @closeModal="handleDeleteModal" />
        <table class="myTable shadow-lg">
            <thead class="w-full">
                <tr class="text-lg text-black" :class="themeStore.getTableTheme()">
                    <th style="width: 1%"></th>
                    <th style="width: 55%">Title</th>
                    <th style="width: 25%">Assignees</th>
                    <th style="width: 10%" class="text-center">Status</th>
                    <th style="width: 9%" class="text-center">Action</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(task, index) in taskStore.tasks" :key="task.id" class="itbkk-item">
                    <td>
                        <div class="flex">
                            {{ index + 1 }}
                        </div>
                    </td>
                    <td :class="$route.name != 'home' ? '' : 'itbkk-title'" class="font-bold h-[30px] text-[2vh] hover:text-blue-500 break-all hover:cursor-pointer" @click="getTask(task.id)">
                        {{ task.title }}
                    </td>
                    <td class="break-all" :class="[$route.name !== 'home' ? '' : 'itbkk-assignees', task.assignees ? '' : 'italic text-gray-500']">
                        {{ task.assignees ? task.assignees : 'Unassigned' }}
                    </td>
                    <td :class="$route.name != 'home' ? '' : 'itbkk-status'">
                        <button class="rounded-2xl w-[100px] h-[30px] text-[2vh] font-bold cursor-default" :style="{ backgroundColor: colorStatus(task.status) }">
                            {{ task.status }}
                        </button>
                    </td>
                    <td class="flex justify-center">
                        <div class="itbkk-button-action dropdown dropdown-hover">
                            <div tabindex="0" role="button" class="">
                                <img src="/element/dots.png" class="w-[2vh] h-[2vh]" alt="list img" />
                            </div>
                            <ul tabindex="0" class="dropdown-content z-[1] w-fit menu p-2 shadow bg-base-100 rounded-box" :class="themeStore.getAlterTheme()">
                                <li>
                                    <p class="itbkk-button-edit" @click="toEditPage(task.id)">Edit</p>
                                </li>
                                <li>
                                    <p class="itbkk-button-delete" @click="deleteTask(task, index + 1)">Delete</p>
                                </li>
                            </ul>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<style scoped></style>
