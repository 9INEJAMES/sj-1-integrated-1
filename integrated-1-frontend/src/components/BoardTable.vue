<script setup>
import { ref, onMounted } from 'vue'
import ConfirmDelete from './ConfirmDelete.vue'
import router from '../router/index.js'
import { useBoardStore } from '../stores/board'
import { useTheme } from '@/stores/theme.js'

const base = import.meta.env.VITE_BASE
const themeStore = useTheme()
const deleteModal = ref(false)
const boardStore = useBoardStore()
const selectedBoard = ref(null)
const selectedIndex = ref(null)

onMounted(async () => {
    console.log(boardStore.fetchBoard())
})
</script>

<template>
    <div>
        <ConfirmDelete v-if="deleteModal" mode="board" :object="selectedBoard" :number="selectedIndex" @closeModal="handleDeleteModal" />
        <table class="myTable table-pin-rows shadow-lg">
            <thead class="w-full">
                <tr class="text-lg" :class="themeStore.getTableTheme()">
                    <th style="width: 1%"></th>
                    <th style="width: 55%">No</th>
                    <th style="width: 25%">Name</th>
                    <th style="width: 10%" class="text-center">action</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(board, index) in boardStore.boards" :key="board.id" class="itbkk-item">
                    <td>
                        <div class="flex">
                            {{ index + 1 }}
                        </div>
                    </td>
                    <td :class="themeStore.isLight ? 'hover:text-pink-300' : 'hover:text-cyan-500'" class="itbkk-title font-bold h-[30px] text-[2vh] break-all hover:cursor-pointer" @click="">
                        {{ board.name }}
                    </td>
                    <td class="break-all" >
                        {{  }}
                    </td>
                    <td >
                        <button class="rounded-2xl w-[100px] h-[30px] text-[2vh] font-bold cursor-default text-black">
                            {{  }}
                        </button>
                    </td>
                    <td>
                        <div class="flex justify-center items-center h-full">
                            <div class="itbkk-button-action dropdown dropdown-hover">
                                <div tabindex="0" role="button" class="">
                                    <img src="/element/dots.png" class="w-[2vh] h-[2vh]" alt="list img" />
                                </div>
                                <ul tabindex="0" class="dropdown-content z-[1] w-fit menu p-2 shadow bg-base-100 rounded-box" :class="themeStore.getAlterTheme()">
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
                </tr>
            </tbody>
        </table>
    </div>
</template>

<style scoped></style>
