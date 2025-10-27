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
        <h3>发布帖子</h3>

        <!-- 标题部分：改为 input，禁止换行 -->
        <input
          v-model="titleContent"
          placeholder="输入标题"
          class="title-input"
          maxlength="20"
        />

        <!-- 正文 -->
        <textarea
          v-model="postContent"
          placeholder="输入正文"
          class="editor-textarea"
        ></textarea>

        <!-- 左下角：添加话题 / 书籍 -->
        <div class="editor-tools">
          <el-icon class="tool-icon" @click="addTopic">
            <ChatLineRound />
          </el-icon>
          <el-icon class="tool-icon" @click="addBook">
            <Notebook />
          </el-icon>
        </div>

        <!-- 操作按钮 -->
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
import { ChatLineRound, Notebook } from '@element-plus/icons-vue'

const showEditor = ref(false)
const titleContent = ref('')
const postContent = ref('')

const toggleEditor = () => {
  showEditor.value = !showEditor.value
}
const closeEditor = () => {
  showEditor.value = false
}
const submitPost = () => {
  if (!postContent.value.trim()) return
  console.log('标题：', titleContent.value)
  console.log('内容：', postContent.value)
  postContent.value = ''
  titleContent.value = ''
  showEditor.value = false
}

const addTopic = () => {
  console.log('添加话题')
  // 可在此处打开话题选择弹窗
}

const addBook = () => {
  console.log('添加书籍链接')
  // 可在此处打开书籍选择窗口
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
  margin-top: 50px;
}

/* --- 发帖窗口 --- */
.editor-window {
  background: white;
  border-radius: 12px;
  padding: 20px;
  width: 900px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  animation: fadeIn 0.2s ease;
}

.editor-window h3 {
  margin-top: 0;
  margin-bottom: 12px;
  color: #007c27;
  font-weight: 600;
}
.title-input{
  width: 880px;
  height: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 8px;
  font-size: 16px;
  margin-bottom: 6px;
  resize: none;
  outline: none;
  transition: border-color 0.2s;
}
.editor-textarea {
  width: 880px;
  height: 500px;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 8px;
  font-size: 15px;
  resize: none;
  outline: none;
  transition: border-color 0.2s;
}
.editor-textarea:focus,
.title-input:focus {
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
/* 工具栏：左下角 */
.editor-tools {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 8px;
}

.tool-icon {
  font-size: 22px;
  color: #007c27;
  cursor: pointer;
  transition: color 0.2s, transform 0.2s;
}

.tool-icon:hover {
  color: #1ad6a1;
  transform: scale(1.1);
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
