// src/api/reader/book-notes.ts - 全书笔记相关API
import request from '@/utils/request'
import type { AxiosResponse } from 'axios'

const unwrapList = (res: AxiosResponse): BookNoteResponse[] => {
  const raw = res?.data?.data ?? res?.data ?? []
  return Array.isArray(raw) ? raw : []
}

/** 全书笔记响应接口 */
export interface BookNoteResponse {
  noteId: number
  bookId: number
  bookTitle: string
  chapterId: number
  chapterName: string
  noteType: string
  quote: string
  startIndex: number
  endIndex: number
  pageNumber: number
  lineType: string
  thought: string
  noteCreatedAt: string
}

/** 前端使用的全书笔记结构 */
export interface BookNote {
  id: string
  bookId: string
  bookTitle: string
  chapterId: string
  chapterName: string
  noteType: string
  quote: string
  startIndex: number
  endIndex: number
  pageNumber: number
  lineType: string
  thought: string
  createdAt: string
  updatedAt?: string
}

/** 数据转换函数 */
const mapBookNote = (raw: BookNoteResponse): BookNote => ({
  id: raw.noteId.toString(),
  bookId: raw.bookId.toString(),
  bookTitle: raw.bookTitle,
  chapterId: raw.chapterId.toString(),
  chapterName: raw.chapterName,
  noteType: raw.noteType,
  quote: raw.quote,
  startIndex: raw.startIndex,
  endIndex: raw.endIndex,
  pageNumber: raw.pageNumber,
  lineType: raw.lineType,
  thought: raw.thought,
  createdAt: raw.noteCreatedAt,
})

/**
 * 获取全书笔记列表
 */
export const getBookNotes = async (bookId: string | number): Promise<BookNote[]> => {
  try {
    const res = await request.get<BookNoteResponse[]>(`/reader/${bookId}/notes`)
    const rawData: BookNoteResponse[] = unwrapList(res)
    return rawData.map(mapBookNote)
  } catch (error) {
    // 如果API不存在，返回空数组
    console.warn(`Book notes API not available for book ${bookId}, returning empty array`)
    return []
  }
}

/**
 * 创建全书笔记
 */
export const createBookNote = async (
  bookId: string | number,
  noteData: {
    chapterId: string | number
    noteType: string
    quote: string
    startIndex: number
    endIndex: number
    pageNumber: number
    lineType: string
    thought: string
  }
): Promise<BookNote> => {
  try {
    const res = await request.post(`/books/${bookId}/notes`, noteData)
    const raw = res?.data?.data ?? res?.data ?? res
    const rawData = raw as BookNoteResponse
    return mapBookNote(rawData)
  } catch (error) {
    console.error(`Create book note API failed for book ${bookId}:`, error)
    // 创建一个模拟的笔记作为回退
    return {
      id: `temp-${Date.now()}`,
      bookId: bookId.toString(),
      bookTitle: '未知书籍', // 模拟数据
      chapterId: noteData.chapterId.toString(),
      chapterName: '未知章节', // 模拟数据
      noteType: noteData.noteType,
      quote: noteData.quote,
      startIndex: noteData.startIndex,
      endIndex: noteData.endIndex,
      pageNumber: noteData.pageNumber,
      lineType: noteData.lineType,
      thought: noteData.thought,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
  }
}

/**
 * 更新全书笔记
 */
export const updateBookNote = async (
  bookId: string | number,
  noteId: string | number,
  noteData: {
    noteType?: string
    quote?: string
    startIndex?: number
    endIndex?: number
    pageNumber?: number
    lineType?: string
    thought?: string
  }
): Promise<BookNote> => {
  try {
    const res = await request.put(`/books/${bookId}/notes/${noteId}`, noteData)
    const raw = res?.data?.data ?? res?.data ?? res
    return mapBookNote(raw as BookNoteResponse)
  } catch (error) {
    console.error(`Update book note API failed for note ${noteId}:`, error)
    throw error
  }
}

/**
 * 删除全书笔记
 */
export const deleteBookNote = async (
  bookId: string | number,
  noteId: string | number
): Promise<boolean> => {
  try {
    const res = await request.delete(`/books/${bookId}/notes/${noteId}`)
    const raw = res?.data?.data ?? res?.data ?? true
    return raw as boolean
  } catch (error) {
    console.error(`Delete book note API failed for note ${noteId}:`, error)
    return true
  }
}
