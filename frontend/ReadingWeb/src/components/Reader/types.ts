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
  id: number
  title: string
  page: number
}

export interface Note {
  id: string
  chapterId: string
  quote: string
  note: string
  date: string
}

export interface Comment {
  id: string
  username: string
  avatar: string
  content: string
  likes: number
  date: string
}

export interface Annotation {
  id: string
  chapterId: string
  pIndex: number
  start: number
  end: number
  type: 'marker' | 'wave' | 'line' | 'thought'
  noteId?: string // Optional link to a note
  data?: any
}

export type ReadingMode = 'paged' | 'scroll'
