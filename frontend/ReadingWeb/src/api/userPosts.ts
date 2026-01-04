import request from '@/utils/request'
import type { Post } from '@/types/post'

/**
 * 接口返回原始结构（后端）
 */
interface UserPostsRaw {
  posts: any[]
  hasMore: boolean
  nextCursor?: string
  postCount: number
  commentCount: number
  likeCount: number
}

interface UserPostsResponse {
  code: number
  message: string
  data: UserPostsRaw
}

/**
 * 前端真正使用的返回结构
 */
export interface GetUserPostsResult {
  list: Post[]
  total: number
  hasMore: boolean
  nextCursor?: string
}

/**
 * 获取「我的帖子」
 */
export async function getPosts(params?: {
  cursor?: string
  limit?: number
}): Promise<GetUserPostsResult> {
  const res = await request.get<UserPostsResponse>('/user/posts', {
    params,
  })

  const raw = res.data.data

  return {
    list: raw.posts.map(mapPost),
    total: raw.postCount,
    hasMore: raw.hasMore,
    nextCursor: raw.nextCursor,
  }
}

/**
 * 后端字段 → Post 类型映射
 */
function mapPost(item: any): Post {
  return {
    id: item.postId,
    username: item.authorName,
    avatar: item.authorAvatar,
    postTime: item.publishTime,
    content: item.content,
    title: item.postTitle,

    likeCount: item.likeCount,
    commentCount: item.commentCount,
    isLiked: item.isLiked,
    isFollowing: item.isFollowingAuthor,

    // 当前 PostCard 只展示一个书，取第一个即可
    book: item.mentionedBooks?.length
      ? {
          title: item.mentionedBooks[0].bookTitle,
          author: item.mentionedBooks[0].authorName,
          cover: item.mentionedBooks[0].cover,
        }
      : null,
  }
}

/**
 * 删除帖子
 *Path: /posts/{postId}
 */
export const deleteUserPost = (postId: number) => {
  return request({
    url: `/posts/${postId}`,
    method: 'delete',
  })
}
