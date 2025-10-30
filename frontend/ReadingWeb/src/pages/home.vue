<script setup lang="ts">
import NavBar from '../components/NavBar.vue'
import GuessYouLike from '../components/GuessYouLike.vue'
import BookRankList from '../components/BookRankList.vue'
import BackToTop from '@/components/BackToTop.vue'
import ReadingTimeCard from '@/components/ReadingTimeCard.vue'
import RecentRead from '@/components/RecentRead.vue'
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
        <BookRankList ranktitle="周榜" desc="最近一周热读书籍" />
        <BookRankList ranktitle="月榜" desc="最近一个月最受欢迎" />
        <BookRankList ranktitle="新书榜" desc="最新上架的优质书籍" />
        <BookRankList ranktitle="神作榜" desc="公认必读的高分神作" />
      </div>
    </div>
    <BackToTop />
  </div>
</template>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  background-color:#f5f5f5;
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
