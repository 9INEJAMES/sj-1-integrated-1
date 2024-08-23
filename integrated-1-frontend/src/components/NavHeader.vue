<script setup>
import { ref } from 'vue'
import { useTheme } from '@/stores/theme.js'
const isChecked = ref(false)
const themeStore = useTheme()
const base = import.meta.env.VITE_BASE
const auth = JSON.parse(localStorage.getItem('authData'))
const username = auth ? auth.username : null
</script>

<template>
    <div class="poetsen-one-regular justify-between pt-[3vh] flex px-[5vh]">
        <div class="flex gap-3 items-center">
            <img :src="`${base ? base : ''}/pig${themeStore.isLight ? '' : '2'}.png`" alt="pig" class="w-[50px] h-[50px] hover:animate-bounce" />
            <div class="flex-col">
                <p class="font-bold text-[4vh]" :class="themeStore.isLight ? 'text-pink-400' : 'text-cyan-500'">SJ-1</p>
                <p class="text-[3vh]" :class="themeStore.isLight ? 'text-pink-300' : 'text-cyan-300'">{{ username }}</p>
            </div>
        </div>
        <div class="flex items-center">
            <label class="flex cursor-pointer gap-2">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="5" />
                    <path d="M12 1v2M12 21v2M4.2 4.2l1.4 1.4M18.4 18.4l1.4 1.4M1 12h2M21 12h2M4.2 19.8l1.4-1.4M18.4 5.6l1.4-1.4" />
                </svg>
                <input id="theme" type="checkbox" value="synthwave" class="toggle theme-controller" @click="themeStore.changeTheme(isChecked)" v-model="isChecked" />
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
                </svg>
            </label>
        </div>
    </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Poetsen+One&display=swap');

.poetsen-one-regular {
    font-family: 'Poetsen One', sans-serif;
    font-weight: 400;
    font-style: normal;
}
</style>
