<!-- eslint-disable -->
<template>
  <el-dialog
    v-model="dialogVisible"
    title="项目权限管理"
    width="600px"
    :close-on-click-modal="false"
  >
    <div class="project-info">
      <h4>{{ project.name }}</h4>
      <p>{{ project.description }}</p>
    </div>

    <div class="permission-list">
      <h5>已授权用户</h5>
      <el-table :data="userPermissions" style="width: 100%">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="permissionType" label="权限类型">
          <template #default="scope">
            <el-tag :type="getPermissionTagType(scope.row.permissionType)">
              {{ getPermissionLabel(scope.row.permissionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-select
              v-model="scope.row.permissionType"
              size="small"
              @change="updatePermission(scope.row)"
            >
              <el-option label="读写" value="write" />
              <el-option label="管理" value="admin" />
            </el-select>
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
    </div>

    <div class="add-user">
      <h5>添加用户</h5>
      <el-form :inline="true">
        <el-form-item label="用户">
          <el-select v-model="selectedUserId" placeholder="选择用户" filterable>
            <el-option
              v-for="user in availableUsers"
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

    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
// eslint-disable-next-line no-undef
// defineProps 和 defineEmits 是 Vue 3 编译器宏，在 <script setup> 中自动可用
import { ref, watch, onMounted } from 'vue'
import axios from 'axios'

const props = defineProps({
  project: Object,
  visible: Boolean
})

const emit = defineEmits(['update:visible', 'update:project'])

const dialogVisible = ref(props.visible)
const userPermissions = ref([])
const availableUsers = ref([])
const selectedUserId = ref('')
const selectedPermission = ref('write')

// 监听外部 visible 变化
watch(() => props.visible, (val) => {
  dialogVisible.value = val
  if (val && props.project) {
    loadPermissions()
    loadUsers()
  }
})

// 监听 dialog 关闭
watch(dialogVisible, (val) => {
  emit('update:visible', val)
})

// 获取权限标签颜色
const getPermissionTagType = (type) => {
  const map = { write: 'warning', admin: 'danger' }
  return map[type] || 'info'
}

// 获取权限标签文字
const getPermissionLabel = (type) => {
  const map = { write: '读写', admin: '管理', owner: '所有者' }
  return map[type] || type
}

// 获取项目权限列表
const loadPermissions = async () => {
  if (!props.project?.id) return
  try {
    const res = await axios.get(`/api/permission/project/${props.project.id}`)
    userPermissions.value = res.data.data || []
  } catch (error) {
    console.error('获取权限列表失败:', error)
  }
}

// 获取所有用户
const loadUsers = async () => {
  try {
    const res = await axios.get('/api/user/alluser')
    availableUsers.value = res.data || []
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
}

// 添加权限
const addPermission = async () => {
  if (!selectedUserId.value || !props.project?.id) {
    alert('请选择用户和项目')
    return
  }

  try {
    await axios.post('/api/permission/grant', {
      projectId: props.project.id,
      userId: selectedUserId.value,
      permissionType: selectedPermission.value
    })
    alert('添加成功')
    loadPermissions()
    selectedUserId.value = ''
  } catch (error) {
    alert('添加失败：' + (error.response?.data?.message || error.message))
  }
}

// 更新权限
const updatePermission = async (row) => {
  try {
    await axios.post('/api/permission/update', {
      projectId: props.project.id,
      userId: row.user_id,
      permissionType: row.permissionType
    })
    alert('更新成功')
  } catch (error) {
    alert('更新失败')
  }
}

// 移除权限
const removePermission = async (row) => {
  try {
    await axios.post('/api/permission/revoke', {
      projectId: props.project.id,
      userId: row.user_id
    })
    alert('移除成功')
    loadPermissions()
  } catch (error) {
    alert('移除失败')
  }
}

// 初始化加载
onMounted(() => {
  if (props.visible && props.project) {
    loadPermissions()
    loadUsers()
  }
})
</script>

<style scoped>
.project-info {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.project-info h4 {
  margin: 0 0 8px 0;
  color: #303133;
}

.project-info p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.permission-list,
.add-user {
  margin-bottom: 20px;
}

.permission-list h5,
.add-user h5 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 14px;
}
</style>
