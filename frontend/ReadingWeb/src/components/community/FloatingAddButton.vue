<template>
  <div>
    <!-- 圆形加号按钮 -->
    <button class="floating-add-btn" @click="toggleEditor" aria-label="新建帖子">+</button>

    <!-- 发帖悬浮窗 -->
    <div v-if="showEditor" class="editor-overlay" @click.self="closeEditor">
      <div class="editor-window">
        <h3>发布帖子</h3>

        <!-- 标题部分：改为 input，禁止换行 -->
        <input v-model="titleContent" placeholder="输入标题" class="title-input" maxlength="20" />

        <!-- 正文 -->
        <textarea v-model="postContent" placeholder="输入正文" class="editor-textarea"></textarea>

        <!-- 左下角：添加话题 / 书籍 -->
        <div class="editor-tools">
          <el-icon class="emoji-icon tool-icon" @click="addEmoji">
            <Smile />
          </el-icon>
          <!-- 如果已经有当前话题，隐藏添加话题按钮 -->
          <el-icon v-if="!hasCurrentTopic" class="tool-icon" @click="addTopic">
            <ChatLineRound />
          </el-icon>
          <el-icon class="tool-icon" @click="addBook">
            <Notebook />
          </el-icon>
        </div>

        <!-- 已选项展示 -->
        <div class="selected-section" v-if="selectedTopics.length || selectedBooks.length">
          <!-- 已选话题-->
          <div v-if="selectedTopics.length" class="selected-group">
            <span class="group-label">话题：</span>
            <el-tag
              v-for="t in selectedTopics"
              :key="t"
              closable
              @close="removeTopic(t)"
              type="success"
              >{{ t }}</el-tag
            >
            <span class="topic-hint" v-if="hasCurrentTopic"></span>
          </div>

          <!-- 已选书籍-->
          <div v-if="selectedBooks.length" class="selected-group">
            <span class="group-label">书籍：</span>
            <el-tag
              v-for="b in selectedBooks"
              :key="b.title"
              closable
              @close="removeBook(b)"
              type="info"
              >{{ b.title }}</el-tag
            >
          </div>
        </div>

        <!-- Emoji 选择器浮层 -->
        <div v-if="showEmojiPicker" class="select-emoji" @click.self="showEmojiPicker = false">
          <emoji-picker></emoji-picker>
        </div>

        <!-- 话题选择浮层 - 如果没有当前话题才显示 -->
        <div
          v-if="showTopicPanel && !hasCurrentTopic"
          class="select-panel"
          @click.self="showTopicPanel = false"
        >
          <div class="panel-content">
            <div class="search-bar">
              <el-icon><Search /></el-icon>
              <input v-model="topicKeyword" placeholder="搜索话题" />
            </div>
            <div class="topic-list">
              <div
                v-for="t in filteredTopics"
                :key="t.name"
                class="topic-item"
                @click="selectTopic(t)"
              >
                <span class="hash">#</span>
                <div class="info">
                  <div class="name">{{ t.name }}</div>
                  <div class="meta">{{ t.view }}浏览 · {{ t.discuss }}讨论</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 书籍选择浮层 -->
        <div v-if="showBookPanel" class="select-panel" @click.self="showBookPanel = false">
          <div class="panel-content">
            <div class="search-bar">
              <el-icon><Search /></el-icon>
              <input v-model="bookKeyword" placeholder="搜索书籍" />
            </div>
            <div class="topic-list">
              <div
                v-for="b in filteredBooks"
                :key="b.title"
                class="topic-item"
                @click="selectBook(b)"
              >
                <span class="hash"
                  ><el-icon><Reading /></el-icon
                ></span>
                <div class="info">
                  <div class="name">{{ b.title }}</div>
                  <div class="meta">作者：{{ b.author }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="editor-actions">
          <button class="cancel-btn" @click="closeEditor">取消</button>
          <button class="submit-btn" @click="submitPost">发布</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, watch } from 'vue'
import { ChatLineRound, Notebook, Search, Reading } from '@element-plus/icons-vue'
import { Smile } from 'lucide-vue-next'
import 'emoji-picker-element'
import type { SimpleBook } from '@/types/book'

// 接口定义
interface Topic {
  name: string
  view: string
  discuss: string
}

interface EmojiClickEvent extends CustomEvent {
  detail: {
    unicode: string
  }
}

interface CurrentTopic {
  id: string
  title: string
  name: string
}

// 定义props - 添加当前话题属性
const props = defineProps<{
  currentTopic?: CurrentTopic
}>()

// 编辑器状态
const showEditor = ref(false)
const titleContent = ref('')
const postContent = ref('')

// emoji 选择框显示
const showEmojiPicker = ref(false)

// 弹窗显示状态
const showTopicPanel = ref(false)
const showBookPanel = ref(false)

// 搜索关键词
const topicKeyword = ref('')
const bookKeyword = ref('')

// 已选内容
const selectedTopics = ref<string[]>([])
const selectedBooks = ref<SimpleBook[]>([])

// 计算属性 - 检查是否有当前话题
const hasCurrentTopic = computed(() => {
  return props.currentTopic && props.currentTopic.name
})

// 监听当前话题变化，自动添加到已选话题
watch(
  () => props.currentTopic,
  (newTopic) => {
    if (newTopic && newTopic.name && !selectedTopics.value.includes(newTopic.name)) {
      selectedTopics.value = [newTopic.name] // 替换而不是追加，确保只有一个话题
    }
  },
  { immediate: true },
)

// 示例数据
const topicList = ref<Topic[]>([
  { name: '小剧场过大年', view: '16.6亿', discuss: '133.7万' },
  { name: '巴黎最前线', view: '8亿', discuss: '95.1万' },
  { name: '三角洲破壁新赛季上线', view: '38.8亿', discuss: '567.5万' },
  { name: '英雄联盟新春激励', view: '16.6亿', discuss: '210.4万' },
  { name: '春节档爆款接力赛', view: '9.5亿', discuss: '70.2万' },
])

const bookList = ref<SimpleBook[]>([
  { title: '人类简史', author: '尤瓦尔·赫拉利' },
  { title: '小王子', author: '圣埃克苏佩里' },
  { title: '乌合之众', author: '古斯塔夫·勒庞' },
  { title: '沉默的大多数', author: '王小波' },
])

// 搜索过滤
const filteredTopics = computed(() =>
  topicList.value.filter((t) => t.name.includes(topicKeyword.value)),
)
const filteredBooks = computed(() =>
  bookList.value.filter((b) => b.title.includes(bookKeyword.value)),
)

// 操作逻辑
const toggleEditor = (): void => {
  showEditor.value = !showEditor.value
}

const closeEditor = (): void => {
  showEditor.value = false
}

const addEmoji = (): void => {
  showEmojiPicker.value = !showEmojiPicker.value
  nextTick(() => {
    const picker = document.querySelector('emoji-picker')
    if (picker) {
      picker.addEventListener('emoji-click', (event: Event) => {
        const emojiEvent = event as EmojiClickEvent
        postContent.value += emojiEvent.detail.unicode
      })
    }
  })
}

const addTopic = (): void => {
  // 如果有当前话题，不允许添加其他话题
  if (hasCurrentTopic.value) {
    return
  }
  showTopicPanel.value = true
  showBookPanel.value = false
}

const addBook = (): void => {
  showBookPanel.value = true
  showTopicPanel.value = false
}

const selectTopic = (topic: Topic): void => {
  // 限制只能选择一个话题
  selectedTopics.value = [topic.name]
  showTopicPanel.value = false
}

const selectBook = (book: Book): void => {
  // 书籍可以多选，但限制最多3个
  if (!selectedBooks.value.find((b) => b.title === book.title) && selectedBooks.value.length < 3) {
    selectedBooks.value.push(book)
  }
  showBookPanel.value = false
}

const removeTopic = (t: string): void => {
  // 如果有当前话题，不允许删除
  if (hasCurrentTopic.value && t === props.currentTopic?.name) {
    return
  }
  selectedTopics.value = selectedTopics.value.filter((i) => i !== t)
}

const removeBook = (b: Book): void => {
  selectedBooks.value = selectedBooks.value.filter((i) => i.title !== b.title)
}

const submitPost = (): void => {
  console.log('发布帖子:', {
    title: titleContent.value,
    content: postContent.value,
    topics: selectedTopics.value,
    books: selectedBooks.value,
    currentTopic: props.currentTopic,
  })
  // TODO: 这里添加实际的发布逻辑
  closeEditor()
  // 清空表单，但保留当前话题
  titleContent.value = ''
  postContent.value = ''
  selectedBooks.value = []
  // 如果有当前话题，保持选中状态
  if (hasCurrentTopic.value) {
    selectedTopics.value = [props.currentTopic!.name]
  } else {
    selectedTopics.value = []
  }
}
</script>

<style scoped>
/* --- 浮动加号按钮 --- */
.floating-add-btn {
  position: fixed;
  bottom: 20px;
  right: 70px; /* 位于 BackToTop 左侧 */
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: var(--primary-green);
  color: white;
  font-size: 24px;
  cursor: pointer;
  transition:
    background 0.3s,
    transform 0.2s;
  z-index: 1000; /* 确保在其他内容之上 */
}

.floating-add-btn:hover {
  background: var(--secondary-green);
  transform: scale(1.05);
}

/* --- 发帖悬浮层 --- */
.editor-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999; /* 确保在最顶层 */
  padding: 20px;
  box-sizing: border-box;
}

/* --- 发帖窗口 --- */
.editor-window {
  background: white;
  border-radius: 12px;
  padding: 20px;
  max-width: 90%;
  width: 100%;
  max-width: 900px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  animation: fadeIn 0.2s ease;
  box-sizing: border-box;
}

.editor-window h3 {
  margin-top: 0;
  margin-bottom: 12px;
  color: var(--primary-green);
  font-weight: 600;
}

.title-input {
  width: 100%;
  height: 40px;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 16px;
  margin-bottom: 12px;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.editor-textarea {
  width: 100%;
  min-height: 300px;
  max-height: 40vh;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 15px;
  resize: vertical;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
  overflow-y: auto;
}

.editor-textarea:focus,
.title-input:focus {
  border-color: var(--secondary-green);
}

/* --- 底部操作按钮 --- */
.editor-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
  gap: 10px;
}

.cancel-btn,
.submit-btn {
  padding: 6px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: 0.2s;
}

.cancel-btn {
  background: #ccc;
  color: white;
}

.cancel-btn:hover {
  background: var(--text-placeholder);
}

.submit-btn {
  background: var(--primary-green);
  color: white;
}

.submit-btn:hover {
  background: var(--secondary-green);
}

/* 工具栏：左下角 */
.editor-tools {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 8px;
}

.tool-icon {
  font-size: 22px;
  color: var(--primary-green);
  cursor: pointer;
  transition:
    color 0.2s,
    transform 0.2s;
}

.tool-icon:hover {
  color: var(--secondary-green);
  transform: scale(1.1);
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.selected-section {
  margin-top: 10px;
}

.selected-group {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 6px;
}

.group-label {
  font-weight: bold;
  color: var(--primary-green);
}

.topic-hint {
  font-size: 12px;
  color: #666;
  font-style: italic;
}

.select-emoji {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1001;
}

/* 浮层 */
.select-panel {
  position: fixed;
  inset: 0;
  background: var(--shadow-color);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1001;
}

/* --- 浮层优化（话题/书籍选择面板）--- */
.panel-content {
  width: 90%;
  max-width: 420px;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px var(--shadow-color);
  display: flex;
  flex-direction: column;
  max-height: 70vh;
}

.search-bar {
  display: flex;
  align-items: center;
  background: var(--background-color);
  padding: 8px 12px;
}

.search-bar input {
  border: none;
  outline: none;
  background: transparent;
  flex: 1;
  margin-left: 6px;
  font-size: 14px;
}

.topic-list {
  flex: 1;
  overflow-y: auto;
  padding: 6px 0;
}

.topic-item {
  display: flex;
  align-items: center;
  padding: 10px 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.topic-item:hover {
  background: #f5f5f5;
}

.hash {
  color: var(--primary-green);
  font-size: 18px;
  margin-right: 10px;
}

.info .name {
  font-weight: 500;
}

.info .meta {
  font-size: 12px;
  color: var(--text-secondary);
}
</style>
