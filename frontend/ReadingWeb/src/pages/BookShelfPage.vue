<script setup lang="ts">
import { ref, computed } from 'vue'
import NavBar from '@/components/layout/NavBar.vue'
import BookCard from '@/components/bookshelf/BookCardMiddle.vue'

// 模拟书籍数据（真实项目中可从接口获取）
const books = ref([
  { id: 1, title: 'Vue 3 进阶实战', cover: 'https://picsum.photos/200/300?1', status: '读完' },
  { id: 2, title: '深入浅出 TypeScript', cover: 'https://picsum.photos/200/300?2', status: '在读' },
  { id: 3, title: '现代前端工程化', cover: 'https://picsum.photos/200/300?3', status: '未读' },
  { id: 4, title: '前端架构设计', cover: 'https://picsum.photos/200/300?4', status: '读完' },
  { id: 5, title: '算法图解', cover: 'https://picsum.photos/200/300?5', status: '在读' },
  { id: 6, title: '计算机网络', cover: 'https://picsum.photos/200/300?6', status: '未读' },
  { id: 7, title: '深入理解浏览器原理', cover: 'https://picsum.photos/200/300?7', status: '在读' },
  { id: 8, title: 'JavaScript 权威指南', cover: 'https://picsum.photos/200/300?8', status: '读完' },
  { id: 9, title: 'React 源码解析', cover: 'https://picsum.photos/200/300?9', status: '未读' },
  { id: 10, title: 'Node.js 实战手册', cover: 'https://picsum.photos/200/300?10', status: '读完' },
  { id: 11, title: 'Python 编程艺术', cover: 'https://picsum.photos/200/300?11', status: '在读' },
  { id: 12, title: '操作系统真相还原', cover: 'https://picsum.photos/200/300?12', status: '未读' },
  { id: 13, title: '数据库系统概念', cover: 'https://picsum.photos/200/300?13', status: '读完' },
  { id: 14, title: 'Web 安全攻防', cover: 'https://picsum.photos/200/300?14', status: '在读' },
  { id: 15, title: 'C++ Primer Plus', cover: 'https://picsum.photos/200/300?15', status: '读完' },
  { id: 16, title: '人工智能导论', cover: 'https://picsum.photos/200/300?16', status: '未读' },
  { id: 17, title: '机器学习实战', cover: 'https://picsum.photos/200/300?17', status: '在读' },
  { id: 18, title: '深度学习入门', cover: 'https://picsum.photos/200/300?18', status: '读完' },
  { id: 19, title: '数据结构与算法分析', cover: 'https://picsum.photos/200/300?19', status: '未读' },
  { id: 20, title: '重构：改善既有代码的设计', cover: 'https://picsum.photos/200/300?20', status: '在读' },
])


// 当前筛选状态
const filterStatus = ref('全部')

// 计算属性：根据筛选状态过滤书籍
const filteredBooks = computed(() => {
  if (filterStatus.value === '全部') return books.value
  return books.value.filter(book => book.status === filterStatus.value)
})

// 切换筛选状态
const setFilter = (status: string) => {
  filterStatus.value = status
}
</script>

<template>
  <div class="shelf">
    <NavBar />

    <div class="bookshelf">
      <div class="shelf-header">
        <!-- 筛选按钮 -->
        <div class="filter-bar">
          <button
            v-for="status in ['全部', '未读', '在读', '读完']"
            :key="status"
            :class="['filter-btn', { active: filterStatus === status }]"
            @click="setFilter(status)"
          >
            {{ status }}
          </button>
        </div>
    </div>
      <!-- 书籍展示区 -->
      <div class="shelf-books">
        <BookCard
          v-for="book in filteredBooks"
          :key="book.id"
          :title="book.title"
          :cover="book.cover"
          :isRead="book.status === '读完'"
          :book-id="book.id"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.shelf {
  min-height: 100vh; /* 高度至少为视口高度 */
  background-color: #f5f5f5; /* 柔和的浅灰色，不突兀 */
  display: flex;
  flex-direction: column;
}
.bookshelf {
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  gap: 40px;
  box-sizing: border-box;
}
.shelf-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 100px 100px 20px 100px; /* 与原来保持一致的边距 */
}

.filter-bar {
  display: flex;
  gap: 12px;
}

.filter-btn {
  background: transparent;
  border: 1px solid #aaa;
  padding: 6px 14px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  color: #555;
}

.filter-btn:hover {
  border-color: #333;
  color: #000;
}

.filter-btn.active {
  background-color: #333;
  color: #fff;
  border-color: #333;
}

.shelf-books {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  padding: 0px 100px 100px 100px;
}
</style>
