<script setup lang="ts">
import { ref, computed } from 'vue'
import NavBar from '@/components/layout/NavBar.vue'
import BookCard from '@/components/bookshelf/BookCardMiddle.vue'
import { ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import Footer from '@/components/layout/Footer.vue'

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
  {
    id: 19,
    title: '数据结构与算法分析',
    cover: 'https://picsum.photos/200/300?19',
    status: '未读',
  },
  {
    id: 20,
    title: '重构：改善既有代码的设计',
    cover: 'https://picsum.photos/200/300?20',
    status: '在读',
  },
])

const filterStatus = ref('全部')
const selectedBookIds = ref<number[]>([])
const isDeleteMode = ref(false)

const filteredBooks = computed(() => {
  if (filterStatus.value === '全部') return books.value
  return books.value.filter((book) => book.status === filterStatus.value)
})

const setFilter = (status: string) => {
  filterStatus.value = status
  selectedBookIds.value = []
}

const toggleDeleteMode = () => {
  isDeleteMode.value = !isDeleteMode.value
  selectedBookIds.value = []
}

const toggleBookSelect = (bookId: number) => {
  const index = selectedBookIds.value.findIndex((id) => id === bookId)
  index > -1 ? selectedBookIds.value.splice(index, 1) : selectedBookIds.value.push(bookId)
}

const deleteSelectedBooks = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要移除选中的 ${selectedBookIds.value.length} 本书吗？`,
      '删除确认',
      { confirmButtonText: '确认删除', cancelButtonText: '取消', type: 'warning' },
    )
    books.value = books.value.filter((book) => !selectedBookIds.value.includes(book.id))
    selectedBookIds.value = []
  } catch (error) {}
}
</script>

<template>
  <div class="shelf">
    <NavBar />
    <div class="bookshelf">
      <div class="shelf-header">
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

        <div class="delete-mode-controls">
          <button
            class="delete-mode-btn"
            @click="toggleDeleteMode"
            :class="{ active: isDeleteMode }"
          >
            <el-icon v-if="!isDeleteMode"><Delete /></el-icon>
            <span v-if="isDeleteMode">取消删除</span>
            <span v-else>批量删除</span>
          </button>

          <button
            class="delete-selected-btn"
            @click="deleteSelectedBooks"
            v-if="isDeleteMode && selectedBookIds.length > 0"
          >
            <el-icon><Delete /></el-icon>
            删除选中 ({{ selectedBookIds.length }})
          </button>
        </div>
      </div>

      <div class="shelf-books">
        <div class="book-item" v-for="book in filteredBooks" :key="book.id">
          <input
            type="checkbox"
            class="book-checkbox"
            :checked="selectedBookIds.includes(book.id)"
            @change="toggleBookSelect(book.id)"
            @click.stop
            v-if="isDeleteMode"
          />
          <!-- 关键：传递disableJump prop，删除模式下禁用跳转 -->
          <BookCard
            :title="book.title"
            :cover="book.cover"
            :isRead="book.status === '读完'"
            :book-id="book.id"
            :disable-jump="isDeleteMode"
          />
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<style scoped>
.shelf {
  min-height: 100vh;
  background-color: #f5f5f5;
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
  margin: 100px 100px 20px 100px;
  flex-wrap: wrap;
  gap: 12px;
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
  border-color: var(--dark-back);
  color: #000;
}

.filter-btn.active {
  background-color: var(--dark-back);
  color: var(--sun-back);
  border-color: var(--dark-back);
}

.delete-mode-controls {
  display: flex;
  gap: 12px;
  align-items: center;
}

.delete-mode-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background-color: var(--sun-back);
  border: 1px solid var(--primary-green);
  color: var(--primary-green);
  border-radius: 10px;
  padding: 6px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.delete-mode-btn:hover {
  background-color: var(--shadow-green);
}

.delete-mode-btn.active {
  background-color: var(--primary-green);
  color: var(--sun-back);
}

.delete-mode-btn.active:hover {
  background-color: var(--primary-green);
}

.delete-selected-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background-color: var(--primary-green);
  color: var(--sun-back);
  border: none;
  border-radius: 10px;
  padding: 6px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.delete-selected-btn:hover {
  background-color: var(--primary-green);
  transform: scale(1.05);
}

.shelf-books {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  padding: 0 100px 100px 100px;
}

.book-item {
  position: relative;
  box-sizing: border-box;
}

.book-checkbox {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 18px;
  height: 18px;
  accent-color: var(--thrid-green);
  cursor: pointer;
  z-index: 20;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  transition: all 0.2s ease;
}

.book-checkbox:hover {
  border-color: var(--thrid-green);
  box-shadow: 0 0 0 2px var(--shadow-green);
}

.book-item:has(.book-checkbox:checked) .book-card {
  /* 将边框改为内部阴影，避免改变元素尺寸 */
  box-shadow:
    0 0 0 2px var(--shadow-green),
    inset 0 0 0 1px var(--thrid-green);
  border: none; /* 移除边框 */
  overflow: visible;
}

/* 删除模式下，卡片鼠标样式改为默认（非指针） */
.book-item:has(.book-checkbox) .book-card {
  cursor: default;
}

@media (max-width: 768px) {
  .shelf-header {
    margin: 60px 20px 20px 20px;
  }
  .shelf-books {
    padding: 0 20px 60px 20px;
  }
}
</style>
