export type SearchTab = 'all' | 'books' | 'authors'

export interface SearchResultBook {
  id: number
  type: 'book'
  title: string
  author: string
  cover: string
  readersCount: string | number
  recommend: string
  description: string
}

export interface SearchResultAuthor {
  id: number
  type: 'author'
  name: string
  avatar: string
  readersCount: string | number
  works: string
  description: string
}

export interface CategorizedResults {
  books: SearchResultBook[]
  authors: SearchResultAuthor[]
}
