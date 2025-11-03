<template>
  <div class="recent-read">
    <div class="cover-list">
      <div
        v-for="book in recentBooks"
        :key="book.id"
        class="cover-item"
      >
        <img :src="book.cover" :alt="book.title" class="cover-img" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// 临时模拟最近阅读数据
const recentBooks = ref([
  { id: 1, title: '书籍1', cover: 'https://picsum.photos/seed/book1/200/300' },
  { id: 2, title: '书籍2', cover: 'https://picsum.photos/seed/book2/200/300' },
  { id: 3, title: '书籍3', cover: 'https://picsum.photos/seed/book3/200/300' },
  { id: 4, title: '书籍4', cover: 'https://picsum.photos/seed/book4/200/300' },
])
</script>

<style scoped>
.recent-read {
  background: transparent; /* 背景透明 */
  overflow: hidden; /* 超出部分不显示 */
  height: 220px; /* 保持与 ReadingTimeCard 一致的高度 */
  display: flex;
  align-items: center;
}

.cover-list {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 20px;
  overflow: hidden;
}

.cover-item {
  flex-shrink: 0;
  width: 150px;
  height: 200px;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  border-radius: 4px;
}

/* 响应式微调 */
@media (max-width: 768px) {
  .recent-read {
    height: 180px;
  }

  .cover-item {
    width: 120px;
    height: 160px;
  }
}
</style>
