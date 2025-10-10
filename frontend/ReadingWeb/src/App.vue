<script setup lang="ts">
import NavBar from './components/NavBar.vue'
import BookCard from './components/BookCardBig.vue'
</script>

<template>
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
    <BookCard cover="../src/img/cover.jpg" title="三体" author="刘慈欣" reason="硬核科幻巅峰之作" />
  </div>
</template>

<style scoped>
#app {
  font-family: 'Helvetica Neue', Arial, sans-serif;
  padding: 16px;
}
.book-list {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  padding: 100px;
}
</style>
