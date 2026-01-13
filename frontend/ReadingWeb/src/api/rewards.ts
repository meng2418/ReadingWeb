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
 * @param minutes 阅读时长要求（分钟），可选，默认为30
 * 每次领取都是2天体验卡
 */
export const postReadingReward = async (minutes?: number): Promise<void> => {
  try {
    // 告知 axios 返回值的类型是 ApiResponse
    // 使用GET方式传递参数，或者使用POST body传递
    // 根据后端接口，使用RequestParam，所以应该用GET或者POST with query params
    const url = minutes ? `/rewards/reading?minutes=${minutes}` : '/rewards/reading'
    const res = await request.post<ApiResponse>(url)

    // 现在 TS 知道 res.data 里面有 code 和 message 了
    if (res.data.code !== 200) {
      throw new Error(res.data.message || '领取失败')
    }
  } catch (error: any) {
    // 处理 axios 错误响应
    if (error.response && error.response.data) {
      const errorData = error.response.data
      if (errorData.message) {
        throw new Error(errorData.message)
      }
    }
    // 如果是我们抛出的 Error，直接重新抛出
    if (error instanceof Error) {
      throw error
    }
    // 其他情况
    throw new Error(error.message || '领取失败，请稍后重试')
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

/**
 * 获取今日已领取的奖励列表
 * @returns 返回已领取的奖励类型列表，如：["daily_5", "daily_30"]
 */
export async function getTodayClaimedRewards(): Promise<string[]> {
  try {
    const res = await request.get<ApiResponse<string[]>>('/rewards/reading/claimed')
    if (res.data.code === 200) {
      return res.data.data || []
    }
    return []
  } catch (error: any) {
    // 处理错误，返回空数组
    console.error('获取已领取奖励失败:', error)
    return []
  }
}
