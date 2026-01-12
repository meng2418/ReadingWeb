// src/api/book-detail/related-recommendations.ts - RelatedRecommendations组件相关API
import request from '@/utils/request'
import { processCoverPath } from '@/utils/imagePath'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

export interface RelatedBook {
  cover: string
  title: string
  description: string
  bookId: number
}

/**
 * 获取相关书籍推荐
 */
export const getRelatedBooks = async (bookId: string | number): Promise<RelatedBook[]> => {
  const res = await request.get<RelatedBook[]>(`/books/${bookId}/related`)
  const data: RelatedBook[] = unwrap(res)
  // 确保返回的数据包含所有必需字段，并处理封面路径
  return data.map((book: RelatedBook) => ({
    cover: processCoverPath(book.cover),
    title: book.title,
    description: book.description,
    bookId: book.bookId,
  }))
}
