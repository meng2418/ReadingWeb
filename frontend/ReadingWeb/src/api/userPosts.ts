//src/api/userPosts.ts
import request from '@/utils/request'
// src/api/posts.ts
import type { Post } from '@/types/post'

const mapPost = (raw: any): Post => ({
  id: raw.postId,
  username: raw.authorName ?? `用户${raw.postId}`,
  avatar: raw.authorAvatar,
  postTime: raw.publishTime,
  timestamp: raw.publishTime ? Date.parse(raw.publishTime) : undefined,

  title: raw.postTitle,
  content: raw.content,

  likeCount: raw.likeCount ?? 0,
  commentCount: raw.commentCount ?? 0,
  shareCount: 0,

  isFollowing: raw.isFollowingAuthor ?? false,
  isLiked: raw.isLiked ?? false,

  book: raw.mentionedBooks?.[0]
    ? {
        id: raw.mentionedBooks[0].bookId ?? raw.mentionedBooks[0].bookTitle,
        title: raw.mentionedBooks[0].bookTitle,
        author: raw.mentionedBooks[0].author,
        cover: raw.mentionedBooks[0].cover,
      }
    : null,
})

export const getPosts = async (type: 'square' | 'following' = 'square', page = 1, limit = 20) => {
  const res = await request.get('/posts', { params: { type, page, limit } })
  const data = res?.data?.data ?? res?.data ?? []
  const rawItems = Array.isArray(data) ? data : data.items ?? []

  return {
    list: rawItems.map(mapPost),
    total: data.total ?? rawItems.length,
    page: data.page ?? page,
    limit: data.limit ?? limit,
  }
}
