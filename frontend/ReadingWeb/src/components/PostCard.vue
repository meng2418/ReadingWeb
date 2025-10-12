<template>
  <div class="post-card">
    <!-- 用户信息头部 -->
    <div class="post-header">
      <!-- 修复头像显示 -->
      <div class="avatar-container">
        <img v-if="avatar && avatar !== ''" :src="avatar" alt="头像" class="avatar-img">
        <div v-else class="avatar-placeholder">
          {{ username.charAt(0) }}
        </div>
      </div>
      <div class="user-info">
        <div class="username">{{ username }}</div>
        <div class="post-time">{{ postTime }}</div>
      </div>
      <!-- 右上角关注按钮 -->
      <button class="follow-btn" @click="toggleFollow">
        {{ isFollowing ? '已关注' : '+ 关注' }}
      </button>
    </div>

    <!-- 帖子标题 -->
    <div class="post-title" v-if="title">
      {{ title }}
    </div>

    <!-- 帖子内容 -->
    <div class="post-content">
      {{ content }}
    </div>

    <!-- 互动操作区域 -->
    <div class="post-actions">
      <div class="action-item" @click="sharePost">
        <div class="icon-share"></div>
        <span class="action-count" v-if="shareCount > 0">{{ shareCount }}</span>
      </div>

      <div class="action-item" @click="commentPost">
        <div class="icon-comment"></div>
        <span class="action-count" v-if="commentCount > 0">{{ commentCount }}</span>
      </div>

      <div class="action-item" @click="handleLike" :class="{ liked: localIsLiked }">
        <div class="icon-heart"></div>
        <span class="action-count" v-if="localLikeCount > 0">{{ localLikeCount }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PostCard',
  props: {
    username: {
      type: String,
      required: true
    },
    avatar: {
      type: String,
      default: ''
    },
    postTime: {
      type: String,
      default: '刚刚'
    },
    title: {
      type: String,
      default: ''
    },
    content: {
      type: String,
      required: true
    },
    likeCount: {
      type: Number,
      default: 0
    },
    commentCount: {
      type: Number,
      default: 0
    },
    shareCount: {
      type: Number,
      default: 0
    },
    isFollowing: {
      type: Boolean,
      default: false
    },
    isLiked: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      localIsLiked: this.isLiked,
      localLikeCount: this.likeCount,
      localIsFollowing: this.isFollowing
    }
  },
  methods: {
    handleLike() {
      // 切换点赞状态
      this.localIsLiked = !this.localIsLiked;

      // 更新点赞数量
      if (this.localIsLiked) {
        this.localLikeCount++;
      } else {
        this.localLikeCount = Math.max(0, this.localLikeCount - 1);
      }

      // 触发事件
      this.$emit('like', this.localLikeCount, this.localIsLiked);
    },
    commentPost() {
      this.$emit('comment');
    },
    sharePost() {
      this.$emit('share');
    },
    toggleFollow() {
      this.localIsFollowing = !this.localIsFollowing;
      this.$emit('follow', this.localIsFollowing);
    }
  }
}
</script>

<style scoped>
.post-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
  position: relative;
  width: 100%;
  max-width: 1200px; /* 增加最大宽度 */
  min-height: 280px; /* 设置最小高度 */
}

.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px; /* 增加底部间距 */
  position: relative;
}

/* 头像容器 */
.avatar-container {
  width: 48px; /* 增大头像尺寸 */
  height: 48px;
  margin-right: 14px; /* 增加右边距 */
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 真实头像图片 */
.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 2px solid #f5f5f5;
  object-fit: cover; /* 确保图片比例正确 */
}

/* 头像占位符 */
.avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: 2px solid #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 20px; /* 增大字体 */
}

.user-info {
  flex: 1;
}

.username {
  font-weight: 600;
  font-size: 18px; /* 增大用户名字体 */
  color: #333;
  margin-bottom: 4px; /* 增加底部间距 */
}

.post-time {
  font-size: 14px; /* 增大时间字体 */
  color: #999;
}

/* 关注按钮样式 */
.follow-btn {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  background: #f8f9fa;
  color: #ff6b6b;
  border: 1px solid #ff6b6b;
  border-radius: 18px; /* 增大圆角 */
  padding: 10px 20px; /* 增大内边距 */
  font-size: 14px; /* 增大字体 */
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.follow-btn:hover {
  background: #ff6b6b;
  color: white;
  transform: translateY(-50%) scale(1.05);
}

.post-title {
  font-size: 20px; /* 增大标题字体 */
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 12px; /* 增加底部间距 */
  line-height: 1.4;
}

.post-content {
  color: #333;
  line-height: 1.7; /* 增加行高 */
  margin-bottom: 20px; /* 增加底部间距 */
  font-size: 16px; /* 增大正文字体 */
  min-height: 120px; /* 设置内容最小高度 */
}

.post-actions {
  display: flex;
  justify-content: space-around;
  border-top: 1px solid #f5f5f5;
  padding-top: 16px; /* 增加顶部内边距 */
}

/*操作按钮尺寸*/
.action-item {
  display: flex;
  align-items: center;
  padding: 8px 20px; /* 增大内边距 */
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  gap: 8px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
  border: 1px solid #f0f0f0;
  min-width: 90px; /* 增大最小宽度 */
  justify-content: center;
}

.action-item:hover {
  background-color: #f8f9fa;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.18);
  transform: translateY(-2px);
}

.action-count {
  font-size: 15px; /* 增大数字字体 */
  color: #999;
  min-width: 20px; /* 增大最小宽度 */
  text-align: center;
  font-weight: 500;
}

/* 自定义图标样式 */
.icon-heart,
.icon-comment,
.icon-share {
  width: 26px; /* 增大图标尺寸 */
  height: 26px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 爱心图标 - 使用纯CSS绘制 */
.icon-heart {
  position: relative;
}

.icon-heart::before {
  content: "♡";
  font-size: 26px; /* 增大图标字体 */
  color: #666;
  transition: all 0.3s ease;
  line-height: 1;
}

.action-item.liked .icon-heart::before {
  content: "♥";
  color: #ff4757;
  font-size: 26px; /* 增大图标字体 */
}

/* 评论图标 */
.icon-comment::before {
  content: "💬";
  font-size: 24px; /* 增大图标字体 */
}

/* 分享图标 */
.icon-share::before {
  content: "🔄";
  font-size: 24px; /* 增大图标字体 */
}

/* 爱心点赞动画 */
@keyframes heartBeat {
  0% { transform: scale(1); }
  25% { transform: scale(1.4); }
  50% { transform: scale(1.2); }
  75% { transform: scale(1.3); }
  100% { transform: scale(1); }
}

.action-item.liked:active .icon-heart::before {
  animation: heartBeat 0.4s ease;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .post-card {
    max-width: 100%;
    margin: 0 16px 20px 16px;
    min-height: 250px;
  }

  .action-item {
    padding: 10px 16px;
    min-width: 80px;
  }
}
</style>
