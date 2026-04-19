<template>
  <div class="md-fuzzy-batch-view">
    <div class="integrated-container">
      <!-- 搜索区域 -->
      <div class="fixed-search">
        <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent="handleSearch">
          <div class="search-conditions">
            <el-form-item label="批次 ID">
              <el-input v-model="searchForm.batchId" placeholder="请输入批次 ID" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="searchForm.status" placeholder="请选择状态" clearable @clear="handleSearch">
                <el-option label="处理中" :value="0" />
                <el-option label="已处理" :value="1" />
                <el-option label="处理失败" :value="2" />
              </el-select>
            </el-form-item>
            <el-form-item label="开始时间">
              <el-date-picker
                  v-model="searchForm.startTime"
                  type="date"
                  placeholder="选择开始时间"
                  value-format="YYYY-MM-DD"
                  @change="handleSearch"
              />
            </el-form-item>
            <el-form-item label="结束时间">
              <el-date-picker
                  v-model="searchForm.endTime"
                  type="date"
                  placeholder="选择结束时间"
                  value-format="YYYY-MM-DD"
                  @change="handleSearch"
              />
            </el-form-item>
          </div>

          <div class="form-actions-wrapper">
            <div class="form-actions">
              <el-button size="small" type="primary" @click="handleSearch" :loading="loading">查询</el-button>
              <el-button size="small" @click="resetSearch">重置</el-button>
              <el-button-group size="small" class="view-toggle">
                <el-button :type="viewMode === 'table' ? 'primary' : 'default'" @click="viewMode = 'table'" title="表格视图"><el-icon><Grid /></el-icon></el-button>
                <el-button :type="viewMode === 'card' ? 'primary' : 'default'" @click="viewMode = 'card'" title="卡片视图"><el-icon><CopyDocument /></el-icon></el-button>
              </el-button-group>
            </div>
          </div>
        </el-form>
      </div>

      <!-- 数据区域 -->
      <div class="data-content" v-loading="loading">
        <!-- 表格视图 -->
        <div v-if="viewMode === 'table'" class="table-view">
          <el-table
              v-if="batchData.list?.length"
              :data="batchData.list"
              height="100%"
              stripe
              border
              fit
              resizable
          >
            <el-table-column prop="batchId" label="批次 ID" min-width="150" show-overflow-tooltip>
              <template #default="{ row }">
                <el-link type="primary" :underline="false" @click="copyText(row.batchId)" :disabled="!row.batchId">
                  {{ row.batchId }}
                </el-link>
              </template>
            </el-table-column>
            <el-table-column prop="originalFilename" label="文件名称" min-width="200" show-overflow-tooltip />
            <el-table-column prop="createTime" label="创建时间" min-width="160" :formatter="formatDate" />
            <el-table-column prop="updateTime" label="更新时间" min-width="160" :formatter="formatDate" />
            <el-table-column prop="statusDesc" label="状态" min-width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'warning'" effect="dark" size="small">
                  {{ row.statusDesc }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="message" label="处理信息" min-width="200" show-overflow-tooltip />
            <el-table-column label="操作" min-width="120" fixed="right">
              <template #default="{ row }">
                <el-button
                    size="small"
                    type="success"
                    @click="toExcel(row)"
                    :loading="isExporting(row.batchId)"
                >
                  {{ isExporting(row.batchId) ? '导出中...' : '导出' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div v-else class="no-data-container">
            <el-empty description="没有找到匹配的批次数据" :image-size="120" />
          </div>
        </div>

        <!-- 卡片视图 -->
        <div v-else class="card-container">
          <el-scrollbar>
            <el-row :gutter="20" style="margin: 0; padding: 16px;">
              <el-col v-for="item in batchData.list" :key="item.batchId" :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                <el-card class="batch-card" shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <span class="card-title">{{ item.originalFilename }}</span>
                      <el-tag :type="item.status === 1 ? 'success' : item.status === 2 ? 'danger' : 'warning'" effect="dark" size="small">{{ item.statusDesc }}</el-tag>
                    </div>
                  </template>
                  <div class="card-body">
                    <div class="card-item"><span class="label">批次 ID：</span><el-link type="primary" :underline="false" @click="copyText(item.batchId)">{{ item.batchId }}</el-link></div>
                    <div class="card-item"><span class="label">文件名称：</span>{{ item.originalFilename }}</div>
                    <div class="card-item"><span class="label">创建时间：</span>{{ formatDateValue(item.createTime) }}</div>
                    <div class="card-item"><span class="label">更新时间：</span>{{ formatDateValue(item.updateTime) }}</div>
                    <div class="card-item"><span class="label">状态：</span>{{ item.statusDesc }}</div>
                    <div class="card-item"><span class="label">处理信息：</span>{{ item.message }}</div>
                  </div>
                  <div class="card-footer">
                    <el-button size="small" type="success" @click="toExcel(item)" :loading="isExporting(item.batchId)">
                      {{ isExporting(item.batchId) ? '导出中...' : '导出' }}
                    </el-button>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <el-empty v-if="!batchData.list?.length" description="没有找到匹配的批次数据" />
          </el-scrollbar>
        </div>
      </div>

      <!-- 分页 -->
      <div class="fixed-pagination" v-if="batchData.list?.length">
        <div class="pagination-content">
          <el-button size="small" plain class="page-btn" :disabled="batchData.isFirstPage" @click="pageNumber > 1 && (pageNumber--, fetchBatchData())">
            上一页
          </el-button>
          <div class="page-jumper">
            <span>跳转到</span>
            <el-input-number
              v-model="jumpPageNumber"
              :min="1"
              :max="batchData.pages"
              size="small"
              controls-position="right"
              @change="handleJumpPage"
              class="page-input"
            />
            <span class="page-total">页，共 {{ batchData.pages }} 页 ({{ batchData.total }} 条)</span>
          </div>
          <el-button size="small" plain class="page-btn" :disabled="batchData.isLastPage" @click="pageNumber < batchData.pages && (pageNumber++, fetchBatchData())">
            下一页
          </el-button>
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

const loading = ref(false)
const exportingRows = ref(new Set())
const pageNumber = ref(1)
const pageSize = ref(20)
const jumpPageNumber = ref(1)
const viewMode = ref('table')

const isExporting = (batchId) => exportingRows.value.has(batchId)

const searchForm = reactive({
  batchId: '',
  status: null,
  startTime: '',
  endTime: ''
})

const batchData = reactive({
  list: [],
  pageNum: 1,
  pages: 1,
  total: 0,
  isFirstPage: true,
  isLastPage: true
})

// 格式化日期
const formatDate = (row, column, cellValue) => {
  if (!cellValue) return ''
  const date = new Date(cellValue)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 格式化日期值（用于卡片视图）
const formatDateValue = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const fetchBatchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNumber.value,
      pageSize: pageSize.value,
      ...searchForm
    }

    const response = await axios.get('/api/maindata/fuzzymatch/getbatchlist', { params })

    if (response.data.code === 200 && response.data.data) {
      const page = response.data.data
      batchData.list = page.list || []
      batchData.pageNum = page.pageNum
      batchData.pages = page.pages
      batchData.total = page.total
      batchData.isFirstPage = page.isFirstPage
      batchData.isLastPage = page.isLastPage
      jumpPageNumber.value = page.pageNum
    } else {
      ElMessage.error(response.data.message || '获取数据失败')
    }
  } catch (error) {
    console.error('获取批次数据失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNumber.value = 1
  jumpPageNumber.value = 1
  fetchBatchData()
}

// 跳转到指定页
const handleJumpPage = () => {
  if (jumpPageNumber.value >= 1 && jumpPageNumber.value <= batchData.pages) {
    pageNumber.value = jumpPageNumber.value
    fetchBatchData()
  }
}

const resetSearch = () => {
  searchForm.batchId = ''
  searchForm.status = null
  searchForm.startTime = ''
  searchForm.endTime = ''
  pageNumber.value = 1
  fetchBatchData()
}

const toExcel = async (row) => {
  exportingRows.value.add(row.batchId)
  try {
    const params = { batchId: row.batchId }
    const response = await axios.get('/api/maindata/fuzzymatch/exportbatch', {
      params,
      responseType: 'blob'
    })

    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const downloadUrl = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = `模糊匹配结果_${row.batchId}_${new Date().getTime()}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(downloadUrl)

    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  } finally {
    exportingRows.value.delete(row.batchId)
  }
}

// 文本复制函数
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

onMounted(() => {
  fetchBatchData()
})
</script>

<style scoped>
/* ==================== 1. 基础布局 ==================== */
.md-fuzzy-batch-view {
  display: flex;
  flex-direction: column;
  height: 100vh;
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

/* ==================== 3. 数据内容区域 ==================== */
.data-content {
  flex: 1;
  height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.table-view {
  flex: 1;
  height: 100%;
  overflow: hidden;
}

:deep(.el-table) {
  height: 100%;
  width: 100%;
  font-size: 14px !important;
}

:deep(.el-table th.el-table__cell) {
  background-color: var(--el-table-header-bg-color, #f8f9fb);
  color: var(--el-text-color-primary, #303133);
  font-weight: 600;
  height: 48px;
}

/* 修复表格边框粗细不一致问题 */
:deep(.el-table--border .el-table__inner-wrapper:before),
:deep(.el-table--border .el-table__inner-wrapper:after) {
  background-color: var(--el-table-border-color);
  content: "";
  position: absolute;
  z-index: calc(var(--el-table-index) + 2);
}

.no-data-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-secondary, #ffffff);
}

/* 卡片视图样式 */
.card-container {
  flex: 1;
  overflow: auto;
}

.batch-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  margin-bottom: 16px;
}

.batch-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  max-width: 70%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-header :deep(.el-tag) {
  flex-shrink: 0;
}

.card-body {
  flex: 1;
  margin: 12px 0;
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
  line-height: 1.6;
  word-break: break-all;
}

.card-item .label {
  color: #909399;
  font-weight: 500;
  flex-shrink: 0;
  min-width: 80px;
  display: inline-block;
}

.card-footer {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  margin-top: auto;
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

.page-total {
  margin-left: 4px;
}

/* ==================== 5. 响应式适配 ==================== */
/* 2K 屏幕优化 */
@media (min-width: 2000px) and (max-width: 2600px) {
  .md-fuzzy-batch-view {
    max-width: min(1920px, 95vw);
  }
}

/* 超宽屏幕 */
@media (min-width: 2600px) {
  .md-fuzzy-batch-view {
    max-width: min(2560px, 95vw);
  }
}

/* ==================== 6. 暗色模式样式 ==================== */
html.dark .md-fuzzy-batch-view,
.dark .md-fuzzy-batch-view {
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

.dark .no-data-container :deep(.el-empty__description) {
  color: var(--el-text-color-secondary, #a0a0a0) !important;
}

.dark .batch-card {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-color: var(--el-border-color, #3a3a4a) !important;
}

.dark .card-header {
  border-bottom-color: var(--el-border-color, #3a3a4a) !important;
}

.dark .card-title {
  color: var(--el-text-color-regular, #e0e0e0) !important;
}

.dark .card-item {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

.dark .card-item .label {
  color: var(--el-text-color-secondary, #a0a0a0) !important;
}

.dark .card-footer {
  border-top-color: var(--el-border-color, #3a3a4a) !important;
}
</style>
