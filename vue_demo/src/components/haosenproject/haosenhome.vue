<template>
  <div class="home-page" :style="homePageStyle">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="navbar-brand">豪森主数据后台管理</div>
      <div class="navbar-links">
        <router-link to="/project/HaosenHome" @click="resetToDashboard">仪表盘</router-link>
        <router-link to="/home">返回项目管理</router-link>
        <div class="avatar">
          <img src="@/assets/images/avatar-modified.png" alt="用户头像" />
        </div>
        <button class="logout-button" @click="handleLogout">
          <font-awesome-icon :icon="['fas', 'sign-out-alt']" size="sm" />
          退出登录
        </button>
      </div>
    </nav>
    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧功能栏 -->
      <div class="sidebar" :class="{ collapsed: isSidebarCollapsed }">
        <div class="sidebar-header">
          <span v-if="!isSidebarCollapsed">功能菜单</span>
          <button class="collapse-button" @click="toggleSidebar">
            <font-awesome-icon
                :icon="isSidebarCollapsed ? ['fas', 'angle-double-right'] : ['fas', 'angle-double-left']"
                size="sm"
            />
          </button>
        </div>
        <ul class="sidebar-menu">
          <!-- 主数据查看菜单 -->
          <li>
            <div class="menu-item" @click="toggleMenu('mainDataManagement')">
              <span>
                <font-awesome-icon :icon="['fas', 'th-list']" size="sm" />
                <span v-if="!isSidebarCollapsed">主数据</span>
              </span>
              <span v-if="!isSidebarCollapsed" class="arrow">
                {{ openMenus.mainDataManagement ? '▼' : '▶' }}
              </span>
            </div>
            <ul v-if="openMenus.mainDataManagement && !isSidebarCollapsed" class="sub-menu">
              <li @click="showHospitalData">
                <font-awesome-icon :icon="['fas', 'hospital-alt']" size="sm" /> &nbsp;
                <span>医院主数据</span>
              </li>
              <li @click="showDrugStoreData">
                <font-awesome-icon :icon="['fas', 'store-alt']" size="sm" /> &nbsp;
                <span>药店主数据</span>
              </li>
              <li @click="companyDataView">
                <font-awesome-icon :icon="['fas', 'synagogue']" size="sm" /> &nbsp;
                <span>商业主数据</span>
              </li>
            </ul>
          </li>
          <!-- 数据清洗 -->
          <li>
            <div class="menu-item" @click="toggleMenu('cleanManagement')">
              <span>
                <font-awesome-icon :icon="['fas', 'oil-can']" size="sm" />
                <span v-if="!isSidebarCollapsed">数据清洗</span>
              </span>
              <span v-if="!isSidebarCollapsed" class="arrow">
                {{ openMenus.cleanManagement ? '▼' : '▶' }}
              </span>
            </div>
            <ul v-if="openMenus.cleanManagement && !isSidebarCollapsed" class="sub-menu">
              <li @click="importCleanData">
                <font-awesome-icon :icon="['fas', 'eraser']" size="sm" /> &nbsp;
                <span>清洗提交</span>
              </li>
            </ul>
          </li>
          <!-- 数据申诉 -->
          <li>
            <div class="menu-item" @click="toggleMenu('appealManagement')">
              <span>
                <font-awesome-icon :icon="['fas', 'flag']" size="sm" />
                <span v-if="!isSidebarCollapsed">数据申诉</span>
              </span>
              <span v-if="!isSidebarCollapsed" class="arrow">
                {{ openMenus.appealManagement ? '▼' : '▶' }}
              </span>
            </div>
            <ul v-if="openMenus.appealManagement && !isSidebarCollapsed" class="sub-menu">
              <li @click="showAppealData">
                <font-awesome-icon :icon="['fas', 'book-reader']" size="sm" /> &nbsp;
                <span>申诉查看</span>
              </li>
              <li @click="importAppealData">
                <font-awesome-icon :icon="['fas', 'cat']" size="sm" /> &nbsp;
                <span>申诉提交</span>
              </li>
            </ul>
          </li>
          <!-- 数据更新 -->
          <li>
            <div class="menu-item" @click="toggleMenu('updateManagement')">
              <span>
                <font-awesome-icon :icon="['fas', 'file-alt']" size="sm" />
                <span v-if="!isSidebarCollapsed">数据更新</span>
              </span>
              <span v-if="!isSidebarCollapsed" class="arrow">
                {{ openMenus.updateManagement ? '▼' : '▶' }}
              </span>
            </div>
            <ul v-if="openMenus.updateManagement && !isSidebarCollapsed" class="sub-menu">
              <li @click="importUpdateData">
                <font-awesome-icon :icon="['fas', 'snowman']" size="sm" /> &nbsp;
                <span>更新提交</span>
              </li>
            </ul>
          </li>
        </ul>
      </div>
      <!-- 右侧内容区域 -->
      <div class="content" :class="{ expanded: isSidebarCollapsed }">
        <!-- 标签页头部 -->
        <div class="content-header">
          <div class="tabs-container">
            <div class="tabs">
              <div
                  v-for="tab in tabs"
                  :key="tab.id"
                  :class="{ active: tab.active }"
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
          <component :is="currentViewComponent" :key="currentView" />
        </transition>
      </div>
    </div>
    <!-- 悬浮提示框 -->
    <div v-if="showToast" class="toast">
      {{ toastMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();

// 响应式状态
const openMenus = ref({
  mainDataManagement: false,
  updateManagement: false,
  cleanManagement: false,
  appealManagement: false,
});

const showToast = ref(false);
const toastMessage = ref('');
const isSidebarCollapsed = ref(false);
const homePageStyle = ref({
  background: 'linear-gradient(135deg, #e3d2ff, #e3d2ff)',
});

const tabs = ref([]);
const currentView = ref('HomeDashboardView');  // 默认渲染仪表盘内容，但不添加标签

// 计算属性
const currentViewComponent = computed(() => currentView.value);

// 方法
const toggleMenu = (menu) => {
  openMenus.value[menu] = !openMenus.value[menu];
};

const addTab = (title, component) => {
  const existingTab = tabs.value.find((tab) => tab.component === component);
  if (existingTab) {
    switchTab(existingTab.id);
    return;
  }

  const tabId = 'tab-' + Date.now();
  tabs.value.forEach((tab) => (tab.active = false));
  tabs.value.push({
    id: tabId,
    title,
    component,
    active: true,
  });
  currentView.value = component;
};

const switchTab = (tabId) => {
  tabs.value.forEach((tab) => {
    tab.active = tab.id === tabId;
    if (tab.active) {
      currentView.value = tab.component;
    }
  });
};

const closeTab = (tabId) => {
  if (tabs.value.length <= 1) {
    // 关闭最后一个标签时，切换回仪表盘（不显示标签）
    currentView.value = 'HomeDashboardView';
  }
  const index = tabs.value.findIndex((tab) => tab.id === tabId);
  if (tabs.value[index].active) {
    let newActiveTab;
    if (index > 0) {
      newActiveTab = tabs.value[index - 1];
    } else if (tabs.value.length > 1) {
      newActiveTab = tabs.value[index + 1];
    } else {
      newActiveTab = null;  // 最后一个，切换到仪表盘
    }
    if (newActiveTab) {
      newActiveTab.active = true;
      currentView.value = newActiveTab.component;
    } else {
      currentView.value = 'HomeDashboardView';
    }
  }
  tabs.value.splice(index, 1);
};

const showHospitalData = () => {
  addTab('医院主数据', 'HospitalDataView');
  window.location.hash = '/dataBase/hospital';
};

const showDrugStoreData = () => {
  addTab('药店主数据', 'DrugStoreDataView');
  window.location.hash = '/dataBase/drugstore';
};

const companyDataView = () => {
  addTab('商业主数据', 'CompanyDataView');
  window.location.hash = '/dataBase/company';
};

const showAppealData = () => {
  addTab('申诉数据', 'AppealDataView');
  window.location.hash = '/appealData/showAppealData';
};

const importAppealData = () => {
  addTab('申诉提交', 'ImportAppealDataView');
  window.location.hash = '/appealData/importCleanData';
};

const importCleanData = () => {
  addTab('清洗提交', 'ImportCleanDataView');
  window.location.hash = '/cleanData/ImportCleanDataView';
};

const importUpdateData = () => {
  addTab('更新提交', 'ImportUpdateDataView');
  window.location.hash = '/updateData/ImportCleanDataView';
};

const resetToDashboard = () => {
  // 点击“仪表盘”链接时，关闭所有标签，显示仪表盘内容
  tabs.value = [];
  currentView.value = 'HomeDashboardView';
  window.location.hash = '';
};

const handleLogout = async () => {
  try {
    await axios.post('/api/user/logout');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    delete axios.defaults.headers.common['Authorization'];
    showToastMessage('退出登录成功');
    setTimeout(() => {
      router.replace('/login');
    }, 400);
  } catch (error) {
    console.error('退出失败:', error);
  }
};

const showToastMessage = (message) => {
  toastMessage.value = message;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 3000);
};

const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value;
};
</script>

<style scoped>
/* 样式完全保持不变 */
.home-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background-color: #af96e6;
  color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}
.navbar-brand {
  font-size: 1.3rem;
  font-weight: bold;
}
.navbar-links {
  display: flex;
  align-items: center;
  gap: 15px;
}
.navbar-links a {
  color: #fff;
  text-decoration: none;
  font-size: 0.9rem;
}
.navbar-links a:hover {
  text-decoration: underline;
}
.avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
}
.logout-button {
  background-color: #fff;
  color: #9478cc;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 6px;
}
.logout-button:hover {
  background-color: #f0f0f0;
}
.main-content {
  display: flex;
  flex: 1;
  background-color: #f9f9f9;
  margin-top: 60px;
}
.sidebar {
  width: 200px;
  background-color: #fff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  padding: 15px;
  transition: width 0.3s ease;
  position: fixed;
  top: 75px;
  bottom: 0;
  z-index: 999;
}
.sidebar.collapsed {
  width: 50px;
  background-color: #fff;
}
.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1rem;
  font-weight: bold;
  margin-bottom: 15px;
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
  padding: 8px;
  font-size: 0.9rem;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.2s;
}
.menu-item:hover {
  background-color: #f0f0f0;
}
.menu-item span {
  display: flex;
  align-items: center;
  gap: 6px;
}
.arrow {
  margin-left: 6px;
  font-size: 0.7rem;
  color: #9478cc;
}
.sub-menu {
  list-style: none;
  padding-left: 15px;
  margin-top: 5px;
  font-size: 0.83rem;
}
.sub-menu li {
  padding: 6px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.2s;
}
.sub-menu li:hover {
  background-color: #f0f0f0;
}
.content {
  flex: 1;
  padding: 5px;
  background-color: #fff;
  transition: margin-left 0.3s ease;
  margin-left: 230px;
}
.content.expanded {
  margin-left: 80px;
}
.toast {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  background-color: #9478cc;
  color: #fff;
  padding: 8px 16px;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  animation: fadeInOut 3s ease-in-out;
}
@keyframes fadeInOut {
  0% { opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { opacity: 0; }
}
.collapse-button {
  background: none;
  border: none;
  color: #9478cc;
  cursor: pointer;
  font-size: 1rem;
}
.collapse-button:hover {
  color: #af96e6;
}
.content-header {
  background-color: white;
  padding: 0;
  margin-bottom: 5px;
  border-radius: 4px;
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
  padding: 8px 16px;
  cursor: pointer;
  border: 1px solid #ddd;
  border-bottom: none;
  border-radius: 4px 4px 0 0;
  margin-right: 5px;
  background-color: #f8f9fa;
  display: flex;
  align-items: center;
  position: relative;
  white-space: nowrap;
  font-size: 0.75rem;
}
.tab.active {
  background-color: #fff;
  border-bottom: 1px solid #fff;
  margin-bottom: -1px;
  color: #9478cc;
  font-weight: bold;
}
.close-tab {
  margin-left: 6px;
  font-size: 1rem;
  color: #999;
  line-height: 1;
}
.close-tab:hover {
  color: #666;
}
</style>