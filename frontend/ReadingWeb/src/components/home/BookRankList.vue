<!--BookRankList.vue -->
<template>
  <div class="book-rank-wrapper">
    <!-- 榜单标题部分 -->
    <div class="book-rank-header">
      <div class="header-left">
        <h2 class="rank-title">{{ ranktitle }}</h2>
        <span class="rank-desc">{{ desc }}</span>
      </div>
      <button class="view-all-btn" @click="handleViewAll">查看全部</button>
    </div>

    <!-- 榜单内容部分 -->
    <div class="book-rank-list">
      <div
        class="book-rank-list-li"
        v-for="(book, index) in books"
        :key="book.id || index"
        @click="handleBookClick(book.id)"
      >
        <img :src="book.cover" alt="book cover" class="book-cover" />
        <div class="book-details">
          <i class="book-rank">{{ index + 1 }}</i>
          <div class="book-info">
            <h3 class="book-title">{{ book.title }}</h3>
            <p class="book-author">{{ book.author }}</p>
            <p class="book-recommend">推荐值： {{ book.recommend }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useBookNavigation } from '@/composables/useBookNavigation'
import type { BookListItem } from '@/types/book'

const router = useRouter()
const { openBookDetail } = useBookNavigation()

// 定义组件 Props 接口
interface Props {
  ranktitle?: string
  desc?: string
  books?: BookListItem[]
  tabId?: string // 用于指定跳转到哪个榜单
  openInNewTab?: boolean // 新增：是否在新标签页打开
}
// 使用解构，直接在解构中定义默认值（books 改为由父组件传入）
const {
  ranktitle,
  desc = '最近一周热读书籍', // 直接赋值默认字符串
  tabId,
  openInNewTab = true, // 默认在新标签页打开
  books = [],
} = defineProps<Props>()

// 查看全部点击事件 - 在当前页跳转
const handleViewAll = (): void => {
  router.push({
    path: '/category',
    query: {
      tab: tabId ?? 'weekly',
      subTab: 'all',
    },
  })
}

// 书籍点击事件 - 跳转到书籍详情页
const handleBookClick = (bookId?: number | string): void => {
  openBookDetail(bookId, openInNewTab ? 'new-tab' : 'current')
}
</script>

<style scoped>
.book-rank-wrapper {
  background-color: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(150, 150, 150, 0.05);
  margin-bottom: 50px;
}

/* 顶部标题栏 */
.book-rank-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.header-left {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.rank-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.rank-desc {
  font-size: 14px;
  color: #888;
}

/* 查看全部按钮 */
.view-all-btn {
  background: none;
  border: none;
  color: #888;
  font-size: 14px;
  cursor: pointer;
  transition: color 0.2s ease;
}

.view-all-btn:hover {
  color: #333;
}

/* 榜单主体 */
.book-rank-list {
  display: flex;
  gap: 12px;
  padding: 8px 0;
  overflow-x: auto;
}

.book-rank-list-li {
  width: 120px;
  height: 230px;
  background-color: #ffffff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  border-radius: 10px;
  overflow: hidden;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
  cursor: pointer;
  flex-shrink: 0;
}

.book-cover {
  width: 100%;
  height: 160px;
  border-radius: 10px;
  object-fit: cover;
  margin-bottom: 6px;
}

.book-details {
  display: flex;
  align-items: flex-start;
}

.book-rank {
  font-size: 40px;
  color: #666;
  margin-left: 4px;
  margin-right: 6px;
  line-height: 1;
}

.book-info {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
  line-height: 1.4;
  margin-left: 6px;
  margin-right: 8px;
}

.book-title {
  font-weight: bold;
  font-size: 14px;
  margin: 0 0 2px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-author {
  color: #666;
  font-size: 12px;
  margin: 0 0 2px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-recommend {
  color: #999;
  font-size: 10px;
  margin: 0;
}

.book-rank-list-li:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 14px rgba(0, 0, 0, 0.15);
}
</style>
