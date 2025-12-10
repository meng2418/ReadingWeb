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
          <span class="value">¥{{ selectedPlanObj?.price || 0 }}</span>
        </div>
        <div class="payment-duration">
          <span class="label">开通时长：</span>
          <span class="value">{{ selectedPlanObj?.duration || '' }}</span>
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
            :class="{ 'active': selectedMethod === method.id }"
            @click="selectedMethod = method.id"
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
  days?: number // 添加天数字段
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
const selectedPlan = ref(2) // 默认选择月度套餐
const selectedMethod = ref('wechat')
const agreed = ref(true)

const vipBenefits: VipBenefit[] = [
  { id: 1, name: '免费读全场', description: '海量图书免费读', icon: Reading },
  { id: 2, name: '听书特权', description: '优质有声书免费听', icon: Collection },
  { id: 3, name: '夜间模式', description: '专属护眼夜间模式', icon: Moon },
  { id: 4, name: '无广告', description: '纯净阅读无广告', icon: CloseBold },
  { id: 5, name: '离线下载', description: '支持离线阅读', icon: Download },
  { id: 6, name: '尊贵标识', description: '专属会员标识', icon: Star },
]

const vipPlans: VipPlan[] = [
  { id: 1, name: '周会员', price: 9, duration: '7天', days: 7, popular: false },
  { id: 2, name: '月会员', price: 25, originalPrice: 30, duration: '30天', days: 30, savings: 5, popular: true },
  { id: 3, name: '季会员', price: 68, originalPrice: 90, duration: '90天', days: 90, savings: 22, popular: false },
  { id: 4, name: '年会员', price: 238, originalPrice: 360, duration: '365天', days: 365, savings: 122, popular: false },
]

const paymentMethods: PaymentMethod[] = [
  { id: 'wechat', name: '微信支付', icon: ChatDotRound },
  { id: 'alipay', name: '支付宝', icon: Money },
  { id: 'balance', name: '余额支付', icon: Wallet },
  { id: 'card', name: '银行卡', icon: CreditCard },
]

const selectedPlanObj = computed(() =>
  vipPlans.find(plan => plan.id === selectedPlan.value)
)

const selectPlan = (id: number) => {
  selectedPlan.value = id
}

const goToPaymentStep = () => {
  if (!agreed.value) {
    ElMessage.warning('请先阅读并同意会员协议')
    return
  }

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

  loading.value = true
  try {
    // 模拟支付过程
    await new Promise(resolve => setTimeout(resolve, 1500))

    // 关闭支付弹窗
    paymentDialogVisible.value = false

    // 显示成功弹窗
    await ElMessageBox.confirm(
      `<div class="success-content">
         <div class="success-title">您已成功开通会员</div>
         <div class="success-desc">
           已成功开通 ${selectedPlanObj.value?.name}，有效期 ${selectedPlanObj.value?.duration}
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
    emit('purchase-success', selectedPlanObj.value)

  } catch (error) {
    // 支付失败处理
    ElMessage.error('开通失败，请重试')
  } finally {
    loading.value = false
  }
}
// 暴露方法供父组件调用
const open = () => {
  dialogVisible.value = true
  selectedPlan.value = 2
  selectedMethod.value = 'wechat'
  agreed.value = true
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
  color: #666;
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
  color: #409eff;
  margin-bottom: 8px;
}

.benefit-name {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
}

.benefit-desc {
  font-size: 12px;
  color: #666;
}

.vip-plans {
  margin-bottom: 24px;
}

.plan-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.plans-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.plan-item {
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  padding: 20px 16px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.plan-item:hover {
  border-color: #409eff;
}

.plan-item.active {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.plan-item.popular {
  border-color: #ff6b6b;
}

.plan-item.popular.active {
  background-color: #fff0f0;
}

.popular-badge {
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
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
}

.plan-price {
  font-size: 24px;
  font-weight: 700;
  color: #ff6b6b;
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
  color: #999;
  text-decoration: line-through;
  margin-left: 8px;
}

.plan-duration {
  font-size: 14px;
  color: #666;
}

.plan-savings {
  text-align: center;
  font-size: 12px;
  color: #ff6b6b;
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
  color: #999;
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
  color: #666;
}

.payment-amount .value {
  font-weight: 600;
  color: #333;
}

.payment-duration .value {
  font-weight: 600;
  color: #67c23a;
}

.payment-methods {
  margin-bottom: 24px;
}

.payment-title {
  font-size: 14px;
  color: #666;
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
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.payment-item:hover {
  border-color: #409eff;
}

.payment-item.active {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.method-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.method-icon {
  width: 24px;
  height: 24px;
  color: #409eff;
}

.method-name {
  font-size: 14px;
  color: #333;
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
  color: #67c23a;
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
  background-color: #67c23a;
  border-color: #67c23a;
  min-width: 100px;
}

:deep(.success-dialog .el-button--primary:hover) {
  background-color: #85ce61;
  border-color: #85ce61;
}

:deep(.success-content) {
  text-align: center;
  padding: 10px 0;
}

:deep(.success-title) {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

:deep(.success-desc) {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}
</style>
