<template>
  <main class="main-container">
    <div
      :class="[
        'content-card',
        isDarkMode ? 'dark' : '',
        readingMode === 'scroll' ? 'scroll-mode-card' : '',
      ]"
    >
      <header class="page-header">
        <p class="chapter-label">{{ pageData.chapter }}</p>
      </header>

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
import { ref, computed, reactive, onMounted, onUnmounted } from 'vue' // 增加 onMounted, onUnmounted
// 引入 SelectionMenu
import SelectionMenu from './SelectionMenu.vue'
// 增加 Copy 图标 (在 SelectionMenu 中使用，但为了完整性先引入)
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
  onTextSelection: (text: string) => void // 增添: 外部处理文本选中的回调
  onActiveThought: (index: number, text: string) => void
}>()

// 响应式状态
const currentPage = ref(1)
const totalPages = ref(248) // 示例：总页数
const selectedText = ref('')
const selectedRange = ref<Range | null>(null)
const annotationPosition = reactive({ top: '0px', left: '0px' })
const highlights = ref<Highlight[]>([])
const annotations = ref<Map<string, string>>(new Map())

// ==== 整合增添的代码 - START (Ref 和 Selection 状态) ====
// DOM 引用
const articleRef = ref<HTMLElement | null>(null)

// 文本选中状态 (用于 SelectionMenu)
const selection = ref<{
  text: string
  position: { top: number; left: number }
} | null>(null)
// ==== 整合增添的代码 - END (Ref 和 Selection 状态) ====

// ==== 整合增添的代码 - START (方法) ====

// 模拟的点击事件处理
const handleActiveThought = (index: number, text: string, event: MouseEvent) => {
  event.stopPropagation()
  props.onActiveThought(index, text)
}

// 隐藏 Selection Menu (绑定到 mousedown)
const clearSelectionMenu = () => {
  selection.value = null
}

// 处理 Selection Menu 的操作
const handleMenuAction = (action: string) => {
  console.log(`Action: ${action} on text: "${selection.value?.text}"`)
  if (action === 'copy') {
    navigator.clipboard.writeText(selection.value?.text || '')
  } // 触发外部回调
  if (selection.value?.text) {
    props.onTextSelection(selection.value.text)
  } // 保持选择状态，但清除菜单 (可选，根据交互需求)
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

// ==== 整合增添的代码 - END (方法) ====

// 处理文本选中 (修改：现在同时处理批注菜单和 SelectionMenu)
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
  const rect = range.getBoundingClientRect() // 1. 处理 SelectionMenu (划词操作)
  // 确保选中在文章内部 (使用 ref 检查)

  if (articleRef.value && articleRef.value.contains(selectionObj.anchorNode)) {
    selection.value = {
      text,
      position: {
        // 居中于选中区域上方
        top: rect.top,
        left: rect.left + rect.width / 2,
      },
    }
  } else {
    clearSelectionMenu()
  } // 2. 处理 AnnotationMenu (旧批注菜单，仅在 annotationMode 为 true 时)

  if (props.annotationMode) {
    selectedText.value = text
    selectedRange.value = range // 计算批注菜单位置 (这里使用 rect.bottom 作为旧菜单位置)

    annotationPosition.top = rect.bottom + 10 + 'px'
    annotationPosition.left = rect.left + 'px'
  } else {
    clearAnnotationMenu()
  }
}

// 清除旧批注菜单状态
const clearAnnotationMenu = () => {
  selectedText.value = ''
  selectedRange.value = null
}

// 1. 抽取通用的 DOM 包裹函数
const wrapSelection = (range: Range, id: string, type: 'highlight' | 'annotation') => {
  const span = document.createElement('span') // 根据类型添加不同的类名
  span.className = type === 'annotation' ? 'highlighted has-note' : 'highlighted'
  span.dataset.highlightId = id // 绑定 ID 到 DOM，方便后续查找
  span.textContent = range.toString() // 简单的点击事件：点击高亮区域显示 ID (实际项目中可改为显示 Popover)

  span.onclick = (e) => {
    e.stopPropagation() // 防止触发外层点击
    const highlightData = highlights.value.find((h) => h.id === id)
    if (highlightData?.note) {
      alert(`批注内容: ${highlightData.note}`) // 临时用 alert，建议改为自定义弹窗
    }
  }

  try {
    range.deleteContents()
    range.insertNode(span)
  } catch (e) {
    console.error('DOM操作失败:', e)
  }
}

// 2. 修改添加批注逻辑
const addAnnotation = () => {
  // 改进：这里可以使用自定义 UI 代替 prompt，但为演示逻辑暂保留 prompt
  const note = prompt('添加批注：')
  if (note && selectedText.value && selectedRange.value) {
    const id = Date.now().toString() // 生成简单 ID
    // 获取段落 ID 逻辑保持不变...
    // const selection = window.getSelection()
    // 假设获取到了

    const paragraphId = 0 // 先存数据

    highlights.value.push({
      id,
      text: selectedText.value,
      note,
      paragraphId,
      type: 'annotation',
    }) // 再更新视觉 (关键！)

    wrapSelection(selectedRange.value, id, 'annotation')

    clearSelection()
  }
}

// 3. 修改高亮逻辑
const highlightText = () => {
  if (selectedText.value && selectedRange.value) {
    const id = Date.now().toString() // ... 获取 paragraphId
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

// 清除选中状态 (清除系统选中和旧批注菜单)
const clearSelection = () => {
  clearAnnotationMenu()
  clearSelectionMenu()
  window.getSelection()?.removeAllRanges()
}

// 分页方法
const previousPage = () => {
  if (currentPage.value > 1) {
    currentPage.value-- // 这里可以加载上一页内容的逻辑
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++ // 这里可以加载下一页内容的逻辑
  }
}
</script>

<style scoped>
/* 样式保持不变 */
/* 基础容器样式 */
.main-container {
  width: 100%;
  display: flex;
  justify-content: center;
  box-sizing: border-box;
  padding: 1rem 1rem;
}

.content-card {
  width: 100%;
  max-width: 1400px;
  padding: 2.5rem 3rem;
  border-radius: 1rem;
  box-sizing: border-box;
  transition: max-width 300ms ease;
}
/* === 新增：滚动模式下的容器样式 === */
.content-card.scroll-mode-card {
  max-width: 800px; /* 设置一个适合单栏阅读的较窄宽度 */
}
@media (max-width: 768px) {
  .content-card {
    padding: 1.5rem 1.75rem;
  }
}

/* 深色模式 */
.content-card.dark {
  background-color: #18181b;
  color: #d4d4d8;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.1);
}

.content-card:not(.dark) {
  background-color: #ffffff;
  color: #1f2937;
  box-shadow: 0 1px 2px 0 rgba(229, 231, 235, 0.5);
}

/* 页面头部 */
.page-header {
  margin-bottom: 2rem;
  border-bottom: 1px solid transparent;
  text-align: left;
}

.chapter-label {
  font-size: 0.875rem;
  color: #9ca3af;
  margin-bottom: 1rem;
  font-family: sans-serif;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

/* 页面内容 - 基础样式 */
.page-content {
  font-family: serif;
  text-align: justify;
  transition: all 300ms;
  line-height: inherit;
  position: relative;
}

/* ============ 翻页模式（paged）- 双列 + 高度限制 ============ */
.page-content.mode-paged {
  max-height: 600px;
  overflow: hidden;
}

@media (min-width: 768px) {
  .page-content.mode-paged {
    columns: 2;
    column-gap: 2.5rem;
    column-rule-color: transparent;
  }
}

/* ============ 滚动模式（scroll）- 单列 + 无高度限制 ============ */
.page-content.mode-scroll {
  max-height: none;
  overflow: visible;
  columns: 1;
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

/* 高亮样式 */
/* 基础高亮 */
.highlighted {
  background-color: #fef3c7;
  padding: 0.1rem 0;
  border-radius: 0.2rem;
  cursor: pointer; /* 提示可点击 */
  transition: background-color 0.2s;
}

/* 批注特有样式：加下划线区分 */
.highlighted.has-note {
  border-bottom: 2px solid #f59e0b; /* 橙色下划线 */
  background-color: rgba(254, 243, 199, 0.5); /* 背景淡一点 */
}

/* 鼠标悬停效果 */
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

/* 分页 */
.pagination {
  margin-top: 3rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-top: 1px solid #f3f4f6;
  padding-top: 1.5rem;
}

.content-card.dark .pagination {
  border-top-color: #27272a;
}

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

.chevron-left {
  transition: transform 300ms;
}

.page-btn:hover:not(:disabled) .chevron-left {
  transform: translateX(-0.125rem);
}

.chevron-right {
  transition: transform 300ms;
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

/* =======================================
 模拟批注样式 (THOUGHTS)
 ======================================= */

/* 用于包裹带有想法的段落内容，抵消段落缩进 */
.indent-reset {
  /* 继承段落的 margin-bottom 和 break-inside */
  display: block;
  text-indent: 0;
  margin-top: 0;
  margin-bottom: 0;
  line-height: inherit;
  /* 确保新样式不会改变现有 .paragraph 的外部布局 */
}

.thought-text-segment {
  /* 模拟下划线和高亮效果 */
  border-bottom: 2px dashed #d1d5db; /* 浅灰色虚线 */
  padding-bottom: 0.125rem;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 0.25rem;
  box-decoration-break: clone;
  position: relative;
}

/* 暗色模式下的虚线颜色 */
.content-card.dark .thought-text-segment {
  border-bottom-color: #4b5563; /* 深灰色虚线 */
}

/* 悬停效果 */
.thought-text-segment:hover {
  background-color: #f3f4f6; /* 浅灰色背景 */
  border-bottom-color: #60a5fa; /* 蓝色虚线 */
}

.content-card.dark .thought-text-segment:hover {
  background-color: #27272a; /* 深色模式下的背景 */
  border-bottom-color: #3b82f6; /* 深色模式下的蓝色虚线 */
}

/* 气泡包裹器 */
.thought-bubble-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-left: 0.25rem;
  vertical-align: super; /* 将气泡提升到文本上方 */
}

/* 气泡本身 */
.thought-bubble {
  background-color: #dbeafe; /* 浅蓝色背景 */
  color: #2563eb; /* 深蓝色文字 */
  font-size: 0.625rem; /* 10px */
  padding: 0.15rem 0.35rem; /* 略微调整以适应小尺寸 */
  border-radius: 9999px;
  display: flex;
  align-items: center;
  gap: 0.125rem;
  font-family: sans-serif;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  line-height: 1; /* 确保气泡内文本行高正常 */
}

/* 暗色模式下的气泡 */
.content-card.dark .thought-bubble {
  background-color: #1e3a8a;
  color: #93c5fd;
}

/* 气泡内的小图标 */
.message-icon-small {
  width: 0.75em;
  height: 0.75em;
  min-width: 8px; /* 最小尺寸保证可见 */
  min-height: 8px;
  transform: translateY(0.5px); /* 微调对齐 */
}
</style>
