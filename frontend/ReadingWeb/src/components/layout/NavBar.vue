<template>
  <header class="navbar">
    <div class="nav-left">
      <!-- 左侧导航内容保持不变 -->
      <h1 class="logo">微信读书</h1>
      <nav class="nav-links">
        <router-link to="/">
          <button class="nav-item">首页</button>
        </router-link>
        <router-link to="/category">
          <button class="nav-item">分类</button>
        </router-link>
        <router-link to="/bookshelf">
          <button class="nav-item">书架</button>
        </router-link>
        <router-link to="/community">
          <button class="nav-item">社区</button>
        </router-link>
      </nav>
    </div>

    <div class="nav-right">
      <!-- 搜索框保持不变 -->
      <div class="search-container">
        <input
          type="text"
          placeholder="搜索书名、作者"
          class="search-input"
          v-model="searchInput"
          @keyup.enter="handleSearch"
        />
        <el-icon class="search-icon" @click="handleSearch"> <Search /> </el-icon>
      </div>

      <!-- 根据登录状态显示不同内容 -->
      <template v-if="userStore.isLoggedIn">
        <!-- 登录状态：使用下拉菜单 -->
        <el-dropdown @command="handleCommand">
          <div class="user-dropdown-trigger">
            <img class="avatar" :src="userStore.userInfo.avatar" alt="用户头像" />
            <span class="username">{{ userStore.userInfo.name || '用户' }}</span>
            <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
          </div>

          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
      <template v-else>
        <!-- 未登录状态：显示登录/注册按钮 -->
        <router-link to="/login">
          <button class="login-btn">登录</button>
        </router-link>
        <router-link to="/login?mode=signup">
          <button class="register-btn">注册</button>
        </router-link>
      </template>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getProfileHome } from '@/api/profile' // 引入获取用户信息的接口
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, ArrowDown, User, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
// --- 新增：组件挂载时获取最新用户信息 ---
onMounted(async () => {
  // 如果已经登录，且当前 store 里没头像（或者想每次刷新都同步一下）
  if (userStore.isLoggedIn) {
    try {
      const profileData = await getProfileHome()
      userStore.userInfo = {
        ...userStore.userInfo,
        name: profileData.username, // 将接口的 username 映射到 store 的 name
        avatar: profileData.avatar,
      }
    } catch (error) {
      console.error('获取导航栏用户信息失败:', error)
    }
  }
})

const searchInput = ref('')

function handleSearch() {
  if (searchInput.value.trim() === '') {
    return
  }

  router.push({
    path: '/search',
    query: {
      q: searchInput.value,
    },
  })
}

// 下拉菜单命令处理
const handleCommand = async (command: string) => {
  switch (command) {
    case 'profile':
      // 跳转到个人中心
      router.push('/profile')
      break
    case 'logout':
      // 退出登录
      await handleLogout()
      break
  }
}

// 退出登录处理
const handleLogout = async () => {
  // 确认对话框
  await ElMessageBox.confirm('确定要退出登录吗？', '退出确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })

  // 调用退出登录方法
  userStore.logout()

  // 显示成功消息
  ElMessage.success('已退出登录')

  // 跳转到首页
  router.push('/')
}
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 64px;
  background-color: #fff;
  color: #000;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  z-index: 1000;
  box-sizing: border-box;
}

.logo {
  font-family: 'SimSun', '宋体', serif;
  font-size: 24px;
  margin-right: 40px;
  cursor: pointer;
  transition: color 0.2s ease;
}
.logo:hover {
  color: #888;
}

.nav-left {
  display: flex;
  align-items: center;
}

.nav-links {
  display: flex;
  gap: 42px;
}

.nav-item {
  text-decoration: none;
  color: #000;
  border: none;
  background-color: transparent;
  font-size: 18px;
  cursor: pointer;
  transition: color 0.2s;
}

.nav-item:hover {
  color: #555;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* 搜索容器样式 */
.search-container {
  position: relative;
  display: flex;
  align-items: center;
}

.search-input {
  border: 1px solid #ccc;
  border-radius: 20px;
  padding: 6px 12px 6px 18px;
  outline: none;
  width: 280px;
  transition: border-color 0.2s;
}

.search-input:focus {
  border-color: #888;
}

/* 搜索图标样式 */
.search-icon {
  position: absolute;
  right: 10px;
  font-size: 16px;
  cursor: pointer;
  color: #888;
}

.login-btn,
.register-btn {
  background: none;
  border: 1px solid #007c27;
  color: #000;
  padding: 6px 14px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.login-btn:hover,
.register-btn:hover {
  color: white;
  box-shadow: inset 0 -100px 0 0 #1ad6a1;
}

.login-btn:active,
.register-btn:active {
  transform: scale(0.9);
}

/* 用户下拉菜单触发器样式 */
.user-dropdown-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;

  /* 去掉默认焦点轮廓和阴影，避免出现黑色粗框 */
  outline: none;
  box-shadow: none;
}

/* hover 保持原来视觉，但确保不出现黑色框线 */
.user-dropdown-trigger:hover {
  border-color: #007c27;
  background-color: rgba(0, 124, 39, 0.05);
  box-shadow: none;
}

/* 明确禁止 focus 导致的黑框（一些浏览器会在 :focus 添加 UA 样式） */
.user-dropdown-trigger:focus,
.user-dropdown-trigger:active {
  outline: none;
  box-shadow: none;
}

/* 头像样式 */
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f0f0;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-icon {
  font-size: 12px;
  color: #666;
  transition: transform 0.2s ease;
}

/* 下拉菜单项样式 */
.el-dropdown-menu {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.el-dropdown-menu__item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  font-size: 14px;
}

.el-dropdown-menu__item .el-icon {
  font-size: 16px;
}

.el-dropdown-menu__item--divided {
  border-top: 1px solid #f0f0f0;
}

.el-dropdown-menu__item:focus:not(.is-disabled),
.el-dropdown-menu__item:hover:not(.is-disabled) {
  background-color: #f5f5f5;
  color: #007c27;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .navbar {
    padding: 0 20px;
  }

  .search-input {
    width: 180px;
  }

  .username {
    display: none;
  }

  .user-dropdown-trigger {
    padding: 4px 8px;
  }
}
</style>
