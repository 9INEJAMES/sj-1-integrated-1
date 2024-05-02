import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

export const useTheme = defineStore('theme', () => {
    const currTheme = ref('bg-white text-black')
    const alterTheme = ref('bg-slate-600 text-amber-50')
    const ligthTheme = 'bg-white text-black'
    const darkTheme = 'bg-slate-600 text-amber-50'
    const tableTheme = ref('bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500')

    function changeTheme(isLight) {
        !isLight ? ((currTheme.value = darkTheme), (alterTheme.value = ligthTheme), (tableTheme.value = 'bg-gradient-to-r from-amber-500 via-yellow-500 to-lime-500')) : ((currTheme.value = ligthTheme), (alterTheme.value = darkTheme), (tableTheme.value = 'bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500'))
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
    function getTableTheme() {
        return tableTheme.value
    }

    return { changeTheme, getTheme, resetTheme, getAlterTheme, getTableTheme }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useTheme, import.meta.hot))
}
