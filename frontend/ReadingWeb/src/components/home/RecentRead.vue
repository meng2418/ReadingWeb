<template>
  <div class="recent-read">
    <div class="cover-list">
      <div v-for="book in books" :key="book.id" class="cover-item">
        <img :src="book.cover" :alt="book.title" class="cover-img" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface RecentBook {
  id: number | string
  title: string
  cover: string
}

const props = defineProps<{
  books: RecentBook[]
}>()
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
