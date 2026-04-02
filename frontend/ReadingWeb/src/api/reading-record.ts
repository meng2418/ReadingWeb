/**
 * 阅读记录上报：阅读时长、当前章节等（用于统计与续读）
 */
import request from '@/utils/request'

/**
 * 记录“正在阅读”本书（用于首页最近阅读等）
 */
export const recordReading = async (bookId: string | number): Promise<void> => {
  await request.post('/reader/record', null, { params: { bookId } })
}

export interface AddReadingRecordRequest {
  bookId: number
  bookTitle?: string
  readingTime: number // 本次阅读时长（分钟）
  pageCount?: number
  chapterId?: number
  chapterTitle?: string
}

/**
 * 上报本次阅读记录（时长 + 章节）
 */
export const addReadingRecord = async (
  params: AddReadingRecordRequest,
): Promise<void> => {
  await request.post('/user/reading-record', params)
}
