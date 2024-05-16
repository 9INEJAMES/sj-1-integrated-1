<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStatusesStore } from '../stores/status.js'
import VButton from '@/ui/VButton.vue'
import { useRouter } from 'vue-router'

const statusesStore = useStatusesStore()
const selectedStatuses = ref([])
const router = useRouter()

const props = defineProps({
    isVisible: Boolean
})

const emits = defineEmits(['close'], ['applyFilter'])

const closeModal = () => {
    emits('close')
}

const applyFilter = () => {
    emits('applyFilter')
    closeModal()
}

const toggleStatus = (statusName) => {
    if (selectedStatuses.value.includes(statusName)) {
        selectedStatuses.value = selectedStatuses.value.filter(name => name !== statusName)
    } else {
        selectedStatuses.value.push(statusName)
    }
    updateRouteParameters()
}

const updateRouteParameters = () => {
    const selectedStatusesQuery = selectedStatuses.value.join(',')
    router.push({ query: { statuses: selectedStatusesQuery } })
    if (selectedStatuses.value.length === 0) {
        router.replace({ query: {} })
    }
}

const selectedStatusNames = computed(() => {
    return statusesStore.statuses.filter(status => selectedStatuses.value.includes(status.name));
})

const clearFilter = () => {
    selectedStatuses.value = []
    router.replace({ query: {} })
}


onMounted(() => {
    const params = router.currentRoute.value.query
    if (params.statuses) {
        const statusNames = params.statuses.split(',')
        statusNames.forEach(name => {
            if (!selectedStatuses.value.includes(name)) {
                selectedStatuses.value.push(name)
            }
        })
    }
})
</script>

<template>
    <div v-if="isVisible" class="py-[27vh] px-[10vh] fixed inset-0 flex justify-center bg-black bg-opacity-50  z-50 w-full">
        <div class="bg-white p-[2vh] rounded-lg w-full h-full">
            <div class="flex justify-between items-center mb-4">
                <p class="text-lg font-semibold">Filter Tasks</p>
                <VButton @click="clearFilter()" msg="Clear filters" class="itbkk-filter" />
            </div>
            <div>
                <div class="grid grid-cols-2 gap-3">
                    <div class="col-span-1">
                        <label for="status">Status</label>
                        <div class="mt-2 overflow-auto max-h-[20vh] border p-[1vh] rounded-lg">
                            <div v-for="status in statusesStore.statuses" :key="status.id">
                                <input type="checkbox" :id="status.id" :value="status.name"
                                    class="mr-[1vh] checkbox checkbox-success" @change="toggleStatus(status.name)"
                                    :checked="selectedStatuses.includes(status.name)">
                                <label :for="status.id">{{ status.name }}</label>
                            </div>
                        </div>
                    </div>
                    <div class="col-span-1">
                        <label for="filter">Filter</label>
                        <div class="mt-2 border w-full h-[20vh] p-[1vh] rounded-lg flex-wrap">
                            <span v-for="status in selectedStatusNames" :key="status.id" class="px-2 py-1 rounded-lg mr-2"
                                :style="{ backgroundColor: status.color }">
                                {{ status.name }}
                            </span>
                        </div>
                    </div>
                </div>
                <div class="flex justify-end gap-2 mt-[1vh]">
                    <button class="itbkk-button-confirm btn btn-success btn-xs sm:btn-sm md:btn-md lg:btn-lg"
                        @click="applyFilter()">Filter</button>
                    <button class="itbkk-button-cancel btn btn-error btn-xs sm:btn-sm md:btn-md lg:btn-lg"
                        @click="closeModal()">Cancel</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
