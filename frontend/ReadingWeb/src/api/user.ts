// src/api/user.ts
import request from '@/utils/request'
import type { UserProfile, UserAccount } from '@/types/user'

const mapUserProfile = (raw: any): UserProfile => ({
  id: raw.userid,
  username: raw.username,
  avatar: raw.avatar,
  bio: raw.bio,
  isVip: raw.isMember,
  coins: raw.coins,
  totalReadingTime: raw.totalReadingTime,
  createdAt: raw.createdAt,
})

export const getUserProfile = async (): Promise<UserProfile> => {
  const res = await request.get('/user/profile')
  return mapUserProfile(res.data.data)
}

/* ---------- account ---------- */

const mapUserAccount = (raw: any): UserAccount => ({
  isMember: raw.isMember,
  memberExpireAt: raw.memberExpireAt,
})

export const getUserAccount = async (): Promise<UserAccount> => {
  const res = await request.get('/user/account')
  return mapUserAccount(res.data)
}

export const getFollowingList = async () => {
  const res = await request.get('/follows/following')
  return res.data.following
}

export const getFollowersList = async () => {
  const res = await request.get('/follows/followers')
  return res.data.followers
}
