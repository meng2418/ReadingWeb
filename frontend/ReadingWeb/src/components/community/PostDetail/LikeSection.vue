<template>
  <section class="like-section">
    <!-- 头部信息 -->
    <div class="like-header">
      <h3 class="like-count">{{ likesCount }} 人觉得很赞</h3>
    </div>

    <!-- 点赞列表 -->
    <div class="like-list">
      <LikeItem
        v-for="like in likes"
        :key="like.userId"
        :user="{
          id: like.userId,
          name: like.username,
          avatar: like.avatar,
        }"
        :timestamp="like.likeTime"
      />
    </div>

    <!-- 如果没有点赞 -->
    <div v-if="likes.length === 0" class="no-likes">
      <p>还没有人点赞，快来抢沙发吧~</p>
    </div>
  </section>
</template>

<script setup lang="ts">
import LikeItem from './LikeItem.vue'

const props = defineProps<{
  likes: any[] // 接收来自 PostDetailPage 的 rawData.likedUsers
}>()

// 计算点赞总数
const likesCount = props.likes.length
</script>

<style scoped>
/* 主点赞区容器，样式与评论区保持一致 */
.like-section {
  background-color: #fff;
  border-radius: 1.25rem; /* 20px */
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  border: 1px solid #f1f5f9; /* slate-100 */
  padding: 1.5rem; /* 24px */
}

/* 头部信息 */
.like-header {
  margin-bottom: 1rem; /* 16px */
  padding-bottom: 0.5rem; /* 8px */
  border-bottom: 1px solid #f1f5f9; /* slate-100 */
}

.like-count {
  font-size: 1rem; /* 16px */
  font-weight: 600;
  color: #1e293b; /* slate-800 */
  margin: 0;
}

/* 点赞列表 */
.like-list {
  display: flex;
  flex-direction: column;
  max-height: 400px; /* 限制最大高度，可根据需要调整 */
  overflow-y: auto; /* 内容过多时显示滚动条 */
}

/* 滚动条样式优化 (可选) */
.like-list::-webkit-scrollbar {
  width: 4px;
}
.like-list::-webkit-scrollbar-thumb {
  background-color: rgba(148, 163, 184, 0.3); /* slate-500 with opacity */
  border-radius: 4px;
}

/* 没有点赞时的提示 */
.no-likes {
  text-align: center;
  padding: 2rem 1rem;
  color: #94a3b8; /* slate-500 */
  font-size: 0.875rem; /* 14px */
}

/* 响应式调整 */
@media (min-width: 768px) {
  .like-section {
    padding: 2rem; /* 32px */
  }
}
</style>
