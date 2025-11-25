<template>
  <!-- 阅读最久榜单 -->
  <div class="longest-read">
    <h3>阅读最久</h3>

    <div class="book-item" v-for="(book, i) in topBooks" :key="i">
      <div class="rank-num">{{ i + 1 }}</div>
      <img :src="book.cover" alt="book cover" class="book-cover" />

      <div class="book-info">
        <div class="book-name">{{ book.name }}</div>
        <div class="read-time">{{ book.time }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
/* ----------------------------
   3. 阅读最久榜单（测试数据）
----------------------------- */
import defaultCover from '@/img/cover.jpg'
const topBooks = [
  { cover: defaultCover, name: '不能承受的生命之轻', time: '120分钟' },
  { cover: defaultCover, name: '百年孤独', time: '98分钟' },
  { cover: defaultCover, name: '小王子', time: '80分钟' },
]
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
