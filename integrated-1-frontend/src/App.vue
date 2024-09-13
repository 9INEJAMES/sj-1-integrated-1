<script setup>

import NavHeader from './components/NavHeader.vue'
import { useTheme } from '@/stores/theme.js'
import VToast from '@/ui/VToast.vue'


const themeStore = useTheme()


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

