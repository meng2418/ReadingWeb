// src/api/book-detail/author-info-section.ts - AuthorInfoSection组件相关API
import request from '@/utils/request'
import { processCoverPath } from '@/utils/imagePath'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

export interface AuthorWork {
  cover: string
  bookTitle: string
  description: string
  bookId: string
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

/**
 * 获取作者作品（基于特定书籍）
 */
export const getAuthorWorks = async (bookId: string | number): Promise<AuthorWork[]> => {
  const res = await request.get<AuthorWork[]>(`/books/${bookId}/author-works`)
  const data: AuthorWork[] = unwrap(res)
  // 确保返回的数据包含所有必需字段，并处理封面路径
  return data.map((work: AuthorWork) => ({
    cover: processCoverPath(work.cover),
    bookTitle: work.bookTitle,
    description: work.description,
    bookId: work.bookId,
  }))
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

  // 将接口数据映射为前端需要的格式，并处理封面路径
  return data.works.map((work: AuthorDetailResponse['works'][0]) => ({
    id: work.bookId,
    title: work.bookTitle,
    summary: work.description,
    cover: processCoverPath(work.cover),
    readersCount: work.readCount,
    recommendationRate: work.rating,
    authorName: work.authorName
  }))
}
