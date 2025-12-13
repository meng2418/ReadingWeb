<template>
  <div class="hot-topics">
    <h3 class="title">热门话题</h3>
    <div class="topic-list">
      <div
        v-for="(topic, index) in topics"
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
  openInNewTab?: boolean // 新增：是否在新标签页打开
}>()

// 默认话题数据 - 使用"文本标签"作为默认值
const defaultTopics: Topic[] = [
  { id: 1, name: '文本标签' },
  { id: 2, name: '文本标签' },
  { id: 3, name: '文本标签' },
  { id: 4, name: '文本标签' },
  { id: 5, name: '文本标签' },
  { id: 6, name: '文本标签' },
  { id: 7, name: '文本标签' },
  { id: 8, name: '文本标签' },
  { id: 9, name: '文本标签' },
  { id: 10, name: '文本标签' }
]

const topics = props.topics || defaultTopics

const handleTopicClick = (topic: Topic) => {
  console.log('点击话题:', topic.name)
  emit('topicClick', topic)

  // 跳转到话题详情页
  if (props.openInNewTab !== false) { // 默认在新标签页打开
    // 在新标签页打开话题详情页
    window.open(`/topicdetail/${topic.id}`, '_blank')
  } else {
    // 在当前页打开
    router.push(`/topicdetail/${topic.id}`)
  }
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
  font-size: 25px;      /* 增大标题 */
  font-weight: bold;    /* 加粗标题 */
  text-align: left;     /* 左对齐 */
  color: #333;          /* 确保颜色醒目 */
}

.topic-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: left;     /* 确保整个列表左对齐 */
}

.topic-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
  justify-content: flex-start; /* 确保项目左对齐 */
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
  color: #ff6b35;       /* 数字使用不同颜色 */
}

.name {
  flex: 1;
  color: #333;
  font-size: 14px;
}
</style>
