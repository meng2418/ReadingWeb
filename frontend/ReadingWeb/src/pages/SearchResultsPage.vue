<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavBar from '@/components/layout/NavBar.vue'
import BookCardSuperBig from '@/components/category/BookCardSuperBig.vue'
import AuthorCard from '@/components/Search/AuthorCard.vue'
import type {
  SearchTab,
  SearchResultBook,
  SearchResultAuthor,
  CategorizedResults,
} from '@/types/search'

const route = useRoute()
const router = useRouter()
const searchQuery = ref<string>((route.query.q as string) || '')

watch(
  () => route.query.q,
  (q) => {
    searchQuery.value = (q as string) || ''
  },
  { immediate: true },
)

// 模拟后端返回的数据
// 后端通常会返回一个包含 list 的对象，或者我们前端自己把 list 分类
const rawData = [
  // --- 书籍数据 ---
  {
    id: 101,
    type: 'book',
    title: '黄金时代',
    author: '王小波',
    cover: `https://picsum.photos/seed/book1/200/300`,
    readersCount: '25000',
    recommend: '99.5%',
    description: '王小波代表作，讲述了知青陈清扬和王二在特殊年代的荒诞故事。',
  },
  {
    id: 102,
    type: 'book',
    title: '沉默的大多数',
    author: '王小波',
    cover: `https://picsum.photos/seed/book2/200/300`,
    readersCount: '18000',
    recommend: '98.2%',
    description: '王小波杂文集，深刻剖析了中国人的思维方式和文化心理。',
  },
  // --- 作者数据 ---
  {
    id: 201,
    type: 'author',
    name: '王小波',
    avatar: `https://picsum.photos/seed/author1/200/200`,
    readersCount: '15000',
    works: '《黄金时代》《一只特立独行的猪》', // 作者特有字段
    description:
      '中国当代学者、作家。代表作品有《黄金时代》、《白银时代》、《青铜时代》、《黑铁时代》等。',
  },
  // 假设还有一个同名或相关的书
  {
    id: 103,
    type: 'book',
    title: '王小波传',
    author: '房伟',
    cover: `https://picsum.photos/seed/book3/200/300`,
    readersCount: '3000',
    recommend: '91.0%',
    description: '一本详尽的王小波传记，考证了大量一手资料。',
  },
]
const goToAuthorPage = (id: number) => {
  router.push(`/authordetail/${id}`)
}
const searchResults = ref<(SearchResultBook | SearchResultAuthor)[]>(rawData as any)

const currentTab = ref<SearchTab>('all')

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

      <div v-if="currentTab === 'all'" class="results-container">
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
            v-bind="item"
            :recommendation-rate="Number.parseFloat(item.recommend || '0')"
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
</style>
