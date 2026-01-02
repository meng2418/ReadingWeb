// src/api/books.ts
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

export interface BookReview {
  avatar: string
  username: string
  rating: 'recommend' | 'average' | 'not_recommend'
  reviewTime: string
  content: string
}

export interface AuthorWork {
  cover: string
  bookTitle: string
  description: string
  bookId: string
}

export interface RelatedBook {
  cover: string
  title: string
  description: string
  bookId: number
}

export interface AuthorDetail {
  id: number
  name: string
  description: string
  worksCount: number
}

export interface AuthorWorkWithId {
  id: number
  title: string
  summary: string
  cover?: string
  readersCount: number
  recommendationRate: number
  authorName?: string
}

// 作者详情接口响应类型
export interface AuthorDetailResponse {
  authorId: number
  authorName: string
  authorBio: string
  followerCount: number
  isFollowing: boolean
  works: Array<{
    bookId: number
    bookTitle: string
    authorName: string
    cover: string
    rating: number
    readCount: number
    description: string
  }>
  workCount: number
}

export const getBookReviews = async (bookId: string | number): Promise<BookReview[]> => {
  const res = await request.get<BookReview[]>(`/books/${bookId}/reviews`)
  return unwrap(res)
}

export const getAuthorWorks = async (bookId: string | number): Promise<AuthorWork[]> => {
  const res = await request.get<AuthorWork[]>(`/books/${bookId}/author-works`)
  const data: AuthorWork[] = unwrap(res)
  // 确保返回的数据包含所有必需字段
  return data.map((work: AuthorWork) => ({
    cover: work.cover,
    bookTitle: work.bookTitle,
    description: work.description,
    bookId: work.bookId,
  }))
}

export const getRelatedBooks = async (bookId: string | number): Promise<RelatedBook[]> => {
  const res = await request.get<RelatedBook[]>(`/books/${bookId}/related`)
  const data: RelatedBook[] = unwrap(res)
  // 确保返回的数据包含所有必需字段
  return data.map((book: RelatedBook) => ({
    cover: book.cover,
    title: book.title,
    description: book.description,
    bookId: book.bookId,
  }))
}

export const getBookDetail = async (bookId: string | number): Promise<BookDetail> => {
  const res = await request.get<BookDetailRaw>(`/books/${bookId}`)
  const rawData = unwrap(res)
  return mapBookDetail(rawData, bookId)
}

/**
 * 获取作者详情
 */
export const getAuthorDetail = async (authorId: number): Promise<AuthorDetail> => {
  const res = await request.get<AuthorDetailResponse>(`/authors/${authorId}`)
  const data = unwrap(res)

  // 将接口数据映射为前端需要的格式
  return {
    id: data.authorId,
    name: data.authorName,
    description: data.authorBio,
    worksCount: data.workCount
  }
}

/**
 * 获取作者所有作品
 */
export const getAuthorAllWorks = async (authorId: number): Promise<AuthorWorkWithId[]> => {
  const res = await request.get<AuthorDetailResponse>(`/authors/${authorId}`)
  const data = unwrap(res)

  // 将接口数据映射为前端需要的格式
  return data.works.map((work: AuthorDetailResponse['works'][0]) => ({
    id: work.bookId,
    title: work.bookTitle,
    summary: work.description,
    cover: work.cover,
    readersCount: work.readCount,
    recommendationRate: work.rating,
    authorName: work.authorName
  }))
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

/**
 * 开始阅读
 */
export const startReading = async (bookId: string | number): Promise<{ readingStatus: 'not_started' | 'reading' | 'finished' }> => {
  const res = await request.post(`/books/${bookId}/reading`)
  return unwrap(res)
}
