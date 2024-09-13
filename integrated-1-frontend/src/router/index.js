import { createRouter, createWebHistory } from 'vue-router'
import BoardModal from '@/components/BoardModal.vue'
import TaskView from '@/views/TaskView.vue'
import TaskModal from '@/components/TaskModal.vue'
import StatusView from '@/views/StatusView.vue'
import StatusModal from '@/components/StatusModal.vue'
import SignIn from '@/components/SignIn.vue'
import BoardView from '@/views/BoardView.vue'
import HomePage from '@/components/HomePage.vue'
import VueJwtDecode from 'vue-jwt-decode'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            redirect: '/login',
        },
        {
            path: '/homepage',
            name: 'homepage',
            component: HomePage,
        },
        {
            path: '/login',
            name: 'login',
            component: SignIn,
        },
        {
            path: '/:pathMatch(.*)*',
            redirect: '/login',
        },
        {
            path: '/board',
            name: 'boardView',
            component: BoardView,
            children: [
                // Nested Board Routes
                {
                    path: 'add',
                    name: 'boardAdd',
                    component: BoardModal,
                },
            ],
        },
        // Nested Status Routes
        {
            path: '/board/:bid/status',
            name: 'statusView',
            component: StatusView,
            children: [
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
        // Nested Task Routes
        {
            path: '/board/:bid/task',
            name: 'taskView',
            component: TaskView,
            children: [
                {
                    path: 'add',
                    name: 'taskAdd',
                    component: TaskModal,
                },
                {
                    path: ':taskId',
                    name: 'taskDetails',
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

router.beforeEach((to, from, next) => {
    const authData = JSON.parse(localStorage.getItem('authData'))
    const token = authData ? authData.token : null
    let isTokenExpired = true

    if (token) {
        try {
            const decodedToken = VueJwtDecode.decode(token)
            isTokenExpired = decodedToken.exp < Date.now() / 1000
        } catch (error) {
            console.error('Failed to decode token:', error)
            isTokenExpired = true
        }
    }

    // If the user is trying to access the login page and is already authenticated, redirect to the taskView
    if (to.name === 'login' && !isTokenExpired) {
        next({ name: 'boardView' })
    }
    // If the user is trying to access a protected page but the token is expired, redirect to the login page
    else if (to.meta.requiresAuth && isTokenExpired) {
        if (to.name !== 'login') {
            // Ensure we are not already trying to go to the login page
            next({ name: 'login' })
        } else {
            next() // If already trying to go to login, proceed
        }
    }
    // For all other cases, proceed as normal
    else {
        next()
    }
})
export default router
