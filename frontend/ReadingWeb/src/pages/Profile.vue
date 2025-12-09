<!-- Profile.vue -->
<template>
  <NavBar />
  <div class="profile-page">
    <section class="header-section">
      <UserProfile />
    </section>

    <section class="main-section">
      <aside class="left-sidebar">
        <SidebarRankings />
      </aside>

      <main class="right-dashboard">
        <ReadingDashboard :initialTab="$route.query.tab" />
        <ReadingNotes />
        <ReadingThoughts />
        <ReadingReviews />
      </main>
    </section>
  </div>
</template>

<script setup>
import NavBar from '@/components/layout/NavBar.vue'
import UserProfile from '@/components/user/UserProfile.vue'
import SidebarRankings from '@/components/user/SidebarRankings.vue'
import ReadingDashboard from '@/components/user/ReadingDashboard.vue'
import ReadingNotes from '@/components/user/ReadingNotes.vue'
import ReadingThoughts from '@/components/user/ReadingThoughts.vue'
import ReadingReviews from '@/components/user/ReadingReviews.vue'
</script>

<style scoped>
/* 定义页面级 CSS 变量 
  如果你的项目中已有全局样式文件(如 global.css)，可以将 :root 部分移过去
*/

/* 限制内容最大宽度，保持大屏美观 */
.profile-page > * {
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

/* 顶部区域间距 */
.header-section {
  margin-top: 60px;
  margin-bottom: 20px;
}

/* Grid 布局：左侧定宽，右侧自适应 */
.main-section {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 20px;
  align-items: start; /* 防止侧边栏被拉伸 */
}
.right-dashboard {
  /* 🔥 关键代码：防止 grid 子元素被内部宽内容撑开 */
  min-width: 0;
  /* 或者使用 overflow: hidden; 但 min-width: 0 更推荐 */
}
/* 响应式处理 */
@media (max-width: 900px) {
  .main-section {
    grid-template-columns: 1fr; /* 平板/手机端变为单列 */
  }

  .left-sidebar {
    order: 2; /* 移动端将排行榜放到下面，如果需要的话 */
  }

  .right-dashboard {
    order: 1;
  }
}
</style>
