<template>
  <div class="reading-time-cards">
    <div class="reading-card" @click="handleCardClick('today')">
      <p class="card-title">今日阅读</p>
      <p class="time-text">{{ todayTime }}</p>
    </div>
    <div class="reading-card" @click="handleCardClick('week')">
      <p class="card-title">本周阅读</p>
      <p class="time-text">{{ weekTime }}</p>
    </div>
    <div class="reading-card" @click="handleCardClick('total')">
      <p class="card-title">总阅读</p>
      <p class="time-text">{{ totalTime }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

// 模拟从数据库获取的阅读时长数据，实际开发中替换为从数据库查询的逻辑
const todayTime = ref('XX小时XX分钟')
const weekTime = ref('XX小时XX分钟')
const totalTime = ref('XX小时XX分钟')

// 从数据库获取阅读时长的方法（示例，实际需根据后端接口调整）
const fetchReadingTimeFromDB = async (type) => {
  // 这里是从数据库获取数据的逻辑，例如调用后端 API
  // 示例：const response = await fetch(`/api/reading-time/${type}`);
  // const data = await response.json();
  // return data.time;
  // 目前先返回模拟数据
  return 'XX小时XX分钟'
}

// 初始化时从数据库获取数据
onMounted(async () => {
  todayTime.value = await fetchReadingTimeFromDB('today')
  weekTime.value = await fetchReadingTimeFromDB('week')
  totalTime.value = await fetchReadingTimeFromDB('total')
})

// 处理卡片点击，跳转到个人中心（预留接口）
const handleCardClick = (type:string) => {
  // 这里是跳转到个人中心的逻辑，例如使用路由跳转
  // 示例：router.push({ path: '/personal-center', query: { type } });
  console.log(`准备跳转到个人中心，卡片类型：${type}`)
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
