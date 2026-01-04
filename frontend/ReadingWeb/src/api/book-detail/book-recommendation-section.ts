// src/api/book-detail/book-recommendation-section.ts - BookRecommendationSection组件相关API
import request from '@/utils/request'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

export interface BookRating {
  rating: 'recommend' | 'average' | 'not_recommend'
  comment?: string
}

/**
 * 提交书籍评分
 */
export const submitBookRating = async (bookId: string | number, rating: BookRating): Promise<boolean> => {
  const res = await request.post(`/books/${bookId}/rate`, rating)
  return unwrap(res)
}

/**
 * 获取用户对书籍的评分
 */
export const getUserBookRating = async (bookId: string | number): Promise<BookRating | null> => {
  try {
    const res = await request.get<BookRating>(`/books/${bookId}/user-rating`)
    return unwrap(res)
  } catch (error) {
    // 如果用户未评分，返回null
    return null
  }
}
