<script setup>
import { useStatusesStore } from '../stores/status.js'
import VButton from '@/ui/VButton.vue'
import { useFilterStore } from '../stores/filter.js'
import { useTheme } from '@/stores/theme'

const themeStore = useTheme()
const statusesStore = useStatusesStore()
const filterStore = useFilterStore()

const emits = defineEmits(['close', 'applyFilter'])

const closeModal = () => {
    filterStore.selectedStatuses = [...filterStore.pastStatuses]
    emits('close')
}

const applyFilter = () => {
    emits('applyFilter', filterStore.selectedStatuses)
    filterStore.pastStatuses = [...filterStore.selectedStatuses]
    closeModal()
}
</script>

<template>
    <div class="fixed inset-0 flex justify-center items-center bg-black bg-opacity-50 z-40 w-full">
        <div class="my-[25vh] mx-[10vh] p-[2vh] rounded-lg w-full h-fit" :class="themeStore.getTheme()">
            <div class="flex justify-between items-center mb-4">
                <p class="text-lg font-semibold">Filter Tasks</p>
                <VButton @click="filterStore.clearFilter()" msg="Clear filters" class="itbkk-filter-clear" />
            </div>
            <div>
                <div class="grid grid-cols-2 gap-3">
                    <div class="col-span-1">
                        <label for="status">Status</label>
                        <div class="mt-2 overflow-auto max-h-[20vh] border p-[1vh] rounded-lg">
                            <div v-for="status in statusesStore.statuses" :key="status.id" class="flex justify-start p-1">
                                <input
                                    type="checkbox"
                                    :id="status.id"
                                    :value="status.name"
                                    class="mr-[1vh] checkbox checkbox-success itbkk-status-choice"
                                    @change="filterStore.toggleStatus(status.name)"
                                    :checked="filterStore.selectedStatuses.includes(status.name)"
                                />
                                <span :for="status.id">{{ status.name }}</span>
                            </div>
                        </div>
                    </div>
                    <div class="col-span-1">
                        <label for="filter">Filter</label>
                        <div class="mt-2 border w-full h-[20vh] p-[1vh] rounded-lg flex flex-wrap items-start">
                            <span
                                v-for="status in filterStore.selectedStatusNames"
                                :key="status.id"
                                class="itbkk-filter-item px-2 py-1 rounded-lg mr-2 mb-2 flex-shrink-0 text-black"
                                :style="{ backgroundColor: status.color }"
                            >
                                {{ status.name }}
                                <img
                                    src="/close.png"
                                    alt="remove filter"
                                    class="itbkk-filter-item-clear w-[1vh] h-[1vh] inline-block ml-[1vh] hover:cursor-pointer"
                                    @click="filterStore.removeStatus(status.name)"
                                />
                            </span>
                        </div>
                    </div>
                </div>
                <div class="flex justify-end gap-2 mt-[1vh]">
                    <button class="itbkk-button-confirm btn btn-success btn-xs sm:btn-sm md:btn-md" @click="applyFilter()">Filter</button>
                    <button class="itbkk-button-cancel btn btn-error btn-xs sm:btn-sm md:btn-md" @click="closeModal()">Cancel</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
