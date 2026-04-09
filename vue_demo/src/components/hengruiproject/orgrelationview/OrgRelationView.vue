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
              <el-table-column prop="businessLicenseName" label="营业执照名称" width="180" show-overflow-tooltip />
              <el-table-column prop="province" label="省份" width="80" show-overflow-tooltip/>
              <el-table-column prop="keyId" label="KeyID" width="120" show-overflow-tooltip />
              <el-table-column prop="name" label="标准名称" width="150" show-overflow-tooltip />
              <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
              <el-table-column prop="addTime" label="创建时间" width="160" :formatter="formatDateTime" />
              <el-table-column prop="updateTime" label="更新时间" width="160" :formatter="formatDateTime" />
              <el-table-column label="操作" width="150" fixed="right">
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
            <el-button size="small" plain :disabled="relationData.isFirstPage" @click="pageNumber > 1 && (pageNumber--, fetchRelationData())">
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
              <span>页，共 {{ relationData.pages }} 页 ({{ relationData.total }} 条)</span>
            </div>
            <el-button size="small" plain :disabled="relationData.isLastPage" @click="pageNumber < relationData.pages && (pageNumber++, fetchRelationData())">
              下一页
            </el-button>
            <el-select v-model="pageSize" size="small" style="width: 110px;" @change="handlePageSizeChange">
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
import '@/assets/css/dark-mode.css'
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

const formatDateTime = (row, column, cellValue) => {
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

// 格式化日期时间值（用于卡片视图）
const formatDateTimeValue = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day}-${hours}-${minutes}-${seconds}`
}

const fetchRelationData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNumber.value,
      pageSize: pageSize.value,
      ...searchForm
    }

    const response = await axios.get('/api/hengrui/relation/getRelationList', { params })

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
    const response = await axios.get('/api/hengrui/relation/exportRelation', {
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
    const response = await axios.post('/api/hengrui/relation/add', addForm)

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
    const response = await axios.post('/api/hengrui/relation/update', editForm)

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

    const response = await axios.post('/api/hengrui/relation/delete', null, {
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
/* ==================== 页面整体布局 ==================== */
.org-relation-view {
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
  .org-relation-view {
    max-width: min(1860px, 90vw);
  }
}

/* 超宽屏幕 */
@media (min-width: 2600px) {
  .org-relation-view {
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

/* 搜索条件区域 */
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

/* ==================== 操作按钮区域样式 ==================== */
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

.view-toggle {
  margin-left: 8px;
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

.card-view {
  height: 100%;
  overflow-y: auto;
  padding: 16px;
}

/* ==================== 卡片视图样式 ==================== */
.relation-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
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
  font-size: 14px;
  font-weight: bold;
  max-width: 70%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-body {
  flex: 1;
  margin: 12px 0;
}

.card-item {
  margin-bottom: 8px;
  line-height: 1.5;
}

.card-item .label {
  color: #606266;
  font-weight: 500;
  margin-right: 6px;
  min-width: 60px;
  display: inline-block;
}

.card-footer {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  margin-top: auto;
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

.no-data-container {
  height: 100%;
  display: flex;
  align-items: center;
  background: var(--bg-secondary, #ffffff);
  justify-content: center;
}
</style>
