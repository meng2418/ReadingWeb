<!-- CommentSection.vue -->
<template>
  <section class="comment-section">
    <!-- Input Area -->
    <div class="comment-input-area">
      <img :src="currentUser.avatar" :alt="currentUser.name" class="user-avatar" />
      <div class="input-container">
        <textarea v-model="input" placeholder="分享你的观点..." class="comment-textarea"></textarea>

        <div class="post-button-container">
          <button class="post-button" :disabled="!input.trim()" @click="sendComment">
            发送评论
          </button>
        </div>
      </div>
    </div>

    <!-- Comment List -->
    <div class="comment-list">
      <CommentItem
        v-for="comment in formattedComments"
        :key="comment.id"
        :comment="comment"
        :post-id="postId"
        @add-reply="handleAddReply"
        @like="handleCommentLike"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import CommentItem from './CommentItem.vue'
import DefaultAvatar from '@/img/avatar.jpg'
import { useUserStore } from '@/stores/user'

const props = defineProps<{
  postId: string | number
  initialComments: any[] // 接收来自 PostDetailPage 的原始接口数据
}>()

const emit = defineEmits<{
  (e: 'add-reply', payload: { parentId: number; content: string }): void // 通知父组件添加回复到本地数据
  (e: 'publish-comment', content: string): void
}>()

// CommentSection.vue 里的核心逻辑
const formattedComments = computed(() => {
  // props.initialComments 是从 API 传进来的数组
  return props.initialComments.map((item) => ({
    id: item.id,
    author: {
      name: item.username, // <--- 必须确认 api/post.ts 里叫 username
      avatar: item.avatar || DefaultAvatar,
    },
    content: item.content,
    timestamp: item.commentTime, // <--- 必须确认 api/post.ts 里叫 commentTime
    likes: item.likeCount || 0,
    isLiked: item.isLiked || false, // 添加点赞状态
    replies: (item.replies || []).map((r: any) => ({
      id: r.id,
      author: { name: r.username, avatar: r.avatar || DefaultAvatar },
      content: r.content,
      timestamp: r.commentTime,
      likes: r.likeCount || 0,
      isLiked: r.isLiked || false, // 子评论也添加点赞状态
    })),
  }))
})

const userStore = useUserStore()
const currentUser = computed(() => ({
  name: userStore.userInfo.name || '游客',
  avatar: userStore.userInfo.avatar || DefaultAvatar,
}))

const input = ref('')
const isSubmittingReply = ref(false)

// 4. 发送评论逻辑
const sendComment = () => {
  const text = input.value.trim()
  if (!text) return

  // 触发父组件事件
  emit('publish-comment', text)
}

// 暴露给父组件的方法
const clearInput = () => {
  input.value = ''
}

defineExpose({
  clearInput,
})

// 5. 回复逻辑
const handleAddReply = (payload: { parentId: number; content: string }) => {
  // 直接通知父组件，让父组件去调接口并更新 comments 数组
  emit('add-reply', payload)
}

// 6. 处理评论点赞
const handleCommentLike = (payload: { commentId: number; isLiked: boolean; likeCount: number }) => {
  // 更新本地评论数据
  const comment = props.initialComments.find((c: any) => (c.id || c.commentId) === payload.commentId)
  if (comment) {
    comment.likeCount = payload.likeCount
    comment.isLiked = payload.isLiked
  }
  // 同时检查子评论
  props.initialComments.forEach((c: any) => {
    if (c.replies) {
      const reply = c.replies.find((r: any) => (r.id || r.commentId) === payload.commentId)
      if (reply) {
        reply.likeCount = payload.likeCount
        reply.isLiked = payload.isLiked
      }
    }
  })
}
</script>

<style scoped>
/* 主评论区容器 */
.comment-section {
  background-color: #fff;
  border-radius: 1.25rem; /* 20px */
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  border: 1px solid #f1f5f9; /* slate-100 */
  padding: 1.5rem; /* 24px */
}
/* 评论输入区域 */
.comment-input-area {
  display: flex;
  gap: 1rem; /* 16px */
  margin-bottom: 10px;
  align-items: flex-start;
}

/* 用户头像 */
.user-avatar {
  width: 2.5rem; /* 40px */
  height: 2.5rem; /* 40px */
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #e2e8f0; /* slate-200 */
  display: none;
}

/* 输入容器 */
.input-container {
  flex: 1;
  position: relative;
}

/* 评论文本域 */
.comment-textarea {
  box-sizing: border-box;
  width: 100%;
  min-height: 6.25rem; /* 100px */
  padding: 1rem; /* 16px */
  padding-right: 3rem; /* 48px */
  border-radius: 1rem; /* 16px */
  border: 1px solid #e2e8f0; /* slate-200 */
  resize: vertical;
  font-size: 0.875rem; /* 14px */
  transition: all 0.2s ease;
}

.comment-textarea:focus {
  outline: none;
  border-color: #3b82f6; /* brand-500 (blue-500) */
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1); /* brand-100 */
}
/* 发布按钮容器 */
.post-button-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 0.5rem; /* 8px */
}

/* 发布按钮 */
.post-button {
  background-color: #2563eb; /* brand-600 (blue-600) */
  color: #fff;
  padding: 0.5rem 1.5rem; /* 8px 24px */
  border-radius: 0.5rem; /* 8px */
  font-weight: 500;
  font-size: 0.875rem; /* 14px */
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem; /* 8px */
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.post-button:hover {
  background-color: #1d4ed8; /* brand-700 (blue-700) */
  box-shadow:
    0 4px 6px -1px rgba(0, 0, 0, 0.1),
    0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.post-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: none;
}

/* 评论列表 */
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem; /* 8px */
}
.reply-input-container {
  margin-left: 48px;
  margin-top: 8px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.reply-textarea {
  width: 100%;
  min-height: 60px;
  padding: 8px 10px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  resize: vertical;
}

.reply-textarea:focus {
  border-color: #0f172a;
}

.reply-submit {
  align-self: flex-end;
  padding: 4px 12px;
  font-size: 14px;
  border-radius: 6px;
  background-color: #0f172a;
  color: white;
  cursor: pointer;
  border: none;
}

/* 动画效果 */
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.animate-spin {
  animation: spin 1s linear infinite;
}

/* 响应式调整 */
@media (min-width: 768px) {
  .comment-section {
    padding: 2rem; /* 32px */
  }
  .user-avatar {
    display: block;
  }
  .comment-textarea {
    font-size: 1rem; /* 16px */
  }
}
</style>
