import type { Ref } from 'vue'
import type { Post } from '@/types/post'

/**
 * 针对帖子列表的通用交互封装（点赞 / 关注 等）
 * 传入 posts 响应式列表，即可在各页面统一调用。
 */
export const usePostInteractions = (posts: Ref<Post[]>) => {
  const updateFollow = (postId: number, isFollowing: boolean) => {
    const post = posts.value.find((p) => p.id === postId)
    if (post) post.isFollowing = isFollowing
  }

  const updateLike = (postId: number, likeCount: number, isLiked: boolean) => {
    const post = posts.value.find((p) => p.id === postId)
    if (post) {
      post.likeCount = likeCount
      post.isLiked = isLiked
    }
  }

  return {
    updateFollow,
    updateLike,
  }
}
