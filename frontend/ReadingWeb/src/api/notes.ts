import request from '@/utils/request'
import type { AxiosResponse } from 'axios'

// 1. 定义单条笔记的原始结构（根据接口文档）
export interface NoteRaw {
  markId: number
  bookId: number
  bookTitle: string
  chapterId: number
  chapterName: string
  quote: string // 划线内容
  startIndex: number
  endIndex: number
  pageNumber: number
  lineTypes: string[] // 划线类型数组
  noteContent: string // 想法内容
  noteCreatedAt: string
  noteType: 'highlight' | 'thought'
}

// 2. 定义整个 Data 的结构
export interface UserNotesResponse {
  notes: NoteRaw[]
  hasMore: boolean
  nextCursor: number | null
  noteCount: number
}

// 3. 辅助函数
const unwrap = <T>(res: AxiosResponse): T => res?.data?.data ?? res?.data ?? {}

/**
 * 获取用户笔记瀑布流
 * @param cursor 游标，第一次请求不传，后续传上次返回的nextCursor
 * @param limit 每页数量，默认20，最大50
 */
export const getUserNotes = async (params?: {
  cursor?: number
  limit?: number
}): Promise<UserNotesResponse> => {
  const res = await request.get('/user/notes', { params })
  const data = unwrap<UserNotesResponse>(res)
  
  return {
    notes: data.notes ?? [],
    hasMore: data.hasMore ?? false,
    nextCursor: data.nextCursor ?? null,
    noteCount: data.noteCount ?? 0
  }
}

/**
 * 删除笔记（想法）
 * Path: /notes/{noteId}
 */
export const deleteUserNote = (noteId: number | string) => {
  return request({
    url: `/notes/${noteId}`,
    method: 'delete',
  })
}
