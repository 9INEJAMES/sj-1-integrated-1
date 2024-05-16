import { createRouter, createWebHistory } from 'vue-router'
import TaskView from '@/views/TaskView.vue'
import Home from '@/components/Home.vue'
import TaskModal from '@/components/TaskModal.vue'
import StatusView from '@/views/StatusView.vue'
import StatusModal from '@/components/StatusModal.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            redirect: '/task',
        },
        {
            path: '/:pathMatch(.*)*',
            redirect: '/task',
        },
        {
            path: '/status',
            name: 'statusView',
            component: StatusView,
            children: [
                {
                    path: '',
                    name: 'home',
                    component: Home,
                },
                {
                    path: 'add',
                    name: 'statusAdd',
                    component: StatusModal,
                },
                {
                    path: ':id/edit',
                    name: 'statusEdit',
                    component: StatusModal,
                },
            ],
        },
        {
            path: '/task',
            name: 'taskView',
            component: TaskView,
            children: [
                {
                    path: '',
                    name: 'home',
                    component: Home,
                },
                {
                    path: ':taskId',
                    name: 'taskDetails',
                    component: TaskModal,
                },
                {
                    path: 'add',
                    name: 'taskAdd',
                    component: TaskModal,
                },
                {
                    path: ':taskId/edit',
                    name: 'taskEdit',
                    component: TaskModal,
                },
            ],
        },
    ],
})

export default router
