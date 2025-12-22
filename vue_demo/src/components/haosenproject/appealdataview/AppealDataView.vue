<template>
  <div class="appeal-data-view">
    <!-- 搜索区域 -->
    <div class="fixed-search">
      <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent="handleSearch">
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
        <el-form-item label="清洗后的编码">
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
        <el-form-item class="form-actions">
          <el-button type="primary" @click="handleSearch" :loading="loading">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="toExcel" :loading="exporting">
            {{ exporting ? '导出中...' : '导出' }}
          </el-button>
          <!-- 视图切换按钮 -->
          <el-button-group class="view-toggle">
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
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据区域 -->
    <div class="data-container">
      <div class="content-wrapper" v-loading="loading">
        <!-- 表格视图 -->
        <div v-if="viewMode === 'table'" class="table-view">
          <el-table
              v-if="appealData.list?.length"
              :data="appealData.list"
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
                <template v-else-if="index === 9">{{ row.appealRemark }}</template>
                <template v-else-if="index === 10">
                  <el-button type="success" size="small" @click="showDetail(row)">详情</el-button>
                  <el-button type="primary" size="small" @click="appealDetail(row)">申诉</el-button>
                </template>
              </template>
            </el-table-column>
          </el-table>
          <div v-else class="no-data">没有找到匹配的数据</div>
        </div>

        <!-- 卡片视图 -->
        <div v-else class="card-view">
          <el-empty v-if="!appealData.list?.length" description="没有找到匹配的数据" />
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
                      <el-tag type="warning" size="small">申诉</el-tag>
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
                  <el-button type="success" size="small" @click="showDetail(appeal)">详情</el-button>
                  <el-button type="primary" size="small" @click="appealDetail(appeal)">申诉</el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- 分页 -->
      <div class="fixed-pagination" v-if="appealData.list?.length">
        <div class="pagination-content">
          <el-button size="small" plain :disabled="!appealData.hasPreviousPage" @click="pageNumber > 1 && (pageNumber--, fetchAppealData())">
            上一页
          </el-button>
          <span class="page-info">
            第 {{ appealData.pageNum }} 页 / 共 {{ appealData.pages }} 页 (共 {{ appealData.total }} 条)
          </span>
          <el-button size="small" plain :disabled="!appealData.hasNextPage" @click="pageNumber < appealData.pages && (pageNumber++, fetchAppealData())">
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
      <template #title>
        <div class="custom-dialog-title">申诉处理</div>
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
              <el-form-item label="经销商"><el-input v-model="currentAppealDetail.companyName" readonly /></el-form-item>
              <el-form-item label="申诉原因"><el-input v-model="currentAppealDetail.appealRemark" readonly /></el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="申诉解决"><el-input v-model="currentAppealDetail.solveRemark" /></el-form-item>
              <el-form-item label="机构类型">
                <el-select v-model="currentAppealDetail.institutionType">
                  <el-option label="医院" value="医院" />
                  <el-option label="药店" value="药店" />
                  <el-option label="商业" value="商业" />
                </el-select>
              </el-form-item>
              <el-form-item label="keyId"><el-input v-model="currentAppealDetail.keyid" /></el-form-item>
              <el-form-item label="标准名称"><el-input v-model="currentAppealDetail.name" /></el-form-item>
              <el-form-item label="历史名称"><el-input v-model="currentAppealDetail.nameHistory" /></el-form-item>
              <el-form-item label="省"><el-input v-model="currentAppealDetail.province" /></el-form-item>
              <el-form-item label="省ID"><el-input v-model="currentAppealDetail.provinceid" /></el-form-item>
              <el-form-item label="市"><el-input v-model="currentAppealDetail.city" /></el-form-item>
              <el-form-item label="市ID"><el-input v-model="currentAppealDetail.cityid" /></el-form-item>
              <el-form-item label="区县"><el-input v-model="currentAppealDetail.area" /></el-form-item>
              <el-form-item label="区县ID"><el-input v-model="currentAppealDetail.areaid" /></el-form-item>
              <el-form-item label="地址"><el-input v-model="currentAppealDetail.address" /></el-form-item>
              <el-form-item label="等级">
                <el-select v-model="currentAppealDetail.level">
                  <el-option label="未定级" value="未定级" />
                  <el-option label="一级" value="一级" />
                  <el-option label="二级" value="二级" />
                  <el-option label="三级" value="三级" />
                </el-select>
              </el-form-item>
              <el-form-item label="等次">
                <el-select v-model="currentAppealDetail.grade">
                  <el-option label="未定等" value="未定等" />
                  <el-option label="甲等" value="甲等" />
                  <el-option label="乙等" value="乙等" />
                  <el-option label="丙等" value="丙等" />
                </el-select>
              </el-form-item>
              <el-form-item label="所有制">
                <el-select v-model="currentAppealDetail.publicflag">
                  <el-option label="公立" value="公立" />
                  <el-option label="民营" value="民营" />
                </el-select>
              </el-form-item>
              <el-form-item label="类别"><el-input v-model="currentAppealDetail.classify" /></el-form-item>
              <el-form-item label="总分院kid"><el-input v-model="currentAppealDetail.generalBranchKid" /></el-form-item>
              <el-form-item label="总分院名称"><el-input v-model="currentAppealDetail.generalBranchName" /></el-form-item>
              <el-form-item label="是否军队医院">
                <el-select v-model="currentAppealDetail.militaryHos">
                  <el-option label="是" value="1" />
                  <el-option label="否" value="0" />
                </el-select>
              </el-form-item>
              <el-form-item label="登记号"><el-input v-model="currentAppealDetail.regcode" /></el-form-item>
              <el-form-item label="有效期"><el-input v-model="currentAppealDetail.validity" /></el-form-item>
              <el-form-item label="诊疗科室"><el-input v-model="currentAppealDetail.subjects" /></el-form-item>
              <el-form-item label="法人代表"><el-input v-model="currentAppealDetail.legalperson" /></el-form-item>
              <el-form-item label="统一社会信用代码"><el-input v-model="currentAppealDetail.usci" /></el-form-item>
              <el-form-item label="经营方式">
                <el-select v-model="currentAppealDetail.operation">
                  <el-option label="零售" value="零售" />
                  <el-option label="批发" value="批发" />
                  <el-option label="连锁" value="连锁" />
                </el-select>
              </el-form-item>
              <el-form-item label="经营范围"><el-input v-model="currentAppealDetail.scope" /></el-form-item>
              <el-form-item label="总分店kid"><el-input v-model="currentAppealDetail.mainBranchKid" /></el-form-item>
              <el-form-item label="总分店名称"><el-input v-model="currentAppealDetail.mainBranchName" /></el-form-item>
              <el-form-item label="成立日期"><el-input v-model="currentAppealDetail.createDate" /></el-form-item>
              <el-form-item label="注册资金"><el-input v-model="currentAppealDetail.registCapi" /></el-form-item>
              <el-form-item label="企业类型"><el-input v-model="currentAppealDetail.econKind" /></el-form-item>
              <el-form-item label="登记状态"><el-input v-model="currentAppealDetail.signStatus" /></el-form-item>
              <el-form-item label="所属行业"><el-input v-model="currentAppealDetail.industry" /></el-form-item>
              <el-form-item label="登记机关"><el-input v-model="currentAppealDetail.belong" /></el-form-item>
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

const appealData = reactive({
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
  { label: '申诉原因', prop: 'appealRemark' },
  { label: '操作', fixed: 'right' , width: 140}
])

const showDetailModal = ref(false)
const appealDetailModal = ref(false)
const currentAppeal = ref({})
const originalAppeal = ref({})
const currentAppealDetail = ref({})
const isSaving = ref(false)
const isFinding = ref(false)

const detailFields = {
  batchCode: { label: '批次编号', value: a => a.batchCode, copy: true },
  dataId: { label: 'dataId', value: a => a.dataId, copy: true },
  dataType: { label: '数据类型', value: a => a.dataType === '1' ? '存量' : a.dataType === '2' ? '增量' : '未知' },
  dataCode: { label: '原始数据编码', value: a => a.dataCode, copy: true },
  originalName: { label: '原始数据名称', value: a => a.originalName, copy: true },
  originalProvince: { label: '省份', value: a => a.originalProvince || a.province },
  originalAddress: { label: '原始地址', value: a => a.originalAddress },
  companyName: { label: '经销商', value: a => a.companyName },
  appealRemark: { label: '申诉原因', value: a => a.appealRemark },
  solveRemark: { label: '申诉解决', value: a => a.solveRemark },
  institutionType: { label: '机构类型', value: a => a.institutionType || a.orgType },
  keyid: { label: 'keyId', value: a => a.keyid, copy: true },
  name: { label: '医院名称', value: a => a.name, copy: true },
  nameHistory: { label: '历史名称', value: a => a.nameHistory },
  province: { label: '省', value: a => a.province },
  provinceId: { label: '省份ID', value: a => a.provinceid || a.provinceId },
  city: { label: '市', value: a => a.city },
  cityId: { label: '市ID', value: a => a.cityid || a.cityId },
  area: { label: '区县', value: a => a.area },
  areaId: { label: '区县ID', value: a => a.areaid || a.areaId },
  address: { label: '地址', value: a => a.address },
  level: { label: '等级', value: a => a.level },
  grade: { label: '等次', value: a => a.grade },
  publicflag: { label: '所有制', value: a => a.publicflag },
  classify: { label: '类别', value: a => a.classify || a.class5 },
  generalBranchKid: { label: '总分院kid', value: a => a.generalBranchKid },
  generalBranchName: { label: '总分院名称', value: a => a.generalBranchName },
  militaryHos: {
    label: '军队医院',
    value: a => a.militaryHos === '1' ? '是' : a.militaryHos === '0' ? '否' : '未知'
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

const fetchAppealData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pageNumber.value, pageSize: pageSize.value, ...searchForm }
    const { data } = await axios.get('/api/appealData/getAppealData', { params })
    if (data.code === 200) {
      Object.assign(appealData, data.data)
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
  fetchAppealData()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => (searchForm[key] = ''))
  pageNumber.value = 1
  fetchAppealData()
}

const handlePageSizeChange = () => {
  pageNumber.value = 1
  fetchAppealData()
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
    const { data: jsonBlob } = await axios.get('/api/appealData/exportAppealData', { params, responseType: 'blob' })
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

const saveChanges = async () => {
  if (isSaving.value) return
  isSaving.value = true
  try {
    const { data } = await axios.post('/api/appealData/handleAppealData', currentAppealDetail.value)
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
    const { data } = await axios.get('/api/updateData/findDaKuData', { params: { keyid: currentAppealDetail.value.keyid } })
    if (data.code === 200 && data.data!=null ) {
      // 先保存原始不可编辑字段
      const protectedFields = {
        batchCode: currentAppealDetail.value.batchCode,
        dataId: currentAppealDetail.value.dataId,
        dataType: currentAppealDetail.value.dataType,
        dataCode: currentAppealDetail.value.dataCode,
        originalName: currentAppealDetail.value.originalName,
        originalProvince: currentAppealDetail.value.originalProvince,
        companyName: currentAppealDetail.value.companyName,
        appealRemark: currentAppealDetail.value.appealRemark,
        solveRemark: currentAppealDetail.value.solveRemark
      }

      // 更新其他字段
      Object.assign(currentAppealDetail.value, data.data)

      // 强制恢复原始字段
      Object.assign(currentAppealDetail.value, protectedFields)

      ElMessage.success('匹配成功，已填充')
    } else {
      ElMessage.error( '未找到匹配数据')
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
  fetchAppealData()
})
</script>

<style scoped>
.appeal-data-view {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: min(1250px, 95vw);
  margin: 0 auto;
  background: white;
  overflow: hidden;
  font-size: 12px;
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

.fixed-search {
  position: sticky;
  top: 1px;
  z-index: 100;
  padding: 10px 16px;
  background: #f5f7fa;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 8px;
  border-radius: 6px;
  max-width: 100%;
  box-sizing: border-box;
}

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

.form-actions {
  margin-left: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 0 0 auto;
}

.view-toggle {
  margin-left: 16px;
}

.data-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-wrapper {
  flex: 1;
  overflow: hidden;
  margin: 8px;
  border: 1px solid #ebeef5;
  border-radius: 3px;
  background: #fff;
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
  color: #909399;
  font-weight: bold;
  margin-right: 6px;
}

.card-footer {
  margin-top: auto;
  text-align: right;
}

.fixed-pagination {
  position: sticky;
  bottom: 0;
  background: #f5f7fa;
  padding: 8px;
  border-top: 1px solid #dcdfe6;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.05);
  text-align: center;
}

.pagination-content {
  display: inline-flex;
  align-items: center;
  gap: 12px;
}

.page-info {
  font-size: 12px;
  color: #606266;
  min-width: 220px;
  text-align: center;
}

.no-data {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.th-content {
  position: relative;
  text-align: center;
}

.resize-handle {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  cursor: col-resize;
}

.resize-handle:hover {
  background: #409eff;
}

.detail-container {
  max-height: 60vh;
  overflow-y: auto;
}

.detail-row {
  display: flex;
  margin-bottom: 10px;
}

.detail-row label {
  min-width: 140px;
  font-weight: bold;
  color: #606266;
}

.detail-row span {
  flex: 1;
  word-break: break-all;
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
}

.update-dialog :deep(.el-dialog__header) {
  padding: 16px 24px;
  border-bottom: 1px solid #ebeef5;
}

.dialog-fixed-header {
  flex-shrink: 0;
  padding: 12px 24px;
  background: #fafafa;
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