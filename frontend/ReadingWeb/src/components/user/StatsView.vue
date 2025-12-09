<!--阅读统计视图，包含图表和阅读时长排行榜-->
<template>
  <div class="stats-view">
    <!-- 图表 -->
    <div class="chart-wrapper">
      <Bar :data="chartData" :options="chartOptions" />
    </div>
    <!-- 阅读时长排行榜 -->
    <ReadingTimeRank />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Bar } from 'vue-chartjs'
import { Chart as ChartJS, BarElement, CategoryScale, LinearScale, Tooltip } from 'chart.js'
import ReadingTimeRank from '@/components/user/ReadingTimeRank.vue'

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip)

const props = defineProps({
  period: { type: String, default: 'week' }, // week | month | year | total
})

/* ----------------------------
   1. 横轴标签根据 period 动态切换
----------------------------- */
const labelsMap = {
  week: ['一', '二', '三', '四', '五', '六', '日'],
  month: ['1', '5', '10', '15', '20', '25', '30'],
  year: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
  total: ['2019', '2020', '2021', '2022', '2023'],
}

// 测试数据（你之后可以替换）
const fakeDataMap = {
  week: [30, 50, 90, 40, 60, 120, 10],
  month: [10, 30, 55, 80, 70, 60, 40],
  year: [80, 60, 40, 100, 120, 90, 50, 30, 10, 40, 70, 110],
  total: [200, 350, 120, 500, 430],
}

const chartData = computed(() => ({
  labels: labelsMap[props.period],
  datasets: [
    {
      data: fakeDataMap[props.period],
      borderRadius: 6,
      backgroundColor: 'rgba(83, 180, 128, 0.8)',
    },
  ],
}))

/* ----------------------------
   2. 图表样式：极简、竖柱状图
----------------------------- */
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    tooltip: { enabled: true },
    legend: { display: false },
  },
  scales: {
    x: {
      grid: { display: false },
      ticks: { color: '#777' },
    },
    y: {
      grid: { color: '#eee' },
      ticks: { color: '#777' },
    },
  },
}
</script>

<style scoped>
.stats-view {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

/* 图表区域 */
.chart-wrapper {
  height: 340px;
  background: var(--card-bg);
  border-radius: 16px;
  padding: 20px;
  box-shadow: var(--shadow);
}
</style>
