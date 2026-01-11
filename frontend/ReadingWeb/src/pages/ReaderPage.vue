<!-- ReaderPage.vue -->
<template>
  <div class="app-container" :class="{ 'dark-mode': isDarkMode }">
    <TopNavigation title="她既想死，又想去巴黎" :isVisible="true" :isDarkMode="isDarkMode" />

    <TableOfContents
      :isOpen="activePanel === 'toc'"
      @close="closePanels"
      :chapters="chapters"
      :currentChapterId="currentChapterId"
      :bookId="bookId"
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
        :pageData="displayedPageData"
        :isDarkMode="isDarkMode"
        :typography="typography"
        :annotations="showThoughts ? currentChapterAnnotations : currentChapterAnnotations.filter((a) => a.type !== 'thought')"
        :hasPrevChapter="currentChapterIndex > 0"
        :hasNextChapter="currentChapterIndex >= 0 && currentChapterIndex < chapters.length - 1"
        :isLastChapter="isLastChapter"
        :bookEndData="{
          bookId: bookData.id,
          bookTitle: bookData.title,
          recommendationValue: bookData.recommendationValue,
          reviewCount: bookData.reviewCount,
          ratingStats: bookData.ratingStats,
          initialCompleted: readingProgress.completed,
          initialCompleteTime: readingProgress.completeTime
        }"
        @addAnnotation="handleAddAnnotation"
        @deleteAnnotation="handleDeleteAnnotation"
        @activeThought="handleActiveThought"
        @prevChapter="goToPrevChapter"
        @nextChapter="goToNextChapter"
        @aiQuery="
          (text) => {
            aiSelectedText = text
            aiPanelOpen = true
          }
        "
        @textAction="handleTextAction"
        @markComplete="handleMarkComplete"
        @viewReviews="handleViewReviews"
        @rateBook="(rating: string) => handleRateBook(rating)"
      />
    </div>

    <FloatingMenu
      :isDarkMode="isDarkMode"
      @toggleTheme="toggleTheme"
      @toggleTOC="togglePanel('toc')"
      @toggleTypography="togglePanel('typography')"
      @toggleAnnotation="togglePanel('notes')"
      @toggleThoughts="showThoughts = !showThoughts"
      :activePanel="activePanel"
      :isAnnotationMode="activePanel === 'notes'"
      :showThoughts="showThoughts"
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
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'

// API导入
import {
  getChapterContent,
  type ChapterContent
} from '@/api/reader/chapter-content'
import {
  getChapterNotes,
  createChapterNote,
  updateChapterNote,
  deleteChapterNote,
  type ChapterNote
} from '@/api/reader/chapter-notes'
import {
  getBookTOC,
  type BookTOCItem
} from '@/api/reader/book-toc'
import {
  getBookNotes,
  createBookNote,
  updateBookNote,
  deleteBookNote,
  type BookNote
} from '@/api/reader/book-notes'

// 组件路径 - 请根据实际项目路径确认
import TopNavigation from '@/components/Reader/TopNavigation.vue'
import FloatingMenu from '@/components/Reader/FloatingMenu.vue'
import ReaderContent from '@/components/Reader/ReaderContent.vue'
import TypographyPanel from '@/components/Reader/TypographyPanel.vue'
import TableOfContents from '@/components/Reader/TableOfContents.vue'
import NotesPanel from '@/components/Reader/NotesPanel.vue'
import ThoughtsBubble from '@/components/Reader/ThoughtsBubble.vue'
import AIAnalysisPanel from '@/components/Reader/AIAnalysisPanel.vue'
import type {
  BookPage,
  TypographySettings,
  Chapter,
  Note,
  Comment,
  Annotation,
} from '@/components/Reader/types'

// 路由和响应式数据
const route = useRoute()

// 从路由参数获取书籍ID和章节ID
const bookId = ref(route.params.bookId as string || '1')
const currentChapterId = ref(Number(route.params.chapterId as string) || 1)

// API数据状态
const currentChapterData = ref<ChapterContent | null>(null)
const bookTOC = ref<BookTOCItem[]>([])
const chapterNotes = ref<ChapterNote[]>([])
const allBookNotes = ref<BookNote[]>([])
const loading = ref(false)

// --------------------------
// 示例数据
// --------------------------
// 模拟多章节内容
const chapterContents = {
  '1': {
    title: '译者序：爱与艺术的交响乐',
    paragraphs: [
      '贝尔特 · 莫里索绘',
      '你本应拥有另一种命运，值得更优秀的人、更纯粹的爱。我竭尽所能，想要向你证明我的爱意。可你渴望的，恰好是我唯一无法给予的。',
      'Préface',
      '1846年7月29日，巴黎一位著名雕塑家詹姆斯 · 普拉迪埃的工作室里，二十五岁的古斯塔夫 · 福楼拜与三十六岁的路易丝 · 科莱初次相逢。这并非一场偶然的邂逅，而是命运的交会。当时，福楼拜尚未崭露头角，1844年他因神经疾病放弃了法律学业，回到故乡克鲁瓦塞过着隐居般的生活，依靠家族遗产与母亲相伴；路易丝已是巴黎文学圈中的一颗明星，她以诗人和作家的身份活跃于沙龙，作品多次获得法兰西学院的嘉奖。她嫁给了音乐家伊波利特 · 科莱，但婚姻并未束缚她的情感与才华，她与哲学家维克托 · 库森等名流有着剪不断、理还乱的关系。',
      '初见时，路易丝的美貌与活力令福楼拜心动。他后来在信中写道："你拥有能让人死而复生般的丰沛爱意。"她的金发、白皙的肌肤与自信的气质，与福楼拜日后笔下塑造的爱玛 · 包法利有着惊人的相似之处，而福楼拜的高大身材、深邃的眼神与文学热情，也吸引了这位比他年长十一岁的女性。他们迅速坠入爱河，开始了一段持续八年的激情与矛盾交织的恋情。这段关系不仅改变了他们的生活轨迹，也为文学史留下了宝贵的书信遗产。',
      '恋情伊始，福楼拜与路易丝的交流主要通过书信完成。由于他不愿离开克鲁瓦塞，声称要陪伴年迈的母亲，他们的见面机会极为有限。1846年至1848年的两年间，他们仅在巴黎或芒特拉若利见到了六次。然而，距离并未冷却他们的热情，反而点燃了书信中的火焰。福楼拜的信件充满了浪漫的想象与炽烈的表白，如1846年8月14日的信中写道："我要让你沉溺在爱欲的沉醉之中……想要你在垂暮之年，回忆起这些短暂的时光时，干枯的骨骼仍能为之战栗，为之欣喜若狂。"这样的文字既展现了他对路易丝的渴望，也透露出一种将爱情艺术化、理想化的倾向。对于福楼拜而言，路易丝不仅是恋人，更是他文学创作的灵感缪斯与倾诉对象。',
      '长夜的写作与清晨的重读成为福楼拜与路易丝的共同节奏。她在巴黎的社交圈子中纵横捭阖，而他在克鲁瓦塞的书房里反复打磨每一个句子。两条轨迹偶尔交汇，但更多时候，他们只能以文字为桥梁。在信中，福楼拜讨论如何在小说中保持"客观冷静的凝视"，又抱怨身体的病痛使他陷入虚弱。他甚至把写作比作"一场缓慢而壮丽的溺水"，既是享受也是折磨。',
      '路易丝则在现实与幻想之间穿梭。她渴望与福楼拜在一起，但又无法舍弃巴黎的舞台。她写道："如果我离开，便不再是我自己；若我留下，又怕失去你。"这种摇摆让他们的信件中充满了质问与自责。福楼拜的回答往往冷静而坚定："我爱你，但我更爱真理与艺术。若写作是我的命运，你便是其中不可或缺的见证。"这种把情感与创作捆绑的态度，使他们的爱情呈现出一种近乎宗教的虔诚。',
      '1848年革命爆发，巴黎陷入动荡，路易丝的收入锐减，生活压力骤增。她希望福楼拜能来巴黎共度难关，但他依旧选择留在克鲁瓦塞。这一次，他给出的理由不仅是母亲的健康，更是"文学需要宁静"。路易丝愤怒地指责他逃避现实，质疑他的爱是否只是为了文学服务。福楼拜则坚持，真正的爱应该让彼此成为更完整的人，而不是彼此的枷锁。',
      '他们的争吵在信件中愈演愈烈，但每一次风暴过后，又会出现温柔的和解。福楼拜会寄去他最新完成的手稿片段，路易丝则回赠她的诗稿。互相批评、互相欣赏，构成了他们关系的基石。福楼拜常说："你是我最苛刻的读者，也是我最忠诚的读者。"而路易丝则回道："只有你能理解我在词句间藏下的颤抖。"这种双向的深度阅读，使他们的情感超越了简单的恋爱，转化为彼此的创作伙伴关系。',
      '随着时间推移，他们都意识到激情终将退却，生活需要更稳固的支撑。路易丝提出分开一段时间，给彼此空间去思考未来。福楼拜接受了这个建议，并在信中写下："若我们注定要各自前行，我也希望在每一个黎明，你仍能听到我在纸上的回响。"这句话后来被许多评论家视为福楼拜写作观的缩影——文字是他与世界沟通的唯一方式，也是他保存感情的容器。',
      '他们最终在1854年正式结束了情感关系，但书信往来并未终止。路易丝继续在巴黎发表诗作，福楼拜则完成了《包法利夫人》。当这部小说问世时，她写信祝贺，并坦言在爱玛身上看到了自己与无数女性的影子。福楼拜回应道："若没有你，我写不出她；若没有你，我也不会理解什么是真实的痛苦。"',
      '回望这段关系，我们看到的不只是两个人的爱情史，更是十九世纪文学与现实交织的样本。他们在信件中不断探索艺术与生活的边界，既渴望彼此，又守护各自的独立。这种张力让他们的文字充满力量，也让后人得以窥见一个时代的情感密度。',
      '或许，福楼拜与路易丝从未真正分开。文字让他们在不同的城市、不同的时间里持续相遇；文学让他们的灵魂在纸上永恒停驻。正如路易丝在分手后写下的那句话："当我翻开你的信，就像再次推开那扇通往夏日花园的门，那里有我未竟的青春，也有我们共同的梦。"',
      '对读者而言，这些书信不仅是八年爱情的见证，也是理解福楼拜创作心路的钥匙。每一封信都是他思想的练习场，情感的宣泄口，也是文学形式的实验。正因如此，《福楼拜与科莱书信集》才会在出版后引起广泛关注，成为研究福楼拜乃至整个十九世纪法国文学的重要文献。',
      '今天，当我们在数字屏幕上重读这些文字时，仍能感受到其中的温度与重量。它提醒我们：真正的阅读不仅是信息的获取，更是与作者、与过去、与自我进行的持续对话。微信读书的存在，正是让这种对话在新时代继续发生的桥梁。',
    ]
  },
  '2': {
    title: '第一章：初遇',
    paragraphs: [
      '1846年的那个夏日午后，巴黎的空气中弥漫着艺术与激情的味道。在詹姆斯·普拉迪埃的工作室里，阳光透过高窗洒在雕塑作品上，为这个充满创造力的空间增添了几分神圣感。正是在这样的氛围中，古斯塔夫·福楼拜第一次见到了路易丝·科莱。',
      '那时的福楼拜，二十五岁，刚刚从法律学业的挫败中走出，回到克鲁瓦塞的家中。他身材高大，眼神深邃，眉宇间透露出对文学的执着与对生活的困惑。虽然尚未在文坛崭露头角，但他已经开始了自己的创作之路，每天都在书房中与文字搏斗，试图找到属于自己的声音。',
      '而路易丝，三十六岁，已经是巴黎文学圈中的知名人物。她以诗人的身份活跃于各个沙龙，作品多次获得法兰西学院的嘉奖。她的美貌与才华同样出众，金发如瀑布般垂在肩上，白皙的肌肤在阳光下闪闪发光。她嫁给了音乐家伊波利特·科莱，但这段婚姻并没有束缚她的情感与创作。',
      '当他们的目光第一次相遇时，时间仿佛静止了。福楼拜后来在信中回忆道："那一刻，我仿佛看到了另一个自己，一个在文字中寻找真理，在情感中寻找真实的灵魂。"而路易丝则被这个年轻作家的热情与才华所吸引，她感受到了某种共鸣，某种在文学与艺术中才能找到的理解。',
      '他们的初次对话并不长，但每一句话都充满了深意。福楼拜谈到了他对文学的理想，对"客观冷静的凝视"的追求；路易丝则分享了她对诗歌的理解，对情感与形式的平衡。他们发现彼此在艺术理念上有着惊人的相似之处，都相信文字应该超越个人情感，成为探索人性与世界的工具。',
      '然而，这次相遇也暴露了他们之间的差异。福楼拜更倾向于隐居式的创作，他需要宁静的环境来打磨每一个句子；而路易丝则活跃于社交圈，她需要外界的刺激来激发灵感。这种差异，在未来的岁月中，将成为他们关系中的主要矛盾。',
    ]
  },
  '3': {
    title: '第二章：书信的火焰',
    paragraphs: [
      '由于福楼拜不愿离开克鲁瓦塞，声称要陪伴年迈的母亲，他们的见面机会极为有限。1846年至1848年的两年间，他们仅在巴黎或芒特拉若利见到了六次。然而，距离并未冷却他们的热情，反而点燃了书信中的火焰。',
      '福楼拜的信件充满了浪漫的想象与炽烈的表白。在1846年8月14日的信中，他写道："我要让你沉溺在爱欲的沉醉之中……想要你在垂暮之年，回忆起这些短暂的时光时，干枯的骨骼仍能为之战栗，为之欣喜若狂。"这样的文字既展现了他对路易丝的渴望，也透露出一种将爱情艺术化、理想化的倾向。',
      '对于福楼拜而言，路易丝不仅是恋人，更是他文学创作的灵感缪斯与倾诉对象。他在信中讨论如何在小说中保持"客观冷静的凝视"，又抱怨身体的病痛使他陷入虚弱。他甚至把写作比作"一场缓慢而壮丽的溺水"，既是享受也是折磨。',
      '路易丝的回信同样充满激情与智慧。她分享自己在巴黎的见闻，谈论文学与艺术，也表达对福楼拜的思念。她写道："如果我离开，便不再是我自己；若我留下，又怕失去你。"这种摇摆让他们的信件中充满了质问与自责。',
      '福楼拜的回答往往冷静而坚定："我爱你，但我更爱真理与艺术。若写作是我的命运，你便是其中不可或缺的见证。"这种把情感与创作捆绑的态度，使他们的爱情呈现出一种近乎宗教的虔诚。',
    ]
  },
  '4': {
    title: '第三章：矛盾与和解',
    paragraphs: [
      '1848年革命爆发，巴黎陷入动荡，路易丝的收入锐减，生活压力骤增。她希望福楼拜能来巴黎共度难关，但他依旧选择留在克鲁瓦塞。这一次，他给出的理由不仅是母亲的健康，更是"文学需要宁静"。',
      '路易丝愤怒地指责他逃避现实，质疑他的爱是否只是为了文学服务。福楼拜则坚持，真正的爱应该让彼此成为更完整的人，而不是彼此的枷锁。他们的争吵在信件中愈演愈烈，但每一次风暴过后，又会出现温柔的和解。',
      '福楼拜会寄去他最新完成的手稿片段，路易丝则回赠她的诗稿。互相批评、互相欣赏，构成了他们关系的基石。福楼拜常说："你是我最苛刻的读者，也是我最忠诚的读者。"而路易丝则回道："只有你能理解我在词句间藏下的颤抖。"',
      '这种双向的深度阅读，使他们的情感超越了简单的恋爱，转化为彼此的创作伙伴关系。他们在文字中找到了理解，也在理解中找到了继续前行的力量。',
    ]
  },
  '5': {
    title: '第四章：别离与永恒',
    paragraphs: [
      '随着时间推移，他们都意识到激情终将退却，生活需要更稳固的支撑。路易丝提出分开一段时间，给彼此空间去思考未来。福楼拜接受了这个建议，并在信中写下："若我们注定要各自前行，我也希望在每一个黎明，你仍能听到我在纸上的回响。"',
      '这句话后来被许多评论家视为福楼拜写作观的缩影——文字是他与世界沟通的唯一方式，也是他保存感情的容器。他们最终在1854年正式结束了情感关系，但书信往来并未终止。',
      '路易丝继续在巴黎发表诗作，福楼拜则完成了《包法利夫人》。当这部小说问世时，她写信祝贺，并坦言在爱玛身上看到了自己与无数女性的影子。福楼拜回应道："若没有你，我写不出她；若没有你，我也不会理解什么是真实的痛苦。"',
      '回望这段关系，我们看到的不只是两个人的爱情史，更是十九世纪文学与现实交织的样本。他们在信件中不断探索艺术与生活的边界，既渴望彼此，又守护各自的独立。这种张力让他们的文字充满力量，也让后人得以窥见一个时代的情感密度。',
      '或许，福楼拜与路易丝从未真正分开。文字让他们在不同的城市、不同的时间里持续相遇；文学让他们的灵魂在纸上永恒停驻。正如路易丝在分手后写下的那句话："当我翻开你的信，就像再次推开那扇通往夏日花园的门，那里有我未竟的青春，也有我们共同的梦。"',
    ]
  }
}

// 将TOC数据转换为章节格式
const chapters = computed<Chapter[]>(() => {
  return bookTOC.value.map(item => ({
    id: item.chapterId, // 使用 chapterId 作为章节ID
    title: item.chapterName,
    page: item.startPage + 1 // 将从0开始的索引转换为从1开始的页码
  }))
})

const displayedPageData = computed<BookPage>(() => {
  if (!currentChapterData.value) {
    return {
      chapter: '加载中...',
      content: ['正在加载章节内容...'],
    }
  }

  return {
    chapter: currentChapterData.value.title,
    content: currentChapterData.value.content.split('\n'), // 假设内容以换行符分割段落
  }
})

const currentChapterIndex = computed(() => {
  return chapters.value.findIndex(chapter => chapter.id === currentChapterId.value)
})

// 计算是否是最后一章
const isLastChapter = computed(() => {
  return currentChapterIndex.value >= 0 && currentChapterIndex.value === chapters.value.length - 1
})

// 计算当前章节的标注
const currentChapterAnnotations = computed(() => {
  return annotations.value.filter(ann => ann.chapterId === String(currentChapterId.value))
})

const goToPrevChapter = () => {
  const prevIndex = currentChapterIndex.value - 1
  if (prevIndex >= 0 && prevIndex < chapters.value.length) {
    const prevChapter = chapters.value[prevIndex]
    if (prevChapter) {
      handleChapterSelect(prevChapter.id)
    }
    // 滚动到顶部
    const article = document.querySelector('.reader-article')
    if (article) {
      article.scrollTop = 0
    }
  }
}

const goToNextChapter = () => {
  // 确保当前章节索引有效，并且不是最后一章
  if (currentChapterIndex.value < 0 || currentChapterIndex.value >= chapters.value.length - 1) {
    return
  }
  
  const nextIndex = currentChapterIndex.value + 1
  const nextChapter = chapters.value[nextIndex]
  
  if (nextChapter) {
    handleChapterSelect(nextChapter.id)
    // 滚动到顶部
    const article = document.querySelector('.reader-article')
    if (article) {
      article.scrollTop = 0
    }
  }
}

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

// 修正初始标注，确保它们有正确的chapterId
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

// API数据加载函数
const loadChapterContent = async (chapterId: number) => {
  try {
    loading.value = true
    const data = await getChapterContent(bookId.value, chapterId)
    currentChapterData.value = data
    currentChapterId.value = chapterId
  } catch (error) {
    console.error('加载章节内容失败:', error)
  } finally {
    loading.value = false
  }
}

const loadBookTOC = async () => {
  try {
    const data = await getBookTOC(bookId.value)

    // 如果API返回有效数据，使用它；否则使用模拟数据
    if (data && data.length > 0) {
      bookTOC.value = data
    } else {
      // 使用基于现有章节内容的模拟目录数据
      console.warn('TOC API returned empty data, using fallback data')
      bookTOC.value = [
        { id: 1, chapterId: 1, startPage: 0, chapterNumber: 1, chapterName: '译者序：爱与艺术的交响乐' },
        { id: 2, chapterId: 2, startPage: 9, chapterNumber: 2, chapterName: '第一章：初遇' },
        { id: 3, chapterId: 3, startPage: 19, chapterNumber: 3, chapterName: '第二章：书信的火焰' },
        { id: 4, chapterId: 4, startPage: 29, chapterNumber: 4, chapterName: '第三章：矛盾与和解' },
        { id: 5, chapterId: 5, startPage: 39, chapterNumber: 5, chapterName: '第四章：别离与永恒' }
      ]
    }
  } catch (error: any) {
    console.error('加载书籍目录失败，使用备用数据:', error?.message)

    // API调用失败时使用模拟数据
    bookTOC.value = [
      { id: 1, chapterId: 1, startPage: 0, chapterNumber: 1, chapterName: '译者序：爱与艺术的交响乐' },
      { id: 2, chapterId: 2, startPage: 9, chapterNumber: 2, chapterName: '第一章：初遇' },
      { id: 3, chapterId: 3, startPage: 19, chapterNumber: 3, chapterName: '第二章：书信的火焰' },
      { id: 4, chapterId: 4, startPage: 29, chapterNumber: 4, chapterName: '第三章：矛盾与和解' },
      { id: 5, chapterId: 5, startPage: 39, chapterNumber: 5, chapterName: '第四章：别离与永恒' }
    ]
  }
}

const loadChapterNotes = async (chapterId: string) => {
  try {
    const data = await getChapterNotes(bookId.value, chapterId)
    chapterNotes.value = data
  } catch (error) {
    console.error('加载章节笔记失败:', error)
  }
}

const loadAllBookNotes = async () => {
  try {
    const data = await getBookNotes(bookId.value)
    allBookNotes.value = data
  } catch (error) {
    console.error('加载全书笔记失败，使用空数据:', (error as any)?.message)
    // API失败时使用空数组
    allBookNotes.value = []
  }
}

// State
const isDarkMode = ref(false)
const activePanel = ref<'none' | 'toc' | 'typography' | 'notes'>('none')
const showThoughts = ref(true)
// 将API的章节笔记转换为组件所需的格式
const notes = computed<Note[]>(() => {
  return chapterNotes.value.map(note => ({
    id: note.id,
    chapterId: String(currentChapterId.value),
    quote: note.quote,
    note: note.thought,
    date: new Date(note.createdAt).toLocaleDateString(),
  }))
})
const annotations = ref<Annotation[]>(initialAnnotations)
const aiPanelOpen = ref(false)
const aiSelectedText = ref('')
const typography = ref<TypographySettings>({ fontSize: 18, lineHeight: 1.8 })

// 添加书籍数据
const bookData = ref({
  id: 'book-123',
  title: '她既想死，又想去巴黎',
  recommendationValue: 90.5,
  reviewCount: 256,
  ratingStats: {
    recommend: 70,
    average: 20,
    poor: 10
  }
})

// 添加阅读进度
const readingProgress = ref({
  completed: false,
  completeTime: null as string | null
})

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

const handleChapterSelect = async (id: number) => {
  currentChapterId.value = id
  closePanels()

  // 加载新章节的内容和笔记
  await Promise.all([
    loadChapterContent(id),
    loadChapterNotes(id.toString()),
  ])
}

const handleActiveThought = (noteId: string, text: string) => {
  activeContext.value = { type: 'view', noteId, text }
}

// 新增：删除标注的处理函数
const handleDeleteAnnotation = async (annotationId: string) => {
  try {
    // 从 annotations 中移除指定ID的标注
    const index = annotations.value.findIndex(ann => ann.id === annotationId)
    if (index !== -1) {
      const annotation = annotations.value[index]

      // 如果是想法标注，调用API删除笔记
      if (annotation && annotation.type === 'thought' && annotation.noteId) {
        await deleteChapterNote(bookId.value, currentChapterId.value, annotation.noteId)
      }

      // 从本地状态中移除
      annotations.value.splice(index, 1)

      // 删除对应的笔记
      if (annotation && annotation.noteId) {
        const noteIndex = notes.value.findIndex(note => note.id === annotation.noteId)
        if (noteIndex !== -1) {
          notes.value.splice(noteIndex, 1)
        }
      }

      // 重新加载章节笔记以保持同步
      await loadChapterNotes(currentChapterId.value.toString())
    }
  } catch (error) {
    console.error('删除标注失败:', error)
  }
}

const handleAddAnnotation = (newAnn: Omit<Annotation, 'id'>) => {
  const id = Date.now().toString()
  // 确保标注使用当前章节ID
  const annotation = {
    ...newAnn,
    id,
    chapterId: String(currentChapterId.value) // 确保使用当前章节ID
  }
  annotations.value.push(annotation)

  if (['marker', 'wave', 'line'].includes(newAnn.type)) {
    const newNote: Note = {
      id: `note-${id}`,
      chapterId: String(currentChapterId.value), // 确保笔记也关联到当前章节
      quote:
        displayedPageData.value?.content[newAnn.pIndex]?.substring(newAnn.start, newAnn.end) || '',
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
      chapterId: String(currentChapterId.value), // 确保使用当前章节ID
      pIndex: activeContext.value.range.pIndex,
      start: activeContext.value.range.start,
      end: activeContext.value.range.end,
      type: action as any,
    })
  }
}

const submitNote = async (noteContent: string) => {
  if (activeContext.value && activeContext.value.range) {
    try {
      // 调用API创建笔记
      const noteData = {
        chapterId: currentChapterId.value,
        quote: activeContext.value.text,
        startIndex: activeContext.value.range.start,
        endIndex: activeContext.value.range.end,
        lineType: ['marker'], // 默认线条类型
        thought: noteContent,
        pageNumber: activeContext.value.range.pIndex + 1, // 页码从1开始
      }

      const createdNote = await createChapterNote(bookId.value, currentChapterId.value, noteData)

      // 更新本地状态
      const newNote: Note = {
        id: createdNote.id,
        chapterId: String(currentChapterId.value),
        quote: createdNote.quote,
        note: createdNote.thought,
        date: new Date(createdNote.createdAt).toLocaleDateString(),
      }
      notes.value.unshift(newNote)

      const newAnn: Annotation = {
        id: `ann-${createdNote.id}`,
        chapterId: String(currentChapterId.value),
        pIndex: activeContext.value.range.pIndex,
        start: activeContext.value.range.start,
        end: activeContext.value.range.end,
        type: 'thought',
        noteId: createdNote.id,
      }
      annotations.value.push(newAnn)

      activePanel.value = 'notes'

      // 重新加载章节笔记以保持同步
      await loadChapterNotes(currentChapterId.value.toString())
    } catch (error) {
      console.error('创建笔记失败:', error)
    }
  }
  activeContext.value = null
}

// 添加标记完成的方法
const handleMarkComplete = async (data: { bookId: string | number, completeTime: string }) => {
  try {
    // 这里可以调用API
    readingProgress.value.completed = true
    readingProgress.value.completeTime = data.completeTime

    // 保存到本地存储
    localStorage.setItem(`book_${bookData.value.id}_completed`, JSON.stringify({
      completed: true,
      completeTime: readingProgress.value.completeTime
    }))

    console.log('标记为已读完', data)
  } catch (error) {
    console.error('标记失败:', error)
  }
}

const handleViewReviews = () => {
  console.log('查看点评')
  // 这里可以跳转到点评页面
}

const handleRateBook = (rating: string) => {
  console.log('评分:', rating)
  // 这里可以处理评分逻辑
}

// 监听路由参数变化
watch(
  () => route.params,
  async (newParams) => {
    const newBookId = newParams.bookId as string
    const newChapterId = Number(newParams.chapterId as string) || 1

    if (newBookId !== bookId.value) {
      bookId.value = newBookId
      // 书籍ID改变时，先加载目录，再加载章节数据
      await loadBookTOC()
      await Promise.all([
        loadChapterContent(newChapterId),
        loadChapterNotes(newChapterId.toString()),
        loadAllBookNotes(),
      ])
      currentChapterId.value = newChapterId
    } else if (newChapterId !== currentChapterId.value) {
      // 章节ID改变时，确保目录已加载，然后加载新章节数据
      if (bookTOC.value.length === 0) {
        await loadBookTOC()
      }
      await Promise.all([
        loadChapterContent(newChapterId),
        loadChapterNotes(newChapterId.toString()),
      ])
      currentChapterId.value = newChapterId
    }
  },
  { immediate: false } // 已经在onMounted中处理了初始加载
)

// 组件挂载时加载数据
onMounted(async () => {
  // 先加载书籍目录，确保章节列表可用
  await loadBookTOC()
  
  // 确保 currentChapterId 与目录中的章节ID匹配
  if (chapters.value.length > 0) {
    // 如果当前章节ID不在目录中，使用第一个章节
    const foundChapter = chapters.value.find(ch => ch.id === currentChapterId.value)
    if (!foundChapter && chapters.value[0]) {
      currentChapterId.value = chapters.value[0].id
    }
  }
  
  // 然后并行加载当前章节内容和笔记
  await Promise.all([
    loadChapterContent(currentChapterId.value),
    loadChapterNotes(currentChapterId.value.toString()),
    loadAllBookNotes(),
  ])

  loadReadingProgress()
})

const loadReadingProgress = () => {
  const savedProgress = localStorage.getItem(`book_${bookData.value.id}_completed`)
  if (savedProgress) {
    try {
      const progress = JSON.parse(savedProgress)
      readingProgress.value.completed = progress.completed
      readingProgress.value.completeTime = progress.completeTime
    } catch (error) {
      console.error('加载阅读进度失败:', error)
    }
  }
}
</script>

<style scoped>
/* 应用容器样式 */
.app-container {
  height: 100vh;
  overflow: hidden;
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
  flex: 1;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

/*
  === 核心容器限制 ===
  这个类控制阅读器在屏幕上的物理尺寸
*/
.reader-viewport {
  width: 100%;
  max-width: 1400px;
  height: calc(100vh - 90px);
  position: relative;
}

/* 确保内部根节点填满视口 */
:deep(.reader-content-root) {
  width: 100%;
  height: 100%;
}
</style>
