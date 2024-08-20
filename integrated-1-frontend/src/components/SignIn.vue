<script setup>
import { ref } from 'vue'
import { useTheme } from '@/stores/theme.js'
import { useAuthApi } from '@/composables/auth-api.js'
import router from '@/router';

const isChecked = ref(false)
const themeStore = useTheme()
const authApi = useAuthApi()

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
            router.push('/task') 
        }
    } catch (error) {
        console.error('Sign in error:', error)

    }
    
}


</script>

<template>
    <div>
        <div class="flex justify-center items-center mt-[25vh] col-span-2">
            <div class="w-[90vh] p-[6vh] bg-pink-300 rounded-lg shadow-lg">
                <h1 class="text-2xl font-bold text-center">Welcome To ITB-KK</h1>

                <div class="mb-4">
                    <div class="flex justify-between items-center">
                        <label for="username" class="block text-sm font-medium text-gray-700">Username</label>
                        <p v-show="loginField.userName.length > 0" class="block text-[0.75rem] font-medium text-gray-700">{{ loginField.userName.length }} /50</p>

                    </div>

                    <input
                        type="text"
                        id="text"
                        name="username"
                        v-model="loginField.userName"
                        maxlength="50"
                        class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                    />
                </div>
                <div class="mb-4">
                    <div class="flex justify-between items-center">
                        <label for="password" class="block text-sm font-medium text-gray-700">Password</label>

                        <p v-show="loginField.password.length > 0" class="block text-[0.75rem] font-medium text-gray-500">{{ loginField.password.length }} /14</p>
                    </div>
                    <div class="relative">
                        <input
                            :type="isPasswordVisible ? 'text' : 'password'"
                            id="password"
                            name="password"
                            v-model="loginField.password"
                            maxlength="14"
                            class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm pr-10"
                        />
                        <img
                            :src="isPasswordVisible ? '../../public/eye_off_icon.png' : '../../public/eye_icon.png'"
                            @click="togglePasswordVisibility"
                            class="absolute inset-y-2 right-3 ml-2 h-5 w-5 cursor-pointercursor-pointer"
                        />
                    </div>
                </div>
                <button :disabled="loginField.userName.length <= 0 || loginField.password.length <= 0" class="itbkk-button-signin btn btn-error btn-xs sm:btn-sm md:btn-md lg:btn-lg w-full" 
                @click="submitSignIn()">
                    Sign In
                </button>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped></style>
