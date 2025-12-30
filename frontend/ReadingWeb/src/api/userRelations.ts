// src/api/userRelations.ts
import request from '@/utils/request'
import type { FollowUser } from '@/types/user'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

/**
 * 通用映射函数：将后端用户结构转换为前端 FollowUser 结构
 */
const mapUserFields = (item: any): FollowUser => ({
  id: item.userId, // 后端是 userId -> 前端组件需要 id
  username: item.username,
  avatar: item.avatar,
  bio: item.bio || '暂无简介',
  isFollowing: item.isFollowing,
  isFollower: item.isFollower,
})

/**
 * 获取关注列表
 */
export const getFollowingList = async () => {
  const res = await request.get('/user/following')
  const data = unwrap(res)
  return {
    items: (data.items || []).map(mapUserFields),
    hasMore: data.hasMore,
    nextCursor: data.nextCursor,
  }
}

/**
 * 获取粉丝列表
 */
export const getFollowersList = async () => {
  const res = await request.get('/user/followers')
  const data = unwrap(res)
  return {
    items: (data.items || []).map(mapUserFields),
    hasMore: data.hasMore,
    nextCursor: data.nextCursor,
  }
}
