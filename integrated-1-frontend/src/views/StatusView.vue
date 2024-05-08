<script setup>
import { useStatusApi } from "@/composables/status-api"
import StatusTable from "@/components/StatusTable.vue";
import { useTheme } from "@/stores/theme"

const myTheme = useTheme()

const chosenStatus = async (id) => {
  selectedStatus.value = await statusApi.getStatusById(id)
  isSelectStatus.value = true
}
</script>

<template>
  <div class="flex justify-start pt-[5vh] pl-[5vh]">
    <RouterLink to="/"><button class="btn btn-xs sm:btn-sm md:btn-md lg:btn-lg">Manage Status</button></RouterLink>

  </div>
  <div class="px-[5vh] pt-[1vh]">
    <StatusTable @get-Status="chosenStatus"></StatusTable>

  </div>
</template>



<style lang="scss" scoped></style>