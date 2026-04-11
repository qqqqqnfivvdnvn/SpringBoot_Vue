<template>
  <div class="permission-test-page">
    <h2>权限管理测试页面</h2>

    <div class="section">
      <h3>1. 选择项目</h3>
      <el-select v-model="selectedProjectId" placeholder="选择项目" filterable @change="loadPermissions">
        <el-option
          v-for="project in projects"
          :key="project.id"
          :label="project.name"
          :value="project.id"
        />
      </el-select>
    </div>

    <div class="section" v-if="selectedProjectId">
      <h3>2. 当前权限列表</h3>
      <el-table :data="permissions" style="width: 100%">
        <el-table-column prop="user_id" label="用户 ID" />
        <el-table-column prop="permission_type" label="权限类型">
          <template #default="scope">
            <el-tag :type="scope.row.permission_type === 'admin' ? 'danger' : 'warning'">
              {{ scope.row.permission_type === 'admin' ? '管理' : '读写' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="danger" size="small" @click="revokePermission(scope.row)">
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="section" v-if="selectedProjectId">
      <h3>3. 添加权限</h3>
      <el-form :inline="true">
        <el-form-item label="用户">
          <el-select v-model="selectedUserId" placeholder="选择用户" filterable>
            <el-option
              v-for="user in users"
              :key="user.id"
              :label="user.username"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="权限">
          <el-select v-model="selectedPermission">
            <el-option label="读写" value="write" />
            <el-option label="管理" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="addPermission">添加</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="section">
      <h3>4. 操作日志</h3>
      <el-input
        v-model="logText"
        type="textarea"
        :rows="10"
        readonly
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const projects = ref([])
const users = ref([])
const selectedProjectId = ref('')
const selectedUserId = ref('')
const selectedPermission = ref('write')
const permissions = ref([])
const logText = ref('')

const addLog = (msg) => {
  const time = new Date().toLocaleTimeString()
  logText.value += `[${time}] ${msg}\n`
}

const loadProjects = async () => {
  try {
    const res = await axios.get('/api/projects/getAllProjects')
    projects.value = res.data.data || []
    addLog(`加载项目列表成功，共 ${projects.value.length} 个项目`)
  } catch (error) {
    addLog(`加载项目列表失败：${error.message}`)
  }
}

const loadUsers = async () => {
  try {
    const res = await axios.get('/api/user/alluser')
    users.value = res.data || []
    addLog(`加载用户列表成功，共 ${users.value.length} 个用户`)
  } catch (error) {
    addLog(`加载用户列表失败：${error.message}`)
  }
}

const loadPermissions = async () => {
  if (!selectedProjectId.value) return
  try {
    const res = await axios.get(`/api/permission/project/${selectedProjectId.value}`)
    permissions.value = res.data.data || []
    addLog(`加载权限列表成功，共 ${permissions.value.length} 条记录`)
  } catch (error) {
    addLog(`加载权限列表失败：${error.message}`)
  }
}

const addPermission = async () => {
  if (!selectedUserId.value || !selectedProjectId.value) {
    addLog('请选择用户和项目')
    return
  }
  try {
    const res = await axios.post('/api/permission/grant', {
      projectId: selectedProjectId.value,
      userId: selectedUserId.value,
      permissionType: selectedPermission.value
    })
    if (res.data.code === 200) {
      addLog(`授权成功：用户 ${selectedUserId.value} -> ${selectedPermission.value}`)
      loadPermissions()
    } else {
      addLog(`授权失败：${res.data.msg}`)
    }
  } catch (error) {
    addLog(`授权失败：${error.message}`)
  }
}

const revokePermission = async (row) => {
  try {
    const res = await axios.post('/api/permission/revoke', {
      projectId: selectedProjectId.value,
      userId: row.user_id
    })
    if (res.data.code === 200) {
      addLog(`移除权限成功：用户 ${row.user_id}`)
      loadPermissions()
    } else {
      addLog(`移除权限失败：${res.data.msg}`)
    }
  } catch (error) {
    addLog(`移除权限失败：${error.message}`)
  }
}

onMounted(() => {
  loadProjects()
  loadUsers()
  addLog('页面已加载')
})
</script>

<style scoped>
.permission-test-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.section {
  margin-bottom: 30px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.section h3 {
  margin-bottom: 15px;
  color: #303133;
}
</style>
