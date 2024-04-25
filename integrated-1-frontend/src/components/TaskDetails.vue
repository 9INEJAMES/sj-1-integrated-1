<script setup>
import { updateTask } from '@/libs/FetchAPI'
import router from '@/router'
import { ref, onMounted, defineProps, defineEmits } from 'vue'
import { useTasks } from '../stores/task.js'
import { useVariables } from '../stores/store.js'

const Tasks = useTasks()
const myVariables = useVariables()
const isSelectTask = ref(false)

const props = defineProps({
  task: {
    type: Object
  }
})

const emit = defineEmits(['updatedTask', 'deleteTask'])

const localTimeZone = ref('')
const createdOn = ref('')
const updatedOn = ref('')

const switchTimeZone = (task) => {
  localTimeZone.value = Intl.DateTimeFormat().resolvedOptions().timeZone
  const localCreatedOn = new Date(task.createdOn).toLocaleString('en-US', { timeZone: localTimeZone.value })
  const localUpdatedOn = new Date(task.updatedOn).toLocaleString('en-US', { timeZone: localTimeZone.value })

  createdOn.value = localCreatedOn
  updatedOn.value = localUpdatedOn
}

const onSubmit = async () => {
  try {
    const Task = Tasks.getTaskById(props.task.id)
    const editedTask = {
      ...Task,
      title: props.task.title,
      description: props.task.description,
      assignees: props.task.assignees,
      status: props.task.status
    }
    await updateTask(editedTask)
    emit('updatedTask') // Emit an event to parent component
  } catch (error) {
    console.error('Update Task Error:', error)
  }
}


onMounted(async () => {
  isSelectTask.value = await myVariables.isSelectTask
  const task = Tasks.getTaskById(props.task.id)

  switchTimeZone(task)
})
</script>

<template>
  <div class="py-[10vh] px-[10vh] fixed inset-0 flex justify-center bg-black bg-opacity-50 w-full">
    <div class="bg-white w-full rounded-lg">
      <form class="grid gap-[2vh] rounded-md border-none p-[2vh]" @submit.prevent="onSubmit()">
        <input
          type="text"
          name="title"
          class="font-bold text-[4vh]"
          id="title"
          v-model="props.task.title"
        />
        <hr />
        <div class="grid grid-cols-12 gap-[3vh]">
          <div class="grid col-span-8">
            <div>
              <label for="description">Description</label>
              <textarea
                id="description"
                v-model="props.task.description"
                rows="20"
                class="block w-full p-[2vh] resize-none text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                placeholder="Write your thoughts here..."
              ></textarea>
            </div>
          </div>

          <div class="grid col-span-4">
            <div>
              <div>
                <label for="assignees">Assignees</label>
                <textarea
                  id="assignees"
                  v-model="props.task.assignees"
                  rows="5"
                  class="block p-[2vh] w-full resize-none text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                  placeholder="Write your thoughts here..."
                ></textarea>
              </div>
              <div class="flex flex-col pt-[3vh]">
                <label for="status">Status</label>
                <select id="status" v-model="props.task.status" class="select select-bordered">
                  <option selected disabled hidden value="">Status</option>
                  <option value="No status">No status</option>
                  <option value="To do">To do</option>
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
                <button type="submit" class="btn btn-success btn-xs sm:btn-sm md:btn-md lg:btn-lg" @click="onSubmit()"> 
                  Confirm
                </button>
                <button class="btn btn-error btn-xs sm:btn-sm md:btn-md lg:btn-lg">Cancel</button>
              </div>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
