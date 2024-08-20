import { createRouter, createWebHistory } from "vue-router"
import TaskView from "@/views/TaskView.vue"
import TaskModal from "@/components/TaskModal.vue"
import StatusView from "@/views/StatusView.vue"
import StatusModal from "@/components/StatusModal.vue"
import SignIn from "@/components/SignIn.vue"
import HomePage from "@/components/HomePage.vue"

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: "/",
            redirect: "/login"
        },
        {
            path:"/homepage",
            name:"homepage",
            component: HomePage
        },
        {
            path: "/login",
            name: "signIn",
            component: SignIn,
        },
        {
            path: "/:pathMatch(.*)*",
            redirect: "/task",
        },
        {
            path: "/status",
            name: "statusView",
            component: StatusView,
            children: [
                {
                    path: "add",
                    name: "statusAdd",
                    component: StatusModal,
                },
                {
                    path: ":id/edit",
                    name: "statusEdit",
                    component: StatusModal,
                },
            ],
        },
        {
            path: "/task",
            name: "taskView",
            component: TaskView,
            children: [
                {
                    path: ":taskId",
                    name: "taskDetails",
                    component: TaskModal,
                },
                {
                    path: "add",
                    name: "taskAdd",
                    component: TaskModal,
                },
                {
                    path: ":taskId/edit",
                    name: "taskEdit",
                    component: TaskModal,
                },
            ],
        },
    ],
})

export default router
