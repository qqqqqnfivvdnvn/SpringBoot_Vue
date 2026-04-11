<template>
  <div class="login-page">
    <!-- 左侧欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <div class="welcome-icon">
          <font-awesome-icon :icon="['fas', 'database']" />
        </div>
        <h1>数据管理系统</h1>
        <p class="welcome-subtitle">统一管理、高效协作、数据驱动决策</p>
        <div class="welcome-features">
          <div class="feature-item">
            <font-awesome-icon :icon="['fas', 'check-circle']" />
            <span>统一数据标准</span>
          </div>
          <div class="feature-item">
            <font-awesome-icon :icon="['fas', 'shield-alt']" />
            <span>安全可靠</span>
          </div>
          <div class="feature-item">
            <font-awesome-icon :icon="['fas', 'chart-line']" />
            <span>高效管理</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="form-section">
      <div class="form-card">
        <form @submit.prevent="handleSubmit">
          <div class="form-header">
            <h2>欢迎回来</h2>
            <p>请登录您的账户继续使用</p>
          </div>

          <div class="form-group">
            <label for="username">用户名</label>
            <input
                type="text"
                id="username"
                v-model="username"
                placeholder="请输入用户名"
                required
                autocomplete="username"
                class="form-input"
            />
          </div>

          <div class="form-group">
            <label for="password">密码</label>
            <input
                type="password"
                id="password"
                v-model="password"
                placeholder="请输入密码"
                required
                autocomplete="current-password"
                class="form-input"
            />
          </div>

          <button type="submit" :disabled="isLoading" class="submit-btn">
            <span v-if="isLoading" class="loading-spinner"></span>
            <font-awesome-icon v-else :icon="['fas', 'sign-in-alt']" />
            {{ isLoading ? '登录中...' : '登录' }}
          </button>

          <div class="links">
            <router-link to="/register">新用户？注册账户</router-link>
          </div>
        </form>
      </div>
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
      localStorage.setItem('token', userData.token)
      localStorage.setItem('user', JSON.stringify(userData))
      sessionStorage.setItem('userData', JSON.stringify(userData))

      showToastMessage('登录成功')

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
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  overflow: hidden;
}

/* ===== 左侧欢迎区 ===== */
.welcome-section {
  flex: 1;
  background: linear-gradient(145deg, #8b5cf6 0%, #7c3aed 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  position: relative;
  overflow: hidden;
  transition: background 0.3s ease;
}

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
  background: linear-gradient(145deg, #0f172a 0%, #1e293b 100%);
}

.welcome-content {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 48px 40px;
  max-width: 420px;
}

.welcome-icon {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.2rem;
  margin: 0 auto 24px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(6px);
  color: #fff;
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
  opacity: 0.85;
  margin-bottom: 28px;
  line-height: 1.6;
}

.welcome-features {
  text-align: left;
  display: inline-flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 24px;
}

.feature-item {
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.1);
  padding: 8px 16px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  transition: all 0.2s ease;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(4px);
}

.feature-item font-awesome-icon {
  font-size: 0.9rem;
}

/* ===== 右侧表单区 ===== */
.form-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
  transition: background-color 0.3s ease;
}

html.dark .form-section {
  background: #0f172a;
}

.form-card {
  width: 100%;
  max-width: 420px;
  padding: 48px 44px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

html.dark .form-card {
  background: #1e293b;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.4);
}

.form-header {
  margin-bottom: 32px;
  text-align: center;
}

.form-header h2 {
  font-size: 1.75rem;
  font-weight: 700;
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  color: #1e293b;
  margin-bottom: 8px;
  transition: color 0.3s ease;
}

html.dark .form-header h2 {
  color: #f1f5f9;
}

.form-header p {
  font-size: 0.9rem;
  color: #64748b;
  transition: color 0.3s ease;
}

html.dark .form-header p {
  color: #94a3b8;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #475569;
  font-weight: 600;
  font-size: 0.875rem;
  transition: color 0.3s ease;
}

html.dark .form-group label {
  color: #cbd5e1;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 1.5px solid #e2e8f0;
  border-radius: 10px;
  font-size: 0.95rem;
  box-sizing: border-box;
  background-color: #ffffff;
  color: #1e293b;
  transition: all 0.25s ease;
  font-family: inherit;
}

html.dark .form-input {
  background-color: #0f172a;
  border-color: #475569;
  color: #f1f5f9;
}

.form-input::placeholder {
  color: #94a3b8;
}

html.dark .form-input::placeholder {
  color: #64748b;
}

.form-input:focus {
  outline: none;
  border-color: #8b5cf6;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.15);
  background-color: #ffffff;
}

html.dark .form-input:focus {
  border-color: #a78bfa;
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.25);
  background-color: #0f172a;
}

.form-input:hover:not(:focus) {
  border-color: #cbd5e1;
}

html.dark .form-input:hover:not(:focus) {
  border-color: #64748b;
}

.submit-btn {
  width: 100%;
  padding: 13px 24px;
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  margin-top: 8px;
  position: relative;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.3);
  font-family: inherit;
  letter-spacing: 0.3px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.submit-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #7c3aed, #6d28d9);
  box-shadow: 0 4px 16px rgba(139, 92, 246, 0.4);
  transform: translateY(-2px);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.3);
}

html.dark .submit-btn {
  background: linear-gradient(135deg, #334155, #1e293b);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
}

html.dark .submit-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #475569, #334155);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.5);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.loading-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.5);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.links {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
}

html.dark .links {
  border-top-color: #334155;
}

.links a {
  color: #8b5cf6;
  text-decoration: none;
  font-size: 0.875rem;
  font-weight: 500;
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  transition: all 0.2s ease;
}

html.dark .links a {
  color: #94a3b8;
}

.links a:hover {
  color: #7c3aed;
  text-decoration: underline;
}

html.dark .links a:hover {
  color: #cbd5e1;
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
    width: 56px;
    height: 56px;
    font-size: 1.5rem;
    margin-bottom: 14px;
  }

  .welcome-content h1 {
    font-size: 1.4rem;
  }

  .welcome-subtitle {
    font-size: 0.82rem;
    margin-bottom: 12px;
  }

  .welcome-features {
    display: none;
  }

  .welcome-content {
    padding: 20px;
  }

  .form-section {
    flex: 1;
    padding: 24px 20px;
  }

  .form-card {
    padding: 32px 24px;
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
  padding: 12px 24px;
  border-radius: 24px;
  font-size: 0.875rem;
  font-weight: 500;
  color: #fff;
  z-index: 9999;
  white-space: nowrap;
  box-shadow: 0 4px 20px rgba(139, 92, 246, 0.4);
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
}

.toast.warning {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  box-shadow: 0 4px 20px rgba(245, 158, 11, 0.4);
}

.toast.error {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  box-shadow: 0 4px 20px rgba(239, 68, 68, 0.4);
}

html.dark .toast {
  background: linear-gradient(135deg, #334155, #1e293b);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.5);
}

html.dark .toast.warning {
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.4);
}

html.dark .toast.error {
  background: linear-gradient(135deg, #f87171, #ef4444);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.4);
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
