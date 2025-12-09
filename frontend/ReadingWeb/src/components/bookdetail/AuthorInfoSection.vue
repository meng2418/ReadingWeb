<template>
  <div class="author-info-section">
    <!-- 作者信息标题 -->
    <div class="section-header">
      <h3 class="section-title">{{ author.name }}</h3>
    </div>

    <!-- 作者简介 -->
    <div class="author-description">
      {{ author.description }}
    </div>

    <!-- 作者作品标题和全部作品按钮 -->
    <div class="works-header">
      <h4 class="works-title">代表作品</h4>
      <button class="view-all-button" @click="handleViewAllWorks">
        全部作品
      </button>
    </div>

    <!-- 作者作品列表 -->
    <div class="works-list">
      <div
        v-for="work in displayedWorks"
        :key="work.id"
        class="work-item"
        @click="handleWorkClick(work)"
      >
        <div class="work-cover">
          <img :src="work.cover" :alt="work.title" @error="handleImageError">
          <div v-if="!work.cover" class="cover-placeholder">
            <span>无封面</span>
          </div>
        </div>
        <div class="work-info">
          <div class="work-title">{{ work.title }}</div>
          <div class="work-summary">{{ work.summary }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
// 添加路由
const router = useRouter()
// 定义props
interface Work {
  id:  number
  title: string
  summary: string
  cover?: string
}

interface Props {
  author: {
    name: string
    description: string
  }
  works: Work[]
  maxDisplayCount?: number
}

const props = withDefaults(defineProps<Props>(), {
  maxDisplayCount: 3
})

// 定义事件
const emit = defineEmits<{
  workClick: [work: Work]
  viewAllWorks: []
}>()

// 计算显示的著作数量
const displayedWorks = computed(() => {
  return props.works.slice(0, props.maxDisplayCount)
})

// 图片加载失败处理
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
}

// 作品点击事件
const handleWorkClick = (work: Work) => {
  emit('workClick', work)
  console.log('点击作品:', work.title)
}

// 查看全部作品
const handleViewAllWorks = () => {
  const authorId = props.author.id || 1 // 这里假设author有id属性
  router.push(`/authordetail/${authorId}`)
}
</script>

<style scoped>
.author-info-section {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
  /* 设置固定宽度，适合放在右侧 */
  width: 300px;
  /* 高度自适应内容 */
  height: fit-content;
}

/* 标题样式 */
.section-header {
  margin-bottom: 15px;
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin: 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

/* 作者简介样式 - 加长区域 */
.author-description {
  font-size: 18px;
  line-height: 1.6;
  color: #555;
  margin-bottom: 20px;
  /* 增加简介区域高度 */
  max-height: 400px;
  overflow-y: auto;
  padding-right: 5px;
}

/* 滚动条样式 */
.author-description::-webkit-scrollbar {
  width: 4px;
}

.author-description::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 2px;
}

.author-description::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.author-description::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 作品标题和按钮区域 */
.works-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.works-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

/* 查看全部按钮样式 - 放在作品标题右侧 */
.view-all-button {
  padding: 6px 12px;
  background: transparent;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  color: #666;
  font-size: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.view-all-button:hover {
  background: #f5f5f5;
  border-color: #ccc;
  color: #333;
}

/* 作品列表样式 */
.works-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.work-item {
  display: flex;
  gap: 12px;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.work-item:hover {
  background: #f9f9f9;
  transform: translateY(-2px);
}

/* 作品封面样式 */
.work-cover {
  flex-shrink: 0;
  width: 75px;
  height: 90px;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.work-cover img {
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
  font-size: 16px;
}

/* 作品信息样式 */
.work-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.work-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
  line-height: 1.3;
}

.work-summary {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
  display: -webkit-box;
  line-clamp: 2;          /* 标准属性 */
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .author-info-section {
    width: 100%; /* 在移动设备上占满宽度 */
    padding: 15px;
  }

  .section-title {
    font-size: 16px;
  }

  .author-description {
    font-size: 13px;
    max-height: 150px; /* 在移动设备上稍微减小高度 */
  }

  .works-title {
    font-size: 15px;
  }

  .work-cover {
    width: 45px;
    height: 63px;
  }

  .work-title {
    font-size: 13px;
  }

  .work-summary {
    font-size: 11px;
  }
}
</style>
