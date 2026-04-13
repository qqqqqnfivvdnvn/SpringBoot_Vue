<template>
  <AdminLayout ref="layoutRef" :config="layoutConfig" />
</template>

<script setup>
import { ref, computed } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'

const layoutRef = ref(null)

// ============ 主题颜色配置 ============
// 亮色模式 - 深绿色系
const lightThemeColors = {
  primary: '#90be6d',
  dark: '#7da85a',
  light: '#a3d180'
}

// 暗色模式 - 暗绿色系
const darkThemeColors = {
  primary: '#3d6b2f',
  dark: '#2d5222',
  light: '#4f8a3f'
}

// ============ 布局配置 ============
const layoutConfig = computed(() => ({
  brandTitle: '主数据管理',
  homeRoute: 'MainDataHome',
  menus: [
    {
      key: 'location',
      title: '区县经纬度',
      icon: ['fas', 'map-marked-alt'],
      items: [
        { title: '批次列表', action: 'showBatchList', icon: ['fas', 'list'] },
        { title: '文件上传', action: 'showUpload', icon: ['fas', 'upload'] },
        { title: '汇总数据', action: 'showLocationData', icon: ['fas', 'database'] }
      ]
    },
    {
      key: 'fuzzyMatch',
      title: '机构模糊匹配',
      icon: ['fas', 'search'],
      items: [
        { title: '批次列表', action: 'fuzzyBatchList', icon: ['fas', 'list'] },
        { title: '文件上传', action: 'fuzzyUpload', icon: ['fas', 'upload'] },
        { title: '汇总数据', action: 'fuzzySummary', icon: ['fas', 'table'] }
      ]
    }
  ],
  defaultView: 'MdLocationBatchView',
  lightThemeColors,
  darkThemeColors,
  actions: {
    showBatchList: () => {
      layoutRef.value?.addTab('批次列表', 'MdLocationBatchView', '/maindata/batch')
    },
    showUpload: () => {
      layoutRef.value?.addTab('文件上传', 'MdLocationUploadView', '/maindata/upload')
    },
    showLocationData: () => {
      layoutRef.value?.addTab('汇总数据', 'MdLocationView', '/maindata/location')
    },
    fuzzyBatchList: () => {
      layoutRef.value?.addTab('批次列表', 'MdFuzzyBatchView', '/maindata/fuzzyMatch/batch')
    },
    fuzzyUpload: () => {
      layoutRef.value?.addTab('文件上传', 'MdFuzzyUploadView', '/maindata/fuzzyMatch/upload')
    },
    fuzzySummary: () => {
      layoutRef.value?.addTab('汇总数据', 'MdFuzzySummaryView', '/maindata/fuzzyMatch/summary')
    }
  }
}))
</script>

<style scoped>
/* 引入主数据项目主题样式 */
@import '@/assets/css/maindata.css';
</style>
