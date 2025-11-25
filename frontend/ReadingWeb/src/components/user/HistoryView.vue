<template>
  <div class="history-view">
    <div class="timeline-list">
      <div class="timeline-card" v-for="(item, i) in records" :key="i">
        <!-- 日期模块 -->
        <div class="date-col">
          <div class="day">{{ formatDay(item.date) }}</div>
          <div class="month">{{ formatMonthEn(item.date) }}</div>
          <div class="weekday">{{ formatFullDate(item.date) }}</div>
        </div>

        <!-- 数据模块 -->
        <div class="stat-col border-right">
          <div class="label">{{ item.statLabel }}</div>
          <div class="val">
            {{ item.statValue }}
            <span class="unit">{{ item.statUnit }}</span>
          </div>
        </div>

        <!-- 内容模块（纯文本 or 引用） -->
        <div class="content-col">
          <!-- 普通记录 -->
          <p v-if="item.text" class="excerpt">{{ item.text }}</p>

          <!-- 有引用的记录 -->
          <div v-if="item.quote">
            <div class="quote-source">{{ item.source }}</div>
            <p class="quote">{{ item.quote }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

// 父组件传入记录数组
const props = defineProps({
  records: {
    type: Array,
    default: () => [],
  },
})

// ===== 日期工具函数 =====
const formatDay = (dateStr) => new Date(dateStr).getDate()

const monthMap = [
  'JANUARY',
  'FEBRUARY',
  'MARCH',
  'APRIL',
  'MAY',
  'JUNE',
  'JULY',
  'AUGUST',
  'SEPTEMBER',
  'OCTOBER',
  'NOVEMBER',
  'DECEMBER',
]

const formatMonthEn = (dateStr) => {
  const m = new Date(dateStr).getMonth()
  return monthMap[m]
}

const formatFullDate = (dateStr) => {
  const date = new Date(dateStr)
  const m = date.getMonth() + 1
  const d = date.getDate()
  const weekday = ['日', '一', '二', '三', '四', '五', '六'][date.getDay()]
  return `${m}月${d}日 · 星期${weekday}`
}
</script>

<style scoped>
.timeline-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.timeline-card {
  border: 1px solid #eee;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: stretch;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}
.timeline-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  border-color: var(--secondary-green);
}
.date-col {
  width: 140px;
  flex-shrink: 0;
}
.date-col .day {
  font-size: 48px;
  font-weight: 800;
  line-height: 1;
  color: #333;
}
.date-col .month {
  font-size: 14px;
  font-weight: bold;
  text-transform: uppercase;
  color: #333;
  margin-bottom: 4px;
}
.date-col .weekday {
  font-size: 12px;
  color: var(--text-light);
}
.stat-col {
  width: 120px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding-left: 20px;
}
.stat-col.border-right {
  border-left: 2px solid #f0f0f0;
  margin-right: 20px;
}
.stat-col .label {
  font-size: 12px;
  color: var(--text-light);
  margin-bottom: 4px;
}
.stat-col .val {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}
.stat-col .unit {
  font-size: 12px;
  font-weight: normal;
}
.content-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #555;
  font-size: 14px;
  line-height: 1.6;
}
.quote-source {
  color: var(--text-light);
  margin-bottom: 5px;
}
.quote {
  font-style: italic;
  background: #f9f9f9;
  padding: 10px;
  border-radius: 4px;
  border-left: 3px solid var(--secondary-green);
}
</style>
