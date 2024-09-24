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
import { useBoardApi } from '@/composables/board-api'
import { useAuthStore } from '@/stores/auth'
import { useBoardStore } from '@/stores/board'

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
                {
                    path: 'add',
                    name: 'boardAdd',
                    component: BoardModal,
                },
                {
                    path: 'delete',
                    name: 'boardDelete',
                    component: BoardModal,
                },
                {
                    path: ':bid/edit',
                    name: 'boardEdit',
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

router.beforeEach(async (to, from, next) => {
    const boardApi = useBoardApi()
    const boardStore = useBoardStore()
    const authStore = useAuthStore()
    const isTokenExpired = !authStore.checkToken()

    // if (to.name === 'login' && !isTokenExpired) {
    //         next({ name: 'boardView' })
    //     } else if (isTokenExpired) {
    //         if (to.name !== 'login') {
    //             next({ name: 'login' })
    //         } else {
    //             next()
    //         }
    //     } else {
    //         next()
    //     }

    // if (isTokenExpired) {
    //     next({ name: 'login' })
    // } else {
    //     next()
    // }
    if (to.name === 'taskView' || to.name === 'statusView') {
        try {
            const board = await boardApi.getBoardById(to.params.bid)
            console.log('board:', board)

            // Check if board exists and is accessible
            if (!board || board.visibility === 'PRIVATE') {
                next({ name: 'login' })
            } else {
                boardStore.addBoard(board)
                next()
            }
        } catch (error) {
            console.error('Error fetching board:', error)
            next({ name: 'login' }) // Redirect to login on error
        }
    } else if (isTokenExpired) {
        next({ name: 'login' })
    } else {
        next()
    }
})
export default router
