<template>
  <teleport to="body">
    <!-- 背景遮罩层 -->
    <div
      class="notes-backdrop"
      :class="{ active: isOpen, inactive: !isOpen }"
      @click="onClose"
    ></div>

    <!-- 侧边面板 -->
    <div
      class="notes-panel"
      :class="[{ open: isOpen, closed: !isOpen }, { 'dark-mode': isDarkMode }]"
    >
      <!-- 面板头部 -->
      <div class="panel-header">
        <h2 class="panel-title">我的笔记 （{{ notes.length }}）</h2>
        <button class="close-btn" @click="onClose">
          <X :size="20" />
        </button>
      </div>

      <!-- 面板内容 -->
      <div class="panel-content">
        <!-- 空状态 -->
        <div v-if="notes.length === 0" class="empty-state">
          <Quote :size="48" class="empty-icon" />
          <p>暂无笔记.</p>
        </div>

        <!-- 笔记列表 -->
        <div v-else class="notes-list">
          <div v-for="note in notes" :key="note.id" class="note-item">
            <div class="note-card">
              <div class="quote-section">
                <Quote :size="14" class="quote-icon" />
                <p class="quote-text">{{ note.quote }}</p>
              </div>

              <p class="note-content">{{ note.note }}</p>

              <div class="note-meta">
                <Calendar :size="12" class="meta-icon" />
                <span class="meta-text">{{ note.date }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </teleport>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'
import { X, Quote, Calendar } from 'lucide-vue-next'

// 类型定义
interface Note {
  id: string
  quote: string
  note: string
  date: string
}

// Props 定义
const props = defineProps<{
  isOpen: boolean
  onClose: () => void
  notes: Note[]
  isDarkMode: boolean
}>()
</script>

<style scoped>
/* 背景遮罩 */
.notes-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(0.25rem);
  z-index: 50;
  transition: opacity 0.3s ease;
}

.notes-backdrop.active {
  opacity: 1;
  pointer-events: auto;
}

.notes-backdrop.inactive {
  opacity: 0;
  pointer-events: none;
}

/* 侧边面板 */
.notes-panel {
  position: fixed;
  top: 0;
  right: 0;
  height: 100%;
  width: 24rem; /* w-96 */
  box-shadow:
    0 10px 25px -5px rgba(0, 0, 0, 0.1),
    0 8px 10px -6px rgba(0, 0, 0, 0.1);
  z-index: 50;
  transform: translateX(0);
  transition: transform 0.3s ease-out;
  display: flex;
  flex-direction: column;
  background-color: white;
  color: #1f2937; /* text-gray-800 */
}

.notes-panel.dark-mode {
  background-color: #18181b; /* bg-zinc-900 */
  color: #e5e7eb; /* text-gray-200 */
}

.notes-panel.open {
  transform: translateX(0);
}

.notes-panel.closed {
  transform: translateX(100%);
}

/* 面板头部 */
.panel-header {
  padding: 1.5rem;
  border-bottom: 1px solid #f3f4f6; /* border-gray-100 */
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dark-mode .panel-header {
  border-bottom-color: #27272a; /* border-zinc-800 */
}

.panel-title {
  font-family: serif;
  font-size: 1.25rem; /* text-xl */
  font-weight: 500;
  margin: 0;
}

.close-btn {
  padding: 0.5rem;
  border-radius: 9999px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  border: none;
  background-color: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  background-color: rgba(0, 0, 0, 0.05); /* bg-black/5 */
}

.dark-mode .close-btn:hover {
  background-color: rgba(255, 255, 255, 0.1); /* bg-white/10 */
}

/* 面板内容 */
.panel-content {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem;
}

/* 空状态 */
.empty-state {
  text-align: center;
  opacity: 0.5;
  margin-top: 5rem; /* mt-20 */
}

.empty-icon {
  margin: 0 auto 1rem; /* mx-auto mb-4 */
  opacity: 0.2;
}

/* 笔记列表 */
.notes-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem; /* space-y-6 */
}

/* 笔记项 */
.note-item {
  width: 100%;
}

.note-card {
  padding: 1rem;
  border-radius: 0.75rem; /* rounded-xl */
  transition: all 0.2s ease;
  border: 1px solid;
  background-color: #f9fafb; /* bg-gray-50 */
  border-color: #f3f4f6; /* border-gray-100 */
}

.dark-mode .note-card {
  background-color: rgba(39, 39, 42, 0.5); /* bg-zinc-800/50 */
  border-color: #3f3f46; /* border-zinc-700 */
}

.note-card:hover {
  background-color: white; /* hover:bg-white */
  border-color: #e5e7eb; /* hover:border-gray-200 */
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05); /* hover:shadow-sm */
}

.dark-mode .note-card:hover {
  background-color: #27272a; /* hover:bg-zinc-800 */
  border-color: #52525b; /* hover:border-zinc-600 */
}

/* 引用部分 */
.quote-section {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem; /* gap-3 */
  margin-bottom: 0.75rem; /* mb-3 */
}

.quote-icon {
  margin-top: 0.25rem; /* mt-1 */
  opacity: 0.4;
  flex-shrink: 0;
}

.quote-text {
  font-family: serif;
  font-size: 0.875rem; /* text-sm */
  font-style: italic;
  line-height: 1.625; /* leading-relaxed */
  color: #6b7280; /* text-gray-500 */
  margin: 0;
}

.dark-mode .quote-text {
  color: #9ca3af; /* text-gray-400 */
}

/* 笔记内容 */
.note-content {
  font-size: 0.875rem; /* text-sm */
  margin-bottom: 0.75rem; /* mb-3 */
  padding-left: 1.5rem; /* pl-6 */
  font-weight: 500; /* font-medium */
  line-height: 1.625; /* leading-relaxed */
  margin-top: 0;
}

/* 笔记元信息 */
.note-meta {
  display: flex;
  align-items: center;
  gap: 0.5rem; /* gap-2 */
  padding-left: 1.5rem; /* pl-6 */
  font-size: 0.75rem; /* text-xs */
  opacity: 0.4;
  font-family: sans-serif;
}

.meta-icon {
  flex-shrink: 0;
}

.meta-text {
  line-height: 1;
}
</style>
