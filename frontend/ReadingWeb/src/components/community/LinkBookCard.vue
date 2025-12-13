<template>
  <div class="link-book-card" @click="goToBook">
    <img :src="cover" alt="书籍封面" class="book-cover" />
    <div class="book-info">
      <div class="book-title">{{ title }}</div>
      <div class="book-author">{{ author }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

// 定义 Props 接口
interface Props {
  cover: string
  title: string
  author: string
  bookId: string | number
  openInNewTab?: boolean // 新增：是否在新标签页打开
}

// 使用 TypeScript 方式定义 props
const props = withDefaults(defineProps<Props>(), {
  openInNewTab: true // 默认在新标签页打开
})

// 跳转到书籍详情页
const goToBook = (): void => {
  if (props.openInNewTab) {
    // 在新标签页打开
    window.open(`/bookdetail?id=${props.bookId}`, '_blank')
  } else {
    // 在当前页打开
    router.push(`/bookdetail/${props.bookId}`)
  }
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
