export interface FollowUser {
  id: number
  username: string
  avatar: string
  bio: string
  isFollowing?: boolean
}

export type FollowListUpdate = FollowUser[]

export interface UserProfile {
  id: number
  username: string
  avatar: string
  bio: string
  isVip: boolean
  coins: number
  totalReadingTime: number
  createdAt: string | null
  followerCount?: number
  followingCount?: number
  postCount?: number
  memberCardCount?: number
  memberExpireDays?: number
}

export interface UserAccount {
  isMember: boolean
  memberExpireAt: string | null
}
