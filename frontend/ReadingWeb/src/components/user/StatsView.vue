<template>
  <div class="stats-view">
    <!-- 图表区域 -->
    <div class="chart-wrapper">
      <Bar v-if="hasData" :data="chartData" :options="chartOptions" />
      <div v-else class="loading-placeholder">暂无数据...</div>
    </div>
    <!-- 阅读时长排行榜 -->
    <ReadingTimeRank :topBooks="topBooks" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend,
  Title,
  type ChartData,
  type ChartOptions,
} from 'chart.js'
import ReadingTimeRank from '@/components/user/ReadingTimeRank.vue'
import type { TopBook } from '@/types/user'

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title)

interface TimelineItem {
  date: string
  readingTime: number // 后端返回的分钟
}

const props = defineProps({
  period: { type: String, default: 'week' },
  timelineList: { type: Array as () => TimelineItem[], default: () => [] },
  topBooks: { type: Array as () => TopBook[], default: () => [] },
})

/**
 * 格式化 X 轴标签
 */
const formatLabel = (dateStr: string, period: string): string => {
  if (!dateStr) return ''
  try {
    if (period === 'week') {
      const days = ['日', '一', '二', '三', '四', '五', '六']
      const dayIndex = new Date(dateStr).getDay()
      return days[dayIndex] || ''
    }
    if (period === 'month') {
      return dateStr.split('-')[2] || ''
    }
    if (period === 'year') {
      const parts = dateStr.split('-')
      return parts[1] ? parseInt(parts[1]) + '月' : ''
    }
    if (period === 'total') {
      // 如果是年份格式（如 "2025"），直接返回
      if (/^\d{4}$/.test(dateStr)) {
        return dateStr
      }
      // 如果是日期格式（如 "2025-01-01"），提取年份
      const year = dateStr.split('-')[0]
      return year || dateStr
    }
  } catch (e) {
    return dateStr
  }
  return dateStr
}

const xAxisUnit = computed(() => {
  const unitMap: Record<string, string> = {
    week: '周',
    month: '日',
    year: '月',
    total: '年',
  }
  return unitMap[props.period] || ''
})

/**
 * 1. 数据处理：将分钟转换为小时
 */
const chartData = computed<ChartData<'bar'>>(() => ({
  labels: props.timelineList.map((item) => formatLabel(item.date || '', props.period)),
  datasets: [
    {
      label: '阅读时长',
      // 修改点：除以 60，并保留一位小数
      data: props.timelineList.map((item) => Number((item.readingTime / 60).toFixed(1))),
      borderRadius: 6,
      backgroundColor: '#53b480cc',
      hoverBackgroundColor: '#53b480',
      barThickness: props.period === 'month' ? 8 : ('flex' as const),
    },
  ],
}))

const hasData = computed(() => {
  return chartData.value.datasets[0]?.data && chartData.value.datasets[0].data.length > 0
})

/**
 * 2. 配置项修改：单位与提示语
 */
const chartOptions = computed<ChartOptions<'bar'>>(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: false },
    tooltip: {
      backgroundColor: '#333',
      callbacks: {
        // 修改点：优化 Tooltip，显示“1.5 小时 (90 分钟)”
        label: (context) => {
          const hours = context.raw as number
          const mins = Math.round(hours * 60)
          return ` ${hours} 小时 (${mins} 分钟)`
        },
      },
    },
  },
  scales: {
    x: {
      grid: { display: false },
      ticks: {
        color: '#999',
        font: { size: 12 },
        autoSkip: props.period === 'month',
      },
      title: {
        display: true,
        text: xAxisUnit.value,
        align: 'end',
        color: '#bbb',
        font: { size: 11 },
      },
    },
    y: {
      beginAtZero: true,
      grid: { color: '#f0f0f0' },
      ticks: {
        color: '#999',
        font: { size: 11 },
      },
      title: {
        display: true,
        // 修改点：单位改为小时
        text: '时',
        align: 'end',
        color: '#bbb',
        font: { size: 11 },
      },
    },
  },
}))
</script>

<style scoped>
.stats-view {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

.chart-wrapper {
  height: 340px;
  padding: 20px;
  position: relative;
}

.loading-placeholder {
  display: flex;
  height: 100%;
  align-items: center;
  justify-content: center;
  color: #999;
}

@media (max-width: 768px) {
  .stats-view {
    grid-template-columns: 1fr;
  }
}
</style>
