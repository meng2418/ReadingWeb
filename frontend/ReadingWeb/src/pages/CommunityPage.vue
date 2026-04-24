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
import { getAvatarUrl, DEFAULT_AVATAR } from '@/utils/defaultImages'
import { Notebook, Search, Reading } from '@element-plus/icons-vue'
import { Smile } from 'lucide-vue-next'
import 'emoji-picker-element'
import { searchBooks } from '@/api/publish'
import { getGuessBooks } from '@/api/home'

export interface SimpleBook {
  id: number
  title: string
  author: string
}

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
      if (entries[0]?.isIntersecting && currentTab.value === 'topics' && topicsHasMore.value) {
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
    syncConversationsFromPosts(postsData)
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
const commentList = ref<unknown[]>([])
const likeList = ref<unknown[]>([])
const currentTab = ref<'square' | 'following' | 'topics' | 'mine' | 'messages'>('square')
const mineTab = ref<'like' | 'comment'>('comment')

type Conversation = {
  id: string
  userId: number
  username: string
  avatar?: string | undefined
  lastMessage: string
  lastTime: string
}

type ChatMessage = {
  id: string
  conversationId: string
  from: 'self' | 'other'
  content: string
  time: string
}

const conversations = ref<Conversation[]>([])
const activeConversationId = ref<string | null>(null)
const draftMessage = ref('')
const messagesByConversation = ref<Record<string, ChatMessage[]>>({})

// 私信增强功能状态
const showEmojiPicker = ref(false)
const showBookPanel = ref(false)
const bookKeyword = ref('')
const bookList = ref<SimpleBook[]>([])
const selectedBooks = ref<SimpleBook[]>([])

// 搜索书籍逻辑
let bookTimer: number | null = null
watch(bookKeyword, () => {
  if (bookTimer) clearTimeout(bookTimer)
  bookTimer = window.setTimeout(fetchBooks, 300)
})

const fetchBooks = async () => {
  if (!bookKeyword.value) {
    const guessBooks = await getGuessBooks()
    bookList.value = guessBooks.map((b) => ({ id: b.bookId, title: b.title, author: b.author }))
    return
  }
  try {
    const res = await searchBooks({ keyword: bookKeyword.value, limit: 10 })
    bookList.value = res.data.data.books.map((b: any) => ({
      id: b.bookId,
      title: b.bookTitle,
      author: b.authorName,
    }))
  } catch (error) {
    console.error('搜索书籍失败:', error)
  }
}

const toggleEmojiPicker = () => {
  showEmojiPicker.value = !showEmojiPicker.value
  showBookPanel.value = false
}

const handleEmojiClick = (e: any) => {
  draftMessage.value += e.detail.unicode
}

const toggleBookPanel = () => {
  showBookPanel.value = !showBookPanel.value
  showEmojiPicker.value = false
  if (showBookPanel.value && bookList.value.length === 0) {
    fetchBooks()
  }
}

const selectBook = (b: SimpleBook) => {
  if (selectedBooks.value.length >= 3) return
  if (!selectedBooks.value.find((i) => i.id === b.id)) {
    selectedBooks.value.push(b)
  }
  showBookPanel.value = false
}

const removeBook = (id: number) => {
  selectedBooks.value = selectedBooks.value.filter((b) => b.id !== id)
}

const formatTimeShort = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  if (Number.isNaN(date.getTime())) return timeStr
  const now = new Date()
  const diffMs = now.getTime() - date.getTime()
  const minutes = Math.floor(diffMs / 60000)
  const hours = Math.floor(diffMs / 3600000)
  const days = Math.floor(diffMs / 86400000)
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

const toPreviewText = (post: Post) => {
  const raw = (post.title && post.title.trim().length > 0 ? post.title : post.content) || ''
  const text = raw.replace(/\s+/g, ' ').trim()
  return text.length > 20 ? `${text.slice(0, 20)}...` : text
}

const syncConversationsFromPosts = (postsData: Post[]) => {
  const map = new Map<string, Conversation>()
  const lastTimeById = new Map<string, number>()
  for (const post of postsData) {
    if (!post.authorId) continue
    if (currentUserId.value && post.authorId === currentUserId.value) continue
    const id = String(post.authorId)
    const existingTime = lastTimeById.get(id) ?? -1
    const candidateTime = new Date(post.postTime).getTime()
    if (!Number.isFinite(candidateTime)) continue
    const candidate: Conversation = {
      id,
      userId: post.authorId,
      username: post.username,
      avatar: post.avatar,
      lastMessage: toPreviewText(post),
      lastTime: formatTimeShort(post.postTime),
    }
    if (!map.has(id) || candidateTime > existingTime) {
      map.set(id, candidate)
      lastTimeById.set(id, candidateTime)
    }
  }
  conversations.value = Array.from(map.values())
  if (!activeConversationId.value && conversations.value[0]) {
    activeConversationId.value = conversations.value[0].id
  }
}

const activeConversation = computed(() => {
  if (!activeConversationId.value) return null
  return conversations.value.find((c) => c.id === activeConversationId.value) || null
})

const activeMessages = computed(() => {
  const id = activeConversationId.value
  if (!id) return []
  return messagesByConversation.value[id] || []
})

const selectConversation = (id: string) => {
  activeConversationId.value = id
  showEmojiPicker.value = false
  showBookPanel.value = false
  selectedBooks.value = []
}

const canSend = computed(() => {
  return (
    !!activeConversationId.value &&
    (draftMessage.value.trim().length > 0 || selectedBooks.value.length > 0)
  )
})

const sendMessage = () => {
  if (!canSend.value || !activeConversationId.value) return
  const id = activeConversationId.value
  let content = draftMessage.value.trim()

  // 如果有分享书籍，将其作为特殊消息内容处理（或追加到内容中）
  if (selectedBooks.value.length > 0) {
    const booksText = selectedBooks.value.map((b) => `《${b.title}》`).join(' ')
    content = content ? `${content}\n分享书籍：${booksText}` : `分享书籍：${booksText}`
  }

  const now = new Date()
  const msg: ChatMessage = {
    id: `${id}-${now.getTime()}`,
    conversationId: id,
    from: 'self',
    content,
    time: now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
  }
  const existing = messagesByConversation.value[id] || []
  messagesByConversation.value = {
    ...messagesByConversation.value,
    [id]: [...existing, msg],
  }
  draftMessage.value = ''
  selectedBooks.value = []
  showEmojiPicker.value = false
  showBookPanel.value = false
}

const handleAvatarError = (event: Event) => {
  const img = event.target as HTMLImageElement
  if (img.src !== DEFAULT_AVATAR) {
    img.src = DEFAULT_AVATAR
  }
}

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

const changeTab = async (tab: 'square' | 'following' | 'topics' | 'mine' | 'messages') => {
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

  if (tab === 'messages') {
    if (!activeConversationId.value && conversations.value[0]) {
      activeConversationId.value = conversations.value[0].id
    }
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
    case 'messages':
      tabName = '私信'
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
        <button :class="{ active: currentTab === 'messages' }" @click="changeTab('messages')">
          私信
        </button>
      </div>

      <!-- main-content部分 -->
      <div class="main-content">
        <div v-show="currentTab === 'messages'" class="messages-layout">
          <div class="messages-list">
            <div class="messages-list-header">私信</div>
            <div v-if="conversations.length === 0" class="empty">暂无私信</div>
            <button
              v-for="c in conversations"
              :key="c.id"
              class="conversation-item"
              :class="{ active: c.id === activeConversationId }"
              @click="selectConversation(c.id)"
            >
              <img
                class="conversation-avatar"
                :src="getAvatarUrl(c.avatar)"
                :alt="`${c.username}的头像`"
                @error="handleAvatarError"
              />
              <div class="conversation-meta">
                <div class="conversation-top">
                  <div class="conversation-name">{{ c.username }}</div>
                  <div class="conversation-time">{{ c.lastTime }}</div>
                </div>
                <div class="conversation-bottom">
                  <div class="conversation-preview">{{ c.lastMessage }}</div>
                </div>
              </div>
            </button>
          </div>

          <div class="chat-panel">
            <div v-if="!activeConversation" class="chat-empty">选择一个联系人开始聊天</div>
            <div v-else class="chat-body">
              <div class="chat-header">{{ activeConversation.username }}</div>
              <div class="chat-history">
                <div
                  v-for="m in activeMessages"
                  :key="m.id"
                  class="chat-row"
                  :class="{ self: m.from === 'self' }"
                >
                  <div class="chat-bubble">{{ m.content }}</div>
                  <div class="chat-time">{{ m.time }}</div>
                </div>
                <div v-if="activeMessages.length === 0" class="empty">暂无消息</div>
              </div>
              <div class="chat-composer">
                <!-- 私信增强面板区 -->
                <div class="chat-composer-panels">
                  <!-- 已选书籍展示 -->
                  <div v-if="selectedBooks.length" class="chat-selected-books">
                    <el-tag
                      v-for="b in selectedBooks"
                      :key="b.id"
                      closable
                      size="small"
                      @close="removeBook(b.id)"
                      type="info"
                      class="book-tag"
                    >
                      《{{ b.title }}》
                    </el-tag>
                  </div>

                  <!-- 书籍搜索面板 -->
                  <div v-if="showBookPanel" class="chat-book-panel">
                    <div class="panel-header">
                      <el-icon><Search /></el-icon>
                      <input
                        v-model="bookKeyword"
                        placeholder="搜索书籍分享..."
                        class="panel-search-input"
                      />
                    </div>
                    <div class="panel-list">
                      <div
                        v-for="b in bookList"
                        :key="b.id"
                        class="panel-item"
                        @click="selectBook(b)"
                      >
                        <el-icon class="item-icon"><Reading /></el-icon>
                        <div class="item-info">
                          <div class="item-name">{{ b.title }}</div>
                          <div class="item-author">{{ b.author }}</div>
                        </div>
                      </div>
                      <div v-if="bookList.length === 0" class="panel-empty">未找到相关书籍</div>
                    </div>
                  </div>
                </div>

                <!-- 工具栏 -->
                <div class="chat-tools">
                  <el-icon
                    class="tool-icon"
                    :class="{ active: showEmojiPicker }"
                    @click="toggleEmojiPicker"
                  >
                    <Smile />
                  </el-icon>
                  <el-icon
                    class="tool-icon"
                    :class="{ active: showBookPanel }"
                    @click="toggleBookPanel"
                  >
                    <Notebook />
                  </el-icon>
                </div>

                <div class="chat-input-wrapper">
                  <textarea
                    v-model="draftMessage"
                    class="chat-input"
                    rows="1"
                    placeholder="输入消息..."
                    @keyup.enter.ctrl="sendMessage"
                  ></textarea>
                  <button class="chat-send" :disabled="!canSend" @click="sendMessage">发送</button>
                </div>
              </div>
            </div>
          </div>
        </div>

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

    <!-- 全局 Emoji 选择器 (私信使用) -->
    <div v-if="showEmojiPicker" class="chat-emoji-panel" @click.self="showEmojiPicker = false">
      <emoji-picker-element @emoji-click="handleEmojiClick"></emoji-picker-element>
    </div>
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

.messages-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 16px;
  width: 100%;
}

.messages-list {
  background-color: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
}

.messages-list-header {
  padding: 14px 16px;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  border-bottom: 1px solid #f1f5f9;
}

.conversation-item {
  width: 100%;
  display: grid;
  grid-template-columns: 44px 1fr;
  gap: 10px;
  align-items: center;
  padding: 12px 14px;
  background: none;
  border: none;
  cursor: pointer;
  text-align: left;
  border-bottom: 1px solid #f1f5f9;
}

.conversation-item.active {
  background-color: #f8fafc;
}

.conversation-avatar {
  width: 44px;
  height: 44px;
  border-radius: 9999px;
  object-fit: cover;
  background-color: #f3f4f6;
}

.conversation-meta {
  min-width: 0;
}

.conversation-top {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 10px;
}

.conversation-name {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conversation-time {
  font-size: 12px;
  color: #9ca3af;
  flex-shrink: 0;
}

.conversation-bottom {
  margin-top: 4px;
}

.conversation-preview {
  font-size: 13px;
  color: #6b7280;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-panel {
  background-color: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  min-height: 520px;
  display: flex;
}

.chat-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #888;
}

.chat-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.chat-header {
  padding: 14px 16px;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  border-bottom: 1px solid #f1f5f9;
}

.chat-history {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background-color: #ffffff;
}

.chat-row {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
  margin-bottom: 12px;
}

.chat-row.self {
  align-items: flex-end;
}

.chat-bubble {
  max-width: 70%;
  padding: 10px 12px;
  border-radius: 12px;
  background-color: #f3f4f6;
  color: #111827;
  line-height: 1.6;
  font-size: 14px;
  white-space: pre-wrap;
  word-break: break-word;
}

.chat-row.self .chat-bubble {
  background-color: rgba(0, 124, 39, 0.12);
}

.chat-time {
  font-size: 12px;
  color: #9ca3af;
}

.chat-composer {
  border-top: 1px solid #f1f5f9;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.chat-composer-panels {
  width: 100%;
}

.chat-selected-books {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 8px;
}

.book-tag {
  background-color: #f3f4f6;
  border-color: #e5e7eb;
  color: #4b5563;
}

.chat-emoji-panel {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10001;
  background: rgba(0, 0, 0, 0.4);
}

.chat-emoji-panel emoji-picker-element {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  display: block;
  min-height: 300px; /* 确保有最小高度 */
}

.chat-book-panel {
  width: 100%;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  margin-bottom: 8px;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  max-height: 300px;
}

.chat-book-panel .panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-bottom: 1px solid #f1f5f9;
}

.panel-search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 14px;
}

.chat-book-panel .panel-list {
  flex: 1;
  overflow-y: auto;
  padding: 4px 0;
}

.chat-book-panel .panel-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.chat-book-panel .panel-item:hover {
  background-color: #f8fafc;
}

.item-icon {
  font-size: 16px;
  color: #9ca3af;
}

.item-info {
  min-width: 0;
}

.item-name {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
}

.item-author {
  font-size: 12px;
  color: #6b7280;
}

.panel-empty {
  padding: 20px;
  text-align: center;
  color: #9ca3af;
  font-size: 13px;
}

.chat-tools {
  display: flex;
  gap: 12px;
  padding: 4px 0;
}

.chat-tools .tool-icon {
  font-size: 20px;
  color: #6b7280;
  cursor: pointer;
  transition: color 0.2s;
}

.chat-tools .tool-icon:hover {
  color: var(--primary-green);
}

.chat-tools .tool-icon.active {
  color: var(--primary-green);
}

.chat-input-wrapper {
  display: grid;
  grid-template-columns: 1fr 88px;
  gap: 12px;
  align-items: end;
}

.chat-input {
  width: 100%;
  height: 40px;
  line-height: 20px;
  resize: none;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px 12px;
  font-size: 14px;
  outline: none;
  background-color: #fff;
  color: #111827;
  box-sizing: border-box;
}

.chat-send {
  height: 40px;
  border: none;
  border-radius: 10px;
  background-color: var(--primary-green);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.chat-send:disabled {
  background-color: #e5e7eb;
  cursor: not-allowed;
  opacity: 0.7;
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
