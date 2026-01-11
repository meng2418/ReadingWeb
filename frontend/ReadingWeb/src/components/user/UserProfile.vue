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
                v-for="themeOption in themes"
                :key="themeOption.value"
                :class="['theme-option', { active: theme.previewThemeValue.value === themeOption.value }]"
                @click="handleThemePreview(themeOption.value)"
              >
                <div :class="['theme-preview']" :style="getThemePreviewStyle(themeOption)"></div>
                <span>{{ themeOption.label }}</span>
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
import { useTheme, themes } from '@/composables/useTheme'
import { updateProfile } from '@/api/profile'
// 添加路由实例
const router = useRouter()

const props = defineProps<{
  user: any
  theme?: ReturnType<typeof useTheme>
}>()

const emit = defineEmits<{
  (e: 'theme-change', theme: string): void
  (e: 'profile-updated'): void
}>()

// 使用传入的主题，如果没有则创建新的主题实例
const theme = props.theme || useTheme()

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
  // 使用 coinAmount 和 bonusCoins，如果没有则使用 amount 和 bonus
  const coinAmount = option.coinAmount || option.amount || 0
  const bonusCoins = option.bonusCoins || option.bonus || 0
  props.user.payCoin += coinAmount + bonusCoins
  ElMessage.success(
    `成功充值${coinAmount}充值币${bonusCoins > 0 ? `，赠送${bonusCoins}充值币` : ''}`,
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
// --- 状态管理 ---

const editDialogVisible = ref(false)
// 移除 .value，直接复制 prop 对象
const editForm = ref({ ...props.user })
const fileInput = ref<HTMLInputElement | null>(null)
const appearanceDrawerVisible = ref(false)

// 新增：用于存储待上传的 File 对象
const tempAvatarFile = ref<File | null>(null)

// 使用主题的 CSS 变量（仅用于当前组件）
const cssVars = theme.cssVars

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
  tempAvatarFile.value = null // 重置临时文件
  editDialogVisible.value = true
}

const handleEditConfirm = async () => {
  // 验证昵称（只有当昵称改变时才验证）
  const nicknameChanged = editForm.value.nickname !== props.user.nickname
  
  if (nicknameChanged) {
    const trimmedNickname = editForm.value.nickname?.trim()
    
    if (!trimmedNickname) {
      ElMessage.warning('昵称不能为空')
      return
    }

    // 验证用户名格式（2-20个字符，只能包含中文、英文、数字、下划线和减号）
    const usernameRegex = /^[\u4e00-\u9fa5a-zA-Z0-9\-_]+$/
    if (!usernameRegex.test(trimmedNickname)) {
      ElMessage.warning('用户名只能包含中文、英文、数字、下划线和减号')
      return
    }

    if (trimmedNickname.length < 2 || trimmedNickname.length > 20) {
      ElMessage.warning('用户名长度必须在2-20个字符之间')
      return
    }
  }

  // 验证个性签名长度
  if (editForm.value.signature && editForm.value.signature.length > 200) {
    ElMessage.warning('个性签名不能超过200个字符')
    return
  }

  try {
    // 构建请求参数，进行字段映射：nickname -> username, signature -> bio
    const updateParams: {
      username?: string
      avatar?: string
      bio?: string
    } = {}

    // 只有字段有变化时才添加到请求中
    const nicknameChanged = editForm.value.nickname !== props.user.nickname
    const signatureChanged = editForm.value.signature !== props.user.signature
    const avatarChanged = editForm.value.avatar !== props.user.avatar

    if (nicknameChanged) {
      updateParams.username = editForm.value.nickname.trim()
    }

    if (signatureChanged) {
      // 如果个性签名为空，传空字符串；否则传实际值
      // 注意：如果用户清空了签名，我们也需要更新（传空字符串）
      const trimmedSignature = editForm.value.signature?.trim() || ''
      updateParams.bio = trimmedSignature
    }

    // 处理头像：如果头像发生了变化（可能是base64或URL）
    if (avatarChanged) {
      // 确保头像值不为空
      if (editForm.value.avatar) {
        // 检查是否是 base64 格式（以 data: 开头）
        const isBase64 = editForm.value.avatar.startsWith('data:')
        
        if (isBase64) {
          // base64 字符串可能很长，检查长度
          // 数据库字段长度限制是 2000 字符
          const base64Length = editForm.value.avatar.length
          console.log('准备上传的头像 base64 长度:', base64Length, '字符')
          
          const maxLength = 2000
          if (base64Length > maxLength) {
            ElMessage.error(`头像图片过大（${(base64Length / 1024).toFixed(1)}KB），超过数据库限制（${maxLength}字符），请选择更小的图片`)
            return
          }
        }
        
        updateParams.avatar = editForm.value.avatar
      }
    }

    // 如果没有需要更新的字段，直接关闭
    if (Object.keys(updateParams).length === 0) {
      editDialogVisible.value = false
      ElMessage.info('没有需要更新的内容')
      return
    }

    console.log('准备更新资料，参数:', updateParams)

    // 调用API更新资料
    const updatedData = await updateProfile(updateParams)

    // 更新本地用户数据
    props.user.nickname = updatedData.username
    props.user.signature = updatedData.bio
    props.user.avatar = updatedData.avatar

    // 清理状态
    tempAvatarFile.value = null
    editDialogVisible.value = false

    // 通知父组件刷新数据
    emit('profile-updated')

    ElMessage.success('个人信息更新成功')
  } catch (error: any) {
    console.error('更新资料失败:', error)
    console.error('错误详情:', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      message: error.message,
    })

    // 尝试从响应中提取错误信息
    let errorMessage = '更新资料失败，请稍后重试'
    
    if (error.response) {
      const responseData = error.response.data
      
      // 如果是验证错误，尝试提取字段错误信息
      if (responseData?.errors) {
        const errors = responseData.errors
        const errorMessages = Object.values(errors).flat() as string[]
        errorMessage = errorMessages.join('; ') || errorMessage
      } else if (responseData?.message) {
        errorMessage = responseData.message
      } else if (typeof responseData === 'string') {
        errorMessage = responseData
      } else if (error.response.status === 400) {
        // 400 错误可能是验证失败
        errorMessage = '数据验证失败，请检查输入内容是否符合要求'
      }
    } else if (error.message) {
      errorMessage = error.message
    }

    ElMessage.error(errorMessage)
  }
}
// 触发文件上传

const triggerFileInput = () => {
  fileInput.value?.click()
}

// 图片压缩函数（智能压缩，逐步降低质量直到满足长度要求）
const compressImage = (
  file: File,
  maxWidth: number = 150,
  maxHeight: number = 150,
  initialQuality: number = 0.7,
  maxBase64Length: number = 1800, // 1800 字符，留一些余量（数据库限制是 2000）
): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = (e) => {
      const img = new Image()
      img.onload = () => {
        // 计算压缩后的尺寸
        let width = img.width
        let height = img.height

        // 计算缩放比例
        const scale = Math.min(maxWidth / width, maxHeight / height, 1)
        width = Math.round(width * scale)
        height = Math.round(height * scale)

        // 创建 canvas 进行压缩
        const canvas = document.createElement('canvas')
        canvas.width = width
        canvas.height = height
        const ctx = canvas.getContext('2d')

        if (!ctx) {
          reject(new Error('无法创建画布上下文'))
          return
        }

        // 设置图片平滑处理
        ctx.imageSmoothingEnabled = true
        ctx.imageSmoothingQuality = 'high'

        // 绘制压缩后的图片
        ctx.drawImage(img, 0, 0, width, height)

        // 尝试不同的质量级别，直到满足长度要求
        const tryCompress = (quality: number): string | null => {
          const base64 = canvas.toDataURL('image/jpeg', quality)
          if (base64.length <= maxBase64Length) {
            return base64
          }
          return null
        }

        // 从初始质量开始，逐步降低
        let quality = initialQuality
        let result: string | null = null
        const qualitySteps = [0.8, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1]

        for (const q of qualitySteps) {
          result = tryCompress(q)
          if (result) {
            resolve(result)
            return
          }
        }

        // 如果还是太大，进一步缩小尺寸
        if (!result) {
          const smallerWidth = Math.round(width * 0.8)
          const smallerHeight = Math.round(height * 0.8)
          canvas.width = smallerWidth
          canvas.height = smallerHeight
          ctx.drawImage(img, 0, 0, smallerWidth, smallerHeight)
          
          // 再次尝试压缩
          for (const q of [0.5, 0.4, 0.3, 0.2]) {
            result = tryCompress(q)
            if (result) {
              resolve(result)
              return
            }
          }
        }

        // 如果还是太大，使用最低质量和更小的尺寸
        let finalWidth = 120
        let finalHeight = 120
        const finalScale = Math.min(finalWidth / width, finalHeight / height, 1)
        finalWidth = Math.round(width * finalScale)
        finalHeight = Math.round(height * finalScale)
        
        canvas.width = finalWidth
        canvas.height = finalHeight
        ctx.drawImage(img, 0, 0, finalWidth, finalHeight)
        
        // 尝试不同的质量，确保不超过限制
        for (const q of [0.3, 0.25, 0.2, 0.15, 0.1, 0.05]) {
          const testBase64 = canvas.toDataURL('image/jpeg', q)
          if (testBase64.length <= maxBase64Length) {
            resolve(testBase64)
            return
          }
        }
        
        // 如果还是太大，使用最小尺寸和最低质量
        finalWidth = 100
        finalHeight = 100
        canvas.width = finalWidth
        canvas.height = finalHeight
        ctx.drawImage(img, 0, 0, finalWidth, finalHeight)
        
        const finalBase64 = canvas.toDataURL('image/jpeg', 0.1)
        // 如果还是超过，至少返回一个结果（让后端处理或提示用户）
        resolve(finalBase64)
      }
      img.onerror = () => {
        reject(new Error('图片加载失败'))
      }
      img.src = e.target?.result as string
    }
    reader.onerror = () => {
      reject(new Error('文件读取失败'))
    }
    reader.readAsDataURL(file)
  })
}

// 处理头像上传
const handleAvatarUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]

  if (!file) {
    return
  }

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }

  // 验证文件大小（限制为10MB，压缩后会变小）
  const maxSize = 10 * 1024 * 1024 // 10MB
  if (file.size > maxSize) {
    ElMessage.warning('图片大小不能超过10MB')
    return
  }

  try {
    // 显示压缩提示
    const loadingMessage = ElMessage({
      message: '正在压缩图片...',
      type: 'info',
      duration: 0, // 不自动关闭
    })

    // 压缩图片（智能压缩，自动调整到合适大小）
    // 目标：压缩到 1800 字符以内（数据库限制是 2000 字符）
    const compressedBase64 = await compressImage(file, 150, 150, 0.7, 1800)

    // 关闭加载提示
    loadingMessage.close()

    // 检查压缩后的 base64 长度
    let base64Length = compressedBase64.length
    console.log('压缩后的 base64 长度:', base64Length, '字符')

    // 检查是否超过数据库限制（2000字符）
    const maxLength = 2000
    if (base64Length > maxLength) {
      console.warn(`压缩后的图片仍然超过数据库字段限制（${maxLength}字符），尝试进一步压缩...`)
      
      // 尝试进一步压缩
      try {
        const furtherCompressed = await compressImage(file, 100, 100, 0.5, maxLength)
        base64Length = furtherCompressed.length
        console.log('进一步压缩后的 base64 长度:', base64Length, '字符')
        
        if (base64Length > maxLength) {
          // 最后一次尝试：最小尺寸和最低质量
          const finalCompressed = await compressImage(file, 80, 80, 0.3, maxLength)
          base64Length = finalCompressed.length
          console.log('最终压缩后的 base64 长度:', base64Length, '字符')
          
          if (base64Length > maxLength) {
            ElMessage.error(`图片过大，无法压缩到 ${maxLength} 字符以内（当前: ${base64Length} 字符）。请选择更小的图片。`)
            return
          }
          
          editForm.value.avatar = finalCompressed
        } else {
          editForm.value.avatar = furtherCompressed
        }
      } catch (error) {
        ElMessage.error('图片压缩失败，请选择更小的图片')
        return
      }
    } else {
      editForm.value.avatar = compressedBase64
    }

    ElMessage.success(`图片已压缩（${(base64Length / 1024).toFixed(1)}KB，${base64Length} 字符）`)

    // 保存原始文件对象（如果需要）
    tempAvatarFile.value = file
  } catch (error: any) {
    console.error('图片压缩失败:', error)
    ElMessage.error('图片压缩失败: ' + (error.message || '未知错误'))
  }
}
// 打开外观抽屉
const openAppearanceDrawer = () => {
  // 打开时，预览值 = 当前生效值
  theme.resetPreview()
  appearanceDrawerVisible.value = true
}

// 点击颜色块（预览）
const handleThemePreview = (val: string) => {
  theme.previewThemeValue.value = val
  emit('theme-change', val)
}

// 关闭抽屉（取消）
const closeAppearanceDrawer = () => {
  theme.resetPreview()
  appearanceDrawerVisible.value = false
}

// 保存设置
const handleAppearanceSave = () => {
  theme.saveTheme()
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
