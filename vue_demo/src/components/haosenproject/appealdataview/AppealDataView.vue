<template>
  <div class="Appeal-data-view">
    <!-- 搜索区域 -->
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
          <label>清洗后的编码：</label>
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
          <label>输入名称：</label>
          <div class="input-wrapper">
            <input
                v-model="searchForm.name"
                placeholder="请输入名称"
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
          <label>输入地址：</label>
          <div class="input-wrapper">
            <input
                v-model="searchForm.address"
                placeholder="请输入地址"
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
          <button
              class="export-btn"
              @click="toExcel"
              :disabled="exporting"
              :style="{ opacity: exporting ? 0.7 : 1 }"
          >
            {{ exporting ? '导出中...' : '导出' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 数据表格区域 -->
    <div class="data-container">
      <div class="table-wrapper">
        <table class="Appeal-table" v-if="AppealData.content && AppealData.content.length > 0">
          <thead>
          <tr>
            <th v-for="(column, index) in columns" :key="index"
                :style="{ width: column.width + 'px' }"
                :class="{ 'fixed-column': column.fixed }"
                @mousedown="startResize($event, index)">
              <div class="th-content">
                {{ column.label }}
                <div class="resize-handle" @mousedown.stop="startResize($event, index)"></div>
              </div>
            </th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="Appeal in AppealData.content" :key="Appeal.dataId">
            <td v-for="(column, index) in columns" :key="index"
                :style="{ width: column.width + 'px' }"
                :class="{ 'fixed-column': column.fixed }">
              <template v-if="index === 0">{{ Appeal.dataId }}</template>
              <template v-else-if="index === 1">{{ Appeal.dataCode }}</template>
              <template v-else-if="index === 2">{{ Appeal.originalName }}</template>
              <template v-else-if="index === 3">{{ Appeal.keyid }}</template>
              <template v-else-if="index === 4">{{ Appeal.name }}</template>
              <template v-else-if="index === 5">{{ Appeal.province }}</template>
              <template v-else-if="index === 6">{{ Appeal.city }}</template>
              <template v-else-if="index === 7">{{ Appeal.area }}</template>
              <template v-else-if="index === 8">{{ Appeal.kuAddress }}</template>
              <template v-else-if="index === 9">{{ Appeal.appealRemark }}</template>
              <template v-else-if="index === 10">
                <button class="detail-btn" @click="showDetail(Appeal)">详情</button>
                &nbsp;
                <button class="update-btn" @click="appealDetail(Appeal)">申诉</button>
              </template>
            </td>
          </tr>
          </tbody>
        </table>
        <div v-else class="no-data">
          {{ loading ? '数据加载中...' : '没有找到匹配的数据' }}
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="AppealData.content && AppealData.content.length > 0">
          <button
              :disabled="AppealData.first"
              @click="prevPage"
              class="page-btn"
          >
            上一页
          </button>
          <span class="page-info">
            第 {{ AppealData.number + 1 }} 页 / 共 {{ AppealData.totalPages }} 页 (共 {{ AppealData.totalElements }} 条)
          </span>
          <button
              :disabled="AppealData.last"
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
          <div class="detail-row" v-if="currentAppeal">
            <label>批次编号：</label>
            <span class="copy-btn" @click="copyText(currentAppeal.batchCode, $event)">
              {{ currentAppeal.batchCode }}
            </span>
          </div>

          <div class="detail-row">
            <label>dataId：</label>
            <span class="copy-btn" @click="copyText(currentAppeal.dataId, $event)">
              {{ currentAppeal.dataId }}
            </span>
          </div>

          <div class="detail-row">
            <label>数据类型：</label>
            <span v-if="currentAppeal.dataType === '1'">存量</span>
            <span v-else-if="currentAppeal.dataType === '2'">增量</span>
            <span v-else>未知</span>
          </div>

          <div class="detail-row">
            <label>原始数据编码：</label>
            <span class="copy-btn" @click="copyText(currentAppeal.dataCode, $event)">
              {{ currentAppeal.dataCode }}
            </span>
          </div>

          <div class="detail-row">
            <label>原始数据名称：</label>
            <span class="copy-btn" @click="copyText(currentAppeal.originalName, $event)">
              {{ currentAppeal.originalName }}
            </span>
          </div>

          <div class="detail-row">
            <label>省份：</label>
            <span>{{ currentAppeal.originalProvince }}</span>
          </div>

          <div class="detail-row">
            <label>原始地址：</label>
            <span>{{ currentAppeal.originalAddress }}</span>
          </div>

          <div class="detail-row">
            <label>经销商：</label>
            <span>{{ currentAppeal.companyName }}</span>
          </div>

          <div class="detail-row">
            <label>申诉原因：</label>
            <span>{{ currentAppeal.appealRemark }}</span>
          </div>

          <div class="detail-row">
            <label>申诉解决：</label>
            <span>{{ currentAppeal.solveRemark }}</span>
          </div>

          <div class="detail-row">
            <label>机构类型：</label>
            <span>{{ currentAppeal.institutionType }}</span>
          </div>

          <div class="detail-row">
            <label>keyId：</label>
            <span class="copy-btn" @click="copyText(currentAppeal.keyid, $event)">
              {{ currentAppeal.keyid }}
            </span>
          </div>

          <div class="detail-row">
            <label>医院名称：</label>
            <span class="copy-btn" @click="copyText(currentAppeal.name, $event)">
              {{ currentAppeal.name }}
            </span>
          </div>

          <div class="detail-row">
            <label>历史名称：</label>
            <span>{{ currentAppeal.nameHistory }}</span>
          </div>

          <div class="detail-row">
            <label>省：</label>
            <span>{{ currentAppeal.province }}</span>
          </div>

          <div class="detail-row">
            <label>省份ID：</label>
            <span>{{ currentAppeal.provinceid }}</span>
          </div>

          <div class="detail-row">
            <label>市：</label>
            <span>{{ currentAppeal.city }}</span>
          </div>

          <div class="detail-row">
            <label>市ID：</label>
            <span>{{ currentAppeal.cityid }}</span>
          </div>

          <div class="detail-row">
            <label>区县：</label>
            <span>{{ currentAppeal.area }}</span>
          </div>

          <div class="detail-row">
            <label>区县ID：</label>
            <span>{{ currentAppeal.areaid }}</span>
          </div>

          <div class="detail-row">
            <label>地址：</label>
            <span>{{ currentAppeal.kuAddress }}</span>
          </div>

          <div class="detail-row">
            <label>等级：</label>
            <span>{{ currentAppeal.level }}</span>
          </div>

          <div class="detail-row">
            <label>等次：</label>
            <span>{{ currentAppeal.grade }}</span>
          </div>

          <div class="detail-row">
            <label>所有制：</label>
            <span>{{ currentAppeal.publicflag }}</span>
          </div>

          <div class="detail-row">
            <label>类别：</label>
            <span>{{ currentAppeal.classify }}</span>
          </div>

          <div class="detail-row">
            <label>总分院kid：</label>
            <span>{{ currentAppeal.generalBranchKid }}</span>
          </div>

          <div class="detail-row">
            <label>总分院名称：</label>
            <span>{{ currentAppeal.generalBranchName }}</span>
          </div>

          <div class="detail-row">
            <label>军队医院：</label>
            <span>{{ currentAppeal.militaryHos }}</span>
          </div>

          <div class="detail-row">
            <label>登记号：</label>
            <span>{{ currentAppeal.regcode }}</span>
          </div>

          <div class="detail-row">
            <label>有效期：</label>
            <span>{{ currentAppeal.validity }}</span>
          </div>

          <div class="detail-row">
            <label>诊疗科室：</label>
            <span>{{ currentAppeal.subjects }}</span>
          </div>

          <div class="detail-row">
            <label>法人代表：</label>
            <span>{{ currentAppeal.legalperson }}</span>
          </div>

          <div class="detail-row">
            <label>统一社会信用代码：</label>
            <span>{{ currentAppeal.usci }}</span>
          </div>

          <div class="detail-row">
            <label>经营方式：</label>
            <span>{{ currentAppeal.operation }}</span>
          </div>

          <div class="detail-row">
            <label>经营范围：</label>
            <span>{{ currentAppeal.scope }}</span>
          </div>

          <div class="detail-row">
            <label>总分店kid：</label>
            <span>{{ currentAppeal.mainBranchKid }}</span>
          </div>

          <div class="detail-row">
            <label>总分店名称：</label>
            <span>{{ currentAppeal.mainBranchName }}</span>
          </div>

          <div class="detail-row">
            <label>成立时间：</label>
            <span>{{ currentAppeal.createDate }}</span>
          </div>

          <div class="detail-row">
            <label>注册资金：</label>
            <span>{{ currentAppeal.registCapi }}</span>
          </div>

          <div class="detail-row">
            <label>企业类型：</label>
            <span>{{ currentAppeal.econKind }}</span>
          </div>

          <div class="detail-row">
            <label>登记状态：</label>
            <span>{{ currentAppeal.signStatus }}</span>
          </div>

          <div class="detail-row">
            <label>所属行业：</label>
            <span>{{ currentAppeal.industry }}</span>
          </div>

          <div class="detail-row">
            <label>登记机关：</label>
            <span>{{ currentAppeal.belong }}</span>
          </div>
        </div>

        <div class="modal-footer">
          <button class="modal-close-btn" @click="closeDetailModal">关闭</button>
        </div>
      </div>
    </div>

    <!-- 申诉处理弹窗 -->
    <div v-if="appealDetailModal" class="modal-overlay" @click.self="closeUpdateModal">
      <div class="modal-container">
        <div class="modal-header">
          <h3>申诉处理</h3>
          <span class="close-btn" @click="closeUpdateModal">&times;</span>
        </div>

        <div class="modal-body">
          <form @submit.prevent="saveChanges" class="hospital-form">
            <div class="form-grid">
              <!-- 第一列 -->
              <div class="form-column">
                <div class="form-group">
                  <label class="form-label">批次编号：</label>
                  <input v-model="currentAppealDetail.batchCode" readonly class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">dataId：</label>
                  <input v-model="currentAppealDetail.dataId" readonly class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">数据类型：</label>
                  <input
                      :value="currentAppealDetail.dataType === '1' ? '存量' : currentAppealDetail.dataType === '2' ? '增量' : '未知'"
                      readonly
                      class="form-input"
                  >
                </div>
                <div class="form-group">
                  <label class="form-label">原始编码：</label>
                  <input v-model="currentAppealDetail.dataCode" readonly class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">原始名称：</label>
                  <input v-model="currentAppealDetail.originalName" readonly class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">原始省份：</label>
                  <input v-model="currentAppealDetail.originalProvince" readonly class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">经销商：</label>
                  <input v-model="currentAppealDetail.companyName" readonly class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">申诉原因：</label>
                  <input v-model="currentAppealDetail.appealRemark" readonly class="form-input">
                </div>
              </div>

              <!-- 第二列 -->
              <div class="form-column">
                <div class="form-group">
                  <label class="form-label">申诉解决：</label>
                  <input v-model="currentAppealDetail.solveRemark" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">机构类型：</label>
                  <select v-model="currentAppealDetail.institutionType" class="form-input">
                    <option value="医院">医院</option>
                    <option value="药店">药店</option>
                    <option value="商业">商业</option>
                  </select>
                </div>
                <div class="form-group">
                  <label class="form-label">keyId：</label>
                  <input v-model="currentAppealDetail.keyid" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">标准名称：</label>
                  <input v-model="currentAppealDetail.name" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">历史名称：</label>
                  <input v-model="currentAppealDetail.nameHistory" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">省：</label>
                  <input v-model="currentAppealDetail.province" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">省ID：</label>
                  <input v-model="currentAppealDetail.provinceid" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">市：</label>
                  <input v-model="currentAppealDetail.city" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">市ID：</label>
                  <input v-model="currentAppealDetail.cityid" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">区县：</label>
                  <input v-model="currentAppealDetail.area" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">区县ID：</label>
                  <input v-model="currentAppealDetail.areaid" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">地址：</label>
                  <input v-model="currentAppealDetail.kuAddress" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">等级：</label>
                  <select v-model="currentAppealDetail.level" class="form-input">
                    <option value="未定级">未定级</option>
                    <option value="一级">一级</option>
                    <option value="二级">二级</option>
                    <option value="三级">三级</option>
                  </select>
                </div>
                <div class="form-group">
                  <label class="form-label">等次：</label>
                  <select v-model="currentAppealDetail.grade" class="form-input">
                    <option value="未定等">未定等</option>
                    <option value="甲等">甲等</option>
                    <option value="乙等">乙等</option>
                    <option value="丙等">丙等</option>
                  </select>
                </div>
                <div class="form-group">
                  <label class="form-label">所有制：</label>
                  <select v-model="currentAppealDetail.publicflag" class="form-input">
                    <option value="公立">公立</option>
                    <option value="民营">民营</option>
                  </select>
                </div>
                <div class="form-group">
                  <label class="form-label">类别：</label>
                  <input v-model="currentAppealDetail.classify" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">总分院kid：</label>
                  <input v-model="currentAppealDetail.generalBranchKid" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">总分院名称：</label>
                  <input v-model="currentAppealDetail.generalBranchName" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">是否军队医院：</label>
                  <select v-model="currentAppealDetail.militaryHos" class="form-input">
                    <option value="1">是</option>
                    <option value="0">否</option>
                  </select>
                </div>
                <div class="form-group">
                  <label class="form-label">登记号：</label>
                  <input v-model="currentAppealDetail.regcode" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">有效期：</label>
                  <input v-model="currentAppealDetail.validity" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">诊疗科室：</label>
                  <input v-model="currentAppealDetail.subjects" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">法人代表：</label>
                  <input v-model="currentAppealDetail.legalperson" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">统一社会信用代码：</label>
                  <input v-model="currentAppealDetail.usci" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">经营方式：</label>
                  <select v-model="currentAppealDetail.operation" class="form-input">
                    <option value="零售">零售</option>
                    <option value="批发">批发</option>
                    <option value="连锁">连锁</option>
                  </select>
                </div>
                <div class="form-group">
                  <label class="form-label">经营范围：</label>
                  <input v-model="currentAppealDetail.scope" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">总分店kid：</label>
                  <input v-model="currentAppealDetail.mainBranchKid" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">总分店名称：</label>
                  <input v-model="currentAppealDetail.mainBranchName" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">成立日期：</label>
                  <input v-model="currentAppealDetail.createDate" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">注册资金：</label>
                  <input v-model="currentAppealDetail.registCapi" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">企业类型：</label>
                  <input v-model="currentAppealDetail.econKind" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">登记状态：</label>
                  <input v-model="currentAppealDetail.signStatus" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">所属行业：</label>
                  <input v-model="currentAppealDetail.industry" class="form-input">
                </div>
                <div class="form-group">
                  <label class="form-label">登记机关：</label>
                  <input v-model="currentAppealDetail.belong" class="form-input">
                </div>
              </div>
            </div>
          </form>
        </div>

        <div class="modal-footer">
          <button @click="resetForm" class="btn btn-primary">重置匹配</button>
          &nbsp;
          <button
              @click="findDaKuData"
              class="btn btn-primary"
              :class="{ 'disabled-btn': isFinding }"
              :disabled="isFinding"
          >
            <span v-if="isFinding">
              <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              匹配中...
            </span>
            <span v-else>匹配大库</span>
          </button>
          &nbsp;
          <button
              @click="saveChanges"
              class="btn btn-primary"
              :class="{ 'disabled-btn': isSaving }"
              :disabled="isSaving"
          >
            <span v-if="isSaving">
              <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              推送中...
            </span>
            <span v-else>提交推送</span>
          </button>
          &nbsp;
          <button type="button" class="btn btn-secondary" @click="closeUpdateModal">取消</button>
        </div>
      </div>
    </div>

    <!-- 信息提示匹配弹窗 -->
    <div v-if="showResultModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content" :class="{
        'success': uploadResult.type === 'success',
        'error': uploadResult.type === 'error',
        'confirm': uploadResult.type === 'confirm'
      }">
        <div class="modal-header">
          <h3>{{ uploadResult.success || '提示' }}</h3>
          <button class="modal-close" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <p>{{ uploadResult.message }}</p>
          <p v-if="uploadResult.details" class="result-details">{{ uploadResult.details }}</p>
        </div>
        <div class="modal-footer">
          <template v-if="uploadResult.type === 'confirm'">
            <button class="modal-btn confirm-btn" @click="closeModal">确定</button>
            <button class="modal-btn cancel-btn" @click="showResultModal = false">取消</button>
          </template>
          <template v-else>
            <button class="modal-btn" @click="closeModal">确定</button>
          </template>
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
      AppealData: {
        content: [],
        totalElements: 0,
        totalPages: 0,
        first: true,
        last: false,
        number: 0
      },
      loading: false,
      exporting: false,
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
      currentAppeal: {},
      currentAppealDetail: {},
      matchedAppeal: {},
      originalAppeal: {},
      appealDetailModal: false,
      showResultModal: false,
      uploadResult: null,
      isSaving: false,
      isFinding: false,
      columns: [
        { label: 'dataId', width: 100 },
        { label: '原始编码', width: 100 },
        { label: '原始名称', width: 100 },
        { label: 'keyId', width: 100 },
        { label: '标准名称', width: 100 },
        { label: '省份', width: 50 },
        { label: '城市', width: 50 },
        { label: '区县', width: 50 },
        { label: '地址', width: 100 },
        { label: '申诉原因', width: 100 },
        { label: '操作', width: 100, fixed: true }
      ],
      resizing: false,
      resizeColumnIndex: null,
      startX: 0,
      startWidth: 0
    }
  },
  mounted() {
    this.fetchAppealData();
  },
  methods: {
    // 数据获取相关方法
    async fetchAppealData() {
      this.loading = true;
      try {
        const params = {
          page: this.pageNumber,
          size: this.pageSize
        };

        if (this.searchForm.dataCode) params.dataCode = this.searchForm.dataCode;
        if (this.searchForm.name) params.name = this.searchForm.name;
        if (this.searchForm.dataId) params.dataId = this.searchForm.dataId;
        if (this.searchForm.keyid) params.keyid = this.searchForm.keyid;
        if (this.searchForm.hsCode) params.hsCode = this.searchForm.hsCode;
        if (this.searchForm.province) params.province = this.searchForm.province;
        if (this.searchForm.city) params.city = this.searchForm.city;
        if (this.searchForm.area) params.area = this.searchForm.area;
        if (this.searchForm.address) params.address = this.searchForm.address;
        if (this.searchForm.originalName) params.originalName = this.searchForm.originalName;

        const response = await axios.get('/api/appealData/getAppealData', {params});
        this.AppealData = response.data.data;
      } catch (error) {
        console.error('获取申诉数据失败:', error);
        this.$message.error('获取申诉数据失败');
      } finally {
        this.loading = false;
      }
    },

    // 导出相关方法
    async toExcel() {
      if (this.exporting) return;
      this.exporting = true;

      try {
        const {data: jsonBlob} = await axios.get('/api/appealData/exportAppealData', {
          responseType: 'blob'
        });

        const jsonText = await jsonBlob.text();
        const {data: base64} = JSON.parse(jsonText);
        const byteChars = atob(base64);
        const byteNums = new Uint8Array(byteChars.length);

        for (let i = 0; i < byteChars.length; i++) {
          byteNums[i] = byteChars.charCodeAt(i);
        }

        const excelBlob = new Blob([byteNums], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        });

        const url = URL.createObjectURL(excelBlob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `豪森申诉数据_${new Date().toLocaleDateString()}.xlsx`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
      } catch (error) {
        console.error('导出失败:', error);
        this.$message.error('导出失败');
      } finally {
        this.exporting = false;
      }
    },

    // 搜索和分页相关方法
    handleSearch() {
      this.pageNumber = 0;
      this.fetchAppealData();
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
      this.fetchAppealData();
    },

    prevPage() {
      if (this.pageNumber > 0) {
        this.pageNumber--;
        this.fetchAppealData();
      }
    },

    nextPage() {
      if (this.pageNumber < this.AppealData.totalPages - 1) {
        this.pageNumber++;
        this.fetchAppealData();
      }
    },

    handlePageSizeChange() {
      this.pageNumber = 0;
      this.fetchAppealData();
    },

    // 详情相关方法
    showDetail(Appeal) {
      this.currentAppeal = Appeal;
      this.showDetailModal = true;
    },

    appealDetail(Appeal) {
      this.originalAppeal = JSON.parse(JSON.stringify(Appeal));
      this.currentAppealDetail = JSON.parse(JSON.stringify(Appeal));
      this.appealDetailModal = true;
    },

    async copyText(text, e) {
      try {
        await navigator.clipboard.writeText(text);
        const target = e.target;
        const old = target.textContent;
        target.textContent = '已复制';
        setTimeout(() => (target.textContent = old), 1200);
      } catch (err) {
        this.$message.error('复制失败，请手动复制');
      }
    },

    // 申诉处理相关方法
    async saveChanges() {
      if (this.isSaving) return;
      this.isSaving = true;

      try {
        const response = await axios.post('/api/appealData/handleAppealData', this.currentAppealDetail);

        if (response.data.code === 200) {
          this.uploadResult = {
            success: '提交申诉结果',
            details: `已成功处理${response.data.data.processedCount || 0}条申诉数据`
          };
        } else {
          this.uploadResult = {
            success: '提交申诉结果',
            message: '提交申诉失败，请检查输入数据'
          };
        }
        this.showResultModal = true;
      } catch (error) {
        this.uploadResult = {
          success: '提交申诉结果',
          message: '提交申诉失败，请检查输入数据'
        };
        this.showResultModal = true;
      } finally {
        this.isSaving = false;
      }
    },

    async findDaKuData() {
      if (this.isFinding) return;
      this.isFinding = true;

      try {
        if (!this.currentAppealDetail.keyid) {
          this.uploadResult = {
            success: '匹配结果',
            message: '匹配失败，请先输入keyid'
          };
          this.showResultModal = true;
          return;
        }

        const response = await axios.get('/api/updateData/findDaKuData', {
          params: {keyid: this.currentAppealDetail.keyid}
        });
        this.matchedAppeal = response.data.data;

        if (!this.matchedAppeal) {
          this.uploadResult = {
            success: '匹配结果',
            message: '匹配失败，请检查keyid是否正确'
          };
          this.showResultModal = true;
          return;
        }

        this.updateFieldsFromMatch();
      } catch (error) {
        this.uploadResult = {
          success: '匹配结果',
          message: '匹配失败，请检查网络连接'
        };
        this.showResultModal = true;
      } finally {
        this.isFinding = false;
      }
    },

    updateFieldsFromMatch() {
      // 医院字段
      this.currentAppealDetail.keyid = this.matchedAppeal.keyid;
      this.currentAppealDetail.name = this.matchedAppeal.name;
      this.currentAppealDetail.nameHistory = this.matchedAppeal.nameHistory;
      this.currentAppealDetail.province = this.matchedAppeal.province;
      this.currentAppealDetail.provinceid = this.matchedAppeal.provinceid;
      this.currentAppealDetail.city = this.matchedAppeal.city;
      this.currentAppealDetail.cityid = this.matchedAppeal.cityid;
      this.currentAppealDetail.area = this.matchedAppeal.area;
      this.currentAppealDetail.areaid = this.matchedAppeal.areaid;
      this.currentAppealDetail.kuAddress = this.matchedAppeal.kuAddress;
      this.currentAppealDetail.level = this.matchedAppeal.level;
      this.currentAppealDetail.grade = this.matchedAppeal.grade;
      this.currentAppealDetail.publicflag = this.matchedAppeal.publicflag;
      this.currentAppealDetail.classify = this.matchedAppeal.classify;
      this.currentAppealDetail.generalBranchKid = this.matchedAppeal.generalBranchKid;
      this.currentAppealDetail.generalBranchName = this.matchedAppeal.generalBranchName;
      this.currentAppealDetail.militaryHos = this.matchedAppeal.militaryHos;
      this.currentAppealDetail.regcode = this.matchedAppeal.regcode;
      this.currentAppealDetail.validity = this.matchedAppeal.validity;
      this.currentAppealDetail.subjects = this.matchedAppeal.subjects;
      this.currentAppealDetail.legalperson = this.matchedAppeal.legalperson;
      this.currentAppealDetail.usci = this.matchedAppeal.usci;

      // 药店字段
      this.currentAppealDetail.operation = this.matchedAppeal.operation;
      this.currentAppealDetail.scope = this.matchedAppeal.scope;
      this.currentAppealDetail.mainBranchKid = this.matchedAppeal.mainBranchKid;
      this.currentAppealDetail.mainBranchName = this.matchedAppeal.mainBranchName;
      this.currentAppealDetail.createDate = this.matchedAppeal.createDate;
      this.currentAppealDetail.registCapi = this.matchedAppeal.registCapi;
      this.currentAppealDetail.econKind = this.matchedAppeal.econKind;
      this.currentAppealDetail.signStatus = this.matchedAppeal.signStatus;
      this.currentAppealDetail.industry = this.matchedAppeal.industry;
      this.currentAppealDetail.belong = this.matchedAppeal.belong;
    },

    resetForm() {
      if (!this.originalAppeal) {
        this.uploadResult = {
          success: '重置结果',
          message: '没有可重置的原始数据'
        };
        this.showResultModal = true;
        return;
      }

      this.uploadResult = {
        success: '确认重置',
        message: '确定要重置所有修改吗？重置后将恢复为原始数据。',
        type: 'confirm',
        onConfirm: () => this.executeReset()
      };
      this.showResultModal = true;
    },

    executeReset() {
      this.currentAppealDetail = JSON.parse(JSON.stringify(this.originalAppeal));
      this.matchedAppeal = null;
      this.uploadResult = {
        success: '重置结果',
        message: '表单已重置为原始数据',
        type: 'success'
      };
      this.showResultModal = true;
    },

    // 弹窗关闭方法
    closeModal() {
      if (this.uploadResult && this.uploadResult.type === 'confirm' && this.uploadResult.onConfirm) {
        this.uploadResult.onConfirm();
      }
      this.showResultModal = false;
    },

    closeDetailModal() {
      this.showDetailModal = false;
      this.currentAppeal = {};
    },

    closeUpdateModal() {
      this.appealDetailModal = false;
      this.currentAppealDetail = {};
      this.originalAppeal = {};
      this.matchedAppeal = {};
    },

    // 表格列宽调整方法
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

        if (newWidth > 50 && newWidth < 500) {
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
/* 主容器 */
.Appeal-data-view {
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

/* 响应式设计 */
@media (min-width: 2000px) and (max-width: 2600px) {
  .Appeal-data-view {
    max-width: min(1860px, 90vw);
  }
}

@media (min-width: 2600px) {
  .Appeal-data-view {
    max-width: min(2400px, 95vw);
  }
}

/* 搜索区域 */
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

.input-wrapper {
  position: relative;
  display: inline-block;
}

.input-wrapper input {
  padding: 5px 20px 5px 8px;
  border: 1px solid #dcdfe6;
  border-radius: 3px;
  min-width: 120px;
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

.form-actions {
  display: flex;
  gap: 8px;
  margin-left: auto;
}

/* 按钮样式 */
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

.export-btn {
  background: #67c23a;
  color: white;
}

.export-btn:hover {
  background: #85ce61;
}

.export-btn:disabled {
  background: #b3e19d;
  cursor: not-allowed;
  opacity: 0.7;
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

/* 数据容器 */
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

/* 表格样式 */
.Appeal-table {
  width: 100%;
  height: 80%;
  border-collapse: collapse;
  font-size: 12px;
  table-layout: fixed;
}

.Appeal-table th,
.Appeal-table td {
  border: 1px solid #ebeef5;
  padding: 6px 8px;
  text-align: left;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.Appeal-table th {
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

.Appeal-table tr:nth-child(even) .fixed-column {
  background-color: #fafafa;
}

.Appeal-table tr:hover .fixed-column {
  background-color: #f5f7fa;
}

.Appeal-table tr:nth-child(even) {
  background-color: #fafafa;
}

.Appeal-table tr:hover {
  background-color: #f5f7fa;
}

/* 无数据提示 */
.no-data {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #909399;
  font-size: 12px;
}

/* 分页样式 */
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

/* 列宽调整 */
.th-content {
  position: relative;
  padding-right: 10px;
  text-align: center;
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

/* 复制按钮 */
.copy-btn {
  cursor: pointer;
  color: #409eff;
}

/* 模态框通用样式 */
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
  position: sticky;
  top: 0;
  z-index: 10;
  background: #fff;
  padding: 10px 12px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  flex: 1;
  text-align: center;
  margin: 0;
  font-size: 13px;
}

.close-btn {
  font-size: 16px;
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

.modal-footer {
  position: sticky;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  border-top: 1px solid #ebeef5;
  padding: 10px 12px;
  text-align: right;
  z-index: 10;
}

/* 详情模态框 */
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

/* 申诉表单样式 */
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

/* 表单按钮 */
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

.disabled-btn {
  background-color: #6c757d !important;
  border-color: #6c757d !important;
  opacity: 0.65;
  cursor: not-allowed;
}

/* 结果弹窗 */
.modal-content {
  background: white;
  border-radius: 3px;
  width: 400px;
  max-width: 90%;
  box-shadow: 0 1px 4px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.modal-content.success {
  border-top: 3px solid #67c23a;
}

.modal-content.error {
  border-top: 3px solid #f56c6c;
}

.modal-content.confirm {
  border-top: 3px solid #9478cc;
}

.modal-close {
  background: none;
  border: none;
  font-size: 17px;
  cursor: pointer;
  color: #909399;
}

.modal-close:hover {
  color: #606266;
}

.modal-body p {
  margin: 0 0 8px;
  font-size: 13px;
  text-align: center;
}

.result-details {
  color: #909399;
  font-size: 12px;
}

.modal-btn {
  padding: 5px 10px;
  background: #9478cc;
  color: white;
  border: none;
  border-radius: 3px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s;
}

.modal-btn:hover {
  background: #af96e6;
}

.confirm-btn {
  background: #9478cc;
  margin-right: 8px;
}

.confirm-btn:hover {
  background: #af96e6;
}

.cancel-btn {
  background: white;
  color: #606266;
  border: 1px solid #dcdfe6;
}

.cancel-btn:hover {
  background: #f5f7fa;
  color: #409eff;
  border-color: #c6e2ff;
}


</style>