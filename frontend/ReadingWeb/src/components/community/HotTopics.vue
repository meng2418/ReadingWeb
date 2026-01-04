<template>
  <div class="hot-topics">
    <h3 class="title">热门话题</h3>
    <div class="topic-list">
      <div
        v-for="(topic, index) in displayTopics"
        :key="topic.id"
        class="topic-item"
        @click="handleTopicClick(topic)"
      >
        <span class="rank">{{ index + 1 }}</span>
        <span class="name">{{ topic.name }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'

// 获取路由实例
const router = useRouter()

interface Topic {
  id: number
  name: string
}

// 定义props
const props = defineProps<{
  topics?: Topic[]
}>()

// 计算属性：显示的话题列表
const displayTopics = computed(() => {
  // 如果有传入的话题且不为空，使用传入的话题
  if (props.topics && props.topics.length > 0) {
    console.log('HotTopics组件接收到的话题数据:', props.topics)
    return props.topics
  }

  console.log('HotTopics组件没有接收到话题数据')
  return [] // 返回空数组，而不是默认数据
})

const handleTopicClick = (topic: Topic) => {
  console.log('点击话题:', topic.name)
  emit('topicClick', topic)
  // 在新标签页打开话题详情页
  window.open(`/topicdetail/${topic.id}`, '_blank')
}

// 定义事件
const emit = defineEmits<{
  topicClick: [topic: Topic]
}>()
</script>

<style scoped>
.hot-topics {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  max-width: 320px;
}

.title {
  margin: 0 0 15px 0;
  font-size: 25px;
  font-weight: bold;
  text-align: left;
  color: #333;
}

.topic-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: left;
}

.topic-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
  justify-content: flex-start;
}

.topic-item:hover {
  background-color: #f5f5f5;
  transform: translateX(2px);
}

.rank {
  width: 20px;
  text-align: center;
  font-weight: bold;
  font-size: 14px;
  color: #ff6b35;
}

.name {
  flex: 1;
  color: #333;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
