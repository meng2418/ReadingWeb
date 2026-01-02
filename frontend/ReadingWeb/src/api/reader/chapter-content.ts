// src/api/reader/chapter-content.ts - 阅读器章节内容相关API
import request from '@/utils/request'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

/** 章节内容响应接口 */
export interface ChapterContentResponse {
  chapterId: number
  chapterTitle: string
  chapterNumber: number
  content: string
  totalWords: number
  totalPages: number
  wordsPerPage: number
  requireExperienceCard: boolean
  isLocked: boolean
  prevChapterId: number | null
  nextChapterId: number | null
  lastReadPosition: number
}

/** 前端使用的章节内容结构 */
export interface ChapterContent {
  id: string
  title: string
  number: number
  content: string
  totalWords: number
  totalPages: number
  wordsPerPage: number
  requireExperienceCard: boolean
  isLocked: boolean
  prevChapterId: string | null
  nextChapterId: string | null
  lastReadPosition: number
}

/** 数据转换函数 */
const mapChapterContent = (raw: ChapterContentResponse): ChapterContent => ({
  id: raw.chapterId.toString(),
  title: raw.chapterTitle,
  number: raw.chapterNumber,
  content: raw.content,
  totalWords: raw.totalWords,
  totalPages: raw.totalPages,
  wordsPerPage: raw.wordsPerPage,
  requireExperienceCard: raw.requireExperienceCard,
  isLocked: raw.isLocked,
  prevChapterId: raw.prevChapterId?.toString() || null,
  nextChapterId: raw.nextChapterId?.toString() || null,
  lastReadPosition: raw.lastReadPosition,
})

/**
 * 获取章节内容
 */
export const getChapterContent = async (bookId: string | number, chapterId: string | number): Promise<ChapterContent> => {
  const res = await request.get<ChapterContentResponse>(`/books/${bookId}/chapters/${chapterId}`)
  const rawData = unwrap(res)
  return mapChapterContent(rawData)
}
