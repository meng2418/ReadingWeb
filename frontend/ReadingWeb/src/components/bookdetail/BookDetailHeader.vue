<template>
  <div class="book-detail-header">
    <!-- 上半部分：封面和基本信息 -->
    <div class="top-section">
      <!-- 左侧：书籍封面 -->
      <div class="book-cover">
        <img :src="coverImage" :alt="title" class="cover-image" @error="handleImageError">
        <div v-if="!coverImage" class="cover-placeholder">
          <span>暂无封面</span>
        </div>
      </div>

      <!-- 右侧：书籍信息 -->
      <div class="book-info">
        <!-- 右上角：操作按钮 -->
        <div class="action-buttons">
          <button
            class="bookshelf-button"
            :class="{ 'added': isInBookshelf }"
            @click="handleBookshelfToggle"
          >
            {{ isInBookshelf ? '✔已在书架' : '加入书架' }}
          </button>

          <button class="read-button" @click="handleStartReading">
            开始阅读
          </button>
        </div>

        <!-- 书名和作者 -->
        <h1 class="book-title">{{ title }}</h1>
        <div class="book-author">{{ author }}</div>

        <!-- 统计卡片 -->
        <div class="stats-cards">
          <div class="stat-card" @click="handleStatClick('reading')">
            <div class="stat-label">阅读</div>
            <div class="stat-value">{{ stats.readingCount }}</div>
            <div class="stat-subtitle">{{ stats.readingSubtitle }}</div>
          </div>

          <div class="stat-card" @click="handleStatClick('myReading')">
            <div class="stat-label">我的阅读</div>
            <div class="stat-value">{{ stats.myReadingStatus }}</div>
            <div class="stat-subtitle">{{ stats.myReadingSubtitle }}</div>
          </div>

          <div class="stat-card" @click="handleStatClick('wordCount')">
            <div class="stat-label">字数</div>
            <div class="stat-value">{{ stats.wordCount }}</div>
            <div class="stat-subtitle">{{ stats.publishInfo }}</div>
          </div>

          <div class="stat-card" @click="handleStatClick('experienceCard')">
            <el-icon><ShoppingCart /></el-icon>
            <div class="stat-value">{{ stats.experienceCardStatus }}</div>
            <div class="stat-subtitle">{{ stats.priceInfo }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 下半部分：书籍简介 -->
    <div class="book-description-section">
      <h3 class="description-title">书籍简介</h3>
      <div class="book-description">{{ description }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ShoppingCart } from '@element-plus/icons-vue'

// 定义props
interface Props {
  title: string
  author: string
  description: string
  coverImage?: string
  initialBookshelfStatus?: boolean
  stats: {
    readingCount: string
    readingSubtitle: string
    myReadingStatus: string
    myReadingSubtitle: string
    wordCount: string
    publishInfo: string
    experienceCardStatus: string
    priceInfo: string
  }
}

const props = withDefaults(defineProps<Props>(), {
  coverImage: '',
  initialBookshelfStatus: false,
  stats: () => ({
    readingCount: '18.3万人',
    readingSubtitle: '7.6万人读完',
    myReadingStatus: '在读',
    myReadingSubtitle: '标记在读',
    wordCount: '11.3万字',
    publishInfo: '2021年7月出版',
    experienceCardStatus: '体验卡可读',
    priceInfo: '电子书价格49元'
  })
})

// 定义事件
const emit = defineEmits<{
  toggleBookshelf: [isAdded: boolean]
  startReading: []
  statClick: [statType: string]
}>()

// 本地状态
const isInBookshelf = ref(props.initialBookshelfStatus)

// 图片加载失败处理
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
}

// 加入书架切换
const handleBookshelfToggle = () => {
  isInBookshelf.value = !isInBookshelf.value
  emit('toggleBookshelf', isInBookshelf.value)
  console.log(`书籍${isInBookshelf.value ? '已加入' : '已移除'}书架`)
}

// 开始阅读
const handleStartReading = () => {
  emit('startReading')
  console.log('开始阅读')
}

// 统计卡片点击
const handleStatClick = (statType: string) => {
  emit('statClick', statType)
  console.log(`点击统计卡片: ${statType}`)
}
</script>

<style scoped>
.book-detail-header {
  max-width: 1200px;
  margin: 0 auto;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 上半部分样式 */
.top-section {
  display: flex;
  gap: 30px;
  padding: 30px;
  border-bottom: 1px solid #f0f0f0;
  align-items: flex-start;
}

/* 书籍封面样式 */
.book-cover {
  flex-shrink: 0;
  width: 200px;
  height: 280px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  margin-top: 20px;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
}

/* 书籍信息样式 */
.book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  margin-top: -20px;
  gap: 0px;
}

/* 按钮区域样式 - 放在右上角 */
.action-buttons {
  display: flex;
  gap: 20px;
  justify-content: flex-end;
  margin-bottom: 15px;
}

.bookshelf-button,
.read-button {
  padding: 12px 24px;
  border: 2px solid #007cba;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 120px;
  text-align: center;
}

.bookshelf-button {
  background: #fff;
  color: #007cba;
}

.bookshelf-button:hover {
  background: #f0f8ff;
  transform: translateY(-2px);
}

.bookshelf-button.added {
  background: #007cba;
  color: white;
}

.bookshelf-button.added:hover {
  background: #005a87;
  transform: translateY(-2px);
}

.read-button {
  background: #007cba;
  color: white;
}

.read-button:hover {
  background: #005a87;
  transform: translateY(-2px);
}

/* 书名和作者样式 */
.book-title {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin: 0 0 5px 0;
  line-height: 1.2;
}

.book-author {
  font-size: 18px;
  color: #666;
  font-weight: 500;
  margin-bottom: 30px;
}

/* 统计卡片样式 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  align-items: center;
  align-items: stretch;

}

.stat-card {
  background: #f0f0f0;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 100px;
  border-radius: 8px;
  align-items: center;
}

/* 图标样式 */
.stat-card .el-icon {
  font-size: 24px;
  color: #007cba;
  margin-bottom: 8px;

}

.stat-card:hover {
  background: #f9f9f9;
  transform: translateY(-2px);
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.stat-subtitle {
  font-size: 12px;
  color: #999;
}

/* 下半部分：书籍简介样式 */
.book-description-section {
  padding: 30px;
}

.description-title {
  font-size: 30px;
  font-weight: bold;
  color: #333;
  margin: 0 0 15px 0;
}

.book-description {
  font-size: 20px;
  line-height: 1.6;
  color: #555;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .top-section {
    flex-direction: column;
    padding: 20px;
    gap: 20px;
  }

  .book-cover {
    width: 150px;
    height: 210px;
    align-self: center;
  }

  .book-title {
    font-size: 24px;
    text-align: center;
  }

  .book-author {
    text-align: center;
  }

  .action-buttons {
    justify-content: center;
    flex-wrap: wrap;
  }

  .bookshelf-button,
  .read-button {
    flex: 1;
    min-width: 120px;
    padding: 10px 20px;
  }

  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .book-description-section {
    padding: 20px;
  }
}

@media (max-width: 480px) {
  .top-section {
    padding: 15px;
  }

  .book-cover {
    width: 120px;
    height: 168px;
  }

  .book-title {
    font-size: 20px;
  }

  .book-author {
    font-size: 16px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .bookshelf-button,
  .read-button {
    width: 100%;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }

  .book-description-section {
    padding: 15px;
  }

  .book-description {
    font-size: 14px;
  }
}
</style>
