<!-- ReviewCard.vue -->
<template>
  <div class="review-card">
    <div class="review-card-wrapper">
      <!-- 左侧：书籍封面 -->
      <div class="review-cover-wrapper">
        <img :src="review.cover" alt="封面" class="review-book-cover" />
      </div>

      <!-- 右侧：评论信息 -->
      <div class="review-content">
        <div class="review-header">
          <h4 class="review-book-title">{{ review.bookName }}</h4>
          <div class="review-rating-tag" :class="ratingConfig[review.rating]?.className || ''">
            {{ ratingConfig[review.rating]?.label || review.rating }}
          </div>
        </div>

        <div class="review-text">
          <p>{{ review.content }}</p>
        </div>

        <div class="review-footer">
          <div class="review-info">
            <span class="review-date">{{ review.date }}</span>
            <span class="review-likes">{{ review.likes }} 人觉得有用</span>
          </div>
          <button class="delete-review-btn" @click="handleDelete">
            <el-icon><Delete /></el-icon>
            删除
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Delete } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

// 定义评级配置类型
interface RatingConfig {
  [key: string]: {
    label: string
    className: string
  }
}

// 定义书评类型
interface ReviewItem {
  id: number
  bookName: string
  cover: string
  rating: string
  date: string
  likes: number
  content: string
}

interface Props {
  review: ReviewItem
  ratingConfig: RatingConfig
}

const props = defineProps<Props>()
const emit = defineEmits<{
  delete: [id: number]
}>()

// 处理删除
const handleDelete = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这篇书评吗？删除后不可恢复。',
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    emit('delete', props.review.id)
  } catch {
    console.log('取消删除')
  }
}
</script>

<style scoped>
.review-card {
  background: white;
  border: 1px solid #f0f0f0;
  overflow: hidden;
}

.review-card-wrapper {
  display: flex;
  padding: 24px;
  gap: 24px;
}

/* 书评封面 */
.review-cover-wrapper {
  flex-shrink: 0;
}

.review-book-cover {
  width: 120px;
  height: 180px;
  object-fit: cover;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* 书评内容区域 */
.review-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.review-book-title {
  font-size: 20px;
  font-weight: 700;
  color: #2d3748;
  margin: 0;
}

/* 评级标签样式 */
.review-rating-tag {
  font-size: 14px;
  padding: 6px 12px;
  border-radius: 4px;
  font-weight: 600;
  white-space: nowrap;
}

.review-rating-tag.tag-recommend {
  color: #42b983;
  background-color: rgba(66, 185, 131, 0.1);
}

.review-rating-tag.tag-average {
  color: #ff9800;
  background-color: rgba(255, 152, 0, 0.1);
}

.review-rating-tag.tag-bad {
  color: #999;
  background-color: #eee;
}

/* 书评正文 */
.review-text {
  flex: 1;
  margin-bottom: 20px;
}

.review-text p {
  margin: 0;
  font-size: 15px;
  color: #4a5568;
  line-height: 1.7;
  font-weight: 400;
}

/* 书评底部 */
.review-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.review-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.review-date {
  font-size: 14px;
  color: #718096;
  font-weight: 500;
}

.review-likes {
  font-size: 14px;
  color: #718096;
  font-weight: 500;
}

.delete-review-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #f5f5f5;
  color: #666;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.delete-review-btn:hover {
  background: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .review-card-wrapper {
    flex-direction: column;
    padding: 16px;
  }

  .review-cover-wrapper {
    align-self: center;
  }

  .review-book-cover {
    width: 100px;
    height: 150px;
  }

  .review-header {
    flex-direction: column;
    gap: 8px;
  }

  .review-footer {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .review-info {
    width: 100%;
    justify-content: space-between;
  }
}

@media (max-width: 480px) {
  .review-text p {
    font-size: 14px;
  }
}
</style>
