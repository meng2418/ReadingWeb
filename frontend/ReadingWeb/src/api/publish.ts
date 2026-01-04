import request from '@/utils/request'

export function searchTopics(params: {
  keyword: string
  cursor?: number | string
  limit?: number
}) {
  return request.get('/posts/search/topics', { params })
}

export function searchBooks(params: { keyword: string; cursor?: number | string; limit?: number }) {
  return request.get('/posts/search/books', { params })
}

export interface PublishPostPayload {
  title: string
  content: string
  bookIds?: number[]
  topics?: string[]
}

/** 发布帖子 */
export const publishPost = (data: PublishPostPayload) => {
  return request.post('/posts', data)
}
