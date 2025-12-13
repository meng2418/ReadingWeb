<!-- UserCard.vue -->
<template>
  <div class="user-card" :class="{ 'user-card-hover': hover }" @mouseenter="hover = true" @mouseleave="hover = false">
    <!-- 左侧头像 -->
    <div class="user-avatar-container">
      <img :src="user.avatar" alt="用户头像" class="user-avatar">
    </div>
    <!-- 右侧内容 -->
    <div class="user-right-content">
      <div class="user-info">
        <h4 class="user-name">{{ user.username }}</h4>
        <p class="user-bio">{{ user.bio }}</p>
      </div>
      <button
        class="follow-btn"
        :class="{ following: isFollowing }"
        @click="handleFollowClick"
      >
        {{ getButtonText }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessageBox } from 'element-plus'

// 定义用户类型
interface User {
  id: number
  username: string
  avatar: string
  bio: string
  isFollowing?: boolean
}

// 定义 props
interface Props {
  user: User
  type: 'following' | 'followers'  // 区分是关注列表还是粉丝列表
}

const props = defineProps<Props>()
const emit = defineEmits<{
  unfollow: [id: number]
  follow: [id: number]
}>()

const hover = ref(false)
const isFollowing = ref(props.user.isFollowing || false)

// 计算按钮文本
const getButtonText = computed(() => {
  if (props.type === 'following') {
    return '已关注'
  } else {
    return isFollowing.value ? '已互关' : '回关'
  }
})

// 处理关注/取消关注点击
const handleFollowClick = () => {
  if (props.type === 'following') {
    // 取消关注
    handleUnfollow()
  } else {
    // 关注/回关
    handleFollow()
  }
}

// 关注操作
const handleFollow = () => {
  isFollowing.value = !isFollowing.value
  emit('follow', props.user.id)
}

// 取消关注操作
const handleUnfollow = () => {
  ElMessageBox.confirm(
    '确定要取消关注吗？',
    '取消关注',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    emit('unfollow', props.user.id)
  }).catch(() => {
    console.log('取消操作')
  })
}
</script>

<style scoped>
.user-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #f0f0f0;
  transition: all 0.2s ease;
  display: flex;
  align-items: stretch;
  min-height: 120px; /* 设置最小高度，使卡片高度统一 */
}


/* 左侧头像容器 */
.user-avatar-container {
  flex-shrink: 0;
  margin-right: 12px;
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
}

/* 右侧内容区域 */
.user-right-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0; /* 防止内容溢出 */
}

/* 用户信息区域 */
.user-info {
  flex: 1;
  margin-bottom: 12px;
  overflow: hidden;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
  line-height: 1.2;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-bio {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 关注按钮样式 */
.follow-btn {
  padding: 8px 16px;
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 16px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  white-space: nowrap;
  height: 32px; /* 固定按钮高度 */
  width: 100%; /* 按钮宽度占满右侧区域 */
}

.follow-btn:hover {
  background: #ff8787;
}

.follow-btn.following {
  background: #f0f0f0;
  color: #666;
}

.follow-btn.following:hover {
  background: #e0e0e0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-card {
    min-height: 100px;
  }

  .user-avatar {
    width: 50px;
    height: 50px;
  }
}
</style>
