<template>
  <div class="write-review-page">
    <!-- 顶部导航栏 -->
    <div class="review-header">
      <div class="back-button" @click="handleBack">&lt;</div>
      <h2 class="review-title">{{ isEditMode ? '编辑点评' : '写点评' }}</h2>
      <div class="book-title">{{ bookTitle }}</div>
    </div>

    <!-- 评分选择 -->
    <div class="rating-section">
      <div class="rating-title">你的评分</div>
      <div class="rating-buttons">
        <button
          class="rating-option recommend"
          :class="{ active: selectedRating === 'recommend' }"
          @click="selectedRating = 'recommend'"
        >
          推荐
        </button>
        <button
          class="rating-option average"
          :class="{ active: selectedRating === 'average' }"
          @click="selectedRating = 'average'"
        >
          一般
        </button>
        <button
          class="rating-option poor"
          :class="{ active: selectedRating === 'poor' }"
          @click="selectedRating = 'poor'"
        >
          不行
        </button>
      </div>
    </div>

    <!-- 点评内容 -->
    <div class="review-content">
      <textarea
        v-model="reviewText"
        class="review-textarea"
        placeholder="写下你的阅读感受..."
        rows="10"
      ></textarea>
      <div class="character-count">{{ reviewText.length }}/1000</div>
    </div>

    <!-- 公开选项 -->
    <div class="privacy-section">
      <div class="privacy-option">
        <input
          type="checkbox"
          id="public-review"
          v-model="isPublic"
          class="privacy-checkbox"
        />
        <label for="public-review" class="privacy-label">
          <span class="privacy-icon">公开</span>
          <span class="privacy-desc">其他用户可见</span>
        </label>
      </div>
      <div class="privacy-note" v-if="!isPublic">
        注：选择不公开，你的点评将不会显示在用户点评列表中
      </div>
    </div>

    <!-- 发表按钮 -->
    <div class="submit-section">
      <!-- 编辑模式下的删除按钮 -->
      <button
        class="delete-button"
        v-if="isEditMode"
        @click="handleDelete"
      >
        删除点评
      </button>

      <button
        class="submit-button"
        :class="{ disabled: !canSubmit }"
        @click="handleSubmit"
      >
        {{ isEditMode ? '更新点评' : '发表点评' }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  formatDate,
  ensureCurrentUserId,
  upsertUserReview,
  upsertPublicReview,
  removePublicReview,
  removeUserReview,
  getUserReview,
} from '@/composables/useReviews'
import type { Review } from '@/types/review'

const route = useRoute()
const router = useRouter()

// 从路由参数获取信息
const bookId = ref(route.query.bookId as string || '')
const bookTitle = ref(route.query.bookTitle as string || '围城') // 默认值
const selectedRating = ref('recommend') // 默认选择推荐
const reviewText = ref('')
const isPublic = ref(true)
const isEditMode = ref(false) // 是否是编辑模式

// 计算属性：是否可以提交
const canSubmit = computed(() => {
  return reviewText.value.trim().length > 0 && reviewText.value.length <= 1000
})

// 加载用户已有的点评（如果是编辑模式）
const loadUserReview = () => {
  if (!bookId.value) return

  try {
    const currentUserId = ensureCurrentUserId()
    const existingReview = getUserReview(bookId.value, currentUserId)
    if (existingReview) {
      selectedRating.value = existingReview.rating || selectedRating.value
      reviewText.value = existingReview.content
      isPublic.value = existingReview.isPublic ?? true
      isEditMode.value = true
    }
  } catch (error) {
    console.error('加载用户点评失败:', error)
  }
}

// 组件挂载时，如果有路由参数，设置对应的评分
onMounted(() => {
  const rating = route.query.rating as string
  if (rating && ['recommend', 'average', 'poor'].includes(rating)) {
    selectedRating.value = rating
  }

  // 检查是否是编辑模式
  const editMode = route.query.editMode as string
  if (editMode === 'true') {
    isEditMode.value = true
  }

  // 加载用户已有的点评
  loadUserReview()
})

// 返回上一页
const handleBack = () => {
  router.back()
}

// 提交点评
const handleSubmit = () => {
  if (!canSubmit.value) return

  const currentUserId = ensureCurrentUserId()
  const formattedDate = formatDate(Date.now())

  const review: Review = {
    id: Date.now(),
    bookId: bookId.value,
    bookTitle: bookTitle.value,
    userId: currentUserId,
    userName: '当前用户',
    rating: selectedRating.value,
    content: reviewText.value,
    isPublic: isPublic.value,
    date: formattedDate,
    lastEditDate: formattedDate,
  }

  try {
    upsertUserReview(review)
    if (isPublic.value) {
      upsertPublicReview(bookId.value, review)
    } else {
      removePublicReview(bookId.value, currentUserId)
    }

    alert(isEditMode.value ? '点评更新成功！' : '点评发表成功！')

    // 通过路由传递数据返回到书籍详情页
    router.push({
      path: '/bookdetail',
      query: {
        bookId: bookId.value,
        bookTitle: bookTitle.value,
        refresh: 'true' // 标记需要刷新数据
      }
    })
  } catch (error) {
    console.error('保存点评失败:', error)
    alert('保存失败，请重试')
  }
}

// 删除点评
const handleDelete = () => {
  if (!confirm('确定要删除这条点评吗？')) return

  try {
    const currentUserId = ensureCurrentUserId()
    removeUserReview(bookId.value, currentUserId)
    removePublicReview(bookId.value, currentUserId)

    alert('点评删除成功！')

    // 返回到书籍详情页
    router.push({
      path: '/bookdetail',
      query: {
        bookId: bookId.value,
        bookTitle: bookTitle.value,
        refresh: 'true' // 标记需要刷新数据
      }
    })
  } catch (error) {
    console.error('删除点评失败:', error)
    alert('删除失败，请重试')
  }
}
</script>

<style scoped>
.write-review-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  min-height: calc(100vh - 40px); /* 使用calc确保占满视口高度 */
  display: flex;
  flex-direction: column;
}

/* 头部样式 */
.review-header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.back-button {
  font-size: 24px;
  color: #666;
  cursor: pointer;
  padding: 0 15px;
  margin-right: 10px;
}

.review-title {
  flex: 1;
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.book-title {
  font-size: 16px;
  color: #666;
  font-weight: normal;
}

/* 评分部分 */
.rating-section {
  margin-bottom: 30px;
  flex-shrink: 0;
}

.rating-title {
  font-size: 16px;
  color: #333;
  margin-bottom: 15px;
}

.rating-buttons {
  display: flex;
  gap: 20px;
}

.rating-option {
  flex: 1;
  padding: 12px 24px;
  border: 1px solid #d0d0d0;
  border-radius: 8px;
  background: #f8f8f8;
  cursor: pointer;
  font-size: 16px;
  color: #666;
  transition: all 0.2s;
  text-align: center;
}

.rating-option:hover {
  background: #f0f0f0;
}

/* 修改：将三个评分按钮的颜色改为淡蓝色 */
.rating-option.recommend.active {
  background: #e6f7ff; /* 淡蓝色背景 */
  border-color: #91d5ff; /* 蓝色边框 */
  color: #1890ff; /* 蓝色文字 */
}

.rating-option.average.active {
  background: #e6f7ff; /* 淡蓝色背景 */
  border-color: #91d5ff; /* 蓝色边框 */
  color: #1890ff; /* 蓝色文字 */
}

.rating-option.poor.active {
  background: #e6f7ff; /* 淡蓝色背景 */
  border-color: #91d5ff; /* 蓝色边框 */
  color: #1890ff; /* 蓝色文字 */
}

/* 点评内容 */
.review-content {
  margin-bottom: 30px;
  flex: 1; /* 让内容区域可以扩展填充空间 */
  display: flex;
  flex-direction: column;
}

.review-textarea {
  flex: 1; /* 让textarea可以扩展填充空间 */
  width: 100%;
  padding: 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.6;
  resize: vertical;
  box-sizing: border-box;
  min-height: 150px; /* 修改：增加最小高度 */
}

.review-textarea:focus {
  outline: none;
  border-color: #666;
}

.character-count {
  text-align: right;
  font-size: 12px;
  color: #999;
  margin-top: 5px;
  flex-shrink: 0;
}

/* 公开选项 */
.privacy-section {
  margin-bottom: 40px;
  flex-shrink: 0;
}

.privacy-option {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.privacy-checkbox {
  width: 18px;
  height: 18px;
  margin-right: 10px;
  cursor: pointer;
}

.privacy-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 14px;
  color: #333;
}

.privacy-icon {
  display: inline-block;
  padding: 4px 8px;
  background: #f0f0f0;
  border-radius: 4px;
  margin-right: 8px;
  font-size: 12px;
}

.privacy-desc {
  color: #666;
}

.privacy-note {
  font-size: 12px;
  color: #999;
  padding-left: 28px;
  margin-top: 5px;
}

/* 提交按钮区域 */
.submit-section {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-shrink: 0;
}

.submit-button {
  width: 200px;
  padding: 12px 24px;
  background: #80b5d8;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.submit-button:hover {
  background: #3693ea;
}

.submit-button.disabled {
  background: #ccc;
  cursor: not-allowed;
}

.delete-button {
  width: 200px;
  padding: 12px 24px;
  background: #fff;
  color: #f5222d;
  border: 1px solid #ff4d4f;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.delete-button:hover {
  background: #fff1f0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .write-review-page {
    padding: 15px;
    min-height: calc(100vh - 30px);
  }

  .rating-buttons {
    flex-direction: column;
    gap: 10px;
  }

  .rating-option {
    width: 100%;
  }

  .submit-section {
    flex-direction: column;
    align-items: center;
    gap: 15px;
  }

  .submit-button, .delete-button {
    width: 100%;
    max-width: 300px;
  }
}
</style>
