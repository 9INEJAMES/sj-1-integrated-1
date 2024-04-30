<script setup>
import { getTaskById, updateTask } from '@/libs/FetchAPI'
import { ref, onMounted, defineProps, defineEmits } from 'vue'
import { useTasks } from '../stores/task.js'
import { useVariables } from '../stores/store.js'
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const myVariables = useVariables()
const isSelectTask = ref(false)
const selectedTask = ref({})
const props = defineProps({
  task: {
    type: Object,
  },
})

</script>
 
<template>
<div>

</div>
</template>
 
<style scoped>

</style>