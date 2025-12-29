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
        <div class="value">
          <!-- 情况1：处理阅读时长 (分钟转换) -->
          <template v-if="k === 'duration'">
            <template v-if="Math.floor(v / 60) > 0">
              {{ Math.floor(v / 60) }}<span class="unit">小时</span>
            </template>
            <template v-if="v % 60 > 0 || v === 0">
              {{ v % 60 }}<span class="unit">分钟</span>
            </template>
          </template>

          <!-- 情况2：处理其他带单位的数值 -->
          <template v-else>
            {{ v }}<span class="unit">{{ units[k] }} </span>
          </template>
        </div>
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

// 新增：定义对应字段的单位
const units = {
  booksRead: '本',
  booksFinished: '本',
  notes: '条',
}

// -----------------------------
// 时间区间逻辑
// -----------------------------
const currentIndex = ref(0)
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
/* --- 保持您的原始样式不变 --- */
.stats-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.date-selector {
  font-size: 14px;
  color: var(--text-light); /* 保持变量使用 */
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

/* --- 新增：单位的样式，与 .label 保持一致 --- */
.unit {
  font-size: 14px; /* 与 label 一致 */
  color: #666; /* 与 label 一致 */
  font-weight: normal; /* 抵消 value 的粗体 */
  margin-left: 2px; /* 稍微拉开一点间距 */
}
</style>
