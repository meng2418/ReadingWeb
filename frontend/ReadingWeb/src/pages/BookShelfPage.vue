<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import NavBar from '@/components/layout/NavBar.vue'
import BookCard from '@/components/bookshelf/BookCardMiddle.vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { getBookshelfAll, batchRemoveBooks, type ShelfBook } from '@/api/bookshelf'

// 全部书籍（只请求一次）
const books = ref<ShelfBook[]>([])

// 当前筛选状态
const filterStatus = ref<'全部' | '未读' | '在读' | '读完'>('全部')

const selectedBookIds = ref<Array<string | number>>([])
const isDeleteMode = ref(false)
const loading = ref(false)

// 前端筛选
const filteredBooks = computed(() => {
  if (filterStatus.value === '全部') return books.value
  return books.value.filter((book) => book.status === filterStatus.value)
})

// 只在页面初始化时请求一次
const loadBooks = async () => {
  loading.value = true
  try {
    books.value = await getBookshelfAll()
    selectedBookIds.value = []
  } finally {
    loading.value = false
  }
}

const statuses: Array<'全部' | '未读' | '在读' | '读完'> = ['全部', '未读', '在读', '读完']

// 只切换状态，不请求接口
const setFilter = (status: '全部' | '未读' | '在读' | '读完') => {
  filterStatus.value = status
  selectedBookIds.value = []
}

const toggleDeleteMode = () => {
  isDeleteMode.value = !isDeleteMode.value
  selectedBookIds.value = []
}

const toggleBookSelect = (bookId: string | number) => {
  const index = selectedBookIds.value.findIndex((id) => id === bookId)
  index > -1 ? selectedBookIds.value.splice(index, 1) : selectedBookIds.value.push(bookId)
}

const deleteSelectedBooks = async () => {
  if (selectedBookIds.value.length === 0) return
  try {
    // 1. 二次确认
    await ElMessageBox.confirm(
      `确定要将选中的 ${selectedBookIds.value.length} 本书从书架移除吗？`,
      '批量删除',
      {
        confirmButtonText: '确定移除',
        cancelButtonText: '取消',
        type: 'warning',
      },
    )
    // 2. 开启 Loading 并调用接口
    loading.value = true
    await batchRemoveBooks(selectedBookIds.value)
    // 3. 后端删除成功后，前端同步更新（不用刷新页面）
    books.value = books.value.filter((book) => !selectedBookIds.value.includes(book.id))
    // 4. 重置状态
    selectedBookIds.value = []
    isDeleteMode.value = false
    ElMessage.success('移除成功')
  } catch (error) {
    // 捕获用户取消和接口报错
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('操作失败，请重试')
    }
  } finally {
    loading.value = false
  }
}

onMounted(loadBooks)
</script>

<template>
  <div class="shelf">
    <NavBar />
    <div class="bookshelf">
      <div class="shelf-header">
        <div class="filter-bar">
          <button
            v-for="status in statuses"
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
