<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import NavBar from '@/components/layout/NavBar.vue'
import PostCard from '@/components/community/PostCard.vue'
import UserProfileCard from '@/components/community/UserProfileCard.vue'
import HotTopics from '@/components/community/HotTopics.vue'
import BackToTop from '@/components/layout/BackToTop.vue'
import Topic from '@/components/community/TopicCard.vue'
import FloatingAddButton from '@/components/community/FloatingAddButton.vue'
import CommentItem from '@/components/community/Mine/CommentItem.vue'
import LikeItem from '@/components/community/Mine/LikeItem.vue'
import Footer from '@/components/layout/Footer.vue'
import { useTitle } from '@/stores/useTitle'
import { usePostInteractions } from '@/composables/usePostInteractions'
import { fetchCommunityPosts, fetchMyComments, fetchMyLikes } from '@/api/community'
import type { Post } from '@/types/post'
import { getProfileHome } from '@/api/profile'
import { getTopicsList } from '@/api/topics/topics-list'
import { getHotTopics, type HotTopic } from '@/api/topics/hot-topics'

// 当前用户信息
const currentUser = reactive({
  userId: null as number | null,
  username: '加载中...',
  bio: '',
  avatar: '',
  followCount: 0,
  fansCount: 0,
  postCount: 0,
})

const currentUserId = computed(() => currentUser.userId)

// 热门话题
const hotTopics = ref<HotTopic[]>([])
const topicsList = ref<{ id: number; cover: string; title: string; number: number }[]>([])

// 话题列表分页相关
const topicsHasMore = ref(true)
const topicsNextCursor = ref<number | undefined>(undefined)
const topicsLoading = ref(false)
const loadMoreTrigger = ref<HTMLElement | null>(null) // 底部无感加载触发器
let observer: IntersectionObserver | null = null

// 加载话题列表
const loadTopicsList = async () => {
  if (topicsLoading.value || !topicsHasMore.value) return

  topicsLoading.value = true
  try {
    // 优化：单次请求20条，确保能填满屏幕高度，否则无法触发滚动
    const result = await getTopicsList(topicsNextCursor.value, 20)
    topicsList.value = [
      ...topicsList.value,
      ...result.items.map((item) => ({
        id: item.id as number,
        cover: item.cover,
        title: item.title,
        number: item.number,
      })),
    ]
    topicsHasMore.value = result.hasMore
    topicsNextCursor.value = result.nextCursor
  } catch (error) {
    console.error('加载话题列表失败:', error)
  } finally {
    topicsLoading.value = false
  }
}

// 初始化 IntersectionObserver (用于无感加载更多话题)
const setupObserver = () => {
  if (observer) observer.disconnect()

  observer = new IntersectionObserver(
    (entries) => {
      // 当触发器进入视口且有更多数据时加载
      if (entries[0].isIntersecting && currentTab.value === 'topics' && topicsHasMore.value) {
        loadTopicsList()
      }
    },
    { rootMargin: '100px' }, // 提前 100px 触发，体验更丝滑
  )

  if (loadMoreTrigger.value) {
    observer.observe(loadMoreTrigger.value)
  }
}

// 加载热门话题
const loadHotTopics = async () => {
  try {
    const topics = await getHotTopics()
    if (topics && topics.length > 0) {
      hotTopics.value = topics.slice(0, 9).map((topic) => ({
        id: topic.id,
        name: topic.name,
      }))
    } else {
      hotTopics.value = []
    }
  } catch (error) {
    console.error('加载热门话题失败:', error)
    hotTopics.value = []
  }
}

// 加载帖子数据
const loadPosts = async (type: 'square' | 'following' = 'square') => {
  try {
    const postsData = await fetchCommunityPosts(type)
    posts.value = postsData
  } catch (error) {
    console.error(`加载${type === 'square' ? '广场' : '关注'}帖子失败:`, error)
  }
}

onMounted(async () => {
  try {
    const [commentsResult, likesResult, profileResult] = await Promise.allSettled([
      fetchMyComments().catch(() => ({ comments: [], hasMore: false, nextCursor: null })),
      fetchMyLikes().catch(() => ({ likes: [], hasMore: false, nextCursor: null })),
      getProfileHome().catch(() => null),
    ])

    if (commentsResult.status === 'fulfilled') commentList.value = commentsResult.value.comments
    if (likesResult.status === 'fulfilled') likeList.value = likesResult.value.likes

    if (profileResult.status === 'fulfilled' && profileResult.value) {
      Object.assign(currentUser, {
        userId: profileResult.value.userId || null,
        username: profileResult.value.username || '用户',
        bio: profileResult.value.bio || '',
        avatar: profileResult.value.avatar || '',
        followCount: profileResult.value.followingCount || 0,
        fansCount: profileResult.value.followerCount || 0,
        postCount: profileResult.value.postCount || 0,
      })
    }
  } catch (error) {
    console.error('加载主要数据失败:', error)
  }

  // 首屏仅加载广场和热门话题，不强制加载话题列表，节省资源
  await Promise.all([loadPosts('square'), loadHotTopics()])

  // 绑定滚动监听器
  setupObserver()
})

onUnmounted(() => {
  if (observer) observer.disconnect()
})

const posts = ref<Post[]>([])
const commentList = ref<any[]>([])
const likeList = ref<any[]>([])
const currentTab = ref<'square' | 'following' | 'topics' | 'mine'>('square')
const mineTab = ref<'like' | 'comment'>('comment')

const loadMyData = async () => {
  try {
    const [commentsResult, likesResult] = await Promise.allSettled([
      fetchMyComments().catch(() => ({ comments: [], hasMore: false, nextCursor: null })),
      fetchMyLikes().catch(() => ({ likes: [], hasMore: false, nextCursor: null })),
    ])
    if (commentsResult.status === 'fulfilled') commentList.value = commentsResult.value.comments
    if (likesResult.status === 'fulfilled') likeList.value = likesResult.value.likes
  } catch (error) {
    console.error('加载我的数据失败:', error)
  }
}

const changeTab = async (tab: 'square' | 'following' | 'topics' | 'mine') => {
  currentTab.value = tab

  if (tab === 'square' || tab === 'following') {
    loadPosts(tab)
  }

  // 懒加载：仅当切换到话题且没有数据时才去加载
  if (tab === 'topics' && topicsList.value.length === 0 && !topicsLoading.value) {
    await loadTopicsList()
  }

  if (tab === 'mine') {
    loadMyData()
  }
}

const title = computed(() => {
  let tabName = ''
  switch (currentTab.value) {
    case 'square':
      tabName = '广场'
      break
    case 'following':
      tabName = '关注'
      break
    case 'topics':
      tabName = '话题'
      break
    case 'mine':
      tabName = mineTab.value === 'like' ? '我的喜欢' : '我的评论'
      break
    default:
      tabName = '首页'
  }
  return `微信读书社区 - ${tabName}`
})
useTitle(title)

const filteredPosts = computed<Post[]>(() => {
  switch (currentTab.value) {
    case 'mine':
      return posts.value.filter((p) => p.username === currentUser.username)
    default:
      return posts.value
  }
})

const handleTopicClick = (topic: HotTopic) => {
  console.log('点击热门话题:', topic.name)
}

const { updateFollow, updateLike } = usePostInteractions(posts)

const handleFollowChange = (postId: number, isFollowing: boolean): void => {
  updateFollow(postId, isFollowing)
}

const handleLike = (postId: number, likeCount: number, isLiked: boolean): void => {
  updateLike(postId, likeCount, isLiked)
}

const handleComment = (postId: number): void => {
  console.log('评论帖子:', postId)
}

const handleShare = (postId: number): void => {
  console.log('转发帖子:', postId)
}
</script>

<template>
  <div class="community">
    <NavBar />
    <BackToTop />
    <FloatingAddButton />
    <div class="community-content">
      <div class="tabs">
        <button :class="{ active: currentTab === 'square' }" @click="changeTab('square')">
          广场
        </button>
        <button :class="{ active: currentTab === 'following' }" @click="changeTab('following')">
          关注
        </button>
        <button :class="{ active: currentTab === 'topics' }" @click="changeTab('topics')">
          话题
        </button>
        <button :class="{ active: currentTab === 'mine' }" @click="changeTab('mine')">我的</button>
      </div>

      <!-- main-content部分 -->
      <div class="main-content">
        <!-- 话题列表 (优化：改成 v-show 保持DOM，不再局部嵌套滚动) -->
        <div v-show="currentTab === 'topics'" class="topics-grid">
          <Topic
            v-for="topic in topicsList"
            :id="topic.id"
            :key="topic.id"
            :cover="topic.cover"
            :title="topic.title"
            :number="topic.number"
          />

          <!-- 加载状态与触发器 -->
          <div class="topics-footer">
            <div v-if="topicsLoading" class="loading-more">加载中...</div>
            <div v-else-if="!topicsHasMore && topicsList.length > 0" class="no-more">
              没有更多话题了
            </div>
            <div v-else-if="topicsList.length === 0 && !topicsLoading" class="empty">暂无话题</div>
            <!-- 这个隐形div用于触发IntersectionObserver -->
            <div ref="loadMoreTrigger" class="scroll-trigger"></div>
          </div>
        </div>

        <!-- 我的 -->
        <div v-show="currentTab === 'mine'" class="mine-grid">
          <div class="mine-tabs">
            <button :class="{ active: mineTab === 'comment' }" @click="mineTab = 'comment'">
              评论
            </button>
            <button :class="{ active: mineTab === 'like' }" @click="mineTab = 'like'">赞</button>
          </div>

          <div v-if="mineTab === 'comment'">
            <CommentItem v-for="(item, index) in commentList" :key="index" :comment="item" />
            <div v-if="commentList.length === 0" class="empty">暂无评论</div>
          </div>

          <div v-else>
            <LikeItem v-for="(item, index) in likeList" :key="index" :like="item" />
            <div v-if="likeList.length === 0" class="empty">暂无点赞</div>
          </div>
        </div>

        <!-- 帖子列表 -->
        <div v-show="currentTab === 'square' || currentTab === 'following'" class="posts-list">
          <PostCard
            v-for="post in filteredPosts"
            :key="post.id"
            v-bind="post"
            :username="post.username"
            :avatar="post.avatar"
            :post-time="post.postTime"
            :title="post.title"
            :content="post.content"
            :like-count="post.likeCount"
            :comment-count="post.commentCount"
            :share-count="post.shareCount"
            :is-following="post.isFollowing"
            :is-liked="post.isLiked"
            :book="post.book"
            :show-follow-button="post.authorId !== currentUserId"
            @follow-change="(isFollowing: boolean) => handleFollowChange(post.id, isFollowing)"
            @like="(likeCount: number, isLiked: boolean) => handleLike(post.id, likeCount, isLiked)"
            @comment="() => handleComment(post.id)"
            @share="() => handleShare(post.id)"
          />
          <div v-if="filteredPosts.length === 0" class="empty">暂无内容</div>
        </div>
      </div>

      <div class="sidebar">
        <UserProfileCard :user="currentUser" />
        <HotTopics :topics="hotTopics" @topic-click="handleTopicClick" />
      </div>
    </div>
    <Footer />
  </div>
</template>

<style scoped>
.community {
  display: grid;
  background-color: rgb(241, 241, 241);
  gap: 24px;
  min-height: 100vh;
}

.community-content {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 24px;
  padding: 80px 100px 100px;
  max-width: 1400px;
  margin: 0 auto;
  align-items: flex-start;
}

.tabs {
  grid-column: 1 / -1;
  display: flex;
  background-color: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  width: 800px;
}

.tabs button {
  flex: 1;
  background: none;
  border: none;
  outline: none;
  padding: 10px 0;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  color: #6b7280;
  transition: all 0.2s ease;
}

.tabs button:hover {
  color: #1f2937;
  background-color: var(--shadow-green);
}

.tabs button.active {
  color: var(--primary-green);
  font-weight: 600;
}

.mine-tabs {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.mine-tabs button {
  background: none;
  border: none;
  font-size: 18px;
  color: #888;
  cursor: pointer;
}

.mine-tabs button.active {
  font-weight: 600;
  color: #333;
}

.main-content {
  width: 800px;
}

.sidebar {
  width: 350px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  position: sticky; /* 让侧边栏跟随页面滚动悬浮，体验更好 */
  top: 80px;
}

/* 彻底移除 max-height 和 overflow-y，拥抱全局滚动 */
.topics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
  justify-items: center;
  padding: 10px 0;
}

.topics-footer {
  grid-column: 1 / -1;
  width: 100%;
}

.scroll-trigger {
  height: 1px;
  width: 100%;
}

.empty {
  padding: 24px;
  text-align: center;
  color: #888;
}

.loading-more,
.no-more {
  padding: 20px 0;
  text-align: center;
  color: #888;
  font-size: 14px;
}
</style>
