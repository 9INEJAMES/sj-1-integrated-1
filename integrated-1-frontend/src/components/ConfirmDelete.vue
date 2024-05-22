<script setup>
import { useTasksStore } from '@/stores/task'
import { useTheme } from '@/stores/theme.js'
import { useTaskApi } from '@/composables/task-api.js'
import { onMounted, ref, watch } from 'vue'
import { useStatusesStore } from '@/stores/status'
import { useStatusApi } from '@/composables/status-api'

const taskApi = useTaskApi()
const statusApi = useStatusApi()
const themeStore = useTheme()
const taskStore = useTasksStore()
const isInUsed = ref(false)
const statusStore = useStatusesStore()
const isSelectNewStatus = ref(false)
const props = defineProps({
    mode: {
        type: String,
        validator: (value) => {
            return ['task', 'status'].includes(value)
        },
        default: 'task',
    },
    object: {
        type: Object,
    },
    number: {
        type: Number,
    },
})
const statusList = statusStore.statuses
const newStatusId = ref(props.object.id)

const emit = defineEmits(['closeModal'])

const submitDelete = async () => {
    if (props.mode == 'task') {
        const result = await taskApi.deleteTask(props.object.id)
        if (result) {
            taskStore.removeTask(result.id)
            emit('closeModal', true)
        }
    } else {
        if (isInUsed.value) {
            const newStatus = statusStore.findStatusById(newStatusId.value)
            const result = await statusApi.deleteStatusAndTransfer(props.object.id, newStatus, props.object.noOfTasks)
            if (result) {
                statusStore.removeStatus(props.object.id)
                emit('closeModal', true)
            } else {
                // newStatusId.value=props.object.id
            }
        } else {
            await statusApi.deleteStatus(props.object.id)
            statusStore.removeStatus(props.object.id)
            emit('closeModal', true)
        }
    }
}
const cancelDelete = () => {
    emit('closeModal', false)
}
onMounted(async () => {
    if (props.mode == 'status') {
        if (props.object.noOfTasks > 0) {
            isInUsed.value = true
        }
    }
})
watch(newStatusId, () => {
    if (newStatusId.value) {
        isSelectNewStatus.value = true
    } else {
        isSelectNewStatus.value = false
    }
})
</script>

<template>
    <div class="py-[30vh] px-[10vh] fixed inset-0 flex justify-center items-center bg-black bg-opacity-35 w-full z-40">
        <div class="flex-col border rounded-md p-[2vh] w-1/4 h-fit" :class="themeStore.getTheme()">
            <p class="font-bold text-[4vh] py-[2vh]" :class="themeStore.getTextHeaderTheme()">Delete a {{ mode == 'task' ? 'Task' : 'Status' }}</p>
            <hr />
            <p v-if="!isInUsed" class="itbkk-message py-[3vh]">
                Do you want to delete <span v-if="mode == 'task'">the Task number {{ number }} "{{ object.title }}"</span>
                <span v-else>{{ object.name }} status</span>
            </p>
            <p v-else-if="(object.name == "No Status" || object.name == "Done") && mode == 'status'" class="itbkk-message py-[3vh]">You can't delete default status</p>
            <div v-else class="pt-[3vh]">
                <p class="itbkk-message">There {{ object.tasks == 1 ? 'is' : 'are' }} {{ object.tasks }} {{ object.tasks == 1 ? 'task' : 'tasks' }} associated with the {{ object.name }} status</p>
                <div class="flex">
                    <p class="w-1/3 flex items-center">Transfer to</p>
                    <select v-model="newStatusId" id="status" class="itbkk-status select select-bordered disabled:text-black w-2/3" :class="themeStore.getTheme()">
                        <option v-for="status in statusList" :disabled="status.id == object.id" :value="status.id">{{ status.name }}</option>
                    </select>
                </div>
            </div>
            <div class="flex gap-[2vh] justify-end py-[2vh]">
                <button @click="cancelDelete" class="itbkk-button-cancel btn btn-error text-white rounded-md p-2">Cancel</button>

                <button
                    @click="submitDelete"
                    class="itbkk-button-confirm btn btn-success text-white rounded-md p-2"
                    :class="(object.name == "No Status" && mode == 'status') || (mode == 'status' && isInUsed && !isSelectNewStatus) ? 'disabled' : ''"
                    :disabled="(object.name == "No Status"  && mode == 'status') || (mode == 'status' && isInUsed && !isSelectNewStatus)"
                >
                    {{ mode == 'status' && isInUsed && object.name == "No Status" &&object.name == "Done"  ? 'Transfer' : 'Confirm' }}
                </button>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped></style>
