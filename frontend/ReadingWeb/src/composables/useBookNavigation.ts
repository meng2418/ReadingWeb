import { useRouter } from 'vue-router'

type BookId = string | number | undefined

const buildQueryHref = (id?: BookId) => (id ? `/bookdetail?id=${id}` : '/bookdetail')
const buildRoutePath = (id?: BookId) => (id ? `/bookdetail/${id}` : '/bookdetail')

/**
 * 统一的书籍详情跳转逻辑
 * 支持新标签、当前页或两者同时（兼容现有交互）
 */
export const useBookNavigation = () => {
  const router = useRouter()

  const openInNewTab = (id?: BookId) => {
    window.open(buildQueryHref(id), '_blank')
  }

  const openInCurrent = (id?: BookId) => router.push(buildRoutePath(id))

  const openBookDetail = (id?: BookId, mode: 'new-tab' | 'current' | 'both' = 'new-tab') => {
    if (mode === 'new-tab') return openInNewTab(id)
    if (mode === 'current') return openInCurrent(id)
    // both
    openInNewTab(id)
    return openInCurrent(id)
  }

  return {
    openBookDetail,
    openInNewTab,
    openInCurrent,
    buildQueryHref,
    buildRoutePath,
  }
}
