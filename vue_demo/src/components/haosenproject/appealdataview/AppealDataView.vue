<template>
  <div class="appeal-data-view">
    <!-- 整合的搜索和数据区域 -->
    <div class="integrated-container">
      <!-- 搜索区域 -->
      <div class="fixed-search">
        <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent="handleSearch">
          <!-- 上半部分：所有搜索条件 -->
          <div class="search-conditions">
            <el-form-item label="原始编码">
              <el-input v-model="searchForm.dataCode" placeholder="请输入原始编码" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="原始名称">
              <el-input v-model="searchForm.originalName" placeholder="请输入原始名称" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="DataId">
              <el-input v-model="searchForm.dataId" placeholder="请输入DataId" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="KeyId">
              <el-input v-model="searchForm.keyid" placeholder="请输入KeyId" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="省份">
              <el-input v-model="searchForm.province" placeholder="请输入省份" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="市">
              <el-input v-model="searchForm.city" placeholder="请输入市" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="区县">
              <el-input v-model="searchForm.area" placeholder="请输入区县" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="输入名称">
              <el-input v-model="searchForm.name" placeholder="请输入名称" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="输入地址">
              <el-input v-model="searchForm.address" placeholder="请输入地址" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="添加日期">
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
      <div class="data-content" v-loading="loading">
        <div v-if="viewMode === 'table'" class="table-container">
          <el-table
              v-if="appealData.list?.length"
              :data="appealData.list"
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

                <template #default="{ row }">
                  <template v-if="index < 5">
                    <!-- 前5列为可复制的链接：dataId, dataCode, originalName, keyid, name -->
                    <el-link type="primary" :underline="false" @click="copyText(row[columns[index].prop])" :disabled="!row[columns[index].prop]">
                      {{ row[columns[index].prop] }}
                    </el-link>
                  </template>
                  <template v-else-if="index < 9">
                    <!-- 5-8列为普通文本：province, city, area, address -->
                    {{ row[columns[index].prop] }}
                  </template>
                  <template v-else-if="index === 9">
                    <!-- 第10列为申诉原因 -->
                    {{ row[columns[index].prop] }}
                  </template>
                  <template v-else>
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
                          <el-dropdown-item :command="{action: 'appeal', row: row}" class="dropdown-item update-item">
                            <el-icon class="menu-icon"><EditPen /></el-icon>
                            <span class="menu-text">申诉处理</span>
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
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
            <el-empty v-if="!appealData.list?.length" description="没有找到匹配的数据" :image-size="120" class="no-data-container" />
            <el-row :gutter="20" v-else>
              <el-col v-for="appeal in appealData.list" :key="appeal.dataId" :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                <el-card class="appeal-card" shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <div class="card-title">
                        <el-link type="primary" :underline="false" @click="copyText(appeal.name)">
                          {{ appeal.name }}
                        </el-link>
                      </div>
                      <div class="appeal-remark" v-if="appeal.appealRemark">
                        <el-tag type="warning" size="small" effect="dark">申诉</el-tag>
                      </div>
                    </div>
                  </template>
                  <div class="card-body">
                    <div class="card-item"><span class="label">dataId：</span><el-link type="primary" :underline="false" @click="copyText(appeal.dataId)">{{ appeal.dataId }}</el-link></div>
                    <div class="card-item"><span class="label">原始编码：</span><el-link type="primary" :underline="false" @click="copyText(appeal.dataCode)">{{ appeal.dataCode }}</el-link></div>
                    <div class="card-item"><span class="label">原始名称：</span><el-link type="primary" :underline="false" @click="copyText(appeal.originalName)">{{ appeal.originalName }}</el-link></div>
                    <div class="card-item"><span class="label">keyId：</span><el-link type="primary" :underline="false" @click="copyText(appeal.keyid)">{{ appeal.keyid }}</el-link></div>
                    <div class="card-item"><span class="label">省份：</span>{{ appeal.province }}</div>
                    <div class="card-item"><span class="label">城市：</span>{{ appeal.city }}</div>
                    <div class="card-item"><span class="label">区县：</span>{{ appeal.area }}</div>
                    <div class="card-item"><span class="label">地址：</span>{{ appeal.address }}</div>
                    <div class="card-item" v-if="appeal.appealRemark"><span class="label">申诉原因：</span>{{ appeal.appealRemark }}</div>
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
                          <el-dropdown-item :command="{action: 'detail', row: appeal}" class="dropdown-item detail-item">
                            <el-icon class="menu-icon"><Search /></el-icon>
                            <span class="menu-text">详情查看</span>
                          </el-dropdown-item>
                          <el-dropdown-item :command="{action: 'appeal', row: appeal}" class="dropdown-item update-item">
                            <el-icon class="menu-icon"><EditPen /></el-icon>
                            <span class="menu-text">申诉处理</span>
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </div>
                </el-card>
              </el-col>
            </el-row>
        </div>

        <!-- 分页 -->
        <div class="fixed-pagination" v-if="appealData.list?.length">
          <div class="pagination-content">
            <el-button size="small" plain :disabled="!appealData.hasPreviousPage" @click="pageNumber > 1 && (pageNumber--, fetchAppealData())">
              上一页
            </el-button>
            <div class="page-jumper">
              <span>跳转到</span>
              <el-input-number
                v-model="jumpPageNumber"
                :min="1"
                :max="appealData.pages"
                size="small"
                controls-position="right"
                @change="handleJumpPage"
                class="page-input"
              />
              <span class="page-total">页，共 {{ appealData.pages }} 页 ({{ appealData.total }} 条)</span>
            </div>
            <el-button size="small" plain :disabled="!appealData.hasNextPage" @click="pageNumber < appealData.pages && (pageNumber++, fetchAppealData())">
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
    </div>

    <!-- 详情弹窗 -->
    <el-dialog v-model="showDetailModal" title="申诉详情" width="500px" destroy-on-close>
      <template #title>
        <div class="custom-dialog-title">申诉详情</div>
      </template>

      <div class="detail-container">
        <div class="detail-row" v-for="(item, key) in detailFields" :key="key">
          <label>{{ item.label }}：</label>
          <span>
            <template v-if="item.copy">
              <el-link type="primary" :underline="false" @click="copyText(item.value(currentAppeal))">
                {{ item.value(currentAppeal) }}
              </el-link>
            </template>
            <template v-else>
              {{ item.value(currentAppeal) }}
            </template>
          </span>
        </div>
      </div>

      <template #footer>
        <el-button @click="showDetailModal = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 申诉处理弹窗 -->
    <el-dialog
        v-model="appealDetailModal"
        width="70%"
        :max-width="1400"
        :close-on-click-modal="false"
        destroy-on-close
        class="update-dialog"
    >


      <!-- 顶部固定操作栏 -->
      <div class="dialog-fixed-header">
        <div class="header-actions">
          <div class="header-main">
            <div class="custom-dialog-title-appeal">数据申诉</div>
          </div>

          <el-button @click="resetForm">重置匹配</el-button>
          <el-button type="primary" :loading="isFinding" @click="findDaKuData">
            {{ isFinding ? '匹配中...' : '匹配大库' }}
          </el-button>
          <el-button type="primary" :loading="isSaving" @click="saveChanges">
            {{ isSaving ? '推送中...' : '提交推送' }}
          </el-button>
          <el-button @click="closeAppealModal">取消</el-button>
        </div>
      </div>

      <!-- 可上下滚动的表单内容区 -->
      <div class="dialog-scroll-body">
        <el-form :model="currentAppealDetail" label-width="140px">
          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="批次编号"><el-input v-model="currentAppealDetail.batchCode" readonly /></el-form-item>
              <el-form-item label="dataId"><el-input v-model="currentAppealDetail.dataId" readonly /></el-form-item>
              <el-form-item label="数据类型">
                <el-input :value="currentAppealDetail.dataType === '1' ? '存量' : currentAppealDetail.dataType === '2' ? '增量' : '未知'" readonly />
              </el-form-item>
              <el-form-item label="原始名称"><el-input v-model="currentAppealDetail.originalName" readonly /></el-form-item>
              <el-form-item label="原始编码"><el-input v-model="currentAppealDetail.dataCode" readonly /></el-form-item>
              <el-form-item label="原始省份"><el-input v-model="currentAppealDetail.originalProvince" readonly /></el-form-item>
              <el-form-item label="原始地址"><el-input v-model="currentAppealDetail.originalAddress" readonly /></el-form-item>
              <el-form-item label="经销商"><el-input v-model="currentAppealDetail.companyName" readonly /></el-form-item>
              <el-form-item label="豪森上传的编码"><el-input v-model="currentAppealDetail.haosenCode" readonly /></el-form-item>
              <el-form-item label="申诉原因">
                <el-input
                  v-model="currentAppealDetail.appealRemark"
                  readonly
                  type="textarea"
                  :rows="3"
                  resize="none"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="申诉解决">
                <el-input
                  v-model="currentAppealDetail.solveRemark"
                  type="textarea"
                  :rows="3"
                  resize="none"
                />
              </el-form-item>
              <el-form-item label="机构类型"  :rules="[{ required: true, message: '请选择机构类型', trigger: 'change' }]">
                <el-select v-model="currentAppealDetail.orgType" @change="handleOrgTypeChange">
                  <el-option label="医院" value="医院" />
                  <el-option label="药店" value="药店" />
                  <el-option label="商业" value="商业" />
                </el-select>
              </el-form-item>
              <el-form-item label="keyId"><el-input v-model="currentAppealDetail.keyid" /></el-form-item>
              <el-form-item label="标准名称"><el-input v-model="currentAppealDetail.name" /></el-form-item>
              <el-form-item label="历史名称"><el-input v-model="currentAppealDetail.nameHistory" /></el-form-item>
              <el-form-item label="省"><el-input v-model="currentAppealDetail.province" /></el-form-item>
              <el-form-item label="省ID"><el-input v-model="currentAppealDetail.provinceId" /></el-form-item>
              <el-form-item label="市"><el-input v-model="currentAppealDetail.city" /></el-form-item>
              <el-form-item label="市ID"><el-input v-model="currentAppealDetail.cityId" /></el-form-item>
              <el-form-item label="区县"><el-input v-model="currentAppealDetail.area" /></el-form-item>
              <el-form-item label="区县ID"><el-input v-model="currentAppealDetail.areaId" /></el-form-item>
              <el-form-item label="地址"><el-input v-model="currentAppealDetail.address" /></el-form-item>
              <el-form-item label="等级">
                <el-select v-model="currentAppealDetail.level" :disabled="isFieldDisabled('level')">
                  <el-option label="未定级" value="未定级" />
                  <el-option label="一级" value="一级" />
                  <el-option label="二级" value="二级" />
                  <el-option label="三级" value="三级" />
                </el-select>
              </el-form-item>
              <el-form-item label="等次">
                <el-select v-model="currentAppealDetail.grade" :disabled="isFieldDisabled('grade')">
                  <el-option label="未定等" value="未定等" />
                  <el-option label="甲等" value="甲等" />
                  <el-option label="乙等" value="乙等" />
                  <el-option label="丙等" value="丙等" />
                </el-select>
              </el-form-item>
              <el-form-item label="所有制">
                <el-select v-model="currentAppealDetail.publicflag" :disabled="isFieldDisabled('publicflag')">
                  <el-option label="公立" value="公立" />
                  <el-option label="民营" value="民营" />
                </el-select>
              </el-form-item>
              <el-form-item label="类别"><el-input v-model="currentAppealDetail.classify" /></el-form-item>
              <el-form-item label="总分院kid"><el-input v-model="currentAppealDetail.generalBranchKid" :disabled="isFieldDisabled('generalBranchKid')" /></el-form-item>
              <el-form-item label="总分院名称"><el-input v-model="currentAppealDetail.generalBranchName" :disabled="isFieldDisabled('generalBranchName')" /></el-form-item>
              <el-form-item label="是否军队医院">
                <el-select v-model="currentAppealDetail.militaryHos" :disabled="isFieldDisabled('militaryHos')">
                  <el-option label="是" value="1" />
                  <el-option label="否" value="0" />
                </el-select>
              </el-form-item>
              <el-form-item label="登记号"><el-input v-model="currentAppealDetail.regcode" :disabled="isFieldDisabled('regcode')" /></el-form-item>
              <el-form-item label="有效期"><el-input v-model="currentAppealDetail.validity" :disabled="isFieldDisabled('validity')" /></el-form-item>
              <el-form-item label="诊疗科室"><el-input v-model="currentAppealDetail.subjects" :disabled="isFieldDisabled('subjects')" /></el-form-item>
              <el-form-item label="法人代表"><el-input v-model="currentAppealDetail.legalperson" /></el-form-item>
              <el-form-item label="统一社会信用代码"><el-input v-model="currentAppealDetail.usci" /></el-form-item>
              <el-form-item label="经营方式">
                <el-select v-model="currentAppealDetail.operation" :disabled="isFieldDisabled('operation')">
                  <el-option label="零售" value="零售" />
                  <el-option label="批发" value="批发" />
                  <el-option label="连锁" value="连锁" />
                </el-select>
              </el-form-item>
              <el-form-item label="经营范围"><el-input v-model="currentAppealDetail.scope" :disabled="isFieldDisabled('scope')" /></el-form-item>
              <el-form-item label="总分店kid"><el-input v-model="currentAppealDetail.mainBranchKid" :disabled="isFieldDisabled('mainBranchKid')" /></el-form-item>
              <el-form-item label="总分店名称"><el-input v-model="currentAppealDetail.mainBranchName" :disabled="isFieldDisabled('mainBranchName')" /></el-form-item>
              <el-form-item label="成立日期"><el-input v-model="currentAppealDetail.createDate" :disabled="isFieldDisabled('createDate')" /></el-form-item>
              <el-form-item label="注册资金"><el-input v-model="currentAppealDetail.registCapi" :disabled="isFieldDisabled('registCapi')" /></el-form-item>
              <el-form-item label="企业类型"><el-input v-model="currentAppealDetail.econKind" :disabled="isFieldDisabled('econKind')" /></el-form-item>
              <el-form-item label="登记状态"><el-input v-model="currentAppealDetail.signStatus" :disabled="isFieldDisabled('signStatus')" /></el-form-item>
              <el-form-item label="所属行业"><el-input v-model="currentAppealDetail.industry" :disabled="isFieldDisabled('industry')" /></el-form-item>
              <el-form-item label="登记机关"><el-input v-model="currentAppealDetail.belong" :disabled="isFieldDisabled('belong')" /></el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </el-dialog>


  </div>
</template>

<script setup>
// ==================== 依赖导入 ====================
import '@/assets/css/dark-mode.css'
import { ref, reactive, onMounted, computed } from 'vue'
import { Grid, CopyDocument, Operation, ArrowDown, Search, EditPen } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// ==================== 视图状态管理 ====================
const viewMode = ref('table')

// ==================== 数据状态管理 ====================
const appealData = reactive({
  list: [],
  total: 0,
  pages: 0,
  pageNum: 1,
  hasNextPage: false,
  hasPreviousPage: false
})

// ==================== 加载状态管理 ====================
const loading = ref(false)
const exporting = ref(false)

// ==================== 分页配置 ====================
const pageSize = ref(20)
const pageNumber = ref(1)
const jumpPageNumber = ref(1)

// ==================== 搜索表单配置 ====================
const searchForm = reactive({
  dataCode: '',
  originalName: '',
  dataId: '',
  keyid: '',
  hsCode: '',
  province: '',
  city: '',
  area: '',
  name: '',
  address: '',
  startTime: '',
  endTime: ''
})

// ==================== 日期范围选择器 ====================
const dateRange = ref([])

// ==================== 表格列配置 ====================
const columns = ref([
  { label: 'dataId', prop: 'dataId', minWidth: 120 },
  { label: '原始编码', prop: 'dataCode', minWidth: 120 },
  { label: '原始名称', prop: 'originalName', minWidth: 150},
  { label: 'keyId', prop: 'keyid', minWidth: 120 },
  { label: '标准名称', prop: 'name', minWidth: 150},
  { label: '省份', prop: 'province', minWidth: 75},
  { label: '城市', prop: 'city', minWidth: 75 },
  { label: '区县', prop: 'area', minWidth: 75 },
  { label: '地址', prop: 'address', minWidth: 200 },
  { label: '申诉原因', prop: 'appealRemark', minWidth: 120 },
  { label: '操作', fixed: 'right' , width: 120, resizable: false }
])

// ==================== 弹窗状态控制 ====================
const showDetailModal = ref(false)
const appealDetailModal = ref(false)

// ==================== 当前操作数据对象 ====================
const currentAppeal = ref({})
const originalAppeal = ref({})
const currentAppealDetail = ref({})

// ==================== 申诉处理状态 ====================
const isSaving = ref(false)
const isFinding = ref(false)

// ==================== 机构类型字段禁用配置 ====================
const disabledFields = reactive({
  // 医院禁用的字段
  hospitalDisabled: {
    operation: true,
    scope: true,
    mainBranchKid: true,
    mainBranchName: true,
    createDate: true,
    registCapi: true,
    econKind: true,
    signStatus: true,
    industry: true,
    belong: true
  },
  // 药店禁用的字段
  pharmacyDisabled: {
    level: true,
    grade: true,
    publicflag: true,
    generalBranchKid: true,
    generalBranchName: true,
    militaryHos: true,
    regcode: true,
    validity: true,
    subjects: true
  },
  // 商业禁用的字段
  commercialDisabled: {
    level: true,
    grade: true,
    publicflag: true,
    generalBranchKid: true,
    generalBranchName: true,
    militaryHos: true,
    regcode: true,
    validity: true,
    subjects: true
  }
})

// ==================== 计算当前机构类型应禁用的字段 ====================
const getDisabledFields = computed(() => {
  const orgType = currentAppealDetail.value.orgType
  if (orgType === '医院') {
    return disabledFields.hospitalDisabled
  } else if (orgType === '药店') {
    return disabledFields.pharmacyDisabled
  } else if (orgType === '商业') {
    return disabledFields.commercialDisabled
  } else {
    return {}
  }
})

// ==================== 详情弹窗字段配置 ====================
const detailFields = {
  batchCode: { label: '批次编号', value: a => a.batchCode, copy: true },
  dataId: { label: 'dataId', value: a => a.dataId, copy: true },
  dataType: { label: '数据类型', value: a => a.dataType === '1' ? '存量' : a.dataType === '2' ? '增量' : '未知' },
  dataCode: { label: '原始数据编码', value: a => a.dataCode, copy: true },
  originalName: { label: '原始数据名称', value: a => a.originalName, copy: true },
  originalProvince: { label: '原始省份', value: a => a.originalProvince || a.province },
  originalAddress: { label: '原始地址', value: a => a.originalAddress , copy: true },
  companyName: { label: '经销商', value: a => a.companyName },
  haosenCode: { label: '豪森上传的编码', value: a => a.haosenCode},
  appealRemark: { label: '申诉原因', value: a => a.appealRemark },
  solveRemark: { label: '申诉解决', value: a => a.solveRemark },
  orgType: { label: '机构类型', value: a => a.orgType },
  keyid: { label: 'keyId', value: a => a.keyid, copy: true },
  name: { label: '医院名称', value: a => a.name, copy: true },
  nameHistory: { label: '历史名称', value: a => a.nameHistory },
  province: { label: '省', value: a => a.province },
  provinceId: { label: '省份ID', value: a => a.provinceId },
  city: { label: '市', value: a => a.city },
  cityId: { label: '市ID', value: a =>  a.cityId },
  area: { label: '区县', value: a => a.area },
  areaId: { label: '区县ID', value: a =>  a.areaId },
  address: { label: '地址', value: a => a.address },
  level: { label: '等级', value: a => a.level },
  grade: { label: '等次', value: a => a.grade },
  publicflag: { label: '所有制', value: a => a.publicflag },
  classify: { label: '类别', value: a => a.classify  },
  generalBranchKid: { label: '总分院kid', value: a => a.generalBranchKid },
  generalBranchName: { label: '总分院名称', value: a => a.generalBranchName },
  militaryHos: {
    label: '军队医院',
    value: a => a.militaryHos === '1' ? '是' : a.militaryHos === '0' ? '否' : ''
  },
  regcode: { label: '登记号', value: a => a.regcode },
  validity: { label: '有效期', value: a => a.validity },
  subjects: { label: '诊疗科室', value: a => a.subjects },
  legalperson: { label: '法人代表', value: a => a.legalperson },
  usci: { label: '统一社会信用代码', value: a => a.usci },
  operation: { label: '经营方式', value: a => a.operation },
  scope: { label: '经营范围', value: a => a.scope },
  mainBranchKid: { label: '总分店kid', value: a => a.mainBranchKid },
  mainBranchName: { label: '总分店名称', value: a => a.mainBranchName },
  createDate: { label: '成立时间', value: a => a.createDate },
  registCapi: { label: '注册资金', value: a => a.registCapi },
  econKind: { label: '企业类型', value: a => a.econKind },
  signStatus: { label: '登记状态', value: a => a.signStatus },
  industry: { label: '所属行业', value: a => a.industry },
  belong: { label: '登记机关', value: a => a.belong }
}

// ==================== 数据加载相关函数 ====================
const fetchAppealData = async () => {
  loading.value = true
  try {

    const params = {
      pageNum: pageNumber.value,
      pageSize: pageSize.value,
      ...Object.fromEntries(
          Object.entries(searchForm).filter(([, value]) => value !== '' && value !== null && value !== undefined)
      )
    }

    const { data } = await axios.get('/api/haosen/appealData/getAppealData', { params })
    if (data.code === 200) {
      Object.assign(appealData, data.data)
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
const handleSearch = () => {
  // 同步日期范围到 searchForm
  if (dateRange.value && dateRange.value.length === 2) {
    searchForm.startTime = dateRange.value[0]
    searchForm.endTime = dateRange.value[1]
  } else {
    searchForm.startTime = ''
    searchForm.endTime = ''
  }

  pageNumber.value = 1
  jumpPageNumber.value = 1
  fetchAppealData()
}

// 跳转到指定页
const handleJumpPage = () => {
  if (jumpPageNumber.value >= 1 && jumpPageNumber.value <= appealData.pages) {
    pageNumber.value = jumpPageNumber.value
    fetchAppealData()
  }
}

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => (searchForm[key] = ''))
  dateRange.value = []
  pageNumber.value = 1
  fetchAppealData()
}

const handlePageSizeChange = () => {
  pageNumber.value = 1
  fetchAppealData()
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

// ==================== 数据导出函数 ====================
const toExcel = async () => {
  if (exporting.value) return
  exporting.value = true
  try {

    // 过滤空值参数
    const params = Object.fromEntries(
        Object.entries(searchForm).filter(([, value]) => value !== '' && value !== null && value !== undefined)
    )

    const { data: jsonBlob } = await axios.get('/api/haosen/appealData/exportAppealData', { params, responseType: 'blob' })
    const jsonText = await jsonBlob.text()
    const { data: base64 } = JSON.parse(jsonText)
    const byteChars = atob(base64)
    const byteNums = new Uint8Array(byteChars.length)
    for (let i = 0; i < byteChars.length; i++) byteNums[i] = byteChars.charCodeAt(i)
    const excelBlob = new Blob([byteNums], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = URL.createObjectURL(excelBlob)
    const link = document.createElement('a')
    link.href = url
    link.download = `豪森申诉数据_${new Date().toLocaleDateString()}.xlsx`
    link.click()
    URL.revokeObjectURL(url)

  } catch {
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

// ==================== 弹窗管理函数 ====================
const showDetail = (row) => {
  currentAppeal.value = { ...row }
  showDetailModal.value = true
}

const appealDetail = (row) => {
  originalAppeal.value = JSON.parse(JSON.stringify(row))
  currentAppealDetail.value = JSON.parse(JSON.stringify(row))
  appealDetailModal.value = true
}

const closeAppealModal = () => {
  appealDetailModal.value = false
  currentAppealDetail.value = {}
  originalAppeal.value = {}
}

// ==================== 字段控制相关函数 ====================
// 清除被禁用的字段的值
const clearDisabledFields = () => {
  const disabledFieldNames = Object.keys(getDisabledFields.value).filter(field => getDisabledFields.value[field]);

  disabledFieldNames.forEach(field => {
    if (field in currentAppealDetail.value) {
      currentAppealDetail.value[field] = '';
    }
  });
};


const handleOrgTypeChange = () => {
  // 机构类型改变时清除被禁用的字段的值
  clearDisabledFields();
};

// 检查字段是否应该被禁用
const isFieldDisabled = (field) => {
  return getDisabledFields.value[field];
};

// ==================== 申诉处理相关函数 ====================
const saveChanges = async () => {
  if (isSaving.value) return
  isSaving.value = true
  try {
    const { data } = await axios.post('/api/haosen/appealData/handleAppealData', currentAppealDetail.value)
    if (data.code === 200) {
      ElMessage.success('提交申诉成功')
      closeAppealModal()
      fetchAppealData()
    } else {
      ElMessage.error(data.msg || '提交失败')
    }
  } catch {
    ElMessage.error('网络异常')
  } finally {
    isSaving.value = false
  }
}

const findDaKuData = async () => {
  if (isFinding.value) return
  if (!currentAppealDetail.value.keyid) {
    ElMessage.warning('请先输入keyid')
    return
  }
  isFinding.value = true
  try {
    const { data } = await axios.get('/api/haosen/updateData/findDaKuData', { params: { keyid: currentAppealDetail.value.keyid } })
    if (data.code === 200 && data.data != null) {
      // 先保存原始不可编辑字段
      const protectedFields = {
        batchCode: currentAppealDetail.value.batchCode,
        dataId: currentAppealDetail.value.dataId,
        dataType: currentAppealDetail.value.dataType,
        dataCode: currentAppealDetail.value.dataCode,
        originalName: currentAppealDetail.value.originalName,
        originalProvince: currentAppealDetail.value.originalProvince,
        originalAddress: currentAppealDetail.value.originalAddress,
        companyName: currentAppealDetail.value.companyName,
        appealRemark: currentAppealDetail.value.appealRemark,
        solveRemark: currentAppealDetail.value.solveRemark,
        orgType: currentAppealDetail.value.orgType,
        haosenCode: currentAppealDetail.value.haosenCode,

      }

      // 更新其他字段
      Object.assign(currentAppealDetail.value, data.data)

      // 强制恢复原始字段
      Object.assign(currentAppealDetail.value, protectedFields)

      ElMessage.success('匹配成功，已填充')
    } else {
      ElMessage.error('未找到匹配数据')
    }
  } catch {
    ElMessage.error('匹配失败')
  } finally {
    isFinding.value = false
  }
}

const resetForm = () => {
  ElMessageBox.confirm('确定要重置所有修改吗？重置后将恢复为原始数据。', '确认重置', { type: 'warning' })
      .then(() => {
        currentAppealDetail.value = JSON.parse(JSON.stringify(originalAppeal.value))
        ElMessage.success('已重置为原始数据')
      })
}

// ==================== 下拉菜单命令处理 ====================
const handleCommand = (command) => {
  const { action, row } = command
  switch (action) {
    case 'detail':
      showDetail(row)
      break
    case 'appeal':
      appealDetail(row)
      break
  }
}

// ==================== 组件生命周期钩子 ====================
onMounted(() => {
  fetchAppealData()
})
</script>

<style scoped>
/* ==================== 页面整体布局 ==================== */
.appeal-data-view {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: min(1600px, 95vw);
  margin: 0 auto;
  background: #ffffff;
  overflow: hidden;
  font-size: 14px;
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

/* ==================== 响应式布局适配 ==================== */
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


/* ==================== 搜索区域样式 ==================== */
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
  font-size: 14px !important;
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
.card-view {
  height: 100%;
  overflow-y: auto;
  padding: 16px;
}

.appeal-card {
  height: 100%;
  font-size: 14px;
  margin-bottom: 16px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.appeal-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.card-header :deep(.el-tag) {
  flex-shrink: 0;
}

.card-body {
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
  flex-shrink: 0;
  min-width: 80px;
  display: inline-block;
}

.card-footer {
  margin-top: auto;
  text-align: right;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

/* ==================== 分页区域样式 ==================== */
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
}

/* ==================== 响应式适配 ==================== */
@media (min-width: 2000px) {
  .appeal-data-view { max-width: min(1920px, 95vw); }
}

@media (min-width: 2600px) {
  .appeal-data-view { max-width: min(2560px, 95vw); }
}

/* ==================== 暗色模式样式 ==================== */
html.dark .appeal-data-view,
.dark .appeal-data-view {
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

/* 表格边框伪元素颜色 - 暗色模式 */
.dark :deep(.el-table--border:before),
.dark :deep(.el-table--border:after),
.dark :deep(.el-table--border .el-table__inner-wrapper:before),
.dark :deep(.el-table--border .el-table__inner-wrapper:after) {
  background-color: var(--el-border-color, #3a3a4a) !important;
}

.dark .fixed-pagination {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-top-color: var(--el-border-color, #3a3a4a) !important;
}

.dark .page-jumper {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

.dark .appeal-card {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-color: var(--el-border-color, #3a3a4a) !important;
}

.dark .card-header {
  border-bottom-color: var(--el-border-color, #3a3a4a) !important;
}

.dark .card-title {
  color: var(--el-text-color-regular, #e0e0e0) !important;
}

.dark .card-item .label {
  color: var(--el-text-color-secondary, #a0a0a0) !important;
}

.dark .card-item {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

.dark .no-data-container :deep(.el-empty__description) {
  color: var(--el-text-color-secondary, #a0a0a0) !important;
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

/* ==================== 申诉处理弹窗样式 ==================== */
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

}

.custom-dialog-title-appeal {
  text-align: left;
  font-size: 18px;
  font-weight: 600;
  width: 100%;

}



.update-dialog :deep(.el-dialog__header) {
  padding: 16px 24px;
  border-bottom: 1px solid #ebeef5;
}


.dialog-fixed-header {
  flex-shrink: 0;
  padding: 14px 24px;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  position: sticky;
  top: 0;
  z-index: 10;
}

.header-main {
  display: flex;
  align-items: center;
  flex: 1;
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

/* ==================== 操作下拉菜单样式 ==================== */
.action-dropdown {
  vertical-align: top;
}

.action-button {
  min-width: 70px;
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
  margin-right: 4px;
  transition: transform 0.3s ease;
}

.action-button:hover .action-icon {
  transform: rotate(90deg);
}

.arrow-icon {
  font-size: 14px;
  margin-left: 4px;
  transition: transform 0.3s ease;
}

.action-dropdown:hover .arrow-icon {
  transform: rotate(180deg);
}

.action-dropdown-menu {
  min-width: 140px;
  border-radius: 8px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  padding: 6px 0;
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

.dropdown-item:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background-color: transparent !important;
  transform: none !important;
}

.dropdown-item:disabled:hover {
  background-color: transparent !important;
  transform: none !important;
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

/* 与HospitalDataView.vue保持一致的颜色设置 */
.detail-item .menu-icon {
  color: var(--el-color-primary);
}
.update-item .menu-icon {
  color: var(--el-color-success);
}

</style>