<script setup>
import { ref, onMounted } from 'vue'
import { useStatusesStore } from '../stores/status.js'
import { useTheme } from '@/stores/theme'
import ConfirmDelete from './ConfirmDelete.vue'

const myStatuses = useStatusesStore()
const myTheme = useTheme()
const deleteModal = ref(false)
const selectedIndex = ref(null)
const selectStatus = ref(null)
const emit = defineEmits(['getStatus'])
onMounted(async () => {
  if (myStatuses.statuses.length <= 0) {
    await myStatuses.fetchStatuses()
  }
})
const toEditPage = (id) => {
  router.push({
    name: 'statusEdit',
    params: {
      id: id,
    },
  })
  if (selectStatus.value === null) {
    selectStatus.value = myTasks.getIdOfTask(id)
  }
  updateModal.value = true
}

const deleteStatus = (status, index) => {
  if (selectStatus.value === null) {
    selectStatus.value = status
    selectedIndex.value = index
  }
  deleteModal.value = true
}

const handleDeleteModal = () => {
  deleteModal.value = false
  selectStatus.value = null
  selectedIndex.value = null
}
</script>

<template>
  <div class="">
    <ConfirmDelete v-if="deleteModal" mode="status" :object="selectStatus" :number="selectedIndex" @closeModal="handleDeleteModal" />
    <table class="myTable shadow-lg">
      <thead>
        <tr class="text-lg text-black" :class="myTheme.getTableTheme()">
          <th style="width: 5%"></th>
          <th style="width: 25%">Name</th>
          <th style="width: 50%">Description</th>
          <th style="width: 20%" class="text-center">Action</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(status, index) in myStatuses.statuses" :key="status.id" class="itbkk-item">
          <td>
            <div class="flex">
              {{ index + 1 }}
            </div>
          </td>
          <td :class="$route.name != 'home' ? '' : 'itbkk-title'" class="font-bold h-[30px] text-[2vh] hover:text-blue-500 break-all hover:cursor-pointer" @click="getStatus(status.id)">
            {{ status.name }}
          </td>
          <td :class="[$route.name !== 'home' ? '' : 'itbkk-assignees', status.assignees ? '' : 'italic text-gray-500']">
            {{ status.description ? status.description : 'Unassigned' }}
          </td>
          <td>
            <div class="flex justify-around">
              <div class="btn btn-sm" @click="toEditPage(status.id)">Edit <img src="/edit.png" alt="edit picture" class="w-4 h-4" /></div>
              <div class="btn btn-sm" @click="deleteStatus(status, index + 1)">Delete <img src="/delete.png" alt="delete picture" class="w-4 h-4" /></div>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style lang="scss" scoped></style>
