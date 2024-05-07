import { createRouter, createWebHistory } from 'vue-router'
import TaskView from '@/views/TaskView.vue'
import Home from '@/components/Home.vue'
import TaskModal from '@/components/TaskModal.vue'

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
            path: '/task',
            name: 'TaskView',
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
