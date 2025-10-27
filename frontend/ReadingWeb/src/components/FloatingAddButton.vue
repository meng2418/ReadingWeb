<template>
  <div>
    <!-- 圆形加号按钮 -->
    <button
      class="floating-add-btn"
      @click="toggleEditor"
      aria-label="新建帖子"
    >
      +
    </button>

    <!-- 发帖悬浮窗 -->
    <div v-if="showEditor" class="editor-overlay" @click.self="closeEditor">
      <div class="editor-window">
        <h3>发布新帖子</h3>
        <textarea
          v-model="postContent"
          placeholder="写点什么吧..."
          class="editor-textarea"
        ></textarea>
        <div class="editor-actions">
          <button class="cancel-btn" @click="closeEditor">取消</button>
          <button class="submit-btn" @click="submitPost">发布</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const showEditor = ref(false)
const postContent = ref('')

const toggleEditor = () => {
  showEditor.value = !showEditor.value
}

const closeEditor = () => {
  showEditor.value = false
}

const submitPost = () => {
  if (!postContent.value.trim()) return
  console.log('发布内容：', postContent.value)
  postContent.value = ''
  showEditor.value = false
}
</script>

<style scoped>
/* --- 浮动加号按钮 --- */
.floating-add-btn {
  position: fixed;
  bottom: 20px;
  right: 70px; /* 位于 BackToTop 左侧 */
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: #007c27;
  color: white;
  font-size: 24px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.25);
  transition: background 0.3s, transform 0.2s;
}

.floating-add-btn:hover {
  background: #1ad6a1;
  transform: scale(1.05);
}

/* --- 发帖悬浮层 --- */
.editor-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

/* --- 发帖窗口 --- */
.editor-window {
  background: white;
  border-radius: 12px;
  padding: 20px;
  width: 420px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  animation: fadeIn 0.2s ease;
}

.editor-window h3 {
  margin-top: 0;
  margin-bottom: 12px;
  color: #007c27;
  font-weight: 600;
}

.editor-textarea {
  width: 100%;
  height: 120px;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 8px;
  font-size: 15px;
  resize: none;
  outline: none;
  transition: border-color 0.2s;
}

.editor-textarea:focus {
  border-color: #1ad6a1;
}

/* --- 底部操作按钮 --- */
.editor-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
  gap: 10px;
}

.cancel-btn,
.submit-btn {
  padding: 6px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: 0.2s;
}

.cancel-btn {
  background: #ccc;
  color: white;
}

.cancel-btn:hover {
  background: #999;
}

.submit-btn {
  background: #007c27;
  color: white;
}

.submit-btn:hover {
  background: #1ad6a1;
}

/* 动画 */
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
</style>
