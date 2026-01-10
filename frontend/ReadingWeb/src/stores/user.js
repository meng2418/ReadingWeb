// src/stores/user.js
import { defineStore } from 'pinia'
import { getProfileHome } from '@/api/profile'

export const useUserStore = defineStore('user', {
  state: () => ({
    // =====================
    // 登录状态
    // =====================
    isLoggedIn: true,

    // =====================
    // 用户基础信息
    // =====================
    userInfo: {
      name: '',
      avatar: '',
    },

    // =====================
    // token
    // =====================
    token: localStorage.getItem('user_token') || null,

    // =====================
    // 阅读相关（新增）
    // =====================
    consecutiveReadingDays: 0, // 连续阅读天数（来自 /user/home）
  }),

  getters: {
    // 是否已认证
    isAuthenticated: (state) => {
      return !!state.token
    },
  },

  actions: {
    // =====================
    // 登录（新）
    // =====================
    login(data) {
      const { token, userInfo } = data

      this.token = token
      this.isLoggedIn = true
      this.userInfo = userInfo || { name: '', avatar: '' }

      localStorage.setItem('user_token', token)
      if (userInfo) {
        localStorage.setItem('user_info', JSON.stringify(userInfo))
      }

      console.log('用户登录成功:', this.userInfo.name)
    },

    // =====================
    // 登录（旧，兼容）
    // =====================
    loginOld(name, avatar) {
      const token = this.generateToken()
      this.login({
        token,
        userInfo: { name, avatar },
      })
    },

    // =====================
    // 退出登录
    // =====================
    logout() {
      this.isLoggedIn = false
      this.token = null
      this.userInfo = { name: '', avatar: '' }
      this.consecutiveReadingDays = 0

      localStorage.removeItem('user_token')
      localStorage.removeItem('user_info')

      console.log('用户已退出登录')
    },

    // =====================
    // 恢复会话
    // =====================
    restoreSession() {
      const token = localStorage.getItem('user_token')
      const userInfoStr = localStorage.getItem('user_info')

      if (token) {
        this.token = token
        this.isLoggedIn = true

        if (userInfoStr) {
          try {
            this.userInfo = JSON.parse(userInfoStr)
          } catch (e) {
            console.error('解析用户信息失败:', e)
            this.userInfo = { name: '', avatar: '' }
          }
        }

        console.log('会话已恢复:', this.userInfo.name)
      }
    },

    // =====================
    // 拉取用户主页数据（新增，关键）
    // =====================
    async fetchUserHome() {
      try {
        const data = await getProfileHome()
        this.consecutiveReadingDays = data.consecutiveReadingDays ?? 0
      } catch (e) {
        console.error('获取用户主页数据失败:', e)
      }
    },

    // =====================
    // 工具方法
    // =====================
    generateToken() {
      return 'mock_token_' + Date.now() + '_' + Math.random().toString(36).substr(2)
    },

    checkLogin() {
      return this.isLoggedIn && !!this.token
    },
  },
})
