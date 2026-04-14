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
                v-if="monitoringData.list?.length"
                :data="monitoringData.list"
                height="100%"
                stripe
                border
                fit
                resizable
            >
              <el-table-column prop="serialNo" label="序号" min-width="60" />
              <el-table-column prop="productName" label="商品名" min-width="120" show-overflow-tooltip />
              <el-table-column prop="productId" label="商品ID" min-width="100" show-overflow-tooltip />
              <el-table-column prop="genericName" label="通用名" min-width="120" show-overflow-tooltip />
              <el-table-column prop="platform" label="平台" min-width="80" />
              <el-table-column prop="specification" label="规格" min-width="100" show-overflow-tooltip />
              <el-table-column prop="onlineStorePrice" label="网店价格" min-width="80" />
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

          <!-- 卡片视图：以卡片形式展示数据，适合浏览 -->
          <div v-else class="card-view">
            <el-empty v-if="!monitoringData.list?.length" description="没有找到匹配的数据" :image-size="120" class="no-data-container" />
            <el-row :gutter="20" v-else>
              <el-col v-for="item in monitoringData.list" :key="item.id" :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                <el-card class="monitoring-card" shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <span class="card-title">{{ item.productName }}</span>
                      <el-tag type="info" size="small">{{ item.platform }}</el-tag>
                    </div>
                  </template>
                  <div class="card-body">
                    <div class="card-item"><span class="label">商品ID：</span>{{ item.productId }}</div>
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
          </div>
        </div>

        <!-- 分页组件：固定在底部 -->
        <div class="fixed-pagination" v-if="monitoringData.list?.length">
          <div class="pagination-content">
            <el-button size="small" plain :disabled="monitoringData.isFirstPage" @click="pageNumber > 1 && (pageNumber--, fetchMonitoringData())">
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
              <span>页，共 {{ monitoringData.pages }} 页 ({{ monitoringData.total }} 条)</span>
            </div>
            <el-button size="small" plain :disabled="monitoringData.isLastPage" @click="pageNumber < monitoringData.pages && (pageNumber++, fetchMonitoringData())">
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
  batchId: '',
  productName: '',
  name: '',
  businessLicenseName: '',
  keyId: '',
  province: '',
  address: '',
  createTime: ''
})

const monitoringData = reactive({
  list: [],
  pageNum: 1,
  pages: 1,
  total: 0,
  isFirstPage: true,
  isLastPage: true
})

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

const fetchMonitoringData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNumber.value,
      pageSize: pageSize.value,
      ...searchForm
    }



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
    const params = { ...searchForm }

    const response = await axios.get('/api/hengrui/monitoring/exportData', {
      params,
      responseType: 'blob'
    })

    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const downloadUrl = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = `恒瑞数据汇总_${new Date().getTime()}.xlsx`
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

onMounted(() => {
  // 从路由参数获取批次 ID
  if (route.query.batchId) {
    searchForm.batchId = route.query.batchId
  }
  fetchMonitoringData()
})
</script>

<style scoped>
/* ==================== 页面整体布局 ==================== */
.monitoring-data-view {
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
  .monitoring-data-view {
    max-width: min(1860px, 90vw);
  }
}

/* 超宽屏幕 */
@media (min-width: 2600px) {
  .monitoring-data-view {
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
}

:deep(.el-table__body-wrapper) {
  height: 100%;
  overflow: auto;
}

.card-view {
  height: 100%;
  overflow-y: auto;
  padding: 16px;
}

/* ==================== 卡片视图样式 ==================== */
.monitoring-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.monitoring-card:hover {
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
