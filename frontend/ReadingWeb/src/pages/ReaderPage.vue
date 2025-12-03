<!-- ReaderPage.vue -->
<template>
  <div :class="['app-container', isDarkMode ? 'dark-mode' : '']">
    <TopNavigation title="她既想死，又想去巴黎" :isDarkMode="isDarkMode" />

    <!-- 侧边面板组件 -->
    <NotesPanel
      :isOpen="activePanel === 'notes'"
      :onClose="closePanels"
      :notes="mockNotes"
      :isDarkMode="isDarkMode"
    />

    <TableOfContents
      :isOpen="activePanel === 'toc'"
      :onClose="closePanels"
      :chapters="chapters"
      :currentChapterId="currentChapterId"
      :onSelectChapter="handleChapterSelect"
      :isDarkMode="isDarkMode"
    />

    <!-- 主要内容区域 -->
    <div class="content-wrapper">
      <!-- 
        核心修改：ReaderViewport
        1. 设定固定高度 (calc(100vh - 120px))
        2. 设定最大宽度
        3. 内部元素没有任何 padding，全部交给 ReaderContent 处理
      -->
      <div class="reader-viewport">
        <ReaderContent
          class="reader-content-root"
          :pageData="samplePageData"
          :isDarkMode="isDarkMode"
          :typography="typography"
          :annotationMode="isAnnotationMode"
          :showThoughts="showThoughts"
          :readingMode="readingMode"
          @onActiveThought="handleActiveThought"
          :onActiveThought="handleActiveThought"
          :onTextSelection="() => {}"
        />
      </div>

      <!-- 浮动菜单 -->
      <FloatingMenu
        :isDarkMode="isDarkMode"
        :toggleTheme="toggleTheme"
        :onToggleTOC="() => togglePanel('toc')"
        :onToggleTypography="() => togglePanel('typography')"
        :onToggleAnnotation="() => togglePanel('notes')"
        :onToggleThoughts="handleToggleThoughts"
        :onToggleReadingMode="handleToggleReadingMode"
        :activePanel="activePanel"
        :isAnnotationMode="activePanel === 'notes'"
        :showThoughts="showThoughts"
        :readingMode="readingMode"
      />

      <!-- 字体设置面板 -->
      <TypographyPanel
        v-if="activePanel === 'typography'"
        :settings="typography"
        :updateSettings="updateTypography"
        :isDarkMode="isDarkMode"
      />

      <!-- 想法气泡 -->
      <ThoughtsBubble
        :isOpen="!!activeThought"
        :onClose="() => (activeThought = null)"
        :comments="mockComments"
        :isDarkMode="isDarkMode"
        :quoteText="activeThought?.text || ''"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// 组件路径 - 请根据实际项目路径确认
import TopNavigation from '@/components/Reader/TopNavigation.vue'
import FloatingMenu from '@/components/Reader/FloatingMenu.vue'
import ReaderContent from '@/components/Reader/ReaderContent.vue'
import TypographyPanel from '@/components/Reader/TypographyPanel.vue'
import TableOfContents from '@/components/Reader/TableOfContents.vue'
import NotesPanel from '@/components/Reader/NotesPanel.vue'
import ThoughtsBubble from '@/components/Reader/ThoughtsBubble.vue'

type ReadingMode = 'paged' | 'scroll'

// --------------------------
// 示例数据
// --------------------------
const samplePageData = {
  chapter: '译者序：爱与艺术的交响乐',
  content: [
    '贝尔特 · 莫里索绘',
    '你本应拥有另一种命运，值得更优秀的人、更纯粹的爱。我竭尽所能，想要向你证明我的爱意。可你渴望的，恰好是我唯一无法给予的。',
    'Préface',
    '1846年7月29日，巴黎一位著名雕塑家詹姆斯 · 普拉迪埃的工作室里，二十五岁的古斯塔夫 · 福楼拜与三十六岁的路易丝 · 科莱初次相逢。这并非一场偶然的邂逅，而是命运的交会。当时，福楼拜尚未崭露头角，1844年他因神经疾病放弃了法律学业，回到故乡克鲁瓦塞过着隐居般的生活，依靠家族遗产与母亲相伴；路易丝已是巴黎文学圈中的一颗明星，她以诗人和作家的身份活跃于沙龙，作品多次获得法兰西学院的嘉奖。她嫁给了音乐家伊波利特 · 科莱，但婚姻并未束缚她的情感与才华，她与哲学家维克托 · 库森等名流有着剪不断、理还乱的关系。',
    '初见时，路易丝的美貌与活力令福楼拜心动。他后来在信中写道："你拥有能让人死而复生般的丰沛爱意。"她的金发、白皙的肌肤与自信的气质，与福楼拜日后笔下塑造的爱玛 · 包法利有着惊人的相似之处，而福楼拜的高大身材、深邃的眼神与文学热情，也吸引了这位比他年长十一岁的女性。他们迅速坠入爱河，开始了一段持续八年的激情与矛盾交织的恋情。这段关系不仅改变了他们的生活轨迹，也为文学史留下了宝贵的书信遗产。',
    '恋情伊始，福楼拜与路易丝的交流主要通过书信完成。由于他不愿离开克鲁瓦塞，声称要陪伴年迈的母亲，他们的见面机会极为有限。1846年至1848年的两年间，他们仅在巴黎或芒特拉若利见到了六次。然而，距离并未冷却他们的热情，反而点燃了书信中的火焰。福楼拜的信件充满了浪漫的想象与炽烈的表白，如1846年8月14日的信中写道："我要让你沉溺在爱欲的沉醉之中……想要你在垂暮之年，回忆起这些短暂的时光时，干枯的骨骼仍能为之战栗，为之欣喜若狂。"这样的文字既展现了他对路易丝的渴望，也透露出一种将爱情艺术化、理想化的倾向。对于福楼拜而言，路易丝不仅是恋人，更是他文学创作的灵感缪斯与倾诉对象。',
  ],
}

const chapters = [
  { id: '1', title: '译者序：爱与艺术的交响乐', page: 1 },
  { id: '2', title: '第一章：初遇', page: 5 },
  { id: '3', title: '第二章：巴黎的诱惑', page: 12 },
  { id: '4', title: '第三章：书信与誓言', page: 28 },
  { id: '5', title: '第四章：别离之苦', page: 45 },
]

const mockNotes = [
  {
    id: 'n1',
    chapterId: '1',
    quote: '你本应拥有另一种命运，值得更优秀的人、更纯粹的爱。',
    note: '这是一种典型的回避型依恋人格的自我辩解，还是深沉的爱？',
    date: '2023-10-15',
  },
  {
    id: 'n2',
    chapterId: '1',
    quote:
      '我要让你沉溺在爱欲的沉醉之中……想要你在垂暮之年，回忆起这些短暂的时光时，干枯的骨骼仍能为之战栗',
    note: '福楼拜的书信文字张力太强了，这种语言本身就是一种诱惑。',
    date: '2023-10-16',
  },
]

const mockComments = [
  {
    id: 'c1',
    username: '文学爱好者',
    avatar: 'LL',
    content:
      '路易丝和艾玛·包法利之间有着有趣的相似之处。这让我不禁想知道，《包法利夫人》中有多少内容是直接受她启发而创作的。',
    likes: 45,
    date: '2d ago',
  },
  {
    id: 'c2',
    username: 'BookWorm99',
    avatar: 'BW',
    content:
      '在那个时代，他们的年龄差距相当显著，但他们在智力上的联系似乎毫不费力地弥合了这一差距。',
    likes: 23,
    date: '1d ago',
  },
  {
    id: 'c3',
    username: '艺术史学家',
    avatar: 'AH',
    content: '福楼拜的书信本身就是杰作。',
    likes: 12,
    date: '5h ago',
  },
]

// 响应式状态
const isDarkMode = ref(false)
const activePanel = ref<'none' | 'toc' | 'typography' | 'notes'>('none')
const isAnnotationMode = ref(false)
const showThoughts = ref(false)
const readingMode = ref<ReadingMode>('paged')
const typography = ref({
  fontSize: 18,
  lineHeight: 1.8,
})
const currentChapterId = ref('1')
const activeThought = ref<{ index: number; text: string } | null>(null)

// 方法
function toggleTheme() {
  isDarkMode.value = !isDarkMode.value
  if (isDarkMode.value) {
    document.body.classList.add('dark-mode')
    document.body.style.backgroundColor = '#18181b'
  } else {
    document.body.classList.remove('dark-mode')
    document.body.style.backgroundColor = '#f3f4f6'
  }
}

function closePanels() {
  activePanel.value = 'none'
  activeThought.value = null
}

function togglePanel(panel: 'toc' | 'typography' | 'notes') {
  activePanel.value = activePanel.value === panel ? 'none' : panel
  activeThought.value = null
}

function handleChapterSelect(id: string) {
  currentChapterId.value = id
  closePanels()
}

function handleToggleAnnotation() {
  isAnnotationMode.value = !isAnnotationMode.value
  closePanels()
}

function handleToggleReadingMode() {
  readingMode.value = readingMode.value === 'paged' ? 'scroll' : 'paged'
}

function handleToggleThoughts() {
  showThoughts.value = !showThoughts.value
}

function updateTypography(newSettings: any) {
  typography.value = { ...typography.value, ...newSettings }
}

function handleActiveThought(index: number, text: string) {
  activeThought.value = { index, text }
}
</script>

<style scoped>
/* 应用容器样式 */
.app-container {
  height: 100vh; /* 强制占满一屏 */
  overflow: hidden; /* 防止外层出现双重滚动条 */
  transition:
    color 300ms,
    background-color 300ms;
  background-color: #f3f4f6;
  color: #1f2937;
  display: flex;
  flex-direction: column;
}

/* 深色模式样式 */
.app-container.dark-mode {
  background-color: #1f1f23;
  color: #d4d4d8;
}

/* 内容包装器：负责居中和分配剩余空间 */
.content-wrapper {
  flex: 1; /* 占满除了 TopNavigation 之外的空间 */
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center; /* 垂直居中 */
  overflow: hidden;
  padding: 0 1rem; /* 左右留一点空隙 */
}

/* 
  === 核心容器限制 === 
  这个类控制阅读器在屏幕上的物理尺寸
*/
.reader-viewport {
  width: 100%;
  max-width: 1200px; /* 限制最大宽度，避免在宽屏上太长 */

  /* 
    高度计算：
    100vh - (导航栏高度 + 上下边距)
    假设导航栏约 60px，这里预留 120px 足够宽松
  */
  height: calc(100vh - 120px);

  position: relative;
  /* 
    注意：这里不要加 padding 或 overflow
    把布局控制权完全交给内部的 ReaderContent
  */
}

/* 确保内部根节点填满视口 */
:deep(.reader-content-root) {
  width: 100%;
  height: 100%;
}
</style>
