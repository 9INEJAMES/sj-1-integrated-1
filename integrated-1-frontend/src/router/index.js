import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import TaskDetails from '../components/TaskDetails.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { 
      path: '/',
      redirect: '/tasks'
    },
    {
      path: '/tasks',
      name: 'home',
      component: HomeView
    },
    {
      path: '/task/:taskId',
      name: 'TaskDetails',
      component: TaskDetails,
      props: true
    }
  ]
})

export default router
