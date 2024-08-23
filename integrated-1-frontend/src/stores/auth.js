import { defineStore, acceptHMRUpdate } from "pinia"
import { ref, computed } from "vue"

export const useAuthStore = defineStore("auth", () => {
    const user = ref({
        value: {
            role: "",
            name: "",
            oid: "",
            email: "",
        },
    })
    
    const role = computed(() => user.value.value.role || "")
    const name = computed(() => user.value.value.name || "")
    const oid = computed(() => user.value.value.oid || "")
    const email = computed(() => user.value.value.email || "")

    // Return the store properties and methods
    return { user, role, name, oid, email }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useAuthStore, import.meta.hot))
}
