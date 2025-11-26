<template>
  <div class="topic-card" @click="goToTopicDetail">
    <div class="topic-cover-container">
      <img :src="cover" alt="topic cover" class="topic-cover" />
    </div>
    <div class="topic-info">
      <h3 class="topic-title">{{ title }}</h3>
      <p class="post-number">{{ number }} 篇帖子</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
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

const goToTopicDetail = () => {
  router.push(`/topicdetail/${props.id}`)
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
