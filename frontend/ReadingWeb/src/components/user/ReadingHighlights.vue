<!-- ReadingHighlights.vue -->
<!-- 读书笔记(划线)展示组件 -->
<template>
  <div class="highlights-card">
    <div class="card-header">
      <h3 class="card-title">我的划线</h3>
      <span class="view-all" @click="goToAllHighlights"
        >查看全部 <ArrowRight class="icon-inline"
      /></span>
    </div>

    <div class="highlights-list">
      <div v-for="highlight in recentHighlights" :key="highlight.id" class="highlight-item">
        <div class="highlight-meta">
          <span class="book-name">《{{ highlight.bookName }}》</span>
          <span class="highlight-date">{{ highlight.date }}</span>
        </div>
        <div class="highlight-content">
          <div class="quote-mark">“</div>
          <p>{{ highlight.text }}</p>
        </div>

        <div class="highlight-footer" v-if="highlight.chapter">
          <span class="chapter-info">章节：{{ highlight.chapter }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight } from 'lucide-vue-next'

const router = useRouter()

const goToAllHighlights = () => {
  // 假设路由名称已更新或存在
  router.push({ name: 'AllReadingHighlights' }).catch(() => {
    // 如果路由不存在，暂时不做操作或跳转到默认页
    console.warn('Route AllReadingHighlights not found')
  })
}

const props = defineProps<{
  highlights: Array<{
    id: number | string
    bookName: string
    date: string
    text: string
    chapter: string
  }>
}>()

// 计算属性：排序 + 截取前3条
const recentHighlights = computed(() => {
  const list = Array.isArray(props.highlights) ? props.highlights : []
  return list
    .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()) // 按日期倒序
    .slice(0, 3) // 只取前3条
})
</script>

<style scoped>
/* 复用 Dashboard 的基础卡片样式 */
.highlights-card {
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

.highlights-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.highlight-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.highlight-meta {
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

.highlight-date {
  color: var(--text-light, #999);
  font-size: 12px;
}

.highlight-content {
  position: relative;
  padding-left: 16px;
  border-left: 3px solid #eee;
  transition: border-color 0.3s;
}

.highlight-item:hover .highlight-content {
  border-left-color: var(--primary-green, #42b983);
}

.highlight-content p {
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

.highlight-footer {
  text-align: right;
}

.chapter-info {
  font-size: 12px;
  color: var(--text-light, #bbb);
}
</style>
