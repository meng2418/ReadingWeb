<template>
  <header class="navbar">
    <div class="nav-left">
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
      <!-- 使用 Element Plus 图标 -->
      <div class="search-container">
        <input type="text" placeholder="搜索书名、作者" class="search-input" />
        <el-icon class="search-icon">
          <Search />
        </el-icon>
      </div>
      <router-link to="/login">
        <button class="login-btn">登录</button>
      </router-link>
      <router-link to="/login?mode=signup">
        <button class="register-btn">注册</button>
      </router-link>
    </div>
  </header>
</template>

<script setup lang="ts">
import { Search } from '@element-plus/icons-vue'
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
</style>
