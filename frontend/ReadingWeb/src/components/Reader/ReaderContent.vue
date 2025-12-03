<!-- ReaderContent.vue -->
<template>
  <main class="main-container">
    <div :class="['content-card', isDarkMode ? 'dark' : '']">
      <!-- 
        1. 头部：flex-shrink: 0 
        防止被挤压
      -->
      <header class="page-header">
        <p class="chapter-label">{{ pageData.chapter }}</p>
      </header>

      <!-- 
        2. 内容区域：flex: 1
        自动占据剩余高度。如果内容超出，由 CSS 控制是隐藏还是滚动
      -->
      <article
        ref="articleRef"
        :class="['page-content', annotationMode ? 'annotation-mode' : '', `mode-${readingMode}`]"
        :style="{
          fontSize: typography.fontSize + 'px',
          lineHeight: typography.lineHeight,
        }"
        @mouseup="handleTextSelection"
        @mousedown="clearSelectionMenu"
      >
        <p
          v-for="(paragraph, index) in pageData.content"
          :key="index"
          class="paragraph"
          :data-paragraph-id="index"
        >
          <template v-if="showThoughts && index === 4">
            <span class="indent-reset">
              <span
                @click="(e) => handleActiveThought(index, paragraph.substring(0, 52), e)"
                class="thought-text-segment"
              >
                {{ paragraph.substring(0, 52) }}
                <span class="thought-bubble-wrapper">
                  <span class="thought-bubble">
                    <MessageSquare :size="8" fill="currentColor" class="message-icon-small" />
                    <span>12</span>
                  </span>
                </span>
              </span>
              {{ paragraph.substring(52) }}
            </span>
          </template>
          <template v-else> {{ paragraph }} </template>
        </p>

        <div
          v-if="annotationMode && selectedText"
          :style="annotationPosition"
          class="annotation-menu"
          :class="{ dark: isDarkMode }"
        >
          <button class="annotation-btn" @click="addAnnotation" title="添加批注">
            <PenLine :size="16" />
          </button>

          <button class="annotation-btn" @click="highlightText" title="高亮">
            <Highlighter :size="16" />
          </button>
        </div>

        <div v-if="showThoughts && highlights.length > 0" class="highlights-list">
          <div v-for="(highlight, idx) in highlights" :key="idx" class="highlight-item">
            <span class="highlight-text">{{ highlight.text }}</span>
            <span v-if="highlight.note" class="highlight-note">{{ highlight.note }}</span>
          </div>
        </div>
      </article>

      <!-- 
        3. 底部按钮：flex-shrink: 0
        永远保持固定高度，不会被文字挤没
      -->
      <div v-if="readingMode === 'paged'" class="pagination">
        <button class="page-btn" @click="previousPage" :disabled="currentPage <= 1">
          <ChevronLeft :size="16" class="chevron-left" />
          <span class="btn-text">上一页</span>
        </button>

        <span class="pagination-text"> - {{ currentPage }} / {{ totalPages }} - </span>

        <button class="page-btn" @click="nextPage" :disabled="currentPage >= totalPages">
          <span class="btn-text">下一页</span>
          <ChevronRight :size="16" class="chevron-right" />
        </button>
      </div>
    </div>

    <SelectionMenu :position="selection?.position || null" :onAction="handleMenuAction" />
  </main>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import SelectionMenu from './SelectionMenu.vue'
import {
  ChevronLeft,
  ChevronRight,
  PenLine,
  Highlighter,
  MessageSquare,
  Copy,
} from 'lucide-vue-next'
import type { BookPage, TypographySettings } from './types'

type ReadingMode = 'paged' | 'scroll'

interface Highlight {
  id: string
  text: string
  note?: string
  paragraphId: number
  type: 'highlight' | 'annotation'
}

// Props 定义
const props = defineProps<{
  pageData: BookPage
  isDarkMode: boolean
  typography: TypographySettings
  annotationMode: boolean
  showThoughts: boolean
  readingMode: ReadingMode
  onTextSelection: (text: string) => void
  onActiveThought: (index: number, text: string) => void
}>()

// 响应式状态
const currentPage = ref(1)
const totalPages = ref(248)
const selectedText = ref('')
const selectedRange = ref<Range | null>(null)
const annotationPosition = reactive({ top: '0px', left: '0px' })
const highlights = ref<Highlight[]>([])
const articleRef = ref<HTMLElement | null>(null)

// 文本选中状态
const selection = ref<{
  text: string
  position: { top: number; left: number }
} | null>(null)

// 模拟的点击事件处理
const handleActiveThought = (index: number, text: string, event: MouseEvent) => {
  event.stopPropagation()
  props.onActiveThought(index, text)
}

// 隐藏 Selection Menu
const clearSelectionMenu = () => {
  selection.value = null
}

// 处理 Selection Menu 的操作
const handleMenuAction = (action: string) => {
  console.log(`Action: ${action} on text: "${selection.value?.text}"`)
  if (action === 'copy') {
    navigator.clipboard.writeText(selection.value?.text || '')
  }
  if (selection.value?.text) {
    props.onTextSelection(selection.value.text)
  }
  // selection.value = null
}

// 滚动清除 Selection Menu
const handleScrollClear = () => {
  if (selection.value) {
    selection.value = null
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScrollClear, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScrollClear)
})

// 处理文本选中
const handleTextSelection = () => {
  const selectionObj = window.getSelection()
  if (!selectionObj || selectionObj.isCollapsed) {
    clearAnnotationMenu()
    clearSelectionMenu()
    return
  }
  const text = selectionObj.toString().trim()
  if (!text) {
    clearAnnotationMenu()
    clearSelectionMenu()
    return
  }

  const range = selectionObj.getRangeAt(0)
  const rect = range.getBoundingClientRect()

  if (articleRef.value && articleRef.value.contains(selectionObj.anchorNode)) {
    selection.value = {
      text,
      position: {
        top: rect.top,
        left: rect.left + rect.width / 2,
      },
    }
  } else {
    clearSelectionMenu()
  }

  if (props.annotationMode) {
    selectedText.value = text
    selectedRange.value = range
    annotationPosition.top = rect.bottom + 10 + 'px'
    annotationPosition.left = rect.left + 'px'
  } else {
    clearAnnotationMenu()
  }
}

const clearAnnotationMenu = () => {
  selectedText.value = ''
  selectedRange.value = null
}

const wrapSelection = (range: Range, id: string, type: 'highlight' | 'annotation') => {
  const span = document.createElement('span')
  span.className = type === 'annotation' ? 'highlighted has-note' : 'highlighted'
  span.dataset.highlightId = id
  span.textContent = range.toString()

  span.onclick = (e) => {
    e.stopPropagation()
    const highlightData = highlights.value.find((h) => h.id === id)
    if (highlightData?.note) {
      alert(`批注内容: ${highlightData.note}`)
    }
  }

  try {
    range.deleteContents()
    range.insertNode(span)
  } catch (e) {
    console.error('DOM操作失败:', e)
  }
}

const addAnnotation = () => {
  const note = prompt('添加批注：')
  if (note && selectedText.value && selectedRange.value) {
    const id = Date.now().toString()
    const paragraphId = 0
    highlights.value.push({
      id,
      text: selectedText.value,
      note,
      paragraphId,
      type: 'annotation',
    })
    wrapSelection(selectedRange.value, id, 'annotation')
    clearSelection()
  }
}

const highlightText = () => {
  if (selectedText.value && selectedRange.value) {
    const id = Date.now().toString()
    const paragraphId = 0
    highlights.value.push({
      id,
      text: selectedText.value,
      paragraphId,
      type: 'highlight',
    })
    wrapSelection(selectedRange.value, id, 'highlight')
    clearSelection()
  }
}

const clearSelection = () => {
  clearAnnotationMenu()
  clearSelectionMenu()
  window.getSelection()?.removeAllRanges()
}

const previousPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}
</script>

<style scoped>
/* 
  === 布局核心修正 === 
  目标：让 Card 占满父容器，内部采用 Flex 垂直布局
*/

.main-container {
  width: 100%;
  height: 100%; /* 继承 ReaderPage 给的高度 */
  display: flex;
  justify-content: center;
  box-sizing: border-box;
  padding: 0; /* 去除 padding，外层已处理 */
}

.content-card {
  width: 100%;
  /* 高度强制占满 */
  height: 100%;

  /* Flexbox 布局：垂直排列 */
  display: flex;
  flex-direction: column;

  /* 原有样式保留 */
  padding: 2.5rem 3rem;
  border-radius: 1rem;
  box-sizing: border-box;
  transition: max-width 300ms ease;
}

@media (max-width: 768px) {
  .content-card {
    padding: 1.5rem 1.75rem;
  }
}

/* 深色/浅色模式 */
.content-card.dark {
  background-color: #18181b;
  color: #d4d4d8;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.1);
}

.content-card:not(.dark) {
  background-color: #ffffff;
  color: #1f2937;
  box-shadow:
    0 4px 6px -1px rgba(0, 0, 0, 0.1),
    0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

/* 页面头部：固定高度，不被压缩 */
.page-header {
  margin-bottom: 2rem;
  border-bottom: 1px solid transparent;
  text-align: left;
  flex-shrink: 0; /* 关键 */
}

.chapter-label {
  font-size: 0.875rem;
  color: #9ca3af;
  margin-bottom: 1rem;
  font-family: sans-serif;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  margin: 0; /* 修正 margin */
}

/* 
  === 核心内容区域 === 
  变化最大：flex: 1 自动占满
*/
.page-content {
  font-family: serif;
  text-align: justify;
  transition:
    color 300ms,
    background-color 300ms; /* 移除高度动画 */
  position: relative;

  flex: 1; /* 占据剩余空间 */
  min-height: 0; /* 允许 flex 容器溢出处理 */
}

/* 翻页模式：内容溢出则隐藏 */
.page-content.mode-paged {
  overflow: hidden;

  /* 分栏逻辑 */
  columns: 2;
  column-gap: 2.5rem;
  column-rule-color: transparent;
  column-fill: auto;
}

@media (max-width: 768px) {
  .page-content.mode-paged {
    columns: 1;
  }
}

/* 滚动模式：内容溢出容器内部滚动 */
.page-content.mode-scroll {
  overflow-y: auto;
  overflow-x: hidden;
  columns: 1;
  padding-right: 12px; /* 防止滚动条遮挡文字 */
}

/* 滚动条美化 */
.page-content.mode-scroll::-webkit-scrollbar {
  width: 6px;
}
.page-content.mode-scroll::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}
.content-card.dark .page-content.mode-scroll::-webkit-scrollbar-thumb {
  background-color: rgba(255, 255, 255, 0.2);
}

/* 批注模式 */
.page-content.annotation-mode {
  cursor: text;
}
.page-content.annotation-mode::selection {
  background-color: #fef3c7;
}
.content-card.dark .page-content.annotation-mode::selection {
  background-color: rgba(120, 53, 15, 0.4);
}

/* 段落 */
.paragraph {
  margin-bottom: 1.2rem;
  text-indent: 1.5rem;
  break-inside: avoid-column;
}
.page-content.mode-scroll .paragraph {
  break-inside: auto;
}

/* 
  === 底部按钮 === 
  flex-shrink: 0 保证永远可见
*/
.pagination {
  margin-top: auto; /* 将按钮推到底部 */
  padding-top: 1.5rem;
  border-top: 1px solid #f3f4f6;

  display: flex;
  align-items: center;
  justify-content: space-between;

  flex-shrink: 0; /* 关键：禁止被压缩 */
}

.content-card.dark .pagination {
  border-top-color: #27272a;
}

/* 按钮样式保持不变 */
.page-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border-radius: 9999px;
  border: 1px solid #e5e7eb;
  color: #6b7280;
  background-color: transparent;
  cursor: pointer;
  transition: all 300ms;
  font-size: 0.8rem;
}
.content-card.dark .page-btn {
  border-color: #3f3f46;
  color: #a1a1aa;
}
.page-btn:hover:not(:disabled) {
  background-color: #f9fafb;
}
.content-card.dark .page-btn:hover:not(:disabled) {
  background-color: #27272a;
}
.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.btn-text {
  font-size: 0.8rem;
}
.chevron-left,
.chevron-right {
  transition: transform 300ms;
}
.page-btn:hover:not(:disabled) .chevron-left {
  transform: translateX(-0.125rem);
}
.page-btn:hover:not(:disabled) .chevron-right {
  transform: translateX(0.125rem);
}
.pagination-text {
  font-size: 0.7rem;
  color: #e5e7eb;
  font-family: sans-serif;
  letter-spacing: 0.1em;
}
.content-card.dark .pagination-text {
  color: #444448;
}

/* 高亮和批注样式保持不变 */
.highlighted {
  background-color: #fef3c7;
  padding: 0.1rem 0;
  border-radius: 0.2rem;
  cursor: pointer;
  transition: background-color 0.2s;
}
.highlighted.has-note {
  border-bottom: 2px solid #f59e0b;
  background-color: rgba(254, 243, 199, 0.5);
}
.highlighted:hover {
  background-color: #fde68a;
}
.content-card.dark .highlighted {
  background-color: rgba(120, 53, 15, 0.4);
}

/* 批注菜单 */
.annotation-menu {
  position: fixed;
  display: flex;
  gap: 0.5rem;
  padding: 0.5rem;
  background-color: white;
  border-radius: 0.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 100;
}
.annotation-menu.dark {
  background-color: #27272a;
}
.annotation-btn {
  width: 2rem;
  height: 2rem;
  padding: 0;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f3f4f6;
  color: #1f2937;
  transition: all 0.2s;
}
.annotation-menu.dark .annotation-btn {
  background-color: #3f3f46;
  color: #d4d4d8;
}
.annotation-btn:hover {
  background-color: #e5e7eb;
}
.annotation-menu.dark .annotation-btn:hover {
  background-color: #52525b;
}

/* 高亮列表 */
.highlights-list {
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e5e7eb;
}
.content-card.dark .highlights-list {
  border-top-color: #27272a;
}
.highlight-item {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
  padding: 0.75rem;
  background-color: #f9fafb;
  border-radius: 0.5rem;
}
.content-card.dark .highlight-item {
  background-color: #27272a;
}
.highlight-text {
  font-weight: 600;
  color: #1f2937;
  flex: 1;
}
.content-card.dark .highlight-text {
  color: #d4d4d8;
}
.highlight-note {
  color: #6b7280;
  font-style: italic;
  font-size: 0.875rem;
}
.content-card.dark .highlight-note {
  color: #a1a1aa;
}

/* Thoughts 模拟批注样式 */
.indent-reset {
  display: block;
  text-indent: 0;
  margin-top: 0;
  margin-bottom: 0;
  line-height: inherit;
}
.thought-text-segment {
  border-bottom: 2px dashed #d1d5db;
  padding-bottom: 0.125rem;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 0.25rem;
  box-decoration-break: clone;
  position: relative;
}
.content-card.dark .thought-text-segment {
  border-bottom-color: #4b5563;
}
.thought-text-segment:hover {
  background-color: #f3f4f6;
  border-bottom-color: #60a5fa;
}
.content-card.dark .thought-text-segment:hover {
  background-color: #27272a;
  border-bottom-color: #3b82f6;
}
.thought-bubble-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-left: 0.25rem;
  vertical-align: super;
}
.thought-bubble {
  background-color: #dbeafe;
  color: #2563eb;
  font-size: 0.625rem;
  padding: 0.15rem 0.35rem;
  border-radius: 9999px;
  display: flex;
  align-items: center;
  gap: 0.125rem;
  font-family: sans-serif;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  line-height: 1;
}
.content-card.dark .thought-bubble {
  background-color: #1e3a8a;
  color: #93c5fd;
}
.message-icon-small {
  width: 0.75em;
  height: 0.75em;
  min-width: 8px;
  min-height: 8px;
  transform: translateY(0.5px);
}
</style>
