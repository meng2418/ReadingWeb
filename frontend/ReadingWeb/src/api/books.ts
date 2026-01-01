// src/api/books.ts
import request from '@/utils/request'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

export interface BookDetailRaw {
  cover: string
  bookTitle: string
  author: string
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
  author: raw.author,
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
}

export interface RelatedBook {
  cover: string
  title: string
  description: string
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
  return unwrap(res)
}

export const getRelatedBooks = async (bookId: string | number): Promise<RelatedBook[]> => {
  const res = await request.get<RelatedBook[]>(`/books/${bookId}/related`)
  return unwrap(res)
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
  return data.works.map(work => ({
    id: work.bookId,
    title: work.bookTitle,
    summary: work.description,
    cover: work.cover,
    readersCount: work.readCount,
    recommendationRate: work.rating,
    authorName: work.authorName
  }))
}
