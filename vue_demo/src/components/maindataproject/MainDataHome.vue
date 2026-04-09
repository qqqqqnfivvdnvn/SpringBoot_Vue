<template>
  <AdminLayout
    ref="adminLayoutRef"
    :project-config="projectConfig"
    :menu-config="menuConfig"
    :menu-handlers="menuHandlers"
    default-view="MdLocationBatchView"
  />
</template>

<script setup>
import { ref, computed } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import {
  mainDataProjectConfig,
  mainDataMenuConfig,
  createMainDataMenuHandlers
} from '@/config/menu/mainDataMenu'

// 项目配置
const projectConfig = mainDataProjectConfig

// 菜单配置
const menuConfig = mainDataMenuConfig

// 组件引用
const adminLayoutRef = ref(null)

// 菜单处理函数（使用 computed 确保 adminLayoutRef 准备好后创建）
const menuHandlers = computed(() => {
  const api = adminLayoutRef.value || {
    addTab: () => {},
    showToastMessage: () => {}
  }
  return createMainDataMenuHandlers(api)
})
</script>
