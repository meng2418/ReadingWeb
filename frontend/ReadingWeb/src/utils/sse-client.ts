export type SseEventHandler = (event: string, data: string) => void

export interface FetchSsePostOptions {
  url: string
  body?: unknown
  signal?: AbortSignal
  onEvent: SseEventHandler
}

export async function fetchSsePost({
  url,
  body,
  signal,
  onEvent,
}: FetchSsePostOptions): Promise<void> {
  const token = localStorage.getItem('user_token')
  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Accept: 'text/event-stream',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: body ? JSON.stringify(body) : undefined,
    signal,
  })

  if (!response.ok) {
    const errText = await response.text().catch(() => '')
    let message = errText || `请求失败 (${response.status})`
    try {
      const parsed = JSON.parse(errText) as { message?: string; data?: { message?: string } }
      message = parsed.message || parsed.data?.message || message
    } catch {
      // keep raw text
    }
    if (response.status === 401) {
      message = '登录已过期，请重新登录后再试'
    }
    throw new Error(message)
  }

  const reader = response.body?.getReader()
  if (!reader) {
    throw new Error('无法读取响应流')
  }

  const decoder = new TextDecoder()
  let buffer = ''

  while (true) {
    const { done, value } = await reader.read()
    if (done) break
    buffer += decoder.decode(value, { stream: true })

    let separatorIndex = buffer.indexOf('\n\n')
    while (separatorIndex !== -1) {
      const block = buffer.slice(0, separatorIndex)
      buffer = buffer.slice(separatorIndex + 2)
      parseSseBlock(block, onEvent)
      separatorIndex = buffer.indexOf('\n\n')
    }
  }

  if (buffer.trim()) {
    parseSseBlock(buffer, onEvent)
  }
}

function parseSseBlock(block: string, onEvent: SseEventHandler) {
  let eventName = 'message'
  const dataLines: string[] = []

  for (const rawLine of block.split('\n')) {
    const line = rawLine.trimEnd()
    if (line.startsWith('event:')) {
      eventName = line.slice(6).trim()
    } else if (line.startsWith('data:')) {
      dataLines.push(line.slice(5).trimStart())
    }
  }

  if (dataLines.length > 0) {
    onEvent(eventName, dataLines.join('\n'))
  }
}
