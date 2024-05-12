<script setup>
import { useRoute, useRouter } from 'vue-router'
import { onMounted, ref, watch } from 'vue'
import { useStatusesStore } from '../stores/status.js'
import { useTheme } from '@/stores/theme.js'
import { useStatusApi } from '@/composables/status-api'
import { useTasksStore } from '@/stores/task.js'

const themeStore = useTheme()
const route = useRoute()
const router = useRouter()
const statusesStore = useStatusesStore()
const statusApi = useStatusApi()
const isDisibled = ref(false)
const isChanged = ref(false)
const taskStore = useTasksStore()
let status
const newStatus = ref({
    name: '',
    description: '',
    color: '#cbd5e1',
})
const oldStatus = ref({
    name: '',
    description: '',
    color: '#cbd5e1',
})

watch(
    newStatus,
    () => {
        if (JSON.stringify(newStatus.value) === JSON.stringify(oldStatus.value)) isChanged.value = false
        else isChanged.value = true
    },
    { deep: true }
)
const submitStatus = async (isSave) => {
    if (isSave) {
        if (route.params.id) {
            const updated = await statusApi.updateStatus(newStatus.value)
            statusesStore.updateStatus({
                ...updated,
            })
        } else {
            const status = await statusApi.addStatus(newStatus.value)
            if (status) statusesStore.addStatus(status)
        }
    }
    router.back()
}
const checkLength = (name, value, length) => {
    if (value.trim().length > length) {
        if (name === 'name') newStatus.value.name = value.trim().slice(0, length)
        if (name === 'description') newStatus.value.description = value.trim().slice(0, length)
    }
}
onMounted(async () => {
    if (route.name !== 'statusAdd') {
        const id = route.params.id
        status = await statusesStore.getIdOfStatus(id)
        if (!status) {
            router.back()
        } else {
            newStatus.value = {
                id: status.id,
                name: status.name,
                description: status.description == null ? '' : status.description,
                color: status.color,
            }
            oldStatus.value = {
                id: status.id,
                name: status.name,
                description: status.description == null ? '' : status.description,
                color: status.color,
            }
        }
    }
})
</script>

<template>
    <div class="py-[10vh] px-[10vh] fixed inset-0 flex justify-center bg-black bg-opacity-50 w-full">
        <div class="w-full rounded-lg" :class="themeStore.getTheme()">
            <div class="grid gap-[2vh] rounded-md border-none p-[2vh] itbkk-modal-status">
                <p class="text-2xl font-semibold" :class="themeStore.getTextHeaderTheme()">
                    {{ route.name == 'statusAdd' ? 'Add Status' : 'Edit Status' }}
                </p>
                <hr />
                <div>
                    <label for="statusName" :class="themeStore.getTextHeaderTheme()">Status Name<span class="text-red-600">*</span></label><br />
                    <input
                        type="text"
                        name="statusName"
                        id="statusName"
                        @input="checkLength('name', newStatus.name, 50)"
                        class="itbkk-name block w-full p-[2vh] resize-none text-sm bg-gray-50 rounded-lg border border-gray-300 itbkk-status-name"
                        :class="newStatus.name.length == 50 ? ' text-gray-500' : ' text-gray-900'"
                        placeholder="Write your status's name"
                        v-model="newStatus.name"
                        :disabled="isDisibled"
                    />
                    <p v-show="newStatus.name.length == 50" class="text-xs pl-3 pt-1 absolute">The name has a maximum length of 50 characters.</p>
                </div>

                <div class="grid grid-cols-12 gap-[3vh] pt-2">
                    <div class="grid col-span-8">
                        <div>
                            <label for="description" :class="themeStore.getTextHeaderTheme()">Description</label>
                            <textarea
                                rows="15"
                                id="description"
                                class="itbkk-status-description block w-full p-[2vh] resize-none overflow-auto text-sm bg-gray-50 rounded-lg border border-gray-300"
                                :class="newStatus.description && newStatus.description.length == 200 ? ' text-gray-500' : ' text-gray-900'"
                                placeholder="Write your description"
                                name="description"
                                @input="checkLength('description', newStatus.description, 200)"
                                v-model="newStatus.description"
                                :disabled="isDisibled"
                            ></textarea>
                            <p v-show="newStatus.description && newStatus.description.length == 200" class="text-xs pl-3 pt-1 absolute">The description has a maximum length of 200 characters.</p>
                        </div>
                    </div>

                    <div class="flex col-span-4 flex-col justify-between">
                        <div class="flex justify-center">
                            <div>
                                <label for="color" :class="themeStore.getTextHeaderTheme()">Color</label>
                                <input
                                    type="color"
                                    class="p-1 w-20 h-20 sm:w-32 md:w-36 lg:w-64 block bg-white border border-gray-200 cursor-pointer rounded-lg"
                                    id="color"
                                    title="Choose your color"
                                    v-model="newStatus.color"
                                />
                            </div>
                        </div>
                        <div>
                            <p :class="themeStore.getTextHeaderTheme()">Preview</p>
                            <div class="border h-20 w-30 flex justify-center items-center border-slate-300 font-bold text-black rounded-md">
                                <button :style="{ backgroundColor: newStatus.color }" class="rounded-2xl w-[100px] h-[30px] text-[2vh] font-bold cursor-default">
                                    {{
                                        newStatus.name
                                            .split('_')
                                            .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
                                            .join(' ')
                                    }}
                                </button>
                            </div>
                        </div>
                        <div class="pt-[4vh]">
                            <div class="flex justify-evenly">
                                <button
                                    class="itbkk-button-confirm btn btn-success btn-xs sm:btn-sm md:btn-md lg:btn-lg"
                                    @click="submitStatus(true)"
                                    :class="newStatus.name.trim().length <= 0 || !isChanged || route.params.id == 1 ? 'disabled' : ''"
                                    :disabled="newStatus.name.trim().length <= 0 || !isChanged || route.params.id == 1"
                                >
                                    save
                                </button>

                                <button class="itbkk-button-cancel btn btn-error btn-xs sm:btn-sm md:btn-md lg:btn-lg" @click="submitStatus(false)">cancel</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
