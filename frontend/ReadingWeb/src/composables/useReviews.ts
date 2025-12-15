import type { Review, RatingStats } from '@/types/review'

// 定义本地存储中公开评论数据的键名
const PUBLIC_REVIEWS_KEY = 'publicReviews'
// 定义本地存储中用户个人评论数据的键名
const USER_REVIEWS_KEY = 'userReviews'

// 本地存储数据结构类型：双层嵌套的对象，外层键为userId/bookId，内层键为bookId/userId
type StorageRecord = Record<string, Record<string, Review>>

/**
 * 安全读取本地存储数据
 * @param key 存储键名
 * @param fallback 读取失败时的默认返回值
 * @returns 解析后的数据或默认值
 */
const safeRead = <T>(key: string, fallback: T): T => {
  try {
    const raw = localStorage.getItem(key) // 从本地存储获取原始数据
    return raw ? (JSON.parse(raw) as T) : fallback // 解析JSON或返回默认值
  } catch (error) {
    console.error(`读取本地存储 ${key} 失败:`, error) // 记录错误信息
    return fallback // 返回默认值
  }
}

/**
 * 写入数据到本地存储
 * @param key 存储键名
 * @param value 要存储的值
 */
const writeStorage = (key: string, value: unknown) => {
  localStorage.setItem(key, JSON.stringify(value)) // 序列化为JSON字符串并存储
}

/**
 * 格式化日期
 * @param input 日期输入（字符串、时间戳或Date对象）
 * @param options 配置选项
 * @param options.withTime 是否包含时间部分，默认包含
 * @param options.separator 日期分隔符，默认'-'
 * @returns 格式化后的日期字符串
 */
export const formatDate = (
  input: string | number | Date,
  options?: { withTime?: boolean; separator?: string },
) => {
  const date = new Date(input) // 创建Date对象
  if (Number.isNaN(date.getTime())) return String(input) // 无效日期返回原始输入

  const sep = options?.separator ?? '-' // 使用指定分隔符或默认'-'
  const y = date.getFullYear() // 年
  const m = (date.getMonth() + 1).toString().padStart(2, '0') // 月（补零）
  const d = date.getDate().toString().padStart(2, '0') // 日（补零）
  const base = [y, m, d].join(sep) // 拼接基础日期部分

  if (options?.withTime === false) return base // 不包含时间则直接返回

  const h = date.getHours().toString().padStart(2, '0') // 时（补零）
  const min = date.getMinutes().toString().padStart(2, '0') // 分（补零）
  return `${base} ${h}:${min}` // 返回完整日期时间
}

/**
 * 确保当前用户ID存在（不存在则创建）
 * @returns 当前用户ID
 */
export const ensureCurrentUserId = () => {
  let userId = localStorage.getItem('currentUserId') // 尝试获取现有用户ID
  if (!userId) {
    // 生成唯一用户ID：user_时间戳_随机字符串
    userId = `user_${Date.now()}_${Math.random().toString(36).slice(2, 11)}`
    localStorage.setItem('currentUserId', userId) // 存储新生成的用户ID
  }
  return userId
}

/**
 * 获取用户对某本书的评论
 * @param bookId 书籍ID
 * @param userId 用户ID
 * @returns 评论对象或null
 */
export const getUserReview = (bookId: string, userId: string) => {
  if (!bookId) return null // 无书籍ID直接返回null
  // 读取用户评论数据
  const userReviews = safeRead<Record<string, Record<string, Review>>>(USER_REVIEWS_KEY, {})
  return userReviews[userId]?.[bookId] ?? null // 返回指定用户的书籍评论
}

/**
 * 插入或更新用户评论（个人存储）
 * @param review 评论对象
 */
export const upsertUserReview = (review: Review) => {
  if (!review.bookId) return // 无书籍ID则直接返回
  const userId = review.userId ?? ensureCurrentUserId() // 使用传入的userId或获取当前用户ID
  const userReviews = safeRead<Record<string, Record<string, Review>>>(USER_REVIEWS_KEY, {})
  if (!userReviews[userId]) userReviews[userId] = {} // 初始化用户的评论记录
  userReviews[userId][review.bookId] = { ...review, userId } // 存储评论
  writeStorage(USER_REVIEWS_KEY, userReviews) // 写入本地存储
}

/**
 * 删除用户评论（个人存储）
 * @param bookId 书籍ID
 * @param userId 用户ID
 */
export const removeUserReview = (bookId: string, userId: string) => {
  const userReviews = safeRead<Record<string, Record<string, Review>>>(USER_REVIEWS_KEY, {})
  if (userReviews[userId]?.[bookId]) {
    delete userReviews[userId][bookId] // 删除指定评论
    writeStorage(USER_REVIEWS_KEY, userReviews) // 更新存储
  }
}

/**
 * 插入或更新公开评论（公开存储）
 * @param bookId 书籍ID
 * @param review 评论对象
 */
export const upsertPublicReview = (bookId: string, review: Review) => {
  if (!bookId) return // 无书籍ID则直接返回
  const userId = review.userId ?? ensureCurrentUserId() // 使用传入的userId或获取当前用户ID
  const publicReviews = safeRead<StorageRecord>(PUBLIC_REVIEWS_KEY, {})
  if (!publicReviews[bookId]) publicReviews[bookId] = {} // 初始化该书籍的评论记录
  publicReviews[bookId][userId] = { ...review, userId, bookId } // 存储评论
  writeStorage(PUBLIC_REVIEWS_KEY, publicReviews) // 写入本地存储
}

/**
 * 删除公开评论（公开存储）
 * @param bookId 书籍ID
 * @param userId 用户ID
 */
export const removePublicReview = (bookId: string, userId: string) => {
  const publicReviews = safeRead<StorageRecord>(PUBLIC_REVIEWS_KEY, {})
  if (publicReviews[bookId]?.[userId]) {
    delete publicReviews[bookId][userId] // 删除指定评论
    writeStorage(PUBLIC_REVIEWS_KEY, publicReviews) // 更新存储
  }
}

/**
 * 获取某本书的最新公开评论（每个用户只保留最新一条）
 * @param bookId 书籍ID
 * @returns 最新评论数组
 */
export const getLatestPublicReviews = (bookId: string): Review[] => {
  if (!bookId) return [] // 无书籍ID返回空数组
  const publicReviews = safeRead<StorageRecord>(PUBLIC_REVIEWS_KEY, {})
  const bookPublicReviews = publicReviews[bookId] || {} // 获取该书籍的所有公开评论

  const userLatest = new Map<string, Review>() // 使用Map记录每个用户的最新评论
  Object.values(bookPublicReviews).forEach((review) => {
    if (review.isPublic === false) return // 跳过非公开评论

    const userId = review.userId ?? 'anonymous' // 用户ID（匿名用户处理）
    const existing = userLatest.get(userId) // 获取该用户已有的最新评论
    const curTime = new Date(review.lastEditDate || review.date) // 当前评论时间

    // 如果该用户还没有评论，或当前评论时间更晚，则更新
    if (!existing || curTime > new Date(existing.lastEditDate || existing.date)) {
      userLatest.set(userId, review)
    }
  })

  return Array.from(userLatest.values()) // 转换为数组返回
}

/**
 * 计算某本书的评分统计
 * @param bookId 书籍ID
 * @param defaults 默认统计值
 * @returns 更新后的评分统计
 */
export const computeRatingStats = (bookId: string, defaults: RatingStats) => {
  let { recommend, average, poor } = defaults // 解构默认值
  // 遍历所有公开评论，统计各评分数量
  getLatestPublicReviews(bookId).forEach((review) => {
    switch (review.rating) {
      case 'recommend':
        recommend++ // 推荐数+1
        break
      case 'average':
        average++ // 一般数+1
        break
      case 'poor':
        poor++ // 差评数+1
        break
      default:
        break // 其他评分类型忽略
    }
  })
  return { recommend, average, poor } // 返回统计结果
}

/**
 * 计算某本书的公开评论总数
 * @param bookId 书籍ID
 * @param baseCount 基础数量（默认为0）
 * @returns 评论总数
 */
export const countPublicReviews = (bookId: string, baseCount = 0) => {
  return baseCount + getLatestPublicReviews(bookId).length // 基础数量+公开评论数量
}
