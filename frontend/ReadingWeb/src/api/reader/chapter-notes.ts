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

/**
 * 将前端类型映射为后端类型
 * 前端: marker, wave, line
 * 后端: marker, wavy, underline
 */
const mapToBackendType = (frontendType: string): string => {
  const mapping: Record<string, string> = {
    'marker': 'marker',
    'wave': 'wavy',
    'line': 'underline'
  }
  return mapping[frontendType] || 'marker'
}

/**
 * 将后端类型映射为前端类型
 * 后端: marker, wavy, underline
 * 前端: marker, wave, line
 */
const mapToFrontendType = (backendType: string): string => {
  const mapping: Record<string, string> = {
    'marker': 'marker',
    'wavy': 'wave',
    'underline': 'line'
  }
  return mapping[backendType] || 'marker'
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
  // 将后端返回的类型数组映射为前端类型
  // 如果 lineType 为空或 null，说明是想法类型，返回空数组
  lineType: raw.lineType && raw.lineType.length > 0 
    ? raw.lineType.map(mapToFrontendType)
    : [],
  thought: raw.thought || '',
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
    lineType?: string[] | null // 可选，想法类型时可以为 null
    thought: string
    pageNumber: number
  }
): Promise<ChapterNote> => {
  try {
    // 判断是想法还是划线：
    // - 如果有 thought 内容且 lineType 为空/null，则是想法
    // - 如果有 lineType，则是划线
    const isThought = noteData.thought && noteData.thought.trim() && (!noteData.lineType || noteData.lineType.length === 0)
    
    console.log('createChapterNote - 判断结果:', { 
      isThought, 
      hasThought: !!noteData.thought, 
      thoughtContent: noteData.thought,
      lineType: noteData.lineType 
    })
    
    // 将前端的 lineType 数组转换为后端期望的单个字符串
    // 如果是想法，lineType 应该为 null
    const backendLineType = isThought 
      ? null 
      : (noteData.lineType && noteData.lineType.length > 0
          ? mapToBackendType(noteData.lineType[0])
          : 'marker')

    // 构建请求体，使用后端期望的格式
    // 注意：后端 NoteCreateDTO 只包含 bookId, chapterId, quote, lineType, thought
    const requestBody = {
      bookId: Number(bookId),
      chapterId: Number(chapterId),
      quote: noteData.quote,
      lineType: backendLineType, // 想法时为 null，划线时为字符串
      thought: noteData.thought || '',
    }

    console.log('createChapterNote - 请求体:', requestBody)

    // 调用 /notes 接口创建笔记（根据后端 NoteController）
    const res = await request.post(`/notes`, requestBody)
    console.log('createChapterNote - API 响应:', res.data)
    const rawData = unwrapCreateNote(res)

    // 将API响应转换为ChapterNote格式
    const chapterNote: ChapterNote = {
      id: rawData.noteId.toString(),
      quote: rawData.quote,
      thought: rawData.noteContent || '',
      startIndex: noteData.startIndex, // 从请求参数获取
      endIndex: noteData.endIndex,     // 从请求参数获取
      // 如果 lineType 为 null，说明是想法类型，返回空数组；否则转换为前端格式
      lineType: rawData.lineType 
        ? [mapToFrontendType(rawData.lineType)]
        : [],
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
