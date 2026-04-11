<template>
  <el-dialog
    v-model="dialogVisible"
    title="项目权限管理"
    width="700px"
    :close-on-click-modal="false"
    @open="handleOpen"
  >
    <!-- 项目信息卡片 -->
    <el-card shadow="never" class="project-info-card">
      <template #header>
        <div class="card-header">
          <el-icon><Folder /></el-icon>
          <span>项目信息</span>
        </div>
      </template>
      <el-descriptions :column="2" size="default">
        <el-descriptions-item label="项目名称" :span="2">
          <el-tag type="primary" size="large">{{ project?.name }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="项目描述" :span="2">
          {{ project?.description || '暂无描述' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 已授权用户列表 -->
    <el-card shadow="never" class="permission-list-card">
      <template #header>
        <div class="card-header">
          <el-icon><User /></el-icon>
          <span>已授权用户</span>
        </div>
      </template>
      <el-table
        :data="userPermissions"
        style="width: 100%"
        empty-text="暂无授权用户"
      >
        <el-table-column prop="username" label="用户名" min-width="150" />
        <el-table-column label="用户 ID" min-width="220">
          <template #default="scope">
            <el-tag size="small">{{ scope.row.user_id }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="权限类型" width="180">
          <template #default="scope">
            <el-select
              v-model="scope.row.permission_type"
              size="small"
              @change="updatePermission(scope.row)"
            >
              <el-option label="读写" value="write" />
              <el-option label="管理" value="admin" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button
              type="danger"
              size="small"
              @click="removePermission(scope.row)"
            >
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加用户 -->
    <el-card shadow="never" class="add-user-card">
      <template #header>
        <div class="card-header">
          <el-icon><Plus /></el-icon>
          <span>添加用户</span>
        </div>
      </template>
      <el-form :inline="true">
        <el-form-item label="选择用户">
          <el-select
            v-model="selectedUserId"
            placeholder="请选择用户"
            filterable
            style="width: 200px"
          >
            <el-option
              v-for="user in availableUsers"
              :key="user.id"
              :label="user.username"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="权限类型">
          <el-select v-model="selectedPermission" style="width: 120px">
            <el-option label="读写" value="write" />
            <el-option label="管理" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="addPermission">
            <el-icon><CirclePlus /></el-icon>
            添加
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
/* eslint-disable vue/no-unused-vars */
/* eslint-disable no-undef */
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Folder, User, Plus, CirclePlus } from '@element-plus/icons-vue'
import axios from 'axios'

const props = defineProps({
  project: Object,
  visible: Boolean
})

const emit = defineEmits(['update:visible'])

const dialogVisible = ref(props.visible)
const userPermissions = ref([])
const availableUsers = ref([])
const selectedUserId = ref('')
const selectedPermission = ref('write')
const loading = ref(false)

// 获取项目权限列表
const loadPermissions = async () => {
  if (!props.project?.id) return
  try {
    const res = await axios.get(`/api/permission/project/${props.project.id}`)
    userPermissions.value = res.data.data || []
  } catch (error) {
    ElMessage.error('获取权限列表失败：' + error.message)
  }
}

// 获取所有用户
const loadUsers = async () => {
  try {
    const res = await axios.get('/api/user/alluser')
    availableUsers.value = res.data || []
  } catch (error) {
    ElMessage.error('获取用户列表失败：' + error.message)
  }
}

// 添加权限
const addPermission = async () => {
  if (!selectedUserId.value) {
    ElMessage.warning('请选择用户')
    return
  }

  loading.value = true
  try {
    const res = await axios.post('/api/permission/grant', {
      projectId: props.project.id,
      userId: selectedUserId.value,
      permissionType: selectedPermission.value
    })

    if (res.data.code === 200) {
      ElMessage.success('添加成功')
      selectedUserId.value = ''
      loadPermissions()
    } else {
      ElMessage.warning(res.data.msg || '添加失败')
    }
  } catch (error) {
    ElMessage.error('添加失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 更新权限
const updatePermission = async (row) => {
  try {
    const res = await axios.post('/api/permission/update', {
      projectId: props.project.id,
      userId: row.user_id,
      permissionType: row.permission_type
    })

    if (res.data.code === 200) {
      ElMessage.success('更新成功')
    } else {
      ElMessage.error(res.data.msg || '更新失败')
      loadPermissions()
    }
  } catch (error) {
    ElMessage.error('更新失败：' + error.message)
    loadPermissions()
  }
}

// 移除权限
const removePermission = async (row) => {
  try {
    const res = await axios.post('/api/permission/revoke', {
      projectId: props.project.id,
      userId: row.user_id
    })

    if (res.data.code === 200) {
      ElMessage.success('移除成功')
      loadPermissions()
    } else {
      ElMessage.error(res.data.msg || '移除失败')
    }
  } catch (error) {
    ElMessage.error('移除失败：' + error.message)
  }
}

// Dialog 打开时加载数据
const handleOpen = () => {
  loadPermissions()
  loadUsers()
}

// 监听外部 visible 变化
watch(() => props.visible, (val) => {
  dialogVisible.value = val
  if (val) {
    loadPermissions()
    loadUsers()
  }
}, { immediate: true })

// 监听 dialog 关闭
watch(dialogVisible, (val) => {
  emit('update:visible', val)
})
</script>

<style scoped>
.project-info-card,
.permission-list-card,
.add-user-card {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 14px;
  color: #303133;
}

:deep(.el-descriptions-item__label) {
  font-weight: 500;
}

:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}
</style>
