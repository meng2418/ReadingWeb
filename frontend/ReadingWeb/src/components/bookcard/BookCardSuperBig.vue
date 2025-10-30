<template>
  <div class="book-card-super-big">
    <!-- 图书封面 -->
    <div class="book-cover-container">
      <img :src="cover" :alt="title" class="book-cover" v-if="cover">
      <div class="book-cover-placeholder" v-else>
        {{ title }}
      </div>
    </div>

    <!-- 图书信息 -->
    <div class="book-info">
      <!-- 书名 -->
      <h1 class="book-title">{{ title }}</h1>

      <!-- 作者 -->
      <div class="book-author">{{ author }}</div>

      <!-- 阅读数据和推荐值 -->
      <div class="book-stats">
        <span class="readers-count">{{ readersCount }}人今日阅读</span>
        <span class="separator">|</span>
        <span class="recommendation">推荐值 {{ recommendationRate }}%</span>
      </div>

      <!-- 作品简介 -->
      <div class="book-description">
        <h3 class="description-title">作品简介</h3>
        <p class="description-content">{{ description }}</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: 'BookCardSuperBig',
  props: {
    cover: {
      type: String,
      default: ''
    },
    title: {
      type: String,
      required: true
    },
    author: {
      type: String,
      required: true
    },
    readersCount: {
      type: [String, Number],
      default: '1021'
    },
    recommendationRate: {
      type: [String, Number],
      default: '93.6'
    },
    description: {
      type: String,
      default: '作品简介Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet. Proin gravida dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar sic tempor. Sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus pronin sapien nunc accuan eget.'
    },
    scale: {
      type: Number,
      default: 1  // 默认 1 = 原尺寸
    },

  }
}
</script>

<style scoped>
.book-card-super-big {
  display: flex;
  background: transparent; /* 改为透明背景 */
  border-radius: 0; /* 移除圆角 */
  box-shadow: none; /* 移除阴影 */
  padding: 0; /* 移除内边距 */
  max-width: 100%; /* 改为100%宽度 */
  margin: 0; /* 移除外边距 */
  gap: 20px;
  transition: none; /* 移除过渡效果 */
  align-items: center;
}

/* 恢复悬停效果 */
.book-card-super-big:hover {
  transform: none;
  background-color: none
}

/* 图书封面样式 */
.book-cover-container {
  flex-shrink: 0;
  width: 220px;
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.book-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 0; /* 移除圆角 */
  box-shadow: none; /* 移除阴影 */
  display: block;
}

.book-cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 0; /* 移除圆角 */
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  padding: 15px;
  box-shadow: none; /* 移除阴影 */
  word-break: break-word;
}

/* 图书信息样式 */
.book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 300px;
}

.book-title {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 12px;
  line-height: 1.2;
}

.book-author {
  font-size: 18px;
  color: #666;
  margin-bottom: 20px;
  font-weight: 500;
}

.book-stats {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.readers-count {
  font-size: 16px;
  color: #ff6b6b;
  font-weight: 600;
}

.separator {
  color: #ddd;
}

.recommendation {
  font-size: 16px;
  color: #4a6cf7;
  font-weight: 600;
}

/* 作品简介样式 */
.book-description {
  flex: 1;
  max-height: calc(300px - 120px);
  overflow-y: auto;
}

.description-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 15px;
  padding-bottom: 8px;
  border-bottom: 2px solid #4a6cf7;
  display: inline-block;
}

.description-content {
  font-size: 16px;
  line-height: 1.7;
  color: #444;
  text-align: justify;
  margin: 0;
}

/* 自定义滚动条样式 */
.book-description::-webkit-scrollbar {
  width: 6px;
}

.book-description::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.book-description::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.book-description::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .book-card-super-big {
    flex-direction: column;
    gap: 25px;
  }

  .book-cover-container {
    width: 100%;
    max-width: 220px;
    height: 300px;
    margin: 0 auto;
  }

  .book-info {
    min-height: auto;
  }

  .book-title {
    font-size: 26px;
  }

  .book-author {
    font-size: 16px;
  }

  .book-stats {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .book-description {
    max-height: none;
    overflow-y: visible;
  }
}
</style>
