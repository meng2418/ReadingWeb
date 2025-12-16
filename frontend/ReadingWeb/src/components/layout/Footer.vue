<template>
  <footer class="minimal-footer">
    <div class="footer-links">
      <a href="#" class="footer-link">用户协议</a>
      <span class="link-separator">-</span>
      <a href="#" class="footer-link">隐私政策</a>
      <span class="link-separator">.</span>
      <a href="#" class="footer-link">帮助与反馈</a>
    </div>
    <div class="copyright">
      © 1998 - 2025 Tencent Inc. All Rights Reserved.
    </div>
  </footer>
</template>

<script setup lang="ts">
// 极简版页脚组件
</script>

<style scoped>
.minimal-footer {
  width: 100%;
  padding: 16px 0 24px 0;
  text-align: center;
  font-size: 12px;
  line-height: 1.5;
  background-color: transparent !important; /* 使用!important强制覆盖 */
  border: none !important; /* 确保没有边框 */
  box-shadow: none !important; /* 确保没有阴影 */
  margin: 0; /* 确保没有外边距 */
}
.footer-links {
  margin-bottom: 4px;
}

.footer-link {
  color: #888;
  text-decoration: none;
}

.footer-link:hover {
  color: #555;
}

.link-separator {
  margin: 0 4px;
  color: #ccc;
}

.copyright {
  color: #aaa;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .minimal-footer {
    padding: 12px 0 20px 0;
    font-size: 11px;
  }
}
</style>
