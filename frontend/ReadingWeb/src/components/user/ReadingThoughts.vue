<!-- ReadingThoughts.vue -->
<template>
  <div class="thoughts-section">
    <div class="card-header">
      <h3 class="card-title">我的想法</h3>
      <span class="view-all" @click="goToAllThoughts">
        查看全部 <ArrowRight class="icon-inline" />
      </span>
    </div>

    <!-- 滚动容器 -->
    <div class="scroll-wrapper">
      <div class="scroll-container">
        <div v-for="item in thoughts" :key="item.id" class="thought-slide-card">
          <div class="slide-header">
            <span class="book-tag">《{{ item.bookName }}》</span>
            <span class="time-text">{{ item.date }}</span>
          </div>

          <div class="slide-content">
            <p>{{ item.thought }}</p>
          </div>

          <div class="slide-footer">
            <div class="quote-line"></div>
            <p class="quote-text">{{ item.quote }}</p>
          </div>

          <Quote class="bg-icon" />
        </div>

        <!-- 这里的 spacer 是为了让最后一张卡片右边也能留出间距 -->
        <div class="spacer"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, Quote } from 'lucide-vue-next'

const router = useRouter()

const goToAllThoughts = () => {
  const url = '/userposts?tab=thoughts'
  // 在新标签页打开
  window.open(url, '_blank')
}

const props = defineProps<{
  thoughts: Array<{
    id: number | string
    bookName: string
    date: string
    thought: string
    quote?: string
  }>
}>()
</script>

<style scoped>
.thoughts-section {
  background: var(--card-bg, #fff);
  border-radius: 16px;
  /* 上下左右的 padding 单独控制，右侧设为 0 是为了让滚动内容能贴边 */
  padding: 24px 0 24px 24px;
  box-shadow: var(--shadow, 0 4px 12px rgba(0, 0, 0, 0.05));
  margin-top: 20px;

  /* 关键：确保自身宽度不超过父元素 */
  width: 100%;
  box-sizing: border-box;
  overflow: hidden; /* 防止圆角溢出 */
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  /* 标题右侧补上 padding，保持视觉对齐 */
  padding-right: 24px;
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
  display: flex;
  align-items: center;
}
.view-all:hover {
  color: var(--primary-green, #42b983);
}
.icon-inline {
  width: 16px;
  height: 16px;
  margin-left: 4px;
}

/* 
  新增一个包装层 
  用来控制滚动条位置，防止滚动条紧贴着内容
*/
.scroll-wrapper {
  width: 100%;
  overflow: hidden; /* 隐藏 wrapper 溢出 */
}

.scroll-container {
  display: flex;
  gap: 16px;

  /* 开启横向滚动 */
  overflow-x: auto;

  /* 关键：限制宽度为 100%，超出出滚动条 */
  width: 100%;

  /* 底部留出一点空间给阴影和滚动条 */
  padding-bottom: 12px;
  padding-right: 24px; /* 让最后一张卡片能划出来 */

  /* 滚动吸附优化 */
  scroll-snap-type: x mandatory;
  scroll-behavior: smooth;
}

/* 
  美化滚动条 (Webkit: Chrome, Safari, Edge) 
  如果你希望完全隐藏滚动条，把 height 设为 0 或者 display: none
*/
.scroll-container::-webkit-scrollbar {
  height: 6px; /* 横向滚动条高度 */
}
.scroll-container::-webkit-scrollbar-thumb {
  background: #e0e0e0;
  border-radius: 3px;
}
.scroll-container::-webkit-scrollbar-thumb:hover {
  background: #ccc;
}
.scroll-container::-webkit-scrollbar-track {
  background: transparent;
}

/* 单个卡片样式 */
.thought-slide-card {
  /* 固定宽度，但不要太大，260px 比较合适 */
  flex: 0 0 260px;
  height: 180px;

  background: #f8f9fa;
  border: 1px solid #eee;
  border-radius: 12px;
  padding: 16px;

  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;

  scroll-snap-align: start;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
  cursor: grab;

  /* 防止文字撑大卡片 */
  box-sizing: border-box;
}

.thought-slide-card:hover {
  background: #fff;
  border-color: var(--primary-green, #42b983);
}

.slide-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.book-tag {
  font-size: 12px;
  font-weight: 600;
  color: var(--primary-green, #42b983);
  background: rgba(66, 185, 131, 0.1);
  padding: 2px 6px;
  border-radius: 4px;
  max-width: 140px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.time-text {
  font-size: 12px;
  color: #bbb;
}

.slide-content {
  flex: 1;
  margin-bottom: 12px;
}
.slide-content p {
  margin: 0;
  font-size: 14px;
  color: #333;
  line-height: 1.5;
  font-weight: 500;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.slide-footer {
  display: flex;
  align-items: center;
  gap: 8px;
}
.quote-line {
  width: 3px;
  height: 24px;
  background: #ddd;
  border-radius: 2px;
  flex-shrink: 0;
}
.quote-text {
  margin: 0;
  font-size: 12px;
  color: #888;
  font-family: serif;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.bg-icon {
  position: absolute;
  bottom: -10px;
  right: -10px;
  width: 60px;
  height: 60px;
  color: rgba(0, 0, 0, 0.03);
  pointer-events: none;
  transform: rotate(15deg);
}

.spacer {
  flex: 0 0 1px;
}
</style>
