<script setup>
import { useRoute, useRouter } from 'vue-router'
import { onMounted, ref, watch } from 'vue'
import { useStatusesStore } from '../stores/status.js'
import { useTheme } from '@/stores/theme.js'
import { useStatusApi } from '@/composables/status-api'


const myTheme = useTheme()
const route = useRoute()
const router = useRouter()
const statusesStore = useStatusesStore()
const statusApi = useStatusApi()
const isDisibled = ref(false)

const isChanged = ref(false)
let status
const newStatus = ref({
    name: '',
    description: '',
    color: '',
})
const oldStatus = ref({
    name: '',
    description: '',
    color: '',
})

watch(
    newStatus.value,
    () => {
        if (JSON.stringify(newStatus.value) === JSON.stringify(oldStatus.value)) isChanged.value = false
        else isChanged.value = true
    },
    { deep: true }
)
const submitStatus = async (isSave) => {
    if (isSave) {
        if (route.params.id) {
            const updated = await statusesStore.updateStatus(newStatus.value)
            mstatusesStore.updateStatus({
                ...updated,
                name: newStatus.value.name,
                description: !newStatus.value.description ? (newStatus.value.description = '') : newStatus.value.description,
                color: newStatus.value.color,
            })
        } else {
            const status = await statusApi.addTask(newStatus.value)
            myTasks.addStatus([status])
        }
    }
    router.back()
}
const checkLength = (name, value, length) => {
    if (value.trim().length > length) {
        if (name === 'name') newStatus.value.title = value.trim().slice(0, length)
        if (name === 'description') newStatus.value.description = value.trim().slice(0, length)
        if (name === 'color') newStatus.value.assignees = value.trim().slice(0, length)
    }
}
onMounted(async () => {
    console.log(route.params.id)
    if (route.name !== 'statusAdd') {
        const id = route.params.id
        status = await statusesStore.getIdOfStatus(id)
        if (!status) {
            // setTimeout(() => {
            //     router.push({ path: `/` })
            // }, 1000)
            router.back()
        } else {
            newStatus.value = {
                id: status.id,
                name: status.name,
                description: status.description == null ? '' : status.description,
                color: status.color == null ? '' : status.color,
            }
            oldStatus.value = {
                id: status.id,
                title: status.name,
                description: status.description == null ? '' : status.description,
                color: status.color == null ? '' : status.color,
            }
        }
    }

})
</script>

<template>
    <div class="py-[10vh] px-[10vh] fixed inset-0 flex justify-center bg-black bg-opacity-50 w-full">
        <div class="w-full rounded-lg" :class="myTheme.getTheme()">
            <div class="grid gap-[2vh] rounded-md border-none p-[2vh]">
                <p class="text-xl font-semibold">
                    {{ route.name == 'statusAdd' ? 'Add Status' : 'Edit Status' }}
                </p>
                <hr />
                <div>
                    <label for="name">Name</label><span class="text-red-600">*</span><br />
                    <input type="text" name="name" id="name" @input="checkLength('name', newStatus.name, 100)"
                        class="itbkk-name block w-full p-[2vh] resize-none text-sm bg-gray-50 rounded-lg border border-gray-300"
                        :class="newStatus.name.length == 100 ? ' text-gray-500' : ' text-gray-900'"
                        placeholder="Write your status's name" v-model="newStatus.name" :disabled="isDisibled" />
                    <p v-show="newStatus.name.length == 100" class="text-xs pl-3 pt-1 absolute">The name has a maximum
                        length of 100 characters.</p>
                </div>

                <div class="grid grid-cols-12 gap-[3vh] pt-2">
                    <div class="grid col-span-8">
                        <div>
                            <label for="description">Description</label>
                            <textarea rows="15" id="description"
                                class="itbkk-description block w-full p-[2vh] resize-none overflow-auto text-sm bg-gray-50 rounded-lg border border-gray-300"
                                :class="newStatus.description && newStatus.description.length == 500 ? ' text-gray-500' : ' text-gray-900'"
                                placeholder="Write your description" name="description"
                                @input="checkLength('description', newStatus.description, 500)"
                                v-model="newStatus.description" :disabled="isDisibled"></textarea>
                            <p v-show="newStatus.description && newStatus.description.length == 500"
                                class="text-xs pl-3 pt-1 absolute">
                                The description has a maximum length of 500 characters.
                            </p>
                        </div>
                    </div>


                    <div class="flex col-span-4 flex-col justify-between">
                        <div>
                            <div>
                                <label for="color">Color</label>
                                <input type="color" class="p-1 h-10 w-14 block bg-white border border-gray-200 cursor-pointer rounded-lg " id="color" title="Choose your color" v-model="newStatus.color">
                            </div>
                        </div>
                        <div class="pt-[4vh]">
                            <div class="flex justify-evenly">
                                <button class="itbkk-button-confirm btn btn-success btn-xs sm:btn-sm md:btn-md lg:btn-lg"
                                    @click="submitStatus(true)"
                                    :class="newStatus.name.trim().length <= 0 || ($route.name == 'statusEdit' && !isChanged) || (newStatus.name.trim().length <= 0 && $route.name == 'statusAdd') ? 'disabled' : ''"
                                    :disabled="newStatus.name.trim().length <= 0 || ($route.name == 'statusEdit' && !isChanged) || (newStatus.name.trim().length <= 0 && $route.name == 'statusAdd')">
                                    save
                                </button>
                                <button class="itbkk-button-cancel btn btn-error btn-xs sm:btn-sm md:btn-md lg:btn-lg"
                                    @click="submitStatus(false)">cancel</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
