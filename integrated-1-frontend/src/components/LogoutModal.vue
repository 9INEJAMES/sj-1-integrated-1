<script setup>
import { useTheme } from '@/stores/theme'
const themeStore = useTheme()

const emit = defineEmits(['closeModal'])
const confirmLogout = () => {
    emit('closeModal', true)
}
const cancelLogout = () => {
    emit('closeModal', false)
}
</script>

<template>
    <div class="py-[30vh] px-[10vh] fixed inset-0 flex justify-center items-center bg-black bg-opacity-35 w-full z-40">
        <div class="flex-col border rounded-md p-[2vh] w-1/4 h-fit" :class="themeStore.getTheme()">
            <p class="font-bold text-[4vh] py-[2vh]" :class="themeStore.getTextHeaderTheme()">Confirm logout</p>
            <hr />
            <p class="itbkk-message py-[3vh]">Are you sure you want to log out ?</p>

            <div class="flex gap-[2vh] justify-end py-[2vh]">
                <button @click="cancelLogout" class="itbkk-button-cancel btn btn-error text-white rounded-md p-2">Cancel</button>
                <button @click="confirmLogout" class="itbkk-button-confirm btn btn-success text-white rounded-md p-2">Confirm</button>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped></style>
