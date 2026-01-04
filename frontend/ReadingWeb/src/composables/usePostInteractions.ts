import type { Ref } from 'vue'
import type { Post } from '@/types/post'
import { toggleLikeApi } from '@/api/community' // 确保你已经在 @/api/community 中导出了此函数

/**
 * 针对帖子列表的通用交互封装（点赞 / 关注 等）
 * 传入 posts 响应式列表，即可在各页面统一调用。
 */
export const usePostInteractions = (posts: Ref<Post[]>) => {
  /** 更新关注状态 */
  const updateFollow = (postId: number, isFollowing: boolean) => {
    const post = posts.value.find((p) => p.id === postId)
    if (post) post.isFollowing = isFollowing
  }

  /**
   * 更新点赞状态（含 API 请求）
   * @param postId 帖子ID
   * @param newLikeCount 点击后应该显示的最终点赞数
   * @param newIsLiked 点击后应该显示的最终点赞状态
   */
  const updateLike = async (postId: number, newLikeCount: number, newIsLiked: boolean) => {
    const post = posts.value.find((p) => p.id === postId)
    if (!post) return

    // 1. 记录旧状态以便请求失败时回滚
    const oldLikeCount = post.likeCount
    const oldIsLiked = post.isLiked

    // 2. 乐观更新：立即改变 UI 状态
    post.likeCount = newLikeCount
    post.isLiked = newIsLiked

    try {
      // 3. 发送请求到后端
      // 根据 API 文档，postId 和 commentId 均为必填。点赞帖子时 commentId 传 0。
      await toggleLikeApi({
        postId: postId,
        commentId: 0,
      })

      console.log(`帖子 ${postId} 点赞操作成功`)
    } catch (error) {
      // 4. 如果请求失败，回滚到之前的状态
      post.likeCount = oldLikeCount
      post.isLiked = oldIsLiked

      console.error('点赞请求失败，已回滚状态:', error)
      // 这里可以根据需要添加全局提示，例如：message.error('点赞失败，请稍后再试')
    }
  }

  return {
    updateFollow,
    updateLike,
  }
}
