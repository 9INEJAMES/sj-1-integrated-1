<script setup>
import VButton from '@/ui/VButton.vue'
import BoardTable from '@/components/BoardTable.vue'
import CollabBoardTable from '@/components/CollabBoardTable.vue'
import { useAuthStore } from '@/stores/auth'
import { onMounted } from 'vue'
import { useBoardStore } from '@/stores/board'
import { useCollabStore } from '@/stores/collab'

const boardStore = useBoardStore()
const collabStore = useCollabStore()

const authStore = useAuthStore()

onMounted(async () => {
    if (authStore.checkToken) await boardStore.fetchBoard()
    if (authStore.checkToken) await collabStore.fetchCollabBoards()
})
</script>

<template>
    <div v-show="$route.name == 'boardView' || 'boardAdd'">
        <div class="text-center text-3xl font-bold pt-[4vh] pb-[1vh]">Personal boards</div>
        <div class="flex justify-between pl-[5vh] pr-[5vh]">
            <div class="flex gap-2">
                <RouterLink :to="{ name: 'boardAdd' }">
                    <VButton class="itbkk-button-create" msg="Create personal board" />
                </RouterLink>
            </div>
        </div>

        <div class="px-[5vh] pt-[1vh] pb-[3vh]">
            <div>
                <BoardTable />
            </div>
        </div>
        <div class="text-center text-3xl font-bold pt-[4vh] pb-[1vh]">Collab Boards</div>
        <div class="px-[5vh] pt-[1vh]">
            <CollabBoardTable />
        </div>
    </div>
    <RouterView class="z-30" />

    <!-- <StatusSetting v-if="isSettingOpen" @close="isSettingOpen = false" class="z-[45]"></StatusSetting>
    <FilterModal v-if="isFilterOpen" @close="isFilterOpen = false" class="z-40" @applyFilter="taskStore.fetchTasks"></FilterModal> -->
</template>

<style scoped></style>
