<template>
  <div class="md-location-batch-view">
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
            </div>
          </div>
        </el-form>
      </div>

      <!-- 数据区域 -->
      <div class="data-content">
        <div class="content-wrapper" v-loading="loading">
          <div class="table-view">
            <el-table
                v-if="batchData.list?.length"
                :data="batchData.list"
                height="100%"
                stripe
                border
                fit
                resizable
            >
              <el-table-column prop="batchId" label="批次 ID" show-overflow-tooltip />
              <el-table-column prop="originalFilename" label="文件名称" width="200" show-overflow-tooltip />
              <el-table-column prop="createTime" label="创建时间" width="160" :formatter="formatDate" />
              <el-table-column prop="updateTime" label="更新时间" width="160" :formatter="formatDate" />
              <el-table-column prop="statusDesc" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'warning'" effect="dark" size="small">
                    {{ row.statusDesc }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="message" label="处理信息" width="200" show-overflow-tooltip />
              <el-table-column label="操作" width="120" fixed="right">
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
        </div>

        <!-- 分页 -->
        <div class="fixed-pagination" v-if="batchData.list?.length">
          <div class="pagination-content">
            <el-button size="small" plain :disabled="batchData.isFirstPage" @click="pageNumber > 1 && (pageNumber--, fetchBatchData())">
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
              <span>页，共 {{ batchData.pages }} 页 ({{ batchData.total }} 条)</span>
            </div>
            <el-button size="small" plain :disabled="batchData.isLastPage" @click="pageNumber < batchData.pages && (pageNumber++, fetchBatchData())">
              下一页
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import '@/assets/css/dark-mode.css'
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const exportingRows = ref(new Set())
const pageNumber = ref(1)
const pageSize = ref(20)
const jumpPageNumber = ref(1)

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
  return `${year}-${month}-${day}-${hours}-${minutes}-${seconds}`
}

const fetchBatchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNumber.value,
      pageSize: pageSize.value,
      ...searchForm
    }

    const response = await axios.get('/api/maindata/batch/getBatchList', { params })

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
    const response = await axios.get('/api/maindata/batch/exportBatch', {
      params,
      responseType: 'blob'
    })

    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const downloadUrl = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = `区县经纬度获取_高德_${row.batchId}_${new Date().getTime()}.xlsx`
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

onMounted(() => {
  fetchBatchData()
})
</script>

<style scoped>
/* ==================== 页面整体布局 ==================== */
.md-location-batch-view {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: min(1250px, 95vw);
  margin: 0 auto;
  background: var(--bg-secondary, #ffffff);
  overflow: hidden;
  font-size: 12px;
}

/* ==================== 整合容器样式 ==================== */
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

/* ==================== 响应式布局适配 ==================== */
/* 2K 屏幕优化 */
@media (min-width: 2000px) and (max-width: 2600px) {
  .md-location-batch-view {
    max-width: min(1860px, 90vw);
  }
}

/* 超宽屏幕 */
@media (min-width: 2600px) {
  .md-location-batch-view {
    max-width: min(2400px, 95vw);
  }
}

/* ==================== 搜索区域样式 ==================== */
.fixed-search {
  flex-shrink: 0;
  padding: 14px 18px 10px;
  background: var(--bg-secondary, #ffffff);
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  border-radius: 6px 6px 0 0;
}

.search-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-conditions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 8px;
  align-items: flex-end;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0 !important;
  flex: 1 1 180px;
  min-width: 180px;
}

.form-actions-wrapper {
  display: flex;
  justify-content: flex-end;
  width: 100%;
}

.form-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

/* ==================== 数据内容区域样式 ==================== */
.data-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-wrapper {
  flex: 1;
  overflow: hidden;
}

.table-view {
  height: 100%;
}

:deep(.el-table) {
  height: 100%;
}

.no-data-container {
  height: 100%;
  display: flex;
  align-items: center;
  background: var(--bg-secondary, #ffffff);
  justify-content: center;
}

/* ==================== 分页区域样式 ==================== */
.fixed-pagination {
  flex-shrink: 0;
  background: var(--bg-secondary, #ffffff);
  padding: 12px;
  border-top: 1px solid var(--bg-secondary, #ffffff);
  box-shadow: 0 -1px 4px rgba(0, 0, 0, 0.05);
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
  gap: 8px;
  font-size: 12px;
  color: #606266;
}

.page-input {
  width: 80px;
}

.page-info {
  font-size: 12px;
  color: #606266;
  min-width: 220px;
  text-align: center;
}
</style>
