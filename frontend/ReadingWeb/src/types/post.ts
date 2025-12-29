// src/types/post.ts
import type { BookListItem } from '@/types/book'

// 用户在帖子中的简要信息
export interface PostUserSummary {
  id: number | string
  username: string
  avatar?: string
  bio?: string
}

// 帖子关联的书籍信息（用于卡片展示）
export type PostBookSummary = Pick<BookListItem, 'title' | 'author' | 'cover'> & {
  id?: string | number
}

// 帖子基础结构（不含 UI 派生字段）
export interface PostBase {
  id: number
  username: string
  avatar?: string
  postTime: string
  timestamp?: number
  title?: string
  content: string
  likeCount: number
  commentCount: number
  shareCount?: number
  isFollowing: boolean
  isLiked: boolean
  book?: PostBookSummary | null
}

// 帖子在列表中的完整模型（可按需扩展）
export type Post = PostBase
export type PostFilter = 'latest' | 'hot' | 'featured'

// 帖子点赞记录（用于详情页 LikeSection 等）
export interface PostLike {
  id: number
  user: PostUserSummary
  timestamp: string
}

// PostCard 组件对外暴露的事件签名
export interface PostCardEmits {
  (e: 'follow-change', isFollowing: boolean): void
  (e: 'like', likeCount: number, isLiked: boolean): void
  (e: 'comment'): void
  (e: 'share'): void
}
