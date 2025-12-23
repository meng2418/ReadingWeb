<template>
  <div class="post-detail-page">
    <NavBar />
    <div class="post-detail-container" v-if="loaded">
      <!-- 左侧主内容区 -->
      <div class="left-column">
        <PostDetail />

        <!-- 新增：评论/点赞切换 Tab -->
        <div class="interaction-tabs">
          <button
            class="tab-btn"
            :class="{ active: isCommentTabActive }"
            @click="isCommentTabActive = true"
          >
            评论
          </button>
          <button
            class="tab-btn"
            :class="{ active: !isCommentTabActive }"
            @click="isCommentTabActive = false"
          >
            点赞
          </button>
        </div>

        <!-- 根据 Tab 状态显示不同内容 -->
        <div class="interaction-content">
          <CommentSection v-if="isCommentTabActive" :postId="postId" />
          <LikeSection v-else :likes="post.likes" />
        </div>
      </div>

      <!-- 右侧栏 -->
      <div class="right-column">
        <PostBook :book="post.book" />
      </div>
    </div>

    <!-- 加载中 -->
    <div v-else class="loading">加载中…</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import PostDetail from '@/components/community/PostDetail/PostDetail.vue'
import CommentSection from '@/components/community/PostDetail/CommentSection.vue'
import PostBook from '@/components/community/PostDetail/PostBook.vue'
import LikeSection from '@/components/community/PostDetail/LikeSection.vue'
import defaultAvatar from '@/img/avatar.jpg'
import defaultCover from '@/img/cover.jpg'
import NavBar from '@/components/layout/NavBar.vue'
// 获取路由参数
const route = useRoute()
const postId = route.params.id

// 本页数据
const post = ref(null)
const loaded = ref(false)
const isFollowed = ref(false)
// 新增：控制 Tab 切换的响应式变量
const isCommentTabActive = ref(true)
// 模拟请求（替换为 axios 请求即可）
const fetchPostDetail = async () => {
  // 假装从后端拿数据
  // 实际开发你会用 axios.get(`/api/post/${postId}`)
  ;((post.value = {
    id: postId,
    title: '不是她的故事，是她们的故事',
    author: {
      name: 'Nana',
      avatar: defaultAvatar,
    },
    time: '2024-07-25 15:46',
    location: '上海',
    edited: true,
    spoiler: true,
    tags: ['最喜欢的书'],
    content: [
      '这个电影前9/10的部分，看了都让人窝火……',
      '前面可能会气竭，并且看的时候伴随着压抑……',
      '很重要的一幕是……',
    ],
    book: {
      title: '呼啸山庄',
      cover: defaultCover,
    },
    // 新增：点赞用户列表（用于 LikeSection 组件）
    likes: [
      {
        id: 1,
        user: { id: 101, name: '电影爱好者小李', avatar: defaultAvatar },
        timestamp: new Date(Date.now() - 1000 * 60 * 5).toISOString(), // 5分钟前
      },
      {
        id: 2,
        user: { id: 102, name: '书评人阿泽', avatar: defaultAvatar },
        timestamp: new Date(Date.now() - 1000 * 60 * 60).toISOString(), // 1小时前
      },
      {
        id: 3,
        user: { id: 103, name: '一个很长很长很长的用户名', avatar: defaultAvatar },
        timestamp: new Date(Date.now() - 1000 * 60 * 60 * 24).toISOString(), // 1天前
      },
    ],
  }),
    (loaded.value = true))
}

onMounted(fetchPostDetail)
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

/* 新增：评论/点赞切换 Tab 样式 */
.interaction-tabs {
  grid-column: 1 / -1;
  display: flex;
  /* 关键：为父容器添加整体的白色背景、边框和圆角 */
  background-color: #fff;
  border: 1px solid #e5e7eb; /* 淡灰色边框 */
  border-radius: 12px; /* 圆润的 corners */
  overflow: hidden; /* 确保内部按钮的圆角不会溢出 */
  margin-bottom: 16px;
}

.tab-btn {
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

.tab-btn:hover {
  color: #1f2937; /* 深灰色文字 */
  background-color: var(--shadow-green);
}

/* 激活状态的 Tab 按钮 */
.tab-btn.active {
  color: var(--sun-back);
  font-weight: 600;
  background-color: var(--bg-green);
}
</style>
