// src/api/reader/book-notes.ts - 全书笔记相关API
import request from '@/utils/request'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

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
  const res = await request.get<BookNoteResponse[]>(`/books/${bookId}/notes`)
  const rawData: BookNoteResponse[] = unwrap(res)
  return rawData.map(mapBookNote)
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
  const res = await request.post(`/books/${bookId}/notes`, noteData)
  const rawData = unwrap(res)
  return mapBookNote(rawData)
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
  const res = await request.put(`/books/${bookId}/notes/${noteId}`, noteData)
  const rawData = unwrap(res)
  return mapBookNote(rawData)
}

/**
 * 删除全书笔记
 */
export const deleteBookNote = async (
  bookId: string | number,
  noteId: string | number
): Promise<boolean> => {
  const res = await request.delete(`/books/${bookId}/notes/${noteId}`)
  return unwrap(res)
}
