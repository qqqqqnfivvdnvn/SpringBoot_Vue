<template>
  <div class="org-relation-view">
    <!-- ==================== 主容器：整合搜索和数据区域 ==================== -->
    <div class="integrated-container">

      <!-- ==================== 搜索区域：固定在顶部 ==================== -->
      <div class="fixed-search">
        <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent="handleSearch">
          <!-- 搜索条件：支持多字段筛选 -->
          <div class="search-conditions">
            <el-form-item label="营业执照名称">
              <el-input v-model="searchForm.businessLicenseName" placeholder="请输入营业执照名称" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="标准名称 KeyID">
              <el-input v-model="searchForm.keyId" placeholder="请输入 KeyID" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="省份">
              <el-input v-model="searchForm.province" placeholder="请输入省份" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="地址">
              <el-input v-model="searchForm.address" placeholder="请输入地址" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
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
              <el-button size="small" type="primary" @click="addRelation">新增</el-button>
              <el-button-group size="small" class="view-toggle">
                <el-button
                    :type="viewMode === 'table' ? 'primary' : 'default'"
                    size="small"
                    @click="viewMode = 'table'"
                    title="表格视图"
                >
                  <el-icon><Grid /></el-icon>
                </el-button>
                <el-button
                    :type="viewMode === 'card' ? 'primary' : 'default'"
                    size="small"
                    @click="viewMode = 'card'"
                    title="卡片视图"
                >
                  <el-icon><CopyDocument /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </div>
        </el-form>
      </div>

      <!-- ==================== 数据展示区域：表格/卡片视图 ==================== -->
      <div class="data-content">
        <div class="content-wrapper" v-loading="loading">
          <!-- 表格视图：显示详细数据列表 -->
          <div v-if="viewMode === 'table'" class="table-view">
            <el-table
                v-if="relationData.list?.length"
                :data="relationData.list"
                height="100%"
                stripe
                border
                fit
                resizable
            >
              <el-table-column prop="businessLicenseName" label="营业执照名称" min-width="180" show-overflow-tooltip />
              <el-table-column prop="province" label="省份" min-width="80" show-overflow-tooltip/>
              <el-table-column prop="keyId" label="KeyID" min-width="120" show-overflow-tooltip>
                <template #default="{ row }">
                  <el-link type="primary" :underline="false" @click="copyText(row.keyId)" :disabled="!row.keyId">
                    {{ row.keyId }}
                  </el-link>
                </template>
              </el-table-column>
              <el-table-column prop="name" label="标准名称" min-width="150" show-overflow-tooltip />
              <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
              <el-table-column prop="addTime" label="创建时间" min-width="160" :formatter="formatDateTime" />
              <el-table-column prop="updateTime" label="更新时间" min-width="160" :formatter="formatDateTime" />
              <el-table-column label="操作" min-width="150" fixed="right">
                <template #default="{ row }">
                  <el-button size="small" type="primary" @click="editRelation(row)">编辑</el-button>
                  <el-button size="small" type="danger" @click="deleteRelation(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <div v-else class="no-data-container">
              <el-empty description="没有找到匹配的比对关系数据" :image-size="120" />
            </div>
          </div>

          <!-- 卡片视图：以卡片形式展示数据，适合浏览 -->
          <div v-else class="card-view">
            <el-empty v-if="!relationData.list?.length" description="没有找到匹配的比对关系数据" :image-size="120" class="no-data-container" />
            <el-row :gutter="20" v-else>
              <el-col v-for="item in relationData.list" :key="item.businessLicenseName" :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                <el-card class="relation-card" shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <span class="card-title">{{ item.name }}</span>
                      <el-tag type="info" size="small">{{ item.province }}</el-tag>
                    </div>
                  </template>
                  <div class="card-body">
                    <div class="card-item"><span class="label">营业执照：</span>{{ item.businessLicenseName }}</div>
                    <div class="card-item"><span class="label">KeyID：</span>{{ item.keyId }}</div>
                    <div class="card-item"><span class="label">地址：</span>{{ item.address }}</div>
                    <div class="card-item"><span class="label">创建时间：</span>{{ formatDateTimeValue(item.addTime) }}</div>
                    <div class="card-item"><span class="label">更新时间：</span>{{ formatDateTimeValue(item.updateTime) }}</div>
                  </div>
                  <div class="card-footer">
                    <el-button size="small" type="primary" @click="editRelation(item)">编辑</el-button>
                    <el-button size="small" type="danger" @click="deleteRelation(item)">删除</el-button>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </div>

        <!-- 分页组件：固定在底部 -->
        <div class="fixed-pagination" v-if="relationData.list?.length">
          <div class="pagination-content">
            <el-button size="small" plain class="page-btn" :disabled="relationData.isFirstPage" @click="pageNumber > 1 && (pageNumber--, fetchRelationData())">
              上一页
            </el-button>
            <div class="page-jumper">
              <span>跳转到</span>
              <el-input-number
                v-model="jumpPageNumber"
                :min="1"
                :max="relationData.pages"
                size="small"
                controls-position="right"
                @change="handleJumpPage"
                class="page-input"
              />
              <span class="page-total">页，共 {{ relationData.pages }} 页 ({{ relationData.total }} 条)</span>
            </div>
            <el-button size="small" plain class="page-btn" :disabled="relationData.isLastPage" @click="pageNumber < relationData.pages && (pageNumber++, fetchRelationData())">
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

    <!-- 编辑对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑比对关系" width="500px" :close-on-click-modal="false">
      <el-form :model="editForm" label-width="100px" label-position="left">
        <el-form-item label="营业执照名称">
          <el-input v-model="editForm.businessLicenseName" />
        </el-form-item>
        <el-form-item label="省份">
          <el-input v-model="editForm.province" />
        </el-form-item>
        <el-form-item label="KeyID">
          <el-input v-model="editForm.keyId" />
        </el-form-item>
        <el-form-item label="标准名称">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="editForm.address" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>

    <!-- 新增对话框 -->
    <el-dialog v-model="addDialogVisible" title="新增比对关系" width="500px" :close-on-click-modal="false">
      <el-form :model="addForm" label-width="100px" label-position="left">
        <el-form-item label="营业执照名称" required>
          <el-input v-model="addForm.businessLicenseName" placeholder="请输入营业执照名称" />
        </el-form-item>
        <el-form-item label="省份" required>
          <el-input v-model="addForm.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="KeyID" required>
          <el-input v-model="addForm.keyId" placeholder="请输入 KeyID" />
        </el-form-item>
        <el-form-item label="标准名称" required>
          <el-input v-model="addForm.name" placeholder="请输入标准名称" />
        </el-form-item>
        <el-form-item label="地址" required>
          <el-input v-model="addForm.address" type="textarea" :rows="3" placeholder="请输入地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Grid, CopyDocument } from '@element-plus/icons-vue'

const loading = ref(false)
const exporting = ref(false)
const submitting = ref(false)
const pageNumber = ref(1)
const pageSize = ref(20)
const jumpPageNumber = ref(1)
const viewMode = ref('table')
const editDialogVisible = ref(false)

const searchForm = reactive({
  businessLicenseName: '',
  keyId: '',
  province: '',
  address: ''
})

const relationData = reactive({
  list: [],
  pageNum: 1,
  pages: 1,
  total: 0,
  isFirstPage: true,
  isLastPage: true
})

const editForm = reactive({
  businessLicenseName: '',
  province: '',
  keyId: '',
  name: '',
  address: ''
})

const addForm = reactive({
  businessLicenseName: '',
  province: '',
  keyId: '',
  name: '',
  address: ''
})

const addDialogVisible = ref(false)

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

// 格式化日期时间：YYYY-MM-DD HH:mm:ss
const formatDateTime = (row, column, cellValue) => {
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

// 格式化日期时间值（用于卡片视图）：YYYY-MM-DD HH:mm:ss
const formatDateTimeValue = (dateTimeStr) => {
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

const fetchRelationData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNumber.value,
      pageSize: pageSize.value,
      ...searchForm
    }

    const response = await axios.get('/api/hengrui/matchedaddress/getrelationlist', { params })

    if (response.data.code === 200 && response.data.data) {
      const page = response.data.data
      relationData.list = page.list || []
      relationData.pageNum = page.pageNum
      relationData.pages = page.pages
      relationData.total = page.total
      relationData.isFirstPage = page.isFirstPage
      relationData.isLastPage = page.isLastPage
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
  fetchRelationData()
}

// 跳转到指定页
const handleJumpPage = () => {
  if (jumpPageNumber.value >= 1 && jumpPageNumber.value <= relationData.pages) {
    pageNumber.value = jumpPageNumber.value
    fetchRelationData()
  }
}

const resetSearch = () => {
  searchForm.businessLicenseName = ''
  searchForm.keyId = ''
  searchForm.province = ''
  searchForm.address = ''
  pageNumber.value = 1
  fetchRelationData()
}

// 每页条数改变处理
const handlePageSizeChange = () => {
  pageNumber.value = 1
  fetchRelationData()
}

const toExcel = async () => {
  exporting.value = true
  try {
    const params = { ...searchForm }
    const response = await axios.get('/api/hengrui/matchedaddress/exportrelation', {
      params,
      responseType: 'blob'
    })

    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const downloadUrl = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = `恒瑞比对关系_${new Date().getTime()}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(downloadUrl)

    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

const editRelation = (row) => {
  editForm.businessLicenseName = row.businessLicenseName
  editForm.province = row.province
  editForm.keyId = row.keyId
  editForm.name = row.name
  editForm.address = row.address
  editDialogVisible.value = true
}

const addRelation = () => {
  addForm.businessLicenseName = ''
  addForm.province = ''
  addForm.keyId = ''
  addForm.name = ''
  addForm.address = ''
  addDialogVisible.value = true
}

const submitAdd = async () => {
  submitting.value = true
  try {
    const response = await axios.post('/api/hengrui/matchedaddress/addrelation', addForm)

    if (response.data.code === 200) {
      ElMessage.success('添加成功')
      addDialogVisible.value = false
      fetchRelationData()
    } else {
      ElMessage.error(response.data.message || '添加失败')
    }
  } catch (error) {
    console.error('添加失败:', error)
    ElMessage.error('添加失败')
  } finally {
    submitting.value = false
  }
}

const submitEdit = async () => {
  submitting.value = true
  try {
    const response = await axios.post('/api/hengrui/matchedaddress/updaterelation', editForm)

    if (response.data.code === 200) {
      ElMessage.success('更新成功')
      editDialogVisible.value = false
      fetchRelationData()
    } else {
      ElMessage.error(response.data.message || '更新失败')
    }
  } catch (error) {
    console.error('更新失败:', error)
    ElMessage.error('更新失败')
  } finally {
    submitting.value = false
  }
}

const deleteRelation = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条比对关系吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await axios.post('/api/hengrui/matchedaddress/deleterelation', null, {
      params: { businessLicenseName: row.businessLicenseName }
    })

    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      fetchRelationData()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchRelationData()
})
</script>

<style scoped>
/* ==================== 1. 基础布局 ==================== */
.org-relation-view {
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
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-wrapper {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
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

:deep(.el-table__body-wrapper) {
  height: 100%;
  overflow: auto;
}

:deep(.el-table th.el-table__cell) {
  background-color: var(--el-table-header-bg-color, #f8f9fb);
  color: var(--el-text-color-primary, #303133);
  font-weight: 600;
  height: 48px;
}

:deep(.el-table--border .el-table__inner-wrapper:before),
:deep(.el-table--border .el-table__inner-wrapper:after) {
  background-color: var(--el-table-border-color);
  content: "";
  position: absolute;
  z-index: calc(var(--el-table-index) + 2);
}

.card-view {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

/* ==================== 4. 卡片视图样式 ==================== */
.relation-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  margin-bottom: 16px;
}

.relation-card:hover {
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

/* ==================== 5. 分页区域 ==================== */
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

.size-select {
  width: 110px !important;
  font-size: 12px;
}

.no-data-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-secondary, #ffffff);
}

/* ==================== 6. 响应式适配 ==================== */
/* 2K 屏幕优化 */
@media (min-width: 2000px) and (max-width: 2600px) {
  .org-relation-view {
    max-width: min(1920px, 95vw);
  }
}

/* 超宽屏幕 */
@media (min-width: 2600px) {
  .org-relation-view {
    max-width: min(2560px, 95vw);
  }
}

/* ==================== 7. 暗色模式样式 ==================== */
html.dark .org-relation-view,
.dark .org-relation-view {
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

.dark .relation-card {
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

.dark .no-data-container :deep(.el-empty__description) {
  color: var(--el-text-color-secondary, #a0a0a0) !important;
}
</style>
