import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

const useChangeBg = defineStore('tasks', () => {
    const tasks = ref(false)

    function changeBg() {
        tasks.value = !tasks.value
    }
    function resetBg() {
        tasks.value = false
    }
    function setBg() {
        tasks.value = true
    }
    function getBg() {
        return tasks.value
    }
    return { changeBg, resetBg, setBg, getBg }
})

export { useChangeBg }

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useChangeBg, import.meta.hot))
}