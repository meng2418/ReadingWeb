<script setup lang="ts">
import { ref, computed } from 'vue'
import NavBar from '../components/NavBar.vue'
import BookCard from '../components/BookCardMiddle.vue'

// 模拟书籍数据（真实项目中可从接口获取）
const books = ref([
  { id: 1, title: 'Vue 3 进阶实战', cover: 'https://picsum.photos/200/300?1', status: '读完' },
  { id: 2, title: '深入浅出 TypeScript', cover: 'https://picsum.photos/200/300?2', status: '在读' },
  { id: 3, title: '现代前端工程化', cover: 'https://picsum.photos/200/300?3', status: '未读' },
  { id: 4, title: '前端架构设计', cover: 'https://picsum.photos/200/300?4', status: '读完' },
  { id: 5, title: '算法图解', cover: 'https://picsum.photos/200/300?5', status: '在读' },
  { id: 6, title: '计算机网络', cover: 'https://picsum.photos/200/300?6', status: '未读' },
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
