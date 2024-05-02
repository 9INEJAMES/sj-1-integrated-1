import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import TaskDetailsPage from '../components/TaskDetailsPage.vue'
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
            name: 'home',
            component: HomeView,
            children: [
                {
                    path: '/task/:taskId',
                    name: 'TaskDetailsPage',
                    component: TaskDetailsPage,
                    props: true,
                },
                {
                    path: '/task/add',
                    name: 'AddTask',
                    component: TaskAddPage,
                },
                {
                    path: '/task/:taskId/edit',
                    name: 'EditTask',
                    component: TaskAddPage,
                },
            ]
        },
       
    ],
})

export default router
