import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

export const useLoad = defineStore('load', () => {
    const loading = ref(false)
    const displayLoading = () => {
        loading.value = true
    }
    const hideLoading = () => {
        loading.value = false
    }
    return { loading, displayLoading, hideLoading }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useLoad, import.meta.hot))
}
