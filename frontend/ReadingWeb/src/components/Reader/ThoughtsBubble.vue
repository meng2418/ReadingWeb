<template>
  <teleport to="body">
    <div v-if="isOpen" class="thoughts-bubble-overlay">
      <!-- 背景遮罩 -->
      <div class="overlay-backdrop" @click="onClose"></div>

      <!-- 气泡卡片 -->
      <div class="thoughts-card" :class="{ 'dark-mode': isDarkMode }">
        <!-- 头部区域 -->
        <div class="card-header">
          <div class="quote-text">{{ quoteText }}</div>
          <button class="close-btn" @click="onClose">
            <X :size="16" />
          </button>
        </div>

        <!-- 内容区域 -->
        <div class="card-content">
          <div class="community-label">热门想法 {{ comments.length }}</div>

          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="avatar">
              {{ comment.avatar }}
            </div>
            <div class="comment-content">
              <div class="comment-header">
                <span class="username">{{ comment.username }}</span>
                <span class="date">{{ comment.date }}</span>
              </div>
              <p class="comment-text">{{ comment.content }}</p>
              <div class="comment-actions">
                <button class="action-btn like-btn">
                  <Heart :size="12" /> {{ comment.likes }}
                </button>
                <button class="action-btn reply-btn"><MessageCircle :size="12" /> Reply</button>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="input-area">
          <input type="text" placeholder="分享你的想法..." class="thought-input" />
        </div>
      </div>
    </div>
  </teleport>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'
import { X, Heart, MessageCircle } from 'lucide-vue-next'

// 类型定义
interface Comment {
  id: string
  avatar: string
  username: string
  date: string
  content: string
  likes: number
}

// Props 定义
const props = defineProps<{
  isOpen: boolean
  onClose: () => void
  comments: Comment[]
  isDarkMode: boolean
  quoteText: string
}>()
</script>

<style scoped>
/* 遮罩层 */
.thoughts-bubble-overlay {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 50;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}

/* 背景遮罩 */
.overlay-backdrop {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background-color: rgba(0, 0, 0, 0.05);
  pointer-events: auto;
}

/* 气泡卡片 */
.thoughts-card {
  pointer-events: auto;
  width: 100%;
  max-width: 28rem; /* max-w-md */
  max-height: 70vh;
  display: flex;
  flex-direction: column;
  border-radius: 1rem;
  box-shadow:
    0 10px 25px -5px rgba(0, 0, 0, 0.1),
    0 8px 10px -6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
  transform: scale(1);
  opacity: 1;
  background-color: white;
  border: 1px solid #f3f4f6; /* border-gray-100 */
  color: #1f2937; /* text-gray-800 */
}

/* 深色模式卡片 */
.thoughts-card.dark-mode {
  background-color: #18181b; /* bg-zinc-900 */
  border-color: #3f3f46; /* border-zinc-700 */
  color: #e5e7eb; /* text-gray-200 */
}

/* 卡片头部 */
.card-header {
  padding: 1rem;
  border-bottom: 1px solid;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
  background-color: #f9fafb; /* bg-gray-50 */
  border-color: #f3f4f6; /* border-gray-100 */
}

.dark-mode .card-header {
  background-color: rgba(39, 39, 42, 0.5); /* bg-zinc-800/50 */
  border-color: #3f3f46; /* border-zinc-700 */
}

/* 引用文本 */
.quote-text {
  font-size: 0.875rem; /* text-sm */
  font-family: serif;
  font-style: italic;
  opacity: 0.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  border-left: 2px solid #3b82f6; /* border-blue-500 */
  padding-left: 0.75rem;
  flex: 1;
}

/* 关闭按钮 */
.close-btn {
  padding: 0.25rem;
  border-radius: 9999px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  border: none;
  background-color: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  background-color: rgba(0, 0, 0, 0.1);
}

.dark-mode .close-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

/* 卡片内容 */
.card-content {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  background-color: rgba(255, 255, 255, 0.5); /* bg-opacity-50 */
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.dark-mode .card-content {
  background-color: rgba(24, 24, 27, 0.5);
}

/* 社区标签 */
.community-label {
  font-size: 0.75rem; /* text-xs */
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  opacity: 0.4;
  margin-bottom: 0.5rem;
}

/* 评论项 */
.comment-item {
  display: flex;
  gap: 0.75rem;
}

/* 头像 */
.avatar {
  width: 2rem; /* w-8 */
  height: 2rem; /* h-8 */
  border-radius: 50%;
  background: linear-gradient(to bottom right, #60a5fa, #6366f1); /* from-blue-400 to-indigo-500 */
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem; /* text-xs */
  font-weight: bold;
  flex-shrink: 0;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1); /* shadow-sm */
}

/* 评论内容 */
.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 0.25rem;
}

.username {
  font-size: 0.875rem; /* text-sm */
  font-weight: 500;
  opacity: 0.9;
}

.date {
  font-size: 10px; /* text-[10px] */
  opacity: 0.4;
}

.comment-text {
  font-size: 0.875rem; /* text-sm */
  line-height: 1.6; /* leading-relaxed */
  opacity: 0.8;
  margin-bottom: 0.5rem;
}

/* 评论操作 */
.comment-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.75rem; /* text-xs */
  opacity: 0.5;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  border: none;
  background: transparent;
  cursor: pointer;
  transition: color 0.2s ease;
  padding: 0;
}

.like-btn:hover {
  color: #ef4444; /* text-red-500 */
}

.reply-btn:hover {
  color: #3b82f6; /* text-blue-500 */
}

/* 输入区域 */
.input-area {
  padding: 0.75rem;
  border-top: 1px solid;
  border-color: #f3f4f6; /* border-gray-100 */
}

.dark-mode .input-area {
  border-color: #27272a; /* border-zinc-800 */
}

/* 输入框 */
.thought-input {
  width: 100%;
  padding: 0.5rem 1rem;
  border-radius: 9999px;
  font-size: 0.875rem; /* text-sm */
  outline: none;
  transition:
    background-color 0.2s ease,
    box-shadow 0.2s ease;
  border: none;
  background-color: #f3f4f6; /* bg-gray-100 */
  color: inherit;
  box-sizing: border-box;
}

.thought-input::placeholder {
  color: #9ca3af; /* placeholder-gray-400 */
}

.thought-input:focus {
  background-color: white; /* focus:bg-white */
  box-shadow: 0 0 0 1px #e5e7eb; /* focus:ring-1 focus:ring-gray-200 */
}

.dark-mode .thought-input {
  background-color: #27272a; /* bg-zinc-800 */
}

.dark-mode .thought-input::placeholder {
  color: #71717a; /* placeholder-zinc-500 */
}

.dark-mode .thought-input:focus {
  background-color: #3f3f46; /* focus:bg-zinc-700 */
  box-shadow: none;
}
</style>
