<template>
  <div class="book-card">
    <div class="card-content">
      <h4 class="section-title">相关书籍</h4>

      <div class="book-info">
        <div class="book-cover-container">
          <img :src="book.cover" :alt="book.title" class="book-cover" />
        </div>

        <div class="book-details">
          <h3 class="book-title">{{ book.title }}</h3>
          <p class="book-author">
            作者：<span class="author-name">{{ book.author }}</span>
          </p>

          <button class="read-button">前往阅读</button>
        </div>
      </div>

      <div class="book-description-container">
        <p class="book-description">"{{ book.description }}"</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 定义接口，确保 TS 知道 book 的结构
interface BookProps {
  title: string
  author: string
  cover: string
  description?: string
}

// 1. 使用 defineProps 接收父组件传递的 book 对象
const props = defineProps<{
  book: BookProps
}>()
</script>

<style scoped>
/* 根容器样式 */
.book-card {
  background-color: #fff;
  border-radius: 1.25rem; /* 20px */
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  border: 1px solid #f1f5f9; /* slate-100 */
  overflow: hidden;
  top: 6rem; /* 96px */
}

/* 卡片内容区样式 */
.card-content {
  padding: 1.5rem; /* 24px */
}

/* 章节标题样式 */
.section-title {
  font-size: 0.75rem; /* 12px */
  font-weight: 700;
  color: #94a3b8; /* slate-400 */
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 1rem; /* 16px */
}

/* 书籍信息容器样式 */
.book-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem; /* 16px */
}

/* 书籍封面容器样式 */
.book-cover-container {
  position: relative;
  cursor: pointer;
}

/* 书籍封面图片样式 */
.book-cover {
  width: 7rem; /* 你原来的 112px */
  height: 9.5rem; /* 【新增】定死高度，保持 3:4 左右的书籍比例 */
  object-fit: cover; /* 【关键】防止图片拉伸，多余部分自动裁剪 */
  border-radius: 0.375rem; /* 6px */
  box-shadow:
    0 4px 6px -1px rgba(0, 0, 0, 0.1),
    0 2px 4px -1px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  transform: translateY(0);
  display: block; /* 消除图片底部间隙 */
}

/* 封面容器 hover 效果 */
.book-cover-container:hover .book-cover {
  box-shadow:
    0 10px 15px -3px rgba(0, 0, 0, 0.1),
    0 4px 6px -2px rgba(0, 0, 0, 0.05);
  transform: translateY(-0.25rem); /* -4px */
}

/* 封面容器伪元素样式 */
.book-cover-container::after {
  content: '';
  position: absolute;
  inset: 0;
  background-color: rgba(0, 0, 0, 0);
  border-radius: 0.375rem; /* 6px */
  transition: background-color 0.3s ease;
}

/* 封面容器 hover 伪元素效果 */
.book-cover-container:hover::after {
  background-color: rgba(0, 0, 0, 0.1);
}

/* 书籍详情容器样式 */
.book-details {
  flex: 1;
  text-align: center;
}

/* 书籍标题样式 */
.book-title {
  font-family: Georgia, Cambria, 'Times New Roman', Times, serif; /* font-serif */
  font-weight: 700;
  color: #0f172a; /* slate-900 */
  font-size: 1.125rem; /* 18px */
  line-height: 1.333; /* leading-snug */
  margin-bottom: 0.25rem; /* 4px */
}

/* 书籍作者样式 */
.book-author {
  color: #64748b; /* slate-500 */
  font-size: 0.875rem; /* 14px */
  margin-bottom: 0.5rem; /* 8px */
}

/* 作者名字样式 */
.author-name {
  color: #334155; /* slate-800 */
  font-weight: 500;
}

/* 购买按钮样式 */
.read-button {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem; /* 8px */
  padding: 0.5rem 1rem; /* 8px 16px */
  background-color: #0f172a; /* slate-900 */
  color: #fff;
  font-size: 0.75rem; /* 12px */
  font-weight: 700;
  border-radius: 0.5rem; /* 8px */
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

/* 购买按钮 hover 效果 */
.read-button:hover {
  background-color: #1e293b; /* slate-800 */
}

/* 书籍描述容器样式 */
.book-description-container {
  margin-top: 1.5rem; /* 24px */
  padding-top: 1rem; /* 16px */
  border-top: 1px solid #f1f5f9; /* slate-100 */
}

/* 书籍描述文本样式 */
.book-description {
  font-size: 0.75rem; /* 12px */
  color: #64748b; /* slate-500 */
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  line-clamp: 4;
  overflow: hidden;
  line-height: 1.625; /* leading-relaxed */
}
/* 媒体查询：中等屏幕及以上 */
@media (min-width: 768px) {
  /* 书籍信息容器改为横向排列 */
  .book-info {
    flex-direction: row;
    align-items: flex-start;
  }

  /* 书籍详情改为左对齐 */
  .book-details {
    text-align: left;
  }

  /* 评分容器改为左对齐 */
  .rating {
    justify-content: flex-start;
  }

  /* 购买按钮宽度自动 */
  .buy-button {
    width: auto;
  }

  /* 书籍封面宽度调整 */
  .book-cover {
    width: 6rem; /* 你原来的 96px */
    height: 8rem; /* 【新增】适配中屏高度 */
  }
}
</style>
