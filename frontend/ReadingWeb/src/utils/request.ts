// src/utils/request.ts
import axios from 'axios'

const service = axios.create({
  // 重点修改：不要写死 http://localhost:8080
  // 改成 '/api'，这样请求发给前端服务器，Vite 代理才会拦截并转发
  baseURL: '/api',
  // baseURL: 'https://m1.apifoxmock.com/m1/7605134-7343879-default',
  timeout: 5000,
})

// 请求拦截器
service.interceptors.request.use((config) => {
  // 重点修改：你之前的 Store 和 Login 逻辑里，存的 key 是 'user_token'
  // 如果这里写 'token'，是取不到值的！
  const token = localStorage.getItem('user_token')

  if (token) {
    // 注意：有些后端要求 'Bearer ' (有个空格)，有些不需要，看文档
    config.headers.Authorization = `Bearer ${token}`
  }
  
  // 禁用缓存，避免304响应导致数据为空
  config.headers['Cache-Control'] = 'no-cache, no-store, must-revalidate'
  config.headers['Pragma'] = 'no-cache'
  config.headers['Expires'] = '0'
  
  return config
}, (error) => {
  return Promise.reject(error)
})

// 响应拦截器 (建议加上，方便处理)
service.interceptors.response.use(
  (response) => {
    // 处理304 Not Modified响应 - 304时响应体可能为空，需要重新请求
    if (response.status === 304) {
      const config = response.config
      // 避免无限循环：如果已经添加了时间戳，就不再添加
      if (config.url && !config.url.includes('_t=')) {
        const separator = config.url.includes('?') ? '&' : '?'
        config.url = `${config.url}${separator}_t=${Date.now()}`
        // 重新发起请求
        return service.request(config)
      }
      // 如果已经添加了时间戳但还是304，说明服务器真的返回304，抛出错误
      return Promise.reject(new Error('304 Not Modified: 数据未更新'))
    }
    // 直接返回 response，或者返回 response.data，看你个人习惯
    return response
  },
  (error) => {
    // 这里可以统一处理 401 token 过期跳转登录页
    if (error.response && error.response.status === 401) {
      // 清除缓存并跳转登录
      localStorage.removeItem('user_token')
      localStorage.removeItem('user_info')
      window.location.href = '/login' // 暴力跳转
    }
    return Promise.reject(error)
  }
)

export default service
