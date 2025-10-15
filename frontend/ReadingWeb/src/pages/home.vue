<script setup lang="ts">
import NavBar from '../components/NavBar.vue'
import GuessYouLike from '../components/GuessYouLike.vue'
import BookRankList from '../components/BookRankList.vue'
import BackToTop from '@/components/BackToTop.vue'
import ReadingTimeCard from '@/components/ReadingTimeCard.vue'
</script>

<template>
  <div class="Home">
    <div class="app">
      <NavBar />
    </div>
    <div class="reading-time-card">
      <ReadingTimeCard />
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
    <BackToTop />
  </div>
</template>

<style scoped>
.Home {
  display: flex;
  flex-direction: column;
  background-color: rgb(241, 241, 241);
  gap: 24px;
}
.reading-time-card {
  display: flex;
  justify-content: left;
  padding: 100px 100px 20px 100px;
}
.guss-you-like {
  display: flex;
  flex-wrap: wrap;
  padding: 0 82px 82px 82px;
}
.book-rank {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  padding: 0 100px;
}
</style>
