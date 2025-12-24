<!-- ReadingTimeCard.vue -->
<template>
  <div class="reading-time-cards">
    <div class="reading-card" @click="handleCardClick('week')">
      <p class="card-title">本周阅读</p>
      <p class="time-text">{{ weekTime }}</p>
    </div>
    <div class="reading-card" @click="handleCardClick('month')">
      <p class="card-title">本月阅读</p>
      <p class="time-text">{{ monthTime }}</p>
    </div>
    <div class="reading-card" @click="handleCardClick('total')">
      <p class="card-title">总阅读</p>
      <p class="time-text">{{ totalTime }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import router from '@/router'
import { getReadingStats } from '@/api/home'

const monthTime = ref('加载中...')
const weekTime = ref('加载中...')
const totalTime = ref('加载中...')

const formatMinutes = (minutes: number) => {
  const h = Math.floor(minutes / 60)
  const m = minutes % 60
  if (h === 0) return `${m} 分钟`
  if (m === 0) return `${h} 小时`
  return `${h} 小时 ${m} 分钟`
}

onMounted(async () => {
  try {
    const stats = await getReadingStats()
    weekTime.value = formatMinutes(stats.weekly)
    monthTime.value = formatMinutes(stats.monthly)
    totalTime.value = formatMinutes(stats.total)
  } catch (error) {
    weekTime.value = '暂无数据'
    monthTime.value = '暂无数据'
    totalTime.value = '暂无数据'
  }
})

// 处理卡片点击，跳转到个人中心（预留接口）
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
  align-items: left;
  cursor: pointer;
}

.card-title {
  font-size: 18px;
  margin-bottom: 10px;
}

.time-text {
  font-size: 24px;
  font-weight: bold;
}
</style>
