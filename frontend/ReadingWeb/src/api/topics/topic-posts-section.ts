// src/api/topics/topic-posts-section.ts - 话题帖子列表相关API
import request from '@/utils/request'
import type { AxiosResponse } from 'axios'
import type { Post } from '@/types/post'

const unwrap = (res: AxiosResponse): RawTopicPost[] => res?.data?.data ?? res?.data ?? []

/** 后端帖子结构 */
interface RawTopicPost {
  postId: number
  author: {
    authorName: string
    authorAvatar: string
  }
  publishTime: string
  publishLocation?: string
  isFollowingAuthor: boolean
  postTitle: string
  content: string
  mentionedFirstBook?: {
    cover: string
    bookTitle: string
    authorName: string
  }
  commentCount: number
  likeCount: number
  isLiked: boolean
}

const mapPost = (raw: RawTopicPost): Post => ({
  id: raw.postId,
  username: raw.author.authorName,
  avatar: raw.author.authorAvatar,
  postTime: raw.publishTime,
  timestamp: new Date(raw.publishTime).getTime(),
  title: raw.postTitle,
  content: raw.content,
  likeCount: raw.likeCount,
  commentCount: raw.commentCount,
  isFollowing: raw.isFollowingAuthor,
  isLiked: raw.isLiked,
  book: raw.mentionedFirstBook
    ? {
        bookId: raw.postId, // 使用postId作为bookId
        title: raw.mentionedFirstBook.bookTitle,
        author: raw.mentionedFirstBook.authorName,
        cover: raw.mentionedFirstBook.cover,
      }
    : null,
})

/** 获取话题帖子 */
export const getTopicPosts = async (
  topicId: number,
  sort: 'latest' | 'hot' | 'quality' = 'latest',
  page: number = 1,
  limit: number = 20
): Promise<Post[]> => {
  const res = await request.get<RawTopicPost[]>(`/topics/${topicId}/posts`, {
    params: { sort, page, limit }
  })
  const list: RawTopicPost[] = unwrap(res)
  return list.map(mapPost)
}
