<template>
  <el-dialog
    v-model="dialogVisible"
    :title="step === 1 ? '充值' : '选择支付方式'"
    width="420px"
    align-center
    :before-close="handleClose"
  >
    <!-- 步骤1：选择充值金额 -->
    <div v-if="step === 1" class="recharge-step1">
      <!-- 当前余额显示 -->
      <div class="balance-display">
        <div class="balance-label">我的充值币</div>
        <div class="balance-amount">{{ currentBalance }}充值币</div>
      </div>

      <!-- 充值选项 -->
      <div class="recharge-options">
        <div
          v-for="option in rechargeOptions"
          :key="option.id"
          class="recharge-option"
          :class="{ 'active': selectedAmount === option.id }"
          @click="selectAmount(option.id)"
        >
          <div class="option-amount">{{ option.amount }}充值币</div>
          <div class="option-price">¥{{ option.price }}</div>
          <div v-if="option.bonus" class="option-bonus">+赠{{ option.bonus }}充值币</div>
        </div>
      </div>

      <!-- 协议提示 -->
      <div class="agreement-hint">
        充值代表同意<a href="#" @click.prevent="showAgreement">《用户充值协议》</a>
      </div>

      <!-- 温馨提示 -->
      <el-collapse v-model="activeCollapse" class="warm-hint">
        <el-collapse-item name="1">
          <template #title>
            <span class="hint-title">温馨提示</span>
          </template>
          <div class="hint-content">
            <p>1. 货币兑换比例：充值1元人民币=10充值币。</p>
            <p>2. 充值点数：在平台各客户端通用。</p>
            <p>3. 充值后不支持退款。如您在充值后余额未发生变化，您可以至【个人设置】-【清除缓存】，或者重新登录账号后查看。</p>
            <p>4. 其他联系方式：客服电话 400-XXX-XXXX（周一到周五 10:00-12:00，13:00-18:00在线）</p>
          </div>
        </el-collapse-item>
      </el-collapse>

      <!-- 操作按钮 -->
      <div class="dialog-actions">
        <el-button @click="handleClose">取消</el-button>
        <el-button
          type="primary"
          @click="goToPaymentStep"
          :disabled="!selectedAmount"
        >
          充值 ¥{{ selectedOption?.price || 0 }}
        </el-button>
      </div>
    </div>

    <!-- 步骤2：选择支付方式 -->
    <div v-else class="recharge-step2">
      <!-- 支付金额信息 -->
      <div class="payment-info">
        <div class="payment-amount">
          <span class="label">支付金额：</span>
          <span class="value">¥{{ selectedOption?.price || 0 }}</span>
        </div>
        <div v-if="paymentInfo?.bonusCoins || selectedOption?.bonus" class="payment-bonus">
          <span class="label">赠送充值币：</span>
          <span class="value">{{ paymentInfo?.bonusCoins || selectedOption?.bonus || 0 }}充值币</span>
        </div>
        <div class="payment-total">
          <span class="label">实付金额：</span>
          <span class="value total-amount">¥{{ paymentInfo?.actualAmount || selectedOption?.price || 0 }}</span>
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

      <!-- 协议确认 -->
      <div class="payment-agreement">
        <el-checkbox v-model="agreed">
          我已阅读并同意<a href="#" @click.prevent="showPaymentAgreement">《支付服务协议》</a>
        </el-checkbox>
      </div>

      <!-- 操作按钮 -->
      <div class="dialog-actions">
        <el-button @click="step = 1">返回</el-button>
        <el-button
          type="primary"
          :loading="loading"
          :disabled="!agreed"
          @click="handleRecharge"
        >
          确认支付 ¥{{ selectedOption?.price || 0 }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, defineEmits, defineExpose, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Wallet,
  CreditCard,
  ChatDotRound,
  Money
} from '@element-plus/icons-vue'
import {
  getRechargePackages,
  getRechargePaymentPage,
  recharge,
  type RechargePackage,
  type RechargePaymentInfo
} from '@/api/payment'

interface RechargeOption {
  id: number
  amount: number
  price: number
  bonus: number
  packageId?: number
}

interface PaymentMethod {
  id: string
  name: string
  icon: any
}

const emit = defineEmits(['recharge-success'])

const dialogVisible = ref(false)
const loading = ref(false)
const step = ref(1)
const selectedAmount = ref<number | null>(null)
const selectedMethod = ref('wechat')
const agreed = ref(true)
const activeCollapse = ref([])
const currentBalance = ref(0)
const rechargeOptions = ref<RechargeOption[]>([])
const availablePaymentMethods = ref<string[]>([])
const paymentInfo = ref<RechargePaymentInfo | null>(null)

const paymentMethods: PaymentMethod[] = [
  { id: 'wechat', name: '微信支付', icon: ChatDotRound },
  { id: 'alipay', name: '支付宝', icon: Money },
  { id: 'balance', name: '余额支付', icon: Wallet },
  { id: 'card', name: '银行卡', icon: CreditCard },
]

const selectedOption = computed(() =>
  rechargeOptions.value.find(option => option.id === selectedAmount.value)
)

// 加载充值套餐列表
const loadRechargePackages = async () => {
  try {
    const packages = await getRechargePackages()
    // 将API返回的套餐转换为前端使用的格式
    // 注意：如果后端不返回packageId，我们使用索引+1作为packageId
    rechargeOptions.value = packages.map((pkg, index) => ({
      id: index + 1,
      amount: pkg.coinAmount,
      price: pkg.cnyAmount,
      bonus: pkg.bonusCoins,
      packageId: pkg.packageId || (index + 1) // 如果没有packageId，使用索引+1
    }))
    
    // 如果没有套餐，使用默认套餐
    if (rechargeOptions.value.length === 0) {
      rechargeOptions.value = [
        { id: 1, amount: 60, price: 6, bonus: 6 },
        { id: 2, amount: 100, price: 10, bonus: 10 },
        { id: 3, amount: 200, price: 20, bonus: 20 },
        { id: 4, amount: 500, price: 50, bonus: 55 },
        { id: 5, amount: 1000, price: 100, bonus: 120 },
        { id: 6, amount: 2000, price: 200, bonus: 240 },
      ]
    }
    
    // 默认选择第二个套餐
    if (rechargeOptions.value.length > 1) {
      selectedAmount.value = rechargeOptions.value[1].id
    } else if (rechargeOptions.value.length > 0) {
      selectedAmount.value = rechargeOptions.value[0].id
    }
  } catch (error) {
    console.error('加载充值套餐失败:', error)
    // 使用默认套餐
    rechargeOptions.value = [
      { id: 1, amount: 60, price: 6, bonus: 6 },
      { id: 2, amount: 100, price: 10, bonus: 10 },
      { id: 3, amount: 200, price: 20, bonus: 20 },
      { id: 4, amount: 500, price: 50, bonus: 55 },
      { id: 5, amount: 1000, price: 100, bonus: 120 },
      { id: 6, amount: 2000, price: 200, bonus: 240 },
    ]
    if (rechargeOptions.value.length > 1) {
      selectedAmount.value = rechargeOptions.value[1].id
    }
  }
}

// 加载支付页信息
const loadPaymentPage = async () => {
  if (!selectedOption.value) {
    ElMessage.error('请先选择充值套餐')
    return
  }
  
  // 使用packageId，如果没有则使用id
  const packageId = selectedOption.value.packageId || selectedOption.value.id
  
  try {
    const info = await getRechargePaymentPage(packageId)
    paymentInfo.value = info
    availablePaymentMethods.value = info.paymentMethods
    
    // 如果当前选择的支付方式不可用，切换到第一个可用方式
    if (!availablePaymentMethods.value.includes(selectedMethod.value)) {
      if (availablePaymentMethods.value.length > 0) {
        selectedMethod.value = availablePaymentMethods.value[0]
      }
    }
  } catch (error: any) {
    console.error('加载支付页信息失败:', error)
    ElMessage.error(error?.message || '加载支付信息失败')
    // 使用默认支付方式
    availablePaymentMethods.value = ['wechat', 'alipay']
  }
}

const selectAmount = (id: number) => {
  selectedAmount.value = id
}

const handlePaymentMethodClick = (methodId: string) => {
  // 如果支付方式被禁用，不允许选择
  if (availablePaymentMethods.value.length > 0 && !availablePaymentMethods.value.includes(methodId)) {
    return
  }
  selectedMethod.value = methodId
}

const goToPaymentStep = async () => {
  if (!selectedAmount.value) {
    ElMessage.warning('请选择充值金额')
    return
  }
  
  // 加载支付页信息
  await loadPaymentPage()
  step.value = 2
}

const handleClose = () => {
  dialogVisible.value = false
  // 重置状态
  step.value = 1
  selectedAmount.value = 2
  selectedMethod.value = 'wechat'
  agreed.value = true
  loading.value = false
}

const handleRecharge = async () => {
  if (!agreed.value) {
    ElMessage.warning('请先阅读并同意支付服务协议')
    return
  }

  if (!selectedOption.value) {
    ElMessage.error('请选择充值套餐')
    return
  }

  // 防止重复点击
  if (loading.value) {
    return
  }

  // 使用packageId，如果没有则使用id
  const packageId = selectedOption.value.packageId || selectedOption.value.id

  loading.value = true

  try {
    // 调用充值API
    const result = await recharge({
      packageId: packageId,
      paymentMethod: selectedMethod.value as 'wechat' | 'alipay' | 'unionpay'
    })

    // 支付成功 - 先重置loading状态
    loading.value = false

    // 关闭充值弹窗 - 立即关闭
    dialogVisible.value = false

    // 显示成功弹窗
    await ElMessageBox.confirm(
      `<div class="success-content">
         <div class="success-title">您已充值成功</div>
         <div class="success-desc">
           已成功充值 ${result.coinAmount} 充值币
           ${result.bonusCoins > 0 ? `+ 赠送 ${result.bonusCoins} 充值币` : ''}
         </div>
       </div>`,
      '充值成功',
      {
        confirmButtonText: '确认',
        cancelButtonText: '',
        showCancelButton: false,
        dangerouslyUseHTMLString: true,
        customClass: 'success-dialog',
        center: true,
      }
    )

    // 触发成功事件，通知父组件更新余额
    emit('recharge-success', {
      ...selectedOption.value,
      coinAmount: result.coinAmount,
      bonusCoins: result.bonusCoins,
      orderId: result.orderId
    })

  } catch (error: any) {
    // 支付失败处理
    loading.value = false
    const errorMessage = error?.response?.data?.message || error?.message || '充值失败，请重试'
    ElMessage.error(errorMessage)
  } finally {
    // 重置加载状态
    loading.value = false
  }
}
  //catch (error) {
    // 支付失败逻辑暂时注释掉，稍后恢复
    /*
    // 支付失败 - 先重置loading状态
    loading.value = false

    // 显示失败弹窗
    try {
      const result = await ElMessageBox.confirm(
        `<div class="error-content">
           <div class="error-icon">✗</div>
           <div class="error-title">支付失败，请重试</div>
           <div class="error-desc">请检查您的支付方式或网络连接</div>
         </div>`,
        '支付失败',
        {
          confirmButtonText: '重新支付',
          cancelButtonText: '取消',
          type: 'error',
          dangerouslyUseHTMLString: true,
          customClass: 'error-dialog',
          center: true,
          distinguishCancelAndClose: true
        }
      )

      if (result === 'confirm') {
        // 用户点击重新支付，重新调用支付函数
        // 使用setTimeout避免递归调用栈溢出
        setTimeout(() => {
          handleRecharge()
        }, 0)
      } else {
        // 用户点击取消，停留在当前支付页面
        console.log('用户取消重新支付')
      }
    } catch (cancelError) {
      // 用户点击了取消按钮
      console.log('用户取消支付')
    }
    */

    // 由于现在固定支付成功，这里理论上不会执行
    // 但如果发生意外错误，显示一个简单的错误提示
    //loading.value = false
   // console.error('支付过程中发生意外错误:', error)
    //ElMessage.error('支付过程发生错误，请稍后重试')



const showAgreement = () => {
  ElMessageBox.alert(
    `
    <div class="agreement-content">
      <h3>用户充值协议</h3>
      <p>1. 充值成功后，充值金额将存入您的账户余额，可用于平台内各项服务。</p>
      <p>2. 充值金额一旦成功，不支持退款，请确认后再进行充值操作。</p>
      <p>3. 如遇充值问题，请联系客服处理，客服电话：400-XXX-XXXX。</p>
      <p>4. 本平台保留对充值规则进行修改的权利，修改后会及时通知用户。</p>
      <p>5. 请妥善保管您的账户信息，因个人原因导致的损失，平台不承担责任。</p>
    </div>
    `,
    '用户充值协议',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '我已阅读并同意',
      customClass: 'agreement-dialog',
      center: true
    }
  )
}

const showPaymentAgreement = () => {
  ElMessageBox.alert(
    `
    <div class="agreement-content">
      <h3>支付服务协议</h3>
      <p>1. 支付过程中请确保网络畅通，避免重复支付。</p>
            <p>2. 支付成功后，系统会自动为您添加相应的充值币。</p>
      <p>3. 如支付过程中遇到问题，请及时联系客服。</p>
      <p>4. 支付信息将严格保密，不会泄露给第三方。</p>
      <p>5. 请确认支付金额无误后再进行支付操作。</p>
    </div>
    `,
    '支付服务协议',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '我已阅读并同意',
      customClass: 'agreement-dialog',
      center: true
    }
  )
}

// 暴露方法供父组件调用
const open = async (balance: number) => {
  currentBalance.value = balance
  dialogVisible.value = true
  step.value = 1
  selectedMethod.value = 'wechat'
  agreed.value = true
  loading.value = false
  
  // 加载充值套餐列表
  await loadRechargePackages()
}

defineExpose({
  open
})
</script>


<style scoped>
.recharge-step1,
.recharge-step2 {
  padding: 10px 0;
}

/* 步骤1样式 */
.balance-display {
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  margin-bottom: 24px;
}

.balance-label {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.balance-amount {
  font-size: 32px;
  font-weight: 700;
  color: var(--primary-green);
}

.recharge-options {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.recharge-option {
  border: 2px solid var(--border-color);
  border-radius: 8px;
  padding: 16px 12px;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
  position: relative;
  background: white;
}

.recharge-option:hover {
  border-color: var(--primary-green);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 124, 39, 0.1); /* 使用 --primary-green 的透明版本 */
}

.recharge-option.active {
  border-color: var(--primary-green);
  background-color: rgba(126, 180, 143, 0.1); /* 使用 --bg-green 的浅色版本 */
  position: relative;
}

.recharge-option.active::after {
  content: '';
  position: absolute;
  top: -2px;
  right: -2px;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 0 20px 20px 0;
  border-color: transparent var(--primary-green) transparent transparent;
}

.recharge-option.active::before {
  content: '✓';
  position: absolute;
  top: 0;
  right: 0;
  width: 20px;
  height: 20px;
  color: white;
  font-size: 12px;
  text-align: center;
  line-height: 20px;
  z-index: 1;
}

.option-amount {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.option-price {
  font-size: 16px;
  font-weight: 500;
  color: var(--primary-pink);
  margin-bottom: 4px;
}

.option-bonus {
  font-size: 12px;
  color: var(--primary-green);
  background-color: rgba(0, 124, 39, 0.1);
  padding: 2px 6px;
  border-radius: 10px;
  display: inline-block;
  margin-top: 4px;
}

.agreement-hint {
  text-align: center;
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 16px;
  padding: 8px;
  background-color: #f8f9fa;
  border-radius: 6px;
}

.agreement-hint a {
  color: var(--primary-green);
  text-decoration: none;
  margin-left: 4px;
}

.agreement-hint a:hover {
  text-decoration: underline;
}

.warm-hint {
  margin-bottom: 24px;
  border: none;
}

.hint-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  border: none;
}

.hint-content {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.6;
  border: none;
}

.hint-content p {
  margin: 4px 0;
  border: none;
}

/* 步骤2样式 */
.payment-info {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
}

.payment-amount,
.payment-bonus,
.payment-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
}

.payment-total {
  border-top: 1px solid var(--border-color);
  padding-top: 12px;
  margin-top: 12px;
}

.payment-amount .label,
.payment-bonus .label,
.payment-total .label {
  color: var(--text-secondary);
}

.payment-amount .value {
  font-weight: 600;
  color: var(--text-primary);
}

.payment-bonus .value {
  color: var(--primary-green);
  font-weight: 600;
}

.total-amount {
  font-size: 18px;
  color: var(--primary-pink);
  font-weight: 700;
}

.payment-methods {
  margin-bottom: 20px;
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
  background: white;
}

.payment-item:hover {
  border-color: var(--primary-green);
  background-color: #fafafa;
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

.method-info {
  display: flex;
  align-items: center;
  gap: 12px;
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

.payment-agreement {
  margin-bottom: 24px;
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.payment-agreement :deep(.el-checkbox__label) {
  font-size: 12px;
  color: var(--text-secondary);
}

.payment-agreement a {
  color: var(--primary-green);
  text-decoration: none;
  margin: 0 2px;
}

.payment-agreement a:hover {
  text-decoration: underline;
}

/* 操作按钮 */
.dialog-actions {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.dialog-actions .el-button {
  flex: 1;
}

/* 全局样式修改 */
:deep(.agreement-dialog) {
  max-width: 500px;
}

:deep(.agreement-dialog .el-message-box) {
  text-align: center;
}

:deep(.agreement-content) {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 10px;
}

:deep(.agreement-content h3) {
  color: var(--text-primary);
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-color);
}

:deep(.agreement-content p) {
  margin-bottom: 12px;
  line-height: 1.6;
  color: var(--text-secondary);
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

/* 失败弹窗样式（保留，以便后续恢复） */
/*
:deep(.error-dialog .el-message-box) {
  text-align: center;
  min-width: 300px;
}

:deep(.error-dialog .el-message-box__status) {
  display: none;
}

:deep(.error-dialog .el-message-box__header) {
  text-align: center;
  padding: 20px 20px 0 20px;
}

:deep(.error-dialog .el-message-box__title) {
  color: #f56c6c;
  font-weight: 600;
}

:deep(.error-dialog .el-message-box__content) {
  padding: 10px 20px;
  text-align: center;
}

:deep(.error-dialog .el-message-box__btns) {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 0 20px 20px 20px;
}

:deep(.error-dialog .el-button--primary) {
  background-color: #f56c6c;
  border-color: #f56c6c;
  min-width: 100px;
}

:deep(.error-dialog .el-button--primary:hover) {
  background-color: #f78989;
  border-color: #f78989;
}

:deep(.error-dialog .el-button--default) {
  min-width: 100px;
}

:deep(.error-content) {
  text-align: center;
  padding: 10px 0;
}

:deep(.error-icon) {
  font-size: 48px;
  color: #f56c6c;
  margin-bottom: 16px;
}

:deep(.error-title) {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

:deep(.error-desc) {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}
*/
</style>
