<template>
  <div class="hospital-data-view">
    <div class="search-container">
      <div class="search-form">
        <div class="form-item">
          <label>医院编码：</label>
          <input
              v-model="searchForm.dataCode"
              placeholder="请输入医院编码"
              @keyup.enter="handleSearch"
          >
        </div>
        <div class="form-item">
          <label>医院名称：</label>
          <input
              v-model="searchForm.name"
              placeholder="请输入医院名称"
              @keyup.enter="handleSearch"
          >
        </div>
        <div class="form-actions">
          <button class="search-btn" @click="handleSearch">查询</button>
          <button class="reset-btn" @click="resetSearch">重置</button>
        </div>
      </div>
    </div>

    <div class="data-container">
      <div class="table-wrapper">
        <table class="hospital-table" v-if="hospitalData.content && hospitalData.content.length > 0">
          <thead>
          <tr>
            <th v-for="(column, index) in columns" :key="index"
                :style="{ width: column.width + 'px' }"
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
          <tr v-for="hospital in hospitalData.content" :key="hospital.dataId">
            <td v-for="(column, index) in columns" :key="index"
                :style="{ width: column.width + 'px' }">
              <template v-if="index === 0">{{ hospital.dataCode }}</template>
              <template v-else-if="index === 1">{{ hospital.name }}</template>
              <template v-else-if="index === 2">{{ hospital.province }}</template>
              <template v-else-if="index === 3">{{ hospital.city }}</template>
              <template v-else-if="index === 4">{{ hospital.level }}</template>
              <template v-else-if="index === 5">{{ hospital.address }}</template>
              <template v-else-if="index === 6">{{ hospital.bedCount }}</template>
              <template v-else-if="index === 7">
                        <span :class="{
                          'status-active': hospital.status === '1',
                          'status-uninactive': hospital.status === '3',
                          'status-inactive': hospital.status === '4'
                        }">
                          {{ hospital.status === '1' ? '清洗成功' :
                            hospital.status === '3' ? '无法清洗' :
                                hospital.status === '4' ? '禁用客户' : '错误' }}
                        </span>
              </template>
              <template v-else-if="index === 8">
                <button class="detail-btn" @click="showDetail(hospital)">详情</button>
              </template>
            </td>
          </tr>
          </tbody>
        </table>
        <div v-else class="no-data">
          {{ loading ? '数据加载中...' : '没有找到匹配的医院数据' }}
        </div>
        <div class="pagination" v-if="hospitalData.content && hospitalData.content.length > 0">
          <button
              :disabled="hospitalData.first"
              @click="prevPage"
              class="page-btn"
          >
            上一页
          </button>
          <span class="page-info">
            第 {{ hospitalData.number + 1 }} 页 / 共 {{ hospitalData.totalPages }} 页 (共 {{ hospitalData.totalElements }} 条)
          </span>
          <button
              :disabled="hospitalData.last"
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
          <h3>医院详情</h3>
          <span class="close-btn" @click="closeDetailModal">&times;</span>
        </div>
        <div class="modal-body">
          <div class="detail-row">
            <label>医院编码：</label>
            <span>{{ currentHospital.dataCode }}</span>
          </div>
          <div class="detail-row">
            <label>医院名称：</label>
            <span>{{ currentHospital.name }}</span>
          </div>
          <div class="detail-row">
            <label>省份：</label>
            <span>{{ currentHospital.province }}</span>
          </div>
          <div class="detail-row">
            <label>城市：</label>
            <span>{{ currentHospital.city }}</span>
          </div>
          <div class="detail-row">
            <label>医院等级：</label>
            <span>{{ currentHospital.level }}</span>
          </div>
          <div class="detail-row">
            <label>医院地址：</label>
            <span>{{ currentHospital.address }}</span>
          </div>
          <div class="detail-row">
            <label>床位数量：</label>
            <span>{{ currentHospital.bedCount }}</span>
          </div>
          <div class="detail-row">
            <label>状态：</label>
            <span :class="{
                'status-active': currentHospital.status === '1',
                'status-uninactive': currentHospital.status === '3',
                'status-inactive': currentHospital.status === '4'
              }">
              {{  currentHospital.status === '1' ? '清洗成功' :
                  currentHospital.status === '3' ? '无法清洗' :
                    currentHospital.status === '4' ? '禁用客户' : '其他状态' }}
            </span>
          </div>
          <div class="detail-row" v-if="currentHospital.description">
            <label>医院描述：</label>
            <span>{{ currentHospital.description }}</span>
          </div>
          <div class="detail-row" v-if="currentHospital.contactPhone">
            <label>联系电话：</label>
            <span>{{ currentHospital.contactPhone }}</span>
          </div>
          <div class="detail-row" v-if="currentHospital.establishDate">
            <label>成立日期：</label>
            <span>{{ formatDate(currentHospital.establishDate) }}</span>
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
      hospitalData: {
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
        name: ''
      },
      pageSize: 10,
      pageNumber: 0,
      showDetailModal: false,
      currentHospital: {},

      // 列定义
      columns: [
        { label: '医院编码', width: 120 },
        { label: '医院名称', width: 150 },
        { label: '省份', width: 100 },
        { label: '城市', width: 100 },
        { label: '医院等级', width: 100 },
        { label: '医院地址', width: 200 },
        { label: '床位数量', width: 100 },
        { label: '状态', width: 100 },
        { label: '操作', width: 100 }
      ],
      resizing: false,
      resizeColumnIndex: null,
      startX: 0,
      startWidth: 0
    }
  },
  mounted() {
    this.fetchHospitalData();
  },
  methods: {

    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString();
    },
    async fetchHospitalData() {
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

        const response = await axios.get('/api/mainData/getHospitalData', {
          params: params
        });

        this.hospitalData = response.data.data;
      } catch (error) {
        console.error('获取医院数据失败:', error);
        this.$message.error('获取医院数据失败');
      } finally {
        this.loading = false;
      }
    },
    handleSearch() {
      this.pageNumber = 0;
      this.fetchHospitalData();
    },
    resetSearch() {
      this.searchForm = {
        dataCode: '',
        name: ''
      };
      this.pageNumber = 0;
      this.fetchHospitalData();
    },
    prevPage() {
      if (this.pageNumber > 0) {
        this.pageNumber--;
        this.fetchHospitalData();
      }
    },
    nextPage() {
      if (this.pageNumber < this.hospitalData.totalPages - 1) {
        this.pageNumber++;
        this.fetchHospitalData();
      }
    },
    handlePageSizeChange() {
      this.pageNumber = 0;
      this.fetchHospitalData();
    },
    showDetail(hospital) {
      this.currentHospital = hospital;
      console.log(hospital);
      this.showDetailModal = true;
    },
    closeDetailModal() {
      this.showDetailModal = false;
      this.currentHospital = {};
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

        if (newWidth > 50) { // 最小宽度限制
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
.hospital-data-view {
  display: flex;
  flex-direction: column;
  height: 85vh;
  padding: 20px;
  box-sizing: border-box;
  background: white;
}

.search-container {
  flex-shrink: 0;
  margin-bottom: 10px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: center;
}

.form-item {
  display: flex;
  align-items: center;
}

.form-item label {
  margin-right: 8px;
  font-weight: bold;
  white-space: nowrap;
}

.form-item input {
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  min-width: 200px;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-left: auto;
}

button {
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  border: none;
  font-size: 14px;
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
  padding: 6px 12px;
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
  border-radius: 4px;
}

.hospital-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  table-layout: fixed;
}

.hospital-table th,
.hospital-table td {
  border: 1px solid #ebeef5;
  padding: 12px;
  text-align: left;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hospital-table th {
  background-color: #f5f7fa;
  font-weight: bold;
  position: sticky;
  top: 0;
  user-select: none;
  -webkit-user-select: none;
}

.hospital-table tr:nth-child(even) {
  background-color: #fafafa;
}

.hospital-table tr:hover {
  background-color: #f5f7fa;
}

.status-active {
  color: #6da1e4;
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



.no-data {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #909399;
  font-size: 16px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  gap: 15px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.page-btn {
  padding: 6px 12px;
  background: #ffffff;
  border: 1px solid #dcdfe6;
  cursor: pointer;
  border-radius: 4px;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #606266;
}

.pagination select {
  padding: 6px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  background: white;
}

/* 详情弹窗样式 */
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
  border-radius: 8px;
  width: 600px;
  max-width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.close-btn {
  font-size: 24px;
  color: #909399;
  cursor: pointer;
}

.close-btn:hover {
  color: #606266;
}

.modal-body {
  padding: 20px;
  flex: 1;
}

.detail-row {
  display: flex;
  margin-bottom: 15px;
  line-height: 1.5;
}

.detail-row label {
  font-weight: bold;
  min-width: 100px;
  color: #606266;
}

.detail-row span {
  flex: 1;
  word-break: break-word;
}

.modal-footer {
  padding: 12px 20px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: flex-end;
}

.modal-close-btn {
  padding: 8px 16px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.modal-close-btn:hover {
  background: #66b1ff;
}

/* 列宽调整相关样式 */
.th-content {
  position: relative;
  padding-right: 16px;
}

.resize-handle {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  width: 5px;
  cursor: col-resize;
  background-color: transparent;
}

.resize-handle:hover {
  background-color: #409eff;
}
</style>