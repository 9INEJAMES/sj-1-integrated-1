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
import { useBoardApi } from '@/composables/board-api'
import { useAuthStore } from '@/stores/auth'
import { useBoardStore } from '@/stores/board'
import { useTasksStore } from '@/stores/task'
import { useStatusesStore } from '@/stores/status'
import PageNotFound from '@/components/PageNotFound.vue'

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
                    path: '/board/:bid',
                    name: 'boardCache',
                    redirect: (to) => {
                        return { name: 'taskView', params: { bid: to.params.bid } }
                    },
                },
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
        {
            path: '/not-found',
            name: 'notFound',
            component: PageNotFound,
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
    let board

    const reset = () => {
        boardStore.resetBoards()
        statusStore.resetStatuses()
        taskStore.resetTasks()
    }
    if (to.params.bid) {
        board = await boardApi.getBoardById(to.params.bid)
    }

    if (!authStore.isLogin) {
        if (to.name === 'taskAdd' || to.name === 'taskEdit' || to.name === 'statusAdd' || to.name === 'statusEdit') {
            next({ name: 'accessDenied' })
        // } else if (board === 403) {
        //     reset()
        //     next({ name: 'accessDenied' })
        } else if (board === 404) {
            reset()
            next({ name: 'notFound' })
        } else if (to.name != 'login' && to.name != 'accessDenied' && to.name != 'notFound') {
            reset()
            next({ name: 'login' })
        } else {
            next()
        }
    } else {
        if (to.name === 'login' && !(await authStore.checkToken())) {
            next({ name: 'boardView' })
            } else if (to.name === 'taskAdd' || to.name === 'taskEdit' || to.name === 'statusAdd' || to.name === 'statusEdit') {
                if (await authStore.isOwner(to.params.bid)) next()
                else next({ name: 'accessDenied' })
        } else if (board === 403) {
            reset()
            next({ name: 'accessDenied' })
        } else if (board === 404) {
            reset()
            next({ name: 'notFound' })
        } else if (await authStore.checkToken()) {
            if (to.name !== 'login') {
                reset()
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
