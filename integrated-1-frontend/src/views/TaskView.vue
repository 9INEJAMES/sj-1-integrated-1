<script setup>
import { ref, onMounted, onUpdated, watch } from "vue"
import TaskTable from "@/components/TaskTable.vue"
import { useTasksStore } from "@/stores/task.js"
import Addicon from "../../public/Addicon.vue"
import { useToast } from "@/stores/toast.js"
import { useTaskApi } from "@/composables/task-api.js"
import VToast from "@/ui/VToast.vue"
import { useTheme } from "@/stores/theme"
import router from "@/router"

const taskApi = useTaskApi()
const myTasks = useTasksStore()
const myToast = useToast()
const isSelectTask = ref(false)
const myTheme = useTheme()
const selectedTask = ref({})

watch(myToast.currToast, async () => {
  if (myToast.currToast.style === "alert-error") {
    myTasks.fetchTasks()
    isSelectTask.value = false
  }
})

const chosenTask = async (id) => {
  selectedTask.value = await taskApi.getTaskById(id)
  isSelectTask.value = true
}
const addTaskBtn = () => {
  router.push({
    name: "taskAdd",
  })
}
</script>

<template>
  <VToast class="z-50" />
  <div class="flex justify-between pt-[5vh] pl-[5vh] pr-[5vh]">
    <RouterLink to="/status"><button class="btn btn-xs sm:btn-sm md:btn-md lg:btn-lg">Manage Status</button></RouterLink>
    <RouterLink v-show="$route.name == 'home'" to="/task/add" class="itbkk-button-add">
      <Addicon v-show="$route.name == 'home'" :class="myTheme.getAlterTheme()"
        class=" w-[13vh] h-[3vh]  transition-all  ease-in hover:cursor-pointer" />
    </RouterLink>
  </div>
  <RouterView class="z-40" />
  <div class="px-[5vh] pt-[1vh]">
    <div class="">
      <TaskTable @get-task="chosenTask"></TaskTable>
    </div>
  </div>

 
</template>

<style scoped></style>
