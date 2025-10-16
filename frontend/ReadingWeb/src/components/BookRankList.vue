<template>
  <div class="book-rank-wrapper">
    <!-- 榜单标题部分 -->
    <div class="book-rank-header">
      <div class="header-left">
        <h2 class="rank-title">{{ ranktitle }}</h2>
        <span class="rank-desc">{{ desc }}</span>
      </div>
      <button class="view-all-btn">查看全部</button>
    </div>

    <!-- 榜单内容部分 -->
    <div class="book-rank-list">
      <div class="book-rank-list-li" v-for="(book, index) in books" :key="index">
        <img :src="book.cover" alt="book cover" class="book-cover" />
        <div class="book-details">
          <i class="book-rank">{{ index + 1 }}</i>
          <div class="book-info">
            <h3 class="book-title">{{ book.title }}</h3>
            <p class="book-author">{{ book.author }}</p>
            <p class="book-recommend">推荐值： {{ book.recommend }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

// 定义接收的属性
defineProps({
  ranktitle: {
    type: String,
    default: '周榜',
  },
  desc: {
    type: String,
    default: '最近一周热读书籍',
  },
  books: {
    type: Array,
    default: () => [
      // 默认10本书数据
      {
        cover: 'https://picsum.photos/seed/book1/200/300',
        title: '书籍标题1',
        author: '作者1',
        recommend: '98%',
      },
      {
        cover: 'https://picsum.photos/seed/book2/200/300',
        title: '书籍标题2',
        author: '作者2',
        recommend: '97%',
      },
      {
        cover: 'https://picsum.photos/seed/book3/200/300',
        title: '书籍标题3',
        author: '作者3',
        recommend: '96%',
      },
      {
        cover: 'https://picsum.photos/seed/book4/200/300',
        title: '书籍标题4',
        author: '作者4',
        recommend: '95%',
      },
      {
        cover: 'https://picsum.photos/seed/book5/200/300',
        title: '书籍标题5',
        author: '作者5',
        recommend: '94%',
      },
      {
        cover: 'https://picsum.photos/seed/book6/200/300',
        title: '书籍标题6',
        author: '作者6',
        recommend: '93%',
      },
      {
        cover: 'https://picsum.photos/seed/book7/200/300',
        title: '书籍标题7',
        author: '作者7',
        recommend: '92%',
      },
      {
        cover: 'https://picsum.photos/seed/book8/200/300',
        title: '书籍标题8',
        author: '作者8',
        recommend: '91%',
      },
      {
        cover: 'https://picsum.photos/seed/book9/200/300',
        title: '书籍标题9',
        author: '作者9',
        recommend: '90%',
      },
      {
        cover: 'https://picsum.photos/seed/book10/200/300',
        title: '书籍标题10',
        author: '作者10',
        recommend: '89%',
      },
    ],
  },
})
</script>

<style scoped>
.book-rank-wrapper {
  background-color: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(150, 150, 150, 0.05);
  margin-bottom: 50px;
}

/* 顶部标题栏 */
.book-rank-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.header-left {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.rank-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.rank-desc {
  font-size: 14px;
  color: #888;
}

/* 查看全部按钮 */
.view-all-btn {
  background: none;
  border: none;
  color: #888;
  font-size: 14px;
  cursor: pointer;
  transition: color 0.2s ease;
}

.view-all-btn:hover {
  color: #333;
}

/* 榜单主体 */
.book-rank-list {
  display: flex;
  gap: 12px;
  padding: 8px 0;
  overflow-x: auto;
}

.book-rank-list-li {
  width: 120px;
  height: 230px;
  background-color: #ffffff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  border-radius: 10px;
  overflow: hidden;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
  cursor: pointer;
  flex-shrink: 0;
}

.book-cover {
  width: 100%;
  height: 160px;
  border-radius: 10px;
  object-fit: cover;
  margin-bottom: 6px;
}

.book-details {
  display: flex;
  align-items: flex-start;
}

.book-rank {
  font-size: 40px;
  color: #666;
  margin-left: 4px;
  margin-right: 6px;
  line-height: 1;
}

.book-info {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
  line-height: 1.4;
  margin-left: 6px;
  margin-right: 8px;
}

.book-title {
  font-weight: bold;
  font-size: 14px;
  margin: 0 0 2px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-author {
  color: #666;
  font-size: 12px;
  margin: 0 0 2px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-recommend {
  color: #999;
  font-size: 10px;
  margin: 0;
}

.book-rank-list-li:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 14px rgba(0, 0, 0, 0.15);
}
</style>
