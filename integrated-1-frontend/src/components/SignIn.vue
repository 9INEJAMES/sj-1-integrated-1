<script setup>
import { ref } from 'vue'
import { useTheme } from '@/stores/theme.js'
import { useAuthApi } from '@/composables/auth-api.js'
import router from '@/router'
import { useBoardStore } from '@/stores/board'
import { useStatusesStore } from '@/stores/status'
import { useTasksStore } from '@/stores/task'

const isChecked = ref(false)
const themeStore = useTheme()
const authApi = useAuthApi()
const taskStore = useTasksStore()
const statusStore = useStatusesStore()
const boardStore = useBoardStore()

const isPasswordVisible = ref(false)
const base = import.meta.env.VITE_BASE
const loginField = ref({
    userName: '',
    password: '',
})

function togglePasswordVisibility() {
    isPasswordVisible.value = !isPasswordVisible.value
}

const submitSignIn = async () => {
    try {
        const token = await authApi.signIn(loginField.value)
        if (token) {
            router.push('/board')
        }
    } catch (error) {
        console.error('Sign in error:', error)
    }
}
</script>

<template>
    <div class="min-h-screen  max-h-full flex items-center justify-center  bg-pink-100" :class="themeStore.getBackgroundTheme()">
        <div class="flex w-[80vw] h-[80vh] bg-white rounded-3xl shadow-lg overflow-hidden">
            <!-- Left Side - Information Section -->
            <div class="w-1/2 bg-pink-300 p-10 flex flex-col justify-center text-white" :class="themeStore.getModalTheme()">
                <h1 class="text-4xl font-bold mb-6">Welcome To ITB-KK</h1>
                <p class="text-lg leading-relaxed">This website is a Kanban board platform designed to empower you to easily visualize your tasks, manage work-in-progress, and enhance workflow efficiency.</p>
            </div>

            <!-- Right Side - Sign In Form -->
            <div class="w-1/2 p-10 flex flex-col justify-center">
                <h2 class="text-3xl font-bold text-center mb-8 text-black">SIGN IN</h2>

                <!-- Username Input with Icon -->
                <div class="mb-6 relative">
                    <div class="relative">
                        <i class="fa fa-user-circle absolute inset-y-0 left-4 flex items-center text-gray-500"></i>
                        <input
                            type="text"
                            id="username"
                            name="username"
                            v-model="loginField.userName"
                            maxlength="50"
                            placeholder="Username"
                            :class="themeStore.getTheme()"
                            class="itbkk-username w-full pl-12 py-4 border border-gray-300 rounded-full shadow-sm focus:ring-2 focus:ring-pink-500 focus:border-pink-500"
                        />
                        <p
                            v-show="loginField.userName.length > 0"
                            :class="themeStore.getTextTheme()"
                            class="absolute right-12 top-1/2 transform -translate-y-1/2 text-[0.75rem] font-medium text-gray-400"
                        >
                            {{ loginField.userName.length }} /50
                        </p>
                    </div>
                </div>

                <!-- Password Input with Icon -->
                <div class="mb-6">
                    <div class="relative">
                        <i class="fa fa-lock absolute inset-y-0 left-4 flex items-center text-gray-500"></i>
                        <input
                            :type="isPasswordVisible ? 'text' : 'password'"
                            id="password"
                            name="password"
                            v-model="loginField.password"
                            maxlength="14"
                            placeholder="Password"
                            :class="themeStore.getTheme()"
                            class="itbkk-password w-full pl-12 py-4 border border-gray-300 rounded-full shadow-sm focus:ring-2 focus:ring-pink-500 focus:border-pink-500 pr-10"
                        />
                        <img
                            :src="`${base ? base : ''}${isPasswordVisible ? '/eye_off_icon.png' : '/eye_icon.png'}`"
                            @click="togglePasswordVisibility"
                            class="absolute mt-2 inset-y-2 right-4 ml-3 h-6 w-6 cursor-pointercursor-pointer"
                        />
                        <i class="absolute inset-y-0 right-4 mt-4 cursor-pointer" @click="togglePasswordVisibility"></i>
                        <p
                            v-show="loginField.password.length > 0"
                            :class="themeStore.getTextTheme()"
                            class="absolute right-12 top-1/2 transform -translate-y-1/2 text-[0.75rem] font-medium text-gray-400"
                        >
                            {{ loginField.password.length }} /14
                        </p>
                    </div>
                </div>

                <!-- Sign In Button -->
                <button
                    :disabled="loginField.userName.length <= 0 || loginField.password.length <= 0"
                    :class="loginField.userName.length <= 0 || loginField.password.length <= 0 ? 'opacity-50 cursor-not-allowed' : themeStore.getButtonTheme()"
                    class="itbkk-button-signin w-full text-white font-bold py-4 rounded-full shadow-lg transition duration-200 disabled:bg-slate-300"
                    @click="submitSignIn"
                >
                    LOGIN
                </button>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped></style>
