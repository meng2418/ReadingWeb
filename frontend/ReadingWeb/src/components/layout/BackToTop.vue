<template>
  <button
    class="back-to-top"
    :class="{ show: isVisible }"
    @click="scrollToTop"
    aria-label="回到顶部"
  >
    ↑
  </button>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

// 控制显示状态
const isVisible = ref(false)

// 监听滚动
const checkScroll = () => {
  isVisible.value = window.scrollY > 300
}

// 回到顶部
const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth',
  })
}

// 生命周期钩子
onMounted(() => {
  window.addEventListener('scroll', checkScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', checkScroll)
})
</script>

<style scoped>
.back-to-top {
  /* 基础定位与样式 */
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  /* 简化样式 */
  background: #007c27;
  color: white;
  border: none;
  border-radius: 90px;

  /* 交互样式 */
  cursor: pointer;
  font-size: 18px;

  /* 默认隐藏 */
  opacity: 0;
  visibility: hidden;
  transition: background 0.3s, transform 0.2s;
}
.back-to-top:hover{
  background: #1ad6a1;
  transform: scale(1.05);
}
/* 显示状态 */
.back-to-top.show {
  opacity: 1;
  visibility: visible;
}
</style>
