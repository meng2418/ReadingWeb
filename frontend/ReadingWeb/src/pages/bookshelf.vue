<script setup lang="ts">
import NavBar from '../components/NavBar.vue'
import BookCard from '../components/BookCardMiddle.vue'
</script>

<template>
  <div class="shelf">
    <div class="bar">
      <NavBar />
    </div>
    <div class="bookshelf">
      <h1 class="shelf-title">我的书架</h1>
      <div class="shelf-books">
        <BookCard v-for="(book, index) in 10" :key="index" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.bookshelf {
  flex-direction: column;
  gap: 24px;
}
.shelf-title {
  font-family: 'SimSun', '宋体', serif;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  margin-top: 100px;
  margin-left: 100px;
}
.shelf-books {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  padding: 0px 100px 100px 100px;
}
</style>
