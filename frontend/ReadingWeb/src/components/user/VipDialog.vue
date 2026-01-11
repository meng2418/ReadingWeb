<template>
  <!-- 第一步：选择套餐弹窗 -->
  <el-dialog
    v-model="dialogVisible"
    title="开通会员"
    width="500px"
    align-center
    :before-close="handleClose"
  >
    <div class="vip-dialog">
      <!-- 会员特权展示 -->
      <div class="vip-benefits">
        <div class="benefit-title">会员特权</div>
        <div class="benefits-grid">
          <div v-for="benefit in vipBenefits" :key="benefit.id" class="benefit-item">
            <el-icon class="benefit-icon"><component :is="benefit.icon" /></el-icon>
            <div class="benefit-info">
              <div class="benefit-name">{{ benefit.name }}</div>
              <div class="benefit-desc">{{ benefit.description }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 会员套餐 -->
      <div class="vip-plans">
        <div class="plan-title">选择套餐</div>
        <div class="plans-container">
          <div
            v-for="plan in vipPlans"
            :key="plan.id"
            class="plan-item"
            :class="{
              'active': selectedPlan === plan.id,
              'popular': plan.popular
            }"
            @click="selectPlan(plan.id)"
          >
            <div v-if="plan.popular" class="popular-badge">最受欢迎</div>
            <div class="plan-header">
              <div class="plan-name">{{ plan.name }}</div>
              <div class="plan-price">
                <span class="currency">¥</span>
                <span class="amount">{{ plan.price }}</span>
                <span v-if="plan.originalPrice" class="original-price">
                  ¥{{ plan.originalPrice }}
                </span>
              </div>
              <div class="plan-duration">{{ plan.duration }}</div>
            </div>
            <div class="plan-savings" v-if="plan.savings">
              立省{{ plan.savings }}元
            </div>
          </div>
        </div>
      </div>

      <!-- 会员协议 -->
      <div class="vip-agreement">
        <el-checkbox v-model="agreed">
          我已阅读并同意
          <el-link type="primary" :underline="false">
            《会员服务协议》
          </el-link>
          和
          <el-link type="primary" :underline="false">
            《自动续费协议》
          </el-link>
        </el-checkbox>
        <div class="auto-renewal">
          *开通即代表同意自动续费，可随时取消
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="dialog-actions">
        <el-button @click="handleClose">取消</el-button>
        <el-button
          type="primary"
          :loading="loading"
          :disabled="!agreed"
          @click="goToPaymentStep"
        >
          立即开通 ¥{{ selectedPlanObj?.price || 0 }}
        </el-button>
      </div>
    </div>
  </el-dialog>

  <!-- 第二步：支付方式弹窗 -->
  <el-dialog
    v-model="paymentDialogVisible"
    title="选择支付方式"
    width="420px"
    align-center
    :before-close="handleClosePayment"
  >
    <div class="payment-dialog">
      <!-- 支付金额信息 -->
      <div class="payment-info">
        <div class="payment-amount">
          <span class="label">支付金额：</span>
          <span class="value">¥{{ paymentInfo?.discountAmount || selectedPlanObj?.price || 0 }}</span>
        </div>
        <div v-if="paymentInfo?.originalAmount && paymentInfo.originalAmount > (paymentInfo.discountAmount || 0)" class="payment-original">
          <span class="label">原价：</span>
          <span class="value" style="text-decoration: line-through; color: #999;">¥{{ paymentInfo.originalAmount }}</span>
        </div>
        <div class="payment-duration">
          <span class="label">开通时长：</span>
          <span class="value">{{ paymentInfo?.durationDays ? `${paymentInfo.durationDays}天` : selectedPlanObj?.duration || '' }}</span>
        </div>
      </div>

      <!-- 支付方式选择 -->
      <div class="payment-methods">
        <div class="payment-title">选择支付方式</div>
        <div class="payment-options">
          <div
            v-for="method in paymentMethods"
            :key="method.id"
            class="payment-item"
            :class="{ 
              'active': selectedMethod === method.id,
              'disabled': availablePaymentMethods.length > 0 && !availablePaymentMethods.includes(method.id)
            }"
            @click="handlePaymentMethodClick(method.id)"
          >
            <div class="method-info">
              <el-icon class="method-icon"><component :is="method.icon" /></el-icon>
              <span class="method-name">{{ method.name }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="dialog-actions">
        <el-button @click="backToPlanStep">返回</el-button>
        <el-button
          type="primary"
          :loading="loading"
          @click="handlePurchase"
        >
          立即支付 ¥{{ selectedPlanObj?.price || 0 }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, defineEmits, defineExpose } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Star,
  Reading,
  Moon,
  Download,
  Collection,
  CloseBold,
  ChatDotRound,
  Money,
  Wallet,
  CreditCard
} from '@element-plus/icons-vue'
import {
  getMembershipPaymentInfo,
  purchaseMembership,
  type MembershipPaymentInfo
} from '@/api/payment'

interface VipBenefit {
  id: number
  name: string
  description: string
  icon: any
}

interface VipPlan {
  id: number
  name: string
  price: number
  originalPrice?: number
  duration: string
  savings?: number
  popular: boolean
  days?: number
  packageId?: number
}

interface PaymentMethod {
  id: string
  name: string
  icon: any
}

const emit = defineEmits(['purchase-success'])

const dialogVisible = ref(false)
const paymentDialogVisible = ref(false)
const loading = ref(false)
const selectedPlan = ref<number | null>(null)
const selectedMethod = ref('wechat')
const agreed = ref(true)
const paymentInfo = ref<MembershipPaymentInfo | null>(null)
const availablePaymentMethods = ref<string[]>([])

const vipBenefits: VipBenefit[] = [
  { id: 1, name: '免费读全场', description: '海量图书免费读', icon: Reading },
  { id: 2, name: '听书特权', description: '优质有声书免费听', icon: Collection },
  { id: 3, name: '夜间模式', description: '专属护眼夜间模式', icon: Moon },
  { id: 4, name: '无广告', description: '纯净阅读无广告', icon: CloseBold },
  { id: 5, name: '离线下载', description: '支持离线阅读', icon: Download },
  { id: 6, name: '尊贵标识', description: '专属会员标识', icon: Star },
]

const vipPlans = ref<VipPlan[]>([
  { id: 1, name: '周会员', price: 9, duration: '7天', days: 7, popular: false },
  { id: 2, name: '月会员', price: 25, originalPrice: 30, duration: '30天', days: 30, savings: 5, popular: true },
  { id: 3, name: '季会员', price: 68, originalPrice: 90, duration: '90天', days: 90, savings: 22, popular: false },
  { id: 4, name: '年会员', price: 238, originalPrice: 360, duration: '365天', days: 365, savings: 122, popular: false },
])

const paymentMethods: PaymentMethod[] = [
  { id: 'wechat', name: '微信支付', icon: ChatDotRound },
  { id: 'alipay', name: '支付宝', icon: Money },
  { id: 'balance', name: '余额支付', icon: Wallet },
  { id: 'card', name: '银行卡', icon: CreditCard },
]

const selectedPlanObj = computed(() =>
  vipPlans.value.find(plan => plan.id === selectedPlan.value)
)

// 加载会员支付信息
const loadPaymentInfo = async (packageId?: number) => {
  try {
    const info = await getMembershipPaymentInfo(packageId)
    paymentInfo.value = info
    
    // 更新套餐信息 - 根据packageId或当前选择的套餐ID匹配
    const plan = vipPlans.value.find(p => {
      if (packageId) {
        return p.packageId === packageId || p.id === packageId
      }
      return p.id === (selectedPlan.value || 2)
    })
    
    if (plan) {
      plan.packageId = info.packageId
      plan.price = info.discountAmount
      plan.originalPrice = info.originalAmount
      plan.days = info.durationDays
    }
    
    availablePaymentMethods.value = info.paymentMethods
    
    // 如果当前选择的支付方式不可用，切换到第一个可用方式
    if (!availablePaymentMethods.value.includes(selectedMethod.value)) {
      if (availablePaymentMethods.value.length > 0) {
        selectedMethod.value = availablePaymentMethods.value[0]
      }
    }
  } catch (error: any) {
    console.error('加载会员支付信息失败:', error)
    // 使用默认支付方式
    availablePaymentMethods.value = ['wechat', 'alipay']
  }
}

const selectPlan = (id: number) => {
  if (loading.value) return // 如果正在加载，不允许切换
  selectedPlan.value = id
}

const goToPaymentStep = async () => {
  if (!agreed.value) {
    ElMessage.warning('请先阅读并同意会员协议')
    return
  }

  if (!selectedPlan.value) {
    ElMessage.warning('请选择会员套餐')
    return
  }

  // 加载支付信息 - 使用当前选择的套餐ID
  const plan = selectedPlanObj.value
  // 如果套餐还没有packageId，先尝试加载，使用套餐的id作为packageId
  await loadPaymentInfo(plan?.packageId || plan?.id)

  // 关闭套餐选择弹窗，打开支付弹窗
  dialogVisible.value = false
  setTimeout(() => {
    paymentDialogVisible.value = true
  }, 300)
}

const backToPlanStep = () => {
  paymentDialogVisible.value = false
  setTimeout(() => {
    dialogVisible.value = true
  }, 300)
}

const handleClose = () => {
  dialogVisible.value = false
}

const handleClosePayment = () => {
  paymentDialogVisible.value = false
}

const handlePurchase = async () => {
  if (loading.value) {
    return
  }

  if (!selectedPlan.value || !selectedPlanObj.value) {
    ElMessage.error('请选择会员套餐')
    return
  }

  // 使用packageId，如果没有则使用id
  const packageId = selectedPlanObj.value.packageId || selectedPlanObj.value.id
  
  if (!packageId) {
    ElMessage.error('套餐信息不完整')
    return
  }

  loading.value = true
  try {
    // 调用开通会员API
    const result = await purchaseMembership({
      packageId: packageId,
      paymentMethod: selectedMethod.value as 'wechat' | 'alipay' | 'balance' | 'card'
    })

    // 关闭支付弹窗
    paymentDialogVisible.value = false

    // 显示成功弹窗
    await ElMessageBox.confirm(
      `<div class="success-content">
         <div class="success-title">您已成功开通会员</div>
         <div class="success-desc">
           已成功开通 ${selectedPlanObj.value?.name}，有效期 ${result.durationDays}天
         </div>
         <div class="success-tip">
           会员特权已生效，立即享受会员服务
         </div>
       </div>`,
      '开通成功',
      {
        confirmButtonText: '确认',
        cancelButtonText: '',
        showCancelButton: false,
        dangerouslyUseHTMLString: true,
        customClass: 'success-dialog',
        center: true,
      }
    )

    // 触发成功事件，通知父组件更新会员信息
    emit('purchase-success', {
      ...selectedPlanObj.value,
      durationDays: result.durationDays,
      durationType: result.durationType
    })

  } catch (error: any) {
    // 支付失败处理
    const errorMessage = error?.response?.data?.message || error?.message || '开通失败，请重试'
    ElMessage.error(errorMessage)
  } finally {
    loading.value = false
  }
}
// 暴露方法供父组件调用
const open = async () => {
  dialogVisible.value = true
  selectedPlan.value = 2 // 默认选择月度套餐
  selectedMethod.value = 'wechat'
  agreed.value = true
  
  // 加载默认套餐的支付信息
  const defaultPlan = vipPlans.value.find(p => p.id === 2)
  if (defaultPlan?.packageId) {
    await loadPaymentInfo(defaultPlan.packageId)
  }
}

defineExpose({
  open
})
</script>

<style scoped>
.vip-dialog {
  padding: 10px 0;
}

.vip-benefits {
  margin-bottom: 24px;
}

.benefit-title {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 12px;
}

.benefits-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.benefit-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 16px 8px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.benefit-icon {
  width: 32px;
  height: 32px;
  color: var(--primary-green);
  margin-bottom: 8px;
}

.benefit-name {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
  color: var(--text-primary);
}

.benefit-desc {
  font-size: 12px;
  color: var(--text-secondary);
}

.vip-plans {
  margin-bottom: 24px;
}

.plan-title {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 12px;
}

.plans-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.plan-item {
  border: 2px solid var(--border-color);
  border-radius: 12px;
  padding: 20px 16px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.plan-item:hover {
  border-color: var(--primary-green);
}

.plan-item.active {
  border-color: var(--primary-green);
  background-color: rgba(126, 180, 143, 0.1); /* 使用 --bg-green 的浅色版本 */
}

.plan-item.popular {
  border-color: var(--primary-pink);
}

.plan-item.popular.active {
  background-color: rgba(255, 107, 107, 0.1); /* 浅粉色背景 */
}

.popular-badge {
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, var(--primary-pink), #ff8e8e);
  color: white;
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 10px;
}

.plan-header {
  text-align: center;
  margin-bottom: 8px;
}

.plan-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--text-primary);
}

.plan-price {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-pink);
  margin-bottom: 4px;
}

.currency {
  font-size: 16px;
}

.amount {
  margin: 0 2px;
}

.original-price {
  font-size: 14px;
  color: var(--text-placeholder);
  text-decoration: line-through;
  margin-left: 8px;
}

.plan-duration {
  font-size: 14px;
  color: var(--text-secondary);
}

.plan-savings {
  text-align: center;
  font-size: 12px;
  color: var(--primary-pink);
  font-weight: 600;
  padding: 4px;
  background-color: rgba(255, 107, 107, 0.1);
  border-radius: 4px;
}

.vip-agreement {
  margin-bottom: 24px;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.auto-renewal {
  font-size: 12px;
  color: var(--text-placeholder);
  margin-top: 8px;
  margin-left: 24px;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 支付弹窗样式 */
.payment-dialog {
  padding: 10px 0;
}

.payment-info {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
}

.payment-amount,
.payment-duration {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
}

.payment-amount .label,
.payment-duration .label {
  color: var(--text-secondary);
}

.payment-amount .value {
  font-weight: 600;
  color: var(--text-primary);
}

.payment-duration .value {
  font-weight: 600;
  color: var(--primary-green);
}

.payment-methods {
  margin-bottom: 24px;
}

.payment-title {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 12px;
  font-weight: 500;
}

.payment-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.payment-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.payment-item:hover {
  border-color: var(--primary-green);
}

.payment-item.active {
  border-color: var(--primary-green);
  background-color: rgba(126, 180, 143, 0.1); /* 使用 --bg-green 的浅色版本 */
}

.payment-item.disabled {
  opacity: 0.5;
  cursor: not-allowed;
  pointer-events: none;
}

.payment-item:not(.disabled) {
  cursor: pointer;
}

.payment-original {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
}

.method-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.method-icon {
  width: 24px;
  height: 24px;
  color: var(--primary-green);
}

.method-name {
  font-size: 14px;
  color: var(--text-primary);
}

/* 成功弹窗样式 */
:deep(.success-dialog .el-message-box) {
  text-align: center;
  min-width: 300px;
}

:deep(.success-dialog .el-message-box__status) {
  display: none;
}

:deep(.success-dialog .el-message-box__header) {
  text-align: center;
  padding: 20px 20px 0 20px;
}

:deep(.success-dialog .el-message-box__title) {
  color: var(--primary-green);
  font-weight: 600;
}

:deep(.success-dialog .el-message-box__content) {
  padding: 10px 20px;
  text-align: center;
}

:deep(.success-dialog .el-message-box__btns) {
  display: flex;
  justify-content: center;
  padding: 0 20px 20px 20px;
}

:deep(.success-dialog .el-button--primary) {
  background-color: var(--primary-green);
  border-color: var(--primary-green);
  min-width: 100px;
}

:deep(.success-dialog .el-button--primary:hover) {
  background-color: var(--thrid-green);
  border-color: var(--thrid-green);
}

:deep(.success-content) {
  text-align: center;
  padding: 10px 0;
}

:deep(.success-title) {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 10px;
}

:deep(.success-desc) {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.5;
}
</style>
