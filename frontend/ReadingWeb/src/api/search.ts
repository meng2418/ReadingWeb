// src/api/search.ts
import request from '@/utils/request'
import { processCoverPath } from '@/utils/imagePath'

// 后端返回的搜索响应结构
export interface SearchResponse {
  books: Array<{
    bookId: number
    bookTitle: string
    authorName: string
    authorId: number
    cover: string
    readingStatus?: string
    description: string
    rating?: number
    readCount?: number
  }>
  authors: Array<{
    authorId: number
    authorName: string
    avatar: string
    followerCount: number
    representativeWorks: string[]
    authorBio: string
  }>
}

/**
 * 搜索接口 - 同时搜索书籍和作者
 * @param keyword 搜索关键词
 */
export const search = async (keyword: string): Promise<SearchResponse> => {
  const res = await request.get<SearchResponse>('/search', {
    params: { keyword },
  })
  
  // SearchController 直接返回 SearchResponseDto，所以数据在 res.data 中
  const data = res?.data || { books: [], authors: [] }
  
  // 确保 books 和 authors 是数组
  if (!Array.isArray(data.books)) {
    data.books = []
  }
  if (!Array.isArray(data.authors)) {
    data.authors = []
  }
  
  // 处理书籍封面路径
  data.books = data.books.map((book: any) => ({
    ...book,
    cover: processCoverPath(book.cover || ''),
  }))
  
  // 处理作者头像路径
  data.authors = data.authors.map((author: any) => ({
    ...author,
    avatar: processCoverPath(author.avatar || ''),
  }))
  
  return data
}
