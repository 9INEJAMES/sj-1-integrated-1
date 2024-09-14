import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

export const useTheme = defineStore('theme', () => {
    const isLight = ref(true)

    function changeTheme(tf) {
        isLight.value = tf
    }

    function getTheme() {
        return isLight.value ? 'bg-white text-black' : 'bg-slate-600 text-amber-50'
    }
    function getModalTheme() {
        return isLight.value ? 'bg-pink-300   text-black' : 'bg-slate-500 text-amber-50'
    }
    function getBackgroundTheme() {
        return isLight.value ? 'bg-pink-100' : 'bg-slate-300'
    }

    function getAlterTheme() {
        return isLight.value ? 'bg-slate-600 text-amber-50' : 'bg-white text-black'
    }
    function getTableTheme() {
        return isLight.value ? 'bg-pink-300 text-black' : 'bg-cyan-400 text-white'
    }
    function getTextTheme() {
        return isLight.value ? 'text-black' : 'text-white'
    }
    function getButtonTheme() {
        return isLight.value ? 'bg-white text-black bg-pink-500 hover:bg-pink-600' : 'bg-black text-white hover:text-cyan-400 hover:bg-slate-800'
    }
    function getTextHeaderTheme() {
        return isLight.value ? 'font-semibold text-pink-400 disabled:text-pink-400' : 'font-semibold text-cyan-400 disabled:text-cyan-400'
    }
    return {
        isLight,
        changeTheme,
        getTheme,
        getModalTheme,
        getBackgroundTheme,
        getAlterTheme,
        getTableTheme,
        getTextTheme,
        getButtonTheme,
        getTextHeaderTheme,
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useTheme, import.meta.hot))
}
