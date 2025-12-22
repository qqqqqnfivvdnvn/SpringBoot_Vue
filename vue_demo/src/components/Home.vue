<template>
  <div class="home-page" :style="homePageStyle">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="navbar-brand">项目管理系统</div>
      <div class="navbar-links">
        <router-link to="/home">首页</router-link>
        <router-link to="">设置</router-link>
        <div class="avatar">
          <img src="@/assets/images/avatar-modified.png" alt="用户头像" />
        </div>
        <button class="logout-button" @click="handleLogout">
          <font-awesome-icon :icon="['fas', 'sign-out-alt']" size="1x" />
          退出登录
        </button>
      </div>
    </nav>

    <!-- 全屏项目入口区域 -->
    <div class="fullscreen-projects">
      <div class="projects-container">
        <!-- 项目搜索框 -->
        <div class="projects-search">
          <font-awesome-icon :icon="['fas', 'search']" class="search-icon" />
          <input
              type="text"
              placeholder="搜索项目..."
              v-model="searchQuery"
          />
        </div>

        <!-- 项目分类标签 -->
        <div class="projects-tabs">
          <button
              v-for="tab in tabs"
              :key="tab.id"
              :class="{ active: activeTab === tab.id }"
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
              <font-awesome-icon :icon="['fas', project.icon]" size="2x" />
            </div>
            <div class="project-info">
              <h3>{{ project.name }}</h3>
              <p>{{ project.description }}</p>
              <div class="project-meta">
                <span><font-awesome-icon :icon="['fas', 'calendar-alt']" /> {{ project.addtime }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 添加新项目按钮 -->
        <div class="add-project" @click="showAddProjectDialog">
          <font-awesome-icon :icon="['fas', 'plus-circle']" size="2x" />
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
            <input type="text" v-model="newProject.name" placeholder="输入项目名称" />
          </div>
          <div class="form-group">
            <label>项目描述</label>
            <textarea v-model="newProject.description" placeholder="输入项目描述"></textarea>
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
          <button class="confirm" @click="addNewProject">确认添加</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 响应式数据
const homePageStyle = ref({
  background: 'linear-gradient(135deg, #e3d2ff, #e3d2ff)'
})

const userData = ref({})
const searchQuery = ref('')
const activeTab = ref('all')
const tabs = ref([
  { id: 'all', label: '全部项目' }
])
const projects = ref([])
const showDialog = ref(false)

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
  '#f9c74f', '#90be6d', '#577590', '#f94144'
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
  const tempData = sessionStorage.getItem('userData')
  if (tempData) {
    try {
      userData.value = JSON.parse(tempData)
    } catch (e) {
      console.error('解析 userData 失败', e)
    }
  }

  getProjects()
})

// 获取所有项目
const getProjects = async () => {
  try {
    const response = await axios.get('/api/projects/getAllProjects')
    projects.value = response.data.data || []
  } catch (error) {
    console.error('获取项目失败:', error)
    ElMessage.error('加载项目列表失败')
  }
}

// 跳转到具体项目页面
const navigateToProject = (project) => {
  const routeName = project.routeName
  if (!router.hasRoute(routeName)) {
    ElMessage.warning('该项目路由不存在，请联系管理员')
    return
  }

  router.push({ name: routeName, params: { id: routeName } }).catch(err => {
    console.error('路由跳转错误:', err)
    ElMessage.error('页面跳转失败')
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

// 添加新项目
const addNewProject = async () => {
  if (!newProject.value.name.trim()) {
    ElMessage.warning('请输入项目名称')
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

    ElMessage.success('项目添加成功')
    showDialog.value = false
    getProjects() // 刷新列表
  } catch (error) {
    console.error('添加项目失败:', error)
    ElMessage.error('添加项目失败，请重试')
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await axios.post('/api/user/logout')
    clearAuthData()
    ElMessage.success('退出登录成功')
    setTimeout(() => {
      router.replace('/login')
    }, 400)
  } catch (error) {
    console.error('退出登录失败:', error)
    ElMessage.error('退出失败，请稍后重试')
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
/* 所有样式完全保留，与原版一模一样 */
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

.fullscreen-projects {
  margin-top: 70px;
  padding: 15px;
  height: calc(100vh - 70px);
  overflow-y: auto;
  background-color: #f9f9f9;
}

.projects-container {
  max-width: 2000px;
  margin-left: 0;
  margin-right: auto;
}

.projects-search {
  position: relative;
  margin-bottom: 15px;
  max-width: 350px;
}

.projects-search input {
  width: 100%;
  padding: 8px 12px 8px 35px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9rem;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #9478cc;
}

.projects-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.projects-tabs button {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0.9rem;
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
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 15px;
  margin-bottom: 25px;
}

.project-card {
  background: white;
  border-radius: 6px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s, box-shadow 0.2s;
  display: flex;
  flex-direction: column;
  position: relative;
  cursor: pointer;
  border-left: 3px solid #9478cc;
  height: 150px;
}

.project-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.project-icon {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 10px;
}

.project-info h3 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 1rem;
}

.project-info p {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 0.8rem;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.project-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.7rem;
  color: #999;
  margin-top: auto;
}

.project-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.add-project {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  border: 2px dashed #ddd;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  color: #9478cc;
  height: 150px;
}

.add-project:hover {
  border-color: #9478cc;
  background: rgba(148, 120, 204, 0.03);
}

.add-project span {
  margin-top: 8px;
  font-weight: 500;
  font-size: 0.9rem;
}

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
  border-radius: 6px;
  width: 85%;
  max-width: 450px;
  padding: 15px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.dialog h3 {
  margin-top: 0;
  color: #333;
  font-size: 1.1rem;
}

.dialog-content {
  margin: 15px 0;
}

.form-group {
  margin-bottom: 10px;
}

.form-group label {
  display: block;
  margin-bottom: 3px;
  font-weight: 500;
  color: #555;
  font-size: 0.8rem;
  font-family: Arial, sans-serif;
}

.form-group input,
.form-group textarea {
  width: 96%;
  padding: 6px 10px;
  border: 1px solid #ddd;
  border-radius: 3px;
  font-size: 0.8rem;
  font-family: Arial, sans-serif;
}

.form-group textarea {
  min-height: 60px;
  resize: vertical;
}

.icon-selector {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 8px;
}

.icon-selector button {
  background: #f5f5f5;
  border: none;
  width: 35px;
  height: 35px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #666;
  font-size: 1rem;
}

.icon-selector button.selected {
  background: #9478cc;
  color: white;
}

.color-selector {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 8px;
}

.color-selector div {
  width: 25px;
  height: 25px;
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
  gap: 8px;
}

.dialog-actions button {
  padding: 6px 12px;
  border-radius: 3px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.8rem;
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

/* 响应式 */
@media (max-width: 768px) {
  .projects-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }

  .projects-tabs {
    overflow-x: auto;
    padding-bottom: 8px;
  }

  .projects-tabs button {
    white-space: nowrap;
  }

  .project-card,
  .add-project {
    height: 130px;
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