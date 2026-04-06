<template>
  <div class="home-page" :style="themeStyle">
    <!-- 顶部导航栏 -->
    <nav class="navbar">
      <div class="navbar-left">
        <div class="logo-area">
          <div class="logo-icon">
            <font-awesome-icon :icon="['fas', 'database']" />
          </div>
          <div class="brand-text">
            <span class="brand-title">恒瑞主数据</span>
            <span class="brand-subtitle">后台管理系统</span>
          </div>
        </div>
      </div>

      <div class="navbar-center">
        <router-link :to="{ name: 'HengRuiHome' }" class="nav-btn">
          <font-awesome-icon :icon="['fas', 'home']" size="sm" />
          <span>仪表盘</span>
        </router-link>
        <router-link :to="{ name: 'Home' }" class="nav-btn">
          <font-awesome-icon :icon="['fas', 'project-diagram']" size="sm" />
          <span>项目管理</span>
        </router-link>
      </div>

      <div class="navbar-right">
        <div class="user-info">
          <div class="avatar">
            <img :src="avatarUrl" alt="用户头像" />
            <span class="status-dot"></span>
          </div>
          <span class="username">管理员</span>
        </div>
        <div class="divider-v"></div>
        <button class="logout-button" @click="handleLogout">
          <font-awesome-icon :icon="['fas', 'sign-out-alt']" size="sm" />
          <span>退出</span>
        </button>
      </div>
    </nav>

    <!-- 主体区域 -->
    <div class="main-content">
      <!-- 左侧功能侧边栏 -->
      <aside class="sidebar" :class="{ collapsed: isSidebarCollapsed }">
        <!-- 折叠按钮（顶部） -->
        <div class="collapse-bar">
          <span class="menu-label" v-if="!isSidebarCollapsed">功能菜单</span>
          <button class="collapse-button" @click="toggleSidebar" :title="isSidebarCollapsed ? '展开菜单' : '折叠菜单'">
            <font-awesome-icon
                :icon="isSidebarCollapsed ? ['fas', 'angle-double-right'] : ['fas', 'angle-double-left']"
            />
          </button>
        </div>

        <ul class="sidebar-menu">
          <!-- 批次管理 -->
          <li :class="{ 'menu-open': openMenus.batchManagement }">
            <div class="menu-item" @click="toggleMenu('batchManagement')">
              <div class="menu-item-icon">
                <font-awesome-icon :icon="['fas', 'layer-group']" />
              </div>
              <span class="menu-item-text" v-if="!isSidebarCollapsed">批次管理</span>
              <span class="menu-arrow" v-if="!isSidebarCollapsed">
                <font-awesome-icon :icon="openMenus.batchManagement ? ['fas', 'chevron-down'] : ['fas', 'chevron-right']" size="xs" />
              </span>
            </div>
            <ul v-if="openMenus.batchManagement && !isSidebarCollapsed" class="sub-menu">
              <li @click="showBatchData">
                <font-awesome-icon :icon="['fas', 'list']" class="sub-icon" />
                <span>批次列表</span>
              </li>
              <li @click="importBatchData">
                <font-awesome-icon :icon="['fas', 'upload']" class="sub-icon" />
                <span>批次导入</span>
              </li>
            </ul>
          </li>

          <!-- 数据汇总 -->
          <li :class="{ 'menu-open': openMenus.monitoringManagement }">
            <div class="menu-item" @click="toggleMenu('monitoringManagement')">
              <div class="menu-item-icon">
                <font-awesome-icon :icon="['fas', 'table']" />
              </div>
              <span class="menu-item-text" v-if="!isSidebarCollapsed">数据汇总</span>
              <span class="menu-arrow" v-if="!isSidebarCollapsed">
                <font-awesome-icon :icon="openMenus.monitoringManagement ? ['fas', 'chevron-down'] : ['fas', 'chevron-right']" size="xs" />
              </span>
            </div>
            <ul v-if="openMenus.monitoringManagement && !isSidebarCollapsed" class="sub-menu">
              <li @click="showMonitoringData">
                <font-awesome-icon :icon="['fas', 'database']" class="sub-icon" />
                <span>汇总数据</span>
              </li>
            </ul>
          </li>

          <!-- 数据比对关系 -->
          <li :class="{ 'menu-open': openMenus.relationManagement }">
            <div class="menu-item" @click="toggleMenu('relationManagement')">
              <div class="menu-item-icon">
                <font-awesome-icon :icon="['fas', 'link']" />
              </div>
              <span class="menu-item-text" v-if="!isSidebarCollapsed">数据比对关系</span>
              <span class="menu-arrow" v-if="!isSidebarCollapsed">
                <font-awesome-icon :icon="openMenus.relationManagement ? ['fas', 'chevron-down'] : ['fas', 'chevron-right']" size="xs" />
              </span>
            </div>
            <ul v-if="openMenus.relationManagement && !isSidebarCollapsed" class="sub-menu">
              <li @click="showRelationData">
                <font-awesome-icon :icon="['fas', 'exchange-alt']" class="sub-icon" />
                <span>比对关系</span>
              </li>
            </ul>
          </li>
        </ul>
      </aside>

      <!-- 右侧内容区域 -->
      <div class="content" :class="{ expanded: isSidebarCollapsed }">
        <!-- 标签页栏 -->
        <div class="content-header" v-if="tabs.length > 0">
          <div class="tabs-container">
            <div class="tabs">
              <div
                  v-for="tab in tabs"
                  :key="tab.id"
                  :class="{ active: tab.active }"
                  class="tab"
                  @click="switchTab(tab.id)"
              >
                <span class="tab-title">{{ tab.title }}</span>
                <span
                    v-if="tabs.length > 1"
                    class="close-tab"
                    @click.stop="closeTab(tab.id)"
                >
                  <font-awesome-icon :icon="['fas', 'times']" size="xs" />
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 内容视图 -->
        <div class="content-body">
          <transition name="fade" mode="out-in">
            <component :is="currentViewComponent" :key="currentView" />
          </transition>
        </div>
      </div>
    </div>

    <!-- 悬浮提示框 -->
    <transition name="toast-slide">
      <div v-if="showToast" class="toast">
        <font-awesome-icon :icon="['fas', 'check-circle']" class="toast-icon" />
        {{ toastMessage }}
      </div>
    </transition>
  </div>
</template>

<script setup>
import '@/assets/css/dark-mode.css'
import avatarImg from '@/assets/img/avatar-modified.png'
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

const avatarUrl = avatarImg

const router = useRouter()
const route = useRoute()

// 主题颜色配置
const themeColors = ref({
  primary: '#ff9e7d',
  dark: '#ff7d6d',
  light: '#ffb09a'
})

// 动态主题样式
const themeStyle = computed(() => {
  const { primary, dark, light } = themeColors.value
  return {
    '--theme-primary': primary,
    '--theme-dark': dark,
    '--theme-light': light,
    '--theme-rgba': `rgba(${hexToRgb(primary)}, 0.35)`,
    '--theme-rgba-light': `rgba(${hexToRgb(primary)}, 0.08)`,
    '--theme-rgba-shadow': `rgba(${hexToRgb(primary)}, 0.45)`,
    '--theme-gradient': `linear-gradient(135deg, ${primary}, ${dark})`,
    '--theme-gradient-nav': `linear-gradient(90deg, ${dark} 0%, ${primary} 60%, ${light} 100%)`,
    '--theme-text-dark': light,
    '--theme-icon-dark': light,
  }
})

// HEX 转 RGB 辅助函数
function hexToRgb(hex) {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex)
  return result
    ? `${parseInt(result[1], 16)}, ${parseInt(result[2], 16)}, ${parseInt(result[3], 16)}`
    : '255, 158, 125'
}

// 根据路由名称加载主题颜色
function loadThemeColor() {
  const routeName = route.name
  // 从 sessionStorage 获取项目颜色
  const savedColor = sessionStorage.getItem(`themeColor_${routeName}`)

  if (savedColor) {
    const hex = savedColor.replace('#', '')
    // 根据主颜色计算深色和浅色变体
    themeColors.value = {
      primary: `#${hex}`,
      dark: adjustColor(hex, -20),
      light: adjustColor(hex, 20)
    }
  } else if (routeName === 'HengRuiHome') {
    // 默认颜色：珊瑚橙
    themeColors.value = {
      primary: '#ff9e7d',
      dark: '#ff7d6d',
      light: '#ffb09a'
    }
  }
}

// 调整颜色亮度（hex 不带#，percent 为正数变亮，负数变暗）
function adjustColor(hex, percent) {
  const num = parseInt(hex, 16)
  const r = Math.max(0, Math.min(255, (num >> 16) + percent))
  const g = Math.max(0, Math.min(255, ((num >> 8) & 0x00FF) + percent))
  const b = Math.max(0, Math.min(255, (num & 0x0000FF) + percent))
  return `#${(1 << 24 | r << 16 | g << 8 | b).toString(16).slice(1)}`
}

onMounted(() => {
  loadThemeColor()
})

const openMenus = ref({
  batchManagement: false,
  monitoringManagement: false,
  relationManagement: false
})

const showToast = ref(false)
const toastMessage = ref('')
const isSidebarCollapsed = ref(false)

const tabs = ref([])
const currentView = ref('BatchDataView')

// 组件映射对象（使用全局注册的组件名称）
const componentsMap = {
  BatchDataView: 'BatchDataView',
  ImportBatchDataView: 'ImportBatchDataView',
  MonitoringDataView: 'MonitoringDataView',
  OrgRelationView: 'OrgRelationView'
}

const currentViewComponent = computed(() => componentsMap[currentView.value] || 'BatchDataView')

const toggleMenu = (menu) => {
  openMenus.value[menu] = !openMenus.value[menu]
}

const addTab = (title, component) => {
  const existingTab = tabs.value.find((tab) => tab.component === component)
  if (existingTab) {
    switchTab(existingTab.id)
    return
  }

  const tabId = 'tab-' + Date.now()
  tabs.value.forEach((tab) => (tab.active = false))
  tabs.value.push({
    id: tabId,
    title,
    component,
    active: true,
  })
  currentView.value = component
}

const switchTab = (tabId) => {
  tabs.value.forEach((tab) => {
    tab.active = tab.id === tabId
    if (tab.active) {
      currentView.value = tab.component
    }
  })
}

const closeTab = (tabId) => {
  if (tabs.value.length <= 1) {
    currentView.value = 'BatchDataView'
  }
  const index = tabs.value.findIndex((tab) => tab.id === tabId)
  if (tabs.value[index].active) {
    let newActiveTab
    if (index > 0) {
      newActiveTab = tabs.value[index - 1]
    } else if (tabs.value.length > 1) {
      newActiveTab = tabs.value[index + 1]
    }
    if (newActiveTab) {
      newActiveTab.active = true
      currentView.value = newActiveTab.component
    } else {
      currentView.value = 'BatchDataView'
    }
  }
  tabs.value.splice(index, 1)
}

const showBatchData = () => {
  addTab('批次列表', 'BatchDataView')
  window.location.hash = '/hengrui/batch'
}

const importBatchData = () => {
  addTab('批次导入', 'ImportBatchDataView')
  window.location.hash = '/hengrui/importBatch'
}

const showMonitoringData = () => {
  addTab('数据汇总', 'MonitoringDataView')
  window.location.hash = '/hengrui/monitoring'
}

const showRelationData = () => {
  addTab('比对关系', 'OrgRelationView')
  window.location.hash = '/hengrui/relation'
}


const handleLogout = async () => {
  try {
    await axios.post('/api/user/logout')
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    delete axios.defaults.headers.common['Authorization']
    showToastMessage('退出登录成功')
    setTimeout(() => {
      router.replace('/login')
    }, 400)
  } catch (error) {
    console.error('退出失败:', error)
  }
}

const showToastMessage = (message) => {
  toastMessage.value = message
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 3000)
}

const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}
</script>

<style scoped>
/* ============ 全局布局 ============ */
.home-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f0f2f8;
  transition: background-color 0.3s ease;
}

html.dark .home-page {
  background-color: #12121e;
}

/* ============ 导航栏 ============ */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 58px;
  background: var(--theme-gradient-nav);
  color: #fff;
  box-shadow: 0 3px 16px var(--theme-rgba);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  transition: background 0.3s ease, box-shadow 0.3s ease;
}

html.dark .navbar {
  background: linear-gradient(90deg, #1e3a5f 0%, #2d4a7c 60%, #3a5a8e 100%);
  box-shadow: 0 3px 20px rgba(0, 0, 0, 0.5);
}

/* 左区：Logo */
.navbar-left {
  display: flex;
  align-items: center;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255,255,255,0.25);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-title {
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.brand-subtitle {
  font-size: 0.68rem;
  opacity: 0.75;
  letter-spacing: 0.3px;
}

/* 中局：导航按钮 */
.navbar-center {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(255,255,255,0.85);
  text-decoration: none;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 0.85rem;
  transition: all 0.25s ease;
  border: 1px solid rgba(255,255,255,0.2);
  background: rgba(255,255,255,0.08);
}

.nav-btn:hover {
  background: rgba(255,255,255,0.22);
  color: #fff;
  border-color: rgba(255,255,255,0.4);
}

/* 右局：用户区 */
.navbar-right {
  display: flex;
  align-items: center;
  gap: 14px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 9px;
}

.avatar {
  position: relative;
  display: inline-flex;
}

.avatar img {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  border: 2px solid rgba(255,255,255,0.6);
  object-fit: cover;
}

.status-dot {
  position: absolute;
  bottom: 1px;
  right: 1px;
  width: 8px;
  height: 8px;
  background: #4ade80;
  border-radius: 50%;
  border: 1.5px solid #fff;
}

.username {
  font-size: 0.85rem;
  font-weight: 500;
  opacity: 0.9;
}

.divider-v {
  width: 1px;
  height: 20px;
  background: rgba(255,255,255,0.3);
}

.logout-button {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255,255,255,0.12);
  color: #fff;
  border: 1px solid rgba(255,255,255,0.25);
  padding: 6px 14px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.83rem;
  transition: all 0.25s ease;
}

.logout-button:hover {
  background: rgba(255,255,255,0.25);
  border-color: rgba(255,255,255,0.5);
}

html.dark .logout-button {
  background: rgba(255,255,255,0.06);
  border-color: rgba(255,255,255,0.12);
}

html.dark .logout-button:hover {
  background: rgba(255,255,255,0.14);
}

/* ============ 主体布局 ============ */
.main-content {
  display: flex;
  flex: 1;
  margin-top: 58px;
  overflow: hidden;
  height: calc(100vh - 58px);
}

/* ============ 侧边栏 ============ */
aside.sidebar {
  width: 185px;
  flex-shrink: 0;
  background-color: #fff;
  box-shadow: 3px 0 16px var(--theme-rgba-light);
  padding: 16px 10px;
  transition: transform 0.25s cubic-bezier(0.4,0,0.2,1),
              width 0.25s cubic-bezier(0.4,0,0.2,1),
              background-color 0.3s;
  position: fixed;
  top: 58px;
  bottom: 0;
  z-index: 999;
  overflow-y: auto;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
  gap: 4px;
  will-change: width;
  backface-visibility: hidden;
}

html.dark aside.sidebar {
  background-color: #1a1a2c;
  box-shadow: 3px 0 20px rgba(0, 0, 0, 0.35);
}

aside.sidebar.collapsed {
  width: 48px;
  padding: 12px 6px;
  overflow: hidden;
}

/* 折叠栏（顶部容器） */
.collapse-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  flex-shrink: 0;
  padding-left: 8px;
  padding-right: 8px;
}

/* 功能菜单标签 */
.menu-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--theme-primary);
  letter-spacing: 0.5px;
  white-space: nowrap;
  padding-left: 8px;
}

html.dark .menu-label {
  color: var(--theme-text-dark, #ffb09a);
}

aside.sidebar.collapsed .collapse-bar {
  justify-content: center;
}

/* 折叠按钮 */
.collapse-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: var(--theme-gradient);
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.82rem;
  transition: all 0.25s ease;
  box-shadow: 0 2px 8px var(--theme-rgba);
  flex-shrink: 0;
}

.collapse-button:hover {
  transform: scale(1.08);
  box-shadow: 0 4px 12px var(--theme-rgba-shadow);
}

html.dark .collapse-button {
  background: var(--theme-gradient);
}

.sidebar-menu {
  list-style: none;
  padding: 0;
  margin: 0;
  flex: 1;
}

.sidebar-menu > li {
  margin-bottom: 2px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 10px;
  font-size: 0.83rem;
  font-weight: 500;
  cursor: pointer;
  border-radius: 10px;
  transition: all 0.22s ease;
  color: var(--theme-primary);
  position: relative;
}

html.dark .menu-item {
  color: var(--theme-text-dark, #ffb09a);
}

.menu-item:hover {
  background: linear-gradient(135deg, #e8f0ff, #dbe7f8);
  color: var(--theme-primary);
}

html.dark .menu-item:hover {
  background: linear-gradient(135deg, #1f3a5f, #1a2f4a);
  color: var(--theme-text-dark, #ffb09a);
}

.menu-open > .menu-item {
  background: linear-gradient(135deg, #e5efff, #d0e0f8);
  color: var(--theme-primary);
}

html.dark .menu-open > .menu-item {
  background: linear-gradient(135deg, #224060, #1a3550);
  color: var(--theme-text-dark, #ffb09a);
}

.menu-item-icon {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--theme-gradient);
  border-radius: 8px;
  color: #fff;
  font-size: 0.95rem;
  flex-shrink: 0;
  transition: all 0.22s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

html.dark .menu-item-icon {
  background: var(--theme-gradient);
  box-shadow: 0 2px 6px rgba(0,0,0,0.3);
}

.menu-item:hover .menu-item-icon,
.menu-open > .menu-item .menu-item-icon {
  transform: scale(1.08);
  box-shadow: 0 4px 10px var(--theme-rgba);
}

.menu-item-text {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
}

.menu-arrow {
  color: var(--theme-primary);
  font-size: 0.8rem;
  transition: transform 0.22s ease, color 0.22s;
  flex-shrink: 0;
}

.menu-open > .menu-item .menu-arrow {
  color: var(--theme-primary);
}

html.dark .menu-arrow {
  color: var(--theme-text-dark, #ffb09a);
}

/* 子菜单 */
.sub-menu {
  list-style: none;
  padding: 4px 0 4px 14px;
  margin: 0;
  animation: slideDown 0.2s ease;
}

@keyframes slideDown {
  from { opacity: 0; transform: translateY(-4px); }
  to   { opacity: 1; transform: translateY(0); }
}

.sub-menu li {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 12px;
  font-size: 0.78rem;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s ease;
  color: var(--theme-primary);
  margin-bottom: 2px;
  border-left: 2px solid transparent;
}

html.dark .sub-menu li {
  color: var(--theme-text-dark, #ffb09a);
}

.sub-menu li:hover {
  background: linear-gradient(135deg, #e8f0ff, #dbe7f8);
  color: var(--theme-primary);
  border-left-color: var(--theme-primary);
  padding-left: 14px;
}

html.dark .sub-menu li:hover {
  background: linear-gradient(135deg, #1f3a5f, #1a2f4a);
  color: var(--theme-text-dark, #ffb09a);
  border-left-color: var(--theme-primary);
}

.sub-icon {
  color: var(--theme-primary);
  font-size: 0.88rem;
  width: 16px;
  flex-shrink: 0;
}

html.dark .sub-icon {
  color: var(--theme-icon-dark, #ffb09a);
}

/* ============ 内容区 ============ */
.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 185px;
  background-color: #f0f2f8;
  transition: margin-left 0.25s cubic-bezier(0.4,0,0.2,1), background-color 0.3s;
  overflow: hidden;
  height: calc(100vh - 58px);
  will-change: margin-left;
}

html.dark .content {
  background-color: #12121e;
}

.content.expanded {
  margin-left: 48px;
}

/* 标签栏 */
.content-header {
  background-color: #fff;
  border-bottom: 1px solid #d0dbe8;
  padding: 0 23px;
  box-shadow: 0 2px 8px var(--theme-rgba-light);
  transition: background-color 0.3s, box-shadow 0.3s;
  flex-shrink: 0;
}

html.dark .content-header {
  background-color: #1a1a2c;
  border-bottom-color: #2a3550;
  box-shadow: 0 2px 12px rgba(0,0,0,0.3);
}

.tabs-container {
  overflow-x: auto;
  scrollbar-width: none;
}

.tabs-container::-webkit-scrollbar {
  display: none;
}

.tabs {
  display: flex;
  min-width: fit-content;
  padding-top: 8px;
}

.tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 7px 16px;
  cursor: pointer;
  border: 1px solid #c8d6e8;
  border-bottom: none;
  border-radius: 8px 8px 0 0;
  margin-right: 4px;
  background-color: #e8eff8;
  white-space: nowrap;
  font-size: 0.8rem;
  color: #3a5a7f;
  transition: all 0.22s ease;
  position: relative;
}

html.dark .tab {
  background-color: #1e2a40;
  border-color: #2a3550;
  color: #6a80a0;
}

.tab:hover {
  background-color: #dbe7f5;
  color: var(--theme-primary);
}

html.dark .tab:hover {
  background-color: #253550;
  color: #7aa8cc;
}

.tab.active {
  background-color: #fff;
  border-color: var(--theme-primary);
  border-bottom: 2px solid #fff;
  margin-bottom: -1px;
  color: var(--theme-primary);
  font-weight: 600;
  box-shadow: 0 -2px 8px var(--theme-rgba-light);
}

html.dark .tab.active {
  background-color: #1a1a2c;
  border-color: var(--theme-primary);
  border-bottom: 2px solid #1a1a2c;
  color: #60a5fa;
}

.tab-title {
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.close-tab {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  color: #90a8c8;
  transition: all 0.2s ease;
  font-size: 0.65rem;
}

.close-tab:hover {
  background: #d8e0f0;
  color: var(--theme-primary);
}

html.dark .close-tab {
  color: #4a5a70;
}

html.dark .close-tab:hover {
  background: #2a3550;
  color: #7aa8cc;
}

/* 内容主体 */
.content-body {
  flex: 1;
  overflow: auto;
  padding: 16px;
}

/* ============ 过渡动画 ============ */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.22s ease, transform 0.22s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* ============ Toast 提示 ============ */
.toast {
  position: fixed;
  top: 72px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--theme-gradient);
  color: #fff;
  padding: 10px 20px;
  border-radius: 24px;
  box-shadow: 0 4px 20px var(--theme-rgba-shadow);
  z-index: 9999;
  font-size: 0.875rem;
  font-weight: 500;
}

html.dark .toast {
  background: linear-gradient(135deg, #3a5a8e, #2d4a7c);
  box-shadow: 0 4px 24px rgba(0,0,0,0.5);
}

.toast-icon {
  font-size: 1rem;
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
