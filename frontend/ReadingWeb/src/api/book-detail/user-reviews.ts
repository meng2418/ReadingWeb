// src/api/book-detail/user-reviews.ts - UserReviews组件相关API
import request from '@/utils/request'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

export interface BookReview {
  avatar: string
  username: string
  rating: 'recommend' | 'average' | 'not_recommend'
  reviewTime: string
  content: string
}

/**
 * 获取书籍评论
 */
export const getBookReviews = async (bookId: string | number): Promise<BookReview[]> => {
  const res = await request.get<BookReview[]>(`/books/${bookId}/reviews`)
  return unwrap(res)
}
