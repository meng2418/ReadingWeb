<template>
  <div class="notes-wrapper">
    <div class="backdrop" :class="{ visible: isOpen }" @click="$emit('close')"></div>

    <div
      class="notes-panel"
      :class="{
        open: isOpen,
        'dark-mode': isDarkMode,
      }"
    >
      <div class="panel-header">
        <h2 class="title">我的笔记 ({{ notes.length }})</h2>
        <button @click="$emit('close')" class="close-btn">
          <X :size="20" />
        </button>
      </div>

      <div class="notes-list">
        <div v-if="notes.length === 0" class="empty-state">
          <Quote :size="48" class="empty-icon" />
          <p>No notes yet.</p>
        </div>

        <div v-for="note in notes" :key="note.id" class="note-item-wrapper">
          <div class="note-card">
            <div class="note-quote-section">
              <Highlighter v-if="isHighlight(note)" :size="14" class="icon highlight-icon" />
              <Quote v-else :size="14" class="icon" />

              <p class="quote-text">"{{ note.quote }}"</p>
            </div>

            <p v-if="!isHighlight(note)" class="note-content">
              {{ note.note }}
            </p>

            <div class="note-footer">
              <Calendar :size="12" />
              <span>{{ note.date }}</span>
              <span v-if="isHighlight(note)">• 划线</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { X, Quote, Calendar, Highlighter } from 'lucide-vue-next'
import type { Note } from './types'

defineProps<{
  isOpen: boolean
  notes: Note[]
  isDarkMode: boolean
}>()

const emit = defineEmits<(e: 'close') => void>()

// 判断是否是划线：如果 note 字段以 [Highlight: 开头，或者是空字符串但有 quote，则是划线
const isHighlight = (note: Note) => {
  // 如果 note 为空或 undefined，但有 quote，则可能是划线
  if (!note.note || note.note.trim() === '') {
    return !!note.quote // 有 quote 但没有 note 内容，通常是划线
  }
  // 如果 note 以 [Highlight: 开头，则是划线
  return note.note.startsWith('[Highlight:')
}
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

.notes-panel {
  position: fixed;
  top: 0;
  right: 0;
  height: 100%;
  width: 24rem; /* 96 */
  box-shadow: -10px 0 30px rgba(0, 0, 0, 0.1);
  z-index: 100;
  transform: translateX(100%);
  transition: transform 0.3s ease-out;
  display: flex;
  flex-direction: column;
  background-color: white;
  color: #1f2937;
}

.notes-panel.open {
  transform: translateX(0);
}

.notes-panel.dark-mode {
  background-color: #18181b;
  color: #e5e7eb;
}

.panel-header {
  padding: 1.5rem;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.dark-mode .panel-header {
  border-bottom-color: #27272a;
}

.title {
  font-family: serif;
  font-size: 1.25rem;
  font-weight: 500;
}

.close-btn {
  padding: 0.5rem;
  border-radius: 9999px;
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

.notes-list {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.empty-state {
  margin-top: 5rem;
  text-align: center;
  opacity: 0.5;
}
.empty-icon {
  margin: 0 auto 1rem;
  opacity: 0.2;
}

.note-card {
  padding: 1rem;
  border-radius: 0.75rem;
  border: 1px solid #f3f4f6;
  background-color: #f9fafb;
  transition: all 0.2s;
}

.note-card:hover {
  background-color: white;
  border-color: #e5e7eb;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.dark-mode .note-card {
  background-color: rgba(39, 39, 42, 0.5); /* zinc-800/50 */
  border-color: #3f3f46;
}
.dark-mode .note-card:hover {
  background-color: #27272a;
  border-color: #52525b;
}

.note-quote-section {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  margin-bottom: 0.75rem;
}

.icon {
  margin-top: 0.25rem;
  opacity: 0.4;
  flex-shrink: 0;
}

.highlight-icon {
  color: #eab308; /* yellow-500 */
}

.quote-text {
  font-family: serif;
  font-size: 0.875rem;
  font-style: italic;
  line-height: 1.6;
  color: #6b7280;
}
.dark-mode .quote-text {
  color: #9ca3af;
}

.note-content {
  font-size: 0.875rem;
  margin-bottom: 0.75rem;
  padding-left: 1.5rem;
  font-weight: 500;
  line-height: 1.6;
}

.note-footer {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding-left: 1.5rem;
  font-size: 0.75rem;
  opacity: 0.4;
  font-family: sans-serif;
}
</style>
