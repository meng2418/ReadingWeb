<!-- ReadingDashboard.vue -->
<template>
  <div class="dashboard-card">
    <div class="nav-tabs">
      <div
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-item"
        :class="{ active: currentTab === tab.key }"
        @click="currentTab = tab.key"
      >
        {{ tab.label }}
      </div>
    </div>

    <div class="tab-content">
      <transition name="fade" mode="out-in">
        <!-- 阅历历史 -->
        <HistoryView :records="historyRecords" v-if="currentTab === 'history'" />

        <!-- 周/月/年/总 -> 显示统计数字和柱状图 -->
        <div v-else>
          <ReadingStats :period="currentTab" />
          <StatsView :period="currentTab" />
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import HistoryView from '@/components/user/HistoryView.vue'
import ReadingStats from '@/components/user/ReadingStats.vue'
import StatsView from '@/components/user/StatsView.vue'

const props = defineProps({
  initialTab: {
    type: String,
    default: 'week',
  },
})

const currentTab = ref(props.initialTab)

const tabs = [
  { label: '周', key: 'week' },
  { label: '月', key: 'month' },
  { label: '年', key: 'year' },
  { label: '总', key: 'total' },
  { label: '阅历', key: 'history' }, // 这个Tab对应图1
]
const historyRecords = ref([
  {
    date: '2024-09-25',
    type: 'reading', // reading | note | finishBook ...
    statLabel: '累计阅读',
    statValue: 60,
    statUnit: '本书',
    text: '那时，你正在阅读《呼啸山庄》。路一步一步向前走，书一页一页往后读。',
  },
  {
    date: '2024-04-28',
    type: 'note',
    statLabel: '发表笔记',
    statValue: 350,
    statUnit: '条',
    source: '在《当尼采哭泣》里，你留下划线：',
    quote:
      '尼采略略地笑着，“我知道她如何在这点上反应。她对传统婚姻显得并不宽容，她认为它是女性卖身契的一种委婉说法。”“就是她跟我说的话！”',
  },
])

// 允许用户在个人中心内切换 tab，同时当路由变化时也跟着变
watch(
  () => props.initialTab,
  (newTab) => {
    if (newTab) currentTab.value = newTab
  },
)
</script>

<style scoped>
.dashboard-card {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 20px;
  min-height: 600px;
  box-shadow: var(--shadow);
}

.nav-tabs {
  display: flex;
  gap: 40px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 24px;
  padding: 0 10px;
}

.tab-item {
  padding: 12px 0;
  cursor: pointer;
  font-weight: 500;
  color: var(--text-light);
  position: relative;
  transition: color 0.3s;
}

.tab-item.active {
  color: var(--text-main);
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  background-color: var(--primary-green);
  border-radius: 2px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
