import { defineStore, acceptHMRUpdate } from "pinia"
import { ref, computed } from "vue"
import { useStatusesStore } from "@/stores/status.js"

export const useFilterStore = defineStore("filter", () => {
    const statusesStore = useStatusesStore()
    const selectedStatuses = ref([])

    const toggleStatus = (statusName) => {
        if (selectedStatuses.value.includes(statusName)) {
            selectedStatuses.value = selectedStatuses.value.filter((name) => name !== statusName)
        } else {
            selectedStatuses.value.push(statusName)
        }
        console.log(selectedStatuses.value)
    }

    const selectedStatusNames = computed(() => {
        return statusesStore.statuses.filter((status) => selectedStatuses.value.includes(status.name))
    })

    const clearFilter = () => {
        selectedStatuses.value = []
    }

    const removeStatus = (statusName) => {
        selectedStatuses.value = selectedStatuses.value.filter((name) => name !== statusName)
    }

    return { selectedStatuses,selectedStatusNames, toggleStatus,  clearFilter , removeStatus }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useFilterStore, import.meta.hot))
}
