//api/auth.ts
import request from '@/utils/request'
import service from '@/utils/request'
// 登录
export type LoginType = 'password' | 'verificationCode'

export interface LoginParams {
  phone: string
  type: LoginType
  password: string | null
  verificationCode: string | null
}

export function login(data: LoginParams) {
  return request.post('/auth/login', data)
}

// 注册
export interface RegisterParams {
  username: string
  phone: string
  password: string
  confirmPassword: string
  verificationCode: string
}

export function register(data: RegisterParams) {
  return service.post('/auth/register', data)
}

// 获取验证码
export interface SendCodeParams {
  phone: string
}

export function sendVerificationCode(data: SendCodeParams) {
  return service.post('/auth/send-verification-code', data)
}

//忘记密码
export function resetPassword(data: {
  phone: string
  verificationCode: string
  newPassword: string
  confirmPassword: string
}) {
  return request.post('/auth/reset-password', data)
}
