<template>
  <div class="home-page" :style="themeStyle">


    <nav class="navbar">
      <div class="navbar-left">
        <div class="logo-area">
          <div class="logo-icon">
            <font-awesome-icon :icon="['fas', 'database']" />
          </div>
          <div class="brand-text">
            <span class="brand-title">{{ projectConfig.brandTitle }}</span>
            <span class="brand-subtitle">{{ projectConfig.brandSubtitle }}</span>
          </div>
        </div>
      </div>

      <div class="navbar-center">
        <router-link :to="{ name: projectConfig.homeRoute }" class="nav-btn" @click="resetToDashboard">
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
          <span class="username">{{ userData.username || '管理员' }}</span>
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
          <li
              v-for="(menu, menuKey) in menuConfig"
              :key="menuKey"
              :class="{ 'menu-open': openMenus[menuKey] }"
          >
            <div class="menu-item" @click="toggleMenu(menuKey)">
              <div class="menu-item-icon">
                <font-awesome-icon :icon="menu.icon" />
              </div>
              <span class="menu-item-text" v-if="!isSidebarCollapsed">{{ menu.label }}</span>
              <span class="menu-arrow" v-if="!isSidebarCollapsed">
                <font-awesome-icon
                    :icon="openMenus[menuKey] ? ['fas', 'chevron-down'] : ['fas', 'chevron-right']"
                    size="xs"
                />
              </span>
            </div>
            <ul v-if="openMenus[menuKey] && !isSidebarCollapsed" class="sub-menu">
              <li
                  v-for="(item, itemKey) in menu.children"
                  :key="itemKey"
                  @click="handleMenuClick(item)"
              >
                <font-awesome-icon :icon="item.icon" class="sub-icon" />
                <span>{{ item.label }}</span>
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
import { ref, computed, onMounted, resolveComponent, defineProps, defineExpose } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

// Props
const props = defineProps({
  // 项目配置
  projectConfig: {
    type: Object,
    required: true,
    default: () => ({
      brandTitle: '主数据',
      brandSubtitle: '后台管理系统',
      homeRoute: 'Home',
      defaultColor: '#9478cc'
    })
  },
  // 菜单配置
  menuConfig: {
    type: Object,
    required: true,
    default: () => ({})
  },
  // 菜单点击处理函数映射
  menuHandlers: {
    type: Object,
    required: true,
    default: () => ({})
  },
  // 默认视图组件
  defaultView: {
    type: String,
    default: 'HomeDashboardView'
  }
})

const avatarUrl = avatarImg
const router = useRouter()
const route = useRoute()
const userData = ref({})

// 主题颜色配置
const themeColors = ref({
  primary: props.projectConfig.defaultColor,
  dark: adjustColor(props.projectConfig.defaultColor.replace('#', ''), -20),
  light: adjustColor(props.projectConfig.defaultColor.replace('#', ''), 20)
})

// 动态主题样式
const themeStyle = computed(() => {
  const { primary, dark, light } = themeColors.value
  const primaryRgb = hexToRgb(primary)
  const darkRgb = hexToRgb(dark)
  return {
    '--theme-primary': primary,
    '--theme-dark': dark,
    '--theme-light': light,
    '--theme-rgba': `rgba(${primaryRgb}, 0.35)`,
    '--theme-rgba-light': `rgba(${primaryRgb}, 0.08)`,
    '--theme-rgba-shadow': `rgba(${primaryRgb}, 0.45)`,
    '--theme-gradient': `linear-gradient(135deg, ${primary}, ${dark})`,
    '--theme-gradient-nav': `linear-gradient(90deg, ${dark} 0%, ${primary} 60%, ${light} 100%)`,
    // 暗色模式导航栏渐变 - 使用更暗的色调
    '--theme-gradient-nav-dark': `linear-gradient(90deg, ${adjustColor(primary.replace('#', ''), -35)} 0%, ${adjustColor(primary.replace('#', ''), -25)} 60%, ${adjustColor(primary.replace('#', ''), -15)} 100%)`,
    '--theme-text-dark': light,
    '--theme-icon-dark': light,
    // 导航栏元素 - 暗色模式
    '--theme-nav-icon-bg': `rgba(${darkRgb}, 0.3)`,
    '--theme-nav-icon-border': `rgba(${darkRgb}, 0.5)`,
    '--theme-nav-btn-bg': `rgba(${darkRgb}, 0.2)`,
    '--theme-nav-btn-border': `rgba(${darkRgb}, 0.35)`,
    '--theme-nav-btn-hover': `rgba(${primaryRgb}, 0.3)`,
    '--theme-nav-text': `rgba(255, 255, 255, 0.85)`,
    // 菜单 hover 背景色 - 使用主题色的浅色版本（亮色模式）
    '--theme-bg-hover': `rgba(${primaryRgb}, 0.08)`,
    '--theme-bg-hover-dark': `rgba(${primaryRgb}, 0.12)`,
    '--theme-bg-open': `rgba(${primaryRgb}, 0.12)`,
    '--theme-bg-open-dark': `rgba(${primaryRgb}, 0.16)`,
    // 暗色模式下的背景色
    '--theme-bg-hover-dark-mode': `rgba(${primaryRgb}, 0.15)`,
    '--theme-bg-hover-dark-mode-dark': `rgba(${primaryRgb}, 0.20)`,
    '--theme-bg-open-dark-mode': `rgba(${primaryRgb}, 0.20)`,
    '--theme-bg-open-dark-mode-dark': `rgba(${primaryRgb}, 0.25)`,
    // 标签页背景色（亮色模式）
    '--theme-tab-border': `rgba(${primaryRgb}, 0.2)`,
    '--theme-tab-bg': `rgba(${primaryRgb}, 0.05)`,
    '--theme-tab-text': primary,
    '--theme-tab-hover-bg': `rgba(${primaryRgb}, 0.1)`,
    // 标签页背景色（暗色模式）
    '--theme-tab-border-dark': `rgba(${primaryRgb}, 0.3)`,
    '--theme-tab-bg-dark': `rgba(${primaryRgb}, 0.15)`,
    '--theme-tab-text-dark': `rgba(${primaryRgb}, 0.7)`,
    '--theme-tab-hover-bg-dark': `rgba(${primaryRgb}, 0.25)`,
  }
})

// HEX 转 RGB 辅助函数
function hexToRgb(hex) {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex)
  return result
    ? `${parseInt(result[1], 16)}, ${parseInt(result[2], 16)}, ${parseInt(result[3], 16)}`
    : '148, 120, 204'
}

// 调整颜色亮度
function adjustColor(hex, percent) {
  const num = parseInt(hex, 16)
  const r = Math.max(0, Math.min(255, (num >> 16) + percent))
  const g = Math.max(0, Math.min(255, ((num >> 8) & 0x00FF) + percent))
  const b = Math.max(0, Math.min(255, (num & 0x0000FF) + percent))
  return `#${(1 << 24 | r << 16 | g << 8 | b).toString(16).slice(1)}`
}

// 根据路由名称加载主题颜色
function loadThemeColor() {
  const routeName = route.name
  const savedColor = sessionStorage.getItem(`themeColor_${routeName}`)

  if (savedColor) {
    const hex = savedColor.replace('#', '')
    themeColors.value = {
      primary: `#${hex}`,
      dark: adjustColor(hex, -20),
      light: adjustColor(hex, 20)
    }
  } else {
    // 使用项目配置的默认颜色
    const defaultHex = props.projectConfig.defaultColor.replace('#', '')
    themeColors.value = {
      primary: `#${defaultHex}`,
      dark: adjustColor(defaultHex, -20),
      light: adjustColor(defaultHex, 20)
    }
  }
}

onMounted(() => {
  loadThemeColor()
  // 从 sessionStorage 获取用户信息
  const tempData = localStorage.getItem('user') || sessionStorage.getItem('userData')
  if (tempData) {
    try {
      userData.value = JSON.parse(tempData)
    } catch (e) {
      console.error('解析 userData 失败', e)
    }
  }
})

// 菜单状态
const openMenus = ref(
  Object.keys(props.menuConfig).reduce((acc, key) => {
    acc[key] = false
    return acc
  }, {})
)

const showToast = ref(false)
const toastMessage = ref('')
const isSidebarCollapsed = ref(false)

const tabs = ref([])
const currentView = ref(props.defaultView)

// 动态解析组件，支持字符串名称自动转换为组件
const currentViewComponent = computed(() => {
  try {
    return resolveComponent(currentView.value)
  } catch (e) {
    console.warn(`Component "${currentView.value}" not found, using fallback`)
    return null
  }
})

// 菜单操作
const toggleMenu = (menu) => {
  openMenus.value[menu] = !openMenus.value[menu]
}

const handleMenuClick = (item) => {
  const handler = props.menuHandlers[item.handler]
  if (handler && typeof handler === 'function') {
    handler(item)
  }
}

// Tab 管理
const addTab = (title, component, hash) => {
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
    currentView.value = props.defaultView
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
      currentView.value = props.defaultView
    }
  }
  tabs.value.splice(index, 1)
}

const resetToDashboard = () => {
  tabs.value = []
  currentView.value = props.defaultView
  window.location.hash = ''
}

// 退出登录
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

// 暴露给父组件的方法
defineExpose({
  addTab,
  switchTab,
  closeTab,
  resetToDashboard,
  showToastMessage,
  currentView,
  tabs
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

/* 左区：Logo */
.navbar-left {
  display: flex;
  align-items: center;
  color: #fff;
}

html.dark .navbar-left {
  color: var(--theme-nav-text, rgba(255,255,255,0.95));
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

html.dark .logo-icon {
  background: var(--theme-nav-icon-bg);
  border-color: var(--theme-nav-icon-border);
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
  color: #fff;
}

html.dark .brand-title {
  color: var(--theme-nav-text, rgba(255,255,255,0.95));
}

.brand-subtitle {
  font-size: 0.68rem;
  opacity: 0.75;
  letter-spacing: 0.3px;
  color: rgba(255,255,255,0.8);
}

html.dark .brand-subtitle {
  color: var(--theme-nav-text, rgba(255,255,255,0.75));
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
  color: var(--theme-nav-text, rgba(255,255,255,0.85));
  text-decoration: none;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 0.85rem;
  transition: all 0.25s ease;
  border: 1px solid var(--theme-nav-btn-border, rgba(255,255,255,0.2));
  background: var(--theme-nav-btn-bg, rgba(255,255,255,0.08));
}

.nav-btn:hover {
  background: var(--theme-nav-btn-hover, var(--theme-primary));
  color: #fff;
  border-color: var(--theme-primary);
  box-shadow: 0 2px 12px var(--theme-rgba-shadow);
}

html.dark .nav-btn {
  background: var(--theme-nav-btn-bg);
  border-color: var(--theme-nav-btn-border);
  color: var(--theme-nav-text);
}

html.dark .nav-btn:hover {
  background: var(--theme-nav-btn-hover);
  border-color: var(--theme-primary);
}

/* 右局：用户区 */
.navbar-right {
  display: flex;
  align-items: center;
  gap: 14px;
  color: #fff;
}

html.dark .navbar-right {
  color: var(--theme-nav-text, rgba(255,255,255,0.95));
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
  border: 2px solid var(--theme-nav-btn-border, rgba(255,255,255,0.6));
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
  color: var(--theme-nav-text, rgba(255,255,255,0.9));
}

.divider-v {
  width: 1px;
  height: 20px;
  background: var(--theme-nav-btn-border, rgba(255,255,255,0.3));
}

.logout-button {
  display: flex;
  align-items: center;
  gap: 6px;
  background: var(--theme-nav-btn-bg, rgba(255,255,255,0.12));
  color: #fff;
  border: 1px solid var(--theme-nav-btn-border, rgba(255,255,255,0.25));
  padding: 6px 14px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.83rem;
  transition: all 0.25s ease;
}

.logout-button:hover {
  background: var(--theme-nav-btn-hover, rgba(255,255,255,0.25));
  border-color: var(--theme-primary);
  color: #fff;
}

html.dark .logout-button {
  background: var(--theme-nav-btn-bg);
  border-color: var(--theme-nav-btn-border);
  color: #fff;
}

html.dark .logout-button:hover {
  background: var(--theme-nav-btn-hover);
  border-color: var(--theme-primary);
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
  color: var(--theme-text-dark, #c8b8ee);
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

/* 菜单 */
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
  color: var(--theme-text-dark, var(--theme-primary));
}

.menu-item:hover {
  background: linear-gradient(135deg, var(--theme-bg-hover), var(--theme-bg-hover-dark));
  color: var(--theme-primary);
}

html.dark .menu-item:hover {
  background: linear-gradient(135deg, var(--theme-bg-hover-dark-mode), var(--theme-bg-hover-dark-mode-dark));
  color: var(--theme-text-dark, var(--theme-primary));
}

.menu-open > .menu-item {
  background: linear-gradient(135deg, var(--theme-bg-open), var(--theme-bg-open-dark));
  color: var(--theme-primary);
}

html.dark .menu-open > .menu-item {
  background: linear-gradient(135deg, var(--theme-bg-open-dark-mode), var(--theme-bg-open-dark-mode-dark));
  color: var(--theme-text-dark, var(--theme-primary));
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
  color: var(--theme-text-dark, var(--theme-primary));
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
  color: var(--theme-text-dark, var(--theme-primary));
}

.sub-menu li:hover {
  background: linear-gradient(135deg, var(--theme-bg-hover), var(--theme-bg-hover-dark));
  color: var(--theme-primary);
  border-left-color: var(--theme-primary);
  padding-left: 14px;
}

html.dark .sub-menu li:hover {
  background: linear-gradient(135deg, var(--theme-bg-hover-dark-mode), var(--theme-bg-hover-dark-mode-dark));
  color: var(--theme-text-dark, var(--theme-primary));
  border-left-color: var(--theme-primary);
}

.sub-icon {
  color: var(--theme-primary);
  font-size: 0.88rem;
  width: 16px;
  flex-shrink: 0;
}

html.dark .sub-icon {
  color: var(--theme-text-dark, var(--theme-primary));
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
  border-bottom: 1px solid var(--theme-tab-border);
  padding: 0 23px;
  box-shadow: 0 2px 8px var(--theme-rgba-light);
  transition: background-color 0.3s, box-shadow 0.3s;
  flex-shrink: 0;
}

html.dark .content-header {
  background-color: #1a1a2c;
  border-bottom-color: var(--theme-tab-border-dark);
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
  border: 1px solid var(--theme-tab-border);
  border-bottom: none;
  border-radius: 8px 8px 0 0;
  margin-right: 4px;
  background-color: var(--theme-tab-bg);
  white-space: nowrap;
  font-size: 0.8rem;
  color: var(--theme-tab-text);
  transition: all 0.22s ease;
  position: relative;
}

html.dark .tab {
  background-color: var(--theme-tab-bg-dark);
  border-color: var(--theme-tab-border-dark);
  color: var(--theme-tab-text-dark);
}

.tab:hover {
  background-color: var(--theme-tab-hover-bg);
  color: var(--theme-primary);
}

html.dark .tab:hover {
  background-color: var(--theme-tab-hover-bg-dark);
  color: var(--theme-text-dark, var(--theme-primary));
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
  color: var(--theme-text-dark, var(--theme-primary));
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
  color: var(--theme-tab-text-dark);
  transition: all 0.2s ease;
  font-size: 0.65rem;
}

.close-tab:hover {
  background: var(--theme-tab-hover-bg);
  color: var(--theme-primary);
}

html.dark .close-tab {
  color: var(--theme-tab-text-dark);
}

html.dark .close-tab:hover {
  background: var(--theme-tab-hover-bg-dark);
  color: var(--theme-text-dark, var(--theme-primary));
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

<style>
/* 暗色模式导航栏样式 - 使用 non-scoped 样式以确保正确应用 */
/* 这些样式依赖于 .home-page 上设置的 CSS 变量 */

html.dark .home-page .navbar {
  background: var(--theme-gradient-nav-dark) !important;
  box-shadow: 0 3px 20px rgba(0, 0, 0, 0.5) !important;
}

html.dark .home-page .navbar .navbar-left,
html.dark .home-page .navbar .navbar-right {
  color: var(--theme-nav-text, rgba(255, 255, 255, 0.85)) !important;
}

html.dark .home-page .navbar .brand-title,
html.dark .home-page .navbar .brand-subtitle {
  color: var(--theme-nav-text, rgba(255, 255, 255, 0.85)) !important;
}

html.dark .home-page .navbar .nav-btn {
  background: var(--theme-nav-btn-bg, rgba(125, 96, 184, 0.2)) !important;
  border-color: var(--theme-nav-btn-border, rgba(125, 96, 184, 0.35)) !important;
  color: var(--theme-nav-text, rgba(255, 255, 255, 0.85)) !important;
}

html.dark .home-page .navbar .nav-btn:hover {
  background: var(--theme-nav-btn-hover, rgba(148, 120, 204, 0.3)) !important;
  border-color: var(--theme-primary) !important;
}

html.dark .home-page .navbar .logout-button {
  background: var(--theme-nav-btn-bg, rgba(125, 96, 184, 0.2)) !important;
  border-color: var(--theme-nav-btn-border, rgba(125, 96, 184, 0.35)) !important;
  color: #fff !important;
}

html.dark .home-page .navbar .logout-button:hover {
  background: var(--theme-nav-btn-hover, rgba(148, 120, 204, 0.3)) !important;
  border-color: var(--theme-primary) !important;
}

html.dark .home-page .navbar .logo-icon {
  background: var(--theme-nav-icon-bg, rgba(125, 96, 184, 0.3)) !important;
  border-color: var(--theme-nav-icon-border, rgba(125, 96, 184, 0.5)) !important;
}

html.dark .home-page .navbar .avatar img {
  border-color: var(--theme-nav-btn-border, rgba(125, 96, 184, 0.6)) !important;
}

html.dark .home-page .navbar .divider-v {
  background: var(--theme-nav-btn-border, rgba(125, 96, 184, 0.35)) !important;
}

html.dark .home-page .navbar .username {
  color: var(--theme-nav-text, rgba(255, 255, 255, 0.85)) !important;
}
</style>
