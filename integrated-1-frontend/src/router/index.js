import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import TaskDetailsPage from '../components/TaskDetailsPage.vue'
import TaskAddPage from '@/components/TaskAddPage.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            redirect: '/tasks',
        },
        {
            path: '/:pathMatch(.*)*',
            redirect: '/tasks',
        },
        {
            path: '/tasks',
            name: 'home',
            component: HomeView,
        },
        {
            path: '/tasks/:taskId',
            name: 'TaskDetailsPage',
            component: TaskDetailsPage,
            props: true,
        },
        {
            path: '/tasks/add',
            name: 'AddTask',
            component: TaskAddPage,
        },
        {
            path: '/tasks/:taskId/edit',
            name: 'EditTask',
            component: TaskAddPage,
        },
    ],
})

export default router
