// src/api/topics/topic-posts-section.ts - 话题帖子列表相关API
import request from '@/utils/request'
import { processCoverPath } from '@/utils/imagePath'
import type { AxiosResponse } from 'axios'
import type { Post } from '@/types/post'

const unwrap = (res: AxiosResponse): RawTopicPost[] => {
  const data = res?.data?.data ?? res?.data
  // 确保返回的是数组
  if (Array.isArray(data)) {
    return data
  }
  // 如果data是对象且包含posts字段，尝试提取posts
  if (data && typeof data === 'object' && 'posts' in data && Array.isArray(data.posts)) {
    return data.posts
  }
  // 如果data是对象且包含list字段，尝试提取list
  if (data && typeof data === 'object' && 'list' in data && Array.isArray(data.list)) {
    return data.list
  }
  // 默认返回空数组
  return []
}

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
        cover: processCoverPath(raw.mentionedFirstBook.cover),
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
