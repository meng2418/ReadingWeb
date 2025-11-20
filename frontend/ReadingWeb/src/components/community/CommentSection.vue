<!-- components/CommentSection.vue -->
<template>
  <div class="comment-section">
    <!-- 评论统计 -->
    <div class="comment-header">
      <h2 class="comment-title">评论区 ({{ commentList.length }})</h2>
    </div>

    <!-- 评论输入框 -->
    <div class="comment-input-container">
      <img src="https://via.placeholder.com/40" alt="当前用户头像" class="user-avatar" />
      <div class="input-wrapper">
        <textarea
          v-model="newComment"
          class="comment-input"
          placeholder="分享你的看法..."
          @keyup.enter="handleSubmit"
        ></textarea>
        <button class="submit-btn" @click="handleSubmit" :disabled="!newComment.trim()">
          发表评论
        </button>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list">
      <div class="comment-item" v-for="(comment, index) in commentList" :key="index">
        <!-- 评论用户头像 -->
        <img :src="comment.avatar" :alt="comment.nickname" class="comment-avatar" />

        <!-- 评论内容区域 -->
        <div class="comment-content">
          <div class="comment-user-info">
            <span class="comment-nickname">{{ comment.nickname }}</span>
            <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
          </div>
          <p class="comment-text">{{ comment.content }}</p>

          <!-- 评论操作（可选） -->
          <div class="comment-actions">
            <span class="like-btn" @click="toggleLike(index)">
              <i class="icon-like">
                <Heart
                  :stroke-width="1.5"
                  :fill="comment.isLiked ? '#ff6b6b' : 'transparent'"
                  :stroke="comment.isLiked ? '#ff6b6b' : '#666'"
                  :class="{ liked: comment.isLiked }"
                />
              </i>
              <span>{{ comment.likeCount }}</span>
            </span>
          </div>
        </div>
      </div>

      <!-- 暂无评论提示 -->
      <div class="no-comment" v-if="commentList.length === 0">暂无评论，快来抢沙发～</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Heart } from 'lucide-vue-next'
// 初始评论数据（模拟已有评论）
const commentList = ref([
  {
    nickname: '电影爱好者小A',
    avatar: '@/img/avatar.jpg',
    content: '完全同意！最后10分钟的蒙太奇太有张力了，女性觉醒的瞬间直击人心～',
    createTime: new Date(Date.now() - 3600000 * 2).getTime(), // 2小时前
    likeCount: 12,
    isLiked: false,
  },
  {
    nickname: '阿泽',
    avatar: '@/img/avatar.jpg',
    content: '前面的压抑感真的太真实了，代入感超强，看到女主反抗的时候哭了',
    createTime: new Date(Date.now() - 3600000 * 8).getTime(), // 8小时前
    likeCount: 8,
    isLiked: false,
  },
  {
    nickname: '橘子汽水',
    avatar: '@/img/avatar.jpg',
    content: '导演对细节的把控很到位，每个女性角色都有自己的故事线，不是工具人',
    createTime: new Date(Date.now() - 3600000 * 24).getTime(), // 1天前
    likeCount: 5,
    isLiked: false,
  },
])

// 新评论输入内容
const newComment = ref('')

// 提交评论
const handleSubmit = () => {
  const content = newComment.value.trim()
  if (!content) return

  // 添加新评论到列表（模拟当前用户评论）
  commentList.value.unshift({
    nickname: '我',
    avatar: '@/img/avatar.jpg',
    content: content,
    createTime: Date.now(),
    likeCount: 0,
    isLiked: false,
  })

  // 清空输入框
  newComment.value = ''
}

// 切换点赞状态
const toggleLike = (index) => {
  const comment = commentList.value[index]
  if (comment.isLiked) {
    comment.likeCount--
  } else {
    comment.likeCount++
  }
  comment.isLiked = !comment.isLiked
}

// 格式化时间（多久前）
const formatTime = (timestamp) => {
  const now = Date.now()
  const diff = now - timestamp
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  const month = 30 * day
  const year = 12 * month

  if (diff < minute) return '刚刚'
  if (diff < hour) return `${Math.floor(diff / minute)}分钟前`
  if (diff < day) return `${Math.floor(diff / hour)}小时前`
  if (diff < month) return `${Math.floor(diff / day)}天前`
  if (diff < year) return `${Math.floor(diff / month)}个月前`
  return `${Math.floor(diff / year)}年前`
}
</script>

<style scoped>
.comment-section {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #eee;
}

/* 评论头部 */
.comment-header {
  margin-bottom: 20px;
}
.comment-title {
  font-size: 20px;
  color: #333;
  font-weight: 600;
}

/* 评论输入框 */
.comment-input-container {
  display: flex;
  gap: 12px;
  margin-bottom: 32px;
}
.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}
.input-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.comment-input {
  width: 97%;
  padding: 12px;
  border: 1px solid #eee;
  border-radius: 8px;
  resize: none;
  height: 100px;
  font-size: 14px;
  color: #333;
  transition: border-color 0.3s;
}
.comment-input:focus {
  outline: none;
  border-color: #409eff;
}
.submit-btn {
  align-self: flex-end;
  padding: 8px 20px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.3s;
}
.submit-btn:disabled {
  background: #a0cfff;
  cursor: not-allowed;
}
.submit-btn:hover:not(:disabled) {
  background: #3086d6;
}

/* 评论列表 */
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.comment-item {
  display: flex;
  gap: 12px;
}
.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}
.comment-content {
  flex: 1;
  background: #fafafa;
  padding: 16px;
  border-radius: 10px;
}
.comment-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.comment-nickname {
  font-weight: 500;
  color: #333;
  font-size: 14px;
}
.comment-time {
  font-size: 12px;
  color: #999;
}
.comment-text {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
  margin-bottom: 10px;
}
.comment-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}
.like-btn {
  font-size: 14px;
  color: #999;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}
.icon-like {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.icon-like:hover {
  color: #ff6b6b;
  transform: scale(1.05);
}

.icon-like .liked {
  color: #ff6b6b;
  transition: all 0.2s ease;
  transform: scale(1.1); /* 选中时轻微放大，增强交互感 */
}

/* 覆盖原有 hover 样式，保持一致性 */
.icon-like:hover svg {
  color: #ff6b6b;
  transform: scale(1.05);
}

.comment-item:nth-child(odd) .comment-content {
  background: #f0f9ff;
}
.comment-item:nth-child(even) .comment-content {
  background: #fef7fb;
}

/* 暂无评论 */
.no-comment {
  text-align: center;
  color: #999;
  padding: 40px 0;
  font-size: 14px;
}
</style>
