// src/api/profile.ts
import request from '@/utils/request'
import { processCoverPath } from '@/utils/imagePath'
import type { UserHomeData } from '@/types/user'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}
const safeArray = <T>(v: unknown): T[] => (Array.isArray(v) ? v : [])

// =========================
// 用户主页
// =========================
export const getProfileHome = async (): Promise<UserHomeData> => {
  const raw = unwrap(await request.get('/user/home'))
  const rs = raw.readingStats ?? {}

  return {
    userId: raw.userId ?? undefined, // 添加用户ID
    avatar: raw.avatar ?? '',
    username: raw.username ?? '',
    bio: raw.bio ?? '',
    followingCount: raw.followingCount ?? 0,
    followerCount: raw.followerCount ?? 0,
    postCount: raw.postCount ?? 0,
    experienceCardCount: raw.experienceCardCount ?? 0,
    coinCount: raw.coinCount ?? 0,
    isMember: raw.isMember ?? false,
    memberExpireDays: raw.memberExpireDays ?? 0,

    readingStats: {
      weeklyReadingTime: rs.weeklyReadingTime ?? 0,
      monthlyReadingTime: rs.monthlyReadingTime ?? 0,
      yearlyReadingTime: rs.yearlyReadingTime ?? 0,
      totalReadingTime: rs.totalReadingTime ?? 0,

      weeklyBooksRead: rs.weeklyBooksRead ?? 0,
      monthlyBooksRead: rs.monthlyBooksRead ?? 0,
      yearlyBooksRead: rs.yearlyBooksRead ?? 0,
      totalBooksRead: rs.totalBooksRead ?? 0,

      weeklyBooksFinished: rs.weeklyBooksFinished ?? 0,
      monthlyBooksFinished: rs.monthlyBooksFinished ?? 0,
      yearlyBooksFinished: rs.yearlyBooksFinished ?? 0,
      totalBooksFinished: rs.totalBooksFinishedCount ?? 0,

      weeklyNoteCount: rs.weeklyNoteCount ?? 0,
      monthlyNoteCount: rs.monthlyNoteCount ?? 0,
      yearlyNoteCount: rs.yearlyNoteCount ?? 0,
      totalNoteCount: rs.totalNoteCount ?? 0,
    },

    consecutiveReadingDays: raw.consecutiveReadingDays ?? 0,
  }
}

// =========================
// 更新用户资料
// =========================
export interface UpdateProfileParams {
  username?: string
  avatar?: string
  bio?: string
}

export const updateProfile = async (params: UpdateProfileParams): Promise<UserHomeData> => {
  const raw = unwrap(await request.put('/user/profile', params))
  const rs = raw.readingStats ?? {}

  return {
    userId: raw.userId ?? undefined, // 添加用户ID
    avatar: raw.avatar ?? '',
    username: raw.username ?? '',
    bio: raw.bio ?? '',
    followingCount: raw.followingCount ?? 0,
    followerCount: raw.followerCount ?? 0,
    postCount: raw.postCount ?? 0,
    experienceCardCount: raw.experienceCardCount ?? 0,
    coinCount: raw.coinCount ?? 0,
    isMember: raw.isMember ?? false,
    memberExpireDays: raw.memberExpireDays ?? 0,

    readingStats: {
      weeklyReadingTime: rs.weeklyReadingTime ?? 0,
      monthlyReadingTime: rs.monthlyReadingTime ?? 0,
      yearlyReadingTime: rs.yearlyReadingTime ?? 0,
      totalReadingTime: rs.totalReadingTime ?? 0,

      weeklyBooksRead: rs.weeklyBooksRead ?? 0,
      monthlyBooksRead: rs.monthlyBooksRead ?? 0,
      yearlyBooksRead: rs.yearlyBooksRead ?? 0,
      totalBooksRead: rs.totalBooksRead ?? 0,

      weeklyBooksFinished: rs.weeklyBooksFinished ?? 0,
      monthlyBooksFinished: rs.monthlyBooksFinished ?? 0,
      yearlyBooksFinished: rs.yearlyBooksFinished ?? 0,
      totalBooksFinished: rs.totalBooksFinishedCount ?? 0,

      weeklyNoteCount: rs.weeklyNoteCount ?? 0,
      monthlyNoteCount: rs.monthlyNoteCount ?? 0,
      yearlyNoteCount: rs.yearlyNoteCount ?? 0,
      totalNoteCount: rs.totalNoteCount ?? 0,
    },

    consecutiveReadingDays: raw.consecutiveReadingDays ?? 0,
  }
}

/* =========================
 * 最近高亮
 * ========================= */

export type RecentHighlight = {
  id: number
  bookName: string
  date: string
  text: string
  chapter: string
}

export const getRecentHighlights = async (): Promise<RecentHighlight[]> => {
  const raw = unwrap(await request.get('/user/highlights/recent'))
  const items = safeArray<any>(raw?.items ?? raw)

  return items.map((it, idx) => ({
    id: idx + 1,
    bookName: it.bookTitle ?? '',
    date: it.highlightDate ?? '',
    text: it.content ?? '',
    chapter: it.chapterName ?? '',
  }))
}

/* =========================
 * 最近笔记（UI 中叫 Thoughts）
 * ========================= */

export type RecentThought = {
  id: number
  bookName: string
  date: string
  thought: string
  quote: string
}

export const getRecentNotes6 = async (): Promise<RecentThought[]> => {
  const raw = unwrap(await request.get('/user/notes/recent6'))
  const items = safeArray<any>(raw?.items ?? raw).slice(0, 6)

  return items.map((it, idx) => ({
    id: idx + 1,
    bookName: it.bookTitle ?? '',
    date: it.noteDate ?? '',
    thought: it.noteContent ?? '',
    quote: it.quote ?? '',
  }))
}

/* =========================
 * 最近书评
 * ========================= */

type ApiRating = 'recommend' | 'average' | 'not_recommend'
type UIRating = 'recommend' | 'average' | 'bad'

export type RecentReview = {
  id: number
  bookName: string
  cover?: string
  rating: UIRating
  date: string
  likes: number
  content: string
}

const ratingMap: Record<ApiRating, UIRating> = {
  recommend: 'recommend',
  average: 'average',
  not_recommend: 'bad',
}

const normalizeRating = (r: unknown): UIRating => {
  if (typeof r === 'string' && r in ratingMap) {
    return ratingMap[r as ApiRating]
  }
  return 'recommend'
}

export const getRecentBookReviews = async (): Promise<RecentReview[]> => {
  const raw = unwrap(await request.get('/user/book-reviews/recent'))
  const items = safeArray<any>(raw?.items ?? raw)

  return items.map((it, idx) => ({
    id: idx + 1,
    bookName: it.bookTitle ?? '',
    cover: processCoverPath(it.cover ?? ''),
    rating: normalizeRating(it.rating),
    date: it.reviewDate ?? '',
    likes: it.helpfulCount ?? 0,
    content: it.reviewContent ?? '',
  }))
}

/* =========================
 * 阅读里程碑/阅历
 * ========================= */
export type HistoryRecord =
  | {
      date: string
      type: 'reading'
      statLabel: string
      statValue: number
      statUnit: string
      text: string
    }
  | {
      date: string
      type: 'note'
      statLabel: string
      statValue: number
      statUnit: string
      source: string
      quote: string
    }
  | {
      date: string
      type: 'finishBook'
      statLabel: string
      statValue: number
      statUnit: string
      text: string
    }

export const getHistoryMilestones = async (): Promise<HistoryRecord[]> => {
  const raw = unwrap(await request.get('/user/latest-milestones'))

  const records: HistoryRecord[] = []

  /* ===== 累计阅读里程碑 ===== */
  if (raw.totalReadingMilestone) {
    const m = raw.totalReadingMilestone
    records.push({
      date: m.achievedAt ?? '',
      type: 'reading',
      statLabel: '累计阅读',
      statValue: m.targetCount ?? 0,
      statUnit: '本书',
      text:
        m.message ?? (m.achievedBookTitle ? `那时，你正在阅读《${m.achievedBookTitle}》。` : ''),
    })
  }

  /* ===== 笔记数量里程碑 ===== */
  if (raw.noteCountMilestone) {
    const m = raw.noteCountMilestone
    records.push({
      date: m.achievedAt ?? '',
      type: 'note',
      statLabel: '发表笔记',
      statValue: m.targetCount ?? 0,
      statUnit: '条',
      source: m.achievedBookTitle ? `在《${m.achievedBookTitle}》里，你留下划线：` : '',
      quote: m.achievedNoteContent ?? '',
    })
  }

  /* ===== 完成阅读里程碑 ===== */
  if (raw.finishedReadingMilestone) {
    const m = raw.finishedReadingMilestone
    records.push({
      date: m.achievedAt ?? '',
      type: 'finishBook',
      statLabel: '完成图书',
      statValue: m.targetCount ?? 0,
      statUnit: '本书',
      text: m.message ?? (m.achievedBookTitle ? '你完成了《${m.achievedBookTitle}》。' : ''),
    })
  }

  /* 时间倒序，方便直接渲染时间轴 */
  return records.sort((a, b) => (a.date < b.date ? 1 : -1))
}

/* =========================
 * 阅读时长排名前三的书籍
 * ========================= */

export interface TopBook {
  cover: string
  title: string
  readingTime: number
}
interface TopBooksApiResponse {
  code: number
  message: string
  data: {
    period: 'week' | 'month' | 'year' | 'total'
    topBooks: {
      bookId: number
      cover: string
      bookTitle: string
      readingTime: number
    }[]
  }
}

export const getTopBooks = async (
  period: 'week' | 'month' | 'year' | 'total' = 'week',
): Promise<TopBook[]> => {
  const res = await request.get<TopBooksApiResponse>('/user/reading-stats/top-books', {
    params: { period },
  })

  const list = res.data?.data?.topBooks ?? []

  return list.map((item) => {
    // 确保bookId存在，如果API没有返回则抛出错误或使用默认值
    if (!item.bookId) {
      console.error('API返回的书籍数据缺少bookId:', item)
    }
    
    return {
      bookId: item.bookId ?? 0, // 如果确实没有，设为0，跳转时会检查
      cover: item.cover ?? '',
      title: item.bookTitle ?? '',
      readingTime: item.readingTime ?? 0,
    }
  }).filter(item => item.bookId > 0) // 过滤掉没有bookId的项
}

/* =========================
 * 柱状图
 * ========================= */

export const getReadingTimeline = () => {
  return request.get('/user/reading-stats/timeline').then(unwrap)
}
