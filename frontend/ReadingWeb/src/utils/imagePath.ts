/**
 * 图片路径处理工具函数
 * 将后端返回的路径（如"/bookresources/book_covers/1_cover.jpeg"）转换为前端可访问的完整路径
 * 
 * @param coverPath 后端返回的封面路径
 * @returns 前端可访问的完整路径
 */
export function processCoverPath(coverPath: string | null | undefined): string {
  if (!coverPath) {
    return ''
  }

  // 如果已经是完整URL（http/https），直接返回
  if (coverPath.startsWith('http://') || coverPath.startsWith('https://')) {
    return coverPath
  }

  // 如果已经包含 /api 前缀，直接返回
  if (coverPath.startsWith('/api')) {
    return coverPath
  }

  // 如果是以 /bookresources 或 /static 开头，添加 /api 前缀
  if (coverPath.startsWith('/bookresources') || coverPath.startsWith('/static')) {
    return `/api${coverPath}`
  }

  // 如果是相对路径（如"1_cover.jpeg"），拼接为完整路径
  // 注意：后端现在返回的是 /bookresources/book_covers/1_cover.jpeg
  return `/api/bookresources/book_covers/${coverPath}`
}
