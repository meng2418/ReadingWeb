<!-- ReaderContent.vue -->
<template>
  <main class="reader-wrapper">
    <div
      class="reader-card"
      :class="{
        'dark-mode': isDarkMode,
      }"
    >
      <!-- 上一章按钮 -->
      <div v-if="hasPrevChapter" class="chapter-nav top-chapter-nav left-chapter-nav">
        <button
          class="chapter-nav-button prev-chapter"
          @click="$emit('prevChapter')"
          :class="{ dark: isDarkMode }"
        >
          <ChevronLeft :size="16" class="nav-icon" />
          <span class="chapter-nav-text">上一章</span>
        </button>
      </div>

      <header class="reader-header">
        <p class="chapter-title">
          {{ pageData.chapter }}
        </p>
      </header>

      <article
        ref="articleRef"
        @mouseup="handleMouseUp"
        @mousedown="selection = null"
        class="reader-article"
        :style="{
          fontSize: `${typography.fontSize}px`,
          lineHeight: typography.lineHeight,
        }"
      >
        <p
          v-for="(paragraph, index) in pageData.content"
          :key="index"
          :data-index="index"
          class="paragraph"
        >
          <span
            v-for="seg in getParagraphSegments(paragraph, index)"
            :key="seg.key"
            :class="seg.classes"
            @click="(e) => handleSegmentClick(e, seg)"
          >
            {{ seg.text }}
            <span v-if="seg.isThought" class="thought-icon-wrapper">
              <span class="thought-icon-badge">
                <MessageSquare :size="8" fill="currentColor" />
              </span>
            </span>
          </span>
        </p>

        <!-- 只在最后一章显示结束操作 -->
        <BookEndActions
          v-if="isLastChapter"
          :book-id="bookEndData.bookId"
          :book-title="bookEndData.bookTitle"
          :recommendation-value="bookEndData.recommendationValue"
          :review-count="bookEndData.reviewCount"
          :rating-stats="bookEndData.ratingStats"
          :initial-completed="bookEndData.initialCompleted"
          :initial-complete-time="bookEndData.initialCompleteTime"
          @markComplete="handleBookEndAction('markComplete', $event)"
          @viewReviews="handleBookEndAction('viewReviews')"
          @rateBook="handleBookEndAction('rateBook', $event)"
          :is-dark-mode="isDarkMode"
        />
      </article>

      <!-- 下一章按钮 -->
      <div v-if="hasNextChapter" class="chapter-nav bottom-chapter-nav">
        <button
          class="chapter-nav-button next-chapter"
          @click="$emit('nextChapter')"
          :class="{ dark: isDarkMode }"
        >
          <span class="chapter-nav-text">下一章</span>
          <ChevronRight :size="16" class="nav-icon" />
        </button>
      </div>
    </div>

    <SelectionMenu
      :position="selection?.position || null"
      :hasOverlap="!!selection?.overlappingAnnotations?.length"
      @action="handleMenuAction"
    />
  </main>
</template>

<script setup lang="ts">
// @ts-nocheck
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { ChevronLeft, ChevronRight, MessageSquare } from 'lucide-vue-next'
import SelectionMenu from './SelectionMenu.vue'
import BookEndActions from './BookEndActions.vue'
import type { BookPage, TypographySettings, Annotation } from './types'

const props = defineProps<{
  pageData: BookPage
  isDarkMode: boolean
  typography: TypographySettings
  annotations: Annotation[]
  hasPrevChapter: boolean
  hasNextChapter: boolean
  isLastChapter: boolean
  bookEndData?: {
    bookId: string | number
    bookTitle: string
    recommendationValue: number
    reviewCount: number
    ratingStats: {
      recommend: number
      average: number
      poor: number
    }
    initialCompleted: boolean
    initialCompleteTime: string | null
  }
}>()

const emit = defineEmits<{
  (e: 'addAnnotation', annotation: Omit<Annotation, 'id'>): void
  (e: 'deleteAnnotation', annotationId: string): void
  (e: 'activeThought', noteId: string, text: string): void
  (e: 'aiQuery', text: string): void
  (e: 'prevChapter'): void
  (e: 'nextChapter'): void
  (
    e: 'textAction',
    text: string,
    action: string,
    range?: { pIndex: number; start: number; end: number },
  ): void
  (e: 'markComplete', data: { bookId: string | number, completeTime: string }): void
  (e: 'viewReviews'): void
  (e: 'rateBook', rating: string): void
}>()

// 为bookEndData提供默认值
const defaultBookEndData = {
  bookId: 'book-123',
  bookTitle: '书籍标题',
  recommendationValue: 90.5,
  reviewCount: 100,
  ratingStats: {
    recommend: 70,
    average: 20,
    poor: 10
  },
  initialCompleted: false,
  initialCompleteTime: null
}

const selection = ref<{
  text: string
  position: { top: number; left: number }
  pIndex: number
  startOffset: number
  endOffset: number
  overlappingAnnotations: Annotation[] // 新增：重叠的标注
} | null>(null)

const articleRef = ref<HTMLElement | null>(null)

// 处理书籍结束操作
const handleBookEndAction = (action: string, data?: any) => {
  switch (action) {
    case 'markComplete':
      emit('markComplete', data)
      break
    case 'viewReviews':
      emit('viewReviews')
      break
    case 'rateBook':
      emit('rateBook', data)
      break
  }
}

// 检测选中文本是否与现有标注重叠
const checkOverlappingAnnotations = (pIndex: number, start: number, end: number): Annotation[] => {
  const overlapping: Annotation[] = []

  for (const annotation of props.annotations) {
    // 检查是否在同一段落
    if (annotation.pIndex !== pIndex) continue

    // 计算重叠情况
    const isOverlapping =
      (start >= annotation.start && start <= annotation.end) || // 选中开始在标注内
      (end >= annotation.start && end <= annotation.end) ||     // 选中结束在标注内
      (start <= annotation.start && end >= annotation.end) ||   // 选中包含整个标注
      (start >= annotation.start && end <= annotation.end)      // 标注包含整个选中

    if (isOverlapping) {
      overlapping.push(annotation)
    }
  }

  return overlapping
}

// 处理标注点击（点击已划线的句子）
const handleAnnotationClick = (e: MouseEvent, segmentInfo: {
  text: string
  pIndex: number
  start: number
  end: number
}) => {
  e.stopPropagation()

  const target = e.target as HTMLElement
  const rect = target.getBoundingClientRect()

  // 检查是否有重叠的标注
  const overlappingAnnotations = checkOverlappingAnnotations(
    segmentInfo.pIndex,
    segmentInfo.start,
    segmentInfo.end
  )

  // 设置选中状态
  selection.value = {
    text: segmentInfo.text,
    position: {
      top: rect.top + rect.height / 2,
      left: rect.left + rect.width / 2
    },
    pIndex: segmentInfo.pIndex,
    startOffset: segmentInfo.start,
    endOffset: segmentInfo.end,
    overlappingAnnotations
  }

  // 如果是想法标注，不触发菜单，直接显示想法
  const hasThought = overlappingAnnotations.some(ann => ann.type === 'thought')
  if (hasThought) {
    const thoughtAnnotation = overlappingAnnotations.find(ann => ann.type === 'thought')
    if (thoughtAnnotation && thoughtAnnotation.noteId) {
      emit('activeThought', thoughtAnnotation.noteId, segmentInfo.text)
    }
    selection.value = null // 清空选中状态，因为想法有独立的弹窗
  }
}

const handleMouseUp = () => {
  const selectionObj = window.getSelection()
  if (!selectionObj || selectionObj.isCollapsed || selectionObj.rangeCount === 0) {
    selection.value = null
    return
  }
  const text = selectionObj.toString().trim()
  if (!text) {
    selection.value = null
    return
  }
  if (articleRef.value && !articleRef.value.contains(selectionObj.anchorNode)) {
    selection.value = null
    return
  }
  const anchorNode = selectionObj.anchorNode
  const focusNode = selectionObj.focusNode

  const findParentP = (node: Node | null): HTMLElement | null => {
    let current = node
    while (current && current !== articleRef.value) {
      if (current.nodeName === 'P' && current.parentElement === articleRef.value) {
        return current as HTMLElement
      }
      current = current.parentNode
    }
    return null
  }

  const pElement = findParentP(anchorNode)
  if (!pElement) {
    selection.value = null
    return
  }
  const endPElement = findParentP(focusNode)
  if (pElement !== endPElement) {
    selection.value = null
    return
  }
  const pIndex = parseInt(pElement.getAttribute('data-index') || '-1')
  if (pIndex === -1) return

  try {
    const range = selectionObj.getRangeAt(0)
    const preCaretRange = range.cloneRange()
    preCaretRange.selectNodeContents(pElement)
    preCaretRange.setEnd(range.startContainer, range.startOffset)
    const startOffset = preCaretRange.toString().length
    const endOffset = startOffset + range.toString().length
    const rect = range.getBoundingClientRect()

    // 检查是否有重叠的标注
    const overlappingAnnotations = checkOverlappingAnnotations(pIndex, startOffset, endOffset)

    selection.value = {
      text,
      position: { top: rect.top, left: rect.left + rect.width / 2 },
      pIndex,
      startOffset,
      endOffset,
      overlappingAnnotations // 保存重叠的标注
    }
  } catch (e) {
    console.error('Selection calculation failed', e)
    selection.value = null
  }
}

const handleScroll = () => {
  if (selection.value) selection.value = null
}
onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
})
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const handleMenuAction = (action: string) => {
  if (!selection.value) return
  if (action === 'copy') {
    navigator.clipboard.writeText(selection.value.text)
    selection.value = null
    return
  }
  if (['marker', 'wave', 'line'].includes(action)) {
    emit('addAnnotation', {
      chapterId: props.pageData.chapter,
      pIndex: selection.value.pIndex,
      start: selection.value.startOffset,
      end: selection.value.endOffset,
      type: action as any,
    })
    selection.value = null
  } else if (action === 'thought') {
    emit('textAction', selection.value.text, 'thought', {
      pIndex: selection.value.pIndex,
      start: selection.value.startOffset,
      end: selection.value.endOffset,
    })
    selection.value = null
  } else if (action === 'ai') {
    emit('aiQuery', selection.value.text)
    selection.value = null
  } else if (action === 'delete') {
    // 删除所有重叠的标注
    if (selection.value.overlappingAnnotations.length > 0) {
      for (const annotation of selection.value.overlappingAnnotations) {
        emit('deleteAnnotation', annotation.id)
      }
    }
    selection.value = null
  }
}

interface TextSegment {
  key: string
  text: string
  classes: string[]
  isThought: boolean
  noteId?: string
  hasAction: boolean
  // 新增字段，用于标注点击
  pIndex: number
  start: number
  end: number
}

const getParagraphSegments = (text: string, pIndex: number): TextSegment[] => {
  type SafeAnnotation = {
    start: number
    end: number
    type: Annotation['type']
    noteId?: string
    data?: any
  }
  const safeAnnotations: SafeAnnotation[] = props.annotations
    .filter((a) => a.pIndex === pIndex)
    .map((a) => ({
      start: (a as Annotation).start ?? 0,
      end: (a as Annotation).end ?? 0,
      type: a.type,
      noteId: a.noteId,
      data: a.data,
    }))
    .sort((a, b) => a.start - b.start)

  if (safeAnnotations.length === 0)
    return [
      {
        key: `${pIndex}-full`,
        text,
        classes: [],
        isThought: false,
        hasAction: false,
        pIndex,
        start: 0,
        end: text.length
      },
    ]

  const boundaries = new Set<number>([0, text.length])
  const annotationRanges: Array<{
    start: number
    end: number
    type: Annotation['type']
    noteId?: string
    data?: any
  }> = safeAnnotations.map((a) => {
    const start = typeof a.start === 'number' ? a.start : 0
    const end = typeof a.end === 'number' ? a.end : text.length
    return {
      start,
      end,
      type: a.type,
      noteId: a.noteId,
      data: a.data,
    }
  })

  annotationRanges.forEach((a) => {
    boundaries.add(Math.max(0, Math.min(a.start, text.length)))
    boundaries.add(Math.max(0, Math.min(a.end, text.length)))
  })

  const sortedBoundaries = Array.from(boundaries).sort((a, b) => a - b)
  const segments: TextSegment[] = []

  for (let i = 0; i < sortedBoundaries.length - 1; i++) {
    const start = sortedBoundaries[i]
    const end = sortedBoundaries[i + 1]
    const segmentText = text.slice(start, end)

    const activeAnns: typeof annotationRanges = []
    for (const ann of annotationRanges) {
      if (ann.start <= start && ann.end >= end) {
        activeAnns.push(ann)
      }
    }

    const classes: string[] = ['segment-transition']
    let hasAction = false
    let noteId = undefined
    let isThought = false

    if (activeAnns.length > 0) {
      if (activeAnns.some((a) => a.type === 'marker')) {
        classes.push('highlight-marker')
      }
      if (activeAnns.some((a) => a.type === 'wave')) {
        classes.push('highlight-wave')
      }
      if (activeAnns.some((a) => a.type === 'line')) {
        classes.push('highlight-line')
      }

      const thoughtAnn = activeAnns.find((a) => a.type === 'thought')
      if (thoughtAnn) {
        classes.push('highlight-thought')
        hasAction = true
        noteId = thoughtAnn.noteId
        isThought = true
      }

      // 如果有标注（马克笔、波浪线、直线），添加点击样式
      if (activeAnns.some(a => ['marker', 'wave', 'line'].includes(a.type))) {
        classes.push('clickable-annotation')
      }
    }

    segments.push({
      key: `${pIndex}-${start}`,
      text: segmentText,
      classes,
      isThought,
      noteId,
      hasAction,
      pIndex,
      start,
      end
    })
  }
  return segments
}

const handleSegmentClick = (e: MouseEvent, seg: TextSegment) => {
  // 如果有标注（马克笔、波浪线、直线），则触发标注点击
  const hasHighlight = seg.classes.some(c =>
    c === 'highlight-marker' ||
    c === 'highlight-wave' ||
    c === 'highlight-line'
  )

  if (hasHighlight) {
    handleAnnotationClick(e, {
      text: seg.text,
      pIndex: seg.pIndex,
      start: seg.start,
      end: seg.end
    })
    return
  }

  // 如果是想法标注，保留原有逻辑
  if (seg.hasAction) {
    e.stopPropagation()
    emit('activeThought', seg.noteId || 'default', seg.text)
  }
}
</script>

<style scoped>
.reader-wrapper {
  width: 100%;
  max-width: 1400px;
  height: 100%;
  display: flex;
  justify-content: center;
  box-sizing: border-box;
  padding: 0;
  margin-top: 16px;
}

.reader-card {
  width: 100%;
  height: 95%;
  display: flex;
  flex-direction: column;
  padding: 1rem 3rem 2.5rem 3rem;
  border-radius: 1rem;
  box-sizing: border-box;
  position: relative;
}

@media (max-width: 768px) {
  .reader-card {
    padding: 1.5rem 1.75rem;
  }
}

.reader-card.dark-mode {
  background-color: #18181b;
  color: #d4d4d8;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.1);
}

.reader-card:not(.dark-mode) {
  background-color: #ffffff;
  color: #1f2937;
  box-shadow:
    0 4px 6px -1px rgba(0, 0, 0, 0.1),
    0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.reader-header {
  margin-bottom: 1.5rem;
  border-bottom: 1px solid transparent;
  text-align: left;
  flex-shrink: 0;
}

.chapter-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #9ca3af;
  margin-bottom: 1rem;
  font-family: sans-serif;
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

.reader-article {
  font-family: serif;
  text-align: justify;
  transition: all 0.3s;
  position: relative;
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 12px;
  margin-bottom: 1rem;
}

.reader-article::-webkit-scrollbar {
  width: 6px;
}

.reader-article::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.reader-card.dark-mode .reader-article::-webkit-scrollbar-thumb {
  background-color: rgba(255, 255, 255, 0.2);
}

.paragraph {
  margin-bottom: 1.2rem;
  text-indent: 1.5rem;
  position: relative;
}

/* 章节导航按钮样式 */
.chapter-nav {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  padding: 1rem 0;
  flex-shrink: 0;
}

.top-chapter-nav {
  margin-bottom: 0.5rem;
  justify-content: flex-start;
  padding-top: 0.5rem;
}

.bottom-chapter-nav {
  margin-top: 0;
  margin-bottom: 0.5rem;
  padding-bottom: 0.5rem;
}

.chapter-nav-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1.25rem;
  border-radius: 9999px;
  border: 1px solid #e5e7eb;
  color: #6b7280;
  background-color: transparent;
  cursor: pointer;
  transition: all 300ms;
  font-size: 0.85rem;
  font-weight: 500;
}

.chapter-nav-button:hover:not(:disabled) {
  background-color: #f9fafb;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.chapter-nav-button.dark {
  border-color: #3f3f46;
  color: #a1a1aa;
}

.chapter-nav-button.dark:hover:not(:disabled) {
  background-color: #27272a;
  color: #f3f4f6;
}

.chapter-nav-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.chapter-nav-text {
  font-size: 0.85rem;
}

.nav-icon {
  transition: transform 300ms;
}

.prev-chapter:hover .nav-icon {
  transform: translateX(-0.125rem);
}

.next-chapter:hover .nav-icon {
  transform: translateX(0.125rem);
}

.segment-transition {
  transition:
    background-color 0.3s,
    color 0.3s,
    text-decoration-color 0.3s;
}

.highlight-marker {
  background-color: #fef3c7;
  color: inherit;
  padding: 0.1rem 0;
  border-radius: 0.2rem;
  cursor: pointer;
  box-decoration-break: clone;
  -webkit-box-decoration-break: clone;
}

.dark-mode .highlight-marker {
  background-color: rgba(120, 53, 15, 0.4);
  color: inherit;
}

.highlight-marker:hover {
  background-color: #fde68a;
}

.highlight-thought {
  border-bottom: 2px dashed #f59e0b;
  padding-bottom: 0.125rem;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 0.25rem;
  box-decoration-break: clone;
  -webkit-box-decoration-break: clone;
}

.dark-mode .highlight-thought {
  border-bottom-color: #f59e0b;
}

.highlight-thought:hover {
  background-color: #f3f4f6;
  border-bottom-color: #f97316;
}

.dark-mode .highlight-thought:hover {
  background-color: #27272a;
  border-bottom-color: #f97316;
}

.highlight-wave {
  text-decoration: underline;
  text-decoration-style: wavy;
  text-decoration-thickness: 2px;
  text-decoration-color: #fb7185;
  text-underline-offset: 4px;
  cursor: pointer;
}

.highlight-line {
  text-decoration: underline;
  text-decoration-style: solid;
  text-decoration-thickness: 2px;
  text-decoration-color: #38bdf8;
  text-underline-offset: 4px;
  cursor: pointer;
}

/* 可点击标注样式 */
.clickable-annotation {
  cursor: pointer;
  transition: all 0.2s;
}

.clickable-annotation:hover {
  opacity: 0.8;
  transform: translateY(-1px);
}

.thought-icon-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-left: 0.25rem;
  vertical-align: super;
}

.thought-icon-badge {
  background-color: #dbeafe;
  font-size: 0.625rem;
  padding: 0.15rem 0.35rem;
  border-radius: 9999px;
  display: flex;
  align-items: center;
  gap: 0.125rem;
  font-family: sans-serif;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  line-height: 1;
  user-select: none;
}

.dark-mode .thought-icon-badge {
  background-color: #1e3a8a;
  color: #93c5fd;
}
</style>
