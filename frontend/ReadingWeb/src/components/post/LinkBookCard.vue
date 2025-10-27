<!-- components/LinkBookCard.vue -->
 <!--帖子里的书籍链接卡片-->
<template>
  <div class="link-book-card" @click="goToBook">
    <img :src="cover" alt="书籍封面" class="book-cover" />
    <div class="book-info">
      <div class="book-title">{{ title }}</div>
      <div class="book-author">{{ author }}</div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

defineProps({
  cover: { type: String, required: true },
  title: { type: String, required: true },
  author: { type: String, required: true },
  bookId: { type: [String, Number], required: true },
})

const goToBook = () => {
  router.push(`/book/${bookId}`)
}
</script>

<style scoped>
.link-book-card {
  display: flex;
  align-items: center;
  background: #fafafa;
  border-radius: 10px;
  padding: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #eee;
  margin-top: 8px;
}

.link-book-card:hover {
  background: #f0f0f0;
  transform: translateY(-2px);
}

.book-cover {
  width: 48px;
  height: 64px;
  border-radius: 6px;
  object-fit: cover;
  margin-right: 10px;
}

.book-info {
  flex: 1;
  overflow: hidden;
}

.book-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-author {
  font-size: 13px;
  color: #888;
}
</style>
