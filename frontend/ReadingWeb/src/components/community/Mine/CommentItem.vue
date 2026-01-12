<template>
  <div class="comment-container">
    <!-- 左侧头像 -->
    <img :src="avatarUrl" alt="用户头像" class="comment-avatar" @error="handleAvatarError" />

    <!-- 中间评论主体 -->
    <div class="comment-main">
      <div class="comment-user-bar">
        <span class="comment-username">{{ comment.user.username }}</span>
        <span class="comment-action">对我的帖子发表了评论</span>
      </div>
      <div class="comment-content">{{ comment.content }}</div>
      <div class="comment-meta">
        <span class="comment-time">{{ comment.time }}</span>
        <button class="comment-btn">回复</button>
        <button class="comment-btn">点赞</button>
      </div>
    </div>

    <!-- 右侧文字卡片（移除图片，只保留文字+省略） -->
    <div class="comment-right-card">
      <div class="card-text">{{ comment.rightCardText }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { getAvatarUrl, DEFAULT_AVATAR } from '@/utils/defaultImages'

const props = defineProps({
  comment: {
    type: Object,
    required: true,
    default: () => ({
      user: {
        avatar: '', // 头像地址
        username: '', // 用户名
      },
      content: '', // 评论内容
      time: '', // 评论时间
      rightCardText: '', // 右侧卡片文字（无图片）
    }),
  },
})

// 计算头像URL，使用默认头像
const avatarUrl = computed(() => getAvatarUrl(props.comment?.user?.avatar))

// 头像加载失败时使用默认头像
const handleAvatarError = (event) => {
  const img = event.target
  if (img.src !== DEFAULT_AVATAR) {
    img.src = DEFAULT_AVATAR
  }
}
</script>

<style scoped>
.comment-container {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  margin: 8px 0;
  border: 1px solid #f5f5f5; /* 加个边框更清晰 */
}

.comment-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  background: #f5f5f5; /* 无头像时占位 */
}

.comment-main {
  flex: 1; /* 占满中间空间 */
}

.comment-user-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.comment-username {
  font-weight: 600;
  font-size: 14px;
  color: #333;
}

.comment-action {
  font-size: 14px;
  color: #666;
}

.comment-content {
  font-size: 14px;
  color: #333;
  margin-bottom: 12px;
  line-height: 1.5;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 12px;
  color: #999;
}

.comment-btn {
  background: transparent;
  border: none;
  color: #999;
  cursor: pointer;
  padding: 0;
  transition: color 0.2s;
}
.comment-btn:hover {
  color: #1890ff; /* hover变蓝色更明显 */
}

/* 右侧文字卡片（核心修改：无图片+文字省略） */
.comment-right-card {
  width: 100px; /* 调整宽度适配纯文字 */
  text-align: center;
  padding: 8px;
  background: #fafafa;
  border-radius: 6px;
}

.card-text {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
  /* 关键：3行省略（可调整line-clamp改变行数） */
  display: -webkit-box;
  -webkit-line-clamp: 3; /* 限制最大3行 */
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
