<script setup>
import { useTheme } from '@/stores/theme'
import { useBoardStore } from '@/stores/board'
import { onMounted, ref, watch } from 'vue'
import { useStatusesStore } from '@/stores/status'
import { useBoardApi } from '@/composables/board-api'
import { useRoute } from 'vue-router'

const themeStore = useTheme()
const boardStore = useBoardStore()
const oldLimit = ref({ limit: false, limitMaximumTask: 10 })
const newLimit = ref({ limit: false, limitMaximumTask: 10 })
const isChanged = ref(false)
const status = useStatusesStore()
const emit = defineEmits(['close'])
const statusReachedLimit = ref([])
const boardApi = useBoardApi()
const route = useRoute()

onMounted(async () => {
    await status.fetchStatuses(route.params.bid)
    oldLimit.value = { ...boardStore.getBoard() }
    newLimit.value = { ...boardStore.getBoard() }
    checkStatusLimit() // Ensure the check is run on mount
})

watch(
    newLimit,
    () => {
        isChanged.value = JSON.stringify(newLimit.value) !== JSON.stringify(oldLimit.value)
        checkStatusLimit() // Ensure the check is run on change
    },
    { deep: true }
)

const submitSetting = async (isSave) => {
    if (isSave) {
        const updated = await boardApi.updateBoard(newLimit.value)
        if (updated) {
            boardStore.updateBoard({ ...updated })
        }
    }
    emit('close')
}

const checkStatusLimit = () => {
    if (newLimit.value.limit) {
        statusReachedLimit.value = status.statuses.filter((s) => s.noOfTasks >= newLimit.value.limitMaximumTask)
        statusReachedLimit.value = statusReachedLimit.value.filter((s) => s.name !== 'Done' && s.name !== 'No Status')
    } else {
        statusReachedLimit.value = []
    }
}
</script>

<template>
    <div class="fixed inset-0 flex justify-center items-center bg-black bg-opacity-50 w-full">
        <div class="h-fit w-full max-w-lg p-4 sm:p-6 rounded-lg" :class="themeStore.getTheme()">
            <div class="itbkk-modal-status grid gap-3 rounded-md border-none p-4">
                <p class="text-xl font-semibold" :class="themeStore.getTextHeaderTheme()">Status Settings</p>
                <hr />
                <div class="text-sm px-1">
                    <p class="font-medium">Users can limit the task in a status by setting maximum tasks in each status (except "No Status" and "Done" statuses).</p>
                    <br />
                    <div v-if="statusReachedLimit.length > 0" class="p-3 rounded-lg text-black bg-amber-200 border border-yellow-900">
                        <p class="text-sm">
                            <span class="font-semibold" v-for="status in statusReachedLimit" :key="status.id">
                                {{ status.name }} ({{ status.noOfTasks }} task{{ status.noOfTasks > 1 ? 's' : '' }})
                            </span>
                            These statuses have reached the task limit. No additional tasks can be added to these statuses.
                        </p>
                    </div>

                    <div class="flex items-center gap-2 mt-3">
                        <label class="flex cursor-pointer gap-2">
                            <input type="checkbox" class="itbkk-limit-task toggle theme-controller" @change="checkStatusLimit" v-model="newLimit.limit" />
                        </label>
                        <span class="text-base">Limit tasks in this status</span>
                    </div>

                    <div class="flex items-center gap-2 mt-3">
                        <label for="statusName" class="text-base">Maximum tasks</label>
                        <input
                            type="number"
                            v-model="newLimit.limitMaximumTask"
                            min="1"
                            :disabled="!newLimit.limit"
                            class="itbkk-max-task block w-full p-2 text-sm rounded-lg border text-gray-900 bg-gray-50 border-gray-300"
                        />
                    </div>
                </div>

                <div class="flex gap-3 justify-end py-3">
                    <button @click="submitSetting(true)" :disabled="!isChanged || newLimit.limitMaximumTask <= 0" class="itbkk-button-confirm btn btn-success text-white rounded-md px-4 py-2">
                        Save
                    </button>
                    <button @click="submitSetting(false)" class="itbkk-button-cancel btn btn-error text-white rounded-md px-4 py-2">Cancel</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
