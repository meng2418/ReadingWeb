<script setup lang="ts">
import NavBar from '../components/NavBar.vue'
import BookCard from '../components/BookCardBig.vue'
import BookRankList from '../components/BookRankList.vue'
</script>

<template>
  <div class="Home">
    <div id="app">
      <NavBar />
    </div>
    <div class="book-list">
      <BookCard
        cover="../src/img/cover.jpg"
        title="活着"
        author="余华"
        reason="一部关于生命与苦难的经典小说"
      />
      <BookCard
        cover="../src/img/cover.jpg"
        title="三体"
        author="刘慈欣"
        reason="中国本土的硬核科幻巅峰之作"
      />
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
#home {
  background-color: rgb(255, 255, 255);
}
.book-list {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  padding: 100px;
}
.book-rank {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  padding: 100px;
}
</style>
