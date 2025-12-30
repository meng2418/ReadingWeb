<template>
  <div class="post-detail-page">
    <NavBar />
    <div class="post-detail-container" v-if="loaded">
      <div class="left-column">
        <PostDetail :post="post" />

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
          <CommentSection v-if="isCommentTabActive" :postId="postId" :initialComments="comments" />
          <LikeSection v-else :likes="likedData.likedUsers" />
        </div>
      </div>

      <div class="right-column">
        <PostBook v-if="post && post.book" :book="post.book" />
      </div>
    </div>

    <div v-else class="loading">加载中…</div>
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
import { getPostDetail, getPostComments, getPostLikes } from '@/api/post'

const route = useRoute()
const postId = route.params.id as string

const post = ref<any>(null)
const loaded = ref(false)
const isCommentTabActive = ref(true)

// 响应式数据
const comments = ref<any[]>([])
const likedData = ref({ totalLikes: 0, likedUsers: [] })

const fetchAllData = async () => {
  try {
    const [postRes, commentRes, likeRes] = await Promise.all([
      getPostDetail(postId),
      getPostComments(postId),
      getPostLikes(postId),
    ])

    // API 层已经统一处理过数据结构，直接赋值
    post.value = postRes
    comments.value = commentRes
    likedData.value = likeRes

    loaded.value = true
  } catch (error) {
    console.error('加载失败', error)
    // 即使失败也设为 true 以显示界面（或显示错误页）
    loaded.value = true
  }
}

onMounted(fetchAllData)
</script>

<style scoped>
/* ========== 这里完全还原了你原始文件的样式 ========== */

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
