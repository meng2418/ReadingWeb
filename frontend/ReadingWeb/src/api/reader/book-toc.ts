// src/api/reader/book-toc.ts - 书籍目录相关API
import request from '@/utils/request'
import type { AxiosResponse } from 'axios'

const unwrap = (res: AxiosResponse): ChapterResponse[] => {
  return res?.data?.data ?? res?.data ?? []
}

/** 章节响应接口（对应 OpenAPI 规范中的 Chapter） */
export interface ChapterResponse {
  chapterId: number
  startPage: number
  chapterNumber: number
  chapterName: string
}

/** 前端使用的目录项结构 */
export interface BookTOCItem {
  id: number
  chapterId: number
  startPage: number
  chapterNumber: number
  chapterName: string
}

/** 数据转换函数 */
const mapTOCItem = (raw: ChapterResponse): BookTOCItem => ({
  id: raw.chapterId,
  chapterId: raw.chapterId,
  startPage: raw.startPage,
  chapterNumber: raw.chapterNumber,
  chapterName: raw.chapterName,
})

/**
 * 获取书籍目录
 * @param bookId 书籍ID
 * @returns 书籍目录列表
 */
export const getBookTOC = async (bookId: string | number): Promise<BookTOCItem[]> => {
  try {
    const res = await request.get<ChapterResponse[]>(`/reader/${bookId}/chapters`)
    const rawData: ChapterResponse[] = unwrap(res)
    return rawData.map(mapTOCItem)
  } catch (error) {
    console.error(`获取书籍目录失败 (bookId: ${bookId}):`, error)
    throw error
  }
}
