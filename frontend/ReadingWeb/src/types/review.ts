export type RatingValue = 'recommend' | 'average' | 'poor' | string

// 基础点评模型（与存储层结构一致）
export interface Review {
  id: number | string
  bookId?: string
  bookTitle?: string
  userId?: string
  userName: string
  content: string
  date: string
  rating?: RatingValue
  isPublic?: boolean
  lastEditDate?: string
  avatar?: string // 添加头像字段
}

// 评分统计数据
export interface RatingStats {
  recommend: number
  average: number
  poor: number
}

// 卡片/列表场景使用的扩展点评模型
export interface ReviewCardItem extends Review {
  bookName: string
  cover: string
  likes?: number
  rating: RatingValue
}

// 评分展示配置
export type RatingConfig = Record<
  string,
  {
    label: string
    className: string
  }
>
