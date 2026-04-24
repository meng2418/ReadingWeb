<template>
  <div class="public-user-header" :style="cssVars">
    <div class="top-row">
      <div class="user-info">
        <div class="avatar">
          <img :src="avatarUrl" alt="用户头像" @error="handleAvatarError" />
        </div>
        <div class="text-info">
          <div class="name-row">
            <h1 class="nickname">{{ profile.username }}</h1>
            <span v-if="profile.isMember" class="member-badge">会员</span>
          </div>
          <p class="bio">{{ profile.bio || '暂无简介' }}</p>
          <div v-if="profile.isSelf" class="self-hint">这是你自己的主页</div>
          <div v-else-if="profile.isFollower" class="follower-hint">TA 关注了你</div>
        </div>
      </div>

      <div class="right-actions">
        <button
          class="follow-btn"
          :class="{ following: localIsFollowing }"
          :disabled="loading"
          @click="toggleFollow"
        >
          {{ localIsFollowing ? '已关注' : '+ 关注' }}
        </button>
        <button
          class="message-btn"
          @click="startMessage"
        >
          私信
        </button>
      </div>
    </div>

    <div class="stats-row">
      <div class="stat-item">
        <span class="num">{{ profile.followingCount }}</span>
        <span class="label">关注</span>
      </div>
      <div class="stat-item">
        <span class="num">{{ profile.followerCount }}</span>
        <span class="label">粉丝</span>
      </div>
      <div class="stat-item">
        <span class="num">{{ profile.postCount }}</span>
        <span class="label">发布</span>
      </div>
      <div class="stat-item">
        <span class="num">{{ profile.consecutiveReadingDays }}</span>
        <span class="label">连续阅读</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { followUserApi, unfollowUserApi } from '@/api/userRelations'
import { useTheme } from '@/composables/useTheme'
import { getAvatarUrl, DEFAULT_AVATAR } from '@/utils/defaultImages'

const props = defineProps<{
  userId: number | string
  profile: {
    avatar: string
    username: string
    bio: string
    followingCount: number
    followerCount: number
    postCount: number
    isMember: boolean
    consecutiveReadingDays: number
    isFollowing: boolean
    isFollower: boolean
    isSelf: boolean
  }
  theme?: ReturnType<typeof useTheme>
}>()

const emit = defineEmits<{
  (e: 'follow-change', isFollowing: boolean): void
  (e: 'start-message'): void
}>()

const theme = props.theme || useTheme()
const cssVars = theme.cssVars

const avatarUrl = computed(() => getAvatarUrl(props.profile?.avatar))
const handleAvatarError = (event: Event) => {
  const img = event.target as HTMLImageElement
  if (img.src !== DEFAULT_AVATAR) img.src = DEFAULT_AVATAR
}

const localIsFollowing = ref(!!props.profile.isFollowing)
watch(
  () => props.profile.isFollowing,
  (v) => {
    localIsFollowing.value = !!v
  },
)

const loading = ref(false)
const startMessage = () => {
  emit('start-message')
}

const toggleFollow = async () => {
  if (loading.value) return
  loading.value = true
  try {
    if (localIsFollowing.value) {
      await unfollowUserApi(props.userId)
      localIsFollowing.value = false
      emit('follow-change', false)
      ElMessage.success('已取消关注')
    } else {
      await followUserApi(props.userId)
      localIsFollowing.value = true
      emit('follow-change', true)
      ElMessage.success('关注成功')
    }
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.message || e?.message || '操作失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.public-user-header {
  background: var(--card-bg, #fff);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--shadow, 0 4px 12px rgba(0, 0, 0, 0.05));
}

.top-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.user-info {
  display: flex;
  gap: 16px;
  align-items: center;
  min-width: 0;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--bg-gray, #f3f4f6);
  flex-shrink: 0;
  border: 3px solid var(--card-bg, #fff);
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1);
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.text-info {
  min-width: 0;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.nickname {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: var(--text-main, #333);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.member-badge {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 999px;
  color: #fff;
  background: var(--primary-green, #42b983);
  flex-shrink: 0;
}

.bio {
  margin: 6px 0 0 0;
  font-size: 14px;
  color: var(--text-light, #999);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.self-hint,
.follower-hint {
  margin-top: 6px;
  font-size: 12px;
  color: var(--text-light, #999);
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.message-btn {
  border: 1px solid #dcdfe6;
  background: #fff;
  color: var(--text-main, #333);
  padding: 8px 14px;
  min-width: 92px;
  height: 38px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s ease;
}

.message-btn:hover {
  background: #f5f7fa;
}

.follow-btn {
  border: 1px solid var(--primary-green, #42b983);
  background: #fff;
  color: var(--primary-green, #42b983);
  padding: 8px 14px;
  min-width: 92px;
  height: 38px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s ease;
}

.follow-btn:hover {
  background: rgba(66, 185, 131, 0.08);
}

.follow-btn.following {
  background: var(--primary-green, #42b983);
  color: #fff;
}

.follow-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.stats-row {
  margin-top: 18px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.stat-item {
  background: var(--bg-gray, #f3f4f6);
  border-radius: 12px;
  padding: 12px 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.num {
  font-size: 18px;
  font-weight: 800;
  color: var(--text-main, #333);
  font-feature-settings: 'tnum';
  font-variant-numeric: tabular-nums;
}

.label {
  margin-top: 4px;
  font-size: 12px;
  color: var(--text-light, #999);
}

@media (max-width: 768px) {
  .top-row {
    flex-direction: column;
    align-items: stretch;
  }
  .follow-btn {
    width: 100%;
  }
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>

