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
        <input type="text" placeholder="搜索书名、作者" class="search-input" />
        <el-icon class="search-icon">
          <Search />
        </el-icon>
      </div>

      <!-- 根据登录状态显示不同内容 -->
      <template v-if="userStore.isLoggedIn">
        <!-- 登录状态：显示个人中心和退出按钮 -->
        <router-link to="/profile" class="user-center">
          <div class="user-profile">
            <img class="avatar" :src="userStore.userInfo.avatar || defaultAvatar" alt="用户头像" />
            <span class="profile-text">个人中心</span>
          </div>
        </router-link>
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
import { Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user.js'
const userStore = useUserStore()
const defaultAvatar = 'https://picsum.photos/id/1027/200'

// 退出登录处理
const handleLogout = () => {
  userStore.logout()
  // 可添加路由跳转，例如回到首页
  // router.push('/')
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

.user-center {
  text-decoration: none;
}

/* 核心：个人中心按钮（收缩/展开容器） */
.user-profile {
  border: 1px solid var(--primary-green);
  display: flex;
  align-items: center;
  justify-content: flex-start; /* 改为左对齐 */
  width: 40px; /* 初始宽度=头像宽度，只显示头像 */
  height: 40px;
  overflow: hidden; /* 隐藏超出容器的文字 */
  background: var(--sun-back);
  border-radius: 50px; /* 圆角和你示例一致 */
  cursor: pointer;
  transition: all 0.3s ease-out; /* 过渡和你示例一致 */
}

/* 头像样式 */
.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  transition: all 0.3s ease-out;
}
/* 个人中心文字（默认隐藏） */
.profile-text {
  opacity: 0;
  width: 0;
  margin-left: 0;
  overflow: hidden;
  transition:
    width 0.3s ease-out,
    background-color 0.4s ease,
    box-shadow 0.4s ease;
  font-size: 14px;
  font-family: 'SimSun', '宋体', serif;
  font-weight: 600;
  color: #ffffff; /* 文字白色，和hover背景对比 */
  white-space: nowrap; /* 防止文字换行 */
}

/* Hover展开效果 */
.user-profile:hover {
  width: 120px; /* 展开后的宽度，刚好容纳头像+文字 */
  padding: 0px 12px 0px 0px; /* 左右内边距，避免文字贴边 */
  box-shadow: 0px 6px 12px var(--shadow-green); /* 主题色阴影 */
  background-color: var(--secondary-green); /* 主题色背景，和你之前按钮呼应 */
}

/* Hover时文字显示 */
.user-profile:hover .profile-text {
  opacity: 1;
  width: auto;
  margin-left: 12px; /* 文字和头像的间距 */
}
</style>
