import type { Ref } from 'vue'
import type { Post } from '@/types/post'
import { toggleLikeApi } from '@/api/community' // 确保你已经在 @/api/community 中导出了此函数
import { followUserApi, unfollowUserApi } from '@/api/userRelations'
import { ElMessage } from 'element-plus'

/**
 * 针对帖子列表的通用交互封装（点赞 / 关注 等）
 * 传入 posts 响应式列表，即可在各页面统一调用。
 */
export const usePostInteractions = (posts: Ref<Post[]>) => {
  /** 更新关注状态（含 API 请求） */
  const updateFollow = async (postId: number, isFollowing: boolean) => {
    const post = posts.value.find((p) => p.id === postId)
    if (!post) return

    // 检查是否有 authorId
    if (!post.authorId) {
      console.error('帖子缺少 authorId，无法执行关注操作')
      ElMessage.error('无法关注：缺少作者信息')
      return
    }

    // 记录旧状态以便请求失败时回滚
    const oldIsFollowing = post.isFollowing

    // 乐观更新：立即改变 UI 状态
    post.isFollowing = isFollowing

    try {
      // 发送请求到后端
      if (isFollowing) {
        await followUserApi(post.authorId)
        console.log(`关注用户 ${post.authorId} 成功`)
      } else {
        await unfollowUserApi(post.authorId)
        console.log(`取消关注用户 ${post.authorId} 成功`)
      }
    } catch (error: any) {
      // 如果请求失败，回滚到之前的状态
      post.isFollowing = oldIsFollowing
      console.error('关注操作失败，已回滚状态:', error)
      ElMessage.error(error?.response?.data?.message || '操作失败，请稍后再试')
    }
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
      // 根据 API 文档，需要传递 targetId 和 targetType
      await toggleLikeApi({
        postId: postId,
        commentId: 0,
        targetId: postId, // 点赞帖子时，targetId 就是 postId
        targetType: 'post', // 目标类型为 post
      })

      console.log(`帖子 ${postId} 点赞操作成功`)
    } catch (error: any) {
      // 4. 如果请求失败，回滚到之前的状态
      post.likeCount = oldLikeCount
      post.isLiked = oldIsLiked

      console.error('点赞请求失败，已回滚状态:', error)
      ElMessage.error(error?.response?.data?.message || '点赞失败，请稍后再试')
    }
  }

  return {
    updateFollow,
    updateLike,
  }
}
