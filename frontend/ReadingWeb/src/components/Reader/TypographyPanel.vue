<template>
  <!-- 排版设置面板 -->
  <div class="typography-panel" :class="isDarkMode ? 'dark-mode' : ''">
    <h3 class="panel-title">Typography</h3>

    <!-- 字体大小设置 -->
    <div class="setting-group">
      <div class="setting-header">
        <span class="setting-label">Size</span>
        <span class="setting-value">{{ settings.fontSize }}px</span>
      </div>

      <div class="control-group">
        <button @click="adjustFontSize(-1)" class="control-btn">
          <span class="text-sm">A</span>
        </button>

        <button @click="adjustFontSize(1)" class="control-btn">
          <span class="text-lg">A</span>
        </button>
      </div>
    </div>

    <!-- 行高设置 -->
    <div class="setting-group">
      <div class="setting-header">
        <span class="setting-label">Spacing</span>
        <span class="setting-value">{{ settings.lineHeight }}</span>
      </div>

      <div class="control-group">
        <button @click="adjustLineHeight(-0.1)" class="control-btn">
          <AlignJustify :size="16" class="icon-narrow" />
        </button>

        <button @click="adjustLineHeight(0.1)" class="control-btn">
          <AlignJustify :size="16" class="icon-wide" />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { AlignJustify } from 'lucide-vue-next'
import type { TypographySettings } from './types'

interface Props {
  settings: TypographySettings
  updateSettings: (s: TypographySettings) => void
  isDarkMode: boolean
}

const props = defineProps<Props>()

const adjustFontSize = (delta: number) => {
  const newSize = Math.max(14, Math.min(32, props.settings.fontSize + delta))
  props.updateSettings({ ...props.settings, fontSize: newSize })
}

const adjustLineHeight = (delta: number) => {
  const newHeight = Math.round((props.settings.lineHeight + delta) * 10) / 10
  const limited = Math.max(1.2, Math.min(2.4, newHeight))
  props.updateSettings({ ...props.settings, lineHeight: limited })
}
</script>

<style scoped>
/* 排版设置面板容器 */
.typography-panel {
  position: absolute;
  right: 5rem; /* 对应right-20 */
  top: 50%;
  transform: translateY(-50%);
  width: 18rem; /* 对应w-72 */
  padding: 1.5rem; /* 对应p-6 */
  border-radius: 1rem; /* 对应rounded-2xl */
  box-shadow:
    0 10px 15px -3px rgba(0, 0, 0, 0.1),
    0 4px 6px -2px rgba(0, 0, 0, 0.05); /* 对应shadow-xl */
  z-index: 50;
  transform-origin: right;
  transition: all 200ms;
  border: 1px solid #f3f4f6; /* 浅色模式边框 */
  background-color: #ffffff; /* 浅色模式背景 */
  color: #1f2937; /* 浅色模式文字 */
}

/* 深色模式面板样式 */
.typography-panel.dark-mode {
  background-color: #27272a; /* 对应bg-zinc-800 */
  color: #e5e7eb; /* 对应text-gray-200 */
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.5); /* 对应shadow-black/50 */
  border-color: #3f3f46; /* 对应border-zinc-700 */
}

/* 面板标题 */
.panel-title {
  font-size: 0.75rem; /* 对应text-xs */
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.1em; /* 对应tracking-wider */
  margin-bottom: 1rem; /* 对应mb-4 */
  opacity: 0.5;
}

/* 设置项组 */
.setting-group {
  margin-bottom: 1.5rem; /* 对应mb-6 */
}

/* 最后一个设置项组取消底部外边距 */
.setting-group:last-child {
  margin-bottom: 0;
}

/* 设置项头部 */
.setting-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.5rem; /* 对应mb-2 */
}

/* 设置项标签 */
.setting-label {
  font-size: 0.875rem; /* 对应text-sm */
}

/* 设置项数值 */
.setting-value {
  font-size: 0.75rem; /* 对应text-xs */
  opacity: 0.5;
}

/* 控制按钮组 */
.control-group {
  display: flex;
  align-items: center;
  gap: 0.25rem; /* 对应gap-1 */
  padding: 0.25rem; /* 对应p-1 */
  border-radius: 0.5rem; /* 对应rounded-lg */
  background-color: #f3f4f6; /* 浅色模式背景 */
}

/* 深色模式控制按钮组背景 */
.typography-panel.dark-mode .control-group {
  background-color: #18181b; /* 对应bg-zinc-900 */
}

/* 控制按钮 */
.control-btn {
  flex: 1;
  padding: 0.5rem; /* 对应p-2 */
  border-radius: 0.375rem; /* 对应rounded-md */
  background-color: transparent;
  border: none;
  cursor: pointer;
  transition: background-color 200ms;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 浅色模式控制按钮hover */
.control-btn:hover {
  background-color: rgba(0, 0, 0, 0.05); /* 对应hover:bg-black/5 */
}

/* 深色模式控制按钮hover */
.typography-panel.dark-mode .control-btn:hover {
  background-color: rgba(255, 255, 255, 0.1); /* 对应dark:hover:bg-white/10 */
}

/* 控制按钮点击效果 */
.control-btn:active {
  transform: scale(0.95); /* 对应active:scale-95 */
}

/* 窄间距图标样式 */
.icon-narrow {
  transform: rotate(90deg); /* 对应rotate-90 */
}

/* 宽间距图标样式 */
.icon-wide {
  transform: rotate(90deg) scaleY(1.25); /* 对应rotate-90 scale-y-125 */
}
</style>
