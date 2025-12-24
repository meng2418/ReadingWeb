import request from '@/utils/request'
import type { Post } from '@/types/post'

/** 后端原始结构 */
interface RawPost {
  postId: number
  authorName: string
  authorAvatar: string
  publishTime: string
  isFollowingAuthor: boolean
  postTitle: string
  content: string
  mentionedBooks?: {
    cover: string
    bookTitle: string
    author: string
  }[]
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
    username: item.authorName,
    avatar: item.authorAvatar,
    postTime: item.publishTime,
    title: item.postTitle,
    content: item.content,
    likeCount: item.likeCount,
    commentCount: item.commentCount,
    isFollowing: item.isFollowingAuthor,
    isLiked: item.isLiked,
    book: item.mentionedBooks?.[0]
      ? {
          title: item.mentionedBooks[0].bookTitle,
          author: item.mentionedBooks[0].author,
          cover: item.mentionedBooks[0].cover,
        }
      : null,
  }))
}
