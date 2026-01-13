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
    topicName: string  // 后端返回的是topicName，不是name
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
    title: topic.topicName,  // 使用topicName而不是name
    postCount: topic.postCount,
  })),
})

/** 获取话题详情（包含关注状态） */
export const getTopicDetail = async (topicId: number): Promise<TopicDetail & { isFollowing?: boolean }> => {
  const res = await request.get<RawTopic>(`/topics/${topicId}`)
  const rawData = unwrap(res)
  const detail = mapTopicDetail(rawData, topicId)
  // 添加关注状态
  return {
    ...detail,
    isFollowing: rawData.isFollowing ?? false
  }
}

/**
 * 关注话题
 * @param topicId 话题ID
 */
export const followTopic = async (topicId: number): Promise<{ isFollowing: boolean }> => {
  const res = await request.post<{ isFollowing: boolean }>(`/topics/${topicId}/follow`)
  const data = unwrap(res)
  return data
}

/**
 * 取消关注话题
 * @param topicId 话题ID
 */
export const unfollowTopic = async (topicId: number): Promise<{ isFollowing: boolean }> => {
  const res = await request.delete<{ isFollowing: boolean }>(`/topics/${topicId}/follow`)
  const data = unwrap(res)
  return data
}
