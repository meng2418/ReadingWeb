import request from '@/utils/request'
import type { HomeRecommendations, HomeRanking, SimpleBookRaw } from '@/types/home'
import type { GuessBook, RankBook, RecentBook } from '@/types/book'

const pickId = (raw: SimpleBookRaw, index: number) =>
  (raw as any).bookId ?? (raw as any).id ?? `${raw.bookTitle}-${index}`

/** 猜你喜欢 */
const mapToGuessBook = (raw: SimpleBookRaw, index: number): GuessBook => ({
  id: pickId(raw, index),
  title: raw.bookTitle,
  author: raw.author,
  cover: raw.cover,
  reason: raw.description ?? '根据你的常看推荐', // ✅ 用简介
})

/** 排行榜 */
const mapToRankBook = (raw: SimpleBookRaw, index: number): RankBook => ({
  id: pickId(raw, index),
  title: raw.bookTitle,
  author: raw.author,
  cover: raw.cover,
  recommend: raw.rating ? `${Number(raw.rating).toFixed(1)} 分` : '-',
})

/** 最近阅读 */
const mapToRecentBook = (raw: any, index: number): RecentBook => ({
  id: pickId(raw, index),
  title: raw.bookTitle,
  cover: raw.cover,
})

const unwrap = <T>(res: any): T => res?.data?.data ?? res?.data ?? []

export const getHomeData = async () => {
  const [recommendRes, weeklyRes, monthlyRes, newRes, masterpieceRes, bookshelfRes] =
    await Promise.all([
      request.get<HomeRecommendations>('/home/recommendations'),
      request.get<HomeRanking>('/home/rankings/weekly'),
      request.get<HomeRanking>('/home/rankings/monthly'),
      request.get<HomeRanking>('/home/rankings/new'),
      request.get<HomeRanking>('/home/rankings/masterpiece'),
      request.get('/bookshelf'), // ✅ 最近阅读接口
    ])

  const recommendations = unwrap<HomeRecommendations>(recommendRes)
  const weeklyRank = unwrap<HomeRanking>(weeklyRes)
  const monthlyRank = unwrap<HomeRanking>(monthlyRes)
  const newRank = unwrap<HomeRanking>(newRes)
  const masterpieceRank = unwrap<HomeRanking>(masterpieceRes)
  const bookshelf = unwrap<any[]>(bookshelfRes)

  return {
    guessBooks: recommendations.map(mapToGuessBook),
    rankWeekly: weeklyRank.map(mapToRankBook),
    rankMonthly: monthlyRank.map(mapToRankBook),
    rankNew: newRank.map(mapToRankBook),
    rankMasterpiece: masterpieceRank.map(mapToRankBook),
    recentBooks: bookshelf.slice(0, 4).map(mapToRecentBook), // ✅ 真·最近阅读
  }
}

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
