<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  task: {
    type: Object
  }
})

const emit = defineEmits(['saveTask', 'deleteTask'])

const localTimeZone = ref('')
const createdOn = ref('')
const updatedOn = ref('')
const switchTimeZone = () => {
  // Get the local time zone of the device
    localTimeZone.value = Intl.DateTimeFormat().resolvedOptions().timeZone
    createdOn.value = props.task.createdOn.slice(0, 19)
  updatedOn.value = props.task.updatedOn.slice(0, 19)
}

const convertToLocalTime = (dateTimeString) => {
  const utcDateTime = new Date(dateTimeString)
  return new Date(utcDateTime.toLocaleString('en-US', { timeZone: 'America/New_York' }))
}

onMounted(() => {
  // Call the switchTimeZone function when the component is mounted
  switchTimeZone()
  emit('saveTask', {
    ...props.task,
    createdOn: convertToLocalTime(props.task.createdOn.slice(0, 19)),
    updatedOn: convertToLocalTime(props.task.updatedOn.slice(0, 19))
  })
})
</script>

<template>
  <div class="py-[10vh] px-[10vh] fixed inset-0 flex justify-center bg-black bg-opacity-50 w-full">
    <div class="bg-white w-full rounded-lg">
      <form class="grid gap-[2vh] rounded-md border-none p-[2vh]" @submit.prevet="">
        <input type="text" class="font-bold text-[4vh]" v-model="task.title" />
        <hr />
        <div class="grid grid-cols-12 gap-[3vh]">
          <div class="grid col-span-8">
            <div>
              <label for="description">Description</label>
              <textarea
                id="description"
                v-model="task.description"
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
                  v-model="task.assignees"
                  rows="5"
                  class="block p-[2vh] w-full resize-none text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                  placeholder="Write your thoughts here..."
                ></textarea>
              </div>
              <div class="flex flex-col pt-[3vh]">
                <label for="status">Status</label>
                <select id="status" name="carlist" form="carform" class="border rounded-lg">
                  <option selected disabled hidden value="" class="">Status</option>
                  <option value="No status">No status</option>
                  <option value="To do">To do</option>
                  <option value="Doing">Doing</option>
                  <option value="Doing">Done</option>
                </select>
              </div>
              <div class="pt-[4vh]">
                <p>Local Time Zone: {{ localTimeZone }}</p>
                <p>Created On: {{ createdOn }}</p>
                <p>Last Updated On: {{ updatedOn }}</p>
              </div>

              <div class="pt-[4vh] flex justify-evenly">
                <button class="btn btn-success btn-xs sm:btn-sm md:btn-md lg:btn-lg">
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
