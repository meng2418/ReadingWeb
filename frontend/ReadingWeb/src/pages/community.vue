<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import NavBar from '@/components/layout/NavBar.vue'
import PostCard from '@/components/post/PostCard.vue'
import UserProfileCard from '@/components/post/UserProfileCard.vue'
import HotTopics from '@/components/post/HotTopics.vue'
import BackToTop from '@/components/layout/BackToTop.vue'
import Topic from '@/components/Topic.vue'
import avatarImg from '@/img/avatar.jpg'
import FloatingAddButton from '@/components/post/FloatingAddButton.vue'

// 当前用户信息
const currentUser = reactive({
  username: '阅读爱好者',
  bio: '热爱阅读，分享好书',
  avatar: avatarImg,
  followCount: 128,
  fansCount: 2560,
  postCount: 45
})

// 热门话题
const hotTopics = ref([
  { id: 1, name: '# 好书推荐' },
  { id: 2, name: '# 阅读心得' },
  { id: 3, name: '# 文学讨论' },
  { id: 4, name: '# 经典名著' },
  { id: 5, name: '# 新书速递' },
  { id: 6, name: '# 读书笔记' },
  { id: 7, name: '# 作者访谈' },
  { id: 8, name: '# 阅读挑战' },
  { id: 9, name: '# 书单分享' },
])
const topicsList = ref([
  { id: 1, cover: 'https://picsum.photos/200?random=1', title: '每日读点小说', number: 200 },
  { id: 2, cover: 'https://picsum.photos/200?random=2', title: '科幻爱好者', number: 156 },
  { id: 3, cover: 'https://picsum.photos/200?random=3', title: '经典文学', number: 320 },
  { id: 4, cover: 'https://picsum.photos/200?random=4', title: '读书笔记精选', number: 187 },
  { id: 5, cover: 'https://picsum.photos/200?random=5', title: '外国名著', number: 98 },
  { id: 6, cover: 'https://picsum.photos/200?random=6', title: '推理与悬疑', number: 240 },
  { id: 7, cover: 'https://picsum.photos/200?random=7', title: '诗歌与散文', number: 142 },
  { id: 8, cover: 'https://picsum.photos/200?random=8', title: '新书速递', number: 75 },
  { id: 9, cover: 'https://picsum.photos/200?random=9', title: '阅读打卡挑战', number: 310 }
])

// 帖子数据
const posts = ref([
  {
    id: 1,
    username: '书虫小王',
    avatar: avatarImg,
    postTime: '2小时前',
    title: '《百年孤独》读后感',
    content: '刚刚读完马尔克斯的《百年孤独》，这本书真的是一种奇妙的阅读体验。书中通过布恩迪亚家族几代人的命运，展现了时间的循环与宿命的荒诞。每个人都在追寻意义，但又被历史的轮回所吞没。尤其是书中的文字节奏，那种冷静而又充满诗意的叙述，让人不自觉地沉浸进去。读到最后，我甚至分不清哪些是真实，哪些是幻觉。魔幻与现实在这里不再有界限，而人的孤独似乎是永恒的。推荐每一个喜欢文学的人都读一读这本书。',
    likeCount: 128,
    commentCount: 23,
    shareCount: 8,
    isFollowing: false,
    isLiked: false,
    book: {
      id: 101,
      title: '百年孤独',
      author: '加西亚·马尔克斯',
      cover: 'https://picsum.photos/100/150?random=1',
    },
  },
  {
    id: 2,
    username: '文学青年',
    avatar: avatarImg,
    postTime: '4小时前',
    title: '推荐几本值得一读的科幻小说',
    content: '最近迷上了科幻小说，推荐《三体》《银河系漫游指南》《基地》三部曲。这几本作品不仅有宏大的世界观，更在科幻设定中探讨了人性与社会的本质。特别是《三体》中对宇宙文明的思考，完全刷新了我的认知维度。',
    likeCount: 89,
    commentCount: 15,
    shareCount: 12,
    isFollowing: true,
    isLiked: true,
    book: null,
  },
  {
    id: 3,
    username: '历史爱好者',
    avatar: avatarImg,
    postTime: '昨天 19:30',
    title: '读《万历十五年》有感',
    content: '黄仁宇先生的《万历十五年》用一种独特的视角解读了明朝的衰落。看似平淡的一年，却隐藏着帝国崩溃的伏笔。书中没有激烈的戏剧冲突，却通过几个关键人物的命运，展现了制度的僵化如何吞噬个人的努力。这种"大历史观"的写法，让我对历史有了新的理解。',
    likeCount: 205,
    commentCount: 37,
    shareCount: 24,
    isFollowing: true,
    isLiked: false,
    book: {
      id: 102,
      title: '万历十五年',
      author: '黄仁宇',
      cover: 'https://picsum.photos/100/150?random=2',
    },
  },
  {
    id: 4,
    username: '职场新人',
    avatar: avatarImg,
    postTime: '3天前',
    title: '《非暴力沟通》改变了我的职场关系',
    content: '入职半年一直苦于和同事沟通不畅，直到读了《非暴力沟通》。书中提出的"观察-感受-需要-请求"四步法，让我学会了如何真诚地表达自己，同时理解他人的需求。现在团队协作顺畅多了，推荐给所有职场人！',
    likeCount: 156,
    commentCount: 42,
    shareCount: 31,
    isFollowing: false,
    isLiked: true,
    book: {
      id: 103,
      title: '非暴力沟通',
      author: '马歇尔·卢森堡',
      cover: 'https://picsum.photos/100/150?random=3',
    },
  },
  {
    id: 5,
    username: '旅行日记',
    avatar: avatarImg,
    postTime: '1周前',
    title: '在书店旅行的日子',
    content: '这半年走访了12座城市的独立书店，每一家都有自己的灵魂。从北京的万圣书园到上海的季风书园，从南京的先锋书店到广州的方所，这些地方不仅是卖书的场所，更是城市文化的灯塔。遇到了很多有趣的店主和读者，听到了许多关于书的故事。',
    likeCount: 289,
    commentCount: 56,
    shareCount: 78,
    isFollowing: true,
    isLiked: false,
    book: null,
  },
  {
    id: 6,
    username: '哲学思考者',
    avatar: avatarImg,
    postTime: '1周前',
    title: '柏拉图《理想国》中的正义观',
    content: '重读《理想国》，依然被柏拉图的深刻思考所震撼。他通过苏格拉底与他人的对话，探讨了正义的本质。在当下这个价值观多元的时代，思考"什么是正义"显得尤为重要。书中关于洞穴寓言的描写，是否暗示着我们每个人都可能活在自己的认知局限中？',
    likeCount: 98,
    commentCount: 29,
    shareCount: 15,
    isFollowing: false,
    isLiked: false,
    book: {
      id: 104,
      title: '理想国',
      author: '柏拉图',
      cover: 'https://picsum.photos/100/150?random=4',
    },
  },
])

const currentTab = ref<'square' | 'following' | 'topics' | 'mine'>('square')
const changeTab = (tab: 'square' | 'following' | 'topics' | 'mine') => currentTab.value = tab

const filteredPosts = computed(() => {
  switch (currentTab.value) {
    case 'following': return posts.value.filter(p => p.isFollowing)
    case 'mine': return posts.value.filter(p => p.username === currentUser.username)
    default: return posts.value
  }
})

const handleTopicClick = (topic: any) => {
  console.log('点击话题:', topic.name)
}
</script>

<template>
  <div class="community">
    <NavBar />
    <BackToTop />
    <FloatingAddButton />
    <div class="community-content">
      <div class="tabs">
        <button :class="{ active: currentTab === 'square' }" @click="changeTab('square')">广场</button>
        <button :class="{ active: currentTab === 'following' }" @click="changeTab('following')">关注</button>
        <button :class="{ active: currentTab === 'topics' }" @click="changeTab('topics')">话题</button>
        <button :class="{ active: currentTab === 'mine' }" @click="changeTab('mine')">我的</button>
      </div>

      <!-- main-content部分 -->
      <div class="main-content">
        <div v-if="currentTab === 'topics'" class="topics-grid">
          <Topic
            v-for="topic in topicsList"
            :key="topic.id"
            :cover="topic.cover"
            :title="topic.title"
            :number="topic.number"
          />
        </div>

        <div v-else class="posts-list">
          <PostCard
            v-for="post in filteredPosts"
            :key="post.id"
            v-bind="post"
          />
          <div v-if="filteredPosts.length === 0" class="empty">暂无内容</div>
        </div>
      </div>


      <div class="sidebar">
        <UserProfileCard :user="currentUser" />
        <HotTopics :topics="hotTopics" @topic-click="handleTopicClick" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.community {
  display: grid;
  background-color: rgb(241, 241, 241);
  gap: 24px;
  min-height: 100vh;
}

.community-content {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 24px;
  padding: 80px 100px 100px;
  max-width: 1400px;
  margin: 0 auto;
  align-items: flex-start;
}

/* tabs */
.tabs {
  grid-column: 1 / -1;
  display: flex;
  gap: 12px;
}
.tabs button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 20px;
  color: #888;
}
.tabs button.active {
  font-weight: 600;
  color: #333;
}

.main-content {
  width: 800px;
}

.sidebar {
  width: 350px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.sidebar-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #f0f0f0;
}

.topics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
  justify-items: center;
}

.empty {
  padding: 24px;
  text-align: center;
  color: #888;
}
</style>
