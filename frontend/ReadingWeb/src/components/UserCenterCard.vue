<template>
  <div class="user-center-card">
    <!-- 左侧：用户基本信息 -->
    <div class="user-left">
      <div class="user-avatar">
        <img :src="user.avatar" alt="用户头像" class="avatar-img">
        <div class="online-status" :class="{ online: user.isOnline }"></div>
      </div>

      <div class="user-info">
        <div class="name-section">
          <h2 class="username">{{ user.username }}</h2>
          <p class="user-bio">{{ user.bio }}</p>
        </div>

        <div class="action-buttons">
          <div class="coin-section" @click="handleCoinRecharge">
            <div class="coin-label">充值币</div>
            <div class="coin-amount">{{ user.coins }}</div>
          </div>
          <button class="vip-btn" @click="handleVipPurchase">
            <span class="btn-text">成为会员</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 中间：统计数据 -->
    <div class="user-stats">
      <button class="stat-item" @click="handleFollowClick">
        <div class="stat-number">{{ user.followCount }}</div>
        <div class="stat-label">关注</div>
      </button>
      <button class="stat-item" @click="handleFansClick">
        <div class="stat-number">{{ user.fansCount }}</div>
        <div class="stat-label">粉丝</div>
      </button>
      <button class="stat-item" @click="handlePostClick">
        <div class="stat-number">{{ user.postCount }}</div>
        <div class="stat-label">发布</div>
      </button>
    </div>

    <!-- 右侧：操作按钮 -->
    <div class="user-right">
      <div class="icon-buttons">
        <button class="icon-btn edit-btn" @click="handleEditProfile" title="编辑资料">
          <el-icon><Edit /></el-icon>
        </button>
        <button class="icon-btn background-btn" @click="handleChangeBackground" title="更换背景">
          <el-icon><Picture /></el-icon>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Edit, Picture } from '@element-plus/icons-vue'

interface User {
  username: string
  avatar: string
  bio: string
  isOnline: boolean
  followCount: number
  fansCount: number
  postCount: number
  coins: number
}

const { user } = defineProps<{
  user: User
}>()
// 定义事件
const emit = defineEmits<{
  editProfile: []
  changeBackground: []
  coinRecharge: []
  vipPurchase: []
  followClick: []
  fansClick: []
  postClick: []
}>()

// 事件处理函数
const handleEditProfile = () => {
  console.log('编辑资料点击')
  emit('editProfile')
}

const handleChangeBackground = () => {
  console.log('更换背景点击')
  emit('changeBackground')
}

const handleCoinRecharge = () => {
  console.log('充值币点击')
  emit('coinRecharge')
}

const handleVipPurchase = () => {
  console.log('成为会员点击')
  emit('vipPurchase')
}

const handleFollowClick = () => {
  console.log('关注点击')
  emit('followClick')
}

const handleFansClick = () => {
  console.log('粉丝点击')
  emit('fansClick')
}

const handlePostClick = () => {
  console.log('发布点击')
  emit('postClick')
}
</script>

<style scoped>
.user-center-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  max-width: 800px;
  min-height: 120px;
  display: flex;
  /*align-items: center;*/
  justify-content: space-between;
}

/* 左侧：用户基本信息 */
.user-left {
  display: flex;
  gap: 16px;
  flex: 1;
}

.user-avatar {
  position: relative;
  width: 60px;
  height: 60px;
  flex-shrink: 0;
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #f0f0f0;
}

.online-status {
  position: absolute;
  bottom: 4px;
  right: 4px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #ccc;
  border: 2px solid white;
}

.online-status.online {
  background: #4cd964;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.name-section {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.user-bio {
  font-size: 14px;
  color: #666;
  margin: 0;
  line-height: 1.4;
}

/* 操作按钮区域 */
.action-buttons {
  display: flex;
  gap: 20px;
}

.coin-section {
  display: flex;
  flex-direction: column;
  /*align-items: center;*/
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid #f0f0f0;
}

.coin-section:hover {
  background: #f9f9f9;
}

.coin-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.coin-amount {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.vip-btn {
  padding: 10px 20px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  background: transparent;
  transition: all 0.2s;
}

.vip-btn:hover {
  background: #f9f9f9;
}

/* 中间：统计数据 */
.user-stats {
  display: flex;
  gap: 30px;
  margin: 0 40px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 2px 4px;
  border-radius: 4px;
  transition: all 0.2s;
  min-width: 50px;
  height: 50px; /* 添加固定高度 */
  justify-content: center; /* 垂直居中 */

}

.stat-item:hover {
  background: #f5f5f5;
}

.stat-number {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 0px;
  line-height: 1; /* 确保行高正常 */
  align-items: center;
}

.stat-label {
  font-size: 12px;
  color: #666;
  line-height: 1; /* 确保行高正常 */
  align-items: center;
}

/* 右侧：操作按钮 */
.user-right {
  display: flex;
  /*align-items: center;*/
}

.icon-buttons {
  display: flex;
  gap: 8px;
}

.icon-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  background: transparent;
  color: #666;
}
/* 调整图标大小 */
.icon-btn .el-icon {
  font-size: 20px;  /* 调整图标字体大小 */
}


.icon-btn:hover {
  background: #f5f5f5;
  color: #333;
}
</style>
