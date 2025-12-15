<!-- views/AllReadingNotes.vue -->
<template>
  <div class="page-wrapper">
    <div class="content-container">
      <!-- 顶部保持不变... -->
      <header class="toolbar">
        <div class="left-action" @click="$router.back()">
          <ArrowLeft class="icon-md" />
          <span class="back-text">返回</span>
        </div>

        <div class="filters">
          <div class="select-wrapper">
            <BookOpen class="select-icon" />
            <select v-model="selectedBook" class="book-select">
              <option value="">全部书籍</option>
              <option v-for="book in bookList" :key="book" :value="book">{{ book }}</option>
            </select>
            <ChevronDown class="arrow-icon" />
          </div>
        </div>
      </header>

      <div class="notes-grid">
        <div v-if="filteredNotes.length === 0" class="empty-state">
          <inbox class="icon-lg" />
          <p>没有找到相关笔记</p>
        </div>

        <div v-for="note in filteredNotes" :key="note.id" class="note-card">
          <div class="card-header">
            <div class="book-badge">
              <Book class="icon-xs" />
              <span>{{ note.bookName }}</span>
            </div>
            <span class="note-date">{{ formatDate(note.date) }}</span>
          </div>

          <div class="card-body">
            <Quote class="quote-icon-bg" />
            <p class="note-text" v-html="highlightText(note.content)"></p>
          </div>

          <div class="card-footer">
            <span class="chapter-info">
              <Bookmark class="icon-xs" v-if="note.chapter" />
              {{ note.chapter || '全书' }}
            </span>

            <div class="action-buttons">
              <!-- 复制按钮：根据状态动态切换图标和颜色 -->
              <button
                class="action-btn"
                :class="{ 'success-btn': copiedId === note.id }"
                title="复制"
                @click="copyNote(note)"
              >
                <!-- 如果当前是复制成功状态，显示 Check，否则显示 Copy -->
                <Check v-if="copiedId === note.id" class="icon-sm" />
                <Copy v-else class="icon-sm" />
                <!-- 可选：显示文字反馈 -->
                <span v-if="copiedId === note.id" class="feedback-text">已复制</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
// 新增 Check 图标
import {
  ArrowLeft,
  Search,
  BookOpen,
  ChevronDown,
  Book,
  Quote,
  Bookmark,
  Copy,
  FilePenLine,
  Inbox,
  Check,
} from 'lucide-vue-next'

const router = useRouter()
const searchQuery = ref('')
const selectedBook = ref('')
const copiedId = ref(null) // 用于记录当前哪个笔记刚刚被复制

// 模拟数据（顺序可以是乱的，前端会自动排）
const allNotes = ref([
  {
    id: 1,
    bookName: '当尼采哭泣',
    date: '2024-04-28',
    chapter: '第四章',
    content: '尼采略略地笑着，“我知道她如何在这点上反应。她对传统婚姻显得并不宽容。”',
  },
  {
    id: 2,
    bookName: '置身事内',
    date: '2024-03-15',
    chapter: '第三章',
    content: '土地金融的本质，是政府将未来的土地收益提前变现。',
  },
  {
    id: 3,
    bookName: '悉达多',
    date: '2024-02-10',
    chapter: '觉醒',
    content: '知识可以传授，但智慧不能。人们可以寻见智慧，在生活中体现智慧。',
  },
  {
    id: 4,
    bookName: '悉达多',
    date: '2024-02-12',
    chapter: '船夫',
    content: '河水在流，它一直在流，永远在流，然而它永远是那条河。',
  },
  {
    id: 5,
    bookName: '三体',
    date: '2023-11-05',
    chapter: '黑暗森林',
    content: '给岁月以文明，而不是给文明以岁月。',
  },
  {
    id: 6,
    bookName: '最新测试',
    date: '2025-12-03',
    chapter: null,
    content: '这条应该是最新的，排在最前面。',
  },
  {
    id: 7,
    bookName: '她既想死，又想去巴黎',
    date: '2025-11-25',
    chapter: '译者序',
    content:
      '你本应拥有另一种命运，值得更优秀的人、更纯粹的爱。我竭尽所能，想要向你证明我的爱意。可你渴望的，恰好是我唯一无法给予的。',
  },
])

const bookList = computed(() => Array.from(new Set(allNotes.value.map((n) => n.bookName))))

// 核心修改：增加排序逻辑 sort((a, b) => new Date(b.date) - new Date(a.date))
const filteredNotes = computed(() => {
  let result = allNotes.value.filter((note) => {
    const matchBook = selectedBook.value ? note.bookName === selectedBook.value : true
    const matchSearch =
      note.content.includes(searchQuery.value) || note.bookName.includes(searchQuery.value)
    return matchBook && matchSearch
  })

  // 按日期降序排序（最新的在前面）
  return result.sort((a, b) => new Date(b.date) - new Date(a.date))
})

const formatDate = (str) => str.replace('-', '.')

const highlightText = (text) => {
  if (!searchQuery.value) return text
  const re = new RegExp(searchQuery.value, 'gi')
  return text.replace(re, (match) => `<span class="highlight">${match}</span>`)
}

// 核心修改：复制功能的反馈逻辑
const copyNote = async (note) => {
  try {
    await navigator.clipboard.writeText(note.content)
    // 视觉反馈
    copiedId.value = note.id
    // 2秒后恢复原状
    setTimeout(() => {
      copiedId.value = null
    }, 2000)
  } catch (err) {
    console.error('Copy failed', err)
    alert('复制失败，请检查浏览器权限')
  }
}
</script>

<style scoped>
/* 保持原有样式，新增以下样式 */

.action-btn {
  /* 原有样式保持 */
  background: none;
  border: none;
  cursor: pointer;
  color: #9ca3af;
  padding: 4px 8px; /* 稍微加宽一点以便显示文字 */
  border-radius: 4px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px; /* 图标和文字的间距 */
}

/* 复制成功时的绿色状态 */
.success-btn {
  color: var(--primary-green, #42b983) !important;
  background-color: rgba(66, 185, 131, 0.1);
}

.feedback-text {
  font-size: 12px;
  font-weight: 500;
}

.page-wrapper {
  background-color: #f5f7fa; /* 浅灰背景，覆盖全屏 */
  min-height: 100vh;
  width: 100%;
  box-sizing: border-box;
  padding-bottom: 40px; /* 底部留白 */
}

.content-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 顶部工具栏 */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: #ffffff;
  padding: 16px 24px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05); /* 极淡的阴影 */
}

.left-action {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--text-main, #333);
  transition: color 0.2s;
}

.left-action:hover {
  color: var(--primary-green, #42b983);
}

.back-text {
  font-weight: 500;
  font-size: 15px;
}

/* 筛选区样式 */
.filters {
  display: flex;
  gap: 16px;
}
/* 自定义 Select 下拉 */
.select-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.select-icon {
  position: absolute;
  left: 12px;
  width: 16px;
  height: 16px;
  color: #888;
  z-index: 1;
}

.arrow-icon {
  position: absolute;
  right: 12px;
  width: 14px;
  height: 14px;
  color: #888;
  pointer-events: none; /* 让点击穿透到 select */
}

.book-select {
  padding: 9px 32px 9px 36px; /* 留出图标位置 */
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: #fff;
  font-size: 14px;
  color: #333;
  outline: none;
  appearance: none; /* 隐藏原生箭头 */
  cursor: pointer;
  min-width: 140px;
  transition: border-color 0.2s;
}

.book-select:focus {
  border-color: var(--primary-green, #42b983);
}

/* 笔记列表 Grid */
.notes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
}

/*
  2. 取消 Hover 悬浮样式
  保持卡片扁平，仅保留基础阴影
*/
.note-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  border: 1px solid #f0f0f0; /* 添加边框增加层次感 */
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.book-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: var(--primary-green, #42b983);
  background: rgba(66, 185, 131, 0.08);
  padding: 6px 10px;
  border-radius: 6px;
}

.note-date {
  font-size: 13px;
  color: #999;
}

.card-body {
  position: relative;
  flex: 1;
  margin-bottom: 24px;
}

/* 背景水印引用图标 */
.quote-icon-bg {
  position: absolute;
  top: -6px;
  left: -4px;
  width: 24px;
  height: 24px;
  color: #f0f0f0;
  fill: #f5f5f5; /* 填充颜色 */
  z-index: 0;
}

.note-text {
  position: relative;
  z-index: 1;
  color: #2c3e50;
  line-height: 1.7;
  font-size: 15px;
  margin: 0;
  /* 稍微缩进一点以避开引号图标 */
  padding-left: 2px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f5f5f5;
}

.chapter-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #b0b0b0;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.action-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #9ca3af;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-btn:hover {
  color: var(--primary-green, #42b983);
  background-color: rgba(66, 185, 131, 0.1);
}

/* 图标通用尺寸 */
.icon-xs {
  width: 14px;
  height: 14px;
}
.icon-sm {
  width: 16px;
  height: 16px;
}
.icon-md {
  width: 20px;
  height: 20px;
}
.icon-lg {
  width: 48px;
  height: 48px;
  color: #d1d5db;
}

/* 搜索高亮 */
:deep(.highlight) {
  background-color: #fff3cd;
  color: #856404;
  padding: 0 2px;
  border-radius: 2px;
}

.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  gap: 16px;
  color: #999;
}
</style>
