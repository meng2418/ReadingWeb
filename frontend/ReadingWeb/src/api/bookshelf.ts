import request from '@/utils/request'

/** 前端统一使用的类型 */
export interface ShelfBook {
  id: number
  title: string
  cover: string
  author?: string
  rating?: number
  readCount?: number
  description?: string
  status: '未读' | '在读' | '读完'
}
const statusMap: Record<'unread' | 'reading' | 'finished', ShelfBook['status']> = {
  unread: '未读',
  reading: '在读',
  finished: '读完',
}
export const getBookshelfAll = async (): Promise<ShelfBook[]> => {
  const res = await request.get('/bookshelf')

  return res.data.data.map((item: any, index: number) => ({
    id: index + 1,
    title: item.bookTitle, // 字段适配
    cover: item.cover,
    author: item.author,
    rating: item.rating,
    readCount: item.readCount,
    description: item.description,
    status: statusMap[item.readingStatus], // 核心：用 status
  }))
}
export const getBookshelfByStatus = async (
  status: 'unread' | 'reading' | 'finished',
): Promise<ShelfBook[]> => {
  const res = await request.get(`/bookshelf/${status}`)

  const statusMap: Record<typeof status, ShelfBook['status']> = {
    unread: '未读',
    reading: '在读',
    finished: '读完',
  }

  return res.data.data.map((item: any, index: number) => ({
    id: index + 1,
    title: item.title,
    cover: item.cover,
    status: statusMap[status],
  }))
}
