<script setup>
import { useRoute, useRouter } from 'vue-router'
import { onMounted, ref, watch } from 'vue'
import { useTheme } from '@/stores/theme.js'

const myTheme = useTheme()
const route = useRoute()
const router = useRouter()
const isChanged = ref(false)
</script>

<template>
  <div>
    
  </div>
</template>

<style scoped></style>
