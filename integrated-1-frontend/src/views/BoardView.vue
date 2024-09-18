<script setup>
import { ref } from 'vue'
import VButton from '@/ui/VButton.vue'
import BoardTable from '@/components/BoardTable.vue'
import { useTheme } from '@/stores/theme'
import { useAuthStore } from '@/stores/auth'
import { onMounted } from 'vue'
import { useBoardStore } from '@/stores/board'
import { useRouter, useRoute } from 'vue-router'
const boardStore = useBoardStore()
const base = import.meta.env.VITE_BASE
const isSelectTask = ref(false)
const themeStore = useTheme()
const router = useRouter()

const authStore = useAuthStore()

onMounted(async () => {
    authStore.checkToken()
    if (boardStore.boards.length === 0) await boardStore.fetchBoard()
    // onlyone board
    // if (boardStore.boards.length === 1) {
    //     router.push({ name: 'taskView', params: { bid: boardStore.boards[0].id } })
    // }
})
</script>

<template>
    <div v-show="$route.name == 'boardView' || 'boardAdd'">
        <div class="flex justify-between pt-[5vh] pl-[5vh] pr-[5vh]">
            <div class="flex gap-2">
                <RouterLink :to="{ name: 'boardAdd' }">
                    <VButton class="itbkk-button-create" msg="Create personal board" />
                </RouterLink>
            </div>
        </div>

        <div class="px-[5vh] pt-[1vh]">
            <div class="">
                <BoardTable />
            </div>
        </div>
    </div>
    <RouterView class="z-30" />

    <!-- <StatusSetting v-if="isSettingOpen" @close="isSettingOpen = false" class="z-[45]"></StatusSetting>
    <FilterModal v-if="isFilterOpen" @close="isFilterOpen = false" class="z-40" @applyFilter="taskStore.fetchTasks"></FilterModal> -->
</template>

<style scoped></style>
