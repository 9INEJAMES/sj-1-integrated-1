<script setup>
import { ref, onMounted } from 'vue'
import ConfirmDelete from './confirmDelete.vue'
import router from '../router/index.js'
import { useTasksStore } from '../stores/task.js'
import { useTheme } from '@/stores/theme.js'

const myTheme = useTheme()
const deleteModal = ref(false)
const updateModal = ref(false)
const myTasks = useTasksStore()
const selectedTask = ref(null)
const selectedIndex = ref(null)
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

onMounted(async () => {
    if (myTasks.tasks.length <= 0) {
        await myTasks.fetchTasks()
    }
})

const colorStatus = (task) => {
    if (task == 'Done') {
        return 'bg-emerald-500'
    } else if (task == 'Doing') {
        return 'bg-sky-300'
    } else if (task == 'No Status') {
        return 'bg-slate-300'
    } else {
        return 'bg-amber-300'
    }
}

const toEditPage = (id) => {
    router.push({
        name: 'taskEdit',
        params: {
            taskId: id,
        },
    })
    if (selectedTask.value === null) {
        selectedTask.value = myTasks.getIdOfTask(id)
    }
    updateModal.value = true
}

const deleteTask = (task, index) => {
    if (selectedTask.value === null) {
        selectedTask.value = task
        selectedIndex.value = index
    }
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
        <ConfirmDelete v-if="deleteModal" :task="selectedTask" :number="selectedIndex" @closeModal="handleDeleteModal" />
        <table class="myTable shadow-lg">
            <thead>
                <tr class="text-lg text-black" :class="myTheme.getTableTheme()">
                    <th></th>
                    <th>Title</th>
                    <th>Assignees</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(task, index) in myTasks.tasks" :key="task.id" class="itbkk-item">
                    <td>
                        <div class="flex">
                            {{ index + 1 }}
                            <div class="itbkk-button-action dropdown dropdown-hover">
                                <div tabindex="0" role="button" class="">
                                    <img src="/element/dots.png" class="w-[2vh] h-[2vh]" alt="list img" />
                                </div>
                                <ul tabindex="0" class="dropdown-content z-[1] w-fit menu p-2 shadow bg-base-100 rounded-box" :class="myTheme.getAlterTheme()">
                                    <li>
                                        <p class="itbkk-button-edit" @click="toEditPage(task.id)">Edit</p>
                                    </li>

                                    <li>
                                        <p class="itbkk-button-delete" @click="deleteTask(task, index + 1)">Delete</p>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </td>
                    <td :class="$route.name != 'home' ? '' : 'itbkk-title'" class="font-bold h-[30px] text-[2vh] hover:text-blue-500 break-all hover:cursor-pointer" @click="getTask(task.id)">
                        {{ task.title }}
                    </td>
                    <td :class="[$route.name !== 'home' ? '' : 'itbkk-assignees', task.assignees ? '' : 'italic text-gray-500']">
                        {{ task.assignees ? task.assignees : 'Unassigned' }}
                    </td>

                    <td :class="$route.name != 'home' ? '' : 'itbkk-status'">
                        <button class="rounded-2xl w-[100px] h-[30px] text-[2vh] font-bold cursor-default" :class="colorStatus(task.status)">
                            {{ task.status }}
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<style scoped></style>
