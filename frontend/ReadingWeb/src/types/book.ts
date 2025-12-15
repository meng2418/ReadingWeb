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

export type BookListItem = BookMetrics

export type SimpleBook = Pick<BookBase, 'title' | 'author'>
