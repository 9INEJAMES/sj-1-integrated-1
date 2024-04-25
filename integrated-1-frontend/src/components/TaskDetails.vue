<script setup>
import { getTaskById, updateTask } from '@/libs/FetchAPI'
import { ref, onMounted, defineProps, defineEmits } from 'vue'
import { useTasks } from '../stores/task.js'
import { useVariables } from '../stores/store.js'
import { useRoute } from 'vue-router'

// const route = useRoute()
// route.params.taskId
const myTasks = useTasks()
const myVariables = useVariables()
const isSelectTask = ref(false)
const selectedTask = ref({})
const foundTask = ref(false)
const props = defineProps({
  task: {
    type: Object
  }
})

const emit = defineEmits(['updatedTask', 'deleteTask','notFoundTask'])

const localTimeZone = ref('')
const createdOn = ref('')
const updatedOn = ref('')

const onSubmit = async () => {
  try {
    const Task = myTasks.getIdOfTask(props.task.id)
    const editedTask = {
      ...Task,
      title: selectedTask.value.title,
      description:
        selectedTask.value.description == null ||
        selectedTask.value.description.length === 0 ||
        selectedTask.value.description === ''
          ? (selectedTask.value.description = '')
          : selectedTask.value.description,
      assignees:
        selectedTask.value.assignees == null ||
        selectedTask.value.assignees.length === 0 ||
        selectedTask.value.assignees === ''
          ? (selectedTask.value.assignees = '')
          : selectedTask.value.assignees,
      status: selectedTask.value.status
    }
    await updateTask(editedTask)
    // location.reload();
    emit('updatedTask', editedTask) // Emit an event to parent component
    foundTask.value = false
  } catch (error) {
    console.error('Update Task Error:', error)
  }
}

const switchTimeZone = () => {
  localTimeZone.value = Intl.DateTimeFormat().resolvedOptions().timeZone

  if (selectedTask.value) {
    const localCreatedOn = new Date(selectedTask.value.createdOn).toLocaleString('en-GB', {
      timeZone: localTimeZone.value
    })
    const localUpdatedOn = new Date(selectedTask.value.updatedOn).toLocaleString('en-GB', {
      timeZone: localTimeZone.value
    })

    createdOn.value = localCreatedOn
    updatedOn.value = localUpdatedOn
  }
}

onMounted(async () => {
  try {
    selectedTask.value = ''
    isSelectTask.value = await myVariables.isSelectTask
    const task = myTasks.getIdOfTask(props.task.id)
    if (task === null || task === undefined) {
      foundTask.value = false
      setTimeout(() => {
        emit('notFoundTask') // Emit an evnt to the parent component after 3 minutes
      }, 3000)
    } else {
      foundTask.value = true
      console.log('Task found')
      selectedTask.value = await getTaskById(task.id)
      console.log(selectedTask.value)
      switchTimeZone(selectedTask.value)
      createdOn
    }
  } catch (error) {
    console.error('Error in onMounted hook:', error)
  }
})
</script>

<template>
  <div class="py-[10vh] px-[10vh] fixed inset-0 flex justify-center bg-black bg-opacity-50 w-full">
    <div class="bg-white w-full rounded-lg">
      <div v-if="foundTask">
        <form class="grid gap-[2vh] rounded-md border-none p-[2vh]" @submit.prevent="onSubmit()">
          <input
            type="text"
            name="title"
            class="font-bold text-[4vh]"
            id="title"
            v-model="selectedTask.title"
          />
          <hr />
          <div class="grid grid-cols-12 gap-[3vh]">
            <div class="grid col-span-8">
              <div>
                <label for="description">Description</label>
                <textarea
                  id="description"
                  v-model="selectedTask.description"
                  rows="20"
                  class="block w-full p-[2vh] resize-none text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                  placeholder="No description"
                ></textarea>
              </div>
            </div>

            <div class="grid col-span-4">
              <div>
                <div>
                  <label for="assignees">Assignees</label>
                  <textarea
                    id="assignees"
                    v-model="selectedTask.assignees"
                    rows="5"
                    class="block p-[2vh] w-full resize-none text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                    placeholder="Unassigned"
                  ></textarea>
                </div>
                <div class="flex flex-col pt-[3vh]">
                  <label for="status">Status</label>
                  <select id="status" v-model="selectedTask.status" class="select select-bordered">
                    <option selected disabled hidden value="">Status</option>
                    <option value="No Status">No Status</option>
                    <option value="To Do">To Do</option>
                    <option value="Doing">Doing</option>
                    <option value="Done">Done</option>
                  </select>
                </div>
                <div class="pt-[4vh]">
                  <p>Local Time Zone: {{ localTimeZone }}</p>
                  <p>Created On: {{ createdOn }}</p>
                  <p>Last Updated On: {{ updatedOn }}</p>
                </div>

                <div class="pt-[4vh] flex justify-evenly">
                  <button
                    type="submit"
                    class="btn btn-success btn-xs sm:btn-sm md:btn-md lg:btn-lg"
                    @click="onSubmit()"
                  >
                    Confirm
                  </button>
                  <button class="btn btn-error btn-xs sm:btn-sm md:btn-md lg:btn-lg">Cancel</button>
                </div>
              </div>
            </div>
          </div>
        </form>
      </div>
      <div v-else>
        <div class="flex justify-center items-center h-full">
          <p class="text-red-500 font-semibold">Task not found</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>