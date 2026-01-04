import request from '@/utils/request'
import type { Post } from '@/types/post'

/** 后端原始结构 */
interface RawPost {
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
export const fetchCommunityPosts = async (): Promise<Post[]> => {
  const res = await request.get('/posts')
  const list: RawPost[] = res.data.data ?? []

  return list.map((item) => ({
    id: item.postId,
    username: item.author.authorName,
    avatar: item.author.authorAvatar,
    postTime: item.publishTime,
    title: item.postTitle,
    content: item.content,
    likeCount: item.likeCount,
    commentCount: item.commentCount,
    isFollowing: item.isFollowingAuthor,
    isLiked: item.isLiked,
    book: item.mentionedFirstBook
      ? {
          bookId: item.mentionedFirstBook.bookId,
          title: item.mentionedFirstBook.bookTitle,
          author: item.mentionedFirstBook.authorName,
          cover: item.mentionedFirstBook.cover,
        }
      : null,
  }))
}

/** 获取我收到的评论 **/
interface RawComment {
  postTitle: string
  commenter: {
    username: string
    avatar: string
  }
  commentContent: string
  commentTime: string
}

export const fetchMyComments = async () => {
  const res = await request.get('/messages/my-posts/comments')
  const list: RawComment[] = res.data.data?.comments ?? []

  return list.map((item) => ({
    user: {
      username: item.commenter.username,
      avatar: item.commenter.avatar,
    },
    content: item.commentContent,
    rightCardText: item.postTitle,
    time: item.commentTime,
  }))
}

/** 获取我收到的点赞 **/
interface RawLike {
  postTitle: string
  commentContent?: string
  liker: {
    username: string
    avatar: string
  }
  likeTime: string
}

export const fetchMyLikes = async () => {
  const res = await request.get('/messages/likes')
  const list: RawLike[] = res.data.data?.likes ?? []

  return list.map((item) => ({
    user: {
      username: item.liker.username,
      avatar: item.liker.avatar,
    },
    rightCardText: item.commentContent || item.postTitle,
    time: item.likeTime,
  }))
}

/** 点赞/取消点赞参数 */
export interface LikeParams {
  commentId: number // 如果是帖子点赞，传 0
  postId: number
}

/** 点赞或取消点赞 */
export const toggleLikeApi = (data: LikeParams) => {
  return request.post('/likes', data)
}
