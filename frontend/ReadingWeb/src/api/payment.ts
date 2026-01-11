// src/api/payment.ts - 支付相关API
import request from '@/utils/request'
import type { AxiosResponse } from 'axios'

const unwrap = <T>(res: AxiosResponse): T => res?.data?.data ?? res?.data ?? {}

// ==================== 会员相关接口 ====================

/** 会员支付页信息 */
export interface MembershipPaymentInfo {
  packageId: number
  packageName: string
  durationDays: number
  originalAmount: number
  discountAmount: number
  paymentMethods: ('wechat' | 'alipay' | 'balance' | 'card')[]
}

/** 开通会员请求参数 */
export interface PurchaseMembershipRequest {
  packageId: number
  paymentMethod: 'wechat' | 'alipay' | 'balance' | 'card'
}

/** 开通会员响应 */
export interface PurchaseMembershipResponse {
  durationType: string
  durationDays: number
}

/**
 * 获取会员支付页
 * @param packageId 套餐ID（可选）
 */
export const getMembershipPaymentInfo = async (packageId?: number): Promise<MembershipPaymentInfo> => {
  const res = await request.get('/membership/payment-info', {
    params: packageId ? { packageId } : {}
  })
  return unwrap<MembershipPaymentInfo>(res)
}

/**
 * 开通会员
 * @param params 开通会员请求参数
 */
export const purchaseMembership = async (params: PurchaseMembershipRequest): Promise<PurchaseMembershipResponse> => {
  const res = await request.post('/membership/purchase', params)
  return unwrap<PurchaseMembershipResponse>(res)
}

// ==================== 充值相关接口 ====================

/** 充值套餐 */
export interface RechargePackage {
  packageId?: number // 后端可能不返回，需要从索引推断
  coinAmount: number
  cnyAmount: number
  bonusCoins: number
}

/** 充值支付页信息 */
export interface RechargePaymentInfo {
  coinAmount: number
  bonusCoins: number
  actualAmount: number
  paymentMethods: ('wechat' | 'alipay' | 'balance' | 'card')[]
}

/** 充值请求参数 */
export interface RechargeRequest {
  packageId: number
  paymentMethod: 'wechat' | 'alipay' | 'unionpay'
}

/** 充值响应 */
export interface RechargeResponse {
  orderId: string
  coinAmount: number
  bonusCoins: number
}

/**
 * 获取充值套餐列表
 */
export const getRechargePackages = async (): Promise<RechargePackage[]> => {
  const res = await request.get('/recharge/packages')
  const data = unwrap<RechargePackage[]>(res)
  return Array.isArray(data) ? data : []
}

/**
 * 获取充值支付页
 * @param packageId 套餐ID
 */
export const getRechargePaymentPage = async (packageId: number): Promise<RechargePaymentInfo> => {
  const res = await request.get('/recharge/payment-page', {
    params: { packageId }
  })
  return unwrap<RechargePaymentInfo>(res)
}

/**
 * 充值充值币
 * @param params 充值请求参数
 */
export const recharge = async (params: RechargeRequest): Promise<RechargeResponse> => {
  const res = await request.post('/recharge', params)
  return unwrap<RechargeResponse>(res)
}
