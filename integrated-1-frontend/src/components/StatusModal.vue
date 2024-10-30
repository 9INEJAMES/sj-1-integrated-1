<script setup>
import { useRoute, useRouter } from 'vue-router'
import { onMounted, ref, watch } from 'vue'
import { useStatusesStore } from '../stores/status.js'
import { useTheme } from '@/stores/theme.js'
import { useStatusApi } from '@/composables/status-api'
import { useAuthStore } from '@/stores/auth'
const themeStore = useTheme()
const route = useRoute()
const router = useRouter()
const statusesStore = useStatusesStore()
const statusApi = useStatusApi()
const isDisibled = ref(false)
const isChanged = ref(false)
const isCanEdit = ref(false)
const authStore = useAuthStore()

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
const isNullStr = (str) => {
    if (str == null || str.trim().length == 0) {
        return null
    } else return str
}

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
        newStatus.value.description = isNullStr(newStatus.value.description)
        if (route.params.id) {
            const updated = await statusApi.updateStatus(route.params.bid, newStatus.value)
            if (updated)
                if (!statusesStore.findStatusById(updated.id)) {
                    statusesStore.addStatus(updated)
                } else {
                    statusesStore.updateStatus({
                        ...updated,
                    })
                }
        } else {
            const status = await statusApi.addStatus(route.params.bid, newStatus.value)

            if (status) statusesStore.addStatus(status)
        }
    }
    router.push({ name: 'statusView' })
}

const checkLength = (name, value, length) => {
    if (value.trim().length > length) {
        if (name === 'name') newStatus.value.name = value.trim().slice(0, length)
        if (name === 'description') newStatus.value.description = value.trim().slice(0, length)
    }
}
onMounted(async () => {
    isCanEdit.value = await authStore.isEditor(route.params.bid)

    if (route.name !== 'statusAdd') {
        const id = route.params.id
        status = await statusApi.getStatusById(route.params.bid, id)
        if (!status) {
            router.push({ name: 'statusView' })
            // router.back()
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
    <div class="fixed inset-0 flex justify-center items-center bg-black bg-opacity-50 w-full">
        <div class="my-[10vh] mx-[10vh] h-fit w-full rounded-lg" :class="themeStore.getTheme()">
            <div class="itbkk-modal-status grid gap-[2vh] rounded-md border-none p-[2vh]">
                <p class="text-2xl font-semibold" :class="themeStore.getTextHeaderTheme()">
                    {{ route.name == 'statusAdd' ? 'Add Status' : 'Edit Status' }}
                </p>
                <hr />
                <div>
                    <label for="statusName" :class="themeStore.getTextHeaderTheme()">Status Name<span class="text-red-600">*</span></label
                    ><br />
                    <input
                        type="text"
                        name="statusName"
                        id="statusName"
                        @input="checkLength('name', newStatus.name, 50)"
                        class="itbkk-status-name block w-full p-[2vh] resize-none text-sm bg-gray-50 rounded-lg border border-gray-300"
                        :class="newStatus.name.length == 50 ? ' text-gray-500' : ' text-gray-900'"
                        placeholder="Write your status's name"
                        v-model="newStatus.name"
                        :disabled="isDisibled"
                    />
                    <p v-show="newStatus.name.length == 50" class="text-xs pl-3 pt-1 absolute overflow-auto">The name has a maximum length of 50 characters.</p>
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
                            <p v-show="newStatus.description && newStatus.description.length == 200" class="text-xs pl-3 pt-1 absolute overflow-auto">
                                The description has a maximum length of 200 characters.
                            </p>
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
                                    {{ newStatus.name }}
                                </button>
                            </div>
                        </div>
                        <div class="pt-[4vh]">
                            <div class="flex justify-evenly">
                                <div :class="!isCanEdit ? 'tooltip tooltip-bottom' : ''" :data-tip="'You need to be board owner to perform this action'">
                                    <button
                                        class="itbkk-button-confirm btn btn-success btn-xs sm:btn-sm md:btn-md lg:btn-lg"
                                        @click="submitStatus(true)"
                                        :class="newStatus.name.trim().length <= 0 || !isChanged || route.params.id == 1 || route.params.id == 2 || !isCanEdit ? 'disabled' : ''"
                                        :disabled="newStatus.name.trim().length <= 0 || !isChanged || route.params.id == 1 || route.params.id == 2 || !isCanEdit"
                                    >
                                        save
                                    </button>
                                </div>

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
