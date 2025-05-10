
<template>
  <div class="login-page">
    <div class="login-container">
      <div class="welcome-section">
        <h1>主数据管理系统</h1>
        <h2>欢迎回来！</h2>
<!--        <p>请使用您的个人信息登录以保持连接</p>-->
      </div>
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
          />
        </div>

        <button type="submit">登录</button>
        <div class="links">
          <router-link to="/register">新用户？注册</router-link>
<!--          <router-link to="/forgot-password">忘记密码？</router-link>-->
        </div>
<!--        <p class="terms">使用条款：Whites editor</p>-->
      </form>

      <!-- 悬浮提示框 -->
      <div v-if="showToast" class="toast">
        {{ toastMessage }}
      </div>

    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      username: '',
      password: '',
      showToast: false,
      toastMessage: '',
      isLoading: false // 添加加载状态
    };
  },
  methods: {
    async handleSubmit() {
      if (!this.username || !this.password) {
        this.showToastMessage('用户名和密码不能为空');
        return;
      }

      this.isLoading = true; // 开始加载

      try {
        const response = await axios.post('/api/user/login', {
          username: this.username,
          password: this.password
        });

        if (response.data.code === 200) {
          // 存储token和用户信息
          localStorage.setItem('token', response.data.data.token);
          localStorage.setItem('user', JSON.stringify(response.data.data.user));

          this.showToastMessage(response.data.message || '登录成功！');

          // 获取重定向路径或默认跳转到首页
          sessionStorage.setItem('userData', JSON.stringify(response.data.data));

          const redirectPath = this.$route.query.redirect || '/home';

          setTimeout(() => {
            this.$router.push(redirectPath);
          }, 400);

        } else if (response.data.code === 500) {
          this.showToastMessage(response.data.message );
        }
      } catch (error) {
        console.log(error);
        if (error.response) {
          this.showToastMessage(error.response.data.message || '登录失败');
        } else {
          this.showToastMessage('请求失败，请检查网络连接');
        }
      } finally {
        this.isLoading = false; // 结束加载
      }
    },
    showToastMessage(message) {
      this.toastMessage = message;
      this.showToast = true;
      setTimeout(() => {
        this.showToast = false;
      }, 3000);
    }
  }
};
</script>


<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #e3d2ff, #e3d2ff); /* 渐变色背景 */
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
  background-color: #9478cc; /* 左侧背景颜色 */
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

.welcome-section p {
  font-size: 1rem;
  line-height: 1.5;
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
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 1rem;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #9478cc; /* 按钮背景颜色 */
  color: #fff;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  cursor: pointer;
  margin-top: 20px;
}

button:hover {
  background-color: #2575fc; /* 按钮悬停颜色 */
}

.links {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
}

.links a {
  color: #9478cc; /* 链接颜色 */
  text-decoration: none;
  font-size: 0.9rem;
}

.links a:hover {
  text-decoration: underline;
}

.terms {
  text-align: center;
  margin-top: 20px;
  font-size: 0.8rem;
  color: #666;
}

/* 悬浮提示框样式 */
.toast {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  background-color: #9478cc;
  color: #fff;
  padding: 10px 20px;
  border-radius: 5px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  animation: fadeInOut 3s ease-in-out;
}

@keyframes fadeInOut {
  0% {
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    opacity: 0;
  }
}
</style>