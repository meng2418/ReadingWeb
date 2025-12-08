<script setup lang="ts">
import { X, Home, Book, Settings, User, HelpCircle } from 'lucide-vue-next'

defineProps<{
  isOpen: boolean
  isDarkMode: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const menuItems = [
  { icon: Home, label: 'Dashboard' },
  { icon: Book, label: 'My Library' },
  { icon: User, label: 'Profile' },
  { icon: Settings, label: 'Settings' },
  { icon: HelpCircle, label: 'Help & Support' },
]
</script>

<template>
  <div class="side-menu-wrapper">
    <div class="backdrop" :class="{ visible: isOpen }" @click="$emit('close')"></div>

    <div
      class="side-panel"
      :class="{
        open: isOpen,
        'dark-mode': isDarkMode,
      }"
    >
      <div class="panel-content">
        <div class="panel-header">
          <span class="brand">Zenith</span>
          <button @click="$emit('close')" class="close-btn">
            <X :size="20" />
          </button>
        </div>

        <nav class="nav-links">
          <a v-for="(item, idx) in menuItems" :key="idx" href="#" class="nav-item group">
            <span class="icon-wrapper">
              <component :is="item.icon" :size="20" />
            </span>
            <span class="label">{{ item.label }}</span>
          </a>
        </nav>

        <div class="panel-footer">
          <p>@ 2024 Zenith Reader</p>
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
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.3s;
}

.backdrop.visible {
  opacity: 1;
  pointer-events: auto;
}

.side-panel {
  position: fixed;
  top: 0;
  left: 0;
  height: 100%;
  width: 18rem; /* 72 */
  box-shadow: 10px 0 30px rgba(0, 0, 0, 0.1);
  z-index: 50;
  transform: translateX(-100%);
  transition: transform 0.3s ease-out;
  background-color: white;
  color: #1f2937;
}

.side-panel.open {
  transform: translateX(0);
}

.side-panel.dark-mode {
  background-color: #18181b;
  color: #e5e7eb;
}

.panel-content {
  padding: 2rem;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 3rem;
}

.brand {
  font-family: serif;
  font-size: 1.5rem;
  font-weight: bold;
  font-style: italic;
}

.close-btn {
  padding: 0.5rem;
  border-radius: 9999px;
  background: transparent;
  border: none;
  cursor: pointer;
  color: inherit;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
}
.dark-mode .close-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.nav-links {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.75rem;
  border-radius: 0.75rem;
  color: inherit;
  text-decoration: none;
  transition: background-color 0.2s;
}

.nav-item:hover {
  background-color: rgba(0, 0, 0, 0.05);
}
.dark-mode .nav-item:hover {
  background-color: rgba(255, 255, 255, 0.05);
}

.icon-wrapper {
  opacity: 0.5;
  transition: opacity 0.2s;
}

.nav-item:hover .icon-wrapper {
  opacity: 1;
}

.label {
  font-weight: 500;
}

.panel-footer {
  margin-top: auto;
  padding-top: 2rem;
  border-top: 1px solid #f3f4f6;
}
.dark-mode .panel-footer {
  border-color: #27272a;
}

.panel-footer p {
  font-size: 0.75rem;
  opacity: 0.4;
}
</style>
