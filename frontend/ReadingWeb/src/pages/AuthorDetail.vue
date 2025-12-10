<!-- AuthorDetail.vue -->
<template>
  <div class="author-detail-container">
    <!-- 简化后的作者信息区域 -->
    <div class="author-header-section">
      <div class="section-header">
        <h1 class="author-title">{{ authorData.name }}</h1>
      </div>

      <div class="author-description">
        {{ authorData.description }}
      </div>
    </div>

    <!-- 作品列表区域 -->
    <div class="works-section">
      <div class="section-header">
        <h2 class="section-title">全部作品</h2>
        <div class="works-count">共 {{ authorData.worksCount }} 部</div>
      </div>

      <div class="works-list">
        <BookCardSuperBig
          v-for="work in allWorks"
          :key="work.id"
          :cover="work.cover"
          :title="work.title"
          :author="authorData.name"
          :readers-count="work.readersCount"
          :recommendation-rate="work.recommendationRate"
          :description="work.summary"
          @click="handleBookClick(work)"
        />
      </div>

      <!-- 没有作品的提示 -->
      <div v-if="allWorks.length === 0" class="empty-works">
        <div class="empty-icon">📚</div>
        <div class="empty-text">该作者暂无作品</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import BookCardSuperBig from '@/components/category/BookCardSuperBig.vue'

// 定义类型
interface Work {
  id: number
  title: string
  summary: string
  cover?: string
  readersCount: number
  recommendationRate: number
}

interface AuthorData {
  id: number
  name: string
  description: string
  worksCount: number
}

// 路由
const router = useRouter()
const route = useRoute()

// 获取作者ID（从路由参数中）
const authorId = ref(Number(route.params.id) || 1)

// 作者数据
const authorData = ref<AuthorData>({
  id: authorId.value,
  name: '扬·马特尔',
  description: '扬·马特尔（Yann Martel，1963年6月25日－）是一位加拿大作家。他出生于西班牙萨拉曼卡，父母是加拿大人。幼时曾旅居哥斯达黎加、法国、墨西哥、加拿大，成年后做客伊朗、土耳其及印度。毕业于加拿大特伦特大学哲学系，其后从事过各种稀奇古怪的行业，包括植树工、洗碗工、保安等。以《少年Pi的奇幻漂流》获得2002年的布克奖及亚洲/太平洋美洲文学奖。马特尔现在住在萨斯卡通（Saskatoon）。',
  worksCount: 10
})

// 所有作品数据
const allWorks = ref<Work[]>([
  {
    id: 1,
    title: '作品一',
    summary: '这是作品一的简介内容...',
    cover: 'https://picsum.photos/200/280?random=60',
    readersCount: 1021,
    recommendationRate: 93.6
  },
  {
    id: 2,
    title: '作品二',
    summary: '这是作品二的简介内容...',
    cover: 'https://picsum.photos/200/280?random=70',
    readersCount: 892,
    recommendationRate: 88.4
  },
  {
    id: 3,
    title: '作品三',
    summary: '这是作品三的简介内容...',
    cover: 'https://picsum.photos/200/280?random=80',
    readersCount: 654,
    recommendationRate: 91.2
  },
  // 可以添加更多作品数据...
])

// 模拟获取作者详情数据
const fetchAuthorData = () => {
  // 这里应该是API调用，根据authorId获取数据
  console.log('获取作者详情，ID:', authorId.value)

  // 模拟数据
  // 实际项目中应该是：const response = await api.getAuthorDetail(authorId.value)
  // authorData.value = response.data
}

// 模拟获取所有作品数据
const fetchWorks = () => {
  // 这里应该是API调用，一次性获取该作者的所有作品
  console.log('获取作者所有作品，作者ID:', authorId.value)

  // 模拟数据
  // 实际项目中应该是：const response = await api.getAuthorWorks(authorId.value)
  // allWorks.value = response.data
}

// 作品点击事件
const handleBookClick = (work: Work) => {
  // 跳转到书籍详情页
  router.push(`/bookdetail/${work.id}`)
}



// 组件挂载时获取数据
onMounted(() => {
  fetchAuthorData()
  fetchWorks()
  window.scrollTo({
    top: 0,
  })
})
</script>

<style scoped>
.author-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
}


/* 作者头部区域 - 简化版 */
.author-header-section {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.section-header {
  margin-bottom: 20px;
}

.author-title {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin: 0 0 20px 0;
}

.author-description {
  font-size: 18px;
  line-height: 1.8;
  color: #555;
  padding-bottom: 0;
  border-bottom: none;
}

/* 作品区域 */
.works-section {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.works-section .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid var(--primary-green, #4CAF50);
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.works-count {
  font-size: 16px;
  color: #666;
  background: #f5f5f5;
  padding: 6px 12px;
  border-radius: 20px;
}

/* 作品列表 */
.works-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(600px, 1fr));
  gap: 24px;
  margin-bottom: 30px;
}

/* 空状态 */
.empty-works {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-text {
  font-size: 18px;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .author-detail-container {
    padding: 15px;
  }

  .author-header-section,
  .works-section {
    padding: 20px;
  }

  .author-title {
    font-size: 24px;
  }

  .author-description {
    font-size: 16px;
  }

  .works-list {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .section-title {
    font-size: 20px;
  }
}
</style>
