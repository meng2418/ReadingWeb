import request from '@/utils/request'
import { processCoverPath } from '@/utils/imagePath'

// api/bookshelf.ts

/** 前端统一使用的类型 */
export interface ShelfBook {
  id: number // 这里的 id 对应后端的 bookId
  title: string
  cover: string
  author?: string
  rating?: number
  readCount?: number
  description?: string
  status: '未读' | '在读' | '读完'
}

// 1. 修改后端返回的数据格式定义，加入 bookId
interface BackendBook {
  bookId: number // <--- 关键字段
  bookTitle: string
  cover: string
  author?: string
  rating?: number
  readCount?: number
  description?: string
  readingStatus: 'unread' | 'reading' | 'finished'
}

const statusMap: Record<'unread' | 'reading' | 'finished', ShelfBook['status']> = {
  unread: '未读',
  reading: '在读',
  finished: '读完',
}

export const getBookshelfAll = async (): Promise<ShelfBook[]> => {
  const res = await request.get('/bookshelf')

  // 2. 映射时使用 item.bookId 赋值给 id
  return res.data.data.map((item: BackendBook) => ({
    id: item.bookId, // <--- 使用后端真实 ID，不要用 index + 1
    title: item.bookTitle,
    cover: processCoverPath(item.cover),
    author: item.author,
    rating: item.rating,
    readCount: item.readCount,
    description: item.description,
    status: statusMap[item.readingStatus],
  }))
}

export const getBookshelfByStatus = async (
  status: 'unread' | 'reading' | 'finished',
): Promise<ShelfBook[]> => {
  const res = await request.get(`/bookshelf/${status}`)

  return res.data.data.map((item: BackendBook) => ({
    id: item.bookId, // <--- 同样保持一致
    title: item.bookTitle,
    cover: processCoverPath(item.cover),
    status: statusMap[status],
  }))
}

/**
 * 批量移除书架书籍
 * @param ids 书籍ID数组
 */
export function batchRemoveBooks(ids: (string | number)[]) {
  return request({
    url: '/bookshelf/batch-remove',
    method: 'delete',
    // 这里的 key 必须叫 bookIds，与你提供的文档一致
    data: {
      bookIds: ids,
    },
  })
}
