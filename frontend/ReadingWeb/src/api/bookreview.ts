import request from '@/utils/request'
import type { ReviewCardItem } from '@/types/review'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? {}

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
