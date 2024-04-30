<script setup>
import { ref, defineProps, defineEmits } from 'vue';
import ConfirmDelete from './confirmDelete.vue';

const deleteModal = ref(false)
const props = defineProps({
  taskList: {
    type: Array,
  },
})
const emit = defineEmits(["getTask"])
const getTask = (id) => {
    emit("getTask", id)
}
const colorStatus = (task) => {
    if (task == "Done") {
        return "bg-emerald-500"
    } else if (task == "Doing") {
        return "bg-sky-300"
    } else if (task == "No Status") {
        return "bg-slate-300"
    } else {
        return "bg-amber-300"
    }
}

const toEditPage = (id) => {
    router.push({ path: `/edit/${id}` })
}

// Define a selectedTask ref to store the selected task
const selectedTask = ref(null);

const deleteTask = (task) => {
    // Set the selectedTask to the task to be deleted
    selectedTask.value = task;
    // Open the delete modal
    deleteModal.value = true;
}
</script>

<template>
    <div class="">
      <table class="myTable">
        <!-- head -->
        <thead>
          <tr>
            <th></th>
            <th>Title</th>
            <th>Assignees</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody> <!-- Add tbody here -->
          <tr v-for="(task, index) in taskList" :key="task.id" class="itbkk-item">
            <!-- Pass selectedTask to ConfirmDelete component -->
            <ConfirmDelete v-if="deleteModal" :task="selectedTask" @closeModal="deleteModal = false" />
            <td>
              <div class="flex">
                {{ index + 1 }}
                <div class="dropdown dropdown-hover">
                  <div tabindex="0" role="button">
                    <img src="/element/dots.png" class=" w-[2vh] h-[2vh]" alt="list img">
                  </div>
                  <ul tabindex="0" class="dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box w-52">
                    <li><p @click="">edit</p></li>
                    <!-- Pass the task to deleteTask method when delete is clicked -->
                    <li><p @click="deleteTask(task)">delete</p></li>
                  </ul>
                </div>
              </div>
            </td>
            <td class="itbkk-title">
              <p class="font-bold pb-[2vh] hover:text-blue-500 break-all hover:cursor-pointer" @click="getTask(task.id)">
                {{ task.title }}
              </p>
            </td>
            <td class="itbkk-assignees" :class="task.assignees ? '' : 'italic text-gray-500'">
              {{ task.assignees ? task.assignees : "Unassigned" }}
            </td>
            <td class="itbkk-status">
              <button class="rounded-2xl w-[100px] h-[30px] text-[2vh] font-bold cursor-default"
                  :class="colorStatus(task.status)">
                  {{ task.status }}
              </button>
            </td>
          </tr>
        </tbody> <!-- Close tbody here -->
      </table>
    </div>
  </template>
  

<style scoped></style>
