<script setup>
import { ref, onMounted} from 'vue'

onMounted(() => {
    
})
</script>

<template>
    <div class="">
        <ConfirmDelete v-if="deleteModal" :task="selectedTask" :number="selectedIndex" @closeModal="handleDeleteModal" />
        <table class="myTable shadow-lg">
            <thead>
                <tr class="text-lg text-black" :class="myTheme.getTableTheme()">
                    <th></th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(task, index) in myTasks.tasks" :key="task.id" class="itbkk-item">
                    <td>
                        <div class="flex">
                            {{ index + 1 }}
                            <div class="itbkk-button-action dropdown dropdown-hover">
                                <div tabindex="0" role="button" class="">
                                    <img src="/element/dots.png" class="w-[2vh] h-[2vh]" alt="list img" />
                                </div>
                                <ul tabindex="0"
                                    class="dropdown-content z-[1] w-fit menu p-2 shadow bg-base-100 rounded-box"
                                    :class="myTheme.getAlterTheme()">
                                    <li>
                                        <p class="itbkk-button-edit" @click="toEditPage(task.id)">Edit</p>
                                    </li>

                                    <li>
                                        <p class="itbkk-button-delete" @click="deleteTask(task, index + 1)">Delete</p>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </td>
                    <td :class="$route.name != 'home' ? '' : 'itbkk-title'"
                        class="font-bold h-[30px] text-[2vh] hover:text-blue-500 break-all hover:cursor-pointer"
                        @click="getTask(task.id)">
                        {{ task.title }}
                    </td>
                    <td
                        :class="[$route.name !== 'home' ? '' : 'itbkk-assignees', task.assignees ? '' : 'italic text-gray-500']">
                        {{ task.assignees ? task.assignees : 'Unassigned' }}
                    </td>

                    <td :class="$route.name != 'home' ? '' : 'itbkk-status'">
                        <button class="rounded-2xl w-[100px] h-[30px] text-[2vh] font-bold cursor-default"
                            :class="colorStatus(task.status)">
                            {{ task.status }}
                        </button>
                    </td>
            </tr>
        </tbody>
    </table>
</div></template>


<style lang="scss" scoped>

</style>