<!-- ReadingReviews.vue -->
<template>
  <div class="reviews-section">
    <div class="card-header">
      <h3 class="card-title">我的书评</h3>
      <span class="view-all" @click="goToAllReviews">
        查看全部 <ArrowRight class="icon-inline" />
      </span>
    </div>

    <div class="reviews-grid">
      <div v-for="review in reviews" :key="review.id" class="review-card">
        <!-- 左侧：书籍封面 -->
        <div class="cover-wrapper">
          <img :src="review.cover" alt="封面" class="book-cover" />
        </div>

        <!-- 右侧：评论信息 -->
        <div class="review-content">
          <div class="review-header">
            <span class="book-title">{{ review.bookName }}</span>
            <!-- 评级标签 -->
            <span class="rating-tag" :class="ratingConfig[review.rating].className">
              {{ ratingConfig[review.rating].label }}
            </span>
          </div>

          <p class="review-text">{{ review.content }}</p>

          <div class="review-footer">
            <span class="review-date">{{ review.date }}</span>
            <span class="likes-count">{{ review.likes }} 人觉得有用</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight } from 'lucide-vue-next'

const router = useRouter()
const defaultCover =
  'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=200'

const goToAllReviews = () => {
  const url = '/userposts?tab=reviews'
  // 在新标签页打开
  window.open(url, '_blank')
}

// 定义评级对应的 配置（文字 + 样式类名）
const ratingConfig = {
  recommend: { label: '推荐', className: 'tag-recommend' },
  average: { label: '一般', className: 'tag-average' },
  bad: { label: '不行', className: 'tag-bad' },
}

const reviews = ref([
  {
    id: 1,
    bookName: '置身事内',
    cover: defaultCover,
    rating: 'recommend', // 对应 ratingConfig 的 key
    date: '2024-05-20',
    likes: 128,
    content:
      '这本书彻底改变了我对宏观经济的看法。它不是枯燥的理论堆砌，而是从地方政府的微观视角切入。',
  },
  {
    id: 2,
    bookName: '当尼采哭泣',
    cover:
      'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&q=80&w=200',
    rating: 'recommend',
    date: '2024-04-15',
    likes: 45,
    content: '心理咨询的开山之作？欧文·亚隆把哲学和心理学融合得太完美了。',
  },
  {
    id: 3,
    bookName: '烂书示例', // 模拟数据
    cover:
      'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?auto=format&fit=crop&q=80&w=200',
    rating: 'bad',
    date: '2024-02-10',
    likes: 2,
    content: '浪费时间的书，逻辑混乱，很多观点都站不住脚，不建议阅读。',
  },
  {
    id: 4,
    bookName: '普通读物', // 模拟数据
    cover:
      'https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&q=80&w=200',
    rating: 'average',
    date: '2023-11-05',
    likes: 12,
    content: '还可以，作为消遣读读不错，但没有什么深度。',
  },
])
</script>

<style scoped>
.reviews-section {
  background: var(--card-bg, #fff);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--shadow, 0 4px 12px rgba(0, 0, 0, 0.05));
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
  color: var(--text-main, #333);
  margin: 0;
}

.view-all {
  font-size: 14px;
  color: var(--text-light, #999);
  cursor: pointer;
  display: flex;
  align-items: center;
}
.view-all:hover {
  color: var(--primary-green, #42b983);
}
.icon-inline {
  width: 16px;
  height: 16px;
  margin-left: 4px;
}

/* Grid 布局 */
.reviews-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}
@media (max-width: 768px) {
  .reviews-grid {
    grid-template-columns: 1fr;
  }
}

/* 
  卡片样式：
  删除了 transform 和 box-shadow 的 hover 变化
  只保留边框颜色的变化，更加扁平稳重
*/
.review-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: #f9f9f9;
  border: 1px solid #eee; /* 默认浅灰边框 */
  cursor: pointer;
  transition:
    border-color 0.2s,
    background-color 0.2s;
}

.review-card:hover {
  background: #fff; /* 悬浮时背景变白 */
  border-color: var(--primary-green, #42b983); /* 仅边框变绿 */
}

/* 左侧封面 */
.cover-wrapper {
  flex-shrink: 0;
  width: 70px;
  height: 98px;
  border-radius: 6px;
  overflow: hidden;
  background: #eee;
}

.book-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 右侧内容 */
.review-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.book-title {
  font-size: 15px;
  font-weight: bold;
  color: var(--text-main, #333);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-right: 8px;
}

/* --- 评级标签样式 --- */
.rating-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 600;
  white-space: nowrap;
  flex-shrink: 0;
}

/* 推荐：绿色 */
.tag-recommend {
  color: var(--primary-green, #42b983);
  background-color: rgba(66, 185, 131, 0.1);
}

/* 一般：橙色 */
.tag-average {
  color: #ff9800;
  background-color: rgba(255, 152, 0, 0.1);
}

/* 不行：灰色 */
.tag-bad {
  color: #999;
  background-color: #eee;
}

.review-text {
  margin: 0;
  font-size: 13px;
  color: #555;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}

.review-footer {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

/* 优化数字字体显示 */
.review-date,
.likes-count {
  font-feature-settings: 'tnum';
  font-variant-numeric: tabular-nums;
}
</style>
