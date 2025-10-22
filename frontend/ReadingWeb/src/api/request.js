import axios from 'axios'

const service = axios.create({
  baseURL: 'http://localhost:3000/api', // 接口文档 servers.url
  timeout: 5000,
})

// 可选：请求拦截器（自动加 token）
service.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export default service
