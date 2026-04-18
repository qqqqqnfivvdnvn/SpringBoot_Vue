<template>
  <div class="monitoring-data-view">
    <!-- ==================== 主容器：整合搜索和数据区域 ==================== -->
    <div class="integrated-container">

      <!-- ==================== 搜索区域：固定在顶部 ==================== -->
      <div class="fixed-search">
        <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent="handleSearch">
          <!-- 搜索条件：支持多字段筛选 -->
          <div class="search-conditions">
            <el-form-item label="批次 ID">
              <el-input v-model="searchForm.batchId" placeholder="请输入批次 ID" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="商品名">
              <el-input v-model="searchForm.productName" placeholder="请输入商品名" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="标准名称">
              <el-input v-model="searchForm.name" placeholder="请输入标准名称" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="营业执照名称">
              <el-input v-model="searchForm.businessLicenseName" placeholder="请输入营业执照名称" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="KeyID">
              <el-input v-model="searchForm.keyId" placeholder="请输入 KeyID" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="省份">
              <el-input v-model="searchForm.province" placeholder="请输入省份" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="地址">
              <el-input v-model="searchForm.address" placeholder="请输入地址" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="创建日期">
              <el-date-picker
                  v-model="searchForm.createTime"
                  type="date"
                  placeholder="选择创建日期"
                  value-format="YYYY-MM-DD"
                  clearable
                  @clear="handleSearch"
                  @change="handleSearch"
              />
            </el-form-item>
          </div>

          <!-- 操作按钮区域：查询、重置、导出、视图切换 -->
          <div class="form-actions-wrapper">
            <div class="form-actions">
              <el-button size="small" type="primary" @click="handleSearch" :loading="loading">查询</el-button>
              <el-button size="small" @click="resetSearch">重置</el-button>
              <el-button size="small" type="success" @click="toExcel" :loading="exporting">
                {{ exporting ? '导出中...' : '导出' }}
              </el-button>

              <el-button-group size="small" class="view-toggle">
                <el-button :type="viewMode === 'table' ? 'primary' : 'default'" @click="viewMode = 'table'" title="表格视图"><el-icon><Grid /></el-icon></el-button>
                <el-button :type="viewMode === 'card' ? 'primary' : 'default'" @click="viewMode = 'card'" title="卡片视图"><el-icon><CopyDocument /></el-icon></el-button>
              </el-button-group>
            </div>
          </div>
        </el-form>
      </div>

      <!-- ==================== 数据展示区域：表格/卡片视图 ==================== -->
      <div class="data-content" v-loading="loading">
        <!-- 表格视图 -->
        <div v-if="viewMode === 'table'" class="table-container">
          <el-table
              v-if="monitoringData.list?.length"
              :data="monitoringData.list"
              height="100%"
              stripe
              border
              fit
              resizable
          >
            <el-table-column prop="serialNo" label="序号" min-width="60" />
            <el-table-column prop="batchId" label="批次 ID" min-width="100" show-overflow-tooltip>
              <template #default="{ row }">
                <el-link type="primary" :underline="false" @click="copyText(row.batchId)" :disabled="!row.batchId">
                  {{ row.batchId }}
                </el-link>
              </template>
            </el-table-column>
            <el-table-column prop="productName" label="商品名" min-width="120" show-overflow-tooltip />
            <el-table-column prop="productId" label="商品 ID" min-width="80" show-overflow-tooltip />
            <el-table-column prop="genericName" label="通用名" min-width="120" show-overflow-tooltip />
            <el-table-column prop="platform" label="平台" min-width="80" />
            <el-table-column prop="specification" label="规格" min-width="100" show-overflow-tooltip />
            <el-table-column prop="onlineStorePrice" label="网店价格" min-width="90" />
            <el-table-column prop="boxQuantity" label="盒数" min-width="70" />
            <el-table-column prop="unitPricePerBox" label="单盒价" min-width="70" />
            <el-table-column prop="storeName" label="店铺名称" min-width="150" show-overflow-tooltip />
            <el-table-column prop="businessLicenseName" label="营业执照名称" min-width="150" show-overflow-tooltip />
            <el-table-column prop="province" label="省" min-width="75" />
            <el-table-column prop="city" label="市" min-width="75" />
            <el-table-column prop="salesVolume" label="销量" min-width="80" show-overflow-tooltip />
            <el-table-column prop="keyId" label="KeyID" min-width="100" show-overflow-tooltip>
              <template #default="{ row }">
                <el-link type="primary" :underline="false" @click="copyText(row.keyId)" :disabled="!row.keyId">
                  {{ row.keyId }}
                </el-link>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="标准名称" min-width="120" show-overflow-tooltip />
            <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip />
            <el-table-column prop="remarks" label="备注" min-width="100" show-overflow-tooltip />
          </el-table>
          <div v-else class="no-data-container">
            <el-empty description="没有找到匹配的数据" :image-size="120" />
          </div>
        </div>

        <!-- 卡片视图 -->
        <div v-else class="card-container">
          <el-scrollbar>
            <el-row :gutter="20" style="margin: 0; padding: 16px;">
              <el-col v-for="item in monitoringData.list" :key="item.id" :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                <el-card class="monitoring-card" shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <span class="card-title">{{ item.productName }}</span>
                      <el-tag type="info" size="small">{{ item.platform }}</el-tag>
                    </div>
                  </template>
                  <div class="card-body">
                    <div class="card-item"><span class="label">商品 ID：</span>{{ item.productId }}</div>
                    <div class="card-item">
                      <span class="label">批次 ID：</span>
                      <el-link type="primary" :underline="false" @click="copyText(item.batchId)" :disabled="!item.batchId">
                        {{ item.batchId }}
                      </el-link>
                    </div>
                    <div class="card-item"><span class="label">通用名：</span>{{ item.genericName }}</div>
                    <div class="card-item"><span class="label">规格：</span>{{ item.specification }}</div>
                    <div class="card-item"><span class="label">价格：</span>¥{{ item.onlineStorePrice }}</div>
                    <div class="card-item"><span class="label">盒数：</span>{{ item.boxQuantity }}</div>
                    <div class="card-item"><span class="label">单盒价：</span>¥{{ item.unitPricePerBox }}</div>
                    <div class="card-item"><span class="label">店铺：</span>{{ item.storeName }}</div>
                    <div class="card-item"><span class="label">营业执照：</span>{{ item.businessLicenseName }}</div>
                    <div class="card-item"><span class="label">地区：</span>{{ item.province }} {{ item.city }}</div>
                    <div class="card-item"><span class="label">销量：</span>{{ item.salesVolume }}</div>
                    <div class="card-item"><span class="label">标准名称：</span>{{ item.name }}</div>
                    <div class="card-item"><span class="label">地址：</span>{{ item.address }}</div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <el-empty v-if="!monitoringData.list?.length" description="没有找到匹配的数据" />
          </el-scrollbar>
        </div>
      </div>

      <!-- ==================== 分页组件：固定在底部 ==================== -->
      <div class="fixed-pagination" v-if="monitoringData.list?.length">
        <div class="pagination-content">
          <el-button
              size="small"
              plain
              class="page-btn"
              :disabled="monitoringData.isFirstPage"
              @click="pageNumber > 1 && (pageNumber--, fetchMonitoringData())"
          >
            上一页
          </el-button>

          <div class="page-jumper">
            <span>跳转到</span>
            <el-input-number
                v-model="jumpPageNumber"
                :min="1"
                :max="monitoringData.pages"
                size="small"
                controls-position="right"
                @change="handleJumpPage"
                class="page-input"
            />
            <span class="page-total">页，共 {{ monitoringData.pages }} 页 ({{ monitoringData.total }} 条)</span>
          </div>

          <el-button
              size="small"
              plain
              class="page-btn"
              :disabled="monitoringData.isLastPage"
              @click="pageNumber < monitoringData.pages && (pageNumber++, fetchMonitoringData())"
          >
            下一页
          </el-button>

          <el-select v-model="pageSize" size="small" class="size-select" @change="handlePageSizeChange">
            <el-option :value="20" label="每页 20 条" />
            <el-option :value="40" label="每页 40 条" />
            <el-option :value="60" label="每页 60 条" />
          </el-select>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Grid, CopyDocument } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const loading = ref(false)
const exporting = ref(false)
const pageNumber = ref(1)
const pageSize = ref(20)
const jumpPageNumber = ref(1)
const viewMode = ref('table')

const searchForm = reactive({
  batchId: '', productName: '', name: '', businessLicenseName: '',
  keyId: '', province: '', address: '', createTime: ''
})

const monitoringData = reactive({
  list: [], pageNum: 1, pages: 1, total: 0, isFirstPage: true, isLastPage: true
})

const copyText = async (text) => {
  if (!text) return
  const textToCopy = String(text)
  try {
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(textToCopy)
      ElMessage.success('已复制')
      return
    }
    const textArea = document.createElement('textarea')
    textArea.value = textToCopy
    textArea.style.position = 'fixed'
    textArea.style.left = '-9999px'
    textArea.style.top = '0'
    document.body.appendChild(textArea)
    textArea.focus()
    textArea.select()
    const successful = document.execCommand('copy')
    document.body.removeChild(textArea)
    if (successful) {
      ElMessage.success('已复制')
    } else {
      ElMessage.error('复制失败')
    }
  } catch (err) {
    console.error('复制失败:', err)
    ElMessage.error('复制失败')
  }
}

const fetchMonitoringData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pageNumber.value, pageSize: pageSize.value, ...searchForm }
    const response = await axios.get('/api/hengrui/monitoring/getDataList', { params })
    if (response.data.code === 200 && response.data.data) {
      const page = response.data.data
      monitoringData.list = page.list || []
      monitoringData.pageNum = page.pageNum
      monitoringData.pages = page.pages
      monitoringData.total = page.total
      monitoringData.isFirstPage = page.isFirstPage
      monitoringData.isLastPage = page.isLastPage
      jumpPageNumber.value = page.pageNum
    } else {
      ElMessage.error(response.data.message || '获取数据失败')
    }
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNumber.value = 1
  jumpPageNumber.value = 1
  fetchMonitoringData()
}

// 跳转到指定页
const handleJumpPage = () => {
  if (jumpPageNumber.value >= 1 && jumpPageNumber.value <= monitoringData.pages) {
    pageNumber.value = jumpPageNumber.value
    fetchMonitoringData()
  }
}

// 重置搜索条件
const resetSearch = () => {
  searchForm.batchId = ''
  searchForm.productName = ''
  searchForm.name = ''
  searchForm.businessLicenseName = ''
  searchForm.keyId = ''
  searchForm.province = ''
  searchForm.address = ''
  searchForm.createTime = ''
  pageNumber.value = 1
  fetchMonitoringData()
}

// 每页条数改变处理
const handlePageSizeChange = () => {
  pageNumber.value = 1
  fetchMonitoringData()
}

const toExcel = async () => {
  exporting.value = true
  try {
    const response = await axios.get('/api/hengrui/monitoring/exportData', {
      params: searchForm,
      responseType: 'blob'
    })
    // 【微调1】显式指定 MIME 类型，更严谨
    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `恒瑞数据汇总_${new Date().getTime()}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)  // 【微调2】显式释放内存
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

onMounted(() => {
  if (route.query.batchId) searchForm.batchId = route.query.batchId
  fetchMonitoringData()
})
</script>

<style scoped>
/* ==================== 1. 基础布局 ==================== */
.monitoring-data-view {
  display: flex;
  flex-direction: column;
  height: 100vh;
  /* 【微调3】恢复宽屏宽度，从 1250px 改回 1600px */
  max-width: min(1600px, 95vw);
  margin: 0 auto;
  background: #ffffff;
  overflow: hidden;
  font-size: 14px;
}

.integrated-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  margin: 8px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* ==================== 2. 搜索区域 ==================== */
.fixed-search {
  flex-shrink: 0;
  padding: 14px 18px 10px;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  border-radius: 6px 6px 0 0;
}

.search-form {
  display: flex;
  flex-direction: column;
}

/* 搜索条件区域 */
.search-conditions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 8px;
  align-items: flex-end;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0 !important;
  flex: 1 1 200px;
  min-width: 200px;
}

.search-form :deep(.el-form-item__label),
.search-form :deep(.el-input__inner) {
  font-size: 14px !important;
  color: #606266;
}

.form-actions-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.form-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.view-toggle {
  margin-left: 16px;
}

/* ==================== 3. 数据展示区域 ==================== */
.data-content {
  flex: 1;
  height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.table-container {
  flex: 1;
  height: 100%;
}

:deep(.el-table) {
  font-size: 14px !important;
}

:deep(.el-table th.el-table__cell) {
  background-color: var(--el-table-header-bg-color, #f8f9fb);
  color: var(--el-text-color-primary, #303133);
  font-weight: 600;
  height: 48px;
}

/* ==================== 4. 分页区域 ==================== */
.fixed-pagination {
  flex-shrink: 0;
  background: #fff;
  padding: 12px;
  border-top: 1px solid #ebeef5;
  text-align: center;
}

.pagination-content {
  display: inline-flex;
  align-items: center;
  gap: 16px;
}

.page-jumper {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #606266;
}

.page-input {
  width: 90px !important;
  margin: 0 8px;
}

.page-input :deep(.el-input__wrapper) {
  padding-left: 10px;
  padding-right: 35px;
}

.page-btn {
  border-radius: 4px;
  padding: 0 15px;
  height: 32px;
  font-size: 12px;
}

.size-select {
  width: 110px !important;
  font-size: 12px;
}

.page-total {
  margin-left: 4px;
}

/* ==================== 5. 卡片视图 ==================== */
.card-container {
  flex: 1;
  overflow: hidden;
}

.monitoring-card {
  height: 100%;
  font-size: 14px;
  margin-bottom: 16px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.monitoring-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.card-header :deep(.el-tag) {
  flex-shrink: 0;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow: auto;
}

.card-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 14px;
  word-break: break-all;
}

.card-item .label {
  color: #909399;
  min-width: 80px;
  flex-shrink: 0;
}

.no-data-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* ==================== 6. 响应式适配 ==================== */
@media (min-width: 2000px) {
  .monitoring-data-view { max-width: min(1920px, 95vw); }
}

@media (min-width: 2600px) {
  .monitoring-data-view { max-width: min(2560px, 95vw); }
}

/* ==================== 7. 暗色模式样式 ==================== */
html.dark .monitoring-data-view,
.dark .monitoring-data-view {
  background: var(--el-bg-color, #1a1a2c);
}

.dark .integrated-container {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-color: var(--el-border-color, #3a3a4a) !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
}

.dark .fixed-search {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-bottom-color: var(--el-border-color, #3a3a4a) !important;
}

.dark .search-form :deep(.el-form-item__label) {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

.dark .search-form :deep(.el-input__wrapper) {
  background-color: var(--el-input-bg-color, #2a2a3a) !important;
  box-shadow: 0 0 0 1px var(--el-input-border-color, #3a3a4a) inset !important;
}

.dark .search-form :deep(.el-picker__wrapper),
.dark .search-form :deep(.el-date-editor .el-input__wrapper) {
  background-color: var(--el-input-bg-color, #2a2a3a) !important;
  box-shadow: 0 0 0 1px var(--el-input-border-color, #3a3a4a) inset !important;
}

.dark :deep(.el-table) {
  background-color: var(--el-bg-color, #1a1a2c) !important;
}

.dark :deep(.el-table__header tr),
.dark :deep(.el-table__header tr th.el-table__cell),
.dark :deep(.el-table thead tr th) {
  background-color: var(--el-fill-color-light, #2a2a3a) !important;
  color: var(--el-text-color-regular, #e0e0e0) !important;
  border-color: var(--el-border-color, #3a3a4a) !important;
}

.dark :deep(.el-table__body tr.el-table__row > td),
.dark :deep(.el-table tbody tr td.el-table__cell) {
  background-color: var(--el-bg-color, #1a1a2c) !important;
  color: var(--el-text-color-regular, #d0d0d0) !important;
  border-color: var(--el-border-color, #3a3a4a) !important;
}

.dark :deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: var(--el-fill-color-lighter, #232330) !important;
}

.dark :deep(.el-table__body tr:hover > td) {
  background-color: var(--el-fill-color-light, #2a2a3a) !important;
}

.dark .fixed-pagination {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-top-color: var(--el-border-color, #3a3a4a) !important;
}

.dark .page-jumper {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

.dark .monitoring-card {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-color: var(--el-border-color, #3a3a4a) !important;
}

.dark .card-header {
  border-bottom-color: var(--el-border-color, #3a3a4a) !important;
}

.dark .card-title {
  color: var(--el-text-color-regular, #e0e0e0) !important;
}

.dark .card-item .label {
  color: var(--el-text-color-secondary, #a0a0a0) !important;
}

.dark .card-item {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

.dark .no-data-container :deep(.el-empty__description) {
  color: var(--el-text-color-secondary, #a0a0a0) !important;
}
</style>