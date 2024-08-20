import { defineStore, acceptHMRUpdate } from "pinia"
import { ref, computed } from "vue"

export const useAuthStore = defineStore("auth", () => {
    const user = ref([])

    return {  }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useAuthStore, import.meta.hot))
}
