<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { ElSkeleton, ElSwitch } from 'element-plus'
import { getBookshelfAll, type ShelfBook } from '@/api/bookshelf'
import BookCard from '@/components/bookshelf/BookCardMiddle.vue'

type PublicLikeBook = {
  bookId: number
  title: string
  cover: string
  readingStatus?: 'unread' | 'reading' | 'finished'
}

const props = withDefaults(
  defineProps<{
    mode?: 'self' | 'public'
    headerTitle?: string
    max?: number
    showToggle?: boolean
    defaultVisible?: boolean
    storageKey?: string
    viewAllHref?: string
    publicBooks?: PublicLikeBook[]
  }>(),
  {
    mode: 'self',
    headerTitle: '书架',
    max: 10,
    showToggle: false,
    defaultVisible: true,
    storageKey: '',
    viewAllHref: '',
    publicBooks: () => [],
  },
)

type PreviewBook = { bookId: number; title: string; cover: string; isRead: boolean }

const loading = ref(false)
const selfBooks = ref<ShelfBook[]>([])

const visible = ref<boolean>(props.defaultVisible)

const readStoredVisible = () => {
  if (!props.storageKey) return
  try {
    const v = localStorage.getItem(props.storageKey)
    if (v === '0') visible.value = false
    if (v === '1') visible.value = true
  } catch {
    // ignore
  }
}

const persistVisible = () => {
  if (!props.storageKey) return
  try {
    localStorage.setItem(props.storageKey, visible.value ? '1' : '0')
  } catch {
    // ignore
  }
}

watch(
  () => props.defaultVisible,
  (v) => {
    visible.value = v
    readStoredVisible()
  },
  { immediate: true },
)

watch(visible, persistVisible)

const normalizeSelf = (books: ShelfBook[]): PreviewBook[] =>
  books.slice(0, props.max).map((b) => ({
    bookId: b.id,
    title: b.title,
    cover: b.cover,
    isRead: b.status === '读完',
  }))

const normalizePublic = (books: PublicLikeBook[]): PreviewBook[] =>
  books.slice(0, props.max).map((b) => ({
    bookId: Number(b.bookId ?? 0),
    title: String(b.title ?? ''),
    cover: String(b.cover ?? ''),
    isRead: b.readingStatus === 'finished',
  }))

const previewBooks = computed<PreviewBook[]>(() => {
  if (props.mode === 'public') return normalizePublic(props.publicBooks || [])
  return normalizeSelf(selfBooks.value)
})

const load = async () => {
  if (props.mode !== 'self') return
  loading.value = true
  try {
    selfBooks.value = await getBookshelfAll()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  readStoredVisible()
  load()
})
</script>

<template>
  <div class="bookshelf-preview" v-if="visible">
    <div class="header">
      <div class="title-row">
        <h3 class="title">{{ headerTitle }}</h3>
        <a v-if="viewAllHref" class="view-all" :href="viewAllHref" target="_blank">查看全部</a>
      </div>

      <div v-if="showToggle" class="toggle">
        <span class="toggle-label">展示</span>
        <ElSwitch v-model="visible" />
      </div>
    </div>

    <div v-if="loading" class="loading">
      <ElSkeleton :rows="2" animated />
    </div>

    <div v-else-if="previewBooks.length === 0" class="empty">
      <span>暂无书籍</span>
    </div>

    <div v-else class="grid">
      <div class="item" v-for="b in previewBooks" :key="b.bookId">
        <BookCard :title="b.title" :cover="b.cover" :isRead="b.isRead" :book-id="b.bookId" />
      </div>
    </div>
  </div>

  <div class="bookshelf-preview-collapsed" v-else-if="showToggle">
    <div class="header">
      <div class="title-row">
        <h3 class="title">{{ headerTitle }}</h3>
      </div>
      <div class="toggle">
        <span class="toggle-label">展示</span>
        <ElSwitch v-model="visible" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.bookshelf-preview,
.bookshelf-preview-collapsed {
  background: var(--card-bg, #fff);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--shadow, 0 4px 12px rgba(0, 0, 0, 0.05));
  margin-top: 20px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.title-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-main, #333);
}

.view-all {
  font-size: 13px;
  color: var(--primary-green, #42b983);
  text-decoration: none;
}

.view-all:hover {
  text-decoration: underline;
}

.toggle {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.toggle-label {
  font-size: 13px;
  color: var(--text-light, #666);
}

.grid {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
}

.item {
  width: 150px;
}

.empty {
  color: var(--text-light, #666);
  font-size: 14px;
  padding: 10px 0;
}
</style>

