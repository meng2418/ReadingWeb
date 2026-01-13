<template>
  <div class="like-container">
    <!-- 左侧头像 -->
    <img :src="avatarUrl" alt="用户头像" class="like-avatar" @error="handleAvatarError" />

    <!-- 中间评论主体 -->
    <div class="like-main">
      <div class="like-user-bar">
        <span class="like-username">{{ like.user.username }}</span>
        <span class="like-action">赞了我的帖子</span>
      </div>
      <div class="like-meta">
        <span class="like-time">{{ formattedTime }}</span>
      </div>
    </div>

    <!-- 右侧文字卡片（帖子内容省略版）（移除图片，只保留文字+省略） -->
    <div class="like-right-card">
      <div class="card-text">{{ like.rightCardText }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { getAvatarUrl, DEFAULT_AVATAR } from '@/utils/defaultImages'

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

// 计算头像URL，使用默认头像
const avatarUrl = computed(() => getAvatarUrl(props.like?.user?.avatar))

// 头像加载失败时使用默认头像
const handleAvatarError = (event) => {
  const img = event.target
  if (img.src !== DEFAULT_AVATAR) {
    img.src = DEFAULT_AVATAR
  }
}

// 格式化时间
const formattedTime = computed(() => {
  const timeStr = props.like?.time || ''
  if (!timeStr) return '未知时间'
  
  try {
    const date = new Date(timeStr)
    if (isNaN(date.getTime())) return timeStr
    
    const now = new Date()
    const diff = now.getTime() - date.getTime()
    const minutes = Math.floor(diff / 60000)
    const hours = Math.floor(diff / 3600000)
    const days = Math.floor(diff / 86400000)

    if (minutes < 1) return '刚刚'
    if (minutes < 60) return `${minutes}分钟前`
    if (hours < 24) return `${hours}小时前`
    if (days < 7) return `${days}天前`
    return date.toLocaleDateString('zh-CN')
  } catch (e) {
    console.error('Error formatting date:', e)
    return timeStr
  }
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
