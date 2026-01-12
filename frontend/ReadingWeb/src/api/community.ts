import request from '@/utils/request'
import { processCoverPath } from '@/utils/imagePath'
import type { Post } from '@/types/post'

/** 后端原始结构 */
interface RawPost {
  postId: number
  author: {
    authorId: number
    authorName: string
    authorAvatar: string
  }
  publishTime: string
  publishLocation?: string
  isFollowingAuthor: boolean
  postTitle: string
  content: string
  mentionedFirstBook?: {
    bookId: number
    cover: string
    bookTitle: string
    authorName: string
  }
  commentCount: number
  likeCount: number
  isLiked: boolean
}

/** 获取社区帖子 */
export const fetchCommunityPosts = async (type: 'square' | 'following' = 'square'): Promise<Post[]> => {
  const res = await request.get('/posts', {
    params: {
      type,
      page: 1,
      limit: 20,
    },
  })
  const list: RawPost[] = res.data.data ?? []

  return list.map((item) => ({
    id: item.postId,
    username: item.author?.authorName || '未知用户',
    avatar: item.author?.authorAvatar || '',
    authorId: item.author?.authorId || 0, // 添加 authorId，如果缺失则设为0
    postTime: item.publishTime,
    title: item.postTitle,
    content: item.content,
    likeCount: item.likeCount || 0,
    commentCount: item.commentCount || 0,
    isFollowing: item.isFollowingAuthor || false,
    isLiked: item.isLiked || false,
    book: item.mentionedFirstBook
      ? {
          bookId: item.mentionedFirstBook.bookId,
          title: item.mentionedFirstBook.bookTitle,
          author: item.mentionedFirstBook.authorName,
          cover: processCoverPath(item.mentionedFirstBook.cover),
        }
      : null,
  }))
}

/** 获取我收到的评论瀑布流 **/
interface RawComment {
  commentId: number
  postId: number
  postTitle: string
  commenter: {
    userId: number
    username: string
    avatar: string
  }
  commentContent: string
  commentTime: string
  parentCommentId?: number
}

export interface MyCommentsResponse {
  comments: RawComment[]
  hasMore: boolean
  nextCursor: number | null
}

export const fetchMyComments = async (params?: {
  cursor?: number
  limit?: number
}): Promise<MyCommentsResponse> => {
  const res = await request.get('/messages/my-posts/comments', { params })
  const data = res.data.data ?? {}
  
  return {
    comments: (data.comments ?? []).map((item: RawComment) => ({
      user: {
        username: item.commenter.username,
        avatar: item.commenter.avatar,
      },
      content: item.commentContent,
      rightCardText: item.postTitle,
      time: item.commentTime,
      commentId: item.commentId,
      postId: item.postId,
      parentCommentId: item.parentCommentId,
    })),
    hasMore: data.hasMore ?? false,
    nextCursor: data.nextCursor ?? null,
  }
}

/** 获取我收到的点赞瀑布流 **/
interface RawLike {
  likeId: number
  targetType: 'post' | 'comment'
  targetId: number
  postTitle: string
  commentContent?: string
  liker: {
    userId: number
    username: string
    avatar: string
  }
  likeTime: string
}

export interface MyLikesResponse {
  likes: RawLike[]
  hasMore: boolean
  nextCursor: number | null
}

export const fetchMyLikes = async (params?: {
  cursor?: number
  limit?: number
}): Promise<MyLikesResponse> => {
  const res = await request.get('/messages/likes', { params })
  const data = res.data.data ?? {}
  
  return {
    likes: (data.likes ?? []).map((item: RawLike) => ({
      user: {
        username: item.liker.username,
        avatar: item.liker.avatar,
      },
      rightCardText: item.commentContent || item.postTitle,
      time: item.likeTime,
      likeId: item.likeId,
      targetType: item.targetType,
      targetId: item.targetId,
    })),
    hasMore: data.hasMore ?? false,
    nextCursor: data.nextCursor ?? null,
  }
}

/** 点赞/取消点赞参数 */
export interface LikeParams {
  commentId: number // 如果是帖子点赞，传 0
  postId: number
  targetId: number // 目标ID：帖子ID或评论ID
  targetType: 'post' | 'comment' // 目标类型
}

/** 点赞或取消点赞 */
export const toggleLikeApi = (data: LikeParams) => {
  // 根据接口文档，需要传递 targetId 和 targetType
  const requestData = {
    postId: data.postId,
    commentId: data.commentId,
    targetId: data.targetId,
    targetType: data.targetType,
  }
  return request.post('/likes', requestData)
}
