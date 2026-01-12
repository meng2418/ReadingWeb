// OpenAPI 中的 SimpleBook
export interface SimpleBookRaw {
  cover: string
  bookTitle: string
  authorName?: string  // 后端返回的是authorName
  author?: string  // 兼容旧字段
  rating: number
  readCount: number
  description?: string
  isFinished?: boolean
  isInBookshelf?: boolean
  hasStarted?: boolean
  readingStatus?: 'not_started' | 'reading' | 'finished'
}

// 推荐书籍（/home/recommendations）
export type HomeRecommendations = SimpleBookRaw[]

// 榜单书籍（/home/rankings/{type}）
export type HomeRanking = SimpleBookRaw[]

// 聚合到首页所需的数据结构（前端自用）
export interface HomeData {
  recommendations: HomeRecommendations
  rankings: {
    weekly: HomeRanking
    monthly: HomeRanking
    new: HomeRanking
    masterpiece: HomeRanking
  }
}
