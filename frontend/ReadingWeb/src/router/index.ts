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
  ],
})

export default router
