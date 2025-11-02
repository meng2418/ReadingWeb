<template>
  <!-- 模板部分完全不需要修改 -->
  <div class="post-card">
    <!-- 用户信息 -->
    <div class="post-header">
      <div class="avatar-container">
        <img v-if="avatar" :src="avatar" class="avatar-img" />
        <div v-else class="avatar-placeholder">{{ username.charAt(0) }}</div>
      </div>

      <div class="user-info">
        <div class="username">{{ username }}</div>
        <div class="post-time">{{ postTime }}</div>
      </div>

      <button
        class="follow-btn"
        :class="{ following: localIsFollowing }"
        @click="toggleFollow"
      >
        {{ localIsFollowing ? '已关注' : '+ 关注' }}
      </button>
    </div>

    <!-- 帖子内容 -->
    <div class="post-body">
      <h3 class="post-title" v-if="title">{{ title }}</h3>
      <div class="post-content-section">
        <p class="post-content">
          {{ showFull ? content : truncatedContent }}
          <span v-if="isTruncated" class="expand-btn" @click="toggleExpand">
            {{ showFull ? ' 收起' : ' 展开' }}
          </span>
        </p>
      </div>
      <!-- 如果有书籍，展示 mini bookcard -->
      <LinkBookCard
        v-if="book"
        :cover="book.cover"
        :title="book.title"
        :author="book.author"
        :book-id="book.id"
      />
    </div>

    <!-- 操作区 -->
    <div class="post-actions">
      <div class="action-item" @click="sharePost">
        <el-icon><Share /></el-icon>
        <span v-if="shareCount > 0">{{ shareCount }}</span>
      </div>

      <div class="action-item" @click="commentPost">
        <el-icon><Comment /></el-icon>
        <span v-if="commentCount > 0">{{ commentCount }}</span>
      </div>

      <div
        class="action-item"
        @click="handleLike"
        :class="{ liked: localIsLiked }"
      >
        <el-icon>
          <StarFilled v-if="localIsLiked" />
          <Star v-else />
        </el-icon>
        <span v-if="localLikeCount > 0">{{ localLikeCount }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">

import { ref, computed } from 'vue'
import { Comment, Share, Star, StarFilled } from '@element-plus/icons-vue'
import LinkBookCard from './LinkBookCard.vue'


interface Book {
  id: number
  cover: string
  title: string
  author: string
}

interface Props {
  username: string
  avatar?: string
  postTime: string
  title?: string
  content: string
  likeCount: number
  commentCount: number
  shareCount: number
  isFollowing: boolean
  isLiked: boolean
  book?: Book | null
}


const props = withDefaults(defineProps<Props>(), {
  avatar: undefined,
  title: undefined,
  book: null
})


const emit = defineEmits<{
  'follow-change': [isFollowing: boolean]
  'like': [likeCount: number, isLiked: boolean]
  'comment': []
  'share': []
}>()


const showFull = ref<boolean>(false)
const maxChars = 120 // 控制显示多少字

// 从props初始化本地状态
const localIsLiked = ref<boolean>(props.isLiked)
const localLikeCount = ref<number>(props.likeCount)
const localIsFollowing = ref<boolean>(props.isFollowing)


const isTruncated = computed((): boolean => {
  return props.content.length > maxChars
})

const truncatedContent = computed((): string => {
  return isTruncated.value
    ? props.content.slice(0, maxChars) + '...'
    : props.content
})


const toggleFollow = (): void => {
  localIsFollowing.value = !localIsFollowing.value
  emit('follow-change', localIsFollowing.value)
}

const handleLike = (): void => {
  localIsLiked.value = !localIsLiked.value
  localLikeCount.value += localIsLiked.value ? 1 : -1
  emit('like', localLikeCount.value, localIsLiked.value)
}

const commentPost = (): void => {
  emit('comment')
}

const sharePost = (): void => {
  emit('share')
}

const toggleExpand = (): void => {
  showFull.value = !showFull.value
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
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
}

.post-actions {
  display: flex;
  justify-content: space-around;
  border-top: 1px solid #f5f5f5;
  padding-top: 10px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 16px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-item:hover {
  color: #ff6b6b;
  transform: scale(1.05);
}

.action-item.liked {
  color: #ff6b6b;
}
</style>
