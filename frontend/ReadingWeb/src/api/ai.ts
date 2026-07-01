// src/api/ai.ts
import request from '@/utils/request'
import { fetchSsePost } from '@/utils/sse-client'

export interface ChatHistoryParams {
  limit?: number
  cursor?: number
}

export interface ChatHistoryMessage {
  messageId: number
  role: 'user' | 'assistant' | 'system'
  content: string
  createdAt: string
}

export interface ChatHistoryData {
  bookTitle: string
  messages: ChatHistoryMessage[]
  hasMore: boolean
  nextCursor: number | null
}

interface ApiResult<T> {
  code: number
  message: string
  data: T
}

export interface ChatStreamCallbacks {
  onMeta?: (userMessageId: number) => void
  onChunk: (chunk: string) => void
  onDone?: (payload: { messageId: number; content: string }) => void
  onError?: (message: string) => void
}

export interface ChatStreamOptions {
  signal?: AbortSignal
}

export function getChatHistory(bookId: number, params?: ChatHistoryParams) {
  return request.get<ApiResult<ChatHistoryData>>(`/ai/chat/session/${bookId}`, { params })
}

export function sendChatMessage(bookId: number, message: string) {
  return request.post(`/ai/chat/session/${bookId}/message`, { message })
}

/**
 * 流式发送 AI 对话消息
 */
export async function sendChatMessageStream(
  bookId: number,
  message: string,
  callbacks: ChatStreamCallbacks,
  options: ChatStreamOptions = {},
): Promise<void> {
  const { onMeta, onChunk, onDone, onError } = callbacks
  let fullText = ''

  try {
    await fetchSsePost({
      url: `/api/ai/chat/session/${bookId}/message/stream`,
      body: { message },
      signal: options.signal,
      onEvent: (event, data) => {
        if (event === 'meta') {
          try {
            const parsed = JSON.parse(data) as { userMessageId?: number }
            if (parsed.userMessageId != null) {
              onMeta?.(parsed.userMessageId)
            }
          } catch {
            // ignore malformed meta
          }
        } else if (event === 'chunk') {
          let text = data
          try {
            text = JSON.parse(data) as string
          } catch {
            // 兼容非 JSON 编码的旧格式
          }
          fullText += text
          onChunk(text)
        } else if (event === 'done') {
          try {
            const parsed = JSON.parse(data) as { messageId: number; content: string }
            onDone?.(parsed)
          } catch {
            onDone?.({ messageId: 0, content: data || fullText })
          }
        } else if (event === 'error') {
          onError?.(data)
        }
      },
    })
    if (!fullText) {
      onDone?.({ messageId: 0, content: '' })
    }
  } catch (err) {
    const msg = err instanceof Error ? err.message : 'AI 对话请求失败'
    onError?.(msg)
    throw err
  }
}

export function interpretSelectedText(
  selectedText: string,
  bookTitle?: string,
  chapterTitle?: string,
  followUp?: string,
) {
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
    await request.get('/ai/chat/session/1', { params: { limit: 1 } })
    return true
  } catch (e) {
    return false
  }
}

export default {
  getChatHistory,
  sendChatMessage,
  sendChatMessageStream,
  interpretSelectedText,
  isAiConfigured,
}
