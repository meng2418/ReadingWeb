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
      <div class="character-count">{{ reviewText.length }}/2000</div>
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
import { submitBookReview, deleteUserReview } from '@/api/bookreview'
import { getBookReviews } from '@/api/book-detail/user-reviews'

const route = useRoute()
const router = useRouter()

// 从路由参数获取信息
const bookId = ref(route.query.bookId as string || '')
const bookTitle = ref(route.query.bookTitle as string || '围城') // 默认值
const selectedRating = ref('recommend') // 默认选择推荐
const reviewText = ref('')
const isPublic = ref(true)
const isEditMode = ref(false) // 是否是编辑模式
const existingReviewId = ref<number | null>(null) // 现有书评ID（用于编辑时删除）

// 计算属性：是否可以提交
const canSubmit = computed(() => {
  const text = reviewText.value.trim()
  return text.length > 0 && text.length <= 2000
})

// 加载用户已有的点评（如果是编辑模式）
const loadUserReview = async () => {
  if (!bookId.value) return

  try {
    const currentUserId = ensureCurrentUserId()
    
    // 先从本地存储加载
    const existingReview = getUserReview(bookId.value, currentUserId)
    if (existingReview) {
      selectedRating.value = existingReview.rating || selectedRating.value
      reviewText.value = existingReview.content
      isPublic.value = existingReview.isPublic ?? true
      isEditMode.value = true
    }

    // 尝试从API获取当前用户的书评ID（用于编辑时删除）
    // 注意：getBookReviews可能只返回公开的书评，如果用户的书评不是公开的，可能找不到
    try {
      const reviews = await getBookReviews(bookId.value)
      // 查找当前用户的书评（通过userId或username匹配）
      const currentUserReview = reviews.find(review => {
        // 如果API返回了userId，直接匹配
        if (review.userId?.toString() === currentUserId) {
          return true
        }
        // 如果API返回了username，尝试匹配（但这种方式不太可靠）
        // 暂时不通过username匹配，因为username可能不是唯一的
        return false
      })
      if (currentUserReview?.reviewId) {
        existingReviewId.value = currentUserReview.reviewId
        console.log('找到现有书评ID:', existingReviewId.value)
      } else {
        console.warn('未找到当前用户的书评ID，编辑时将尝试创建新书评')
      }
    } catch (error) {
      console.warn('从API获取书评ID失败，编辑时将尝试创建新书评:', error)
    }
  } catch (error) {
    console.error('加载用户点评失败:', error)
  }
}

// 组件挂载时，如果有路由参数，设置对应的评分
onMounted(async () => {
  const rating = route.query.rating as string
  if (rating && ['recommend', 'average', 'poor'].includes(rating)) {
    selectedRating.value = rating
  }

  // 检查是否是编辑模式
  const editMode = route.query.editMode as string
  if (editMode === 'true') {
    isEditMode.value = true
  }

  // 加载用户已有的点评（异步）
  await loadUserReview()
})

// 返回上一页
const handleBack = () => {
  router.back()
}

// 提交点评
const handleSubmit = async () => {
  if (!canSubmit.value) return

  const currentUserId = ensureCurrentUserId()
  const formattedDate = formatDate(Date.now())

  try {
    // 如果是编辑模式，先删除旧书评（包括本地存储）
    if (isEditMode.value) {
      // 先删除本地存储中的旧书评
      removeUserReview(bookId.value, currentUserId)
      removePublicReview(bookId.value, currentUserId)
      
      // 如果有API返回的reviewId，尝试从API删除
      if (existingReviewId.value) {
        try {
          await deleteUserReview(existingReviewId.value)
          console.log('删除旧书评成功:', existingReviewId.value)
        } catch (error) {
          console.warn('删除旧书评失败，继续创建新书评:', error)
          // 如果删除失败，仍然尝试创建新书评（可能是旧书评不存在）
        }
      }
    }

    // 调用API提交书评（创建新书评）
    const apiResponse = await submitBookReview(bookId.value, selectedRating.value, reviewText.value, isPublic.value)
    console.log('API提交书评成功:', apiResponse)

    // 更新本地存储
    const review: Review = {
      id: apiResponse.review?.reviewId || Date.now(),
      reviewId: apiResponse.review?.reviewId, // 添加reviewId字段
      bookId: bookId.value,
      bookTitle: bookTitle.value,
      userId: currentUserId,
      userName: apiResponse.review?.username || '当前用户', // 使用API返回的用户名
      rating: selectedRating.value,
      content: reviewText.value,
      isPublic: isPublic.value,
      date: formattedDate,
      lastEditDate: formattedDate,
      avatar: apiResponse.review?.avatar || '', // 使用API返回的头像
    }

    // 更新本地存储（会覆盖旧的书评）
    upsertUserReview(review)
    if (isPublic.value) {
      upsertPublicReview(bookId.value, review)
    } else {
      // 如果不公开，删除公开存储中的旧书评
      removePublicReview(bookId.value, currentUserId)
    }
    
    // 如果是编辑模式，确保清理所有可能的旧数据
    // 通过重新设置来确保只保留最新的
    if (isEditMode.value) {
      // 再次确保删除旧数据（防止有遗漏）
      removeUserReview(bookId.value, currentUserId)
      removePublicReview(bookId.value, currentUserId)
      // 然后重新添加最新的
      upsertUserReview(review)
      if (isPublic.value) {
        upsertPublicReview(bookId.value, review)
      }
    }

    alert(isEditMode.value ? '点评更新成功！' : '点评发表成功！')

    // 等待一小段时间，确保后端数据已更新
    await new Promise(resolve => setTimeout(resolve, 500))

    // 通过路由传递数据返回到书籍详情页，强制刷新数据
    router.push({
      path: '/bookdetail',
      query: {
        bookId: bookId.value,
        bookTitle: bookTitle.value,
        refresh: 'true', // 标记需要刷新数据
        timestamp: Date.now().toString() // 添加时间戳，强制刷新
      }
    })
  } catch (error) {
    console.error('❌ API提交点评失败:', error)
    alert('网络连接失败，请检查网络后重试')

    // 不再回退到本地存储，直接提示用户重试
    return
  }
}

// 删除点评
const handleDelete = async () => {
  if (!confirm('确定要删除这条点评吗？')) return

  try {
    // 首先尝试调用API删除点评
    // 注意：这里需要知道点评的ID，但当前实现可能没有保存
    // 暂时先调用本地删除，后面可以根据实际需求调整
    const currentUserId = ensureCurrentUserId()

    // 如果有具体的点评ID，可以调用API
    // await deleteUserReview(reviewId)

    // 同时删除本地存储
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
    console.error('❌ 删除点评失败:', error)
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
