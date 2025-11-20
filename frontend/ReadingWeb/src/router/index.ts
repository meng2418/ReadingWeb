import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../pages/HomePage.vue'
import BookshelfPage from '../pages/BookShelfPage.vue'
import LoginPage from '../pages/LoginPage.vue'
import CommunityPage from '../pages/CommunityPage.vue'
import ForgetPassword from '@/pages/ForgetPassword.vue'
import BookDetail from '@/pages/BookDetail.vue'
import CategoryPage from '@/pages/CategoryPage.vue'
import PostDetail from '@/pages/PostDetail.vue'
import Profile from '@/pages/Profile.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: HomePage,
    },
    {
      path: '/login',
      name: 'Login',
      component: LoginPage,
    },
    {
      path: '/bookshelf',
      name: 'Bookshelf',
      component: BookshelfPage,
    },
    {
      path: '/community',
      name: 'Community',
      component: CommunityPage,
    },
    {
      path: '/forget-password',
      name: 'ForgetPassword',
      component: ForgetPassword,
    },
    {
      path: '/category',
      name: 'Category',
      component: CategoryPage,
    },

    {
      path: '/bookdetail/:id?', // 修改：添加动态参数，?表示可选
      name: 'BookDetail',
      component: BookDetail,
      props: true, // 重要：将路由参数作为 props 传递
    },
    {
      path: '/postdetail/:id?', // 修改：添加动态参数，?表示可选
      name: 'PostDetail',
      component: PostDetail,
      props: true, // 重要：将路由参数作为 props 传递
    },
    {
      path: '/profile',
      name: 'Profile',
      component: Profile,
    },
  ],
})

export default router
