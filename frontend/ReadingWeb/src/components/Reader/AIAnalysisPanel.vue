<script setup lang="ts">
import { ref, watch } from 'vue'
import { Sparkles, X, Bot } from 'lucide-vue-next'

const props = defineProps<{
  isOpen: boolean
  selectedText: string
  isDarkMode: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const loading = ref(false)
const analysis = ref<string | null>(null)

watch([() => props.isOpen, () => props.selectedText], ([newOpen]) => {
  if (newOpen && props.selectedText) {
    loading.value = true
    analysis.value = null
    // Simulate API call
    setTimeout(() => {
      loading.value = false
      analysis.value = `Based on the selected text "${props.selectedText.substring(0, 20)}...", the author seems to be exploring themes of longing...`
    }, 1500)
  }
})
</script>

<template>
  <div v-if="isOpen">
    <div class="backdrop" @click="$emit('close')"></div>

    <div class="ai-panel" :class="{ 'dark-mode': isDarkMode }">
      <div class="panel-header">
        <div class="header-title">
          <Sparkles :size="16" class="sparkle-icon" />
          AI Analysis
        </div>
        <button @click="$emit('close')" class="close-btn">
          <X :size="16" />
        </button>
      </div>

      <div class="panel-body">
        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <p class="loading-text">Analyzing context...</p>
        </div>

        <div v-else class="content fade-in">
          <div class="analysis-row">
            <div class="bot-icon-wrapper">
              <Bot :size="16" class="bot-icon" />
            </div>
            <div class="analysis-bubble">
              {{ analysis }}
            </div>
          </div>
          <div class="actions-row">
            <button class="follow-up-btn">Ask follow-up question</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.backdrop {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(4px);
  z-index: 50;
}

.ai-panel {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: white;
  border-top: 1px solid #f3f4f6;
  color: #1f2937;
  z-index: 50;
  box-shadow: 0 -10px 15px -3px rgba(0, 0, 0, 0.1);
  border-top-left-radius: 1rem;
  border-top-right-radius: 1rem;
  transition: all 0.3s ease-out;
}

@media (min-width: 768px) {
  .ai-panel {
    left: auto;
    right: 2rem;
    bottom: 2rem;
    width: 24rem;
    border-radius: 1rem;
    border: 1px solid #f3f4f6;
  }
}

.ai-panel.dark-mode {
  background-color: #18181b;
  border-color: #3f3f46;
  color: #e5e7eb;
}

.panel-header {
  padding: 1rem;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.dark-mode .panel-header {
  border-color: #27272a;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 500;
  font-size: 0.875rem;
}

.sparkle-icon {
  color: #a855f7;
}

.close-btn {
  padding: 0.25rem;
  border-radius: 9999px;
  background: transparent;
  border: none;
  cursor: pointer;
  color: inherit;
}
.close-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
}
.dark-mode .close-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.panel-body {
  padding: 1.5rem;
  min-height: 200px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 2rem 0;
  gap: 1rem;
}

.spinner {
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  border: 2px solid #a855f7;
  border-top-color: transparent;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: 0.75rem;
  opacity: 0.5;
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 0.5;
  }
  50% {
    opacity: 0.2;
  }
}

.fade-in {
  animation: fadeIn 0.5s ease-out;
}
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.analysis-row {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.bot-icon-wrapper {
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  background-color: #f3e8ff; /* purple-100 */
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.dark-mode .bot-icon-wrapper {
  background-color: rgba(107, 33, 168, 0.3);
}

.bot-icon {
  color: #9333ea;
}
.dark-mode .bot-icon {
  color: #c084fc;
}

.analysis-bubble {
  padding: 0.75rem;
  border-radius: 1rem;
  border-top-left-radius: 0;
  font-size: 0.875rem;
  line-height: 1.6;
  background-color: #f9fafb;
}
.dark-mode .analysis-bubble {
  background-color: #27272a;
}

.actions-row {
  padding-left: 2.75rem;
}

.follow-up-btn {
  font-size: 0.75rem;
  color: #a855f7;
  font-weight: 500;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
}
.follow-up-btn:hover {
  color: #9333ea;
}
</style>
