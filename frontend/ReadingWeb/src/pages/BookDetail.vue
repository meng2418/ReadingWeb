<template>
  <div class="book-detail-page">
    <NavBar />
    <!-- 添加导航栏 -->
    <BackToTop />
    <div class="book-detail-layout">
      <!-- 左侧主要内容区域 -->
      <div class="main-content">
        <!-- 书籍基本信息组件 -->
        <BookDetailHeader
          :title="bookTitle"
          :author="bookDetail?.author || '未知作者'"
          :description="bookDetail?.description || bookDescription"
          :cover-image="bookDetail?.cover || 'https://picsum.photos/200/280?random=25'"
          :initial-bookshelf-status="bookDetail?.isInBookshelf || false"
          :stats="computedBookStats"
          :book-id="bookId"
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
          :book-id="bookId"
          :book-title="bookTitle"
          @view-reviews="handleViewReviews"
          @rate-book="handleRateBook"
        />

        <!-- 用户点评组件 -->
        <UserReviews
          :reviews="convertedReviews"
          :book-id="bookId"
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

// 默认测试数据
const getDefaultBookDetail = (): BookDetail => ({
  id: 'book-123',
  title: '少年Pi的奇幻漂流',
  author: '扬·马特尔',
  authorId: 1,
  cover: 'https://picsum.photos/200/280?random=25',
  description: '《少年Pi的奇幻漂流》是加拿大作家扬·马特尔于2001年发表的虚构小说，描述一名印度男孩Pi在太平洋上与一只孟加拉虎同船而行的冒险故事。这部小说探讨了信仰、生存和人类与自然的关系等深刻主题，获得了2002年的布克奖及亚洲/太平洋美洲文学奖。',
  rating: 90.5,
  readCount: 183000,
  isFinished: false,
  isInBookshelf: false,
  hasStarted: true,
  readingCount: 183000,
  finishedCount: 76000,
  readingStatus: 'reading',
  wordCount: 113000,
  publishDate: '2021-07-01',
  isFreeForMember: true,
  price: 4900,
  ratingDetail: {
    recommendPercent: 70,
    averagePercent: 20,
    notRecommendPercent: 10,
  },
  ratingCount: 1250,
  authorBio: '扬·马特尔（Yann Martel，1963年6月25日－）是一位加拿大作家。他出生于西班牙萨拉曼卡，父母是加拿大人。幼时曾旅居哥斯达黎加、法国、墨西哥、加拿大，成年后做客伊朗、土耳其及印度。毕业于加拿大特伦特大学哲学系，其后从事过各种稀奇古怪的行业，包括植树工、洗碗工、保安等。以《少年Pi的奇幻漂流》获得2002年的布克奖及亚洲/太平洋美洲文学奖。马特尔现在住在萨斯卡通（Saskatoon）。',
})

const getDefaultReviews = (): BookReview[] => [
  {
    avatar: 'https://picsum.photos/40/40?random=1',
    username: '书虫小张',
    rating: 'recommend',
    reviewTime: '2023-10-15T10:30:00Z',
    content: '这本书真的让我爱不释手，故事情节紧凑，人物塑造生动。作者的文字功底深厚，能够将复杂的情感用简单的语言表达出来。特别是主角的成长历程，让我感同身受。'
  },
  {
    avatar: 'https://picsum.photos/40/40?random=2',
    username: '文学爱好者',
    rating: 'average',
    reviewTime: '2023-10-12T14:20:00Z',
    content: '一开始是被封面吸引的，读完后发现内容更加精彩。作者对细节的把握非常到位，每一个场景都描绘得栩栩如生。虽然有些情节略显拖沓，但整体来说是一部值得推荐的作品。'
  },
  {
    avatar: 'https://picsum.photos/40/40?random=3',
    username: '深夜读书人',
    rating: 'recommend',
    reviewTime: '2023-10-08T20:15:00Z',
    content: '这本书让我重新找回了阅读的乐趣。故事情节虽然简单，但蕴含的哲理却很深刻。适合在安静的夜晚慢慢品味，每一章都能带来新的思考。'
  }
]

const getDefaultAuthorWorks = (): AuthorWork[] => [
  {
    cover: 'https://picsum.photos/80/100?random=21',
    bookTitle: '少年Pi的奇幻漂流',
    description: '一名印度男孩与孟加拉虎在太平洋上的生存故事',
    bookId: '1'
  },
  {
    cover: 'https://picsum.photos/80/100?random=22',
    bookTitle: '标本师的魔幻剧本',
    description: '关于大屠杀记忆与文学创作的深刻探讨',
    bookId: '2'
  },
  {
    cover: 'https://picsum.photos/80/100?random=23',
    bookTitle: '赫尔曼',
    description: '关于友谊、艺术与人生选择的温暖故事',
    bookId: '3'
  }
]

const getDefaultRelatedBooks = (): RelatedBook[] => [
  {
    cover: 'https://picsum.photos/80/100?random=1',
    title: '时光旅行者的妻子',
    description: '一段跨越时空的爱情故事，感人至深。',
    bookId: 1
  },
  {
    cover: 'https://picsum.photos/80/100?random=2',
    title: '追风筝的人',
    description: '关于友谊、背叛与救赎的动人故事。',
    bookId: 2
  },
  {
    cover: 'https://picsum.photos/80/100?random=3',
    title: '解忧杂货店',
    description: '穿越时空的信件，连接过去与未来。',
    bookId: 3
  }
]
const route = useRoute()
const userProfileRef = ref() // UserProfile组件引用
const bookDetail = ref<BookDetail | null>(null)
const reviewsData = ref<BookReview[]>([])
const authorWorks = ref<AuthorWork[]>([])
const relatedBooks = ref<RelatedBook[]>([])
const isLoading = ref(true)

// 在组件挂载时滚动到顶部并获取书籍详情
onMounted(async () => {
  window.scrollTo({
    top: 0,
  })

  try {
    // 从路由参数获取bookId
    const currentBookId = bookId.value
    console.log('正在获取书籍详情和点评数据，bookId:', currentBookId)

    // 并行获取书籍详情和点评数据
    const [bookDetailData, reviews, authorWorksData, relatedBooksData] = await Promise.allSettled([
      getBookDetail(currentBookId),
      getBookReviews(currentBookId),
      getAuthorWorks(currentBookId),
      getRelatedBooks(currentBookId)
    ])

    // 处理每个API调用的结果，使用默认数据作为fallback
    bookDetail.value = bookDetailData.status === 'fulfilled' ? bookDetailData.value : getDefaultBookDetail()
    reviewsData.value = reviews.status === 'fulfilled' ? reviews.value : getDefaultReviews()
    authorWorks.value = authorWorksData.status === 'fulfilled' ? authorWorksData.value : getDefaultAuthorWorks()
    relatedBooks.value = relatedBooksData.status === 'fulfilled' ? relatedBooksData.value : getDefaultRelatedBooks()

    console.log('获取到的书籍详情:', bookDetail.value)
    console.log('获取到的点评数据:', reviewsData.value)
    console.log('获取到的作者作品:', authorWorks.value)
    console.log('获取到的相关推荐:', relatedBooks.value)
  } catch (err) {
    console.error('获取数据失败，使用默认数据:', err)
    // 使用默认数据作为fallback
    bookDetail.value = getDefaultBookDetail()
    reviewsData.value = getDefaultReviews()
    authorWorks.value = getDefaultAuthorWorks()
    relatedBooks.value = getDefaultRelatedBooks()
  } finally {
    isLoading.value = false
  }
})

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

// 修改：从API获取书籍详情
const bookId = computed(() => {
  const paramId = route.params.id
  const queryId = route.query.bookId
  return (Array.isArray(paramId) ? paramId[0] : paramId) ||
         (Array.isArray(queryId) ? queryId[0] : queryId) ||
         'book-123'
})
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
    console.log('使用默认评分统计数据')
    return { recommend: 70, average: 20, poor: 10 }
  }

  console.log('API返回的评分详情:', bookDetail.value.ratingDetail)

  // 获取API返回的百分比
  let recommend = Math.round(bookDetail.value.ratingDetail.recommendPercent)
  let average = Math.round(bookDetail.value.ratingDetail.averagePercent)
  let poor = Math.round(bookDetail.value.ratingDetail.notRecommendPercent)

  // 确保三个百分比加起来等于100%
  const total = recommend + average + poor
  if (total !== 100) {
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
// 将BookReview转换为Review格式
const convertedReviews = computed(() => {
  return reviewsData.value.map((review, index) => ({
    id: index + 1,
    bookId: bookId.value.toString(),
    userName: review.username,
    content: review.content,
    date: review.reviewTime,
    rating: review.rating,
    isPublic: true,
    avatar: review.avatar, // 添加avatar字段
  }))
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
  if (!bookDetail.value?.ratingCount) {
    return countPublicReviews(bookId.value, convertedReviews.value.length)
  }
  return countPublicReviews(bookId.value, bookDetail.value.ratingCount)
})

// 监听路由变化，刷新数据
watch(
  () => route.query,
  (newQuery) => {
    if (newQuery.refresh === 'true') {
      // 重新计算数据
      console.log('刷新页面数据')
    }
  },
)

// 作者信息数据 - 从API获取
const computedAuthorInfo = computed(() => ({
  id: bookDetail.value?.authorId || 1,
  name: bookDetail.value?.author || '扬·马特尔',
  description: bookDetail.value?.authorBio ||
    '扬·马特尔（Yann Martel，1963年6月25日－）是一位加拿大作家。他出生于西班牙萨拉曼卡，父母是加拿大人。幼时曾旅居哥斯达黎加、法国、墨西哥、加拿大，成年后做客伊朗、土耳其及印度。毕业于加拿大特伦特大学哲学系，其后从事过各种稀奇古怪的行业，包括植树工、洗碗工、保安等。以《少年Pi的奇幻漂流》获得2002年的布克奖及亚洲/太平洋美洲文学奖。马特尔现在住在萨斯卡通（Saskatoon）。',
}))



// 书籍描述
const bookDescription = `《少年Pi的奇幻漂流》是加拿大作家扬·马特尔于2001年发表的虚构小说，描述一名印度男孩Pi在太平洋上与一只孟加拉虎同船而行的冒险故事。这部小说探讨了信仰、生存和人类与自然的关系等深刻主题，获得了2002年的布克奖及亚洲/太平洋美洲文学奖。`

// 书籍统计信息 - 从API数据计算
const computedBookStats = computed(() => {
  if (!bookDetail.value) {
    return {
      readingCount: '18.3万人',
      readingSubtitle: '7.6万人读完',
      myReadingStatus: '在读',
      myReadingSubtitle: '标记在读',
      wordCount: '11.3万字',
      publishInfo: '2021年7月出版',
      experienceCardStatus: '体验卡可读',
      priceInfo: '电子书价格49元',
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

  // 提供默认值以防API数据无效
  const defaultReadingCount = 183000
  const defaultFinishedCount = 76000
  const defaultWordCount = 113000
  const defaultPrice = 4900

  return {
    readingCount: readingCount > 0 ? `${(readingCount / 10000).toFixed(1)}万人` : `${(defaultReadingCount / 10000).toFixed(1)}万人`,
    readingSubtitle: finishedCount > 0 ? `${(finishedCount / 10000).toFixed(1)}万人读完` : `${(defaultFinishedCount / 10000).toFixed(1)}万人读完`,
    myReadingStatus: getReadingStatusText(readingStatus),
    myReadingSubtitle: getReadingStatusSubtitle(readingStatus),
    wordCount: wordCount > 0 ? `${(wordCount / 10000).toFixed(1)}万字` : `${(defaultWordCount / 10000).toFixed(1)}万字`,
    publishInfo: publishDate ? `${new Date(publishDate).getFullYear()}年${new Date(publishDate).getMonth() + 1}月出版` : '2021年7月出版',
    experienceCardStatus: isFreeForMember !== undefined ? (isFreeForMember ? '体验卡可读' : '需购买') : '体验卡可读',
    priceInfo: price > 0 ? `电子书价格${(price / 100).toFixed(0)}元` : `电子书价格${(defaultPrice / 100).toFixed(0)}元`,
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
  try {
    const currentBookId = bookId.value
    if (isAdded) {
      await addToBookshelf(currentBookId)
      console.log('成功加入书架')
    } else {
      await removeFromBookshelf(currentBookId)
      console.log('成功从书架移除')
    }

    // 重新获取书籍详情以更新状态
    const updatedBookDetail = await getBookDetail(currentBookId)
    if (bookDetail.value) {
      bookDetail.value.isInBookshelf = updatedBookDetail.isInBookshelf
    }
  } catch (error) {
    console.error('书架操作失败:', error)
    // 可以在这里添加错误提示
  }
}

const handleStartReading = async () => {
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

    // 可以在这里添加跳转到阅读器的逻辑
    // router.push(`/reader/${currentBookId}`)
  } catch (error) {
    console.error('开始阅读失败:', error)
    // 可以在这里添加错误提示
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
</style>
