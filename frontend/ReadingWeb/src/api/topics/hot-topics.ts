// src/api/topics/hot-topics.ts
import request from '@/utils/request'


/** 后端热门话题结构（根据你提供的接口） */
interface RawHotTopic {
  topicId: number
  topicName: string
}

/** 后端热门话题响应 */
interface RawHotTopicsResponse {
  code: number
  message: string
  data: Array<{
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
const mapHotTopic = (raw: RawHotTopic): HotTopic => ({
  id: raw.topicId,
  name: `# ${raw.topicName}`
})

/** 获取热门话题列表 */
export const getHotTopics = async (): Promise<HotTopic[]> => {
  try {
    const res = await request.get<RawHotTopicsResponse>('/topics/hot')
    console.log('热门话题原始响应:', res)

    // 根据标准接口，直接从 res.data.data 获取话题数组
    const topics: RawHotTopic[] = res.data?.data || []
    console.log('解析出的热门话题:', topics)

    if (topics.length === 0) {
      console.warn('热门话题接口返回空数组')
    }

    return topics.map(mapHotTopic)
  } catch (error) {
    console.error('获取热门话题失败:', error)
    return []
  }
}
