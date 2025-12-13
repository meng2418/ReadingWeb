// router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
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
import AllReadingNotes from '@/pages/AllReadingNotes.vue'
import AuthorDetail from '@/pages/AuthorDetail.vue'
import SearchResultsPage from '@/pages/SearchResultsPage.vue'
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
      meta: { requiresAuth: true }, // 需要登录
    },
    {
      path: '/community',
      name: 'Community',
      component: CommunityPage,
      meta: { requiresAuth: true }, // 需要登录
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
      path: '/bookdetail/:id?',
      name: 'BookDetail',
      component: BookDetail,
      props: true,
    },
    {
      path: '/postdetail/:id?',
      name: 'PostDetail',
      component: PostDetailPage,
      props: true,
    },
    {
      path: '/profile',
      name: 'Profile',
      component: Profile,
      meta: { requiresAuth: true }, // 需要登录
    },
    {
      path: '/topicdetail/:id?',
      name: 'TopicDetail',
      component: TopicDetail,
      props: true,
    },
    {
      path: '/userposts/:id?',
      name: 'UserPosts',
      component: UserPosts,
      props: true,
    },
    {
      path: '/reader/:id?',
      name: 'ReaderPage',
      component: ReaderPage,
      props: true,
    },
    {
      path: '/writereview/:id?',
      name: 'WriteReview',
      component: WriteReview,
      props: true,
      meta: { requiresAuth: true }, // 需要登录
    },
    {
      path: '/allreadingnotes',
      name: 'AllReadingNotes',
      component: AllReadingNotes,
    },
    {
      path: '/authordetail/:id',
      name: 'AuthorDetail',
      component: AuthorDetail,
      props: true // 将路由参数作为props传递
    },
    {
      path: '/search',
      name: 'SearchResult', // 路由名称
      component: SearchResultsPage, // 关联到 SearchResultPage 组件
      props: (route) => ({
        // 通过 props 传递 query 参数 q (搜索关键词) 给组件
      searchQuery: route.query.q,
      }),
    },
  ],
})

// 路由守卫：登录拦截
router.beforeEach(async (to, from, next) => {
  // 获取用户状态
  const userStore = useUserStore()

  // 恢复会话（如果是刷新页面）
  if (!userStore.token) {
    userStore.restoreSession()
  }

  // 调试信息
  console.log(`路由跳转: ${from.path} -> ${to.path}`)
  console.log(`需要登录吗: ${to.meta.requiresAuth}`)
  console.log(`用户登录状态: ${userStore.isLoggedIn}`)

  // 检查目标路由是否需要登录
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)

  // 如果需要登录但用户未登录
  if (requiresAuth && !userStore.isLoggedIn) {
    try {
      // 显示Element Plus的确认对话框
      await ElMessageBox.confirm('当前未登录，请先登录以继续操作', '登录提示', {
        confirmButtonText: '立即登录',
        cancelButtonText: '取消',
        type: 'warning',
        showClose: false,
        closeOnClickModal: false,
        closeOnPressEscape: false,
      })

      // 用户点击了"立即登录"
      // 跳转到登录页面，并携带当前想去的页面路径
      next({
        path: '/login',
        query: { redirect: to.fullPath },
      })
    } catch (error) {
      // 用户点击了"取消"
      // 返回上一页或首页
      if (from.path !== '/') {
        next(from.path)
      } else {
        next('/')
      }
    }
  } else {
    // 不需要登录或已登录，正常放行
    next()
  }
})

export default router
