<script setup lang="ts">
import NavBar from '@/components/layout/NavBar.vue'
import GuessYouLike from '@/components/home/GuessYouLike.vue'
import BookRankList from '@/components/home/BookRankList.vue'
import BackToTop from '@/components/layout/BackToTop.vue'
import ReadingTimeCard from '@/components/home/ReadingTimeCard.vue'
import RecentRead from '@/components/home/RecentRead.vue'
import { onMounted } from 'vue'
import axios from 'axios'

onMounted(async () => {
  try {
    const res = await axios.get('http://localhost:3000/api/home')
    console.log('首页接口响应：', res.data)
  } catch (err) {
    console.error('请求出错：', err)
  }
})
</script>

<template>
  <div class="home">
    <div class="app">
      <NavBar />
    </div>
    <div class="spacer">
      <div class="reading-time-card">
        <ReadingTimeCard />
        <RecentRead />
      </div>
      <div class="guss-you-like">
        <GuessYouLike />
      </div>
      <div class="book-rank">
        <!-- 周榜 -->
        <BookRankList
          ranktitle="周榜"
          desc="最近一周热读书籍"
          tabId="weekly"
        />

        <!-- 月榜 -->
        <BookRankList
          ranktitle="月榜"
          desc="最近一月热读书籍"
          tabId="monthly"
        />

        <!-- 新书榜 -->
        <BookRankList
          ranktitle="新书榜"
          desc="最新上架书籍"
          tabId="new"
        />

<!-- 神作榜 -->
<BookRankList
  ranktitle="神作榜"
  desc="经典神作书籍"
  tabId="masterpiece"
/>
      </div>
    </div>
    <BackToTop />
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
