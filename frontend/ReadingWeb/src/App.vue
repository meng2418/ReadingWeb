<script setup lang="ts">
import NavBar from './components/NavBar.vue'
import BookCard from './components/BookCardBig.vue'
import PostCard from './components/PostCard.vue'
import { ref } from 'vue'
import BookCardSuperBig from './components/BookCardSuperBig.vue'
// 导入本地头像图片
import localAvatar from './img/avatar.jpg'
import coverImg from '@/img/cover2.jpg';
// 测试数据
const postData = ref({
  isFollowing: false,
  isLiked: false,
  likeCount: 5,
  commentCount: 3,
  shareCount: 1
})

const handleLike = (newCount, isLiked) => {
  console.log('点赞状态:', isLiked, '点赞数:', newCount)
  postData.value.likeCount = newCount
  postData.value.isLiked = isLiked
}

const handleComment = () => {
  console.log('评论点击')
}

const handleShare = () => {
  console.log('分享点击')
}

const handleFollow = (isFollowing) => {
  console.log('关注状态:', isFollowing)
  postData.value.isFollowing = isFollowing
}
</script>

<template>
  <div id="app">
    <NavBar />
  </div>
  <div class="book-list">
    <BookCard
      cover="../src/img/cover.jpg"
      title="活着"
      author="余华"
      reason="一部关于生命与苦难的经典小说"
    />
    <BookCard cover="../src/img/cover.jpg" title="三体" author="刘慈欣" reason="硬核科幻巅峰之作" />
  <BookCardSuperBig
        :cover="coverImg"
        title="百年孤独"
        author="加西亚·马尔克斯"
        :readers-count="873"
        :recommendation-rate="94.8"
        description="《百年孤独》是哥伦比亚作家加西亚·马尔克斯创作的长篇小说，是其代表作，也是拉丁美洲魔幻现实主义文学的代表作，被誉为'再现拉丁美洲历史社会图景的鸿篇巨著'。作品描写了布恩迪亚家族七代人的传奇故事，以及加勒比海沿岸小镇马孔多的百年兴衰，反映了拉丁美洲一个世纪以来风云变幻的历史。"
  />
  </div>
<!-- PostCard测试区域 -->
    <div style="margin: 20px; padding: 20px; max-width: 1200px;">
      <h3 style="margin-bottom: 20px;">社区帖子预览</h3>
      <PostCard
        username="Ash"
        :avatar="localAvatar"
        post-time="2小时前"
        title="读小说的乐趣"
        content="花了4小时读完了马伯庸的新书《桃花源没事儿》，读完仔细一看竟然是今年6月份才出版，和之前读杨素秋的《世上为什么要有图书馆》在西西弗买完纸质书读完后大半年了，这本书才上微信推荐榜，心里隐隐有一种慧眼识好书且有实力先尝鲜的优越感。在读《桃花源没事儿》时，看到对一个佛系狮平编制老道围着一盆盆栽思考要不要把哪一枝突兀的树枝剪掉的心理描写，觉得写的好形象，公务员工作节奏维的完美叠加，内心小爽了一下。"
        :like-count="postData.likeCount"
        :comment-count="postData.commentCount"
        :share-count="postData.shareCount"
        :is-following="postData.isFollowing"
        :is-liked="postData.isLiked"
        @like="handleLike"
        @comment="handleComment"
        @share="handleShare"
        @follow="handleFollow"
      />
    </div>
</template>

<style scoped>
#app {
  font-family: 'Helvetica Neue', Arial, sans-serif;
  padding: 16px;
}
.book-list {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  padding: 100px;
}
</style>
