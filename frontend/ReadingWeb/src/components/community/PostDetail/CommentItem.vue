<template>
  <div :class="['comment-item', isReply ? 'reply-item' : '']">
    <img :src="comment.author.avatar" :alt="comment.author.name" class="commenter-avatar" />
    <div class="comment-content">
      <div class="comment-card">
        <div class="comment-header">
          <span class="commenter-name">{{ comment.author.name }}</span>
          <span class="comment-time">{{ comment.timestamp }}</span>
        </div>
        <p class="comment-text">{{ comment.content }}</p>
      </div>

      <div class="comment-actions">
        <button
          @click="toggleLike"
          :class="['action-button', 'like-button', isLiked ? 'liked' : '']"
        >
          <Heart :class="isLiked ? 'fill-current' : ''" />
          <span v-if="likeCount > 0" class="like-count">{{ likeCount }}</span>
        </button>

        <button class="action-button reply-button" @click="toggleReplyBox">
          <el-icon><Comment /></el-icon>
          回复
        </button>
      </div>

      <!-- 内联回复框 -->
      <div v-if="showReplyBox" class="inline-reply-box">
        <textarea
          ref="replyTextareaRef"
          v-model="replyText"
          class="inline-reply-textarea"
          :placeholder="isReply ? '' : `回复 ${comment.author.name}：`"
        ></textarea>
        <div class="inline-reply-actions">
          <button class="btn-cancel" @click="cancelReply">取消</button>
          <button class="btn-submit" :disabled="!replyText.trim()" @click="submitReply">
            发表回复
          </button>
        </div>
      </div>

      <!-- 
        核心修改：添加 !isReply 判断
        只有一级评论（!isReply）才渲染子评论容器
        这样可以防止无限嵌套，强制视觉上只有两级（根评论 -> 子评论列表）
        二级评论的回复在视觉上会作为该列表的兄弟元素由父组件处理，或者通过文本 "回复@..." 区分
      -->
      <div
        v-if="!isReply && comment.replies && comment.replies.length > 0"
        class="replies-container"
      >
        <button
          v-if="!showReplies"
          @click="setShowReplies(true)"
          class="toggle-replies-button show-replies"
        >
          展开 {{ comment.replies.length }} 回复
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="14"
            height="14"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="chevron-icon"
          >
            <polyline points="6 9 12 15 18 9"></polyline>
          </svg>
        </button>

        <div v-else :class="['nested-replies', `nested-replies-${comment.id}`]">
          <!-- 递归渲染子项，标记 is-reply=true -->
          <CommentItem
            v-for="reply in comment.replies"
            :key="reply.id"
            :comment="reply"
            :is-reply="true"
            @add-reply="forwardAddReply"
          />
          <button @click="setShowReplies(false)" class="toggle-replies-button hide-replies">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="14"
              height="14"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
              class="chevron-icon"
            >
              <polyline points="18 15 12 9 6 15"></polyline>
            </svg>
            隐藏回复
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineOptions({ name: 'CommentItem' })
import { ref, nextTick } from 'vue'
import { Comment, Share } from '@element-plus/icons-vue'
import { Heart } from 'lucide-vue-next'

const emit = defineEmits(['add-reply', 'like'])
const props = defineProps<{
  comment: {
    id: number
    author: {
      name: string
      avatar: string
    }
    content: string
    timestamp: string
    likes?: number
    replies?: any[]
  }
  isReply?: boolean
}>()

// 本地状态
const isLiked = ref(false)
const likeCount = ref(props.comment.likes ?? 0)
const showReplies = ref(false)
const showReplyBox = ref(false)
const replyText = ref('')
const replyTextareaRef = ref<HTMLTextAreaElement | null>(null)

const setShowReplies = (value: boolean) => {
  showReplies.value = value
  if (value) {
    const el = document.querySelector(`.nested-replies-${props.comment.id}`)
    if (el) {
      el.classList.add('animate-in')
      setTimeout(() => el.classList.remove('animate-in'), 300)
    }
  }
}

const toggleLike = () => {
  isLiked.value = !isLiked.value
  likeCount.value = Math.max(0, isLiked.value ? likeCount.value + 1 : likeCount.value - 1)
  emit('like', { commentId: props.comment.id, isLiked: isLiked.value })
}

const toggleReplyBox = () => {
  if (showReplyBox.value) {
    // 关闭
    showReplyBox.value = false
    replyText.value = ''
  } else {
    // 打开
    showReplyBox.value = true

    // 核心修改：如果是二级评论（回复项），自动预填 "回复@xxx:"
    if (props.isReply) {
      replyText.value = `回复@${props.comment.author.name}:`
    } else {
      replyText.value = ''
    }

    // 聚焦输入框
    nextTick(() => {
      replyTextareaRef.value?.focus()
    })
  }
}

const cancelReply = () => {
  replyText.value = ''
  showReplyBox.value = false
}

// 当在当前项提交回复
const submitReply = () => {
  const text = replyText.value.trim()
  if (!text) return
  emit('add-reply', { parentId: props.comment.id, content: text })
  replyText.value = ''
  showReplyBox.value = false
}

// 子项发来的 add-reply 事件，原样向上转发
const forwardAddReply = (payload: any) => {
  emit('add-reply', payload)
}
</script>

<style scoped>
/* 评论项容器 */
.comment-item {
  display: flex;
  gap: 0.75rem; /* 12px */
  margin-top: 1.5rem; /* 24px */
  animation: fadeIn 0.3s ease-out;
  font-family: '';
}

/* 回复项样式 */
.reply-item {
  margin-top: 1rem; /* 16px */
  padding-left: 1rem; /* 16px */
  border-left: 2px solid #f1f5f9; /* slate-100 */
}

/* 评论者头像 */
.commenter-avatar {
  width: 2rem; /* 32px */
  height: 2rem; /* 32px */
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.reply-item .commenter-avatar {
  width: 2rem; /* 32px */
  height: 2rem; /* 32px */
}

/* 评论内容区域 */
.comment-content {
  flex: 1;
}

/* 评论卡片 */
.comment-card {
  background-color: #f8fafc; /* slate-50 */
  border-radius: 1.25rem; /* 20px */
  padding: 1rem; /* 16px */
}

.reply-item .comment-card {
  background-color: rgba(248, 250, 252, 0.6); /* slate-50 with opacity */
}

/* 评论头部 */
.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.25rem; /* 4px */
}

/* 评论者姓名 */
.commenter-name {
  font-weight: 600;
  color: #0f172a; /* slate-900 */
  font-size: 16px;
}

/* 评论时间 */
.comment-time {
  font-size: 0.75rem; /* 12px */
  color: #94a3b8; /* slate-400 */
}

/* 评论文本 */
.comment-text {
  color: #334155; /* slate-700 */
  font-size: 0.875rem; /* 14px */
  line-height: 1.625; /* leading-relaxed */
}

/* 评论操作按钮区域 */
.comment-actions {
  display: flex;
  align-items: center;
  gap: 1.5rem; /* 24px */
  margin-top: 0.5rem; /* 8px */
  padding-left: 0.5rem; /* 8px */
}

/* 操作按钮通用样式 */
.action-button {
  display: flex;
  align-items: center;
  gap: 0.25rem; /* 4px */
  background: none;
  border: none;
  font-size: 0.75rem; /* 12px */
  font-weight: 500;
  cursor: pointer;
  transition: color 0.2s ease;
  padding: 0.25rem 0;
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

/* 点赞按钮 */
.like-button {
  color: #64748b; /* slate-500 */
}

.like-button:hover {
  color: #ef4444; /* red-500 */
}

.like-button.liked {
  color: #ef4444; /* red-500 */
}

.like-count {
  margin-left: 0.125rem; /* 2px */
}

/* 回复按钮 */
.reply-button {
  color: #64748b; /* slate-500 */
}

.reply-button:hover {
  color: #3b82f6; /* brand-600 (blue-600) */
}

/* 回复容器 */
.replies-container {
  margin-top: 0.75rem; /* 12px */
}

/* 切换回复按钮 */
.toggle-replies-button {
  background: none;
  border: none;
  font-size: 0.75rem; /* 12px */
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.25rem; /* 4px */
  transition: color 0.2s ease;
  padding: 0.25rem 0;
}

/* 显示回复按钮 */
.show-replies {
  color: #3b82f6; /* brand-600 (blue-600) */
}

.show-replies:hover {
  color: #2563eb; /* brand-700 (blue-700) */
  text-decoration: underline;
}

/* 隐藏回复按钮 */
.hide-replies {
  color: #94a3b8; /* slate-400 */
  margin-left: 0.5rem; /* 8px */
}

.hide-replies:hover {
  color: #64748b; /* slate-600 */
}

/* 嵌套回复区域 */
.nested-replies {
  margin-top: 0.75rem; /* 12px */
  animation: fadeInSlideIn 0.3s ease-out;
}

/* 新增内联回复样式，避免溢出并与卡片风格一致 */
.inline-reply-box {
  margin-top: 8px;
  margin-left: 48px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-width: calc(100% - 48px);
}

.inline-reply-textarea {
  width: 100%;
  min-height: 56px;
  padding: 10px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  resize: vertical;
  font-size: 14px;
  box-sizing: border-box;
}

.inline-reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.btn-cancel {
  background: transparent;
  border: none;
  color: #64748b;
  padding: 6px 10px;
  border-radius: 6px;
  cursor: pointer;
}

.btn-submit {
  background: #2563eb;
  color: #fff;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes fadeInSlideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式调整 */
@media (min-width: 768px) {
  .comment-item {
    gap: 1rem; /* 16px */
  }

  .reply-item {
    padding-left: 3rem; /* 48px */
  }

  .comment-card {
    padding: 1rem; /* 16px */
  }

  .comment-text {
    font-size: 1rem; /* 16px */
  }
}
</style>
