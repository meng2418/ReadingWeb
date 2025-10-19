<template>
  <div class="category-page">
    <NavBar />

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
          <p class="ranking-desc">{{ currentTabDesc }}</p>
        </div>

        <!-- 书籍榜单 -->
        <div class="book-ranking">
          <div
            v-for="(book, index) in currentRanking"
            :key="book.id"
            class="ranking-item"
          >
            <div class="ranking-number">{{ index + 1 }}</div>
            <BookCardSuperBig
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
import { ref, computed } from 'vue'
import NavBar from '@/components/NavBar.vue'
import BookCardSuperBig from '@/components/BookCardSuperBig.vue'

// 导航标签
const tabs = [
  { id: 'weekly', name: '周榜', desc: '最近一周热读书籍' },
  { id: 'monthly', name: '月榜', desc: '最近一个月最受欢迎' },
  { id: 'new', name: '新书榜', desc: '最新上架的优质书籍' },
  { id: 'masterpiece', name: '神作榜', desc: '公认必读的高分神作' },
  { id: 'category', name: '分类', desc: '按分类浏览书籍' }
]

const currentTab = ref('weekly')

// 计算属性
const currentTabName = computed(() => {
  const tab = tabs.find(t => t.id === currentTab.value)
  return tab?.name || '周榜'
})

const currentTabDesc = computed(() => {
  const tab = tabs.find(t => t.id === currentTab.value)
  return tab?.desc || '最近一周热读书籍'
})

// 模拟数据 - 这里需要替换为真实的API数据
const rankings = {
  weekly: generateRankingData('weekly'),
  monthly: generateRankingData('monthly'),
  new: generateRankingData('new'),
  masterpiece: generateRankingData('masterpiece'),
  category: generateRankingData('category')
}

const currentRanking = computed(() => rankings[currentTab.value])

// 切换标签
const switchTab = (tabId: string) => {
  currentTab.value = tabId
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
}

.category-container {
  display: flex;
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  gap: 30px;
}

/* 左侧导航 */
.left-nav {
  width: 200px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: fit-content;
  position: sticky;
  top: 20px;
}

.nav-item {
  padding: 12px 16px;
  cursor: pointer;
  border-radius: 8px;
  margin-bottom: 8px;
  transition: all 0.2s;
  font-size: 16px;
  color: #333;
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
}

.ranking-header {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.ranking-title {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.ranking-desc {
  font-size: 16px;
  color: #666;
  margin: 0;
}

/* 书籍榜单 */
.book-ranking {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.ranking-item {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.ranking-number {
  font-size: 32px;
  font-weight: bold;
  color: #007fff;
  min-width: 60px;
  text-align: center;
  margin-top: 10px;
}
</style>
