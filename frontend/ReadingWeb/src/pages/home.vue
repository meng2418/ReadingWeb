<script setup lang="ts">
import NavBar from '../components/NavBar.vue'
import GuessYouLike from '../components/GuessYouLike.vue'
import BookRankList from '../components/BookRankList.vue'
</script>

<template>
  <div class="Home">
    <div class="app">
      <NavBar />
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
</template>

<style scoped>
.Home {
  display: flex;
  flex-direction: column;
  background-color: rgb(241, 241, 241);
  gap: 24px;
}
.guss-you-like {
  display: flex;
  flex-wrap: wrap;
  padding: 82px;
}
.book-rank {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  padding: 0 100px;
}
</style>
