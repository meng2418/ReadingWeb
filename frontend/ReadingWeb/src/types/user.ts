export interface FollowUser {
  id: number
  username: string
  avatar: string
  bio: string
  isFollowing?: boolean
  isFollower?: boolean
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
  userId?: number
  avatar: string
  username: string
  bio: string
  followingCount: number
  followerCount: number
  postCount: number
  experienceCardCount: number
  coinCount: number
  isMember: boolean
  memberExpireDays: number
  readingStats: ReadingStatsData
  consecutiveReadingDays: number
}

export type PublicProfileVisibility = {
  // 对齐后端 visibility
  bookshelf?: boolean
  readingStats?: boolean
  highlights?: boolean
  thoughts?: boolean
  bookReviews?: boolean
  followers?: boolean
  following?: boolean

  // 兼容旧字段：他人主页里“最近在读”用这个开关
  recentBooks?: boolean
}

export type PublicRecentBook = {
  bookId: number
  title: string
  cover: string
}

export type PublicHighlight = {
  id: number | string
  bookName: string
  date: string
  text: string
  chapter: string
}

export type PublicThought = {
  id: number | string
  bookName: string
  date: string
  thought: string
  quote?: string
}

export type PublicBookReview = {
  id: number | string
  bookName: string
  cover?: string
  rating: 'recommend' | 'average' | 'bad'
  date: string
  likes: number
  content: string
}

export interface PublicUserHomeData {
  avatar: string
  username: string
  bio: string
  followingCount: number
  followerCount: number
  postCount: number
  isMember: boolean
  readingStats: ReadingStatsData
  consecutiveReadingDays: number
  visibility: PublicProfileVisibility
  recentBooks: PublicRecentBook[]
  highlights: PublicHighlight[]
  thoughts: PublicThought[]
  bookReviews: PublicBookReview[]
  isFollowing: boolean
  isFollower: boolean
  isSelf: boolean
}

export interface TopBook {
  bookId: number
  cover: string
  title: string
  readingTime: number
}
