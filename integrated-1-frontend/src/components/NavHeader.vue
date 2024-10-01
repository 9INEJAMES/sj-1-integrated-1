<script setup>
import { ref, onMounted, watch } from 'vue'
import { useTheme } from '@/stores/theme.js'
import { useAuthStore } from '@/stores/auth.js'
import { useTasksStore } from '@/stores/task'
import { useStatusesStore } from '@/stores/status'
import { useRouter, useRoute } from 'vue-router'
import { useBoardStore } from '@/stores/board'
import ConfirmDelete from './ConfirmDelete.vue'
import { useBoardApi } from '@/composables/board-api'

const isChecked = ref(false)
const themeStore = useTheme()
const authStore = useAuthStore()
const base = import.meta.env.VITE_BASE
const taskStore = useTasksStore()
const status = useStatusesStore()
const router = useRouter()
const route = useRoute()
const boardStore = useBoardStore()
const boardApi = useBoardApi()
const bName = ref('default')

const goBackHome = () => {
    if (!!authStore.checkToken()) {
        router.push({ name: 'boardView' })
        taskStore.resetTasks()
        status.resetStatuses()
    } else {
        logout()
    }
}
const checkPage = (pages = []) => {
    const excludedRoutes = ['login', 'homepage', 'boardView', 'boardAdd', 'boardEdit', 'boardDelete', ...pages]
    return !excludedRoutes.includes(route.name)
}
const updateBoardName = async () => {
    const bid = route.params.bid
    if (bid) {
        const board = await boardStore.getCurrentBoard()
        bName.value = board ? board.name : null
    } else {
        bName.value = null
    }
}

watch(() => route.params.bid, updateBoardName)

updateBoardName()

const logout = () => {
    authStore.logout()
    !authStore.checkToken()
    router.push({ name: 'login' })
}
const deleteModal = ref(false)
const deleteBoard = () => {
    deleteModal.value = true
}
const handleDeleteModal = (confirmed) => {
    if (confirmed) {
        boardStore.removeBoard(route.params.bid)
        router.push({ name: 'boardView' })
    }
    deleteModal.value = false
}
</script>
<template>
    <ConfirmDelete v-if="deleteModal" mode="board" :object="boardStore.findBoard(route.params.bid)" :number="1" @closeModal="handleDeleteModal" />
    <div class="poetsen-one-regular justify-between pt-[3vh] flex px-[5vh] items-center">
        <div class="flex gap-5 items-center">
            <img
                v-if="checkPage()"
                @click="goBackHome"
                :src="`${base ? base : ''}/pHome${themeStore.isLight ? '1' : '2'}.png`"
                alt="pig"
                class="itbkk-home w-[50px] h-[50px] hover:cursor-pointer transition-transform duration-300 transform hover:scale-105"
            />

            <img
                v-else
                :src="`${base ? base : ''}/pig${themeStore.isLight ? '1' : '2'}.png`"
                alt="pig"
                class="w-[50px] h-[50px] hover:animate-bounce transition-transform duration-300 transform hover:scale-105"
            />
            <div class="flex flex-col">
                <p
                    class="itbkk-board-name font-bold text-[4vh] leading-none itbkk-boardname"
                    :class="[themeStore.isLight ? 'text-pink-400' : 'text-cyan-500', bName?.length > 50 ? 'text-xs' : 'text-xl']"
                >
                    {{ checkPage(['accessDenied', 'notFound']) ? bName : 'SJ-1' }}
                </p>
                <div class="flex flex-row" v-if="route.name != 'login'">
                    <p class="text-[3vh] itbkk-fullname" :class="themeStore.isLight ? 'text-pink-300' : 'text-cyan-300'" v-if="$route.name !== 'login'">
                        {{ authStore.getAuthData() ? authStore.getAuthData().name : 'Guest' }}
                    </p>
                    <img
                        v-show="authStore.isLogin"
                        :src="`${base ? base : ''}/logout.png`"
                        alt="pig"
                        class="itbkk-sign-out w-[24px] h-[24px] transition-transform duration-300 transform hover:scale-105 flex self-center ml-1 hover:cursor-pointer"
                        @click="logout"
                    />
                </div>
            </div>
        </div>
        <div class="flex flex-row items-center align-middle gap-8">
            <div class="flex justify-center items-center h-full">
                <img
                    v-if="checkPage(['accessDenied', 'notFound'])"
                    @click="deleteBoard()"
                    :src="`${base ? base : ''}/delete${themeStore.isLight ? '' : '2'}.png`"
                    class="w-[20px] h-[20px] hover:cursor-pointer"
                    alt="list img"
                />
            </div>

            <!-- <div class="flex items-center">
                <label class="flex cursor-pointer items-center gap-3 p-2 bg-slate-500 bg-opacity-10 rounded-lg shadow">
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="20"
                        height="20"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        :class="themeStore.getTheme"
                    >
                        <circle cx="12" cy="12" r="5" />
                        <path d="M12 1v2M12 21v2M4.2 4.2l1.4 1.4M18.4 18.4l1.4 1.4M1 12h2M21 12h2M4.2 19.8l1.4-1.4M18.4 5.6l1.4-1.4" />
                    </svg>

                    <input id="theme" type="checkbox" value="synthwave" class="toggle theme-controller hidden" @click="themeStore.changeTheme(isChecked)" v-model="isChecked" />
                    <div class="relative inline-block w-12 h-6 transition duration-200 ease-in bg-gray-300 rounded-full">
                        <span class="absolute left-0 w-6 h-6 transition-transform duration-200 ease-in bg-white rounded-full transform" :class="{ 'translate-x-full': isChecked }"></span>
                    </div>

                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="20"
                        height="20"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        :class="themeStore.getTheme"
                    >
                        <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
                    </svg>
                </label>
            </div> -->
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
