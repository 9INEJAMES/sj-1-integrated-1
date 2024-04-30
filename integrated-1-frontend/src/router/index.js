import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import TaskDetailsPage from '../components/TaskDetailsPage.vue'
import EditTask from '../views/EditTask.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
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
            path: '/tasks/:taskId/edit',
            name: 'EditTask',
            component: EditTask,
            props: true,
        },

    ],
})

export default router
