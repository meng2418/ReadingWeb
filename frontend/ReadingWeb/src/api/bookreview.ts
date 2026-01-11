import request from '@/utils/request'
import type { ReviewCardItem } from '@/types/review'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

// 提交书评相关接口
export interface SubmitReviewRequest {
  bookId: number
  rating: string
  content: string
  isPublic?: boolean
}

export interface SimpleBook {
  cover: string
  bookTitle: string
  authorName: string
  authorId: number
  rating: number
  readCount: number
  description: string
}

export interface SubmitReviewResponse {
  review: {
    reviewId: number
    book: SimpleBook
    rating: string
    content: string
    isPublic: boolean
    createdAt: string
  }
}

/**
 * 内部工具：将后端评级映射为前端组件识别的 key
 * 兼容处理：中文、英文、以及后端可能的下划线命名
 */
const mapRating = (apiRating: string): 'recommend' | 'average' | 'bad' => {
  const map: Record<string, 'recommend' | 'average' | 'bad'> = {
    // 推荐
    推荐: 'recommend',
    recommend: 'recommend',

    // 一般
    一般: 'average',
    average: 'average',

    // 不行 (坏评)
    不行: 'bad',
    bad: 'bad',
    not_recommend: 'bad', // 匹配你提到的 not_recommend
    dislike: 'bad', // 额外备选
  }

  return map[apiRating] || 'recommend' // 默认返回推荐
}

export const getUserReviews = async () => {
  const res = await request.get('/user/book-reviews')
  const data = unwrap(res)

  // 在这里进行数据映射
  const formattedReviews: ReviewCardItem[] = (data.reviews || []).map(
    (item: any, index: number) => ({
      id: item.id || `${item.bookTitle}-${index}`, // 如果后端没给ID，生成一个临时ID
      userName: '我',
      bookName: item.bookTitle,
      cover: item.cover,
      rating: mapRating(item.rating),
      date: item.reviewDate,
      likes: item.helpfulCount || 0,
      content: item.reviewContent,
    }),
  )

  return {
    reviews: formattedReviews,
    totalCount: data.reviewCount || 0,
    hasMore: data.hasMore,
    nextCursor: data.nextCursor,
  }
}

/**
 * 将前端评分映射为API要求的枚举值
 */
const mapRatingToApi = (frontendRating: string): string => {
  const ratingMap: Record<string, string> = {
    'recommend': 'recommend',
    'average': 'average',
    'poor': 'not_recommend'
  }
  return ratingMap[frontendRating] || 'recommend'
}

/**
 * 提交书评
 */
export const submitBookReview = async (bookId: string | number, rating: string, content: string, isPublic: boolean = true): Promise<SubmitReviewResponse> => {
  // 转换bookId：如果是字符串"book-123"，提取数字部分；如果是纯数字，直接使用
  let numericBookId: number
  if (typeof bookId === 'string') {
    const match = bookId.match(/book-(\d+)/)
    numericBookId = match ? parseInt(match[1]) : parseInt(bookId) || 1
  } else {
    numericBookId = bookId
  }

  const reviewData: SubmitReviewRequest = {
    bookId: numericBookId,
    rating: mapRatingToApi(rating),
    content,
    isPublic
  }

  console.log('🚀 提交书评 - 请求信息:', {
    originalBookId: bookId,
    convertedBookId: numericBookId,
    originalRating: rating,
    apiRating: mapRatingToApi(rating),
    contentLength: content.length,
    isPublic,
    reviewData,
    fullUrl: `https://m1.apifoxmock.com/m1/7605134-7343879-default/book-reviews`,
    method: 'POST'
  })

  try {
    const res = await request.post<SubmitReviewResponse>(`/book-reviews`, reviewData)
    const result = unwrap(res)

    console.log('✅ 提交书评 - 成功响应:', result)

    return result
  } catch (error) {
    console.error('❌ 提交书评 - 请求失败:', {
      reviewData,
      fullUrl: `https://m1.apifoxmock.com/m1/7605134-7343879-default/book-reviews`,
      error: error.response?.data || error.message,
      status: error.response?.status
    })

    // 重新抛出错误，让调用方处理
    throw error
  }
}

/**
 * 删除书评
 * Path: /book-reviews/{reviewId}
 */
export const deleteUserReview = async (reviewId: number | string) => {
  console.log('🗑️ 删除书评 - 请求信息:', {
    reviewId,
    url: `/book-reviews/${reviewId}`,
    method: 'DELETE'
  })

  try {
    const res = await request({
      url: `/book-reviews/${reviewId}`,
      method: 'delete',
    })

    console.log('✅ 删除书评 - 成功响应:', res)

    return res
  } catch (error) {
    console.error('❌ 删除书评 - 请求失败:', {
      reviewId,
      error: error,
      url: `/book-reviews/${reviewId}`
    })

    throw error
  }
}
