<template>
  <div class="sidebar-card">
    <h3 class="title">阅读激励</h3>

    <!-- 今日阅读任务 -->
    <div class="section-title">今日阅读任务</div>
    <div v-for="task in dailyTasks" :key="task.minutes" class="task-block">
      <div class="task-info">
        <div class="task-name">阅读满 {{ formatMinutes(task.minutes) }}</div>

        <div class="task-progress">
          <div class="progress-bar">
            <div class="progress-inner" :style="{ width: getDailyProgress(task) + '%' }"></div>
          </div>
        </div>
      </div>

      <button
        class="reward-btn"
        :disabled="todayRead < task.minutes || task.claimed"
        @click="claimDaily(task)"
      >
        <template v-if="task.claimed">已领取</template>
        <template v-else-if="todayRead >= task.minutes">领取</template>
        <template v-else>未达成</template>
      </button>
    </div>

    <!-- 连续阅读任务 -->
    <div class="section-title">连续阅读任务</div>
    <div v-for="task in streakTasks" :key="task.days" class="task-block">
      <div class="task-info">
        <div class="task-name">连续阅读 {{ task.days }} 天</div>
        <div class="task-streak">已连续 {{ streak }} 天</div>
      </div>

      <button
        class="reward-btn"
        :disabled="streak < task.days || task.claimed"
        @click="claimStreak(task)"
      >
        <template v-if="task.claimed">已领取</template>
        <template v-else-if="streak >= task.days">领取</template>
        <template v-else>未达成</template>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      todayRead: 46, // 用户今日阅读分钟数（示例）
      streak: 3, // 连续阅读天数（示例）

      dailyTasks: [
        { minutes: 5, claimed: false },
        { minutes: 30, claimed: false },
        { minutes: 60, claimed: false },
        { minutes: 180, claimed: false },
        { minutes: 300, claimed: false },
      ],

      streakTasks: [
        { days: 2, claimed: false },
        { days: 4, claimed: false },
        { days: 7, claimed: false },
      ],
    }
  },

  methods: {
    formatMinutes(m) {
      if (m < 60) return `${m} 分钟`
      const h = m / 60
      return `${h} 小时`
    },

    getDailyProgress(task) {
      const val = Math.min((this.todayRead / task.minutes) * 100, 100)
      return val.toFixed(2)
    },

    claimDaily(task) {
      if (this.todayRead >= task.minutes) task.claimed = true
    },

    claimStreak(task) {
      if (this.streak >= task.days) task.claimed = true
    },
  },
}
</script>

<style scoped>
.sidebar-card {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 20px;
  box-shadow: var(--shadow);
}

.title {
  font-size: 16px;
  border-bottom: 1px solid #eee;
  padding-bottom: 12px;
}

.section-title {
  margin-top: 20px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.task-block {
  margin-top: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-info {
  flex: 1;
  margin-right: 12px;
  margin-bottom: 8px;
}

.task-name {
  font-size: 14px;
  margin-bottom: 6px;
  font-weight: 500;
}

.task-progress {
  display: flex;
  align-items: center;
}

.progress-bar {
  width: 120px;
  height: 6px;
  background: #eee;
  border-radius: 4px;
  margin-right: 8px;
  overflow: hidden;
}

.progress-inner {
  height: 100%;
  background: var(--secondary-green);
}

.task-streak {
  font-size: 13px;
  color: #666;
}

.reward-btn {
  background: var(--third-green);
  color: #fff;
  border: none;
  padding: 6px 14px;
  border-radius: 14px;
  cursor: pointer;
  font-size: 13px;
}

.reward-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>
