import { createRouter, createWebHistory } from 'vue-router'
import PKIndexView from '../views/pk/PKIndexView'
import RanklistIndexView from '../views/ranklist/RanklistIndexView'
import RecordIndexView from '../views/record/RecordIndexView'
import RecordContentView from '../views/record/RecordContentView'
import UserBotIndexView from '../views/user/bot/UserBotIndexView'
import NotFound from '../views/error/NotFound'
import UserAccountLoginView from '../views/user/account/UserAccountLoginView'
import UserAccountRegisterView from '../views/user/account/UserAccountRegisterView'
import store from '../store/index'  //需要判断当前页面是否登录,引过来的全局变量

const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/pk/",
    meta: {    //额外信息，存到一个域里面，名字可以随便取，大多数都是取meta
      requestAuth: true,  //这个页面是否需要授权
    }
  },
  {
    path: "/pk/",
    name: "pk_index",
    component: PKIndexView,
    meta: {    
      requestAuth: true, 
    }
  },
  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
    meta: {    
      requestAuth: true,  
    }
  },
  {
    path: "/record/:recordId/",
    name: "record_content",
    component: RecordContentView,
    meta: {    
      requestAuth: true,  
    }
  },
  {
    path: "/ranklist/",
    name: "ranklist_index",
    component: RanklistIndexView,
    meta: {    
      requestAuth: true,  
    }
  },
  {
    path: "/user/bot/",
    name: "user_bot_index",
    component: UserBotIndexView,
    meta: {   
      requestAuth: true,  
    }
  },
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
    meta: {   
      requestAuth: false,  
    }
  },
  {
    path: "/user/account/register/",
    name: "user_account_register",
    component: UserAccountRegisterView,
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
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

//to:点击的想去的页面，from:从哪个页面跳转，
//如果要去授权页面但是没登陆就跳到登陆页面
router.beforeEach((to, from, next) => {
  if (to.meta.requestAuth && !store.state.user.is_login) {
    next({name: "user_account_login"});
  } else {  //如果不需要授权就跳转到默认的页面
    next();
  }
})
export default router
