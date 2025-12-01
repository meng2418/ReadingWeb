<template>
  <header :class="['top-bar', isDarkMode ? 'dark' : '']">
    <div class="nav-left">
      <div class="logo-container">
        <BookOpen :size="18" />
        <span>{{ title }}</span>
      </div>

      <button class="add-library-btn">
        <Library :size="12" />
        <span>加入书架</span>
      </button>
    </div>

    <div class="nav-right">
      <a href="#" class="nav-link">首页</a>
      <span class="divider"></span>
      <a href="#" class="nav-link">我的书架</a>
      <span class="divider"></span>
      <a href="#" class="nav-link">登录</a>
    </div>
  </header>
</template>

<script setup lang="ts">
import { BookOpen, Library } from 'lucide-vue-next'

interface Props {
  title: string
  isDarkMode: boolean
}

defineProps<Props>()
</script>

<style scoped>
/* 顶部信息条 —— 不再是导航栏，不 fixed */
.top-bar {
  width: 100%;
  max-width: 1400px; /* 和 content-card 对齐 */
  margin: 0 auto 0; /* 底部留一点间距给阅读区 */
  padding: 16px 4px 0 4px; /* 和阅读卡片左右内边距接近 */

  display: flex;
  align-items: center;
  justify-content: space-between;

  font-size: 0.875rem;
  color: inherit; /* 跟随 app-container 的浅/深色 */
  box-sizing: border-box;
}
/* 导航左侧区域 */
.nav-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

/* Logo容器 */
.logo-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 500;
}

/* 加入书架按钮（原样保留） */
.add-library-btn {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  border: 1px solid #d1d5db;
  font-size: 0.75rem;
  background-color: transparent;
  cursor: pointer;
  transition: all 300ms;
  color: inherit; /* 新增 */
}
.add-library-btn:hover {
  border-color: #9ca3af;
  background-color: #ffffff;
}
/* 导航右侧区域 */
.nav-right {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

/* 导航链接 */
.nav-link {
  color: inherit;
  text-decoration: none;
  transition: color 300ms;
}

.nav-link:hover {
  color: #111827;
}

/* 分隔符 */
.divider {
  width: 1px;
  height: 0.75rem;
  background-color: #d1d5db;
}
</style>
