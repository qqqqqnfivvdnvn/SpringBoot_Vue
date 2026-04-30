<template>
  <AdminLayout ref="layoutRef" :config="layoutConfig" />
</template>

<script setup>
import { ref, computed } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
const layoutRef = ref(null)

// ============ 主题颜色配置 ============
// 亮色模式 - 紫色系
const lightThemeColors = {
  primary: '#9478cc',
  dark: '#7d60b8',
  light: '#b89ff0'
}

// 暗色模式 - 深紫色系
const darkThemeColors = {
  primary: '#a488dc',
  dark: '#8d70c8',
  light: '#c8aff0'
}

// ============ 布局配置 ============
const layoutConfig = computed(() => ({
  brandTitle: '豪森主数据',
  homeRoute: 'HaoSenHome',
  menus: [
    {
      key: 'mainDataManagement',
      title: '主数据',
      icon: ['fas', 'th-list'],
      items: [
        { title: '医院主数据', action: 'showHospitalData', icon: ['fas', 'hospital-alt'] },
        { title: '药店主数据', action: 'showDrugStoreData', icon: ['fas', 'store-alt'] },
        { title: '商业主数据', action: 'companyDataView', icon: ['fas', 'synagogue'] }
      ]
    },
    {
      key: 'cleanManagement',
      title: '数据清洗',
      icon: ['fas', 'oil-can'],
      items: [
        { title: '清洗查看', action: 'selectCleanData', icon: ['fas', 'paw'] },
        { title: '清洗提交', action: 'importCleanData', icon: ['fas', 'eraser'] },
        { title: '线下编码', action: 'hsOfflineView', icon: ['fas', 'barcode'] },
        { title: '返回编码', action: 'hsBackCodeView', icon: ['fas', 'reply'] }
      ]
    },
    {
      key: 'appealManagement',
      title: '数据申诉',
      icon: ['fas', 'flag'],
      items: [
        { title: '申诉查看', action: 'showAppealData', icon: ['fas', 'book-reader'] },
        { title: '申诉提交', action: 'importAppealData', icon: ['fas', 'cat'] }
      ]
    },
    {
      key: 'updateManagement',
      title: '数据更新',
      icon: ['fas', 'file-alt'],
      items: [
        { title: '更新提交', action: 'importUpdateData', icon: ['fas', 'snowman'] }
      ]
    },
    {
      key: 'repeatDataManagement',
      title: '数据核查',
      icon: ['fas', 'feather'],
      items: [
        { title: '重复数据查看', action: 'showRepeatData', icon: ['fas', 'paper-plane'] },
        { title: '客户确认数据', action: 'showExportHSData', icon: ['fas', 'leaf'] },
        { title: '确认数据上传', action: 'importExportHSData', icon: ['fas', 'upload'] }
      ]
    },
    {
      key: 'customerDataManagement',
      title: '客户数据',
      icon: ['fas', 'users'],
      items: [
        { title: '名称地址变更', action: 'nameAddrChangeView', icon: ['fas', 'edit'] },
        { title: '互联网医院', action: 'internetHosView', icon: ['fas', 'hospital'] },
        { title: '医疗机构大类', action: 'bigClassifyView', icon: ['fas', 'layer-group'] },
        { title: '医联体编码', action: 'hsMedicalAllianceView', icon: ['fas', 'hospital-user'] },
        { title: '医共体编码', action: 'hsMedicalCommunityView', icon: ['fas', 'hospital-user'] }
      ]
    },
    {
      key: 'branchCodeManagement',
      title: '总分院店',
      icon: ['fas', 'sitemap'],
      items: [
        { title: '总分店编码', action: 'drugBranchCodeView', icon: ['fas', 'store-alt'] },
        { title: '总分院编码', action: 'hosBranchCodeView', icon: ['fas', 'hospital-alt'] }
      ]
    }
  ],
  defaultView: 'HomeDashboardView',
  lightThemeColors,
  darkThemeColors,
  actions: {
    showHospitalData: () => {
      layoutRef.value?.addTab('医院主数据', 'HospitalDataView', '/haosen/database/hospital')
    },
    showDrugStoreData: () => {
      layoutRef.value?.addTab('药店主数据', 'DrugStoreDataView', '/haosen/database/drugstore')
    },
    companyDataView: () => {
      layoutRef.value?.addTab('商业主数据', 'CompanyDataView', '/haosen/database/company')
    },
    nameAddrChangeView: () => {
      layoutRef.value?.addTab('名称地址变更', 'NameAddrChangeView', '/haosen/customerdata/nameaddrchange')
    },
    internetHosView: () => {
      layoutRef.value?.addTab('互联网医院', 'InternetHosView', '/haosen/customerdata/internethos')
    },
    bigClassifyView: () => {
      layoutRef.value?.addTab('医疗机构大类', 'BigClassifyView', '/haosen/customerdata/bigclassify')
    },
    drugBranchCodeView: () => {
      layoutRef.value?.addTab('总分店编码', 'DrugBranchCodeView', '/haosen/customerdata/drugbranchcode')
    },
    hosBranchCodeView: () => {
      layoutRef.value?.addTab('豪森总分院编码', 'HosBranchCodeView', '/haosen/customerdata/hosbranchcode')
    },
    hsOfflineView: () => {
      layoutRef.value?.addTab('线下编码', 'HsOfflineView', '/haosen/cleandata/hscode')
    },
    hsMedicalAllianceView: () => {
      layoutRef.value?.addTab('医联体编码', 'HsMedicalAllianceView', '/haosen/customerdata/hsmedicalalliance')
    },
    hsMedicalCommunityView: () => {
      layoutRef.value?.addTab('医共体编码', 'HsMedicalCommunityView', '/haosen/customerdata/hsmedicalcommunity')
    },
    hsBackCodeView: () => {
      layoutRef.value?.addTab('返回编码', 'HsBackCodeView', '/haosen/cleandata/backhscode')
    },
    showAppealData: () => {
      layoutRef.value?.addTab('申诉数据', 'AppealDataView', '/haosen/appealdata/list')
    },
    importAppealData: () => {
      layoutRef.value?.addTab('申诉提交', 'ImportAppealDataView', '/haosen/appealdata/importappealdata')
    },
    selectCleanData: () => {
      layoutRef.value?.addTab('清洗查看', 'SelectCleanDataView', '/haosen/cleandata/selectcleandata')
    },
    importCleanData: () => {
      layoutRef.value?.addTab('清洗提交', 'ImportCleanDataView', '/haosen/cleandata/importcleandata')
    },
    importUpdateData: () => {
      layoutRef.value?.addTab('更新提交', 'ImportUpdateDataView', '/haosen/updatedata/importupdatedata')
    },
    showRepeatData: () => {
      layoutRef.value?.addTab('重复数据查看', 'GetRepeatDataView', '/haosen/repeatdata/getrepeatdata')
    },
    showExportHSData: () => {
      layoutRef.value?.addTab('客户确认数据', 'GetExportHSDataView', '/haosen/repeatdata/getexporthsdata')
    },
    importExportHSData: () => {
      layoutRef.value?.addTab('确认数据上传', 'ImportExportHSDataView', '/haosen/repeatdata/importexporthsdata')
    }
  }
}))


</script>

<style scoped>
/* 引入豪森项目主题样式 */
@import '@/assets/css/haosen.css';
</style>
