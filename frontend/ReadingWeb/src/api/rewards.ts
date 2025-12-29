/* =========================
 * 阅读激励Post
 * ========================= */
import request from '@/utils/request'

/** 业务码枚举 */
export enum RewardCode {
  SUCCESS = 200,
  BAD_REQUEST = 400,
  UNAUTHORIZED = 401,
}

/**
 * 领取阅读奖励
 */
export const postReadingReward = async (type: 'daily' | 'streak', value: number): Promise<void> => {
  const res = await request.post('/rewards/reading', { type, value })

  if (res.data.code !== RewardCode.SUCCESS) {
    throw new Error(res.data.message || '请求失败')
  }
}

/*=========================
 *获取每日阅读时长
 * ========================= */
export interface DailyReadingStat {
  date: string
  readingTime: number // 单位：分钟
}

export interface DailyReadingResponse {
  code: number
  message: string
  data: DailyReadingStat[]
}

/** 获取每日阅读时长 */
export const fetchDailyReading = async (): Promise<number> => {
  const res = await request.get<DailyReadingResponse>('/user/reading-stats/daily')
  if (res.data.code !== 200) throw new Error(res.data.message || '获取阅读时长失败')

  const stats = res.data.data ?? []

  const today = new Date().toISOString().slice(0, 10) // 格式 'YYYY-MM-DD'
  const todayStat = stats.find((item) => item.date === today)

  return todayStat?.readingTime ?? 0
}
