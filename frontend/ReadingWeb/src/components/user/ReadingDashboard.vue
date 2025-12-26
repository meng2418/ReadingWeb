<!-- ReadingDashboard.vue -->
<script setup lang="ts">
import { ref, watch, computed, onMounted } from 'vue'
import HistoryView from '@/components/user/HistoryView.vue'
import ReadingStats from '@/components/user/ReadingStats.vue'
import StatsView from '@/components/user/StatsView.vue'
import { getTopBooks } from '@/api/profile'
import type { TopBook } from '@/types/user'

const props = defineProps({
  initialTab: {
    type: String,
    default: 'week',
  },
  readingStats: {
    type: Object,
    default: null,
  },
  historyRecords: {
    type: Array,
    default: () => [],
  },
  topBooks: {
    type: Array as () => TopBook[],
    default: () => [],
  },
})

const emit = defineEmits<{
  (e: 'update:topBooks', books: TopBook[]): void
}>()

const currentTab = ref(props.initialTab)
const localTopBooks = ref<TopBook[]>(props.topBooks) // 添加本地状态

const tabs = [
  { label: '周', key: 'week' },
  { label: '月', key: 'month' },
  { label: '年', key: 'year' },
  { label: '总', key: 'total' },
  { label: '阅历', key: 'history' },
]

// 监听父组件传递的 topBooks 变化
watch(
  () => props.topBooks,
  (newBooks) => {
    localTopBooks.value = newBooks
  },
)

// 监听 tab 变化 → 重新获取数据
watch(currentTab, async (tab) => {
  if (tab === 'history') return
  try {
    const books = await getTopBooks(tab as any)
    localTopBooks.value = books
    emit('update:topBooks', books) // 通知父组件更新
  } catch (error) {
    console.error('获取榜单数据失败:', error)
  }
})

const statsForPeriod = computed(() => {
  const rs: any = props.readingStats || {}
  const map = {
    week: {
      duration: rs.weeklyReadingTime ?? 0,
      booksRead: rs.weeklyBooksRead ?? 0,
      booksFinished: rs.weeklyBooksFinished ?? 0,
      notes: rs.weeklyNoteCount ?? 0,
    },
    month: {
      duration: rs.monthlyReadingTime ?? 0,
      booksRead: rs.monthlyBooksRead ?? 0,
      booksFinished: rs.monthlyBooksFinished ?? 0,
      notes: rs.monthlyNoteCount ?? 0,
    },
    year: {
      duration: rs.yearlyReadingTime ?? 0,
      booksRead: rs.yearlyBooksRead ?? 0,
      booksFinished: rs.yearlyBooksFinished ?? 0,
      notes: rs.yearlyNoteCount ?? 0,
    },
    total: {
      duration: rs.totalReadingTime ?? 0,
      booksRead: rs.totalBooksRead ?? 0,
      booksFinished: rs.totalBooksFinished ?? 0,
      notes: rs.totalNoteCount ?? 0,
    },
  } as const
  return map[currentTab.value as keyof typeof map] ?? map.week
})
</script>

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
          <ReadingStats :period="currentTab" :stats="statsForPeriod" />
          <StatsView :period="currentTab" :topBooks="localTopBooks" />
        </div>
      </transition>
    </div>
  </div>
</template>

<style scoped>
.dashboard-card {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 20px;
  min-height: 600px;
  box-shadow: var(--shadow, 0 4px 12px rgba(0, 0, 0, 0.05));
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
