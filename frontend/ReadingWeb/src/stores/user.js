// stores/user.js
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    // 登录状态
    isLoggedIn: false,
    // 用户信息
    userInfo: {
      name: '',
      avatar: '',
    },
    // token令牌（新增）
    token: localStorage.getItem('user_token') || null,
  }),

  getters: {
    // 计算属性：是否已认证（基于token）
    isAuthenticated: (state) => {
      return !!state.token
    }
  },

  actions: {
    // 登录方法（支持新格式）
    login(data) {
      const { token, userInfo } = data

      // 更新状态
      this.token = token
      this.isLoggedIn = true
      this.userInfo = userInfo || { name: '', avatar: '' }

      // 保存到本地存储
      localStorage.setItem('user_token', token)
      if (userInfo) {
        localStorage.setItem('user_info', JSON.stringify(userInfo))
      }

      console.log('用户登录成功:', this.userInfo.name)
    },

    // 保持兼容性的登录方法
    loginOld(name, avatar) {
      const token = this.generateToken() // 生成模拟token
      this.login({
        token,
        userInfo: { name, avatar }
      })
    },

    // 退出登录
    logout() {
      this.isLoggedIn = false
      this.token = null
      this.userInfo = { name: '', avatar: '' }

      // 清除本地存储
      localStorage.removeItem('user_token')
      localStorage.removeItem('user_info')

      console.log('用户已退出登录')
    },

    // 恢复会话（页面刷新时调用）
    restoreSession() {
      // 从本地存储恢复会话
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

    // 生成模拟token（用于测试）
    generateToken() {
      return 'mock_token_' + Date.now() + '_' + Math.random().toString(36).substr(2)
    },

    // 检查登录状态
    checkLogin() {
      return this.isLoggedIn && !!this.token
    }
  }
})
