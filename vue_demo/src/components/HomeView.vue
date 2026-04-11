<template>
  <div class="home-page">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="navbar-brand">
        <div class="brand-icon-box">
          <font-awesome-icon :icon="['fas', 'briefcase']" />
        </div>
        <span class="brand-name">项目管理系统</span>
      </div>
      <div class="navbar-links">
        <div class="user-info">
          <div class="avatar">
            <img :src="avatarUrl" alt="用户头像" />
          </div>
          <span class="username-text">{{ userData.username || '用户' }}</span>
        </div>
        <button class="logout-button" @click="handleLogout">
          <font-awesome-icon :icon="['fas', 'sign-out-alt']" size="sm" />
          退出登录
        </button>
      </div>
    </nav>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 工具栏 -->
      <div class="toolbar">
        <div class="search-box">
          <font-awesome-icon :icon="['fas', 'search']" class="search-icon" />
          <input
              type="text"
              placeholder="搜索项目..."
              v-model="searchQuery"
          />
        </div>
        <div class="tabs">
          <button
              v-for="tab in tabs"
              :key="tab.id"
              :class="{ active: activeTab === tab.id }"
              @click="activeTab = tab.id"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>

      <!-- 项目网格 -->
      <div class="projects-grid">
        <div
            v-for="project in filteredProjects"
            :key="project.id"
            class="project-card"
            @click="navigateToProject(project)"
        >
          <div class="card-header" :style="{ backgroundColor: project.color }">
            <font-awesome-icon :icon="['fas', project.icon]" size="2x" />
          </div>
          <div class="card-body">
            <h3>{{ project.name }}</h3>
            <p>{{ project.description }}</p>
            <div class="card-footer">
              <font-awesome-icon :icon="['fas', 'calendar-alt']" />
              <span>{{ project.addtime }}</span>
              <button
                v-if="project.permissionType === 'admin' || project.permissionType === 'owner'"
                class="permission-btn"
                @click.stop="openPermissionManager(project)"
                title="权限管理"
              >
                <font-awesome-icon :icon="['fas', 'user-shield']" />
              </button>
            </div>
          </div>
        </div>

        <!-- 添加新项目卡片 -->
        <div class="project-card add-card" @click="showAddProjectDialog">
          <div class="add-card-inner">
            <div class="add-icon">
              <font-awesome-icon :icon="['fas', 'plus']" size="2x" />
            </div>
            <span>新建项目</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Toast 提示 -->
    <transition name="toast-slide">
      <div v-if="showToast" class="toast" :class="toastType">
        <font-awesome-icon :icon="toastType === 'success' ? ['fas', 'check-circle'] : toastType === 'warning' ? ['fas', 'exclamation-circle'] : ['fas', 'times-circle']" class="toast-icon" />
        {{ toastMessage }}
      </div>
    </transition>

    <!-- 添加项目对话框 -->
    <div class="dialog-overlay" v-if="showDialog" @click.self="showDialog = false">
      <div class="dialog">
        <div class="dialog-header">
          <h3>创建新项目</h3>
          <button class="dialog-close" @click="showDialog = false">&times;</button>
        </div>

        <div class="dialog-content">
          <div class="form-group">
            <label>项目名称 <span class="required">*</span></label>
            <input type="text" v-model="newProject.name" placeholder="输入项目名称" />
          </div>
          <div class="form-group">
            <label>项目描述</label>
            <textarea v-model="newProject.description" placeholder="输入项目描述（可选）"></textarea>
          </div>
          <div class="form-group">
            <label>项目图标</label>
            <div class="icon-selector">
              <button
                  v-for="icon in availableIcons"
                  :key="icon"
                  :class="{ selected: newProject.icon === icon }"
                  @click="newProject.icon = icon"
              >
                <font-awesome-icon :icon="['fas', icon]" />
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
                  :class="{ selected: newProject.color === color }"
                  @click="newProject.color = color"
              ></div>
            </div>
          </div>
        </div>

        <div class="dialog-actions">
          <button class="cancel" @click="showDialog = false">取消</button>
          <button class="confirm" @click="addNewProject">创建项目</button>
        </div>
      </div>
    </div>

    <!-- 权限管理对话框 -->
    <PermissionManager
      v-if="currentProject"
      :project="currentProject"
      v-model:visible="permissionDialogVisible"
    />
  </div>
</template>

<script setup>
import '@/assets/css/dark-mode.css'
import avatarImg from '@/assets/img/avatar-modified.png'
import PermissionManager from './layout/PermissionManager.vue'

import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const avatarUrl = avatarImg
const userData = ref({})
const searchQuery = ref('')
const activeTab = ref('all')
const tabs = ref([
  { id: 'all', label: '全部项目' }
])
const projects = ref([])
const showDialog = ref(false)
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('success')
const currentProject = ref(null)
const permissionDialogVisible = ref(false)

const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 3000)
}

const newProject = ref({
  name: '',
  description: '',
  icon: 'project-diagram',
  color: '#9478cc'
})

const availableIcons = ref([
  'project-diagram', 'database', 'chart-line', 'laptop-code',
  'users', 'book', 'file-alt', 'hospital', 'store'
])

const availableColors = ref([
  '#9478cc', '#6ab7ff', '#ff9e7d', '#7dcf85',
  '#f9c74f', '#90be6d', '#577590', '#e38b8b'
])

const router = useRouter()

// 计算属性：过滤项目（支持搜索）
const filteredProjects = computed(() => {
  let filtered = projects.value

  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(project =>
        project.name.toLowerCase().includes(query) ||
        project.description.toLowerCase().includes(query)
    )
  }

  return filtered
})

// 生命周期：获取用户信息和项目列表
onMounted(() => {
  // 从 sessionStorage 获取用户信息
  const tempData = localStorage.getItem('user') || sessionStorage.getItem('userData')
  if (tempData) {
    try {
      userData.value = JSON.parse(tempData)
    } catch (e) {
      console.error('解析 userData 失败', e)
    }
  }

  getProjects()
})

// 获取用户有权限的项目
const getProjects = async () => {
  try {
    const userId = userData.value.userid;
    const response = await axios.get(`/api/projects/getMyProjects?userId=${userId}`)
    projects.value = response.data.data || []
  } catch (error) {
    console.error('获取项目失败:', error)
    showToastMessage('加载项目列表失败', 'error')
  }
}

// 跳转到具体项目页面
const navigateToProject = (project) => {
  let routeName = project.routeName

  // 如果 routeName 包含斜杠，说明存储的是路径而不是路由名称，需要转换
  if (routeName && routeName.includes('/')) {
    // 从路径提取路由名称，例如 "/projects/project/HaoSenHome" -> "HaoSenHome"
    const pathParts = routeName.split('/').filter(part => part)
    routeName = pathParts[pathParts.length - 1]
  }

  if (!router.hasRoute(routeName)) {
    showToastMessage('该项目路由不存在，请联系管理员', 'warning')
    return
  }

  // 将项目颜色存储到 sessionStorage，供项目页面使用
  if (project.color) {
    sessionStorage.setItem(`themeColor_${routeName}`, project.color)
  }

  router.push({ name: routeName }).catch(err => {
    console.error('路由跳转错误:', err)
    showToastMessage('页面跳转失败', 'error')
  })
}

// 显示添加项目对话框
const showAddProjectDialog = () => {
  newProject.value = {
    name: '',
    description: '',
    icon: 'project-diagram',
    color: '#9478cc'
  }
  showDialog.value = true
}

// 打开权限管理对话框
const openPermissionManager = (project) => {
  currentProject.value = project
  permissionDialogVisible.value = true
}

// 添加新项目
const addNewProject = async () => {
  if (!newProject.value.name.trim()) {
    showToastMessage('请输入项目名称', 'warning')
    return
  }

  try {
    await axios.post('/api/projects/addProject', {
      name: newProject.value.name.trim(),
      description: newProject.value.description,
      icon: newProject.value.icon,
      color: newProject.value.color,
      userId: userData.value.userid,
      userName: userData.value.username
    })

    showToastMessage('项目添加成功')
    showDialog.value = false
    getProjects() // 刷新列表
  } catch (error) {
    console.error('添加项目失败:', error)
    showToastMessage('添加项目失败，请重试', 'error')
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await axios.post('/api/user/logout')
    clearAuthData()
    showToastMessage('退出登录成功')
    setTimeout(() => {
      router.replace('/login')
    }, 400)
  } catch (error) {
    console.error('退出登录失败:', error)
    showToastMessage('退出失败，请稍后重试', 'error')
  }
}

// 清除认证信息
const clearAuthData = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  sessionStorage.removeItem('userData')
  delete axios.defaults.headers.common['Authorization']
}



</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: #f0f2f8;
  transition: background-color 0.3s ease;
}

html.dark .home-page {
  background: #12121e;
}

/* ===== 导航栏 ===== */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 58px;
  background: linear-gradient(90deg, #7c5cbf 0%, #9d7de8 60%, #b89ff0 100%);
  color: #fff;
  box-shadow: 0 3px 16px rgba(124, 92, 191, 0.35);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  transition: background 0.3s ease, box-shadow 0.3s ease;
}

html.dark .navbar {
  background: linear-gradient(90deg, #1e293b 0%, #0f172a 60%, #1e293b 100%);
  box-shadow: 0 3px 20px rgba(0, 0, 0, 0.5);
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-icon-box {
  width: 34px;
  height: 34px;
  background: rgba(255,255,255,0.18);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  border: 1px solid rgba(255,255,255,0.25);
}

.brand-name {
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.navbar-links {
  display: flex;
  align-items: center;
  gap: 16px;
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

.username-text {
  font-size: 0.88rem;
  font-weight: 500;
  opacity: 0.9;
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
  font-family: inherit;
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

/* ===== 主内容区 ===== */
.main-content {
  margin-top: 58px;
  padding: 20px 24px;
  height: calc(100vh - 58px);
  overflow-y: auto;
}

/* ===== 工具栏 ===== */
.toolbar {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 22px;
  flex-wrap: wrap;
}

.search-box {
  position: relative;
  flex: 0 0 300px;
}

.search-box input {
  width: 100%;
  padding: 10px 14px 10px 38px;
  border: 1.5px solid #ddd8f0;
  border-radius: 24px;
  font-size: 0.88rem;
  background: #fff;
  color: #333;
  transition: all 0.25s ease;
  box-sizing: border-box;
  font-family: inherit;
}

.search-box input:focus {
  outline: none;
  border-color: #64748b;
  box-shadow: 0 0 0 3px rgba(100, 116, 139, 0.15);
}

html.dark .search-box input {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

html.dark .search-box input:focus {
  border-color: #475569;
  box-shadow: 0 0 0 3px rgba(71, 85, 99, 0.25);
}

.search-icon {
  position: absolute;
  left: 13px;
  top: 50%;
  transform: translateY(-50%);
  color: #64748b;
  font-size: 0.85rem;
}

.tabs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tabs button {
  padding: 7px 18px;
  border: 1.5px solid #ddd8f0;
  background: #fff;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.85rem;
  color: #7a6a9e;
  transition: all 0.22s ease;
  font-family: inherit;
}

.tabs button:hover {
  border-color: #9d7de8;
  color: #7c5cbf;
  background: #f5f0ff;
}

.tabs button.active {
  background: linear-gradient(135deg, #9d7de8, #7c5cbf);
  border-color: transparent;
  color: #fff;
  box-shadow: 0 3px 10px rgba(124, 92, 191, 0.3);
}

html.dark .tabs button {
  background: #1e293b;
  border-color: #334155;
  color: #94a3b8;
}

html.dark .tabs button:hover {
  background: #334155;
  border-color: #475569;
  color: #cbd5e1;
}

html.dark .tabs button.active {
  background: linear-gradient(135deg, #334155, #1e293b);
  border-color: transparent;
  color: #e2e8f0;
}

/* ===== 项目网格 ===== */
.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 18px;
}

/* ===== 项目卡片 ===== */
.project-card {
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(124, 92, 191, 0.08);
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease;
  display: flex;
  flex-direction: column;
}

.project-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 28px rgba(124, 92, 191, 0.18);
}

html.dark .project-card {
  background: #1a1a2c;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.35);
}

html.dark .project-card:hover {
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.55);
}

.card-header {
  height: 90px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 1.5rem;
  position: relative;
  overflow: hidden;
}

.card-header::after {
  content: '';
  position: absolute;
  width: 80px;
  height: 80px;
  background: rgba(255,255,255,0.1);
  border-radius: 50%;
  bottom: -30px;
  right: -20px;
}

.card-body {
  padding: 14px 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-body h3 {
  margin: 0 0 6px 0;
  font-size: 0.95rem;
  font-weight: 600;
  color: #1e293b;
  transition: color 0.3s;
}

html.dark .card-body h3 {
  color: #e2e8f0;
}

.card-body p {
  margin: 0 0 10px 0;
  font-size: 0.78rem;
  color: #64748b;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}

html.dark .card-body p {
  color: #94a3b8;
}

.card-footer {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.72rem;
  color: #94a3b8;
  margin-top: auto;
  padding-top: 8px;
  border-top: 1px solid #e2e8f0;
}

html.dark .card-footer {
  border-top-color: #334155;
  color: #64748b;
}

.permission-btn {
  background: linear-gradient(135deg, #9d7de8, #7c5cbf);
  border: none;
  color: #fff;
  padding: 4px 8px;
  border-radius: 6px;
  cursor: pointer;
  margin-left: auto;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.permission-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 2px 8px rgba(124, 92, 191, 0.4);
}

html.dark .permission-btn {
  background: linear-gradient(135deg, #334155, #1e293b);
}

html.dark .permission-btn:hover {
  box-shadow: 0 2px 8px rgba(100, 116, 139, 0.4);
}

/* ===== 新建卡片 ===== */
.add-card {
  border: 2px dashed #e2e8f0;
  box-shadow: none;
  background: transparent;
  min-height: 160px;
}

.add-card:hover {
  border-color: #9d7de8;
  box-shadow: none;
  transform: translateY(-5px);
  background: rgba(157, 125, 232, 0.04);
}

html.dark .add-card {
  border-color: #334155;
  background: transparent;
}

html.dark .add-card:hover {
  border-color: #475569;
  background: rgba(71, 85, 99, 0.08);
}

.add-card-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 160px;
  gap: 12px;
  color: #b0a0d0;
  transition: color 0.22s;
}

.add-card:hover .add-card-inner {
  color: #9d7de8;
}

html.dark .add-card-inner {
  color: #64748b;
}

html.dark .add-card:hover .add-card-inner {
  color: #94a3b8;
}

.add-icon {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  border: 2px dashed currentColor;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-card-inner span {
  font-size: 0.88rem;
  font-weight: 500;
}

/* ===== 对话框 ===== */
.dialog-overlay {
  position: fixed;
  inset: 0;
  background: rgba(20, 10, 40, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  backdrop-filter: blur(3px);
}

.dialog {
  background: #fff;
  border-radius: 16px;
  width: 90%;
  max-width: 460px;
  box-shadow: 0 12px 48px rgba(124, 92, 191, 0.25);
  overflow: hidden;
  transition: background-color 0.3s;
}

html.dark .dialog {
  background: #1a1a2c;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.6);
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 22px 14px;
  border-bottom: 1px solid #ece8f5;
  background: linear-gradient(90deg, #f8f5ff, #fff);
}

html.dark .dialog-header {
  border-bottom-color: #334155;
  background: linear-gradient(90deg, #1e293b, #334155);
}

.dialog-header h3 {
  margin: 0;
  font-size: 1.05rem;
  font-weight: 600;
  color: #2c2060;
}

html.dark .dialog-header h3 {
  color: #e2e8f0;
}

.dialog-close {
  background: none;
  border: none;
  font-size: 1.4rem;
  color: #b0a0d0;
  cursor: pointer;
  line-height: 1;
  padding: 0 4px;
  transition: color 0.2s;
}

.dialog-close:hover {
  color: #7c5cbf;
}

html.dark .dialog-close:hover {
  color: #cbd5e1;
}

.dialog-content {
  padding: 18px 22px;
}

.form-group {
  margin-bottom: 14px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 600;
  color: #7a6a9e;
  font-size: 0.83rem;
  transition: color 0.3s;
}

.required {
  color: #e05080;
  margin-left: 2px;
}

html.dark .form-group label {
  color: #94a3b8;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 9px 12px;
  border: 1.5px solid #ddd8f0;
  border-radius: 10px;
  font-size: 0.875rem;
  background: #fff;
  color: #333;
  box-sizing: border-box;
  transition: all 0.22s ease;
  font-family: inherit;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #9d7de8;
  box-shadow: 0 0 0 3px rgba(157, 125, 232, 0.15);
}

html.dark .form-group input,
html.dark .form-group textarea {
  background: #1e293b;
  border-color: #334155;
  color: #e2e8f0;
}

html.dark .form-group input:focus,
html.dark .form-group textarea:focus {
  border-color: #7060b8;
  box-shadow: 0 0 0 3px rgba(112, 96, 184, 0.25);
}

.form-group textarea {
  min-height: 72px;
  resize: vertical;
}

/* 图标选择器 */
.icon-selector {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 6px;
}

.icon-selector button {
  background: #f5f0ff;
  border: 2px solid transparent;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #9d7de8;
  transition: all 0.2s;
  font-family: inherit;
}

.icon-selector button:hover {
  background: #ede8fa;
  border-color: #c8b8ee;
}

html.dark .icon-selector button {
  background: #1e293b;
  color: #94a3b8;
}

html.dark .icon-selector button:hover {
  background: #334155;
  border-color: #64748b;
}

.icon-selector button.selected {
  background: linear-gradient(135deg, #9d7de8, #7c5cbf);
  border-color: transparent;
  color: #fff;
  box-shadow: 0 3px 8px rgba(124, 92, 191, 0.3);
}

html.dark .icon-selector button.selected {
  background: linear-gradient(135deg, #334155, #1e293b);
}

/* 颜色选择器 */
.color-selector {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 6px;
}

.color-selector div {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  cursor: pointer;
  border: 3px solid transparent;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 6px rgba(0,0,0,0.15);
}

.color-selector div:hover {
  transform: scale(1.18);
}

.color-selector div.selected {
  border-color: #2c2060;
  transform: scale(1.18);
  box-shadow: 0 3px 10px rgba(0,0,0,0.25);
}

html.dark .color-selector div.selected {
  border-color: #d0c0f8;
}

/* 对话框按钮区 */
.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 14px 22px 18px;
  border-top: 1px solid #e2e8f0;
}

html.dark .dialog-actions {
  border-top-color: #334155;
}

.dialog-actions button {
  padding: 9px 22px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 600;
  font-size: 0.875rem;
  transition: all 0.22s;
  font-family: inherit;
}

.dialog-actions .cancel {
  background: #f5f0ff;
  border: 1.5px solid #ddd8f0;
  color: #7a6a9e;
}

.dialog-actions .cancel:hover {
  background: #ede8fa;
  border-color: #c8b8ee;
  color: #7c5cbf;
}

html.dark .dialog-actions .cancel {
  background: #1e293b;
  border-color: #334155;
  color: #94a3b8;
}

html.dark .dialog-actions .cancel:hover {
  background: #334155;
  border-color: #475569;
  color: #cbd5e1;
}

.dialog-actions .confirm {
  background: linear-gradient(135deg, #9d7de8, #7c5cbf);
  border: none;
  color: #fff;
  box-shadow: 0 4px 12px rgba(124, 92, 191, 0.35);
}

.dialog-actions .confirm:hover {
  background: linear-gradient(135deg, #b09af0, #9d7de8);
  box-shadow: 0 6px 18px rgba(124, 92, 191, 0.5);
  transform: translateY(-1px);
}

html.dark .dialog-actions .confirm {
  background: linear-gradient(135deg, #334155, #1e293b);
  box-shadow: 0 4px 12px rgba(0,0,0,0.4);
}

html.dark .dialog-actions .confirm:hover {
  background: linear-gradient(135deg, #475569, #334155);
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .main-content {
    padding: 16px;
  }

  .projects-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 12px;
  }

  .search-box {
    flex: 1;
    min-width: 0;
  }

  .toolbar {
    gap: 10px;
  }
}

@media (max-width: 480px) {
  .projects-grid {
    grid-template-columns: 1fr 1fr;
  }

  .username-text {
    display: none;
  }
}

/* ===== Toast 提示 ===== */
.toast {
  position: fixed;
  top: 24px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 11px 22px;
  border-radius: 24px;
  font-size: 0.875rem;
  font-weight: 500;
  color: #fff;
  z-index: 9999;
  white-space: nowrap;
  box-shadow: 0 4px 20px rgba(139, 92, 246, 0.4);
  background: linear-gradient(135deg, #9d7de8, #7c5cbf);
}

.toast.warning {
  background: linear-gradient(135deg, #f0a050, #d97820);
  box-shadow: 0 4px 20px rgba(217, 120, 32, 0.4);
}

.toast.error {
  background: linear-gradient(135deg, #e86868, #c04040);
  box-shadow: 0 4px 20px rgba(192, 64, 64, 0.4);
}

html.dark .toast {
  background: linear-gradient(135deg, #334155, #1e293b);
  box-shadow: 0 4px 24px rgba(0,0,0,0.5);
}

html.dark .toast.warning {
  background: linear-gradient(135deg, #a06020, #7a4010);
  box-shadow: 0 4px 20px rgba(0,0,0,0.4);
}

html.dark .toast.error {
  background: linear-gradient(135deg, #8c3030, #5a1a1a);
  box-shadow: 0 4px 20px rgba(0,0,0,0.4);
}

.toast-icon {
  font-size: 1rem;
  flex-shrink: 0;
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