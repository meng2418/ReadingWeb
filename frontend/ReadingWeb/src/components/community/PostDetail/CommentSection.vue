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
        v-for="comment in comments"
        :key="comment.id"
        :comment="comment"
        @add-reply="handleAddReply"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import CommentItem from './CommentItem.vue'
import DefaultAvatar from '@/img/avatar.jpg'

interface Reply {
  id: number
  author: {
    name: string
    avatar: string
  }
  content: string
  timestamp: string
  likes?: number
  replies?: Reply[]
}

interface Comment {
  id: number
  author: {
    name: string
    avatar: string
  }
  content: string
  timestamp: string
  likes: number
  replies: Reply[]
}

const currentUser = ref({
  name: 'Current User',
  avatar: DefaultAvatar,
})

const comments = ref<Comment[]>([
  {
    id: 1,
    author: {
      name: '爱丽儿',
      avatar: DefaultAvatar,
    },
    content: '谢谢你，写下这些。',
    timestamp: '2 hours ago',
    likes: 15,
    replies: [
      {
        id: 101,
        author: {
          name: '回南天',
          avatar: DefaultAvatar,
        },
        content: '写得真好。',
        timestamp: '1 hour ago',
        likes: 5,
        replies: [
          // 示例数据：即使有深层数据，新逻辑下新回复也会添加到 Level 1 的 replies 中
          // 且 CommentItem 组件会隐藏这些深层回复的渲染
          {
            id: 1001,
            author: { name: '三毛', avatar: DefaultAvatar },
            content: '很有共鸣。',
            timestamp: '30 minutes ago',
            likes: 0,
            replies: [],
          },
        ],
      },
    ],
  },
  {
    id: 2,
    author: {
      name: '西红柿',
      avatar: DefaultAvatar,
    },
    content: '还是应了那句话，好人不长命，祸害遗千年。',
    timestamp: '5 hours ago',
    likes: 8,
    replies: [],
  },
])

const input = ref('')
const sendComment = () => {
  const text = input.value.trim()
  if (!text) return

  const newComment: Comment = {
    id: Date.now(),
    author: {
      name: currentUser.value.name,
      avatar: currentUser.value.avatar,
    },
    content: text,
    timestamp: '刚刚',
    likes: 0,
    replies: [],
  }
  comments.value.unshift(newComment)
  input.value = ''
}

// 辅助函数：递归查找 ID 是否存在于列表中
function findInTree(list: Reply[] | undefined, targetId: number): boolean {
  if (!list || list.length === 0) return false
  for (const item of list) {
    if (item.id === targetId) return true
    if (item.replies && findInTree(item.replies, targetId)) return true
  }
  return false
}

// 修改后的回复处理逻辑：所有子回复都展平添加到一级评论的 replies 中
const handleAddReply = (payload: { parentId: number; content: string }) => {
  const newReply: Reply = {
    id: Date.now(),
    author: { name: currentUser.value.name, avatar: currentUser.value.avatar },
    content: payload.content,
    timestamp: '刚刚',
    likes: 0,
    replies: [],
  }

  let posted = false
  for (const comment of comments.value) {
    // 1. 如果是直接回复一级评论
    if (comment.id === payload.parentId) {
      if (!comment.replies) comment.replies = []
      comment.replies.push(newReply)
      posted = true
      break
    }

    // 2. 如果是回复该一级评论下的子评论（任意深度）
    // 我们的目标是把新回复加到一级评论的 replies 列表里，成为兄弟节点（扁平化）
    if (findInTree(comment.replies, payload.parentId)) {
      if (!comment.replies) comment.replies = []
      comment.replies.push(newReply)
      posted = true
      break
    }
  }

  if (!posted) {
    // 如果未找到父级（例如已被删除），作为新的一级评论添加
    const newRootComment: Comment = {
      ...newReply,
      likes: 0,
      replies: [],
    }
    comments.value.unshift(newRootComment)
  }
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
