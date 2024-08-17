<script setup>
import { ref } from 'vue'
import { useTheme } from '@/stores/theme.js'
const isChecked = ref(false)
const themeStore = useTheme()
const base = import.meta.env.VITE_BASE

</script>

<template>
    <div>
        <div class="flex justify-center items-center h-screen">
            <div class="w-[90vh] p-[6vh] bg-pink-300 rounded-lg shadow-lg " >
                <h1 class="text-2xl font-bold text-center">Sign In</h1>
                <form class="mt-4">
                    <div class="mb-4">
                        <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
                        <input type="email" id="email" name="email" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
                    </div>
                    <div class="mb-4">
                        <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
                        <input type="password" id="password" name="password" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" />
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>



<style lang="scss" scoped>

</style>