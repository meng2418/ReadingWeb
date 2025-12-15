<!-- UserList.vue -->
<template>
  <div class="user-list-container">
    <!-- 标题和统计信息 -->
    <div class="section-title-with-stats">
      <div class="section-title-left">
        <h3>{{ title }}</h3>
        <div class="title-line"></div>
      </div>
      <div class="section-title-stats">
        <div class="stat-item">
          <span class="stat-number">{{ users.length }}</span>
          <span class="stat-label">{{ statLabel }}</span>
        </div>
      </div>
    </div>

    <!-- 用户列表网格 -->
    <div class="users-grid">
      <UserCard
        v-for="user in users"
        :key="user.id"
        :user="user"
        :type="type"
        @unfollow="handleUnfollow"
        @follow="handleFollow"
      />
    </div>

    <!-- 空状态 -->
    <div v-if="users.length === 0" class="empty-state">
      <div class="empty-illustration">
        <div class="empty-icon">{{ emptyIcon }}</div>
      </div>
      <h3>{{ emptyTitle }}</h3>
      <p class="empty-hint">{{ emptyHint }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import UserCard from '@/components/userposts/UserCard.vue'

import type { FollowUser } from '@/types/user'

const props = defineProps<{ type: 'following' | 'followers'; users: FollowUser[] }>()
const emit = defineEmits<{
  update: [users: FollowUser[]]
}>()

// 根据类型计算相关文本
const title = computed(() => {
  return props.type === 'following' ? '关注列表' : '粉丝列表'
})

const statLabel = computed(() => {
  return props.type === 'following' ? '关注' : '粉丝'
})

const emptyIcon = computed(() => {
  return props.type === 'following' ? '👤' : '👥'
})

const emptyTitle = computed(() => {
  return props.type === 'following' ? '还没有关注任何人' : '还没有粉丝'
})

const emptyHint = computed(() => {
  return props.type === 'following' ? '快去发现有趣的人吧！' : '积极创作，吸引更多关注吧！'
})

// 处理取消关注
const handleUnfollow = (userId: number) => {
  const updatedUsers = props.users.filter(user => user.id !== userId)
  emit('update', updatedUsers)
  ElMessage.success('已取消关注')
}

// 处理关注
const handleFollow = (userId: number) => {
  const updatedUsers = props.users.map(user => {
    if (user.id === userId) {
      return { ...user, isFollowing: !user.isFollowing }
    }
    return user
  })
  emit('update', updatedUsers)
  ElMessage.success(updatedUsers.find(u => u.id === userId)?.isFollowing ? '关注成功' : '已取消关注')
}
</script>

<style scoped>
.user-list-container {
  background: white;
  padding: 0px;
}

/* 标题和统计信息 */
.section-title-with-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.section-title-left {
  flex: 1;
  min-width: 200px;
}

.section-title-with-stats h3 {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.section-title-with-stats .title-line {
  width: 60px;
  height: 3px;
  background: var(--primary-green);
}

.section-title-stats {
  display: flex;
  gap: 24px;
  margin-top: 0; /* 调整这个值可以控制统计项与顶部的距离 */
}

.section-title-stats .stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  min-width: 60px;
}

.section-title-stats .stat-number {
  font-size: 16px;
  font-weight: 700;
  color: var(--primary-green);
}

.section-title-stats .stat-label {
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: 500;
}

/* 用户卡片网格布局 */
.users-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border: 1px solid var(--border-color);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.empty-hint {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 24px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .users-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .section-title-with-stats {
    flex-direction: column;
    align-items: flex-start;
  }

  .section-title-stats {
    width: 100%;
    justify-content: flex-start;
    padding: 0;
  }

  .users-grid {
    grid-template-columns: repeat(1, 1fr);
  }
}
</style>
