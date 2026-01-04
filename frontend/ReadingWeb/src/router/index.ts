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
import AllReadingHighlights from '@/pages/AllReadingHighlights.vue'
import AuthorDetail from '@/pages/AuthorDetail.vue'
import SearchResultsPage from '@/pages/SearchResultsPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: HomePage,
      meta: { title: '微信读书 - 首页' },
    },
    {
      path: '/login',
      name: 'Login',
      component: LoginPage,
      meta: { title: '微信读书 - 登录' },
    },
    {
      path: '/bookshelf',
      name: 'Bookshelf',
      component: BookshelfPage,
      meta: { requiresAuth: true, title: '微信读书 - 书架' }, // 需要登录
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
      meta: { title: '微信读书 - 忘记密码' },
    },
    {
      path: '/category/:categoryId?',
      name: 'Category',
      component: CategoryPage,
      meta: { title: '微信读书 - 分类' },
      props: (route) => ({
        categoryId: route.params.categoryId,
      }),
    },
    {
      path: '/bookdetail/:id?',
      name: 'BookDetail',
      component: BookDetail,
      props: true,
      meta: { title: '微信读书 - 书籍详情' },
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
      path: '/reader/:bookId/:chapterId?',
      name: 'ReaderPage',
      component: ReaderPage,
      props: true,
    },
    {
      path: '/writereview/:id?',
      name: 'WriteReview',
      component: WriteReview,
      props: true,
      meta: { requiresAuth: true, title: '书评编辑' }, // 需要登录
    },
    {
      path: '/allreadinghighlights',
      name: 'AllReadingHighlights',
      component: AllReadingHighlights,
      meta: { title: '微信读书 - 全部划线' },
    },
    {
      path: '/authordetail/:id',
      name: 'AuthorDetail',
      component: AuthorDetail,
      props: true, // 将路由参数作为props传递
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

// --- 修复后的路由守卫 ---
router.beforeEach(async (to, from, next) => {
  // 1. 设置标题
  if (to.meta.title) {
    document.title = to.meta.title as string
  }

  // 2. 获取用户状态
  const userStore = useUserStore()

  // 3. 恢复会话（如果是刷新页面，Pinia 状态可能丢失，尝试恢复）
  if (!userStore.token) {
    userStore.restoreSession()
  }

  // 调试信息（可选）
  console.log(`路由跳转: ${from.path} -> ${to.path}`)
  console.log(`需要登录: ${to.meta.requiresAuth}`)
  console.log(`当前登录状态: ${userStore.isLoggedIn}`)

  // 4. 检查权限
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)

  // 情况 A: 需要登录 且 用户未登录
  if (requiresAuth && !userStore.isLoggedIn) {
    try {
      // 弹出确认框 (这会暂停路由跳转，直到用户点击)
      await ElMessageBox.confirm('当前未登录，请先登录以继续操作', '登录提示', {
        confirmButtonText: '立即登录',
        cancelButtonText: '取消',
        type: 'warning',
        showClose: false,
        closeOnClickModal: false,
        closeOnPressEscape: false,
      })

      // 用户点击“立即登录” -> 跳转去登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }, // 保存原本想去的路径
      })
    } catch (error) {
      // 用户点击“取消” -> 中断跳转或回首页
      console.warn('登录提示对话框被取消')

      // 这里推荐使用 next(false) 来取消当前的导航
      // 或者如果这是一个初始加载（from.path 是 '/' 且 from.name 是 undefined），可以跳转到首页
      if (from.name) {
        next(false) // 停留在当前页
      } else {
        next('/') // 如果是直接输网址进来的，取消后去首页，避免白屏
      }
    }
  }
  // 情况 B: 不需要登录，或者用户已登录
  else {
    next() // 放行
  }
})

export default router
