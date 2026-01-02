<template>
  <div class="topic-detail">
    <NavBar />
    <FloatingAddButton :current-topic="topic" />
    <!-- 添加发布按钮 -->
    <BackToTop />
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
            <span class="stat-label">今日发帖</span>
          </div>
        </div>

        <div class="topic-actions">
          <button class="follow-btn" :class="{ followed: isFollowing }" @click="toggleFollow">
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
            v-for="post in sortedPosts"
            :key="post.id"
            v-bind="post"
            @follow-change="(isFollowing) => handleFollowChange(post.id, isFollowing)"
            @like="(likeCount, isLiked) => handleLike(post.id, likeCount, isLiked)"
            @comment="() => handleComment(post.id)"
          />

          <!-- 加载更多 -->
          <div v-if="hasMore" class="load-more" @click="loadMore">加载更多...</div>

          <!-- 空状态 -->
          <div v-else-if="sortedPosts.length === 0" class="empty-state">
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
            <button class="refresh-btn" @click="refreshRelatedTopics">换一批</button>
          </div>
          <div
            v-for="relatedTopic in relatedTopics"
            :key="relatedTopic.id"
            class="related-topic-item"
            @click="goToTopic(relatedTopic.id)"
          >
            <div class="related-topic-cover">
              <img :src="relatedTopic.cover" alt="话题封面" />
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
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavBar from '@/components/layout/NavBar.vue'
import PostCard from '@/components/community/PostCard.vue'
import FloatingAddButton from '@/components/community/FloatingAddButton.vue'
import BackToTop from '@/components/layout/BackToTop.vue'
import { usePostInteractions } from '@/composables/usePostInteractions'
import { getTopicDetail, type TopicDetail } from '@/api/topics/topic-detail-header'
import { getTopicPosts } from '@/api/topics/topic-posts-section'
import { getHotTopics, type HotTopic } from '@/api/topics/hot-topics'
import type { Post } from '@/types/post'

const route = useRoute()
const router = useRouter()
const topicId = route.params.id as string

// 初始化数据
const topic = ref<TopicDetail>({
  id: '',
  title: '',
  name: '',
  description: '',
  fullDescription: '',
  cover: '',
  postCount: 0,
  followerCount: 0,
  dailyActive: 0,
  createTime: '',
  manager: '',
  relatedTopics: [],
})
const posts = ref<Post[]>([])
const hotTopics = ref<HotTopic[]>([])
const isLoading = ref(true)
const error = ref<string | null>(null)

// 相关话题数据池
const allRelatedTopicsRef = ref<Array<{id: string, cover: string, title: string, postCount: number}>>([])

// 相关话题批次索引
const relatedTopicsBatchIndex = ref(0)

// 当前显示的相关话题（每次显示3个，按批次切换）
const relatedTopics = computed(() => {
  const startIndex = relatedTopicsBatchIndex.value * 3
  return allRelatedTopicsRef.value.slice(startIndex, startIndex + 3)
})

// 获取话题数据
const fetchTopicData = async (id: string) => {
  console.log('正在获取话题数据:', id)

  try {
    // 获取话题详情
    const topicDetailData = await getTopicDetail(id)
    topic.value = topicDetailData
  } catch (err) {
    console.error('获取话题详情失败:', err)
    error.value = '获取话题详情失败'
  }

  try {
    // 获取所有话题帖子（一次性获取足够多的数据用于排序）
    const postsData = await getTopicPosts(id, 'latest', 1, 100) // 获取100个帖子用于排序
    posts.value = postsData
  } catch (err) {
    console.error('获取话题帖子失败:', err)
    error.value = '获取帖子失败'
  }

  try {
    // 获取热门话题
    const hotTopicsData = await getHotTopics()
    hotTopics.value = hotTopicsData
  } catch (err) {
    console.error('获取热门话题失败:', err)
    error.value = '获取热门话题失败'
  }

  // 合并热门话题和相关话题作为相关话题池
  const allRelatedTopics = [
    ...hotTopics.value.slice(0, 5), // 取前5个热门话题
    ...(topic.value.relatedTopics || [])
  ]

  // 更新相关话题数据
  allRelatedTopicsRef.value = allRelatedTopics.map((t, index) => ({
    id: t.id || `related-${index}`,
    cover: t.cover,
    title: t.title,
    postCount: t.postCount,
  }))

  // 重置相关话题批次索引
  relatedTopicsBatchIndex.value = 0

  console.log('获取到的数据:', {
    topic: topic.value,
    posts: posts.value,
    relatedTopics: allRelatedTopicsRef.value,
  })

  isLoading.value = false
}

onMounted(async () => {
  await fetchTopicData(topicId)
  // 滚动到页面顶部
  window.scrollTo(0, 0)
})

const isFollowing = ref(false)
// 自定义的帖子列表逻辑，支持本地排序
const currentFilter = ref<'latest' | 'hot' | 'quality'>('latest')
const page = ref(1)
const pageSize = 20

const filterTabs = [
  { value: 'latest', label: '最新' },
  { value: 'hot', label: '热门' },
  { value: 'quality', label: '精华' },
]

// 排序函数
const sortPosts = (posts: Post[], filter: 'latest' | 'hot' | 'quality') => {
  const sorted = [...posts]
  switch (filter) {
    case 'latest':
      // 最新：按时间排序（最新的在前面）
      return sorted.sort((a, b) => (b.timestamp ?? 0) - (a.timestamp ?? 0))
    case 'hot':
      // 热门：按评论数排序（评论数多的在前面）
      return sorted.sort((a, b) => b.commentCount - a.commentCount)
    case 'quality':
      // 精华：按点赞数排序（点赞数多的在前面）
      return sorted.sort((a, b) => b.likeCount - a.likeCount)
    default:
      return sorted
  }
}

// 显示的帖子（先排序再分页）
const displayedPosts = computed(() => {
  const sorted = sortPosts(posts.value, currentFilter.value)
  const startIndex = 0
  const endIndex = page.value * pageSize
  return sorted.slice(startIndex, endIndex)
})

// 检查是否还有更多数据
const hasMore = computed(() => {
  const sorted = sortPosts(posts.value, currentFilter.value)
  return displayedPosts.value.length < sorted.length
})

// 切换筛选条件
const changeFilter = (filter: 'latest' | 'hot' | 'quality') => {
  if (currentFilter.value !== filter) {
    currentFilter.value = filter
    page.value = 1 // 重置分页
  }
}

// 加载更多
const loadMore = () => {
  if (hasMore.value) {
    page.value++
  }
}

// 为了兼容usePostInteractions，创建一个sortedPosts的别名
const sortedPosts = displayedPosts

// 方法
const toggleFollow = () => {
  isFollowing.value = !isFollowing.value
  if (isFollowing.value) {
    topic.value.followerCount++
  } else {
    topic.value.followerCount--
  }
}

const goToTopic = (id: string) => {
  router.push(`/topicdetail/${id}`)
  // 在新标签页打开话题详情页
  window.open(`/topicdetail/${id}`, '_blank')
}

// 刷新相关话题 - 显示下一批3个话题
const refreshRelatedTopics = () => {
  const totalBatches = Math.ceil(allRelatedTopicsRef.value.length / 3)
  relatedTopicsBatchIndex.value = (relatedTopicsBatchIndex.value + 1) % totalBatches
}

// PostCard 事件处理（统一封装）
const { updateFollow, updateLike } = usePostInteractions(posts)

const handleFollowChange = (postId: number, isFollowing: boolean) => {
  updateFollow(postId, isFollowing)
}

const handleLike = (postId: number, likeCount: number, isLiked: boolean) => {
  updateLike(postId, likeCount, isLiked)
}

const handleComment = (postId: number) => {
  // 这里可以添加跳转到评论页面或打开评论弹窗的逻辑
  console.log('评论帖子:', postId)
}
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
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.3));
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
  background: #007c27;
  color: white;
  box-shadow: 0 2px 6px  rgba(126, 180, 143, 0.757);;
}

.posts-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.load-more {
  text-align: center;
  padding: 20px;
  color: #007c27;
  cursor: pointer;
  background: white;
  border-radius: 8px;
  transition: background 0.2s ease;
}

.load-more:hover {
  background: #f0fff6;
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
