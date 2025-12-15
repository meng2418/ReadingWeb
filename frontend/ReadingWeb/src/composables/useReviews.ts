const PUBLIC_REVIEWS_KEY = 'publicReviews'
const USER_REVIEWS_KEY = 'userReviews'

export interface Review {
  id: number | string
  bookId?: string
  bookTitle?: string
  userId?: string
  userName: string
  content: string
  date: string
  rating?: 'recommend' | 'average' | 'poor' | string
  isPublic?: boolean
  lastEditDate?: string
}

export interface RatingStats {
  recommend: number
  average: number
  poor: number
}

type StorageRecord = Record<string, Record<string, Review>>

const safeRead = <T>(key: string, fallback: T): T => {
  try {
    const raw = localStorage.getItem(key)
    return raw ? (JSON.parse(raw) as T) : fallback
  } catch (error) {
    console.error(`读取本地存储 ${key} 失败:`, error)
    return fallback
  }
}

const writeStorage = (key: string, value: unknown) => {
  localStorage.setItem(key, JSON.stringify(value))
}

export const formatDate = (
  input: string | number | Date,
  options?: { withTime?: boolean; separator?: string },
) => {
  const date = new Date(input)
  if (Number.isNaN(date.getTime())) return String(input)

  const sep = options?.separator ?? '-'
  const y = date.getFullYear()
  const m = (date.getMonth() + 1).toString().padStart(2, '0')
  const d = date.getDate().toString().padStart(2, '0')
  const base = [y, m, d].join(sep)

  if (options?.withTime === false) return base

  const h = date.getHours().toString().padStart(2, '0')
  const min = date.getMinutes().toString().padStart(2, '0')
  return `${base} ${h}:${min}`
}

export const ensureCurrentUserId = () => {
  let userId = localStorage.getItem('currentUserId')
  if (!userId) {
    userId = `user_${Date.now()}_${Math.random().toString(36).slice(2, 11)}`
    localStorage.setItem('currentUserId', userId)
  }
  return userId
}

export const getUserReview = (bookId: string, userId: string) => {
  if (!bookId) return null
  const userReviews = safeRead<Record<string, Record<string, Review>>>(USER_REVIEWS_KEY, {})
  return userReviews[userId]?.[bookId] ?? null
}

export const upsertUserReview = (review: Review) => {
  if (!review.bookId) return
  const userId = review.userId ?? ensureCurrentUserId()
  const userReviews = safeRead<Record<string, Record<string, Review>>>(USER_REVIEWS_KEY, {})
  if (!userReviews[userId]) userReviews[userId] = {}
  userReviews[userId][review.bookId] = { ...review, userId }
  writeStorage(USER_REVIEWS_KEY, userReviews)
}

export const removeUserReview = (bookId: string, userId: string) => {
  const userReviews = safeRead<Record<string, Record<string, Review>>>(USER_REVIEWS_KEY, {})
  if (userReviews[userId]?.[bookId]) {
    delete userReviews[userId][bookId]
    writeStorage(USER_REVIEWS_KEY, userReviews)
  }
}

export const upsertPublicReview = (bookId: string, review: Review) => {
  if (!bookId) return
  const userId = review.userId ?? ensureCurrentUserId()
  const publicReviews = safeRead<StorageRecord>(PUBLIC_REVIEWS_KEY, {})
  if (!publicReviews[bookId]) publicReviews[bookId] = {}
  publicReviews[bookId][userId] = { ...review, userId, bookId }
  writeStorage(PUBLIC_REVIEWS_KEY, publicReviews)
}

export const removePublicReview = (bookId: string, userId: string) => {
  const publicReviews = safeRead<StorageRecord>(PUBLIC_REVIEWS_KEY, {})
  if (publicReviews[bookId]?.[userId]) {
    delete publicReviews[bookId][userId]
    writeStorage(PUBLIC_REVIEWS_KEY, publicReviews)
  }
}

export const getLatestPublicReviews = (bookId: string): Review[] => {
  if (!bookId) return []
  const publicReviews = safeRead<StorageRecord>(PUBLIC_REVIEWS_KEY, {})
  const bookPublicReviews = publicReviews[bookId] || {}

  const userLatest = new Map<string, Review>()
  Object.values(bookPublicReviews).forEach((review) => {
    if (review.isPublic === false) return
    const userId = review.userId ?? 'anonymous'
    const existing = userLatest.get(userId)
    const curTime = new Date(review.lastEditDate || review.date)
    if (!existing || curTime > new Date(existing.lastEditDate || existing.date)) {
      userLatest.set(userId, review)
    }
  })

  return Array.from(userLatest.values())
}

export const computeRatingStats = (bookId: string, defaults: RatingStats) => {
  let { recommend, average, poor } = defaults
  getLatestPublicReviews(bookId).forEach((review) => {
    switch (review.rating) {
      case 'recommend':
        recommend++
        break
      case 'average':
        average++
        break
      case 'poor':
        poor++
        break
      default:
        break
    }
  })
  return { recommend, average, poor }
}

export const countPublicReviews = (bookId: string, baseCount = 0) => {
  return baseCount + getLatestPublicReviews(bookId).length
}

