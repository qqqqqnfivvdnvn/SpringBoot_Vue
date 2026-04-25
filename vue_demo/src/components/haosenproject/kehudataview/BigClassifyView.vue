<template>
  <div class="big-classify-view">
    <div class="integrated-container">
      <!-- 搜索区域 -->
      <div class="fixed-search">
        <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent="handleSearch">
          <div class="search-conditions">
            <el-form-item label="KEYID">
              <el-input v-model="searchForm.keyid" placeholder="请输入 KEYID" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="名称">
              <el-input v-model="searchForm.name" placeholder="请输入名称" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="医院大类">
              <el-select v-model="searchForm.bigClassify" placeholder="请选择医院大类" clearable @clear="handleSearch" style="width: 150px;">
                <el-option label="诊所/医务室" value="诊所/医务室" />
                <el-option label="卫生院/卫生服务中心" value="卫生院/卫生服务中心" />
                <el-option label="县区医院" value="县区医院" />
                <el-option label="市级医院" value="市级医院" />
                <el-option label="其他" value="其他" />
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
                style="width: 240px;"
              />
            </el-form-item>
            <el-form-item label="更新时间">
              <el-date-picker
                v-model="updateDateRange"
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                clearable
                @clear="handleSearch"
                @change="handleSearch"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 240px;"
              />
            </el-form-item>
          </div>

          <div class="form-actions-wrapper">
            <div class="form-actions">
              <el-button size="small" type="primary" @click="handleSearch" :loading="loading">查询</el-button>
              <el-button size="small" @click="resetSearch">重置</el-button>
              <el-button size="small" type="success" @click="showAddModal">新增</el-button>
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
            <el-table-column prop="keyid" label="KEYID" min-width="150" show-overflow-tooltip>
              <template #default="{ row }">
                <el-link type="primary" :underline="false" @click="copyText(row.keyid)" :disabled="!row.keyid">
                  {{ row.keyid }}
                </el-link>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="名称" min-width="150" show-overflow-tooltip>
              <template #default="{ row }">
                <el-link type="primary" :underline="false" @click="copyText(row.name)" :disabled="!row.name">
                  {{ row.name }}
                </el-link>
              </template>
            </el-table-column>
            <el-table-column prop="bigClassify" label="医院大类" min-width="120" align="center" />
            <el-table-column prop="addTime" label="添加时间" min-width="160" :formatter="formatDate" />
            <el-table-column prop="updateTime" label="更新时间" min-width="160" :formatter="formatDate" />
            <el-table-column label="操作" min-width="140" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="showEditModal(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="showDeleteConfirm(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div v-else class="no-data-container">
            <el-empty description="没有找到匹配的数据" :image-size="120" />
          </div>
        </div>

        <!-- 卡片视图 -->
        <div v-else class="card-view">
          <el-empty v-if="!dataList.list?.length" description="没有找到匹配的数据" :image-size="120" class="no-data-container" />
          <el-row :gutter="20" v-else>
            <el-col v-for="item in dataList.list" :key="item.keyid" :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
              <el-card class="data-card" shadow="hover">
                <template #header>
                  <div class="card-header">
                    <div class="card-title">
                      <el-link type="primary" :underline="false" @click="copyText(item.name)">
                        {{ item.name }}
                      </el-link>
                    </div>
                  </div>
                </template>
                <div class="card-body">
                  <div class="card-item"><span class="label">KEYID：</span><el-link type="primary" :underline="false" @click="copyText(item.keyid)">{{ item.keyid }}</el-link></div>
                  <div class="card-item"><span class="label">名称：</span>{{ item.name }}</div>
                  <div class="card-item"><span class="label">医院大类：</span>{{ item.bigClassify }}</div>
                  <div class="card-item"><span class="label">添加时间：</span>{{ formatDateTime(item.addTime) }}</div>
                  <div class="card-item"><span class="label">更新时间：</span>{{ formatDateTime(item.updateTime) }}</div>
                </div>
                <div class="card-footer">
                  <el-button size="small" type="primary" @click="showEditModal(item)">编辑</el-button>
                  <el-button size="small" type="danger" @click="showDeleteConfirm(item)">删除</el-button>
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
          <el-select v-model="pageSize" size="small" class="size-select" @change="handlePageSizeChange">
            <el-option :value="20" label="每页20条" />
            <el-option :value="40" label="每页40条" />
            <el-option :value="60" label="每页60条" />
          </el-select>
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="showFormModal"
      :title="isEdit ? '编辑' : '新增'"
      width="600px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px">
        <el-form-item label="KEYID" prop="keyid">
          <el-input v-model="formData.keyid" placeholder="请输入 KEYID" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="医院大类" prop="bigClassify">
          <el-select v-model="formData.bigClassify" placeholder="请选择医院大类" style="width: 100%;" clearable>
            <el-option label="诊所/医务室" value="诊所/医务室" />
            <el-option label="卫生院/卫生服务中心" value="卫生院/卫生服务中心" />
            <el-option label="县区医院" value="县区医院" />
            <el-option label="市级医院" value="市级医院" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showFormModal = false">取消</el-button>
        <el-button type="primary" :loading="isSubmitting" @click="submitForm">
          {{ isSubmitting ? '提交中...' : '确认' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import '@/assets/css/dark-mode.css'
import { ref, reactive, onMounted } from 'vue'
import { Grid, CopyDocument } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// ==================== 视图状态管理 ====================
const viewMode = ref('table')

// ==================== 日期范围选择器 ====================
const dateRange = ref([])
const updateDateRange = ref([])

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
const isSubmitting = ref(false)

// ==================== 分页配置 ====================
const pageSize = ref(20)
const pageNumber = ref(1)
const jumpPageNumber = ref(1)

// ==================== 搜索表单配置 ====================
const searchForm = reactive({
  keyid: '',
  name: '',
  bigClassify: '',
  startTime: '',
  endTime: '',
  updateStartTime: '',
  updateEndTime: ''
})

// ==================== 弹窗状态控制 ====================
const showFormModal = ref(false)
const isEdit = ref(false)

// ==================== 表单数据及验证规则 ====================
const formRef = ref(null)
const formData = reactive({
  keyid: '',
  name: '',
  bigClassify: ''
})

const formRules = {
  keyid: [{ required: true, message: '请输入 KEYID', trigger: 'blur' }],
  name: [{ required: false, trigger: 'blur' }],
  bigClassify: [{ required: false, trigger: 'blur' }]
}

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

    const { data } = await axios.get('/api/haosen/kehu/bigclassify/getlist', { params })
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

  if (updateDateRange.value && updateDateRange.value.length === 2) {
    searchForm.updateStartTime = updateDateRange.value[0]
    searchForm.updateEndTime = updateDateRange.value[1]
  } else {
    searchForm.updateStartTime = ''
    searchForm.updateEndTime = ''
  }

  pageNumber.value = 1
  jumpPageNumber.value = 1
  fetchData()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => (searchForm[key] = ''))
  dateRange.value = []
  updateDateRange.value = []
  pageNumber.value = 1
  fetchData()
}

const handlePageSizeChange = () => {
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

// ==================== 新增/编辑/删除操作 ====================
const showAddModal = () => {
  isEdit.value = false
  formData.keyid = ''
  formData.name = ''
  formData.bigClassify = ''
  showFormModal.value = true
}

const showEditModal = (row) => {
  isEdit.value = true
  formData.keyid = row.keyid
  formData.name = row.name
  formData.bigClassify = row.bigClassify || ''
  showFormModal.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    isSubmitting.value = true
    try {
      const { data } = await axios.post(
        isEdit.value ? '/api/haosen/kehu/bigclassify/update' : '/api/haosen/kehu/bigclassify/insert',
        formData
      )
      if (data.code === 200) {
        ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
        showFormModal.value = false
        fetchData()
      } else {
        ElMessage.error(data.msg || (isEdit.value ? '更新失败' : '新增失败'))
      }
    } catch (error) {
      console.error('提交失败:', error)
      ElMessage.error('网络请求异常')
    } finally {
      isSubmitting.value = false
    }
  })
}

const showDeleteConfirm = (row) => {
  ElMessageBox.confirm(
    `<strong>名称：</strong>${row.name || '未知'}<br/><strong>KEYID：</strong>${row.keyid}<br/><span style="color: #f56c6c;">此操作不可恢复！</span>`,
    '删除确认',
    {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning',
      dangerouslyUseHTMLString: true,
      confirmButtonClass: 'el-button--danger',
      center: true
    }
  ).then(() => {
    confirmDelete(row)
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

const confirmDelete = async (row) => {
  try {
    const { data } = await axios.post('/api/haosen/kehu/bigclassify/delete', { keyid: row.keyid })
    if (data.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    } else {
      ElMessage.error(data.msg || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('网络请求异常')
  }
}

// ==================== 组件生命周期 ====================
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.big-classify-view {
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
  .big-classify-view {
    max-width: min(1860px, 90vw);
  }
}

@media (min-width: 2600px) {
  .big-classify-view {
    max-width: min(2400px, 95vw);
  }
}

html.dark .big-classify-view,
.dark .big-classify-view {
  background: var(--el-bg-color, #1a1a2c);
}

.integrated-container {
  flex: 1;
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

:deep(.el-table--border:before),
:deep(.el-table--border:after),
:deep(.el-table--border .el-table__inner-wrapper:before),
:deep(.el-table--border .el-table__inner-wrapper:after) {
  background-color: var(--el-table-border-color);
  content: "";
  position: absolute;
  z-index: calc(var(--el-table-index) + 2);
}

.dark :deep(.el-table--border:before),
.dark :deep(.el-table--border:after),
.dark :deep(.el-table--border .el-table__inner-wrapper:before),
.dark :deep(.el-table--border .el-table__inner-wrapper:after) {
  background-color: var(--el-border-color, #3a3a4a) !important;
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

.fixed-pagination {
  flex-shrink: 0;
  background: #fff;
  padding: 12px;
  border-top: 1px solid var(--el-border-color-light, #ebeef5);
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
  color: var(--el-text-color-regular, #606266);
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

.no-data-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--el-bg-color, #ffffff);
}

html.dark .integrated-container,
.dark .integrated-container {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-color: var(--el-border-color, #3a3a4a) !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
}

html.dark .fixed-search,
.dark .fixed-search {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-bottom-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark .search-form :deep(.el-form-item__label) {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

html.dark .search-form :deep(.el-input__wrapper) {
  background-color: var(--el-input-bg-color, #2a2a3a) !important;
  box-shadow: 0 0 0 1px var(--el-input-border-color, #3a3a4a) inset !important;
}

html.dark .search-form :deep(.el-picker__wrapper),
html.dark .search-form :deep(.el-date-editor .el-input__wrapper) {
  background-color: var(--el-input-bg-color, #2a2a3a) !important;
  box-shadow: 0 0 0 1px var(--el-input-border-color, #3a3a4a) inset !important;
}

html.dark :deep(.el-table) {
  background-color: var(--el-bg-color, #1a1a2c) !important;
}

html.dark :deep(.el-table__header tr),
html.dark :deep(.el-table__header tr th.el-table__cell),
html.dark :deep(.el-table thead tr th) {
  background-color: var(--el-fill-color-light, #2a2a3a) !important;
  color: var(--el-text-color-regular, #e0e0e0) !important;
  border-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark :deep(.el-table__body tr.el-table__row > td),
html.dark :deep(.el-table tbody tr td.el-table__cell) {
  background-color: var(--el-bg-color, #1a1a2c) !important;
  color: var(--el-text-color-regular, #d0d0d0) !important;
  border-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark :deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: var(--el-fill-color-lighter, #232330) !important;
}

html.dark :deep(.el-table__body tr:hover > td) {
  background-color: var(--el-fill-color-light, #2a2a3a) !important;
}

html.dark .fixed-pagination,
.dark .fixed-pagination {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-top-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark .page-jumper,
.dark .page-jumper {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

html.dark .data-card,
.dark .data-card {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark .card-header,
.dark .card-header {
  border-bottom-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark .card-title,
.dark .card-title {
  color: var(--el-text-color-regular, #e0e0e0) !important;
}

html.dark .card-item,
.dark .card-item {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

html.dark .card-item .label,
.dark .card-item .label {
  color: var(--el-text-color-secondary, #a0a0a0) !important;
}

html.dark .card-footer,
.dark .card-footer {
  border-top-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark .no-data-container :deep(.el-empty__description),
.dark .no-data-container :deep(.el-empty__description) {
  color: var(--el-text-color-secondary, #a0a0a0) !important;
}
</style>
