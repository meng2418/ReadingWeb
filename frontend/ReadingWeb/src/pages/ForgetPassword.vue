<template>
  <div class="fp-container">
    <div class="fp-box">
      <h1 class="fp-title">找回密码</h1>
      <p class="fp-desc">{{ stepDesc }}</p>
      
      <!-- 步骤指示器 -->
      <div class="fp-steps">
        <span :class="{'fp-step': true, 'fp-step--active': step === 1, 'fp-step--done': step > 1}">1. 验证邮箱</span>
        <span :class="{'fp-step': true, 'fp-step--active': step === 2, 'fp-step--done': step > 2}">2. 验证码</span>
        <span :class="{'fp-step': true, 'fp-step--active': step === 3}">3. 重置密码</span>
      </div>
      
      <!-- 步骤1：输入邮箱 -->
      <div v-if="step === 1" class="fp-form">
        <div class="fp-form-group">
          <label class="fp-label">邮箱地址</label>
          <input 
            type="email" 
            v-model="email" 
            placeholder="请输入注册邮箱"
            :class="{'fp-input': true, 'fp-input--error': errors.email}"
          >
          <p class="fp-error" v-if="errors.email">{{ errors.email }}</p>
        </div>
        <button class="fp-btn" @click="nextStep">发送验证码</button>
      </div>
      
      <!-- 步骤2：输入验证码 -->
      <div v-if="step === 2" class="fp-form">
        <div class="fp-form-group">
          <label class="fp-label">验证码</label>
          <input 
            type="text" 
            v-model="code" 
            placeholder="请输入6位验证码"
            :class="{'fp-input': true, 'fp-input--error': errors.code}"
          >
          <p class="fp-error" v-if="errors.code">{{ errors.code }}</p>
          <div class="fp-code-info">
            <span>验证码已发送至 {{ email }}</span>
            <button 
              @click="resendCode" 
              :disabled="resendDisabled"
              class="fp-resend"
            >
              {{ resendText }}
            </button>
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
          <input 
            :type="showPwd ? 'text' : 'password'" 
            v-model="newPwd" 
            placeholder="请设置新密码"
            :class="{'fp-input': true, 'fp-input--error': errors.newPwd}"
          >
          <p class="fp-error" v-if="errors.newPwd">{{ errors.newPwd }}</p>
        </div>
        
        <div class="fp-form-group">
          <label class="fp-label">确认密码</label>
          <input 
            :type="showPwd ? 'text' : 'password'" 
            v-model="confirmPwd" 
            placeholder="请再次输入密码"
            :class="{'fp-input': true, 'fp-input--error': errors.confirmPwd}"
          >
          <p class="fp-error" v-if="errors.confirmPwd">{{ errors.confirmPwd }}</p>
        </div>
        
        <div class="fp-btn-group">
          <button @click="prevStep" class="fp-btn fp-btn--secondary">返回</button>
          <button @click="resetPassword" class="fp-btn">完成重置</button>
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
      <p class="fp-support">客服支持：649609693@qq.com</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      step: 1,
      email: '',
      code: '',
      newPwd: '',
      confirmPwd: '',
      showPwd: false,
      success: false,
      resendDisabled: false,
      resendSeconds: 60,
      errors: {
        email: '',
        code: '',
        newPwd: '',
        confirmPwd: ''
      }
    }
  },
  computed: {
    stepDesc() {
      const descs = [
        '请输入您注册的邮箱，我们将发送验证码',
        '请输入收到的验证码进行验证',
        '请设置您的新密码'
      ]
      return descs[this.step - 1]
    },
    resendText() {
      return this.resendDisabled 
        ? `重新发送(${this.resendSeconds}s)` 
        : '重新发送'
    }
  },
  methods: {
    nextStep() {
      if (this.step === 1 && this.validateEmail()) {
        this.step = 2
        this.startResendTimer()
      } else if (this.step === 2 && this.validateCode()) {
        this.step = 3
      }
    },
    prevStep() {
      if (this.step > 1) this.step--
    },
    validateEmail() {
      this.errors.email = ''
      if (!this.email) {
        this.errors.email = '请输入邮箱'
        return false
      }
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email)) {
        this.errors.email = '邮箱格式不正确'
        return false
      }
      return true
    },
    validateCode() {
      this.errors.code = ''
      if (!this.code) {
        this.errors.code = '请输入验证码'
        return false
      }
      if (this.code.length !== 6) {
        this.errors.code = '验证码长度为6位'
        return false
      }
      return true
    },
    validatePassword() {
      let valid = true
      this.errors.newPwd = ''
      this.errors.confirmPwd = ''
      
      if (this.newPwd.length < 8) {
        this.errors.newPwd = '密码至少8位'
        valid = false
      }
      if (this.newPwd !== this.confirmPwd) {
        this.errors.confirmPwd = '两次密码不一致'
        valid = false
      }
      return valid
    },
    resetPassword() {
      if (this.validatePassword()) {
        // 模拟API请求
        setTimeout(() => {
          this.success = true
        }, 800)
      }
    },
    resendCode() {
      if (!this.resendDisabled) {
        this.code = ''
        this.startResendTimer()
      }
    },
    startResendTimer() {
      this.resendDisabled = true
      const timer = setInterval(() => {
        this.resendSeconds--
        if (this.resendSeconds <= 0) {
          clearInterval(timer)
          this.resendDisabled = false
          this.resendSeconds = 60
        }
      }, 1000)
    },
    toLogin() {
      this.$router.push('/login')
      console.log('前往登录页')
    }
  }
}
</script>

<style scoped>
/* 基础样式 */
.fp-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.fp-box {
  width: 100%;
  max-width: 400px;
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

/* 标题区域 */
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

/* 步骤指示器 */
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

/* 表单样式 */
.fp-form-group {
  margin-bottom: 20px;
  width: 375px;
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

/* 按钮样式 */
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

/* 验证码信息 */
.fp-code-info {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}

.fp-resend {
  width: auto;
  padding: 0;
  background: none;
  color: #007c27;
  font-size: 12px;
  cursor: pointer;
  border: none;
}

.fp-resend:disabled {
  color: #999;
  cursor: not-allowed;
}

/* 成功提示 */
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

/* 底部信息 */
.fp-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
}

.fp-link {
  color: #007c27;
  text-decoration: underline;
  cursor: pointer;
}

.fp-support {
  color: #999;
  margin-top: 10px;
  font-size: 12px;
}
</style>