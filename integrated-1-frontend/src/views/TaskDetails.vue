<!-- ParentComponent.vue -->
<script setup>
import { useRoute } from 'vue-router';
import { useTasks } from '@/stores/task';
const route = useRoute();
const taskId = route.params.id;
const tasks = useTasks();
const task = tasks.getTaskById(taskId);

console.log(taskId)
console.log(tasks.getTasks());
console.log(task)
</script>

<template>
    <div class="px-[5vh] pt-[5vh]">  
        <div class="flex-col justify-start gap-[2vh] rounded-md border-none shadow-lg p-[2vh]">
            <p class="font-bold">{{ task.title }}</p>
            <p>{{ task.description }}</p>
            <p>{{ task.assignees }}</p>
            <p>{{ task.status }}</p>
            <p>{{ task.createdOn }}</p>
            <p>{{ task.updatedOn }}</p>
        </div>

    </div>
</template>



<style lang="scss" scoped></style>
