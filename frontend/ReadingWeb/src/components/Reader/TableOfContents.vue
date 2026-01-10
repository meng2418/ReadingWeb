<template>
  <div class="toc-wrapper">
    <div class="backdrop" :class="{ visible: isOpen }" @click="$emit('close')"></div>

    <div
      class="toc-panel"
      :class="{
        open: isOpen,
        'dark-mode': isDarkMode,
      }"
    >
      <div class="panel-content">
        <div class="panel-header">
          <h2 class="title">目录</h2>
          <button @click="$emit('close')" class="close-btn">
            <X :size="20" />
          </button>
        </div>

        <div class="chapter-list">
          <button
            v-for="chapter in chapters"
            :key="chapter.id"
            @click="$emit('select', chapter.id)"
            class="chapter-item"
            :class="{
              active: currentChapterId === chapter.id,
            }"
          >
            <div class="page-num">Page {{ chapter.page }}</div>
            <div class="chapter-title">{{ chapter.title }}</div>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { X } from 'lucide-vue-next'
import type { Chapter } from './types'

defineProps<{
  isOpen: boolean
  chapters: Chapter[]
  currentChapterId: string
  isDarkMode: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'select', id: number): void
}>()
</script>

<style scoped>
.backdrop {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(4px);
  z-index: 50;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.3s;
}

.backdrop.visible {
  opacity: 1;
  pointer-events: auto;
}

.toc-panel {
  position: fixed;
  top: 0;
  right: 0;
  height: 100%;
  width: 20rem; /* 80 */
  box-shadow: -10px 0 30px rgba(0, 0, 0, 0.1);
  z-index: 100;
  transform: translateX(100%);
  transition: transform 0.3s ease-out;
  background-color: white;
  color: #1f2937;
}

.toc-panel.open {
  transform: translateX(0);
}

.toc-panel.dark-mode {
  background-color: #18181b;
  color: #e5e7eb;
}

.panel-content {
  padding: 1.5rem;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2rem;
}

.title {
  font-family: serif;
  font-size: 1.25rem;
  font-weight: 500;
}

.close-btn {
  padding: 0.5rem;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 50%;
  background: transparent;
  border: none;
  cursor: pointer;
  color: inherit;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
}
.dark-mode .close-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.chapter-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.chapter-item {
  width: 100%;
  text-align: left;
  padding: 1rem;
  border-radius: 0.75rem;
  transition: all 0.2s;
  background: transparent;
  border: none;
  color: inherit;
  cursor: pointer;
  opacity: 0.6;
  border-left: 2px solid transparent;
}

.chapter-item:hover {
  opacity: 1;
  background-color: rgba(0, 0, 0, 0.05);
}
.dark-mode .chapter-item:hover {
  background-color: rgba(255, 255, 255, 0.05);
}

.chapter-item.active {
  opacity: 1;
  background-color: #f9fafb;
  border-left-color: black;
}

.dark-mode .chapter-item.active {
  background-color: #27272a;
  border-left-color: white;
}

.page-num {
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  opacity: 0.5;
  margin-bottom: 0.25rem;
}

.chapter-title {
  font-family: serif;
}
</style>
