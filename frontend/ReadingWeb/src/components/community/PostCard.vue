<template>
  <div class="post-card">
    <!-- 用户信息 -->
    <div class="post-header">
      <div class="avatar-container">
        <img :src="avatarUrl" class="avatar-img" :alt="`${username}的头像`" @error="handleAvatarError" />
      </div>

      <div class="user-info">
        <div class="username">{{ username }}</div>
        <div class="post-time">{{ postTime }}</div>
      </div>

      <!-- 条件渲染关注按钮 -->
      <button
        v-if="showFollowButton"
        class="follow-btn"
        :class="{ following: localIsFollowing }"
        @click="toggleFollow"
      >
        {{ localIsFollowing ? '已关注' : '+ 关注' }}
      </button>
    </div>

    <!-- 帖子内容 -->
    <div class="post-body">
      <h3 class="post-title" v-if="title" @click="handleCardClick">{{ title }}</h3>
      <div class="post-content-section" @click="handleCardClick">
        <p class="post-content">
          {{ showFull ? content : truncatedContent }}
          <span v-if="isTruncated" class="expand-btn" @click.stop="toggleExpand"> 展开 </span>
        </p>
      </div>
      <!-- 如果有书籍，展示 mini bookcard -->
      <LinkBookCard
        v-if="book"
        :cover="book.cover as string"
        :title="book.title"
        :author="book.author as string"
        :book-id="book.bookId as number"
      />
    </div>

    <!-- 操作区 -->
    <div class="post-actions">
      <div class="action-item" @click="commentPost">
        <el-icon><Comment /></el-icon>
        <span v-if="commentCount > 0">{{ commentCount }}</span>
      </div>

      <div class="action-item" @click="handleLike" :class="{ liked: localIsLiked }">
        <Heart
          :fill="localIsLiked ? '#ff6b6b' : 'transparent'"
          :stroke="localIsLiked ? '#ff6b6b' : '#666'"
          width="18px"
          height="18px"
          stroke-width="1.5"
          class="heart-icon"
        />
        <span v-if="localLikeCount > 0">{{ localLikeCount }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import router from '@/router'
import { ref, computed } from 'vue'
import { Comment } from '@element-plus/icons-vue'
import LinkBookCard from './LinkBookCard.vue'
import { Heart } from 'lucide-vue-next'
import type { Post, PostBookSummary, PostCardEmits } from '@/types/post'
import { getAvatarUrl, DEFAULT_AVATAR } from '@/utils/defaultImages'

interface Props extends Post {
  postId?: number // 补充postId类型定义，修复跳转TS警告
  showFollowButton?: boolean // 新增：控制是否显示关注按钮
  book?: PostBookSummary | null
}

const props = withDefaults(defineProps<Props>(), {
  avatar: undefined,
  title: undefined,
  book: null,
  postId: undefined,
  showFollowButton: true, // 默认显示关注按钮
})

const emit = defineEmits<PostCardEmits>()

const showFull = ref<boolean>(false)
const maxChars = 120 // 控制显示多少字

// 本地状态分离：关注/点赞互不干扰
const localIsLiked = ref<boolean>(props.isLiked)
const localLikeCount = ref<number>(props.likeCount)
const localIsFollowing = ref<boolean>(props.isFollowing)

// 计算头像URL，使用默认头像
const avatarUrl = computed(() => getAvatarUrl(props.avatar))

// 头像加载失败时使用默认头像
const handleAvatarError = (event: Event) => {
  const img = event.target as HTMLImageElement
  if (img.src !== DEFAULT_AVATAR) {
    img.src = DEFAULT_AVATAR
  }
}

// 内容截断计算
const isTruncated = computed((): boolean => {
  return props.content.length > maxChars && !showFull.value
})

const truncatedContent = computed((): string => {
  return isTruncated.value ? props.content.slice(0, maxChars) + '...' : props.content
})

// 关注切换：仅处理关注状态，与点赞完全分离
const toggleFollow = (): void => {
  localIsFollowing.value = !localIsFollowing.value
  emit('follow-change', localIsFollowing.value)
}

// 点赞切换：仅处理点赞状态，逻辑清晰
const handleLike = (): void => {
  localIsLiked.value = !localIsLiked.value
  localLikeCount.value += localIsLiked.value ? 1 : -1
  emit('like', localLikeCount.value, localIsLiked.value)
}

// 其他操作事件保持不变
const commentPost = (): void => {
  emit('comment')
  // 优先使用 props.id (来自 Post 接口)，如果没有则尝试使用 postId
  const targetId = props.id || props.postId
  if (targetId) {
    router.push(`/postdetail/${targetId}`)
  } else {
    console.error('无法跳转：缺少帖子 ID')
  }
}

const toggleExpand = (): void => {
  showFull.value = !showFull.value
}

// 点击卡片跳转：修复postId判断逻辑
const handleCardClick = () => {
  const targetId = props.id || props.postId
  if (targetId) {
    const url = `/postdetail/${targetId}`
    // 在新标签页打开
    window.open(url, '_blank')
  } else {
    console.error('无法跳转：缺少帖子 ID')
  }
}
</script>

<style scoped>
.post-content {
  color: #444;
  line-height: 1.7;
  font-size: 15px;
  white-space: pre-wrap;
  word-break: break-word;
}

.expand-btn {
  color: #0078f2;
  cursor: pointer;
  font-size: 14px;
}
.expand-btn:hover {
  text-decoration: underline;
}

.post-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  /*box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);*/
  border: 1px solid #f0f0f0;
}

.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.avatar-container {
  width: 48px;
  height: 48px;
  margin-right: 12px;
}

.avatar-img,
.avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder {
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 18px;
}

.user-info {
  flex: 1;
}

.username {
  font-weight: 600;
  font-size: 16px;
  color: #333;
}

.post-time {
  font-size: 13px;
  color: #999;
}

/* 关注按钮样式：仅用于关注功能，不与点赞混淆 */
.follow-btn {
  border: 1px solid #ff6b6b;
  background: #fff;
  color: #ff6b6b;
  border-radius: 18px;
  padding: 6px 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.follow-btn:hover {
  background: #ff6b6b;
  color: #fff;
}

.follow-btn.following {
  background: #ff6b6b;
  color: #fff;
}

.follow-btn.following:hover {
  background: #ff8787;
}

.post-body {
  margin-bottom: 16px;
}

.post-title {
  font-size: 17px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 8px;
  cursor: pointer;
}

.post-content-section {
  cursor: pointer;
}

/* 操作区：统一三个图标样式 */
.post-actions {
  display: flex;
  justify-content: center;
  align-items: center;
  border-top: 1px solid #f5f5f5;
  padding-top: 10px;
  gap: 250px; /* 控制两个按钮之间的距离 */
}

.action-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 16px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s ease;
  padding: 6px 12px;
  border-radius: 6px;
}

.action-item:hover {
  color: #ff6b6b;
  transform: scale(1.05);
}

/* 点赞选中状态：仅控制点赞区域 */
.action-item.liked {
  color: #ff6b6b;
}

/* 爱心图标统一样式：与评论图标尺寸、对齐一致 */
.heart-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

/* 确保Element图标与自定义爱心图标垂直对齐 */
.el-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
</style>
