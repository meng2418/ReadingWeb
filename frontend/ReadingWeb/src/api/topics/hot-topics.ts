// src/api/topics/hot-topics.ts
import request from '@/utils/request'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

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
const mapHotTopic = (raw: RawHotTopic): HotTopic => ({
  id: raw.topicId,
  name: `# ${raw.topicName}`
})

/** 获取热门话题列表 */
export const getHotTopics = async (): Promise<HotTopic[]> => {
  try {
    const res = await request.get<RawHotTopicsResponse>('/topics/hot')
    console.log('热门话题原始响应:', res)

    // 使用 unwrap 提取数据
    const data = unwrap(res)
    console.log('提取后的数据:', data)

    // 处理不同的返回格式
    let topics: RawHotTopic[] = []

    if (Array.isArray(data)) {
      // 格式: [{"topicName":"string","topicId":0}]
      topics = data
    } else if (data?.items && Array.isArray(data.items)) {
      // 格式: { items: [...] }
      topics = data.items
    } else if (data && typeof data === 'object') {
      // 尝试从对象中提取数组
      const values = Object.values(data)
      if (Array.isArray(values[0])) {
        topics = values[0] as RawHotTopic[]
      }
    }

    console.log('解析出的热门话题:', topics)

    if (topics.length === 0) {
      console.warn('热门话题接口返回空数组或格式不正确')
    }

    return topics.map(mapHotTopic)
  } catch (error) {
    console.error('获取热门话题失败:', error)
    return []
  }
}
