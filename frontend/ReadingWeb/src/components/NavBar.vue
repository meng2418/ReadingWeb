<template>
  <header class="navbar">
    <div class="nav-left">
      <h1 class="logo">微信读书</h1>
      <nav class="nav-links">
        <a href="#" class="nav-item">首页</a>
        <a href="#" class="nav-item">分类</a>
        <a href="#" class="nav-item">书架</a>
        <a href="#" class="nav-item">社区</a>
      </nav>
    </div>

    <div class="nav-right">
      <!-- 使用 Element Plus 图标 -->
      <div class="search-container">
        <input type="text" placeholder="搜索书名、作者" class="search-input" />
        <!-- 使用 <el-icon> 和 Search 图标 -->
        <el-icon class="search-icon">
          <Search />
        </el-icon>
      </div>
      <button class="login-btn">登录</button>
      <button class="register-btn">注册</button>
    </div>
  </header>
</template>

<script setup></script>

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
  font-size: 18px;
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
  padding: 6px 12px 6px 36px;
  outline: none;
  width: 240px;
  transition: border-color 0.2s;
}

.search-input:focus {
  border-color: #888;
}

/* 搜索图标样式 */
.search-icon {
  position: absolute;
  left: 12px;
  color: #888;
  font-size: 16px;
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
