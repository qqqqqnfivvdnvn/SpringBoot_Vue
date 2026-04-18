<template>
  <div class="home-page" :class="homePageClass" :style="themeStyle">
    <!-- 顶部导航栏 -->
    <nav class="navbar">
      <div class="navbar-left">
        <div class="logo-area">
          <div class="logo-icon">
            <font-awesome-icon :icon="['fas', 'database']" />
          </div>
          <div class="brand-text">
            <span class="brand-title">{{ config.brandTitle }}</span>
            <span class="brand-subtitle">后台管理系统</span>
          </div>
        </div>
      </div>

      <div class="navbar-center">
        <router-link :to="{ name: config.homeRoute }" class="nav-btn" v-if="config.homeRoute" @click="handleDashboardClick">
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
          <span class="username">{{ userName }}</span>
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
      <aside class="sidebar" :class="[sidebarClass, { collapsed: isSidebarCollapsed }]">
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
          <template v-for="menu in config.menus" :key="menu.key">
            <li :class="{ 'menu-open': openMenus[menu.key] }">
              <div class="menu-item" @click="toggleMenu(menu.key)">
                <div class="menu-item-icon">
                  <font-awesome-icon :icon="menu.icon" />
                </div>
                <span class="menu-item-text" v-if="!isSidebarCollapsed">{{ menu.title }}</span>
                <span class="menu-arrow" v-if="!isSidebarCollapsed">
                  <font-awesome-icon
                      :icon="openMenus[menu.key] ? ['fas', 'chevron-down'] : ['fas', 'chevron-right']"
                      size="xs"
                  />
                </span>
              </div>
              <ul v-if="openMenus[menu.key] && !isSidebarCollapsed" class="sub-menu">
                <template v-for="item in menu.items" :key="item.action">
                  <li @click="executeAction(item.action)">
                    <font-awesome-icon :icon="item.icon" class="sub-icon" />
                    <span>{{ item.title }}</span>
                  </li>
                </template>
              </ul>
            </li>
          </template>
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
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

// ============ Props ============
const props = defineProps({
  config: {
    type: Object,
    required: true,
    validator: (value) => {
      return value.brandTitle && value.menus && value.defaultView &&
          value.lightThemeColors && value.darkThemeColors && value.actions
    }
  }
})

// ============ 响应式状态 ============
const avatarUrl = avatarImg
const router = useRouter()
const route = useRoute()

const userName = ref('')

// 获取用户信息
function loadUserInfo() {
  const userDataStr = localStorage.getItem('user') || sessionStorage.getItem('userData')
  if (userDataStr) {
    try {
      const userData = JSON.parse(userDataStr)
      userName.value = userData.username || '用户'
    } catch (e) {
      console.error('解析 userData 失败', e)
      userName.value = '用户'
    }
  } else {
    userName.value = '用户'
  }
}

const themeColors = ref({ ...props.config.lightThemeColors })

const openMenus = reactive(
    props.config.menus.reduce((acc, menu) => {
      acc[menu.key] = false
      return acc
    }, {})
)

const showToast = ref(false)
const toastMessage = ref('')
const isSidebarCollapsed = ref(false)

const tabs = ref([])
const currentView = ref(props.config.defaultView)
const isDarkMode = ref(document.documentElement.classList.contains('dark'))

// ============ 计算属性 ============
const homePageClass = computed(() => {
  // 根据 brandTitle 生成项目特定的类名
  const projectMap = {
    '豪森主数据': 'haosen-project',
    '恒瑞主数据': 'hengrui-project',
    '主数据管理': 'maindata-project'
  }
  return projectMap[props.config.brandTitle] || ''
})

const sidebarClass = computed(() => {
  // 根据 brandTitle 生成侧边栏特定的类名
  const projectMap = {
    '豪森主数据': 'haosen-sidebar',
    '恒瑞主数据': 'hengrui-sidebar',
    '主数据管理': 'maindata-sidebar'
  }
  return projectMap[props.config.brandTitle] || ''
})

const themeStyle = computed(() => {
  const { primary, dark, light } = themeColors.value
  // 重新检查暗色模式状态，确保 computed 会响应变化
  const isDark = isDarkMode.value

  // 基础样式所有模式都需要
  const style = {
    '--theme-primary': primary,
    '--theme-dark': dark,
    '--theme-light': light,
    '--theme-rgba': `rgba(${hexToRgb(primary)}, 0.35)`,
    '--theme-rgba-light': `rgba(${hexToRgb(primary)}, 0.08)`,
    '--theme-rgba-shadow': `rgba(${hexToRgb(primary)}, 0.45)`,
    '--theme-gradient': `linear-gradient(135deg, ${primary}, ${dark})`,
    '--theme-hover': `rgba(${hexToRgb(primary)}, 0.08)`,
    '--theme-hover-active': `rgba(${hexToRgb(primary)}, 0.12)`,
    '--theme-hover-open': `rgba(${hexToRgb(primary)}, 0.1)`,
  }

  // 亮色模式下设置 gradient-nav，暗色模式下让 CSS 文件中的项目特定值生效
  if (!isDark) {
    style['--theme-gradient-nav'] = `linear-gradient(90deg, ${dark} 0%, ${primary} 60%, ${light} 100%)`
  }

  return style
})

const currentViewComponent = computed(() => currentView.value)

// ============ 工具函数 ============
function hexToRgb(hex) {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex)
  return result
      ? `${parseInt(result[1], 16)}, ${parseInt(result[2], 16)}, ${parseInt(result[3], 16)}`
      : '106, 183, 255'
}

function adjustColor(hex, percent) {
  const num = parseInt(hex, 16)
  const r = Math.max(0, Math.min(255, (num >> 16) + percent))
  const g = Math.max(0, Math.min(255, ((num >> 8) & 0x00FF) + percent))
  const b = Math.max(0, Math.min(255, (num & 0x0000FF) + percent))
  return `#${(1 << 24 | r << 16 | g << 8 | b).toString(16).slice(1)}`
}

// ============ 主题管理 ============
function updateThemeForDarkMode() {
  const isDark = document.documentElement.classList.contains('dark')
  isDarkMode.value = isDark
  themeColors.value = isDark ? { ...props.config.darkThemeColors } : { ...props.config.lightThemeColors }
}

function loadThemeColor() {
  const routeName = route.name
  const savedColor = sessionStorage.getItem(`themeColor_${routeName}`)
  isDarkMode.value = document.documentElement.classList.contains('dark')

  if (savedColor) {
    const hex = savedColor.replace('#', '')
    const savedThemeColors = {
      primary: `#${hex}`,
      dark: adjustColor(hex, -20),
      light: adjustColor(hex, 20)
    }
    if (document.documentElement.classList.contains('dark')) {
      themeColors.value = { ...props.config.darkThemeColors }
    } else {
      themeColors.value = savedThemeColors
    }
  } else {
    updateThemeForDarkMode()
  }
}

// ============ 菜单操作 ============
function toggleMenu(menuKey) {
  openMenus[menuKey] = !openMenus[menuKey]
}

function executeAction(actionName) {
  const action = props.config.actions[actionName]
  if (action) {
    action()
  }
}

// ============ Tab 管理 ============
function addTab(title, component, hash) {
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

  if (hash) {
    window.location.hash = hash
  }
}

function switchTab(tabId) {
  tabs.value.forEach((tab) => {
    tab.active = tab.id === tabId
    if (tab.active) {
      currentView.value = tab.component
    }
  })
}

function closeTab(tabId) {
  if (tabs.value.length <= 1) {
    currentView.value = props.config.defaultView
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
      currentView.value = props.config.defaultView
    }
  }
  tabs.value.splice(index, 1)
}

function resetToDashboard() {
  tabs.value = []
  currentView.value = props.config.defaultView
  window.location.hash = ''
}

function handleDashboardClick() {
  // 如果有自定义的仪表盘点击处理函数则调用
  if (props.config.onDashboardClick) {
    props.config.onDashboardClick()
  } else {
    resetToDashboard()
  }
}

// ============ 退出登录 ============
async function handleLogout() {
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

function showToastMessage(message) {
  toastMessage.value = message
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 3000)
}

function toggleSidebar() {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}

// ============ 暴露方法给父组件 ============
defineExpose({
  addTab,
  resetToDashboard
})

// ============ 生命周期 ============
onMounted(() => {
  loadThemeColor()
  loadUserInfo()
  window.addEventListener('themeChanged', updateThemeForDarkMode)
})
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
  background: var(--theme-gradient-nav);
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

/* 恒瑞项目 - 深蓝色系侧边栏 */
html.dark aside.sidebar.hengrui-sidebar {
  background-color: #1a2230;
}

/* 主数据项目 - 暗绿色系侧边栏 */
html.dark aside.sidebar.maindata-sidebar {
  background-color: #1a2a18;
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
  color: var(--theme-text-dark);
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
  color: #fff;
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
  color: var(--theme-text-dark);
}

.menu-item:hover {
  background: var(--theme-hover);
  color: var(--theme-primary);
  transform: translateX(2px);
}

html.dark .menu-item:hover {
  background: rgba(255, 255, 255, 0.04);
  color: var(--theme-text-dark);
  transform: translateX(2px);
}

.menu-item.active {
  background: var(--theme-hover-active);
  border-left: 3px solid var(--theme-primary);
  color: var(--theme-primary);
  font-weight: 600;
}

html.dark .menu-item.active {
  background: rgba(255, 255, 255, 0.06);
  border-left: 3px solid var(--theme-text-dark);
  color: var(--theme-text-dark);
}

.menu-open > .menu-item {
  background: var(--theme-hover-open);
  color: var(--theme-primary);
}

html.dark .menu-open > .menu-item {
  background: rgba(255, 255, 255, 0.05);
  color: var(--theme-text-dark);
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
  color: var(--theme-text-dark);
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
  color: var(--theme-text-dark);
}

.sub-menu li:hover {
  background: var(--theme-hover);
  color: var(--theme-primary);
  border-left-color: var(--theme-primary);
  padding-left: 16px;
  transform: translateX(2px);
}

html.dark .sub-menu li:hover {
  background: rgba(255, 255, 255, 0.04);
  color: var(--theme-text-dark);
  border-left-color: var(--theme-text-dark);
  transform: translateX(2px);
}

.sub-icon {
  color: var(--theme-primary);
  font-size: 0.88rem;
  width: 16px;
  flex-shrink: 0;
}

html.dark .sub-icon {
  color: var(--theme-icon-dark);
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
  border-bottom: 1px solid #ece8f5;
  padding: 0 23px;
  box-shadow: 0 2px 8px var(--theme-rgba-light);
  transition: background-color 0.3s, box-shadow 0.3s;
  flex-shrink: 0;
}

html.dark .content-header {
  background-color: #1a1a2c;
  border-bottom-color: #2a2040;
  box-shadow: 0 2px 12px rgba(0,0,0,0.3);
}

/* 恒瑞项目 - 蓝色系内容头 */
html.dark .hengrui-project .content-header {
  background-color: #1a2230;
  border-bottom-color: #2a3550;
}

/* 主数据项目 - 绿色系内容头 */
html.dark .maindata-project .content-header {
  background-color: #1a2a18;
  border-bottom-color: #2a4030;
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
  border: 1px solid #ddd8f0;
  border-bottom: none;
  border-radius: 8px 8px 0 0;
  margin-right: 4px;
  background-color: #f5f2fc;
  white-space: nowrap;
  font-size: 0.8rem;
  color: #7a6a9e;
  transition: all 0.22s ease;
  position: relative;
}

html.dark .tab {
  background-color: #22203a;
  border-color: #38305a;
  color: #8878a8;
}

.tab:hover {
  background-color: #ede8fa;
  color: var(--theme-primary);
}

html.dark .tab:hover {
  background-color: #2c2850;
  color: var(--theme-text-dark);
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
  background-color: #1a2230;
  border-color: var(--theme-primary);
  border-bottom: 2px solid #1a2230;
  color: var(--theme-primary);
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
  color: #b0a0d0;
  transition: all 0.2s ease;
  font-size: 0.65rem;
}

.close-tab:hover {
  background: #e0d8f5;
  color: var(--theme-primary);
}

html.dark .close-tab {
  color: #5a5080;
}

html.dark .close-tab:hover {
  background: #2e2a50;
  color: var(--theme-text-dark);
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
  background: var(--theme-gradient);
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