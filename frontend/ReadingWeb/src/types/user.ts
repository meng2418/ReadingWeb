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

export interface ReadingStatsData {
  weeklyReadingTime: number
  monthlyReadingTime: number
  yearlyReadingTime: number
  totalReadingTime: number

  weeklyBooksRead: number
  monthlyBooksRead: number
  yearlyBooksRead: number
  totalBooksRead: number

  weeklyBooksFinished: number
  monthlyBooksFinished: number
  yearlyBooksFinished: number
  totalBooksFinished: number

  weeklyNoteCount: number
  monthlyNoteCount: number
  yearlyNoteCount: number
  totalNoteCount: number
}

export interface UserHomeData {
  avatar: string
  username: string
  bio: string
  followingCount: number
  followerCount: number
  postCount: number
  memberCardCount: number
  coinCount: number
  isMember: boolean
  memberExpireDays: number
  readingStats: ReadingStatsData
  consecutiveReadingDays: number
}

export interface TopBook {
  cover: string
  title: string
  readingTime: number
}
