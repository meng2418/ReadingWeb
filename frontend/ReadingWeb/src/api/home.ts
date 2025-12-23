import request from '@/utils/request'
import type { HomeData, HomeBookRaw } from '@/types/home'
import type { GuessBook, RankBook, RecentBook } from '@/types/book'

/**
 * 接口 → 猜你喜欢 UI 模型
 */
const mapToGuessBook = (raw: HomeBookRaw): GuessBook => ({
  id: raw.id,
  title: raw.title,
  author: raw.author,
  cover: raw.cover,
  reason: raw.isFree ? '免费好书' : '热门推荐',
})

/**
 * 接口 → 榜单 UI 模型
 */
const mapToRankBook = (raw: HomeBookRaw): RankBook => ({
  id: raw.id,
  title: raw.title,
  author: raw.author,
  cover: raw.cover,
  recommend: `${raw.rating}%`,
})

/**
 * 接口 → 最近阅读（示例：取推荐前几本）
 */
const mapToRecentBook = (raw: HomeBookRaw): RecentBook => ({
  id: raw.id,
  title: raw.title,
  cover: raw.cover,
})

export const getHomeData = async () => {
  const res = await request.get<HomeData>('/home')
  const data = res.data

  return {
    banners: data.banners,

    guessBooks: data.recommendations.map(mapToGuessBook),

    rankWeekly: data.rankings.weekly.map(mapToRankBook),
    rankMonthly: data.rankings.monthly.map(mapToRankBook),

    recentBooks: data.recommendations.slice(0, 4).map(mapToRecentBook),
  }
}
