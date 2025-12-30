<!--CategoryPage.vue-->
<template>
  <div class="category-page">
    <NavBar />
    <BackToTop />
    <div class="category-container">
      <div class="left-nav">
        <div
          v-for="tab in categories"
          :key="tab.id"
          class="nav-item"
          :class="{ active: currentCategoryId === tab.id }"
          @click="switchCategory(tab.id)"
        >
          {{ tab.name }}
        </div>
      </div>

      <div class="right-content">
        <div class="ranking-header">
          <h1 class="ranking-title">{{ currentCategoryName }}</h1>
        </div>

        <div v-if="subCategoryList.length" class="category-tabs">
          <div
            v-for="category in subCategoryList"
            :key="category.id"
            class="category-tab"
            :class="{ active: currentSubCategory === category.id }"
            @click="switchSubCategory(category.id)"
          >
            {{ category.name }}
          </div>
        </div>

        <div class="book-ranking">
          <div
            v-for="(book, index) in books.slice(0, displayCount)"
            :key="book.id"
            class="ranking-item"
          >
            <div class="ranking-number">{{ index + 1 }}</div>
            <BookCardSuperBig
              :book-id="book.id"
              :cover="book.cover"
              :title="book.title"
              :author="book.author"
              :readers-count="book.readersCount || '—'"
              :recommendation-rate="book.recommendationRate || 0"
              :description="book.description || `${book.title}是一本优秀的作品，值得一读。`"
            />
          </div>
          <div v-if="!books.length && !loading" class="empty">暂无数据</div>
          <div v-if="loading" class="empty">加载中...</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import NavBar from '@/components/layout/NavBar.vue'
import BookCardSuperBig from '@/components/category/BookCardSuperBig.vue'
import BackToTop from '@/components/layout/BackToTop.vue'
import { useBookNavigation } from '@/composables/useBookNavigation'
import type { CategoryTab, RankedBook, SubCategory } from '@/types/category'
import { getCategoryBooks } from '@/api/categories'
import { useRoute, useRouter } from 'vue-router'
// 固定分类（原有静态类别，避免被清空）
const categories: CategoryTab[] = [
  { id: 'weekly', name: '周榜' },
  { id: 'monthly', name: '月榜' },
  { id: 'new', name: '新书榜' },
  { id: 'masterpiece', name: '神作榜' },
  { id: 'novel', name: '精品小说' },
  { id: 'history', name: '历史' },
  { id: 'art', name: '艺术' },
  { id: 'biography', name: '人物传记' },
  { id: 'computer', name: '计算机' },
  { id: 'social_culture', name: '社会文化' },
  { id: 'economy_finance', name: '经济理财' },
  { id: 'children_books', name: '童书' },
  { id: 'medical_health', name: '医学健康' },
  { id: 'literature', name: '文学' },
  { id: 'philosophy_religion', name: '哲学宗教' },
  { id: 'psychology', name: '心理' },
  { id: 'personal_development', name: '个人成长' },
  { id: 'politics_military', name: '政治军事' },
  { id: 'education_learning', name: '教育学习' },
  { id: 'science_technology', name: '科学技术' },
  { id: 'life_skills', name: '生活百科' },
  { id: 'periodicals', name: '期刊杂志' },
]
const route = useRoute()
const router = useRouter()
const currentCategoryId = ref<string | number>(
  typeof route.query.tab === 'string' ? (route.query.tab as string) : categories[0]?.id || '',
)
const currentSubCategory = ref<string>(
  typeof route.query.subTab === 'string' ? (route.query.subTab as string) : 'all',
)
const books = ref<RankedBook[]>([])
const page = ref(1)
const limit = ref(50) // 固定 50 本
const total = ref(0)
const loading = ref(false)
const step = 20 // 每次加载多少本
const displayCount = ref(step)
const { openBookDetail } = useBookNavigation()

const currentCategoryName = computed(() => {
  const tab = categories.find((item) => item.id === currentCategoryId.value)
  return tab?.name ?? '分类'
})

const getSubCategories = (tabId: string): SubCategory[] => {
  const subCategories: Record<string, SubCategory[]> = {
    novel: [
      { id: 'all', name: '全部' },
      { id: 'social', name: '社会小说' },
      { id: 'emotion', name: '情感小说' },
      { id: 'youth', name: '青春文学' },
      { id: 'sci_fi', name: '科幻小说' },
      { id: 'war', name: '战争军旅' },
      { id: 'fantasy', name: '玄幻小说' },
      { id: 'horror', name: '恐怖惊悚' },
      { id: 'mystery', name: '悬疑推理' },
      { id: 'martial_arts', name: '武侠小说' },
      { id: 'film_adaptation', name: '影视原著' },
      { id: 'premium_web', name: '优质网文' },
      { id: 'historical_fiction', name: '历史架空' },
      { id: 'family', name: '家庭故事' },
      { id: 'workplace', name: '职场小说' },
      { id: 'officialdom', name: '官场小说' },
      { id: 'ancient_romance', name: '古言精品' },
      { id: 'folklore', name: '民俗志怪' },
      { id: 'women_fiction', name: '女性小说' },
      { id: 'era', name: '年代小说' },
      { id: 'healing', name: '治愈小说' },
    ],
    history: [
      { id: 'all', name: '全部' },
      { id: 'history_geography', name: '历史地理' },
      { id: 'historical_documents', name: '历史典籍' },
      { id: 'history_reading', name: '历史读物' },
      { id: 'historical_fiction', name: '历史小说' },
      { id: 'oral_history', name: '口述史' },
      { id: 'world_history', name: '世界史' },
      { id: 'ancient_china', name: '中国古代' },
      { id: 'history_culture', name: '历史文化' },
      { id: 'modern_china', name: '中国近现代' },
    ],
    literature: [
      { id: 'all', name: '全部' },
      { id: 'classical_literature', name: '古典文学' },
      { id: 'documentary_literature', name: '纪实文学' },
      { id: 'folk_literature', name: '民间文学' },
      { id: 'classic_works', name: '经典作品' },
      { id: 'prose_essays', name: '散文杂著' },
      { id: 'literary_appreciation', name: '文学鉴赏' },
      { id: 'drama_literature', name: '戏剧文学' },
      { id: 'language_writing', name: '语言文字' },
      { id: 'modern_poetry', name: '现代诗歌' },
      { id: 'ancient_poetry', name: '古代诗词' },
      { id: 'foreign_literature', name: '外国文学' },
      { id: 'world_classics', name: '世界名著' },
    ],
    art: [
      { id: 'all', name: '全部' },
      { id: 'sculpture', name: '雕塑' },
      { id: 'painting', name: '绘画' },
      { id: 'architecture', name: '建筑' },
      { id: 'calligraphy', name: '书法' },
      { id: 'crafts', name: '工艺' },
      { id: 'appreciation', name: '鉴赏' },
      { id: 'theory', name: '理论' },
      { id: 'film_tv', name: '影视' },
      { id: 'photography', name: '摄影' },
      { id: 'folk_art', name: '民艺' },
      { id: 'design', name: '设计' },
      { id: 'dance', name: '舞蹈' },
      { id: 'music', name: '音乐' },
    ],
    biography: [
      { id: 'all', name: '全部' },
      { id: 'financial_figures', name: '财经人物' },
      { id: 'biography_comprehensive', name: '传记综合' },
      { id: 'political_leaders', name: '军政领袖' },
      { id: 'scientists', name: '科学家' },
      { id: 'historical_figures', name: '历史人物' },
      { id: 'women_figures', name: '女性人物' },
      { id: 'literary_figures', name: '文学家' },
      { id: 'scholars', name: '学者' },
      { id: 'artists', name: '艺术家' },
      { id: 'celebrities', name: '娱乐明星' },
    ],
    philosophy_religion: [
      { id: 'all', name: '全部' },
      { id: 'eastern_philosophy', name: '东方哲学' },
      { id: 'ethics_morality', name: '伦理道德' },
      { id: 'logic', name: '逻辑学' },
      { id: 'marxist_philosophy', name: '马克思哲学' },
      { id: 'aesthetics', name: '美学' },
      { id: 'cognitive_science', name: '思维科学' },
      { id: 'western_philosophy', name: '西方哲学' },
      { id: 'philosophy_reading', name: '哲学读物' },
      { id: 'philosophical_works', name: '哲学著作' },
      { id: 'religion', name: '宗教' },
    ],
    computer: [
      { id: 'all', name: '全部' },
      { id: 'software_learning', name: '软件学习' },
      { id: 'programming_design', name: '编程设计' },
      { id: 'computer_comprehensive', name: '计算机综合' },
      { id: 'theoretical_knowledge', name: '理论知识' },
      { id: 'artificial_intelligence', name: '人工智能' },
      { id: 'database', name: '数据库' },
      { id: 'image_video', name: '图像视频' },
    ],
    psychology: [
      { id: 'all', name: '全部' },
      { id: 'cognition_behavior', name: '认知与行为' },
      { id: 'developmental_psychology', name: '发展心理学' },
      { id: 'positive_psychology', name: '积极心理学' },
      { id: 'psychology_research', name: '心理学研究' },
      { id: 'intimate_relationships', name: '亲密关系' },
      { id: 'social_psychology', name: '社会心理学' },
      { id: 'psychology_application', name: '心理学应用' },
    ],
    social_culture: [
      { id: 'all', name: '全部' },
      { id: 'law', name: '法律' },
      { id: 'social_science', name: '社科' },
      { id: 'culture', name: '文化' },
    ],
    economy_finance: [
      { id: 'all', name: '全部' },
      { id: 'financial_planning', name: '理财' },
      { id: 'finance', name: '财经' },
      { id: 'business', name: '商业' },
      { id: 'management', name: '管理' },
    ],
    medical_health: [
      { id: 'all', name: '全部' },
      { id: 'health', name: '健康' },
      { id: 'gender_relations', name: '两性' },
      { id: 'medicine', name: '医学' },
    ],
    life_skills: [
      { id: 'all', name: '全部' },
      { id: 'home_living', name: '居家' },
      { id: 'travel', name: '旅游' },
      { id: 'food', name: '美食' },
      { id: 'metaphysics', name: '命理' },
      { id: 'emotion', name: '情感' },
      { id: 'fashion', name: '时尚' },
      { id: 'handicraft', name: '手工' },
      { id: 'sports', name: '体育' },
      { id: 'games', name: '游戏' },
    ],
    science_technology: [
      { id: 'all', name: '全部' },
      { id: 'industrial_technology', name: '工业技术' },
      { id: 'architecture', name: '建筑' },
      { id: 'science_popularization', name: '科学科普' },
      { id: 'agriculture_forestry', name: '农林牧业' },
      { id: 'natural_science', name: '自然科学' },
    ],
    education_learning: [
      { id: 'all', name: '全部' },
      { id: 'reference_books', name: '工具书' },
      { id: 'textbooks', name: '教材' },
      { id: 'education', name: '教育' },
      { id: 'exams', name: '考试' },
      { id: 'foreign_languages', name: '外语' },
      { id: 'parenting', name: '育儿' },
    ],
    children_books: [
      { id: 'all', name: '全部' },
      { id: 'children_literature', name: '儿童文学' },
      { id: 'comics_cartoons', name: '漫画卡通' },
      { id: 'children_english', name: '少儿英语' },
      { id: 'early_education', name: '幼儿启蒙' },
      { id: 'reading_reference', name: '阅读工具书' },
    ],
    personal_development: [
      { id: 'all', name: '全部' },
      { id: 'communication', name: '沟通表达' },
      { id: 'motivational_growth', name: '励志成长' },
      { id: 'emotion_mind', name: '情绪心灵' },
      { id: 'life_philosophy', name: '人生哲学' },
      { id: 'workplace_life', name: '人在职场' },
      { id: 'cognitive_thinking', name: '认知思维' },
      { id: 'women_growth', name: '女性成长' },
    ],
    politics_military: [
      { id: 'all', name: '全部' },
      { id: 'military', name: '军事' },
      { id: 'politics', name: '政治' },
    ],
    periodicals: [
      { id: 'all', name: '全部' },
      { id: 'finance', name: '财经' },
      { id: 'life', name: '生活' },
      { id: 'literature', name: '文学' },
      { id: 'other', name: '其他' },
    ],
  }

  return subCategories[tabId] || []
}

const subCategoryList = computed(() => getSubCategories(String(currentCategoryId.value)))

const fetchBooks = async () => {
  if (!currentCategoryId.value) return
  loading.value = true
  try {
    const res = await getCategoryBooks(currentCategoryId.value, 1, limit.value)
    books.value = res.list.slice(0, limit.value) // 最多 50
    total.value = books.value.length
    page.value = 1
    displayCount.value = step
  } finally {
    loading.value = false
  }
}
const onScroll = () => {
  if (loading.value) return

  const scrollBottom = window.innerHeight + window.scrollY >= document.body.offsetHeight - 100

  if (scrollBottom && displayCount.value < books.value.length) {
    displayCount.value = Math.min(
      displayCount.value + step,
      books.value.length, // 👈 保证最多到 50
    )
  }
}
onMounted(() => {
  window.addEventListener('scroll', onScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
})

// 监听路由参数变化并立即触发数据加载
watch(
  () => route.query,
  (q) => {
    const tab = typeof q.tab === 'string' ? q.tab : categories[0]?.id || ''
    const sub = typeof q.subTab === 'string' ? q.subTab : 'all'
    if (currentCategoryId.value !== tab) currentCategoryId.value = tab
    if (currentSubCategory.value !== sub) currentSubCategory.value = sub
    fetchBooks()
  },
  { immediate: true },
)

const switchCategory = async (id: string | number) => {
  if (currentCategoryId.value === id) return
  router.push({
    path: '/category',
    query: { tab: String(id), subTab: 'all' },
  })
}

const switchSubCategory = async (id: string) => {
  if (currentSubCategory.value === id) return
  router.push({
    path: '/category',
    query: { tab: String(currentCategoryId.value), subTab: id },
  })
}

// 新增：生成分类数据结构
function generateCategoryDataStructure(mainCategory: string): Record<string, RankedBook[]> {
  const subCategories = getSubCategories(mainCategory)
  const result: Record<string, RankedBook[]> = {}

  // 修复：使用 for...of 替代 forEach
  for (const category of subCategories) {
    result[category.id] = generateCategoryData(mainCategory, category.id)
  }

  return result
}

// 新增：生成分类数据函数（用于子分类）
function generateCategoryData(mainCategory: string, subCategory: string): RankedBook[] {
  const data: RankedBook[] = []
  for (let i = 1; i <= 50; i++) {
    // 使用哈希函数生成唯一的数字id
    const uniqueId = stringToHash(`${mainCategory}-${subCategory}-${i}`)
    data.push({
      id: uniqueId,
      cover: `https://picsum.photos/seed/${mainCategory}-${subCategory}-${i}/200/300`,
      title: `${getSubCategoryTitle(mainCategory, subCategory)} ${i}`,
      author: `${getSubCategoryAuthor(mainCategory, subCategory)} ${i}`,
      recommend: `${(95 - i * 0.1).toFixed(1)}%`,
      readersCount: (10000 - i * 100).toString(),
      recommendationRate: 95 - i * 0.1,
      description: `这是${getSubCategoryTitle(mainCategory, subCategory)}第${i}本书的详细描述。这是一本非常优秀的作品，专注于${getSubCategoryDescription(mainCategory, subCategory)}领域。`,
    })
  }
  return data
}

function getTitleByType(type: string): string {
  const titles: Record<string, string> = {
    weekly: '周榜热门书籍',
    monthly: '月榜精选书籍',
    new: '新书推荐',
    masterpiece: '经典神作',
    novel: '精品小说',
    history: '历史书籍',
    art: '艺术书籍',
    biography: '人物传记',
    computer: '计算机书籍',
    social_culture: '社会文化书籍',
    economy_finance: '经济理财书籍',
    children_books: '童书',
    medical_health: '医学健康书籍',
    literature: '文学作品',
    philosophy_religion: '哲学宗教书籍',
    psychology: '心理学书籍',
    personal_development: '个人成长书籍',
    politics_military: '政治军事书籍',
    education_learning: '教育学习书籍',
    science_technology: '科学技术书籍',
    life_skills: '生活百科书籍',
    periodicals: '期刊杂志',
  }
  return titles[type] || '书籍'
}

// 新增：获取子分类标题
function getSubCategoryTitle(mainCategory: string, subCategory: string): string {
  const subCategories = getSubCategories(mainCategory)
  const category = subCategories.find((cat) => cat.id === subCategory)

  if (category && category.name !== '全部') {
    return category.name
  }

  // 如果找不到或为"全部"，返回主分类名称
  return getTitleByType(mainCategory)
}

// 新增：获取子分类作者
function getSubCategoryAuthor(mainCategory: string, subCategory: string): string {
  const subCategories = getSubCategories(mainCategory)
  const category = subCategories.find((cat) => cat.id === subCategory)

  if (category && category.name !== '全部') {
    return `${category.name}作者`
  }

  // 如果找不到或为"全部"，返回主分类作者
  return `${getTitleByType(mainCategory)}作者`
}

// 新增：获取子分类描述 - 修复重复函数问题
function getSubCategoryDescription(mainCategory: string, subCategory: string): string {
  const subCategories = getSubCategories(mainCategory)
  const category = subCategories.find((cat) => cat.id === subCategory)

  if (category && category.name !== '全部') {
    return `关于${category.name}的精选书籍`
  }

  // 如果找不到或为"全部"，返回主分类描述
  return getTitleByType(mainCategory)
}

// 添加字符串到数字的哈希函数
function stringToHash(str: string): number {
  let hash = 0
  if (str.length === 0) return hash

  // 使用字符串迭代器，它会自动处理 Unicode 代理对
  for (const char of str) {
    const codePoint = char.codePointAt(0) || 0
    hash = (hash << 5) - hash + codePoint
    hash = hash & hash
  }

  return Math.abs(hash)
}
</script>

<style scoped>
.category-page {
  background-color: #f1f1f1;
  min-height: 100vh;
  padding-top: 56px;
}

.category-container {
  display: flex;
  max-width: 80%;
  min-height: 100vh;
  margin: 16px auto;
  padding: 0;
  gap: 0;
  background-color: white;
}

.left-nav {
  width: 160px;
  background: white;
  border-radius: 0;
  padding: 0;
  height: calc(100vh - 100px);
  position: sticky;
  top: 100px;
  box-shadow: none;
  border-right: 2px solid #e0e0e0;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.left-nav::-webkit-scrollbar {
  display: none;
}

.nav-item {
  padding: 16px 20px;
  cursor: pointer;
  border-radius: 8px;
  margin-bottom: 8px;
  transition: all 0.2s;
  font-size: 16px;
  font-weight: 400;
  color: #0e1017;
  background: white;
}

.nav-item:hover {
  background: #eaf1fc;
  font-weight: 600;
}

.nav-item.active {
  background: var(--primary-green);
  color: white;
  font-weight: 600;
}

.right-content {
  flex: 1;
  background: white;
  overflow-y: auto;
}

.ranking-header {
  background: white;
  border-radius: 0;
  padding: 24px;
  margin-bottom: 0;
  box-shadow: none;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.ranking-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 16px 0 4px 0;
}
/* 分类标签栏样式 */
.category-tabs {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-top: 20px;
  padding-bottom: 12px;
}

.category-tab {
  padding: 16px 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 16px;
  font-weight: 400;
  color: #333;
  text-align: center;
  background: transparent;
}

.category-tab:hover {
  color: #333;
  font-weight: 600;
  background-color: transparent;
}

.category-tab.active {
  color: #00a735;
  font-weight: 600;
  background-color: transparent;
}
.book-ranking {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 20px;
  background: white;
  border-radius: 0;
  padding: 10px;
  box-shadow: none;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s ease;
  cursor: pointer;
}

.ranking-item:hover {
  background-color: #f0f8ff;
}

.ranking-item:last-child {
  border-bottom: none;
}

.ranking-number {
  font-style: italic;
  font-size: 32px;
  font-weight: bold;
  color: #424242;
  min-width: 60px;
  text-align: center;
  justify-content: center;
  margin-top: 0;
  flex-shrink: 0;
}

.empty {
  padding: 40px;
  text-align: center;
  color: #888;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-btn {
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 6px;
  background: white;
  cursor: pointer;
}

.page-btn:disabled {
  cursor: not-allowed;
  color: #aaa;
  border-color: #eee;
}

.page-text {
  color: #555;
}

@media (max-width: 1200px) {
  .category-container {
    max-width: 90%;
  }
  .left-nav {
    width: 150px;
  }
}

@media (max-width: 992px) {
  .category-container {
    max-width: 98%;
  }
  .left-nav {
    width: 180px;
  }
}

@media (max-width: 768px) {
  .category-container {
    max-width: 100%;
    flex-direction: column;
  }
  .left-nav {
    width: 100%;
    height: auto;
    position: static;
    border-right: none;
    border-bottom: 2px solid #e0e0e0;
    display: flex;
    overflow-x: auto;
    padding: 10px 0;
  }
  .nav-item {
    flex-shrink: 0;
    margin-bottom: 0;
    margin-right: 10px;
    padding: 15px 20px;
    font-size: 18px;
  }
  .ranking-header {
    padding: 20px;
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}

@media (max-width: 576px) {
  .ranking-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  .ranking-number {
    align-self: flex-start;
  }
}
</style>
