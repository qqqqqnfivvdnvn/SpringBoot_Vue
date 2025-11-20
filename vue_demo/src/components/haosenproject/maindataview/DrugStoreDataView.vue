<template>
  <div class="DrugStore-data-view">
    <div class="search-container">
      <div class="search-form">
        <div class="form-item">
          <label>原始编码：</label>
          <div class="input-wrapper">
            <input
                v-model="searchForm.dataCode"
                placeholder="请输入原始编码"
                @keyup.enter="handleSearch"
            >
            <i v-if="searchForm.dataCode"
               class="fas fa-times-circle clear-icon"
               @click="searchForm.dataCode = ''; handleSearch()">
              <font-awesome-icon :icon="['fas', 'times-circle']" size="1x" />
            </i>
          </div>
        </div>

        <div class="form-item">
          <label>原始名称：</label>
          <div class="input-wrapper">
            <input
                v-model="searchForm.originalName"
                placeholder="请输入原始名称"
                @keyup.enter="handleSearch"
            >
            <i v-if="searchForm.originalName"
               class="fas fa-times-circle clear-icon"
               @click="searchForm.originalName = ''; handleSearch()">
              <font-awesome-icon :icon="['fas', 'times-circle']" size="1x" />
            </i>
          </div>
        </div>

        <div class="form-item">
          <label>DataId：</label>
          <div class="input-wrapper">
            <input
                v-model="searchForm.dataId"
                placeholder="请输入DataId"
                @keyup.enter="handleSearch"
            >
            <i v-if="searchForm.dataId"
               class="fas fa-times-circle clear-icon"
               @click="searchForm.dataId = ''; handleSearch()">
              <font-awesome-icon :icon="['fas', 'times-circle']" size="1x" />
            </i>
          </div>
        </div>

        <div class="form-item">
          <label>KeyId：</label>
          <div class="input-wrapper">
            <input
                v-model="searchForm.keyid"
                placeholder="请输入KeyId"
                @keyup.enter="handleSearch"
            >
            <i v-if="searchForm.keyid"
               class="fas fa-times-circle clear-icon"
               @click="searchForm.keyid = ''; handleSearch()">
              <font-awesome-icon :icon="['fas', 'times-circle']" size="1x" />
            </i>
          </div>
        </div>

        <div class="form-item">
          <label>豪森编码：</label>
          <div class="input-wrapper">
            <input
                v-model="searchForm.hsCode"
                placeholder="请输入清洗后的编码"
                @keyup.enter="handleSearch"
            >
            <i v-if="searchForm.hsCode"
               class="fas fa-times-circle clear-icon"
               @click="searchForm.hsCode = ''; handleSearch()">
              <font-awesome-icon :icon="['fas', 'times-circle']" size="1x" />
            </i>
          </div>
        </div>

        <div class="form-item">
          <label>省份：</label>
          <div class="input-wrapper">
            <input
                v-model="searchForm.province"
                placeholder="请输入省份"
                @keyup.enter="handleSearch"
            >
            <i v-if="searchForm.province"
               class="fas fa-times-circle clear-icon"
               @click="searchForm.province = ''; handleSearch()">
              <font-awesome-icon :icon="['fas', 'times-circle']" size="1x" />
            </i>
          </div>
        </div>

        <div class="form-item">
          <label>市：</label>
          <div class="input-wrapper">
            <input
                v-model="searchForm.city"
                placeholder="请输入市"
                @keyup.enter="handleSearch"
            >
            <i v-if="searchForm.city"
               class="fas fa-times-circle clear-icon"
               @click="searchForm.city = ''; handleSearch()">
              <font-awesome-icon :icon="['fas', 'times-circle']" size="1x" />
            </i>
          </div>
        </div>

        <div class="form-item">
          <label>区县：</label>
          <div class="input-wrapper">
            <input
                v-model="searchForm.area"
                placeholder="请输入区县"
                @keyup.enter="handleSearch"
            >
            <i v-if="searchForm.area"
               class="fas fa-times-circle clear-icon"
               @click="searchForm.area = ''; handleSearch()">
              <font-awesome-icon :icon="['fas', 'times-circle']" size="1x" />
            </i>
          </div>
        </div>

        <div class="form-item">
          <label>输入药店名称：</label>
          <div class="input-wrapper">
            <input
                v-model="searchForm.name"
                placeholder="请输入药店名称"
                @keyup.enter="handleSearch"
            >
            <i v-if="searchForm.name"
               class="fas fa-times-circle clear-icon"
               @click="searchForm.name = ''; handleSearch()">
              <font-awesome-icon :icon="['fas', 'times-circle']" size="1x" />
            </i>
          </div>
        </div>

        <div class="form-item">
          <label>输入药店地址：</label>
          <div class="input-wrapper">
            <input
                v-model="searchForm.address"
                placeholder="请输入药店地址"
                @keyup.enter="handleSearch"
            >
            <i v-if="searchForm.address"
               class="fas fa-times-circle clear-icon"
               @click="searchForm.address = ''; handleSearch()">
              <font-awesome-icon :icon="['fas', 'times-circle']" size="1x" />
            </i>
          </div>
        </div>

        <div class="form-actions">
          <button class="search-btn" @click="handleSearch">查询</button>
          <button class="reset-btn" @click="resetSearch">重置</button>
        </div>
      </div>
    </div>

    <div class="data-container">
      <div class="table-wrapper">
        <table class="DrugStore-table" v-if="DrugStoreData.content && DrugStoreData.content.length > 0">
          <thead>
          <tr>
            <th v-for="(column, index) in columns" :key="index"
                :style="{ width: column.width + 'px' }"
                :class="{ 'fixed-column': column.fixed }"
                @mousedown="startResize($event, index)">
              <div class="th-content">
                {{ column.label }}
                <div class="resize-handle"
                     @mousedown.stop="startResize($event, index)"></div>
              </div>
            </th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="DrugStore in DrugStoreData.content" :key="DrugStore.dataId">
            <td v-for="(column, index) in columns" :key="index"
                :style="{ width: column.width + 'px' }"
                :class="{ 'fixed-column': column.fixed }">
              <template v-if="index === 0">{{ DrugStore.dataId }}</template>
              <template v-else-if="index === 1">{{ DrugStore.dataCode }}</template>
              <template v-else-if="index === 2">{{ DrugStore.originalName }}</template>
              <template v-else-if="index === 3">{{ DrugStore.keyid }}</template>
              <template v-else-if="index === 4">{{ DrugStore.name }}</template>
              <template v-else-if="index === 5">{{ DrugStore.province }}</template>
              <template v-else-if="index === 6">{{ DrugStore.city }}</template>
              <template v-else-if="index === 7">{{ DrugStore.area }}</template>
              <template v-else-if="index === 8">{{ DrugStore.address }}</template>
              <template v-else-if="index === 9">
                <span :class="{
                  'status-active': DrugStore.status === '1',
                  'status-uninactive': DrugStore.status === '3',
                  'status-inactive': DrugStore.status === '4',
                  'status-invalid': DrugStore.status === '2',
                  'status-repeat': DrugStore.status === '5'
                }">
                  {{ DrugStore.status === '1' ? '清洗成功' :
                    DrugStore.status === '3' ? '无法清洗' :
                        DrugStore.status === '4' ? '禁用客户':
                            DrugStore.status === '2' ? '作废数据':
                                DrugStore.status === '5' ? '重复数据': '其他状态' }}
                </span>
              </template>
              <template v-else-if="index === 10">
                <button class="detail-btn" @click="showDetail(DrugStore)">详情</button>
              </template>
            </td>
          </tr>
          </tbody>
        </table>
        <div v-else class="no-data">
          {{ loading ? '数据加载中...' : '没有找到匹配的药店数据' }}
        </div>
        <div class="pagination" v-if="DrugStoreData.content && DrugStoreData.content.length > 0">
          <button
              :disabled="DrugStoreData.first"
              @click="prevPage"
              class="page-btn"
          >
            上一页
          </button>
          <span class="page-info">
            第 {{ DrugStoreData.number + 1 }} 页 / 共 {{ DrugStoreData.totalPages }} 页 (共 {{ DrugStoreData.totalElements }} 条)
          </span>
          <button
              :disabled="DrugStoreData.last"
              @click="nextPage"
              class="page-btn"
          >
            下一页
          </button>
          <select v-model="pageSize" @change="handlePageSizeChange">
            <option value="10">每页10条</option>
            <option value="20">每页20条</option>
            <option value="50">每页50条</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 详情弹窗 -->
    <div v-if="showDetailModal" class="modal-overlay" @click.self="closeDetailModal">
      <div class="modal-container">
        <div class="modal-header">
          <h3>药店详情</h3>
          <span class="close-btn" @click="closeDetailModal">&times;</span>
        </div>

        <div class="modal-body">
          <div class="detail-row">
            <label>dataId：</label>
            <span>{{ currentDrugStore.dataId }}</span>
          </div>
          <div class="detail-row">
            <label>原始编码：</label>
            <span>{{ currentDrugStore.dataCode }}</span>
          </div>

          <div class="detail-row">
            <label>原始名称：</label>
            <span>{{ currentDrugStore.originalName }}</span>
          </div>

          <div class="detail-row">
            <label>keyId：</label>
            <span>{{ currentDrugStore.keyid }}</span>
          </div>

          <div class="detail-row">
            <label>数据类型：</label>
            <span>{{ currentDrugStore.dataType }}</span>
          </div>

          <div class="detail-row">
            <label>豪森清洗后编码：</label>
            <span>{{ currentDrugStore.hsCode }}</span>
          </div>

          <div class="detail-row">
            <label>标准名称：</label>
            <span>{{ currentDrugStore.name }}</span>
          </div>
          <div class="detail-row">
            <label>历史名称：</label>
            <span>{{ currentDrugStore.historyName }}</span>
          </div>

          <div class="detail-row">
            <label>省份：</label>
            <span>{{ currentDrugStore.province }}</span>
          </div>

          <div class="detail-row">
            <label>省份ID：</label>
            <span>{{ currentDrugStore.provinceId }}</span>
          </div>
          <div class="detail-row">
            <label>市：</label>
            <span>{{ currentDrugStore.city }}</span>
          </div>
          <div class="detail-row">
            <label>市ID：</label>
            <span>{{ currentDrugStore.cityId }}</span>
          </div>

          <div class="detail-row">
            <label>区县：</label>
            <span>{{ currentDrugStore.area }}</span>
          </div>

          <div class="detail-row">
            <label>区县ID：</label>
            <span>{{ currentDrugStore.areaId }}</span>
          </div>

          <div class="detail-row">
            <label>是否市区：</label>
            <span>{{ currentDrugStore.isCity }}</span>
          </div>

          <div class="detail-row">
            <label>地址：</label>
            <span>{{ currentDrugStore.address }}</span>
          </div>

          <div class="detail-row">
            <label>统一社会信用代码：</label>
            <span>{{ currentDrugStore.usci }}</span>
          </div>

          <div class="detail-row">
            <label>药店类型：</label>
            <span>{{ currentDrugStore.pharmacyAture }}</span>
          </div>

          <div class="detail-row">
            <label>经营方式：</label>
            <span>{{ currentDrugStore.operation }}</span>
          </div>

          <div class="detail-row">
            <label>药店位置：</label>
            <span>{{ currentDrugStore.location }}</span>
          </div>

          <div class="detail-row">
            <label>是否双通道：</label>
            <span>{{ currentDrugStore.twoChannels }}</span>
          </div>

          <div class="detail-row">
            <label>是否双互联网：</label>
            <span>{{ currentDrugStore.isInternet }}</span>
          </div>

          <div class="detail-row">
            <label>是否DTP药房：</label>
            <span>{{ currentDrugStore.dtp }}</span>
          </div>

          <div class="detail-row">
            <label>药品经营许可证：</label>
            <span>{{ currentDrugStore.licenseNumber }}</span>
          </div>

          <div class="detail-row">
            <label>药品经营许可证有效期：</label>
            <span>{{ currentDrugStore.validity }}</span>
          </div>

          <div class="detail-row">
            <label>是否门诊统筹：</label>
            <span>{{ currentDrugStore.mztc }}</span>
          </div>

          <div class="detail-row">
            <label>成立日期：</label>
            <span>{{ currentDrugStore.createDate }}</span>
          </div>

          <div class="detail-row">
            <label>注册资金：</label>
            <span>{{ currentDrugStore.registCapi }}</span>
          </div>

          <div class="detail-row">
            <label>法人：</label>
            <span>{{ currentDrugStore.operName }}</span>
          </div>

          <div class="detail-row">
            <label>医保编码：</label>
            <span>{{ currentDrugStore.ybcode }}</span>
          </div>

          <div class="detail-row">
            <label>是否医保药店：</label>
            <span>{{ currentDrugStore.isYb }}</span>
          </div>

          <div class="detail-row">
            <label>豪森重复数据ID：</label>
            <span>{{ currentDrugStore.repeatId }}</span>
          </div>

          <div class="detail-row">
            <label>经营范围：</label>
            <span>{{ currentDrugStore.scope }}</span>
          </div>

          <div class="detail-row">
            <label>是否新增：</label>
            <span>{{ currentDrugStore.isInsert }}</span>
          </div>

          <div class="detail-row">
            <label>上级单位豪森编码：</label>
            <span>{{ currentDrugStore.mainBranch }}</span>
          </div>

          <div class="detail-row">
            <label>上级单位编码：</label>
            <span>{{ currentDrugStore.mainBranchKid }}</span>
          </div>

          <div class="detail-row">
            <label>上级单位名称：</label>
            <span>{{ currentDrugStore.mainBranchName }}</span>
          </div>


          <div class="detail-row">
            <label>登记状态：</label>
            <span>{{ currentDrugStore.signStatus }}</span>
          </div>

          <div class="detail-row">
            <label>院内店：</label>
            <span>{{ currentDrugStore.hosInside }}</span>
          </div>

          <div class="detail-row">
            <label>院边店：</label>
            <span>{{ currentDrugStore.hosOutside }}</span>
          </div>

          <div class="detail-row">
            <label>经纬度：</label>
            <span>{{ currentDrugStore.field1 }}</span>
          </div>


          <div class="detail-row">
            <label>添加日期：</label>
            <span>{{ currentDrugStore.addtime }}</span>
          </div>

          <div class="detail-row">
            <label>更新日期：</label>
            <span>{{ currentDrugStore.updatetime }}</span>
          </div>


          <div class="detail-row">
            <label>状态：</label>
            <span :class="{
                'status-active': currentDrugStore.status === '1',
                'status-uninactive': currentDrugStore.status === '3',
                'status-inactive': currentDrugStore.status === '4',
                'status-invalid': currentDrugStore.status === '2',
                'status-repeat': currentDrugStore.status === '5'
              }">
              {{
                currentDrugStore.status === '1' ? '清洗成功' :
                    currentDrugStore.status === '3' ? '无法清洗' :
                        currentDrugStore.status === '4' ? '禁用客户':
                            currentDrugStore.status === '2' ? '作废数据':
                                currentDrugStore.status === '5' ? '重复数据' : '其他状态'
              }}
            </span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="modal-close-btn" @click="closeDetailModal">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      DrugStoreData: {
        content: [],
        totalElements: 0,
        totalPages: 0,
        first: true,
        last: false,
        number: 0
      },
      loading: false,
      searchForm: {
        dataCode: '',
        name: '',
        dataId: '',
        keyid: '',
        hsCode: '',
        province: '',
        city: '',
        area: '',
        address: '',
        originalName: ''
      },
      pageSize: 10,
      pageNumber: 0,
      showDetailModal: false,
      currentDrugStore: {},

      // 列定义 - 为操作列添加 fixed 属性
      columns: [
        {label: 'dataId', width: 100},
        {label: '原始编码', width: 100},
        {label: '原始名称', width: 100},
        {label: 'keyId', width: 100},
        {label: '标准名称', width: 100},
        {label: '省份', width: 50},
        {label: '城市', width: 50},
        {label: '区县', width: 50},
        {label: '地址', width: 100},
        {label: '状态', width: 50},
        {label: '操作', width: 100, fixed: true}  // 添加 fixed: true
      ],
      resizing: false,
      resizeColumnIndex: null,
      startX: 0,
      startWidth: 0
    }
  },
  mounted() {
    this.fetchDrugStoreData();
  },
  methods: {
    async fetchDrugStoreData() {
      this.loading = true;
      try {
        const params = {
          page: this.pageNumber,
          size: this.pageSize
        };

        // 添加搜索条件
        if (this.searchForm.dataCode) {
          params.dataCode = this.searchForm.dataCode;
        }
        if (this.searchForm.name) {
          params.name = this.searchForm.name;
        }
        if (this.searchForm.dataId) {
          params.dataId = this.searchForm.dataId;
        }
        if (this.searchForm.keyid) {
          params.keyid = this.searchForm.keyid;
        }
        if (this.searchForm.hsCode) {
          params.hsCode = this.searchForm.hsCode;
        }
        if (this.searchForm.province) {
          params.province = this.searchForm.province;
        }
        if (this.searchForm.city) {
          params.city = this.searchForm.city;
        }
        if (this.searchForm.area) {
          params.area = this.searchForm.area;
        }
        if (this.searchForm.address) {
          params.address = this.searchForm.address;
        }
        if (this.searchForm.originalName) {
          params.originalName = this.searchForm.originalName;
        }

        const response = await axios.get('/api/mainData/getDrugStoreData', {
          params: params
        });

        this.DrugStoreData = response.data.data;

      } catch (error) {
        console.error('获取药店数据失败:', error);
        this.$message.error('获取药店数据失败');
      } finally {
        this.loading = false;
      }
    },
    handleSearch() {
      this.pageNumber = 0;
      this.fetchDrugStoreData();
    },
    resetSearch() {
      this.searchForm = {
        dataCode: '',
        name: '',
        dataId: '',
        keyid: '',
        hsCode: '',
        province: '',
        city: '',
        area: '',
        address: '',
        originalName: ''
      };
      this.pageNumber = 0;
      this.fetchDrugStoreData();
    },
    prevPage() {
      if (this.pageNumber > 0) {
        this.pageNumber--;
        this.fetchDrugStoreData();
      }
    },
    nextPage() {
      if (this.pageNumber < this.DrugStoreData.totalPages - 1) {
        this.pageNumber++;
        this.fetchDrugStoreData();
      }
    },
    handlePageSizeChange() {
      this.pageNumber = 0;
      this.fetchDrugStoreData();
    },
    showDetail(DrugStore) {
      this.currentDrugStore = DrugStore;
      this.showDetailModal = true;
    },
    closeDetailModal() {
      this.showDetailModal = false;
      this.currentDrugStore = {};
    },
    // 列宽调整方法
    startResize(e, index) {
      this.resizing = true;
      this.resizeColumnIndex = index;
      this.startX = e.clientX;
      this.startWidth = this.columns[index].width;

      document.addEventListener('mousemove', this.handleResize);
      document.addEventListener('mouseup', this.stopResize);

      e.preventDefault();
    },
    handleResize(e) {
      if (this.resizing) {
        const dx = e.clientX - this.startX;
        const newWidth = this.startWidth + dx;

        if (newWidth > 50 && newWidth < 500) { // 最小宽度限制
          this.columns[this.resizeColumnIndex].width = newWidth;
        }
      }
    },
    stopResize() {
      this.resizing = false;
      document.removeEventListener('mousemove', this.handleResize);
      document.removeEventListener('mouseup', this.stopResize);
    }
  }
}
</script>

<style scoped>
.DrugStore-data-view {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  max-width: min(1250px, 95vw);
  padding: 12px;
  box-sizing: border-box;
  background: white;
  font-size: 12px;
  margin: 0 auto;
}

/* 2K屏幕优化 */
@media (min-width: 2000px) and (max-width: 2600px) {
  .DrugStore-data-view  {
    max-width: min(2200px, 96vw);
  }
}

/* 超宽屏幕 */
@media (min-width: 2600px) {
  .DrugStore-data-view  {
    max-width: min(2400px, 95vw);
  }
}


.search-container {
  flex-shrink: 0;
  margin-bottom: 8px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 3px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.form-item {
  display: flex;
  align-items: center;
}

.form-item label {
  margin-right: 4px;
  font-weight: bold;
  white-space: nowrap;
  font-size: 12px;
}

.form-item input {
  padding: 5px 8px;
  border: 1px solid #dcdfe6;
  border-radius: 3px;
  min-width: 120px;
  font-size: 12px;
}

.form-actions {
  display: flex;
  gap: 8px;
  margin-left: auto;
}

button {
  padding: 5px 10px;
  border-radius: 3px;
  cursor: pointer;
  border: none;
  font-size: 12px;
}

.search-btn {
  background: #409eff;
  color: white;
}

.search-btn:hover {
  background: #66b1ff;
}

.reset-btn {
  background: #f5f7fa;
  border: 1px solid #dcdfe6;
}

.reset-btn:hover {
  background: #e6e9ed;
}

.detail-btn {
  background: #67c23a;
  color: white;
  padding: 4px 8px;
}

.detail-btn:hover {
  background: #85ce61;
}

.data-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.table-wrapper {
  flex: 1;
  overflow: auto;
  border: 1px solid #ebeef5;
  border-radius: 3px;
  position: relative;
}

.DrugStore-table {
  width: 100%;
  height: 80%;
  border-collapse: collapse;
  font-size: 12px;
  table-layout: fixed;
}

.DrugStore-table th,
.DrugStore-table td {
  border: 1px solid #ebeef5;
  padding: 6px 8px;
  text-align: left;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.DrugStore-table th {
  background-color: #f5f7fa;
  font-weight: bold;
  position: sticky;
  top: 0;
  user-select: none;
  -webkit-user-select: none;
}

/* 固定列样式 */
.fixed-column {
  position: sticky;
  right: 0;
  background-color: #f5f7fa;
  z-index: 10;
  box-shadow: -2px 0 5px rgba(0, 0, 0, 0.1);
}

.DrugStore-table tr:nth-child(even) .fixed-column {
  background-color: #fafafa;
}

.DrugStore-table tr:hover .fixed-column {
  background-color: #f5f7fa;
}

.DrugStore-table tr:nth-child(even) {
  background-color: #fafafa;
}

.DrugStore-table tr:hover {
  background-color: #f5f7fa;
}

.status-active {
  color: #78a2cc;
  font-weight: bold;
}

.status-uninactive {
  color: #9478cc;
  font-weight: bold;
}

.status-inactive {
  color: #f56c6c;
  font-weight: bold;
}

.status-repeat {
  color: rgba(228, 109, 186, 0.5);
  font-weight: bold;
}

.status-invalid {
  color: #d77030;
  font-weight: bold;
}

.no-data {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #909399;
  font-size: 12px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 12px;
  gap: 8px;
  padding: 6px;
  background: #f5f7fa;
  border-radius: 3px;
}

.page-btn {
  padding: 4px 8px;
  background: #ffffff;
  border: 1px solid #dcdfe6;
  cursor: pointer;
  border-radius: 3px;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 12px;
  color: #606266;
}

.pagination select {
  padding: 4px;
  border-radius: 3px;
  border: 1px solid #dcdfe6;
  background: white;
  font-size: 12px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-container {
  background: white;
  border-radius: 6px;
  width: 400px;
  max-width: 80%;
  max-height: 60vh;
  overflow-y: auto;
  box-shadow: 0 2px 10px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 10px 12px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 12px;
  color: #303133;
  font-weight: bold;
}

.close-btn {
  font-size: 12px;
  color: #909399;
  cursor: pointer;
}

.close-btn:hover {
  color: #606266;
}

.modal-body {
  padding: 12px;
  flex: 1;
}

.detail-row {
  display: flex;
  margin-bottom: 8px;
  line-height: 1.4;
}

.detail-row label {
  font-weight: bold;
  min-width: 70px;
  color: #606266;
  font-size: 12px;
}

.detail-row span {
  flex: 1;
  word-break: break-word;
  font-size: 12px;
}

.modal-footer {
  padding: 8px 12px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: flex-end;
}

.modal-close-btn {
  padding: 5px 10px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 3px;
  cursor: pointer;
  font-size: 12px;
}

.modal-close-btn:hover {
  background: #66b1ff;
}

.th-content {
  position: relative;
  padding-right: 10px;
}

.resize-handle {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  cursor: col-resize;
  background-color: transparent;
}

.resize-handle:hover {
  background-color: #409eff;
}

.input-wrapper {
  position: relative;
  display: inline-block;
}

.input-wrapper input {
  border: 1px solid #dcdfe6;
  border-radius: 3px;
  min-width: 120px;
  padding: 5px 20px 5px 8px;
  font-size: 11px;
}

.clear-icon {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  color: #c0c4cc;
  cursor: pointer;
  font-size: 12px;
}

.clear-icon:hover {
  color: #909399;
}
</style>