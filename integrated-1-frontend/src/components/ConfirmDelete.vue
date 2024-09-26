<script setup>
import { useTasksStore } from '@/stores/task'
import { useTheme } from '@/stores/theme.js'
import { useTaskApi } from '@/composables/task-api.js'
import { onMounted, ref, watch } from 'vue'
import { useStatusesStore } from '@/stores/status'
import { useStatusApi } from '@/composables/status-api'
import { useBoardApi } from '@/composables/board-api'
import { useBoardStore } from '@/stores/board'
import { useAuthStore } from '@/stores/auth'
import { useRoute } from 'vue-router'

const taskApi = useTaskApi()
const statusApi = useStatusApi()
const themeStore = useTheme()
const taskStore = useTasksStore()
const statusStore = useStatusesStore()
const boardApi = useBoardApi()
const isInUsed = ref(false)
const isSelectNewStatus = ref(false)
const boardStore = useBoardStore()
const authStore = useAuthStore()
const isCanEdit = ref(false)
const route = useRoute()

const props = defineProps({
    mode: {
        type: String,
        validator: (value) => {
            return ['task', 'status', 'board'].includes(value)
        },
        default: 'board',
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
    try {
        // authStore.checkToken()
        if (props.mode == 'task') {
            const result = await taskApi.deleteTask(route.params.bid, props.object.id)
            if (result) {
                taskStore.removeTask(result.id)
                emit('closeModal', true)
            }
        } else if (props.mode == 'status') {
            if (isInUsed.value) {
                const newStatus = statusStore.findStatusById(newStatusId.value)
                const result = await statusApi.deleteStatusAndTransfer(route.params.bid, props.object.id, newStatus, props.object.noOfTasks)
                if (result) {
                    statusStore.removeStatus(props.object.id)
                    emit('closeModal', true)
                }
            } else {
                await statusApi.deleteStatus(route.params.bid, props.object.id)
                statusStore.removeStatus(props.object.id)
                emit('closeModal', true)
            }
        } else if (props.mode == 'board') {
            await boardApi.deleteBoard(route.params.bid, props.object.id)
            boardStore.removeBoard(props.object.id)
            statusStore.resetStatuses()
            taskStore.resetTasks()
            emit('closeModal', true)
        }
    } catch (error) {
        console.error('Delete failed:', error)
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
    // if (authStore.checkToken() && boardStore.boards.length === 0) await boardStore.fetchBoard()
    if (props.mode == 'board') isCanEdit.value = await authStore.isOwner(props.object.id)
    else isCanEdit.value = await authStore.isOwner(props.object.bid)
})

watch(newStatusId, (newVal) => {
    isSelectNewStatus.value = !!newVal
})
</script>

<template>
    <div class="py-[30vh] px-[10vh] fixed inset-0 flex justify-center items-center bg-black bg-opacity-35 w-full z-40">
        <div class="flex-col border rounded-md p-[2vh] w-1/4 h-fit" :class="themeStore.getTheme()">
            <p class="font-bold text-[4vh] py-[2vh]" :class="themeStore.getTextHeaderTheme()">Delete a {{ mode == 'task' ? 'Task' : mode == 'status' ? 'Status' : 'Board' }}</p>
            <hr />
            <p v-if="mode == 'task' && !isInUsed" class="itbkk-message py-[3vh]">Do you want to delete the Task number {{ number }} "{{ object.title }}"</p>
            <p v-else-if="mode == 'status' && (object.name == 'No Status' || object.name == 'Done')" class="itbkk-message py-[3vh]">You can't delete default status</p>
            <p v-else-if="mode == 'status' && !isInUsed" class="itbkk-message py-[3vh]">Do you want to delete the {{ object.name }} status?</p>
            <p v-else-if="mode == 'board'" class="itbkk-message py-[3vh]">Do you want to delete the Board name "{{ object.name }}"?</p>
            <div v-else-if="mode == 'status'" class="pt-[3vh]">
                <p class="itbkk-message">
                    There {{ object.noOfTasks == 1 ? 'is' : 'are' }} {{ object.noOfTasks }} {{ object.noOfTasks == 1 ? 'task' : 'tasks' }} associated with the {{ object.name }} status
                </p>
                <div class="flex">
                    <p class="w-1/3 flex items-center">Transfer to</p>
                    <select v-model="newStatusId" id="status" class="itbkk-status select select-bordered disabled:text-black w-2/3" :class="themeStore.getTheme()">
                        <option v-for="status in statusList" :disabled="status.id == object.id" :value="status.id">{{ status.name }}</option>
                    </select>
                </div>
            </div>
            <div class="flex gap-[2vh] justify-end py-[2vh]">
                <button @click="cancelDelete" class="itbkk-button-cancel btn btn-error text-white rounded-md p-2">Cancel</button>
                <div :class="!isCanEdit ? 'tooltip tooltip-bottom' : ''" data-tip="You need to be board owner to perform this action">
                    <button
                        @click="submitDelete"
                        class="itbkk-button-confirm btn btn-success text-white rounded-md p-2"
                        :class="(mode == 'status' && isInUsed && !isSelectNewStatus) || (mode == 'status' && object.name == 'No Status') || !isCanEdit ? 'disabled' : ''"
                        :disabled="(mode == 'status' && isInUsed && !isSelectNewStatus) || (mode == 'status' && object.name == 'No Status') || !isCanEdit"
                    >
                        {{ mode == 'status' && isInUsed && (object.name !== 'No Status' || object.name !== 'Done') ? 'Transfer' : 'Confirm' }}
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped></style>
