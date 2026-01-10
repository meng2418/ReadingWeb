// src/api/reader/book-toc.ts - 书籍目录相关API
import request from '@/utils/request'
import type { AxiosResponse } from 'axios'

const unwrap = (res: AxiosResponse): BookTOCItemResponse[] => {
  return res?.data?.data ?? res?.data ?? []
}

/** 书籍目录项响应接口 */
export interface BookTOCItemResponse {
  id?: number  // 可选的章节ID，如果后端提供
  startPage: number
  chapterNumber: number
  chapterName: string
}

/** 前端使用的目录项结构 */
export interface BookTOCItem {
  id: number
  startPage: number
  chapterNumber: number
  chapterName: string
}

/** 数据转换函数 */
const mapTOCItem = (raw: BookTOCItemResponse): BookTOCItem => ({
  id: raw.id ?? raw.chapterNumber,
  startPage: raw.startPage,
  chapterNumber: raw.chapterNumber,
  chapterName: raw.chapterName,
})

/**
 * 获取书籍目录
 */
export const getBookTOC = async (bookId: string | number): Promise<BookTOCItem[]> => {
  // 在开发环境下，直接返回模拟数据，避免404错误
  if (import.meta.env.DEV) {
    console.log(`DEV: Using mock TOC data for book ${bookId}`)
    return [
      { id: 1, startPage: 0, chapterNumber: 1, chapterName: '译者序：爱与艺术的交响乐' },
      { id: 2, startPage: 9, chapterNumber: 2, chapterName: '第一章：初遇' },
      { id: 3, startPage: 19, chapterNumber: 3, chapterName: '第二章：书信的火焰' },
      { id: 4, startPage: 29, chapterNumber: 4, chapterName: '第三章：矛盾与和解' },
      { id: 5, startPage: 39, chapterNumber: 5, chapterName: '第四章：别离与永恒' }
    ]
  }

  try {
    const res = await request.get<BookTOCItemResponse[]>(`/books/${bookId}/toc`)
    const rawData: BookTOCItemResponse[] = unwrap(res)
    return rawData.map(mapTOCItem)
  } catch (error) {
    // 如果API不存在，返回空数组，让调用方处理
    console.warn(`TOC API not available for book ${bookId}, returning empty array`)
    return []
  }
}
