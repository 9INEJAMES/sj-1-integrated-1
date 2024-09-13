<script setup>
import { onMounted } from 'vue'

import { useTheme } from '@/stores/theme.js'
import { useBoardStore } from '@/stores/board'
import { useStatusesStore } from '@/stores/status'
import { useTasksStore } from '@/stores/task'

const taskStore = useTasksStore()
const statusStore = useStatusesStore()
const boardStore = useBoardStore()
onMounted(async () => {
    // if (taskStore.tasks.length <= 0) await taskStore.fetchTasks()
    // if (statusStore.statuses.length <= 0) await statusStore.fetchStatuses()
    if (boardStore.boards.length <= 0) await boardStore.fetchBoard()
})
</script>

<template>
    <div>
        <div class="h-screen text-xl">Hello, Welcome</div>
    </div>
</template>

<style lang="scss" scoped></style>
