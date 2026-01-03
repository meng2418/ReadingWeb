// src/api/reader/chapter-notes.ts - 章节笔记相关API
import request from '@/utils/request'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

/** 章节笔记响应接口 */
export interface ChapterNoteResponse {
  noteId: number
  quote: string
  startIndex: number
  endIndex: number
  lineType: string[]
  thought: string
  createdAt: string
  pageNumber: number
}

/** 前端使用的章节笔记结构 */
export interface ChapterNote {
  id: string
  quote: string
  startIndex: number
  endIndex: number
  lineType: string[]
  thought: string
  createdAt: string
  pageNumber: number
}

/** 数据转换函数 */
const mapChapterNote = (raw: ChapterNoteResponse): ChapterNote => ({
  id: raw.noteId.toString(),
  quote: raw.quote,
  startIndex: raw.startIndex,
  endIndex: raw.endIndex,
  lineType: raw.lineType,
  thought: raw.thought,
  createdAt: raw.createdAt,
  pageNumber: raw.pageNumber,
})

/**
 * 获取章节笔记列表
 */
export const getChapterNotes = async (bookId: string | number, chapterId: string | number): Promise<ChapterNote[]> => {
  const res = await request.get<ChapterNoteResponse[]>(`/books/${bookId}/chapters/${chapterId}/notes`)
  const rawData: ChapterNoteResponse[] = unwrap(res)
  return rawData.map(mapChapterNote)
}

/**
 * 创建章节笔记
 */
export const createChapterNote = async (
  bookId: string | number,
  chapterId: string | number,
  noteData: {
    quote: string
    startIndex: number
    endIndex: number
    lineType: string[]
    thought: string
    pageNumber: number
  }
): Promise<ChapterNote> => {
  const res = await request.post(`/books/${bookId}/chapters/${chapterId}/notes`, noteData)
  const rawData = unwrap(res)
  return mapChapterNote(rawData)
}

/**
 * 更新章节笔记
 */
export const updateChapterNote = async (
  bookId: string | number,
  chapterId: string | number,
  noteId: string | number,
  noteData: {
    quote?: string
    startIndex?: number
    endIndex?: number
    lineType?: string[]
    thought?: string
    pageNumber?: number
  }
): Promise<ChapterNote> => {
  const res = await request.put(`/books/${bookId}/chapters/${chapterId}/notes/${noteId}`, noteData)
  const rawData = unwrap(res)
  return mapChapterNote(rawData)
}

/**
 * 删除章节笔记
 */
export const deleteChapterNote = async (
  bookId: string | number,
  chapterId: string | number,
  noteId: string | number
): Promise<boolean> => {
  const res = await request.delete(`/books/${bookId}/chapters/${chapterId}/notes/${noteId}`)
  return unwrap(res)
}
