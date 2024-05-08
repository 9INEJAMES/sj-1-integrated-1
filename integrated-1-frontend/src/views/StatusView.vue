<script setup>
import { useStatusApi } from "@/composables/status-api"
import StatusTable from "@/components/StatusTable.vue"
import { useTheme } from "@/stores/theme"
import Addicon from "../../public/Addicon.vue"
import router from "@/router"

const myTheme = useTheme()

const chosenStatus = async (id) => {
  selectedStatus.value = await statusApi.getStatusById(id)
  isSelectStatus.value = true
}

const addTaskBtn = () => {
  router.push({
    name: "statusAdd",
  })
}
</script>

<template>
  <div class="flex justify-between  pt-[5vh] pl-[5vh] pr-[5vh]">
    <RouterLink to="/"><button class="btn btn-xs sm:btn-sm md:btn-md lg:btn-lg">Manage Task</button></RouterLink>
    <Addicon  :class="myTheme.getAlterTheme()" @click="addTaskBtn"
        class="  itbkk-button-add w-[20vh] h-[3vh]  transition-all  ease-in hover:cursor-pointer" />

  </div>
  
   <RouterView class="z-40" />
  <div class="px-[5vh] pt-[1vh]">
    <StatusTable @get-Status="chosenStatus"></StatusTable>

  </div>
</template>



<style lang="scss" scoped></style>