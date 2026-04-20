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
              <el-input v-model="searchForm.dataId" placeholder="请输入 DataId" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="原始省份">
              <el-input v-model="searchForm.originalProvince" placeholder="请输入原始省份" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
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
              <el-select v-model="searchForm.batchStatus" placeholder="请选择清洗状态" clearable @clear="handleSearch">
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
      <div class="data-content" v-loading="loading">
        <!-- 表格视图 -->
        <div v-if="viewMode === 'table'" class="table-container">
          <el-table
              v-if="cleanData.list?.length"
              :data="cleanData.list"
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
                  <template v-if="index < 3">
                    <!-- 前 3 列为可复制的链接：batchCode, dataId, dataCode -->
                    <el-link type="primary" :underline="false" @click="copyText(row[columns[index].prop])" :disabled="!row[columns[index].prop]">
                      {{ row[columns[index].prop] }}
                    </el-link>
                  </template>
                  <template v-else-if="index === 3">
                    <!-- 第 4 列为数据类型 -->
                    {{ row.dataType === '1' ? '存量' : row.dataType === '2' ? '增量' : '未知' }}
                  </template>
                  <template v-else-if="index === 4">
                    <!-- 第 5 列为原始省份 -->
                    {{ row[columns[index].prop] }}
                  </template>
                  <template v-else-if="index === 5">
                    <!-- 第 6 列为可复制的原始名称 -->
                    <el-link type="primary" :underline="false" @click="copyText(row[columns[index].prop])">
                      {{ row[columns[index].prop] }}
                    </el-link>
                  </template>
                  <template v-else-if="index < 8">
                    <!-- 6-7 列为普通文本：originalAddress, companyName -->
                    {{ row[columns[index].prop] }}
                  </template>

                  <template v-else-if="index === 10">
                    <!-- 第 10 列为是否多批次 -->
                    <el-tag :type="getStatusTagType(row.duplicateFlag, 'duplicateFlag')" size="small">
                      {{ getDuplicateFlagStatus(row.duplicateFlag) }}
                    </el-tag>

                  </template>

                  <template v-else-if="index < 11">
                    <!-- 8-9 列为状态标签：batchStatus, cleanStatus -->
                    <el-tag
                      :type="index === 8 ? getStatusTagType(row.batchStatus, 'batchStatus') : getStatusTagType(row.cleanStatus, 'cleanStatus')"
                      size="small"
                    >
                      {{ index === 8 ? formatStatus(row.batchStatus) : formatCleanStatus(row.cleanStatus) }}
                    </el-tag>
                  </template>
                  <template v-else>
                    <!-- 最后一列为操作按钮 - 使用下拉菜单 -->
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
                              :command="{action: 'clean', row: row}"
                              :disabled="!(row.batchStatus === 1 && row.cleanStatus === 0 && row.duplicateFlag === '2')"
                              class="dropdown-item update-item">
                            <el-icon class="menu-icon"><Refresh /></el-icon>
                            <span class="menu-text">数据清洗</span>
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
            <el-empty v-if="!cleanData.list?.length" description="没有找到匹配的数据" :image-size="120" class="no-data-container" />
            <el-row :gutter="20" v-else>
              <el-col v-for="item in cleanData.list" :key="item.dataId" :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                <el-card class="clean-data-card" shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <div class="card-title">
                        <el-link type="primary" :underline="false" @click="copyText(item.originalName)">
                          {{ item.originalName }}
                        </el-link>
                      </div>
                      <div class="status-badges">
                        <el-tag :type="getStatusTagType(item.batchStatus, 'batchStatus')" size="small" effect="dark">
                          {{ formatStatus(item.batchStatus) }}
                        </el-tag>
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
                    <div class="card-item"><span class="label">豪森编码：</span>{{ item.haosenCode }}</div>
                    <div class="card-item"><span class="label">添加时间：</span>{{ formatDateTime(item.addTime) }}</div>
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
                          <el-dropdown-item
                              :command="{action: 'clean', row: item}"
                              :disabled="!(item.batchStatus === 1 && item.cleanStatus === 0 && item.duplicateFlag === '2')"
                              class="dropdown-item update-item">
                            <el-icon class="menu-icon"><Refresh /></el-icon>
                            <span class="menu-text">数据清洗</span>
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </div>
                </el-card>
              </el-col>
            </el-row>
        </div>

      </div>

      <!-- 分页 -->
      <div class="fixed-pagination" v-if="cleanData.list?.length">
        <div class="pagination-content">
          <el-button size="small" plain class="page-btn" :disabled="!cleanData.hasPreviousPage" @click="pageNumber > 1 && (pageNumber--, fetchCleanData())">
            上一页
          </el-button>
          <div class="page-jumper">
            <span class="page-total">跳转到</span>
            <el-input-number
              v-model="jumpPageNumber"
              :min="1"
              :max="cleanData.pages"
              size="small"
              controls-position="right"
              @change="handleJumpPage"
              class="page-input"
            />
            <span class="page-total">页，共 {{ cleanData.pages }} 页 ({{ cleanData.total }} 条)</span>
          </div>
          <el-button size="small" plain class="page-btn" :disabled="!cleanData.hasNextPage" @click="pageNumber < cleanData.pages && (pageNumber++, fetchCleanData())">
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
            <template v-else-if="item.isStatus">
              <el-tag :type="getStatusTagTypeForDetail(currentCleanItem[key], item.type)" size="small">
                {{ item.value(currentCleanItem) }}
              </el-tag>
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

      </template>

      <!-- 顶部固定操作栏 -->
      <div class="dialog-fixed-header">
        <div class="header-main">
          <div class="custom-dialog-title-clean">数据清洗</div>
        </div>
        <div class="header-actions">
          <el-button @click="resetCleanForm">重置匹配</el-button>
          <el-button type="primary" :loading="isFinding" @click="findDaKuData">
            {{ isFinding ? '匹配中...' : '匹配大库' }}
          </el-button>
          <el-button type="primary" :loading="isSaving" @click="saveChanges">
            {{ isSaving ? '推送中...' : '推送清洗' }}
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
              <el-form-item label="清洗备注"><el-input v-model="currentCleanDetail.solveRemark"  /></el-form-item>
              <el-form-item label="机构类型" :rules="[{ required: true, message: '请选择机构类型', trigger: 'change' }]">
                <el-select v-model="currentCleanDetail.orgType" @change="handleOrgTypeChange" placeholder="请选择机构类型">
                  <el-option label="医院" value="医院" />
                  <el-option label="药店" value="药店" />
                  <el-option label="商业" value="商业" />
                </el-select>
              </el-form-item>
              <el-form-item label="keyId"><el-input v-model="currentCleanDetail.keyid"   /></el-form-item>
              <el-form-item label="标准名称"><el-input v-model="currentCleanDetail.name"   /></el-form-item>
              <el-form-item label="历史名称"><el-input v-model="currentCleanDetail.nameHistory"   /></el-form-item>
              <el-form-item label="省"><el-input v-model="currentCleanDetail.province"   /></el-form-item>
              <el-form-item label="省 ID"><el-input v-model="currentCleanDetail.provinceId"  /></el-form-item>
              <el-form-item label="市"><el-input v-model="currentCleanDetail.city"   /></el-form-item>
              <el-form-item label="市 ID"><el-input v-model="currentCleanDetail.cityId"   /></el-form-item>
              <el-form-item label="区县"><el-input v-model="currentCleanDetail.area"  /></el-form-item>
              <el-form-item label="区县 ID"><el-input v-model="currentCleanDetail.areaId"   /></el-form-item>
              <el-form-item label="地址"><el-input v-model="currentCleanDetail.address" /></el-form-item>
              <el-form-item label="等级">
                <el-select v-model="currentCleanDetail.level" :disabled="isFieldDisabled('level')">
                  <el-option label="未定级" value="未定级" />
                  <el-option label="一级" value="一级" />
                  <el-option label="二级" value="二级" />
                  <el-option label="三级" value="三级" />
                </el-select>
              </el-form-item>
              <el-form-item label="等次">
                <el-select v-model="currentCleanDetail.grade" :disabled="isFieldDisabled('grade')">
                  <el-option label="未定等" value="未定等" />
                  <el-option label="甲等" value="甲等" />
                  <el-option label="乙等" value="乙等" />
                  <el-option label="丙等" value="丙等" />
                </el-select>
              </el-form-item>
              <el-form-item label="所有制">
                <el-select v-model="currentCleanDetail.publicflag" :disabled="isFieldDisabled('publicflag')">
                  <el-option label="公立" value="公立" />
                  <el-option label="民营" value="民营" />
                </el-select>
              </el-form-item>
              <el-form-item label="类别"><el-input v-model="currentCleanDetail.classify"   /></el-form-item>
              <el-form-item label="总分院 kid"><el-input v-model="currentCleanDetail.generalBranchKid" :disabled="isFieldDisabled('generalBranchKid')" /></el-form-item>
              <el-form-item label="总分院名称"><el-input v-model="currentCleanDetail.generalBranchName" :disabled="isFieldDisabled('generalBranchName')" /></el-form-item>
              <el-form-item label="是否军队医院">
                <el-select v-model="currentCleanDetail.militaryHos" :disabled="isFieldDisabled('militaryHos')">
                  <el-option label="是" value="1" />
                  <el-option label="否" value="0" />
                </el-select>
              </el-form-item>
              <el-form-item label="登记号"><el-input v-model="currentCleanDetail.regcode" :disabled="isFieldDisabled('regcode')" /></el-form-item>
              <el-form-item label="有效期"><el-input v-model="currentCleanDetail.validity" :disabled="isFieldDisabled('validity')" /></el-form-item>
              <el-form-item label="诊疗科室"><el-input v-model="currentCleanDetail.subjects" :disabled="isFieldDisabled('subjects')" /></el-form-item>
              <el-form-item label="法人代表"><el-input v-model="currentCleanDetail.legalperson"  /></el-form-item>
              <el-form-item label="统一社会信用代码"><el-input v-model="currentCleanDetail.usci"  /></el-form-item>
              <el-form-item label="经营方式">
                <el-select v-model="currentCleanDetail.operation" :disabled="isFieldDisabled('operation')">
                  <el-option label="零售" value="零售" />
                  <el-option label="批发" value="批发" />
                  <el-option label="连锁" value="连锁" />
                </el-select>
              </el-form-item>
              <el-form-item label="经营范围"><el-input v-model="currentCleanDetail.scope" :disabled="isFieldDisabled('scope')" /></el-form-item>
              <el-form-item label="总分店 kid"><el-input v-model="currentCleanDetail.mainBranchKid" :disabled="isFieldDisabled('mainBranchKid')" /></el-form-item>
              <el-form-item label="总分店名称"><el-input v-model="currentCleanDetail.mainBranchName" :disabled="isFieldDisabled('mainBranchName')" /></el-form-item>
              <el-form-item label="成立日期"><el-input v-model="currentCleanDetail.createDate" :disabled="isFieldDisabled('createDate')" /></el-form-item>
              <el-form-item label="注册资金"><el-input v-model="currentCleanDetail.registCapi" :disabled="isFieldDisabled('registCapi')" /></el-form-item>
              <el-form-item label="企业类型"><el-input v-model="currentCleanDetail.econKind" :disabled="isFieldDisabled('econKind')" /></el-form-item>
              <el-form-item label="登记状态"><el-input v-model="currentCleanDetail.signStatus" :disabled="isFieldDisabled('signStatus')" /></el-form-item>
              <el-form-item label="所属行业"><el-input v-model="currentCleanDetail.industry" :disabled="isFieldDisabled('industry')" /></el-form-item>
              <el-form-item label="登记机关"><el-input v-model="currentCleanDetail.belong" :disabled="isFieldDisabled('belong')" /></el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </el-dialog>
</template>

<script setup>
// ==================== 依赖导入 ====================
import '@/assets/css/dark-mode.css'
import { ref, reactive, onMounted, computed } from 'vue'
import { Grid, CopyDocument, Search, Refresh, Operation, ArrowDown } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// ==================== 视图状态管理 ====================
const viewMode = ref('table')

// ==================== 数据状态管理 ====================
const cleanData = reactive({
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
  batchCode: '',
  dataCode: '',
  originalName: '',
  dataId: '',
  originalProvince: '',
  haosenCode: '',
  dataType: '',
  batchStatus: '',
  cleanStatus: '',
  startTime: '',
  endTime: ''
})

// ==================== 日期范围选择器 ====================
const dateRange = ref([])

// ==================== 表格列配置 ====================
const columns = ref([
  { label: '批次编号', prop: 'batchCode', minWidth: 120 },
  { label: 'dataId', prop: 'dataId', minWidth: 120 },
  { label: '原始编码', prop: 'dataCode', minWidth: 120 },
  { label: '数据类型', prop: 'dataType', minWidth: 90  },
  { label: '原始省份', prop: 'originalProvince', minWidth: 90 },
  { label: '原始名称', prop: 'originalName', minWidth: 130 },
  { label: '原始地址', prop: 'originalAddress', minWidth: 120  },
  { label: '豪森编码', prop: 'haosenCode', minWidth: 120 },
  { label: '清洗状态', prop: 'batchStatus', minWidth: 100  },
  { label: '抽取状态', prop: 'cleanStatus', minWidth: 100 },
  { label: '是否多批次', prop: 'duplicateFlag', minWidth: 100 },
  { label: '操作', fixed: 'right', width: 120, resizable: false }
])

// ==================== 弹窗状态控制 ====================
const showDetailModal = ref(false)
const cleanDetailModal = ref(false)

// ==================== 当前操作数据对象 ====================
const currentCleanItem = ref({})
const originalCleanItem = ref({})
const currentCleanDetail = ref({})

// ==================== 清洗处理状态 ====================
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
  const orgType = currentCleanDetail.value.orgType
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

// ==================== 字段控制相关函数 ====================
// 检查字段是否应该被禁用
const isFieldDisabled = (field) => {
  return getDisabledFields.value[field];
};

// 清除被禁用的字段的值
const clearDisabledFields = () => {
  const disabledFieldNames = Object.keys(getDisabledFields.value).filter(field => getDisabledFields.value[field]);

  disabledFieldNames.forEach(field => {
    if (field in currentCleanDetail.value) {
      currentCleanDetail.value[field] = '';
    }
  });
};

// 机构类型改变时清除被禁用的字段的值
const handleOrgTypeChange = () => {
  clearDisabledFields();
};

// ==================== 详情弹窗字段配置 ====================
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
  batchStatus: { label: '清洗状态', value: a => formatStatus(a.batchStatus), isStatus: true, type: 'batchStatus' },
  cleanStatus: { label: '抽取状态', value: a => formatCleanStatus(a.cleanStatus), isStatus: true, type: 'cleanStatus' },
  duplicateFlag: { label: '是否多批次', value: a => getDuplicateFlagStatus(a.duplicateFlag), isStatus: true, type: 'duplicateFlag' },
  addTime: { label: '添加时间', value: a => formatDateTime(a.addTime) }
}

// ==================== 状态映射工具函数 ====================
// 格式化状态显示
const formatStatus = (batchStatus) => {
  switch (batchStatus) {
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


// 统一的状态标签类型管理
const getStatusTagType = (status, type = 'batchStatus') => {
  // 根据不同类型返回相应的标签类型
  if (type === 'batchStatus') {
    // 清洗状态：1-待清洗 (warning)，2-已清洗 (success)
    switch (status) {
      case 1: return 'warning'
      case 2: return 'success'
      default: return 'info'
    }
  } else if (type === 'cleanStatus') {
    // 抽取状态：0-待抽取 (warning)，1-已抽取 (success)
    switch (status) {
      case 0: return 'warning'
      case 1: return 'success'
      default: return 'info'
    }
  } else if (type === 'duplicateFlag') {
    // 是否多批次：'1'-是 (danger)，'2'-否 (success)
    if (status === '1') {
      return 'danger'   // 是 - 红色
    } else if (status === '2') {
      return 'success'   // 否 - 绿色
    } else {
      return 'info'
    }
  }

  // 默认处理数字状态值
  switch (status) {
    case 0:
    case '0':
    case 1:
    case '1': return 'warning'   // 待处理状态 - 黄色
    case 2:
    case '2':
    case 3:
    case '3': return 'success'   // 已完成状态 - 绿色
    default: return 'info'   // 默认返回 info，避免 el-tag 验证失败
  }
}

// 详情页状态标签类型（保持兼容性）
const getStatusTagTypeForDetail = (status, type) => {
  return getStatusTagType(status, type);
}

const getDuplicateFlagStatus = (duplicateFlag) => {
  if ( duplicateFlag === '1') {
    return '是'
  } else if ( duplicateFlag === '2') {
    return '否'
  }
  return ''
}

// ==================== 日期时间格式化工具：YYYY-MM-DD HH:mm:ss ====================
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// ==================== 数据加载相关函数 ====================
const fetchCleanData = async () => {
  loading.value = true
  try {
    // 构建查询参数，过滤空值
    const params = {
      pageNum: pageNumber.value,
      pageSize: pageSize.value,
      ...Object.fromEntries(
          Object.entries(searchForm).filter(([, value]) => value !== '' && value !== null && value !== undefined)
      )
    }

    const { data } = await axios.get('/api/haosen/cleandata/selectcleandata', { params })

    if (data.code === 200) {
      Object.assign(cleanData, data.data)
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
  pageNumber.value = 1
  jumpPageNumber.value = 1
  // 同步日期范围到搜索表单
  if (dateRange.value && dateRange.value.length === 2) {
    searchForm.startTime = dateRange.value[0]
    searchForm.endTime = dateRange.value[1]
  } else {
    searchForm.startTime = ''
    searchForm.endTime = ''
  }
  fetchCleanData()
}

// 跳转到指定页
const handleJumpPage = () => {
  if (jumpPageNumber.value >= 1 && jumpPageNumber.value <= cleanData.pages) {
    pageNumber.value = jumpPageNumber.value
    fetchCleanData()
  }
}

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => (searchForm[key] = ''))
  dateRange.value = []
  pageNumber.value = 1
  fetchCleanData()
}

const handlePageSizeChange = () => {
  pageNumber.value = 1
  fetchCleanData()
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
    const { data: jsonBlob } = await axios.get('/api/haosen/cleandata/exportcleandata', { params, responseType: 'blob' })
    const jsonText = await jsonBlob.text()
    const { data: base64 } = JSON.parse(jsonText)
    const byteChars = atob(base64)
    const byteNums = new Uint8Array(byteChars.length)
    for (let i = 0; i < byteChars.length; i++) byteNums[i] = byteChars.charCodeAt(i)
    const excelBlob = new Blob([byteNums], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = URL.createObjectURL(excelBlob)
    const link = document.createElement('a')
    link.href = url
    link.download = `豪森清洗数据_${new Date().toLocaleDateString()}.xlsx`
    link.click()
    URL.revokeObjectURL(url)
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

// ==================== 弹窗管理函数 ====================
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

// ==================== 清洗处理相关函数 ====================
const saveChanges = async () => {
  if (isSaving.value) return
  isSaving.value = true
  try {
    const { data } = await axios.post('/api/haosen/cleandata/handlecleandata', currentCleanDetail.value)
    if (data.code === 200) {
      ElMessage.success('推送清洗成功')
      closeCleanModal()
      fetchCleanData()
    } else {
      ElMessage.error(data.msg || '推送失败')
    }
  } catch (error) {
    console.error('推送失败:', error)
    ElMessage.error('网络异常')
  } finally {
    isSaving.value = false
  }
}

const findDaKuData = async () => {
  if (isFinding.value) return
  if (!currentCleanDetail.value.keyid) {
    ElMessage.warning('请先输入 keyid')
    return
  }
  isFinding.value = true
  try {
    const { data } = await axios.get('/api/haosen/updatedata/finddakupdata', { params: { keyid: currentCleanDetail.value.keyid } })
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
        appealRemark: currentCleanDetail.value.appealRemark,
        solveRemark: currentCleanDetail.value.solveRemark,
        orgType: currentCleanDetail.value.orgType,
        haosenCode: currentCleanDetail.value.haosenCode,

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
// ==================== 下拉菜单命令处理 ====================
// 处理下拉菜单命令
const handleCommand = (command) => {
  const { action, row } = command;
  switch(action) {
    case 'detail':
      showDetail(row);
      break;
    case 'clean':
      cleanDetail(row);
      break;
  }
}

const resetCleanForm = () => {
  ElMessageBox.confirm('确定要重置所有修改吗？重置后将恢复为原始数据。', '确认重置', { type: 'warning' })
      .then(() => {
        currentCleanDetail.value = JSON.parse(JSON.stringify(originalCleanItem.value))
        // 机构类型改变时清除被禁用的字段的值
        handleOrgTypeChange();
        ElMessage.success('已重置为原始数据')
      })
      .catch(() => {
        // 用户取消重置
      })
}



// ==================== 组件生命周期钩子 ====================
onMounted(() => {
  fetchCleanData()
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
  border: 1px solid var(--el-border-color-light, #ebeef5);
  border-radius: 6px;
  background: var(--el-bg-color, #fff);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
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

/* ==================== 数据内容区域 ==================== */
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

/* ==================== 卡片视图样式 ==================== */
.card-view {
  height: 100%;
  overflow-y: auto;
  padding: 16px;
}

.clean-data-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  background: var(--el-bg-color, #ffffff);
}

.clean-data-card :deep(.el-card__header) {
  background: var(--el-bg-color, #ffffff);
  border-bottom: 1px solid var(--el-border-color-light, #ebeef5);
}

.clean-data-card :deep(.el-card__body) {
  background: var(--el-bg-color, #ffffff);
}

.clean-data-card:hover {
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
  gap: 8px;
  font-size: 14px;
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

.dark .fixed-pagination {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-top-color: var(--el-border-color, #3a3a4a) !important;
}

.dark .page-jumper {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

.dark .clean-data-card {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-color: var(--el-border-color, #3a3a4a) !important;
}

.dark .clean-data-card :deep(.el-card__header) {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-bottom-color: var(--el-border-color, #3a3a4a) !important;
}

.dark .clean-data-card :deep(.el-card__body) {
  background: var(--el-bg-color, #1a1a2c) !important;
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
  color: var(--el-text-color-regular, #606266);
}

.detail-row span {
  flex: 1;
  word-break: break-word;
  line-height: 1.5;
  color: var(--el-text-color-regular, #606266);
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

.custom-dialog-title-clean {
  text-align: left;
  font-size: 18px;
  font-weight: 600;
  width: 100%;
}

.update-dialog :deep(.el-dialog__header) {
  padding: 16px 24px;
  border-bottom: 1px solid var(--el-border-color-light, #ebeef5);
  background: var(--el-bg-color, #ffffff);
}

.dialog-fixed-header {
  flex-shrink: 0;
  padding: 14px 24px;
  background: var(--el-bg-color, #ffffff);
  border-bottom: 1px solid var(--el-border-color-light, #ebeef5);
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.detail-item .menu-icon {
  color: var(--el-color-primary);
}
.update-item .menu-icon {
  color: var(--el-color-success);
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
