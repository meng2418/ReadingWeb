<!-- 时间选择器 -->
<template>
  <div class="stats-container">
    <!-- 时间选择器（仅周/月/年显示） -->
    <div v-if="showDateSelector" class="date-selector">
      {{ dateText }}
      <span class="arrows">
        <i class="arrow left" @click="prev">&lt;</i>
        <i class="arrow right" @click="next">&gt;</i>
      </span>
    </div>

    <!-- 统计卡片区域 -->
    <div class="stats-grid">
      <div class="card" v-for="(v, k) in stats" :key="k">
        <div class="label">{{ labels[k] }}</div>
        <div class="value">{{ v }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  period: {
    type: String,
    default: 'week', // week / month / year / total
  },
  stats: {
    type: Object,
    default: () => ({
      duration: 0,
      booksRead: 0,
      booksFinished: 0,
      notes: 0,
    }),
  },
})

const labels = {
  duration: '阅读时长',
  booksRead: '读过的书',
  booksFinished: '读完的书',
  notes: '笔记数量',
}

// -----------------------------
// 时间区间（示例：你可接入真实数据）
// -----------------------------
const currentIndex = ref(0)

// 假数据：你可改成从后端获取
const weekRanges = ['9月22日 - 9月28日', '9月15日 - 9月21日']
const monthRanges = ['2025年9月', '2025年8月']
const yearRanges = ['2025年', '2024年']

const dateText = computed(() => {
  if (props.period === 'week') return weekRanges[currentIndex.value]
  if (props.period === 'month') return monthRanges[currentIndex.value]
  if (props.period === 'year') return yearRanges[currentIndex.value]
  return ''
})

const showDateSelector = computed(() => props.period !== 'total')

const prev = () => {
  currentIndex.value = (currentIndex.value + 1) % getRange().length
}

const next = () => {
  currentIndex.value = (currentIndex.value - 1 + getRange().length) % getRange().length
}

function getRange() {
  if (props.period === 'week') return weekRanges
  if (props.period === 'month') return monthRanges
  if (props.period === 'year') return yearRanges
  return []
}
</script>

<style scoped>
.stats-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 时间选择器独占第一行 */
.date-selector {
  font-size: 14px;
  color: var(--text-light);
  display: flex;
  align-items: center;
}

.arrows .arrow {
  cursor: pointer;
  background: #eaeaea;
  border-radius: 50%;
  width: 22px;
  height: 22px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-style: normal;
  font-size: 12px;
  margin-left: 6px;
}

/* 第二行开始是卡片区 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.card {
  background: #fff;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #eee;
  display: flex;
  flex-direction: column;
}

.label {
  font-size: 14px;
  color: #666;
}
.value {
  margin-top: 6px;
  font-size: 22px;
  font-weight: 600;
}
</style>
