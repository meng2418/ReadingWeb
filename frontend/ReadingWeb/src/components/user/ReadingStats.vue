<!-- 统计卡片 -->
<template>
  <div class="stats-container">
    <!-- 统计卡片区域 -->
    <div class="stats-grid">
      <div class="card" v-for="(v, k) in stats" :key="k">
        <div class="label">{{ labels[k] }}</div>
        <div class="value">
          <!-- 情况1：处理阅读时长 (分钟转换) -->
          <template v-if="k === 'duration'">
            <template v-if="Math.floor(v / 60) > 0">
              {{ Math.floor(v / 60) }}<span class="unit"> 小时 </span>
            </template>
            <template v-if="v % 60 > 0 || v === 0">
              {{ v % 60 }}<span class="unit"> 分钟 </span>
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
  booksRead: ' 本',
  booksFinished: ' 本',
  notes: ' 条',
}
</script>

<style scoped>
/* --- 保持您的原始样式不变 --- */
.stats-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.card {
  background: var(--card-bg, #fff);
  padding: 16px;
  border-radius: 12px;
  border: 1px solid var(--border-color, #eee);
  display: flex;
  flex-direction: column;
  transition: background-color 0.3s ease, border-color 0.3s ease;
}

.label {
  font-size: 14px;
  color: var(--text-light, #666);
  transition: color 0.3s ease;
}

.value {
  margin-top: 6px;
  font-size: 22px;
  font-weight: 600;
  color: var(--text-main, #333);
  transition: color 0.3s ease;
}

/* --- 新增：单位的样式，与 .label 保持一致 --- */
.unit {
  font-size: 14px; /* 与 label 一致 */
  color: var(--text-light, #666); /* 与 label 一致 */
  font-weight: normal; /* 抵消 value 的粗体 */
  margin-left: 2px; /* 稍微拉开一点间距 */
  transition: color 0.3s ease;
}
</style>
