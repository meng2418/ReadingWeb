<template>
  <div class="user-reviews">
   <h2 class="component-title">用户点评</h2>

    <div class="reviews-list">
      <div
        v-for="review in displayedReviews"
        :key="review.id"
        class="review-item"
      >
        <div class="user-avatar">
          <span class="avatar-text">{{ getInitials(review.userName) }}</span>
        </div>

        <div class="review-content">
          <div class="review-header">
            <span class="user-name">{{ review.userName }}</span>
            <span class="review-date">{{ formatDate(review.date) }}</span>
          </div>

          <div class="review-text">{{ review.content }}</div>
        </div>
      </div>
    </div>

    <div class="reviews-footer">
      <button
        class="view-more-btn"
        @click="loadMoreReviews"
        :disabled="isLoading || currentPage >= totalPages"
      >
        {{ isLoading ? '加载中...' : '查看更多精彩评论' }}
      </button>

      <div class="pagination" v-if="totalPages > 1">
        <button
          class="page-btn"
          :disabled="currentPage === 1"
          @click="changePage(currentPage - 1)"
        >
          上一页
        </button>
        <span class="page-info">第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
        <button
          class="page-btn"
          :disabled="currentPage === totalPages"
          @click="changePage(currentPage + 1)"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';

// 定义评论接口
interface Review {
  id: number;
  userName: string;
  content: string;
  date: string;
}

// 定义组件属性
interface Props {
  reviews: Review[];
  reviewsPerPage?: number;
}

// 定义组件事件
interface Emits {
  (e: 'loadMore'): void;
}

// 接收属性
const props = withDefaults(defineProps<Props>(), {
  reviewsPerPage: 3
});

// 定义事件
const emit = defineEmits<Emits>();

// 响应式数据
const currentPage = ref(1);
const isLoading = ref(false);

// 计算属性
const totalPages = computed(() => Math.ceil(props.reviews.length / props.reviewsPerPage));

const displayedReviews = computed(() => {
  const start = (currentPage.value - 1) * props.reviewsPerPage;
  const end = start + props.reviewsPerPage;
  return props.reviews.slice(start, end);
});

// 方法
const getInitials = (name: string): string => {
  return name.substring(0, 2);
};

const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
};

const loadMoreReviews = () => {
  if (currentPage.value < totalPages.value) {
    isLoading.value = true;

    // 模拟异步加载
    setTimeout(() => {
      currentPage.value++;
      isLoading.value = false;
      emit('loadMore');
    }, 800);
  }
};

const changePage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

// 生命周期
onMounted(() => {
  console.log('用户点评组件已加载');
});
</script>

<style scoped>
.user-reviews {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  padding: 20px;
  margin-bottom: 20px;
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
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
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
}

.review-content {
  flex: 1;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.user-name {
  font-weight: 500;
  color: #333;
}

.review-date {
  font-size: 12px;
  color: #999;
}

.review-text {
  color: #555;
  font-size: 14px;
  line-height: 1.5;
  text-align: justify;
}

.reviews-footer {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.view-more-btn {
  padding: 12px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  text-align: center;
  cursor: pointer;
  transition: background-color 0.2s;
  font-size: 14px;
}

.view-more-btn:hover:not(:disabled) {
  background-color: #f8f8f8;
}

.view-more-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
}

.page-btn {
  padding: 6px 12px;
  background: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .review-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }

  .pagination {
    flex-direction: column;
    gap: 10px;
  }
}
</style>
