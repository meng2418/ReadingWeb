<!-- ReadingNotes.vue -->
<!-- 读书笔记展示组件 -->
<template>
  <div class="notes-card">
    <div class="card-header">
      <h3 class="card-title">我的笔记</h3>
      <span class="view-all" @click="goToAllNotes"
        >查看全部 <ArrowRight class="icon-inline"
      /></span>
    </div>

    <div class="notes-list">
      <div v-for="note in recentNotes" :key="note.id" class="note-item">
        <div class="note-meta">
          <span class="book-name">《{{ note.bookName }}》</span>
          <span class="note-date">{{ note.date }}</span>
        </div>

        <div class="note-content">
          <div class="quote-mark">“</div>
          <p>{{ note.content }}</p>
        </div>

        <div class="note-footer" v-if="note.chapter">
          <span class="chapter-info">章节：{{ note.chapter }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight } from 'lucide-vue-next'
const router = useRouter()
const goToAllNotes = () => {
  router.push({ name: 'AllReadingNotes' })
}
// 模拟数据，实际使用时可以通过 props 传入或从 API 获取
const notes = ref([
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
    chapter: '测试章节',
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
// 计算属性：排序 + 截取前3条
const recentNotes = computed(() => {
  return notes.value
    .sort((a, b) => new Date(b.date) - new Date(a.date)) // 按日期倒序
    .slice(0, 3) // 只取前3条
})
</script>

<style scoped>
/* 复用 Dashboard 的基础卡片样式 */
.notes-card {
  background: var(--card-bg, #fff); /* 默认白色，兼容暗黑模式变量 */
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--shadow, 0 4px 12px rgba(0, 0, 0, 0.05));
  margin-top: 20px; /* 与上方组件保持间距 */
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
  color: var(--text-main, #333);
  margin: 0;
}

.view-all {
  font-size: 14px;
  color: var(--text-light, #999);
  cursor: pointer;
  transition: color 0.3s;

  /* 新增以下三行，实现垂直居中 */
  display: flex;
  align-items: center;
  line-height: 1; /* 防止行高影响对齐 */
}

.view-all:hover {
  color: var(--primary-green, #42b983);
}

/* 新增这个样式类 */
.icon-inline {
  width: 16px; /* 设置图标宽度 */
  height: 16px; /* 设置图标高度 */
  margin-left: 4px; /* 文字和图标之间的间距 */
  stroke-width: 2px; /* 如果觉得图标太细，可以加粗（Lucide图标支持） */
}

.notes-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.note-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.note-meta {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.book-name {
  font-weight: 600;
  color: var(--primary-green, #42b983);
  background-color: rgba(66, 185, 131, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
}

.note-date {
  color: var(--text-light, #999);
  font-size: 12px;
}

.note-content {
  position: relative;
  padding-left: 16px;
  border-left: 3px solid #eee;
  transition: border-color 0.3s;
}

.note-item:hover .note-content {
  border-left-color: var(--primary-green, #42b983);
}

.note-content p {
  margin: 0;
  color: var(--text-main, #444);
  line-height: 1.6;
  font-size: 15px;
}

.quote-mark {
  position: absolute;
  top: -10px;
  left: 6px;
  font-size: 24px;
  color: #eee;
  font-family: serif;
  line-height: 1;
}

.note-footer {
  text-align: right;
}

.chapter-info {
  font-size: 12px;
  color: var(--text-light, #bbb);
}
</style>
