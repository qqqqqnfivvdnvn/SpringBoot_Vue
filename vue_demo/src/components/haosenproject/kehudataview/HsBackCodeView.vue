<template>
  <div class="hs-back-code-view">
    <div class="integrated-container">
      <!-- 搜索区域 -->
      <div class="fixed-search">
        <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent="handleSearch">
          <div class="search-conditions">
            <el-form-item label="数据返回类型">
              <el-select v-model="searchForm.feedbackType" placeholder="请选择返回类型" clearable @clear="handleSearch" style="width: 150px;">
                <el-option label="申诉" value="1" />
                <el-option label="清洗完成" value="2" />
              </el-select>
            </el-form-item>
            <el-form-item label="批次编号">
              <el-input v-model="searchForm.batchCode" placeholder="请输入批次编号" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="原始编码">
              <el-input v-model="searchForm.dataCode" placeholder="请输入原始编码" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="豪森编码">
              <el-input v-model="searchForm.hsCode" placeholder="请输入豪森编码" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="searchForm.status" placeholder="请选择状态" clearable @clear="handleSearch" style="width: 120px;">
                <el-option label="未处理" value="1" />
                <el-option label="已处理" value="2" />
              </el-select>
            </el-form-item>
            <el-form-item label="添加时间">
              <el-date-picker
                v-model="dateRange"
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                clearable
                @clear="handleSearch"
                @change="handleSearch"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 480px;"
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
        <div v-if="viewMode === 'table'" class="table-container">
          <el-table
            v-if="dataList.list?.length"
            :data="dataList.list"
            height="100%"
            stripe
            border
            fit
            resizable
          >
            <el-table-column prop="feedbackType" label="数据返回类型" min-width="100" show-overflow-tooltip>
              <template #default="{ row }">
                <el-tag :type="row.feedbackType === '1' ? 'warning' : 'success'" size="small">
                  {{ row.feedbackType === '1' ? '申诉' : '清洗完成' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="batchCode" label="批次编号" min-width="150" show-overflow-tooltip>
              <template #default="{ row }">
                <el-link type="primary" :underline="false" @click="copyText(row.batchCode)" :disabled="!row.batchCode">
                  {{ row.batchCode }}
                </el-link>
              </template>
            </el-table-column>
            <el-table-column prop="dataCode" label="原始编码" min-width="150" show-overflow-tooltip>
              <template #default="{ row }">
                <el-link type="primary" :underline="false" @click="copyText(row.dataCode)" :disabled="!row.dataCode">
                  {{ row.dataCode }}
                </el-link>
              </template>
            </el-table-column>
            <el-table-column prop="hsCode" label="豪森编码" min-width="150" show-overflow-tooltip>
              <template #default="{ row }">
                <el-link type="primary" :underline="false" @click="copyText(row.hsCode)" :disabled="!row.hsCode">
                  {{ row.hsCode }}
                </el-link>
              </template>
            </el-table-column>
            <el-table-column prop="remarks" label="备注" min-width="150" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" min-width="80" show-overflow-tooltip>
              <template #default="{ row }">
                <el-tag :type="row.feedbackType === '2' ? 'success' : (row.status === '1' ? 'info' : 'success')" size="small">
                  {{ row.feedbackType === '2' ? '已处理' : (row.status === '1' ? '未处理' : '已处理') }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="addTime" label="添加时间" min-width="160" :formatter="formatDate" />
          </el-table>

          <div v-else class="no-data-container">
            <el-empty description="没有找到匹配的数据" :image-size="120" />
          </div>
        </div>

        <!-- 卡片视图 -->
        <div v-else class="card-view">
          <el-empty v-if="!dataList.list?.length" description="没有找到匹配的数据" :image-size="120" class="no-data-container" />
          <el-row :gutter="20" v-else>
            <el-col v-for="item in dataList.list" :key="item.dataCode" :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
              <el-card class="data-card" shadow="hover">
                <template #header>
                  <div class="card-header">
                    <div class="card-title">
                      <el-link type="primary" :underline="false" @click="copyText(item.dataCode)">
                        {{ item.dataCode }}
                      </el-link>
                    </div>
                  </div>
                </template>
                <div class="card-body">
                  <div class="card-item"><span class="label">数据返回类型：</span><el-tag :type="item.feedbackType === '1' ? 'warning' : 'success'" size="small">{{ item.feedbackType === '1' ? '申诉' : '清洗完成' }}</el-tag></div>
                  <div class="card-item"><span class="label">批次编号：</span><el-link type="primary" :underline="false" @click="copyText(item.batchCode)" :disabled="!item.batchCode">{{ item.batchCode }}</el-link></div>
                  <div class="card-item"><span class="label">豪森编码：</span><el-link type="primary" :underline="false" @click="copyText(item.hsCode)" :disabled="!item.hsCode">{{ item.hsCode }}</el-link></div>
                  <div class="card-item"><span class="label">备注：</span>{{ item.remarks }}</div>
                  <div class="card-item"><span class="label">状态：</span><el-tag :type="item.feedbackType === '2' ? 'success' : (item.status === '1' ? 'info' : 'success')" size="small">{{ item.feedbackType === '2' ? '已处理' : (item.status === '1' ? '未处理' : '已处理') }}</el-tag></div>
                  <div class="card-item"><span class="label">添加时间：</span>{{ formatDateTime(item.addTime) }}</div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- 分页 -->
      <div class="fixed-pagination" v-if="dataList.list?.length">
        <div class="pagination-content">
          <el-button size="small" plain class="page-btn" :disabled="!dataList.hasPreviousPage" @click="pageNumber > 1 && (pageNumber--, fetchData())">
            上一页
          </el-button>
          <div class="page-jumper">
            <span>跳转到</span>
            <el-input-number
              v-model="jumpPageNumber"
              :min="1"
              :max="dataList.pages"
              size="small"
              controls-position="right"
              @change="handleJumpPage"
              class="page-input"
            />
            <span class="page-total">页，共 {{ dataList.pages }} 页 ({{ dataList.total }} 条)</span>
          </div>
          <el-button size="small" plain class="page-btn" :disabled="!dataList.hasNextPage" @click="pageNumber < dataList.pages && (pageNumber++, fetchData())">
            下一页
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import '@/assets/css/dark-mode.css'
import { ref, reactive, onMounted } from 'vue'
import { Grid, CopyDocument } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// ==================== 视图状态管理 ====================
const viewMode = ref('table')

// ==================== 日期范围选择器 ====================
const dateRange = ref([])

// ==================== 数据状态管理 ====================
const dataList = reactive({
  list: [],
  total: 0,
  pages: 0,
  pageNum: 1,
  hasNextPage: false,
  hasPreviousPage: false
})

// ==================== 加载状态管理 ====================
const loading = ref(false)

// ==================== 分页配置 ====================
const pageSize = ref(20)
const pageNumber = ref(1)
const jumpPageNumber = ref(1)

// ==================== 搜索表单配置 ====================
const searchForm = reactive({
  feedbackType: '',
  batchCode: '',
  dataCode: '',
  hsCode: '',
  status: '',
  startTime: '',
  endTime: ''
})

// ==================== 日期时间格式化 ====================
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

const formatDateTime = (dateTimeStr) => {
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

// ==================== 数据加载函数 ====================
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNumber.value,
      pageSize: pageSize.value,
      ...Object.fromEntries(
        Object.entries(searchForm).filter(([, value]) => value !== '' && value !== null && value !== undefined)
      )
    }

    const { data } = await axios.get('/api/haosen/kehu/backhscode/getlist', { params })
    if (data.code === 200) {
      Object.assign(dataList, data.data)
      pageNumber.value = data.data.pageNum
      jumpPageNumber.value = data.data.pageNum
    } else {
      ElMessage.error(data.msg || '获取数据失败')
    }
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('网络请求异常')
  } finally {
    loading.value = false
  }
}

// ==================== 搜索和分页处理函数 ====================
const handleSearch = () => {
  if (dateRange.value && dateRange.value.length === 2) {
    searchForm.startTime = dateRange.value[0]
    searchForm.endTime = dateRange.value[1]
  } else {
    searchForm.startTime = ''
    searchForm.endTime = ''
  }

  pageNumber.value = 1
  fetchData()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => (searchForm[key] = ''))
  dateRange.value = []
  pageNumber.value = 1
  fetchData()
}

const handleJumpPage = () => {
  if (jumpPageNumber.value >= 1 && jumpPageNumber.value <= dataList.pages) {
    pageNumber.value = jumpPageNumber.value
    fetchData()
  }
}

// ==================== 文本复制工具函数 ====================
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

// ==================== 初始化加载 ====================
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.hs-back-code-view {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: min(1600px, 95vw);
  margin: 0 auto;
  background: #ffffff;
  overflow: hidden;
  font-size: 14px;
}

@media (min-width: 2000px) and (max-width: 2600px) {
  .hs-back-code-view {
    max-width: min(1860px, 90vw);
  }
}

@media (min-width: 2600px) {
  .hs-back-code-view {
    max-width: min(2400px, 95vw);
  }
}

html.dark .hs-back-code-view,
.dark .hs-back-code-view {
  background: var(--el-bg-color, #1a1a2c);
}

.integrated-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  margin: 8px;
  border: 1px solid var(--el-border-color-light, #ebeef5);
  border-radius: 6px;
  background: var(--el-bg-color, #fff);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.fixed-search {
  flex-shrink: 0;
  padding: 14px 18px 10px;
  background: var(--el-bg-color, #ffffff);
  border-bottom: 1px solid var(--el-border-color-light, #ebeef5);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  border-radius: 6px 6px 0 0;
}

html.dark .fixed-search,
.dark .fixed-search {
  background: var(--el-bg-color, #1a1a2c);
  border-bottom: 1px solid var(--el-border-color, #3d3d3d);
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
  background: var(--el-bg-color, #ffffff);
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0 !important;
  flex: 1 1 200px;
  min-width: 200px;
}

.form-actions-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
  width: 100%;
}

.form-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.view-toggle {
  margin-left: 8px;
}

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
  height: 100%;
  width: 100%;
  font-size: 14px;
}

:deep(.el-table th.el-table__cell) {
  background-color: var(--el-table-header-bg-color, #f8f9fb);
  color: var(--el-text-color-primary, #303133);
  font-weight: 600;
  height: 48px;
}

.card-view {
  height: 100%;
  overflow-y: auto;
  padding: 16px;
}

.data-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  background: var(--el-bg-color, #ffffff);
}

.data-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.data-card :deep(.el-card__header) {
  background: var(--el-bg-color, #ffffff);
  border-bottom: 1px solid var(--el-border-color-lighter, #f0f0f0);
  padding: 14px 16px;
}

.dark .data-card :deep(.el-card__header) {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-bottom-color: var(--el-border-color, #3a3a4a) !important;
}

.data-card :deep(.el-card__body) {
  background: var(--el-bg-color, #ffffff);
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 14px 16px;
}

.dark .data-card :deep(.el-card__body) {
  background: var(--el-bg-color, #1a1a2c) !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--el-border-color-light, #ebeef5);
}

.card-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary, #303133);
  max-width: 70%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-body {
  flex: 1;
  margin: 12px 0;
  overflow: auto;
}

.card-item {
  margin-bottom: 8px;
  line-height: 1.6;
  color: var(--el-text-color-regular, #606266);
  word-break: break-all;
}

.card-item .label {
  color: var(--el-text-color-secondary, #909399);
  font-weight: 600;
  flex-shrink: 0;
  min-width: 80px;
  display: inline-block;
}

.card-footer {
  margin-top: auto;
  text-align: right;
}

.no-data-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.fixed-pagination {
  flex-shrink: 0;
  background: #fff;
  padding: 12px;
  border-top: 1px solid var(--el-border-color-light, #ebeef5);
  text-align: center;
}

html.dark .fixed-pagination,
.dark .fixed-pagination {
  background: var(--el-bg-color, #1a1a2c);
  border-top: 1px solid var(--el-border-color, #3d3d3d);
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
  color: var(--el-text-color-regular, #606266);
}

html.dark .page-jumper,
.dark .page-jumper {
  color: var(--el-text-color-regular, #d0d0d0);
}

.page-input {
  width: 90px !important;
  margin: 0 8px;
}

.page-input :deep(.el-input__wrapper) {
  padding-left: 10px;
  padding-right: 35px;
}

.page-total {
  margin-left: 8px;
}

.page-btn {
  border-radius: 4px;
  padding: 0 15px;
  height: 32px;
  font-size: 12px;
}
</style>
