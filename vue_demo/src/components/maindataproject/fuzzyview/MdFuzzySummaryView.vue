<template>
  <div class="md-fuzzy-summary-view">
    <div class="integrated-container">
      <!-- 搜索区域 -->
      <div class="fixed-search">
        <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent="handleSearch">
          <div class="search-conditions">
            <el-form-item label="批次 ID">
              <el-input v-model="searchForm.batchId" placeholder="请输入批次 ID" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="原始 ID">
              <el-input v-model="searchForm.originalId" placeholder="请输入原始 ID" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="原始省份">
              <el-input v-model="searchForm.originalProvince" placeholder="请输入原始省份" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="原始名称">
              <el-input v-model="searchForm.originalName" placeholder="请输入原始名称" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="匹配 ID">
              <el-input v-model="searchForm.keyid" placeholder="请输入匹配 ID" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="匹配名称">
              <el-input v-model="searchForm.name" placeholder="请输入匹配名称" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
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
                v-if="summaryData.list?.length"
                :data="summaryData.list"
                height="100%"
                stripe
                border
                fit
                resizable
            >
              <el-table-column prop="originalId" label="id" min-width="200">
                <template #default="{ row }">
                  <el-link type="primary" :underline="false" @click="copyText(row.originalId)" :disabled="!row.originalId">
                    {{ row.originalId }}
                  </el-link>
                </template>
              </el-table-column>
              <el-table-column prop="batchId" label="批次 ID" min-width="150" show-overflow-tooltip>
                <template #default="{ row }">
                  <el-link type="primary" :underline="false" @click="copyText(row.batchId)" :disabled="!row.batchId">
                    {{ row.batchId }}
                  </el-link>
                </template>
              </el-table-column>
              <el-table-column prop="originalProvince" label="省份" min-width="100" show-overflow-tooltip />
              <el-table-column prop="originalName" label="名称" min-width="150" show-overflow-tooltip />
              <el-table-column prop="keyid" label="keyid" min-width="120">
                <template #default="{ row }">
                  <el-link type="primary" :underline="false" @click="copyText(row.keyid)" :disabled="!row.keyid">
                    {{ row.keyid }}
                  </el-link>
                </template>
              </el-table-column>
              <el-table-column prop="name" label="标准名称" min-width="150" show-overflow-tooltip />
              <el-table-column prop="namehistory" label="历史名称" min-width="150" show-overflow-tooltip />
              <el-table-column prop="province" label="省" min-width="80" show-overflow-tooltip />
              <el-table-column prop="cityname" label="市" min-width="80" show-overflow-tooltip/>
              <el-table-column prop="areaname" label="区" min-width="80" show-overflow-tooltip/>
              <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
              <el-table-column prop="principal" label="负责人" min-width="80" />
              <el-table-column prop="legalperson" label="法人" min-width="80" />
              <el-table-column prop="sign_status" label="登记状态" min-width="80" />
              <el-table-column prop="status" label="大库状态" min-width="80" />
            </el-table>

            <div v-else class="no-data-container">
              <el-empty description="没有找到汇总数据" :image-size="120" />
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="fixed-pagination" v-if="summaryData.list?.length">
          <div class="pagination-content">
            <el-button size="small" plain :disabled="summaryData.isFirstPage" @click="pageNumber > 1 && (pageNumber--, fetchSummaryData())">
              上一页
            </el-button>
            <div class="page-jumper">
              <span>跳转到</span>
              <el-input-number
                v-model="jumpPageNumber"
                :min="1"
                :max="summaryData.pages"
                size="small"
                controls-position="right"
                @change="handleJumpPage"
                class="page-input"
              />
              <span>页，共 {{ summaryData.pages }} 页 ({{ summaryData.total }} 条)</span>
            </div>
            <el-button size="small" plain :disabled="summaryData.isLastPage" @click="pageNumber < summaryData.pages && (pageNumber++, fetchSummaryData())">
              下一页
            </el-button>
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

const loading = ref(false)
const pageNumber = ref(1)
const pageSize = ref(20)
const jumpPageNumber = ref(1)

const searchForm = reactive({
  batchId: '',
  originalId: '',
  originalProvince: '',
  originalName: '',
  keyid: '',
  name: ''
})

const summaryData = reactive({
  list: [],
  pageNum: 1,
  pages: 1,
  total: 0,
  isFirstPage: true,
  isLastPage: true
})

const fetchSummaryData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNumber.value,
      pageSize: pageSize.value,
      ...searchForm
    }
    const response = await axios.get('/api/maindata/fuzzyMatch/getSummaryList', { params })

    if (response.data.code === 200 && response.data.data) {
      const page = response.data.data
      summaryData.list = page.list || []
      summaryData.pageNum = page.pageNum
      summaryData.pages = page.pages
      summaryData.total = page.total
      summaryData.isFirstPage = page.isFirstPage
      summaryData.isLastPage = page.isLastPage
      jumpPageNumber.value = page.pageNum
    } else {
      ElMessage.error(response.data.message || '获取数据失败')
    }
  } catch (error) {
    console.error('获取汇总数据失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNumber.value = 1
  jumpPageNumber.value = 1
  fetchSummaryData()
}

const resetSearch = () => {
  searchForm.batchId = ''
  searchForm.originalId = ''
  searchForm.originalProvince = ''
  searchForm.originalName = ''
  searchForm.keyid = ''
  searchForm.name = ''
  pageNumber.value = 1
  fetchSummaryData()
}

// 跳转到指定页
const handleJumpPage = () => {
  if (jumpPageNumber.value >= 1 && jumpPageNumber.value <= summaryData.pages) {
    pageNumber.value = jumpPageNumber.value
    fetchSummaryData()
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
  // 页面加载时自动发送请求
  const urlParams = new URLSearchParams(window.location.search)
  const batchId = urlParams.get('batchId')
  if (batchId) {
    searchForm.batchId = batchId
  }
  fetchSummaryData()
})
</script>

<style scoped>
/* ==================== 页面整体布局 ==================== */
.md-fuzzy-summary-view {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: min(1400px, 95vw);
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
  .md-fuzzy-summary-view {
    max-width: min(1860px, 90vw);
  }
}

/* 超宽屏幕 */
@media (min-width: 2600px) {
  .md-fuzzy-summary-view {
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
</style>
