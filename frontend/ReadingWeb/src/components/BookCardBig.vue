<template>
  <div class="book-card">
    <img :src="cover" alt="book cover" class="book-cover" />
    <div class="book-info">
      <h3 class="book-title">{{ title }}</h3>
      <p class="book-author">{{ author }}</p>
      <p class="book-reason">{{ reason }}</p>
    </div>
  </div>
</template>

<script setup>
defineProps({
  cover: String, // 封面图片链接
  title: String, // 书名
  author: String, // 作者
  reason: String, // 推荐理由
})
</script>

<style scoped>
.book-card {
  width: 180px;
  background-color: #fff;
  border-radius: 10px;
  /*box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);*/
  overflow: hidden;
  text-align: center;
  padding: 12px;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
  cursor: pointer;
}

.book-card:hover {
  transform: translateY(-4px) scale(1.05);
  filter: brightness(1.05);
  /*box-shadow: 0 6px 14px rgba(0, 0, 0, 0.15);*/
}

.book-cover {
  width: 120px;
  height: 160px;
  object-fit: cover;
  border-radius: 6px;
  margin-bottom: 10px;
}

.book-info {
  line-height: 1.4;
}

.book-title {
  font-weight: bold;
  font-size: 16px;
  margin: 4px 0;
}

.book-author {
  color: #666;
  font-size: 14px;
  margin: 2px 0;
}

.book-reason {
  color: #999;
  font-size: 13px;
  margin-top: 4px;
}
</style>
