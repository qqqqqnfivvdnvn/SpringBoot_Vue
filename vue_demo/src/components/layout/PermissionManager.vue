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
