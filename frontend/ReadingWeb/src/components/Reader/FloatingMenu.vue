<template>
  <div class="floating-menu">
    <button
      v-for="(item, index) in menuItems"
      :key="index"
      @click="item.action"
      :class="[
        'menu-button',
        {
          active: item.isActive,
          dark: isDarkMode,
          'dark-active': item.isActive && isDarkMode,
        },
      ]"
      :title="item.label"
    >
      <component :is="item.icon" :size="20" />
      <span class="tooltip">{{ item.label }}</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  Sparkles,
  PenLine,
  ListCollapse,
  Type,
  Moon,
  Sun,
  MessageSquareQuote,
  ScrollText,
  BookOpen,
} from 'lucide-vue-next'

interface ReadingMode {
  value: 'paged' | 'vertical'
}

interface Props {
  isDarkMode: boolean
  toggleTheme: () => void
  onToggleTOC: () => void
  onToggleTypography: () => void
  onToggleAnnotation: () => void
  onToggleThoughts: () => void
  onToggleReadingMode: () => void
  activePanel: 'none' | 'toc' | 'typography' | 'notes' // <--- 增添: 'notes'
  isAnnotationMode: boolean // Note: In React, this was replaced by activePanel === 'notes' or (isAnnotationMode || activePanel === 'notes')
  showThoughts: boolean
  readingMode: ReadingMode | string
}

const props = defineProps<Props>()

const menuItems = computed(() => [
  {
    icon: ListCollapse,
    label: '目录 (Directory)',
    action: props.onToggleTOC,
    isActive: props.activePanel === 'toc',
  },
  {
    icon: Sparkles,
    label: 'AI 解读 (AI Analysis)',
    action: () => {},
    isActive: false,
  },
  {
    icon: PenLine,
    label: '笔记 (Notes)',
    action: props.onToggleAnnotation,
    // <--- 增添/修改: 激活状态现在检查 notes 面板是否打开
    isActive: props.isAnnotationMode || props.activePanel === 'notes',
  },
  {
    icon: MessageSquareQuote,
    label: '想法 (Thoughts)',
    action: props.onToggleThoughts,
    isActive: props.showThoughts,
  },
  {
    // 假设 props.readingMode 的值为 'paged' 或 'scroll'
    icon: props.readingMode === 'paged' ? ScrollText : BookOpen,
    label: props.readingMode === 'paged' ? '切换上下滚动 (Vertical)' : '切换左右翻页 (Paged)',
    action: props.onToggleReadingMode,
    isActive: false,
  },
  {
    icon: Type,
    label: '版式 (Typography)',
    action: props.onToggleTypography,
    isActive: props.activePanel === 'typography',
  },
  {
    icon: props.isDarkMode ? Sun : Moon,
    label: props.isDarkMode ? '日间模式 (Light)' : '夜间模式 (Dark)',
    action: props.toggleTheme,
    isActive: false,
  },
])
</script>

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

.menu-button {
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  transition: all 0.2s;
  position: relative;
  border: none;
  cursor: pointer;
  background-color: white;
  color: #9ca3af;
}

.menu-button:hover {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  color: #1f2937;
}

.menu-button.active {
  background-color: #1f2937;
  color: white;
}

.menu-button.dark {
  background-color: #1f2937;
  color: #d1d5db;
}

.menu-button.dark:hover {
  background-color: #374151;
}

.menu-button.dark-active {
  background-color: white;
  color: #1f2937;
}

.tooltip {
  position: absolute;
  right: 100%;
  margin-right: 0.75rem;
  padding: 0.5rem 0.5rem;
  background-color: black;
  color: white;
  font-size: 0.75rem;
  border-radius: 0.25rem;
  opacity: 0;
  transition: opacity 0.2s;
  white-space: nowrap;
  pointer-events: none;
  z-index: 50;
}

.menu-button:hover .tooltip {
  opacity: 1;
}
</style>
