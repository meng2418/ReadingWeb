<template>
  <div class="post-detail-page">
    <div class="post-detail-container" v-if="loaded">
      <!-- 左侧主内容区 -->
      <div class="left-column">
        <!-- 返回按钮 -->
        <div class="post-header">
          <button class="back-btn" @click="handleBack">← 返回社区</button>
        </div>
        <PostDetail />
        <CommentSection :postId="postId" />
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
import router from '@/router'
import defaultAvatar from '@/img/avatar.jpg'
import defaultCover from '@/img/cover.jpg'
// 获取路由参数
const route = useRoute()
const postId = route.params.id

// 本页数据
const post = ref(null)
const loaded = ref(false)
const isFollowed = ref(false)

const toggleFollow = () => {
  isFollowed.value = !isFollowed.value
}
// 模拟请求（替换为 axios 请求即可）
const fetchPostDetail = async () => {
  // 假装从后端拿数据
  // 实际开发你会用 axios.get(`/api/post/${postId}`)
  post.value = {
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
  }

  loaded.value = true
}

onMounted(fetchPostDetail)

const handleBack = () => {
  if (window.history.length > 1) router.back()
  else router.push('/community')
}
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
}

/* 右侧栏（占比更小） */
.right-column {
  flex: 1;
  min-width: 220px;
}
/* 新增：返回按钮 + 标题 样式 */

.back-btn {
  padding: 6px 12px;
  background: div(--shadow-green);
  color: #333;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s;
  margin: 20px 0;
}

.back-btn:hover {
  background: #eee;
  color: #409eff;
}
/* 帖子标题 */
.post-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}

/* 发布者信息 */
.publisher-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}
.publisher-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}
.publisher-meta {
  flex: 1;
}
.publisher-top {
  display: flex;
  align-items: center;
  gap: 8px;
}
.nickname {
  font-weight: 500;
}
.publisher-bottom {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  display: flex;
  gap: 8px;
}
.publisher-info {
  display: flex;
  align-items: center;
  justify-content: space-between; /* 关键：按钮靠右 */
  gap: 12px;
}

/* 关注按钮 */
.follow-btn {
  padding: 6px 14px;
  font-size: 14px;
  border-radius: 20px;
  border: 1px solid #409eff;
  background: #fff;
  color: #409eff;
  cursor: pointer;
  transition: all 0.25s ease;
  white-space: nowrap;
}

.follow-btn:hover {
  background: #ecf5ff;
}

.follow-btn.followed {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}

.follow-btn.followed:hover {
  opacity: 0.9;
}

/* 帖子正文 */
.post-content {
  line-height: 1.8;
  color: #333;
  margin-bottom: 24px;
}
.post-content p {
  margin-bottom: 12px;
}

/* 话题标签 */
.topic-tag {
  font-size: 14px;
  color: #409eff;
  margin-bottom: 32px;
}
</style>
