<script setup>
import { getTaskById, updateTask } from '@/libs/FetchAPI'
import { ref, onMounted, defineProps, defineEmits } from 'vue'
import { useTasks } from '../stores/task.js'
import { useVariables } from '../stores/store.js'
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const myTasks = useTasks()
const myVariables = useVariables()
const isSelectTask = ref(false)
const selectedTask = ref({})
const props = defineProps({
    task: {
        type: Object,
    },
})


const localTimeZone = ref('')
const createdOn = ref('')
const updatedOn = ref('')



const switchTimeZone = () => {
    localTimeZone.value = Intl.DateTimeFormat().resolvedOptions().timeZone

    if (selectedTask.value) {
        const localCreatedOn = new Date(selectedTask.value.createdOn).toLocaleString('en-GB', {
            timeZone: localTimeZone.value,
        })
        const localUpdatedOn = new Date(selectedTask.value.updatedOn).toLocaleString('en-GB', {
            timeZone: localTimeZone.value,
        })

        createdOn.value = localCreatedOn
        updatedOn.value = localUpdatedOn
    }
}

onMounted(async () => {
    const id = route.params.taskId ? route.params.taskId : props.task.id
    selectedTask.value = ''
    isSelectTask.value = myVariables.isSelectTask
    selectedTask.value = await getTaskById(id)
    console.log(selectedTask.value.status)
    if (selectedTask.value.status==404) router.push({ path: `/` })
    switchTimeZone(selectedTask.value)
    console.log(selectedTask.value)
})
</script>

<template>
    <div class="py-[10vh] px-[10vh] fixed inset-0 flex justify-center bg-black bg-opacity-50 w-full">
        <div class="bg-white w-full rounded-lg">
            <div v-if="selectedTask" class="grid gap-[2vh] rounded-md border-none p-[2vh]">
                <p type="text" name="title" class="itbkk-title font-bold text-[4vh]" id="title">{{ selectedTask.title }}</p>
                <hr />
                <div class="grid grid-cols-12 gap-[3vh]">
                    <div class="grid col-span-8">
                        <div>
                            <label for="description">Description</label>
                            <p
                                id="description"
                                class="itbkk-description block w-full p-[2vh] resize-none text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                                :class="selectedTask.description ? '' : 'italic text-gray-500'"
                            >
                                {{ selectedTask.description ? selectedTask.description : 'No Description Provided' }}
                            </p>
                        </div>
                    </div>

                    <div class="grid col-span-4">
                        <div>
                            <div>
                                <label for="assignees">Assignees</label>
                                <p
                                    id="assignees"
                                    class="itbkk-assignees block p-[2vh] w-full resize-none text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                                    :class="selectedTask.assignees ? '' : 'italic text-gray-500'"
                                >
                                    {{ selectedTask.assignees ? selectedTask.assignees : 'Unassigned' }}
                                </p>
                            </div>
                            <div class="flex flex-col pt-[3vh]">
                                <label for="status">Status</label>
                                <p id="status" class="itbkk-status">
                                    {{ selectedTask.status }}
                                </p>
                            </div>
                            <div class="pt-[4vh]">
                                <p class="itbkk-timezone">Local Time Zone: {{ localTimeZone }}</p>
                                <p class="itbkk-created-on">Created On: {{ createdOn }}</p>
                                <p class="itbkk-updated-on">Last Updated On: {{ updatedOn }}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <p v-else>The requested task does not exist</p>
        </div>
    </div>
</template>

<style lang="scss" scoped></style>
