<script setup>
import { useTheme } from '@/stores/theme'
import { useLimitStore } from '@/stores/limitTask'
import { onMounted, ref, watch } from 'vue'
import { useLimitApi } from '@/composables/limit-api'
const limitApi = useLimitApi()
const themeStore = useTheme()
const limitStore = useLimitStore()
const oldLimit = ref({ limit: false, limitMaximumTask: 10 })
const newLimit = ref({ limit: false, limitMaximumTask: 10 })
const isChanged = ref(false)
const emit = defineEmits(['close'])
onMounted(() => {
    oldLimit.value = { ...limitStore.getLimit() }
    newLimit.value = { ...limitStore.getLimit() }
})
watch(
    newLimit,
    () => {
        if (JSON.stringify(newLimit.value) === JSON.stringify(oldLimit.value)) isChanged.value = false
        else isChanged.value = true
    },
    { deep: true }
)
const submitSetting = async (isSave) => {
    if (isSave) {
        const updated = await limitApi.updateLimit(newLimit.value)
        if (updated)
            limitStore.updateLimit({
                ...updated,
            })
    }
    emit('close')
}
</script>

<template>
    <div class="py-[24vh] px-[60vh] fixed inset-0 flex justify-center bg-black bg-opacity-50 w-full">
        <div class="w-full rounded-lg" :class="themeStore.getTheme()">
            <div class="itbkk-modal-status grid gap-[2vh] rounded-md border-none p-[2vh]">
                <p class="text-2xl font-semibold" :class="themeStore.getTextHeaderTheme()">Status Settings</p>
                <hr />
                <div class="px-[2vh]">
                    <p class="text-lg font-medium">Users can limit the task in a status by setting maximum tasks in each status(except "No Status" and "Done" statuses).</p>
                    <br />
                    <div class="flex items-center gap-2">
                        <label class="flex cursor-pointer gap-2"> <input type="checkbox" value="synthwave" class="toggle theme-controller" v-model="newLimit.limit" /> </label
                        ><span class="text-base">Limit tasks in this status</span>
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
                    <button @click="submitSetting(true)" :disabled="!isChanged || newLimit.limitMaximumTask <= 0" class="itbkk-button-save btn btn-success text-white rounded-md p-2">Save</button
                    ><button @click="submitSetting(false)" class="itbkk-button-cancel btn btn-error text-white rounded-md p-2">Cancel</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
@/stores/limitTask
