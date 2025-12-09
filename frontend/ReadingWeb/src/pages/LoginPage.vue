<!-- LoginPage.vue -->
<script setup lang="ts">
import { ref, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 登录 / 注册模式切换
const isSignUp = ref(false)

// 登录表单字段
const isCaptchaLogin = ref(false)
const phone = ref('')
const username = ref('')
const password = ref('')
const code = ref('')

// 注册表单字段
const signUpUsername = ref('')
const signUpPhone = ref('')
const signUpPassword = ref('')
const signUpConfirmPassword = ref('')
const signUpCode = ref('')

// 根据路由参数决定显示登录或注册界面
watchEffect(() => {
  isSignUp.value = route.query.mode === 'signup'
})

// 点击 "注册" 按钮
function handleSignUp() {
  isSignUp.value = true
}

// 点击 "登录" 按钮（异步函数）
function handleLogin() {
  isSignUp.value = false
}

// 切换验证码 / 密码登录
function toggleCaptchaLogin() {
  isCaptchaLogin.value = !isCaptchaLogin.value
  phone.value = ''
  password.value = ''
  code.value = ''
}

// 发送验证码（登录）
function sendCode() {
  if (!phone.value || phone.value.trim().length < 6) {
    window.alert('请输入有效的手机号')
    return
  }
  window.alert(`已向 ${phone.value} 发送验证码（模拟）`)
}

// 发送验证码（注册）
function sendSignUpCode() {
  if (!signUpPhone.value || signUpPhone.value.trim().length < 6) {
    window.alert('请输入有效的手机号')
    return
  }
  window.alert(`已向 ${signUpPhone.value} 发送验证码（模拟）`)
}

// 忘记密码跳转
function goForgetPassword() {
  router.push('/forget-password')
}

// 模拟登录函数（用于测试，不需要真实API）
async function simulateLogin() {
  // 模拟登录成功
  userStore.login({
    token: userStore.generateToken(),
    userInfo: {
      name: username.value || '测试用户',
      avatar: 'https://picsum.photos/200/200',
    },
  })

  // 检查是否有重定向参数
  const redirect = route.query.redirect as string

  // 如果有重定向路径，就跳转回去
  if (redirect) {
    router.push(redirect)
  } else {
    // 否则跳转到首页
    router.push('/')
  }
}
</script>

<template>
  <section class="user">
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
          <form class="forms_form" @submit.prevent="simulateLogin">
            <fieldset class="forms_fieldset">
              <div class="forms_field">
                <!-- 根据 isCaptchaLogin 切换输入类型 / 占位 -->
                <input
                  v-model="username"
                  type="text"
                  placeholder="用户名/邮箱/手机号"
                  class="forms_field-input"
                  required
                  autofocus
                />
              </div>

              <div class="forms_field" style="display: flex; align-items: center; gap: 8px">
                <input
                  v-model="password"
                  type="password"
                  placeholder="密码"
                  class="forms_field-input"
                  required
                />
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
              <button type="submit" class="forms_buttons-action">登录</button>
            </div>
          </form>
        </div>

        <!-- 注册表单 -->
        <div class="user_forms-signup">
          <h2 class="forms_title">注册</h2>
          <form class="forms_form">
            <fieldset class="forms_fieldset">
              <!-- 用户名 -->
              <div class="forms_field">
                <input
                  v-model="username"
                  type="text"
                  placeholder="用户名"
                  class="forms_field-input"
                  required
                />
              </div>

              <!-- 手机号码 -->
              <div class="forms_field">
                <input
                  v-model="signUpPhone"
                  type="tel"
                  placeholder="手机号码"
                  class="forms_field-input"
                  required
                />
              </div>
              <div class="forms_field">
                <input type="tel" placeholder="手机号码" class="forms_field-input" required />
              </div>

              <!-- 密码 -->
              <div class="forms_field">
                <input
                  v-model="signUpPassword"
                  type="password"
                  placeholder="密码"
                  class="forms_field-input"
                  required
                />
              </div>

              <!-- 确认密码 -->
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
              <button type="submit" class="forms_buttons-action">注册</button>
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
input::-moz-placeholder {
  font-size: 0.85rem;
  font-family: 'Montserrat', sans-serif;
  font-weight: 300;
  letter-spacing: 0.1rem;
  color: #ccc;
}
input:-ms-input-placeholder {
  font-size: 0.85rem;
  font-family: 'Montserrat', sans-serif;
  font-weight: 300;
  letter-spacing: 0.1rem;
  color: #ccc;
}
input::placeholder {
  font-size: 0.85rem;
  font-family: 'Montserrat', sans-serif;
  font-weight: 300;
  letter-spacing: 0.1rem;
  color: #ccc;
}

@-webkit-keyframes bounceLeft {
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
/**
 * * Bounce to the left side
 * */
@-webkit-keyframes bounceRight {
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

@-webkit-keyframes showSignUp {
  100% {
    opacity: 1;
    visibility: visible;
    transform: translate3d(0, 0, 0);
  }
}
@keyframes showSignUp {
  100% {
    opacity: 1;
    visibility: visible;
    transform: translate3d(0, 0, 0);
  }
}

.user {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100vh;
  background: url('@/img/bg.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}
.user_options-container {
  position: relative;
  width: 80%;
}
.user_options-text {
  display: flex;
  justify-content: space-between;
  width: 100%;
  background-color: rgba(241, 241, 241, 0.9);
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
}
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
.forms_buttons {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.forms_buttons-left {
  display: flex;
  align-items: center;
  gap: 16px; /* 两个按钮的间距，可以改成8~20px按需求 */
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
  align-items: center; /* 垂直居中 */
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
  -webkit-animation: bounceLeft 1s forwards;
  animation: bounceLeft 1s forwards;
}
.user_options-forms.bounceLeft .user_forms-signup {
  -webkit-animation: showSignUp 1s forwards;
  animation: showSignUp 1s forwards;
}
.user_options-forms.bounceLeft .user_forms-login {
  opacity: 0;
  visibility: hidden;
  transform: translate3d(-120px, 0, 0);
}
.user_options-forms.bounceRight {
  -webkit-animation: bounceRight 1s forwards;
  animation: bounceRight 1s forwards;
}

/**
 * * Responsive 990px
 * */
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
