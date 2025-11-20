<template>
  <div class="register-page">
    <div class="register-container">
      <div class="welcome-section">
        <h1>主数据管理系统</h1>
        <h2>欢迎加入！</h2>
        <p>请填写以下信息完成注册</p>
      </div>
      <form @submit.prevent="handleSubmit" class="form-section">
        <h2>注册</h2>
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
          <label for="email">邮箱：</label>
          <input
              type="email"
              id="email"
              v-model="email"
              placeholder="请输入邮箱"
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

        <button type="submit">注册</button>
        <div class="links">
          <router-link to="/login">已有账号？登录</router-link>
        </div>

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
      email: '', // 新增邮箱字段
      password: '',
      showToast: false, // 控制提示框显示
      toastMessage: '' // 提示框内容
    };
  },
  methods: {
    async handleSubmit() {
      // 检查用户名、邮箱和密码是否为空
      if (!this.username || !this.email || !this.password) {
        this.showToastMessage('用户名、邮箱和密码不能为空');
        return;
      }

      try {
        const response = await axios.post('/api/user/register', {
          username: this.username,
          email: this.email, // 发送邮箱字段
          password: this.password
        });
        // 根据后端返回的 code 判断是否成功
        if (response.data.code === 200) {
          // this.showToastMessage(response.data.msg || '注册成功!'); // 显示成功提示
          this.showToastMessage( '注册成功!')
          // 延迟 1 秒后跳转到登录页面
          setTimeout(() => {
            this.$router.push('/login');
          }, 500); // 1000 毫秒 = 1 秒
        }
        else {
          this.showToastMessage(response.data.msg  );
        }
      } catch (error) {
        if (error.response) {
          // 如果后端返回了错误信息，显示错误信息
          // this.showToastMessage(error.response.data.msg || '注册失败！');
          this.showToastMessage( '注册失败！');
        } else {
          // 网络错误或其他异常
          this.showToastMessage('注册失败，请检查网络连接');
        }
      }
    },
    showToastMessage(message) {
      this.toastMessage = message;
      this.showToast = true;
      // 3 秒后隐藏提示框
      setTimeout(() => {
        this.showToast = false;
      }, 3000);
    }
  }
};
</script>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #e3d2ff, #e3d2ff);
  font-family: Arial, sans-serif;
}

.register-container {
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
  background-color: #9478cc;
  color: #fff;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  cursor: pointer;
  margin-top: 20px;
}

button:hover {
  background-color: #2575fc;
}

.links {
  text-align: center;
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