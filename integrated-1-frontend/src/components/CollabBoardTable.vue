<script setup>
import { useTheme } from '@/stores/theme.js'
import { useCollabStore } from '@/stores/collab';
import { useCollabApi } from '@/composables/collab-api';
import { useAuthStore } from '@/stores/auth';


const themeStore = useTheme()
const collabStore = useCollabStore()
const collabApi = useCollabApi()
const authStore = useAuthStore()

const deleteCollabBoard = (collabBoard) => {
    collabStore.removeCollabBoard(collabBoard.id)
    console.log(authStore.getAuthData().oid , collabBoard.id)
    collabApi.deleteCollabBoard(collabBoard.id, authStore.getAuthData().oid)

}

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
                        <button class="btn" @click="deleteCollabBoard(collabBoard)">
                            Leave
                        </button>
                    </td>

                </tr>
            </tbody>

        </table>
    </div>
</template>

<style lang="scss" scoped></style>