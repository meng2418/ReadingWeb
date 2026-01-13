<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
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
  userId: null as number | null, // 添加用户ID
  username: '加载中...',
  bio: '',
  avatar: '', // 初始使用本地默认图
  followCount: 0,
  fansCount: 0,
  postCount: 0,
})

// 当前用户ID（用于判断是否是自己的帖子）
const currentUserId = computed(() => currentUser.userId)

// 热门话题 - 使用更明确的结构
const hotTopics = ref<HotTopic[]>([])
const topicsList = ref<{ id: number; cover: string; title: string; number: number }[]>([])

// 话题列表分页相关
const topicsHasMore = ref(true)
const topicsNextCursor = ref<number | undefined>(undefined)
const topicsLoading = ref(false)

// 加载话题列表
const loadTopicsList = async () => {
  if (topicsLoading.value) return

  topicsLoading.value = true
  try {
    const result = await getTopicsList(topicsNextCursor.value, 9)
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

// 加载热门话题
const loadHotTopics = async () => {
  try {
    console.log('开始加载热门话题...')

    // 调用API获取热门话题
    const topics = await getHotTopics()
    console.log('API返回的热门话题数据:', topics)

    if (topics && topics.length > 0) {
      // 确保只取前9个（如果需要的话）
      hotTopics.value = topics.slice(0, 9).map((topic) => ({
        id: topic.id,
        name: topic.name,
      }))
      console.log('设置后的热门话题数据:', hotTopics.value)
    } else {
      // 如果接口返回空或失败，使用默认数据
      console.warn('热门话题接口返回空，使用默认数据')
      hotTopics.value = []
    }

    console.log('热门话题加载完成，数量:', hotTopics.value.length)
  } catch (error) {
    console.error('加载热门话题失败:', error)
    // 如果接口失败，使用默认数据
    hotTopics.value = []
  }
}

// 加载帖子数据
const loadPosts = async (type: 'square' | 'following' = 'square') => {
  try {
    const postsData = await fetchCommunityPosts(type)
    posts.value = postsData
    console.log(`加载${type === 'square' ? '广场' : '关注'}帖子成功:`, postsData.length)
  } catch (error) {
    console.error(`加载${type === 'square' ? '广场' : '关注'}帖子失败:`, error)
  }
}

// 帖子数据
onMounted(async () => {
  console.log('CommunityPage mounted, 开始加载数据...')

  try {
    // 并发请求所有数据，使用Promise.allSettled避免一个失败影响其他
    const [commentsResult, likesResult, profileResult] = await Promise.allSettled([
      fetchMyComments().catch(err => {
        console.warn('获取评论失败:', err)
        return { comments: [], hasMore: false, nextCursor: null }
      }),
      fetchMyLikes().catch(err => {
        console.warn('获取点赞失败:', err)
        return { likes: [], hasMore: false, nextCursor: null }
      }),
      getProfileHome().catch(err => {
        console.warn('获取用户信息失败:', err)
        return null
      }),
    ])

    // 处理评论数据
    if (commentsResult.status === 'fulfilled') {
      commentList.value = commentsResult.value.comments
    }

    // 处理点赞数据
    if (likesResult.status === 'fulfilled') {
      likeList.value = likesResult.value.likes
    }

    // 处理用户信息
    if (profileResult.status === 'fulfilled' && profileResult.value) {
      Object.assign(currentUser, {
        userId: profileResult.value.userId || null, // 添加用户ID
        username: profileResult.value.username || '用户',
        bio: profileResult.value.bio || '',
        avatar: profileResult.value.avatar || '',
        followCount: profileResult.value.followingCount || 0,
        fansCount: profileResult.value.followerCount || 0,
        postCount: profileResult.value.postCount || 0,
      })
      console.log('用户信息加载完成:', currentUser)
    } else {
      console.warn('用户信息加载失败，使用默认值')
    }
  } catch (error) {
    console.error('加载主要数据失败:', error)
  }

  // 加载初始帖子数据（广场）
  await loadPosts('square')

  // 加载话题相关数据
  console.log('开始加载话题相关数据...')
  await Promise.all([loadTopicsList(), loadHotTopics()])

  console.log('所有数据加载完成')
  console.log('热门话题数据:', hotTopics.value)
})

const posts = ref<Post[]>([])
const commentList = ref<any[]>([])
const likeList = ref<any[]>([])
const currentTab = ref<'square' | 'following' | 'topics' | 'mine'>('square')

// 加载我的评论和点赞数据
const loadMyData = async () => {
  try {
    const [commentsResult, likesResult] = await Promise.allSettled([
      fetchMyComments().catch(err => {
        console.warn('获取评论失败:', err)
        return { comments: [], hasMore: false, nextCursor: null }
      }),
      fetchMyLikes().catch(err => {
        console.warn('获取点赞失败:', err)
        return { likes: [], hasMore: false, nextCursor: null }
      }),
    ])

    // 处理评论数据
    if (commentsResult.status === 'fulfilled') {
      commentList.value = commentsResult.value.comments
    }

    // 处理点赞数据
    if (likesResult.status === 'fulfilled') {
      likeList.value = likesResult.value.likes
    }
  } catch (error) {
    console.error('加载我的数据失败:', error)
  }
}

const changeTab = (tab: 'square' | 'following' | 'topics' | 'mine') => {
  console.log('切换标签:', tab)
  currentTab.value = tab

  // 切换到广场或关注页时，加载对应的帖子数据
  if (tab === 'square' || tab === 'following') {
    loadPosts(tab)
  }

  // 切换到话题页时，如果没有数据则加载
  if (tab === 'topics' && topicsList.value.length === 0 && !topicsLoading.value) {
    loadTopicsList()
  }

  // 切换到我的tab时，重新加载数据
  if (tab === 'mine') {
    loadMyData()
  }
}

// "我的"内部的二级 Tab
const mineTab = ref<'like' | 'comment'>('comment')

// 动态页面标题
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
      // square 和 following 的数据已经通过 API 过滤，直接返回
      return posts.value
  }
})

const handleTopicClick = (topic: HotTopic) => {
  console.log('点击热门话题:', topic.name)
  // 这里可以添加跳转到话题详情页的逻辑
  // window.open(`/topicdetail/${topic.id}`, '_blank')
}

// 滚动加载更多话题
const handleTopicsScroll = (event: Event) => {
  const target = event.target as HTMLElement
  const { scrollTop, scrollHeight, clientHeight } = target

  if (
    scrollTop + clientHeight >= scrollHeight - 100 &&
    topicsHasMore.value &&
    !topicsLoading.value
  ) {
    loadTopicsList()
  }
}

// 统一：帖子交互逻辑（关注 / 点赞）
const { updateFollow, updateLike } = usePostInteractions(posts)

const handleFollowChange = (postId: number, isFollowing: boolean): void => {
  updateFollow(postId, isFollowing)
}

const handleLike = (postId: number, likeCount: number, isLiked: boolean): void => {
  updateLike(postId, likeCount, isLiked)
}

// 评论事件
const handleComment = (postId: number): void => {
  console.log('评论帖子:', postId)
  // TODO: 实现评论功能
}

// 转发事件
const handleShare = (postId: number): void => {
  console.log('转发帖子:', postId)
  // TODO: 实现转发功能
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
        <div v-if="currentTab === 'topics'" class="topics-grid" @scroll="handleTopicsScroll">
          <Topic
            v-for="topic in topicsList"
            :id="topic.id"
            :key="topic.id"
            :cover="topic.cover"
            :title="topic.title"
            :number="topic.number"
          />
          <!-- 加载更多提示 -->
          <div v-if="topicsLoading" class="loading-more">加载中...</div>
          <div v-else-if="!topicsHasMore && topicsList.length > 0" class="no-more">
            没有更多话题了
          </div>
          <div v-else-if="topicsList.length === 0 && !topicsLoading" class="empty">暂无话题</div>
        </div>
        <!--我的-->
        <div v-else-if="currentTab === 'mine'" class="mine-grid">
          <div class="mine-tabs">
            <button :class="{ active: mineTab === 'comment' }" @click="mineTab = 'comment'">
              评论
            </button>
            <button :class="{ active: mineTab === 'like' }" @click="mineTab = 'like'">赞</button>
          </div>

          <!-- 评论 -->
          <div v-if="mineTab === 'comment'">
            <CommentItem v-for="(item, index) in commentList" :key="index" :comment="item" />
            <div v-if="commentList.length === 0" class="empty">暂无评论</div>
          </div>

          <!-- 赞 -->
          <div v-else>
            <LikeItem v-for="(item, index) in likeList" :key="index" :like="item" />
            <div v-if="likeList.length === 0" class="empty">暂无点赞</div>
          </div>
        </div>
        <!-- 帖子列表 -->
        <div v-else class="posts-list">
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
        <!-- 传递热门话题数据 -->
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
/* tabs */
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
}

.sidebar-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #f0f0f0;
}

.topics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
  justify-items: center;
  max-height: 600px;
  overflow-y: auto;
  padding: 10px;
}

.empty {
  padding: 24px;
  text-align: center;
  color: #888;
}

.loading-more,
.no-more {
  grid-column: 1 / -1;
  padding: 16px;
  text-align: center;
  color: #888;
  font-size: 14px;
}
</style>
