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
                    <!-- 前3列为可复制的链接：batchCode, dataId, dataCode -->
                    <el-link type="primary" :underline="false" @click="copyText(row[columns[index].prop])" :disabled="!row[columns[index].prop]">
                      {{ row[columns[index].prop] }}
                    </el-link>
                  </template>
                  <template v-else-if="index === 3">
                    <!-- 第4列为数据类型 -->
                    {{ row.dataType === '1' ? '存量' : row.dataType === '2' ? '增量' : '未知' }}
                  </template>
                  <template v-else-if="index === 4">
                    <!-- 第5列为原始省份 -->
                    {{ row[columns[index].prop] }}
                  </template>
                  <template v-else-if="index === 5">
                    <!-- 第6列为可复制的原始名称 -->
                    <el-link type="primary" :underline="false" @click="copyText(row[columns[index].prop])">
                      {{ row[columns[index].prop] }}
                    </el-link>
                  </template>
                  <template v-else-if="index < 8">
                    <!-- 6-7列为普通文本：originalAddress, companyName -->
                    {{ row[columns[index].prop] }}
                  </template>

                  <template v-else-if="index === 10">
                    <!-- 第10列为是否多批次 -->
                    <el-tag :type="getStatusTagType(row.duplicateFlag, 'duplicateFlag')" size="small">
                      {{ getDuplicateFlagStatus(row.duplicateFlag) }}
                    </el-tag>

                  </template>

                  <template v-else-if="index < 11">
                    <!-- 8-9列为状态标签：batchStatus, cleanStatus -->
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
                <el-card class="appeal-card" shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <div class="card-title">
                        <el-link type="primary" :underline="false" @click="copyText(item.originalName)">
                          {{ item.originalName }}
                        </el-link>
                      </div>
                      <div class="status-badges">
                        <el-tag :type="getStatusTagType(item.batchStatus)" size="small" effect="dark">
                          {{ formatStatus(item.batchStatus) }}
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
            <el-button size="small" plain :disabled="!cleanData.hasPreviousPage" @click="pageNumber > 1 && (pageNumber--, fetchCleanData())">
              上一页
            </el-button>
            <div class="page-jumper">
              <span>跳转到</span>
              <el-input-number
                v-model="jumpPageNumber"
                :min="1"
                :max="cleanData.pages"
                size="small"
                controls-position="right"
                @change="handleJumpPage"
                class="page-input"
              />
              <span>页，共 {{ cleanData.pages }} 页 ({{ cleanData.total }} 条)</span>
            </div>
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
              <el-form-item label="省ID"><el-input v-model="currentCleanDetail.provinceId"  /></el-form-item>
              <el-form-item label="市"><el-input v-model="currentCleanDetail.city"   /></el-form-item>
              <el-form-item label="市ID"><el-input v-model="currentCleanDetail.cityId"   /></el-form-item>
              <el-form-item label="区县"><el-input v-model="currentCleanDetail.area"  /></el-form-item>
              <el-form-item label="区县ID"><el-input v-model="currentCleanDetail.areaId"   /></el-form-item>
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
              <el-form-item label="总分院kid"><el-input v-model="currentCleanDetail.generalBranchKid" :disabled="isFieldDisabled('generalBranchKid')" /></el-form-item>
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
              <el-form-item label="总分店kid"><el-input v-model="currentCleanDetail.mainBranchKid" :disabled="isFieldDisabled('mainBranchKid')" /></el-form-item>
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
  </div>
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
    // 清洗状态: 1-待清洗(warning)，2-已清洗(success)
    switch (status) {
      case 1: return 'warning'
      case 2: return 'success'
      default: return ''
    }
  } else if (type === 'cleanStatus') {
    // 抽取状态: 0-待抽取(warning)，1-已抽取(success)
    switch (status) {
      case 0: return 'warning'
      case 1: return 'success'
      default: return ''
    }
  } else if (type === 'duplicateFlag') {
    // 是否多批次: '1'-是(danger)，'2'-否(success)
    if (status === '1') {
      return 'danger'   // 是 - 红色
    } else if (status === '2') {
      return 'success'   // 否 - 绿色
    } else {
      return ''
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
    default: return ''
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

    const { data } = await axios.get('/api/haosen/cleanData/selectCleanData', { params })

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
    const { data: jsonBlob } = await axios.get('/api/haosen/cleanData/exportCleanData', { params, responseType: 'blob' })
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
    const { data } = await axios.post('/api/haosen/cleanData/handleCleanData', currentCleanDetail.value)
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
    ElMessage.warning('请先输入keyid')
    return
  }
  isFinding.value = true
  try {
    const { data } = await axios.get('/api/haosen/updateData/findDaKuData', { params: { keyid: currentCleanDetail.value.keyid } })
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

  line-height: 1.5;
}

.detail-row span {
  flex: 1;
  word-break: break-word;
  line-height: 1.5;

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

}

.custom-dialog-title-clean {
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
  background: var(--bg-color, var(--bg-secondary, #ffffff));
  border-bottom: 1px solid #ebeef5;
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

/* 操作下拉菜单样式 */
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


