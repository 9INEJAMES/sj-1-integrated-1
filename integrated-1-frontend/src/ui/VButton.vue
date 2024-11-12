<script setup>
import { useTheme } from '@/stores/theme'
const themeStore = useTheme()
const props = defineProps({
    mode: String,
    msg: String,
    iconurl: String,
})
</script>

<template>
    <button class="w-fit h-[3vh] transition-all ease-i btn btn-md" :class="themeStore.getButtonTheme()">
        {{ msg }} <img v-if="iconurl" :src="iconurl" alt="icon" class="w-8 h-8" />
    </button>
</template>

<style scoped></style>
