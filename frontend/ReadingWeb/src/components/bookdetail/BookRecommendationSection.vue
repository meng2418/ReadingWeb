<template>
  <div class="book-recommendation-section">
    <!-- 背景框 -->
    <div class="recommendation-box"></div>

    <!-- 上半部分：推荐值和进度条 -->
    <div class="top-section">
      <!-- 左侧推荐值 -->
      <div class="recommendation-value">
        <div class="recommendation-label">推荐值</div>
        <div class="recommendation-percentage">
          <span class="percentage-number">{{ recommendationValue }}</span>
          <span class="percentage-symbol">%</span>
        </div>
        <div class="review-count" @click="handleViewReviews">{{ reviewCount }}人点评 &gt;</div>
      </div>

      <!-- 右侧进度条 -->
      <div class="progress-bars">
        <div class="progress-item">
          <span class="progress-label">推荐</span>
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: ratingPercentages.recommend + '%' }"></div>
          </div>
          <span class="progress-percentage">{{ Math.round(ratingPercentages.recommend) }}%</span>
        </div>
        <div class="progress-item">
          <span class="progress-label">一般</span>
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: ratingPercentages.average + '%' }"></div>
          </div>
          <span class="progress-percentage">{{ Math.round(ratingPercentages.average) }}%</span>
        </div>
        <div class="progress-item">
          <span class="progress-label">不行</span>
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: ratingPercentages.poor + '%' }"></div>
          </div>
          <span class="progress-percentage">{{ Math.round(ratingPercentages.poor) }}%</span>
        </div>
      </div>
    </div>

    <!-- 下半部分：三个按钮横向排列 -->
    <div class="bottom-section">
      <!-- 修改：根据用户评分状态添加active类 -->
      <button
        class="rating-button recommend"
        :class="{ 'rated-active': userHasReviewed && userReview.rating === 'recommend' }"
        @click="handleRate('recommend')"
      >
        {{ userHasReviewed && userReview.rating === 'recommend' ? '已推荐' : '推荐' }}
      </button>
      <button
        class="rating-button average"
        :class="{ 'rated-active': userHasReviewed && userReview.rating === 'average' }"
        @click="handleRate('average')"
      >
        {{ userHasReviewed && userReview.rating === 'average' ? '已评一般' : '一般' }}
      </button>
      <button
        class="rating-button poor"
        :class="{ 'rated-active': userHasReviewed && userReview.rating === 'poor' }"
        @click="handleRate('poor')"
      >
        {{ userHasReviewed && userReview.rating === 'poor' ? '已评不行' : '不行' }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router' // 修改：添加路由相关

// 定义props
interface Props {
  recommendationValue: number
  reviewCount: number
  ratingStats?: {
    recommend: number
    average: number
    poor: number
  }
  bookId?: string // 修改：添加书籍ID参数
  bookTitle?: string // 修改：添加书籍标题参数
}

const props = withDefaults(defineProps<Props>(), {
  recommendationValue: 90.5,
  reviewCount: 100,
  ratingStats: () => ({
    recommend: 70,
    average: 20,
    poor: 10
  }),
  bookId: '',
  bookTitle: ''
})

// 修改：获取路由实例
const route = useRoute()
const router = useRouter()

// 修改：添加响应式数据跟踪用户评分
const userHasReviewed = ref(false)
const userReview = ref<any>(null)

// 定义事件
const emit = defineEmits<{
  viewReviews: []
  rateBook: [rating: string]
}>()

// 计算各评分选项的百分比
const ratingPercentages = computed(() => {
  const total = props.ratingStats.recommend + props.ratingStats.average + props.ratingStats.poor
  if (total === 0) {
    return { recommend: 0, average: 0, poor: 0 }
  }

  return {
    recommend: (props.ratingStats.recommend / total) * 100,
    average: (props.ratingStats.average / total) * 100,
    poor: (props.ratingStats.poor / total) * 100
  }
})

// 修改：检查用户是否已评分
const checkUserReview = () => {
  if (!props.bookId) return

  try {
    // 获取当前用户ID（这里用固定用户，实际项目中应该从登录状态获取）
    const currentUserId = getCurrentUserId()

    // 从localStorage获取用户的所有点评
    const userReviews = JSON.parse(localStorage.getItem('userReviews') || '{}')

    if (userReviews[currentUserId] && userReviews[currentUserId][props.bookId]) {
      userHasReviewed.value = true
      userReview.value = userReviews[currentUserId][props.bookId]
    } else {
      userHasReviewed.value = false
      userReview.value = null
    }
  } catch (error) {
    console.error('检查用户点评失败:', error)
    userHasReviewed.value = false
    userReview.value = null
  }
}

// 获取当前用户ID（简化版，实际项目中应该从登录状态获取）
const getCurrentUserId = () => {
  // 尝试从localStorage获取用户ID
  let userId = localStorage.getItem('currentUserId')

  // 如果没有用户ID，创建一个新的（模拟新用户）
  if (!userId) {
    userId = 'user_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
    localStorage.setItem('currentUserId', userId)
  }

  return userId
}

// 查看点评
const handleViewReviews = () => {
  console.log('查看点评')
  emit('viewReviews')
}

// 评分
const handleRate = (rating: string) => {
  console.log('评分:', rating)
  emit('rateBook', rating)

  // 修改：跳转到写点评页面时传递书籍信息和编辑模式
  const currentUserId = getCurrentUserId()

  router.push({
    path: '/writereview',
    query: {
      bookId: props.bookId,
      bookTitle: props.bookTitle,
      rating: rating,
      editMode: userHasReviewed.value ? 'true' : 'false'
    }
  })
}

// 修改：监听路由变化，检查评分状态
watch(() => route.query, (newQuery) => {
  if (newQuery.refresh === 'true') {
    checkUserReview()
  }
}, { immediate: true })

// 修改：组件挂载时检查评分状态
onMounted(() => {
  checkUserReview()
})
</script>

<style scoped>
/* 样式保持不变，只添加新的类 */
.book-recommendation-section {
  width: 800px;
  height: 220px;
  position: relative;
  margin: 0; /* 移除外部边距 */
  display: flex;
  flex-direction: column;
  box-sizing: border-box; /* 确保padding和border包含在尺寸内 */
}

.recommendation-box {
  position: absolute;
  width: 100%;
  height: 100%;
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  z-index: 1;
  box-sizing: border-box;
}

/* 上半部分 */
.top-section {
  display: flex;
  align-items: center;
  flex: 1;
  padding: 18px 20px 0px 20px; /* 上右下左：上边距15px，下边距5px */
  z-index: 2;
  position: relative;
  box-sizing: border-box;
}

/* 推荐值部分 */
.recommendation-value {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 120px;
  padding-right: 20px;
  box-sizing: border-box;
}

.recommendation-label {
  font-size: 20px;
  color: #666;
  margin-bottom: 8px;
}

.recommendation-percentage {
  display: flex;
  align-items: baseline;
  margin-bottom: 8px;
}

.percentage-number {
  font-size: 40px;
  font-weight: bold;
  color: #000;
  line-height: 1;
}

.percentage-symbol {
  font-size: 20px;
  color: #000;
}

.review-count {
  font-size: 15px;
  color: #666;
  cursor: pointer;
  text-decoration: none;
}

.review-count:hover {
  color: #333;
}

/* 进度条部分 */
.progress-bars {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  box-sizing: border-box;
}

.progress-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-label {
  font-size: 12px;
  color: #666;
  width: 30px;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background-color: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease;
}

.progress-item:nth-child(1) .progress-fill {
  background-color: #606060;
}

.progress-item:nth-child(2) .progress-fill {
  background-color: #808080;
}

.progress-item:nth-child(3) .progress-fill {
  background-color: #A0A0A0;
}

.progress-percentage {
  font-size: 12px;
  color: #666;
  width: 25px;
  text-align: right;
}

/* 下半部分 */
.bottom-section {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 5px 20px 15px 20px; /* 上右下左：上边距5px，下边距15px */
  z-index: 2;
  position: relative;
  box-sizing: border-box;
}

.rating-button {
  flex: 1; /* 让每个按钮平均分配空间 */
  padding: 8px 24px;
  border: 1px solid #d0d0d0;
  border-radius: 8px;
  background: #f8f8f8;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 18px;
  color: #333;
  min-width: 220px;
  text-align: center;
  box-sizing: border-box;
}

.rating-button:hover {
  background: #f0f0f0;
  border-color: #b0b0b0;
}

.rating-button:active {
  background: #e8e8e8;
}

/* 修改：添加已评分的按钮样式（淡蓝色） */
.rating-button.rated-active {
  background: #e6f7ff; /* 淡蓝色背景 */
  border-color: #91d5ff; /* 蓝色边框 */
  color: #1890ff; /* 蓝色文字 */
}

/* 响应式调整 */
@media (max-width: 768px) {
  .book-recommendation-section {
    width: 100%;
    min-width: 400px;
    height: auto;
    min-height: 144px;
  }

  .top-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .recommendation-value {
    width: 100%;
    padding-right: 0;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }

  .bottom-section {
    flex-wrap: wrap;
  }

  .rating-button {
    min-width: 100px;
  }
}
</style>
