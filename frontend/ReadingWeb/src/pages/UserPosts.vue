<template>
  <div class="user-posts-page">
    <NavBar title="我的发布" />
    <BackToTop />
    <div class="page-content">
      <!-- 返回按钮 -->
      <div class="back-button-container">
        <button class="back-btn" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </button>
      </div>

      <!-- 用户信息卡片 - 美化版 -->
      <div class="profile-card">
        <div class="profile-bg"></div>
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
                <div class="stat-icon">
                  <Heart
                    fill="transparent"
                    stroke="#666"
                    width="20px"
                    height="20px"
                    stroke-width="1.5"
                    class="heart-icon"
                  />
                </div>
                <div class="stat-content">
                  <span class="stat-number">{{ totalLikes }}</span>
                  <span class="stat-label">获赞</span>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon">
                  <el-icon size="20"><ChatDotRound /></el-icon>
                </div>
                <div class="stat-content">
                  <span class="stat-number">{{ totalComments }}</span>
                  <span class="stat-label">评论</span>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon">
                  <el-icon size="20"><Edit /></el-icon>
                </div>
                <div class="stat-content">
                  <span class="stat-number">{{ userInfo.postCount }}</span>
                  <span class="stat-label">发布</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 帖子列表 -->
      <div class="posts-container">
        <div class="section-title">
          <h3>我的帖子</h3>
          <div class="title-line"></div>
        </div>

        <div
          v-for="post in userPosts"
          :key="post.id"
          class="post-item"
        >
          <!-- 修改后的 PostCard，不显示关注按钮 -->
          <div class="post-card-wrapper">
            <PostCard
              v-bind="post"
              :show-follow-button="false"
              @like="(likeCount, isLiked) => handleLike(post.id, likeCount, isLiked)"
              @comment="handleComment"
            />

            <!-- 删除按钮 - 与帖子融为一体 -->
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
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, ArrowLeft, Edit, ChatDotRound } from '@element-plus/icons-vue'
import NavBar from '@/components/layout/NavBar.vue'
import PostCard from '@/components/community/PostCard.vue'
import BackToTop from '@/components/layout/BackToTop.vue'
import { Heart } from 'lucide-vue-next'

// 定义 props 接收路由参数
interface Props {
  id?: string
}

const props = defineProps<Props>()
const router = useRouter()

// 返回上一页
const goBack = () => {
  router.back()
}

// 跳转到发布页面
const goToCreatePost = () => {
  // 这里可以跳转到发布帖子的页面
  console.log('跳转到发布页面')
  // router.push('/create-post')
}

// 根据路由参数加载对应用户数据
const loadUserData = (userId?: string) => {
  if (userId) {
    console.log('加载用户ID:', userId)
  } else {
    console.log('加载当前用户数据')
  }
}

// 计算总点赞数
const totalLikes = computed(() => {
  return userPosts.value.reduce((sum, post) => sum + post.likeCount, 0)
})

// 计算总评论数
const totalComments = computed(() => {
  return userPosts.value.reduce((sum, post) => sum + post.commentCount, 0)
})

// 用户信息 - 根据路由参数动态设置
const userInfo = ref({
  username: props.id ? `用户${props.id}` : '当前用户',
  avatar: `https://picsum.photos/100?random=${props.id || 1}`,
  postCount: 8
})

// 监听路由参数变化
watch(
  () => props.id,
  (newId) => {
    loadUserData(newId)
  }
)

onMounted(() => {
  loadUserData(props.id)
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

// 加载用户帖子
const loadUserPosts = async () => {
  // 这里可以调用API获取用户发布的帖子
}

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
</script>

<style scoped>
.user-posts-page {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  min-height: 100vh;
}

.page-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

/* 返回按钮样式 */
.back-button-container {
  margin-bottom: 16px;
  margin-top: 40px;
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

/* 美化版个人资料卡片 */
.profile-card {
  background: white;
  border-radius: 12px;
  margin-bottom: 24px;
  margin-top: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
  overflow: hidden;
  position: relative;
}

.profile-bg {
  height: 80px;
  background: #f8fafc;
  position: relative;
}

.profile-content {
  padding: 0 30px 30px;
  margin-top: -40px;
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
  width: 100px;
  height: 100px;
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
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 24px;
  color: #2d3748;
  position: relative;
}

.username::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 3px;
  background: #a0aec0;
  border-radius: 2px;
}

.stats-row {
  display: flex;
  justify-content: center;
  gap: 40px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 12px;
  transition: all 0.2s ease;
  cursor: pointer;
}

.stat-item:hover {
  background: #edf2f7;
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
}

.stat-icon .heart-icon {
  transition: all 0.2s ease;
}

.stat-item:hover .stat-icon .heart-icon {
  stroke: #ff6b6b;
  transform: scale(1.1);
}

.stat-icon .el-icon {
  color: #666;
  transition: all 0.2s ease;
}

.stat-item:hover .stat-icon .el-icon {
  color: #64adf7;
  transform: scale(1.1);
}

.stat-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.stat-number {
  font-size: 18px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 2px;
}

.stat-label {
  font-size: 12px;
  color: #718096;
  font-weight: 500;
}

/* 帖子容器 */
.posts-container {
  margin-top: 30px;
}

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
@media (max-width: 768px) {
  .page-content {
    padding: 16px;
  }

  .profile-content {
    padding: 0 20px 20px;
  }

  .stats-row {
    gap: 16px;
    flex-wrap: wrap;
  }

  .stat-item {
    flex: 1;
    min-width: 120px;
    justify-content: center;
  }

  .delete-action {
    padding: 10px 16px;
  }

  .empty-state {
    padding: 40px 16px;
  }
}
</style>
