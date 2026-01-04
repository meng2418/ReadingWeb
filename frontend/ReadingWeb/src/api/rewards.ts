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

// 定义后端统一返回的格式
interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

/**
 * 领取阅读奖励
 */
export const postReadingReward = async (type: 'daily' | 'streak', value: number): Promise<void> => {
  // 告知 axios 返回值的类型是 ApiResponse
  const res = await request.post<ApiResponse>('/rewards/reading', { type, value })

  // 现在 TS 知道 res.data 里面有 code 和 message 了
  if (res.data.code !== 200) {
    throw new Error(res.data.message || '领取失败')
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

/**
 *  获取今日阅读时长（分钟）
 */
export function getTodayReadingTime() {
  return request({
    url: '/user/reading-time/today',
    method: 'get',
  })
}
