<template>
  <div class="topic-card" @click="goToTopicDetail">
    <div class="topic-cover-container">
      <img :src="coverUrl" alt="topic cover" class="topic-cover" />
    </div>
    <div class="topic-info">
      <h3 class="topic-title">{{ title }}</h3>
      <p class="post-number">{{ number }} 篇帖子</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { getTopicCoverUrl } from '@/utils/defaultImages'

const router = useRouter()

const props = defineProps({
  id: {
    type: [String, Number],
    required: true
  },
  cover: {
    type: String,
    default: 'https://picsum.photos/200/300',
  },
  title: {
    type: String,
    default: '每日读点小说',
  },
  number: {
    type: Number,
    default: 200,
  },
})

// 计算话题封面URL，使用默认图片
const coverUrl = computed(() => getTopicCoverUrl(props.cover))

const goToTopicDetail = () => {
  // 使用 router.resolve 获取路由的完整路径
  const route = router.resolve({ name: 'TopicDetail', params: { id: props.id } })
  window.open(route.href, '_blank')
}
</script>

<style scoped>
.topic-card {
  width: 160px;
  padding: 12px;
  text-align: center;
  cursor: pointer;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.topic-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.topic-cover-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 10px;
}

.topic-cover {
  border-radius: 50%;
  width: 100px;
  height: 100px;
  object-fit: cover;
  border: 2px solid #f0f0f0;
}

.topic-info {
  line-height: 1.4;
}

.topic-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 4px 0;
  transition: color 0.2s ease;
}

.topic-card:hover .topic-title {
  color: #0078f2;
}

.post-number {
  font-size: 13px;
  color: #888;
  margin-top: 2px;
}
</style>
