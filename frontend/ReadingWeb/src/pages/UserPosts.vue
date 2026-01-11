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
                  <!-- 修改为（添加 .stop）： -->
                  <button class="delete-btn" @click.stop="handleDeletePost(post.id)">
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

          <!-- 关注列表 -->
          <div
            v-if="currentTab === 'following'"
            class="user-list-wrapper"
            v-loading="isLoadingRelations"
          >
            <UserList type="following" :users="followingList" @update="handleFollowingUpdate" />
            <!-- 如果关注人数很多，可以在这里添加“查看更多”按钮（配合 hasMore） -->
          </div>

          <!-- 粉丝列表 -->
          <div
            v-if="currentTab === 'followers'"
            class="user-list-wrapper"
            v-loading="isLoadingRelations"
          >
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
// 引入 ElMessageBox
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Edit, User, UserFilled, ChatLineSquare, Star } from '@element-plus/icons-vue'

// 组件导入
import NavBar from '@/components/layout/NavBar.vue'
import PostCard from '@/components/community/PostCard.vue'
import BackToTop from '@/components/layout/BackToTop.vue'
import FloatingAddButton from '@/components/community/FloatingAddButton.vue'
import ThoughtCard from '@/components/userposts/ThoughtCard.vue'
import ReviewCard from '@/components/userposts/ReviewCard.vue'
import UserList from '@/components/userposts/UserList.vue'

// 类型定义与 API
import type { Post } from '@/types/post'
import type { FollowUser } from '@/types/user'
import type { ReviewCardItem, RatingConfig } from '@/types/review'
import { useTitle } from '@/stores/useTitle'
// 引入新的删除 API
import { getPosts, deleteUserPost } from '@/api/userPosts'
import { getUserNotes, deleteUserNote } from '@/api/notes'
import { getUserReviews, deleteUserReview } from '@/api/bookreview'
import { getFollowingList, getFollowersList } from '@/api/userRelations'

/* -----------------------------------------
       1. 基础配置与导航状态 (保持不变)
    ----------------------------------------- */
const props = defineProps<{ id?: string }>()
const route = useRoute()
const currentTab = ref('posts')

const tabTitleMap: Record<string, string> = {
  posts: '我的发布',
  following: '我的关注',
  followers: '我的粉丝',
  thoughts: '我的想法',
  reviews: '我的书评',
}

const switchTab = (tab: string) => {
  currentTab.value = tab
}

/* -----------------------------------------
       2. 业务数据状态 (保持不变)
    ----------------------------------------- */
const userStats = ref({
  postCount: 0,
  followingCount: 0,
  followerCount: 0,
})

const userPosts = ref<Post[]>([])
const isLoadingPosts = ref(false)

const thoughts = ref<any[]>([])
const isLoadingThoughts = ref(false)

const reviews = ref<ReviewCardItem[]>([])
const reviewTotalCount = ref(0)
const isLoadingReviews = ref(false)
const ratingConfig: RatingConfig = {
  recommend: { label: '推荐', className: 'tag-recommend' },
  average: { label: '一般', className: 'tag-average' },
  bad: { label: '不行', className: 'tag-bad' },
}

const followingList = ref<FollowUser[]>([])
const followersList = ref<FollowUser[]>([])
const isLoadingRelations = ref(false)

/* -----------------------------------------
       3. 计算属性 (保持不变)
    ----------------------------------------- */
const totalLikes = computed(() => userPosts.value.reduce((sum, post) => sum + post.likeCount, 0))
const totalComments = computed(() =>
  userPosts.value.reduce((sum, post) => sum + post.commentCount, 0),
)
const pageTitle = computed(() => `微信读书 - ${tabTitleMap[currentTab.value] || '个人中心'}`)

useTitle(pageTitle)

/* -----------------------------------------
       4. 数据请求函数 (保持不变)
    ----------------------------------------- */
const fetchUserPosts = async () => {
  try {
    isLoadingPosts.value = true
    const res = await getPosts()
    userPosts.value = res.list
    userStats.value.postCount = res.total
  } catch (err) {
    console.error('获取帖子失败:', err)
  } finally {
    isLoadingPosts.value = false
  }
}

const fetchUserThoughts = async () => {
  try {
    isLoadingThoughts.value = true
    const allNotesResponse = await getUserNotes()
    thoughts.value = allNotesResponse.notes
      .filter((n: any) => n.noteType === 'thought')
      // 确保这里的 id 对应的是 noteId，方便后续删除使用
      .map((n: any) => ({
        id: n.markId, // 注意：如果接口返回的是 noteId，这里要确保映射正确
        bookName: n.bookTitle,
        date: n.noteCreatedAt,
        thought: n.noteContent,
        quote: n.quote,
      }))
  } catch (err) {
    console.error('获取想法失败:', err)
  } finally {
    isLoadingThoughts.value = false
  }
}

const fetchUserReviews = async () => {
  try {
    isLoadingReviews.value = true
    const data = await getUserReviews()
    reviews.value = data.reviews
    reviewTotalCount.value = data.totalCount
  } catch (err) {
    console.error('获取书评失败:', err)
  } finally {
    isLoadingReviews.value = false
  }
}

const fetchUserRelations = async () => {
  try {
    isLoadingRelations.value = true
    const [followingRes, followersRes] = await Promise.all([getFollowingList(), getFollowersList()])
    followingList.value = followingRes.items
    followersList.value = followersRes.items
    userStats.value.followingCount = followingRes.items.length
    userStats.value.followerCount = followersRes.items.length
  } catch (err) {
    console.error('获取用户关系失败:', err)
  } finally {
    isLoadingRelations.value = false
  }
}

/* -----------------------------------------
       5. 事件处理函数 (修改了删除逻辑)
    ----------------------------------------- */
const handleLike = (postId: number, likeCount: number, isLiked: boolean) => {
  const post = userPosts.value.find((p) => p.id === postId)
  if (post) {
    post.likeCount = likeCount
    post.isLiked = isLiked
  }
}
const handleComment = () => console.log('跳转到评论页面')

// 1. 定义后端数据结构接口
interface BackendResponse<T = any> {
  code: number
  message: string
  data: T
}

// ------------------------------------------------------

// 2. 修改：删除帖子逻辑
const handleDeletePost = async (postId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条帖子吗？删除后无法恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    // 注意：没有拦截器，Axios 返回的是 AxiosResponse<BackendResponse>
    // 这里我们解构出 data，重命名为 resData，方便后续使用
    const { data: resData } = (await deleteUserPost(postId)) as {
      data: BackendResponse<{ remainingPostCount: number }>
    }

    // 使用 resData.code 判断业务状态
    if (resData.code === 200) {
      userPosts.value = userPosts.value.filter((post) => post.id !== postId)
      // 数据在 resData.data 里面
      userStats.value.postCount = resData.data?.remainingPostCount ?? userStats.value.postCount - 1
      ElMessage.success('帖子删除成功')
    } else {
      ElMessage.error(resData.message || '删除失败')
    }
  } catch (err) {
    if (err !== 'cancel') console.error('删除帖子出错:', err)
  }
}

// ------------------------------------------------------

// 3. 修改：删除想法逻辑
const handleDeleteThought = async (thoughtId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条想法吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    const { data: resData } = (await deleteUserNote(thoughtId)) as { data: BackendResponse }

    if (resData.code === 200) {
      thoughts.value = thoughts.value.filter((thought) => thought.id !== thoughtId)
      ElMessage.success('想法删除成功')
    } else {
      ElMessage.error(resData.message || '删除失败')
    }
  } catch (err) {
    if (err !== 'cancel') console.error('删除想法出错:', err)
  }
}

// ------------------------------------------------------

// 4. 修改：删除书评逻辑
const handleDeleteReview = async (id: string | number) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇书评吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    const { data: resData } = (await deleteUserReview(id)) as {
      data: BackendResponse<{ remainingReviewCount: number }>
    }

    if (resData.code === 200) {
      reviews.value = reviews.value.filter((review) => review.id !== id)
      if (resData.data?.remainingReviewCount !== undefined) {
        reviewTotalCount.value = resData.data.remainingReviewCount
      }
      ElMessage.success('书评删除成功')
    } else {
      ElMessage.error(resData.message || '删除失败')
    }
  } catch (err) {
    if (err !== 'cancel') console.error('删除书评出错:', err)
  }
}

const handleFollowingUpdate = (updatedUsers: FollowUser[]) => {
  followingList.value = updatedUsers
  userStats.value.followingCount = updatedUsers.length
}
const handleFollowersUpdate = (updatedUsers: FollowUser[]) => {
  followersList.value = updatedUsers
}

const goToCreatePost = () => console.log('跳转到发布页面')
const goToCreateThought = () => console.log('跳转到记录想法页面')
const goToCreateReview = () => console.log('跳转到撰写书评页面')

/* -----------------------------------------
       6. 生命周期与监听 (保持不变)
    ----------------------------------------- */
onMounted(async () => {
  window.scrollTo(0, 0)
  const tabParam = route.query.tab as string
  if (tabParam && tabTitleMap[tabParam]) {
    currentTab.value = tabParam
  }
  await Promise.all([
    fetchUserPosts(),
    fetchUserThoughts(),
    fetchUserReviews(),
    fetchUserRelations(),
  ])
})

watch(
  () => route.query.tab,
  (newTab) => {
    if (newTab && tabTitleMap[newTab as string]) {
      currentTab.value = newTab as string
    }
  },
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
