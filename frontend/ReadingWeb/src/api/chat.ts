// src/api/chat.ts
import request from '@/utils/request'
import { processCoverPath } from '@/utils/imagePath'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

/** 书籍消息附带的书籍信息 */
export interface ChatBookInfo {
  bookId: number
  bookTitle: string
  cover: string
  authorName: string
  description: string
}

/** 统一后的前端消息模型（兼容真实后端字段与接口规格字段） */
export interface NormalizedMessage {
  messageId: number
  conversationId: number | null
  senderId: number
  receiverId: number
  senderName?: string
  senderAvatar?: string
  messageType: string // 'text' | 'book' | 'image'
  content: string
  book?: ChatBookInfo
  isWithdrawn: boolean
  createdAt: string
  from: 'self' | 'other'
}

/** 会话列表项 */
export interface ChatConversationItem {
  id: string
  userId: number
  username: string
  avatar: string
  lastMessage: string
  /** 原始时间字符串，由调用方按需格式化 */
  lastTime: string
}

/** 发送消息请求体 */
export interface SendMessagePayload {
  receiverId: number
  messageType: string
  content: string
  bookInfo?: ChatBookInfo
}

/**
 * 兼容书籍字段：
 * - 接口① getMessages 返回 BookVO：{ bookId, bookTitle, cover, authorName }
 * - 接口② sendMessage 返回原始 BookEntity：{ bookId, title, cover, authorName }
 * - 规格字段：bookInfo / description
 */
const normalizeBook = (raw: any): ChatBookInfo | undefined => {
  const b = raw?.book ?? raw?.bookInfo
  if (!b || (b.bookId == null && b.id == null)) return undefined
  return {
    bookId: Number(b.bookId ?? b.id ?? 0),
    bookTitle: String(b.bookTitle ?? b.title ?? ''),
    cover: processCoverPath(String(b.cover ?? '')),
    authorName: String(b.authorName ?? b.author ?? ''),
    description: String(b.description ?? ''),
  }
}

/**
 * 把后端任意形态的一条消息归一化为前端模型。
 * 兼容：isRecalled↔isWithdrawn、createdAt↔sendTime、book↔bookInfo、嵌套 conversation。
 */
export const normalizeMessage = (raw: any, currentUserId: number | null): NormalizedMessage => {
  const senderId = Number(raw?.senderId ?? 0)
  const conversationId =
    raw?.conversationId != null
      ? Number(raw.conversationId)
      : raw?.conversation?.conversationId != null
        ? Number(raw.conversation.conversationId)
        : null
  return {
    messageId: Number(raw?.messageId ?? raw?.id ?? 0),
    conversationId,
    senderId,
    receiverId: Number(raw?.receiverId ?? raw?.receiver?.userId ?? raw?.receiver?.id ?? 0),
    senderName: String(raw?.senderName ?? raw?.sender?.username ?? ''),
    senderAvatar: String(raw?.senderAvatar ?? raw?.sender?.avatar ?? ''),
    messageType: String(raw?.messageType ?? 'text'),
    content: String(raw?.content ?? ''),
    book: normalizeBook(raw),
    isWithdrawn: Boolean(raw?.isWithdrawn ?? raw?.isRecalled ?? false),
    createdAt: String(raw?.createdAt ?? raw?.sendTime ?? ''),
    from: currentUserId != null && senderId === currentUserId ? 'self' : 'other',
  }
}

/**
 * 接口①：获取与某用户的聊天记录
 * GET /chat/conversation/{userId}
 * 真实后端返回扁平数组；同时兼容规格 { messages, targetUser, hasMore, nextCursor }。
 */
export const getConversationMessages = async (
  userId: number | string,
  currentUserId: number | null,
): Promise<{
  messages: NormalizedMessage[]
  targetUser?: { userId: number; username: string; avatar: string }
  hasMore: boolean
  nextCursor: number | null
}> => {
  const data = unwrap(await request.get(`/chat/conversation/${userId}`))
  const rawList: any[] = Array.isArray(data)
    ? data
    : Array.isArray(data?.messages)
      ? data.messages
      : []
  return {
    messages: rawList.map((m) => normalizeMessage(m, currentUserId)),
    targetUser: Array.isArray(data) ? undefined : data?.targetUser,
    hasMore: Array.isArray(data) ? false : Boolean(data?.hasMore),
    nextCursor: Array.isArray(data) ? null : (data?.nextCursor ?? null),
  }
}

/**
 * 接口②：发送私信消息
 * POST /chat/message
 * 真实后端 data 直接是消息实体；同时兼容规格 { message, showTimestamp }。
 */
export const sendMessage = async (
  payload: SendMessagePayload,
  currentUserId: number | null,
): Promise<NormalizedMessage> => {
  const data = unwrap(await request.post('/chat/message', payload))
  const rawMsg = data?.message ?? data
  return normalizeMessage(rawMsg, currentUserId)
}

/**
 * 接口③：撤回消息
 * POST /chat/message/{messageId}/withdraw
 * 真实后端返回 data:null；错误时由 axios 抛出（含后端 message）。
 */
export const withdrawMessage = async (messageId: number): Promise<void> => {
  await request.post(`/chat/message/${messageId}/withdraw`)
}

/**
 * 会话列表
 * GET /chat/conversations → ConversationVO[]
 */
export const getConversations = async (): Promise<ChatConversationItem[]> => {
  const data = unwrap(await request.get('/chat/conversations'))
  const list: any[] = Array.isArray(data) ? data : Array.isArray(data?.items) ? data.items : []
  return list
    .map((c) => {
      const userId = Number(c.targetUserId ?? c.userId ?? 0)
      return {
        id: String(userId),
        userId,
        username: String(c.nickname ?? c.username ?? (userId ? `用户${userId}` : '未知用户')),
        avatar: String(c.avatar ?? ''),
        lastMessage: String(c.lastMessageContent ?? c.lastMessage ?? ''),
        lastTime: String(c.lastMessageTime ?? c.lastTime ?? ''),
      }
    })
    .filter((c) => c.userId > 0)
}
