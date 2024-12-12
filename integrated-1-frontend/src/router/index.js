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
import CollabView from '@/views/CollabView.vue'
import CollabInviteView from '@/views/CollabInviteView.vue'

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
            meta: { requireLogin: true }, //requireViewer

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
                    meta: { requireLogin: true },
                },
                {
                    path: 'delete',
                    name: 'boardDelete',
                    component: BoardModal,
                    meta: { requireOwner: true },
                },
                {
                    path: ':bid/edit',
                    name: 'boardEdit',
                    component: BoardModal,
                    meta: { requireOwner: true },
                },
            ],
        },
        {
            path: '/board/:bid/status',
            name: 'statusView',
            component: StatusView,
            meta: { requireViewer: true },

            children: [
                {
                    path: 'add',
                    name: 'statusAdd',
                    component: StatusModal,
                    meta: { requireEditor: true },
                },
                {
                    path: ':id/edit',
                    name: 'statusEdit',
                    component: StatusModal,
                    meta: { requireEditor: true },
                },
            ],
        },
        {
            path: '/board/:bid/collab',
            name: 'collabView',
            component: CollabView,
            meta: { requireViewer: true },
        },
        {
            path: '/board/:bid/collab/invitations',
            name: 'collabInviteView',
            component: CollabInviteView,
            meta: { requireLogin: true },
        },
        {
            path: '/board/:bid/task',
            name: 'taskView',
            component: TaskView,
            meta: { requireViewer: true },

            children: [
                {
                    path: 'add',
                    name: 'taskAdd',
                    component: TaskModal,
                    meta: { requireEditor: true },
                },
                {
                    path: ':taskId',
                    name: 'taskDetails',
                    component: TaskModal,
                    meta: { requireViewer: true },
                },
                {
                    path: ':taskId/edit',
                    name: 'taskEdit',
                    component: TaskModal,
                    meta: { requireEditor: true },
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
    const taskStore = useTasksStore()
    const statusStore = useStatusesStore()
    let board
    let bStatus

    await authStore.loadAzureData()

    const reset = () => {
        boardStore.resetBoards()
        statusStore.resetStatuses()
        taskStore.resetTasks()
    }
    if (to.meta.requireViewer && to.params.bid) {
        const { response, status } = await boardApi.getBoardById(to.params.bid)
        board = response
        bStatus = status
    }
    const isLogin = await authStore.getLoginStatus()
    if (to.meta.requireOwner) {
        if ((await authStore.isOwner(to.params.bid)) == false) {
            next({ name: 'accessDenied' })
        } else {
            next()
        }
    } else if (to.meta.requireEditor || (to.meta.requireViewer && bStatus == 403)) {
        if ((await authStore.isOwner(to.params.bid)) || (await authStore.isEditor(to.params.bid))) next()
        else next({ name: 'accessDenied' })
        
    } else if (!isLogin) {
        if (to.meta.requireLogin || to.meta.requireOwner || to.meta.requireEditor) {
            const redirectTo = to.fullPath
            next({ name: 'login', query: { redirectTo } })
        } else {
            next()
        }
    } else if (isLogin) {
        if (to.name === 'login' && !(await authStore.checkToken())) {
            next({ name: 'boardView' })
        } else if ((from.name === 'login' || from.name === 'boardAdd') && to.name === 'boardView') {
            const result = await boardApi.getAllBoard()
            if (result?.person_boards?.length == 1 && result?.collab_boards == 0) {
                next({ name: 'taskView', params: { bid: result?.person_boards[0].id } })
            } else {
                next()
            }
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
    } else if (bStatus == 404) {
        reset()
        next({ name: 'notFound' })
    } else if (bStatus === 403) {
        reset()
        next({ name: 'accessDenied' })
    } else {
        next()
    }
})
export default router
