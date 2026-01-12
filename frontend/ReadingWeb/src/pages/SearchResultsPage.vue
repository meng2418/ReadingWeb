<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavBar from '@/components/layout/NavBar.vue'
import BookCardSuperBig from '@/components/category/BookCardSuperBig.vue'
import AuthorCard from '@/components/Search/AuthorCard.vue'
import { search } from '@/api/search'
import type {
  SearchTab,
  SearchResultBook,
  SearchResultAuthor,
  CategorizedResults,
} from '@/types/search'

const route = useRoute()
const router = useRouter()
const searchQuery = ref<string>((route.query.q as string) || '')

// 搜索状态
const loading = ref(false)
const error = ref<string | null>(null)
const searchResults = ref<(SearchResultBook | SearchResultAuthor)[]>([])
const currentTab = ref<SearchTab>('all')

// 执行搜索
const performSearch = async (keyword: string) => {
  if (!keyword || keyword.trim() === '') {
    searchResults.value = []
    return
  }

  loading.value = true
  error.value = null

  try {
    const response = await search(keyword.trim())
    
    // 将后端返回的数据转换为前端需要的格式
    const books: SearchResultBook[] = (response.books || []).map((book) => ({
      id: book.bookId,
      type: 'book' as const,
      title: book.bookTitle,
      author: book.authorName || '未知作者',
      cover: book.cover || '',
      readersCount: book.readCount || 0,
      recommend: book.rating ? `${book.rating.toFixed(1)}%` : '0%',
      description: book.description || '',
    }))

    const authors: SearchResultAuthor[] = (response.authors || []).map((author) => ({
      id: author.authorId,
      type: 'author' as const,
      name: author.authorName || '未知作者',
      avatar: author.avatar || '',
      readersCount: author.followerCount || 0,
      works: author.representativeWorks?.join('、') || '暂无代表作',
      description: author.authorBio || '',
    }))

    searchResults.value = [...books, ...authors]
  } catch (err: any) {
    console.error('搜索失败:', err)
    error.value = err?.message || '搜索失败，请稍后重试'
    searchResults.value = []
  } finally {
    loading.value = false
  }
}

// 监听搜索关键词变化
watch(
  () => route.query.q,
  (q) => {
    const newQuery = (q as string) || ''
    searchQuery.value = newQuery
    if (newQuery) {
      performSearch(newQuery)
    } else {
      searchResults.value = []
    }
  },
  { immediate: true },
)

// 组件挂载时执行搜索
onMounted(() => {
  if (searchQuery.value) {
    performSearch(searchQuery.value)
  }
})

const goToAuthorPage = (id: number) => {
  router.push(`/authordetail/${id}`)
}

// 计算属性：将数据分类，方便模板渲染
const categorizedResults = computed<CategorizedResults>(() => {
  const books = searchResults.value.filter((item): item is SearchResultBook => item.type === 'book')
  const authors = searchResults.value.filter(
    (item): item is SearchResultAuthor => item.type === 'author',
  )
  return { books, authors }
})

const selectTab = (tab: SearchTab) => {
  currentTab.value = tab
}
</script>

<template>
  <div>
    <NavBar />

    <main class="search-page-main">
      <div class="search-header">
        <h1 class="search-query-title">“{{ searchQuery }}” 的搜索结果</h1>
        <nav class="search-tabs">
          <button :class="{ active: currentTab === 'all' }" @click="selectTab('all')">全部</button>
          <button :class="{ active: currentTab === 'books' }" @click="selectTab('books')">
            书籍
          </button>
          <button :class="{ active: currentTab === 'authors' }" @click="selectTab('authors')">
            作者
          </button>
        </nav>
      </div>

      <hr class="thin-divider" />

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <p>搜索中...</p>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="error" class="error-container">
        <p>{{ error }}</p>
      </div>

      <!-- 搜索结果 -->
      <div v-else-if="currentTab === 'all'" class="results-container">
        <section v-if="categorizedResults.authors.length > 0" class="result-section">
          <h3 class="section-title">相关作者</h3>
          <div class="list-group">
            <AuthorCard
              v-for="item in categorizedResults.authors"
              :key="item.id"
              :name="item.name"
              :avatar="item.avatar"
              :readersCount="String(item.readersCount)"
              :works="item.works"
              :description="item.description"
            />
          </div>
        </section>

        <section v-if="categorizedResults.books.length > 0" class="result-section">
          <h3 class="section-title">相关书籍</h3>
          <div class="list-group">
            <BookCardSuperBig
              v-for="item in categorizedResults.books"
              :key="item.id"
              :title="item.title"
              :author="item.author"
              :cover="item.cover"
              :readers-count="item.readersCount"
              :recommendation-rate="Number.parseFloat(item.recommend)"
              :description="item.description"
              class="book-item-spacing"
            />
          </div>
        </section>

        <div
          v-if="categorizedResults.authors.length === 0 && categorizedResults.books.length === 0"
          class="no-results"
        >
          <p>未找到相关结果</p>
        </div>
      </div>

      <div v-else-if="currentTab === 'books'" class="results-container">
        <div class="list-group">
          <BookCardSuperBig
            v-for="item in categorizedResults.books"
            :key="item.id"
            :title="item.title"
            :author="item.author"
            :cover="item.cover"
            :readers-count="item.readersCount"
            :recommendation-rate="Number.parseFloat(item.recommend || '0')"
            :description="item.description"
            class="book-item-spacing"
          />
        </div>
        <div v-if="categorizedResults.books.length === 0" class="no-results">暂无书籍结果</div>
      </div>

      <div v-else-if="currentTab === 'authors'" class="results-container">
        <div class="list-group">
          <AuthorCard
            v-for="item in categorizedResults.authors"
            :key="item.id"
            :name="item.name"
            :avatar="item.avatar"
            :readersCount="String(item.readersCount)"
            :works="item.works"
            :description="item.description"
            @view="goToAuthorPage(item.id)"
          />
        </div>
        <div v-if="categorizedResults.authors.length === 0" class="no-results">暂无作者结果</div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.search-page-main {
  max-width: 1000px;
  margin: 32px auto;
  padding: 40px;
}

/* 顶部 Header */
.search-query-title {
  font-size: 1.4rem;
  font-weight: 400;
  margin-bottom: 24px;
  color: #333;
}

.search-tabs {
  display: flex;
  gap: 32px;
}

.search-tabs button {
  background: none;
  border: none;
  font-size: 1rem;
  color: #999;
  cursor: pointer;
  padding-bottom: 8px;
  font-weight: 300;
}

.search-tabs button.active {
  color: #000;
  font-weight: 500;
  border-bottom: 1px solid #000; /* 极细下划线 */
}

.thin-divider {
  border: none;
  border-bottom: 1px solid #eee;
  margin: 10px 0 40px 0;
}

/* 分区样式 */
.result-section {
  margin-bottom: 60px; /* 区块间大留白 */
}

.section-title {
  font-size: 1rem;
  color: #999;
  font-weight: 400;
  margin-bottom: 20px;
  letter-spacing: 1px; /* 增加字间距，更有设计感 */
}

/* 列表样式 */
.list-group {
  display: flex;
  flex-direction: column;
  gap: 40px; /* 卡片间距 */
}

.book-item-spacing {
  padding-bottom: 40px;
  border-bottom: 1px dashed #f5f5f5;
}
.book-item-spacing:last-child {
  border-bottom: none;
}

.no-results {
  text-align: center;
  color: #ccc;
  margin-top: 100px;
}

.loading-container,
.error-container {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.error-container {
  color: #f56c6c;
}
</style>
