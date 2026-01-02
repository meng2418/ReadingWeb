// src/api/topics.ts (修改后的完整代码)
import request from '@/utils/request'
import type { Post } from '@/types/post'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

/** 后端话题详情结构 */
export interface RawTopic {
  image: string
  name: string
  postCount: number
  description: string
  followerCount: number
  todayPostCount: number
  isFollowing: boolean
  introduction: string
  createdAt: string
  adminName: string
  relatedTopics: Array<{
    image: string
    name: string
    postCount: number
  }>
}

/** 前端话题列表项结构（用于话题卡片） */
export interface TopicListItem {
  id: string | number
  title: string
  name: string
  cover: string
  number: number // 帖子总数
  postCount: number
}

/** 后端话题列表结构 */
interface RawTopicsResponse {
  items: Array<{
    topicId: number
    topicName: string
    image: string
    postCount: number
  }>
  hasMore: boolean
  nextCursor: number
}

/** 前端话题详情结构 */
export interface TopicDetail {
  id: string
  title: string
  name: string
  description: string
  fullDescription: string
  cover: string
  postCount: number
  followerCount: number
  dailyActive: number
  createTime: string
  manager: string
  relatedTopics: Array<{
    id: string
    cover: string
    title: string
    postCount: number
  }>
}

/** 后端帖子结构 */
interface RawTopicPost {
  postId: number
  author: {
    authorName: string
    authorAvatar: string
  }
  publishTime: string
  publishLocation?: string
  isFollowingAuthor: boolean
  postTitle: string
  content: string
  mentionedFirstBook?: {
    cover: string
    bookTitle: string
    authorName: string
  }
  commentCount: number
  likeCount: number
  isLiked: boolean
}

/** 后端热门话题结构（根据你提供的接口） */
interface RawHotTopic {
  topicId: number
  topicName: string
}

/** 后端热门话题响应 */
interface RawHotTopicsResponse {
  items?: Array<{
    topicId: number
    topicName: string
  }>
}

/** 前端热门话题结构（用于HotTopics组件） */
export interface HotTopic {
  id: number
  name: string
}

/** 数据转换函数 */
const mapTopicDetail = (raw: RawTopic, topicId: string): TopicDetail => ({
  id: topicId,
  title: raw.name,
  name: raw.name,
  description: raw.description,
  fullDescription: raw.introduction,
  cover: raw.image,
  postCount: raw.postCount,
  followerCount: raw.followerCount,
  dailyActive: raw.todayPostCount,
  createTime: raw.createdAt,
  manager: raw.adminName,
  relatedTopics: raw.relatedTopics.map((topic, index) => ({
    id: `related-${index}`,
    cover: topic.image,
    title: topic.name,
    postCount: topic.postCount,
  })),
})

const mapPost = (raw: RawTopicPost): Post => ({
  id: raw.postId,
  username: raw.author.authorName,
  avatar: raw.author.authorAvatar,
  postTime: raw.publishTime,
  timestamp: new Date(raw.publishTime).getTime(),
  title: raw.postTitle,
  content: raw.content,
  likeCount: raw.likeCount,
  commentCount: raw.commentCount,
  isFollowing: raw.isFollowingAuthor,
  isLiked: raw.isLiked,
  book: raw.mentionedFirstBook
    ? {
        id: raw.postId, // 临时使用postId作为bookId
        title: raw.mentionedFirstBook.bookTitle,
        author: raw.mentionedFirstBook.authorName,
        cover: raw.mentionedFirstBook.cover,
      }
    : null,
})

const mapHotTopic = (raw: RawHotTopic): HotTopic => ({
  id: raw.topicId,
  name: `# ${raw.topicName}`
})

const mapTopicListItem = (raw: any): TopicListItem => ({
  id: raw.topicId,
  title: raw.topicName,
  name: raw.topicName,
  cover: raw.image,
  number: raw.postCount,
  postCount: raw.postCount,
})

/** 获取话题列表 */
export const getTopicsList = async (
  cursor?: number,
  limit: number = 9
): Promise<{
  items: TopicListItem[]
  hasMore: boolean
  nextCursor?: number
}> => {
  const res = await request.get<RawTopicsResponse>('/topics', {
    params: { cursor, limit }
  })

  const data = unwrap(res)

  return {
    items: data.items.map(mapTopicListItem),
    hasMore: data.hasMore,
    nextCursor: data.nextCursor
  }
}

/** 获取话题详情 */
export const getTopicDetail = async (topicId: string): Promise<TopicDetail> => {
  const res = await request.get<RawTopic>(`/topics/${topicId}`)
  const rawData = unwrap(res)
  return mapTopicDetail(rawData, topicId)
}

/** 获取话题帖子 */
export const getTopicPosts = async (
  topicId: string,
  sort: 'latest' | 'hot' | 'quality' = 'latest',
  page: number = 1,
  limit: number = 20
): Promise<Post[]> => {
  const res = await request.get<RawTopicPost[]>(`/topics/${topicId}/posts`, {
    params: { sort, page, limit }
  })
  const list: RawTopicPost[] = unwrap(res)
  return list.map(mapPost)
}

/** 获取热门话题列表 */
export const getHotTopics = async (): Promise<HotTopic[]> => {
  const res = await request.get<RawHotTopicsResponse>('/topics/hot')
  const data = unwrap(res)

  // 处理不同的返回格式
  if (Array.isArray(data)) {
    // 如果是数组格式 [{"topicName":"string","topicId":0}]
    return data.map(mapHotTopic)
  } else if (data.items && Array.isArray(data.items)) {
    // 如果是包含items的对象格式
    return data.items.map(mapHotTopic)
  }

  console.error('热门话题接口返回格式异常:', data)
  return []
}
