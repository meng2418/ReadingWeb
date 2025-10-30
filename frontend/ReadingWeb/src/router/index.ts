import { createRouter, createWebHistory } from 'vue-router'
import Home from '../pages/home.vue'
import Bookshelf from '../pages/bookshelf.vue'
import Login from '../pages/login.vue'
import Community from '../pages/community.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home,
    },
    {
      path: '/login',
      name: 'Login',
      component: Login,
    },
    {
      path: '/bookshelf',
      name: 'Bookshelf',
      component: Bookshelf,
    },
    {
      path: '/community',
      name: 'Community',
      component: Community,
    },
    {
      path: '/category',
      name: 'Category',
      component: () => import('../pages/Category.vue'),
      props: (route) => ({ tab: route.query.tab }), // 添加参数支持
    },

    {
      path: '/book/:id',
      name: 'BookDetail',
      component: () => import('../pages/BookDetail.vue'),
      props: true // 重要：将路由参数作为 props 传递
    },
    {
      path: '/test/book-detail',
      name: 'TestBookDetail',
      component: () => import('../pages/TestBookDetail.vue') // 或 @/pages/TestBookDetail.vue

    },
  ],
})

export default router
