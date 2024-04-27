import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import TaskDetails from '../components/TaskDetails.vue'
import TaskDetailsPage from '../components/TaskDetailsPage.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            redirect: '/task',
        },
        {
            path: '/task',
            name: 'home',
            component: HomeView,
        },
        {
            path: '/task/:taskId',
            name: 'TaskDetailsPage',
            component: TaskDetailsPage,
            props: true,
        },
    ],
})

export default router
