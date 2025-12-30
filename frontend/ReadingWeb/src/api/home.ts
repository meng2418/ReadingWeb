// src/api/home.ts
import request from '@/utils/request'
import type { HomeRanking, SimpleBookRaw } from '@/types/home'
import type { GuessBook, RankBook, RecentBook } from '@/types/book'

/* =======================
   猜你喜欢：Raw 类型
   ======================= */
interface GuessBookRaw {
  bookId: number
  cover: string
  bookTitle: string
  authorName: string
  description: string
}

/** 猜你喜欢映射 */
const mapToGuessBook = (raw: GuessBookRaw): GuessBook => ({
  bookId: raw.bookId,
  cover: raw.cover,
  title: raw.bookTitle,
  author: raw.authorName,
  reason: raw.description,
})
export const getGuessBooks = async (): Promise<GuessBook[]> => {
  const res = await request.get('/home/recommendations')
  const list = res.data?.data ?? []
  return list.map(mapToGuessBook)
}
/* =======================
   排行榜（沿用旧结构）
   ======================= */
const pickId = (raw: SimpleBookRaw, index: number) =>
  (raw as any).bookId ?? (raw as any).id ?? `${raw.bookTitle}-${index}`

const mapToRankBook = (raw: SimpleBookRaw, index: number): RankBook => ({
  id: Number(pickId(raw, index)),
  title: raw.bookTitle,
  author: raw.author,
  cover: raw.cover,
  recommend: raw.rating ? `${Number(raw.rating).toFixed(1)} 分` : '-',
})

/* =======================
   最近阅读
   ======================= */
const mapToRecentBook = (raw: any, index: number): RecentBook => ({
  id: raw.bookId ?? index,
  title: raw.bookTitle,
  cover: raw.cover,
})

/* =======================
   通用 unwrap
   ======================= */
const unwrap = <T>(res: any): T => res?.data?.data ?? []

/* =======================
   首页数据
   ======================= */
export const getHomeData = async () => {
  const [recommendRes, weeklyRes, monthlyRes, newRes, masterpieceRes, recentBooksData] =
    await Promise.all([
      request.get<GuessBookRaw[]>('/home/recommendations'),
      request.get<HomeRanking>('/home/rankings/weekly'),
      request.get<HomeRanking>('/home/rankings/monthly'),
      request.get<HomeRanking>('/home/rankings/new'),
      request.get<HomeRanking>('/home/rankings/masterpiece'),
      request.get('/home/recent-books'),
    ])

  const recommendations = unwrap<GuessBookRaw[]>(recommendRes)
  const weeklyRank = unwrap<HomeRanking>(weeklyRes)
  const monthlyRank = unwrap<HomeRanking>(monthlyRes)
  const newRank = unwrap<HomeRanking>(newRes)
  const masterpieceRank = unwrap<HomeRanking>(masterpieceRes)
  const recentBooksRaw = unwrap<any[]>(recentBooksData)
  return {
    guessBooks: recommendations.map(mapToGuessBook),
    rankWeekly: weeklyRank.map(mapToRankBook),
    rankMonthly: monthlyRank.map(mapToRankBook),
    rankNew: newRank.map(mapToRankBook),
    rankMasterpiece: masterpieceRank.map(mapToRankBook),
    recentBooks: recentBooksRaw.map(mapToRecentBook),
  }
}

/* =======================
   阅读统计
   ======================= */
export const getReadingStats = async () => {
  const res = await request.get('/home/reading-stats')
  const data = unwrap<{
    weeklyReadingTime?: number
    monthlyReadingTime?: number
    totalReadingTime?: number
  }>(res)

  return {
    weekly: data.weeklyReadingTime ?? 0,
    monthly: data.monthlyReadingTime ?? 0,
    total: data.totalReadingTime ?? 0,
  }
}
