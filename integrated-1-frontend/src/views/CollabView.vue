<script setup>
import { ref, onMounted } from 'vue';
import { useTheme } from '@/stores/theme.js';
import { useCollabStore } from '@/stores/collab';
import { useAuthStore } from '@/stores/auth';
import { useRoute } from 'vue-router';
import CollaboratorModal from '@/components/CollaboratorModal.vue';

const themeStore = useTheme();
const collabStore = useCollabStore();
const authStore = useAuthStore();
const route = useRoute();

const showModal = ref(false);
const modalAction = ref('');
const selectedCollaborator = ref(null); // Store the selected collaborator for deletion


const openModal = (action, collaborator = null , accessRight = null) => {
    modalAction.value = action;
    selectedCollaborator.value = collaborator; // Set the selected collaborator for deletion
    showModal.value = true;
};

const closeCollabModal = () => {
    // collabStore.fetchCollaborator(route.params.bid);
    showModal.value = false;
    modalAction.value = '';
    selectedCollaborator.value = null; // Clear the selected collaborator
};


onMounted(async () => {
    collabStore.fetchCollaborator(route.params.bid);
    console.log(collabStore.collaborators);
});



</script>


<template>
    <div class="text-center text-3xl font-bold pt-[4vh]">Collaborator Management</div>
    <div class="flex justify-between pt-[2vh] pr-[5vh]">
        <div class="flex">
            <div class="pl-[5vh] pr-[1vh] text-2xl font-bold text-blue-500">{{ authStore.getAuthData().name }}</div>
            <div class="text-2xl font-bold"> > Collaborator</div>
        </div>
        <div class="btn" @click="openModal('add')">Add collaborator</div>
    </div>
    <div class="flex justify-between pt-[1vh] pl-[5vh] pr-[5vh]">
        <table class="myTable table-lg table-pin-rows shadow-lg">
            <thead class="w-full">
                <tr class="text-lg" :class="themeStore.getTableTheme()">
                    <th style="width: 1%">No</th>
                    <th style="width: 35%">Name</th>
                    <th style="width: 30%">Owner</th>
                    <th style="width: 20%">Access Right</th>
                    <th style="width: 14%">Action</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(collaborator, index) in collabStore.collaborators" :key="collaborator.oid">
                    <td>
                        <div class="flex">{{ index + 1 }}</div>
                    </td>
                    <td>{{ collaborator.name }}</td>
                    <td>{{ collaborator.email }}</td>
                    <td>
                        <!-- <select name="accessRight" id="accessRight" class="btn">
                            <option :value="collaborator.accessRight">{{ collaborator.accessRight }}</option>
                            <option>{{ collaborator.accessRight === 'WRITE' ? 'READ' : 'WRITE' }}</option>
                        </select> -->
                        <div class="dropdown">
                            <div tabindex="0" role="button" class="btn m-1">{{ collaborator.accessRight }} v </div>
                            <ul tabindex="0" class="dropdown-content menu bg-base-100 rounded-box z-[1] w-52 p-2 shadow">
                                <li><a>{{ collaborator.accessRight }}</a></li>
                                <li @click="openModal('update', collaborator)"><a>{{ collaborator.accessRight === 'WRITE' ?
                                    'READ' : 'WRITE' }}</a></li>
                            </ul>
                        </div>
                    </td>
                    <td>
                        <button class="btn" @click="openModal('delete', collaborator)">
                            Leave
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <CollaboratorModal v-if="showModal" @close="closeCollabModal" :action="modalAction" :collaborator="selectedCollaborator"
        class="z-[45]" />
</template>

<style scoped></style>
