// src/api/ai.ts
import request from '@/utils/request'

export interface ChatHistoryParams {
  limit?: number
  cursor?: number
}

export function getChatHistory(bookId: number, params?: ChatHistoryParams) {
  return request.get(`/ai/chat/session/${bookId}`, { params })
}

export function sendChatMessage(bookId: number, message: string) {
  return request.post(`/ai/chat/session/${bookId}/message`, { message })
}

export function interpretSelectedText(selectedText: string, bookTitle?: string, chapterTitle?: string, followUp?: string) {
  return request.post('/reader/ai/interpret', {
    selectedText,
    bookTitle,
    chapterTitle,
    followUp,
  })
}

// 简单检测后端 AI 是否已配置（返回布尔）
export async function isAiConfigured() {
  try {
    // 尝试请求一个短的接口：取书籍会话（limit=1），如果返回 401/403/5xx则认为不可用
    // 注意：需要传入 bookId，这里用 1 做探测（后端应对不存在的 book 报 400/404），所以以 catch 为准
    await request.get('/ai/chat/session/1', { params: { limit: 1 } })
    return true
  } catch (e) {
    return false
  }
}

export default {
  getChatHistory,
  sendChatMessage,
  interpretSelectedText,
  isAiConfigured,
}
