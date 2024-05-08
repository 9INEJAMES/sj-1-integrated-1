<script setup>
import { ref, onMounted } from 'vue'
import { useStatusesStore } from '../stores/status.js'
import { useTheme } from "@/stores/theme"

const myStatuses = useStatusesStore()
const myTheme = useTheme()

const emit = defineEmits(['getStatus'])
onMounted(async () => {
    console.log(myStatuses.statuses)
    if (myStatuses.statuses.length <= 0) {
        await myStatuses.fetchStatuses()
    }
})
</script>

<template>
    <div class="">
        <ConfirmDelete v-if="deleteModal" :Status="selectedStatus" :number="selectedIndex"
            @closeModal="handleDeleteModal" />
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
                    <td :class="$route.name != 'home' ? '' : 'itbkk-title'"
                        class="font-bold h-[30px] text-[2vh] hover:text-blue-500 break-all hover:cursor-pointer"
                        @click="getStatus(status.id)">
                        {{ status.name }}
                    </td>
                    <td
                        :class="[$route.name !== 'home' ? '' : 'itbkk-assignees', status.assignees ? '' : 'italic text-gray-500']">
                        {{ status.description ? status.description : 'Unassigned' }}
                    </td>
                    <td>
                        <div class="flex justify-around">
                            <div class="btn btn-sm">
                                Edit <img src="/edit.png" alt="edit picture" class=" w-4 h-4">
                            </div>
                            <div class="btn btn-sm">
                                Delete <img src="/delete.png" alt="delete picture" class=" w-4 h-4">
                            </div>
                        </div>
                    </td>

                </tr>
            </tbody>
        </table>
    </div>
</template>


<style lang="scss" scoped></style>