<template>
  <div class="book-detail-header">
    <!-- 上半部分：封面和基本信息 -->
    <div class="top-section">
      <!-- 左侧：书籍封面 -->
      <div class="book-cover">
        <img :src="coverImage" :alt="title" class="cover-image" @error="handleImageError" />
        <div v-if="!coverImage" class="cover-placeholder">
          <span>暂无封面</span>
        </div>
      </div>

      <!-- 右侧：书籍信息 -->
      <div class="book-info">
        <!-- 书名和作者 -->
        <h1 class="book-title">{{ title }}</h1>
        <div class="book-author">{{ author }}</div>

        <!-- 右上角：操作按钮 -->
        <div class="action-buttons">
          <button
            class="bookshelf-button"
            :class="{ added: isInBookshelf }"
            @click="handleBookshelfToggle"
          >
            {{ isInBookshelf ? '✔已在书架' : '加入书架' }}
          </button>

          <button class="read-button" @click="handleStartReading">开始阅读</button>
        </div>
        <!-- 统计卡片 -->
        <div class="stats-cards">
          <!-- 前三个卡片改为纯显示，移除点击事件和cursor:pointer -->
          <div class="stat-card display-only">
            <div class="stat-label">阅读</div>
            <div class="stat-value">{{ stats.readingCount }}</div>
            <div class="stat-subtitle">{{ stats.readingSubtitle }}</div>
          </div>

          <div class="stat-card display-only">
            <div class="stat-label">我的阅读</div>
            <div class="stat-value">{{ stats.myReadingStatus }}</div>
            <div class="stat-subtitle">{{ stats.myReadingSubtitle }}</div>
          </div>

          <div class="stat-card display-only">
            <div class="stat-label">字数</div>
            <div class="stat-value">{{ stats.wordCount }}</div>
            <div class="stat-subtitle">{{ stats.publishInfo }}</div>
          </div>

          <!-- 体验卡可读卡片，保留点击功能 -->
          <div
            :class="isPurchased ? 'stat-card display-only purchased' : 'stat-card clickable'"
            @click="!isPurchased && handleExperienceCardClick()"
          >
            <el-icon><ShoppingCart /></el-icon>
            <div class="stat-value">{{ isPurchased ? '已购买' : stats.experienceCardStatus }}</div>
            <div class="stat-subtitle">{{ stats.priceInfo }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 下半部分：书籍简介 -->
    <div class="book-description-section">
      <h3 class="description-title">书籍简介</h3>
      <div class="book-description">{{ description }}</div>
    </div>

    <!-- 购买确认对话框 -->
    <el-dialog
      v-model="purchaseDialogVisible"
      title="购买确认"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div class="purchase-dialog-content">
        <!-- 主要购买信息 -->
        <div class="purchase-info">
          <div class="purchase-title">购买本书需要支付</div>
          <div class="coin-required">
            <span class="coin-amount">{{ requiredCoins }}</span>
            <span class="coin-label">充值币</span>
          </div>
        </div>

        <!-- 充值币余额信息 -->
        <div class="balance-section">
          <div class="balance-left">
            <div class="balance-title">充值币余额</div>
            <div class="balance-amount">{{ userPayCoin }}</div>
          </div>
          <button class="recharge-btn" @click="handleRechargeClick">
            去充值
          </button>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <button class="purchase-btn primary" @click="confirmPurchase">
            立即购买
          </button>
          <button class="purchase-btn cancel" @click="purchaseDialogVisible = false">
            取消购买
          </button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ShoppingCart } from '@element-plus/icons-vue'
import router from '@/router'
import { ElMessage, ElMessageBox } from 'element-plus'

// 定义props
interface Props {
  title: string
  author: string
  description: string
  coverImage?: string
  initialBookshelfStatus?: boolean
  bookId?: string | number
  stats: {
    readingCount: string
    readingSubtitle: string
    myReadingStatus: string
    myReadingSubtitle: string
    wordCount: string
    publishInfo: string
    experienceCardStatus: string
    priceInfo: string
  }
  // 添加用户充值币信息（可以从父组件传递）
  userPayCoin?: number
  // 添加是否已购买的初始状态
  initialPurchased?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  coverImage: '',
  initialBookshelfStatus: false,
  stats: () => ({
    readingCount: '18.3万人',
    readingSubtitle: '7.6万人读完',
    myReadingStatus: '在读',
    myReadingSubtitle: '标记在读',
    wordCount: '11.3万字',
    publishInfo: '2021年7月出版',
    experienceCardStatus: '体验卡可读',
    priceInfo: '电子书价格49元',
  }),
  userPayCoin: 180, // 默认值，与UserProfile.vue中的payCoin一致
  initialPurchased: false // 默认未购买
})

// 定义事件
const emit = defineEmits<{
  toggleBookshelf: [isAdded: boolean]
  startReading: []
  statClick: [statType: string]
  openRechargeDialog: [] // 添加打开充值弹窗的事件
}>()

// 本地状态
const isInBookshelf = ref(props.initialBookshelfStatus)
const isPurchased = ref(props.initialPurchased)
const purchaseDialogVisible = ref(false)
const requiredCoins = 50 // 假设本书需要50充值币

// 图片加载失败处理
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
}

// 加入书架切换
const handleBookshelfToggle = () => {
  isInBookshelf.value = !isInBookshelf.value
  emit('toggleBookshelf', isInBookshelf.value)
  console.log(`书籍${isInBookshelf.value ? '已加入' : '已移除'}书架`)
}

// 开始阅读
const handleStartReading = () => {
  emit('startReading')
  if (props.bookId) {
    router.push(`/reader/${props.bookId}/1`)
  } else {
    router.push('/reader')
  }
  console.log('开始阅读')
}

// 统计卡片点击 - 只处理体验卡可读
const handleStatClick = (statType: string) => {
  emit('statClick', statType)
  console.log(`点击统计卡片: ${statType}`)
}

// 体验卡可读点击
const handleExperienceCardClick = () => {
  // 如果已经购买，直接返回，不弹出购买对话框
  if (isPurchased.value) return

  handleStatClick('experienceCard')
  purchaseDialogVisible.value = true
}

// 跳转到充值页面
const goToRecharge = () => {
  console.log('跳转到充值页面')
  purchaseDialogVisible.value = false
  emit('openRechargeDialog')
  // router.push('/recharge') // 如果需要跳转，取消注释
}

// 检查余额是否足够
const checkBalance = () => {
  return props.userPayCoin >= requiredCoins
}

// 显示余额不足提示
const showInsufficientBalanceAlert = () => {
  ElMessageBox.alert(
    `您的充值币余额不足，当前余额${props.userPayCoin}充值币，购买本书需要${requiredCoins}充值币。`,
    '余额不足',
    {
      confirmButtonText: '去充值',
      type: 'warning',
      callback: (action: string) => {
        if (action === 'confirm') {
          goToRecharge()
        }
      }
    }
  )
}

// 确认购买
const confirmPurchase = () => {
  // 检查余额是否足够
  if (!checkBalance()) {
    // 余额不足，显示提示框
    showInsufficientBalanceAlert()
    return
  }

  // 余额足够，显示确认购买对话框
  ElMessageBox.confirm(
    `确定使用${requiredCoins}充值币购买本书吗？`,
    '确认购买',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 用户确认购买
    isPurchased.value = true // 设置已购买状态
    ElMessage.success('购买成功！')
    purchaseDialogVisible.value = false

    // 这里可以添加实际购买逻辑，如更新用户余额等
    // 例如：更新用户余额，调用API等
    console.log(`购买成功，扣除${requiredCoins}充值币`)

  }).catch(() => {
    // 用户取消购买
    console.log('取消购买')
  })
}
</script>

<style scoped>
.book-detail-header {
  /* 移除固定最大宽度，让它填充父容器 */
  width: 100%;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  position: relative; /* 为对话框定位 */
}

/* 上半部分样式 */
.top-section {
  display: flex;
  gap: 30px;
  padding: 30px;
  border-bottom: 1px solid #f0f0f0;
  align-items: flex-start;
}

/* 书籍封面样式 */
.book-cover {
  flex-shrink: 0;
  width: 200px;
  height: 280px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  margin-top: 20px;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
}

/* 书籍信息样式 */
.book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  margin-top: 15px;
}

/* 按钮区域样式 - 放在右上角 */
.action-buttons {
  display: flex;
  gap: 20px;
  justify-content: flex-end;
  margin-bottom: 25px;
}

.bookshelf-button,
.read-button {
  padding: 12px 24px;
  border: 2px solid #007cba;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 120px;
  text-align: center;
}

.bookshelf-button {
  background: #fff;
  color: #007cba;
}

.bookshelf-button:hover {
  background: #f0f8ff;
  transform: translateY(-2px);
}

.bookshelf-button.added {
  background: #007cba;
  color: white;
}

.bookshelf-button.added:hover {
  background: #005a87;
  transform: translateY(-2px);
}

.read-button {
  background: #007cba;
  color: white;
}

.read-button:hover {
  background: #005a87;
  transform: translateY(-2px);
}

/* 书名和作者样式 */
.book-title {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin: 0 0 5px 0;
  line-height: 1.2;
}

.book-author {
  font-size: 18px;
  color: #666;
  font-weight: 500;
  margin-bottom: 30px;
}

/* 统计卡片样式 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  align-items: center;
  align-items: stretch;
}

.stat-card {
  background: #f0f0f0;
  padding: 4px;
  text-align: center;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 100px;
  border-radius: 8px;
  align-items: center;
}

/* 前三个卡片：纯显示，不可点击 */
.stat-card.display-only {
  cursor: default; /* 鼠标样式改为默认 */
  background: #f0f0f0; /* 保持原有背景色 */
}

.stat-card.display-only:hover {
  background: #f0f0f0; /* 悬停时保持原背景色 */
  transform: none; /* 移除悬停效果 */
}

/* 已购买状态样式 */
.stat-card.purchased {
  cursor: default;
  background: #f0f0f0;
}

.stat-card.purchased:hover {
  background: #f0f0f0;
  transform: none;
}

/* 已购买状态的图标颜色可以稍微调整，表示已拥有 */
.stat-card.purchased .el-icon {
  color: #52c41a; /* 绿色表示已购买 */
}

/* 可点击的卡片：体验卡可读 */
.stat-card.clickable {
  cursor: pointer; /* 保持手型指针 */
}

.stat-card.clickable:hover {
  background: #f9f9f9;
  transform: translateY(-2px);
}

/* 图标样式 */
.stat-card .el-icon {
  font-size: 24px;
  color: #007cba;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 16px;
  color: #666;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.stat-subtitle {
  font-size: 12px;
  color: #999;
}

/* 下半部分：书籍简介样式 */
.book-description-section {
  padding: 30px;
}

.description-title {
  font-size: 30px;
  font-weight: bold;
  color: #333;
  margin: 0 0 15px 0;
}

.book-description {
  font-size: 20px;
  line-height: 1.6;
  color: #555;
}

/* 购买对话框样式 */
.purchase-dialog-content {
  padding: 0 20px;
}

.purchase-info {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.purchase-title {
  font-size: 16px;
  color: #666;
  margin-bottom: 10px;
}

.coin-required {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.coin-amount {
  font-size: 32px;
  font-weight: bold;
  color: #333;
}

.coin-label {
  font-size: 18px;
  color: #666;
}

.balance-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
}

.balance-left {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.balance-title {
  font-size: 14px;
  color: #666;
}

.balance-amount {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.recharge-btn {
  padding: 10px 20px;
  background: #fff;
  border: 1px solid #007cba;
  border-radius: 6px;
  color: #007cba;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.recharge-btn:hover {
  background: #f0f8ff;
  transform: translateY(-1px);
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
  width: 100%;
}

.purchase-btn {
  padding: 12px 30px;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 120px;
}

.purchase-btn.primary {
  background: #007cba;
  color: white;
}

.purchase-btn.primary:hover {
  background: #005a87;
  transform: translateY(-2px);
}

.purchase-btn.cancel {
  background: #f0f0f0;
  color: #666;
}

.purchase-btn.cancel:hover {
  background: #e0e0e0;
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .top-section {
    flex-direction: column;
    padding: 20px;
    gap: 20px;
  }

  .book-cover {
    width: 150px;
    height: 210px;
    align-self: center;
    margin-top: 0;
  }

  .book-title {
    font-size: 24px;
    text-align: center;
  }

  .book-author {
    text-align: center;
  }

  .action-buttons {
    justify-content: center;
    flex-wrap: wrap;
  }

  .bookshelf-button,
  .read-button {
    flex: 1;
    min-width: 120px;
    padding: 10px 20px;
  }

  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .book-description-section {
    padding: 20px;
  }

  .description-title {
    font-size: 24px;
  }

  .book-description {
    font-size: 16px;
  }

  .dialog-footer {
    flex-direction: column;
    gap: 10px;
  }

  .purchase-btn {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .top-section {
    padding: 15px;
  }

  .book-cover {
    width: 120px;
    height: 168px;
  }

  .book-title {
    font-size: 20px;
  }

  .book-author {
    font-size: 16px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .bookshelf-button,
  .read-button {
    width: 100%;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }

  .book-description-section {
    padding: 15px;
  }

  .description-title {
    font-size: 20px;
  }

  .book-description {
    font-size: 14px;
  }
}
</style>
