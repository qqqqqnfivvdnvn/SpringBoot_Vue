<template>
  <div class="appeal-data-view">
    <!-- 整合的搜索和数据区域 -->
    <div class="integrated-container">
      <!-- 搜索区域 -->
      <div class="fixed-search">
        <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent="handleSearch">
          <!-- 上半部分：所有搜索条件 -->
          <div class="search-conditions">
            <el-form-item label="批次编号">
              <el-input v-model="searchForm.batchCode" placeholder="请输入批次编号" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="原始编码">
              <el-input v-model="searchForm.dataCode" placeholder="请输入原始编码" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="原始名称">
              <el-input v-model="searchForm.originalName" placeholder="请输入原始名称" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="DataId">
              <el-input v-model="searchForm.dataId" placeholder="请输入DataId" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="原始省份">
              <el-input v-model="searchForm.originalProvince" placeholder="请输入原始省份" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="经销商">
              <el-input v-model="searchForm.companyName" placeholder="请输入经销商" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="豪森编码">
              <el-input v-model="searchForm.haosenCode" placeholder="请输入豪森编码" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="数据类型">
              <el-select v-model="searchForm.dataType" placeholder="请选择数据类型" clearable @clear="handleSearch">
                <el-option label="存量" value="1" />
                <el-option label="增量" value="2" />
              </el-select>
            </el-form-item>
            <el-form-item label="清洗状态">
              <el-select v-model="searchForm.status" placeholder="请选择清洗状态" clearable @clear="handleSearch">
                <el-option label="待清洗" :value="1" />
                <el-option label="已清洗" :value="2" />
              </el-select>
            </el-form-item>
            <el-form-item label="抽取状态">
              <el-select v-model="searchForm.cleanStatus" placeholder="请选择抽取状态" clearable @clear="handleSearch">
                <el-option label="待抽取" :value="0" />
                <el-option label="已抽取" :value="1" />
              </el-select>
            </el-form-item>
          </div>

          <!-- 下半部分：操作按钮区域，靠右对齐 -->
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
      <!-- 数据区域 -->
      <div class="data-content">
        <div class="content-wrapper" v-loading="loading">
          <!-- 表格视图 -->
          <div v-if="viewMode === 'table'" class="table-view">
            <el-table
                v-if="cleanData.list?.length"
                :data="cleanData.list"
                height="100%"
                stripe
                border
                fit
            >
              <el-table-column
                  v-for="(col, index) in columns"
                  :key="index"
                  :prop="col.prop"
                  :label="col.label"
                  :width="col.width"
                  :fixed="col.fixed"
                  show-overflow-tooltip
              >
                <template #header>
                  <div class="th-content">
                    {{ col.label }}
                    <div class="resize-handle" @mousedown.stop="startResize($event, index)"></div>
                  </div>
                </template>
                <template #default="{ row }">
                  <template v-if="index === 0">
                    <el-link type="primary" :underline="false" @click="copyText(row.batchCode)" :disabled="!row.batchCode">
                      {{ row.batchCode }}
                    </el-link>
                  </template>
                  <template v-else-if="index === 1">
                    <el-link type="primary" :underline="false" @click="copyText(row.dataId)" :disabled="!row.dataId">
                      {{ row.dataId }}
                    </el-link>
                  </template>
                  <template v-else-if="index === 2">
                    <el-link type="primary" :underline="false" @click="copyText(row.dataCode)">
                      {{ row.dataCode }}
                    </el-link>
                  </template>

                  <template v-else-if="index === 3">
                    {{ row.dataType === '1' ? '存量' : row.dataType === '2' ? '增量' : '未知' }}
                  </template>

                  <template v-else-if="index === 4">{{ row.originalProvince }}</template>

                  <template v-else-if="index === 5">
                    <el-link type="primary" :underline="false" @click="copyText(row.originalName)">
                      {{ row.originalName }}
                    </el-link>
                  </template>

                  <template v-else-if="index === 6">{{ row.originalAddress }}</template>

                  <template v-else-if="index === 7">{{ row.companyName }}</template>


                  <template v-else-if="index === 8">
                    <el-tag :type="getStatusTagType(row.status)" size="small">
                      {{ formatStatus(row.status) }}
                    </el-tag>
                  </template>


                  <template v-else-if="index === 9">
                    <el-tag :type="getCleanStatusTagType(row.cleanStatus)" size="small">
                      {{ formatCleanStatus(row.cleanStatus) }}
                    </el-tag>
                  </template>

                  <template v-else-if="index === 10">
                    <el-button type="success" size="small" @click="showDetail(row)">详情</el-button>
                    <el-button type="primary" size="small" :disabled="row.status !== 1" @click="cleanDetail(row)">清洗</el-button>
                  </template>
                </template>
              </el-table-column>
            </el-table>

            <div v-else class="no-data-container">
              <el-empty description="没有找到匹配的数据" :image-size="120" />
            </div>

          </div>

          <!-- 卡片视图 -->
          <div v-else class="card-view">
            <el-empty v-if="!cleanData.list?.length" description="没有找到匹配的数据" :image-size="120" class="no-data-container" />
            <el-row :gutter="20" v-else>
              <el-col v-for="item in cleanData.list" :key="item.dataId" :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                <el-card class="appeal-card" shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <div class="card-title">
                        <el-link type="primary" :underline="false" @click="copyText(item.originalName)">
                          {{ item.originalName }}
                        </el-link>
                      </div>
                      <div class="status-badges">
                        <el-tag :type="getStatusTagType(item.status)" size="small" effect="dark">
                          {{ formatStatus(item.status) }}
                        </el-tag>
                        <!--                        <el-tag :type="getCleanStatusTagType(item.cleanStatus)" size="small" effect="dark" style="margin-left: 4px;">-->
                        <!--                          {{ formatCleanStatus(item.cleanStatus) }}-->
                        <!--                        </el-tag>-->
                      </div>
                    </div>
                  </template>
                  <div class="card-body">
                    <div class="card-item"><span class="label">批次编号：</span><el-link type="primary" :underline="false" @click="copyText(item.batchCode)">{{ item.batchCode }}</el-link></div>
                    <div class="card-item"><span class="label">dataId：</span><el-link type="primary" :underline="false" @click="copyText(item.dataId)">{{ item.dataId }}</el-link></div>
                    <div class="card-item"><span class="label">原始编码：</span><el-link type="primary" :underline="false" @click="copyText(item.dataCode)">{{ item.dataCode }}</el-link></div>
                    <div class="card-item"><span class="label">数据类型：</span>{{ item.dataType === '1' ? '存量' : item.dataType === '2' ? '增量' : '未知' }}</div>
                    <div class="card-item"><span class="label">原始省份：</span>{{ item.originalProvince }}</div>
                    <div class="card-item"><span class="label">原始地址：</span>{{ item.originalAddress }}</div>
                    <div class="card-item"><span class="label">经销商：</span>{{ item.companyName }}</div>
                    <div class="card-item"><span class="label">添加时间：</span>{{ formatDateTime(item.addTime) }}</div>
                  </div>
                  <div class="card-footer">
                    <el-button type="success" size="small" @click="showDetail(item)">详情</el-button>
                    <el-button type="primary" size="small" :disabled="item.status !== 1" @click="cleanDetail(item)">清洗</el-button>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </div>

        <!-- 分页 -->
        <div class="fixed-pagination" v-if="cleanData.list?.length">
          <div class="pagination-content">
            <el-button size="small" plain :disabled="!cleanData.hasPreviousPage" @click="pageNumber > 1 && (pageNumber--, fetchCleanData())">
              上一页
            </el-button>
            <span class="page-info">
              第 {{ cleanData.pageNum }} 页 / 共 {{ cleanData.pages }} 页 (共 {{ cleanData.total }} 条)
            </span>
            <el-button size="small" plain :disabled="!cleanData.hasNextPage" @click="pageNumber < cleanData.pages && (pageNumber++, fetchCleanData())">
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

    <!-- 详情弹窗 -->
    <el-dialog v-model="showDetailModal" title="数据详情" width="500px" destroy-on-close>
      <template #title>
        <div class="custom-dialog-title">数据详情</div>
      </template>

      <div class="detail-container">
        <div class="detail-row" v-for="(item, key) in detailFields" :key="key">
          <label>{{ item.label }}：</label>
          <span>
            <template v-if="item.copy">
              <el-link type="primary" :underline="false" @click="copyText(item.value(currentCleanItem))">
                {{ item.value(currentCleanItem) }}
              </el-link>
            </template>
            <template v-else>
              {{ item.value(currentCleanItem) }}
            </template>
          </span>
        </div>
      </div>

      <template #footer>
        <el-button @click="showDetailModal = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 清洗处理弹窗 -->
    <el-dialog
        v-model="cleanDetailModal"
        width="70%"
        :max-width="1400"
        :close-on-click-modal="false"
        destroy-on-close
        class="update-dialog"
    >
      <template #title>
        <div class="custom-dialog-title">数据清洗</div>
      </template>

      <!-- 顶部固定操作栏 -->
      <div class="dialog-fixed-header">
        <div class="header-actions">
          <el-button @click="resetForm">重置匹配</el-button>
          <el-button type="primary" :loading="isFinding" @click="findDaKuData">
            {{ isFinding ? '匹配中...' : '匹配大库' }}
          </el-button>
          <el-button type="primary" :loading="isSaving" @click="saveChanges">
            {{ isSaving ? '保存中...' : '保存清洗' }}
          </el-button>
          <el-button @click="closeCleanModal">取消</el-button>
        </div>
      </div>

      <!-- 可上下滚动的表单内容区 -->
      <div class="dialog-scroll-body">
        <el-form :model="currentCleanDetail" label-width="140px">
          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="批次编号"><el-input v-model="currentCleanDetail.batchCode" readonly /></el-form-item>
              <el-form-item label="dataId"><el-input v-model="currentCleanDetail.dataId" readonly /></el-form-item>
              <el-form-item label="数据类型">
                <el-input :value="currentCleanDetail.dataType === '1' ? '存量' : currentCleanDetail.dataType === '2' ? '增量' : '未知'" readonly />
              </el-form-item>
              <el-form-item label="原始名称"><el-input v-model="currentCleanDetail.originalName" readonly /></el-form-item>
              <el-form-item label="原始编码"><el-input v-model="currentCleanDetail.dataCode" readonly /></el-form-item>
              <el-form-item label="原始省份"><el-input v-model="currentCleanDetail.originalProvince" readonly /></el-form-item>
              <el-form-item label="原始地址"><el-input v-model="currentCleanDetail.originalAddress" readonly /></el-form-item>
              <el-form-item label="经销商"><el-input v-model="currentCleanDetail.companyName" readonly /></el-form-item>
              <el-form-item label="豪森编码"><el-input v-model="currentCleanDetail.haosenCode" readonly /></el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="清洗备注"><el-input v-model="currentCleanDetail.cleanRemark"  /></el-form-item>
              <el-form-item label="机构类型">
                <el-select v-model="currentCleanDetail.institutionType">
                  <el-option label="医院" value="医院" />
                  <el-option label="药店" value="药店" />
                  <el-option label="商业" value="商业" />
                </el-select>
              </el-form-item>
              <el-form-item label="keyId"><el-input v-model="currentCleanDetail.keyid"   /></el-form-item>
              <el-form-item label="标准名称"><el-input v-model="currentCleanDetail.name"   /></el-form-item>
              <el-form-item label="历史名称"><el-input v-model="currentCleanDetail.nameHistory"   /></el-form-item>
              <el-form-item label="省"><el-input v-model="currentCleanDetail.province"   /></el-form-item>
              <el-form-item label="省ID"><el-input v-model="currentCleanDetail.provinceid"  /></el-form-item>
              <el-form-item label="市"><el-input v-model="currentCleanDetail.city"   /></el-form-item>
              <el-form-item label="市ID"><el-input v-model="currentCleanDetail.cityid"   /></el-form-item>
              <el-form-item label="区县"><el-input v-model="currentCleanDetail.area"  /></el-form-item>
              <el-form-item label="区县ID"><el-input v-model="currentCleanDetail.areaid"   /></el-form-item>
              <el-form-item label="地址"><el-input v-model="currentCleanDetail.address" /></el-form-item>
              <el-form-item label="等级">
                <el-select v-model="currentCleanDetail.level">
                  <el-option label="未定级" value="未定级" />
                  <el-option label="一级" value="一级" />
                  <el-option label="二级" value="二级" />
                  <el-option label="三级" value="三级" />
                </el-select>
              </el-form-item>
              <el-form-item label="等次">
                <el-select v-model="currentCleanDetail.grade">
                  <el-option label="未定等" value="未定等" />
                  <el-option label="甲等" value="甲等" />
                  <el-option label="乙等" value="乙等" />
                  <el-option label="丙等" value="丙等" />
                </el-select>
              </el-form-item>
              <el-form-item label="所有制">
                <el-select v-model="currentCleanDetail.publicflag">
                  <el-option label="公立" value="公立" />
                  <el-option label="民营" value="民营" />
                </el-select>
              </el-form-item>
              <el-form-item label="类别"><el-input v-model="currentCleanDetail.classify"   /></el-form-item>
              <el-form-item label="总分院kid"><el-input v-model="currentCleanDetail.generalBranchKid"   /></el-form-item>
              <el-form-item label="总分院名称"><el-input v-model="currentCleanDetail.generalBranchName"   /></el-form-item>
              <el-form-item label="是否军队医院">
                <el-select v-model="currentCleanDetail.militaryHos">
                  <el-option label="是" value="1" />
                  <el-option label="否" value="0" />
                </el-select>
              </el-form-item>
              <el-form-item label="登记号"><el-input v-model="currentCleanDetail.regcode"   /></el-form-item>
              <el-form-item label="有效期"><el-input v-model="currentCleanDetail.validity"  /></el-form-item>
              <el-form-item label="诊疗科室"><el-input v-model="currentCleanDetail.subjects"   /></el-form-item>
              <el-form-item label="法人代表"><el-input v-model="currentCleanDetail.legalperson"  /></el-form-item>
              <el-form-item label="统一社会信用代码"><el-input v-model="currentCleanDetail.usci"  /></el-form-item>
              <el-form-item label="经营方式">
                <el-select v-model="currentCleanDetail.operation">
                  <el-option label="零售" value="零售" />
                  <el-option label="批发" value="批发" />
                  <el-option label="连锁" value="连锁" />
                </el-select>
              </el-form-item>
              <el-form-item label="经营范围"><el-input v-model="currentCleanDetail.scope" /></el-form-item>
              <el-form-item label="总分店kid"><el-input v-model="currentCleanDetail.mainBranchKid"  /></el-form-item>
              <el-form-item label="总分店名称"><el-input v-model="currentCleanDetail.mainBranchName"  /></el-form-item>
              <el-form-item label="成立日期"><el-input v-model="currentCleanDetail.createDate"   /></el-form-item>
              <el-form-item label="注册资金"><el-input v-model="currentCleanDetail.registCapi"   /></el-form-item>
              <el-form-item label="企业类型"><el-input v-model="currentCleanDetail.econKind"   /></el-form-item>
              <el-form-item label="登记状态"><el-input v-model="currentCleanDetail.signStatus"    /></el-form-item>
              <el-form-item label="所属行业"><el-input v-model="currentCleanDetail.industry"   /></el-form-item>
              <el-form-item label="登记机关"><el-input v-model="currentCleanDetail.belong"  /></el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Grid, CopyDocument } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// 视图模式：table 或 card
const viewMode = ref('table')

const cleanData = reactive({
  list: [],
  total: 0,
  pages: 0,
  pageNum: 1,
  hasNextPage: false,
  hasPreviousPage: false
})

const loading = ref(false)
const exporting = ref(false)
const pageSize = ref(20)
const pageNumber = ref(1)

const searchForm = reactive({
  batchCode: '',
  dataCode: '',
  originalName: '',
  dataId: '',
  originalProvince: '',
  companyName: '',
  haosenCode: '',
  dataType: '',
  status: '',
  cleanStatus: ''
})

const columns = ref([
  { label: '批次编号', prop: 'batchCode'  },
  { label: 'dataId', prop: 'dataId' },
  { label: '原始编码', prop: 'dataCode' },
  { label: '数据类型', prop: 'dataType'  },
  { label: '原始省份', prop: 'originalProvince' },
  { label: '原始名称', prop: 'originalName' },
  { label: '原始地址', prop: 'originalAddress'  },
  { label: '经销商', prop: 'companyName' },
  { label: '清洗状态', prop: 'status'  },
  { label: '抽取状态', prop: 'cleanStatus' },
  { label: '操作', fixed: 'right', width: 140 }
])

const showDetailModal = ref(false)
const cleanDetailModal = ref(false)
const currentCleanItem = ref({})
const originalCleanItem = ref({})
const currentCleanDetail = ref({})
const isSaving = ref(false)
const isFinding = ref(false)

const detailFields = {
  batchCode: { label: '批次编号', value: a => a.batchCode, copy: true },
  dataId: { label: 'dataId', value: a => a.dataId, copy: true },
  dataType: { label: '数据类型', value: a => a.dataType === '1' ? '存量' : a.dataType === '2' ? '增量' : '' },
  dataCode: { label: '原始编码', value: a => a.dataCode, copy: true },
  originalName: { label: '原始名称', value: a => a.originalName, copy: true },
  originalProvince: { label: '原始省份', value: a => a.originalProvince },
  originalAddress: { label: '原始地址', value: a => a.originalAddress  },
  companyName: { label: '经销商', value: a => a.companyName },
  haosenCode: { label: '豪森编码', value: a => a.haosenCode  },
  status: { label: '清洗状态', value: a => formatStatus(a.status) },
  cleanStatus: { label: '抽取状态', value: a => formatCleanStatus(a.cleanStatus) },
  addTime: { label: '添加时间', value: a => formatDateTime(a.addTime) }
}

// 格式化状态显示
const formatStatus = (status) => {
  switch (status) {
    case 1: return '待清洗'
    case 2: return '已清洗'

  }
}


const formatCleanStatus = (cleanStatus) => {
  switch (cleanStatus) {
    case 0: return '待抽取'
    case 1: return '已抽取'

  }
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  switch (status) {
    case 0: return 'info'      // 待推送 - 灰色
    case 1: return 'warning'   // 推送中 - 黄色
    case 2: return 'success'   // 已推送 - 绿色
    case 3: return 'danger'    // 推送失败 - 红色
    default: return ''
  }
}

const getCleanStatusTagType = (cleanStatus) => {
  switch (cleanStatus) {
    case 0: return 'info'      // 未清洗 - 灰色
    case 1: return 'warning'   // 清洗中 - 黄色
    case 2: return 'success'   // 已清洗 - 绿色
    case 3: return 'danger'    // 清洗失败 - 红色
    default: return ''
  }
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  try {
    const date = new Date(dateTime)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false
    }).replace(/\//g, '-')
  } catch {
    return dateTime
  }
}

const fetchCleanData = async () => {
  loading.value = true
  try {
    // 构建查询参数，过滤空值
    const params = {
      pageNum: pageNumber.value,
      pageSize: pageSize.value,
      ...Object.fromEntries(
          Object.entries(searchForm).filter(([_, value]) => value !== '' && value !== null && value !== undefined)
      )
    }

    const { data } = await axios.get('/api/cleanData/selectCleanData', { params })

    if (data.code === 200) {
      Object.assign(cleanData, data.data)
      pageNumber.value = data.data.pageNum
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

const handleSearch = () => {
  pageNumber.value = 1
  fetchCleanData()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => (searchForm[key] = ''))
  pageNumber.value = 1
  fetchCleanData()
}

const handlePageSizeChange = () => {
  pageNumber.value = 1
  fetchCleanData()
}

const copyText = async (text) => {
  if (!text) return
  try {
    await navigator.clipboard.writeText(text)
    ElMessage.success('已复制')
  } catch {
    ElMessage.error('复制失败')
  }
}

const toExcel = async () => {
  if (exporting.value) return
  exporting.value = true
  try {

    // 过滤空值参数
    const params = Object.fromEntries(
        Object.entries(searchForm).filter(([_, value]) => value !== '' && value !== null && value !== undefined)
    )
    const { data: jsonBlob } = await axios.get('/api/cleanData/exportCleanData', { params, responseType: 'blob' })
    const jsonText = await jsonBlob.text()
    const { data: base64 } = JSON.parse(jsonText)
    const byteChars = atob(base64)
    const byteNums = new Uint8Array(byteChars.length)
    for (let i = 0; i < byteChars.length; i++) byteNums[i] = byteChars.charCodeAt(i)
    const excelBlob = new Blob([byteNums], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = URL.createObjectURL(excelBlob)
    const link = document.createElement('a')
    link.href = url
    link.download = `清洗数据_${new Date().toLocaleDateString()}.xlsx`
    link.click()
    URL.revokeObjectURL(url)
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

const showDetail = (row) => {
  currentCleanItem.value = { ...row }
  showDetailModal.value = true
}

const cleanDetail = (row) => {
  originalCleanItem.value = JSON.parse(JSON.stringify(row))
  currentCleanDetail.value = JSON.parse(JSON.stringify(row))
  cleanDetailModal.value = true
}

const closeCleanModal = () => {
  cleanDetailModal.value = false
  currentCleanDetail.value = {}
  originalCleanItem.value = {}
}

const saveChanges = async () => {
  if (isSaving.value) return
  isSaving.value = true
  try {
    const { data } = await axios.post('/api/cleanData/handleCleanData', currentCleanDetail.value)
    if (data.code === 200) {
      ElMessage.success('保存清洗成功')
      closeCleanModal()
      fetchCleanData()
    } else {
      ElMessage.error(data.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('网络异常')
  } finally {
    isSaving.value = false
  }
}

const findDaKuData = async () => {
  if (isFinding.value) return
  if (!currentCleanDetail.value.keyid) {
    ElMessage.warning('请先输入keyid')
    return
  }
  isFinding.value = true
  try {
    const { data } = await axios.get('/api/updateData/findDaKuData', { params: { keyid: currentCleanDetail.value.keyid } })
    if (data.code === 200 && data.data != null) {
      // 先保存原始不可编辑字段
      const protectedFields = {
        batchCode: currentCleanDetail.value.batchCode,
        dataId: currentCleanDetail.value.dataId,
        dataType: currentCleanDetail.value.dataType,
        dataCode: currentCleanDetail.value.dataCode,
        originalName: currentCleanDetail.value.originalName,
        originalProvince: currentCleanDetail.value.originalProvince,
        originalAddress: currentCleanDetail.value.originalAddress,
        companyName: currentCleanDetail.value.companyName,
        haosenCode: currentCleanDetail.value.haosenCode,
        cleanRemark: currentCleanDetail.value.cleanRemark
      }

      // 更新其他字段
      Object.assign(currentCleanDetail.value, data.data)

      // 强制恢复原始字段
      Object.assign(currentCleanDetail.value, protectedFields)

      ElMessage.success('匹配成功，已填充')
    } else {
      ElMessage.error('未找到匹配数据')
    }
  } catch (error) {
    console.error('匹配失败:', error)
    ElMessage.error('匹配失败')
  } finally {
    isFinding.value = false
  }
}

const resetForm = () => {
  ElMessageBox.confirm('确定要重置所有修改吗？重置后将恢复为原始数据。', '确认重置', { type: 'warning' })
      .then(() => {
        currentCleanDetail.value = JSON.parse(JSON.stringify(originalCleanItem.value))
        ElMessage.success('已重置为原始数据')
      })
      .catch(() => {
        // 用户取消重置
      })
}

// 列宽拖拽
const resizing = ref(false)
const resizeColumnIndex = ref(null)
const startX = ref(0)
const startWidth = ref(0)

const startResize = (e, index) => {
  resizing.value = true
  resizeColumnIndex.value = index
  startX.value = e.clientX
  startWidth.value = columns.value[index].width
  document.addEventListener('mousemove', handleResize)
  document.addEventListener('mouseup', stopResize)
}

const handleResize = (e) => {
  if (!resizing.value) return
  const dx = e.clientX - startX.value
  const newWidth = Math.max(80, Math.min(600, startWidth.value + dx))
  columns.value[resizeColumnIndex.value].width = newWidth
}

const stopResize = () => {
  resizing.value = false
  document.removeEventListener('mousemove', handleResize)
  document.removeEventListener('mouseup', stopResize)
}

onMounted(() => {
  fetchCleanData()
})
</script>

<style scoped>
/* 样式保持不变，只修改了类名从 appeal 到 clean */
.appeal-data-view {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: min(1250px, 95vw);
  margin: 0 auto;
  background: var(--bg-secondary, #ffffff);
  overflow: hidden;
  font-size: 12px;
}

/* 整合的整体容器 */
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

/* 顶部操作区域 */
.top-operation-area {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  padding: 14px 18px 0;
  background: #ffffff;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  border-radius: 6px 6px 0 0;
  max-width: 100%;
  box-sizing: border-box;
}

/* 搜索条件盒子 */
.search-conditions-box {
  width: 100%;
  margin-bottom: 16px;
}

/* 操作按钮盒子 */
.action-buttons-box {
  width: 100%;
  background: #ffffff;
  border-radius: 4px;
  padding: 12px 16px;
  margin-bottom: 16px;
}

.action-buttons-content {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 16px;

}

.view-toggle {
  margin-left: auto;
}

/* 搜索表单样式 */
.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 8px;
  align-items: end;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0 !important;
  flex: 1 1 180px;
}

/* 2K屏幕优化 */
@media (min-width: 2000px) and (max-width: 2600px) {
  .appeal-data-view {
    max-width: min(1860px, 90vw);
  }
}

/* 超宽屏幕 */
@media (min-width: 2600px) {
  .appeal-data-view {
    max-width: min(2400px, 95vw);
  }
}


/* 搜索区域 - 现在在整合容器内 */

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

/* 按钮区域整体容器 - 用于靠右对齐 */
.form-actions-wrapper {
  display: flex;
  justify-content: flex-end;
  width: 100%;
}

/* 按钮本身容器 */
.form-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

/* 视图切换按钮组微调 */
.view-toggle {
  margin-left: 8px;
}



/* 数据内容区域 */
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

.appeal-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.appeal-card:hover {
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

.status-badges {
  display: flex;
  align-items: center;
}

.appeal-remark {
  margin-left: 8px;
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
  margin-top: auto;
  text-align: right;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

/* 分页区域 */
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

.th-content {
  position: relative;
  text-align: center;
  font-weight: 600;
}

.resize-handle {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  cursor: col-resize;
  opacity: 0;
  transition: opacity 0.2s;
}

.th-content:hover .resize-handle {
  opacity: 1;
  background: #409eff;
}

/* 详情弹窗样式 */
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
  color: #303133;
  line-height: 1.5;
}

.detail-row span {
  flex: 1;
  word-break: break-word;
  line-height: 1.5;
  color: #606266;
}

/* 申诉弹窗整体 */
.update-dialog :deep(.el-dialog) {
  display: flex;
  flex-direction: column;
  max-height: 90vh;
  margin: 5vh auto;
}

.custom-dialog-title {
  text-align: center;
  font-size: 18px;
  font-weight: 600;
  width: 100%;
  color: #303133;
}

.update-dialog :deep(.el-dialog__header) {
  padding: 16px 24px;
  border-bottom: 1px solid #ebeef5;
}

.dialog-fixed-header {
  flex-shrink: 0;
  padding: 14px 24px;
  background: var(--bg-secondary, #ffffff);
  border-bottom: 1px solid #ebeef5;
  position: sticky;
  top: 0;
  z-index: 10;
}

.header-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.dialog-scroll-body {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 20px 24px;
  box-sizing: border-box;
}

.dialog-scroll-body::-webkit-scrollbar {
  width: 8px;
}

.dialog-scroll-body::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 4px;
}

.dialog-scroll-body::-webkit-scrollbar-track {
  background: transparent;
}

.update-dialog :deep(.el-dialog__body) {
  flex: 1;
  padding: 0;
  overflow: hidden;
}
</style>


