<!-- components/reader/BookEndActions.vue -->
<template>
  <div class="book-end-actions" :class="{ 'dark-mode': isDarkMode }">
    <!-- 全文完标题 -->
    <div class="end-title">
      <span class="end-title-text">全文完</span>
    </div>

    <!-- 标记读完按钮 -->
    <div class="mark-complete-section">
      <button
        class="mark-complete-btn"
        :class="{ 'completed': isCompleted }"
        @click="handleMarkComplete"
      >
        <span class="btn-content">
          <svg v-if="!isCompleted" class="icon-check" width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
          </svg>
          <svg v-else class="icon-check-circle" width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
          </svg>
          {{ isCompleted ? '已读' : '标记读完' }}
        </span>
      </button>

      <div v-if="completeTime" class="complete-time">
        完成于 {{ formatCompleteTime(completeTime) }}
      </div>
    </div>

    <!-- 推荐值组件 - 添加包装容器 -->
    <div class="recommendation-wrapper">
      <BookRecommendationSection
        :recommendation-value="recommendationValue"
        :review-count="reviewCount"
        :rating-stats="ratingStats"
        :book-id="String(bookId)"
        :book-title="bookTitle"
        :is-dark-mode="isDarkMode"
        @viewReviews="$emit('viewReviews')"
        @rateBook="$emit('rateBook', $event)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import BookRecommendationSection from '@/components/bookdetail/BookRecommendationSection.vue'

// 定义props
interface Props {
  bookId: string | number
  bookTitle: string
  recommendationValue: number
  reviewCount: number
  ratingStats: {
    recommend: number
    average: number
    poor: number
  }
  initialCompleted: boolean
  initialCompleteTime: string | null
  isDarkMode: boolean  // 添加深色模式props
}

const props = withDefaults(defineProps<Props>(), {
  recommendationValue: 90.5,
  reviewCount: 100,
  ratingStats: () => ({
    recommend: 70,
    average: 20,
    poor: 10
  }),
  initialCompleted: false,
  initialCompleteTime: null,
  isDarkMode: false  // 默认是浅色模式
})

// 定义emit
const emit = defineEmits<{
  markComplete: [data: { bookId: string | number, completeTime: string | null }]
  viewReviews: []
  rateBook: [rating: string]
}>()

// 本地状态
const isCompleted = ref(props.initialCompleted)
const completeTime = ref(props.initialCompleteTime)

// 监听props变化
watch(() => props.initialCompleted, (newVal) => {
  isCompleted.value = newVal
})

watch(() => props.initialCompleteTime, (newVal) => {
  completeTime.value = newVal
})

// 标记完成
const handleMarkComplete = () => {
  const confirmed = window.confirm(
    isCompleted.value
      ? '确定要取消标记已读吗？'
      : '确定要标记为已读吗？'
  )

  if (!confirmed) return

  if (isCompleted.value) {
    // 取消标记已读
    isCompleted.value = false
    completeTime.value = null
    emit('markComplete', {
      bookId: props.bookId,
      completeTime: null
    })
  } else {
    // 标记为已读
    isCompleted.value = true
    completeTime.value = new Date().toISOString()
    emit('markComplete', {
      bookId: props.bookId,
      completeTime: completeTime.value
    })
  }
}

// 格式化完成时间
const formatCompleteTime = (time: string) => {
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.book-end-actions {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 0 60px;
  box-sizing: border-box;
}

/* 全文完标题 */
.end-title {
  text-align: center;
  margin-bottom: 60px;
  padding: 20px 0;
}

.end-title-text {
  font-size: 3rem;
  font-weight: 300;
  color: #666;
  letter-spacing: 4px;
  position: relative;
  display: inline-block;
}

.end-title-text::before,
.end-title-text::after {
  content: "";
  position: absolute;
  top: 50%;
  width: 100px;
  height: 1px;
  background-color: #ddd;
}

.end-title-text::before {
  right: 100%;
  margin-right: 20px;
}

.end-title-text::after {
  left: 100%;
  margin-left: 20px;
}

.mark-complete-section {
  text-align: center;
  margin-bottom: 60px;
  padding: 30px;
  border-radius: 12px;
  background-color: rgba(250, 250, 250, 0.8);
  transition: background-color 0.3s ease;
}
.mark-complete-btn {
  background: linear-gradient(135deg, #4CAF50 0%, #8BC34A 100%);
  color: white;
  border: none;
  padding: 15px 40px;
  font-size: 18px;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-weight: 500;
  min-width: 160px;
}

.mark-complete-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(76, 175, 80, 0.3);
}

.mark-complete-btn.completed {
  background: linear-gradient(135deg, #4CAF50 0%, #8BC34A 100%);
}

/* 深色模式样式 */
.book-end-actions.dark-mode .end-title-text {
  color: #aaa;
}

.book-end-actions.dark-mode .end-title-text::before,
.book-end-actions.dark-mode .end-title-text::after {
  background-color: #444;
}

.book-end-actions.dark-mode .mark-complete-section {
  background-color: rgba(40, 40, 40, 0.8);
}

/* 深色模式下的按钮样式 */
.book-end-actions.dark-mode .mark-complete-btn {
  background: linear-gradient(135deg, #494a49 0%, #252926 100%);
  color: #e0e0e0;
}

.book-end-actions.dark-mode .mark-complete-btn:hover:not(:disabled) {
  box-shadow: 0 8px 20px rgba(46, 125, 50, 0.3);
}

.book-end-actions.dark-mode .mark-complete-btn.completed {
  background: linear-gradient(135deg, #494a49 0%, #252926 100%);
  color: #ffffff;
}

.book-end-actions.dark-mode .complete-time {
  color: #aaa;
}

/* 推荐值组件的包装容器 */
.recommendation-wrapper {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  position: relative;
  padding: 20px 0;
}

/* 深度选择器覆盖BookRecommendationSection的内部样式 */
.recommendation-wrapper :deep(.book-recommendation-section) {
  width: 100%;
  height: auto;
  min-height: 220px;
  padding: 0;
}

/* 调整推荐值组件的背景框样式 */
.recommendation-wrapper :deep(.recommendation-box) {
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.3s ease;
}

.recommendation-wrapper :deep(.recommendation-box):hover {
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.12);
}

/* 调整上下部分的间距 */
.recommendation-wrapper :deep(.top-section) {
  padding: 25px 30px 15px 30px;
}

.recommendation-wrapper :deep(.bottom-section) {
  padding: 10px 30px 25px 30px;
  gap: 15px;
}

/* 调整推荐值显示 */
.recommendation-wrapper :deep(.recommendation-value) {
  width: 140px;
}

.recommendation-wrapper :deep(.recommendation-label) {
  font-size: 18px;
  font-weight: 500;
  color: #666;
  margin-bottom: 10px;
}

.recommendation-wrapper :deep(.recommendation-percentage) {
  margin-bottom: 12px;
}

.recommendation-wrapper :deep(.percentage-number) {
  font-size: 42px;
  font-weight: 700;
  color: #000;
  line-height: 1;
}

.recommendation-wrapper :deep(.percentage-symbol) {
  font-size: 24px;
  font-weight: 600;
  color: #000;
}

.recommendation-wrapper :deep(.review-count) {
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: color 0.2s;
}

.recommendation-wrapper :deep(.review-count:hover) {
  color: #333;
}

/* 调整进度条 */
.recommendation-wrapper :deep(.progress-bars) {
  gap: 16px;
}

.recommendation-wrapper :deep(.progress-item) {
  align-items: center;
}

.recommendation-wrapper :deep(.progress-label) {
  font-size: 14px;
  color: #666;
  width: 40px;
}

.recommendation-wrapper :deep(.progress-bar) {
  height: 8px;
  border-radius: 4px;
}

.recommendation-wrapper :deep(.progress-fill) {
  border-radius: 4px;
}

/* 调整按钮样式 - 只调整基本样式，保持原有悬停效果 */
.recommendation-wrapper :deep(.rating-button) {
  padding: 10px 24px;
  border-radius: 8px;
  font-size: 18px;
  min-width: auto;
  flex: 1;
  max-width: 220px;
}

/* 深色模式下推荐值组件内部的样式 - 这部分已经在BookRecommendationSection.vue中处理了 */

/* 响应式调整 */
@media (max-width: 768px) {
  .recommendation-wrapper {
    padding: 0 16px;
  }

  .recommendation-wrapper :deep(.book-recommendation-section) {
    min-height: 200px;
  }

  .recommendation-wrapper :deep(.top-section) {
    padding: 20px 20px 10px 20px;
  }

  .recommendation-wrapper :deep(.bottom-section) {
    padding: 10px 20px 20px 20px;
    flex-wrap: wrap;
  }

  .recommendation-wrapper :deep(.rating-button) {
    min-width: 120px;
    padding: 10px 16px;
    font-size: 16px;
  }

  .recommendation-wrapper :deep(.recommendation-value) {
    width: 120px;
  }

  .recommendation-wrapper :deep(.percentage-number) {
    font-size: 36px;
  }
}
</style>
