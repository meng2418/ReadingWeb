<template>
  <!-- 遮罩层 -->
  <div
    class="overlay"
    :class="isOpen ? 'overlay-active' : 'overlay-inactive'"
    @click="onClose"
  ></div>

  <!-- 侧边面板 -->
  <div
    :class="[
      'sidebar-panel',
      isOpen ? 'panel-open' : 'panel-closed',
      isDarkMode ? 'dark-mode' : '',
    ]"
  >
    <div class="panel-content">
      <!-- 头部 -->
      <div class="panel-header">
        <h2 class="panel-title">目录</h2>
        <button @click="onClose" class="close-btn">
          <X :size="20" />
        </button>
      </div>

      <!-- 章节列表 -->
      <div class="chapter-list">
        <button
          v-for="chapter in chapters"
          :key="chapter.id"
          @click="onSelectChapter(chapter.id)"
          :class="['chapter-item', currentChapterId === chapter.id ? 'active-chapter' : '']"
        >
          <div class="chapter-page">Page {{ chapter.page }}</div>
          <div class="chapter-title">{{ chapter.title }}</div>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { X } from 'lucide-vue-next'
import type { Chapter } from './types'

const props = defineProps<{
  isOpen: boolean
  onClose: () => void
  chapters: Chapter[]
  currentChapterId: string
  onSelectChapter: (id: string) => void
  isDarkMode: boolean
}>()
</script>

<style scoped>
/* 遮罩层样式 */
.overlay {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background-color: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(4px);
  z-index: 50;
  transition: opacity 300ms;
}

.overlay-active {
  opacity: 1;
}

.overlay-inactive {
  opacity: 0;
  pointer-events: none;
}

/* 侧边面板样式 */
.sidebar-panel {
  position: fixed;
  top: 0;
  right: 0;
  height: 100%;
  width: 20rem; /* 对应w-80 */
  box-shadow:
    0 10px 15px -3px rgba(0, 0, 0, 0.1),
    0 4px 6px -4px rgba(0, 0, 0, 0.1);
  z-index: 100;
  transform: translateX(100%);
  transition: transform 300ms ease-out;
}

.panel-open {
  transform: translateX(0);
}

.panel-closed {
  transform: translateX(100%);
}

/* 浅色模式侧边面板 */
.sidebar-panel:not(.dark-mode) {
  background-color: #ffffff;
  color: #1f2937;
}

/* 深色模式侧边面板 */
.sidebar-panel.dark-mode {
  background-color: #18181b;
  color: #e5e7eb;
}

/* 面板内容容器 */
.panel-content {
  padding: 1.5rem;
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 面板头部 */
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.panel-title {
  font-family: serif;
  font-size: 20px;
}

.close-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  padding: 0; /* 关键：去掉 padding */
  cursor: pointer;
  transition: background-color 300ms;
}

/* 浅色模式关闭按钮hover */
.sidebar-panel:not(.dark-mode) .close-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

/* 深色模式关闭按钮hover */
.sidebar-panel.dark-mode .close-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

/* 章节列表 */
.chapter-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

/* 章节项 */
.chapter-item {
  width: 100%;
  text-align: left;
  padding: 1rem;
  border-radius: 1rem;
  transition: all 200ms;
  background: transparent;
  border: none;
  cursor: pointer;
  color: inherit;
  font-family: inherit;
}

/* 未选中章节项默认样式 */
.chapter-item:not(.active-chapter) {
  opacity: 0.6;
}

/* 未选中章节项hover效果 */
.chapter-item:not(.active-chapter):hover {
  opacity: 1;
}

/* 浅色模式未选中章节项hover背景 */
.sidebar-panel:not(.dark-mode) .chapter-item:not(.active-chapter):hover {
  background-color: rgba(0, 0, 0, 0.05);
}

/* 深色模式未选中章节项hover背景 */
.sidebar-panel.dark-mode .chapter-item:not(.active-chapter):hover {
  background-color: rgba(255, 255, 255, 0.05);
}

/* 浅色模式选中章节项 */
.sidebar-panel:not(.dark-mode) .active-chapter {
  background-color: #f9fafb;
  border-left: 2px solid #000000;
}

/* 深色模式选中章节项 */
.sidebar-panel.dark-mode .active-chapter {
  background-color: #27272a;
  border-left: 2px solid #ffffff;
}

/* 章节页码 */
.chapter-page {
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  opacity: 0.5;
  margin-bottom: 0.25rem;
}

/* 章节标题 */
.chapter-title {
  font-family: serif;
}
</style>
