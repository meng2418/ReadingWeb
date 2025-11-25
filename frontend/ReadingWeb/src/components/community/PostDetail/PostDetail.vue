<template>
  <article class="post-detail">
    <div class="post-content">
      <div class="author-header">
        <div class="author-info">
          <img :src="post.author.avatar" :alt="post.author.name" class="author-avatar" />
          <div class="author-name-container">
            <h3 class="author-name">{{ post.author.name }}</h3>
            <div class="post-meta">
              <span class="post-time">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="12"
                  height="12"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <circle cx="12" cy="12" r="10"></circle>
                  <polyline points="12 6 12 12 16 14"></polyline>
                </svg>
                {{ post.timestamp }}
              </span>
              <span v-if="post.isEdited" class="post-edited">• Edited</span>
              <span v-if="post.author.location" class="post-location">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="12"
                  height="12"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"></path>
                  <circle cx="12" cy="10" r="3"></circle>
                </svg>
                {{ post.author.location }}
              </span>
            </div>
          </div>
        </div>
        <button
          @click="isFollowing = !isFollowing"
          :class="isFollowing ? 'following-button' : 'follow-button'"
        >
          {{ isFollowing ? '已关注' : '关注' }}
        </button>
      </div>

      <h1 class="post-title">{{ post.title }}</h1>

      <div class="post-body">
        <p v-for="(paragraph, idx) in post.content" :key="idx" class="post-paragraph">
          {{ paragraph }}
        </p>
      </div>

      <div class="tags-container">
        <span v-for="tag in post.tags" :key="tag" class="tag"> #{{ tag }} </span>
      </div>

      <div class="action-bar">
        <div class="left-actions">
          <!-- Placeholder for like stats if needed -->
        </div>
        <div class="right-actions">
          <button
            class="action-button"
            :class="{ liked: isLiked }"
            title="Like"
            @click="toggleLike"
          >
            <Heart />
            <span v-if="likeCount" class="like-count">{{ likeCount }}</span>
          </button>
          <button class="action-button" title="Share">
            <el-icon><Share /></el-icon>
          </button>
        </div>
      </div>
    </div>
  </article>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import DefaultAvatar from '@/img/avatar.jpg'
import { Comment, Share } from '@element-plus/icons-vue'
import { Heart } from 'lucide-vue-next'
// 直接在组件内部定义 post 数据
const post = ref({
  id: 1,
  title: '探寻林奕含的“真相”',
  content: [
    '——你以血书，我以泪和。',
    '这几天又在看林奕含的各种视频。',
    '去年只看了那一期书房里、她离世8天前的采访，这一次才发现了更多线索。（锵锵三人行那一期，真的是太粗糙了，窦文涛对她没有丝毫了解，仅凭一些花边新闻就开始拼凑想象。反倒是另一位女嘉宾说了几句掷地有声的话。所以如果只看到事物的片面，真的不如完全不看。不知道这期节目，给观众带来了多少对林奕含的误解！）',
    '同时看到了一个台湾三立新闻的谈话，某党立委林俊宪很激愤地说“他（陈国星）就是一个变态！”我一开始是感到比较新奇，因为很少见政界人士直接在电视节目中用“变态”这样直白的词，看到后面，我才知道他为什么如此激愤。',
  ],
  author: {
    name: 'Sarah Johnson',
    avatar: DefaultAvatar,
    location: 'San Francisco, CA',
  },
  timestamp: '2 hours ago',
  isEdited: false,
  tags: ['房思琪的初恋乐园', '台湾文学', '林奕含'],
})

// 响应式状态
const isFollowing = ref(false)

const isLiked = ref(false)
const likeCount = ref(12)

const toggleLike = () => {
  isLiked.value = !isLiked.value
  likeCount.value += isLiked.value ? 1 : -1
}
</script>

<style scoped>
/* 文章容器 */
.post-detail {
  background-color: #fff;
  border-radius: 1.25rem; /* 20px */
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  border: 1px solid #f1f5f9; /* slate-100 */
  overflow: hidden;
  margin-bottom: 2rem; /* 32px */
}

/* 文章内容区 */
.post-content {
  padding: 1.5rem; /* 24px */
}

/* 作者头部信息 */
.author-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.5rem; /* 24px */
}

/* 作者信息（头像 + 名称） */
.author-info {
  display: flex;
  align-items: center;
  gap: 1rem; /* 16px */
}

/* 作者头像 */
.author-avatar {
  width: 3rem; /* 48px */
  height: 3rem; /* 48px */
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow:
    0 4px 6px -1px rgba(0, 0, 0, 0.1),
    0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

/* 作者名称容器 */
.author-name-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* 作者名称 */
.author-name {
  font-weight: 700;
  color: #0f172a; /* slate-900 */
  font-size: 1.125rem; /* 18px */
  line-height: 1.25; /* leading-tight */
}

/* 文章元信息（时间、编辑状态、位置） */
.post-meta {
  display: flex;
  align-items: center;
  font-size: 0.75rem; /* 12px */
  color: #64748b; /* slate-500 */
  gap: 0.5rem; /* 8px */
  margin-top: 0.125rem; /* 2px */
}

/* 文章元信息中的各个部分 */
.post-time,
.post-location {
  display: flex;
  align-items: center;
  gap: 0.25rem; /* 4px */
}

.post-edited {
  font-size: 0.75rem;
}

/* 关注按钮 */
.follow-button {
  padding: 0.375rem 1.25rem; /* 6px 20px */
  border-radius: 9999px; /* rounded-full */
  font-size: 0.875rem; /* 14px */
  font-weight: 500;
  background-color: var(--primary-pink);
  color: var(--sun-back);
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 1px 2px var(--shadow-color);
}

.follow-button:hover {
  background-color: var(--primary-pink-hover);
  box-shadow:
    0 4px 6px -1px rgba(0, 0, 0, 0.1),
    0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

/* 已关注按钮 */
.following-button {
  padding: 0.375rem 1.25rem; /* 6px 20px */
  border-radius: 9999px; /* rounded-full */
  font-size: 0.875rem; /* 14px */
  font-weight: 500;
  background-color: #f1f5f9; /* slate-100 */
  color: #475569; /* slate-600 */
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.following-button:hover {
  background-color: #e2e8f0; /* slate-200 */
}

/* 文章标题 */
.post-title {
  font-size: 1.5rem; /* 24px */
  font-weight: 700;
  color: #0f172a; /* slate-900 */
  margin-bottom: 1.5rem; /* 24px */
  line-height: 1.25; /* leading-tight */
  letter-spacing: -0.025em; /* tracking-tight */
}

/* 文章正文容器 */
.post-body {
  color: #334155; /* slate-700 */
  margin-bottom: 2rem; /* 32px */
  font-size: 1.125rem; /* 18px */ /* prose-lg */
  line-height: 1.625; /* leading-relaxed */
}

/* 文章段落 */
.post-paragraph {
  margin-bottom: 1rem; /* 16px */
}

/* 标签容器 */
.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem; /* 8px */
  margin-bottom: 1.5rem; /* 24px */
}

/* 标签样式 */
.tag {
  padding: 0.25rem 0.75rem; /* 4px 12px */
  background-color: rgba(37, 99, 235, 0.1); /* brand-50 */
  color: #2563eb; /* brand-600 */
  font-size: 0.875rem; /* 14px */
  border-radius: 9999px; /* rounded-full */
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.tag:hover {
  background-color: rgba(37, 99, 235, 0.2); /* brand-100 */
}

/* 操作栏 */
.action-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 1.5rem; /* 24px */
  border-top: 1px solid #f1f5f9; /* slate-100 */
}

/* 右侧操作按钮组 */
.right-actions {
  display: flex;
  gap: 0.75rem; /* 12px */
}

/* 操作按钮 */
.action-button {
  color: #94a3b8; /* slate-400 */
  background: none;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s ease;
}

/* 让所有 action-button 内的 SVG 图标保持统一大小 & 颜色继承 */
.action-button svg {
  width: 20px;
  height: 20px;
  stroke: currentColor; /* 让 lucide 图标继承颜色 */
  fill: none;
}

/* element-plus 的图标默认 fill，需要手动改为 inherit */
.action-button .el-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: inherit;
}

/* 点赞后的颜色变化 */
.liked {
  color: #ef4444; /* red-500 */
}

/* 点赞数字 */
.like-count {
  font-size: 0.75rem;
  margin-left: 0.25rem;
  color: #94a3b8;
  font-weight: 600;
}
/* 点赞后：数字变红 */
.liked .like-count {
  color: #ef4444; /* red-500 */
}

/* 点按反馈 */
.action-button:active {
  transform: scale(0.85);
}

/* 点赞时微动效 */
.action-button .liked {
  animation: pop 0.25s ease;
}
/* 响应式调整 */
@media (min-width: 768px) {
  .post-content {
    padding: 2rem; /* 32px */
  }
  .post-title {
    font-size: 1.875rem; /* 30px */
  }
}
</style>
