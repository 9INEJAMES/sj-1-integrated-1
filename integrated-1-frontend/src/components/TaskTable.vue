<script setup>
import { ref, computed } from 'vue'
import ConfirmDelete from './ConfirmDelete.vue'
import router from '../router/index.js'
import { useTasksStore } from '../stores/task.js'
import { useTheme } from '@/stores/theme.js'
import { useStatusesStore } from '@/stores/status'

const themeStore = useTheme()
const deleteModal = ref(false)
const taskStore = useTasksStore()
const selectedTask = ref(null)
const selectedIndex = ref(null)
const sort = ref('default') 

const switchSortOrder = () => {
    if (sort.value === 'default') {
        sort.value = 'asc'
    } else if (sort.value === 'asc') {
        sort.value = 'desc'
    } else {
        sort.value = 'default'
    }
}

const sortedTasks = computed(() => {
    if (sort.value === 'default') {
        return taskStore.tasks
    } else if (sort.value === 'asc') {
        return taskStore.tasks.slice().sort((a, b) => a.status.name.localeCompare(b.status.name))
    } else {
        return taskStore.tasks.slice().sort((a, b) => b.status.name.localeCompare(a.status.name))
    }
})

const getTask = (id) => {
    router.push({
        name: 'taskDetails',
        params: {
            taskId: id,
        },
    })
}

const toEditPage = (id) => {
    router.push({
        name: 'taskEdit',
        params: {
            taskId: id,
        },
    })
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
    <div>
        <ConfirmDelete v-if="deleteModal" mode="task" :object="selectedTask" :number="selectedIndex"
            @closeModal="handleDeleteModal" />
        <table class="myTable table-pin-rows shadow-lg">
            <thead class="w-full">
                <tr class="text-lg" :class="themeStore.getTableTheme()">
                    <th style="width: 1%"></th>
                    <th style="width: 55%">
                        Title
                    </th>
                    <th style="width: 25%">Assignees</th>
                    <th style="width: 10%" class="text-center">Status
                                            <div class="itbkk-status-sort rounded-md w-[25px] border border-white h-[25px] relative float-right cursor-pointer shadow-md flex justify-center items-center"
                                @click="switchSortOrder">
                                <img :src="'/sort-' + sort + '.png'" class="w-[15px] h-[15px]" />
                            </div></th>
                    <th style="width: 9%" class="text-center">Action</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(task, index) in sortedTasks" :key="task.id" class="itbkk-item">
                    <td>
                        <div class="flex">
                            {{ index + 1 }}
                        </div>
                    </td>
                    <td :class="themeStore.isLight ? 'hover:text-pink-300' : 'hover:text-cyan-500'"
                        class="itbkk-title font-bold h-[30px] text-[2vh] break-all hover:cursor-pointer"
                        @click="getTask(task.id)">
                        {{ task.title }}
                    </td>
                    <td class="break-all"
                        :class="[$route.name !== 'home' ? '' : 'itbkk-assignees', task.assignees ? '' : 'italic text-gray-500']">
                        {{ task.assignees ? task.assignees : 'Unassigned' }}
                    </td>
                    <td :class="$route.name != 'home' ? '' : 'itbkk-status'">
                        <button class="rounded-2xl w-[100px] h-[30px] text-[2vh] font-bold cursor-default text-black"
                            :style="{ backgroundColor: task.status.color }">
                            {{ task.status.name }}
                        </button>
                    </td>
                    <td>
                        <div class="flex justify-center items-center h-full">
                            <div class="itbkk-button-action dropdown dropdown-hover">
                                <div tabindex="0" role="button" class="">
                                    <img src="/element/dots.png" class="w-[2vh] h-[2vh]" alt="list img" />
                                </div>
                                <ul tabindex="0"
                                    class="dropdown-content z-[1] w-fit menu p-2 shadow bg-base-100 rounded-box"
                                    :class="themeStore.getAlterTheme()">
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
                </tr>
            </tbody>
        </table>
    </div>
</template>

<style scoped></style>
