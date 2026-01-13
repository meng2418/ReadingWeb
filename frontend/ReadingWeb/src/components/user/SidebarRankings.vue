<!--SidebarRankings.vue -->
<template>
  <div class="sidebar-card">
    <h3 class="title">阅读激励</h3>

    <!-- 今日阅读任务 -->
    <div class="section-title">今日阅读任务</div>
    <div v-for="task in dailyTasks" :key="task.minutes" class="task-block">
      <div class="task-info">
        <div class="task-name">阅读满 {{ formatMinutes(task.minutes) }}</div>

        <div class="task-progress">
          <div class="progress-bar">
            <div class="progress-inner" :style="{ width: getDailyProgress(task) + '%' }"></div>
          </div>
        </div>
      </div>

      <button
        class="reward-btn"
        :disabled="todayRead < task.minutes || task.claimed"
        @click="claimDaily(task)"
      >
        <template v-if="task.claimed">已领取</template>
        <template v-else-if="todayRead >= task.minutes">领取</template>
        <template v-else>未达成</template>
      </button>
    </div>

    <!-- 连续阅读任务 -->
    <div class="section-title">连续阅读任务</div>
    <div v-for="task in streakTasks" :key="task.days" class="task-block">
      <div class="task-info">
        <div class="task-name">连续阅读 {{ task.days }} 天</div>
        <div class="task-streak">已连续 {{ streak }} 天</div>
      </div>

      <button
        class="reward-btn"
        :disabled="streak < task.days || task.claimed"
        @click="claimStreak(task)"
      >
        <template v-if="task.claimed">已领取</template>
        <template v-else-if="streak >= task.days">领取</template>
        <template v-else>未达成</template>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user' // 引入Pinia状态管理store
import { ElMessage } from 'element-plus' // 假设用Element Plus的提示组件（可选）
import { postReadingReward, getTodayReadingTime } from '@/api/rewards'
import { getProfileHome } from '@/api/profile'

// 定义emit事件，用于通知父组件更新用户信息
const emit = defineEmits(['user-data-updated'])

// 1. 响应式数据定义
const todayRead = ref(0) // 用户今日阅读分钟数
const dailyTasks = ref([
  { minutes: 5, claimed: false },
  { minutes: 30, claimed: false },
  { minutes: 60, claimed: false },
  { minutes: 180, claimed: false },
  { minutes: 300, claimed: false },
])
const streakTasks = ref([
  { days: 2, claimed: false },
  { days: 4, claimed: false },
  { days: 7, claimed: false },
])

// 2. Pinia store（用于组件间共享giftVIP数据）
const userStore = useUserStore()
onMounted(async () => {
  userStore.fetchUserHome()
  const res = await getTodayReadingTime()
  todayRead.value = res.data.data.readingTime ?? 0
})
const streak = computed(() => userStore.consecutiveReadingDays)
// 3. 工具方法
const formatMinutes = (m) => {
  if (m < 60) return `${m} 分钟`
  const h = m / 60
  return `${h} 小时`
}

const getDailyProgress = (task) => {
  const val = Math.min((todayRead.value / task.minutes) * 100, 100)
  return val.toFixed(2)
}

// 4. 领取奖励的核心逻辑
// 根据接口文档，每次领取都是2天体验卡，不需要传type和value参数
const claimReward = async (task) => {
  try {
    // 调用API领取奖励（不需要参数）
    await postReadingReward()

    // 标记已领取
    task.claimed = true

    // 刷新用户信息：更新store和通知父组件
    await userStore.fetchUserHome()
    
    // 获取最新的用户主页数据，包括体验卡天数、充值币、会员信息等
    const homeData = await getProfileHome()
    
    // 触发事件，通知父组件更新用户数据
    emit('user-data-updated', homeData)

    ElMessage.success('领取成功！获得2天体验卡')
  } catch (e) {
    ElMessage.error(e.message || '领取失败')
  }
}

const claimDaily = (task) => claimReward(task)
const claimStreak = (task) => claimReward(task)
</script>

<style scoped>
.sidebar-card {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 20px;
  box-shadow: var(--shadow, 0 4px 12px rgba(0, 0, 0, 0.05));
}

.title {
  font-size: 16px;
  border-bottom: 1px solid #eee;
  padding-bottom: 12px;
}

.section-title {
  margin-top: 20px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.task-block {
  margin-top: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-info {
  flex: 1;
  margin-right: 12px;
  margin-bottom: 8px;
}

.task-name {
  font-size: 14px;
  margin-bottom: 6px;
  font-weight: 500;
}

.task-progress {
  display: flex;
  align-items: center;
}

.progress-bar {
  width: 120px;
  height: 6px;
  background: #eee;
  border-radius: 4px;
  margin-right: 8px;
  overflow: hidden;
}

.progress-inner {
  height: 100%;
  background: var(--secondary-green);
}

.task-streak {
  font-size: 13px;
  color: #666;
}

.reward-btn {
  background: var(--third-green);
  color: #fff;
  border: none;
  padding: 6px 14px;
  border-radius: 14px;
  cursor: pointer;
  font-size: 13px;
}

.reward-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>
