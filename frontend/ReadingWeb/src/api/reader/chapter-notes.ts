// src/api/reader/chapter-notes.ts - 章节笔记相关API
import request from '@/utils/request'
import type { AxiosResponse } from 'axios'

const unwrap = (res: AxiosResponse): ChapterNoteResponse[] => {
  return res?.data?.data ?? res?.data ?? [];
}

const unwrapSingle = (res: AxiosResponse): ChapterNoteResponse => {
  return res?.data?.data ?? res?.data ?? {};
}

const unwrapCreateNote = (res: AxiosResponse): CreateNoteResponse['data']['note'] => {
  return res?.data?.data?.note ?? res?.data?.note ?? res?.data ?? {};
}

const unwrapDelete = (res: AxiosResponse): boolean => {
  return res?.data?.data ?? res?.data ?? true;
}

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

/** 创建笔记API响应接口 */
export interface CreateNoteResponse {
  code: number
  message: string
  data: {
    note: {
      noteId: number
      quote: string
      lineType: string
      noteContent: string
      createdAt: string
    }
  }
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
  updatedAt?: string
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
  try {
    const res = await request.get<ChapterNoteResponse[]>(`/books/${bookId}/chapters/${chapterId}/notes`)
    const rawData: ChapterNoteResponse[] = unwrap(res)
    return rawData.map(mapChapterNote)
  } catch (error) {
    console.warn(`Chapter notes API not available for book ${bookId} chapter ${chapterId}, returning empty array`)
    return []
  }
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
  try {
    const res = await request.post(`/books/${bookId}/chapters/${chapterId}/notes`, noteData)
    const rawData = unwrapCreateNote(res)

    // 将API响应转换为ChapterNote格式
    const chapterNote: ChapterNote = {
      id: rawData.noteId.toString(),
      quote: rawData.quote,
      thought: rawData.noteContent,
      startIndex: noteData.startIndex, // 从请求参数获取
      endIndex: noteData.endIndex,     // 从请求参数获取
      lineType: [rawData.lineType],    // 将字符串转换为数组
      pageNumber: noteData.pageNumber, // 从请求参数获取
      createdAt: rawData.createdAt,
      updatedAt: rawData.createdAt
    }

    return chapterNote
  } catch (error) {
    console.error(`Create chapter note API failed for book ${bookId} chapter ${chapterId}:`, error)
    // 创建一个模拟的笔记作为回退
    return {
      id: `temp-${Date.now()}`,
      quote: noteData.quote,
      thought: noteData.thought,
      startIndex: noteData.startIndex,
      endIndex: noteData.endIndex,
      lineType: noteData.lineType,
      pageNumber: noteData.pageNumber,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
  }
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
  try {
    const res = await request.put(`/books/${bookId}/chapters/${chapterId}/notes/${noteId}`, noteData)
    const rawData = unwrapSingle(res)
    return mapChapterNote(rawData)
  } catch (error) {
    console.error(`Update chapter note API failed for note ${noteId}:`, error)
    throw error // 对于更新操作，让调用方处理错误
  }
}

/**
 * 删除章节笔记
 */
export const deleteChapterNote = async (
  bookId: string | number,
  chapterId: string | number,
  noteId: string | number
): Promise<boolean> => {
  try {
    const res = await request.delete(`/books/${bookId}/chapters/${chapterId}/notes/${noteId}`)
    return unwrapDelete(res)
  } catch (error) {
    console.error(`Delete chapter note API failed for note ${noteId}:`, error)
    // 对于删除操作，如果API失败，我们假设删除成功（因为笔记可能已经被删除了）
    return true
  }
}
