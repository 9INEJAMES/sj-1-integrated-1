<script setup>
import { useRoute, useRouter } from 'vue-router'
import { onMounted, ref, watch } from 'vue'
import { useTasksStore } from '../stores/task.js'
import { useTheme } from '@/stores/theme.js'
import { useTaskApi } from '@/composables/task-api'
import { useStatusesStore } from '../stores/status.js'
import { useBoardStore } from '@/stores/board'
import { useAuthStore } from '@/stores/auth'
import { useAttachmentApi } from '@/composables/attachment-api'
import { useToast } from '@/stores/toast'

const authStore = useAuthStore()
const statusStore = useStatusesStore()
const statusList = ref([])
const themeStore = useTheme()
const taskStore = useTasksStore()
const taskApi = useTaskApi()
const boardStore = useBoardStore()
const attachmentApi = useAttachmentApi()
const toastStore = useToast()

const route = useRoute()
const router = useRouter()

const isChanged = ref(false)
const isDisibled = ref(false)
const localTimeZone = ref('')
const createdOn = ref('')
const updatedOn = ref('')
const limitTask = ref({})
const isCanEdit = ref(false)
const maxFiles = ref(10)
const disableFileInput = ref(false)

const files = ref([])

let task
let fileName
const newTask = ref({
    title: '',
    description: '',
    assignees: '',
    status: 1,
})
const oldTask = ref({
    title: '',
    description: '',
    assignees: '',
    status: 1,
})

const isNullStr = (str) => {
    if (str == null || str.trim().length == 0) {
        return null
    } else return str
}

const errorFiles = ref([])

const submitTask = async (isSave) => {
    if (isSave) {
        newTask.value.description = isNullStr(newTask.value.description)
        newTask.value.assignees = isNullStr(newTask.value.assignees)

        if (route.params.taskId) {
            const updated = await taskApi.updateTask(route.params.bid, newTask.value, taskStore.uploadFiles.value)
            if (updated) {
                taskStore.updateTask({
                    ...updated,
                })
            }

        } else {
            const task = await taskApi.addTask(route.params.bid, newTask.value)
            if (task) taskStore.addTask(task)
        }
    }
    taskStore.uploadFiles = []
    router.back()
}

const checkLength = (name, value, length) => {
    if (value.trim().length > length) {
        if (name === 'title') newTask.value.title = value.trim().slice(0, length)
        if (name === 'description') newTask.value.description = value.trim().slice(0, length)
        if (name === 'assignees') newTask.value.assignees = value.trim().slice(0, length)
    }
}
const switchTimeZone = (task) => {
    localTimeZone.value = Intl.DateTimeFormat().resolvedOptions().timeZone
    createdOn.value = new Date(task.createdOn).toLocaleString('en-GB', {
        timeZone: localTimeZone.value,
    })
    updatedOn.value = new Date(task.updatedOn).toLocaleString('en-GB', {
        timeZone: localTimeZone.value,
    })
}
onMounted(async () => {
    await statusStore.fetchStatuses(route.params.bid)
    limitTask.value = boardStore.findBoard(route.params.bid)
    statusList.value = statusStore.statuses

    if (route.name === 'taskDetails') {
        isDisibled.value = true
    }

    if (route.name !== 'taskAdd') {
        const id = route.params.taskId
        task = await taskApi.getTaskById(route.params.bid, id)
        if (!task) {
            router.push({ name: 'taskView' })
        } else {
            newTask.value = {
                id: task.id,
                title: task.title,
                description: task.description ?? '',
                assignees: task.assignees ?? '',
                status: task.status.id,
                attachments: task.attachments,
                createdOn: task.createdOn,
                updatedOn: task.updatedOn,
                numOfAttachments: task.attachments.length,
            }
            oldTask.value = { ...newTask.value }
            switchTimeZone(task)

            if (newTask.value.attachments) {
                for (const attachment of newTask.value.attachments) {
                    loadFileLocation(attachment.location.split('/').pop())
                }
            }
        }
    }
    checkLimitStatus(oldTask.value.status)
    isCanEdit.value = await authStore.isEditor(route.params.bid)
})

const currStatus = ref(0)
const isNotDefault = ref(false)
const checkLimitStatus = (id) => {
    currStatus.value = taskStore.tasks.filter((t) => t.status.id == newTask.value.status).length
    if (id) {
        const newStatus = statusStore.findStatusById(id)
        isNotDefault.value = newStatus.name != 'No Status' && newStatus.name != 'Done'
    }
}

const fileUrls = ref({})

const loadFileLocation = async (fileName) => {
    fileUrls.value[fileName] = await attachmentApi.loadFileDisplay(route.params.bid, newTask.value.id, fileName)
}

const removeAttachmentFromTask = (attachments) => {
    newTask.value.attachments = newTask.value.attachments.filter((attachment) => attachment.attachmentId !== attachments.attachmentId)
}

const removeFileFromInput = (index) => {
    files.value = Array.from(files.value).filter((file, i) => i !== index)
}

const checkTaskChange = () => {
    if (Array.from(files.value).some(file => newTask.value.attachments.some(f => f.location.split('/').pop() == file.name))) {
                toastStore.changeToast('error', 'Error', `File with the same filename cannot be added or updated to the attachements. Please delete the attachment and add again  to update the file.`)
        console.log('files', newTask.value.attachments)
        isChanged.value = false
    } else {
        isChanged.value = true
    }
}
const handleFileUpload = (e) => {
    files.value = e.target.files
    for (const file of files.value) {
        if (file.size > 20 * 1024 * 1024) {
            errorFiles.value.push(file)
            files.value = Array.from(files.value).filter((f) => f !== file)
        }

    }
    if (errorFiles.value.length > 0) {
        toastStore.changeToast('error', 'Error', `List of files that exceed the maximum size of 20MB: ${errorFiles.value.map((file) => file.name).join(', ')}`)
        errorFiles.value = []
    }
    if (files.value.length + newTask.value.attachments.length > maxFiles.value) {
        const left = maxFiles.value - newTask.value.attachments.length
        errorFiles.value = Array.from(files.value).slice(left)
        files.value = Array.from(files.value).slice(0, left)
        console.log('files', files.value)
        console.log('errorFiles', errorFiles.value)
        // isChanged.value = false
        toastStore.changeToast('error', 'Error', `Each task can have at most ${maxFiles.value} files. The following files are not added: ${errorFiles.value.map((file) => file.name).join(', ')}`)
    }
    // if (Array.from(files.value).some(file => newTask.value.attachments.some(f => f.location.split('/').pop() == file.name))) {
    //     console.log('errorFiles', Array.from(files.value).some(file => newTask.value.attachments.some(f => f.location.split('/').pop() == file.name)))
    //     isChanged.value = false
    // }
    else if (files) {
        taskStore.uploadFiles.value = files
        newTask.value.numOfAttachments += files.value.length
        console.log(files.value.length)
    }
}

watch(
    newTask,
    () => {
        if (JSON.stringify(newTask.value) === JSON.stringify(oldTask.value)) isChanged.value = false
        else checkTaskChange()
    },
    { deep: true }
)

</script>

<template>
    <div class="fixed inset-0 flex justify-center items-center bg-black bg-opacity-50 w-full">
        <div class="itbkk-modal-task my-[10vh] mx-[10vh] h-fit w-full rounded-lg pb-2" :class="themeStore.getTheme()">
            <!-- <p v-if="route.name == 'taskDetails' && newTask?.id == null">The requested task does not exist</p> -->
            <div class="grid gap-[2vh] border-none p-[2vh]">
                <div class="flex justify-between items-center">
                    <p class="text-2xl font-semibold" :class="themeStore.getTextHeaderTheme()">
                        {{ route.name != 'taskDetails' ? (route.params.taskId ? 'Edit' : 'New') : '' }}
                        Task {{ route.name == 'taskDetails' ? 'Details' : '' }}
                    </p>
                    <button v-if="route.name != 'taskDetails'" class="rounded-2xl text-md font-normal cursor-default"
                        :class="limitTask.limit ? 'bg-green-400  text-zinc-700  w-[24vh] h-[36px]' : 'text-zinc-300 bg-slate-500 w-[15vh] h-[40px] text-[2vh]'">
                        {{ limitTask.limit ? `Limit ${limitTask.limitMaximumTask} task enable` : 'Limit disable' }}
                    </button>
                </div>
                <hr />
                <div>
                    <p :class="themeStore.getTextHeaderTheme()">Title<span v-if="route.name != 'taskDetails'"
                            class="text-red-600">*</span></p>
                    <p v-if="$route.name == 'taskDetails'" id="title"
                        class="itbkk-title block w-full p-[2vh] resize-none text-sm bg-gray-50 rounded-lg border border-gray-300"
                        :class="newTask.title.length == 100 ? ' text-gray-500' : ' text-gray-900'">
                        {{ newTask.title }}
                    </p>
                    <input v-else type="text" name="title" @input="checkLength('title', newTask.title, 100)"
                        class="itbkk-title block w-full p-[2vh] resize-none text-sm bg-gray-50 rounded-lg border border-gray-300"
                        :class="newTask.title.length == 100 ? ' text-gray-500' : ' text-gray-900'" id="title"
                        placeholder="Write your task's title" v-model="newTask.title" :disabled="isDisibled" />
                    <p v-show="$route.name != 'taskDetails' && newTask.title.length == 100"
                        class="text-xs pl-3 pt-1 absolute overflow-auto">The title have a maximum length of 100 characters.
                    </p>
                </div>

                <div class="grid grid-cols-12 gap-[3vh] pt-2">
                    <div :class="route.name !== 'taskAdd' ? 'grid col-span-5' : 'grid col-span-8'">
                        <div>
                            <p :class="themeStore.getTextHeaderTheme()">Description</p>
                            <p v-if="$route.name == 'taskDetails'" id="description"
                                class="itbkk-description block min-w-full max-h-fit p-[2vh] resize-none text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 break-all h-5/6"
                                :class="newTask.description ? '' : 'italic text-gray-500'">
                                {{ newTask.description ? newTask.description : 'No Description Provided' }}
                            </p>
                            <textarea v-else rows="14" id="description"
                                class="itbkk-description block w-full p-[2vh] resize-none overflow-auto text-sm bg-gray-50 rounded-lg border border-gray-300"
                                :class="newTask.description && newTask.description.length == 500 ? ' text-gray-500' : ' text-gray-900'"
                                placeholder="Write your description" name="description"
                                @input="checkLength('description', newTask.description, 500)" v-model="newTask.description"
                                :disabled="isDisibled"></textarea>
                            <p v-show="$route.name != 'taskDetails' && newTask.description && newTask.description.length == 500"
                                class="text-xs pl-3 pt-1 absolute overflow-auto">
                                The description have a maximum length of 500 characters.
                            </p>
                        </div>
                    </div>

                    <div v-if="route.name !== 'taskAdd'" class="col-span-3">
                        <div class="flex flex-col">
                            <div v-if="$route.name == 'taskEdit'">
                                <p :class="themeStore.getTextHeaderTheme()" class="pt-[2vh]">Uploading attachment{{
                                    files.length > 0 ? 's' : '' }}</p>
                                <div>
                                    <input type="file" @change="handleFileUpload" multiple :disabled="disableFileInput" />
                                </div>
                                <div class="overflow-y-scroll h-fit">
                                    <div v-for="(file, index) in files" :key="index">
                                        <div class="flex justify-between">
                                            <p>{{ file.name }}</p>
                                            <p class="hover:text-red-500" @click="removeFileFromInput(index)">x</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div>
                                <p :class="themeStore.getTextHeaderTheme()" class="pt-[2vh]">Attachments</p>
                                <div class="overflow-y-scroll h-fit">
                                    <div v-for="(attachments, attachmentId) in newTask.attachments" :key="attachmentId"
                                        class="overflow-y-auto">
                                        <div
                                            class="flex gap-2 items-center hover:text-pink-500 hover:underline justify-between">
                                            <p
                                                @click="attachmentApi.downloadFile(route.params.bid, newTask.id, attachments.attachmentId, attachments.location)">
                                                {{ (fileName = attachments.location.split('/').pop()) }}
                                            </p>
                                            <div class="flex gap-3">
                                                <img v-if="fileName.endsWith('.png') || fileName.endsWith('.jpg') || fileName.endsWith('.jpg')"
                                                    :src="fileUrls[fileName]" alt="Attachment preview"
                                                    class="w-[20px] h-[20px]" />
                                                <img v-else src="/file.png" alt="file" class="w-[15px] h-[15px]" />
                                                <img v-if="route.name !== 'taskDetails'" src="/delete.png" alt="delete"
                                                    class="w-[15px] h-[15px]"
                                                    @click="removeAttachmentFromTask(attachments)" />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="flex col-span-4 flex-col justify-between">
                        <div>
                            <div>
                                <p :class="themeStore.getTextHeaderTheme()">Assignees</p>
                                <p v-if="$route.name == 'taskDetails'" id="assignees"
                                    class="itbkk-assignees block p-[2vh] w-full resize-none text-sm bg-gray-50 rounded-lg border border-gray-300 break-all"
                                    :class="newTask.assignees ? 'text-gray-900' : 'italic text-gray-500'">
                                    {{ newTask.assignees ? newTask.assignees : 'Unassigned' }}
                                </p>
                                <textarea v-else id="assignees" rows="1"
                                    class="itbkk-assignees block p-[2vh] w-full resize-none text-sm bg-gray-50 rounded-lg border border-gray-300"
                                    :class="newTask.assignees && newTask.assignees.length == 30 ? ' text-gray-500' : ' text-gray-900'"
                                    placeholder="Enter assignees" name="assignees"
                                    @input="checkLength('assignees', newTask.assignees, 30)" v-model="newTask.assignees"
                                    :disabled="isDisibled"></textarea>
                                <p v-show="$route.name != 'taskDetails' && newTask.assignees && newTask.assignees.length == 30"
                                    class="text-xs pl-3 pt-1 overflow-auto break-all">
                                    The assignees have a maximum length of 30 characters.
                                </p>
                            </div>

                            <div class="flex flex-col pt-[3vh]">
                                <select v-model="newTask.status" id="status" @change="checkLimitStatus(newTask.status)"
                                    class="itbkk-status select select-bordered" :class="themeStore.getTextHeaderTheme()"
                                    :disabled="isDisibled">
                                    <option v-for="status in statusList" :disabled="status.name == newTask.status"
                                        :value="status.id">{{ status.name }}</option>
                                </select>
                                <p v-if="$route.name != 'taskDetails' && limitTask.limit && isNotDefault"
                                    class="text-end font-semibold text-sm m-2">
                                    Status usage: {{ currStatus }}/{{ limitTask.limitMaximumTask }} tasks
                                </p>
                            </div>

                            <div v-if="$route.name != 'taskAdd'" class="pt-[4vh] text-sm">
                                <p class="itbkk-timezone"><span :class="themeStore.getTextHeaderTheme()" class="">Local Time
                                        Zone: </span> {{ localTimeZone }}</p>
                                <p class="itbkk-created-on"><span :class="themeStore.getTextHeaderTheme()">Created On:
                                    </span>{{ createdOn }}</p>
                                <p class="itbkk-updated-on"><span :class="themeStore.getTextHeaderTheme()">Last Updated On:
                                    </span>{{ updatedOn }}</p>
                            </div>
                        </div>
                        <div class="pt-[4vh]">
                            <div v-if="$route.name != 'taskDetails'" class="flex justify-evenly">
                                <div :class="!isCanEdit ? 'tooltip tooltip-bottom' : ''"
                                    data-tip="You need to be board owner to perform this action">
                                    <button
                                        class="itbkk-button-confirm btn btn-success btn-xs sm:btn-sm md:btn-md lg:btn-lg"
                                        @click="submitTask(true)" :class="newTask.title.trim().length <= 0 ||
                                            ($route.name == 'taskEdit' && !isChanged) ||
                                            (newTask.title.trim().length <= 0 && $route.name == 'taskAdd') ||
                                            !isCanEdit
                                            ? 'disabled'
                                            : ''
                                            " :disabled="newTask.title.trim().length <= 0 ||
        ($route.name == 'taskEdit' && !isChanged) ||
        (newTask.title.trim().length <= 0 && $route.name == 'taskAdd') ||
        !isCanEdit
        ">
                                        save
                                    </button>
                                </div>

                                <button class="itbkk-button-cancel btn btn-error btn-xs sm:btn-sm md:btn-md lg:btn-lg"
                                    @click="submitTask(false)">cancel</button>
                            </div>
                            <div v-else class="flex justify-end items-end">
                                <button class="itbkk-button-close btn btn-error text-white"
                                    @click="submitTask(false)">close</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
