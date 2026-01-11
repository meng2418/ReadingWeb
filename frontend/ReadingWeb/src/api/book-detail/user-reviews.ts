// src/api/book-detail/user-reviews.ts - UserReviews组件相关API
import request from '@/utils/request'
import type { AxiosResponse } from 'axios'

const unwrap = (res: AxiosResponse) => res?.data?.data ?? res?.data ?? {}

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

/**
 * 用户书评瀑布流响应结构
 */
export interface UserBookReview {
  cover: string
  bookTitle: string
  reviewDate: string
  helpfulCount: number
  reviewContent: string
  rating: 'recommend' | 'average' | 'bad'
}

export interface UserBookReviewsResponse {
  reviews: UserBookReview[]
  hasMore: boolean
  nextCursor: number | null
  reviewCount: number
}

/**
 * 获取登录用户书评瀑布流
 * @param cursor 游标，第一次请求不传，后续传上次返回的nextCursor
 * @param limit 每页数量，默认20，最大50
 */
export const getUserBookReviews = async (params?: {
  cursor?: number
  limit?: number
}): Promise<UserBookReviewsResponse> => {
  const res = await request.get('/user/book-reviews', { params })
  const data = unwrap<UserBookReviewsResponse>(res)
  
  return {
    reviews: data.reviews ?? [],
    hasMore: data.hasMore ?? false,
    nextCursor: data.nextCursor ?? null,
    reviewCount: data.reviewCount ?? 0
  }
}
