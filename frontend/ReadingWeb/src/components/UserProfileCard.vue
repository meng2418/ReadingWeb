<template>
  <div class="user-profile-card">
    <div class="user-avatar">
      <img :src="user.avatar" alt="用户头像" />
    </div>
    <div class="user-info">
      <div class="username">{{ user.username }}</div>
      <div class="bio">{{ user.bio }}</div>
      <div class="stats">
        <div class="stat" @click="handleStatClick('follow')">
          <div class="number">{{ user.followCount }}</div>
          <div class="label">关注</div>
        </div>
        <div class="stat" @click="handleStatClick('fans')">
          <div class="number">{{ user.fansCount }}</div>
          <div class="label">粉丝</div>
        </div>
        <div class="stat" @click="handleStatClick('post')">
          <div class="number">{{ user.postCount }}</div>
          <div class="label">发布</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface User {
  username: string
  bio: string
  avatar: string
  followCount: number
  fansCount: number
  postCount: number
}

// 定义props
const { user } = defineProps<{
  user: User
}>()

// 如果没有传入user，使用默认数据
const user = props.user || {
  username: '昵称',
  bio: '个性签名',
  avatar: '@/img/avatar.jpg',
  followCount: 16,
  fansCount: 1636,
  postCount: 8,
// 定义事件
}
const emit = defineEmits<{
  statClick: [type: 'follow' | 'fans' | 'post']
}>()

// 统计项点击处理
const handleStatClick = (type: 'follow' | 'fans' | 'post') => {
  console.log(`点击了${type}统计`)
  emit('statClick', type)
}
</script>

<style scoped>
.user-profile-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  max-width: 450px;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  background: #f0f0f0;
  margin-bottom: 15px;
  border: 3px solid #f0f0f0;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  width: 100%;
}

.username {
  font-weight: bold;
  font-size: 22px;
  margin-bottom: 8px;
  color: #333;
}

.bio {
  color: #666;
  font-size: 16px;
  margin-bottom: 20px;
  line-height: 1.4;
}

.stats {
  display: flex;
  justify-content: space-between;
  width: 100%;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.stat {
  text-align: center;
  flex: 1;
  cursor: pointer; /* 添加手型光标 */
  transition: all 0.2s; /* 添加过渡效果 */
  padding: 8px 4px; /* 增加内边距，扩大点击区域 */
  border-radius: 6px; /* 添加圆角 */
}

.stat:hover {
  background: #f5f5f5; /* 悬停背景色 */
  transform: translateY(-1px); /* 悬停轻微上移 */
}

.number {
  font-weight: bold;
  font-size: 20px;
  color: #007fff;
  margin-bottom: 5px;
}

.label {
  font-size: 14px;
  color: #999;
}
</style>
