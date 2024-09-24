import { createRouter, createWebHistory } from 'vue-router'
import BoardModal from '@/components/BoardModal.vue'
import TaskView from '@/views/TaskView.vue'
import TaskModal from '@/components/TaskModal.vue'
import StatusView from '@/views/StatusView.vue'
import StatusModal from '@/components/StatusModal.vue'
import SignIn from '@/components/SignIn.vue'
import AccessDenied from '@/components/AccessDenied.vue'
import BoardView from '@/views/BoardView.vue'
import HomePage from '@/components/HomePage.vue'
import VueJwtDecode from 'vue-jwt-decode'
import { useBoardApi } from '@/composables/board-api'
import { useAuthStore } from '@/stores/auth'
import { useBoardStore } from '@/stores/board'
import { useTasksStore } from '@/stores/task'
import { useStatusesStore } from '@/stores/status'

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
        {
            path: '/access-denied',
            name: 'accessDenied',
            component: AccessDenied,
        },
    ],
})

router.beforeEach(async (to, from, next) => {
    const boardApi = useBoardApi()
    const boardStore = useBoardStore()
    const authStore = useAuthStore()
    const toastStore = useTasksStore()
    const taskStore = useTasksStore()
    const statusStore = useStatusesStore()


    // if (to.name === 'taskView' || to.name === 'statusView') {
    //     try {
    //         const board = await boardApi.getBoardById(to.params.bid)
    //         const authData = authStore.getAuthData()
    //         // Check if board exists and is accessible
    //         if (board.visibility === 'PRIVATE') {
    //             toastStore.changeToast(false, 'Accsess denied, you do not have permission to view this page')
    //             next({ name: 'login' })
    //         } else {
    //             await boardStore.addBoard(board)
    //             await taskStore.fetchTasks()
    //             await statusStore.fetchStatuses()
    //             next()
    //         }
    //     } catch (error) {
    //         console.error('Error fetching board:', error)
    //         next({ name: 'login' }) // Redirect to login on error
    //     }
    // } else if ((await authStore.checkToken()) && to.name !== 'login') {
    //     next({ name: 'login' })
    // } else {
    //     next()
    // }

    if (!authStore.isLogin) {
        if (to.name === 'taskView' || to.name === 'statusView') {
            try {
                const board = await boardApi.getBoardById(to.params.bid)
                const authData = authStore.getAuthData()
                // Check if board exists and is accessible
                if (board.visibility === 'PRIVATE') {
                    toastStore.changeToast(false, 'Accsess denied, you do not have permission to view this page')
                    next({ name: 'login' })
                } else {
                    await boardStore.addBoard(board)
                    await taskStore.fetchTasks()
                    await statusStore.fetchStatuses()
                    next()
                }
            } catch (error) {
                console.error('Error fetching board:', error)
                next({ name: 'login' }) // Redirect to login on error
            }
        } else {
            next()
        }
    } else {
        if (to.name === 'login' && !(await authStore.checkToken())) {
            next({ name: 'boardView' })
        } else if (await authStore.checkToken()) {
            if (to.name !== 'login') {
                next({ name: 'login' })
            } else {
                next()
            }
        } else {
            next()
        }
    }
})
export default router
