<template>
  <div class="hospital-data-view">
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
          <label>输入医院名称：</label>
          <div class="input-wrapper">
            <input
                v-model="searchForm.name"
                placeholder="请输入医院名称"
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
          <label>输入医院地址：</label>
          <div class="input-wrapper">
            <input
                v-model="searchForm.address"
                placeholder="请输入医院地址"
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
              <template v-if="index === 0">{{ hospital.dataId }}</template>
              <template v-else-if="index === 1">{{ hospital.dataCode }}</template>
              <template v-else-if="index === 2">{{ hospital.originalName }}</template>
              <template v-else-if="index === 3">{{ hospital.keyid }}</template>
              <template v-else-if="index === 4">{{ hospital.name }}</template>
              <!--              <template v-else-if="index === 5">{{ hospital.nameHistory }}</template>-->
              <template v-else-if="index === 5">{{ hospital.province }}</template>
              <template v-else-if="index === 6">{{ hospital.city }}</template>
              <template v-else-if="index === 7">{{ hospital.area }}</template>
              <template v-else-if="index === 8">{{ hospital.address }}</template>
              <template v-else-if="index === 9">
                        <span :class="{
                          'status-active': hospital.status === '1',
                          'status-uninactive': hospital.status === '3',
                          'status-inactive': hospital.status === '4',
                          'status-invalid': hospital.status === '2',
                          'status-repeat': hospital.status === '5'
                        }">
                          {{ hospital.status === '1' ? '清洗成功' :
                            hospital.status === '3' ? '无法清洗' :
                                hospital.status === '4' ? '禁用客户' :
                                    hospital.status === '2' ? '数据作废' :
                                        hospital.status === '5' ? '数据重复' :
                                            '其他状态' }}
                        </span>
              </template>
              <template v-else-if="index === 10">
                <button class="detail-btn" @click="showDetail(hospital)">详情</button> &nbsp;
                <button class="update-btn" @click="updateDetail(hospital)">更新</button>
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
            <label>dataId：</label>
            <span>{{ currentHospital.dataId }}</span>
          </div>
          <div class="detail-row">
            <label>原始编码：</label>
            <span>{{ currentHospital.dataCode }}</span>
          </div>


          <div class="detail-row">
            <label>keyId：</label>
            <span>{{ currentHospital.keyid }}</span>
          </div>

          <div class="detail-row">
            <label>数据类型：</label>
            <span>{{ currentHospital.dataType }}</span>
          </div>

          <div class="detail-row">
            <label>豪森清洗后编码：</label>
            <span>{{ currentHospital.hsCode }}</span>
          </div>

          <div class="detail-row">
            <label>标准名称：</label>
            <span>{{ currentHospital.name }}</span>
          </div>
          <div class="detail-row">
            <label>历史名称：</label>
            <span>{{ currentHospital.nameHistory }}</span>
          </div>

          <div class="detail-row">
            <label>省份：</label>
            <span>{{ currentHospital.province }}</span>
          </div>

          <div class="detail-row">
            <label>省份ID：</label>
            <span>{{ currentHospital.provinceId }}</span>
          </div>
          <div class="detail-row">
            <label>市：</label>
            <span>{{ currentHospital.city }}</span>
          </div>
          <div class="detail-row">
            <label>市ID：</label>
            <span>{{ currentHospital.cityId }}</span>
          </div>

          <div class="detail-row">
            <label>区县：</label>
            <span>{{ currentHospital.area }}</span>
          </div>

          <div class="detail-row">
            <label>区县ID：</label>
            <span>{{ currentHospital.areaId }}</span>
          </div>

          <div class="detail-row">
            <label>是否市区：</label>
            <span>{{ currentHospital.isCity }}</span>
          </div>

          <div class="detail-row">
            <label>地址：</label>
            <span>{{ currentHospital.address }}</span>
          </div>

          <div class="detail-row">
            <label>统一社会信用代码：</label>
            <span>{{ currentHospital.usci }}</span>
          </div>

          <div class="detail-row">
            <label>等级：</label>
            <span>{{ currentHospital.level }}</span>
          </div>

          <div class="detail-row">
            <label>等次：</label>
            <span>{{ currentHospital.grade }}</span>
          </div>

          <div class="detail-row">
            <label>所有制：</label>
            <span>{{ currentHospital.publicflag }}</span>
          </div>

          <div class="detail-row">
            <label>豪森医院类型：</label>
            <span>{{ currentHospital.hosClass }}</span>
          </div>

          <div class="detail-row">
            <label>豪森医院大类：</label>
            <span>{{ currentHospital.hosBigClass }}</span>
          </div>

          <div class="detail-row">
            <label>一级属性：</label>
            <span>{{ currentHospital.class1 }}</span>
          </div>

          <div class="detail-row">
            <label>二级属性：</label>
            <span>{{ currentHospital.class2 }}</span>
          </div>
          <div class="detail-row">
            <label>三级属性：</label>
            <span>{{ currentHospital.class3 }}</span>
          </div>
          <div class="detail-row">
            <label>四级属性：</label>
            <span>{{ currentHospital.class4 }}</span>
          </div>
          <div class="detail-row">
            <label>五级属性：</label>
            <span>{{ currentHospital.class5 }}</span>
          </div>

          <div class="detail-row">
            <label>是否军队医院：</label>
            <span>{{ currentHospital.militaryHos }}</span>
          </div>

          <div class="detail-row">
            <label>是否互联网医院：</label>
            <span>{{ currentHospital.internetHos }}</span>
          </div>

          <div class="detail-row">
            <label>医联体/医共体：</label>
            <span>{{ currentHospital.medUnionCommunity }}</span>
          </div>

          <div class="detail-row">
            <label>医保编码：</label>
            <span>{{ currentHospital.ybcode }}</span>
          </div>

          <div class="detail-row">
            <label>登记号：</label>
            <span>{{ currentHospital.regcode }}</span>
          </div>

          <div class="detail-row">
            <label>许可证有效期：</label>
            <span>{{ currentHospital.validity }}</span>
          </div>

          <div class="detail-row">
            <label>是否门诊统筹：</label>
            <span>{{ currentHospital.menzhen }}</span>
          </div>

          <div class="detail-row">
            <label>床位数目：</label>
            <span>{{ currentHospital.bedCount }}</span>
          </div>

          <div class="detail-row">
            <label>医护人数：</label>
            <span>{{ currentHospital.medicalCount }}</span>
          </div>

          <div class="detail-row">
            <label>诊疗科目：</label>
            <span>{{ currentHospital.subjects }}</span>
          </div>

          <div class="detail-row">
            <label>法人：</label>
            <span>{{ currentHospital.legalperson }}</span>
          </div>

          <div class="detail-row">
            <label>是否新增：</label>
            <span>{{ currentHospital.isInsert }}</span>
          </div>

          <div class="detail-row">
            <label>豪森重复数据ID：</label>
            <span>{{ currentHospital.repeatId }}</span>
          </div>

          <div class="detail-row">
            <label>上级单位豪森编码：</label>
            <span>{{ currentHospital.generalBranch }}</span>
          </div>

          <div class="detail-row">
            <label>上级单位KeyId：</label>
            <span>{{ currentHospital.generalBranchKid }}</span>
          </div>

          <div class="detail-row">
            <label>上级单位名称：</label>
            <span>{{ currentHospital.generalBranchName }}</span>
          </div>

          <div class="detail-row">
            <label>添加日期：</label>
            <span>{{ currentHospital.addtime }}</span>
          </div>

          <div class="detail-row">
            <label>更新日期：</label>
            <span>{{ currentHospital.updatetime }}</span>
          </div>



          <div class="detail-row">
            <label>状态：</label>
            <span :class="{
                'status-active': currentHospital.status === '1',
                'status-invalid': currentHospital.status === '2',
                'status-uninactive': currentHospital.status === '3',
                'status-inactive': currentHospital.status === '4',
                'status-repeat': currentHospital.status === '5'
              }">
              {{
                currentHospital.status === '1' ? '清洗成功' :
                    currentHospital.status === '2' ? '数据作废' :
                        currentHospital.status === '3' ? '无法清洗' :
                            currentHospital.status === '4' ? '禁用客户' :
                                currentHospital.status === '5' ? '数据重复':'其他状态'
              }}
            </span>
          </div>
        </div>

        <div class="modal-footer">
          <button class="modal-close-btn" @click="closeDetailModal">关闭</button>
        </div>
      </div>
    </div>

    <!-- 更新弹窗 -->
    <div v-if="updateDetailModal" class="modal-overlay" @click.self="closeUpdateModal">
      <div class="modal-container">
        <div class="modal-header">
          <h3>数据更新</h3>
          <span class="close-btn" @click="closeUpdateModal">&times;</span>
        </div>

        <div class="modal-body">
          <form @submit.prevent="saveChanges" class="hospital-form">
            <div class="form-grid">
              <!-- 第一列 -->
              <div class="form-column">

                <div class="form-group">
                  <label class="form-label">批次编号：</label>
                  <input v-model="currentHospital.batchCode" readonly class="form-input">
                </div>


                <div class="form-group">
                  <label class="form-label">dataId：</label>
                  <input v-model="currentHospital.dataId" readonly class="form-input">
                </div>

                <div class="form-group">
                  <label class="form-label">数据类型：</label>
                  <input v-model="currentHospital.dataType"  readonly class="form-input">
                </div>

                <div class="form-group">
                  <label class="form-label">原始编码：</label>
                  <input v-model="currentHospital.dataCode" readonly class="form-input">
                </div>

                <div class="form-group">
                  <label class="form-label">原始名称：</label>
                  <input v-model="currentHospital.originalName" readonly class="form-input">
                </div>

                <div class="form-group">
                  <label class="form-label">机构类型：</label>
                  <input  readonly class="form-input" value="医院">
                </div>



              </div>

              <!-- 第二列 -->
              <div class="form-column">

                <div class="form-group">
                  <label class="form-label">备注：</label>
                  <input  class="form-input">
                </div>

                <div class="form-group">
                  <label class="form-label">keyId：</label>
                  <input v-model="currentHospital.keyid" class="form-input">
                </div>

                <div class="form-group">
                  <label class="form-label">医院名称：</label>
                  <input v-model="currentHospital.name" class="form-input">
                </div>

                <div class="form-group">
                  <label class="form-label">历史名称：</label>
                  <input v-model="currentHospital.nameHistory" class="form-input">
                </div>

                <div class="form-group">
                  <label class="form-label">省：</label>
                  <input v-model="currentHospital.province" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">省ID：</label>
                  <input v-model="currentHospital.provinceId" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">市：</label>
                  <input v-model="currentHospital.city" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">市ID：</label>
                  <input v-model="currentHospital.cityId" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">区县：</label>
                  <input v-model="currentHospital.area" class="form-input">
                </div>

                <div class="form-group">
                  <label class="form-label">区县ID：</label>
                  <input v-model="currentHospital.areaId" class="form-input">
                </div>

                <div class="form-group">
                  <label class="form-label">地址：</label>
                  <input v-model="currentHospital.address" class="form-input">
                </div>

                <div class="form-group">
                  <label class="form-label">等级：</label>
                  <input v-model="currentHospital.level" class="form-input">
                </div>

                <div class="form-group">
                  <label class="form-label">等次：</label>
                  <input v-model="currentHospital.grade" class="form-input">
                </div>


              </div>

            </div>

            <div class="form-actions">
              <button type="submit" class="btn btn-primary">提交推送</button>
              <button type="button" class="btn btn-secondary" @click="closeUpdateModal">取消</button>
            </div>
          </form>
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
        // dataCode   dataId keyid hsCode province city area name address
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
      updateDetailModal: false,
      currentHospital: {},

      // 列定义
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
        {label: '操作', width: 100}
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

    // 详情查看
    showDetail(hospital) {
      this.currentHospital = hospital;
      this.showDetailModal = true;
    },

    closeDetailModal() {
      this.showDetailModal = false;
      this.currentHospital = {};
    },

    // 更新查看
    updateDetail(hospital) {
      this.currentHospital = hospital;
      this.updateDetailModal = true;
    },

    closeUpdateModal() {
      this.updateDetailModal = false;
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
.hospital-data-view {
  display: flex;
  flex-direction: column;
  height: 85vh;
  width: 1250px;
  padding: 12px;
  box-sizing: border-box;
  background: white;
  font-size: 12px;
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

.update-btn {
  background: #3073d7;
  color: white;
  padding: 4px 8px;
}

.update-btn:hover {
  background: #3073d7;
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
}

.hospital-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
  table-layout: fixed;
}

.hospital-table th,
.hospital-table td {
  border: 1px solid #ebeef5;
  padding: 6px 8px;
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

.hospital-form {
  padding: 12px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
  margin-bottom: 12px;
}

.form-column {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-label {
  margin-bottom: 4px;
  font-weight: 500;
  color: #555;
  font-size: 12px;
}

.form-input {
  padding: 6px 8px;
  border: 1px solid #ddd;
  border-radius: 3px;
  font-size: 12px;
  transition: border-color 0.3s;
  background-color: #fff;
}

.form-input:focus {
  border-color: #409eff;
  outline: none;
  box-shadow: 0 0 0 1px rgba(64, 158, 255, 0.2);
}

.form-input[readonly] {
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #eee;
}

.btn {
  padding: 5px 10px;
  border-radius: 3px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.btn-primary {
  background-color: #409eff;
  color: white;
}

.btn-primary:hover {
  background-color: #66b1ff;
}

.btn-secondary {
  background-color: #fff;
  color: #606266;
  border-color: #dcdfe6;
}

.btn-secondary:hover {
  color: #409eff;
  border-color: #c6e2ff;
  background-color: #ecf5ff;
}
</style>




