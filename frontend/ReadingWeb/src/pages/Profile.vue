<template>
  <UserCenterCard :user="testUser1" />
  <div class="personal-center"></div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import UserCenterCard from '@/components/user/UserCenterCard.vue'
import TabsBar from '@/components/user/TabsBar.vue'
import DataOverview from '@/components/user/DataOverview.vue'
import StatsModule from '@/components/user/StatsModule.vue'
const testUser1 = {
  username: '林小书',
  avatar: 'https://picsum.photos/id/64/200/200', // 固定头像（避免随机变化）
  bio: '喜欢阅读和分享书籍的普通读者～',
  isOnline: true, // 在线状态
  followCount: 120, // 关注数
  fansCount: 230, // 粉丝数
  postCount: 35, // 发布数
  coins: 180, // 充值币
}
// 模拟不同标签对应的动态数据
const tabDataMap = ref({
  week: {
    dataList: [
      { key: 'readTime', label: '阅读时长', value: '58 分钟' },
      { key: 'readBooks', label: '读过书籍', value: '读过 4 本' },
      { key: 'finishedBooks', label: '读完书籍', value: '读完 0 本' },
      { key: 'notes', label: '笔记数量', value: '笔记 2 条' },
    ],
    chartData: {
      xAxisData: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      seriesData: [
        { name: '东京', data: [8, 12, 10, 15, 9, 18, 12] },
        { name: '伦敦', data: [6, 9, 8, 12, 7, 15, 10] },
      ],
    },
    booksList: [
      { id: 1, name: '《人类简史》', readTime: '2小时13分', coverColor: '#e8f4f8' },
      { id: 2, name: '《原子习惯》', readTime: '1小时47分', coverColor: '#fdf2f8' },
      { id: 3, name: '《刻意练习》', readTime: '1小时22分', coverColor: '#f5fafe' },
    ],
  },
  month: {
    dataList: [
      { key: 'readTime', label: '阅读时长', value: '3.2 小时' },
      { key: 'readBooks', label: '读过书籍', value: '读过 8 本' },
      { key: 'finishedBooks', label: '读完书籍', value: '读完 2 本' },
      { key: 'notes', label: '笔记数量', value: '笔记 5 条' },
    ],
    chartData: {
      xAxisData: ['第1周', '第2周', '第3周', '第4周'],
      seriesData: [
        { name: '东京', data: [45, 62, 58, 72] },
        { name: '伦敦', data: [38, 52, 48, 65] },
      ],
    },
    booksList: [
      { id: 4, name: '《置身事内》', readTime: '3小时05分', coverColor: '#f0f8fb' },
      { id: 5, name: '《人类简史》', readTime: '2小时47分', coverColor: '#e8f4f8' },
      { id: 6, name: '《原则》', readTime: '2小时18分', coverColor: '#fcf1f7' },
    ],
  },
  // 可扩展 year/total/experience 标签的对应数据
})
</script>

<style scoped></style>
