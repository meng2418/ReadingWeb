<template>
  <el-dialog
    v-model="dialogVisible"
    title="充值"
    width="400px"
    align-center
    :before-close="handleClose"
  >
    <div class="recharge-dialog">
      <div class="recharge-amounts">
        <div
          v-for="amount in rechargeOptions"
          :key="amount.id"
          class="amount-item"
          :class="{ 'active': selectedAmount === amount.id }"
          @click="selectAmount(amount.id)"
        >
          <div class="amount-info">
            <div class="amount">{{ amount.amount }}书币</div>
            <div class="price">¥{{ amount.price }}</div>
          </div>
          <div v-if="amount.bonus" class="bonus">赠{{ amount.bonus }}书币</div>
        </div>
      </div>

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
              <span>{{ method.name }}</span>
            </div>
            <el-radio :model-value="selectedMethod === method.id" />
          </div>
        </div>
      </div>

      <div class="recharge-summary">
        <div class="summary-item">
          <span>充值金额</span>
          <span>¥{{ selectedOption?.price || 0 }}</span>
        </div>
        <div v-if="selectedOption?.bonus" class="summary-item">
          <span>赠送书币</span>
          <span class="bonus-text">{{ selectedOption.bonus }}书币</span>
        </div>
        <div class="summary-item total">
          <span>实付金额</span>
          <span class="total-amount">¥{{ selectedOption?.price || 0 }}</span>
        </div>
      </div>

      <div class="dialog-actions">
        <el-button @click="handleClose">取消</el-button>
        <el-button
          type="primary"
          :loading="loading"
          @click="handleRecharge"
        >
          立即支付 ¥{{ selectedOption?.price || 0 }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, defineEmits, defineExpose } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Wallet,
  CreditCard,
  ChatDotRound,
  Money
} from '@element-plus/icons-vue'

interface RechargeOption {
  id: number
  amount: number
  price: number
  bonus: number
}

interface PaymentMethod {
  id: string
  name: string
  icon: any
}

const emit = defineEmits(['recharge-success'])

const dialogVisible = ref(false)
const loading = ref(false)
const selectedAmount = ref(1)
const selectedMethod = ref('wechat')

const rechargeOptions: RechargeOption[] = [
  { id: 1, amount: 60, price: 6, bonus: 0 },
  { id: 2, amount: 300, price: 30, bonus: 30 },
  { id: 3, amount: 680, price: 68, bonus: 80 },
  { id: 4, amount: 1280, price: 128, bonus: 200 },
  { id: 5, amount: 2980, price: 298, bonus: 500 },
  { id: 6, amount: 5880, price: 588, bonus: 1200 },
]

const paymentMethods: PaymentMethod[] = [
  { id: 'wechat', name: '微信支付', icon: ChatDotRound },
  { id: 'alipay', name: '支付宝', icon: Money },
  { id: 'balance', name: '余额支付', icon: Wallet },
  { id: 'card', name: '银行卡', icon: CreditCard },
]

const selectedOption = computed(() =>
  rechargeOptions.find(option => option.id === selectedAmount.value)
)

const selectAmount = (id: number) => {
  selectedAmount.value = id
}

const handleClose = () => {
  dialogVisible.value = false
}

const handleRecharge = async () => {
  loading.value = true
  try {
    // 模拟支付过程
    await new Promise(resolve => setTimeout(resolve, 1500))

    ElMessage.success('充值成功！')
    emit('recharge-success', selectedOption.value)
    dialogVisible.value = false
  } catch (error) {
    ElMessage.error('支付失败，请重试')
  } finally {
    loading.value = false
  }
}

// 暴露方法供父组件调用
const open = () => {
  dialogVisible.value = true
  selectedAmount.value = 1
  selectedMethod.value = 'wechat'
}

defineExpose({
  open
})
</script>

<style scoped>
.recharge-dialog {
  padding: 10px 0;
}

.recharge-amounts {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

.amount-item {
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  padding: 16px 12px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  text-align: center;
}

.amount-item:hover {
  border-color: #409eff;
}

.amount-item.active {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.amount-info {
  margin-bottom: 4px;
}

.amount {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.price {
  font-size: 14px;
  color: #666;
}

.bonus {
  position: absolute;
  top: -8px;
  right: 8px;
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
}

.payment-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.payment-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
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
  width: 20px;
  height: 20px;
}

.recharge-summary {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
}

.summary-item:last-child {
  margin-bottom: 0;
}

.summary-item.total {
  border-top: 1px solid #e0e0e0;
  padding-top: 12px;
  margin-top: 12px;
  font-size: 16px;
  font-weight: 600;
}

.bonus-text {
  color: #ff6b6b;
  font-weight: 600;
}

.total-amount {
  color: #ff6b6b;
  font-size: 18px;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
