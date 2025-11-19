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
/* 全局样式 */
</style>
