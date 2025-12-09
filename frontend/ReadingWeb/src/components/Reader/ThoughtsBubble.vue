<script setup lang="ts">
import { ref } from 'vue'
import {
  X,
  Heart,
  MessageCircle,
  Copy,
  Highlighter,
  Activity,
  Underline,
  Send,
} from 'lucide-vue-next'
import type { Comment } from '@/components/Reader/types'

const props = defineProps<{
  isOpen: boolean
  comments: Comment[]
  isDarkMode: boolean
  quoteText: string
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'action', action: string): void
  (e: 'submit', text: string): void
}>()

const inputText = ref('')

const handleSend = () => {
  if (inputText.value.trim()) {
    emit('submit', inputText.value)
    inputText.value = ''
  }
}
</script>

<template>
  <div v-if="isOpen" class="modal-overlay">
    <!-- Backdrop -->
    <div class="backdrop" @click="$emit('close')"></div>

    <!-- Floating Card -->
    <div class="bubble-card" :class="{ 'dark-mode': isDarkMode }">
      <!-- Toolbar -->
      <div class="toolbar">
        <div class="toolbar-actions">
          <button @click="$emit('action', 'copy')" class="icon-btn" title="复制">
            <Copy :size="18" />
          </button>
          <div class="divider"></div>
          <button
            @click="$emit('action', 'marker')"
            class="icon-btn highlight-btn-yellow"
            title="马克笔"
          >
            <Highlighter :size="18" />
          </button>
          <button
            @click="$emit('action', 'wave')"
            class="icon-btn highlight-btn-red"
            title="波浪线"
          >
            <Activity :size="18" />
          </button>
          <button @click="$emit('action', 'line')" class="icon-btn highlight-btn-blue" title="直线">
            <Underline :size="18" />
          </button>
        </div>
        <button @click="$emit('close')" class="close-btn">
          <X :size="20" />
        </button>
      </div>

      <!-- Quote Context -->
      <div class="quote-section">
        <div class="quote-text">"{{ quoteText }}"</div>
      </div>

      <!-- Comments List -->
      <div class="comments-list">
        <div v-if="comments.length > 0" class="list-header">想法 ({{ comments.length }})</div>

        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="avatar">
            {{ comment.avatar }}
          </div>
          <div class="comment-body">
            <div class="comment-meta">
              <span class="username">{{ comment.username }}</span>
              <span class="date">{{ comment.date }}</span>
            </div>
            <p class="comment-content">{{ comment.content }}</p>
            <div class="comment-actions">
              <button class="action-link like-link">
                <Heart :size="12" /> {{ comment.likes }}
              </button>
              <button class="action-link reply-link"><MessageCircle :size="12" /> 回复</button>
            </div>
          </div>
        </div>

        <div v-if="comments.length === 0" class="empty-state">
          <MessageCircle :size="32" class="empty-icon" />
          <p>暂无</p>
        </div>
      </div>

      <!-- Input Area -->
      <div class="input-section">
        <div class="input-wrapper">
          <textarea
            v-model="inputText"
            placeholder="分享你的想法..."
            class="thought-input"
          ></textarea>
          <div class="input-footer">
            <button
              @click="handleSend"
              :disabled="!inputText.trim()"
              class="send-btn"
              :class="{ active: inputText.trim(), disabled: !inputText.trim() }"
            >
              <span>发布</span>
              <Send :size="14" />
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 50;
  display: flex;
  align-items: center;
  justify-content: center;
}

.backdrop {
  position: absolute;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(4px);
}

.bubble-card {
  position: relative;
  width: 100%;
  max-width: 32rem; /* max-w-lg */
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  border-radius: 1rem;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  background-color: white;
  border: 1px solid #f3f4f6;
  color: #1f2937;
  transform: scale(1);
  opacity: 1;
  /* Simple entry animation placeholder - Vue Transition would be better */
  animation: fadeIn 0.2s ease-out;
}

.bubble-card.dark-mode {
  background-color: #18181b;
  border-color: #3f3f46;
  color: #e5e7eb;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

/* Toolbar */
.toolbar {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #f3f4f6;
  background-color: rgba(249, 250, 251, 0.8);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dark-mode .toolbar {
  background-color: rgba(39, 39, 42, 0.8);
  border-color: #3f3f46;
}

.toolbar-actions {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.icon-btn {
  padding: 0.5rem;
  border-radius: 0.5rem;
  background: transparent;
  border: none;
  cursor: pointer;
  color: #6b7280;
  transition: background-color 0.2s;
}

.dark-mode .icon-btn {
  color: #374151;
}

.icon-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
}
.dark-mode .icon-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.highlight-btn-yellow {
  color: #ca8a04;
}
.dark-mode .highlight-btn-yellow {
  color: #eab308;
}

.highlight-btn-red {
  color: #f43f5e;
}
.highlight-btn-blue {
  color: #0ea5e9;
}

.divider {
  width: 1px;
  height: 1rem;
  background-color: #d1d5db;
  margin: 0 0.25rem;
}
.dark-mode .divider {
  background-color: #52525b;
}

.close-btn {
  padding: 0.5rem;
  border-radius: 9999px;
  background: transparent;
  border: none;
  cursor: pointer;
  color: #9ca3af;
}
.close-btn:hover {
  color: #4b5563;
}
.dark-mode .close-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

/* Quote Section */
.quote-section {
  padding: 1rem;
  border-bottom: 1px solid #f3f4f6;
}
.dark-mode .quote-section {
  border-color: #27272a;
}

.quote-text {
  font-size: 0.875rem;
  font-family: serif;
  font-style: italic;
  opacity: 0.7;
  border-left: 4px solid #3b82f6;
  padding-left: 0.75rem;
  line-height: 1.625;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Comments List */
.comments-list {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
}

.list-header {
  font-size: 0.75rem;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  opacity: 0.4;
  margin-bottom: 0.5rem;
}

.comment-item {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1rem;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.avatar {
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  background: linear-gradient(to bottom right, #60a5fa, #6366f1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: bold;
  flex-shrink: 0;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.comment-body {
  flex: 1;
}

.comment-meta {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 0.25rem;
}

.username {
  font-size: 0.875rem;
  font-weight: 500;
  opacity: 0.9;
}
.date {
  font-size: 0.625rem;
  opacity: 0.4;
}

.comment-content {
  font-size: 0.875rem;
  line-height: 1.6;
  opacity: 0.8;
  margin-bottom: 0.5rem;
}

.comment-actions {
  display: flex;
  gap: 1rem;
  font-size: 0.75rem;
  opacity: 0.5;
}

.action-link {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  color: inherit;
  transition: color 0.2s;
}

.like-link:hover {
  color: #ef4444;
}
.reply-link:hover {
  color: #3b82f6;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  opacity: 0.4;
  padding: 2rem 0;
}
.empty-icon {
  margin-bottom: 0.5rem;
}

/* Input Section */
.input-section {
  padding: 1rem;
  border-top: 1px solid #f3f4f6;
  background-color: #f9fafb;
}
.dark-mode .input-section {
  background-color: rgba(39, 39, 42, 0.5);
  border-color: #3f3f46;
}

.input-wrapper {
  border-radius: 0.75rem;
  border: 1px solid #e5e7eb;
  background-color: white;
  transition: all 0.2s;
}
.dark-mode .input-wrapper {
  background-color: #18181b;
  border-color: #3f3f46;
}
.input-wrapper:focus-within {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1); /* ring-blue-100 */
}

.thought-input {
  width: 100%;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  border: none;
  background: transparent;
  resize: none;
  min-height: 100px;
  outline: none;
  color: inherit;
}
.thought-input::placeholder {
  color: #9ca3af;
}
.dark-mode .thought-input::placeholder {
  color: #52525b;
}

.input-footer {
  display: flex;
  justify-content: flex-end;
  padding: 0.5rem;
  border-top: 1px solid transparent;
}

.send-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.375rem 1rem;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  border: none;
  transition: all 0.2s;
}

.send-btn.active {
  background-color: #2563eb;
  color: white;
  box-shadow: 0 4px 6px -1px rgba(59, 130, 246, 0.3);
  cursor: pointer;
}
.send-btn.active:hover {
  background-color: #1d4ed8;
}

.send-btn.disabled {
  background-color: #f3f4f6;
  cursor: not-allowed;
}
.dark-mode .send-btn.disabled {
  background-color: #27272a;
}
</style>
