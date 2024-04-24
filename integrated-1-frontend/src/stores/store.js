import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

const useVariables = defineStore('variables', () => {
    const isSelectTask = ref(false)
    return {
        isSelectTask,
    }
    
})

export { useVariables }

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useVariables, import.meta.hot))
}