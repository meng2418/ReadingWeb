<template>
  <div class="guess-you-like">
    <div class="header">
      <h2>猜你想看</h2>
      <button @click="fetchBooks" class="change-btn">换一批</button>
    </div>

    <div class="book-cards-container">
      <BookCard
        v-for="(book, index) in books"
        :key="book.id || index"
        :cover="book.cover"
        :title="book.title"
        :author="book.author"
        :reason="book.reason"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import BookCard from './BookCardBig.vue'

// 模拟后端书籍库
const allBooks = [
  { id: 1, cover: 'https://picsum.photos/200/300?1', title: 'Vue 3 实战', author: '张三', reason: '最近热门技术推荐' },
  { id: 2, cover: 'https://picsum.photos/200/300?2', title: '深入 TypeScript', author: '李四', reason: '类型安全最佳实践' },
  { id: 3, cover: 'https://picsum.photos/200/300?3', title: '前端工程化指南', author: '王五', reason: '构建现代化前端体系' },
  { id: 4, cover: 'https://picsum.photos/200/300?4', title: '算法图解', author: 'Aditya Bhargava', reason: '算法初学者必备' },
  { id: 5, cover: 'https://picsum.photos/200/300?5', title: 'JavaScript 高级程序设计', author: 'Nicholas Zakas', reason: '经典 JS 入门书籍' },
  { id: 6, cover: 'https://picsum.photos/200/300?6', title: 'React 深入解析', author: '刘强', reason: '解构前端框架核心原理' },
  { id: 7, cover: 'https://picsum.photos/200/300?7', title: 'Node.js 实战', author: '李雷', reason: '全栈开发必备技能' },
  { id: 8, cover: 'https://picsum.photos/200/300?8', title: 'Web 安全攻防', author: '韩梅梅', reason: '保护你的网站安全' },
  { id: 9, cover: 'https://picsum.photos/200/300?9', title: 'CSS 世界', author: '张鑫旭', reason: '从细节掌握样式魔法' },
  { id: 10, cover: 'https://picsum.photos/200/300?10', title: '计算机网络', author: '谢希仁', reason: '夯实基础的经典教材' },
  { id: 11, cover: 'https://picsum.photos/200/300?11', title: '操作系统真相还原', author: '郑钢', reason: '系统底层的奥秘' },
  { id: 12, cover: 'https://picsum.photos/200/300?12', title: 'Redis 深入学习', author: '黄亮', reason: '提升系统性能关键' },
  { id: 13, cover: 'https://picsum.photos/200/300?13', title: 'MySQL 性能优化', author: '周杰', reason: '数据库性能调优指南' },
  { id: 14, cover: 'https://picsum.photos/200/300?14', title: 'Python 机器学习', author: '吴恩达', reason: 'AI 工程师必修课' },
  { id: 15, cover: 'https://picsum.photos/200/300?15', title: '数据结构与算法分析', author: 'Mark Allen Weiss', reason: '编程面试必备' },
  { id: 16, cover: 'https://picsum.photos/200/300?16', title: '重构：改善既有代码的设计', author: 'Martin Fowler', reason: '写出更优雅的代码' },
  { id: 17, cover: 'https://picsum.photos/200/300?17', title: '设计模式之美', author: 'Alex Xu', reason: '高质量架构的关键' },
  { id: 18, cover: 'https://picsum.photos/200/300?18', title: 'Git 从入门到精通', author: '王者', reason: '版本控制实践指南' },
  { id: 19, cover: 'https://picsum.photos/200/300?19', title: 'Docker 实战', author: '张恒', reason: '容器化部署入门' },
  { id: 20, cover: 'https://picsum.photos/200/300?20', title: 'Kubernetes 入门', author: '李想', reason: '云原生时代的必修课' },
]

// 当前显示的书籍
const books = ref([])

// 从 allBooks 中随机抽取 N 本
function getRandomBooks(count = 6) {
  return [...allBooks].sort(() => Math.random() - 0.5).slice(0, count)
}

// 模拟“换一批”
function fetchBooks() {
  books.value = getRandomBooks()
}

// 初始加载
fetchBooks()
</script>

<style scoped>
.guess-you-like {
  padding: 20px 20px 0 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h2 {
  font-size: 24px;
}

.book-cards-container {
  display: flex;
  gap: 26px;
  flex-wrap: wrap;
  margin-top: 12px;
  justify-content: space-between; /* 让6本平均分布 */
  flex-wrap: nowrap; /* 不换行 */
  overflow: hidden; /* 超出隐藏 */
}

.change-btn {
  background: none;
  border: 1px solid #007c27;
  color: #000;
  padding: 6px 14px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.change-btn:hover {
  color: white;
  box-shadow: inset 0 -100px 0px 0 #1ad6a1;
}
</style>
