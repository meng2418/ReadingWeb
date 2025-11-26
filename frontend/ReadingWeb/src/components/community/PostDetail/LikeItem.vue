<template>
  <div class="like-item">
    <img :src="user.avatar" :alt="user.name" class="user-avatar" />
    <div class="like-info">
      <div class="user-name">{{ user.name }}</div>
      <div class="like-time">{{ timeAgo(timestamp) }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { formatDistanceToNow } from 'date-fns'
import DefaultAvatar from '@/img/avatar.jpg' // 确保路径正确

const props = defineProps<{
  user: {
    id: number
    name: string
    avatar: string
  }
  timestamp: Date | string // 可以是 Date 对象或 ISO 字符串
}>()

/**
 * 格式化时间为 "多久以前" 的形式
 * @param date - 日期对象或字符串
 */
const timeAgo = (date: Date | string): string => {
  try {
    const formattedDate = typeof date === 'string' ? new Date(date) : date
    return formatDistanceToNow(formattedDate, { addSuffix: true })
  } catch (e) {
    console.error('Error formatting date:', e)
    return '未知时间'
  }
}
</script>

<style scoped>
.like-item {
  display: flex;
  align-items: center;
  gap: 0.75rem; /* 12px */
  padding: 0.75rem 0;
  border-bottom: 1px solid #f1f5f9; /* slate-100 */
  transition: background-color 0.2s ease;
}

.like-item:hover {
  background-color: #f8fafc; /* slate-50 */
}

.user-avatar {
  width: 2.5rem; /* 40px */
  height: 2.5rem; /* 40px */
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #e2e8f0; /* slate-200 */
  flex-shrink: 0;
}

.like-info {
  flex: 1;
  overflow: hidden;
}

.user-name {
  font-weight: 500;
  color: #334155; /* slate-700 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.like-time {
  font-size: 0.75rem; /* 12px */
  color: #94a3b8; /* slate-500 */
}
</style>
