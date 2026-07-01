/**
 * 阅读器 AI 解读：对接端侧大模型 / 云端代理，支持 SSE 流式输出
 */
import { fetchSsePost } from '@/utils/sse-client'

export interface InterpretOptions {
  bookTitle?: string
  chapterTitle?: string
  /** 追问内容，与 selectedText 一起发给模型 */
  followUp?: string
}

export interface InterpretStreamCallbacks {
  onChunk: (chunk: string) => void
  onDone?: (fullText: string) => void
  onError?: (message: string) => void
}

export interface InterpretStreamOptions extends InterpretOptions {
  signal?: AbortSignal
}

/**
 * 流式解读选中文本，逐块回调 onChunk
 */
export async function interpretTextStream(
  selectedText: string,
  callbacks: InterpretStreamCallbacks,
  options: InterpretStreamOptions = {},
): Promise<void> {
  const { onChunk, onDone, onError } = callbacks
  let fullText = ''

  try {
    await fetchSsePost({
      url: '/api/reader/ai/interpret/stream',
      body: {
        selectedText: selectedText.trim(),
        bookTitle: options.bookTitle ?? undefined,
        chapterTitle: options.chapterTitle ?? undefined,
        followUp: options.followUp ?? undefined,
      },
      signal: options.signal,
      onEvent: (event, data) => {
        if (event === 'chunk') {
          let text = data
          try {
            text = JSON.parse(data) as string
          } catch {
            // 兼容非 JSON 编码的旧格式
          }
          fullText += text
          onChunk(text)
        } else if (event === 'done') {
          let finalText = fullText
          if (data) {
            try {
              finalText = JSON.parse(data) as string
            } catch {
              finalText = data || fullText
            }
          }
          onDone?.(finalText)
        } else if (event === 'error') {
          onError?.(data)
        }
      },
    })
    if (!fullText) {
      onDone?.('')
    }
  } catch (err) {
    const message = err instanceof Error ? err.message : 'AI 解读请求失败'
    onError?.(message)
    throw err
  }
}

/** @deprecated 请使用 interpretTextStream 获得更好的体验 */
export async function interpretText(
  selectedText: string,
  options: InterpretOptions = {},
): Promise<string> {
  return new Promise((resolve, reject) => {
    let result = ''
    interpretTextStream(
      selectedText,
      {
        onChunk: (chunk) => {
          result += chunk
        },
        onDone: (fullText) => resolve(fullText || result),
        onError: (message) => reject(new Error(message)),
      },
      options,
    ).catch(reject)
  })
}
