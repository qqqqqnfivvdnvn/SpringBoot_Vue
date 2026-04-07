<template>
  <div class="hospital-data-view">
    <!-- ==================== 主容器：整合搜索和数据区域 ==================== -->
    <div class="integrated-container">

      <!-- ==================== 搜索区域：固定在顶部 ==================== -->
      <div class="fixed-search">
        <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent="handleSearch">
          <!-- 搜索条件：支持多字段筛选 -->
          <div class="search-conditions">
            <el-form-item label="机构类型">
              <el-select v-model="searchForm.orgType" placeholder="请选择机构类型" clearable @clear="handleSearch" style="width: 120px;">
                <el-option label="医院" value="医院" />
                <el-option label="药店" value="药店" />
                <el-option label="商业" value="商业" />
              </el-select>
            </el-form-item>
            <el-form-item label="DataId">
              <el-input v-model="searchForm.dataId" placeholder="请输入DataId" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="原始名称">
              <el-input v-model="searchForm.originalName" placeholder="请输入原始名称" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="原始编码">
              <el-input v-model="searchForm.dataCode" placeholder="请输入原始编码" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="KeyId">
              <el-input v-model="searchForm.keyid" placeholder="请输入KeyId" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="省份">
              <el-input v-model="searchForm.province" placeholder="请输入省份" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="豪森编码">
              <el-input v-model="searchForm.hsCode" placeholder="请输入豪森编码" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
          </div>

          <!-- 操作按钮区域：查询、重置、导出、上传 -->
          <div class="form-actions-wrapper">
            <div class="form-actions">
              <el-button size="small" type="primary" @click="handleSearch" :loading="loading">查询</el-button>
              <el-button size="small" @click="resetSearch">重置</el-button>
              <el-button size="small" type="success" @click="toExcel" :loading="exporting">
                {{ exporting ? '导出中...' : '导出' }}
              </el-button>
              <el-button size="small" type="warning" @click="triggerUpload" :loading="uploading">
                <el-icon v-if="!uploading"><Upload /></el-icon>
                {{ uploading ? '上传中...' : '上传数据' }}
              </el-button>
              <!-- 隐藏的文件输入框 -->
              <input
                ref="fileInputRef"
                type="file"
                accept=".xlsx,.xls"
                style="display: none"
                @change="handleFileChange"
              />
            </div>
          </div>
        </el-form>
      </div>

      <!-- ==================== 数据展示区域：表格视图 ==================== -->
      <div class="data-content">
        <div class="content-wrapper" v-loading="loading">
          <!-- 表格视图：显示详细数据列表 -->
          <div class="table-view">
            <el-table
                v-if="duplicateData.list?.length"
                :data="duplicateData.list"
                height="100%"
                stripe
                border
                fit
                resizable
            >
              <el-table-column
                  v-for="(col, index) in columns"
                  :key="index"
                  :prop="col.prop"
                  :label="col.label"
                  :width="col.width"
                  :min-width="col.minWidth || 100"
                  :fixed="col.fixed"
                  :resizable="col.resizable !== false"
                  show-overflow-tooltip
              >
                <template #header>
                  {{ col.label }}
                </template>
                <template #default="{ row }">
                  <template v-if="col.prop === 'orgType'">
                    <!-- 机构类型 -->
                    <el-tag :type="getOrgTypeType(row.orgType)" size="small">
                      {{ row.orgType }}
                    </el-tag>
                  </template>
                  <template v-else-if="['dataId', 'dataCode', 'originalName', 'keyid', 'name', 'hsCode'].includes(col.prop)">
                    <!-- 可点击复制的链接字段 -->
                    <el-link type="primary" :underline="false" @click="copyText(row[col.prop])" :disabled="!row[col.prop]">
                      {{ row[col.prop] }}
                    </el-link>
                  </template>
                  <template v-else-if="col.prop === 'addtime'">
                    <!-- 添加时间 -->
                    {{ formatDate(row.addtime) }}
                  </template>
                  <template v-else-if="col.prop === 'action'">
                    <!-- 操作列 -->
                    <el-dropdown size="small" @command="handleCommand" class="action-dropdown">
                      <el-button type="primary" size="small" class="action-button">
                        <el-icon class="action-icon"><Operation /></el-icon>
                        操作
                        <el-icon class="arrow-icon"><ArrowDown /></el-icon>
                      </el-button>
                      <template #dropdown>
                        <el-dropdown-menu class="action-dropdown-menu">
                          <el-dropdown-item :command="{action: 'detail', row: row}" class="dropdown-item detail-item">
                            <el-icon class="menu-icon"><Search /></el-icon>
                            <span class="menu-text">详情查看</span>
                          </el-dropdown-item>
                          <el-dropdown-item :command="{action: 'update', row: row}" class="dropdown-item update-item">
                            <el-icon class="menu-icon"><EditPen /></el-icon>
                            <span class="menu-text">数据管理</span>
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </template>
                  <template v-else>
                    <!-- 普通文本字段 -->
                    {{ row[col.prop] }}
                  </template>
                </template>
              </el-table-column>
            </el-table>
            <div v-else class="no-data-container">
              <el-empty description="没有找到匹配的数据" :image-size="120" />
            </div>
          </div>
        </div>

        <!-- 分页组件：固定在底部 -->
        <div class="fixed-pagination" v-if="duplicateData.list?.length">
          <div class="pagination-content">
            <el-button size="small" plain :disabled="!duplicateData.hasPreviousPage" @click="pageNumber > 1 && (pageNumber--, fetchDuplicateData())">
              上一页
            </el-button>
            <div class="page-jumper">
              <span>跳转到</span>
              <el-input-number
                v-model="jumpPageNumber"
                :min="1"
                :max="duplicateData.pages"
                size="small"
                controls-position="right"
                @change="handleJumpPage"
                class="page-input"
              />
              <span>页，共 {{ duplicateData.pages }} 页 ({{ duplicateData.total }} 条)</span>
            </div>
            <el-button size="small" plain :disabled="!duplicateData.hasNextPage" @click="pageNumber < duplicateData.pages && (pageNumber++, fetchDuplicateData())">
              下一页
            </el-button>
            <el-select v-model="pageSize" size="small" style="width: 110px;" @change="handlePageSizeChange">
              <el-option :value="20" label="每页20条" />
              <el-option :value="40" label="每页40条" />
              <el-option :value="60" label="每页60条" />
            </el-select>
          </div>
        </div>
      </div>
    </div>

    <!-- ==================== 详情弹窗：查看数据完整信息 ==================== -->
    <el-dialog v-model="showDetailModal" title="数据详情" width="500px" destroy-on-close>
      <template #title>
        <div class="custom-dialog-title">数据详情</div>
      </template>

      <div class="detail-container">
        <div class="detail-row" v-for="(item, key) in detailFields" :key="key">
          <label>{{ item.label }}：</label>
          <span>
            <template v-if="item.copy">
              <el-link type="primary" :underline="false" @click="copyText(item.value(currentDuplicate))">
                {{ item.value(currentDuplicate) }}
              </el-link>
            </template>
            <template v-else-if="key === 'orgType'">
              <!-- 对orgType字段特殊处理，显示带样式的tag -->
              <el-tag :type="item.type(currentDuplicate)" size="small" effect="dark" style="color: #fff;">
                {{ item.value(currentDuplicate) }}
              </el-tag>
            </template>
            <template v-else>
              {{ item.value(currentDuplicate) }}
            </template>
          </span>
        </div>
      </div>

      <template #footer>
        <el-button @click="showDetailModal = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- ==================== 数据管理弹窗：更新数据状态 ==================== -->
    <el-dialog
        v-model="updateDetailModal"
        width="500px"
        :close-on-click-modal="false"
        destroy-on-close
        class="update-dialog"
    >
      <template #title>
        <div class="custom-dialog-title">数据管理</div>
      </template>

      <el-form :model="statusForm" label-width="100px">
        <el-form-item label="dataId">
          <el-input v-model="currentUpdateDuplicate.dataId" readonly />
        </el-form-item>
        <el-form-item label="标准名称">
          <el-input v-model="currentUpdateDuplicate.name" readonly />
        </el-form-item>
        <el-form-item label="选择状态" :rules="[{ required: true, message: '请选择状态', trigger: 'change' }]">
          <el-select v-model="statusForm.status" placeholder="请选择状态" style="width: 100%;">
            <el-option label="正常" value="1" />
            <el-option label="作废" value="2" />
            <el-option label="无法清洗" value="3" />
            <el-option label="豪森禁用客户" value="4" />
            <el-option label="重复数据" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注信息">
          <el-input
              v-model="statusForm.remark"
              type="textarea"
              :rows="3"
              placeholder="请输入备注信息（可选）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="closeUpdateModal">取消</el-button>
        <el-button type="primary" :loading="isSaving" @click="saveChanges">确认更新</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
// ==================== 依赖导入 ====================
import '@/assets/css/dark-mode.css'
import { ref, reactive, onMounted } from 'vue'
import { Search, EditPen, Operation, ArrowDown, Upload } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// ==================== 数据状态管理 ====================
const duplicateData = reactive({
  list: [],
  total: 0,
  pages: 0,
  pageNum: 1,
  hasNextPage: false,
  hasPreviousPage: false
})

// ==================== 加载状态管理 ====================
const loading = ref(false)
const isSaving = ref(false)
const exporting = ref(false)
const uploading = ref(false)
const fileInputRef = ref(null)

// ==================== 分页配置 ====================
const pageSize = ref(20)
const pageNumber = ref(1)
const jumpPageNumber = ref(1)

// ==================== 搜索表单配置 ====================
const searchForm = reactive({
  orgType: '',
  dataId: '',
  originalName: '',
  dataCode: '',
  keyid: '',
  province: '',
  hsCode: ''
})

// ==================== 表格列配置 ====================
const columns = ref([
  { label: '机构类型', prop: 'orgType', minWidth: 90 },
  { label: 'dataId', prop: 'dataId', minWidth: 120 },
  { label: '原始名称', prop: 'originalName', minWidth: 120 },
  { label: '原始编码', prop: 'dataCode', minWidth: 120 },
  { label: 'keyId', prop: 'keyid', minWidth: 120 },
  { label: '省份', prop: 'province', minWidth: 80 },
  { label: '标准名称', prop: 'name', minWidth: 140 },
  { label: '豪森编码', prop: 'hsCode', minWidth: 120 },
  { label: '地址', prop: 'address', minWidth: 120 },
  { label: '添加时间', prop: 'addtime', minWidth: 160 },
  { label: '操作', prop: 'action', fixed: 'right', width: 120, resizable: false }
])

// ==================== 弹窗状态控制 ====================
const showDetailModal = ref(false)
const updateDetailModal = ref(false)

// ==================== 当前操作数据对象 ====================
const currentDuplicate = ref({})
const currentUpdateDuplicate = ref({})

// ==================== 状态更新表单 ====================
const statusForm = reactive({
  status: '',
  remark: ''
})

// ==================== 机构类型标签样式 ====================
const getOrgTypeType = (orgType) => {
  const map = { '医院': 'primary', '药店': 'success', '商业': 'warning' }
  return map[orgType] || 'info'
}

// ==================== 日期格式化工具函数 ====================
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  // 处理 "2026-02-12 10:59:53.87237" 格式
  return dateStr.split('.')[0] || dateStr
}

// ==================== 详情弹窗字段配置 ====================
const detailFields = {
  orgType: { label: '机构类型', value: d => d.orgType, type: d => getOrgTypeType(d.orgType) },
  dataId: { label: 'dataId', value: d => d.dataId, copy: true },
  originalName: { label: '原始名称', value: d => d.originalName, copy: true },
  dataCode: { label: '原始编码', value: d => d.dataCode, copy: true },
  keyid: { label: 'keyId', value: d => d.keyid, copy: true },
  province: { label: '省份', value: d => d.province },
  name: { label: '标准名称', value: d => d.name, copy: true },
  nameHistory: { label: '历史名称', value: d => d.nameHistory },
  hsCode: { label: '豪森编码', value: d => d.hsCode, copy: true },
  address: { label: '地址', value: d => d.address },
  addtime: { label: '添加时间', value: d => formatDate(d.addtime) },
  signStatus: { label: '登记状态', value: d => d.signStatus },
  ylRemark: { label: '是否需易联禁用这条对应关系', value: d => d.ylRemark },
  remark: { label: '备注', value: d => d.remark }
}

// ==================== 文本复制工具函数 ====================
const copyText = async (text) => {
  if (!text) return

  // 将文本转换为字符串
  const textToCopy = String(text)

  try {
    // 优先使用现代 Clipboard API
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(textToCopy)
      ElMessage.success('已复制')
      return
    }

    // 降级方案：使用传统的 execCommand
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

// ==================== 数据加载相关函数 ====================
// 获取重复数据列表
const fetchDuplicateData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNumber.value,
      pageSize: pageSize.value,
      ...Object.fromEntries(
          Object.entries(searchForm).filter(([, value]) => value !== '' && value !== null && value !== undefined)
      )
    }
    const { data } = await axios.get('/api/haosen/duplicateData/getDuplicateDataByCondition', { params })
    if (data.code === 200) {
      Object.assign(duplicateData, data.data)
      pageNumber.value = data.data.pageNum
      jumpPageNumber.value = data.data.pageNum
    } else {
      ElMessage.error(data.msg || '获取数据失败')
    }
  } catch {
    ElMessage.error('网络请求异常')
  } finally {
    loading.value = false
  }
}

// ==================== 搜索和分页处理函数 ====================
// 触发搜索
const handleSearch = () => {
  pageNumber.value = 1
  fetchDuplicateData()
}

// 重置搜索条件
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => (searchForm[key] = ''))
  pageNumber.value = 1
  fetchDuplicateData()
}

// 每页条数改变处理
const handlePageSizeChange = () => {
  pageNumber.value = 1
  fetchDuplicateData()
}

// 跳转到指定页
const handleJumpPage = () => {
  if (jumpPageNumber.value >= 1 && jumpPageNumber.value <= duplicateData.pages) {
    pageNumber.value = jumpPageNumber.value
    fetchDuplicateData()
  }
}

// ==================== 数据导出函数 ====================
// 导出Excel文件
const toExcel = async () => {
  if (exporting.value) return
  exporting.value = true
  try {
    const params = Object.fromEntries(
        Object.entries(searchForm).filter(([, value]) => value !== '' && value !== null && value !== undefined)
    )
    const { data: jsonBlob } = await axios.get('/api/haosen/duplicateData/exportDuplicateData', { params, responseType: 'blob' })
    const jsonText = await jsonBlob.text()
    const { data: base64 } = JSON.parse(jsonText)
    const byteChars = atob(base64)
    const byteNums = new Uint8Array(byteChars.length)
    for (let i = 0; i < byteChars.length; i++) byteNums[i] = byteChars.charCodeAt(i)
    const excelBlob = new Blob([byteNums], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = URL.createObjectURL(excelBlob)
    const link = document.createElement('a')
    link.href = url
    link.download = `豪森需要确认的编码数据导出_${new Date().toLocaleDateString()}.xlsx`
    link.click()
    URL.revokeObjectURL(url)
  } catch {
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

// ==================== 文件上传相关函数 ====================
// 触发文件选择
const triggerUpload = () => {
  if (fileInputRef.value) {
    fileInputRef.value.click()
  }
}

// 处理文件选择变化
const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证文件类型
  const validTypes = ['application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet']
  if (!validTypes.includes(file.type) && !file.name.endsWith('.xlsx') && !file.name.endsWith('.xls')) {
    ElMessage.error('请选择 Excel 文件（.xlsx 或 .xls）')
    event.target.value = ''
    return
  }

  // 验证文件大小（限制 10MB）
  const maxSize = 10 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过 10MB')
    event.target.value = ''
    return
  }

  uploading.value = true
  const formData = new FormData()
  formData.append('file', file)

  try {
    const { data } = await axios.post('/api/haosen/duplicateData/uploadDuplicateData', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (data.code === 200) {
      ElMessage.success('上传成功')
      fetchDuplicateData() // 刷新数据列表
    } else {
      ElMessage.error(data.msg || '上传失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    if (error.response?.data?.msg) {
      ElMessage.error(error.response.data.msg)
    } else {
      ElMessage.error('上传失败，请稍后重试')
    }
  } finally {
    uploading.value = false
    event.target.value = '' // 清空文件选择，允许重复选择同一文件
  }
}

// ==================== 弹窗管理函数 ====================
// 显示详情弹窗
const showDetail = (row) => {
  currentDuplicate.value = { ...row }
  showDetailModal.value = true
}

// 打开数据管理弹窗
const updateDetail = (row) => {
  currentUpdateDuplicate.value = JSON.parse(JSON.stringify(row))
  // 初始化状态表单
  statusForm.status = ''
  statusForm.remark = ''
  updateDetailModal.value = true
}

// 关闭数据管理弹窗
const closeUpdateModal = () => {
  updateDetailModal.value = false
  currentUpdateDuplicate.value = {}
  statusForm.status = ''
  statusForm.remark = ''
}

// ==================== 下拉菜单命令处理 ====================
// 处理操作下拉菜单
const handleCommand = (command) => {
  const { action, row } = command
  switch(action) {
    case 'detail':
      showDetail(row)
      break
    case 'update':
      updateDetail(row)
      break
  }
}

// 保存更新的数据 - 使用 updateInstitutionStatus 接口更新状态
const saveChanges = async () => {
  if (!statusForm.status) {
    ElMessage.warning('请选择状态')
    return
  }

  if (isSaving.value) return
  isSaving.value = true

  try {
    // 将机构类型转换为接口需要的格式
    const orgTypeMap = {
      '医院': 'hospital',
      '药店': 'drugStore',
      '商业': 'company'
    }

    const payload = {
      dataId: currentUpdateDuplicate.value.dataId,
      status: parseInt(statusForm.status),
      remark: statusForm.remark || null,
      institutionType: orgTypeMap[currentUpdateDuplicate.value.orgType] || 'hospital'
    }

    const { data } = await axios.post('/api/haosen/updateData/updateInstitutionStatus', payload)
    if (data.code === 200) {
      ElMessage.success('状态更新成功')
      closeUpdateModal()
      fetchDuplicateData()
    } else {
      ElMessage.error(data.msg || '状态更新失败')
    }
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('网络请求异常')
  } finally {
    isSaving.value = false
  }
}

// ==================== 组件生命周期钩子 ====================
// 组件挂载时自动加载数据
onMounted(() => {
  fetchDuplicateData()
})
</script>

<style scoped>
/* ==================== 页面整体布局 ==================== */
.hospital-data-view {
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
/* 2K屏幕优化 */
@media (min-width: 2000px) and (max-width: 2600px) {
  .hospital-data-view {
    max-width: min(1860px, 90vw);
  }
}

/* 超宽屏幕 */
@media (min-width: 2600px) {
  .hospital-data-view {
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

/* ==================== 详情弹窗样式 ==================== */
.detail-container {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 8px;
}

.detail-row {
  display: flex;
  margin-bottom: 14px;
  align-items: flex-start;
}

.detail-row label {
  min-width: 160px;
  font-weight: 600;
  line-height: 1.5;
}

.detail-row span {
  flex: 1;
  word-break: break-word;
  line-height: 1.5;
}

/* ==================== 弹窗标题样式 ==================== */
.custom-dialog-title {
  text-align: center;
  font-size: 18px;
  font-weight: 600;
  width: 100%;
}

/* ==================== 操作下拉菜单样式 ==================== */
.action-dropdown {
  display: inline-block;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 6px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.action-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.action-icon {
  font-size: 16px;
  transition: transform 0.3s ease;
}

.action-button:hover .action-icon {
  transform: rotate(90deg);
}

.arrow-icon {
  font-size: 14px;
  transition: transform 0.3s ease;
}

.action-dropdown:hover .arrow-icon {
  transform: rotate(180deg);
}

.action-dropdown-menu {
  border-radius: 8px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  padding: 6px 0;
  min-width: 140px;
}

.dropdown-item {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  margin: 2px 8px;
  border-radius: 6px;
  transition: all 0.2s ease;
  cursor: pointer;
}

.dropdown-item:hover {
  background-color: var(--el-color-primary-light-9);
  transform: translateX(4px);
}

.menu-icon {
  font-size: 16px;
  margin-right: 8px;
  min-width: 16px;
}

.menu-text {
  font-size: 14px;
  font-weight: 500;
}

.detail-item .menu-icon {
  color: var(--el-color-primary);
}

.update-item .menu-icon {
  color: var(--el-color-success);
}

/* ==================== 数据处理弹窗样式 ==================== */
.update-dialog :deep(.el-dialog) {
  display: flex;
  flex-direction: column;
  max-height: 90vh;
  margin: 5vh auto;
}

.update-dialog :deep(.el-dialog__header) {
  padding: 16px 24px;
  border-bottom: 1px solid #ebeef5;
}

.update-dialog :deep(.el-dialog__body) {
  flex: 1;
  padding: 20px 24px;
  overflow: hidden;
}

/* ==================== 暗色主题适配 ==================== */
@media (prefers-color-scheme: dark) {
  .action-dropdown-menu {
    background-color: var(--el-bg-color-overlay);
    border: 1px solid var(--el-border-color);
  }

  .dropdown-item:hover {
    background-color: var(--el-color-primary-light-9);
    transform: translateX(4px);
  }

  .menu-text {
    color: var(--el-text-color-primary);
  }
}
</style>