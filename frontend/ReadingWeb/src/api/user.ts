// src/api/user.ts
import request from '@/utils/request'
import type { UserProfile, UserAccount } from '@/types/user'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

const mapUserProfile = (raw: any): UserProfile => ({
  id: raw.userId ?? raw.id ?? 0,
  username: raw.username ?? '',
  avatar: raw.avatar ?? '',
  bio: raw.bio ?? '',
  isVip: raw.isMember ?? false,
  coins: raw.coinCount ?? 0,
  totalReadingTime: raw.readingStats?.totalReadingTime ?? 0,
  createdAt: raw.createdAt ?? null,
  followerCount: raw.followerCount ?? 0,
  followingCount: raw.followingCount ?? 0,
  postCount: raw.postCount ?? 0,
  memberCardCount: raw.memberCardCount ?? 0,
  memberExpireDays: raw.memberExpireDays ?? 0,
})

export const getUserProfile = async (): Promise<UserProfile> => {
  const res = await request.get('/user/profile')
  return mapUserProfile(unwrap(res))
}

/* ---------- account ---------- */

const mapUserAccount = (raw: any): UserAccount => ({
  isMember: raw.isMember ?? false,
  memberExpireAt: raw.memberExpireDays ? `${raw.memberExpireDays} 天` : null,
})

export const getUserAccount = async (): Promise<UserAccount> => {
  const res = await request.get('/user/profile')
  return mapUserAccount(unwrap(res))
}

// 当前 OpenAPI 未提供关注/粉丝列表接口，先返回空列表避免 404
export const getFollowingList = async () => []
export const getFollowersList = async () => []
