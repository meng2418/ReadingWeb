// src/api/book-detail/book-detail-header.ts - BookDetailHeader组件相关API
import request from '@/utils/request'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

export interface BookDetailRaw {
  cover: string
  bookTitle: string
  author: string
  authorName: string
  authorId: number
  rating: number
  readCount: number
  description: string
  isFinished: boolean
  isInBookshelf: boolean
  hasStarted: boolean
  readingCount: number
  finishedCount: number
  readingStatus: 'not_started' | 'reading' | 'finished'
  wordCount: number
  publishDate: string
  isFreeForMember: boolean
  price: number // 分
  ratingDetail: {
    recommendPercent: number
    averagePercent: number
    notRecommendPercent: number
  }
  ratingCount: number
  authorBio: string
}

export interface BookDetail {
  id: string | number
  title: string
  author: string
  authorId: number
  cover: string
  description: string
  rating: number
  readCount: number
  isFinished: boolean
  isInBookshelf: boolean
  hasStarted: boolean
  readingCount: number
  finishedCount: number
  readingStatus: 'not_started' | 'reading' | 'finished'
  wordCount: number
  publishDate: string
  isFreeForMember: boolean
  price: number // 分
  ratingDetail: {
    recommendPercent: number
    averagePercent: number
    notRecommendPercent: number
  }
  ratingCount: number
  authorBio: string
}

const mapBookDetail = (raw: BookDetailRaw, bookId: string | number): BookDetail => ({
  id: bookId,
  title: raw.bookTitle,
  author: raw.authorName || raw.author,
  authorId: raw.authorId,
  cover: raw.cover,
  description: raw.description,
  rating: raw.rating,
  readCount: raw.readCount,
  isFinished: raw.isFinished,
  isInBookshelf: raw.isInBookshelf,
  hasStarted: raw.hasStarted,
  readingCount: raw.readingCount,
  finishedCount: raw.finishedCount,
  readingStatus: raw.readingStatus,
  wordCount: raw.wordCount,
  publishDate: raw.publishDate,
  isFreeForMember: raw.isFreeForMember,
  price: raw.price,
  ratingDetail: raw.ratingDetail,
  ratingCount: raw.ratingCount,
  authorBio: raw.authorBio,
})

/**
 * 获取书籍详情
 */
export const getBookDetail = async (bookId: string | number): Promise<BookDetail> => {
  const res = await request.get<BookDetailRaw>(`/books/${bookId}`)
  const rawData = unwrap(res)
  return mapBookDetail(rawData, bookId)
}

/**
 * 开始阅读
 */
export const startReading = async (bookId: string | number): Promise<{ readingStatus: 'not_started' | 'reading' | 'finished' }> => {
  const res = await request.post(`/books/${bookId}/reading`)
  return unwrap(res)
}

/**
 * 加入书架
 */
export const addToBookshelf = async (bookId: string | number): Promise<boolean> => {
  const res = await request.post('/bookshelf', { bookId })
  return unwrap(res)
}

/**
 * 从书架移除
 */
export const removeFromBookshelf = async (bookId: string | number): Promise<boolean> => {
  const res = await request.delete(`/bookshelf/${bookId}`)
  return unwrap(res)
}
