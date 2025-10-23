<template>
  <div id="app">
    <NavBar />
    <div class="book-list">
      <BookCardSuperBig
        :cover="coverImg3"
        title="百年孤独"
        author="加西亚·马尔克斯"
        :readers-count="873"
        :recommendation-rate="94.8"
        description="《百年孤独》是哥伦比亚作家加西亚·马尔克斯创作的长篇小说，是其代表作，也是拉丁美洲魔幻现实主义文学的代表作，被誉为'再现拉丁美洲历史社会图景的鸿篇巨著'。作品描写了布恩迪亚家族七代人的传奇故事，以及加勒比海沿岸小镇马孔多的百年兴衰，反映了拉丁美洲一个世纪以来风云变幻的历史。"
      />
    </div>

    <div class="components-grid">
      <!-- 用户信息卡片测试 -->
      <div class="component-section">
        <h2>用户信息卡片</h2>
        <UserProfileCard :user="testUser" />
      </div> <!-- 这里添加了闭合标签 -->

      <!-- 热门话题测试 - 使用自定义的测试数据 -->
      <div class="component-section">
        <h2>热门话题</h2>
        <HotTopics :topics="testTopics" @topic-click="handleTopicClick" />
      </div>


     <!-- 用户中心卡片测试 -->
  <div class="component-section full-width">
    <h2>用户中心卡片</h2>
    <UserCenterCard
      :user="centerCardUser"
      @edit-profile="handleEditProfile"
      @change-background="handleChangeBackground"
      @coin-recharge="handleCoinRecharge"
      @vip-purchase="handleVipPurchase"
      @follow-click="handleFollowClick"
      @fans-click="handleFansClick"
      @post-click="handlePostClick"

    />
  </div>

    </div> <!-- 这里添加了闭合标签 -->

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
  </div>
</template>

<script setup lang="ts">
import NavBar from '@/components/NavBar.vue'
import PostCard from '@/components/PostCard.vue'
import { ref } from 'vue'
import BookCardSuperBig from '@/components/BookCardSuperBig.vue'
import UserProfileCard from '@/components/UserProfileCard.vue'

// 导入本地图片
import localAvatar from '@/img/avatar.jpg'
import coverImg3 from '@/img/cover2.jpg'
import HotTopics from '@/components/HotTopics.vue'
import UserCenterCard from '@/components/UserCenterCard.vue'

// 测试用户数据
const testUser = ref({
  username: '读书大王',
  bio: '嗯，小生爱读书',
  avatar: localAvatar, // 使用导入的图片对象，而不是字符串路径
  followCount: 24,
  fansCount: 1890,
  postCount: 12
})

// 测试数据
const postData = ref({
  isFollowing: false,
  isLiked: false,
  likeCount: 5,
  commentCount: 3,
  shareCount: 1
})


// 用户中心卡片测试数据 - 更新为包含统计数据
const centerCardUser = ref({
  username: '书海漫游者',
  avatar: localAvatar,
  bio: '每天阅读一小时，让心灵去旅行。专注于文学小说和心理学书籍。',
  //level: 5,暂时不用等级吧，先删掉
  isOnline: true,
  followCount: 24,
  fansCount: 1890,
  postCount: 36,
  coins: 128
})


// 测试热门话题数据 - 使用真实的话题名称
const testTopics = ref([
  { id: 1, name: '小说推荐' },
  { id: 2, name: '读书笔记' },
  { id: 3, name: '书单分享' },
  { id: 4, name: '文学讨论' },
  { id: 5, name: '电子书' },
  { id: 6, name: '阅读打卡' },
  { id: 7, name: '作家访谈' },
  { id: 8, name: '新书速递' },
  { id: 9, name: '阅读方法' },
  { id: 10, name: '书籍评测' }
])


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

// 新的话题点击处理
const handleTopicClick = (topic: any) => {
  console.log('话题被点击:', topic)
  // 这里可以添加跳转到话题页面的逻辑
}

// 添加新的事件处理函数
const handleEditProfile = () => {
  console.log('编辑资料按钮点击')
  // 这里可以添加编辑资料逻辑
}

// 添加新的事件处理函数
const handleCoinRecharge = () => {
  console.log('充值币按钮点击')
  // 这里可以添加充值逻辑
}

const handleVipPurchase = () => {
  console.log('成为会员按钮点击')
  // 这里可以添加会员购买逻辑
}


const handleChangeBackground = () => {
  console.log('更换背景按钮点击')
  // 这里可以添加更换背景逻辑
}

const handleFollowClick = () => {
  console.log('关注按钮点击')
 // 这里可以添加查看关注列表逻辑
}

const handleFansClick = () => {
  console.log('粉丝按钮点击')
  // 这里可以添加查看粉丝列表逻辑
}


const handlePostClick = () => {
  console.log('发布按钮点击')
  // 这里可以添加查看发布内容逻辑
}




</script>

<style scoped>
.book-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
  padding-top: 100px; /* 增加顶部内边距 */
}

.components-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
  margin: 20px;
}

.component-section {
  background: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.component-section h2 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #333;
  font-size: 18px;
}
</style>
