<!--分类排行榜里的书籍卡片-->
<template>
  <div class="book-card-super-big" @click.stop="goToBookDetail">
    <!-- 图书封面 -->
    <div class="book-cover-container">
      <img :src="cover" :alt="title" class="book-cover" v-if="cover" />
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

<script setup lang="ts">
import { useBookNavigation } from '@/composables/useBookNavigation'

const props = withDefaults(
  defineProps<{
    cover?: string
    title: string
    author: string
    readersCount?: string | number
    recommendationRate?: string | number
    description?: string
    scale?: number
  }>(),
  {
    cover: '',
    readersCount: '1021',
    recommendationRate: '93.6',
    description:
      '作品简介Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet. Proin gravida dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar sic tempor. Sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus pronin sapien nunc accuan eget.',
    scale: 1,
  },
)

const { openInNewTab } = useBookNavigation()

// 保持原行为：始终在新标签打开书籍详情（无指定ID时跳默认页）
const goToBookDetail = () => {
  openInNewTab()
}
</script>

<style scoped>
.book-card-super-big {
  display: flex;
  background: transparent;
  border-radius: 0;
  box-shadow: none;
  padding: 0;
  max-width: 100%;
  margin: 0;
  gap: 16px;
  transition: none;
  align-items: flex-start; /* 改为顶部对齐 */
}

/* 图书封面样式 */
.book-cover-container {
  flex-shrink: 0;
  width: 120px;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.book-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 0;
  box-shadow: none;
  display: block;
}

.book-cover-placeholder {
  width: 100%;
  height: 100%;
  background: var(--primary-green);
  border-radius: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  font-weight: bold;
  text-align: center;
  padding: 10px;
  box-shadow: none;
  word-break: break-word;
}

/* 图书信息样式 */
.book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 190px; /* 固定高度与封面相同 */
  overflow: hidden; /* 防止内容超出 */
}

.book-title {
  font-size: 22px; /* 稍微增大字体 */
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
  line-height: 1.2;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap; /* 标题单行显示 */
}

.book-author {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 10px; /* 减小间距 */
  font-weight: 500;
}

.book-stats {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 0px; /* 减小内边距 */
  border-bottom: 1px solid var(--border-color);
  margin-bottom: 4px; /* 减小与作品简介的间距 */
}

.readers-count {
  font-size: 12px;
  color: var(--secondary-green);
  font-weight: 600;
}

.separator {
  color: var(--border-color);
}

.recommendation {
  font-size: 12px;
  color: var(--primary-green);
  font-weight: 600;
}

/* 作品简介样式 */
.book-description {
  flex: 1;
  overflow-y: auto;
  padding-top: 0; /* 移除上边距 */
}

.description-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 6px; /* 减小间距 */
  padding-bottom: 4px;
  border-bottom: 2px solid var(--primary-green);
  display: inline-block;
}

.description-content {
  font-size: 12px;
  line-height: 1.4; /* 稍微减小行高 */
  color: var(--text-secondary);
  text-align: justify;
  margin: 0;
  display: -webkit-box;
  line-clamp: 3;
  -webkit-line-clamp: 3; /* 限制显示3行 */
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 自定义滚动条样式 */
.book-description::-webkit-scrollbar {
  width: 4px; /* 减小滚动条宽度 */
}

.book-description::-webkit-scrollbar-track {
  background: var(--background-color);
  border-radius: 2px;
}

.book-description::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.book-description::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .book-card-super-big {
    flex-direction: column;
    gap: 16px;
    align-items: center; /* 居中对齐 */
  }

  .book-cover-container {
    width: 100%;
    max-width: 140px;
    height: 190px;
    margin: 0 auto;
  }

  .book-info {
    height: auto; /* 移动端取消固定高度 */
    min-height: 160px;
  }

  .book-title {
    font-size: 20px;
    white-space: normal; /* 移动端允许标题换行 */
  }

  .book-author {
    font-size: 14px;
  }

  .book-stats {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .book-description {
    max-height: none;
    overflow-y: visible;
  }

  .description-content {
    line-clamp: unset;
    -webkit-line-clamp: unset; /* 移动端取消行数限制 */
  }
}
</style>
