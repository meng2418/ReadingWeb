import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../pages/HomePage.vue'
import BookshelfPage from '../pages/BookShelfPage.vue'
import LoginPage from '../pages/LoginPage.vue'
import CommunityPage from '../pages/CommunityPage.vue'
import ForgetPassword from '@/pages/ForgetPassword.vue'
import BookDetail from '@/pages/BookDetail.vue'
import CategoryPage from '@/pages/CategoryPage.vue'
import PostDetailPage from '@/pages/PostDetailPage.vue'
import Profile from '@/pages/Profile.vue'
import TopicDetail from '@/pages/TopicDetail.vue'
import UserPosts from '@/pages/UserPosts.vue'
import ReaderPage from '@/pages/ReaderPage.vue'
import WriteReview from '@/pages/WriteReview.vue'

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
      component: PostDetailPage,
      props: true, // 重要：将路由参数作为 props 传递
    },
    {
      path: '/profile',
      name: 'Profile',
      component: Profile,
    },
    {
      path: '/topicdetail/:id?', // 修改：添加动态参数，?表示可选
      name: 'TopicDetail',
      component: TopicDetail,
      props: true, // 重要：将路由参数作为 props 传递
    },
    {
      path: '/userposts/:id?', // 修改：添加动态参数，?表示可选
      name: 'UserPosts',
      component: UserPosts,
      props: true, // 重要：将路由参数作为 props 传递
    },
    {
      path: '/reader/:id?', // 修改：添加动态参数，?表示可选
      name: 'ReaderPage',
      component: ReaderPage,
      props: true, // 重要：将路由参数作为 props 传递
    },
        {
      path: '/writereview/:id?', // 修改：添加动态参数，?表示可选
      name: 'WriteReview',
      component: WriteReview,
      props: true, // 重要：将路由参数作为 props 传递
    },
  ],
})

export default router
