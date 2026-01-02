// src/api/post.ts
import request from '@/utils/request'

// 定义接口返回的类型（可选，方便排查）
interface RawComment {
  id: number
  username: string
  avatar: string
  content: string
  commentTime: string
  likeCount: number
  replies?: RawComment[]
}
/** 帖子详情API响应结构 */
export interface PostDetailResponse {
  postId: number
  author: {
    authorId: number
    authorAvatar: string
    authorName: string
  }
  publishTime: string
  publishLocation?: string
  isFollowingAuthor: boolean
  postTitle: string
  content: string
  mentionedBooks: Array<{
    bookId: number
    cover: string
    bookTitle: string
    authorName: string
    description: string
  }>
  commentCount: number
  likeCount: number
  isLiked: boolean
  topics: string[]
}

/** 获取帖子详情 */
export const getPostDetail = async (postId: string | number): Promise<PostDetailResponse> => {
  const res = await request.get(`/posts/${postId}`)
  return res.data.data
}

/** 获取评论列表 */
export const getPostComments = async (postId: string | number) => {
  const res = await request.get(`/posts/${postId}/comments`)
  const list = res.data.data ?? []

  // 【关键点】：这里返回的字段必须和 CommentSection 里的 initialComments 对应
  // CommentSection 在 computed 里找的是: item.username, item.commentTime
  return list.map((item: any) => ({
    id: item.id,
    username: item.username || item.authorName || '匿名用户', // 兼容后端可能的不同字段
    avatar: item.avatar || '',
    content: item.content,
    commentTime: item.commentTime || item.createTime, // 兼容时间字段
    likeCount: item.likeCount || 0,
    replies: (item.replies || []).map((r: any) => ({
      id: r.id,
      username: r.username,
      avatar: r.avatar,
      content: r.content,
      commentTime: r.commentTime,
    })),
  }))
}

/** 获取点赞列表 */
export const getPostLikes = async (postId: string | number) => {
  const res = await request.get(`/posts/${postId}/likes/detailed`)
  const data = res.data.data

  // LikeSection 需要 likes 数组，里面包含 userId, username, avatar
  return {
    totalLikes: data?.totalLikes ?? 0,
    likedUsers: (data?.likedUsers ?? []).map((user: any) => ({
      userId: user.userId,
      username: user.username,
      avatar: user.avatar,
      likeTime: user.likeTime,
    })),
  }
}

export interface PublishCommentResponse {
  code: number
  data: {
    comment: {
      commentId: number
      username: string
      avatar: string
      content: string
      commentTime: string
      [property: string]: any
    }
    commentCount: number
  }
  message: string
}

export const publishComment = async (postId: string | number, content: string) => {
  const res = await request.post<PublishCommentResponse>(`/posts/${postId}/comments`, { content })
  return res.data.data // 返回包含 comment 和 commentCount 的对象
}

/** 回复评论的请求参数 */
export interface ReplyCommentRequest {
  content: string
}

/** 回复评论的响应结构 */
export interface ReplyCommentResponse {
  code: number
  data: {
    comment: {
      avatar: string
      commentId: number
      commentTime: string
      content: string
      parentCommentId: number
      replyToUsername: string
      userId: number
      username: string
      [property: string]: any
    }
    replyCount: number
    [property: string]: any
  }
  message: string
  [property: string]: any
}

/** 回复评论 */
export const replyComment = async (
  commentId: number,
  content: string,
): Promise<ReplyCommentResponse['data']> => {
  const res = await request.post<ReplyCommentResponse>(`/comments/${commentId}/reply`, {
    content,
  })
  return res.data.data
}
