<!-- ReaderPage.vue -->
<template>
  <div class="app-container" :class="{ 'dark-mode': isDarkMode }">
    <TopNavigation title="她既想死，又想去巴黎" :isVisible="true" :isDarkMode="isDarkMode" />

    <!-- SideMenu 已移除，如需恢复请 import 并注册组件 -->

    <TableOfContents
      :isOpen="activePanel === 'toc'"
      @close="closePanels"
      :chapters="chapters"
      :currentChapterId="currentChapterId"
      @select="handleChapterSelect"
      :isDarkMode="isDarkMode"
    />

    <NotesPanel
      :isOpen="activePanel === 'notes'"
      @close="closePanels"
      :notes="notes"
      :isDarkMode="isDarkMode"
    />

    <div class="content-wrapper">
      <ReaderContent
        :pageData="samplePageData"
        :isDarkMode="isDarkMode"
        :typography="typography"
        :readingMode="readingMode"
        :annotations="showThoughts ? annotations : annotations.filter((a) => a.type !== 'thought')"
        @addAnnotation="handleAddAnnotation"
        @activeThought="handleActiveThought"
        @aiQuery="
          (text) => {
            aiSelectedText = text
            aiPanelOpen = true
          }
        "
        @textAction="handleTextAction"
      />
    </div>

    <FloatingMenu
      :isDarkMode="isDarkMode"
      @toggleTheme="toggleTheme"
      @toggleTOC="togglePanel('toc')"
      @toggleTypography="togglePanel('typography')"
      @toggleAnnotation="togglePanel('notes')"
      @toggleThoughts="showThoughts = !showThoughts"
      @toggleReadingMode="readingMode = readingMode === 'paged' ? 'scroll' : 'paged'"
      :activePanel="activePanel"
      :isAnnotationMode="activePanel === 'notes'"
      :showThoughts="showThoughts"
      :readingMode="readingMode"
    />

    <TypographyPanel
      v-if="activePanel === 'typography'"
      :settings="typography"
      @update="(newSettings) => (typography = newSettings)"
      :isDarkMode="isDarkMode"
    />

    <ThoughtsBubble
      :isOpen="!!activeContext"
      @close="activeContext = null"
      :comments="activeContext?.type === 'view' ? mockComments : []"
      :isDarkMode="isDarkMode"
      :quoteText="activeContext?.text || ''"
      @action="handleThoughtsBubbleAction"
      @submit="submitNote"
    />

    <AIAnalysisPanel
      :isOpen="aiPanelOpen"
      @close="aiPanelOpen = false"
      :selectedText="aiSelectedText"
      :isDarkMode="isDarkMode"
    />
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

import type {
  BookPage,
  TypographySettings,
  Chapter,
  ReadingMode,
  Note,
  Comment,
  Annotation,
} from '@/components/Reader/types'

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

const initialAnnotations: Annotation[] = [
  {
    id: 'init-1',
    chapterId: '1',
    pIndex: 5,
    start: 33,
    end: 80,
    type: 'thought',
    noteId: 'c1',
  },
]

// State
const isDarkMode = ref(false)
const activePanel = ref<'none' | 'toc' | 'typography' | 'notes'>('none')
const showThoughts = ref(true)
const readingMode = ref<ReadingMode>('paged')
const notes = ref<Note[]>(mockNotes) // 修复：mockNotes 不是 ref
const annotations = ref<Annotation[]>(initialAnnotations)
const aiPanelOpen = ref(false)
const aiSelectedText = ref('')
const typography = ref<TypographySettings>({ fontSize: 18, lineHeight: 1.8 })
const currentChapterId = ref('1')

interface ActiveContext {
  type: 'view' | 'create'
  range?: { pIndex: number; start: number; end: number }
  text: string
  noteId?: string
}
const activeContext = ref<ActiveContext | null>(null)

// Methods
const toggleTheme = () => {
  isDarkMode.value = !isDarkMode.value
  // Body background handled by CSS in App or Global styles usually,
  // but here we might toggle a class on body or just rely on the main div
  document.body.style.backgroundColor = !isDarkMode.value ? '#18181b' : '#f3f4f6'
}

const closePanels = () => {
  activePanel.value = 'none'
  activeContext.value = null
  aiPanelOpen.value = false
}

const togglePanel = (panel: 'toc' | 'typography' | 'notes') => {
  activePanel.value = activePanel.value === panel ? 'none' : panel
  activeContext.value = null
}

const handleChapterSelect = (id: string) => {
  currentChapterId.value = id
  closePanels()
}

const handleActiveThought = (noteId: string, text: string) => {
  activeContext.value = { type: 'view', noteId, text }
}

const handleAddAnnotation = (newAnn: Omit<Annotation, 'id'>) => {
  const id = Date.now().toString()
  const annotation = { ...newAnn, id }
  annotations.value.push(annotation)

  if (['marker', 'wave', 'line'].includes(newAnn.type)) {
    const newNote: Note = {
      id: `note-${id}`,
      chapterId: currentChapterId.value,
      quote: samplePageData.content[newAnn.pIndex].substring(newAnn.start, newAnn.end),
      note: `[Highlight: ${newAnn.type}]`,
      date: new Date().toLocaleDateString(),
    }
    notes.value.unshift(newNote)
  }
}

const handleTextAction = (
  text: string,
  action: string,
  range?: { pIndex: number; start: number; end: number },
) => {
  if (action === 'thought' && range) {
    activeContext.value = { type: 'create', range, text }
  }
}

const handleThoughtsBubbleAction = (action: string) => {
  if (!activeContext.value) return

  if (action === 'copy') {
    navigator.clipboard.writeText(activeContext.value.text)
    return
  }

  if (['marker', 'wave', 'line'].includes(action) && activeContext.value.range) {
    handleAddAnnotation({
      chapterId: currentChapterId.value,
      pIndex: activeContext.value.range.pIndex,
      start: activeContext.value.range.start,
      end: activeContext.value.range.end,
      type: action as any,
    })
  }
}

const submitNote = (noteContent: string) => {
  if (activeContext.value && activeContext.value.range) {
    const noteId = Date.now().toString()
    const newNote: Note = {
      id: noteId,
      chapterId: currentChapterId.value,
      quote: activeContext.value.text,
      note: noteContent,
      date: new Date().toLocaleDateString(),
    }
    notes.value.unshift(newNote)

    const newAnn: Annotation = {
      id: `ann-${noteId}`,
      chapterId: currentChapterId.value,
      pIndex: activeContext.value.range.pIndex,
      start: activeContext.value.range.start,
      end: activeContext.value.range.end,
      type: 'thought',
      noteId: noteId,
    }
    annotations.value.push(newAnn)

    activePanel.value = 'notes'
  }
  activeContext.value = null
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
}

/* 
  === 核心容器限制 === 
  这个类控制阅读器在屏幕上的物理尺寸
*/
.reader-viewport {
  width: 100%;
  max-width: 1400px; /* 限制最大宽度，避免在宽屏上太长 */
  height: calc(100vh - 90px);
  position: relative;
}

/* 确保内部根节点填满视口 */
:deep(.reader-content-root) {
  width: 100%;
  height: 100%;
}
</style>
