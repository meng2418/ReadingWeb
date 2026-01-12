<script setup lang="ts">
import { ref, onMounted } from 'vue'

import NavBar from '@/components/layout/NavBar.vue'
import GuessYouLike from '@/components/home/GuessYouLike.vue'
import BookRankList from '@/components/home/BookRankList.vue'
import BackToTop from '@/components/layout/BackToTop.vue'
import ReadingTimeCard from '@/components/home/ReadingTimeCard.vue'
import RecentRead from '@/components/home/RecentRead.vue'
import Footer from '@/components/layout/Footer.vue'

import { getHomeData, getGuessBooks } from '@/api/home'
import type { GuessBook, RankBook, RecentBook } from '@/types/book'

// ===== 状态 =====
const recentBooks = ref<RecentBook[]>([])
const guessBooks = ref<GuessBook[]>([])
const weeklyRank = ref<RankBook[]>([])
const monthlyRank = ref<RankBook[]>([])
const newRank = ref<RankBook[]>([])
const masterpieceRank = ref<RankBook[]>([])
const isRefreshing = ref(false) // 添加加载状态

onMounted(async () => {
  const data = await getHomeData()

  recentBooks.value = data.recentBooks
  guessBooks.value = data.guessBooks
  weeklyRank.value = data.rankWeekly
  monthlyRank.value = data.rankMonthly
  newRank.value = data.rankNew
  masterpieceRank.value = data.rankMasterpiece
})

const refreshGuessBooks = async () => {
  // 防止重复点击
  if (isRefreshing.value) {
    return
  }

  try {
    isRefreshing.value = true
    const list = await getGuessBooks()
    
    // 只有当返回的数据有效且不为空时才更新
    if (Array.isArray(list) && list.length > 0) {
      guessBooks.value = list
    } else {
      console.warn('刷新失败：返回的数据为空或无效', list)
      // 保持原有数据不变，不显示错误提示，避免影响用户体验
    }
  } catch (error) {
    console.error('刷新猜你喜欢失败:', error)
    // 请求失败时保持原有数据不变
  } finally {
    isRefreshing.value = false
  }
}
</script>

<template>
  <div class="home">
    <div class="app">
      <NavBar />
    </div>
    <div class="spacer">
      <div class="reading-time-card">
        <ReadingTimeCard />
        <RecentRead :books="recentBooks" />
      </div>
      <div class="guss-you-like">
        <GuessYouLike :books="guessBooks" :is-refreshing="isRefreshing" @refresh="refreshGuessBooks" />
      </div>
      <div class="book-rank">
        <!-- 周榜 -->
        <BookRankList ranktitle="周榜" desc="最近一周热读书籍" tabId="weekly" :books="weeklyRank" />

        <!-- 月榜 -->
        <BookRankList
          ranktitle="月榜"
          desc="最近一月热读书籍"
          tabId="monthly"
          :books="monthlyRank"
        />

        <!-- 新书榜 -->
        <BookRankList ranktitle="新书榜" desc="最新上架书籍" tabId="new" :books="newRank" />

        <!-- 神作榜 -->
        <BookRankList
          ranktitle="神作榜"
          desc="经典神作书籍"
          tabId="masterpiece"
          :books="masterpieceRank"
        />
      </div>
    </div>
    <BackToTop />
    <Footer />
  </div>
</template>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
  gap: 24px;
}
.spacer {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  gap: 40px;
  box-sizing: border-box;
}

.reading-time-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 100px 5vw 0px 5vw;
  gap: 40px;
}

.guss-you-like {
  display: flex;
  flex-wrap: wrap;
  justify-content: center; /* 中间对齐 */
  padding: 0 5vw 2vw 5vw;
}

.book-rank {
  display: flex;
  flex-wrap: wrap;
  justify-content: center; /* 中间对齐 */
  gap: 12px;
  padding: 0 5vw;
}
</style>
