<!--PostDetailPage.vue-->
<template>
  <div class="post-detail-page">
    <NavBar />
    <div class="post-detail-container" v-if="loaded && post">
      <div class="left-column">
        <PostDetail
          :post="post"
          :current-user-id="currentUserId"
          @update-like="handleLikeUpdate"
          @update-follow="handleFollowUpdate"
        />

        <div class="interaction-tabs">
          <button
            class="tab-btn"
            :class="{ active: isCommentTabActive }"
            @click="isCommentTabActive = true"
          >
            评论 {{ comments ? comments.length : 0 }}
          </button>
          <button
            class="tab-btn"
            :class="{ active: !isCommentTabActive }"
            @click="isCommentTabActive = false"
          >
            点赞 {{ likedData ? likedData.totalLikes : 0 }}
          </button>
        </div>

        <div class="interaction-content">
          <CommentSection
            v-if="isCommentTabActive"
            ref="commentSectionRef"
            :postId="postId"
            :initialComments="comments"
            @add-reply="handleAddReply"
            @publish-comment="handlePublishComment"
          />
          <LikeSection v-else :likes="likedData.likedUsers" />
        </div>
      </div>

      <!-- PostDetailPage.vue -->
      <div class="right-column">
        <!-- 1. 建立一个包裹容器，承载所有的书籍卡片 -->
        <!-- 2. 将 sticky 效果应用在这里，而不是单个卡片上 -->
        <div class="sidebar-sticky-wrapper" v-if="post?.mentionedBooks?.length">
          <PostBook
            v-for="(item, index) in post.mentionedBooks"
            :key="index"
            :book="{
              title: item.bookTitle,
              author: item.authorName,
              cover: item.cover,
              description: item.description,
              bookId: item.bookId,
            }"
          />
        </div>
      </div>
    </div>

    <div v-else class="loading">正在加载帖子详情...</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '@/components/layout/NavBar.vue'
import PostDetail from '@/components/community/PostDetail/PostDetail.vue'
import CommentSection from '@/components/community/PostDetail/CommentSection.vue'
import PostBook from '@/components/community/PostDetail/PostBook.vue'
import LikeSection from '@/components/community/PostDetail/LikeSection.vue'
// 引入修改后的 API 方法
import {
  getPostDetail,
  getPostComments,
  getPostLikes,
  replyComment,
  publishComment,
} from '@/api/post'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import type { PostDetailResponse } from '@/api/post'
import { getProfileHome } from '@/api/profile'

const route = useRoute()
const postId = route.params.id as string
const userStore = useUserStore()

const post = ref<PostDetailResponse | null>(null)
const loaded = ref(false)
const isCommentTabActive = ref(true)
const commentSectionRef = ref()
const currentUserId = ref<number | null>(null)

// 响应式数据
const comments = ref<any[]>([])
const likedData = ref({ totalLikes: 0, likedUsers: [] })

/**
 * 2. 提取获取点赞列表的独立函数
 */
const fetchPostLikes = async () => {
  try {
    const likeRes = await getPostLikes(postId)
    likedData.value = likeRes

    // 如果 post 已经加载了，同步更新 post 里的 likeCount
    if (post.value) {
      post.value.likeCount = likeRes.totalLikes
    }
  } catch (error) {
    console.error('刷新点赞列表失败', error)
  }
}

const handleLikeUpdate = async (payload: { isLiked: boolean; likeCount: number }) => {
  if (!post.value) return

  // 1. 立即更新 UI (乐观更新)
  post.value.isLiked = payload.isLiked
  post.value.likeCount = payload.likeCount
  likedData.value.totalLikes = payload.likeCount

  // 2. 刷新点赞列表
  // 如果你的接口支持，这里其实最好是重新获取最新的 likedUsers
  await fetchPostLikes()
}
// 处理关注状态更新
const handleFollowUpdate = (isFollowing: boolean) => {
  if (post.value) {
    post.value.isFollowingAuthor = isFollowing
  }
}

// 处理添加回复（结合 API 的正确写法）
const handleAddReply = async (payload: { parentId: number; content: string }) => {
  try {
    // 2. 修正函数名：使用 replyComment
    // 3. 修正参数：直接使用本地的 postId，而不是 props.postId
    // 注意：根据你的 api 定义，replyComment 接收 (parentId, content)
    const resData = await replyComment(payload.parentId, payload.content)

    // 4. 从接口返回的响应中获取真实的评论数据
    // 你在 api/post.ts 里定义的返回结构是 res.data.data，在函数里 return res.data.data
    // 所以这里的 resData 就是那个包含了 { comment: {...} } 的对象
    const realComment = resData.comment

    // 5. 构造要显示在界面上的对象
    const newReply = {
      id: realComment.commentId,
      username: realComment.username,
      avatar: realComment.avatar,
      content: realComment.content,
      commentTime: realComment.commentTime,
      replies: [],
    }

    // 6. 插入到界面
    const parentComment = comments.value.find((c) => c.id === payload.parentId)
    if (parentComment) {
      if (!parentComment.replies) parentComment.replies = []
      parentComment.replies.push(newReply)
      ElMessage.success('回复成功')
    }
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error('回复失败，请稍后再试')
  }
}

const handlePublishComment = async (content: string) => {
  try {
    // 调用 publishComment(postId, content)
    // 注意：postId 是当前组件已定义的局部变量
    const res = await publishComment(postId, content)

    // res 结构: { comment: { commentId, ... }, commentCount: number }
    const serverComment = res.comment

    // 字段转换：将 commentId 映射为 id
    // 构造评论对象，结构必须与 formattedComments 所需字段匹配
    const newComment = {
      id: serverComment.commentId,
      username: serverComment.username || userStore.userInfo.name,
      avatar: serverComment.avatar || userStore.userInfo.avatar,
      content: serverComment.content,
      commentTime: serverComment.commentTime,
      likeCount: 0,
      replies: [],
    }

    // 更新列表：插入到 comments.value 数组的最前面
    comments.value.unshift(newComment)

    // 更新评论数
    if (post.value) {
      post.value.commentCount = res.commentCount
    }

    // 清理工作：通知 CommentSection 清空输入框
    if (commentSectionRef.value) {
      commentSectionRef.value.clearInput()
    }

    ElMessage.success('评论发表成功')
  } catch (error) {
    console.error('发表评论失败', error)
    ElMessage.error('发表评论失败')
  }
}

// 刷新评论列表（保留但不使用，用于其他场景）
const refreshComments = async () => {
  try {
    const commentRes = await getPostComments(postId)
    comments.value = commentRes
    // 确保数据被更新（Vue 的响应式系统会自动处理，但这里明确赋值确保更新）
  } catch (error) {
    console.error('刷新评论列表失败', error)
    // 刷新失败时不显示错误提示，因为回复本身可能已经成功
  }
}

const fetchAllData = async () => {
  try {
    // 先获取当前用户信息（用于判断是否是自己的帖子）
    try {
      const userData = await getProfileHome()
      if (userData.userId) {
        currentUserId.value = userData.userId
      }
    } catch (error) {
      console.warn('获取当前用户信息失败，将无法判断是否是自己的帖子:', error)
    }

    // 先获取帖子详情和评论（必需数据）
    const [postRes, commentRes] = await Promise.all([
      getPostDetail(postId),
      getPostComments(postId),
    ])

    // API 返回完整的数据结构
    post.value = postRes
    comments.value = commentRes

    // 尝试获取点赞列表（可选数据，失败不影响帖子详情显示）
    // 注意：只有作者可以查看点赞详情，非作者访问会返回403
    try {
      const likeRes = await getPostLikes(postId)
      likedData.value = likeRes
    } catch (likeError: any) {
      console.warn('获取点赞列表失败（可能是非作者访问）:', likeError)
      // 非作者访问时，设置默认值，不显示错误提示
      if (likeError?.response?.status !== 403) {
        // 只有非403错误才显示提示
        console.error('获取点赞列表失败:', likeError)
      }
      // 设置默认值
      likedData.value = {
        totalLikes: postRes.likeCount || 0,
        likedUsers: [],
      }
    }

    loaded.value = true
  } catch (error: any) {
    console.error('加载失败', error)
    // 显示错误信息
    if (error?.response?.status === 404) {
      ElMessage.error('帖子不存在')
    } else if (error?.response?.status === 403) {
      ElMessage.error('无权限访问此帖子')
    } else if (error?.response?.status === 500) {
      ElMessage.error('服务器错误，请稍后再试')
      console.error('服务器错误详情:', error?.response?.data)
    } else if (error?.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error(error?.response?.data?.message || '加载帖子详情失败，请稍后再试')
    }
    // 即使失败也设为 true 以显示界面（或显示错误页）
    loaded.value = true
  }
}

onMounted(fetchAllData)
</script>

<style scoped>
.post-detail-page {
  background-color: #f9f9f9;
}
.loading {
  text-align: center;
  padding: 40px;
  color: #888;
}
/* 整体容器 */
.post-detail-container {
  max-width: 1200px;
  margin: auto;
  display: flex;
  gap: 30px;
  padding: 0 20px;
}

/* 左侧栏（占比更大） */
.left-column {
  flex: 3;
  margin-bottom: 60px;
  margin-top: 80px;
}

/* 右侧栏（占比更小） */
.right-column {
  flex: 1;
  min-width: 220px;
}
/* 侧边栏粘性容器 */
.sidebar-sticky-wrapper {
  position: sticky;
  top: 6rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem; /* 多本书之间的间距 */
}
/* Tab 样式还原 */
.interaction-tabs {
  grid-column: 1 / -1;
  display: flex;
  background-color: #fff;
  border: 1px solid #e5e7eb; /* 淡灰色边框 */
  border-radius: 12px; /* 圆润的 corners */
  overflow: hidden; /* 确保内部按钮的圆角不会溢出 */
  margin-bottom: 16px;
}

.tab-btn {
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

.tab-btn:hover {
  color: #1f2937; /* 深灰色文字 */
  /* 还原原本的 CSS 变量 */
  background-color: var(--shadow-green);
}

/* 激活状态的 Tab 按钮 */
.tab-btn.active {
  /* 还原原本的 CSS 变量 */
  color: var(--sun-back);
  font-weight: 600;
  background-color: var(--bg-green);
}
</style>
