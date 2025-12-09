<script setup lang="ts">
import { AlignJustify } from 'lucide-vue-next'
import type { TypographySettings } from './types'

const props = defineProps<{
  settings: TypographySettings
  isDarkMode: boolean
}>()

const emit = defineEmits<(e: 'update', settings: TypographySettings) => void>()

const adjustFontSize = (delta: number) => {
  const newSize = Math.max(14, Math.min(32, props.settings.fontSize + delta))
  emit('update', { ...props.settings, fontSize: newSize })
}

const adjustLineHeight = (delta: number) => {
  const newHeight = Math.round((props.settings.lineHeight + delta) * 10) / 10
  emit('update', {
    ...props.settings,
    lineHeight: Math.max(1.2, Math.min(2.4, newHeight)),
  })
}
</script>

<template>
  <div class="typography-panel" :class="{ 'dark-mode': isDarkMode }">
    <h3 class="panel-title">字体</h3>

    <!-- Font Size -->
    <div class="control-group">
      <div class="control-header">
        <span class="label">大小</span>
        <span class="value">{{ settings.fontSize }}px</span>
      </div>
      <div class="button-group">
        <button @click="adjustFontSize(-1)" class="control-btn">
          <span class="text-sm">A</span>
        </button>
        <button @click="adjustFontSize(1)" class="control-btn">
          <span class="text-lg">A</span>
        </button>
      </div>
    </div>

    <!-- Line Height -->
    <div>
      <div class="control-header">
        <span class="label">间距</span>
        <span class="value">{{ settings.lineHeight }}</span>
      </div>
      <div class="button-group">
        <button @click="adjustLineHeight(-0.1)" class="control-btn center-content">
          <AlignJustify :size="16" class="rotate-90" />
        </button>
        <button @click="adjustLineHeight(0.1)" class="control-btn center-content">
          <AlignJustify :size="16" class="rotate-90 scale-y-125" />
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.typography-panel {
  position: absolute;
  right: 5rem;
  top: 50%;
  transform: translateY(-50%);
  width: 18rem; /* 72 */
  padding: 1.5rem;
  border-radius: 1rem;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
  z-index: 50;
  transition: all 0.2s;
  transform-origin: right;

  background-color: white;
  color: #1f2937;
  border: 1px solid #f3f4f6;
}

.typography-panel.dark-mode {
  background-color: #27272a; /* zinc-800 */
  color: #e5e7eb;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.5);
  border-color: #3f3f46;
}

.panel-title {
  font-size: 0.75rem;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.control-group {
  margin-bottom: 1.5rem;
}

.control-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.label {
  font-size: 0.875rem;
}
.value {
  font-size: 0.75rem;
  opacity: 0.5;
}

.button-group {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem;
  border-radius: 0.5rem;
  background-color: #f3f4f6;
}
.dark-mode .button-group {
  background-color: #18181b;
}

.control-btn {
  flex: 1;
  padding: 0.5rem;
  border-radius: 0.375rem;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s;
  color: inherit;
}
.control-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
}
.dark-mode .control-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}
.control-btn:active {
  transform: scale(0.95);
}

.center-content {
  display: flex;
  justify-content: center;
  align-items: center;
}

.rotate-90 {
  transform: rotate(90deg);
}
.scale-y-125 {
  transform: rotate(90deg) scaleY(1.25);
}
.text-sm {
  font-size: 0.875rem;
}
.text-lg {
  font-size: 1.125rem;
}
</style>
