<!-- components/PostBook.vue -->
<template>
  <div class="post-book-card">
    <!-- 左侧：小尺寸书籍封面 -->
    <div class="book-cover">
      <img :src="book.cover" :alt="`${book.title} - ${book.author}`" class="cover-img" />
    </div>

    <!-- 右侧：书名 + 作者 -->
    <div class="book-info">
      <h3 class="book-title">{{ book.title }}</h3>
      <p class="book-author">作者：{{ book.author }}</p>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  book: {
    type: Object,
    required: true,
    default: () => ({
      title: '未知书籍',
      author: '未知作者',
      cover: 'https://via.placeholder.com/80x120', // 适配小封面尺寸
    }),
  },
})
</script>

<style scoped>
/* 整体卡片：横向布局、紧凑尺寸 */
.post-book-card {
  display: flex;
  align-items: center;
  gap: 12px;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 10px;
  background: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.03);
  width: 100%;
}

/* 左侧封面：固定小尺寸，保持图书比例（约 2:3） */
.book-cover {
  width: 80px;
  height: 120px;
  flex-shrink: 0;
}
.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

/* 右侧文字：垂直居中，紧凑排版 */
.book-info {
  flex: 1;
  overflow: hidden; /* 防止文字溢出 */
}
.book-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 最多显示2行标题 */
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}
.book-author {
  font-size: 13px;
  color: #666;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
