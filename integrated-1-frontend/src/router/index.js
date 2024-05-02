import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import Home from '@/components/Home.vue'
import TaskAddPage from '@/components/TaskAddPage.vue'

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
            name: 'homeView',
            component: HomeView,
            children: [
                {
                    path: '',
                    name: 'home',
                    component: Home,
                },
                {
                    path: ':taskId',
                    name: 'taskDetails',
                    component: TaskAddPage,
                },
                {
                    path: 'add',
                    name: 'taskAdd',
                    component: TaskAddPage,
                },
                {
                    path: ':taskId/edit',
                    name: 'taskEdit',
                    component: TaskAddPage,
                },
            ],
        },
    ],
})

export default router
