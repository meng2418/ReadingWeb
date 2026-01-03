// src/api/topics/topic-detail-header.ts - 话题详情页头部相关API
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

/** 获取话题详情 */
export const getTopicDetail = async (topicId: string): Promise<TopicDetail> => {
  const res = await request.get<RawTopic>(`/topics/${topicId}`)
  const rawData = unwrap(res)
  return mapTopicDetail(rawData, topicId)
}
