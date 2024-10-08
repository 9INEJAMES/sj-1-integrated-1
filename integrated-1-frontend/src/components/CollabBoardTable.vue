<script setup>
import { useTheme } from '@/stores/theme.js'
import { useCollabStore } from '@/stores/collab';


const themeStore = useTheme()
const collabStore = useCollabStore()


</script>

<template>
    <div>
        <table class="myTable table-lg table-pin-rows shadow-lg">
            <thead class="w-full">
                <tr class="text-lg" :class="themeStore.getTableTheme()">
                    <th style="width: 1%">No</th>
                    <th style="width: 1%">Name</th>
                    <th style="width: 1%">Owner</th>
                    <th style="width: 1%">Access Right</th>
                    <th style="width: 1%">Action</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(collabBoard, index) in collabStore.collabBoards" :key="collabBoard.id" class="itbkk-item">
                    <td>
                        <div class="flex">
                            {{ index + 1 }}
                        </div>
                    </td>
                    <td>{{ collabBoard.name }}</td>
                    <td :class="themeStore.isLight ? 'hover:text-pink-300' : 'hover:text-cyan-500'"
                        class="itbkk-title h-[30px]  break-all" >
                        {{  collabBoard.owner.name }}
                    </td>
                    <td>
                        {{ collabBoard.accessRight }}
                    </td>
                    <td>
                        <button class="btn">
                            Leave
                        </button>
                    </td>

                </tr>
            </tbody>

        </table>
    </div>
</template>

<style lang="scss" scoped></style>