<!-- LoginPage.vue -->
<script setup lang="ts">
import { ref, watchEffect, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login, register, sendVerificationCode } from '@/api/auth'
import type { LoginParams } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 登录 / 注册模式切换
const isSignUp = ref(false)

// 登录表单字段
const isCaptchaLogin = ref(false)
const phone = ref('')
const password = ref('')
const code = ref('')

// 注册表单字段
const signUpUsername = ref('')
const signUpPhone = ref('')
const signUpPassword = ref('')
const signUpConfirmPassword = ref('')
const signUpCode = ref('')

// --- 粒子背景逻辑开始 ---
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

    // 鼠标排斥效果
    const dx = mouse.x - this.x
    const dy = mouse.y - this.y
    const distance = Math.sqrt(dx * dx + dy * dy)
    if (distance < 120) {
      this.x -= dx * 0.01
      this.y -= dy * 0.01
    }
  }
  draw() {
    if (!ctx) return
    ctx.fillStyle = 'rgba(0, 124, 39, 0.4)' // 使用主题绿色
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2)
    ctx.fill()
  }
}

const initCanvas = () => {
  const canvas = canvasRef.value
  if (!canvas) return
  ctx = canvas.getContext('2d')
  handleResize()
  particles = Array.from({ length: 100 }, () => new Particle(canvas.width, canvas.height))
  animate()
}

const handleResize = () => {
  if (!canvasRef.value) return
  canvasRef.value.width = window.innerWidth
  canvasRef.value.height = window.innerHeight
}

const handleMouseMove = (e: MouseEvent) => {
  mouse.x = e.clientX
  mouse.y = e.clientY
}

const animate = () => {
  if (!ctx || !canvasRef.value) return
  ctx.clearRect(0, 0, canvasRef.value.width, canvasRef.value.height)

  particles.forEach((p, i) => {
    p.update(canvasRef.value!.width, canvasRef.value!.height)
    p.draw()
    // 连线逻辑
    for (let j = i + 1; j < particles.length; j++) {
      const dx = p.x - particles[j].x
      const dy = p.y - particles[j].y
      const dist = Math.sqrt(dx * dx + dy * dy)
      if (dist < 100) {
        ctx!.strokeStyle = `rgba(0, 124, 39, ${0.15 * (1 - dist / 100)})`
        ctx!.lineWidth = 0.5
        ctx!.beginPath()
        ctx!.moveTo(p.x, p.y)
        ctx!.lineTo(particles[j].x, particles[j].y)
        ctx!.stroke()
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
// --- 粒子背景逻辑结束 ---

watchEffect(() => {
  isSignUp.value = route.query.mode === 'signup'
})

function handleSignUp() {
  isSignUp.value = true
}

function handleLogin() {
  isSignUp.value = false
}

function toggleCaptchaLogin() {
  isCaptchaLogin.value = !isCaptchaLogin.value
  phone.value = ''
  password.value = ''
  code.value = ''
}

async function sendCode() {
  if (!phone.value) {
    alert('请输入手机号')
    return
  }
  try {
    await sendVerificationCode({ phone: phone.value })
    alert('验证码已发送')
  } catch (e: any) {
    alert(e?.response?.data?.message || '发送验证码失败')
  }
}

async function sendSignUpCode() {
  if (!signUpPhone.value) {
    alert('请输入手机号')
    return
  }
  try {
    await sendVerificationCode({ phone: signUpPhone.value })
    alert('验证码已发送')
  } catch (e: any) {
    alert(e?.response?.data?.message || '发送验证码失败')
  }
}

function goForgetPassword() {
  router.push('/forget-password')
}

async function submitLogin() {
  if (!phone.value) {
    alert('请输入手机号')
    return
  }
  const payload: LoginParams = {
    phone: phone.value,
    type: isCaptchaLogin.value ? 'verificationCode' : 'password',
    password: isCaptchaLogin.value ? null : password.value,
    verificationCode: isCaptchaLogin.value ? code.value : null,
  }

  try {
    const res = await login(payload)

    // 🔍 1. 解构后端返回的数据
    // 根据你的 OpenAPI，结构是: res.data.data = { token: '...', user: { ... } }
    const { token, user } = res.data.data

    // 🔍 2. 调用 Store 的 login action
    // 注意：你的 Store 里期望的参数结构是 { token, userInfo }
    userStore.login({
      token: token,
      userInfo: {
        ...user, // 保留 userId, bio, avatar 等
        name: user.username, // 👈 手动把 username 映射为 store 习惯的 name
      },
    })

    // 3. 跳转首页
    router.push('/')
  } catch (err: any) {
    console.error(err)
    alert(err?.response?.data?.message || '登录失败')
  }
}

async function submitRegister() {
  if (!signUpUsername.value) {
    alert('请输入用户名')
    return
  }
  if (!signUpPhone.value) {
    alert('请输入手机号')
    return
  }
  if (!signUpPassword.value || !signUpConfirmPassword.value) {
    alert('请输入密码')
    return
  }
  if (signUpPassword.value !== signUpConfirmPassword.value) {
    alert('两次密码不一致')
    return
  }
  if (!signUpCode.value) {
    alert('请输入验证码')
    return
  }
  const payload = {
    username: signUpUsername.value,
    phone: signUpPhone.value,
    password: signUpPassword.value,
    confirmPassword: signUpConfirmPassword.value,
    verificationCode: signUpCode.value,
  }
  try {
    await register(payload)
    alert('注册成功，请登录')
    isSignUp.value = false
    isCaptchaLogin.value = false
  } catch (e: any) {
    alert(e?.response?.data?.message || '注册失败')
  }
}
</script>

<template>
  <section class="user">
    <!-- 粒子背景画布 -->
    <canvas ref="canvasRef" class="particle-canvas"></canvas>

    <div class="user_options-container">
      <div class="user_options-text">
        <div class="user_options-unregistered">
          <h2 class="user_unregistered-title">还没有账户？</h2>
          <p class="user_unregistered-text">
            覆盖网文与出版书，个性化推荐打破书荒。书圈可聊剧情、晒批注，
            阅读时长还能兑换会员，让碎片时间变成长知识的乐趣时光。
          </p>
          <button class="user_unregistered-signup" @click="handleSignUp">注册</button>
        </div>
        <div class="user_options-registered">
          <h2 class="user_registered-title">已经有账户？</h2>
          <p class="user_registered-text">
            覆盖网文与出版书，个性化推荐打破书荒。书圈可聊剧情、晒批注，
            阅读时长还能兑换会员，让碎片时间变成长知识的乐趣时光。
          </p>
          <button class="user_registered-login" @click="handleLogin">登录</button>
        </div>
      </div>
      <div
        class="user_options-forms"
        :class="isSignUp ? 'bounceLeft' : 'bounceRight'"
        id="user_options-forms"
      >
        <!-- 登录表单 -->
        <div class="user_forms-login">
          <h2 class="forms_title">登录</h2>
          <form class="forms_form" @submit.prevent="submitLogin">
            <fieldset class="forms_fieldset">
              <div class="forms_field">
                <input
                  v-model="phone"
                  type="tel"
                  placeholder="手机号码"
                  class="forms_field-input"
                  required
                  autofocus
                />
              </div>

              <div class="forms_field" style="display: flex; align-items: center; gap: 8px">
                <input
                  v-if="!isCaptchaLogin"
                  v-model="password"
                  type="password"
                  placeholder="密码"
                  class="forms_field-input"
                  required
                />
                <div v-else style="display: flex; gap: 8px; align-items: center; width: 100%">
                  <input
                    v-model="code"
                    type="text"
                    placeholder="验证码"
                    class="forms_field-input"
                    required
                    style="flex: 1"
                  />
                  <button
                    type="button"
                    class="forms_buttons-action"
                    @click="sendCode"
                    style="padding: 6px 12px"
                  >
                    发送验证码
                  </button>
                </div>
              </div>
            </fieldset>

            <div class="forms_buttons">
              <div class="forms_buttons-left">
                <button type="button" class="forms_buttons-forgot" @click="goForgetPassword">
                  忘记密码？
                </button>
                <button type="button" class="forms_buttons-loginway" @click="toggleCaptchaLogin">
                  {{ isCaptchaLogin ? '密码登录' : '验证码登录' }}
                </button>
              </div>
              <input type="submit" value="登录" class="forms_buttons-action" />
            </div>
          </form>
        </div>

        <!-- 注册表单 -->
        <div class="user_forms-signup">
          <h2 class="forms_title">注册</h2>
          <form class="forms_form" @submit.prevent="submitRegister">
            <fieldset class="forms_fieldset">
              <div class="forms_field">
                <input
                  v-model="signUpUsername"
                  type="text"
                  placeholder="用户名"
                  class="forms_field-input"
                  required
                />
              </div>

              <div class="forms_field">
                <input
                  v-model="signUpPhone"
                  type="tel"
                  placeholder="手机号码"
                  class="forms_field-input"
                  required
                />
              </div>

              <div class="forms_field" style="display: flex; align-items: center; gap: 8px">
                <input
                  v-model="signUpCode"
                  type="text"
                  placeholder="验证码"
                  class="forms_field-input"
                  required
                  style="flex: 1"
                />
                <button
                  type="button"
                  class="forms_buttons-action"
                  @click="sendSignUpCode"
                  style="padding: 6px 12px"
                >
                  发送验证码
                </button>
              </div>

              <div class="forms_field">
                <input
                  v-model="signUpPassword"
                  type="password"
                  placeholder="密码"
                  class="forms_field-input"
                  required
                />
              </div>

              <div class="forms_field">
                <input
                  v-model="signUpConfirmPassword"
                  type="password"
                  placeholder="确认密码"
                  class="forms_field-input"
                  required
                />
              </div>
            </fieldset>

            <div class="forms_buttons">
              <input type="submit" value="注册" class="forms_buttons-action" />
            </div>
          </form>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

html,
body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

body {
  font-family: 'Montserrat', sans-serif;
  font-size: 12px;
  line-height: 1em;
}

button {
  background-color: transparent;
  padding: 0;
  border: 0;
  outline: 0;
  cursor: pointer;
}

input {
  background-color: transparent;
  padding: 0;
  border: 0;
  outline: 0;
}
input[type='submit'] {
  cursor: pointer;
}
input::placeholder {
  font-size: 0.85rem;
  font-family: 'Montserrat', sans-serif;
  font-weight: 300;
  letter-spacing: 0.1rem;
  color: #ccc;
}

/* 动画定义保持不变 */
@keyframes bounceLeft {
  0% {
    transform: translate3d(100%, -50%, 0);
  }
  50% {
    transform: translate3d(-30px, -50%, 0);
  }
  100% {
    transform: translate3d(0, -50%, 0);
  }
}
@keyframes bounceRight {
  0% {
    transform: translate3d(0, -50%, 0);
  }
  50% {
    transform: translate3d(calc(100% + 30px), -50%, 0);
  }
  100% {
    transform: translate3d(100%, -50%, 0);
  }
}
@keyframes showSignUp {
  100% {
    opacity: 1;
    visibility: visible;
    transform: translate3d(0, 0, 0);
  }
}

/**
 * * Page background 修改
 * */
.user {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100vh;
  /* 移除 bg.jpg */
  background-color: #f0f2f5;
  position: relative;
  overflow: hidden;
}

.particle-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0; /* 置于底层 */
  pointer-events: none; /* 防止拦截鼠标点击 */
}

.user_options-container {
  position: relative;
  width: 80%;
  z-index: 1; /* 置于粒子上方 */
}

.user_options-text {
  display: flex;
  justify-content: space-between;
  width: 100%;
  background-color: var(--bg-green);
  border-radius: 3px;
}

.user_options-registered,
.user_options-unregistered {
  width: 50%;
  padding: 75px 45px;
  color: #333;
  font-weight: 300;
}

.user_registered-title,
.user_unregistered-title {
  margin-bottom: 15px;
  font-size: 1.66rem;
  line-height: 1em;
}

.user_unregistered-text,
.user_registered-text {
  font-size: 0.83rem;
  line-height: 1.4em;
}

.user_registered-login,
.user_unregistered-signup {
  margin-top: 30px;
  border: 1px solid #333;
  border-radius: 3px;
  padding: 10px 30px;
  color: #333;
  text-transform: uppercase;
  line-height: 1em;
  letter-spacing: 0.2rem;
  transition:
    background-color 0.2s ease-in-out,
    color 0.2s ease-in-out;
}
.user_registered-login:hover,
.user_unregistered-signup:hover {
  color: #fff;
  background-color: #333;
}

.user_options-forms {
  position: absolute;
  top: 50%;
  left: 30px;
  width: calc(50% - 30px);
  min-height: 480px;
  background-color: #fff;
  border-radius: 3px;
  box-shadow: 2px 0 15px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  transform: translate3d(100%, -50%, 0);
  transition: transform 0.4s ease-in-out;
  z-index: 2; /* 确保表单在最顶层 */
}

/* 其余样式逻辑保持不变 */
.user_options-forms .user_forms-login {
  transition:
    opacity 0.4s ease-in-out,
    visibility 0.4s ease-in-out;
}
.user_options-forms .forms_title {
  font-size: 1.5rem;
  font-weight: 500;
  line-height: 1em;
  text-transform: uppercase;
  color: #007c27;
  letter-spacing: 0.1rem;
}
.user_options-forms .forms_field:not(:last-of-type) {
  margin-bottom: 20px;
}
.user_options-forms .forms_field-input {
  width: 100%;
  border-bottom: 1px solid #ccc;
  padding: 6px 20px 6px 6px;
  font-family: 'Montserrat', sans-serif;
  font-size: 1rem;
  font-weight: 300;
  color: gray;
  letter-spacing: 0.1rem;
  transition: border-color 0.2s ease-in-out;
}
.user_options-forms .forms_field-input:focus {
  border-color: gray;
}
.user_options-forms .forms_buttons {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 35px;
}
.user_options-forms .forms_buttons-forgot,
.user_options-forms .forms_buttons-loginway {
  font-family: 'Montserrat', sans-serif;
  letter-spacing: 0.1rem;
  color: #ccc;
  text-decoration: underline;
  transition: color 0.2s ease-in-out;
}
.user_options-forms .forms_buttons-forgot:hover,
.user_options-forms .forms_buttons-loginway:hover {
  color: #b3b3b3;
}
.forms_buttons-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.user_options-forms .forms_buttons-action {
  background-color: #007c27;
  border-radius: 3px;
  padding: 10px 35px;
  font-size: 1rem;
  font-family: 'Montserrat', sans-serif;
  font-weight: 300;
  color: #fff;
  text-transform: uppercase;
  letter-spacing: 0.1rem;
  transition: background-color 0.2s ease-in-out;
}
.user_options-forms .forms_buttons-action:hover {
  background-color: #1ad6a1;
}
.user_options-forms .user_forms-signup,
.user_options-forms .user_forms-login {
  position: absolute;
  top: 20px;
  left: 40px;
  width: calc(100% - 80px);
  opacity: 0;
  visibility: hidden;
  transition:
    opacity 0.4s ease-in-out,
    visibility 0.4s ease-in-out,
    transform 0.5s ease-in-out;
}
.user_options-forms .user_forms-signup {
  transform: translate3d(120px, 0, 0);
}
.user_options-forms .user_forms-signup .forms_buttons {
  justify-content: flex-end;
}
.user_options-forms .user_forms-login {
  transform: translate3d(0, 0, 0);
  opacity: 1;
  visibility: visible;
}

.user_options-forms.bounceLeft {
  animation: bounceLeft 1s forwards;
}
.user_options-forms.bounceLeft .user_forms-signup {
  animation: showSignUp 1s forwards;
}
.user_options-forms.bounceLeft .user_forms-login {
  opacity: 0;
  visibility: hidden;
  transform: translate3d(-120px, 0, 0);
}
.user_options-forms.bounceRight {
  animation: bounceRight 1s forwards;
}

@media screen and (max-width: 990px) {
  .user_options-forms {
    min-height: 350px;
  }
  .user_options-forms .forms_buttons {
    flex-direction: column;
  }
  .user_options-forms .user_forms-login .forms_buttons-action {
    margin-top: 30px;
  }
  .user_options-forms .user_forms-signup,
  .user_options-forms .user_forms-login {
    top: 40px;
  }
  .user_options-registered,
  .user_options-unregistered {
    padding: 50px 45px;
  }
}
</style>
