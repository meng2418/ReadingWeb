<!--UserProfile.vue-->
<template>
  <div class="user-profile-card" :style="cssVars">
    <div class="profile-header">
      <div class="user-info">
        <div class="avatar">
          <img :src="user.avatar" alt="用户头像" />
        </div>
        <div class="text-info">
          <h1 class="nickname">{{ user.nickname }}</h1>
          <p class="signature">
            {{ user.signature }}
          </p>
        </div>
      </div>

      <div class="stats-row">
        <button class="stat-item stat-btn" @click="goToUserPosts('following')">
          <span class="num">{{ user.stats.following }}</span>
          <span class="label">关注</span>
        </button>
        <button class="stat-item stat-btn" @click="goToUserPosts('followers')">
          <span class="num">{{ user.stats.followers }}</span>
          <span class="label">粉丝</span>
        </button>
        <button class="stat-item stat-btn" @click="goToUserPosts('posts')">
          <span class="num">{{ user.stats.posts }}</span>
          <span class="label">发布</span>
        </button>

        <div class="actions">
          <button class="icon-btn" @click="openEditDialog">
            <Edit2 :size="16" />
            <span>编辑</span>
          </button>
          <button class="icon-btn" @click="openAppearanceDrawer">
            <Palette :size="16" />
            <span>外观</span>
          </button>
        </div>
      </div>
    </div>

    <div class="card-actions">
      <div class="balance-box">
        <span>体验卡</span>
        <span class="balance-num">{{ user.giftVIP }} 天</span>
      </div>
      <div class="coin-box" @click="openRechargeDialog">
        <span>充值币</span>
        <span class="coin-num">{{ user.payCoin }}</span>
      </div>
      <button class="vip-btn" @click="openVipDialog">
        {{ getVipButtonText() }}
      </button>
    </div>

    <el-dialog
      v-model="editDialogVisible"
      title="编辑个人信息"
      width="500px"
      :append-to-body="true"
    >
      <div class="edit-form">
        <div class="form-group">
          <label>头像</label>
          <div class="avatar-upload">
            <img :src="editForm.avatar" alt="预览" class="avatar-preview" />
            <el-button @click="triggerFileInput">上传图片</el-button>
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              style="display: none"
              @change="handleAvatarUpload"
            />
          </div>
        </div>
        <div class="form-group">
          <label>昵称</label>
          <el-input
            v-model="editForm.nickname"
            placeholder="请输入昵称"
            maxlength="20"
            show-word-limit
          />
        </div>
        <div class="form-group">
          <label>个性签名</label>
          <el-input
            v-model="editForm.signature"
            type="textarea"
            placeholder="请输入个性签名"
            maxlength="200"
            show-word-limit
            :rows="4"
          />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleEditConfirm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <div
      v-if="appearanceDrawerVisible"
      class="appearance-drawer-overlay"
      @click="closeAppearanceDrawer"
    ></div>

    <transition name="slide-up">
      <div v-if="appearanceDrawerVisible" class="appearance-drawer">
        <div class="drawer-header">
          <h3>外观设置</h3>
          <button class="close-btn" @click="closeAppearanceDrawer">
            <X :size="24" />
          </button>
        </div>

        <div class="drawer-content">
          <div class="appearance-section">
            <h4>选择主题色</h4>
            <div class="theme-options">
              <div
                v-for="theme in themes"
                :key="theme.value"
                :class="['theme-option', { active: previewThemeValue === theme.value }]"
                @click="handleThemePreview(theme.value)"
              >
                <div :class="['theme-preview']" :style="getThemePreviewStyle(theme)"></div>
                <span>{{ theme.label }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="drawer-footer">
          <el-button @click="closeAppearanceDrawer">取消</el-button>
          <el-button type="primary" @click="handleAppearanceSave">保存设置</el-button>
        </div>
      </div>
    </transition>
  </div>

  <RechargeDialog ref="rechargeDialogRef" @recharge-success="handleRechargeSuccess" />

  <VipDialog ref="vipDialogRef" @purchase-success="handlePurchaseSuccess" />
</template>

<script setup lang="ts">
import { Edit2, Palette, X } from 'lucide-vue-next' // 导入 X 图标
import { ref, computed } from 'vue'
import RechargeDialog from '@/components/user/RechargeDialog.vue'
import VipDialog from '@/components/user/VipDialog.vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
// 添加路由实例
const router = useRouter()

const props = defineProps<{
  user: any
}>()

// 跳转到UserPosts页面的对应标签页 - 在新标签页打开
const goToUserPosts = (tab: string) => {
  // 在新标签页打开UserPosts页面，并传递tab参数
  window.open(`/userposts?tab=${tab}`, '_blank')
}

// 充值弹窗
const rechargeDialogRef = ref()
const openRechargeDialog = () => {
  // 移除 .value
  rechargeDialogRef.value.open(props.user.payCoin)
}

const handleRechargeSuccess = (option: any) => {
  console.log('充值成功:', option)
  props.user.payCoin += option.amount + (option.bonus || 0)
  ElMessage.success(
    `成功充值${option.amount}书币${option.bonus ? '，赠送${option.bonus}书币' : ''}`,
  )
}

// 会员弹窗
const vipDialogRef = ref()
const openVipDialog = () => {
  vipDialogRef.value.open()
}

const handlePurchaseSuccess = (plan: any) => {
  console.log('会员购买成功:', plan)

  // 计算要添加的天数
  let daysToAdd = 0
  if (plan.duration.includes('7') || plan.name.includes('周')) {
    daysToAdd = 7
  } else if (plan.duration.includes('30') || plan.name.includes('月')) {
    daysToAdd = 30
  } else if (plan.duration.includes('90') || plan.name.includes('季')) {
    daysToAdd = 90
  } else if (plan.duration.includes('365') || plan.name.includes('年')) {
    daysToAdd = 365
  } else {
    // 尝试从duration中提取数字
    const match = plan.duration.match(/\d+/)
    daysToAdd = match ? parseInt(match[0]) : 30
  }

  // 更新会员状态：移除 .value
  props.user.isVip = true

  // 计算新的会员到期时间：移除 .value
  const now = new Date()
  if (props.user.vipEndTime) {
    // 如果已有会员，则在现有到期时间上累加
    const endTime = new Date(props.user.vipEndTime)
    endTime.setDate(endTime.getDate() + daysToAdd)
    props.user.vipEndTime = endTime.toISOString()
    props.user.vipDays = Math.ceil((endTime.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
  } else {
    // 如果没有会员，从今天开始计算
    now.setDate(now.getDate() + daysToAdd)
    props.user.vipEndTime = now.toISOString()
    props.user.vipDays = daysToAdd
  }

  ElMessage.success(`成功开通${plan.name}，有效期${plan.duration}`)
}

// 计算会员按钮显示的文字
const getVipButtonText = () => {
  // 移除 .value
  if (props.user.isVip && props.user.vipDays > 0) {
    return `会员卡 ${props.user.vipDays}天`
  }
  return '成为会员'
}
// --- 主题配置 ---
const themes = [
  {
    value: 'classic-white',
    label: '经典白',
    colors: {
      '--primary-green': '#007c27',
      '--secondary-green': '#1ad6a1',
      '--third-green': '#00a86b',
      '--card-bg': '#ffffff',
      '--text-main': '#1f2937',
      '--text-light': '#9ca3af',
      '--bg-gray': '#f3f4f6',
      '--shadow': '0 4px 12px rgba(0, 0, 0, 0.05)',
    },
    // 用于抽屉里那个小圆块的预览样式
    previewBg: '#ffffff',
  },
  {
    value: 'silent-black',
    label: '沉静黑',
    colors: {
      '--primary-green': '#d4d4d4',
      '--secondary-green': '#404040',
      '--third-green': '#595959',
      '--card-bg': '#0a0a0a',
      '--text-main': '#e0e0e0',
      '--text-light': '#a0a0a0',
      '--bg-gray': '#1a1a1a',
      '--shadow': '0 4px 12px rgba(0, 0, 0, 0.5)',
    },
    previewBg: '#0a0a0a',
  },
  {
    value: 'tranquil-green',
    label: '恬静绿',
    colors: {
      '--primary-green': '#2d5a3d',
      '--secondary-green': '#4a7c59',
      '--third-green': '#6ba876',
      '--card-bg': '#f0f5f3',
      '--text-main': '#1a3a2a',
      '--text-light': '#5c7a68',
      '--bg-gray': '#e1e8e4',
      '--shadow': '0 4px 12px rgba(45, 90, 61, 0.1)',
    },
    previewBg: 'linear-gradient(135deg, #f0f5f3 0%, #d5e6db 100%)',
  },
  {
    value: 'gay-purple',
    label: '基佬紫',
    colors: {
      '--primary-green': '#7c3aed',
      '--secondary-green': '#a78bfa',
      '--third-green': '#c4b5fd',
      '--card-bg': '#f5f3ff',
      '--text-main': '#4c1d95',
      '--text-light': '#8b5cf6',
      '--bg-gray': '#ede9fe',
      '--shadow': '0 4px 12px rgba(124, 58, 237, 0.1)',
    },
    previewBg: 'linear-gradient(135deg, #f5f3ff 0%, #ddd6fe 100%)',
  },
  {
    value: 'sweet-pink',
    label: '甜美粉',
    colors: {
      '--primary-green': '#db2777',
      '--secondary-green': '#f472b6',
      '--third-green': '#fbcfe8',
      '--card-bg': '#fff1f2',
      '--text-main': '#881337',
      '--text-light': '#be123c',
      '--bg-gray': '#ffe4e6',
      '--shadow': '0 4px 12px rgba(219, 39, 119, 0.1)',
    },
    previewBg: 'linear-gradient(135deg, #fff1f2 0%, #fecdd3 100%)',
  },
]

// --- 状态管理 ---

const editDialogVisible = ref(false)
// 移除 .value，直接复制 prop 对象
const editForm = ref({ ...props.user })
const fileInput = ref<HTMLInputElement | null>(null)
const appearanceDrawerVisible = ref(false)

// activeThemeValue: 当前真正生效、已保存的主题
const activeThemeValue = ref(localStorage.getItem('theme') || 'classic-white')
// previewThemeValue: 当前正在预览的主题（随点击变化，未保存时可回滚）
const previewThemeValue = ref(activeThemeValue.value)
// 新增：用于存储待上传的 File 对象
const tempAvatarFile = ref<File | null>(null)
const cssVars = computed(() => {
  const current = themes.find((t) => t.value === previewThemeValue.value)
  return current ? current.colors : {}
})

// --- 方法 ---

// 获取颜色块的预览样式
const getThemePreviewStyle = (theme: any) => {
  return {
    background: theme.previewBg,
    border: '1px solid rgba(0,0,0,0.1)',
  }
}

// 打开编辑
const openEditDialog = () => {
  editForm.value = { ...props.user }
  editDialogVisible.value = true
}

const handleEditConfirm = () => {
  if (!editForm.value.nickname.trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }
  Object.assign(props.user, editForm.value)

  // 处理头像上传和更新
  if (tempAvatarFile.value) {
    ElMessage.info('正在模拟上传新头像...')
    // 演示代码：直接使用 DataURL 作为新的 Avatar URL
    // 移除 .value
    props.user.avatar = editForm.value.avatar
  }
  // 3. 清理状态并关闭
  tempAvatarFile.value = null // 清理临时文件
  ElMessage.success('信息更新成功')
  editDialogVisible.value = false
}
// 触发文件上传

const triggerFileInput = () => {
  fileInput.value?.click()
}

// 处理头像上传

const handleAvatarUpload = (event: Event) => {
  const target = event.target as HTMLInputElement

  const file = target.files?.[0]

  if (file) {
    const reader = new FileReader()

    reader.onload = (e) => {
      editForm.value.avatar = e.target?.result as string
    }

    reader.readAsDataURL(file)
  }
}
// 打开外观抽屉
const openAppearanceDrawer = () => {
  // 打开时，预览值 = 当前生效值
  previewThemeValue.value = activeThemeValue.value
  appearanceDrawerVisible.value = true
}

// 点击颜色块（预览）
const handleThemePreview = (val: string) => {
  previewThemeValue.value = val
  // 不需要 applyTheme 函数，computed cssVars 会自动响应更新 DOM
}

// 关闭抽屉（取消）
const closeAppearanceDrawer = () => {
  if (previewThemeValue.value !== activeThemeValue.value) {
    previewThemeValue.value = activeThemeValue.value
  }
  appearanceDrawerVisible.value = false
}

// 保存设置
const handleAppearanceSave = () => {
  activeThemeValue.value = previewThemeValue.value
  localStorage.setItem('theme', activeThemeValue.value)
  ElMessage.success('外观设置已保存')
  appearanceDrawerVisible.value = false
}
</script>

<style scoped>
/* 所有颜色值都使用 CSS 变量，变量由父容器的 style 动态注入 
  这样就不需要 applyTheme 这种复杂的 JS 操作 DOM 逻辑了
*/

.user-profile-card {
  /* 默认变量兜底，防止加载瞬间闪烁 */
  --card-bg: #fff;
  --text-main: #333;
  --bg-gray: #f3f4f6;
  --primary-green: #007c27;

  background: var(--card-bg);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--shadow, 0 4px 12px rgba(0, 0, 0, 0.05));
  transition:
    background 0.3s ease,
    color 0.3s ease;
  position: relative;
  overflow: hidden; /* 防止抽屉溢出 */
}

/* 顶部用户信息 */
.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 3px solid var(--card-bg); /* 跟随背景色 */
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1);
  background: var(--bg-gray);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.text-info {
  display: flex;
  flex-direction: column;
  max-width: 650px;
}

.nickname {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: var(--text-main);
  transition: color 0.3s ease;
}

.signature {
  margin: 4px 0 0 0;
  color: var(--text-light);
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  transition: color 0.3s ease;
}

/* 数据统计行 */
.stats-row {
  display: flex;
  gap: 24px;
  align-items: center;
  flex-shrink: 0;
}

/* 新增：统计按钮样式 */
.stat-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  transition:
    transform 0.2s ease,
    opacity 0.2s ease;
}

.stat-btn:hover {
  transform: translateY(-2px);
  opacity: 0.9;
}

.stat-btn:active {
  transform: translateY(0);
}

.stat-btn .num {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-main);
  transition: color 0.2s ease;
}

.stat-btn:hover .num {
  color: var(--primary-green);
}

.stat-btn .label {
  font-size: 12px;
  color: var(--text-light);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-item .num {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-main);
  transition: color 0.3s ease;
}

.stat-item .label {
  font-size: 12px;
  color: var(--text-light);
  transition: color 0.3s ease;
}

/* 按钮操作 */
.actions {
  display: flex;
  gap: 12px;
}

.icon-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: var(--bg-gray);
  border: none;
  border-radius: 8px;
  padding: 8px 12px;
  cursor: pointer;
  font-size: 14px;
  color: var(--text-main);
  transition: all 0.3s;
}

.icon-btn:hover {
  background: var(--secondary-green);
  color: #fff;
}

/* 底部卡片操作 */
.card-actions {
  margin-top: 24px;
  display: flex;
  gap: 16px;
}

.balance-box {
  border: 1px solid rgba(0, 0, 0, 0.05);
  padding: 10px 24px;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 12px;
  color: var(--text-light);
  min-width: 80px;
  background: var(--bg-gray);
  transition: all 0.3s ease;
}

.balance-num {
  font-size: 16px;
  font-weight: bold;
  color: var(--text-main);
  margin-top: 4px;
  transition: color 0.3s ease;
}

.coin-box {
  background: var(--primary-green);
  border: none;
  color: #fff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  padding: 10px 24px;
  cursor: pointer;
  transition: all 0.3s;
  min-width: 80px;
  align-items: center;
  font-size: 12px;
}

.coin-box:hover {
  background: var(--secondary-green);
}

.coin-num {
  font-size: 16px;
  font-weight: bold;
  margin-top: 4px;
}

.vip-btn {
  background: var(--primary-green);
  border: none;
  color: #fff;
  padding: 0 32px;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s;
}

.vip-btn:hover {
  background: var(--secondary-green);
}

/* 编辑表单样式 */
.edit-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.avatar-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-preview {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  border: 1px solid #e0e0e0;
}
/* --- 外观抽屉样式 (保留第一版的自定义设计) --- */
.appearance-drawer-overlay {
  position: absolute; /* 改为 absolute 以限制在卡片内，或者 fixed 全屏 */
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 10;
  border-radius: 16px; /* 适配卡片圆角 */
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.appearance-drawer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--card-bg); /* 抽屉背景也跟随主题 */
  border-radius: 16px 16px 16px 16px;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.15);
  z-index: 20;
  display: flex;
  flex-direction: column;
  transition: background 0.3s ease;
  overflow: hidden;
}

.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.drawer-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-main);
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  color: var(--text-light);
}

.drawer-content {
  padding: 24px;
}

.appearance-section h4 {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: var(--text-light);
}

.theme-options {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.theme-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.2s;
}

.theme-option:hover {
  background: var(--bg-gray);
}

.theme-option.active .theme-preview {
  transform: scale(1.1);
  box-shadow: 0 0 0 2px var(--primary-green);
}

.theme-preview {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.theme-option span {
  font-size: 12px;
  color: var(--text-main);
}

.drawer-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding: 16px 24px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  background: var(--bg-gray); /* 底部稍微深一点 */
}

/* 动画 */
.slide-up-enter-active {
  animation: slideUp 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}
.slide-up-leave-active {
  animation: slideUp 0.3s cubic-bezier(0.16, 1, 0.3, 1) reverse;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

/* 响应式适配 */
@media (max-width: 600px) {
  .profile-header {
    flex-direction: column;
    align-items: flex-start;
  }
  .stats-row {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
