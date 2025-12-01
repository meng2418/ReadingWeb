export interface BookPage {
  content: string[]
  chapter: string
}

export interface ReaderState {
  currentPage: number
  totalPages: number
  fontSize: number
  isDarkMode: boolean
}

export interface TypographySettings {
  fontSize: number
  lineHeight: number
}

export interface Chapter {
  id: string
  title: string
  page: number
}

export type ReadingMode = 'paged' | 'scroll'
