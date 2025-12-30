<!---src/pages/UserPosts.vue-->
<template>
  <div class="user-posts-page">
    <NavBar />
    <BackToTop />
    <!-- 只有在发布页面才显示浮动添加按钮 -->
    <FloatingAddButton v-if="currentTab === 'posts'" />
    <div class="page-content">
      <!-- 左右分栏布局 -->
      <div class="main-layout">
        <!-- 左侧导航栏 -->
        <div class="sidebar">
          <div
            class="nav-item"
            :class="{ active: currentTab === 'posts' }"
            @click="switchTab('posts')"
          >
            <el-icon><Edit /></el-icon>
            <span>发布</span>
          </div>
          <div
            class="nav-item"
            :class="{ active: currentTab === 'following' }"
            @click="switchTab('following')"
          >
            <el-icon><User /></el-icon>
            <span>关注</span>
          </div>
          <div
            class="nav-item"
            :class="{ active: currentTab === 'followers' }"
            @click="switchTab('followers')"
          >
            <el-icon><UserFilled /></el-icon>
            <span>粉丝</span>
          </div>
          <div
            class="nav-item"
            :class="{ active: currentTab === 'thoughts' }"
            @click="switchTab('thoughts')"
          >
            <el-icon><ChatLineSquare /></el-icon>
            <span>想法</span>
          </div>
          <div
            class="nav-item"
            :class="{ active: currentTab === 'reviews' }"
            @click="switchTab('reviews')"
          >
            <el-icon><Star /></el-icon>
            <span>书评</span>
          </div>
        </div>

        <!-- 右侧内容区域 -->
        <div class="content-area">
          <!-- 发布内容 -->
          <div v-if="currentTab === 'posts'" class="posts-container">
            <!-- 标题和统计信息 -->
            <div class="section-title-with-stats">
              <div class="section-title-left">
                <h3>我的帖子</h3>
                <div class="title-line"></div>
              </div>
              <div class="section-title-stats">
                <div class="stat-item">
                  <span class="stat-number">{{ userStats.postCount }}</span>
                  <span class="stat-label">发布</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ totalLikes }}</span>
                  <span class="stat-label">获赞</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ totalComments }}</span>
                  <span class="stat-label">评论</span>
                </div>
              </div>
            </div>

            <!-- 帖子列表 -->
            <div class="posts-list">
              <div v-for="post in userPosts" :key="post.id" class="post-item">
                <PostCard
                  v-bind="post"
                  :show-follow-button="false"
                  @like="(likeCount, isLiked) => handleLike(post.id, likeCount, isLiked)"
                  @comment="handleComment"
                />

                <!-- 删除按钮 -->
                <div class="delete-action">
                  <button class="delete-btn" @click="handleDeletePost(post.id)">
                    <el-icon><Delete /></el-icon>
                    删除
                  </button>
                </div>
              </div>
            </div>

            <!-- 空状态 -->
            <div v-if="userPosts.length === 0" class="empty-state">
              <div class="empty-illustration">
                <div class="empty-icon">✍️</div>
              </div>
              <h3>还没有发布过任何帖子</h3>
              <p class="empty-hint">分享你的阅读心得，开始你的创作之旅吧！</p>
              <button class="create-post-btn" @click="goToCreatePost">立即发布</button>
            </div>
          </div>

          <!-- 关注列表 - 使用 UserList 组件 -->
          <div v-if="currentTab === 'following'" class="user-list-wrapper">
            <UserList type="following" :users="followingList" @update="handleFollowingUpdate" />
          </div>

          <!-- 粉丝列表 - 使用 UserList 组件 -->
          <div v-if="currentTab === 'followers'" class="user-list-wrapper">
            <UserList type="followers" :users="followersList" @update="handleFollowersUpdate" />
          </div>

          <!-- 想法列表 -->
          <div v-if="currentTab === 'thoughts'" class="thoughts-container">
            <div class="section-title-with-stats">
              <div class="section-title-left">
                <h3>我的想法</h3>
                <div class="title-line"></div>
              </div>
              <div class="section-title-stats">
                <div class="stat-item">
                  <span class="stat-number">{{ thoughts.length }}</span>
                  <span class="stat-label">想法</span>
                </div>
              </div>
            </div>

            <!-- 想法列表 - 使用 ThoughtCard 组件 -->
            <div class="thoughts-list">
              <ThoughtCard
                v-for="item in thoughts"
                :key="item.id"
                :thought="item"
                @delete="handleDeleteThought"
              />
            </div>

            <div v-if="thoughts.length === 0" class="empty-state">
              <div class="empty-illustration">
                <div class="empty-icon">💭</div>
              </div>
              <h3>还没有任何想法</h3>
              <p class="empty-hint">记录你的阅读感悟，分享你的思考</p>
              <button class="create-post-btn" @click="goToCreateThought">记录想法</button>
            </div>
          </div>

          <!-- 书评列表 -->
          <div v-if="currentTab === 'reviews'" class="reviews-container">
            <div class="section-title-with-stats">
              <div class="section-title-left">
                <h3>我的书评</h3>
                <div class="title-line"></div>
              </div>
              <div class="section-title-stats">
                <div class="stat-item">
                  <span class="stat-number">{{ reviews.length }}</span>
                  <span class="stat-label">书评</span>
                </div>
              </div>
            </div>

            <!-- 书评列表 - 使用 ReviewCard 组件 -->
            <div class="reviews-list">
              <ReviewCard
                v-for="review in reviews"
                :key="review.id"
                :review="review"
                :rating-config="ratingConfig"
                @delete="handleDeleteReview"
              />
            </div>

            <div v-if="reviews.length === 0" class="empty-state">
              <div class="empty-illustration">
                <div class="empty-icon">📚</div>
              </div>
              <h3>还没有任何书评</h3>
              <p class="empty-hint">写下你的读书感悟，分享你的见解</p>
              <button class="create-post-btn" @click="goToCreateReview">撰写书评</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Delete, Edit, User, UserFilled, ChatLineSquare, Star } from '@element-plus/icons-vue'
import NavBar from '@/components/layout/NavBar.vue'
import PostCard from '@/components/community/PostCard.vue'
import BackToTop from '@/components/layout/BackToTop.vue'
import FloatingAddButton from '@/components/community/FloatingAddButton.vue'
import ThoughtCard from '@/components/userposts/ThoughtCard.vue'
import ReviewCard from '@/components/userposts/ReviewCard.vue'
import UserList from '@/components/userposts/UserList.vue'
import type { Post } from '@/types/post'
import type { FollowUser } from '@/types/user'
import type { ReviewCardItem, RatingConfig } from '@/types/review'
import { useTitle } from '@/stores/useTitle'
import { getPosts } from '@/api/userPosts'
const props = defineProps<{ id?: string }>()
const route = useRoute()

// 当前选中的标签页
const currentTab = ref('posts')

// 切换标签页
const switchTab = (tab: string) => {
  currentTab.value = tab
}

// 跳转到发布页面
const goToCreatePost = () => {
  console.log('跳转到发布页面')
  // router.push('/create-post')
}

// 跳转到创建想法页面
const goToCreateThought = () => {
  console.log('跳转到创建想法页面')
  // router.push('/create-thought')
}

// 跳转到创建书评页面
const goToCreateReview = () => {
  console.log('跳转到创建书评页面')
  // router.push('/create-review')
}

// 用户统计信息
const userStats = ref({
  postCount: 8,
  followingCount: 24,
  followerCount: 156,
})

// 计算总点赞数
const totalLikes = computed(() => {
  return userPosts.value.reduce((sum, post) => sum + post.likeCount, 0)
})

// 计算总评论数
const totalComments = computed(() => {
  return userPosts.value.reduce((sum, post) => sum + post.commentCount, 0)
})

// 用户发布的帖子数据
const userPosts = ref<Post[]>([])
onMounted(async () => {
  window.scrollTo(0, 0)
  const tabParam = route.query.tab as string
  if (tabParam && ['posts', 'following', 'followers', 'thoughts', 'reviews'].includes(tabParam)) {
    currentTab.value = tabParam
  }
  const res = await getPosts()
  userPosts.value = res.list
  userStats.value.postCount = res.total
})

// 想法数据
const thoughts = ref([
  {
    id: 1,
    bookName: '置身事内',
    date: '2025-05-20',
    thought:
      '地方政府热衷开发区的本质是在经营土地。风险在于人口流入一旦停止，游戏就难以为继。作者通过详实的案例和数据，揭示了地方政府与土地财政之间的紧密关系，让我对中国的经济运作有了更深入的理解。这种发展模式的可持续性值得思考。',
    quote: '土地财政的本质，是政府将未来的土地收益提前变现。',
  },
  {
    id: 2,
    bookName: '当尼采哭泣',
    date: '2025-04-29',
    thought:
      '布雷尔医生是我们大多数人的缩影，拥有世俗的成功，内心却充满对"未选生活"的恐惧。欧文·亚隆通过这部小说展现了心理治疗的魅力，尼采与布雷尔之间的对话充满了哲学智慧。每个人都在某种程度上害怕过自己真正想要的生活，这种恐惧往往源于对未知的担忧和对现有安全感的依恋。',
    quote: '通过这一层层的面具，我看到了那个孤独的人。他不仅害怕死，更害怕生。',
  },
  {
    id: 3,
    bookName: '长安的荔枝',
    date: '2025-02-15',
    thought:
      '职场生存守则第一条：永远不要相信领导画的饼，除非饼已经在你嘴里了。马伯庸通过唐代小吏的故事，生动展现了古代职场的种种规则。虽然时代不同，但人性与职场规则的本质并没有太大变化。这本书让我反思现代职场中的种种现象，以及如何在复杂的环境中保持自己的原则。',
    quote: '流程，是弱者才遵守的规矩。',
  },
  {
    id: 4,
    bookName: '三体II',
    date: '2025-01-10',
    thought:
      '罗辑才是真正的面壁者，欺骗世界也欺骗自己，只为最后的对决。黑暗森林理论让我重新思考宇宙文明之间的关系。刘慈欣的想象力令人惊叹，他将宏大的宇宙观与细腻的人性描写完美结合。这本书不仅是一部科幻作品，更是对人类社会、道德和文明的深刻思考。',
    quote: '我有一个梦，也许有一天，灿烂的阳光能照进黑暗森林。',
  },
])

// 书评数据
const defaultCover =
  'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=300'

// 定义评级对应的配置
const ratingConfig: RatingConfig = {
  recommend: { label: '推荐', className: 'tag-recommend' },
  average: { label: '一般', className: 'tag-average' },
  bad: { label: '不行', className: 'tag-bad' },
}

const reviews = ref<ReviewCardItem[]>([
  {
    id: 1,
    userName: '当前用户',
    bookName: '置身事内',
    cover: defaultCover,
    rating: 'recommend',
    date: '2024-05-20',
    likes: 128,
    content:
      '这本书彻底改变了我对宏观经济的看法。它不是枯燥的理论堆砌，而是从地方政府的微观视角切入，通过详实的案例和数据，揭示了中国经济发展背后的真实逻辑。作者兰小欢教授用通俗易懂的语言，将复杂的政治经济学问题讲得深入浅出。特别值得一提的是，书中对地方政府与土地财政关系的分析，让我对城市化进程有了全新的认识。这本书不仅适合经济学专业的学生，也适合所有关心中国发展的普通读者。',
  },
  {
    id: 2,
    userName: '当前用户',
    bookName: '当尼采哭泣',
    cover:
      'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&q=80&w=300',
    rating: 'recommend',
    date: '2024-04-15',
    likes: 45,
    content:
      '欧文·亚隆将哲学和心理学融合得太完美了。这本书不仅是一部小说，更是一本心理治疗和哲学思考的杰作。作者虚构了尼采与布雷尔医生的会面，通过他们之间的对话，探讨了存在、孤独、自由、责任等深刻的哲学命题。书中的对话充满智慧，每一句话都值得反复品味。作为心理咨询的开山之作，它展现了心理治疗的魅力与深度。读完后，我不禁反思自己的人生选择和对生活的态度。',
  },
  {
    id: 3,
    userName: '当前用户',
    bookName: '长安的荔枝',
    cover:
      'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?auto=format&fit=crop&q=80&w=300',
    rating: 'recommend',
    date: '2024-02-10',
    likes: 89,
    content:
      '马伯庸用一个小人物的视角，展现了大唐盛世的另一面。故事围绕"一骑红尘妃子笑，无人知是荔枝来"展开，讲述了小吏李善德如何克服重重困难，将鲜荔枝从岭南运到长安的惊险历程。这本书不仅是一部历史小说，更是一部职场生存指南。作者通过古代的故事，反映了现代职场的种种现象。书中的智慧和幽默让我在阅读过程中既感受到历史的厚重，又不乏轻松的阅读体验。',
  },
])

// 关注列表数据
const followingList = ref<FollowUser[]>([
  {
    id: 1,
    username: '鱼鹅来了',
    avatar: 'https://picsum.photos/100?random=10',
    bio: '每天18点开播 请多多关注我 Q群1032791648',
    isFollowing: true,
  },
  {
    id: 2,
    username: 'Borseronie',
    avatar: 'https://picsum.photos/100?random=11',
    bio: '成为砂砾、成为羽毛、成为狼。',
    isFollowing: true,
  },
  {
    id: 3,
    username: '哔哩哔哩会员购',
    avatar: 'https://picsum.photos/100?random=12',
    bio: '【关注网购】每日为你传递最新的手办周边...',
    isFollowing: true,
  },
  {
    id: 4,
    username: '哔哩哔哩漫画',
    avatar: 'https://picsum.photos/100?random=13',
    bio: '漫画授权，联动合作，品牌联名请发送邮件...',
    isFollowing: true,
  },
  {
    id: 5,
    username: '读书达人小王',
    avatar: 'https://picsum.photos/100?random=14',
    bio: '每周读一本书，分享读书笔记',
    isFollowing: true,
  },
  {
    id: 6,
    username: '文学爱好者小李',
    avatar: 'https://picsum.photos/100?random=15',
    bio: '专注于经典文学作品的解读',
    isFollowing: true,
  },
])

// 粉丝列表数据
const followersList = ref<FollowUser[]>([
  {
    id: 7,
    username: '新读者',
    avatar: 'https://picsum.photos/100?random=16',
    bio: '刚刚开始阅读之旅的新手',
    isFollowing: true,
  },
  {
    id: 8,
    username: '书虫小张',
    avatar: 'https://picsum.photos/100?random=17',
    bio: '每天都要读书的忠实读者',
    isFollowing: false,
  },
  {
    id: 9,
    username: '文学评论家',
    avatar: 'https://picsum.photos/100?random=18',
    bio: '专业文学评论，深度解析作品',
    isFollowing: true,
  },
  {
    id: 10,
    username: '读书分享者',
    avatar: 'https://picsum.photos/100?random=19',
    bio: '分享好书，交流心得',
    isFollowing: false,
  },
  {
    id: 11,
    username: '小说爱好者',
    avatar: 'https://picsum.photos/100?random=20',
    bio: '热爱各种类型的小说',
    isFollowing: false,
  },
  {
    id: 12,
    username: '历史书迷',
    avatar: 'https://picsum.photos/100?random=21',
    bio: '专注于历史类书籍的阅读',
    isFollowing: true,
  },
])

// 处理点赞
const handleLike = (postId: number, likeCount: number, isLiked: boolean) => {
  const post = userPosts.value.find((p) => p.id === postId)
  if (post) {
    post.likeCount = likeCount
    post.isLiked = isLiked
  }
}

// 处理评论
const handleComment = () => {
  console.log('跳转到评论页面')
}

// 删除帖子
const handleDeletePost = async (postId: number) => {
  userPosts.value = userPosts.value.filter((post) => post.id !== postId)
  userStats.value.postCount = userPosts.value.length
  ElMessage.success('帖子删除成功')
}

// 删除想法
const handleDeleteThought = (thoughtId: number) => {
  thoughts.value = thoughts.value.filter((thought) => thought.id !== thoughtId)
  ElMessage.success('想法删除成功')
}

// 删除书评
const handleDeleteReview = (id: string | number) => {
  reviews.value = reviews.value.filter((review) => review.id !== id)
  ElMessage.success('书评删除成功')
}

// 更新关注列表
const handleFollowingUpdate = (updatedUsers: FollowUser[]) => {
  followingList.value = updatedUsers
  userStats.value.followingCount = updatedUsers.length
}

// 更新粉丝列表
const handleFollowersUpdate = (updatedUsers: FollowUser[]) => {
  followersList.value = updatedUsers
  userStats.value.followerCount = updatedUsers.length
}

// 监听路由参数变化，以便从UserProfileCard跳转时能切换到对应标签页
onMounted(() => {
  window.scrollTo(0, 0)

  // 检查是否有tab参数
  const tabParam = route.query.tab as string
  if (tabParam && ['posts', 'following', 'followers', 'thoughts', 'reviews'].includes(tabParam)) {
    currentTab.value = tabParam
  }
})

// 监听路由变化
watch(
  () => route.query.tab,
  (newTab) => {
    if (
      newTab &&
      ['posts', 'following', 'followers', 'thoughts', 'reviews'].includes(newTab as string)
    ) {
      currentTab.value = newTab as string
    }
  },
)

const tabTitleMap: Record<string, string> = {
  posts: '我的发布',
  following: '我的关注',
  followers: '我的粉丝',
  thoughts: '我的想法',
  reviews: '我的书评',
}

const pageTitle = computed(() => {
  const tabTitle = tabTitleMap[currentTab.value] || '个人中心'
  return `微信读书 - ${tabTitle}`
})

useTitle(pageTitle)
</script>

<style scoped>
.user-posts-page {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  min-height: 100vh;
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  margin-top: 28px;
}

/* 左右分栏布局 */
.main-layout {
  display: flex;
  gap: 24px;
  margin-top: 20px;
}

/* 左侧导航栏 */
.sidebar {
  width: 200px;
  background: white;
  border: 1px solid var(--border-color);
  padding: 16px 0;
  flex-shrink: 0;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 24px;
  cursor: pointer;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  border-left: 3px solid transparent;
}

.nav-item:hover {
  background: #f8fafc;
  color: var(--primary-green);
}

.nav-item.active {
  background: rgba(126, 180, 143, 0.1); /* 使用 --bg-green 的浅色版本 */
  color: var(--primary-green);
  border-left: 3px solid var(--primary-green);
}

.nav-item .el-icon {
  font-size: 18px;
}

/* 右侧内容区域 */
.content-area {
  flex: 1;
  min-width: 0; /* 防止flex item溢出 */
}

/* 所有页面的标题与统计区域 */
.section-title-with-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 24px;
}

.section-title-left {
  flex: 1;
  min-width: 200px;
}

.section-title-with-stats h3 {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.section-title-with-stats .title-line {
  width: 60px;
  height: 3px;
  background: var(--primary-green);
}

.section-title-stats {
  display: flex;
  gap: 24px;
  margin-top: 16px;
}

.section-title-stats .stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  min-width: 60px;
}

.section-title-stats .stat-number {
  font-size: 16px;
  font-weight: 700;
  color: var(--primary-green);
}

.section-title-stats .stat-label {
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: 500;
}

/* 发布容器样式 - 与其他页面保持一致 */
.posts-container {
  background: white;
  border: 1px solid var(--border-color);
  padding: 24px;
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 帖子项样式 */
.post-item {
  border: 1px solid var(--border-color);
  padding: 0;
}

/* 关键修改：调整帖子卡片的外边距和内边距 */
.post-item :deep(.post-card) {
  margin-bottom: 0;
  border: none;
}

/* 删除按钮样式 */
.delete-action {
  padding: 12px 20px;
  display: flex;
  justify-content: flex-end;
  background: white;
  border-top: 1px solid var(--border-color);
}

.delete-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #f5f5f5;
  color: var(--text-secondary);
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.delete-btn:hover {
  background: var(--primary-pink);
  color: white;
  border-color: var(--primary-pink);
}

/* 用户列表包装器 */
.user-list-wrapper {
  background: white;
  border: 1px solid var(--border-color);
  padding: 24px;
}

/* 想法容器样式 */
.thoughts-container {
  background: white;
  border: 1px solid var(--border-color);
  padding: 24px;
}

.thoughts-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 书评容器样式 */
.reviews-container {
  background: white;
  border: 1px solid var(--border-color);
  padding: 24px;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border: 1px solid var(--border-color);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.empty-hint {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 24px;
}

.create-post-btn {
  padding: 10px 24px;
  background: var(--primary-green);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.create-post-btn:hover {
  background: var(--thrid-green);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-content {
    padding: 16px;
  }

  .main-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    height: auto;
    overflow-y: visible;
  }

  /* 移动端适配：标题与统计区域 */
  .section-title-with-stats {
    flex-direction: column;
    align-items: flex-start;
  }

  .section-title-stats {
    width: 100%;
    justify-content: flex-start;
    padding: 0;
  }
}
</style>
