<!-- Profile.vue -->
<template>
  <NavBar />
  <div class="profile-page" :style="cssVars">
    <section class="header-section">
      <UserProfile
        :user="user"
        :theme="theme"
        @theme-change="handleThemeChange"
        @profile-updated="handleProfileUpdated"
      />
    </section>

    <section class="main-section">
      <aside class="left-sidebar">
        <SidebarRankings @user-data-updated="handleUserDataUpdated" />
      </aside>

      <main class="right-dashboard">
        <ReadingDashboard
          :initialTab="initialTab"
          :readingStats="readingStats"
          :historyRecords="historyRecords"
          :timelineData="timelineData"
          :topBooks="topBooks"
        />
        <ReadingHighlights :highlights="highlights" />
        <ReadingThoughts :thoughts="thoughts" />
        <ReadingReviews :reviews="reviews" />
      </main>
    </section>
  </div>
  <Footer />
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import type { TopBook } from '@/types/user'
import { useTitle } from '@/stores/useTitle'
import { useTheme } from '@/composables/useTheme'
import NavBar from '@/components/layout/NavBar.vue'
import UserProfile from '@/components/user/UserProfile.vue'
import SidebarRankings from '@/components/user/SidebarRankings.vue'
import ReadingDashboard from '@/components/user/ReadingDashboard.vue'
import ReadingHighlights from '@/components/user/ReadingHighlights.vue'
import ReadingThoughts from '@/components/user/ReadingThoughts.vue'
import ReadingReviews from '@/components/user/ReadingReviews.vue'
import Footer from '@/components/layout/Footer.vue'
import {
  getProfileHome,
  getRecentHighlights,
  getRecentNotes6,
  getRecentBookReviews,
  getHistoryMilestones,
  getTopBooks,
  getReadingTimeline,
} from '@/api/profile'

// 主题管理
const theme = useTheme()
const cssVars = theme.cssVars

// 处理主题变化事件
const handleThemeChange = (newTheme: string) => {
  theme.previewThemeValue.value = newTheme
}

// 初始化时应用主题
onMounted(() => {
  // 主题会在 useTheme 的 watch 中自动应用
})

const route = useRoute()
const user = ref({
  nickname: '',
  signature: '',
  avatar: '',
  stats: {
    following: 0,
    followers: 0,
    posts: 0,
  },
  payCoin: 0,
  giftVIP: 0,
  isVip: false,
  vipDays: 0,
  vipEndTime: null,
})
const readingStats = ref<any>(null)
const highlights = ref<any[]>([])
const reviews = ref<any[]>([])
const thoughts = ref<any[]>([])
const historyRecords = ref<any[]>([])
const topBooks = ref<TopBook[]>([])
const timelineData = ref<any>(null)
// 加载用户数据
const loadUserData = async () => {
  const period = typeof route.query.tab === 'string' ? (route.query.tab as any) : 'week'

  const [
    home,
    highlightsData,
    thoughtsData,
    reviewsRecent,
    historyRecordsData,
    topBooksData,
    timelineDataValue,
  ] = await Promise.all([
    getProfileHome(),
    getRecentHighlights(),
    getRecentNotes6(),
    getRecentBookReviews(),
    getHistoryMilestones(),
    getTopBooks(period),
    getReadingTimeline(),
  ])

  user.value.nickname = home.username
  user.value.signature = home.bio
  user.value.avatar = home.avatar
  user.value.isVip = home.isMember
  user.value.payCoin = home.coinCount
  user.value.giftVIP = home.experienceCardCount ?? 0
  user.value.vipDays = home.memberExpireDays ?? 0
  user.value.stats.following = home.followingCount ?? 0
  user.value.stats.followers = home.followerCount ?? 0
  user.value.stats.posts = home.postCount ?? 0
  user.value.vipEndTime = null

  readingStats.value = home.readingStats
  highlights.value = highlightsData
  thoughts.value = thoughtsData
  reviews.value = reviewsRecent
  historyRecords.value = historyRecordsData
  topBooks.value = topBooksData
  timelineData.value = timelineDataValue
}

// 处理资料更新事件
const handleProfileUpdated = async () => {
  // 重新加载用户数据
  await loadUserData()
}

// 处理用户数据更新事件（来自阅读激励领取）
const handleUserDataUpdated = (homeData: any) => {
  // 更新用户信息，包括体验卡天数、充值币、会员信息等
  user.value.nickname = homeData.username
  user.value.signature = homeData.bio
  user.value.avatar = homeData.avatar
  user.value.isVip = homeData.isMember
  user.value.payCoin = homeData.coinCount
  user.value.giftVIP = homeData.experienceCardCount ?? 0
  user.value.vipDays = homeData.memberExpireDays ?? 0
  user.value.stats.following = homeData.followingCount ?? 0
  user.value.stats.followers = homeData.followerCount ?? 0
  user.value.stats.posts = homeData.postCount ?? 0
  readingStats.value = homeData.readingStats
}

onMounted(async () => {
  await loadUserData()
})

// 动态页面标题
const title = computed(() =>
  user.value.nickname ? `${user.value.nickname} - 个人主页` : '个人主页',
)
useTitle(title)
const initialTab = computed(() => (typeof route.query.tab === 'string' ? route.query.tab : 'week'))
</script>

<style scoped>
/* 定义页面级 CSS 变量 
  如果你的项目中已有全局样式文件(如 global.css)，可以将 :root 部分移过去
*/

/* 限制内容最大宽度，保持大屏美观 */
.profile-page > * {
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

/* 顶部区域间距 */
.header-section {
  margin-top: 60px;
  margin-bottom: 20px;
}

/* Grid 布局：左侧定宽，右侧自适应 */
.main-section {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 20px;
  align-items: start; /* 防止侧边栏被拉伸 */
}
.right-dashboard {
  /* 关键代码：防止 grid 子元素被内部宽内容撑开 */
  min-width: 0;
  /* 或者使用 overflow: hidden; 但 min-width: 0 更推荐 */
}
/* 响应式处理 */
@media (max-width: 900px) {
  .main-section {
    grid-template-columns: 1fr; /* 平板/手机端变为单列 */
  }

  .left-sidebar {
    order: 2; /* 移动端将排行榜放到下面，如果需要的话 */
  }

  .right-dashboard {
    order: 1;
  }
}
</style>
