<template>
  <div class="topic-detail">
    <NavBar />

    <!-- 话题头部 -->
    <div class="topic-header">
      <div class="topic-cover" :style="{ backgroundImage: `url(${topic.cover})` }">
        <div class="cover-overlay"></div>
      </div>

      <div class="topic-info">
        <h1 class="topic-title">{{ topic.title }}</h1>
        <p class="topic-description">{{ topic.description }}</p>

        <div class="topic-stats">
          <div class="stat-item">
            <span class="stat-number">{{ topic.postCount }}</span>
            <span class="stat-label">帖子</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ topic.followerCount }}</span>
            <span class="stat-label">关注者</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ topic.dailyActive }}</span>
            <span class="stat-label">今日活跃</span>
          </div>
        </div>

        <div class="topic-actions">
          <button
            class="follow-btn"
            :class="{ followed: isFollowing }"
            @click="toggleFollow"
          >
            {{ isFollowing ? '已关注' : '关注话题' }}
          </button>
        </div>
      </div>
    </div>

    <div class="topic-content">
      <!-- 主要内容 -->
      <div class="main-content">
        <!-- 筛选标签栏 -->
        <div class="filter-tabs">
          <button
            v-for="tab in filterTabs"
            :key="tab.value"
            :class="{ active: currentFilter === tab.value }"
            @click="changeFilter(tab.value)"
          >
            {{ tab.label }}
          </button>
        </div>

        <!-- 帖子列表 -->
        <div class="posts-container">
          <PostCard
            v-for="post in filteredPosts"
            :key="post.id"
            v-bind="post"
            @follow-change="handleFollowChange"
            @like="handleLike"
            @comment="handleComment"
            @share="handleShare"
          />

          <!-- 加载更多 -->
          <div v-if="hasMore" class="load-more" @click="loadMore">
            加载更多...
          </div>

          <!-- 空状态 -->
          <div v-else-if="filteredPosts.length === 0" class="empty-state">
            <div class="empty-icon">📝</div>
            <p>这个话题下还没有帖子</p>
            <p class="empty-hint">成为第一个分享的人吧！</p>
          </div>
        </div>
      </div>

      <!-- 侧边栏 -->
      <div class="sidebar">
        <!-- 话题信息卡片 -->
        <div class="topic-info-card sidebar-card">
          <h3 class="card-title">关于这个话题</h3>
          <p class="topic-full-description">{{ topic.fullDescription }}</p>

          <div class="topic-meta">
            <div class="meta-item">
              <span class="meta-label">创建时间:</span>
              <span class="meta-value">{{ topic.createTime }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">管理员:</span>
              <span class="meta-value">{{ topic.manager }}</span>
            </div>
          </div>
        </div>

        <!-- 相关话题推荐 -->
        <div class="related-topics sidebar-card">
          <div class="related-topics-header">
            <h3 class="card-title">相关话题</h3>
            <button class="refresh-btn" @click="refreshRelatedTopics">
              换一批
            </button>
          </div>
          <div
            v-for="relatedTopic in relatedTopics"
            :key="relatedTopic.id"
            class="related-topic-item"
            @click="goToTopic(relatedTopic.id)"
          >
            <div class="related-topic-cover">
              <img :src="relatedTopic.cover" alt="话题封面">
            </div>
            <div class="related-topic-info">
              <h4>{{ relatedTopic.title }}</h4>
              <span>{{ relatedTopic.postCount }} 篇帖子</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavBar from '@/components/layout/NavBar.vue'
import PostCard from '@/components/community/PostCard.vue'

const route = useRoute()
const router = useRouter()
const topicId = route.params.id as string

// 响应式数据
const topic = ref({
  id: topicId,
  title: '每日读点小说',
  description: '分享你最近阅读的小说，交流阅读心得',
  fullDescription: '这是一个专注于小说阅读与分享的社区话题。无论你是喜欢经典文学、网络小说、还是国外名著，都可以在这里找到志同道合的朋友。欢迎大家分享阅读笔记、书评和推荐书单！',
  cover: 'https://picsum.photos/800/400?random=1',
  postCount: 200,
  followerCount: 1500,
  dailyActive: 89,
  createTime: '2024-01-15',
  manager: '文学社官方'
})

const posts = ref([
  {
    id: 1,
    username: '书虫小王',
    avatar: 'https://picsum.photos/100?random=10',
    postTime: '2小时前',
    title: '《百年孤独》读后感',
    content: '刚刚读完马尔克斯的《百年孤独》，这本书真的是一种奇妙的阅读体验。书中通过布恩迪亚家族几代人的命运，展现了时间的循环与宿命的荒诞。每个人都在追寻意义，但又被历史的轮回所吞没。尤其是书中的文字节奏，那种冷静而又充满诗意的叙述，让人不自觉地沉浸进去。读到最后，我甚至分不清哪些是真实，哪些是幻觉。魔幻与现实在这里不再有界限，而人的孤独似乎是永恒的。推荐每一个喜欢文学的人都读一读这本书。',
    likeCount: 128,
    commentCount: 23,
    shareCount: 8,
    isFollowing: false,
    isLiked: false,
    book: {
      id: 101,
      title: '百年孤独',
      author: '加西亚·马尔克斯',
      cover: 'https://picsum.photos/100/150?random=1',
    },
  },
  {
    id: 2,
    username: '小说爱好者',
    avatar: 'https://picsum.photos/100?random=11',
    postTime: '5小时前',
    title: '推荐几本近期读的好小说',
    content: '最近读了《围城》《平凡的世界》和《活着》，每本都让我感触很深。《围城》的幽默讽刺，《平凡的世界》的厚重真实，《活着》的生命力量，都值得一读。特别是《活着》，虽然故事很沉重，但读完后对生命有了更深的理解。',
    likeCount: 89,
    commentCount: 15,
    shareCount: 12,
    isFollowing: true,
    isLiked: true,
    book: null,
  },
  {
    id: 3,
    username: '文学青年',
    avatar: 'https://picsum.photos/100?random=12',
    postTime: '昨天',
    title: '《红楼梦》人物分析：林黛玉',
    content: '重读《红楼梦》，对林黛玉这个角色有了新的认识。她不仅仅是多愁善感的才女，更是一个有着独立思想和反抗精神的女性。在封建社会的背景下，她的悲剧命运让人唏嘘，但她的才情和个性却永远闪耀。',
    likeCount: 156,
    commentCount: 42,
    shareCount: 31,
    isFollowing: false,
    isLiked: true,
    book: {
      id: 102,
      title: '红楼梦',
      author: '曹雪芹',
      cover: 'https://picsum.photos/100/150?random=2',
    },
  }
])

const isFollowing = ref(false)
const currentFilter = ref('latest')
const hasMore = ref(true)
const page = ref(1)

// 筛选选项
const filterTabs = [
  { value: 'latest', label: '最新' },
  { value: 'hot', label: '热门' },
  { value: 'featured', label: '精华' }
]

// 相关话题数据池
const allRelatedTopics = ref([
  { id: 2, cover: 'https://picsum.photos/200?random=2', title: '科幻爱好者', postCount: 156 },
  { id: 3, cover: 'https://picsum.photos/200?random=3', title: '经典文学', postCount: 320 },
  { id: 4, cover: 'https://picsum.photos/200?random=4', title: '读书笔记精选', postCount: 187 },
  { id: 5, cover: 'https://picsum.photos/200?random=5', title: '外国名著', postCount: 98 },
  { id: 6, cover: 'https://picsum.photos/200?random=6', title: '推理与悬疑', postCount: 240 },
  { id: 7, cover: 'https://picsum.photos/200?random=7', title: '诗歌与散文', postCount: 142 },
  { id: 8, cover: 'https://picsum.photos/200?random=8', title: '新书速递', postCount: 75 },
  { id: 9, cover: 'https://picsum.photos/200?random=9', title: '阅读打卡挑战', postCount: 310 }
])

// 当前显示的相关话题
const relatedTopics = ref(allRelatedTopics.value.slice(0, 3))

// 计算属性
const filteredPosts = computed(() => {
  // 这里可以根据currentFilter对posts进行排序和过滤
  // 暂时返回所有帖子
  return posts.value
})

// 方法
const toggleFollow = () => {
  isFollowing.value = !isFollowing.value
  if (isFollowing.value) {
    topic.value.followerCount++
  } else {
    topic.value.followerCount--
  }
}

const changeFilter = (filter: string) => {
  currentFilter.value = filter
  // 这里可以添加根据筛选条件重新获取帖子的逻辑
}

const loadMore = () => {
  page.value++
  // 这里可以添加加载更多帖子的逻辑
}

const goToTopic = (id: string) => {
  router.push(`/topicdetail/${id}`)
}

// 刷新相关话题
const refreshRelatedTopics = () => {
  // 打乱数组并取前3个
  const shuffled = [...allRelatedTopics.value]
    .sort(() => 0.5 - Math.random())
    .slice(0, 3)
  relatedTopics.value = shuffled
}

// PostCard 事件处理
const handleFollowChange = (postId: number, isFollowing: boolean) => {
  const post = posts.value.find(p => p.id === postId)
  if (post) {
    post.isFollowing = isFollowing
  }
}

const handleLike = (postId: number, likeCount: number, isLiked: boolean) => {
  const post = posts.value.find(p => p.id === postId)
  if (post) {
    post.likeCount = likeCount
    post.isLiked = isLiked
  }
}

const handleComment = (postId: number) => {
  // 这里可以添加跳转到评论页面或打开评论弹窗的逻辑
  console.log('评论帖子:', postId)
}

const handleShare = (postId: number) => {
  // 这里可以添加分享逻辑
  console.log('分享帖子:', postId)
}

onMounted(() => {
  // 这里可以添加初始化数据获取的逻辑
  // fetchTopicDetail()
  // fetchTopicPosts()
})
</script>

<style scoped>
.topic-detail {
  background-color: #f1f1f1;
  min-height: 100vh;
}

.topic-header {
  position: relative;
  background: white;

  max-width: 1200px; /* 与 .topic-content 保持一致 */
  margin: 20px auto; /* 居中显示 */
}

.topic-cover {
  height: 200px;
  background-size: cover;
  background-position: center;
  position: relative;
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(transparent, rgba(0,0,0,0.3));
}

.topic-info {
  padding: 30px;
  text-align: center;
}

.topic-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 12px;
  color: #333;
}

.topic-description {
  font-size: 18px;
  color: #666;
  margin-bottom: 24px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.topic-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 24px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.topic-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.follow-btn {
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 10px 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.follow-btn:hover {
  background: #ff8787;
}

.follow-btn.followed {
  background: #ccc;
}

.follow-btn.followed:hover {
  background: #999;
}

.topic-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.main-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 筛选标签栏样式 - 增大按钮并添加圆角 */
.filter-tabs {
  display: flex;
  gap: 16px;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
}

.filter-tabs button {
  padding: 12px 24px;
  border: none;
  background: none;
  cursor: pointer;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #666;
  transition: all 0.2s ease;
  flex: 1;
}

.filter-tabs button:hover {
  background: #f5f5f5;
  transform: translateY(-1px);
}

.filter-tabs button.active {
  background: #64adf7;
  color: white;
  box-shadow: 0 2px 6px rgba(0, 127, 255, 0.3);
}

.posts-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.load-more {
  text-align: center;
  padding: 20px;
  color: #007fff;
  cursor: pointer;
  background: white;
  border-radius: 8px;
  transition: background 0.2s ease;
}

.load-more:hover {
  background: #f0f8ff;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-hint {
  color: #666;
  font-size: 14px;
  margin-top: 8px;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sidebar-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
  padding: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
}

.topic-full-description {
  color: #666;
  line-height: 1.6;
  margin-bottom: 16px;
}

.topic-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.meta-label {
  color: #999;
}

.meta-value {
  color: #333;
}

/* 相关话题推荐样式 */
.related-topics-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0;
}

.refresh-btn {
  background: #f5f5f5;
  border: none;
  border-radius: 6px;
  padding: 6px 12px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s ease;
}

.refresh-btn:hover {
  background: #e8e8e8;
  color: #333;
}

.related-topics {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.related-topic-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 18px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.related-topic-item:hover {
  background: #f5f5f5;
  transform: translateX(2px);
}

.related-topic-cover {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0;
}

.related-topic-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.related-topic-info {
  flex: 1;
  min-width: 0;
}

.related-topic-info h4 {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.related-topic-info span {
  font-size: 12px;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .topic-content {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .sidebar {
    display: none;
  }
}

@media (max-width: 768px) {
  .topic-header {
    margin-bottom: 16px;
  }

  .topic-info {
    padding: 20px;
  }

  .topic-title {
    font-size: 24px;
  }

  .topic-stats {
    gap: 20px;
  }

  .topic-actions {
    flex-direction: column;
    gap: 12px;
  }

  .topic-content {
    padding: 0 16px;
  }

  .filter-tabs {
    padding: 16px;
  }

  .filter-tabs button {
    padding: 10px 16px;
    font-size: 14px;
  }
}
</style>
