<template>
  <div>
    <!-- 圆形加号按钮 -->
    <button class="floating-add-btn" @click="toggleEditor" aria-label="新建帖子">+</button>

    <!-- 发帖悬浮窗 -->
    <div v-if="showEditor" class="editor-overlay" @click.self="closeEditor">
      <div class="editor-window">
        <h3>发布帖子</h3>

        <!-- 标题 -->
        <input v-model="titleContent" placeholder="输入标题" class="title-input" maxlength="20" />

        <!-- 正文 -->
        <textarea v-model="postContent" placeholder="输入正文" class="editor-textarea" />

        <!-- 工具栏 -->
        <div class="editor-tools">
          <el-icon class="tool-icon" @click="addEmoji"><Smile /></el-icon>
          <el-icon v-if="!hasCurrentTopic" class="tool-icon" @click="openTopicPanel">
            <ChatLineRound />
          </el-icon>
          <el-icon class="tool-icon" @click="openBookPanel">
            <Notebook />
          </el-icon>
        </div>

        <!-- 已选项 -->
        <div v-if="selectedTopics.length || selectedBooks.length" class="selected-section">
          <div v-if="selectedTopics.length" class="selected-group">
            <span class="group-label">话题：</span>
            <el-tag
              v-for="t in selectedTopics"
              :key="t"
              closable
              @close="removeTopic(t)"
              type="success"
            >
              {{ t }}
            </el-tag>
          </div>

          <div v-if="selectedBooks.length" class="selected-group">
            <span class="group-label">书籍：</span>
            <el-tag
              v-for="b in selectedBooks"
              :key="b.id"
              closable
              @close="removeBook(b)"
              type="info"
            >
              {{ b.title }}
            </el-tag>
          </div>
        </div>

        <!-- Emoji -->
        <div v-if="showEmojiPicker" class="select-emoji" @click.self="showEmojiPicker = false">
          <emoji-picker />
        </div>

        <!-- 话题选择 -->
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
              <div v-for="t in topicList" :key="t.id" class="topic-item" @click="selectTopic(t)">
                <span class="hash">#</span>
                <div class="info">
                  <div class="name">{{ t.name }}</div>
                  <div class="meta">{{ t.view }} 浏览 · {{ t.discuss }} 讨论</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 书籍选择 -->
        <div v-if="showBookPanel" class="select-panel" @click.self="showBookPanel = false">
          <div class="panel-content">
            <div class="search-bar">
              <el-icon><Search /></el-icon>
              <input v-model="bookKeyword" placeholder="搜索书籍" />
            </div>

            <div class="topic-list">
              <div v-for="b in bookList" :key="b.id" class="topic-item" @click="selectBook(b)">
                <span class="hash">
                  <el-icon><Reading /></el-icon>
                </span>
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
import { ref, computed, watch, nextTick, onMounted } from 'vue'
import { ChatLineRound, Notebook, Search, Reading } from '@element-plus/icons-vue'
import { Smile } from 'lucide-vue-next'
import 'emoji-picker-element'

import { searchTopics, searchBooks, publishPost } from '@/api/publish'
import { getGuessBooks } from '@/api/home'
import { getTopicsList } from '@/api/topics/topics-list'

interface Topic {
  id: number
  name: string
  view: number
  discuss: number
}

export interface SimpleBook {
  id: number
  title: string
  author: string
}

interface CurrentTopic {
  id: string
  name: string
}

/* ---------- props ---------- */
const props = defineProps<{
  currentTopic?: CurrentTopic
}>()

/* ---------- state ---------- */
const showEditor = ref(false)
const showEmojiPicker = ref(false)
const showTopicPanel = ref(false)
const showBookPanel = ref(false)

const titleContent = ref('')
const postContent = ref('')

const topicKeyword = ref('')
const bookKeyword = ref('')

const topicList = ref<Topic[]>([])
const bookList = ref<SimpleBook[]>([])

const selectedTopics = ref<string[]>([])
const selectedBooks = ref<SimpleBook[]>([])

const hasCurrentTopic = computed(() => !!props.currentTopic?.name)

/* ---------- 初始化示例数据 ---------- */
onMounted(async () => {
  // 获取猜你喜欢书籍
  const guessBooks = await getGuessBooks()
  bookList.value = guessBooks.map((b) => ({ id: b.bookId, title: b.title, author: b.author }))

  // 获取话题列表
  const topicsRes = await getTopicsList()
  topicList.value = topicsRes.items.map((t) => ({
    id: t.id as number,
    name: t.name,
    view: 0,
    discuss: t.postCount,
  }))
})

/* ---------- watch ---------- */
watch(
  () => props.currentTopic,
  (val) => {
    if (val?.name) selectedTopics.value = [val.name]
  },
  { immediate: true },
)

let topicTimer: number | null = null
watch(topicKeyword, () => {
  if (topicTimer) clearTimeout(topicTimer)
  topicTimer = window.setTimeout(fetchTopics, 300)
})

let bookTimer: number | null = null
watch(bookKeyword, () => {
  if (bookTimer) clearTimeout(bookTimer)
  bookTimer = window.setTimeout(fetchBooks, 300)
})

/* ---------- 搜索方法 ---------- */
const fetchTopics = async () => {
  if (!topicKeyword.value) return
  const res = await searchTopics({ keyword: topicKeyword.value, limit: 10 })
  topicList.value = res.data.data.topics.map((t: any) => ({
    id: t.topicId,
    name: t.topicName,
    view: t.viewCount,
    discuss: t.discussionCount,
  }))
}

const fetchBooks = async () => {
  if (!bookKeyword.value) return
  const res = await searchBooks({ keyword: bookKeyword.value, limit: 10 })
  bookList.value = res.data.data.books.map((b: any) => ({
    id: b.bookId,
    title: b.bookTitle,
    author: b.authorName,
  }))
}

/* ---------- UI 操作 ---------- */
const toggleEditor = () => (showEditor.value = !showEditor.value)
const closeEditor = () => (showEditor.value = false)

const addEmoji = () => {
  showEmojiPicker.value = !showEmojiPicker.value
  nextTick(() => {
    document.querySelector('emoji-picker')?.addEventListener('emoji-click', (e: any) => {
      postContent.value += e.detail.unicode
    })
  })
}

const openTopicPanel = () => {
  showTopicPanel.value = true
  showBookPanel.value = false
}

const openBookPanel = () => {
  showBookPanel.value = true
  showTopicPanel.value = false
}

const selectTopic = (t: Topic) => {
  selectedTopics.value = [t.name]
  showTopicPanel.value = false
}

const selectBook = (b: SimpleBook) => {
  if (selectedBooks.value.length >= 3) return
  if (!selectedBooks.value.find((i) => i.id === b.id)) {
    selectedBooks.value.push(b)
  }
  showBookPanel.value = false
}

const removeTopic = (name: string) => {
  if (hasCurrentTopic.value && name === props.currentTopic?.name) return
  selectedTopics.value = []
}

const removeBook = (b: SimpleBook) => {
  selectedBooks.value = selectedBooks.value.filter((i) => i.id !== b.id)
}

const submitPost = async () => {
  if (!titleContent.value.trim() || !postContent.value.trim()) return

  await publishPost({
    title: titleContent.value,
    content: postContent.value,
    bookIds: selectedBooks.value.map((b) => b.id),
    topics: selectedTopics.value,
  })

  titleContent.value = ''
  postContent.value = ''
  selectedBooks.value = []
  selectedTopics.value = hasCurrentTopic.value ? [props.currentTopic!.name] : []

  closeEditor()
}
</script>

<style scoped>
/* --- 浮动加号按钮 --- */
.floating-add-btn {
  position: fixed;
  bottom: 20px;
  right: 70px;
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
  z-index: 1000;
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
  z-index: 9999;
  padding: 20px;
  box-sizing: border-box;
}

/* --- 发帖窗口 --- */
.editor-window {
  background: white;
  border-radius: 12px;
  padding: 20px;
  max-width: 900px;
  width: 100%;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  animation: fadeIn 0.2s ease;
}

.editor-window h3 {
  margin-top: 0;
  margin-bottom: 12px;
  color: var(--primary-green);
  font-weight: 600;
}

.title-input,
.editor-textarea {
  width: 100%;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.title-input {
  height: 40px;
  margin-bottom: 12px;
}

.editor-textarea {
  min-height: 300px;
  max-height: 40vh;
  resize: vertical;
  overflow-y: auto;
}

.title-input:focus,
.editor-textarea:focus {
  border-color: var(--secondary-green);
}

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

.select-emoji,
.select-panel {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1001;
}
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
