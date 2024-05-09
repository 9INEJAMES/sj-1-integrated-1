<script setup>
const props = defineProps({
    mode: String,
    msg: String,
})
</script>

<template>
    <button class="w-fit h-[3vh] transition-all ease-in hover:cursor-pointer btn btn-sm sm:btn-sm md:btn-md lg:btn-lg">{{ msg }}</button>
</template>

<style scoped></style>
