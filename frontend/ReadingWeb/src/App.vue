<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'



const userStore = useUserStore()
onMounted(() => {
  userStore.restoreSession()
})
</script>

<template>
  <router-view></router-view>
</template>

<style>
/* 设置全局背景色 */
html, body {
  background-color: #f5f5f5;
  margin: 0;
  padding: 0;
}

/* 或者给根元素设置背景 */
#app {
  background-color: #f5f5f5;
  min-height: 100vh;
}
</style>
