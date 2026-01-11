<template>
  <div class="user-reviews">
    <h2 class="component-title">用户点评</h2>

    <div class="reviews-list" v-if="displayedReviews.length > 0">
      <div
        v-for="review in displayedReviews"
        :key="review.id"
        class="review-item"
      >
        <div class="user-avatar">
          <img v-if="review.avatar" :src="review.avatar" :alt="review.userName || '用户'" class="avatar-image" @error="handleAvatarError" />
          <span v-else class="avatar-text">{{ getInitials(review.userName || '用户') }}</span>
          <!-- 当前用户的点评标记 -->
          <span class="current-user-badge" v-if="review.userId && isCurrentUserReview(review.userId)">我</span>
        </div>

        <div class="review-content">
          <div class="review-header">
            <div class="user-info">
              <span class="user-name">{{ review.userName }}</span>
              <!-- 添加评分标签 -->
              <span class="rating-tag" :class="review.rating">{{ getRatingText(review.rating) }}</span>
              <!-- 编辑按钮（仅当前用户可见） -->
              <button
                class="edit-button"
                v-if="review.userId && isCurrentUserReview(review.userId)"
                @click="handleEditReview(review)"
              >
                编辑
              </button>
            </div>
            <div class="review-meta">
              <span class="review-date">{{ formatDate(review.date) }}</span>
              <span class="edit-indicator" v-if="review.lastEditDate && review.lastEditDate !== review.date">
                (已编辑)
              </span>
            </div>
          </div>

          <div class="review-text">{{ review.content }}</div>
        </div>
      </div>
    </div>

    <div class="no-reviews" v-else>
      <div class="no-reviews-icon">📝</div>
      <p class="no-reviews-text">暂无公开点评</p>
      <p class="no-reviews-subtext">成为第一个发表点评的人吧！</p>
    </div>

    <div class="reviews-footer" v-if="shouldShowButton">
      <button
        class="view-more-btn"
        @click="expandAllReviews"
        :disabled="isLoading"
      >
        {{ isLoading ? '加载中...' : '查看更多精彩评论' }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import {
  formatDate,
  ensureCurrentUserId,
  getLatestPublicReviews,
} from '@/composables/useReviews';
import type { Review } from '@/types/review';

const router = useRouter();

interface Props {
  reviews: Review[];
  reviewsPerPage?: number;
  bookId?: string; // 修改：添加书籍ID参数
}

// 接收属性
const props = withDefaults(defineProps<Props>(), {
  reviewsPerPage: 3,
  bookId: ''
});

// 响应式数据
const isLoading = ref(false);
const isExpanded = ref(false); // 是否已展开所有评论
const localReviews = ref<Review[]>([]); // 修改：添加本地存储的评论

// 计算属性
// 计算是否应该显示"查看更多"按钮
const shouldShowButton = computed(() => {
  const totalReviews = [...props.reviews, ...localReviews.value];
  return totalReviews.length > props.reviewsPerPage && !isExpanded.value;
});

// 显示的评论列表
const displayedReviews = computed(() => {
  const totalReviews = [...props.reviews, ...localReviews.value];

  const sortedReviews = totalReviews.sort((a, b) => {
    const dateA = a.lastEditDate || a.date;
    const dateB = b.lastEditDate || b.date;
    return new Date(dateB).getTime() - new Date(dateA).getTime();
  });

  if (isExpanded.value) {
    return sortedReviews;
  }
  return sortedReviews.slice(0, props.reviewsPerPage);
});

// 方法
const getInitials = (name: string): string => {
  return name.substring(0, 2);
};

// 添加获取评分文本的方法
const getRatingText = (rating: string | undefined): string => {
  if (!rating) return '';
  switch (rating) {
    case 'recommend': return '推荐';
    case 'average': return '一般';
    case 'poor': return '不行';
    case 'not_recommend': return '不行'; // API返回的枚举值
    default: return '';
  }
};

// 检查是否是当前用户的点评
const isCurrentUserReview = (userId: string): boolean => {
  const currentUserId = ensureCurrentUserId();
  return userId === currentUserId;
};

// 编辑点评
const handleEditReview = (review: Review) => {
  router.push({
    path: '/writereview',
    query: {
      bookId: review.bookId,
      bookTitle: review.bookTitle,
      editMode: 'true'
    }
  });
};

// 处理头像加载失败
const handleAvatarError = (event: Event) => {
  const img = event.target as HTMLImageElement;
  img.style.display = 'none';
  const parent = img.parentElement;
  if (parent) {
    const fallback = parent.querySelector('.avatar-text') as HTMLElement;
    if (fallback) {
      fallback.style.display = 'block';
    }
  }
};

const expandAllReviews = () => {
  isLoading.value = true;

  setTimeout(() => {
    isExpanded.value = true;
    isLoading.value = false;
  }, 800);
};

// 修改：加载本地存储的公开点评
const loadPublicReviews = () => {
  if (!props.bookId) return;

  try {
    localReviews.value = getLatestPublicReviews(props.bookId);
  } catch (error) {
    console.error('加载公共点评失败:', error);
    localReviews.value = [];
  }
};

// 修改：监听props变化，重新加载点评
watch(() => props.bookId, () => {
  loadPublicReviews();
});

// 监听localStorage变化（用于跨页面更新）
const setupLocalStorageListener = () => {
  window.addEventListener('storage', (event) => {
    if (event.key === 'publicReviews' || event.key === 'userReviews') {
      loadPublicReviews();
    }
  });
};

// 生命周期
onMounted(() => {
  console.log('用户点评组件已加载');
  loadPublicReviews();
  setupLocalStorageListener();
});
</script>

<style scoped>
.user-reviews {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  padding: 20px;
  margin-bottom: 20px;
  /* 移除固定最小高度，让组件高度自适应内容 */
}

.component-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.reviews-list {
  margin-bottom: 20px;
}

.review-item {
  display: flex;
  padding: 22px 0;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
}

.review-item:last-child {
  border-bottom: none;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  flex-shrink: 0;
  color: #666;
  font-weight: bold;
  font-size: 14px;
  position: relative;
  overflow: hidden;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-text {
  display: block;
}

.current-user-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #1890ff;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.review-content {
  flex: 1;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start; /* 修改：调整为顶部对齐 */
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px; /* 修改：添加间隔 */
  flex-wrap: wrap;
}

.user-name {
  font-weight: 500;
  color: #333;
}

.review-meta {
  display: flex;
  align-items: center;
  gap: 5px;
  flex-shrink: 0;
}

.review-date {
  font-size: 12px;
  color: #999;
}

.edit-indicator {
  font-size: 12px;
  color: #999;
  font-style: italic;
}

/* 添加评分标签样式 */
.rating-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 12px;
  background-color: #e6f7ff; /* 淡蓝色背景 */
  color: #1890ff; /* 蓝色文字 */
  border: 1px solid #91d5ff; /* 蓝色边框 */
}

/* 可以根据不同的评分类型设置不同的颜色 */
.rating-tag.recommend {
  background-color: #e6f7ff;
  color: #1890ff;
  border-color: #91d5ff;
}

.rating-tag.average {
  background-color: #fff7e6;
  color: #fa8c16;
  border-color: #ffd591;
}

.rating-tag.poor,
.rating-tag.not_recommend {
  background-color: #fff1f0;
  color: #f5222d;
  border-color: #ffa39e;
}

/* 编辑按钮样式 */
.edit-button {
  font-size: 12px;
  padding: 2px 8px;
  background-color: transparent;
  color: #1890ff;
  border: 1px solid #1890ff;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.edit-button:hover {
  background-color: #e6f7ff;
}

.review-text {
  color: #555;
  font-size: 14px;
  line-height: 1.5;
  text-align: justify;
}

/* 无点评状态 */
.no-reviews {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.no-reviews-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.no-reviews-text {
  font-size: 16px;
  margin-bottom: 8px;
  color: #666;
}

.no-reviews-subtext {
  font-size: 14px;
  color: #999;
}

.reviews-footer {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 10px;
  padding-top: 10px;
  /* 移除gap，使用margin-top和padding-top来控制间距 */
}

.view-more-btn {
  padding: 12px 24px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  width: 100%;
  max-width: 200px;
}

.view-more-btn:hover:not(:disabled) {
  background-color: #f8f8f8;
  border-color: #ccc;
}

.view-more-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .review-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }

  .review-meta {
    width: 100%;
  }

  .view-more-btn {
    max-width: 100%;
  }
}
</style>
