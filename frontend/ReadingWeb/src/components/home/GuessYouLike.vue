<template>
  <div class="guess-you-like">
    <div class="header">
      <h2>猜你想看</h2>
      <button @click="emit('change')" class="change-btn">换一批</button>
    </div>

    <div class="book-cards-container">
      <BookCard
        v-for="(book, index) in books"
        :key="book.id || index"
        :cover="book.cover"
        :title="book.title"
        :author="book.author"
        :reason="book.reason"
        :book-id="book.id"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import BookCard from '@/components/home/BookCardBig.vue'
import type { BookListItem } from '@/types/book'

type GuessBook = BookListItem & {
  id: number
  cover: string
  author: string
  reason: string
}

const props = defineProps<{
  books: GuessBook[]
}>()

const emit = defineEmits<{
  (e: 'change'): void
}>()
</script>

<style scoped>
.guess-you-like {
  padding: 20px 20px 0 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h2 {
  font-size: 24px;
}

.book-cards-container {
  display: flex;
  gap: 26px;
  flex-wrap: wrap;
  margin-top: 12px;
  justify-content: space-between; /* 让6本平均分布 */
  flex-wrap: nowrap; /* 不换行 */
  overflow: hidden; /* 超出隐藏 */
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
