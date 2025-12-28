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

/** 接口返回 data */
export interface ReadingRewardData {
  rewardDays: number
  expireDate: string
  isMember: boolean
}

/** 接口完整返回 */
export interface ReadingRewardResponse {
  code: RewardCode
  message: string
  data: ReadingRewardData | null
}

/**
 * 领取阅读奖励
 */
export const postReadingReward = async (
  type: 'daily' | 'streak',
  value: number,
): Promise<ReadingRewardData> => {
  const res = await request.post<ReadingRewardResponse>('/rewards/reading', { type, value })

  const { code, message, data } = res.data

  switch (code) {
    case RewardCode.SUCCESS:
      return data as ReadingRewardData

    case RewardCode.UNAUTHORIZED:
      // 未登录：统一交给上层处理
      throw new Error('未登录或登录已过期')

    case RewardCode.BAD_REQUEST:
    default:
      throw new Error(message || '请求失败')
  }
}
