<template>
  <AdminLayout ref="layoutRef" :config="layoutConfig" />
</template>

<script setup>
import { ref, computed } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'

const layoutRef = ref(null)

// ============ 主题颜色配置 ============
// 亮色模式 - 蓝色系
const lightThemeColors = {
  primary: '#6ab7ff',
  dark: '#4a97e0',
  light: '#8ad7ff'
}

// 暗色模式 - 深蓝色系
const darkThemeColors = {
  primary: '#5aa7ef',
  dark: '#3a87d0',
  light: '#7ac7ff'
}

// ============ 布局配置 ============
const layoutConfig = computed(() => ({
  brandTitle: '恒瑞主数据',
  homeRoute: 'HengRuiHome',
  menus: [
    {
      key: 'batchManagement',
      title: '批次管理',
      icon: ['fas', 'layer-group'],
      items: [
        { title: '批次列表', action: 'showBatchData', icon: ['fas', 'list'] },
        { title: '批次导入', action: 'importBatchData', icon: ['fas', 'upload'] }
      ]
    },
    {
      key: 'monitoringManagement',
      title: '数据汇总',
      icon: ['fas', 'table'],
      items: [
        { title: '汇总数据', action: 'showMonitoringData', icon: ['fas', 'database'] }
      ]
    },
    {
      key: 'relationManagement',
      title: '数据比对关系',
      icon: ['fas', 'link'],
      items: [
        { title: '比对关系', action: 'showRelationData', icon: ['fas', 'exchange-alt'] },
        { title: '关系导入', action: 'importRelationData', icon: ['fas', 'upload'] }
      ]
    }
  ],
  defaultView: 'HrBatchDataView',
  lightThemeColors,
  darkThemeColors,
  actions: {
    showBatchData: () => {
      layoutRef.value?.addTab('批次列表', 'HrBatchDataView', '/hengrui/matchedaddress/batchData')
    },
    importBatchData: () => {
      layoutRef.value?.addTab('批次导入', 'HrImportBatchDataView', '/hengrui/matchedaddress/importBatch')
    },
    showMonitoringData: () => {
      layoutRef.value?.addTab('数据汇总', 'HrMonitoringDataView', '/hengrui/matchedaddress/monitoringData')
    },
    showRelationData: () => {
      layoutRef.value?.addTab('比对关系', 'HrOrgRelationView', '/hengrui/matchedaddress/relationData')
    },
    importRelationData: () => {
      layoutRef.value?.addTab('关系导入', 'HrImportOrgRelationView', '/hengrui/matchedaddress/importrelation')
    }
  }
}))
</script>

<style scoped>
/* 引入恒瑞项目主题样式 */
@import '@/assets/css/hengrui.css';
</style>
