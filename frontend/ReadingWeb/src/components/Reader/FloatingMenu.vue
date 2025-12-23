<script setup lang="ts">
import {
  Sparkles,
  PenLine,
  ListCollapse,
  Type,
  Moon,
  Sun,
  MessageSquareQuote,
} from 'lucide-vue-next'

const props = defineProps<{
  isDarkMode: boolean
  activePanel: 'none' | 'toc' | 'typography' | 'notes'
  isAnnotationMode: boolean
  showThoughts: boolean
}>()

const emit = defineEmits<{
  (e: 'toggleTheme'): void
  (e: 'toggleTOC'): void
  (e: 'toggleTypography'): void
  (e: 'toggleAnnotation'): void
  (e: 'toggleThoughts'): void
}>()
</script>

<template>
  <div class="floating-menu">
    <button
      @click="$emit('toggleTOC')"
      class="menu-btn"
      :class="{
        active: activePanel === 'toc',
        dark: isDarkMode,
      }"
      title="目录"
    >
      <ListCollapse :size="20" />
      <span class="tooltip">目录 (Directory)</span>
    </button>

    <button class="menu-btn" :class="{ dark: isDarkMode }" title="AI 解读">
      <Sparkles :size="20" />
      <span class="tooltip">AI 解读 (AI Analysis)</span>
    </button>

    <button
      @click="$emit('toggleAnnotation')"
      class="menu-btn"
      :class="{
        active: isAnnotationMode || activePanel === 'notes',
        dark: isDarkMode,
      }"
      title="笔记"
    >
      <PenLine :size="20" />
      <span class="tooltip">笔记 (Notes)</span>
    </button>

    <button
      @click="$emit('toggleThoughts')"
      class="menu-btn"
      :class="{
        active: showThoughts,
        dark: isDarkMode,
      }"
      title="想法"
    >
      <MessageSquareQuote :size="20" />
      <span class="tooltip">想法 (Thoughts)</span>
    </button>

    <button
      @click="$emit('toggleTypography')"
      class="menu-btn"
      :class="{
        active: activePanel === 'typography',
        dark: isDarkMode,
      }"
      title="版式"
    >
      <Type :size="20" />
      <span class="tooltip">版式 (Typography)</span>
    </button>

    <button
      @click="$emit('toggleTheme')"
      class="menu-btn"
      :class="{ dark: isDarkMode }"
      title="主题"
    >
      <Sun v-if="isDarkMode" :size="20" />
      <Moon v-else :size="20" />
      <span class="tooltip">
        {{ isDarkMode ? '日间模式' : '夜间模式' }}
      </span>
    </button>
  </div>
</template>

<style scoped>
.floating-menu {
  position: fixed;
  right: 1.5rem;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 1rem;
  z-index: 50;
}

.menu-btn {
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  transition: all 0.2s;
  border: none;
  cursor: pointer;
  position: relative;

  /* Default Light Mode State */
  background-color: white;
}

.menu-btn:hover {
  color: #1f2937; /* gray-800 */
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

/* Active State Light Mode */
.menu-btn.active {
  background-color: #18181b; /* zinc-900 */
  color: white;
}

/* Dark Mode States */
.menu-btn.dark {
  background-color: #27272a; /* zinc-800 */
  color: #d4d4d8; /* zinc-300 */
}

.menu-btn.dark:hover {
  background-color: #3f3f46; /* zinc-700 */
}

.menu-btn.dark.active {
  background-color: white;
  color: #18181b;
}

/* Tooltip */
.tooltip {
  position: absolute;
  right: 100%;
  margin-right: 0.75rem;
  padding: 0.25rem 0.5rem;
  background-color: black;
  color: white;
  font-size: 0.75rem;
  border-radius: 0.25rem;
  opacity: 0;
  pointer-events: none;
  white-space: nowrap;
  transition: opacity 0.2s;
  z-index: 50;
}

.menu-btn:hover .tooltip {
  opacity: 1;
}
</style>
