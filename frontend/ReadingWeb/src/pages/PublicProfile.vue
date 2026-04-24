<template>
  <NavBar />
  <div class="public-profile-page" :style="cssVars">
    <section class="header-section">
      <PublicUserHeader
        v-if="home"
        :user-id="userId"
        :profile="home"
        :theme="theme"
        @follow-change="handleFollowChange"
      />
    </section>

    <section v-if="home" class="content-section">
      <BookshelfPreview
        v-if="home.visibility?.bookshelf !== false"
        mode="public"
        header-title="TA的书架"
        :public-books="home.bookshelf.items"
      />

      <!-- 阅读统计（对齐 visibility.readingStats） -->
      <div v-if="home.visibility?.readingStats !== false" class="reading-stats-card">
        <div class="section-header">
          <h3>TA的阅读统计</h3>
        </div>

        <div class="stats-tabs">
          <button
            v-for="t in periodTabs"
            :key="t.key"
            class="tab-btn"
            :class="{ active: currentPeriod === t.key }"
            @click="currentPeriod = t.key"
          >
            {{ t.label }}
          </button>
        </div>

        <ReadingStats :period="currentPeriod" :stats="statsForPeriod" />
      </div>

      <ReadingHighlights
        v-if="home.visibility?.highlights !== false"
        :highlights="home.highlights"
        owner-label="TA"
        :show-view-all="false"
      />
      <ReadingThoughts
        v-if="home.visibility?.thoughts !== false"
        :thoughts="home.thoughts"
        owner-label="TA"
        :show-view-all="false"
      />
      <ReadingReviews
        v-if="home.visibility?.bookReviews !== false"
        :reviews="home.bookReviews"
        owner-label="TA"
        :show-view-all="false"
      />
    </section>

    <section v-else class="loading-section">
      <el-skeleton :rows="6" animated />
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

import NavBar from '@/components/layout/NavBar.vue'
import Footer from '@/components/layout/Footer.vue'
import PublicUserHeader from '@/components/user/PublicUserHeader.vue'
import ReadingHighlights from '@/components/user/ReadingHighlights.vue'
import ReadingThoughts from '@/components/user/ReadingThoughts.vue'
import ReadingReviews from '@/components/user/ReadingReviews.vue'
import ReadingStats from '@/components/user/ReadingStats.vue'
import BookshelfPreview from '@/components/user/BookshelfPreview.vue'

import { getPublicUserHome } from '@/api/profile'
import type { PublicUserHomeData } from '@/types/user'
import { useTitle } from '@/stores/useTitle'
import { useTheme } from '@/composables/useTheme'

const route = useRoute()
const theme = useTheme()
const cssVars = theme.cssVars

const userId = computed(() => String(route.params.id || ''))
const home = ref<PublicUserHomeData | null>(null)

const periodTabs = [
  { label: '周', key: 'week' },
  { label: '月', key: 'month' },
  { label: '年', key: 'year' },
  { label: '总', key: 'total' },
] as const
const currentPeriod = ref<(typeof periodTabs)[number]['key']>('week')

const statsForPeriod = computed(() => {
  const rs: any = home.value?.readingStats || {}
  const map = {
    week: {
      duration: rs.weeklyReadingTime ?? 0,
      booksRead: rs.weeklyBooksRead ?? 0,
      booksFinished: rs.weeklyBooksFinished ?? 0,
      notes: rs.weeklyNoteCount ?? 0,
    },
    month: {
      duration: rs.monthlyReadingTime ?? 0,
      booksRead: rs.monthlyBooksRead ?? 0,
      booksFinished: rs.monthlyBooksFinished ?? 0,
      notes: rs.monthlyNoteCount ?? 0,
    },
    year: {
      duration: rs.yearlyReadingTime ?? 0,
      booksRead: rs.yearlyBooksRead ?? 0,
      booksFinished: rs.yearlyBooksFinished ?? 0,
      notes: rs.yearlyNoteCount ?? 0,
    },
    total: {
      duration: rs.totalReadingTime ?? 0,
      booksRead: rs.totalBooksRead ?? 0,
      booksFinished: rs.totalBooksFinished ?? 0,
      notes: rs.totalNoteCount ?? 0,
    },
  } as const
  return map[currentPeriod.value] ?? map.week
})

const load = async () => {
  if (!userId.value) return
  try {
    home.value = await getPublicUserHome(userId.value)
  } catch (e: any) {
    console.error('加载他人主页失败:', e)
    ElMessage.error(e?.response?.data?.message || e?.message || '加载失败，请稍后重试')
    home.value = null
  }
}

const handleFollowChange = (isFollowing: boolean) => {
  if (!home.value) return
  home.value.isFollowing = isFollowing
  home.value.followerCount = Math.max(0, home.value.followerCount + (isFollowing ? 1 : -1))
}

onMounted(load)
watch(() => userId.value, load)

useTitle(
  computed(() => {
    const name = home.value?.username
    return name ? `${name} - 个人主页` : '个人主页'
  }),
)
</script>

<style scoped>
.public-profile-page > * {
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.header-section {
  margin-top: 60px;
  margin-bottom: 20px;
}

.content-section {
  padding-bottom: 40px;
}

.recent-books {
  background: var(--card-bg, #fff);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--shadow, 0 4px 12px rgba(0, 0, 0, 0.05));
  margin-top: 20px;
}

.reading-stats-card {
  background: var(--card-bg, #fff);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--shadow, 0 4px 12px rgba(0, 0, 0, 0.05));
  margin-top: 20px;
}

.stats-tabs {
  display: flex;
  gap: 10px;
  margin: 12px 0 16px;
  flex-wrap: wrap;
}

.tab-btn {
  border: 1px solid #eaeaea;
  background: #fff;
  color: var(--text-light, #666);
  padding: 6px 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.15s ease;
}

.tab-btn:hover {
  border-color: var(--primary-green, #42b983);
  color: var(--primary-green, #42b983);
}

.tab-btn.active {
  background: rgba(66, 185, 131, 0.1);
  border-color: rgba(66, 185, 131, 0.3);
  color: var(--primary-green, #42b983);
  font-weight: 700;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-main, #333);
}

.loading-section {
  margin-top: 24px;
  padding: 24px;
  background: var(--card-bg, #fff);
  border-radius: 16px;
}
</style>

