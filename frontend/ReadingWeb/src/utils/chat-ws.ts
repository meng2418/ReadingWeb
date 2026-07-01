export type ChatWsMessage = {
  type: string
  payload?: Record<string, unknown>
}

export type ChatWsListener = (event: ChatWsMessage) => void

export interface ChatWsSendPayload {
  receiverId: number
  messageType: string
  content: string
  bookInfo?: Record<string, unknown>
}

let socket: WebSocket | null = null
let listeners: ChatWsListener[] = []
let isConnected = false

const notifyListeners = (event: ChatWsMessage) => {
  listeners.forEach((listener) => {
    try {
      listener(event)
    } catch (error) {
      console.error('chat-ws listener error', error)
    }
  })
}

export const addChatWebSocketListener = (listener: ChatWsListener): (() => void) => {
  listeners.push(listener)
  return () => {
    listeners = listeners.filter((item) => item !== listener)
  }
}

export const connectChatWebSocket = async (): Promise<void> => {
  if (socket && socket.readyState === WebSocket.OPEN) {
    return
  }

  const token = localStorage.getItem('user_token')
  if (!token) {
    throw new Error('WebSocket token not found')
  }

  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  const url = `${protocol}//${window.location.host}/ws/chat?token=${encodeURIComponent(token)}`

  closeChatWebSocket()

  return new Promise((resolve, reject) => {
    socket = new WebSocket(url)

    socket.onopen = () => {
      isConnected = true
      resolve()
    }

    socket.onmessage = (event) => {
      try {
        const payload = JSON.parse(event.data) as ChatWsMessage
        notifyListeners(payload)
      } catch (error: unknown) {
        console.error('chat-ws received invalid JSON', error)
      }
    }

    socket.onclose = () => {
      isConnected = false
      socket = null
      notifyListeners({ type: 'close' })
    }

    socket.onerror = (event) => {
      console.error('chat-ws error', event)
      if (!isConnected) {
        reject(new Error('WebSocket connection failed'))
      }
    }
  })
}

export const closeChatWebSocket = (): void => {
  if (socket) {
    try {
      socket.close()
    } catch (error) {
      console.error('chat-ws close failed', error)
    }
  }
  socket = null
  isConnected = false
}

export const sendChatWsSendMessage = async (payload: ChatWsSendPayload): Promise<void> => {
  if (!socket || socket.readyState !== WebSocket.OPEN) {
    throw new Error('WebSocket not connected')
  }
  socket.send(JSON.stringify({ action: 'sendMessage', ...payload }))
}

export const chatWsIsConnected = (): boolean => isConnected
