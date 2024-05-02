<script setup>
import { useRoute, useRouter } from 'vue-router'
import { onMounted, ref } from 'vue'
import { useTasksStore } from '../stores/task.js'
import { useTheme } from '@/stores/theme.js'
import { useTaskApi } from '@/composables/task-api'

const myTheme = useTheme()
const route = useRoute()
const router = useRouter()
const myTasks = useTasksStore()
const taskApi = useTaskApi()
const isDisibled = ref(false)
const localTimeZone = ref('')
const createdOn = ref('')
const updatedOn = ref('')
const newTask = ref({
    title: '',
    description: '',
    assignees: '',
    status: 'No Status',
})
const submitTask = async (isSave) => {
    if (isSave) {
        if (route.params.taskId) {
            const updated = await taskApi.updateTask(newTask.value)
            myTasks.updateTask({
                ...updated,
                title: newTask.value.title,
                description: !newTask.value.description ? (newTask.value.description = '') : newTask.value.description,
                assignees: !newTask.value.assignees ? (newTask.value.assignees = '') : newTask.value.assignees,
                status: newTask.value.status,
            })
        } else {
            const task = await taskApi.addTask(newTask.value)
            myTasks.addTasks([task])
        }
    }
    router.push({ path: `/` })
}
const checkLength = (name, value, length) => {
    if (value.trim().length > length) {
        if (name === 'title') newTask.value.title = value.trim().slice(0, length)
        if (name === 'description') newTask.value.description = value.trim().slice(0, length)
        if (name === 'assignees') newTask.value.assignees = value.trim().slice(0, length)
    }
}
const switchTimeZone = (task) => {
    localTimeZone.value = Intl.DateTimeFormat().resolvedOptions().timeZone
    createdOn.value = new Date(task.createdOn).toLocaleString('en-GB', {
        timeZone: localTimeZone.value,
    })
    updatedOn.value = new Date(task.updatedOn).toLocaleString('en-GB', {
        timeZone: localTimeZone.value,
    })
}
onMounted(async () => {
    if (route.name === 'taskDetails') {
        isDisibled.value = true
    }
    if (route.name !== 'taskAdd') {
        const id = route.params.taskId
        const task = await taskApi.getTaskById(id)
        if (!task) {
            // setTimeout(() => {
            //     router.push({ path: `/` })
            // }, 1000)
            router.push({ path: `/` })
        } else {
            newTask.value = {
                id: task.id,
                title: task.title,
                description: task.description,
                assignees: task.assignees,
                status: task.status
                    .split('_')
                    .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
                    .join(' '),
                createdOn: task.createdOn,
                updatedOn: task.updatedOn,
            }
            switchTimeZone(task)
        }
    }
})
</script>

<template>
    <div class="py-[10vh] px-[10vh] fixed inset-0 flex justify-center bg-black bg-opacity-50 w-full">
        <div class="w-full rounded-lg" :class="myTheme.getTheme()">
            <p v-if="route.name == 'taskDetails' && newTask?.id == null">The requested task does not exist</p>
            <div v-else class="grid gap-[2vh] rounded-md border-none p-[2vh]">
                <p class="text-xl font-semibold">
                    {{ route.name != 'taskDetails' ? (route.params.taskId ? 'Edit' : 'New') : '' }}
                    Task {{ route.name == 'taskDetails' ? 'Details' : '' }}
                </p>
                <hr />
                <div>
                    <label for="title">Titie</label><span v-if="route.name != 'taskDetails'" class="text-red-600">*</span><br />
                    <input
                        type="text"
                        name="title"
                        @input="checkLength('title', newTask.title, 100)"
                        class="itbkk-title block w-full p-[2vh] resize-none text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300"
                        id="title"
                        placeholder="Write your task's title"
                        v-model="newTask.title"
                        :disabled="isDisibled"
                    />
                </div>
                <div class="grid grid-cols-12 gap-[3vh]">
                    <div class="grid col-span-8">
                        <div>
                            <label for="description">Description</label>
                            <p
                                v-if="$route.name == 'taskDetails'"
                                id="description"
                                class="itbkk-description block w-full p-[2vh] resize-none text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 break-all h-3/4"
                                :class="newTask.description ? '' : 'italic text-gray-500'"
                            >
                                {{ newTask.description ? newTask.description : 'No Description Provided' }}
                            </p>
                            <textarea
                                v-else
                                rows="15"
                                id="description"
                                class="itbkk-description block w-full p-[2vh] resize-none overflow-auto text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300"
                                placeholder="Write your description"
                                name="description"
                                @input="checkLength('description', newTask.description, 500)"
                                v-model="newTask.description"
                                :disabled="isDisibled"
                            ></textarea>
                        </div>
                    </div>

                    <div class="flex col-span-4 flex-col justify-between">
                        <div>
                            <div>
                                <label for="assignees">Assignees</label>
                                <p
                                    v-if="$route.name == 'taskDetails'"
                                    id="assignees"
                                    class="itbkk-assignees block p-[2vh] w-full resize-none text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 break-all"
                                    :class="newTask.assignees ? '' : 'italic text-gray-500'"
                                >
                                    {{ newTask.assignees ? newTask.assignees : 'Unassigned' }}
                                </p>
                                <textarea
                                    v-else
                                    id="assignees"
                                    rows="1"
                                    class="itbkk-assignees block p-[2vh] w-full resize-none text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300"
                                    placeholder="Enter assignees"
                                    name="assignees"
                                    @input="checkLength('assignees', newTask.assignees, 30)"
                                    v-model="newTask.assignees"
                                    :disabled="isDisibled"
                                ></textarea>
                            </div>
                            <div class="flex flex-col pt-[3vh]">
                                <label for="status">Status</label>
                                <select id="status" class="itbkk-status select select-bordered disabled:text-white" :class="myTheme.getTheme()" :disabled="isDisibled" v-model="newTask.status">
                                    <option selected value="No Status">No Status</option>
                                    <option value="To Do">To Do</option>
                                    <option value="Doing">Doing</option>
                                    <option value="Done">Done</option>
                                </select>
                            </div>
                            <div v-if="$route.name != 'taskAdd'" class="pt-[4vh] text-sm">
                                <p class="itbkk-timezone">Local Time Zone: {{ localTimeZone }}</p>
                                <p class="itbkk-created-on">Created On: {{ createdOn }}</p>
                                <p class="itbkk-updated-on">Last Updated On: {{ updatedOn }}</p>
                            </div>
                        </div>
                        <div class="pt-[4vh]">
                            <div v-if="$route.name != 'taskDetails'" class="flex justify-evenly">
                                <button class="itbkk-button-confirm btn btn-success btn-xs sm:btn-sm md:btn-md lg:btn-lg" @click="submitTask(true)" :disabled="newTask.title.trim().length <= 0">
                                    Ok
                                </button>
                                <button class="itbkk-button-cancel btn btn-error btn-xs sm:btn-sm md:btn-md lg:btn-lg" @click="submitTask(false)">Cancel</button>
                            </div>
                            <div v-else class="flex justify-end items-end">
                                <button class="itbkk-button-close btn btn-error text-white" @click="submitTask(false)">close</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
