// src/api/topics/topics-list.ts
import request from '@/utils/request'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

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
