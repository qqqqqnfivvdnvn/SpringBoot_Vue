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
              <el-input v-model="searchForm.dataId" placeholder="请输入 DataId" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="原始名称">
              <el-input v-model="searchForm.originalName" placeholder="请输入原始名称" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="原始编码">
              <el-input v-model="searchForm.dataCode" placeholder="请输入原始编码" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="KeyId">
              <el-input v-model="searchForm.keyid" placeholder="请输入 KeyId" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="省份">
              <el-input v-model="searchForm.province" placeholder="请输入省份" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="豪森编码">
              <el-input v-model="searchForm.hsCode" placeholder="请输入豪森编码" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
          </div>

          <!-- 操作按钮区域：查询、重置、处理重复数据、视图切换 -->
          <div class="form-actions-wrapper">
            <div class="form-actions">
              <el-button size="small" type="primary" @click="handleSearch" :loading="loading">查询</el-button>
              <el-button size="small" type="primary" @click="handleDuplicate" :loading="isDealing">处理重复数据</el-button>
              <el-button size="small" @click="resetSearch">重置</el-button>

              <el-button-group size="small" class="view-toggle">
                <el-button :type="viewMode === 'table' ? 'primary' : 'default'" @click="viewMode = 'table'" title="表格视图">
                  <el-icon><Grid /></el-icon>
                </el-button>
                <el-button :type="viewMode === 'card' ? 'primary' : 'default'" @click="viewMode = 'card'" title="卡片视图">
                  <el-icon><CopyDocument /></el-icon>
                </el-button>
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
                  <!-- 操作列：只保留查看和数据管理 -->
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
                        <el-dropdown-item
                            :command="{action: 'update', row: row}"
                            class="dropdown-item update-item">
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
            <el-empty description="没有找到匹配的重复数据" :image-size="120" />
          </div>
        </div>

        <!-- 卡片视图 -->
        <div v-else class="card-container">
          <el-scrollbar>
            <el-row :gutter="20" style="margin: 0; padding: 16px;">
              <el-col v-for="item in duplicateData.list" :key="item.dataId" :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                <el-card class="repeat-card" shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <span class="card-title">{{ item.originalName }}</span>
                      <el-tag :type="getOrgTypeType(item.orgType)" size="small">{{ item.orgType }}</el-tag>
                    </div>
                  </template>
                  <div class="card-body">
                    <div class="card-item"><span class="label">机构类型：</span>{{ item.orgType }}</div>
                    <div class="card-item"><span class="label">dataId：</span><el-link type="primary" :underline="false" @click="copyText(item.dataId)">{{ item.dataId }}</el-link></div>
                    <div class="card-item"><span class="label">原始编码：</span><el-link type="primary" :underline="false" @click="copyText(item.dataCode)">{{ item.dataCode }}</el-link></div>
                    <div class="card-item"><span class="label">keyId：</span><el-link type="primary" :underline="false" @click="copyText(item.keyid)">{{ item.keyid }}</el-link></div>
                    <div class="card-item"><span class="label">省份：</span>{{ item.province }}</div>
                    <div class="card-item"><span class="label">标准名称：</span><el-link type="primary" :underline="false" @click="copyText(item.name)">{{ item.name }}</el-link></div>
                    <div class="card-item"><span class="label">豪森编码：</span><el-link type="primary" :underline="false" @click="copyText(item.hsCode)">{{ item.hsCode }}</el-link></div>
                    <div class="card-item"><span class="label">添加时间：</span>{{ formatDate(item.addtime) }}</div>
                  </div>
                  <div class="card-footer">
                    <el-dropdown size="small" @command="handleCommand" class="action-dropdown">
                      <el-button type="primary" size="small" class="action-button">
                        <el-icon class="action-icon"><Operation /></el-icon>
                        操作
                        <el-icon class="arrow-icon"><ArrowDown /></el-icon>
                      </el-button>
                      <template #dropdown>
                        <el-dropdown-menu class="action-dropdown-menu">
                          <el-dropdown-item :command="{action: 'detail', row: item}" class="dropdown-item detail-item">
                            <el-icon class="menu-icon"><Search /></el-icon>
                            <span class="menu-text">详情查看</span>
                          </el-dropdown-item>
                          <el-dropdown-item :command="{action: 'update', row: item}" class="dropdown-item update-item">
                            <el-icon class="menu-icon"><EditPen /></el-icon>
                            <span class="menu-text">数据管理</span>
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <el-empty v-if="!duplicateData.list?.length" description="没有找到匹配的重复数据" />
          </el-scrollbar>
        </div>

      </div>

      <!-- 分页 -->
      <div class="fixed-pagination" v-if="duplicateData.list?.length">
        <div class="pagination-content">
          <el-button size="small" plain class="page-btn" :disabled="!duplicateData.hasPreviousPage" @click="pageNumber > 1 && (pageNumber--, fetchDuplicateData())">
            上一页
          </el-button>
          <div class="page-jumper">
            <span class="page-total">跳转到</span>
            <el-input-number
              v-model="jumpPageNumber"
              :min="1"
              :max="duplicateData.pages"
              size="small"
              controls-position="right"
              @change="handleJumpPage"
              class="page-input"
            />
            <span class="page-total">页，共 {{ duplicateData.pages }} 页 ({{ duplicateData.total }} 条)</span>
          </div>
          <el-button size="small" plain class="page-btn" :disabled="!duplicateData.hasNextPage" @click="pageNumber < duplicateData.pages && (pageNumber++, fetchDuplicateData())">
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

    <!-- ==================== 详情弹窗：查看重复数据完整信息 ==================== -->
    <el-dialog v-model="showDetailModal" title="重复数据详情" width="500px" destroy-on-close>
      <template #title>
        <div class="custom-dialog-title">重复数据详情</div>
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
              <!-- 对 orgType 字段特殊处理，显示带样式的 tag -->
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

    <!-- ==================== 数据管理弹窗：更新重复数据状态 ==================== -->
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
            <el-option label="数据正常" value="1" />
            <el-option label="数据作废" value="2" />
            <el-option label="无法清洗" value="3" />
            <el-option label="禁用客户" value="4" />
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
</template>

<script setup>
// ==================== 依赖导入 ====================
import '@/assets/css/dark-mode.css'
import { ref, reactive, onMounted } from 'vue'
import { Search, EditPen, Operation, ArrowDown, Grid, CopyDocument } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

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
const isDealing = ref(false)

// ==================== 视图模式 ====================
const viewMode = ref('table')

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
  { label: '添加时间', prop: 'addtime', minWidth: 160 },
  { label: '操作', prop: 'action', fixed: 'right', width: 120, resizable: false }
])

// ==================== 弹窗状态控制 ====================
const showDetailModal = ref(false)
const updateDetailModal = ref(false)

// ==================== 当前操作数据对象 ====================
const currentDuplicate = ref({})
const originalDuplicate = ref({})
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

// ==================== 日期格式化工具函数：YYYY-MM-DD HH:mm:ss ====================
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
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
  nameHistory: { label: '历史名称', value: d => d.nameHistory   },
  hsCode: { label: '豪森编码', value: d => d.hsCode, copy: true },
  addtime: { label: '添加时间', value: d => formatDate(d.addtime) }
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
    const { data } = await axios.get('/api/haosen/duplicatedata/getduplicatedata', { params })
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


// 处理重复数据
const handleDuplicate = async () => {
  if (isDealing.value) return

  try {
    await ElMessageBox.confirm(
      '确定要处理所有重复数据吗？',
      '确认处理',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
  } catch {
    return
  }

  isDealing.value = true
  try {
    const { data } = await axios.get('/api/haosen/duplicatedata/updateduplicatedata')
    if (data.code === 200) {
      ElMessage.success('重复数据处理成功')
      fetchDuplicateData()
    } else {
      ElMessage.error(data.msg || '处理重复数据失败')
    }
  } catch (error) {
    console.error('处理重复数据失败:', error)
    ElMessage.error('网络请求异常')
  } finally {
    isDealing.value = false
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

// ==================== 弹窗管理函数 ====================
// 显示详情弹窗
const showDetail = (row) => {
  currentDuplicate.value = { ...row }
  showDetailModal.value = true
}

// 打开数据管理弹窗
const updateDetail = (row) => {
  originalDuplicate.value = JSON.parse(JSON.stringify(row))
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
  originalDuplicate.value = {}
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

    const { data } = await axios.post('/api/haosen/updatedata/updateinstitutionstatus', payload)
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
  max-width: min(1600px, 95vw);
  margin: 0 auto;
  background: #ffffff;
  overflow: hidden;
  font-size: 14px;
}

/* 暗色模式支持 */
html.dark .hospital-data-view,
.dark .hospital-data-view {
  background: var(--el-bg-color, #1a1a2c);
}

/* ==================== 整合容器样式 ==================== */
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

/* ==================== 响应式布局适配 ==================== */
/* 2K 屏幕优化 */
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

/* 修复表格边框粗细不一致问题 */
:deep(.el-table--border .el-table__inner-wrapper:before),
:deep(.el-table--border .el-table__inner-wrapper:after) {
  background-color: var(--el-table-border-color);
  content: "";
  position: absolute;
  z-index: calc(var(--el-table-index) + 2);
}

:deep(.el-table th.el-table__cell) {
  background-color: var(--el-table-header-bg-color, #f8f9fb);
  color: var(--el-text-color-primary, #303133);
  font-weight: 600;
  height: 48px;
}

/* ==================== 卡片视图样式 ==================== */
.card-container {
  height: 100%;
  overflow: hidden;
}

.repeat-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  background: var(--el-bg-color, #ffffff);
}

.repeat-card :deep(.el-card__header) {
  background: var(--el-bg-color, #ffffff);
  border-bottom: 1px solid var(--el-border-color-light, #ebeef5);
}

.repeat-card :deep(.el-card__body) {
  background: var(--el-bg-color, #ffffff);
}

.repeat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
  margin-top: auto;
  text-align: right;
  padding-top: 12px;
  border-top: 1px solid var(--el-border-color-light, #f0f0f0);
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

/* ==================== 分页区域样式 ==================== */
.fixed-pagination {
  flex-shrink: 0;
  background: var(--el-bg-color, #ffffff);
  padding: 12px;
  border-top: 1px solid var(--el-border-color-light, #ebeef5);
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
  font-size: 12px;
  color: var(--el-text-color-regular, #606266);
  white-space: nowrap;
}

.page-input {
  width: 90px !important;
  margin: 0 4px;
}

.page-input :deep(.el-input__wrapper) {
  padding-left: 10px;
  padding-right: 35px;
}

.page-total {
  margin-left: 4px;
  white-space: nowrap;
}

.page-info {
  font-size: 14px;
  color: var(--el-text-color-regular, #606266);
  min-width: 220px;
  text-align: center;
}

.no-data-container {
  height: 100%;
  display: flex;
  align-items: center;
  background: var(--el-bg-color, #ffffff);
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
  color: var(--el-text-color-regular, #606266);
}

.detail-row span {
  flex: 1;
  word-break: break-word;
  line-height: 1.5;
  color: var(--el-text-color-regular, #606266);
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
  border-bottom: 1px solid var(--el-border-color-light, #ebeef5);
}

.update-dialog :deep(.el-dialog__body) {
  flex: 1;
  padding: 20px 24px;
  overflow: hidden;
}

/* ==================== 暗色主题适配 ==================== */
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

html.dark .fixed-pagination,
.dark .fixed-pagination {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-top-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark .page-jumper,
.dark .page-jumper {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

html.dark .detail-row label,
.dark .detail-row label {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

html.dark .detail-row span,
.dark .detail-row span {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

html.dark .update-dialog :deep(.el-dialog),
.dark .update-dialog :deep(.el-dialog) {
  background: var(--el-bg-color, #1a1a2c) !important;
}

html.dark .update-dialog :deep(.el-dialog__header),
.dark .update-dialog :deep(.el-dialog__header) {
  border-bottom-color: var(--el-border-color, #3a3a4a) !important;
}

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
