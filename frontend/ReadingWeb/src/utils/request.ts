//src/utils/request.ts
import axios from 'axios'

const service = axios.create({
  // 使用 OpenAPI 文档的服务器地址，方便本地联调
  baseURL: 'https://m1.apifoxmock.com/m1/7605134-7343879-default',
  timeout: 5000,
})

// 可选：请求拦截器（自动加 token）
service.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export default service
