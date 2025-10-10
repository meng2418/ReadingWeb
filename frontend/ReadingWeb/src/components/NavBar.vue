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
      <input type="text" placeholder="搜索书名、作者" class="search-input" />
      <button class="login-btn">登录</button>
      <button class="register-btn">注册</button>
    </div>
  </header>
</template>

<script setup>
// 这里只是静态布局，不需要逻辑
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
  padding: 0 32px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  z-index: 1000; /* 确保不被覆盖 */
  box-sizing: border-box;
}

.logo {
  font-family: 'SimSun', '宋体', serif;
  font-size: 22px;
  margin-right: 32px;
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
  gap: 24px;
}

.nav-item {
  text-decoration: none;
  color: #000;
  font-size: 16px;
  transition: color 0.2s;
}

.nav-item:hover {
  color: #555;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-input {
  border: 1px solid #ccc;
  border-radius: 20px;
  padding: 6px 12px;
  outline: none;
  width: 180px;
  transition: border-color 0.2s;
}

.search-input:focus {
  border-color: #888;
}

.login-btn,
.register-btn {
  background: none;
  border: 1px solid #000;
  color: #000;
  padding: 6px 14px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.login-btn:hover,
.register-btn:hover {
  background-color: #f5f5f5;
}
</style>
