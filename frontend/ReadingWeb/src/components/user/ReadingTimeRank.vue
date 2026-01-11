<!--ReadingTimeRank.vue-->
<template>
  <!-- 阅读最久榜单 -->
  <div class="longest-read">
    <h3>阅读最久</h3>

    <div 
      class="book-item" 
      v-for="(book, i) in topBooks" 
      :key="i"
      @click="handleBookClick(book)"
    >
      <div class="rank-num">{{ i + 1 }}</div>
      <img :src="book.cover" alt="book cover" class="book-cover" />

      <div class="book-info">
        <div class="book-name">{{ book.title }}</div>
        <div class="read-time">{{ book.readingTime }}分钟</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import type { TopBook } from '@/types/user'

const router = useRouter()

const props = defineProps({
  topBooks: { type: Array as () => TopBook[], default: () => [] },
})

const handleBookClick = (book: TopBook) => {
  if (book.bookId) {
    router.push({
      path: '/bookdetail',
      query: { id: book.bookId }
    })
  } else {
    console.warn('书籍bookId不存在，无法跳转', book)
  }
}
</script>

<style scoped>
/* 阅读最久排行榜 --------------------------------------------------- */
.longest-read {
  padding: 20px;
}

.longest-read h3 {
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: 600;
}

/* 单条 */
.book-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.book-item:hover {
  opacity: 0.8;
}

.rank-num {
  color: var(--primary-green);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-style: italic;
  margin-right: 16px;
}

.book-cover {
  width: 36px;
  height: 48px;
  background: #ddd;
  border-radius: 6px;
  margin-right: 12px;
}

.book-info .book-name {
  font-size: 14px;
  font-weight: 500;
}
.book-info .read-time {
  font-size: 12px;
  color: #666;
  margin-top: 2px;
}
</style>
