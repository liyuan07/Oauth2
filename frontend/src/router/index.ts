import UserAccountLogin from "@/views/UserAccountLogin.vue"
import UserAccountRegister from "@/views/UserAccountRegister.vue"
import { createRouter, createWebHistory } from "vue-router"
import NotFound from "@/views/error/NotFound.vue"

const routes = [

  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLogin,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/user/account/register/",
    name: "user_account_register",
    component: UserAccountRegister,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/404/",
    name: "404",
    component: NotFound,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/:catchAll(.*)",
    redirect: "/404/"
  }
]



const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

// to表示跳转到哪个页面，from表示从哪个页面跳转过去
// next表示将页面要不要执行下一步擦欧洲哦。
router.beforeEach((to , from , next) => {
  if(to.meta.requestAuth){
    next({name: "user_account_login"});
  }else{
    next();
  }
})
