<template>
  <header :class="['top-bar', isDarkMode ? 'dark' : '']">
    <div class="nav-left">
      <div class="logo-container">
        <BookOpen :size="18" />
        <span>{{ title }}</span>
      </div>

      <!-- 修改区域：绑定点击事件，动态绑定 class 和内容 -->
      <button class="add-library-btn" :class="{ 'is-added': isAdded }" @click="handleToggleLibrary">
        <!-- 动态切换图标：已加入显示 Check，未加入显示 Library -->
        <component :is="isAdded ? Check : Library" :size="12" />
        <span>{{ isAdded ? '已在书架' : '加入书架' }}</span>
      </button>
    </div>

    <div class="nav-right">
      <router-link to="/" class="nav-link">首页</router-link>
      <span class="divider"></span>
      <router-link to="/bookshelf" class="nav-link">我的书架</router-link>
      <span class="divider"></span>

      <template v-if="userStore.isLoggedIn">
        <router-link to="/profile" class="nav-link profile-link" title="个人中心">
          <img
            v-if="userStore.userInfo.avatar"
            :src="userStore.userInfo.avatar"
            alt="User"
            class="avatar-img"
          />
          <User v-else :size="20" />
        </router-link>
      </template>

      <template v-else>
        <router-link to="/login" class="nav-link">登录</router-link>
      </template>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref } from 'vue'
// 引入 Check 图标用于“已加入”状态
import { BookOpen, Library, User, Check } from 'lucide-vue-next'
import { useUserStore } from '@/stores/user'

interface Props {
  title: string
  isDarkMode: boolean
  // 可选：实际开发中可能需要传入 bookId
  // bookId?: string
}

defineProps<Props>()

const userStore = useUserStore()
// 测试数据
userStore.login('TestUser', 'https://picsum.photos/id/1027/200')

// --- 书架功能逻辑 ---
// 状态：是否已在书架（实际项目中这里应该根据 API 返回的初始化数据来设定）
const isAdded = ref(false)

// 模拟 API 调用函数
const mockApiRequest = (type: 'add' | 'remove') => {
  return new Promise<void>((resolve) => {
    console.log(`正在请求接口: ${type === 'add' ? '加入书架' : '移出书架'}...`)
    setTimeout(() => {
      console.log(`接口请求成功: ${type === 'add' ? '已加入' : '已移出'}`)
      resolve()
    }, 500) // 模拟 0.5秒 网络延迟
  })
}

const handleToggleLibrary = async () => {
  // 1. 检查登录状态 (可选)
  if (!userStore.isLoggedIn) {
    alert('请先登录')
    // router.push('/login')
    return
  }

  // 2. 如果已经在书架中，执行移除逻辑
  if (isAdded.value) {
    // 弹出确认框
    const confirmed = window.confirm('确定要将这本书移出书架吗？')
    if (confirmed) {
      await mockApiRequest('remove')
      isAdded.value = false
    }
  }
  // 3. 如果不在书架中，执行加入逻辑
  else {
    await mockApiRequest('add')
    isAdded.value = true
  }
}
</script>

<style scoped>
/* 顶部信息条 */
.top-bar {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto 0;
  padding: 16px 4px 0 4px;

  display: flex;
  align-items: center;
  justify-content: space-between;

  font-size: 0.875rem;
  color: inherit;
  box-sizing: border-box;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 500;
}

/* --- 按钮基础样式 --- */
.add-library-btn {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  border: 1px solid #d1d5db; /* 默认浅灰边框 */
  font-size: 0.75rem;
  background-color: transparent;
  cursor: pointer;
  transition: all 200ms ease-in-out;
  color: inherit;
  outline: none;
}

/* 悬停 (未加入时) */
.add-library-btn:hover {
  border-color: #9ca3af;
  background-color: rgba(0, 0, 0, 0.05); /* 淡淡的灰色背景 */
}
/* 夜间模式下的悬停 (未加入时) */
.dark .add-library-btn:hover {
  background-color: rgba(255, 255, 255, 0.1); /* 淡淡的白色透明背景 */
  border-color: #6b7280;
}

/* --- 已在书架状态 (Active State) --- */

/* 1. 日间模式下：深灰背景，白字 (保持不变或微调) */
.add-library-btn.is-added {
  background-color: #374151; /*这种深灰色比纯黑更柔和 */
  color: #ffffff;
  border-color: #374151;
}
.add-library-btn.is-added:hover {
  background-color: #1f2937; /* 悬停变更深 */
}

/* 2. 夜间模式下：中灰色背景，白字 (关键修改) */
/* 不再用白色背景，而是用比背景稍亮的灰色，既能区分状态，又不刺眼 */
.dark .add-library-btn.is-added {
  background-color: #4b5563; /* Cool Gray 600 */
  color: #f3f4f6; /* 接近纯白 */
  border-color: #4b5563;
}

.dark .add-library-btn.is-added:hover {
  background-color: #6b7280; /* 悬停变亮一点点 */
  border-color: #6b7280;
}

/* --- 导航栏其他样式保持不变 --- */
.nav-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.nav-link {
  color: inherit;
  text-decoration: none;
  transition: color 300ms;
  display: flex;
  align-items: center;
}

.nav-link:hover {
  color: #111827;
}

/* 夜间模式下链接 hover 颜色需要反转一下，否则看不清 */
.dark .nav-link:hover {
  color: #e5e7eb;
}

.avatar-img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #e5e7eb;
}
.dark .avatar-img {
  border-color: #e1e1e1; /* 夜间模式头像边框变深 */
}

.profile-link {
  cursor: pointer;
}

.divider {
  width: 1px;
  height: 0.75rem;
  background-color: #d1d5db;
}
.dark .divider {
  background-color: #4b5563;
}
</style>
