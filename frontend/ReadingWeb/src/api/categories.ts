import request from '@/utils/request'
import type { SimpleBookRaw } from '@/types/home'
import type { CategoryTab, RankedBook } from '@/types/category'

const unwrap = (res: any) => res?.data?.data ?? res?.data ?? []

const mapCategory = (raw: any, index: number): CategoryTab => ({
  id: raw.categoryId ?? raw.id ?? raw.categoryName ?? index.toString(),
  name: raw.categoryName ?? raw.name ?? `分类${index + 1}`,
})

const mapBook = (raw: SimpleBookRaw, index: number): RankedBook => ({
  id: (raw as any).bookId ?? (raw as any).id ?? `${raw.bookTitle}-${index}`,
  cover: raw.cover,
  title: raw.bookTitle,
  author: raw.author,
  recommend: raw.rating ? `${Number(raw.rating).toFixed(1)} 分` : '-',
  readersCount: raw.readCount ?? '-',
  recommendationRate: Number(raw.rating ?? 0),
  description: raw.description ?? '',
})

export const getCategories = async (): Promise<CategoryTab[]> => {
  const res = await request.get('/categories')
  const data = unwrap(res)
  return Array.isArray(data) ? data.map(mapCategory) : []
}

export const getCategoryBooks = async (
  categoryId: string | number,
  page = 1,
  limit = 20,
): Promise<{
  list: RankedBook[]
  total: number
  page: number
  limit: number
}> => {
  const res = await request.get(`/categories/${categoryId}/books`, { params: { page, limit } })
  const data = unwrap<{
    books?: SimpleBookRaw[]
    total?: number
    page?: number
    limit?: number
  }>(res)
  const books = Array.isArray(data.books) ? data.books : []
  return {
    list: books.map(mapBook),
    total: data.total ?? books.length,
    page: data.page ?? page,
    limit: data.limit ?? limit,
  }
}

