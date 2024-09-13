<script setup>
import { ref } from 'vue'
import VButton from '@/ui/VButton.vue'
import BoardTable from '@/components/BoardTable.vue'
import { useTheme } from '@/stores/theme'
import { useAuthStore } from '@/stores/auth'
import { onMounted } from 'vue'

const base = import.meta.env.VITE_BASE
const isSelectTask = ref(false)
const themeStore = useTheme()

const authStore = useAuthStore()
</script>

<template>
    <div class="flex justify-between pt-[5vh] pl-[5vh] pr-[5vh]">
        <div class="flex gap-2">
            <RouterLink :to="{ name: 'boardAdd' }">
                <VButton class="itbkk-button-create" msg="Create personal board" />
            </RouterLink>
        </div>
    </div>

    <RouterView class="z-30" />
    <div class="px-[5vh] pt-[1vh]">
        <div class="">
            <BoardTable />
        </div>
    </div>
    <!-- <StatusSetting v-if="isSettingOpen" @close="isSettingOpen = false" class="z-[45]"></StatusSetting>
    <FilterModal v-if="isFilterOpen" @close="isFilterOpen = false" class="z-40" @applyFilter="taskStore.fetchTasks"></FilterModal> -->
</template>

<style scoped></style>
