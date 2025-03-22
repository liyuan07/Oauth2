import UserAccountLogin from "@/views/UserAccountLogin.vue"
import UserAccountRegister from "@/views/UserAccountRegister.vue"
import { createRouter, createWebHistory } from "vue-router"
import NotFound from "@/views/error/NotFound.vue"
import HomeView from "@/views/HomeView.vue"

const router = createRouter({
  history: createWebHistory(),
  routes : [
    {
      path: "/",
      name: "root",
      component: HomeView
    },
    {
      path: "/user/account/login",
      name: "user_account_login",
      component: UserAccountLogin,
    },
    {
      path: "/user/account/register",
      name: "user_account_register",
      component: UserAccountRegister,
    },
    {
      path: "/404",
      name: "404",
      component: NotFound,
    },
    {
      path: "/:catchAll(.*)",
      redirect: "/404"
    }
  ]
})

export default router
