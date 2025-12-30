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
// 当前用户信息（初始值设为空或默认值）
const currentUser = reactive({
  username: '加载中...',
  bio: '',
  avatar: '', // 初始使用本地默认图
  followCount: 0,
  fansCount: 0,
  postCount: 0,
})

// 热门话题
const hotTopics = ref([
  { id: 1, name: '# 好书推荐' },
  { id: 2, name: '# 阅读心得' },
  { id: 3, name: '# 文学讨论' },
  { id: 4, name: '# 经典名著' },
  { id: 5, name: '# 新书速递' },
  { id: 6, name: '# 读书笔记' },
  { id: 7, name: '# 作者访谈' },
  { id: 8, name: '# 阅读挑战' },
  { id: 9, name: '# 书单分享' },
])
const topicsList = ref([
  { id: 1, cover: 'https://picsum.photos/200?random=1', title: '每日读点小说', number: 200 },
  { id: 2, cover: 'https://picsum.photos/200?random=2', title: '科幻爱好者', number: 156 },
  { id: 3, cover: 'https://picsum.photos/200?random=3', title: '经典文学', number: 320 },
  { id: 4, cover: 'https://picsum.photos/200?random=4', title: '读书笔记精选', number: 187 },
  { id: 5, cover: 'https://picsum.photos/200?random=5', title: '外国名著', number: 98 },
  { id: 6, cover: 'https://picsum.photos/200?random=6', title: '推理与悬疑', number: 240 },
  { id: 7, cover: 'https://picsum.photos/200?random=7', title: '诗歌与散文', number: 142 },
  { id: 8, cover: 'https://picsum.photos/200?random=8', title: '新书速递', number: 75 },
  { id: 9, cover: 'https://picsum.photos/200?random=9', title: '阅读打卡挑战', number: 310 },
])

// 帖子数据
onMounted(async () => {
  // 并发请求所有数据
  try {
    const [postsData, commentsData, likesData, profileData] = await Promise.all([
      fetchCommunityPosts(),
      fetchMyComments(),
      fetchMyLikes(),
      getProfileHome(), // 获取用户信息
    ])

    posts.value = postsData
    commentList.value = commentsData
    likeList.value = likesData

    // 3. 将接口数据映射到 currentUser
    // 注意：这里将接口返回的字段名映射到你组件内使用的字段名
    Object.assign(currentUser, {
      username: profileData.username,
      bio: profileData.bio,
      avatar: profileData.avatar, // 如果后端没头像则用本地默认
      followCount: profileData.followingCount, // 接口字段是 followingCount
      fansCount: profileData.followerCount, // 接口字段是 followerCount
      postCount: profileData.postCount,
    })
  } catch (error) {
    console.error('加载数据失败:', error)
  }
})
const posts = ref<Post[]>([])
const commentList = ref<any[]>([])
const likeList = ref<any[]>([])
const currentTab = ref<'square' | 'following' | 'topics' | 'mine'>('square')
const changeTab = (tab: 'square' | 'following' | 'topics' | 'mine') => (currentTab.value = tab)
// “我的”内部的二级 Tab
const mineTab = ref<'like' | 'comment'>('comment')

// 动态页面标题
// 直接使用 computed
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
    case 'following':
      return posts.value.filter((p) => p.isFollowing)
    case 'mine':
      return posts.value.filter((p) => p.username === currentUser.username)
    default:
      return posts.value
  }
})

const handleTopicClick = (topic: any) => {
  console.log('点击话题:', topic.name)
}
// TODO: 等待接口文档确认

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
  // 这里可以添加跳转到评论页面或打开评论弹窗的逻辑
  // 例如：router.push(`/post/${postId}/comments`)
  void postId // 明确表示此参数未使用
  // TODO: 实现评论功能
}

// 转发事件
const handleShare = (postId: number): void => {
  // 这里可以添加分享逻辑
  // 例如：showShareDialog(postId)
  void postId // 明确表示此参数未使用
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
        <div v-if="currentTab === 'topics'" class="topics-grid">
          <Topic
            v-for="topic in topicsList"
            :id="topic.id"
            :key="topic.id"
            :cover="topic.cover"
            :title="topic.title"
            :number="topic.number"
          />
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
          </div>

          <!-- 赞 -->
          <div v-else>
            <LikeItem v-for="(item, index) in likeList" :key="index" :like="item" />
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
/* tabs */
.tabs {
  grid-column: 1 / -1;
  display: flex;
  /* 关键：为父容器添加整体的白色背景、边框和圆角 */
  background-color: #fff;
  border: 1px solid #e5e7eb; /* 淡灰色边框 */
  border-radius: 12px; /* 圆润的 corners */
  overflow: hidden; /* 确保内部按钮的圆角不会溢出 */
  width: 800px;
}

.tabs button {
  /* 关键：让按钮填满父容器的空间，并去掉边框和背景 */
  flex: 1; /* 四个按钮平分宽度 */
  background: none;
  border: none;
  outline: none;

  /* 内边距和字体 */
  padding: 10px 0; /* 上下 padding，左右由 flex 自动分配 */
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  color: #6b7280; /* 灰色文字 */

  /* 过渡效果 */
  transition: all 0.2s ease;
}

/* Hover 状态 */
.tabs button:hover {
  color: #1f2937; /* 深灰色文字 */
  background-color: var(--shadow-green);
}

/* Active 激活状态 */
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
}

.empty {
  padding: 24px;
  text-align: center;
  color: #888;
}
</style>
