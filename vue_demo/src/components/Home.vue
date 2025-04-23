<template xmlns="">
<!--  <link-->
<!--      rel="stylesheet"-->
<!--      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css"-->
<!--  />-->
  <div class="home-page" :style="homePageStyle">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="navbar-brand">ADOS 后台管理</div>
      <div class="navbar-links">
        <router-link to="/home">首页</router-link>
        <router-link to="/settings">设置</router-link>
        <!-- 头像 -->
        <div class="avatar">
          <img src="@/assets/avatar-modified.png" alt="用户头像" />
        </div>
        <!-- 退出登录按钮 -->

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
<!--            <i :class="isSidebarCollapsed ? 'fas fa-angle-double-right' : 'fas fa-angle-double-left'"></i>-->
            <font-awesome-icon :icon="isSidebarCollapsed ? ['fas', 'angle-double-right'] : ['fas', 'angle-double-left']"/>
          </button>
        </div>
        <ul class="sidebar-menu">
          <!-- 用户管理 -->
          <li>
            <div class="menu-item" @click="toggleMenu('user')">
              <span>
<!--                <i class="fas fa-users"></i>-->
                <font-awesome-icon :icon="['fas', 'fa-th-list'] " size="1x"></font-awesome-icon>

                <span v-if="!isSidebarCollapsed">主数据查看</span>
              </span>
              <span v-if="!isSidebarCollapsed" class="arrow">
                {{ openMenus.user ? '▼' : '▶' }}
              </span>
            </div>
            <ul v-if="openMenus.user && !isSidebarCollapsed" class="sub-menu">
              <li @click="navigateTo('/users/list')">
                <font-awesome-icon :icon="['fas', 'hospital-alt'] " size="1x"></font-awesome-icon> &nbsp;
                <span>医院主数据</span>
              </li>

              <li @click="navigateTo('/users/roles')">
                <i class="fas fa-user-shield"></i>
                <font-awesome-icon :icon="['fas', 'store-alt'] " size="1x"></font-awesome-icon> &nbsp;
                <span>药店主数据</span>
              </li>

              <li @click="navigateTo('/users/role')">
                <font-awesome-icon :icon="['fas', 'synagogue'] " size="1x"></font-awesome-icon> &nbsp;
                <i class="fas fa-user-shield"></i>
                <span>商业主数据</span>
              </li>

            </ul>
          </li>



        </ul>
      </div>

      <!-- 右侧内容区域 -->
      <div class="content" :class="{ 'expanded': isSidebarCollapsed }">
        <!-- 网格布局 -->
        <div class="grid-container">

          <!-- 替换为五个大小一致的盒子 -->
          <div class="grid-item-container">
            <div class="grid-item-small">

<!--                <i class="fas fa-hospital fa-2x"></i> &nbsp;&nbsp;-->
              <font-awesome-icon :icon="['fas', 'hospital'] " size="2x"></font-awesome-icon> &nbsp;&nbsp;
              <div style="display: flex; flex-direction: column; align-items: center;  ">
                <span style="font-weight: bold;font-size: 24px;color:#000000 ">医院周清洗</span>
                <span style="font-size: 22px; color:#2575fc;font-weight: bold ">{{ cleanCount.hospitalCount }} </span>
              </div>

            </div>

            <div class="grid-item-small" >
              <font-awesome-icon :icon="['fas', 'store'] " size="2x"></font-awesome-icon> &nbsp;&nbsp;

              <div style="display: flex; flex-direction: column; align-items: center;  ">
                <span style="font-weight: bold;font-size: 24px;color:#000000 ">药店周清洗</span>
                <span style="font-size: 22px; color:#2575fc;font-weight: bold ">{{ cleanCount.drugstoreCount }} </span>
              </div>

            </div>

            <div class="grid-item-small">
              <font-awesome-icon :icon="['fas', 'university'] " size="2x"></font-awesome-icon> &nbsp;&nbsp;
              <div style="display: flex; flex-direction: column; align-items: center;  ">
                <span style="font-weight: bold;font-size: 24px;color:#000000 ">商业周清洗</span>
                <span style="font-size: 22px; color:#2575fc;font-weight: bold ">{{  cleanCount.companyCount }} </span>
              </div>
            </div>

            <div class="grid-item-small">
              <font-awesome-icon :icon="['fas', 'trash'] " size="2x"></font-awesome-icon> &nbsp;&nbsp;
              <div style="display: flex; flex-direction: column; align-items: center;  ">
                <span style="font-weight: bold;font-size: 24px;color:#000000 ">申诉待处理</span>
                <span style="font-size: 22px; color:#2575fc;font-weight: bold ">{{  cleanCount.unappealingCount }} </span>
              </div>
            </div>

            <div class="grid-item-small">
              <font-awesome-icon :icon="['fas', 'clock'] " size="2x"></font-awesome-icon> &nbsp;&nbsp;
              <div style="display: flex; flex-direction: column; align-items: center;  ">
                <span style="font-weight: bold;font-size: 24px;color:#000000 ">待清洗</span>
                <span style="font-size: 22px; color:#2575fc;font-weight: bold ">{{  cleanCount.uncleanedCount }} </span>
              </div>


            </div>
          </div>


          <!-- 第二行：合并 4、5、6 格，并分为两个格子 -->
          <div class="grid-item grid-item-4-6">
            <div class="sub-grid-item">
              <!-- 引入并使用 BarChart 柱状图组件 -->
              <BarChart />
            </div>
            <div class="sub-grid-item">
              <!-- 引入并使用 BarChart 饼图组件 -->
              <PieChart />
            </div>
          </div>
          <!-- 第三行：合并 7、8、9 格，并分为两个格子 -->
          <div class="grid-item grid-item-7-9">
            <div class="sub-grid-item">
              <!-- 引入并使用 LineChart 折线图组件 -->
              <RosePieChart />
            </div>

            <div class="sub-grid-item">
              <LineChart />
            </div>

          </div>
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
import BarChart from '@/components/homechart/CleanBarChart.vue';
import PieChart from '@/components/homechart/MainDataPieChart.vue';
import LineChart from '@/components/homechart/AppealUpdateChart.vue';
import RosePieChart from '@/components/homechart/BranchBarChart.vue';
import axios from 'axios'; // 引入axios用于可能的后端退出请求

export default {
  // 显示柱状图组件: BarChart,
  // 显示饼图组件: PieChart,
  components: { BarChart,PieChart,LineChart,RosePieChart },

  data() {
    return {
      openMenus: {
        user: false,
        data: false,
        dataManagement: false,
        settings: false,
      },
      showToast: false,
      toastMessage: '',
      isSidebarCollapsed: false,
      homePageStyle: {
        background: 'linear-gradient(135deg, #e3d2ff, #e3d2ff)',
      },
      cleanCount:{
        uncleanedCount: 0,
        unappealingCount: 0,
        companyCount: 0,
        hospitalCount: 0,
        drugstoreCount: 0
      }

    };
  },

  async created() {
    try {
      const response = await axios.post('/api/homeData/getHomeData');
      if (response.data.code === 200) {
        this.cleanCount = response.data.data;
      } else {
        console.error('获取数据失败:', response.data.msg);
      }

    } catch (error) {
      console.error('获取数据失败:', error);
    }
  },


  methods: {
    toggleMenu(menu) {
      this.openMenus[menu] = !this.openMenus[menu];
    },

    navigateTo(path) {
      this.$router.push(path);
    },

    async handleLogout() {


      try {

        // 可选：调用后端退出接口
        await axios.post('/api/user/logout', null, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        });
      } catch (error) {
        console.error('退出请求失败:', error);
        // 即使API调用失败也继续前端清理
      } finally {
        // 清理前端认证数据
        this.clearAuthData();

        // 显示退出成功提示
        this.showToastMessage('退出登录成功');

        // 安全跳转到登录页
        setTimeout(() => {
          this.$router.replace({
            path: '/login',
            query: {
              logout: 'true',
              timestamp: new Date().getTime() // 防止缓存
            }
          }).catch(() => {});
        }, 400);
      }
    },

    clearAuthData() {
      // 清除所有存储的认证信息
      localStorage.removeItem('token');
      localStorage.removeItem('user');

      // 如果有Vuex状态也需要重置
      if (this.$store && this.$store.commit) {
        this.$store.commit('resetAuthState');
      }

      // 清除axios默认授权头
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
      this.homePageStyle.background = this.isSidebarCollapsed
          ? ''
          : 'linear-gradient(135deg, #e3d2ff, #e3d2ff)';
    },

  },

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

/* 网格布局 */
.grid-container {
  display: grid;
  grid-template-rows: 150px 1fr 1fr; /* 第一行高度缩小，第二行和第三行平分剩余空间 */
  gap: 10px; /* 网格之间的间距 */
  height: 100%;
}

/* 新增样式：五个盒子的容器 */
.grid-item-container {
  grid-column: 1 / -1; /* 占据整行 */
  display: flex; /* 使用 Flexbox 布局 */
  gap: 10px; /* 盒子之间的间距 */
  height: 150px; /* 高度与原来的 grid-item-1-3 一致 */
}

/* 新增样式：每个小盒子的样式 */
.grid-item-small {
  flex: 1; /* 每个盒子平分宽度 */
  background-color: #fff; /* 背景色为白色 */
  border: 1px solid #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: #333;
  border-radius: 5px;
  transition: box-shadow 0.2s;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); /* 悬浮效果 */
}

.grid-item-small:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2); /* 悬浮时阴影加深 */
}

/* 第二行和第三行：合并 4、5、6 格和 7、8、9 格，并分为两个格子 */
.grid-item-4-6,
.grid-item-7-9 {
  grid-column: 1 / -1; /* 占据整行 */
  display: grid;
  grid-template-columns: 1fr 1fr; /* 分为两个格子 */
  gap: 10px; /* 子网格之间的间距 */
}

/* 子网格项样式 */
.sub-grid-item {
  background-color: #fff; /* 背景色为白色 */
  border: 1px solid #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: #333;
  border-radius: 5px;
  transition: box-shadow 0.2s;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); /* 悬浮效果 */
}

.sub-grid-item:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2); /* 悬浮时阴影加深 */
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
</style>