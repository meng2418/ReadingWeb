import { createRouter, createWebHistory } from 'vue-router'
import Home from '../pages/home.vue'
import Bookshelf from '../pages/bookshelf.vue'
import Login from '../pages/Login.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/bookshelf',
      name: 'Bookshelf',
      component: Bookshelf
    },
    {
      path: '/test',
      name: 'Test',
      component: () => import('../pages/Test.vue') // 使用相对路径更可靠
    },
    {
      path: '/category',
      name: 'Category',
      component: () => import('../pages/Category.vue'),
      props: (route) => ({ tab: route.query.tab }) // 添加参数支持
    }
  ]


})

export default router
