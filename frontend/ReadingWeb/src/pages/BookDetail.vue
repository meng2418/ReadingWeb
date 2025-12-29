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
          @view-reviews="handleViewReviews"
        />

        <!-- 用户点评组件 -->
        <UserReviews
          :reviews="reviewsData"
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
          :works="authorWorks"
          @work-click="handleWorkClick"
          @view-all-works="handleViewAllWorks"
        />

        <!-- 相关推荐作品组件 -->
        <RelatedRecommendations
          :books="relatedBooks"
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
import { getBookDetail } from '@/api/books'
import type { BookDetail } from '@/api/books'
const route = useRoute()
const userProfileRef = ref() // UserProfile组件引用
const bookDetail = ref<BookDetail | null>(null)
const isLoading = ref(true)
const error = ref<string | null>(null)

// 在组件挂载时滚动到顶部并获取书籍详情
onMounted(async () => {
  window.scrollTo({
    top: 0,
  })

  try {
    // 从路由参数获取bookId
    const currentBookId = bookId.value
    console.log('正在获取书籍详情，bookId:', currentBookId)
    bookDetail.value = await getBookDetail(currentBookId)
    console.log('获取到的书籍详情:', bookDetail.value)
    console.log('作者信息:', bookDetail.value?.author)
    console.log('标题信息:', bookDetail.value?.title)
  } catch (err) {
    console.error('获取书籍详情失败:', err)
    error.value = '获取书籍详情失败'
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
const title = ref(`${bookTitle.value} - 书籍详情`)
useTitle(title)
// 定义相关类型
interface Work {
  id: number
  title: string
  summary: string
  cover?: string
}

type RelatedBook = Required<Pick<BookListItem, 'id' | 'title' | 'cover'>> & {
  intro: string
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
const computedReviewCount = computed(() => {
  if (!bookDetail.value?.ratingCount) {
    return countPublicReviews(bookId.value, reviewsData.value.length)
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
  name: bookDetail.value?.author || '扬·马特尔',
  description: bookDetail.value?.authorBio ||
    '扬·马特尔（Yann Martel，1963年6月25日－）是一位加拿大作家。他出生于西班牙萨拉曼卡，父母是加拿大人。幼时曾旅居哥斯达黎加、法国、墨西哥、加拿大，成年后做客伊朗、土耳其及印度。毕业于加拿大特伦特大学哲学系，其后从事过各种稀奇古怪的行业，包括植树工、洗碗工、保安等。以《少年Pi的奇幻漂流》获得2002年的布克奖及亚洲/太平洋美洲文学奖。马特尔现在住在萨斯卡通（Saskatoon）。',
}))

const authorWorks = [
  {
    id: 1,
    title: '少年Pi的奇幻漂流',
    summary: '一名印度男孩与孟加拉虎在太平洋上的生存故事',
    cover: 'https://picsum.photos/80/100?random=21',
  },
  {
    id: 2,
    title: '标本师的魔幻剧本',
    summary: '关于大屠杀记忆与文学创作的深刻探讨',
    cover: 'https://picsum.photos/80/100?random=22',
  },
  {
    id: 3,
    title: '赫尔曼',
    summary: '关于友谊、艺术与人生选择的温暖故事',
    cover: 'https://picsum.photos/80/100?random=23',
  },
  {
    id: 4,
    title: '自我',
    summary: '探讨身份认同与存在意义的哲学小说',
    cover: 'https://picsum.photos/80/100?random=24',
  },
]

// 相关推荐作品数据
const relatedBooks = ref<RelatedBook[]>([
  {
    id: 1,
    title: '时光旅行者的妻子',
    intro: '一段跨越时空的爱情故事，感人至深。',
    cover: 'https://picsum.photos/80/100?random=1',
  },
  {
    id: 2,
    title: '追风筝的人',
    intro: '关于友谊、背叛与救赎的动人故事。',
    cover: 'https://picsum.photos/80/100?random=2',
  },
  {
    id: 3,
    title: '解忧杂货店',
    intro: '穿越时空的信件，连接过去与未来。',
    cover: 'https://picsum.photos/80/100?random=3',
  },
  {
    id: 4,
    title: '挪威的森林',
    intro: '青春、爱情与死亡的经典之作。',
    cover: 'https://picsum.photos/80/100?random=4',
  },
  {
    id: 5,
    title: '百年孤独',
    intro: '魔幻现实主义文学的代表作品。',
    cover: 'https://picsum.photos/80/100?random=5',
  },
  {
    id: 6,
    title: '小王子',
    intro: '写给成年人的童话，寓意深远。',
    cover: 'https://picsum.photos/80/100?random=6',
  },
  {
    id: 7,
    title: '活着',
    intro: '讲述一个人与命运抗争的感人故事。',
    cover: 'https://picsum.photos/80/100?random=7',
  },
  {
    id: 8,
    title: '围城',
    intro: '现代中国文学中的经典讽刺小说。',
    cover: 'https://picsum.photos/80/100?random=8',
  },
  {
    id: 9,
    title: '三体',
    intro: '科幻小说的巅峰之作，探索宇宙与人性。',
    cover: 'https://picsum.photos/80/100?random=9',
  },
  {
    id: 10,
    title: '银河帝国',
    intro: '阿西莫夫经典科幻巨著，描绘宏大的银河帝国兴衰。',
    cover: 'https://picsum.photos/80/100?random=10',
  },
  {
    id: 11,
    title: '沙丘',
    intro: '科幻史上的里程碑，讲述沙漠星球的政治与宗教斗争。',
    cover: 'https://picsum.photos/80/100?random=11',
  },
  {
    id: 12,
    title: '神经漫游者',
    intro: '赛博朋克开山之作，描绘虚拟现实与人工智能的未来。',
    cover: 'https://picsum.photos/80/100?random=12',
  },
  {
    id: 13,
    title: '白夜行',
    intro: '东野圭吾代表作，一段跨越十九年的悬疑爱情故事。',
    cover: 'https://picsum.photos/80/100?random=13',
  },
  {
    id: 14,
    title: '达芬奇密码',
    intro: '宗教符号学与悬疑推理的完美结合。',
    cover: 'https://picsum.photos/80/100?random=14',
  },
  {
    id: 15,
    title: '福尔摩斯探案集',
    intro: '侦探小说的经典之作，展现逻辑推理的魅力。',
    cover: 'https://picsum.photos/80/100?random=15',
  },
  {
    id: 16,
    title: '明朝那些事儿',
    intro: '用现代语言讲述明朝历史的通俗读物。',
    cover: 'https://picsum.photos/80/100?random=16',
  },
])

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

// 用户点评数据（模拟数据）
const reviewsData = ref<Review[]>([
  {
    id: 1,
    userName: '书虫小张',
    content:
      '这本书真的让我爱不释手，故事情节紧凑，人物塑造生动。作者的文字功底深厚，能够将复杂的情感用简单的语言表达出来。特别是主角的成长历程，让我感同身受。',
    date: '2023-10-15',
    rating: 'recommend',
  },
  {
    id: 2,
    userName: '文学爱好者',
    content:
      '一开始是被封面吸引的，读完后发现内容更加精彩。作者对细节的把握非常到位，每一个场景都描绘得栩栩如生。虽然有些情节略显拖沓，但整体来说是一部值得推荐的作品。',
    date: '2023-10-12',
    rating: 'average',
  },
  {
    id: 3,
    userName: '深夜读书人',
    content:
      '这本书让我重新找回了阅读的乐趣。故事情节虽然简单，但蕴含的哲理却很深刻。适合在安静的夜晚慢慢品味，每一章都能带来新的思考。',
    date: '2023-10-08',
    rating: 'recommend',
  },
  {
    id: 4,
    userName: '书海漫游',
    content:
      '非常喜欢这本书的叙事风格，作者用独特的视角讲述了一个平凡而又不平凡的故事。读完之后久久不能平静，强烈推荐给喜欢文学的朋友。',
    date: '2023-10-05',
    rating: 'recommend',
  },
  {
    id: 5,
    userName: '阅读时光',
    content:
      '这本书的人物关系处理得非常巧妙，每个角色都有自己鲜明的个性。故事情节跌宕起伏，让人一旦开始阅读就停不下来。',
    date: '2023-10-01',
    rating: 'recommend',
  },
  {
    id: 6,
    userName: '文学探索者',
    content:
      '作者的文笔细腻，能够精准地捕捉人物内心的微妙变化。虽然故事背景设定在特定的时代，但其中蕴含的情感却是普世的，容易引起共鸣。',
    date: '2023-09-28',
    rating: 'average',
  },
  {
    id: 7,
    userName: '科幻迷小王',
    content:
      '作为科幻爱好者，我认为这本书在科学设定上非常严谨，同时又没有忽视人文关怀。作者成功地在硬核科幻与情感表达之间找到了平衡点。',
    date: '2023-09-25',
    rating: 'recommend',
  },
  {
    id: 8,
    userName: '历史研究者',
    content:
      '从历史研究的角度来看，这本书对时代背景的还原相当准确。作者显然做了大量的资料收集工作，让读者能够身临其境地感受那个时代的气息。',
    date: '2023-09-22',
    rating: 'recommend',
  },
  {
    id: 9,
    userName: '心理学学生',
    content:
      '书中对人物心理的描写十分到位，特别是主角在面对困境时的心理变化过程，真实而细腻。这对我学习心理学很有启发。',
    date: '2023-09-18',
    rating: 'average',
  },
  {
    id: 10,
    userName: '旅行作家',
    content:
      '阅读这本书就像进行了一场心灵旅行。作者笔下的场景描写让我仿佛亲眼看到了那些风景，文字中充满了画面感。',
    date: '2023-09-15',
    rating: 'recommend',
  },
])

// 事件处理函数
const handleWorkClick = (work: Work) => {
  console.log('点击作品:', work.title)
}

const handleViewAllWorks = () => {
  console.log('查看全部作品')
}

const handleBookshelfToggle = (isAdded: boolean) => {
  console.log(`书架状态: ${isAdded ? '已加入' : '已移除'}`)
}

const handleStartReading = () => {
  console.log('开始阅读事件触发')
}

const handleStatClick = (statType: string) => {
  console.log(`统计卡片点击: ${statType}`)
}

const handleViewReviews = () => {
  console.log('查看点评事件触发')
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
