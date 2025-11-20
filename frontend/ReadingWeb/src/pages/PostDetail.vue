<template>
  <div class="post-detail-container">
    <!-- 左侧主内容区 -->
    <div class="left-column">
      <!-- 新增：返回按钮 + 标题 组合 -->
      <div class="post-header">
        <button class="back-btn" @click="handleBack">← 返回社区</button>
      </div>
      <!-- 帖子标题 -->
      <h1 class="post-title">不是她的故事，是她们的故事</h1>

      <!-- 发布者信息 -->
      <div class="publisher-info">
        <img src="@/img/avatar.jpg" alt="用户头像" class="publisher-avatar" />
        <div class="publisher-meta">
          <div class="publisher-top">
            <span class="nickname">Nana</span>
          </div>
          <div class="publisher-bottom">
            <span>2024-07-25 15:46:39</span>
            <span>已编辑</span>
            <span>上海</span>
          </div>
        </div>
      </div>

      <!-- 剧透提示 -->
      <div class="spoiler-tip">这篇影评可能有剧透</div>

      <!-- 帖子正文 -->
      <div class="post-content">
        <p>这个电影前9/10的部分，看了都让人窝火，特别是女性观众来看这个电影的时候</p>
        <p>前面可能会气竭，并且看的时候伴随着压抑和恶心和窒息的感觉 因为真的代入感太强</p>
        <p>
          前面一直在描述母亲忙碌的家庭生活，宛如一个被上了发条的陀螺。马不停蹄的做家务，工作。被冷漠的丈夫暴力和埋怨。她一边工作一遍照顾三个孩子和自己的公公，连件衣服自己都不舍得买。家里严重的重男轻女，丈夫和公公都急切的想把她的女儿嫁出去换个好价钱。
        </p>
        <p>这一切都不陌生，不因为国籍和语言的改变有啥不同。</p>
        <p>电影最后的10分钟才是点睛之笔，结局的篇幅虽然短，但是非常重要。</p>
        <p>我们就倒过来先看这个结局，再解析前面的剧情。</p>
        <p>
          很多人可能没有看懂，认为女主角没有真的解决问题的根本，即使拥有了投票权，回到家仍然无法摆脱被家暴的命运，我们来看真的是这样吗？
        </p>
        <p>
          刚才说了片尾的剧情短但是十分重要，这一幕这是全片的高潮，大家注意看，女主投票结束之后，她在楼下发现了自己的丈夫，她条件反射的要去躲避丈夫，但是她突然想到了什么一样，转过了头，电影出现了十分蒙太奇的一幕，所有人都闭着嘴看着男主，直勾勾的盯着他，并没有说话，但是张力十足。
        </p>
      </div>

      <!-- 话题标签 -->
      <div class="topic-tag">#还有明天</div>

      <!-- 评论区组件（预留） -->
      <CommentSection />
    </div>

    <!-- 右侧侧边栏 -->
    <div class="right-column">
      <!-- 相关书籍组件（预留） -->
      <PostBook />
    </div>
  </div>
</template>

<script setup>
// 引入子组件
import CommentSection from '@/components/community/CommentSection.vue'
import PostBook from '@/components/community/PostBook.vue'
import router from '@/router'

// 新增：返回社区页逻辑
const handleBack = () => {
  // 两种方式可选，推荐第一种（返回上一页，保留历史记录）
  if (window.history.length > 1) {
    router.back() // 有历史记录则返回上一页（即社区页）
  } else {
    router.push('/') // 无历史记录则直接跳转到社区页
  }
}
</script>

<style scoped>
/* 整体容器 */
.post-detail-container {
  max-width: 1200px;
  margin: 20px auto;
  display: flex;
  gap: 30px;
  padding: 0 20px;
}

/* 左侧栏（占比更大） */
.left-column {
  flex: 3;
}

/* 右侧栏（占比更小） */
.right-column {
  flex: 1;
  min-width: 220px;
}
/* 新增：返回按钮 + 标题 样式 */

.back-btn {
  padding: 6px 12px;
  background: #f5f5f5;
  color: #333;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s;
}

.back-btn:hover {
  background: #eee;
  color: #409eff;
}
/* 帖子标题 */
.post-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}

/* 发布者信息 */
.publisher-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}
.publisher-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}
.publisher-meta {
  flex: 1;
}
.publisher-top {
  display: flex;
  align-items: center;
  gap: 8px;
}
.nickname {
  font-weight: 500;
}
.publisher-bottom {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  display: flex;
  gap: 8px;
}

/* 帖子正文 */
.post-content {
  line-height: 1.8;
  color: #333;
  margin-bottom: 24px;
}
.post-content p {
  margin-bottom: 12px;
}

/* 话题标签 */
.topic-tag {
  font-size: 14px;
  color: #409eff;
  margin-bottom: 32px;
}
</style>
