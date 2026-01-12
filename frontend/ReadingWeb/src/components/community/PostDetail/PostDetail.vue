<!--PostDetail.vue-->
<template>
  <article class="post-detail">
    <div class="post-content">
      <div class="author-header">
        <div class="author-info">
          <img
            :src="avatarUrl"
            :alt="post.author.authorName"
            class="author-avatar"
            @error="handleAvatarError"
          />
          <div class="author-name-container">
            <h3 class="author-name">{{ post.author.authorName }}</h3>
            <div class="post-meta">
              <span class="post-time">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="12"
                  height="12"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <circle cx="12" cy="12" r="10"></circle>
                  <polyline points="12 6 12 12 16 14"></polyline>
                </svg>
                {{ formatTime(post.publishTime) }}
              </span>
              <span v-if="post.publishLocation" class="post-location">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="12"
                  height="12"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"></path>
                  <circle cx="12" cy="10" r="3"></circle>
                </svg>
                {{ post.publishLocation }}
              </span>
            </div>
          </div>
        </div>
        <button
          v-if="!isOwnPost"
          @click="handleFollow"
          :class="post.isFollowingAuthor ? 'following-button' : 'follow-button'"
        >
          {{ post.isFollowingAuthor ? '已关注' : '关注' }}
        </button>
      </div>

      <h1 class="post-title">{{ post.postTitle }}</h1>

      <div class="post-body">
        <p class="post-paragraph">{{ post.content }}</p>
      </div>

      <div class="tags-container" v-if="post.topics && post.topics.length > 0">
        <span v-for="topic in post.topics" :key="topic" class="tag"> #{{ topic }} </span>
      </div>

      <div class="action-bar">
        <div class="left-actions">
          <!-- Placeholder for like stats if needed -->
        </div>
        <div class="right-actions">
          <button
            class="action-button"
            :class="{ liked: post.isLiked }"
            title="Like"
            @click="handleLike"
          >
            <Heart />
            <span v-if="post.likeCount > 0" class="like-count">{{ post.likeCount }}</span>
          </button>
        </div>
      </div>
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { Heart } from 'lucide-vue-next'
import { useTitle } from '@/stores/useTitle'
import type { PostDetailResponse } from '@/api/post'
import { toggleLikeApi } from '@/api/community'
import { getAvatarUrl, DEFAULT_AVATAR } from '@/utils/defaultImages'

const props = defineProps<{
  post: PostDetailResponse
  currentUserId?: number | null
}>()

// 计算头像URL，使用默认头像
const avatarUrl = computed(() => getAvatarUrl(props.post?.author?.authorAvatar))

// 头像加载失败时使用默认头像
const handleAvatarError = (event: Event) => {
  const img = event.target as HTMLImageElement
  if (img.src !== DEFAULT_AVATAR) {
    img.src = DEFAULT_AVATAR
  }
}
// 格式化时间
const formatTime = (timeStr: string) => {
  const date = new Date(timeStr)
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
}

// 动态页面标题
const pagetitle = computed(() => `${props.post.postTitle} - 帖子详情`)
useTitle(pagetitle)

// 处理关注
// 1. 引入 API
import { followUserApi, unfollowUserApi } from '@/api/userRelations'

// 2. 增加 update-follow 到 emits 中
const emit = defineEmits(['update-like', 'update-follow'])

// 处理关注/取消关注
import { ElMessage } from 'element-plus'
import { getProfileHome } from '@/api/profile'
import { ref, computed, onMounted } from 'vue'

// 判断是否是自己的帖子
const isOwnPost = computed(() => {
  if (!props.currentUserId || !props.post?.author?.authorId) {
    return false
  }
  return props.currentUserId === props.post.author.authorId
})

const handleFollow = async () => {
  const authorId = props.post.author.authorId
  try {
    if (props.post.isFollowingAuthor) {
      await unfollowUserApi(authorId)
      ElMessage.success('已取消关注')
    } else {
      await followUserApi(authorId)
      ElMessage.success('关注成功')
    }
    emit('update-follow', !props.post.isFollowingAuthor)
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  }
}

const handleLike = async () => {
  try {
    // 调用 API
    await toggleLikeApi({
      commentId: 0,
      postId: props.post.postId,
      targetId: props.post.postId, // 点赞帖子时，targetId 就是 postId
      targetType: 'post', // 目标类型为 post
    })

    // 2. 不要直接修改 props.post！
    // 计算新状态
    const newLikedStatus = !props.post.isLiked
    const newCount = props.post.likeCount + (newLikedStatus ? 1 : -1)

    // 3. 传给父组件处理
    emit('update-like', {
      isLiked: newLikedStatus,
      likeCount: newCount,
    })
  } catch (error) {
    console.error('点赞失败', error)
  }
}

// 响应式状态
const isFollowing = ref(false)
</script>

<style scoped>
/* 文章容器 */
.post-detail {
  background-color: #fff;
  border-radius: 1.25rem; /* 20px */
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  border: 1px solid #f1f5f9; /* slate-100 */
  overflow: hidden;
  margin-bottom: 2rem; /* 32px */
}

/* 文章内容区 */
.post-content {
  padding: 1.5rem; /* 24px */
}

/* 作者头部信息 */
.author-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.5rem; /* 24px */
}

/* 作者信息（头像 + 名称） */
.author-info {
  display: flex;
  align-items: center;
  gap: 1rem; /* 16px */
}

/* 作者头像 */
.author-avatar {
  width: 3rem; /* 48px */
  height: 3rem; /* 48px */
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow:
    0 4px 6px -1px rgba(0, 0, 0, 0.1),
    0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

/* 作者名称容器 */
.author-name-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* 作者名称 */
.author-name {
  font-weight: 700;
  color: #0f172a; /* slate-900 */
  font-size: 1.125rem; /* 18px */
  line-height: 1.25; /* leading-tight */
}

/* 文章元信息（时间、编辑状态、位置） */
.post-meta {
  display: flex;
  align-items: center;
  font-size: 0.75rem; /* 12px */
  color: #64748b; /* slate-500 */
  gap: 0.5rem; /* 8px */
  margin-top: 0.125rem; /* 2px */
}

/* 文章元信息中的各个部分 */
.post-time,
.post-location {
  display: flex;
  align-items: center;
  gap: 0.25rem; /* 4px */
}

.post-edited {
  font-size: 0.75rem;
}

/* 关注按钮 */
.follow-button {
  padding: 0.375rem 1.25rem; /* 6px 20px */
  border-radius: 9999px; /* rounded-full */
  font-size: 0.875rem; /* 14px */
  font-weight: 500;
  background-color: var(--primary-pink);
  color: var(--sun-back);
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 1px 2px var(--shadow-color);
}

.follow-button:hover {
  background-color: var(--primary-pink-hover);
  box-shadow:
    0 4px 6px -1px rgba(0, 0, 0, 0.1),
    0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

/* 已关注按钮 */
.following-button {
  padding: 0.375rem 1.25rem; /* 6px 20px */
  border-radius: 9999px; /* rounded-full */
  font-size: 0.875rem; /* 14px */
  font-weight: 500;
  background-color: #f1f5f9; /* slate-100 */
  color: #475569; /* slate-600 */
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.following-button:hover {
  background-color: #e2e8f0; /* slate-200 */
}

/* 文章标题 */
.post-title {
  font-size: 1.5rem; /* 24px */
  font-weight: 700;
  color: #0f172a; /* slate-900 */
  margin-bottom: 1.5rem; /* 24px */
  line-height: 1.25; /* leading-tight */
  letter-spacing: -0.025em; /* tracking-tight */
}

/* 文章正文容器 */
.post-body {
  color: #334155; /* slate-700 */
  margin-bottom: 2rem; /* 32px */
  font-size: 1.125rem; /* 18px */ /* prose-lg */
  line-height: 1.625; /* leading-relaxed */
}

/* 文章段落 */
.post-paragraph {
  margin-bottom: 1rem; /* 16px */
}

/* 标签容器 */
.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem; /* 8px */
  margin-bottom: 1.5rem; /* 24px */
}

/* 标签样式 */
.tag {
  padding: 0.25rem 0.75rem; /* 4px 12px */
  background-color: rgba(37, 99, 235, 0.1); /* brand-50 */
  color: #2563eb; /* brand-600 */
  font-size: 0.875rem; /* 14px */
  border-radius: 9999px; /* rounded-full */
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.tag:hover {
  background-color: rgba(37, 99, 235, 0.2); /* brand-100 */
}

/* 操作栏 */
.action-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 1.5rem; /* 24px */
  border-top: 1px solid #f1f5f9; /* slate-100 */
}

/* 右侧操作按钮组 */
.right-actions {
  display: flex;
  gap: 0.75rem; /* 12px */
}

/* 操作按钮 */
.action-button {
  color: #94a3b8; /* slate-400 */
  background: none;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s ease;
}

/* 让所有 action-button 内的 SVG 图标保持统一大小 & 颜色继承 */
.action-button svg {
  width: 20px;
  height: 20px;
  stroke: currentColor; /* 让 lucide 图标继承颜色 */
  fill: none;
}

/* element-plus 的图标默认 fill，需要手动改为 inherit */
.action-button .el-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: inherit;
}
/* 点赞后的样式 */
.action-button.liked {
  color: #ff4d4f; /* 红色 */
  transform: scale(1.1); /* 稍微放大点，增加动感 */
}

.action-button.liked svg {
  fill: #ff4d4f; /* 填充红色 */
}

/* 点按反馈 */
.action-button:active {
  transform: scale(0.85);
}

/* 点赞时微动效 */
.action-button .liked {
  animation: pop 0.25s ease;
}
/* 响应式调整 */
@media (min-width: 768px) {
  .post-content {
    padding: 2rem; /* 32px */
  }
  .post-title {
    font-size: 1.875rem; /* 30px */
  }
}
</style>
