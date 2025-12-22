<template>
  <div class="login-page">
    <div class="login-container">
      <!-- 左侧欢迎区域 -->
      <div class="welcome-section">
        <h1>主数据管理系统</h1>
        <h2>欢迎回来！</h2>
        <!-- <p>请使用您的个人信息登录以保持连接</p> -->
      </div>

      <!-- 右侧登录表单 -->
      <form @submit.prevent="handleSubmit" class="form-section">
        <h2>登录</h2>

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
          {{ isLoading ? '登录中...' : '登录' }}
        </button>

        <div class="links">
          <router-link to="/register">新用户？注册</router-link>
          <!-- <router-link to="/forgot-password">忘记密码？</router-link> -->
        </div>

        <!-- <p class="terms">使用条款：Whites editor</p> -->
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const username = ref('')
const password = ref('')
const isLoading = ref(false)

const router = useRouter()
const route = useRoute()

const handleSubmit = async () => {
  if (!username.value.trim() || !password.value) {
    ElMessage.warning('用户名和密码不能为空')
    return
  }

  isLoading.value = true

  try {
    const response = await axios.post('/api/user/login', {
      username: username.value.trim(),
      password: password.value
    })

    if (response.data.code === 200) {
      const { token, user } = response.data.data

      // 存储登录信息
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))
      sessionStorage.setItem('userData', JSON.stringify(response.data.data))

      ElMessage.success(response.data.msg || '登录成功！')

      // 跳转：优先使用 redirect 参数，否则去首页
      const redirectPath = route.query.redirect || '/home'

      setTimeout(() => {
        router.push(redirectPath)
      }, 400)
    } else {
      ElMessage.error(response.data.msg || '登录失败')
    }
  } catch (error) {
    console.error('登录错误:', error)
    if (error.response?.data?.msg) {
      ElMessage.error(error.response.data.msg)
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #e3d2ff, #e3d2ff);
  font-family: Arial, sans-serif;
}

.login-container {
  display: flex;
  width: 800px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.welcome-section {
  flex: 1;
  padding: 40px;
  background-color: #9478cc;
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.welcome-section h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
}

.welcome-section h2 {
  font-size: 1.8rem;
  margin-bottom: 20px;
}

.form-section {
  flex: 1;
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-section h2 {
  font-size: 1.8rem;
  margin-bottom: 20px;
  color: #333;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #333;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 1rem;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #9478cc;
  box-shadow: 0 0 0 2px rgba(148, 120, 204, 0.2);
}

.submit-btn {
  width: 100%;
  padding: 10px;
  background-color: #9478cc;
  color: #fff;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  cursor: pointer;
  margin-top: 20px;
  position: relative;
  transition: background-color 0.3s;
}

.submit-btn:hover:not(:disabled) {
  background-color: #2575fc;
}

.submit-btn:disabled {
  background-color: #a992d1;
  cursor: not-allowed;
}

.loading-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid #fff;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 8px;
  vertical-align: middle;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.links {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
}

.links a {
  color: #9478cc;
  text-decoration: none;
  font-size: 0.9rem;
}

.links a:hover {
  text-decoration: underline;
}
</style>