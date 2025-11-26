<template>
  <div class="like-container">
    <!-- 左侧头像 -->
    <img :src="like.user.avatar" alt="用户头像" class="like-avatar" />

    <!-- 中间评论主体 -->
    <div class="like-main">
      <div class="like-user-bar">
        <span class="like-username">{{ like.user.username }}</span>
        <span class="like-action">赞了我的帖子</span>
      </div>
      <div class="like-meta">
        <span class="like-time">{{ like.time }}</span>
      </div>
    </div>

    <!-- 右侧文字卡片（帖子内容省略版）（移除图片，只保留文字+省略） -->
    <div class="like-right-card">
      <div class="card-text">{{ like.rightCardText }}</div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  like: {
    type: Object,
    required: true,
    default: () => ({
      user: {
        avatar: '', // 头像地址
        username: '', // 用户名
      },
      time: '', // 点赞时间
      rightCardText: '', // 右侧卡片文字
    }),
  },
})
</script>

<style scoped>
.like-container {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  margin: 8px 0;
  border: 1px solid #f5f5f5; /* 加个边框更清晰 */
}

.like-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  background: #f5f5f5; /* 无头像时占位 */
}

.like-main {
  flex: 1; /* 占满中间空间 */
}

.like-user-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.like-username {
  font-weight: 600;
  font-size: 14px;
  color: #333;
}

.like-action {
  font-size: 14px;
  color: #666;
}

.like-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 12px;
  color: #999;
}

/* 右侧文字卡片（核心修改：无图片+文字省略） */
.like-right-card {
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
