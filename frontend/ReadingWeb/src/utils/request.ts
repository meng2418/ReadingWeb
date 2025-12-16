//src/utils/request.ts
import axios from 'axios'

const service = axios.create({
  baseURL: 'http://127.0.0.1:4523/m1/7570184-7307845-default', // 接口文档 servers.url
  timeout: 5000,
})

// 可选：请求拦截器（自动加 token）
service.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export default service
