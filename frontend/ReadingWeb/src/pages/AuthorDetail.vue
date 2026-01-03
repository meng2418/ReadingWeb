<!-- AuthorDetail.vue -->
<template>
  <div class="author-detail-container">
    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-container">
      <div class="loading-spinner"></div>
      <div class="loading-text">加载中...</div>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <div class="error-icon">❌</div>
      <div class="error-text">{{ error }}</div>
      <button @click="retryLoading" class="retry-button">重试</button>
    </div>

    <!-- 作者信息区域 -->
    <div v-else-if="authorData" class="author-header-section">
      <div class="section-header">
        <h1 class="author-title">{{ authorData.name }}</h1>
      </div>

      <div class="author-description">
        {{ authorData.description }}
      </div>
    </div>

    <!-- 作品列表区域 -->
    <div v-if="authorData && !isLoading" class="works-section">
      <div class="section-header">
        <h2 class="section-title">全部作品</h2>
        <div class="works-count">共 {{ authorData.worksCount }} 部</div>
      </div>

      <div class="works-list">
        <BookCardSuperBig
          v-for="work in allWorks"
          :key="work.id"
          :book-id="work.id"
          :cover="work.cover"
          :title="work.title"
          :author="work.authorName || authorData?.name || '未知作者'"
          :readers-count="work.readersCount"
          :recommendation-rate="work.recommendationRate"
          :description="work.summary"
        />
      </div>

      <!-- 没有作品的提示 -->
      <div v-if="allWorks.length === 0 && !isLoading" class="empty-works">
        <div class="empty-icon">📚</div>
        <div class="empty-text">该作者暂无作品</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useBookNavigation } from '@/composables/useBookNavigation'
import BookCardSuperBig from '@/components/category/BookCardSuperBig.vue'
import { useTitle } from '@/stores/useTitle'
import { getAuthorDetail, getAuthorAllWorks, type AuthorDetail, type AuthorWorkWithId } from '@/api/book-detail/author-info-section'

// 路由
const router = useRouter()
const route = useRoute()
const { openBookDetail } = useBookNavigation()

// 获取作者ID（从路由参数中）
const authorId = ref(Number(route.params.id) || 1)

// 作者数据
const authorData = ref<AuthorDetail | null>(null)
// 动态页面标题
const title = ref('作者详情')
useTitle(title)
// 所有作品数据
const allWorks = ref<AuthorWorkWithId[]>([])
// 加载状态
const isLoading = ref(true)
// 错误信息
const error = ref<string | null>(null)

// 获取作者详情数据
const fetchAuthorData = async () => {
  try {
    isLoading.value = true
    error.value = null

    console.log('获取作者详情，ID:', authorId.value)
    const response = await getAuthorDetail(authorId.value)
    authorData.value = response

    // 更新页面标题
    title.value = `${response.name} - 作者详情`
    useTitle(title)

  } catch (err: any) {
    console.error('获取作者详情失败:', err)
    error.value = err.response?.data?.message || '获取作者信息失败，请稍后重试'

    // 设置默认数据作为fallback（仅用于开发测试）
    authorData.value = {
      id: authorId.value,
      name: '扬·马特尔',
      description: '扬·马特尔（Yann Martel，1963年6月25日－）是一位加拿大作家。他出生于西班牙萨拉曼卡，父母是加拿大人。幼时曾旅居哥斯达黎加、法国、墨西哥、加拿大，成年后做客伊朗、土耳其及印度。毕业于加拿大特伦特大学哲学系，其后从事过各种稀奇古怪的行业，包括植树工、洗碗工、保安等。以《少年Pi的奇幻漂流》获得2002年的布克奖及亚洲/太平洋美洲文学奖。马特尔现在住在萨斯卡通（Saskatoon）。',
      worksCount: 3,
    }
  }
}

// 获取所有作品数据
const fetchWorks = async () => {
  try {
    error.value = null

    console.log('获取作者所有作品，作者ID:', authorId.value)
    const response = await getAuthorAllWorks(authorId.value)
    allWorks.value = response

  } catch (err: any) {
    console.error('获取作者作品失败:', err)
    error.value = err.response?.data?.message || '获取作品列表失败，请稍后重试'

    // 设置默认数据作为fallback（仅用于开发测试）
    allWorks.value = [
      {
        id: 1,
        title: '少年Pi的奇幻漂流',
        summary: '一名印度男孩Pi在太平洋上与一只孟加拉虎同船而行的冒险故事',
        cover: 'https://picsum.photos/200/280?random=25',
        readersCount: 183000,
        recommendationRate: 90.5,
        authorName: '扬·马特尔'
      },
      {
        id: 2,
        title: '标本师的魔幻剧本',
        summary: '关于大屠杀记忆与文学创作的深刻探讨',
        cover: 'https://picsum.photos/200/280?random=26',
        readersCount: 45200,
        recommendationRate: 85.2,
        authorName: '扬·马特尔'
      },
      {
        id: 3,
        title: '赫尔曼',
        summary: '关于友谊、艺术与人生选择的温暖故事',
        cover: 'https://picsum.photos/200/280?random=27',
        readersCount: 32100,
        recommendationRate: 88.7,
        authorName: '扬·马特尔'
      },
    ]
  }
}

// 重试加载
const retryLoading = async () => {
  error.value = null
  isLoading.value = true
  try {
    await Promise.all([fetchAuthorData(), fetchWorks()])
  } catch (err) {
    console.error('重试加载失败:', err)
    error.value = '加载失败，请刷新页面重试'
  } finally {
    isLoading.value = false
  }
}

// 作品点击事件由 BookCardSuperBig 组件处理：新标签打开书籍详情

// 组件挂载时获取数据
onMounted(async () => {
  try {
    // 并行获取作者详情和作品数据
    await Promise.all([fetchAuthorData(), fetchWorks()])
  } catch (err) {
    console.error('页面初始化失败:', err)
    error.value = '页面加载失败，请刷新重试'
  } finally {
    isLoading.value = false
  }

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

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #4caf50;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: 18px;
  color: #666;
}

/* 错误状态 */
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  text-align: center;
}

.error-icon {
  font-size: 64px;
  margin-bottom: 20px;
  color: #f44336;
}

.error-text {
  font-size: 18px;
  color: #666;
  margin-bottom: 30px;
  max-width: 400px;
}

.retry-button {
  padding: 10px 30px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.retry-button:hover {
  background-color: #45a049;
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
  white-space: pre-line;
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
  border-bottom: 2px solid var(--primary-green, #4caf50);
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
