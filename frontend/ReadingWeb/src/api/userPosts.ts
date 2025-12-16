//src/api/userPosts.ts
import request from '@/utils/request'
// src/api/posts.ts
import type { Post } from '@/types/post'

const mapPost = (raw: any): Post => ({
  id: raw.postId,
  username: `用户${raw.authorId}`, // ⚠️ 后端没给，只能兜底
  avatar: undefined, // 可选字段
  postTime: raw.createdAt,
  timestamp: Date.parse(raw.createdAt),

  title: raw.title,
  content: raw.content,

  likeCount: raw.likesCount,
  commentCount: raw.commentsCount,
  shareCount: 0,

  isFollowing: false, // UI 状态，后端一般不管
  isLiked: false,

  book: null, // 接口暂未提供
})

export const getPosts = async () => {
  const res = await request.get('/posts')
  const data = res.data

  const rawItems = Array.isArray(data.items) ? data.items : [data.items]

  return {
    list: rawItems.map(mapPost),
    total: data.total,
    page: data.page,
    limit: data.limit,
  }
}
