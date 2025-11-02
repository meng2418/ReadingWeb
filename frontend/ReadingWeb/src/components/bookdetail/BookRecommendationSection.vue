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
      <button class="rating-button" @click="handleRate('recommend')">推荐</button>
      <button class="rating-button" @click="handleRate('average')">一般</button>
      <button class="rating-button" @click="handleRate('poor')">不行</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

// 定义props
interface Props {
  recommendationValue: number
  reviewCount: number
  ratingStats?: {
    recommend: number
    average: number
    poor: number
  }
}

const props = withDefaults(defineProps<Props>(), {
  recommendationValue: 90.5,
  reviewCount: 100,
  ratingStats: () => ({
    recommend: 70,
    average: 20,
    poor: 10
  })
})

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

// 查看点评
const handleViewReviews = () => {
  console.log('查看点评')
  emit('viewReviews')
}

// 评分
const handleRate = (rating: string) => {
  console.log('评分:', rating)
  emit('rateBook', rating)
}
</script>

<style scoped>
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
