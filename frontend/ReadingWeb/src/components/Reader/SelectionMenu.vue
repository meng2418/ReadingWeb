<!-- SelectionMenu.vue -->
<template>
  <div v-if="position" class="selection-menu-container" :style="menuStyle" @mousedown.prevent>
    <div class="menu-arrow"></div>

    <button
      v-for="action in menuActions"
      :key="action.id"
      @click.stop="() => onAction(action.id)"
      class="menu-item-btn"
    >
      <span class="menu-item-icon">
        <component :is="action.icon" :size="18" :strokeWidth="2" />
      </span>
      <span class="menu-item-label">{{ action.label }}</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Copy, Highlighter, Waves, Underline, MessageSquarePlus, Search, Trash2 } from 'lucide-vue-next'

interface Position {
  top: number
  left: number
}

interface Props {
  position: Position | null
  hasOverlap: boolean
  onAction: (action: string) => void
}

const props = defineProps<Props>()

// 基础操作项
const baseActions = [
  { id: 'copy', icon: Copy, label: '复制' },
  { id: 'marker', icon: Highlighter, label: '马克笔' },
  { id: 'wave', icon: Waves, label: '波浪线' },
  { id: 'line', icon: Underline, label: '直线' },
  { id: 'thought', icon: MessageSquarePlus, label: '写想法' },
  { id: 'ai', icon: Search, label: 'AI问书' },
]

// 删除操作项
const deleteAction = { id: 'delete', icon: Trash2, label: '删除划线' }

// 根据是否有重叠标注来构建菜单操作项
const menuActions = computed(() => {
  if (props.hasOverlap) {
    // 如果有重叠，在基础操作项前添加删除选项
    return [deleteAction, ...baseActions]
  }
  return baseActions
})

// 计算菜单的定位和动画样式
const menuStyle = computed(() => {
  if (!props.position) return {}

  return {
    top: `${props.position.top}px`,
    left: `${props.position.left}px`,
    // CSS 转换：水平居中 (-50%)，向上移动自身高度 (-100%)，再向上移动 14px 间距
    transform: 'translate(-50%, -100%) translateY(-14px)',
  }
})
</script>

<style scoped>
/* 菜单容器 */
.selection-menu-container {
  /* 定位和层级 */
  position: fixed;
  z-index: 100;

  /* 布局和外观 */
  display: flex;
  align-items: center;
  padding: 8px; /* px-2 py-2 */
  background-color: #27272a; /* bg-zinc-800 */
  color: white;
  border-radius: 8px; /* rounded-lg */
  box-shadow:
    0 20px 25px -5px rgba(0, 0, 0, 0.2),
    0 10px 10px -5px rgba(0, 0, 0, 0.1); /* shadow-2xl */

  /* 动画 (Vue transition 或使用 keyframes 模拟 animate-in fade-in zoom-in) */
  opacity: 0;
  animation: fadeInZoomIn 0.2s ease-out forwards;
}

@keyframes fadeInZoomIn {
  from {
    opacity: 0;
    transform: translate(-50%, -100%) translateY(-14px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translate(-50%, -100%) translateY(-14px) scale(1);
  }
}

/* 底部小箭头 */
.menu-arrow {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translate(-50%, 50%) rotate(45deg);
  width: 12px;
  height: 12px;
  background-color: #27272a; /* bg-zinc-800 */
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* 菜单项按钮 */
.menu-item-btn {
  /* 布局和间距 */
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px; /* gap-1 */
  padding: 4px 16px; /* px-4 py-1 */

  /* 外观 */
  border: none;
  background-color: transparent;
  cursor: pointer;
  border-radius: 6px; /* rounded-md */
  transition:
    background-color 0.2s ease,
    color 0.2s ease;

  /* 移除默认的 relative, 因为不需要 group hover */
  position: static;
}

.menu-item-btn:hover {
  background-color: #3f3f46; /* hover:bg-zinc-700 */
}

/* 菜单项图标 */
.menu-item-icon {
  color: #d4d4d8; /* text-gray-300 */
  transition: color 0.2s ease;
}

.menu-item-btn:hover .menu-item-icon {
  color: white;
}

/* 删除按钮的特殊样式 */
.menu-item-btn:first-child .menu-item-icon {
  color: #ef4444; /* 删除按钮使用红色 */
}

.menu-item-btn:first-child:hover .menu-item-icon {
  color: #fca5a5; /* 删除按钮hover时使用浅红色 */
}

.menu-item-btn:first-child .menu-item-label {
  color: #ef4444; /* 删除按钮标签使用红色 */
}

.menu-item-btn:first-child:hover .menu-item-label {
  color: #fca5a5; /* 删除按钮标签hover时使用浅红色 */
}

/* 菜单项标签 */
.menu-item-label {
  font-size: 10px; /* text-[10px] */
  font-weight: 500;
  color: #a1a1aa; /* text-gray-400 */
  transition: color 0.2s ease;
  white-space: nowrap;
}

.menu-item-btn:hover .menu-item-label {
  color: white;
}
</style>
