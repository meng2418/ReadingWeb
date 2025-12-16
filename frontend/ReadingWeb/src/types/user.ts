export interface FollowUser {
  id: number
  username: string
  avatar: string
  bio: string
  isFollowing?: boolean
}

export type FollowListUpdate = FollowUser[]
