<script setup>
import { ref, onMounted } from 'vue'
import ConfirmDelete from './ConfirmDelete.vue'
import router from '../router/index.js'
import { useTasksStore } from '../stores/task.js'
import { useTheme } from '@/stores/theme.js'
import { useStatusesStore } from '@/stores/status.js'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'

const authStore = useAuthStore()
const base = import.meta.env.VITE_BASE
const themeStore = useTheme()
const deleteModal = ref(false)
const taskStore = useTasksStore()
const selectedTask = ref(null)
const selectedIndex = ref(null)
const statusesStore = useStatusesStore()
const route = useRoute()
const isCanEdit = ref(false)

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
onMounted(async () => {
    if (taskStore.tasks.length === 0) await taskStore.fetchTasks(route.params.bid)
    if (statusesStore.statuses.length === 0) await statusesStore.fetchStatuses(route.params.bid)
    isCanEdit.value = authStore.checkToken() ? (await authStore.isOwner(route.params.bid)) || (await authStore.isEditor(route.params.bid)) : false
})
</script>

<template>
    <div>
        <ConfirmDelete v-if="deleteModal" mode="task" :object="selectedTask" :number="selectedIndex" @closeModal="handleDeleteModal" />
        <table class="myTable table-pin-rows shadow-lg">
            <thead class="w-full">
                <tr class="text-lg" :class="themeStore.getTableTheme()">
                    <th style="width: 1%"></th>
                    <th style="width: 55%">Title</th>
                    <th style="width: 25%">Assignees</th>
                    <th style="width: 10%" class="text-center">
                        Status
                        <div
                            class="itbkk-status-sort rounded-md w-[25px] border border-white h-[25px] relative float-right cursor-pointer shadow-md flex justify-center items-center"
                            @click="taskStore.switchSortOrder()"
                        >
                            <img :src="`${base ? base : ''}/sort-${taskStore.sortDirection}.png`" class="w-[15px] h-[15px]" />
                        </div>
                    </th>
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
                    <td
                        :class="themeStore.isLight ? 'hover:text-pink-300' : 'hover:text-cyan-500'"
                        class="itbkk-title font-bold h-[30px] text-[2vh] break-all hover:cursor-pointer"
                        @click="getTask(task.id)"
                    >
                        {{ task.title }}
                    </td>
                    <td class="break-all itbkk-assignees" :class="[task.assignees ? '' : 'italic text-gray-500']">
                        {{ task.assignees ? task.assignees : 'Unassigned' }}
                    </td>
                    <td class="itbkk-status">
                        <button class="rounded-2xl w-[100px] h-[30px] text-[2vh] font-bold cursor-default text-black" :style="{ backgroundColor: task.status.color }">
                            {{ task.status.name }}
                        </button>
                    </td>
                    <td>
                        <div class="flex justify-center items-center h-full">
                            <div class="dropdown">
                                <button
                                    tabindex="0"
                                    role="button"
                                    class="m-1"
                                    :class="(themeStore.getButtonTheme(), !isCanEdit ? 'disabled tooltip tooltip-left' : '')"
                                    :disabled="!isCanEdit"
                                    :data-tip="'You need to be board owner to perform this action'"
                                >
                                    <img src="/element/dots.png" class="w-[3vh] h-[3vh]" alt="list img" />
                                </button>
                                <ul tabindex="0" class="dropdown-content menu bg-base-100 rounded-box z-[1] w-52 p-2 shadow">
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
