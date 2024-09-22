// import { createRouter, createWebHistory } from 'vue-router'
// import BoardModal from '@/components/BoardModal.vue'
// import TaskView from '@/views/TaskView.vue'
// import TaskModal from '@/components/TaskModal.vue'
// import StatusView from '@/views/StatusView.vue'
// import StatusModal from '@/components/StatusModal.vue'
// import SignIn from '@/components/SignIn.vue'
// import BoardView from '@/views/BoardView.vue'
// import HomePage from '@/components/HomePage.vue'
// import { useBoardApi } from '@/composables/board-api'
// import { useAuthStore } from '@/stores/auth'
// import { useBoardStore } from '@/stores/board'

// const router = createRouter({
//     history: createWebHistory(import.meta.env.BASE_URL),
//     routes: [
//         {
//             path: '/',
//             redirect: '/login',
//         },
//         {
//             path: '/homepage',
//             name: 'homepage',
//             component: HomePage,
//         },
//         {
//             path: '/login',
//             name: 'login',
//             component: SignIn,
//         },
//         {
//             path: '/:pathMatch(.*)*',
//             redirect: '/login',
//         },
//         {
//             path: '/board',
//             name: 'boardView',
//             component: BoardView,
//             children: [
//                 {
//                     path: 'add',
//                     name: 'boardAdd',
//                     component: BoardModal,
//                 },
//                 {
//                     path: 'delete',
//                     name: 'boardDelete',
//                     component: BoardModal,
//                 },
//                 {
//                     path: ':bid/edit',
//                     name: 'boardEdit',
//                     component: BoardModal,
//                 },
//             ],
//         },
//         // Nested Status Routes
//         {
//             path: '/board/:bid/status',
//             name: 'statusView',
//             component: StatusView,
//             children: [
//                 {
//                     path: 'add',
//                     name: 'statusAdd',
//                     component: StatusModal,
//                 },
//                 {
//                     path: ':id/edit',
//                     name: 'statusEdit',
//                     component: StatusModal,
//                 },
//             ],
//         },
//         // Nested Task Routes
//         {
//             path: '/board/:bid/task',
//             name: 'taskView',
//             component: TaskView,
//             children: [
//                 {
//                     path: 'add',
//                     name: 'taskAdd',
//                     component: TaskModal,
//                 },
//                 {
//                     path: ':taskId',
//                     name: 'taskDetails',
//                     component: TaskModal,
//                 },
//                 {
//                     path: ':taskId/edit',
//                     name: 'taskEdit',
//                     component: TaskModal,
//                 },
//             ],
//         },
//     ],
// })

// router.beforeEach(async (to, from, next) => {
//     const boardApi = useBoardApi()
//     const boardStore = useBoardStore()
//     const authStore = useAuthStore()
//     await authStore.checkToken()
//     const isTokenExpired = authStore.isTokenExpired()

//     const token = authStore.getToken()
//     // if (to.name === 'login' && !authStore.isTokenExpired()) {
//     //     next({ name: 'boardView' })
//     // } else if (authStore.isTokenExpired()) {
//     //     authStore.logout()
//     //     next()
//     // }
//     if (isTokenExpired && to.name !== 'login') {
//         next({ name: 'login' })
//     } else {
//         next()
//     }

//     // if (!token && to.name !== 'login') {
//     //     next({ name: 'login' })
//     // }
//     // if (to.name === 'taskView' || to.name === 'statusView') {
//     //     try {
//     //         const board = await boardApi.getCurrentBoard()

//     //         // Check if board exists and is accessible
//     //         if (!board || board.visibility === 'PRIVATE') {
//     //             next({ name: 'login' })
//     //         } else {
//     //             boardStore.addBoard(board)
//     //             if (to.name === 'taskView') {
//     //                 next({ name: 'taskView', params: { bid: board.id } })
//     //             } else if (to.name === 'statusView') {
//     //                 next({ name: 'statusView', params: { bid: board.id } })
//     //             }
//     //         }
//     //     } catch (error) {
//     //         console.error('Error fetching board:', error)
//     //         next({ name: 'login' }) // Redirect to login on error
//     //     }
//     // } else {
//     //     next()
//     // }
// })

// export default router
