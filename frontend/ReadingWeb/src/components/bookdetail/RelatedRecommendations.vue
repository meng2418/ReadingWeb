<template>
  <div class="related-recommendations">
    <!-- 组件标题和换一批按钮 -->
    <div class="recommendations-header">
      <h3 class="section-title">相关推荐作品</h3>
      <button class="refresh-button" @click="refreshRecommendations">换一批</button>
    </div>

    <!-- 推荐作品列表 -->
    <div class="recommendations-list">
      <div
        v-for="book in displayedBooks"
        :key="book.id"
        class="recommendation-item"
        @click.stop="goToBookDetail(book)"
      >
        <div class="book-cover">
          <img v-if="book.cover" :src="book.cover" :alt="book.title" @error="handleImageError" />
          <div v-else class="cover-placeholder">
            <span>书籍封面</span>
          </div>
        </div>
        <div class="book-info">
          <div class="book-title">{{ book.title }}</div>
          <div class="book-intro">{{ book.intro }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useBookNavigation } from '@/composables/useBookNavigation'
import type { BookListItem } from '@/types/book'

// 定义组件属性
interface Props {
  books: BookListItem[]
  maxDisplayCount?: number
}

// 定义组件事件
interface Emits {
  (e: 'bookSelect', book: BookListItem): void
  (e: 'refresh'): void
}

// 接收属性
const props = withDefaults(defineProps<Props>(), {
  maxDisplayCount: 3,
})

// 定义事件
const emit = defineEmits<Emits>()

// 响应式数据
const currentIndex = ref(0)

const { openInNewTab } = useBookNavigation()

// 计算属性
const displayedBooks = computed(() => {
  const start = currentIndex.value
  const end = start + props.maxDisplayCount
  return props.books.slice(start, end)
})

// 方法
const refreshRecommendations = () => {
  // 循环展示推荐书籍
  currentIndex.value = (currentIndex.value + props.maxDisplayCount) % props.books.length
  emit('refresh')
}

// 跳转到书籍详情页
const goToBookDetail = (book: BookListItem): void => {
  console.log(`选择了书籍: ${book.title}`)

  // 触发事件通知父组件
  emit('bookSelect', book)

  // 在新标签页打开书籍详情页
  // 优先使用 book.bookId，如果不存在则用 book.id
  const bookId = book.bookId || book.id

  if (bookId) {
    openInNewTab(bookId)
  } else {
    openInNewTab()
  }
}

// 图片加载失败处理
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
}

// 生命周期
onMounted(() => {
  console.log('相关推荐作品组件已加载')
})
</script>

<style scoped>
.related-recommendations {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
  width: 300px;
  height: fit-content;
}

/* 标题和按钮区域 */
.recommendations-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

/* 换一批按钮样式 */
.refresh-button {
  padding: 6px 12px;
  background: transparent;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  color: #666;
  font-size: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.refresh-button:hover {
  background: #f5f5f5;
  border-color: #ccc;
  color: #333;
}

/* 推荐作品列表样式 */
.recommendations-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.recommendation-item {
  display: flex;
  gap: 12px;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.recommendation-item:hover {
  background: #f9f9f9;
  transform: translateY(-2px);
}

/* 书籍封面样式 */
.book-cover {
  flex-shrink: 0;
  width: 80px;
  height: 117px;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.book-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 16px;
}

/* 书籍信息样式 */
.book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.book-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
  line-height: 1.3;
}

.book-intro {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .related-recommendations {
    width: 100%;
    padding: 15px;
  }

  .section-title {
    font-size: 16px;
  }

  .refresh-button {
    font-size: 15px;
    padding: 5px 10px;
  }

  .book-cover {
    width: 45px;
    height: 63px;
  }

  .book-title {
    font-size: 13px;
  }

  .book-intro {
    font-size: 11px;
  }
}
</style>
