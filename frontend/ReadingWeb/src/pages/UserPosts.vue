<template>
  <div class="user-posts-page">
    <NavBar title="我的发布" />
    <BackToTop />
    <!-- 只有在发布页面才显示浮动添加按钮 -->
    <FloatingAddButton v-if="currentTab === 'posts'" />
    <div class="page-content">
      <!-- 返回按钮 -->
      <div class="back-button-container">
        <button class="back-btn" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </button>
      </div>

      <!-- 用户信息卡片 - 普通样式 -->
      <div class="profile-card">
        <div class="profile-content">
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <img :src="userInfo.avatar" alt="用户头像" class="profile-avatar">
            </div>
          </div>
          <div class="profile-info">
            <h2 class="username">{{ userInfo.username }}</h2>
            <div class="stats-row">
              <div class="stat-item">
                <span class="stat-number">{{ userInfo.postCount }}</span>
                <span class="stat-label">发布</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userInfo.followingCount }}</span>
                <span class="stat-label">关注</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userInfo.followerCount }}</span>
                <span class="stat-label">粉丝</span>
              </div>
              <!-- 删除了获赞数 -->
            </div>
          </div>
        </div>
      </div>

      <!-- 左右分栏布局 -->
      <div class="main-layout">
        <!-- 左侧导航栏 -->
        <div class="sidebar">
          <div class="nav-item" :class="{ active: currentTab === 'posts' }" @click="switchTab('posts')">
            <el-icon><Edit /></el-icon>
            <span>发布</span>
          </div>
          <div class="nav-item" :class="{ active: currentTab === 'following' }" @click="switchTab('following')">
            <el-icon><User /></el-icon>
            <span>关注</span>
          </div>
          <div class="nav-item" :class="{ active: currentTab === 'followers' }" @click="switchTab('followers')">
            <el-icon><UserFilled /></el-icon>
            <span>粉丝</span>
          </div>
        </div>

        <!-- 右侧内容区域 -->
        <div class="content-area">
          <!-- 发布内容 -->
          <div v-if="currentTab === 'posts'" class="posts-container">
            <!-- 修改了这部分：在标题右侧添加获赞数和评论数 -->
            <div class="section-title-with-stats">
              <div class="section-title-left">
                <h3>我的帖子</h3>
                <div class="title-line"></div>
              </div>
              <div class="section-title-stats">
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

            <div
              v-for="post in userPosts"
              :key="post.id"
              class="post-item"
            >
              <div class="post-card-wrapper">
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
              <button class="create-post-btn" @click="goToCreatePost">
                立即发布
              </button>
            </div>
          </div>

          <!-- 关注列表 -->
          <div v-if="currentTab === 'following'" class="following-container">
            <div class="section-title">
              <h3>关注列表</h3>
              <div class="title-line"></div>
            </div>

            <div class="users-grid">
              <div
                v-for="user in followingList"
                :key="user.id"
                class="user-card"
              >
                <!-- 左侧头像 -->
                <div class="user-avatar-container">
                  <img :src="user.avatar" alt="用户头像" class="user-avatar">
                </div>
                <!-- 右侧内容 -->
                <div class="user-right-content">
                  <div class="user-info">
                    <h4 class="user-name">{{ user.username }}</h4>
                    <p class="user-bio">{{ user.bio }}</p>
                  </div>
                  <button class="follow-btn following" @click="handleUnfollow(user.id)">
                    已关注
                  </button>
                </div>
              </div>
            </div>

            <div v-if="followingList.length === 0" class="empty-state">
              <div class="empty-illustration">
                <div class="empty-icon">👤</div>
              </div>
              <h3>还没有关注任何人</h3>
              <p class="empty-hint">快去发现有趣的人吧！</p>
            </div>
          </div>

          <!-- 粉丝列表 -->
          <div v-if="currentTab === 'followers'" class="followers-container">
            <div class="section-title">
              <h3>粉丝列表</h3>
              <div class="title-line"></div>
            </div>

            <div class="users-grid">
              <div
                v-for="user in followersList"
                :key="user.id"
                class="user-card"
              >
                <!-- 左侧头像 -->
                <div class="user-avatar-container">
                  <img :src="user.avatar" alt="用户头像" class="user-avatar">
                </div>
                <!-- 右侧内容 -->
                <div class="user-right-content">
                  <div class="user-info">
                    <h4 class="user-name">{{ user.username }}</h4>
                    <p class="user-bio">{{ user.bio }}</p>
                  </div>
                  <button
                    class="follow-btn"
                    :class="{ following: user.isFollowing }"
                    @click="handleFollow(user.id)"
                  >
                    {{ user.isFollowing ? '已互关' : '回关' }}
                  </button>
                </div>
              </div>
            </div>

            <div v-if="followersList.length === 0" class="empty-state">
              <div class="empty-illustration">
                <div class="empty-icon">👥</div>
              </div>
              <h3>还没有粉丝</h3>
              <p class="empty-hint">积极创作，吸引更多关注吧！</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, ArrowLeft, Edit, User, UserFilled } from '@element-plus/icons-vue'
import NavBar from '@/components/layout/NavBar.vue'
import PostCard from '@/components/community/PostCard.vue'
import BackToTop from '@/components/layout/BackToTop.vue'
import FloatingAddButton from '@/components/community/FloatingAddButton.vue'

// 定义 props 接收路由参数
interface Props {
  id?: string
}

const props = defineProps<Props>()
const router = useRouter()
const route = useRoute()

// 当前选中的标签页
const currentTab = ref('posts')

// 切换标签页
const switchTab = (tab: string) => {
  currentTab.value = tab
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 跳转到发布页面
const goToCreatePost = () => {
  console.log('跳转到发布页面')
  // router.push('/create-post')
}

// 用户信息
const userInfo = ref({
  username: props.id ? `用户${props.id}` : '当前用户',
  avatar: `https://picsum.photos/100?random=${props.id || 1}`,
  postCount: 8,
  followingCount: 24,
  followerCount: 156
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
const userPosts = ref([
  {
    id: 1,
    username: props.id ? `用户${props.id}` : '当前用户',
    avatar: `https://picsum.photos/100?random=${props.id || 1}`,
    postTime: '2小时前',
    title: '《百年孤独》读后感',
    content: '刚刚读完马尔克斯的《百年孤独》，这本书真的是一种奇妙的阅读体验...',
    likeCount: 128,
    commentCount: 23,
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
    username: props.id ? `用户${props.id}` : '当前用户',
    avatar: `https://picsum.photos/100?random=${props.id || 1}`,
    postTime: '1天前',
    title: '推荐几本好书',
    content: '最近读了几本很不错的小说，推荐给大家...',
    likeCount: 89,
    commentCount: 15,
    isFollowing: false,
    isLiked: true,
    book: null,
  }
])

// 关注列表数据
const followingList = ref([
  {
    id: 1,
    username: '鱼鹅来了',
    avatar: 'https://picsum.photos/100?random=10',
    bio: '每天18点开播 请多多关注我 Q群1032791648',
    isFollowing: true
  },
  {
    id: 2,
    username: 'Borseronie',
    avatar: 'https://picsum.photos/100?random=11',
    bio: '成为砂砾、成为羽毛、成为狼。',
    isFollowing: true
  },
  {
    id: 3,
    username: '哔哩哔哩会员购',
    avatar: 'https://picsum.photos/100?random=12',
    bio: '【关注网购】每日为你传递最新的手办周边...',
    isFollowing: true
  },
  {
    id: 4,
    username: '哔哩哔哩漫画',
    avatar: 'https://picsum.photos/100?random=13',
    bio: '漫画授权，联动合作，品牌联名请发送邮件...',
    isFollowing: true
  },
  {
    id: 5,
    username: '读书达人小王',
    avatar: 'https://picsum.photos/100?random=14',
    bio: '每周读一本书，分享读书笔记',
    isFollowing: true
  },
  {
    id: 6,
    username: '文学爱好者小李',
    avatar: 'https://picsum.photos/100?random=15',
    bio: '专注于经典文学作品的解读',
    isFollowing: true
  }
])

// 粉丝列表数据
const followersList = ref([
  {
    id: 7,
    username: '新读者',
    avatar: 'https://picsum.photos/100?random=16',
    bio: '刚刚开始阅读之旅的新手',
    isFollowing: true
  },
  {
    id: 8,
    username: '书虫小张',
    avatar: 'https://picsum.photos/100?random=17',
    bio: '每天都要读书的忠实读者',
    isFollowing: false
  },
  {
    id: 9,
    username: '文学评论家',
    avatar: 'https://picsum.photos/100?random=18',
    bio: '专业文学评论，深度解析作品',
    isFollowing: true
  },
  {
    id: 10,
    username: '读书分享者',
    avatar: 'https://picsum.photos/100?random=19',
    bio: '分享好书，交流心得',
    isFollowing: false
  },
  {
    id: 11,
    username: '小说爱好者',
    avatar: 'https://picsum.photos/100?random=20',
    bio: '热爱各种类型的小说',
    isFollowing: false
  },
  {
    id: 12,
    username: '历史书迷',
    avatar: 'https://picsum.photos/100?random=21',
    bio: '专注于历史类书籍的阅读',
    isFollowing: true
  }
])

// 处理点赞
const handleLike = (postId: number, likeCount: number, isLiked: boolean) => {
  const post = userPosts.value.find(p => p.id === postId)
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
  try {
    await ElMessageBox.confirm(
      '确定要删除这篇帖子吗？删除后不可恢复。',
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    userPosts.value = userPosts.value.filter(post => post.id !== postId)
    userInfo.value.postCount = userPosts.value.length
    ElMessage.success('帖子删除成功')
  } catch {
    console.log('取消删除')
  }
}

// 关注用户
const handleFollow = (userId: number) => {
  const user = followersList.value.find(u => u.id === userId)
  if (user) {
    user.isFollowing = !user.isFollowing
    ElMessage.success(user.isFollowing ? '关注成功' : '已取消关注')
  }
}

// 取消关注
const handleUnfollow = (userId: number) => {
  try {
    ElMessageBox.confirm(
      '确定要取消关注吗？',
      '取消关注',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    ).then(() => {
      followingList.value = followingList.value.filter(user => user.id !== userId)
      userInfo.value.followingCount--
      ElMessage.success('已取消关注')
    }).catch(() => {
      console.log('取消操作')
    })
  } catch {
    console.log('取消操作')
  }
}

// 监听路由参数变化，以便从UserProfileCard跳转时能切换到对应标签页
onMounted(() => {
  window.scrollTo(0, 0)

  // 检查是否有tab参数
  const tabParam = route.query.tab as string
  if (tabParam && ['posts', 'following', 'followers'].includes(tabParam)) {
    currentTab.value = tabParam
  }
})

// 监听路由变化
watch(
  () => route.query.tab,
  (newTab) => {
    if (newTab && ['posts', 'following', 'followers'].includes(newTab as string)) {
      currentTab.value = newTab as string
    }
  }
)
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
}

/* 返回按钮样式 */
.back-button-container {
  margin-bottom: 16px;
  margin-top: 20px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: white;
  color: #4a5568;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.back-btn:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
}

/* 个人资料卡片 - 普通样式 */
.profile-card {
  background: white;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
  overflow: hidden;
  position: relative;
  padding: 40px 20px 30px;
}

.profile-content {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  position: relative;
  z-index: 1;
}

.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.avatar-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
}

.profile-avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.profile-info {
  text-align: center;
}

.username {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 20px;
  color: #2d3748;
}

.stats-row {
  display: flex;
  justify-content: center;
  gap: 40px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  cursor: default;
}

.stat-number {
  font-size: 18px;
  font-weight: 700;
  color: #2d3748;
}

.stat-label {
  font-size: 12px;
  color: #718096;
  font-weight: 500;
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
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
  padding: 16px 0;
  flex-shrink: 0;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 24px;
  cursor: pointer;
  color: #666;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  border-left: 3px solid transparent;
}

.nav-item:hover {
  background: #f8fafc;
  color: #4a9af5;
}

.nav-item.active {
  background: #f0f7ff;
  color: #4a9af5;
  border-left: 3px solid #4a9af5;
}

.nav-item .el-icon {
  font-size: 18px;
}

/* 右侧内容区域 */
.content-area {
  flex: 1;
  min-width: 0; /* 防止flex item溢出 */
}

/* 修改：发布页面的标题与统计区域 */
.section-title-with-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.section-title-left {
  flex: 1;
  min-width: 200px;
}

.section-title-with-stats h3 {
  font-size: 20px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 8px;
}

.section-title-with-stats .title-line {
  width: 60px;
  height: 3px;
  background: #a0aec0;
  border-radius: 2px;
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
  color: #2d3748;
}

.section-title-stats .stat-label {
  font-size: 12px;
  color: #718096;
  font-weight: 500;
}

/* 关注和粉丝页面的标题样式保持不变 */
.section-title {
  margin-bottom: 24px;
  position: relative;
}

.section-title h3 {
  font-size: 20px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 8px;
}

.title-line {
  width: 60px;
  height: 3px;
  background: #a0aec0;
  border-radius: 2px;
}

/* 帖子样式 */
.post-item {
  margin-bottom: 16px;
}

.post-card-wrapper {
  background: white;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
  overflow: hidden;
}

/* 关键修改：调整帖子卡片的外边距和内边距 */
.post-card-wrapper :deep(.post-card) {
  margin-bottom: 0;
  border-radius: 0;
  box-shadow: none;
  border: none;
}

/* 删除按钮样式 */
.delete-action {
  padding: 12px 20px;
  display: flex;
  justify-content: flex-end;
  background: white;
}

.delete-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #f5f5f5;
  color: #666;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.delete-btn:hover {
  background: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
  transform: translateY(-1px);
}

/* 用户卡片网格布局 */
.users-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.user-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #f0f0f0;
  transition: all 0.2s ease;
  display: flex;
  align-items: stretch;
  min-height: 120px; /* 设置最小高度，使卡片高度统一 */
}

.user-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

/* 左侧头像容器 */
.user-avatar-container {
  flex-shrink: 0;
  margin-right: 12px;
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
}

/* 右侧内容区域 */
.user-right-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0; /* 防止内容溢出 */
}

/* 用户信息区域 */
.user-info {
  flex: 1;
  margin-bottom: 12px;
  overflow: hidden;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
  line-height: 1.2;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-bio {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 关注按钮样式 */
.follow-btn {
  padding: 8px 16px;
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 16px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  white-space: nowrap;
  height: 32px; /* 固定按钮高度 */
  width: 100%; /* 按钮宽度占满右侧区域 */
}

.follow-btn:hover {
  background: #ff8787;
}

.follow-btn.following {
  background: #f0f0f0;
  color: #666;
}

.follow-btn.following:hover {
  background: #e0e0e0;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.empty-hint {
  color: #666;
  font-size: 14px;
  margin-bottom: 24px;
}

.create-post-btn {
  padding: 10px 24px;
  background: #64adf7;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.create-post-btn:hover {
  background: #4a9af5;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .users-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .page-content {
    padding: 16px;
  }

  .profile-content {
    padding: 0;
  }

  .stats-row {
    gap: 20px;
    flex-wrap: wrap;
  }

  .main-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    height: auto; /* 移动端恢复自动高度 */
    overflow-y: visible; /* 移除滚动条 */
  }

  .users-grid {
    grid-template-columns: repeat(1, 1fr);
  }

  .user-card {
    min-height: 100px;
  }

  .user-avatar {
    width: 50px;
    height: 50px;
  }

  /* 移动端适配：发布页面标题与统计区域 */
  .section-title-with-stats {
    flex-direction: column;
    align-items: flex-start;
  }

  .section-title-stats {
    width: 100%;
    justify-content: flex-start;
    padding: 0; /* 去掉内边距 */
  }
}
</style>
