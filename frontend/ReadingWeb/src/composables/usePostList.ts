import { ref, computed } from 'vue'
import type { Ref } from 'vue'
import type { Post, PostFilter } from '@/types/post'

export const usePostList = (
  posts: Ref<Post[]>,
  options?: { pageSize?: number; initialFilter?: PostFilter },
) => {
  const pageSize = options?.pageSize ?? 10
  const currentFilter = ref<PostFilter>(options?.initialFilter ?? 'latest')
  const page = ref(1)

  const filterTabs = [
    { value: 'latest', label: '最新' },
    { value: 'hot', label: '热门' },
    { value: 'featured', label: '精华' },
  ]

  const sortedAll = computed(() => {
    const arr = [...posts.value]
    switch (currentFilter.value) {
      case 'latest':
        return arr.sort((a, b) => (b.timestamp ?? 0) - (a.timestamp ?? 0))
      case 'hot':
        return arr.sort((a, b) => b.commentCount - a.commentCount)
      case 'featured':
        return arr.sort((a, b) => b.likeCount - a.likeCount)
      default:
        return arr
    }
  })

  const sortedPosts = computed(() => sortedAll.value.slice(0, page.value * pageSize))

  const hasMore = computed(() => sortedPosts.value.length < sortedAll.value.length)

  const changeFilter = (filter: PostFilter | string) => {
    const next = filter as PostFilter
    if (currentFilter.value !== next) {
      currentFilter.value = next
      page.value = 1
    }
  }

  const loadMore = () => {
    if (hasMore.value) page.value++
  }

  const reset = () => {
    page.value = 1
  }

  return {
    filterTabs,
    currentFilter,
    sortedPosts,
    hasMore,
    changeFilter,
    loadMore,
    page,
    reset,
  }
}
