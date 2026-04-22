/**
 * 笔记管理 Composable
 * 用于处理笔记的创建、保存等逻辑，与 Pinia store 集成
 */

import { ref } from 'vue'
import { createNote, type CreateNoteParams, type NoteResponse } from '@/api/notes'
import { useReaderStore } from '@/stores/reader'
import {
  calculateTextRangeIndex,
  extractTextByRange,
} from './useTextRangeIndex'

export interface NoteData {
  selectedText: string
  lineType?: string
  thought?: string
  rangeStart: number
  rangeEnd: number
}

export function useNoteManager() {
  const readerStore = useReaderStore()

  // 状态
  const isSubmitting = ref(false)
  const errorMessage = ref('')
  const successMessage = ref('')

  /**
   * 保存笔记或划线
   * @param selectedText - 被选中的文本
   * @param lineType - 划线类型 ('marker' | 'wavy' | 'underline') 或 undefined（如果是想法）
   * @param thought - 想法内容（可选）
   * @param chapterContent - 章节的完整原文内容
   * @param contentElement - 内容容器 DOM 元素（可选，用于精确计算范围）
   */
  async function saveNote(
    selectedText: string,
    lineType: string | undefined,
    thought: string | undefined,
    chapterContent: string,
    _contentElement?: HTMLElement,
  ): Promise<NoteResponse | null> {
    if (!selectedText && !thought) {
      errorMessage.value = '请选择文本或输入想法内容'
      return null
    }

    if (!readerStore.currentBook || !readerStore.currentChapter) {
      errorMessage.value = '书籍或章节信息丢失'
      return null
    }

    // 计算范围索引
    const { rangeStart, rangeEnd } = calculateTextRangeIndex(
      selectedText || '',
      chapterContent || '',
    )

    // 验证范围索引（如果两者都是 -1 则不是致命错误，可以继续保存但警告）
    if (rangeStart === -1 && rangeEnd === -1 && selectedText) {
      console.warn('无法计算精确的范围索引，将使用默认值。选中文本：', selectedText)
    }

    try {
      isSubmitting.value = true
      errorMessage.value = ''

      const params: CreateNoteParams = {
        bookId: readerStore.currentBook.bookId,
        chapterId: readerStore.currentChapter.chapterId,
        quote: selectedText || '',
        lineType: lineType,
        thought: thought,
        rangeStart: Math.max(0, rangeStart), // 确保不是负数
        rangeEnd: Math.max(0, rangeEnd),
      }

      // 调用 API 保存笔记
      const response = await createNote(params)

      successMessage.value = thought ? '想法已保存' : '划线已保存'

      // 清除消息
      setTimeout(() => {
        successMessage.value = ''
      }, 3000)

      return response
    } catch (error: any) {
      errorMessage.value = error?.message || '保存笔记失败，请重试'
      console.error('Error saving note:', error)
      return null
    } finally {
      isSubmitting.value = false
    }
  }

  /**
   * 验证保存的笔记范围是否正确
   * @param chapterContent - 完整章节内容
   * @param rangeStart - 起始索引
   * @param rangeEnd - 结束索引
   * @returns 提取的文本
   */
  function verifyNoteRange(chapterContent: string, rangeStart: number, rangeEnd: number): string {
    return extractTextByRange(chapterContent, rangeStart, rangeEnd)
  }

  /**
   * 批量验证笔记范围（用于在章节加载时验证已保存的笔记）
   * @param notes - 笔记数组
   * @param chapterContent - 完整章节内容
   * @returns 验证结果，包含有效和无效的笔记
   */
  function verifyNoteRanges(
    notes: Array<{ rangeStart: number; rangeEnd: number; quote: string }>,
    chapterContent: string,
  ): {
    valid: typeof notes
    invalid: typeof notes
  } {
    const valid: typeof notes = []
    const invalid: typeof notes = []

    notes.forEach((note) => {
      if (note.rangeStart >= 0 && note.rangeEnd >= 0) {
        const extractedText = extractTextByRange(chapterContent, note.rangeStart, note.rangeEnd)

        // 检查提取的文本是否与原始 quote 匹配（允许一些差异）
        if (extractedText.trim() === note.quote.trim()) {
          valid.push(note)
        } else {
          invalid.push(note)
          console.warn(`Note range mismatch. Expected: "${note.quote}", Got: "${extractedText}"`)
        }
      } else {
        invalid.push(note)
      }
    })

    return { valid, invalid }
  }

  /**
   * 在更改章节时，重新计算所有笔记的范围索引
   * （用于处理跨刷新或跨章节的显示问题）
   * @param notes - 笔记数组
   * @param oldChapterContent - 旧章节内容
   * @param newChapterContent - 新章节内容
   * @returns 重新计算后的笔记
   */
  function recalculateNoteRanges(
    notes: Array<{ quote: string; rangeStart: number; rangeEnd: number }>,
    oldChapterContent: string,
    newChapterContent: string,
  ): Array<{ quote: string; rangeStart: number; rangeEnd: number }> {
    return notes.map((note) => {
      // 如果范围索引有效，直接返回（假设同一章节内容不变）
      if (note.rangeStart >= 0 && note.rangeEnd >= 0) {
        const extracted = extractTextByRange(newChapterContent, note.rangeStart, note.rangeEnd)
        if (extracted.trim() === note.quote.trim()) {
          return note
        }
      }

      // 否则根据 quote 重新计算
      const { rangeStart, rangeEnd } = calculateTextRangeIndex(note.quote, newChapterContent)

      return {
        ...note,
        rangeStart: Math.max(0, rangeStart),
        rangeEnd: Math.max(0, rangeEnd),
      }
    })
  }

  return {
    isSubmitting,
    errorMessage,
    successMessage,
    saveNote,
    verifyNoteRange,
    verifyNoteRanges,
    recalculateNoteRanges,
  }
}
