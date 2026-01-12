<template>
  <div class="book-detail-page">
    <NavBar />
    <!-- 添加导航栏 -->
      <BackToTop />
      <div class="book-detail-layout">
        <!-- 错误提示 -->
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
        <!-- 加载中提示 -->
        <div v-if="isLoading" class="loading-message">
          加载中...
        </div>
        <!-- 左侧主要内容区域 -->
        <div class="main-content" v-if="!isLoading && bookDetail">
        <!-- 书籍基本信息组件 -->
        <BookDetailHeader
          :title="bookTitle"
          :author="bookDetail?.author || '未知作者'"
          :description="bookDetail?.description || '暂无描述'"
          :cover-image="bookDetail?.cover || 'https://picsum.photos/200/280?random=25'"
          :initial-bookshelf-status="bookDetail?.isInBookshelf || false"
          :stats="computedBookStats"
          :book-id="bookId || undefined"
          @toggle-bookshelf="handleBookshelfToggle"
          @start-reading="handleStartReading"
          @stat-click="handleStatClick"
          @open-recharge-dialog="handleOpenRechargeDialog"
        />

        <!-- 推荐值组件 -->
        <BookRecommendationSection
          :recommendation-value="computedRatingStats.recommend"
          :review-count="computedReviewCount"
          :rating-stats="computedRatingStats"
          :book-id="bookId || undefined"
          :book-title="bookTitle"
          @view-reviews="handleViewReviews"
          @rate-book="handleRateBook"
        />

        <!-- 用户点评组件 -->
        <UserReviews
          :reviews="convertedReviews"
          :book-id="bookId || undefined"
          @review-like="handleReviewLike"
          @load-more="handleLoadMoreReviews"
        />
      </div>

      <!-- 右侧边栏区域 -->
      <div class="sidebar">
        <!-- 作者信息组件 -->
        <AuthorInfoSection
          :author="computedAuthorInfo"
          :works="convertedAuthorWorks"
          @work-click="handleWorkClick"
          @view-all-works="handleViewAllWorks"
        />

        <!-- 相关推荐作品组件 -->
        <RelatedRecommendations
          :books="convertedRelatedBooks"
          @book-select="handleBookSelect"
          @refresh="handleRefreshRecommendations"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '@/components/layout/NavBar.vue'
import BookDetailHeader from '@/components/bookdetail/BookDetailHeader.vue'
import BookRecommendationSection from '@/components/bookdetail/BookRecommendationSection.vue'
import AuthorInfoSection from '@/components/bookdetail/AuthorInfoSection.vue'
import RelatedRecommendations from '@/components/bookdetail/RelatedRecommendations.vue'
import UserReviews from '@/components/bookdetail/UserReviews.vue'
import BackToTop from '@/components/layout/BackToTop.vue'
import { useTitle } from '@/stores/useTitle'
import { countPublicReviews } from '@/composables/useReviews'
import type { Review } from '@/types/review'
import type { BookListItem } from '@/types/book'
import { getBookDetail, addToBookshelf, removeFromBookshelf, startReading } from '@/api/book-detail/book-detail-header'
import { getBookReviews } from '@/api/book-detail/user-reviews'
import { getAuthorWorks } from '@/api/book-detail/author-info-section'
import { getRelatedBooks } from '@/api/book-detail/related-recommendations'
import type { BookDetail } from '@/api/book-detail/book-detail-header'
import type { BookReview } from '@/api/book-detail/user-reviews'
import type { AuthorWork } from '@/api/book-detail/author-info-section'
import type { RelatedBook } from '@/api/book-detail/related-recommendations'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
const route = useRoute()
const router = useRouter()
const userProfileRef = ref() // UserProfile组件引用
const bookDetail = ref<BookDetail | null>(null)
const reviewsData = ref<BookReview[]>([])
const authorWorks = ref<AuthorWork[]>([])
const relatedBooks = ref<RelatedBook[]>([])
const isLoading = ref(true)
const errorMessage = ref<string | null>(null)

// 修改：从API获取书籍详情
// 支持多种方式获取 bookId：
// 1. 路径参数：/bookdetail/105
// 2. query参数：/bookdetail?id=105 或 /bookdetail?bookId=105
const bookId = computed(() => {
  const paramId = route.params.id
  const queryBookId = route.query.bookId
  const queryId = route.query.id
  const id = (Array.isArray(paramId) ? paramId[0] : paramId) ||
             (Array.isArray(queryBookId) ? queryBookId[0] : queryBookId) ||
             (Array.isArray(queryId) ? queryId[0] : queryId) ||
             null
  const result = id ? String(id) : null
  console.log('获取 bookId:', { paramId, queryBookId, queryId, result, route: route.fullPath })
  return result
})

// 提取获取数据的逻辑为独立函数，以便在路由变化时复用
const fetchBookData = async (currentBookId: string | null) => {
  // 如果没有 bookId，显示错误提示
  if (!currentBookId) {
    isLoading.value = false
    errorMessage.value = '缺少书籍ID，无法加载书籍详情'
    bookDetail.value = null
    reviewsData.value = []
    authorWorks.value = []
    relatedBooks.value = []
    return
  }

  isLoading.value = true
  errorMessage.value = null
  try {
    console.log('正在获取书籍详情和点评数据，bookId:', currentBookId)

    // 并行获取书籍详情和点评数据
    const [bookDetailData, reviews, authorWorksData, relatedBooksData] = await Promise.allSettled([
      getBookDetail(currentBookId),
      getBookReviews(currentBookId),
      getAuthorWorks(currentBookId),
      getRelatedBooks(currentBookId)
    ])

    // 处理每个API调用的结果，如果失败则使用空数据
    if (bookDetailData.status === 'fulfilled') {
      const detail = bookDetailData.value
      console.log('API返回的原始书籍详情数据:', detail)
      // 检查数据是否为空对象
      if (detail && Object.keys(detail).length > 0) {
        bookDetail.value = detail
      } else {
        console.warn('书籍详情数据为空')
        bookDetail.value = null
        errorMessage.value = '获取书籍详情失败：数据为空'
      }
    } else {
      console.error('获取书籍详情失败:', bookDetailData.reason)
      const error = bookDetailData.reason
      // 检查是否是304错误
      if (error?.response?.status === 304) {
        errorMessage.value = '数据未更新（304），请刷新页面重试'
      } else {
        errorMessage.value = `获取书籍详情失败：${error?.message || '未知错误'}`
      }
      bookDetail.value = null
    }

    reviewsData.value = reviews.status === 'fulfilled' ? (reviews.value || []) : []
    authorWorks.value = authorWorksData.status === 'fulfilled' ? (authorWorksData.value || []) : []
    relatedBooks.value = relatedBooksData.status === 'fulfilled' ? (relatedBooksData.value || []) : []

    console.log('获取到的书籍详情:', bookDetail.value)
    console.log('获取到的点评数据:', reviewsData.value)
    console.log('获取到的作者作品:', authorWorks.value)
    console.log('获取到的相关推荐:', relatedBooks.value)
  } catch (err) {
    console.error('获取数据失败:', err)
    errorMessage.value = '获取数据失败，请稍后重试'
    bookDetail.value = null
    reviewsData.value = []
    authorWorks.value = []
    relatedBooks.value = []
  } finally {
    isLoading.value = false
  }
}

// 在组件挂载时滚动到顶部并获取书籍详情
onMounted(async () => {
  window.scrollTo({
    top: 0,
  })
  console.log('BookDetail onMounted, bookId:', bookId.value, 'route:', route.fullPath)
  await fetchBookData(bookId.value)
})

// 监听 bookId 变化，当路由参数改变时重新获取数据
watch(
  () => bookId.value,
  async (newBookId, oldBookId) => {
    // 只有当 bookId 真正改变时才重新获取数据
    if (newBookId !== oldBookId) {
      console.log('bookId 变化:', { oldBookId, newBookId, route: route.fullPath })
      window.scrollTo({
        top: 0,
      })
      await fetchBookData(newBookId)
    }
  },
  { immediate: false }
)

// 同时监听路由变化（包括 query 参数变化）
watch(
  () => route.query,
  async (newQuery, oldQuery) => {
    const newId = newQuery.id || newQuery.bookId
    const oldId = oldQuery?.id || oldQuery?.bookId
    if (newId !== oldId && newId) {
      console.log('路由 query 参数变化:', { oldQuery, newQuery, newId })
      window.scrollTo({
        top: 0,
      })
      await fetchBookData(String(newId))
    }
  },
  { immediate: false }
)

// 处理打开充值弹窗
const handleOpenRechargeDialog = () => {
  // 触发UserProfile组件的充值弹窗
  // 注意：这里需要确保UserProfile组件已经加载并暴露openRechargeDialog方法
  if (userProfileRef.value) {
    // 假设UserProfile组件有一个方法可以打开充值弹窗
    // 你可能需要在UserProfile组件中添加一个公共方法来打开充值弹窗
    // userProfileRef.value.openRechargeDialog?.(userPayCoin.value)
    console.log('打开充值弹窗')
  }
}
const bookTitle = computed(() => bookDetail.value?.title || '加载中...')
// 动态页面标题
const title = computed(() =>
  bookTitle.value && bookTitle.value !== '加载中...' ? `${bookTitle.value} - 书籍详情` : '书籍详情'
)
useTitle(title)
// 定义相关类型
interface Work {
  id: number
  title: string
  summary: string
  cover?: string
}

// 修改：计算推荐值统计数据 - 直接使用API返回的百分比
const computedRatingStats = computed(() => {
  if (!bookDetail.value?.ratingDetail) {
    return { recommend: 0, average: 0, poor: 0 }
  }

  console.log('API返回的评分详情:', bookDetail.value.ratingDetail)

  // 获取API返回的百分比
  let recommend = Math.round(bookDetail.value.ratingDetail.recommendPercent)
  let average = Math.round(bookDetail.value.ratingDetail.averagePercent)
  let poor = Math.round(bookDetail.value.ratingDetail.notRecommendPercent)

  // 确保三个百分比加起来等于100%
  const total = recommend + average + poor
  if (total !== 100 && total > 0) {
    // 如果不等于100%，按比例调整
    const factor = 100 / total
    recommend = Math.round(recommend * factor)
    average = Math.round(average * factor)
    poor = Math.round(poor * factor)

    // 处理四舍五入误差，确保加起来正好是100%
    const adjustedTotal = recommend + average + poor
    if (adjustedTotal !== 100) {
      // 将差值加到推荐上
      recommend += (100 - adjustedTotal)
    }
  }

  return {
    recommend,
    average,
    poor,
  }
})

// 修改：计算总点评数
// 将BookReview转换为Review格式，并进行去重（同一用户对同一本书只保留最新的一条）
const convertedReviews = computed(() => {
  // 先转换为Review格式
  const reviews = reviewsData.value.map((review, index) => ({
    id: review.reviewId || index + 1,
    reviewId: review.reviewId, // 确保reviewId被正确传递
    bookId: bookId.value?.toString() || '',
    userId: review.userId?.toString(), // 确保userId是字符串格式
    userName: review.username || '用户',
    content: review.content,
    date: review.reviewTime,
    rating: review.rating,
    isPublic: true,
    avatar: review.avatar, // 添加avatar字段
  }))
  
  // 去重：对于同一用户和同一本书，只保留reviewId最大的（最新的）
  const reviewMap = new Map<string, typeof reviews[0]>()
  reviews.forEach(review => {
    const key = `user_${review.userId}_book_${review.bookId}`
    const existing = reviewMap.get(key)
    if (!existing) {
      reviewMap.set(key, review)
    } else {
      // 如果已存在，比较reviewId，保留更大的（更新的）
      const existingReviewId = existing.reviewId ? Number(existing.reviewId) : 0
      const currentReviewId = review.reviewId ? Number(review.reviewId) : 0
      if (currentReviewId > existingReviewId) {
        reviewMap.set(key, review)
      }
    }
  })
  
  return Array.from(reviewMap.values())
})

// 将AuthorWork转换为组件期望的格式
const convertedAuthorWorks = computed(() => {
  return authorWorks.value.map((work, index) => ({
    id: work.bookId ? parseInt(work.bookId.toString()) : (index + 1),
    title: work.bookTitle,
    summary: work.description,
    cover: work.cover,
  }))
})

// 将RelatedBook转换为BookListItem格式
const convertedRelatedBooks = computed(() => {
  return relatedBooks.value.map((book) => ({
    id: book.bookId,
    title: book.title,
    cover: book.cover,
    intro: book.description, // API返回的是description，组件期望的是intro
  }))
})

const computedReviewCount = computed(() => {
  if (!bookId.value) return 0
  // 直接使用API返回的ratingCount，不叠加本地存储的数据
  // 因为API返回的数据已经包含了所有公开的书评
  return bookDetail.value?.ratingCount || convertedReviews.value.length
})

// 监听路由变化，刷新数据（保留原有逻辑）
watch(
  () => route.query,
  async (newQuery, oldQuery) => {
    if (newQuery.refresh === 'true') {
      // 重新获取数据，确保统计数据是最新的
      console.log('刷新页面数据')
      await fetchBookData(bookId.value)
      // 清除refresh参数，避免重复刷新
      if (oldQuery?.refresh !== 'true') {
        router.replace({
          path: route.path,
          query: {
            ...Object.fromEntries(
              Object.entries(route.query).filter(([key]) => key !== 'refresh' && key !== 'timestamp')
            )
          }
        })
      }
    }
  },
)

// 作者信息数据 - 从API获取
const computedAuthorInfo = computed(() => ({
  id: bookDetail.value?.authorId || 0,
  name: bookDetail.value?.author || '未知作者',
  description: bookDetail.value?.authorBio || '暂无作者简介',
}))

// 书籍统计信息 - 从API数据计算
const computedBookStats = computed(() => {
  if (!bookDetail.value) {
    return {
      readingCount: '0人',
      readingSubtitle: '0人读完',
      myReadingStatus: '未读',
      myReadingSubtitle: '标记未读',
      wordCount: '0字',
      publishInfo: '未知',
      experienceCardStatus: '未知',
      priceInfo: '未知',
    }
  }

  const { readingCount, finishedCount, readingStatus, wordCount, publishDate, isFreeForMember, price } = bookDetail.value

  console.log('API返回的统计数据:', { readingCount, finishedCount, wordCount, readingStatus, publishDate, isFreeForMember, price })

  // 格式化阅读状态
  const getReadingStatusText = (status: string) => {
    switch (status) {
      case 'reading': return '在读'
      case 'finished': return '读完'
      default: return '未读'
    }
  }

  const getReadingStatusSubtitle = (status: string) => {
    switch (status) {
      case 'reading': return '标记在读'
      case 'finished': return '标记读完'
      default: return '标记未读'
    }
  }

  return {
    readingCount: readingCount > 0 ? `${(readingCount / 10000).toFixed(1)}万人` : '0人',
    readingSubtitle: finishedCount > 0 ? `${(finishedCount / 10000).toFixed(1)}万人读完` : '0人读完',
    myReadingStatus: getReadingStatusText(readingStatus),
    myReadingSubtitle: getReadingStatusSubtitle(readingStatus),
    wordCount: wordCount > 0 ? `${(wordCount / 10000).toFixed(1)}万字` : '0字',
    publishInfo: publishDate ? `${new Date(publishDate).getFullYear()}年${new Date(publishDate).getMonth() + 1}月出版` : '未知',
    experienceCardStatus: isFreeForMember !== undefined ? (isFreeForMember ? '体验卡可读' : '需购买') : '未知',
    priceInfo: price > 0 ? `电子书价格${(price / 100).toFixed(0)}元` : '未知',
  }
})


// 事件处理函数
const handleWorkClick = (work: Work) => {
  console.log('点击作品:', work.title)
}

const handleViewAllWorks = () => {
  console.log('查看全部作品')
}

const handleBookshelfToggle = async (isAdded: boolean) => {
  if (!bookId.value) {
    console.error('缺少书籍ID，无法操作书架')
    ElMessage.error('缺少书籍ID，无法操作书架')
    return
  }
  try {
    const currentBookId = bookId.value
    if (isAdded) {
      await addToBookshelf(currentBookId)
      ElMessage.success('成功加入书架')
    } else {
      await removeFromBookshelf(currentBookId)
      ElMessage.success('成功从书架移除')
    }

    // 重新获取书籍详情以更新状态
    const updatedBookDetail = await getBookDetail(currentBookId)
    if (bookDetail.value) {
      bookDetail.value.isInBookshelf = updatedBookDetail.isInBookshelf
    }
  } catch (error: any) {
    console.error('书架操作失败:', error)
    const errorMessage = error?.response?.data?.message || error?.message || '操作失败，请稍后重试'
    ElMessage.error(errorMessage)
  }
}

const handleStartReading = async () => {
  if (!bookId.value) {
    console.error('缺少书籍ID，无法开始阅读')
    return
  }
  try {
    const currentBookId = bookId.value
    const result = await startReading(currentBookId)
    console.log('开始阅读成功，新的阅读状态:', result.readingStatus)

    // 重新获取书籍详情以更新状态
    const updatedBookDetail = await getBookDetail(currentBookId)
    if (bookDetail.value) {
      bookDetail.value.readingStatus = updatedBookDetail.readingStatus
      bookDetail.value.hasStarted = updatedBookDetail.hasStarted
    }

    // 跳转到阅读器页面
    router.push(`/reader/${currentBookId}`)
  } catch (error: any) {
    console.error('开始阅读失败:', error)
    // 如果是401错误，可能是token过期，但已经在request拦截器中处理了
    // 这里只处理其他错误
    if (error?.response?.status !== 401) {
      // 可以在这里添加错误提示
      console.error('开始阅读失败，错误信息:', error.message)
    }
  }
}

const handleStatClick = (statType: string) => {
  console.log(`统计卡片点击: ${statType}`)
}

const handleViewReviews = () => {
  console.log('查看点评事件触发')
}

const handleRateBook = (rating: string) => {
  console.log('评分事件触发:', rating)
}


// 相关推荐作品组件事件
const handleBookSelect = (book: BookListItem) => {
  console.log('选择了书籍:', book.title)
}

const handleRefreshRecommendations = () => {
  console.log('刷新了推荐列表')
}

// 用户点评组件事件
const handleReviewLike = (review: Review) => {
  console.log('点赞了评论:', review)
}

const handleLoadMoreReviews = () => {
  console.log('加载更多评论')
}
</script>

<style scoped>
.book-detail-page {
  /* 确保页面内容在导航栏下方 */
  padding-top: 64px; /* 根据导航栏高度调整 */
  background-color: #f8f9fa;
}

.book-detail-layout {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  display: flex;
  gap: 30px;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  max-width: 800px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.sidebar {
  width: 350px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 移除测试用的标题和边框样式 */
:deep(.test-section) {
  display: none;
}

/* 确保组件样式与原型一致 */
:deep(.book-detail-header) {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin-bottom: 0;
  /* 调试样式 - 确保组件有适当的高度 */
  min-height: 400px;
}

:deep(.book-recommendation-section) {
  margin-bottom: 0;
}

:deep(.user-reviews) {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin-bottom: 0;
}

:deep(.author-info-section) {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin-bottom: 0;
}

:deep(.related-recommendations) {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin-bottom: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .book-detail-layout {
    flex-direction: column;
    padding: 15px;
    gap: 20px;
  }

  .sidebar {
    width: 100%;
  }

  .main-content {
    max-width: 100%;
  }
}

@media (max-width: 480px) {
  .book-detail-layout {
    padding: 10px;
    gap: 16px;
  }
}

.error-message {
  background-color: #fee;
  color: #c33;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  text-align: center;
  border: 1px solid #fcc;
}

.loading-message {
  text-align: center;
  padding: 40px;
  color: #666;
  font-size: 16px;
}
</style>
