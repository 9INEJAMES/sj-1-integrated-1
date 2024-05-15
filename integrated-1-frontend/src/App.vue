<script setup>
import { ref, onMounted, watch } from 'vue'
import NavHeader from './components/NavHeader.vue'
import { useTheme } from '@/stores/theme.js'
import VToast from '@/ui/VToast.vue'
import { useToast } from '@/stores/toast.js'
import { useTasksStore } from '@/stores/task.js'
import { useStatusesStore } from '@/stores/status'
import { useLimitStore } from '@/stores/limitTask'

const themeStore = useTheme()
const myToast = useToast()
const taskStore = useTasksStore()
const statusStore = useStatusesStore()
const limitStore = useLimitStore()
onMounted(async () => {
    if (taskStore.tasks.length <= 0) await taskStore.fetchTasks()
    if (statusStore.statuses.length <= 0) await statusStore.fetchStatuses()
    if (limitStore.limitTask.length <= 0) await limitStore.fetchLimit()
})
</script>

<template>
    <VToast class="z-50" />
    <div class="roboto-light min-h-screen max-h-fit pb-24" :class="themeStore.getTheme()">
        <NavHeader />
        <hr class="mt-[3vh] mx-[3vh] shadow-lg bg-gray-100 rounded" />
        <div>
            <RouterView />
        </div>
    </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&display=swap');

.roboto-light {
    font-family: 'Roboto', sans-serif;
    font-weight: 300;
    font-style: normal;
}
</style>
@/stores/limitTask
