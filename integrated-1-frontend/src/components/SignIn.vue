<script setup>
import { ref, onMounted } from 'vue'
import { useTheme } from '@/stores/theme.js'
import { useAuthApi } from '@/composables/auth-api.js'
import router from '@/router'
import { useAuthStore } from '@/stores/auth'
import { useRoute } from 'vue-router'
import { msalInstance, state } from '@/configs/msalConfig'

const route = useRoute()
const themeStore = useTheme()
const authApi = useAuthApi()
const authStore = useAuthStore()
const isLoading = ref(false)

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
            const redirectTo = route.query.redirectTo
            if (redirectTo) {
                router.push(redirectTo)
                return
            }

            router.push({ name: 'boardView' })
        }
    } catch (error) {
        console.error('Sign in error:', error)
    }
}

const signInWithAzure = async () => {
    if (isLoading.value) {
        console.log('Authentication already in progress')
        return
    }

    try {
        isLoading.value = true
        console.log('Starting Azure sign in...')
        
        // Check if we already have an account
        const accounts = msalInstance.getAllAccounts()
        if (accounts.length > 0) {
            console.log('Account already exists, loading data...')
            await authStore.loadAzureData()
            router.push({ name: 'boardView' })
            return
        }
        
        // No account, start new login
        await authStore.azureLogin()
    } catch (error) {
        console.error('Azure sign in error:', error)
        localStorage.removeItem('msal.interaction.status')
    } finally {
        isLoading.value = false
    }
}

const initialize = async () => {
    if (isLoading.value) {
        console.log('Initialization already in progress')
        return
    }

    try {
        isLoading.value = true
        console.log('Initializing MSAL...')
        
        await msalInstance.initialize()
        console.log('MSAL initialized successfully')
    
        const response = await msalInstance.handleRedirectPromise()
        console.log('Redirect response:', response)
        
        if (response) {
            console.log('Processing redirect response...')
            await authStore.azureHandleRedirect()
            router.push({ name: 'boardView' })
        } else {
            const accounts = msalInstance.getAllAccounts()
            if (accounts.length > 0) {
                console.log('Found existing account, loading data...')
                await authStore.loadAzureData()
                router.push({ name: 'boardView' })
            } else {
                console.log('No existing account found')
                // Clear any stale state since we're starting fresh
                localStorage.removeItem('msal.interaction.status')
            }
        }
    } catch (error) {
        console.error('MSAL initialization error:', error)
        localStorage.removeItem('msal.interaction.status')
    } finally {
        isLoading.value = false
    }
}

onMounted(async () => {
    authStore.isLogin = false
    await initialize()
})
</script>

<template>
    <div class="min-h-screen max-h-full flex items-center justify-center" :class="themeStore.getBackgroundTheme()">
        <div class="flex w-[80vw] h-[80vh] bg-white rounded-3xl shadow-lg overflow-hidden">
            <div class="w-1/2 bg-pink-300 p-10 flex flex-col justify-center text-white" :class="themeStore.getModalTheme()">
                <h1 class="text-4xl font-bold mb-6">Welcome To ITB-KK</h1>
                <p class="text-lg leading-relaxed">
                    This website is a Kanban board platform designed to empower you to easily visualize your tasks, manage work-in-progress, and enhance workflow efficiency.
                </p>
            </div>

            <div class="w-1/2 p-10 flex flex-col justify-center">
                <h2 class="text-3xl font-bold text-center mb-8 text-black">SIGN IN</h2>

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

                <button
                    :disabled="loginField.userName.length <= 0 || loginField.password.length <= 0"
                    :class="loginField.userName.length <= 0 || loginField.password.length <= 0 ? 'opacity-50 cursor-not-allowed' : themeStore.getButtonTheme()"
                    class="mb-2 itbkk-button-signin w-full font-bold py-4 rounded-full shadow-lg transition duration-200 disabled:bg-slate-300 text-black hover:text-white"
                    @click="submitSignIn"
                >
                    LOGIN
                </button>
                <button
                    :class="themeStore.getButtonTheme()"
                    class="itbkk-button-signin w-full flex items-center justify-center font-bold py-4 rounded-full shadow-lg transition duration-200 text-black hover:text-white"
                    @click="signInWithAzure"
                >
                    <svg class="mr-2" width="16" height="16" viewBox="0 0 256 256" xmlns="http://www.w3.org/2000/svg">
                        <path fill="#F1511B" d="M121.666 121.666H0V0h121.666z" />
                        <path fill="#80CC28" d="M256 121.666H134.335V0H256z" />
                        <path fill="#00ADEF" d="M121.663 256.002H0V134.336h121.663z" />
                        <path fill="#FBBC09" d="M256 256.002H134.335V134.336H256z" />
                    </svg>
                    MICROSOFT LOGIN
                </button>
                {{ authStore.isLogin }}
                <button @click="signout">sign out</button>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped></style>
