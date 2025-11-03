<!--书架里的书籍卡片-->
<template>
  <div class="book-card" @click="goToBookDetail">
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

<script setup lang="ts">
// 导入 useRouter
import { useRouter } from 'vue-router'

// 初始化 router
const router = useRouter()

// 定义 props - 添加 bookId 用于跳转
interface Props {
  cover?: string
  title?: string
  isRead?: boolean
  bookId?: string | number  // 新增：书籍ID，用于路由跳转
}

const props = withDefaults(defineProps<Props>(), {
  cover: 'https://picsum.photos/200/300?grayscale',
  title: 'Vue 组件设计指南与精品开发',
  isRead: true,
  bookId: ''  // 默认值
})

// 跳转到书籍详情页
const goToBookDetail = (): void => {
  if (props.bookId) {
    // 使用书籍ID跳转到对应详情页
    router.push(`/bookdetail/${props.bookId}`)
  } else {
    // 如果没有bookId，跳转到默认详情页（备用方案）
    router.push('/bookdetail')
  }
}

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
