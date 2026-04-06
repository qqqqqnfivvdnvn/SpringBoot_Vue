<template>
  <div class="login-page">
    <!-- 左侧欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <div class="welcome-icon">&#128274;</div>
        <h1>主数据管理系统</h1>
        <p class="welcome-subtitle">统一管理、高效协作、数据驱动决策</p>
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="form-section">
      <form @submit.prevent="handleSubmit" class="form-wrapper">
        <div class="form-header">
          <h2>欢迎回来</h2>
          <p>请登录您的账户继续使用</p>
        </div>

        <div class="form-group">
          <label for="username">用户名：</label>
          <input
              type="text"
              id="username"
              v-model="username"
              placeholder="请输入用户名"
              required
              autocomplete="username"
          />
        </div>

        <div class="form-group">
          <label for="password">密码：</label>
          <input
              type="password"
              id="password"
              v-model="password"
              placeholder="*********"
              required
              autocomplete="current-password"
          />
        </div>

        <button type="submit" :disabled="isLoading" class="submit-btn">
          <span v-if="isLoading" class="loading-spinner"></span>
          <font-awesome-icon v-else :icon="['fas', 'sign-in-alt']" style="margin-right:6px" />
          {{ isLoading ? '登录中...' : '登录' }}
        </button>

        <div class="links">
          <router-link to="/register">新用户？注册</router-link>
        </div>
      </form>
    </div>

    <!-- Toast 提示 -->
    <transition name="toast-slide">
      <div v-if="showToast" class="toast" :class="toastType">
        <font-awesome-icon :icon="toastType === 'success' ? ['fas', 'check-circle'] : toastType === 'warning' ? ['fas', 'exclamation-circle'] : ['fas', 'times-circle']" class="toast-icon" />
        {{ toastMessage }}
      </div>
    </transition>
  </div>
</template>

<script setup>
import '@/assets/css/dark-mode.css'
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

const username = ref('')
const password = ref('')
const isLoading = ref(false)
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('success')

const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 3000)
}

const router = useRouter()
const route = useRoute()

const handleSubmit = async () => {
  if (!username.value.trim() || !password.value) {
    showToastMessage('用户名和密码不能为空', 'warning')
    return
  }

  isLoading.value = true

  try {
    const response = await axios.post('/api/user/login', {
      username: username.value.trim(),
      password: password.value
    })

    if (response.data.code === 200) {
      const userData = response.data.data
      // 存储登录信息 持久保留
      localStorage.setItem('token', userData.token)
      localStorage.setItem('user', JSON.stringify(userData))
      // 临时数据
      sessionStorage.setItem('userData', JSON.stringify(userData))



      showToastMessage('登录成功')

      // 跳转：优先使用 redirect 参数，否则去首页
      const redirectPath = route.query.redirect || '/home'

      setTimeout(() => {
        router.push(redirectPath)
      }, 400)
    } else {
      showToastMessage(response.data.msg || '登录失败', 'error')
    }
  } catch (error) {
    console.error('登录错误:', error)
    if (error.response?.data?.msg) {
      showToastMessage(error.response.data.msg, 'error')
    } else {
      showToastMessage('网络错误，请稍后重试', 'error')
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  height: 100vh;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  overflow: hidden;
}

/* ===== 左侧欢迎区 ===== */
.welcome-section {
  flex: 1;
  background: linear-gradient(145deg, #7c5cbf 0%, #9d7de8 50%, #b89ff0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  position: relative;
  overflow: hidden;
  transition: background 0.3s ease;
}

/* 装饰圆形背景 */
.welcome-section::before {
  content: '';
  position: absolute;
  width: 500px;
  height: 500px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 50%;
  top: -180px;
  left: -160px;
  pointer-events: none;
}

.welcome-section::after {
  content: '';
  position: absolute;
  width: 360px;
  height: 360px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 50%;
  bottom: -120px;
  right: -100px;
  pointer-events: none;
}

html.dark .welcome-section {
  background: linear-gradient(145deg, #1e1430 0%, #2d2048 50%, #3a2a5e 100%);
}

.welcome-content {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 48px 40px;
  max-width: 420px;
}

.welcome-icon {
  width: 72px;
  height: 72px;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  margin: 0 auto 24px;
  border: 1px solid rgba(255,255,255,0.25);
  backdrop-filter: blur(6px);
}

.welcome-content h1 {
  font-size: 1.9rem;
  font-weight: 700;
  margin-bottom: 10px;
  line-height: 1.3;
  letter-spacing: 0.5px;
}

.welcome-subtitle {
  font-size: 0.92rem;
  opacity: 0.8;
  margin-bottom: 36px;
  line-height: 1.7;
}

.welcome-features {
  text-align: left;
  display: inline-flex;
  flex-direction: column;
  gap: 10px;
}

.feature-item {
  font-size: 0.88rem;
  opacity: 0.9;
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255,255,255,0.1);
  padding: 8px 14px;
  border-radius: 20px;
  border: 1px solid rgba(255,255,255,0.15);
}

/* ===== 右侧表单区 ===== */
.form-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f2f8;
  transition: background-color 0.3s ease;
}

html.dark .form-section {
  background-color: #12121e;
}

.form-wrapper {
  width: 100%;
  max-width: 400px;
  padding: 0 40px;
}

.form-header {
  margin-bottom: 32px;
}

.form-header h2 {
  font-size: 1.75rem;
  font-weight: 700;
  color: #2c2060;
  margin-bottom: 6px;
  transition: color 0.3s ease;
}

html.dark .form-header h2 {
  color: #d0c0f8;
}

.form-header p {
  font-size: 0.88rem;
  color: #9090a8;
  transition: color 0.3s ease;
}

html.dark .form-header p {
  color: #6a6080;
}

.form-group {
  margin-bottom: 18px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  color: #4a4068;
  font-weight: 600;
  font-size: 0.875rem;
  transition: color 0.3s ease;
}

html.dark .form-group label {
  color: #a898cc;
}

.form-group input {
  width: 100%;
  padding: 11px 14px;
  border: 1.5px solid #ddd8f0;
  border-radius: 10px;
  font-size: 0.95rem;
  box-sizing: border-box;
  background-color: #fff;
  color: #333;
  transition: all 0.25s ease;
  font-family: inherit;
}

html.dark .form-group input {
  background-color: #1a1a2c;
  border-color: #38305a;
  color: #e0d8f8;
}

.form-group input::placeholder {
  color: #c0b8d8;
}

html.dark .form-group input::placeholder {
  color: #4a4068;
}

.form-group input:focus {
  outline: none;
  border-color: #9d7de8;
  box-shadow: 0 0 0 3px rgba(157, 125, 232, 0.18);
  background-color: #fff;
}

html.dark .form-group input:focus {
  border-color: #7060b8;
  box-shadow: 0 0 0 3px rgba(112, 96, 184, 0.25);
  background-color: #1e1e30;
}

.submit-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #9d7de8, #7c5cbf);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  margin-top: 8px;
  position: relative;
  transition: all 0.25s ease;
  box-shadow: 0 4px 14px rgba(124, 92, 191, 0.35);
  font-family: inherit;
  letter-spacing: 0.5px;
}

.submit-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #b09af0, #9d7de8);
  box-shadow: 0 6px 20px rgba(124, 92, 191, 0.5);
  transform: translateY(-1px);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 3px 10px rgba(124, 92, 191, 0.3);
}

html.dark .submit-btn {
  background: linear-gradient(135deg, #5a3d8c, #3e246e);
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.4);
}

html.dark .submit-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #7050a8, #5a3d8c);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.5);
}

.submit-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
  transform: none;
}

.loading-spinner {
  display: inline-block;
  width: 15px;
  height: 15px;
  border: 2px solid rgba(255,255,255,0.5);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-right: 8px;
  vertical-align: middle;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.links {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.links a {
  color: #9d7de8;
  text-decoration: none;
  font-size: 0.875rem;
  font-weight: 500;
  transition: color 0.25s ease;
}

html.dark .links a {
  color: #a898cc;
}

.links a:hover {
  color: #7c5cbf;
  text-decoration: underline;
}

html.dark .links a:hover {
  color: #c8b8ee;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .login-page {
    flex-direction: column;
  }

  .welcome-section {
    flex: none;
    height: 220px;
  }

  .welcome-icon {
    width: 52px;
    height: 52px;
    font-size: 1.5rem;
    margin-bottom: 14px;
  }

  .welcome-content h1 {
    font-size: 1.4rem;
  }

  .welcome-subtitle {
    font-size: 0.85rem;
    margin-bottom: 0;
  }

  .welcome-features {
    display: none;
  }

  .welcome-content {
    padding: 24px 20px;
  }

  .form-section {
    flex: 1;
  }

  .form-wrapper {
    padding: 0 24px;
  }
}

/* ===== Toast 提示 ===== */
.toast {
  position: fixed;
  top: 24px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 11px 22px;
  border-radius: 24px;
  font-size: 0.875rem;
  font-weight: 500;
  color: #fff;
  z-index: 9999;
  white-space: nowrap;
  box-shadow: 0 4px 20px rgba(124, 92, 191, 0.4);
  background: linear-gradient(135deg, #9d7de8, #7c5cbf);
}

.toast.warning {
  background: linear-gradient(135deg, #f0a050, #d97820);
  box-shadow: 0 4px 20px rgba(217, 120, 32, 0.4);
}

.toast.error {
  background: linear-gradient(135deg, #e86868, #c04040);
  box-shadow: 0 4px 20px rgba(192, 64, 64, 0.4);
}

html.dark .toast {
  background: linear-gradient(135deg, #5a3d8c, #3e246e);
  box-shadow: 0 4px 24px rgba(0,0,0,0.5);
}

html.dark .toast.warning {
  background: linear-gradient(135deg, #a06020, #7a4010);
  box-shadow: 0 4px 20px rgba(0,0,0,0.4);
}

html.dark .toast.error {
  background: linear-gradient(135deg, #8c3030, #5a1a1a);
  box-shadow: 0 4px 20px rgba(0,0,0,0.4);
}

.toast-icon {
  font-size: 1rem;
  flex-shrink: 0;
}

.toast-slide-enter-active,
.toast-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.toast-slide-enter-from,
.toast-slide-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(-16px) scale(0.9);
}
</style>