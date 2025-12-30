<!-- ReadingTimeCard.vue -->
<template>
  <div class="reading-time-cards">
    <!-- 本周阅读 -->
    <div class="reading-card" @click="handleCardClick('week')">
      <p class="card-title">本周阅读</p>
      <div class="time-text">
        <template v-if="stats.weekly !== null">
          <span v-if="Math.floor(stats.weekly / 60) > 0">
            {{ Math.floor(stats.weekly / 60) }}<span class="unit">小时</span>
          </span>
          <span v-if="stats.weekly % 60 > 0 || stats.weekly === 0">
            {{ stats.weekly % 60 }}<span class="unit">分钟</span>
          </span>
        </template>
        <span v-else class="loading">加载中...</span>
      </div>
    </div>

    <!-- 本月阅读 -->
    <div class="reading-card" @click="handleCardClick('month')">
      <p class="card-title">本月阅读</p>
      <div class="time-text">
        <template v-if="stats.monthly !== null">
          <span v-if="Math.floor(stats.monthly / 60) > 0">
            {{ Math.floor(stats.monthly / 60) }}<span class="unit">小时</span>
          </span>
          <span v-if="stats.monthly % 60 > 0 || stats.monthly === 0">
            {{ stats.monthly % 60 }}<span class="unit">分钟</span>
          </span>
        </template>
        <span v-else class="loading">加载中...</span>
      </div>
    </div>

    <!-- 总阅读 -->
    <div class="reading-card" @click="handleCardClick('total')">
      <p class="card-title">总阅读</p>
      <div class="time-text">
        <template v-if="stats.total !== null">
          <span v-if="Math.floor(stats.total / 60) > 0">
            {{ Math.floor(stats.total / 60) }}<span class="unit">小时</span>
          </span>
          <span v-if="stats.total % 60 > 0 || stats.total === 0">
            {{ stats.total % 60 }}<span class="unit">分钟</span>
          </span>
        </template>
        <span v-else class="loading">加载中...</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getReadingStats } from '@/api/home'

const router = useRouter()

// 使用对象存储原始数值，方便在模板中进行数学计算
const stats = reactive({
  weekly: null as number | null,
  monthly: null as number | null,
  total: null as number | null,
})

onMounted(async () => {
  try {
    const data = await getReadingStats()
    stats.weekly = data.weekly
    stats.monthly = data.monthly
    stats.total = data.total
  } catch (error) {
    // 失败处理：设为0以防显示错误
    stats.weekly = 0
    stats.monthly = 0
    stats.total = 0
  }
})

const handleCardClick = (type: string) => {
  router.push({ path: '/profile', query: { tab: type } })
}
</script>

<style scoped>
.reading-time-cards {
  display: flex;
  gap: 20px;
}

.reading-card {
  width: 160px;
  height: 160px;
  padding: 20px;
  border: 1px solid #ccc;
  background-color: white;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start; /* 改为 flex-start 配合左对齐 */
  cursor: pointer;
  transition: transform 0.2s;
}

.card-title {
  font-size: 18px;
  margin-bottom: 10px;
  color: #333;
}

.time-text {
  font-size: 24px;
  font-weight: bold;
  color: #000;
}

/* --- 新增：单位样式，与数字区分开 --- */
.unit {
  font-size: 14px; /* 单位字体变小 */
  font-weight: normal; /* 取消粗体 */
  color: #666; /* 颜色变淡，类似 label */
  margin-left: 2px; /* 离数字稍微远点 */
  margin-right: 4px; /* 小时和分钟之间拉开距离 */
}

.loading {
  font-size: 16px;
  font-weight: normal;
  color: #999;
}
</style>
