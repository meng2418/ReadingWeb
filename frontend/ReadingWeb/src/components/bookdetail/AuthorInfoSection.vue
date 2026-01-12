<template>
  <div class="author-info-section">
    <!-- 作者信息标题 -->
    <div class="section-header">
      <!-- 添加 title 属性，鼠标悬停时可以看到全名 -->
      <h3 class="section-title" :title="author.name">{{ author.name }}</h3>
    </div>

    <!-- 作者简介 -->
    <!-- 添加 title 属性，鼠标悬停可以看到完整简介(可选) -->
    <div class="author-description" :title="author.description">
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
          <div class="work-title" :title="work.title">{{ work.title }}</div>
          <div class="work-summary">{{ work.summary }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref,computed } from 'vue'
import { useRouter } from 'vue-router'
import { useBookNavigation } from '@/composables/useBookNavigation'
import type { AuthorWork, AuthorInfo } from '@/types/author'

const router = useRouter()
const { openInNewTab } = useBookNavigation()

interface Props {
  author: AuthorInfo
  works: AuthorWork[]
  maxDisplayCount?: number
  openInNewTab?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  maxDisplayCount: 3,
  openInNewTab: true
})

const emit = defineEmits<{
  workClick: [work: AuthorWork]
  viewAllWorks: []
}>()
//添加展开状态控制
const isDescriptionExpanded = ref(false)
const toggleDescription = () => {
  isDescriptionExpanded.value = !isDescriptionExpanded.value
}
const displayedWorks = computed(() => {
  return props.works.slice(0, props.maxDisplayCount)
})

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
}

const handleWorkClick = (work: AuthorWork) => {
  emit('workClick', work)
  if (work.id) {
    openInNewTab(work.id)
  }
}

const handleViewAllWorks = () => {
  const authorId = props.author.id || 1
  emit('viewAllWorks')

  if (props.openInNewTab) {
    window.open(`/authordetail/${authorId}`, '_blank')
  } else {
    router.push(`/authordetail/${authorId}`)
  }
}
</script>

<style scoped>
.author-info-section {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
  width: 300px;
  height: fit-content;
}

.section-header {
  margin-bottom: 15px;
}

/* 1. 作者姓名：限制为 1 行，超出显示省略号 */
.section-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin: 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
  
  white-space: nowrap;      /* 不换行 */
  overflow: hidden;         /* 隐藏溢出 */
  text-overflow: ellipsis;  /* 显示省略号 */
  width: 100%;              /* 确保宽度填满以触发溢出 */
}

/* 2. 作者简介：限制为 6 行（可根据需要调整数字），超出显示省略号 */
.author-description {
  font-size: 18px;
  line-height: 1.6;
  color: #555;
  margin-bottom: 20px;
  
  /* 移除原本的滚动条样式*/
  /* max-height: 400px;  
  /* overflow-y: auto;   
  
  display: -webkit-box;           /* 必须结合的属性 */
  -webkit-box-orient: vertical;   /* 必须结合的属性 */
  -webkit-line-clamp: 6;          /* 限制显示 6 行 */
  line-clamp: 6;
  overflow: hidden;
  text-overflow: ellipsis;
  word-break: break-all;          /* 防止长单词撑破布局 */
}

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

.view-all-button {
  padding: 6px 12px;
  background: transparent;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  color: #666;
  font-size: 14px; /* 稍微调小一点，原20px可能太大 */
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
  white-space: nowrap;
}

.view-all-button:hover {
  background: #f5f5f5;
  border-color: #ccc;
  color: #333;
}

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
  font-size: 12px; /* 稍微调小 */
}

.work-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow: hidden; /* 确保子元素溢出处理生效 */
}

/* 3. 作品标题：限制为 2 行 */
.work-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
  line-height: 1.3;
  
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2; /* 限制 2 行 */
  line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 4. 作品简介：限制为 2 行 */
.work-summary {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
  
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2; /* 限制 2 行 */
  line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
}

@media (max-width: 768px) {
  .author-info-section {
    width: 100%;
    padding: 15px;
  }

  .section-title {
    font-size: 16px;
  }

  /* 移动端可能希望显示的行数少一点或多一点，这里保持一致或微调 */
  .author-description {
    font-size: 13px;
    -webkit-line-clamp: 4; /* 移动端只显示4行 */
    line-clamp: 4;
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
    -webkit-line-clamp: 1; /* 移动端标题限制1行 */
    line-clamp: 1;
  }

  .work-summary {
    font-size: 11px;
  }
}
</style>