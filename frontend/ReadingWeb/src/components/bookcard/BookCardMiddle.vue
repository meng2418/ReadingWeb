<!--书架里的书籍卡片-->
<template>
  <div class="book-card">
    <div class="book-cover-container">
      <img :src="cover" alt="book cover" class="book-cover" />
      <!-- 读完标签 -->
      <div v-if="isRead" class="read-tag">读完</div>
    </div>
    <div class="book-info">
      <h3 class="book-title">{{ title }}</h3>
    </div>
  </div>
</template>

<script setup>
defineProps({
  cover: {
    type: String,
    default: 'https://picsum.photos/200/300?grayscale',
  },
  title: {
    type: String,
    default: 'Vue 组件设计指南与精品开发',
  },
  isRead: {
    type: Boolean,
    default: true,
  },
  
})
</script>

<style scoped>
.book-card {
  width: 150px;
  overflow: hidden;
  text-align: left;
  cursor: pointer;
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
  text-overflow: ellipsis;
}

.book-title:hover {
  color: #000;
}
</style>
