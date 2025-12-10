<template>
  <div class="user-profile-card">
    <!-- 顶部用户信息 -->
    <div class="profile-header">
      <div class="user-info">
        <div class="avatar">
          <img :src="user.avatar" alt="用户头像" />
        </div>
        <div class="text-info">
          <h1 class="nickname">{{ user.nickname }}</h1>
          <p class="signature">
            {{ user.signature }}
          </p>
        </div>
      </div>

      <div class="stats-row">
        <!-- 修改关注为可点击按钮 -->
        <button class="stat-item stat-btn" @click="goToUserPosts('following')">
          <span class="num">{{ user.stats.following }}</span>
          <span class="label">关注</span>
        </button>
        <!-- 修改粉丝为可点击按钮 -->
        <button class="stat-item stat-btn" @click="goToUserPosts('followers')">
          <span class="num">{{ user.stats.followers }}</span>
          <span class="label">粉丝</span>
        </button>
        <!-- 修改发布为可点击按钮 -->
        <button class="stat-item stat-btn" @click="goToUserPosts('posts')">
          <span class="num">{{ user.stats.posts }}</span>
          <span class="label">发布</span>
        </button>

        <!-- 操作按钮 -->
        <div class="actions">
          <button class="icon-btn">
            <Edit2 />
            <span>编辑</span>
          </button>
          <button class="icon-btn">
            <Palette />
            <span>外观</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 底部卡片操作 -->
    <div class="card-actions">
      <div class="balance-box">
        <span>体验卡</span>
        <span class="balance-num">{{ user.giftVIP }} 天</span>
      </div>
      <div class="coin-box" @click="openRechargeDialog">
        <span>充值币</span>
        <span class="coin-num">{{ user.payCoin }}</span>
      </div>
      <!-- 会员按钮：保持样式不变，只修改文字 -->
      <button class="vip-btn" @click="openVipDialog">
        {{ getVipButtonText() }}
      </button>
    </div>
  </div>

  <!-- 充值弹窗 -->
  <RechargeDialog ref="rechargeDialogRef" @recharge-success="handleRechargeSuccess" />

  <!-- 会员弹窗 -->
  <VipDialog ref="vipDialogRef" @purchase-success="handlePurchaseSuccess" />
</template>

<script setup lang="ts">
import { Edit2, Palette } from 'lucide-vue-next'
import { ref, computed } from 'vue'
import DefaultAvatar from '@/img/avatar.jpg'
import RechargeDialog from '@/components/user/RechargeDialog.vue'
import VipDialog from '@/components/user/VipDialog.vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

// 添加路由实例
const router = useRouter()

// 个人信息数据
const user = ref({
  nickname: '幼稚园战神',
  signature:
    'B站有机生物，人工制造，含水分，碳包碳，小学毕业证，初中毕业证，高中毕业证，卒業証明書，会吃饭睡觉，不主持仪式不喜欢小男孩儿的神父（凑字数）',
  avatar: DefaultAvatar,
  stats: {
    following: 10,
    followers: 1000,
    posts: 5,
  },
  payCoin: 180,
  giftVIP: 12,
  isVip: false, // 是否会员
  vipDays: 0,   // 会员剩余天数
  vipEndTime: null as string | null, // 会员到期时间
})


// 跳转到UserPosts页面的对应标签页
const goToUserPosts = (tab: string) => {
  // 跳转到UserPosts页面，并传递tab参数
  router.push({
    path: '/userposts',
    query: { tab }
  })
}

// 充值弹窗
const rechargeDialogRef = ref()
const openRechargeDialog = () => {
  rechargeDialogRef.value.open(user.value.payCoin)
}

const handleRechargeSuccess = (option: any) => {
  console.log('充值成功:', option)
  // 更新用户的书币数量
  user.value.payCoin += option.amount + (option.bonus || 0)
  ElMessage.success(`成功充值${option.amount}书币${option.bonus ? `，赠送${option.bonus}书币` : ''}`)
}

// 会员弹窗
const vipDialogRef = ref()
const openVipDialog = () => {
  vipDialogRef.value.open()
}

const handlePurchaseSuccess = (plan: any) => {
  console.log('会员购买成功:', plan)

  // 计算要添加的天数
  let daysToAdd = 0
  if (plan.duration.includes('7') || plan.name.includes('周')) {
    daysToAdd = 7
  } else if (plan.duration.includes('30') || plan.name.includes('月')) {
    daysToAdd = 30
  } else if (plan.duration.includes('90') || plan.name.includes('季')) {
    daysToAdd = 90
  } else if (plan.duration.includes('365') || plan.name.includes('年')) {
    daysToAdd = 365
  } else {
    // 尝试从duration中提取数字
    const match = plan.duration.match(/\d+/)
    daysToAdd = match ? parseInt(match[0]) : 30
  }

  // 更新会员状态
  user.value.isVip = true

  // 计算新的会员到期时间
  const now = new Date()
  if (user.value.vipEndTime) {
    // 如果已有会员，则在现有到期时间上累加
    const endTime = new Date(user.value.vipEndTime)
    endTime.setDate(endTime.getDate() + daysToAdd)
    user.value.vipEndTime = endTime.toISOString()
    user.value.vipDays = Math.ceil((endTime.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
  } else {
    // 如果没有会员，从今天开始计算
    now.setDate(now.getDate() + daysToAdd)
    user.value.vipEndTime = now.toISOString()
    user.value.vipDays = daysToAdd
  }

  ElMessage.success(`成功开通${plan.name}，有效期${plan.duration}`)
}

// 计算会员按钮显示的文字
const getVipButtonText = () => {
  if (user.value.isVip && user.value.vipDays > 0) {
    return `VIP剩余${user.value.vipDays}天`
  }
  return '成为会员'
}
</script>

<style scoped>
.user-profile-card {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--shadow, 0 4px 12px rgba(0, 0, 0, 0.05));
}

/* 顶部用户信息 */
.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px; /* 增加元素之间的间距，防止靠得太近 */
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
  /* 关键改动：让用户信息部分占据尽可能多的空间，但不挤压右侧 */
  flex: 1;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 3px solid #f0f0f0;
  background: var(--bg-gray);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.text-info {
  display: flex;
  flex-direction: column;
  /* 关键改动：限制文本区域的最大宽度 */
  max-width: 650px;
}

.nickname {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: var(--text-main);
}

.signature {
  margin: 4px 0 0 0;
  color: var(--text-light);
  font-size: 14px;
  /* 关键改动：处理文字溢出 */
  overflow: hidden; /* 隐藏超出容器的部分 */
  text-overflow: ellipsis; /* 用省略号代替被隐藏的部分 */
}

/* 数据统计行 */
.stats-row {
  display: flex;
  gap: 24px;
  align-items: center;
  /* 关键改动：防止统计行被压缩得过小 */
  flex-shrink: 0;
}

/* 新增：统计按钮样式 */
.stat-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  transition: transform 0.2s ease, opacity 0.2s ease;
}

.stat-btn:hover {
  transform: translateY(-2px);
  opacity: 0.9;
}

.stat-btn:active {
  transform: translateY(0);
}

.stat-btn .num {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-main);
  transition: color 0.2s ease;
}

.stat-btn:hover .num {
  color: var(--primary-green);
}

.stat-btn .label {
  font-size: 12px;
  color: var(--text-light);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-item .num {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-main);
}

.stat-item .label {
  font-size: 12px;
  color: var(--text-light);
}

/* 按钮操作 */
.actions {
  display: flex;
  gap: 12px;
}

.icon-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: var(--bg-gray);
  border: none;
  border-radius: 8px;
  padding: 6px 12px;
  cursor: pointer;
  font-size: 14px;
  color: var(--text-main);
  transition: all 0.3s;
}

.icon-btn:hover {
  background: var(--secondary-green);
  color: #fff;
}

/* 底部卡片操作 */
.card-actions {
  margin-top: 24px;
  display: flex;
  gap: 16px;
}

.balance-box {
  border: 1px solid #eee;
  padding: 10px 24px;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 12px;
  color: var(--text-light);
  min-width: 80px;
  background: #fafafa;
}

.balance-num {
  font-size: 16px;
  font-weight: bold;
  color: var(--text-main);
  margin-top: 4px;
}

.coin-box {
  background: var(--primary-green);
  border: none;
  color: #fff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  padding: 10px 24px;
  cursor: pointer;
  transition: all 0.3s;
  min-width: 80px;
  align-items: center;
  font-size: 12px;
}

.coin-box:hover {
  background: var(--third-green);
}

.coin-num {
  font-size: 16px;
  font-weight: bold;
  margin-top: 4px;
}

.vip-btn {
  background: var(--primary-green);
  border: none;
  color: #fff;
  padding: 0 32px;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s;
}

.vip-btn:hover {
  background: var(--third-green);
}


</style>
