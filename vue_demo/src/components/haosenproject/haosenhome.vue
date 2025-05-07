
<template>
  <div class="home-page" :style="homePageStyle">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="navbar-brand">主数据后台管理</div>
      <div class="navbar-links">
        <router-link to="/home" @click="resetToDashboard">首页</router-link>

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

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧功能栏 -->
      <div class="sidebar" :class="{ 'collapsed': isSidebarCollapsed }">
        <div class="sidebar-header">
          <span v-if="!isSidebarCollapsed">功能菜单</span>
          <button class="collapse-button" @click="toggleSidebar">
            <font-awesome-icon :icon="isSidebarCollapsed ? ['fas', 'angle-double-right'] : ['fas', 'angle-double-left']"/>
          </button>
        </div>
        <ul class="sidebar-menu">
          <!-- 主数据查看菜单 -->
          <li>
            <div class="menu-item" @click="toggleMenu('user')">
              <span>
                <font-awesome-icon :icon="['fas', 'th-list']" size="1x"/>
                <span v-if="!isSidebarCollapsed">主数据</span>
              </span>
              <span v-if="!isSidebarCollapsed" class="arrow">
                {{ openMenus.user ? '▼' : '▶' }}
              </span>
            </div>
            <ul v-if="openMenus.user && !isSidebarCollapsed" class="sub-menu">

              <li @click="showHospitalData">
                <font-awesome-icon :icon="['fas', 'hospital-alt']" size="1x"/> &nbsp;
                <span>医院主数据</span>
              </li>
              <li @click="showDrugStoreData">
                <font-awesome-icon :icon="['fas', 'store-alt']" size="1x"/> &nbsp;
                <span>药店主数据</span>
              </li>
              <li @click="CompanyDataView">
                <font-awesome-icon :icon="['fas', 'synagogue']" size="1x"/> &nbsp;
                <span>商业主数据</span>
              </li>
            </ul>
          </li>

          <!-- 数据申诉 -->
          <li>
            <div class="menu-item" @click="toggleMenu('dataManagement')">
              <span>
                <font-awesome-icon :icon="['fas', 'file-alt']" size="1x"/>
                <span v-if="!isSidebarCollapsed">数据申诉</span>
              </span>
              <span v-if="!isSidebarCollapsed" class="arrow">
                {{ openMenus.dataManagement ? '▼' : '▶' }}
              </span>
            </div>
            <ul v-if="openMenus.dataManagement && !isSidebarCollapsed" class="sub-menu">

              <li @click="showAppealData">
                <font-awesome-icon :icon="['fas', 'book-reader']" size="1x"/> &nbsp;
                <span>数据查看</span>
              </li>

              <li @click="navigateTo('/data-management/export')">
                <font-awesome-icon :icon="['fas', 'cat']" size="1x"/> &nbsp;
                <span>数据导入</span>
              </li>
            </ul>
          </li>

        </ul>
      </div>

      <!-- 右侧内容区域 -->
      <div class="content" :class="{ 'expanded': isSidebarCollapsed }">
        <!-- 简化后的标签页头部 -->
        <div class="content-header">
          <div class="tabs-container">
            <div class="tabs">
              <div
                  v-for="tab in tabs"
                  :key="tab.id"
                  :class="{ 'active': tab.active }"
                  class="tab"
                  @click="switchTab(tab.id)"
              >
                {{ tab.title }}
                <span
                    v-if="tabs.length > 1"
                    class="close-tab"
                    @click.stop="closeTab(tab.id)"
                >
              ×
            </span>
              </div>
            </div>
          </div>
        </div>

        <transition name="fade" mode="out-in">
          <component :is="currentViewComponent"></component>
        </transition>

      </div>
    </div>


    <!-- 悬浮提示框 -->
    <div v-if="showToast" class="toast">
      {{ toastMessage }}
    </div>
  </div>
</template>

<script>
import HospitalDataView from '@/components/haosenproject/maindataview/HospitalDataView.vue';
import HomeDashboardView from '@/components/haosenproject/homechart/HomeDashboardView.vue';
import DrugStoreDataView from '@/components/haosenproject/maindataview/DrugStoreDataView.vue';
import CompanyDataView from '@/components/haosenproject/maindataview/CompanyDataView.vue';
import AppealDataView from '@/components/haosenproject/appealdataview/AppealDataView.vue';
import axios from 'axios';

export default {
  components: {
    HospitalDataView,
    HomeDashboardView,
    DrugStoreDataView,
    CompanyDataView,
    AppealDataView
  },

  data() {
    return {
      openMenus: {
        user: false,
        dataManagement: false,
      },
      showToast: false,
      toastMessage: '',
      isSidebarCollapsed: false,
      homePageStyle: {
        background: 'linear-gradient(135deg, #e3d2ff, #e3d2ff)',
      },
      currentView: 'HomeDashboardView' // 默认显示HomeDashboard
      ,
      tabs: [
        // { id: 'dashboard', title: '仪表盘', component: 'HomeDashboardView', active: true }
      ],
      activeTab: 'dashboard'
    };
  },

  computed: {
    currentViewComponent() {
      return this.currentView; // 直接返回组件名
    }
  },

  methods: {

    toggleMenu(menu) {
      this.openMenus[menu] = !this.openMenus[menu];
    },


    // -----------------------------加载医院主数据视图组件

    addTab(title, component) {
      // 检查是否已存在相同标签
      const existingTab = this.tabs.find(tab => tab.component === component);
      if (existingTab) {
        this.switchTab(existingTab.id);
        return;
      }

      const tabId = 'tab-' + Date.now();
      this.tabs.forEach(tab => tab.active = false);
      this.tabs.push({
        id: tabId,
        title,
        component,
        active: true
      });
      this.activeTab = tabId;
      this.currentView = component;
    },
    switchTab(tabId) {
      this.tabs.forEach(tab => {
        tab.active = tab.id === tabId;
        if (tab.active) {
          this.currentView = tab.component;
          this.activeTab = tabId;
        }
      });
    },

    closeTab(tabId) {
      if (this.tabs.length <= 1) return;

      const index = this.tabs.findIndex(tab => tab.id === tabId);
      if (this.tabs[index].active) {
        const newActiveTab = index > 0 ? this.tabs[index - 1] : this.tabs[index + 1];
        newActiveTab.active = true;
        this.currentView = newActiveTab.component;
        this.activeTab = newActiveTab.id;
      }
      this.tabs.splice(index, 1);
    },

    showHospitalData() {
      this.addTab('医院主数据', 'HospitalDataView');
      window.location.hash = '/dataBase/hospital';
    },

    showDrugStoreData() {
      this.addTab('药店主数据', 'DrugStoreDataView');
      window.location.hash = '/dataBase/drugstore';
    },
    CompanyDataView() {
      this.addTab('商业主数据', 'CompanyDataView');
      window.location.hash = '/dataBase/company';
    },

    showAppealData() {
      this.addTab('申诉数据', 'AppealDataView');
      window.location.hash = '/appealData/showAppealData';
    },


    resetToDashboard() {
      const dashboardTab = this.tabs.find(tab => tab.component === 'HomeDashboardView');
      if (dashboardTab) {
        this.switchTab(dashboardTab.id);
      } else {
        this.addTab('仪表盘', 'HomeDashboardView');
      }
      window.location.hash = '';
    },



    // -----------用户退出登录

    navigateTo(path) {
      this.$router.push(path);
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
    },

    toggleSidebar() {
      this.isSidebarCollapsed = !this.isSidebarCollapsed;
    }
  }
};
</script>

<style scoped>

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
  position: fixed; /* 固定定位 */
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000; /* 确保导航栏位于最上层 */
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

/* 头像样式 */
.avatar img {
  width: 45px;
  height: 45px;
  border-radius: 50%; /* 圆形头像 */
  cursor: pointer;
}

/* 退出登录按钮样式 */
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
  gap: 8px; /* 按钮文字和图标之间的间距 */
}

.logout-button:hover {
  background-color: #f0f0f0;
}

.logout-button i {
  font-size: 0.9rem; /* 关闭图标大小 */
}

/* 主要内容区域 */
.main-content {
  display: flex;
  flex: 1;
  background-color: #fff; /* 右侧内容区域背景色为白色 */
  margin-top: 80px; /* 增加与导航栏的间距 */
}

/* 左侧功能栏样式 */
.sidebar {
  width: 250px;
  background-color: #fff;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
  transition: width 0.3s ease;
  position: fixed; /* 固定定位 */
  top: 90px; /* 增加与导航栏的间距 */
  bottom: 0;
  z-index: 999; /* 确保位于内容区域之上 */
}

.sidebar.collapsed {
  width: 60px;
  background-color: #fff; /* 折叠状态下背景色为白色 */
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.sidebar-menu {
  list-style: none;
  padding: 0;
}

.sidebar-menu li {
  margin-bottom: 10px;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  cursor: pointer;
  border-radius: 5px;
  transition: background-color 0.2s;
}

.menu-item:hover {
  background-color: #f0f0f0;
}

.menu-item span {
  display: flex;
  align-items: center;
  gap: 8px;
}

.arrow {
  margin-left: 8px;
  font-size: 0.8rem;
  color: #9478cc;
}

.sub-menu {
  list-style: none;
  padding-left: 20px;
  margin-top: 5px;
}

.sub-menu li {
  padding: 8px;
  cursor: pointer;
  border-radius: 5px;
  transition: background-color 0.2s;
}

.sub-menu li:hover {
  background-color: #f0f0f0;
}

/* 右侧内容区域样式 */
.content {
  flex: 1;
  padding: 20px;
  background-color: #fff; /* 右侧内容区域背景色为白色 */
  transition: margin-left 0.3s ease;
  margin-left: 280px; /* 初始左侧边距（250px + 10px） */
}

.content.expanded {
  margin-left: 90px; /* 折叠状态下的左侧边距（60px + 10px） */
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

/* 折叠按钮样式 */
.collapse-button {
  background: none;
  border: none;
  color: #9478cc;
  cursor: pointer;
  font-size: 1.2rem;
}

.collapse-button:hover {
  color: #af96e6;
}

/* 内容标题栏样式 */
.content-header {
  background-color: white;
  padding: 0;
  margin-bottom: 5px;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);

}

.tabs-container {
  overflow-x: auto;
  padding: 0 10px;
}

.tabs {
  display: flex;
  min-width: fit-content;
}

.tab {
  padding: 10px 20px;
  cursor: pointer;
  border: 1px solid #ddd;
  border-bottom: none;
  border-radius: 5px 5px 0 0;
  margin-right: 5px;
  background-color: #f8f9fa;
  display: flex;
  align-items: center;
  position: relative;
  white-space: nowrap;
}

.tab.active {
  background-color: #fff;
  border-bottom: 1px solid #fff;
  margin-bottom: -1px;
  color: #9478cc;
  font-weight: bold;
}

.close-tab {
  margin-left: 8px;
  font-size: 1.2rem;
  color: #999;
  line-height: 1;
}

.close-tab:hover {
  color: #666;
}
</style>


