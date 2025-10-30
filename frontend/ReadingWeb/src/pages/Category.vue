<template>
  <div class="category-page">
    <NavBar />
    <BackToTop />
    <div class="category-container">
      <!-- 左侧导航 -->
      <div class="left-nav">
        <div
          v-for="tab in tabs"
          :key="tab.id"
          class="nav-item"
          :class="{ active: currentTab === tab.id }"
          @click="switchTab(tab.id)"
        >
          {{ tab.name }}
        </div>
      </div>

      <!-- 右侧内容 -->
      <div class="right-content">
        <div class="ranking-header">
          <h1 class="ranking-title">{{ currentTabName }}</h1>
        </div>

        <!-- 书籍榜单 -->
        <div class="book-ranking" >
          <div
            v-for="(book, index) in currentRanking"
            :key="book.id"
            class="ranking-item"
            @click="goToBookDetail(book.id)"
            >
            <div class="ranking-number" >{{ index + 1 }}</div>
            <BookCardSuperBig
              :book-id="book.id"
              :cover="book.cover"
              :title="book.title"
              :author="book.author"
              :readers-count="book.readersCount || '1021'"
              :recommendation-rate="book.recommendationRate || book.recommend"
              :description="book.description || `${book.title}是一本优秀的作品，值得一读。`"

              />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavBar from '@/components/layout/NavBar.vue'
import BookCardSuperBig from '@/components/bookcard/BookCardSuperBig.vue'
import BackToTop from '@/components/layout/BackToTop.vue'

const route = useRoute()
const router = useRouter()

const goToBookDetail = (bookId: string | number) => {
  console.log('跳转到书籍详情页，书籍ID:', bookId)
  router.push(`/book/${bookId}`)
}
// 导航标签
const tabs = [
  { id: 'weekly', name: '周榜' },
  { id: 'monthly', name: '月榜' },
  { id: 'new', name: '新书榜' },
  { id: 'masterpiece', name: '神作榜' },
  { id: 'category', name: '分类' }
]
const currentTab = ref('weekly')

// 监听路由参数变化
watch(
  () => route.query.tab,
  (newTab) => {
    if (newTab && tabs.some(tab => tab.id === newTab)) {
      currentTab.value = newTab as string
    }
  }
)

// 组件挂载时检查参数
onMounted(() => {
  const tabParam = route.query.tab as string
  if (tabParam && tabs.some(tab => tab.id === tabParam)) {
    currentTab.value = tabParam
  }
})

// 计算属性
const currentTabName = computed(() => {
  const tab = tabs.find(t => t.id === currentTab.value)
  return tab?.name || '周榜'
})



// 模拟数据 - 这里需要替换为真实的API数据
const rankings = {
  weekly: generateRankingData('weekly'),
  monthly: generateRankingData('monthly'),
  new: generateRankingData('new'),
  masterpiece: generateRankingData('masterpiece'),
 // category: generateRankingData('category')
}

const currentRanking = computed(() => rankings[currentTab.value as keyof typeof rankings])

// 切换标签
const switchTab = (tabId: string) => {
  currentTab.value = tabId
  // 更新URL参数但不触发页面刷新
  router.replace({ query: { tab: tabId } })
  // 这里可以添加加载对应榜单数据的逻辑
}

// 生成模拟数据函数
function generateRankingData(type: string) {
  const data = []
  for (let i = 1; i <= 50; i++) {
    data.push({
      id: `${type}-${i}`,
      cover: `https://picsum.photos/seed/${type}${i}/200/300`,
      title: `${getTitleByType(type)} ${i}`,
      author: `作者${i}`,
      recommend: `${95 - i * 0.1}%`,
      readersCount: (10000 - i * 100).toString(),
      recommendationRate: 95 - i * 0.1,
      description: `这是${getTitleByType(type)}第${i}本书的详细描述。这是一本非常优秀的作品，故事情节引人入胜，人物形象鲜明，值得每一位读者细细品味。`
    })
  }
  return data
}

function getTitleByType(type: string): string {
  const titles = {
    weekly: '周榜热门书籍',
    monthly: '月榜精选书籍',
    new: '新书推荐',
    masterpiece: '经典神作',
    category: '分类书籍'
  }
  return titles[type as keyof typeof titles] || '书籍'
}
</script>

<style scoped>
.category-page {
  background-color: #f1f1f1;
  min-height: 100vh;
  /* 导航栏高度64px + 额外的间距 = 80px */
  padding-top: 56px;
}

.category-container {
  zoom: 0.75;
  display: flex;
  max-width: 90%;
  margin: 0 auto;
  padding: 0; /* 移除内边距 */
  gap: 0;
  background-color: white; /* 添加白色背景 */
}

/* 左侧导航 */
.left-nav {
  width: 200px;
  background: white; /* 改为白色背景 */
  border-radius: 0;
  padding: 0px 20px 40px 40px;
  height: fit-content;
  position: sticky;
  top: 100px;/* 距离顶部100px */
  box-shadow: none; /* 确保没有阴影 */
  border-right: 1px solid #e0e0e0; /* 添加细分割线 */
}

.nav-item {
  padding: 20px 25px;
  cursor: pointer;
  border-radius: 8px;
  margin-bottom: 8px; /* 恢复导航项之间的间距 */
  transition: all 0.2s;
  font-size: 20px;
  color: #333;
  background: white;
}

.nav-item:hover {
  background: #f5f5f5;
}

.nav-item.active {
  background: #007fff;
  color: white;
  font-weight: 500;
}

/* 右侧内容 */
.right-content {
  flex: 1;
  background: white; /* 改为白色背景 */
}

.ranking-header {
  background: white;
  border-radius: 0;
  padding: 24px;
  margin-bottom: 0;
  box-shadow: none; /* 移除阴影 */
  border-bottom: 1px solid #e0e0e0; /* 添加底部细线 */
}

.ranking-title {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}


/* 书籍榜单 */
.book-ranking {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 40px;
  background: white;
  border-radius: 0;
  padding: 20px;
  box-shadow: none; /* 移除阴影 */
  border-bottom: 1px solid #f0f0f0; /* 添加项之间的分割线 */
  transition: background-color 0.2s ease;
  cursor: pointer; /* 添加指针样式 */
}

.ranking-item:hover {
  background-color: #f0f8ff; /* 浅蓝色背景 */
}

.ranking-item:last-child {
  border-bottom: none; /* 最后一项不加底部分割线 */
}

.ranking-number {
  font-style: italic;
  font-size: 60px;
  font-weight: bold;
  color: #424242;
  min-width: 80px;
  text-align: center;
  justify-content: center;
  margin-top: 0; /* 移除顶部边距 */
  flex-shrink: 0; /* 防止数字区域被压缩 */
}
</style>
