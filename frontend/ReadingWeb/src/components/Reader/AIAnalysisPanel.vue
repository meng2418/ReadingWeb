<script setup lang="ts">
import { ref, watch, computed, nextTick } from 'vue'
import { Sparkles, X, Bot, Send } from 'lucide-vue-next'
import { interpretTextStream } from '@/api/reader/ai-interpret'
import { getChatHistory, sendChatMessageStream } from '@/api/ai'

interface ChatMessage {
  role: 'assistant' | 'user'
  content: string
  streaming?: boolean
}

const props = defineProps<{
  isOpen: boolean
  selectedText: string
  isDarkMode: boolean
  bookId?: number | string
  bookTitle?: string
  chapterTitle?: string
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const loading = ref(false)
const streaming = ref(false)
const inputValue = ref('')
const messages = ref<ChatMessage[]>([])
const chatContainerRef = ref<HTMLElement | null>(null)
const abortController = ref<AbortController | null>(null)

// Watchdog timer id to avoid stuck streaming state
let streamWatchdog: number | null = null
const WATCHDOG_TIMEOUT_MS = 60_000 // 60s

const resetWatchdog = () => {
  if (streamWatchdog) {
    clearTimeout(streamWatchdog)
  }
  streamWatchdog = window.setTimeout(() => {
    // If stream hangs, stop it and clear flags to restore UI
    abortController.value?.abort()
    abortController.value = null
    streaming.value = false
    streamWatchdog = null
  }, WATCHDOG_TIMEOUT_MS)
}

const clearWatchdog = () => {
  if (streamWatchdog) {
    clearTimeout(streamWatchdog)
    streamWatchdog = null
  }
}

const selectedTextPreview = computed(() => {
  return props.selectedText
    ? props.selectedText.length > 80
      ? `${props.selectedText.slice(0, 80)}...`
      : props.selectedText
    : ''
})

const scrollToBottom = async () => {
  await nextTick()
  const el = chatContainerRef.value
  if (el) {
    el.scrollTop = el.scrollHeight
  }
}

const stopStreaming = () => {
  abortController.value?.abort()
  abortController.value = null
}

const appendAssistantMessage = () => {
  messages.value.push({ role: 'assistant', content: '', streaming: true })
  return messages.value.length - 1
}

const updateAssistantContent = (index: number, chunk: string) => {
  const msg = messages.value[index]
  if (msg) {
    msg.content += chunk
  }
}

const finishAssistantMessage = (index: number) => {
  const msg = messages.value[index]
  if (msg) {
    msg.streaming = false
    if (!msg.content.trim()) {
      msg.content = '（未生成内容）'
    }
  }
}

const handleStreamError = (index: number, message: string) => {
  const msg = messages.value[index]
  if (msg) {
    msg.streaming = false
    msg.content = message || 'AI 请求失败，请稍后重试'
  }
}

const runInterpretStream = async (followUp?: string) => {
  if (!props.selectedText?.trim()) return

  const assistantIndex = appendAssistantMessage()
  streaming.value = true
  loading.value = false
  await scrollToBottom()

  abortController.value = new AbortController()

  // start watchdog to recover from hanging streams
  resetWatchdog()

  try {
    await interpretTextStream(
      props.selectedText,
      {
        onChunk: (chunk) => {
          updateAssistantContent(assistantIndex, chunk)
          // reset watchdog on activity
          resetWatchdog()
          scrollToBottom()
        },
        onDone: () => {
          finishAssistantMessage(assistantIndex)
          clearWatchdog()
          // ensure streaming is cleared immediately when done
          streaming.value = false
          abortController.value = null
        },
        onError: (message) => {
          handleStreamError(assistantIndex, message)
          clearWatchdog()
          streaming.value = false
          abortController.value = null
        },
      },
      {
        bookTitle: props.bookTitle,
        chapterTitle: props.chapterTitle,
        followUp,
        signal: abortController.value.signal,
      },
    )
  } catch {
    // onError 已处理
  } finally {
    // final cleanup (no-op if already cleared in callbacks)
    clearWatchdog()
    streaming.value = false
    abortController.value = null
    await scrollToBottom()
  }
}

const runChatStream = async (message: string) => {
  const bookId = Number(props.bookId)
  if (!bookId) {
    messages.value.push({
      role: 'assistant',
      content: '无法获取书籍信息，请刷新页面后重试。',
    })
    return
  }

  const assistantIndex = appendAssistantMessage()
  streaming.value = true
  await scrollToBottom()

  abortController.value = new AbortController()

  // start watchdog to recover from hanging streams
  resetWatchdog()

  try {
    await sendChatMessageStream(
      bookId,
      message,
      {
        onChunk: (chunk) => {
          updateAssistantContent(assistantIndex, chunk)
          // reset watchdog on activity
          resetWatchdog()
          scrollToBottom()
        },
        onDone: () => {
          finishAssistantMessage(assistantIndex)
          clearWatchdog()
          streaming.value = false
          abortController.value = null
        },
        onError: (errMsg) => {
          handleStreamError(assistantIndex, errMsg)
          clearWatchdog()
          streaming.value = false
          abortController.value = null
        },
      },
      { signal: abortController.value.signal },
    )
  } catch {
    // onError 已处理
  } finally {
    clearWatchdog()
    streaming.value = false
    abortController.value = null
    await scrollToBottom()
  }
}

const GREETING_CONTENT =
  '你好！我是你的 AI 阅读助手。你可以就当前阅读的内容向我提问，也可以在书中划线选中某段文字让我帮你进行详细分析。'

const makeGreeting = (): ChatMessage => ({ role: 'assistant', content: GREETING_CONTENT })

const loadHistory = async () => {
  const bookId = Number(props.bookId)
  if (!bookId) {
    messages.value = [makeGreeting()]
    return
  }

  loading.value = true
  try {
    const res = await getChatHistory(bookId, { limit: 50 })
    const history = (res?.data?.data?.messages ?? [])
      .filter((m) => m.role === 'user' || m.role === 'assistant')
      .map<ChatMessage>((m) => ({ role: m.role as ChatMessage['role'], content: m.content }))
    messages.value = history.length ? history : [makeGreeting()]
  } catch {
    // 历史加载失败不阻塞对话，回退到欢迎语
    messages.value = [makeGreeting()]
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}

const startAnalysis = async () => {
  stopStreaming()
  messages.value = []
  inputValue.value = ''

  if (props.selectedText?.trim()) {
    loading.value = true
    await runInterpretStream()
    loading.value = false
  } else {
    await loadHistory()
  }
}

watch(
  () => [props.isOpen, props.selectedText] as const,
  ([isOpen]) => {
    if (isOpen) {
      startAnalysis()
    } else {
      stopStreaming()
    }
  },
)

const sendMessage = async () => {
  const text = inputValue.value.trim()
  if (!text || loading.value || streaming.value) return

  messages.value.push({ role: 'user', content: text })
  inputValue.value = ''
  await scrollToBottom()

  if (props.selectedText?.trim()) {
    await runInterpretStream(text)
  } else {
    await runChatStream(text)
  }
}

const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    sendMessage()
  }
}

const handleClose = () => {
  stopStreaming()
  emit('close')
}
</script>

<template>
  <div v-if="isOpen">
    <div class="backdrop" @click="handleClose"></div>

    <div class="ai-panel" :class="{ 'dark-mode': isDarkMode }">
      <div class="panel-header">
        <div class="header-title">
          <Sparkles :size="18" class="sparkle-icon" />
          AI 助手
        </div>
        <button @click="handleClose" class="close-btn">
          <X :size="18" />
        </button>
      </div>

      <div v-if="selectedTextPreview" class="panel-meta">
        <div class="meta-label">选中文本</div>
        <div class="meta-content">{{ selectedTextPreview }}</div>
      </div>

      <div class="panel-body">
        <div v-if="loading && messages.length === 0" class="loading-state">
          <div class="spinner"></div>
          <p class="loading-text">正在连接 AI...</p>
        </div>

        <div v-else class="chat-container" ref="chatContainerRef">
          <div class="message-list">
            <div
              v-for="(message, index) in messages"
              :key="index"
              :class="[
                'message-row',
                message.role === 'user' ? 'user-message-row' : 'assistant-message-row',
              ]"
            >
              <div v-if="message.role === 'assistant'" class="avatar-wrapper">
                <div class="avatar-icon">
                  <Bot :size="16" />
                </div>
              </div>
              <div
                :class="[
                  'message-bubble',
                  message.role === 'user' ? 'user-bubble' : 'assistant-bubble',
                ]"
              >
                <p>
                  {{ message.content }}<span v-if="message.streaming" class="cursor-blink">▍</span>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="panel-footer">
        <textarea
          v-model="inputValue"
          :placeholder="loading || streaming ? 'AI 正在回复中...' : '输入你的问题...'"
          :disabled="loading || streaming"
          class="chat-input"
          rows="1"
          @keydown="handleKeydown"
        />
        <button
          class="send-btn"
          @click="sendMessage"
          :disabled="loading || streaming || !inputValue.trim()"
        >
          <Send :size="16" />
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.backdrop {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.24);
  z-index: 50;
}

.ai-panel {
  position: fixed;
  top: 0;
  right: 0;
  height: 100%;
  width: min(420px, 100%);
  max-width: 420px;
  background-color: #ffffff;
  color: #111827;
  z-index: 60;
  box-shadow: -16px 0 40px rgba(15, 23, 42, 0.12);
  display: flex;
  flex-direction: column;
  transition: transform 0.25s ease-out;
}

.ai-panel.dark-mode {
  background-color: #111827;
  color: #e5e7eb;
  box-shadow: -16px 0 40px rgba(0, 0, 0, 0.4);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.25rem 1.25rem 0.75rem;
  border-bottom: 1px solid #e5e7eb;
}

.ai-panel.dark-mode .panel-header {
  border-color: #27272a;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1rem;
  font-weight: 600;
}

.sparkle-icon {
  color: #111827;
}
.ai-panel.dark-mode .sparkle-icon {
  color: #f3f4f6;
}

.close-btn {
  width: 2rem;
  height: 2rem;
  border-radius: 6px;
  display: grid;
  place-items: center;
  background: transparent;
  border: none;
  color: inherit;
  cursor: pointer;
}

.close-btn:hover {
  background-color: rgba(17, 24, 39, 0.06);
}

.ai-panel.dark-mode .close-btn:hover {
  background-color: rgba(255, 255, 255, 0.08);
}

.panel-meta {
  padding: 0.85rem 1.25rem 0;
}

.meta-label {
  font-size: 0.75rem;
  font-weight: 500;
  color: #6b7280;
  margin-bottom: 0.35rem;
}

.ai-panel.dark-mode .meta-label {
  color: #9ca3af;
}

.meta-content {
  font-size: 0.875rem;
  line-height: 1.6;
  color: inherit;
  background: #f8fafc;
  border-radius: 6px;
  padding: 0.65rem 0.8rem;
  border: 1px solid #e2e8f0;
}

.ai-panel.dark-mode .meta-content {
  background: #1f2937;
  border-color: #374151;
}

.panel-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 1rem 1.25rem 0;
  overflow: hidden;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  gap: 1rem;
  padding: 1rem;
}

.spinner {
  width: 1.75rem;
  height: 1.75rem;
  border-radius: 50%;
  border: 2px solid #111827;
  border-top-color: transparent;
  animation: spin 1s linear infinite;
}

.ai-panel.dark-mode .spinner {
  border-color: #f3f4f6;
  border-top-color: transparent;
}

.loading-text {
  font-size: 0.875rem;
  color: #6b7280;
  text-align: center;
}

.ai-panel.dark-mode .loading-text {
  color: #9ca3af;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.chat-container {
  flex: 1;
  overflow-y: auto;
  padding-right: 0.25rem;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding-bottom: 0.5rem;
}

.message-row {
  display: flex;
  gap: 0.6rem;
}

.assistant-message-row {
  align-items: flex-start;
}

.user-message-row {
  justify-content: flex-end;
}

.avatar-wrapper {
  width: 2rem;
  height: 2rem;
  min-width: 2rem;
  border-radius: 6px;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  display: grid;
  place-items: center;
}

.ai-panel.dark-mode .avatar-wrapper {
  background: #374151;
  border-color: #4b5563;
}

.avatar-icon {
  color: #111827;
}

.ai-panel.dark-mode .avatar-icon {
  color: #f3f4f6;
}

.message-bubble {
  max-width: 90%;
  padding: 0.5rem 0.75rem;
  border-radius: 6px;
  font-size: 0.9rem;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
}

.message-bubble p {
  margin: 0;
}

.cursor-blink {
  animation: blink 1s step-end infinite;
}

@keyframes blink {
  50% {
    opacity: 0;
  }
}

.assistant-bubble {
  background: #f8fafc;
  color: #111827;
  border: 1px solid #e2e8f0;
  border-top-left-radius: 2px;
}

.ai-panel.dark-mode .assistant-bubble {
  background: #1f2937;
  color: #e5e7eb;
  border-color: #374151;
}

.user-bubble {
  background: #111827;
  color: #ffffff;
  border-top-right-radius: 2px;
}

.ai-panel.dark-mode .user-bubble {
  background: #f3f4f6;
  color: #111827;
}

.panel-footer {
  padding: 0.85rem 1rem 1rem;
  border-top: 1px solid #e5e7eb;
  display: flex;
  gap: 0.6rem;
  align-items: center;
}

.ai-panel.dark-mode .panel-footer {
  border-color: #27272a;
}

.chat-input {
  width: 100%;
  min-height: 40px;
  resize: none;
  border-radius: 6px;
  border: 1px solid #d1d5db;
  padding: 0.5rem 0.75rem;
  font-size: 0.9rem;
  color: inherit;
  background: #ffffff;
  outline: none;
  transition: all 0.2s;
}

.ai-panel.dark-mode .chat-input {
  background: #111827;
  border-color: #4b5563;
}

.chat-input:focus {
  border-color: #111827;
  box-shadow: 0 0 0 2px rgba(17, 24, 39, 0.08);
}

.ai-panel.dark-mode .chat-input:focus {
  border-color: #f3f4f6;
  box-shadow: 0 0 0 2px rgba(243, 244, 246, 0.08);
}

.send-btn {
  width: 2.4rem;
  height: 2.4rem;
  border-radius: 6px;
  border: none;
  background-color: #111827;
  color: #ffffff;
  display: grid;
  place-items: center;
  cursor: pointer;
  transition: opacity 0.2s;
}

.ai-panel.dark-mode .send-btn {
  background-color: #f3f4f6;
  color: #111827;
}

.send-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .ai-panel {
    width: 100%;
  }

  .panel-body {
    padding: 0.8rem 1rem 0;
  }

  .panel-footer {
    padding: 0.8rem 1rem 1rem;
  }
}
</style>
