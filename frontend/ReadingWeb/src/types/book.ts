export type BookId = string | number

export interface BookBase {
  id?: BookId
  bookId?: BookId
  title: string
  author?: string
  cover?: string
}

export interface BookContent extends BookBase {
  description?: string
  summary?: string
  intro?: string
}

export interface BookMetrics extends BookContent {
  recommend?: string
  readersCount?: string | number
  recommendationRate?: string | number
}
// 最近阅读封面数据
export interface RecentBook {
  id: number
  title: string
  cover: string
}

// 猜你喜欢书籍类型
export type GuessBook = BookListItem & {
  id: number
  cover: string
  author: string
  reason: string
}

// 榜单书籍类型
export type RankBook = BookListItem & {
  id: number
  cover: string
  author: string
  recommend: string
}
export type BookListItem = BookMetrics

export type SimpleBook = Pick<BookBase, 'title' | 'author'>
