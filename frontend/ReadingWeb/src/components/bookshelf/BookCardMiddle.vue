<template>
  <!-- 使用 <a> 标签包裹整个卡片 -->
  <a
    v-if="!disableJump"
    :href="bookId ? `/bookdetail/${bookId}` : '/bookdetail'"
    target="_blank"
    class="book-card-link"
  >
    <div class="book-card">
      <div class="book-cover-container">
        <img :src="cover" alt="book cover" class="book-cover" />
        <!-- 读完标签 -->
        <div v-if="isRead && !disableJump" class="read-tag">读完</div>
      </div>
      <div class="book-info">
        <h3 class="book-title" :title="title">{{ title }}</h3>
      </div>
    </div>
  </a>

  <!-- 如果禁用跳转，只显示卡片，不包裹链接 -->
  <div v-else class="book-card" :class="{ 'no-link': disableJump }">
    <div class="book-cover-container">
      <img :src="cover" alt="book cover" class="book-cover" />
      <!-- 读完标签 -->
      <div v-if="isRead && !disableJump" class="read-tag">读完</div>
    </div>
    <div class="book-info">
      <h3 class="book-title" :title="title">{{ title }}</h3>
    </div>
  </div>
</template>

<script setup lang="ts">
// 定义 props
interface Props {
  cover?: string
  title?: string
  isRead?: boolean
  bookId?: string | number
  disableJump?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  cover: '',
  title: '',
  isRead: true,
  bookId: '',
  disableJump: false,
})
</script>

<style scoped>
/* 链接样式 */
.book-card-link {
  text-decoration: none;
  color: inherit;
  display: inline-block;
}

/* 卡片基础样式 */
.book-card {
  width: 150px;
  overflow: hidden;
  text-align: left;
  cursor: pointer;
}

/* 禁用跳转时的卡片样式 */
.book-card.no-link {
  cursor: default;
}

.book-cover-container {
  position: relative;
  width: 150px;
  height: 200px;
  margin-bottom: 4px;
}

.book-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.read-tag {
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: rgba(0, 128, 0, 0.8);
  color: white;
  padding: 3px 8px;
  font-size: 12px;
  font-weight: bold;
  border-radius: 3px;
  z-index: 10;
}

.book-info {
  line-height: 1.4;
}

.book-title {
  font-size: 16px;
  margin: 0px;
  transition: color 0.2s ease;
  color: gray;
  
  /* 限制为两行，超出部分显示省略号 */
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
  word-wrap: break-word;
  word-break: break-all;
}

/* 只有在可点击的情况下才显示悬停效果 */
.book-card-link .book-title:hover {
  color: #000;
}
</style>
