<template>
  <div class="home-page" :style="homePageStyle">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="navbar-brand">项目管理系统</div>
      <div class="navbar-links">
        <router-link to="/home">首页</router-link>
        <router-link to="/settings">设置</router-link>
        <div class="avatar">
          <img src="@/assets/avatar-modified.png" alt="用户头像" />
        </div>
        <button class="logout-button" @click="handleLogout">
          <font-awesome-icon :icon="['fas', 'sign-out-alt']" size="1x" />
          退出登录
        </button>
      </div>
    </nav>

    <!-- 全屏平铺的项目入口区域 -->
    <div class="fullscreen-projects">
      <div class="projects-container">
        <h2 class="projects-title">我的项目</h2>

        <!-- 项目搜索框 -->
        <div class="projects-search">
          <font-awesome-icon :icon="['fas', 'search']" class="search-icon"/>
          <input
              type="text"
              placeholder="搜索项目..."
              v-model="searchQuery"
              @input="filterProjects"
          >
        </div>

        <!-- 项目分类标签 -->
        <div class="projects-tabs">
          <button
              v-for="tab in tabs"
              :key="tab.id"
              :class="{ 'active': activeTab === tab.id }"
              @click="activeTab = tab.id"
          >
            {{ tab.label }}
          </button>
        </div>

        <!-- 项目卡片网格 -->
        <div class="projects-grid">
          <div
              v-for="project in filteredProjects"
              :key="project.id"
              class="project-card"
              @click="navigateToProject(project)"
          >
            <div class="project-icon" :style="{ backgroundColor: project.color }">
              <font-awesome-icon :icon="['fas', project.icon]" size="2x"/>
            </div>
            <div class="project-info">
              <h3>{{ project.name }}</h3>
              <p>{{ project.description }}</p>
              <div class="project-meta">
<!--                <span><font-awesome-icon :icon="['fas', 'user']"/> {{ project.owner }}</span>-->
                <span><font-awesome-icon :icon="['fas', 'calendar-alt']"/> {{ project.addtime }}</span>
              </div>
            </div>

          </div>
        </div>

        <!-- 添加新项目按钮 -->
        <div class="add-project" @click="showAddProjectDialog">
          <font-awesome-icon :icon="['fas', 'plus-circle']" size="3x"/>
          <span>添加新项目</span>
        </div>
      </div>
    </div>

    <!-- 添加项目对话框 -->
    <div class="dialog-overlay" v-if="showDialog">
      <div class="dialog">
        <h3>添加新项目</h3>

        <div class="dialog-content">
          <div class="form-group">
            <label>项目名称</label>
            <input type="text" v-model="newProject.name" placeholder="输入项目名称">
          </div>
          <div class="form-group">
            <label>项目描述</label>
            <textarea  v-model="newProject.description" placeholder="输入项目描述"></textarea>
          </div>
          <div class="form-group">
            <label>项目图标</label>
            <div class="icon-selector">
              <button
                  v-for="icon in availableIcons"
                  :key="icon"
                  :class="{ 'selected': newProject.icon === icon }"
                  @click="newProject.icon = icon"
              >
                <font-awesome-icon :icon="['fas', icon]"/>
              </button>
            </div>
          </div>
          <div class="form-group">
            <label>项目颜色</label>
            <div class="color-selector">
              <div
                  v-for="color in availableColors"
                  :key="color"
                  :style="{ backgroundColor: color }"
                  :class="{ 'selected': newProject.color === color }"
                  @click="newProject.color = color"
              ></div>
            </div>
          </div>
        </div>
        <div class="dialog-actions">
          <button class="cancel" @click="showDialog = false">取消</button>
          <button class="confirm" @click="addNewProject">确认添加</button>
        </div>
      </div>
    </div>

    <!-- 悬浮提示框 -->
    <div v-if="showToast" class="toast">
      {{ toastMessage }}
    </div>
  </div>
</template>

<script>

import axios from 'axios';
import {onMounted} from "vue";

export default {
  data() {

    return {
      userData:{},
      showToast: false,
      toastMessage: '',
      homePageStyle: {
        background: 'linear-gradient(135deg, #e3d2ff, #e3d2ff)',
      },
      searchQuery: '',
      activeTab: 'all',
      tabs: [
        { id: 'all', label: '全部项目' },
        { id: 'recent', label: '最近访问' },
        // { id: 'favorites', label: '我的收藏' },
        // { id: 'personal', label: '个人项目' },
        // { id: 'team', label: '团队项目' }
      ],
      projects: [],
      showDialog: false,
      newProject: {
        name: '',
        description: '',
        icon: 'project-diagram',
        color: '#9478cc'
      },
      availableIcons: ['project-diagram', 'database', 'chart-line', 'laptop-code', 'users', 'book', 'file-alt', 'hospital', 'store'],
      availableColors: ['#9478cc', '#6ab7ff', '#ff9e7d', '#7dcf85', '#f9c74f', '#90be6d', '#577590', '#f94144']
    };
  },


  // 生命周期钩子中
  created() {
    this.getProjects();

    const tempData = sessionStorage.getItem('userData');
    if (tempData) {
      try {
        this.userData = JSON.parse(tempData);
        console.log('userData:', this.userData);

      } catch (e) {
        console.error('解析 tempData 失败', e);
      }
    }

  },
  computed: {
    filteredProjects() {
      let filtered = this.projects;

      // 按标签筛选
      if (this.activeTab !== 'all') {
        filtered = filtered.filter(project => {
          if (this.activeTab === 'recent') {
            const now = new Date();
            const projectDate = new Date(project.addtime); // 假设 addtime 是 ISO 字符串或时间戳

            const diffDays = Math.floor((now - projectDate) / (1000 * 60 * 60 * 24));
            console.log(diffDays);
            return diffDays <= 3; // 最近7天内的项目
          }
        });
      }

      // 按搜索词筛选
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase();
        filtered = filtered.filter(project =>
            project.name.toLowerCase().includes(query) ||
            project.description.toLowerCase().includes(query)
        );
      }

      return filtered;
    }
  },
  methods: {
    navigateToProject(project) {
      // 检查路由是否存在
      if (!this.$router.hasRoute(project.routeName)) {
        console.error('路由不存在:', project.routeName);
        this.showToastMessage('路由不存在，请联系管理员添加');
        return;
      }

      this.$router.push({
        name: project.routeName,
        params: { id: project.routeName }
      }).catch(error => {
        console.error('路由跳转失败:', error);
        this.showToastMessage('路由跳转失败');
      });
    },

    getProjects() {
      axios.get('/api/projects/getAllProjects')
       .then(response => {
          this.projects = response.data.data;
        })
    },


    showAddProjectDialog() {
      this.newProject = {
        name: '',
        description: '',
        icon: 'project-diagram',
        color: '#9478cc'
      };
      this.showDialog = true;
    },


    async addNewProject() {
      if (!this.newProject.name.trim()) {
        this.showToastMessage('请输入项目名称');

        return;
      }

      const response = await axios.post('/api/projects/addProject', {
        name: this.newProject.name,
        description: this.newProject.description,
        icon: this.newProject.icon,
        color: this.newProject.color,
        userId: this.userData.userid,
        userName: this.userData.username
      });



      this.showDialog = false;

      this.showToastMessage('项目添加成功');
      this.getProjects();


    },
    filterProjects() {
      // 搜索功能已在计算属性中实现
    },

    async handleLogout() {
      try {
        await axios.post('/api/user/logout');
        this.clearAuthData();
        this.showToastMessage('退出登录成功');
        setTimeout(() => {
          this.$router.replace('/login');
        }, 400);
      } catch (error) {
        console.error('退出失败:', error);
      }
    },
    clearAuthData() {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      delete axios.defaults.headers.common['Authorization'];
    },

    showToastMessage(message) {
      this.toastMessage = message;
      this.showToast = true;
      setTimeout(() => {
        this.showToast = false;
      }, 3000);
    }
  },


};
</script>

<style scoped>

/* 基础样式 */
.home-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* 导航栏样式 */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: #af96e6;
  color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.navbar-brand {
  font-size: 1.5rem;
  font-weight: bold;
}

.navbar-links {
  display: flex;
  align-items: center;
  gap: 20px;
}

.navbar-links a {
  color: #fff;
  text-decoration: none;
  font-size: 1rem;
}

.navbar-links a:hover {
  text-decoration: underline;
}

.avatar img {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  cursor: pointer;
}

.logout-button {
  background-color: #fff;
  color: #9478cc;
  border: none;
  padding: 8px 16px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1rem;
  display: flex;
  align-items: center;
  gap: 8px;
}

.logout-button:hover {
  background-color: #f0f0f0;
}

/* 全屏项目区域 */
.fullscreen-projects {
  margin-top: 85px; /* 与导航栏的间距 */
  padding: 20px;
  height: calc(100vh - 85px);
  overflow-y: auto;
  background-color: #f9f9f9;
}

.projects-container {
  max-width: 1500px;
  margin: 0 auto;
}

.projects-title {
  color: #333;
  margin-bottom: 20px;
  font-size: 1.8rem;
}

.projects-search {
  position: relative;
  margin-bottom: 20px;
  max-width: 400px;
}

.projects-search input {
  width: 100%;
  padding: 10px 15px 10px 40px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1rem;
}

.search-icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #9478cc;
}

.projects-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.projects-tabs button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.projects-tabs button:hover {
  background: #f5f5f5;
}

.projects-tabs button.active {
  background: #9478cc;
  color: white;
  border-color: #9478cc;
}

.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.project-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s, box-shadow 0.2s;
  display: flex;
  flex-direction: column;
  position: relative;
  cursor: pointer;
  border-left: 4px solid #9478cc;
  height: 180px;
}

.project-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.project-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 15px;
}

.project-info h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 1.2rem;
}

.project-info p {
  margin: 0 0 15px 0;
  color: #666;
  font-size: 0.9rem;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.project-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  color: #999;
  margin-top: auto;
}

.project-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.project-actions {
  position: absolute;
  top: 15px;
  right: 15px;
}

.project-actions button {
  background: none;
  border: none;
  color: #ddd;
  cursor: pointer;
  font-size: 1.2rem;
}

.project-actions button:hover {
  color: #f9c74f;
}

.project-actions .favorite {
  color: #f9c74f;
}

.add-project {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  color: #9478cc;
  height: 180px;
}

.add-project:hover {
  border-color: #9478cc;
  background: rgba(148, 120, 204, 0.05);
}

.add-project span {
  margin-top: 10px;
  font-weight: 500;
}

/* 对话框样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  padding: 20px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
}

.dialog h3 {
  margin-top: 0;
  color: #333;
}

.dialog-content {
  margin: 20px 0;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #555;
}

.form-group input,
.form-group textarea {
  width: 98%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group textarea {
  min-height: 80px;
  resize: vertical;
}

.icon-selector {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 10px;
}

.icon-selector button {
  background: #f5f5f5;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #666;
  font-size: 1.1rem;
}

.icon-selector button.selected {
  background: #9478cc;
  color: white;
}

.color-selector {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 10px;
}

.color-selector div {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
  border: 2px solid transparent;
}

.color-selector div.selected {
  border-color: #333;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.dialog-actions button {
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
}

.dialog-actions .cancel {
  background: #f5f5f5;
  border: 1px solid #ddd;
  color: #666;
}

.dialog-actions .confirm {
  background: #9478cc;
  border: 1px solid #9478cc;
  color: white;
}

/* 悬浮提示框 */
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

/* 响应式调整 */
@media (max-width: 768px) {
  .projects-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }

  .projects-tabs {
    overflow-x: auto;
    padding-bottom: 10px;
  }

  .projects-tabs button {
    white-space: nowrap;
  }

  .project-card,
  .add-project {
    height: 160px;
  }
}

@media (max-width: 480px) {
  .projects-grid {
    grid-template-columns: 1fr;
  }

  .navbar-links {
    gap: 10px;
  }

  .logout-button span {
    display: none;
  }
}
</style>