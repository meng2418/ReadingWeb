<!-- ThoughtCard.vue -->
<template>
  <div class="thought-card">
    <div class="thought-card-wrapper">
      <!-- 头部：书名和时间 -->
      <div class="thought-header">
        <div class="book-title-wrapper">
          <span class="book-title">《{{ thought.bookName }}》</span>
        </div>
        <span class="thought-time">{{ thought.date }}</span>
      </div>

      <!-- 引用句子（灰色字体） -->
      <div class="quote-section">
        <div class="quote-icon">"</div>
        <p class="quote-text">{{ thought.quote }}</p>
      </div>

      <!-- 自己的想法 -->
      <div class="thought-content">
        <p>{{ thought.thought }}</p>
      </div>

      <!-- 操作按钮 -->
      <div class="thought-actions">
        <button class="delete-thought-btn" @click.stop="handleDelete">
          <el-icon><Delete /></el-icon>
          删除
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Delete } from '@element-plus/icons-vue'

// 定义 props
interface ThoughtItem {
  id: number
  bookName: string
  date: string
  thought: string
  quote: string
}

interface Props {
  thought: ThoughtItem
}

const props = defineProps<Props>()
const emit = defineEmits<{
  delete: [id: number]
}>()

// 处理删除
const handleDelete = async () => {
  // 直接通知父组件，把决定权交给父组件
  emit('delete', props.thought.id)
}
</script>

<style scoped>
.thought-card {
  background: white;
  border: 1px solid #f0f0f0;
  overflow: hidden;
}

.thought-card-wrapper {
  padding: 24px;
}

/* 想法头部 */
.thought-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.book-title-wrapper {
  display: flex;
  align-items: center;
}

.book-title {
  font-size: 18px;
  font-weight: 700;
  color: #2d3748;
}

.thought-time {
  font-size: 14px;
  color: #718096;
  font-weight: 500;
}

/* 引用部分 */
.quote-section {
  background: #f8f9fa;
  border-left: 4px solid #42b983;
  padding: 20px;
  margin-bottom: 20px;
  position: relative;
}

.quote-icon {
  position: absolute;
  top: 10px;
  left: 10px;
  font-size: 32px;
  color: rgba(66, 185, 131, 0.3);
  font-family: serif;
  font-weight: bold;
}

.quote-text {
  margin: 0;
  font-size: 16px;
  color: #718096;
  font-style: italic;
  line-height: 1.6;
  padding-left: 20px;
}

/* 想法内容 */
.thought-content {
  margin-bottom: 20px;
}

.thought-content p {
  margin: 0;
  font-size: 15px;
  color: #4a5568;
  line-height: 1.7;
  font-weight: 400;
}

/* 想法操作按钮 */
.thought-actions {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.delete-thought-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #f5f5f5;
  color: #666;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.delete-thought-btn:hover {
  background: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .thought-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .thought-time {
    align-self: flex-end;
  }

  .quote-section {
    padding: 16px;
  }

  .quote-text {
    font-size: 15px;
  }
}

@media (max-width: 480px) {
  .thought-content p {
    font-size: 14px;
  }

  .quote-text {
    font-size: 14px;
  }
}
</style>
