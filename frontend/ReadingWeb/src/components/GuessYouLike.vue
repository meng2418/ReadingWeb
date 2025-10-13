<!-- GuessYouLike.vue -->
<template>
  <div class="guess-you-like">
    <div class="header">
      <h2>猜你想看</h2>
      <button @click="fetchBooks" class="change-btn">换一批</button>
    </div>
    <div class="book-cards-container">
      <BookCard
        v-for="(book, index) in books"
        :key="index"
        :cover="book.cover"
        :title="book.title"
        :author="book.author"
        :reason="book.reason"
      />
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'
import BookCard from './BookCardBig.vue'

const books = [
  { cover: 'https://picsum.photos/200', title: '书名1', author: '作者1', reason: '推荐理由1' },
  { cover: 'https://picsum.photos/201', title: '书名2', author: '作者2', reason: '推荐理由2' },
  { cover: 'https://picsum.photos/202', title: '书名3', author: '作者3', reason: '推荐理由3' },
  { cover: 'https://picsum.photos/203', title: '书名4', author: '作者4', reason: '推荐理由4' },
  { cover: 'https://picsum.photos/204', title: '书名5', author: '作者5', reason: '推荐理由5' },
  { cover: 'https://picsum.photos/205', title: '书名6', author: '作者6', reason: '推荐理由6' },
]
</script>

<style scoped>
.guess-you-like {
  padding: 20px 20px 0 20px;
}
.header {
  display: flex;
  justify-content: space-between; /* 让标题和按钮分开 */
  align-items: center; /* 垂直居中 */
}
h2 {
  font-size: 24px;
}

.book-cards-container {
  display: flex;
  gap: 26px;
}

.change-btn {
  background: none;
  border: 1px solid #007c27;
  color: #000;
  padding: 6px 14px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.change-btn:hover {
  color: white;
  box-shadow: inset 0 -100px 0px 0 #1ad6a1;
}
</style>
