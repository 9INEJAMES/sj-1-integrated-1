<script setup>
import { ref, onMounted, defineProps, defineEmits } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTheme } from '@/stores/theme.js'
import { useTaskApi } from '@/composables/task-api.js'

const taskApi = useTaskApi()
const myTheme = useTheme()
const route = useRoute()
const router = useRouter()
const selectedTask = ref({})
const props = defineProps({
    task: {
        type: Object,
    },
})
const emit = defineEmits(['closeModal'])
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

const close = () => {
    if (route.params.taskId) router.push({ path: `/` })
    else emit('closeModal')
}
onMounted(async () => {
    const id = route.params.taskId ? route.params.taskId : props.task.id
    // selectedTask.value = ''
    selectedTask.value = await taskApi.getTaskById(id)
    if (selectedTask.value) {
        selectedTask.value.status = selectedTask.value.status
            .split('_')
            .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
            .join(' ')
        switchTimeZone(selectedTask.value)
    }
    if (!selectedTask.value) {
        router.push({ path: `/` })
    }
})
</script>

<template>
    <div class="detailpage py-[10vh] px-[10vh] fixed inset-0 flex justify-center bg-black bg-opacity-50 w-full">
        <div class="w-full rounded-lg flex flex-col justify-between" :class="myTheme.getTheme()">
            <div v-if="selectedTask" class="grid gap-[2vh] rounded-md border-none p-[2vh] break-all">
                <p name="title" class="itbkk-title font-bold text-[4vh]" id="title">
                    {{ selectedTask.title }}
                </p>
                <hr />
                <div class="grid grid-cols-12 gap-[3vh]">
                    <div class="grid col-span-8">
                        <div>
                            <label for="description">Description</label>
                            <p
                                id="description"
                                class="itbkk-description block w-full p-[2vh] resize-none text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 break-all h-full"
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
                                    class="itbkk-assignees block p-[2vh] w-full resize-none text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 break-all"
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
            <div class="flex justify-end items-end p-5">
                <button class="btn btn-error text-white" @click="close">close</button>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped></style>
