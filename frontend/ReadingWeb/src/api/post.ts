// src/api/post.ts
import request from '@/utils/request'
import { processCoverPath } from '@/utils/imagePath'

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
  // 确保 postId 是数字类型
  const numericPostId = typeof postId === 'string' ? parseInt(postId, 10) : postId
  if (isNaN(numericPostId)) {
    throw new Error('无效的帖子ID')
  }
  const res = await request.get(`/posts/${numericPostId}`)
  // 处理不同的响应格式
  const data = res?.data?.data ?? res?.data
  if (!data) {
    throw new Error('帖子详情数据为空')
  }
  // 确保返回的数据结构正确
  if (!data.postId) {
    console.error('帖子详情数据格式不正确: 缺少postId', data)
    throw new Error('帖子详情数据格式不正确: 缺少postId')
  }
  
  // 如果作者信息缺失，设置默认值
  if (!data.author) {
    console.warn('帖子详情缺少作者信息，使用默认值', data)
    data.author = {
      authorId: 0,
      authorName: '未知用户',
      authorAvatar: '',
    }
  } else {
    // 确保作者信息完整
    if (!data.author.authorName) {
      data.author.authorName = '未知用户'
    }
    if (!data.author.authorAvatar) {
      data.author.authorAvatar = ''
    }
    if (!data.author.authorId) {
      data.author.authorId = 0
    }
  }
  // 处理书籍封面路径
  if (data.mentionedBooks && Array.isArray(data.mentionedBooks)) {
    data.mentionedBooks = data.mentionedBooks.map((book: any) => ({
      ...book,
      cover: processCoverPath(book.cover),
    }))
  }
  return data
}

/** 获取评论列表 */
export const getPostComments = async (postId: string | number) => {
  const res = await request.get(`/posts/${postId}/comments`)
  const list = res.data.data ?? []

  // 【关键点】：这里返回的字段必须和 CommentSection 里的 initialComments 对应
  // CommentSection 在 computed 里找的是: item.username, item.commentTime
  return list.map((item: any) => ({
    id: item.id || item.commentId,
    username: item.username || item.authorName || '匿名用户', // 兼容后端可能的不同字段
    avatar: item.avatar || '',
    content: item.content,
    commentTime: item.commentTime || item.createTime, // 兼容时间字段
    likeCount: item.likeCount || 0,
    isLiked: item.isLiked || false, // 添加点赞状态
    replies: (item.replies || []).map((r: any) => ({
      id: r.id || r.commentId,
      username: r.username,
      avatar: r.avatar,
      content: r.content,
      commentTime: r.commentTime,
      likeCount: r.likeCount || 0,
      isLiked: r.isLiked || false, // 子评论也添加点赞状态
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
