<template>
  <div class="recent-read">
    <div class="cover-list">
      <!-- 遍历计算后的 displayBooks -->
      <div 
        v-for="(book, index) in displayBooks" 
        :key="book.bookId || index" 
        class="cover-item"
        @click="handleBookClick(book)"
      >
        <!-- 如果有封面则显示图片 -->
        <img v-if="book.cover" :src="book.cover" :alt="book.title" class="cover-img" />
        <!-- 如果没有封面则显示白色占位块 -->
        <div v-else class="cover-placeholder"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'

interface RecentBook {
  bookId: number
  title: string
  cover: string
}

const router = useRouter()

const props = defineProps<{
  books: RecentBook[]
}>()

// --- 核心逻辑：确保数组长度始终为 4 ---
const displayBooks = computed(() => {
  const maxCount = 4
  const result = [...props.books].slice(0, maxCount) // 取前4本

  // 如果不足4本，用空对象补齐
  while (result.length < maxCount) {
    result.push({
      bookId: 0,
      title: '',
      cover: '',
    })
  }
  return result
})

const handleBookClick = (book: RecentBook) => {
  // 如果是空占位（bookId为0），不处理
  if (!book.bookId || book.bookId === 0) {
    return
  }
  
  // 跳转到阅读器页面
  router.push({
    name: 'ReaderPage',
    params: {
      bookId: book.bookId,
      chapterId: 1 // 默认跳转到第一章
    }
  })
}
</script>

<style scoped>
.recent-read {
  background: transparent;
  overflow: hidden;
  height: 220px;
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
  /* 增加边框或投影，防止纯白占位在亮色背景下“隐身”（可选） */
  border: 1px solid #f0f0f0;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  border-radius: 4px;
}

/* 新增：白色占位块样式 */
.cover-placeholder {
  width: 100%;
  height: 100%;
  background-color: #ffffff;
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
