<script setup>
import { useTheme } from '@/stores/theme'
import { useLimitStore } from '@/stores/limitTask'
import { onMounted, ref, watch } from 'vue'
import { useLimitApi } from '@/composables/limit-api'
import { useStatusesStore } from '@/stores/status'

const limitApi = useLimitApi()
const themeStore = useTheme()
const limitStore = useLimitStore()
const oldLimit = ref({ limit: false, limitMaximumTask: 10 })
const newLimit = ref({ limit: false, limitMaximumTask: 10 })
const isChanged = ref(false)
const status = useStatusesStore()
const emit = defineEmits(['close'])
const statusReachedLimit = ref([])

onMounted(async () => {
    await status.fetchStatuses()
    oldLimit.value = { ...limitStore.getLimit() }
    newLimit.value = { ...limitStore.getLimit() }
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
        const updated = await limitApi.updateLimit(newLimit.value)
        if (updated) {
            limitStore.updateLimit({ ...updated })
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
    <div class="py-[20vh] px-[56vh] fixed inset-0 flex justify-center items-center bg-black bg-opacity-50 w-full">
        <div class="h-fit w-full rounded-lg" :class="themeStore.getTheme()">
            <div class="itbkk-modal-status grid gap-[2vh] rounded-md border-none p-[2vh]">
                <p class="text-2xl font-semibold" :class="themeStore.getTextHeaderTheme()">Status Settings</p>
                <hr />
                <div class="text-sm sm:text-lg px-[2vh]">
                    <p class="font-medium">Users can limit the task in a status by setting maximum tasks in each status (except "No Status" and "Done" statuses).</p>
                    <br />
                    <div v-if="statusReachedLimit.length > 0" class="p-[2vh] rounded-lg text-black bg-amber-200 border border-yellow-900 pb-[3vh]">
                        <p class="text-sm">
                            <span class="font-semibold" v-for="status in statusReachedLimit" :key="status.id"
                                >{{ status.name }}({{ status.noOfTasks }} task{{ status.noOfTasks > 1 ? 's' : '' }})
                            </span>
                            These statuses have reached the task limit. No additional tasks can be added to these statuses
                        </p>
                        <!-- <details class="dropdown w-[20vh] h-[3vh]">
                            <summary class="m-1 btn text-sm">list of statuses</summary>
                            <ul class="p-2 shadow menu dropdown-content z-[1] bg-base-100 rounded-box w-60">
                                <li v-for="status in statusReachedLimit" :key="status.id">{{ status.name }}({{ status.noOfTasks }} task{{ status.noOfTasks > 1 ? 's' : '' }})</li>
                            </ul>
                        </details> -->
                    </div>
                    <div class="flex items-center gap-2 mt-[1vh]">
                        <label class="flex cursor-pointer gap-2">
                            <input type="checkbox" class="toggle theme-controller" @change="checkStatusLimit" v-model="newLimit.limit" />
                        </label>
                        <span class="text-base">Limit tasks in this status</span>
                    </div>
                    <br />
                    <div class="flex items-center gap-2">
                        <p for="statusName">Maximum tasks</p>
                        <input
                            type="number"
                            v-model="newLimit.limitMaximumTask"
                            min="1"
                            :disabled="!newLimit.limit"
                            class="itbkk-max-task block w-[vh] p-[2vh] resize-none text-sm rounded-lg border text-gray-900 bg-gray-50 border-gray-300"
                        />
                    </div>
                </div>
                <div class="flex gap-[2vh] justify-end py-[2vh]">
                    <button @click="submitSetting(true)" :disabled="!isChanged || newLimit.limitMaximumTask <= 0" class="itbkk-button-save btn btn-success btn-xs sm:btn-md text-white rounded-md p-2">
                        Save
                    </button>
                    <button @click="submitSetting(false)" class="itbkk-button-cancel btn btn-error btn-xs sm:btn-md text-white rounded-md p-2">Cancel</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
