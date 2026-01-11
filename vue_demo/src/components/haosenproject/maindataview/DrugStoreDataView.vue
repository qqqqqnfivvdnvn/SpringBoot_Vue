<template>
  <div class="drugstore-data-view">

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
            <el-form-item label="豪森编码">
              <el-input v-model="searchForm.hsCode" placeholder="请输入清洗后的编码" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
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
                v-if="drugstoreData.list?.length"
                :data="drugstoreData.list"
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
                    <el-link type="primary" :underline="false" @click="copyText(row.dataId)" :disabled="!row.dataId">
                      {{ row.dataId }}
                    </el-link>
                  </template>
                  <template v-else-if="index === 1">
                    <el-link type="primary" :underline="false" @click="copyText(row.dataCode)">
                      {{ row.dataCode }}
                    </el-link>
                  </template>
                  <template v-else-if="index === 2">
                    <el-link type="primary" :underline="false" @click="copyText(row.originalName)">
                      {{ row.originalName }}
                    </el-link>
                  </template>
                  <template v-else-if="index === 3">
                    <el-link type="primary" :underline="false" @click="copyText(row.keyid)">
                      {{ row.keyid }}
                    </el-link>
                  </template>
                  <template v-else-if="index === 4">
                    <el-link type="primary" :underline="false" @click="copyText(row.name)">
                      {{ row.name }}
                    </el-link>
                  </template>
                  <template v-else-if="index === 5">{{ row.province }}</template>
                  <template v-else-if="index === 6">{{ row.city }}</template>
                  <template v-else-if="index === 7">{{ row.area }}</template>
                  <template v-else-if="index === 8">{{ row.address }}</template>
                  <template v-else-if="index === 9">
                    <el-tag :type="getStatusType(row.status)" effect="dark" size="small">
                      {{ getStatusText(row.status) }}
                    </el-tag>
                  </template>
                  <template v-else-if="index === 10">
                    <el-button type="success" size="small" @click="showDetail(row)">详情</el-button>
                    <el-button type="primary" size="small" :disabled="!row.hsCode" @click="updateDetail(row)">更新</el-button>
                  </template>
                </template>
              </el-table-column>
            </el-table>
            <div v-else class="no-data-container">
              <el-empty description="没有找到匹配的药店数据" :image-size="120" />
            </div>
          </div>

          <!-- 卡片视图 -->
          <div v-else class="card-view">
            <el-empty v-if="!drugstoreData.list?.length" description="没有找到匹配的药店数据" class="no-data-container" />
            <el-row :gutter="20" v-else>
              <el-col v-for="drugstore in drugstoreData.list" :key="drugstore.dataId" :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                <el-card class="drugstore-card" shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <div class="card-title">
                        <el-link type="primary" :underline="false" @click="copyText(drugstore.name)">
                          {{ drugstore.name }}
                        </el-link>
                      </div>
                      <el-tag :type="getStatusType(drugstore.status)" size="small" effect="dark">
                        {{ getStatusText(drugstore.status) }}
                      </el-tag>
                    </div>
                  </template>
                  <div class="card-body">
                    <div class="card-item"><span class="label">dataId：</span><el-link type="primary" :underline="false" @click="copyText(drugstore.dataId)">{{ drugstore.dataId }}</el-link></div>
                    <div class="card-item"><span class="label">原始编码：</span><el-link type="primary" :underline="false" @click="copyText(drugstore.dataCode)">{{ drugstore.dataCode }}</el-link></div>
                    <div class="card-item"><span class="label">原始名称：</span><el-link type="primary" :underline="false" @click="copyText(drugstore.originalName)">{{ drugstore.originalName }}</el-link></div>
                    <div class="card-item"><span class="label">keyId：</span><el-link type="primary" :underline="false" @click="copyText(drugstore.keyid)">{{ drugstore.keyid }}</el-link></div>
                    <div class="card-item"><span class="label">省份：</span>{{ drugstore.province }}</div>
                    <div class="card-item"><span class="label">城市：</span>{{ drugstore.city }}</div>
                    <div class="card-item"><span class="label">区县：</span>{{ drugstore.area }}</div>
                    <div class="card-item"><span class="label">地址：</span>{{ drugstore.address }}</div>
                  </div>
                  <div class="card-footer">
                    <el-button type="success" size="small" @click="showDetail(drugstore)">详情</el-button>
                    <el-button type="primary" size="small" :disabled="!drugstore.hsCode" @click="updateDetail(drugstore)">更新</el-button>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </div>

        <!-- 分页 -->
        <div class="fixed-pagination" v-if="drugstoreData.list?.length">
          <div class="pagination-content">
            <el-button size="small" plain :disabled="!drugstoreData.hasPreviousPage" @click="pageNumber > 1 && (pageNumber--, fetchDrugStoreData())">
              上一页
            </el-button>
            <span class="page-info">
              第 {{ drugstoreData.pageNum }} 页 / 共 {{ drugstoreData.pages }} 页 (共 {{ drugstoreData.total }} 条)
            </span>
            <el-button size="small" plain :disabled="!drugstoreData.hasNextPage" @click="pageNumber < drugstoreData.pages && (pageNumber++, fetchDrugStoreData())">
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
    <el-dialog v-model="showDetailModal" title="药店详情" width="500px" destroy-on-close>
      <template #title>
        <div class="custom-dialog-title">药店详情</div>
      </template>

      <div class="detail-container">
        <div class="detail-row" v-for="(item, key) in detailFields" :key="key">
          <label>{{ item.label }}：</label>
          <span>
            <template v-if="item.copy">
              <el-link type="primary" :underline="false" @click="copyText(item.value(currentDrugStore))">
                {{ item.value(currentDrugStore) }}
              </el-link>
            </template>
            <template v-else-if="key === 'status'">
              <el-tag :type="item.type(currentDrugStore)" size="small" effect="dark" style="color: #fff;">
                {{ item.value(currentDrugStore) }}
              </el-tag>
            </template>
            <template v-else>
              {{ item.value(currentDrugStore) }}
            </template>
          </span>
        </div>
      </div>

      <template #footer>
        <el-button @click="showDetailModal = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 更新弹窗 -->
    <el-dialog
        v-model="updateDetailModal"
        width="70%"
        :max-width="1400"
        :close-on-click-modal="false"
        destroy-on-close
        class="update-dialog"
    >
      <template #title>
        <div class="custom-dialog-title">数据更新</div>
      </template>

      <!-- 顶部固定操作栏 -->
      <div class="dialog-fixed-header">
        <div class="header-actions">
          <el-button @click="resetForm">重置匹配</el-button>
          <el-button type="primary" :loading="isFinding" @click="findDaKuData">
            {{ isFinding ? '匹配中...' : '匹配大库' }}
          </el-button>
          <el-button type="primary" :loading="isSaving" @click="saveChanges">
            {{ isSaving ? '推送中...' : '提交推送' }}
          </el-button>
          <el-button @click="closeUpdateModal">取消</el-button>
        </div>
      </div>

      <!-- 可上下滚动的表单内容区 -->
      <div class="dialog-scroll-body">
        <el-form :model="currentUpdateDrugStore" label-width="140px">
          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="批次编号"><el-input v-model="currentUpdateDrugStore.batchCode" readonly /></el-form-item>
              <el-form-item label="dataId"><el-input v-model="currentUpdateDrugStore.dataId" readonly /></el-form-item>
              <el-form-item label="原始名称"><el-input v-model="currentUpdateDrugStore.originalName" readonly /></el-form-item>
              <el-form-item label="数据类型">
                <el-input :value="currentUpdateDrugStore.dataType === '1' ? '存量' : currentUpdateDrugStore.dataType === '2' ? '增量' : '未知'" readonly />
              </el-form-item>
              <el-form-item label="原始编码"><el-input v-model="currentUpdateDrugStore.dataCode" readonly /></el-form-item>
              <el-form-item label="原始省份"><el-input v-model="currentUpdateDrugStore.originalProvince" readonly /></el-form-item>
              <el-form-item label="经销商"><el-input v-model="currentUpdateDrugStore.companyName" readonly /></el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="机构类型">
                <el-select v-model="currentUpdateDrugStore.orgType">
                  <el-option label="医院" value="医院" />
                  <el-option label="药店" value="药店" />
                  <el-option label="商业" value="商业" />
                </el-select>
              </el-form-item>
              <el-form-item label="keyId"><el-input v-model="currentUpdateDrugStore.keyid" /></el-form-item>
              <el-form-item label="标准名称"><el-input v-model="currentUpdateDrugStore.name" /></el-form-item>
              <el-form-item label="历史名称"><el-input v-model="currentUpdateDrugStore.nameHistory" /></el-form-item>
              <el-form-item label="省"><el-input v-model="currentUpdateDrugStore.province" /></el-form-item>
              <el-form-item label="省ID"><el-input v-model="currentUpdateDrugStore.provinceId" /></el-form-item>
              <el-form-item label="市"><el-input v-model="currentUpdateDrugStore.city" /></el-form-item>
              <el-form-item label="市ID"><el-input v-model="currentUpdateDrugStore.cityId" /></el-form-item>
              <el-form-item label="区县"><el-input v-model="currentUpdateDrugStore.area" /></el-form-item>
              <el-form-item label="区县ID"><el-input v-model="currentUpdateDrugStore.areaId" /></el-form-item>
              <el-form-item label="地址"><el-input v-model="currentUpdateDrugStore.address" /></el-form-item>
              <el-form-item label="等级">
                <el-select v-model="currentUpdateDrugStore.level">
                  <el-option label="未定级" value="未定级" />
                  <el-option label="一级" value="一级" />
                  <el-option label="二级" value="二级" />
                  <el-option label="三级" value="三级" />
                </el-select>
              </el-form-item>
              <el-form-item label="等次">
                <el-select v-model="currentUpdateDrugStore.grade">
                  <el-option label="未定等" value="未定等" />
                  <el-option label="甲等" value="甲等" />
                  <el-option label="乙等" value="乙等" />
                  <el-option label="丙等" value="丙等" />
                </el-select>
              </el-form-item>
              <el-form-item label="所有制">
                <el-select v-model="currentUpdateDrugStore.publicflag">
                  <el-option label="公立" value="公立" />
                  <el-option label="民营" value="民营" />
                </el-select>
              </el-form-item>
              <el-form-item label="类别"><el-input v-model="currentUpdateDrugStore.classify" /></el-form-item>
              <el-form-item label="总分院kid"><el-input v-model="currentUpdateDrugStore.generalBranchKid" /></el-form-item>
              <el-form-item label="总分院名称"><el-input v-model="currentUpdateDrugStore.generalBranchName" /></el-form-item>
              <el-form-item label="是否军队医院">
                <el-select v-model="currentUpdateDrugStore.militaryHos">
                  <el-option label="是" value="1" />
                  <el-option label="否" value="0" />
                </el-select>
              </el-form-item>
              <el-form-item label="登记号"><el-input v-model="currentUpdateDrugStore.regcode" /></el-form-item>
              <el-form-item label="诊疗科室"><el-input v-model="currentUpdateDrugStore.subjects" /></el-form-item>
              <el-form-item label="法人代表"><el-input v-model="currentUpdateDrugStore.legalperson" /></el-form-item>
              <el-form-item label="统一社会信用代码"><el-input v-model="currentUpdateDrugStore.usci" /></el-form-item>
              <el-form-item label="经营方式">
                <el-select v-model="currentUpdateDrugStore.operation">
                  <el-option label="零售" value="零售" />
                  <el-option label="批发" value="批发" />
                  <el-option label="连锁" value="连锁" />
                </el-select>
              </el-form-item>
              <el-form-item label="经营范围"><el-input v-model="currentUpdateDrugStore.scope" /></el-form-item>
              <el-form-item label="总分店kid"><el-input v-model="currentUpdateDrugStore.mainBranchKid" /></el-form-item>
              <el-form-item label="总分店名称"><el-input v-model="currentUpdateDrugStore.mainBranchName" /></el-form-item>
              <el-form-item label="成立日期"><el-input v-model="currentUpdateDrugStore.createDate" /></el-form-item>
              <el-form-item label="注册资金"><el-input v-model="currentUpdateDrugStore.registCapi" /></el-form-item>
              <el-form-item label="企业类型"><el-input v-model="currentUpdateDrugStore.econKind" /></el-form-item>
              <el-form-item label="登记状态"><el-input v-model="currentUpdateDrugStore.signStatus" /></el-form-item>
              <el-form-item label="所属行业"><el-input v-model="currentUpdateDrugStore.industry" /></el-form-item>
              <el-form-item label="登记机关"><el-input v-model="currentUpdateDrugStore.belong" /></el-form-item>
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

const drugstoreData = reactive({
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
  dataCode: '',
  originalName: '',
  dataId: '',
  keyid: '',
  hsCode: '',
  province: '',
  city: '',
  area: '',
  name: '',
  address: ''
})

const columns = ref([
  { label: 'dataId', prop: 'dataId' },
  { label: '原始编码', prop: 'dataCode' },
  { label: '原始名称', prop: 'originalName'},
  { label: 'keyId', prop: 'keyid' },
  { label: '标准名称', prop: 'name'},
  { label: '省份', prop: 'province'},
  { label: '城市', prop: 'city' },
  { label: '区县', prop: 'area' },
  { label: '地址', prop: 'address' },
  { label: '状态', prop: 'status' },
  { label: '操作', fixed: 'right' , width: 140}
])

const showDetailModal = ref(false)
const updateDetailModal = ref(false)
const currentDrugStore = ref({})
const originalDrugStore = ref({})
const currentUpdateDrugStore = ref({})
const isSaving = ref(false)
const isFinding = ref(false)

const detailFields = {
  batchCode: { label: '批次编码', value: d => d.batchCode, copy: true },
  dataId: { label: 'dataId', value: d => d.dataId, copy: true },
  dataCode: { label: '原始编码', value: d => d.dataCode, copy: true },
  originalName: { label: '原始名称', value: d => d.originalName, copy: true },
  originalProvince: { label: '原始省份', value: d => d.originalProvince },
  companyName: { label: '经销商', value: d => d.companyName },
  keyid: { label: 'keyId', value: d => d.keyid, copy: true },
  dataType: { label: '数据类型', value: d => d.dataType === '1' ? '存量' : d.dataType === '2' ? '增量' : '未知' },
  hsCode: { label: '豪森清洗后编码', value: d => d.hsCode, copy: true },
  name: { label: '标准名称', value: d => d.name, copy: true },
  nameHistory: { label: '历史名称', value: d => d.nameHistory },
  province: { label: '省份', value: d => d.province },
  provinceId: { label: '省份ID', value: d => d.provinceId },
  city: { label: '市', value: d => d.city },
  cityId: { label: '市ID', value: d => d.cityId },
  area: { label: '区县', value: d => d.area },
  areaId: { label: '区县ID', value: d => d.areaId },
  isCity: { label: '是否市区', value: d => d.isCity },
  address: { label: '地址', value: d => d.address },
  usci: { label: '统一社会信用代码', value: d => d.usci },
  classify: { label: '药店类型', value: d => d.classify },
  operation: { label: '经营方式', value: d => d.operation },
  location: { label: '药店位置', value: d => d.location },
  twoChannels: { label: '是否双通道', value: d => d.twoChannels },
  isInternet: { label: '是否双互联网', value: d => d.isInternet },
  dtp: { label: '是否DTP药房', value: d => d.dtp },
  licenseNumber: { label: '药品经营许可证', value: d => d.licenseNumber },
  licenseValidity: { label: '药品经营许可证有效期', value: d => d.licenseValidity },
  mztc: { label: '是否门诊统筹', value: d => d.mztc },
  createDate: { label: '成立日期', value: d => d.createDate },
  registCapi: { label: '注册资金', value: d => d.registCapi },
  legalperson: { label: '法人', value: d => d.legalperson },
  ybcode: { label: '医保编码', value: d => d.ybcode },
  isYb: { label: '是否医保药店', value: d => d.isYb },
  repeatId: { label: '豪森重复数据ID', value: d => d.repeatId },
  scope: { label: '经营范围', value: d => d.scope },
  isInsert: { label: '是否新增', value: d => d.isInsert },
  mainBranch: { label: '上级单位豪森编码', value: d => d.mainBranch },
  mainBranchKid: { label: '上级单位编码', value: d => d.mainBranchKid },
  mainBranchName: { label: '上级单位名称', value: d => d.mainBranchName },
  signStatus: { label: '登记状态', value: d => d.signStatus },
  hosInside: { label: '院内店', value: d => d.hosInside },
  hosOutside: { label: '院边店', value: d => d.hosOutside },
  field1: { label: '经纬度', value: d => d.field1 },
  remarks: { label: '备注', value: d => d.remarks },
  addtime: { label: '添加日期', value: d => d.addtime },
  updatetime: { label: '更新日期', value: d => d.updatetime },
  status: {
    label: '状态',
    value: d => getStatusText(d.status),
    type: d => getStatusType(d.status)
  }
}

const getStatusText = (status) => {
  const map = { '1': '清洗成功', '2': '数据作废', '3': '无法清洗', '4': '禁用客户', '5': '数据重复' }
  return map[status] || '其他状态'
}

const getStatusType = (status) => {
  const map = { '1': 'success', '2': 'warning', '3': 'info', '4': 'danger', '5': 'warning' }
  return map[status] || 'info'
}

const fetchDrugStoreData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pageNumber.value, pageSize: pageSize.value, ...searchForm }
    const { data } = await axios.get('/api/mainData/getDrugStoreData', { params })
    if (data.code === 200) {
      Object.assign(drugstoreData, data.data)
      pageNumber.value = data.data.pageNum
    } else {
      ElMessage.error(data.msg || '获取数据失败')
    }
  } catch {
    ElMessage.error('网络请求异常')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNumber.value = 1
  fetchDrugStoreData()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => (searchForm[key] = ''))
  pageNumber.value = 1
  fetchDrugStoreData()
}

const handlePageSizeChange = () => {
  pageNumber.value = 1
  fetchDrugStoreData()
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
    const params = { ...searchForm }
    const { data: jsonBlob } = await axios.get('/api/mainData/exportDrugStoreData', { params, responseType: 'blob' })
    const jsonText = await jsonBlob.text()
    const { data: base64 } = JSON.parse(jsonText)
    const byteChars = atob(base64)
    const byteNums = new Uint8Array(byteChars.length)
    for (let i = 0; i < byteChars.length; i++) byteNums[i] = byteChars.charCodeAt(i)
    const excelBlob = new Blob([byteNums], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = URL.createObjectURL(excelBlob)
    const link = document.createElement('a')
    link.href = url
    link.download = `豪森药店数据导出_${new Date().toLocaleDateString()}.xlsx`
    link.click()
    URL.revokeObjectURL(url)
  } catch {
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

const showDetail = (row) => {
  currentDrugStore.value = { ...row }
  showDetailModal.value = true
}

const updateDetail = (row) => {
  originalDrugStore.value = JSON.parse(JSON.stringify(row))
  currentUpdateDrugStore.value = JSON.parse(JSON.stringify(row))
  updateDetailModal.value = true
}

const closeUpdateModal = () => {
  updateDetailModal.value = false
  currentUpdateDrugStore.value = {}
  originalDrugStore.value = {}
}

const saveChanges = async () => {
  if (isSaving.value) return
  isSaving.value = true
  try {
    const { data } = await axios.post('/api/updateData/OneUpdateHaoSenData', currentUpdateDrugStore.value)
    if (data.code === 200) {
      ElMessage.success('提交更新成功')
      closeUpdateModal()
      fetchDrugStoreData()
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
  if (!currentUpdateDrugStore.value.keyid) {
    ElMessage.warning('请先输入keyid')
    return
  }
  isFinding.value = true
  try {
    const { data } = await axios.get('/api/updateData/findDaKuData', { params: { keyid: currentUpdateDrugStore.value.keyid } })
    if (data.code === 200 && data.data) {
      // 先保存原始不可编辑字段
      const protectedFields = {
        batchCode: currentUpdateDrugStore.value.batchCode,
        dataId: currentUpdateDrugStore.value.dataId,
        dataType: currentUpdateDrugStore.value.dataType,
        dataCode: currentUpdateDrugStore.value.dataCode,
        originalName: currentUpdateDrugStore.value.originalName,
        originalProvince: currentUpdateDrugStore.value.originalProvince,
        companyName: currentUpdateDrugStore.value.companyName,
      }

      // 更新其他字段
      Object.assign(currentUpdateDrugStore.value, data.data)

      // 强制恢复原始字段
      Object.assign(currentUpdateDrugStore.value, protectedFields)

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
        currentUpdateDrugStore.value = JSON.parse(JSON.stringify(originalDrugStore.value))
        ElMessage.success('已重置为原始数据')
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
  fetchDrugStoreData()
})
</script>

<style scoped>
.drugstore-data-view {
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 2K屏幕优化 */
@media (min-width: 2000px) and (max-width: 2600px) {
  .drugstore-data-view {
    max-width: min(1860px, 90vw);
  }
}

/* 超宽屏幕 */
@media (min-width: 2600px) {
  .drugstore-data-view {
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

.drugstore-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.drugstore-card:hover {
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
  justify-content: center;
  background: var(--bg-secondary, #ffffff);
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

/* 更新弹窗整体 */
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