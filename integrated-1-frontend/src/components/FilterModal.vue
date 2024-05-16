<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStatusesStore } from '../stores/status.js'
import VButton from '@/ui/VButton.vue'
import { useRouter } from 'vue-router'

const statusesStore = useStatusesStore()
const selectedStatuses = ref([])
const router = useRouter()

const emits = defineEmits(['close', 'applyFilter'])

const closeModal = () => {
    emits('close')
}

const applyFilter = () => {
    emits('applyFilter', selectedStatuses.value)
    closeModal()
}

const toggleStatus = (statusName) => {
    if (selectedStatuses.value.includes(statusName)) {
        selectedStatuses.value = selectedStatuses.value.filter((name) => name !== statusName)
    } else {
        selectedStatuses.value.push(statusName)
    }
}

const selectedStatusNames = computed(() => {
    return statusesStore.statuses.filter((status) => selectedStatuses.value.includes(status.name))
})

const clearFilter = () => {
    selectedStatuses.value = []
}

onMounted(() => {
    const params = router.currentRoute.value.query
    if (params.statuses) {
        const statusNames = params.statuses.split(',')
        statusNames.forEach((name) => {
            if (!selectedStatuses.value.includes(name)) {
                selectedStatuses.value.push(name)
            }
        })
    }
})
</script>

<template>
    <div class="py-[25vh] px-[10vh] fixed inset-0 flex justify-center bg-black bg-opacity-50 z-50 w-full">
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
                                <input
                                    type="checkbox"
                                    :id="status.id"
                                    :value="status.name"
                                    class="mr-[1vh] checkbox checkbox-success"
                                    @change="toggleStatus(status.name)"
                                    :checked="selectedStatuses.includes(status.name)"
                                />
                                <label :for="status.id">{{ status.name }}</label>
                            </div>
                        </div>
                    </div>
                    <div class="col-span-1">
                        <label for="filter">Filter</label>
                        <div class="mt-2 border w-full h-[20vh] p-[1vh] rounded-lg flex-wrap">
                            <span v-for="status in selectedStatusNames" :key="status.id" class="px-2 py-1 rounded-lg mr-2" :style="{ backgroundColor: status.color }">
                                {{ status.name }}
                            </span>
                        </div>
                    </div>
                </div>
                <div class="flex justify-end gap-2 mt-[1vh]">
                    <button class="itbkk-button-confirm btn btn-success btn-xs sm:btn-sm md:btn-md " @click="applyFilter()">Filter</button>
                    <button class="itbkk-button-cancel btn btn-error btn-xs sm:btn-sm md:btn-md " @click="closeModal()">Cancel</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
