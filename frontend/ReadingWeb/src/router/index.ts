import { createRouter, createWebHistory } from 'vue-router'
import Home from '../pages/home.vue'
import Bookshelf from '../pages/bookshelf.vue'
import Login from '../pages/login.vue'
import Community from '../pages/community.vue'
import ForgetPassword from '@/pages/ForgetPassword.vue'
import BookDetail from '@/pages/BookDetail.vue'
import Category from '@/pages/Category.vue'
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
      path: '/forget-password',
      name: 'ForgetPassword',
      component: ForgetPassword,
    },
    {
      path: '/category',
      name: 'Category',
      component: Category,
    },

    {
      path: '/bookdetail',
      name: 'BookDetail',
      component: BookDetail,
    },
  ],
})

export default router
