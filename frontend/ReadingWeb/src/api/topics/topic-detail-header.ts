// src/api/topics/topic-detail-header.ts - 话题详情页头部相关API
import request from '@/utils/request'
import type { AxiosResponse } from 'axios'
import type { Post } from '@/types/post'

const unwrap = (res: AxiosResponse): RawTopic => res?.data?.data ?? res?.data ?? {}

/** 后端话题详情结构 */
export interface RawTopic {
  image: string
  topicName: string
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
    topicId: number
  }>
}

/** 前端话题详情结构 */
export interface TopicDetail {
  id: number
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
    id: number
    cover: string
    title: string
    postCount: number
  }>
}

/** 数据转换函数 */
const mapTopicDetail = (raw: RawTopic, topicId: number): TopicDetail => ({
  id: topicId,
  title: raw.topicName,
  name: raw.topicName,
  description: raw.description,
  fullDescription: raw.introduction,
  cover: raw.image,
  postCount: raw.postCount,
  followerCount: raw.followerCount,
  dailyActive: raw.todayPostCount,
  createTime: raw.createdAt,
  manager: raw.adminName,
  relatedTopics: raw.relatedTopics.map((topic) => ({
    id: topic.topicId,
    cover: topic.image,
    title: topic.name,
    postCount: topic.postCount,
  })),
})

/** 获取话题详情 */
export const getTopicDetail = async (topicId: number): Promise<TopicDetail> => {
  const res = await request.get<RawTopic>(`/topics/${topicId}`)
  const rawData = unwrap(res)
  return mapTopicDetail(rawData, topicId)
}
