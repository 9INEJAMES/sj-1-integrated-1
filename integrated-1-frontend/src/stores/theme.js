import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

export const useTheme = defineStore('theme', () => {
    const currTheme = ref('bg-white text-black')
    const alterTheme = ref('bg-slate-600 text-amber-50')
    const ligthTheme = 'bg-white text-black'
    const darkTheme = 'bg-slate-600 text-amber-50'

    function changeTheme(isLight) {
        if (isLight) {
            currTheme.value = ligthTheme
            alterTheme.value = darkTheme
        } else {
            currTheme.value = darkTheme
            alterTheme.value = ligthTheme
        }
    }

    function getTheme() {
        return currTheme.value
    }
    function resetTheme() {
        currTheme.value = ligthTheme
        alterTheme.value = darkTheme
    }
    function getAlterTheme() {
        return alterTheme.value
    }

    return { changeTheme, getTheme, resetTheme, getAlterTheme }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useTheme, import.meta.hot))
}
