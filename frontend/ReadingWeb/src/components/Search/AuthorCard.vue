<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'


const router = useRouter()
const route = useRoute()
const props = defineProps({
  name: { type: String, required: true },
  avatar: { type: String, required: true },
  description: { type: String, default: '暂无简介' },
  readersCount: { type: String, default: '0' },
  works: { type: String, default: '' },
})

const emit = defineEmits(['view'])

const formattedReaders = computed(() => {
  const count = parseInt(props.readersCount)
  // 如果是 NaN 或 0，处理一下
  if (isNaN(count)) return '0'
  return count >= 10000 ? `${(count / 10000).toFixed(1)}w` : count.toString()
})

const handleViewDetail = () => {
  emit('view')
  router.push(`/authordetail/${props.id}`)
}
</script>

<template>
  <div class="author-card">
    <div class="author-left">
      <img :src="avatar" :alt="name" class="author-avatar" />
    </div>

    <div class="author-right">
      <div class="header-row">
        <div class="name-wrapper">
          <h2 class="author-name">{{ name }}</h2>
        </div>
        <button class="detail-btn" @click="handleViewDetail">
          查看详情
          <span class="arrow">→</span>
        </button>
      </div>

      <div class="meta-row">
        <span class="meta-item">关注 {{ formattedReaders }}</span>
        <span class="meta-item divider">/</span>
        <span class="meta-item works">代表作：{{ works }}</span>
      </div>

      <p class="author-desc">{{ description }}</p>
    </div>
  </div>
</template>

<style scoped>
.author-card {
  display: flex;
  gap: 24px;
  align-items: flex-start;
  padding: 24px 0;
  /* 保持干净，无背景色 */
}

.author-avatar {
  width: 110px;
  height: 110px;
  border-radius: 50%; /* 圆形头像 */
  object-fit: cover;
  border: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.author-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.header-row {
  display: flex;
  justify-content: space-between; /* 名字靠左，按钮靠右 */
  align-items: flex-start;
}

.author-name {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin: 0;
  letter-spacing: 0.5px;
}

/* 详情按钮样式 - 极简线框风格 */
.detail-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 16px;
  border: 1px solid #ccc;
  border-radius: 20px; /* 胶囊形状 */
  font-size: 0.85rem;
  color: #555;
  background: transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 400;
}

.detail-btn .arrow {
  font-size: 1rem;
  transition: transform 0.3s ease;
}

.detail-btn:hover {
  border-color: #333;
  color: #000;
  background-color: rgba(0, 0, 0, 0.02); /* 极淡的背景 */
}

.detail-btn:hover .arrow {
  transform: translateX(3px); /* 箭头微动动画 */
}

.meta-row {
  display: flex;
  align-items: center;
  font-size: 0.9rem;
  color: #888;
  gap: 12px;
  margin-top: -4px; /* 拉近与标题的距离 */
}

.divider {
  color: #e0e0e0;
  font-size: 0.8rem;
}

.works {
  color: #666;
  font-weight: 500;
}

.author-desc {
  margin-top: 6px;
  font-size: 0.95rem;
  line-height: 1.7; /* 增加行高，提升阅读舒适度 */
  color: #666;
  text-align: justify; /* 两端对齐，更像印刷品 */

  /* 多行省略 */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
