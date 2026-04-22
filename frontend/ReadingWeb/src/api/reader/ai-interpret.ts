/**
 * 阅读器 AI 解读：对接端侧大模型（本地 Ollama / 后端代理）
 */
import request from '@/utils/request'

export interface InterpretOptions {
  bookTitle?: string
  chapterTitle?: string
  /** 追问内容，与 selectedText 一起发给模型 */
  followUp?: string
}

export interface InterpretResponse {
  text: string
}

/**
 * 请求端侧大模型对选中文本做解读
 * 后端会代理到配置的 LLM（如 Ollama），或前端可改为直连本地 URL
 */
export async function interpretText(
  selectedText: string,
  options: InterpretOptions = {},
): Promise<string> {
  const res = await request.post<InterpretResponse>('/reader/ai/interpret', {
    selectedText: selectedText.trim(),
    bookTitle: options.bookTitle ?? undefined,
    chapterTitle: options.chapterTitle ?? undefined,
    followUp: options.followUp ?? undefined,
  })
  const data = res?.data?.data ?? res?.data
  if (data && typeof (data as InterpretResponse).text === 'string') {
    return (data as InterpretResponse).text
  }
  throw new Error('AI 解读返回格式异常')
}
