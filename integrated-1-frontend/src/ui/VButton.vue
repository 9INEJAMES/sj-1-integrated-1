<script setup>
import { useTheme } from '@/stores/theme'
const themeStore = useTheme()
const props = defineProps({
    mode: String,
    msg: String,
})
</script>

<template>
    <button class="w-fit h-[3vh] transition-all ease-i btn btn-sm sm:btn-sm md:btn-md lg:btn-lg " :class="themeStore.getButtonTheme()">{{ msg }}</button>
</template>

<style scoped></style>
