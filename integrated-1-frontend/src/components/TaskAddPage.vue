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
const newTask = ref({
    title: '',
    description: '',
    assignees: '',
    status: 'No Status',
})

const taskApi = useTaskApi()

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
const localTimeZone = ref('')
const createdOn = ref('')
const updatedOn = ref('')
const selectedTask = ref({})
const switchTimeZone = (task) => {
    localTimeZone.value = Intl.DateTimeFormat().resolvedOptions().timeZone
    createdOn.value = new Date(task.createdOn).toLocaleString('en-GB', {
        timeZone: localTimeZone.value,
    })
    updatedOn.value = new Date(task.updatedOn).toLocaleString('en-GB', {
        timeZone: localTimeZone.value,
    })
}
onMounted(() => {
    if (route.params.taskId) {
        const task = myTasks.getIdOfTask(route.params.taskId)
        newTask.value = {
            id: task.id,
            title: task.title,
            description: task.description,
            assignees: task.assignees,
            status: task.status,
            createdOn: task.createdOn,
            updatedOn: task.updatedOn,
        }
        switchTimeZone(task)
    }
})
</script>

<template>
    <div class="py-[10vh] px-[10vh] fixed inset-0 flex justify-center bg-black bg-opacity-50 w-full">
        <div class="w-full rounded-lg" :class="myTheme.getTheme()">
            <div class="grid gap-[2vh] rounded-md border-none p-[2vh]">
                <p class="text-lg font-semibold">{{ route.params.taskId ? 'Edit' : 'New' }} Task</p>
                <hr />
                <div>
                    <label for="title">Titie</label><br />
                    <input
                        type="text"
                        name="title"
                        @input="checkLength('title', newTask.title, 100)"
                        class="block w-full p-[2vh] resize-none text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300"
                        id="title"
                        placeholder="Write your task's title"
                        v-model="newTask.title"
                    />
                </div>
                <div class="grid grid-cols-12 gap-[3vh]">
                    <div class="grid col-span-8">
                        <div>
                            <label for="description">Description</label>
                            <textarea
                                rows="15"
                                id="description"
                                class="block w-full p-[2vh] resize-none overflow-auto text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300"
                                placeholder="Write your description"
                                name="description"
                                @input="checkLength('description', newTask.description, 500)"
                                v-model="newTask.description"
                            ></textarea>
                        </div>
                    </div>

                    <div class="grid col-span-4">
                        <div>
                            <div>
                                <label for="assignees">Assignees</label>
                                <textarea
                                    id="assignees"
                                    rows="5"
                                    class="block p-[2vh] w-full resize-none text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300"
                                    placeholder="Enter assignees"
                                    name="assignees"
                                    @input="checkLength('assignees', newTask.assignees, 30)"
                                    v-model="newTask.assignees"
                                ></textarea>
                            </div>
                            <div class="flex flex-col pt-[3vh]">
                                <label for="status">Status</label>
                                <select id="status" class="select select-bordered" :class="myTheme.getTheme()" v-model="newTask.status">
                                    <option selected value="No Status">No Status</option>
                                    <option value="To Do">To Do</option>
                                    <option value="Doing">Doing</option>
                                    <option value="Done">Done</option>
                                </select>
                            </div>
                            <div v-if="route.params.taskId" class="pt-[4vh]">
                                <p class="itbkk-timezone">Local Time Zone: {{ localTimeZone }}</p>
                                <p class="itbkk-created-on">Created On: {{ createdOn }}</p>
                                <p class="itbkk-updated-on">Last Updated On: {{ updatedOn }}</p>
                            </div>
                            <div class="pt-[4vh] flex justify-evenly">
                                <button class="btn btn-success btn-xs sm:btn-sm md:btn-md lg:btn-lg" @click="submitTask(true)" :disabled="newTask.title.trim().length <= 0">Ok</button>
                                <button class="btn btn-error btn-xs sm:btn-sm md:btn-md lg:btn-lg" @click="submitTask(false)">Cancel</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
