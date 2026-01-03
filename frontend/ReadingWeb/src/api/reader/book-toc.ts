// src/api/reader/book-toc.ts - 书籍目录相关API
import request from '@/utils/request'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

/** 书籍目录项响应接口 */
export interface BookTOCItemResponse {
  startPage: number
  chapterNumber: number
  chapterName: string
}

/** 前端使用的目录项结构 */
export interface BookTOCItem {
  id: string
  startPage: number
  chapterNumber: number
  chapterName: string
}

/** 数据转换函数 */
const mapTOCItem = (raw: BookTOCItemResponse): BookTOCItem => ({
  id: raw.chapterNumber.toString(),
  startPage: raw.startPage,
  chapterNumber: raw.chapterNumber,
  chapterName: raw.chapterName,
})

/**
 * 获取书籍目录
 */
export const getBookTOC = async (bookId: string | number): Promise<BookTOCItem[]> => {
  const res = await request.get<BookTOCItemResponse[]>(`/books/${bookId}/toc`)
  const rawData: BookTOCItemResponse[] = unwrap(res)
  return rawData.map(mapTOCItem)
}
