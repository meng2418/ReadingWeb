import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    isLoggedIn: false,
    userInfo: {
      name: '',
      avatar: '',
    },
  }),
  actions: {
    login(name, avatar) {
      this.isLoggedIn = true
      this.userInfo = { name, avatar }
    },
    logout() {
      this.isLoggedIn = false
      this.userInfo = { name: '', avatar: '' }
    },
    restoreSession() {
      const token = localStorage.getItem('token')
      const userInfo = localStorage.getItem('userInfo')
      if (token && userInfo) {
        this.token = token
        this.userInfo = JSON.parse(userInfo)
      }
    },
  },
})
