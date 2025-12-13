<!-- 猜你喜欢里的书籍卡片 -->
<template>
  <div class="book-card" @click="goToBookDetail">
    <img :src="cover" alt="book cover" class="book-cover" />
    <div class="book-info">
      <h3 class="book-title">{{ title }}</h3>
      <p class="book-author">{{ author }}</p>
      <p class="book-reason">{{ reason }}</p>
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
  author?: string
  reason?: string
  bookId?: string | number // 新增：书籍ID，用于路由跳转
  openInNewTab?: boolean // 新增：是否在新标签页打开
}

const props = withDefaults(defineProps<Props>(), {
  cover: '',
  title: '',
  author: '',
  reason: '',
  bookId: '', // 默认值
  openInNewTab: true, // 默认在新标签页打开
})

// 跳转到书籍详情页
const goToBookDetail = (): void => {
  if (props.bookId) {
    if (props.openInNewTab) {
      // 在新标签页打开
      window.open(`/bookdetail?id=${props.bookId}`, '_blank')
    } else {
      // 在当前页打开
      router.push(`/bookdetail/${props.bookId}`)
    }
  } else {
    // 如果没有bookId，跳转到默认详情页（备用方案）
    if (props.openInNewTab) {
      window.open('/bookdetail', '_blank')
    } else {
      router.push('/bookdetail')
    }
  }
}
</script>

<style scoped>
.book-card {
  width: 180px;
  background-color: #fff;
  border-radius: 10px;
  text-align: center;
  padding: 12px;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
  cursor: pointer;
}

.book-card:hover {
  filter: brightness(1.05);
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
