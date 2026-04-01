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
  rangeStart?: number // 笔记在当前章节原文中的起始字符索引
  rangeEnd?: number // 笔记在当前章节原文中的结束字符索引
}

// 2. 定义整个 Data 的结构
export interface UserNotesResponse {
  notes: NoteRaw[]
  hasMore: boolean
  nextCursor: number | null
  noteCount: number
}

// 3. 发布笔记时的请求参数
export interface CreateNoteParams {
  bookId: number
  chapterId?: number
  quote: string // 被标注的原文
  lineType?: string // 'marker' | 'wavy' | 'underline'，划线类型
  thought?: string // 想法/笔记内容
  rangeStart: number // 标注文本在章节中的起始索引
  rangeEnd: number // 标注文本在章节中的结束索引
}

// 4. 笔记响应数据
export interface NoteResponse {
  noteId: number
  quote: string
  lineType?: string // 划线类型或 null（想法类型时为 null）
  noteContent: string
  createdAt: string
  rangeStart: number // 笔记在当前章节原文中的起始字符索引
  rangeEnd: number // 笔记在当前章节原文中的结束字符索引
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
    noteCount: data.noteCount ?? 0,
  }
}

/**
 * 创建笔记（划线或想法）
 * @param params 笔记创建参数
 */
export const createNote = async (params: CreateNoteParams): Promise<NoteResponse> => {
  const res = await request.post('/notes', params)
  const data = unwrap<{ note: NoteResponse }>(res)
  return data.note
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
