<template>
  <div class="book-detail-header">
    <!-- 上半部分：封面和基本信息 -->
    <div class="top-section">
      <!-- 左侧：书籍封面 -->
      <div class="book-cover">
        <img 
          :src="coverImage" 
          :alt="title" 
          class="cover-image" 
          @error="handleImageError" 
        />
        <div v-if="!coverImage" class="cover-placeholder">
          <span>暂无封面</span>
        </div>
      </div>

      <!-- 右侧：书籍信息 -->
      <div class="book-info">
        <!-- 书名和作者 -->
        <!-- 添加 :title 属性，悬停显示完整内容 -->
        <h1 class="book-title" :title="title">{{ title }}</h1>
        <div class="book-author" :title="author">{{ author }}</div>

        <!-- 右上角：操作按钮 -->
        <div class="action-buttons">
          <button
            class="bookshelf-button"
            :class="{ added: isInBookshelf }"
            @click="handleBookshelfToggle"
          >
            {{ isInBookshelf ? '已在书架' : '加入书架' }}
          </button>

          <button class="read-button" @click="handleStartReading">开始阅读</button>
        </div>
        
        <!-- 统计卡片 -->
        <div class="stats-cards">
          <!-- 前三个卡片：纯展示 -->
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

          <!-- 购买/体验卡片：可点击 -->
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
      <!-- 添加 :title 属性，悬停显示完整简介 -->
      <div class="book-description" :title="description">{{ description }}</div>
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
import { ref, computed } from 'vue'
import { ShoppingCart } from '@element-plus/icons-vue'
import router from '@/router'
import { ElMessage, ElMessageBox } from 'element-plus'

// 定义 Props 接口
interface BookStats {
  readingCount: string
  readingSubtitle: string
  myReadingStatus: string
  myReadingSubtitle: string
  wordCount: string
  publishInfo: string
  experienceCardStatus: string
  priceInfo: string
}

interface Props {
  title: string
  author: string
  description: string
  coverImage?: string
  initialBookshelfStatus?: boolean
  bookId?: string | number
  stats?: BookStats
  userPayCoin?: number
  initialPurchased?: boolean
  // 新增：价格属性，用于替代之前写死的50币
  price?: number
}

// 设置 Props 默认值 (已去除静态业务数据)
const props = withDefaults(defineProps<Props>(), {
  coverImage: '',
  initialBookshelfStatus: false,
  // 提供空的结构体防止模板报错
  stats: () => ({
    readingCount: '-',
    readingSubtitle: '',
    myReadingStatus: '未读',
    myReadingSubtitle: '',
    wordCount: '-',
    publishInfo: '',
    experienceCardStatus: '体验卡可读',
    priceInfo: ''
  }),
  userPayCoin: 0,
  initialPurchased: false,
  price: 0
})

// 定义事件
const emit = defineEmits<{
  toggleBookshelf: [isAdded: boolean]
  startReading: []
  statClick: [statType: string]
  openRechargeDialog: []
  purchaseSuccess: [] // 新增：购买成功事件
}>()

// 本地状态
const isInBookshelf = ref(props.initialBookshelfStatus)
const isPurchased = ref(props.initialPurchased)
const purchaseDialogVisible = ref(false)

// 计算所需代币 (从 Props 获取，不再写死)
const requiredCoins = computed(() => props.price || 0)

// 图片加载失败处理
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
}

// 加入书架切换
const handleBookshelfToggle = () => {
  isInBookshelf.value = !isInBookshelf.value
  emit('toggleBookshelf', isInBookshelf.value)
}

// 开始阅读
const handleStartReading = () => {
  emit('startReading')
  if (props.bookId) {
    router.push(`/reader/${props.bookId}/1`)
  } else {
    router.push('/reader')
  }
}

// 统计卡片点击
const handleStatClick = (statType: string) => {
  emit('statClick', statType)
}

// 体验卡/购买点击
const handleExperienceCardClick = () => {
  if (isPurchased.value) return
  handleStatClick('experienceCard')
  purchaseDialogVisible.value = true
}

// 处理充值点击
const handleRechargeClick = () => {
  purchaseDialogVisible.value = false
  emit('openRechargeDialog')
}

// 检查余额
const checkBalance = () => {
  return (props.userPayCoin || 0) >= requiredCoins.value
}

// 显示余额不足提示
const showInsufficientBalanceAlert = () => {
  ElMessageBox.alert(
    `您的充值币余额不足，当前余额${props.userPayCoin}充值币，购买本书需要${requiredCoins.value}充值币。`,
    '余额不足',
    {
      confirmButtonText: '去充值',
      type: 'warning',
      callback: (action: string) => {
        if (action === 'confirm') {
          handleRechargeClick()
        }
      }
    }
  )
}

// 确认购买
const confirmPurchase = () => {
  if (!checkBalance()) {
    showInsufficientBalanceAlert()
    return
  }

  ElMessageBox.confirm(
    `确定使用${requiredCoins.value}充值币购买本书吗？`,
    '确认购买',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 实际项目中这里应调用 API
    isPurchased.value = true
    ElMessage.success('购买成功！')
    purchaseDialogVisible.value = false
    emit('purchaseSuccess')
  }).catch(() => {
    // 取消购买
  })
}
</script>

<style scoped>
.book-detail-header {
  width: 100%;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  position: relative;
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
  border-radius: 4px;
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
  /* 防止 Flex 子项内容溢出 */
  overflow: hidden; 
}

/* 书名样式：限制为 2 行 */
.book-title {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin: 0 0 5px 0;
  line-height: 1.3;
  
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
  word-wrap: break-word;
}

/* 作者样式：限制为 1 行 */
.book-author {
  font-size: 18px;
  color: #666;
  font-weight: 500;
  margin-bottom: 30px;
  
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
}

/* 按钮区域 */
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

/* 统计卡片区域 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
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

/* 纯展示卡片 */
.stat-card.display-only,
.stat-card.display-only:hover {
  cursor: default;
  background: #f0f0f0;
  transform: none;
}

/* 已购买状态 */
.stat-card.purchased,
.stat-card.purchased:hover {
  cursor: default;
  background: #f0f0f0;
  transform: none;
}

.stat-card.purchased .el-icon {
  color: #52c41a; /* 绿色 */
}

/* 可点击卡片 */
.stat-card.clickable {
  cursor: pointer;
}

.stat-card.clickable:hover {
  background: #f9f9f9;
  transform: translateY(-2px);
}

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
  
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.stat-subtitle {
  font-size: 12px;
  color: #999;
  
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
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

/* 简介样式：限制为 6 行 */
.book-description {
  font-size: 20px;
  line-height: 1.6;
  color: #555;
  
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 6; 
  overflow: hidden;
  text-overflow: ellipsis;
  line-clamp: 6;
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
    -webkit-line-clamp: 2; 
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
    -webkit-line-clamp: 5; 
    line-clamp: 5;
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