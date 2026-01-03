<template>
  <div class="fp-container">
    <!-- 粒子背景画布 -->
    <canvas ref="canvasRef" class="particle-canvas"></canvas>

    <div class="fp-box">
      <h1 class="fp-title">找回密码</h1>
      <p class="fp-desc">{{ stepDesc }}</p>

      <!-- 步骤指示器 -->
      <div class="fp-steps">
        <span :class="{ 'fp-step': true, 'fp-step--active': step === 1, 'fp-step--done': step > 1 }"
          >1. 验证手机</span
        >
        <span :class="{ 'fp-step': true, 'fp-step--active': step === 2, 'fp-step--done': step > 2 }"
          >2. 验证码</span
        >
        <span :class="{ 'fp-step': true, 'fp-step--active': step === 3 }">3. 重置密码</span>
      </div>

      <!-- 步骤1：输入手机号 -->
      <div v-if="step === 1" class="fp-form">
        <div class="fp-form-group">
          <label class="fp-label">手机号码</label>
          <input
            type="tel"
            v-model="phone"
            placeholder="请输入注册手机号"
            maxlength="11"
            :class="{ 'fp-input': true, 'fp-input--error': errors.phone }"
          />
          <p class="fp-error" v-if="errors.phone">{{ errors.phone }}</p>
        </div>
        <button class="fp-btn" @click="nextStep">发送验证码</button>
      </div>

      <!-- 步骤2：输入验证码 -->
      <div v-if="step === 2" class="fp-form">
        <div class="fp-form-group">
          <label class="fp-label">验证码</label>
          <div class="fp-code-input-group">
            <input
              type="text"
              v-model="code"
              placeholder="请输入6位验证码"
              maxlength="6"
              :class="{ 'fp-input': true, 'fp-input--error': errors.code }"
            />
            <button @click="sendCode" :disabled="resendDisabled" class="fp-send-code">
              {{ resendText }}
            </button>
          </div>
          <p class="fp-error" v-if="errors.code">{{ errors.code }}</p>
          <div class="fp-code-info">
            <span>验证码已发送至 {{ maskedPhone }}</span>
          </div>
        </div>
        <div class="fp-btn-group">
          <button @click="prevStep" class="fp-btn fp-btn--secondary">返回</button>
          <button @click="nextStep" class="fp-btn">验证</button>
        </div>
      </div>

      <!-- 步骤3：重置密码 -->
      <div v-if="step === 3" class="fp-form">
        <div class="fp-form-group">
          <label class="fp-label">新密码</label>
          <div class="fp-password-group">
            <input
              :type="showPwd ? 'text' : 'password'"
              v-model="newPwd"
              placeholder="请设置新密码"
              :class="{ 'fp-input': true, 'fp-input--error': errors.newPwd }"
            />
            <button @click="toggleShowPwd" class="fp-toggle-pwd" type="button">
              <Eye v-if="!showPwd" :size="20" />
              <EyeOff v-else :size="20" />
            </button>
          </div>
          <p class="fp-error" v-if="errors.newPwd">{{ errors.newPwd }}</p>
        </div>

        <div class="fp-form-group">
          <label class="fp-label">确认密码</label>
          <div class="fp-password-group">
            <input
              :type="showPwd ? 'text' : 'password'"
              v-model="confirmPwd"
              placeholder="请再次输入密码"
              :class="{ 'fp-input': true, 'fp-input--error': errors.confirmPwd }"
            />
            <button @click="toggleShowPwd" class="fp-toggle-pwd" type="button">
              <Eye v-if="!showPwd" :size="20" />
              <EyeOff v-else :size="20" />
            </button>
          </div>
          <p class="fp-error" v-if="errors.confirmPwd">{{ errors.confirmPwd }}</p>
        </div>

        <div class="fp-btn-group">
          <button @click="prevStep" class="fp-btn fp-btn--secondary">返回</button>
          <button @click="handleResetPassword" class="fp-btn">完成重置</button>
        </div>
      </div>

      <!-- 成功提示 -->
      <div v-if="success" class="fp-success">
        <h3>密码重置成功！</h3>
        <p>请使用新密码登录</p>
        <button class="fp-btn" @click="toLogin">返回登录</button>
      </div>
    </div>

    <div class="fp-footer">
      <a @click="toLogin" class="fp-link">想起密码了？返回登录</a>
      <p class="fp-support">客服支持：1234567890</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Eye, EyeOff } from 'lucide-vue-next' // 引入眼睛图标
import { sendVerificationCode as apiSendCode, resetPassword as apiResetPassword } from '@/api/auth'

const router = useRouter()

// --- 粒子效果逻辑开始 ---
const canvasRef = ref<HTMLCanvasElement | null>(null)
let ctx: CanvasRenderingContext2D | null = null
let animationFrameId: number
let particles: Particle[] = []
const mouse = { x: -1000, y: -1000 }

class Particle {
  x: number
  y: number
  vx: number
  vy: number
  size: number
  constructor(w: number, h: number) {
    this.x = Math.random() * w
    this.y = Math.random() * h
    this.vx = (Math.random() - 0.5) * 1.2
    this.vy = (Math.random() - 0.5) * 1.2
    this.size = Math.random() * 2 + 1
  }
  update(w: number, h: number) {
    this.x += this.vx
    this.y += this.vy
    if (this.x < 0 || this.x > w) this.vx *= -1
    if (this.y < 0 || this.y > h) this.vy *= -1
    const dx = mouse.x - this.x
    const dy = mouse.y - this.y
    const distance = Math.sqrt(dx * dx + dy * dy)
    if (distance < 120) {
      this.x -= dx * 0.01
      this.y -= dy * 0.01
    }
  }
  draw(context: CanvasRenderingContext2D) {
    context.fillStyle = 'rgba(0, 124, 39, 0.4)'
    context.beginPath()
    context.arc(this.x, this.y, this.size, 0, Math.PI * 2)
    context.fill()
  }
}

const initCanvas = () => {
  const canvas = canvasRef.value
  if (!canvas) return
  ctx = canvas.getContext('2d')
  handleResize()
  if (ctx) {
    particles = Array.from({ length: 80 }, () => new Particle(canvas.width, canvas.height))
    animate()
  }
}

const handleResize = () => {
  const canvas = canvasRef.value
  if (!canvas) return
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight
}

const handleMouseMove = (e: MouseEvent) => {
  mouse.x = e.clientX
  mouse.y = e.clientY
}

const animate = () => {
  const canvas = canvasRef.value
  const currentCtx = ctx
  if (!canvas || !currentCtx) return
  const width = canvas.width
  const height = canvas.height
  currentCtx.clearRect(0, 0, width, height)
  particles.forEach((p, i) => {
    p.update(width, height)
    p.draw(currentCtx)
    for (let j = i + 1; j < particles.length; j++) {
      const dx = p.x - particles[j].x
      const dy = p.y - particles[j].y
      const dist = Math.sqrt(dx * dx + dy * dy)
      if (dist < 100) {
        currentCtx.strokeStyle = `rgba(0, 124, 39, ${0.15 * (1 - dist / 100)})`
        currentCtx.lineWidth = 0.5
        currentCtx.beginPath()
        currentCtx.moveTo(p.x, p.y)
        currentCtx.lineTo(particles[j].x, particles[j].y)
        currentCtx.stroke()
      }
    }
  })
  animationFrameId = requestAnimationFrame(animate)
}

onMounted(() => {
  initCanvas()
  window.addEventListener('resize', handleResize)
  window.addEventListener('mousemove', handleMouseMove)
})

onUnmounted(() => {
  cancelAnimationFrame(animationFrameId)
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('mousemove', handleMouseMove)
})
// --- 粒子效果逻辑结束 ---

const step = ref(1)
const phone = ref('')
const code = ref('')
const newPwd = ref('')
const confirmPwd = ref('')
const showPwd = ref(false)
const success = ref(false)
const resendDisabled = ref(false)
const resendSeconds = ref(60)

const errors = reactive({
  phone: '',
  code: '',
  newPwd: '',
  confirmPwd: '',
})

const stepDesc = computed(() => {
  const descs = [
    '请输入您注册的手机号，我们将发送验证码',
    '请输入收到的验证码进行验证',
    '请设置您的新密码',
  ]
  return descs[step.value - 1]
})

const resendText = computed(() => {
  return resendDisabled.value ? `重新发送(${resendSeconds.value}s)` : '发送验证码'
})

const maskedPhone = computed(() => {
  if (phone.value.length >= 7) {
    return phone.value.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
  }
  return phone.value
})

const nextStep = () => {
  if (step.value === 1 && validatePhone()) {
    sendCode()
    step.value = 2
  } else if (step.value === 2 && validateCode()) {
    step.value = 3
  }
}

const prevStep = () => {
  if (step.value > 1) step.value--
}

const validatePhone = () => {
  errors.phone = ''
  if (!phone.value) {
    errors.phone = '请输入手机号'
    return false
  }
  if (!/^1[3-9]\d{9}$/.test(phone.value)) {
    errors.phone = '手机号格式不正确'
    return false
  }
  return true
}

const validateCode = () => {
  errors.code = ''
  if (!code.value) {
    errors.code = '请输入验证码'
    return false
  }
  if (!/^\d{6}$/.test(code.value)) {
    errors.code = '验证码为6位数字'
    return false
  }
  return true
}

const validatePassword = () => {
  let valid = true
  errors.newPwd = ''
  errors.confirmPwd = ''
  if (newPwd.value.length < 8) {
    errors.newPwd = '密码至少8位'
    valid = false
  }
  if (newPwd.value !== confirmPwd.value) {
    errors.confirmPwd = '两次密码不一致'
    valid = false
  }
  return valid
}

const sendCode = async () => {
  if (!validatePhone()) return
  try {
    await apiSendCode({ phone: phone.value })
    startResendTimer()
  } catch (e) {
    errors.phone = '验证码发送失败'
  }
}

const handleResetPassword = async () => {
  if (!validatePassword()) return
  try {
    await apiResetPassword({
      phone: phone.value,
      verificationCode: code.value,
      newPassword: newPwd.value,
      confirmPassword: confirmPwd.value,
    })
    success.value = true
  } catch (e) {
    errors.newPwd = '重置密码失败'
  }
}

const toggleShowPwd = () => {
  showPwd.value = !showPwd.value
}

const startResendTimer = () => {
  resendDisabled.value = true
  resendSeconds.value = 60
  const timer = setInterval(() => {
    resendSeconds.value--
    if (resendSeconds.value <= 0) {
      clearInterval(timer)
      resendDisabled.value = false
    }
  }, 1000)
}

const toLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.fp-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100vh;
  background-color: #f0f2f5; /* 移除 bg.jpg */
  position: relative;
  overflow: hidden;
}

.particle-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

.fp-box {
  position: relative;
  z-index: 1; /* 保证卡片在粒子上方 */
  width: 100%;
  max-width: 400px;
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.fp-title {
  text-align: center;
  color: #333;
  margin-bottom: 10px;
}

.fp-desc {
  text-align: center;
  color: #666;
  margin-bottom: 30px;
  font-size: 14px;
}

.fp-steps {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  font-size: 14px;
}

.fp-step {
  color: #999;
  padding-bottom: 5px;
}

.fp-step--active {
  color: #007c27;
  font-weight: bold;
}

.fp-step--done {
  color: #1ad6a1;
}

.fp-form-group {
  margin-bottom: 20px;
  width: 100%; /* 修复了之前固定的 375px 可能导致的溢出问题 */
}

.fp-label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-size: 14px;
}

.fp-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 15px;
  box-sizing: border-box;
}

.fp-input:focus {
  outline: none;
  border-color: #007c27;
}

.fp-input--error {
  border-color: #ff4d4f;
}

.fp-error {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 5px;
}

.fp-btn {
  width: 100%;
  padding: 12px;
  background: #007c27;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 15px;
  cursor: pointer;
  transition: background 0.2s;
}

.fp-btn:hover {
  background: #006620;
}

.fp-btn--secondary {
  background: #f5f5f5;
  color: #333;
}

.fp-btn--secondary:hover {
  background: #e9e9e9;
}

.fp-btn-group {
  display: flex;
  gap: 10px;
}

.fp-code-info {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}

.fp-success {
  text-align: center;
  padding: 20px 0;
}

.fp-success h3 {
  color: #333;
  margin-bottom: 10px;
}

.fp-success p {
  color: #666;
  margin-bottom: 20px;
  font-size: 14px;
}

.fp-footer {
  position: relative;
  z-index: 1;
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
}

.fp-link {
  color: #888; /* 稍微加深了颜色提高可读性 */
  text-decoration: underline;
  cursor: pointer;
}

.fp-support {
  color: #999;
  margin-top: 10px;
  font-size: 12px;
}

.fp-code-input-group {
  display: flex;
  gap: 12px;
}

.fp-send-code {
  min-width: 120px;
  padding: 0 16px;
  background-color: #007c27;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.fp-send-code:disabled {
  background-color: #c0c4cc;
  cursor: not-allowed;
}

.fp-password-group {
  position: relative;
}

/* 眼睛图标按钮样式优化 */
.fp-toggle-pwd {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px;
  transition: color 0.2s;
}

.fp-toggle-pwd:hover {
  color: #007c27;
}

.fp-code-info {
  margin-top: 8px;
  font-size: 14px;
  color: #666;
}
</style>
