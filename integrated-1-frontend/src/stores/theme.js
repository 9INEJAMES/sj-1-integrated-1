import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

const useTheme = defineStore('theme', () => {
  const currTheme = ref('')
  const ligthTheme = 'bg-white text-black'
  const darkTheme = 'bg-slate-600 text-amber-50'

  function changeTheme(isDark) {
    isDark ? (currTheme.value = darkTheme) : (currTheme.value = ligthTheme)
  }

  function getTheme() {
    return currTheme.value
  }
  function resetTheme() {
    currTheme.value = ligthTheme
  }

  return {}
})

export { useTheme }

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useTheme, import.meta.hot))
}
