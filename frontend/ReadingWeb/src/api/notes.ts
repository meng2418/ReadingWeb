import request from '@/utils/request'

// 1. 定义单条笔记的原始结构
export interface NoteRaw {
  markId: number
  bookId: number
  bookTitle: string
  chapterName: string
  quote: string // 划线内容
  noteContent: string // 想法内容
  noteCreatedAt: string
  noteType: 'highlight' | 'thought'
}

// 2. 定义整个 Data 的结构
interface NoteData {
  notes: NoteRaw[]
  hasMore: boolean
  nextCursor: string
  noteCount: number
}

// 3. 辅助函数（参考你 profile.ts 里的逻辑）
const unwrap = <T>(res: any): T => res?.data?.data ?? res?.data ?? {}

/**
 * 获取用户笔记列表
 */
export const getUserNotes = async (): Promise<NoteRaw[]> => {
  // 注意：这里不传泛型给 get，而是交给 unwrap 处理类型
  const res = await request.get('/user/notes')
  const data = unwrap<NoteData>(res)

  // 确保返回的是数组
  return data.notes ?? []
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
