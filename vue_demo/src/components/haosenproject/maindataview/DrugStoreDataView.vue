<template>
  <div class="drugstore-data-view">
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
            <el-form-item label="批次编号" v-if="showMoreFilters">
              <el-input v-model="searchForm.batchCode" placeholder="请输入批次编号" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="状态" v-if="showMoreFilters">
              <el-select v-model="searchForm.status" placeholder="请选择状态" clearable @clear="handleSearch" style="width: 120px;">
                <el-option label="正常" value="1" />
                <el-option label="作废" value="2" />
                <el-option label="无法清洗" value="3" />
                <el-option label="禁用客户" value="4" />
                <el-option label="重复数据" value="5" />
              </el-select>
            </el-form-item>
            <el-form-item label="添加日期" v-if="showMoreFilters">
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
            <el-form-item label="更新日期" v-if="showMoreFilters">
              <el-date-picker
                v-model="updateDateRange"
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

          <!-- 操作按钮区域：查询、重置、导出、更多筛选、视图切换 -->
          <div class="form-actions-wrapper">
            <div class="form-actions">
              <el-button size="small" type="primary" @click="handleSearch" :loading="loading">查询</el-button>
              <el-button size="small" @click="resetSearch">重置</el-button>
              <el-button size="small" type="success" @click="toExcel" :loading="exporting">
                {{ exporting ? '导出中...' : '导出' }}
              </el-button>
              <el-button size="small" :type="showMoreFilters ? 'primary' : 'default'" @click="toggleMoreFilters">
                更多筛选
                <el-icon class="expand-icon" :class="{ 'is-expanded': showMoreFilters }"><ArrowUp /></el-icon>
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
                  <template v-else-if="index < 8 || index ===9">
                    <!-- 5-8列为普通文本：province, city, area, address -->
                    {{ row[columns[index].prop] }}
                  </template>
                  <template v-else-if="index === 8">
                    <!-- 第9列为状态标签 -->
                    <el-tag :type="getStatusType(row.status)" effect="dark" size="small">
                      {{ getStatusText(row.status) }}
                    </el-tag>
                  </template>
                  <template v-else>
                    <!-- 最后一列为操作按钮 - 使用下拉菜单 -->
<!--                    :disabled="!row.hsCode"-->
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
                              :command="{action: 'update', row: row}"

                              class="dropdown-item update-item">
                            <el-icon class="menu-icon"><EditPen /></el-icon>
                            <span class="menu-text">数据处理</span>
                          </el-dropdown-item>
                          <el-dropdown-item :command="{action: 'status', row: row}" class="dropdown-item status-item">
                            <el-icon class="menu-icon"><Tickets /></el-icon>
                            <span class="menu-text">状态管理</span>
                          </el-dropdown-item>
                          <!--divided-->
                          <el-dropdown-item
                              :command="{action: 'delete', row: row}"

                              class="dropdown-item delete-item">
                            <el-icon class="menu-icon"><Delete /></el-icon>
                            <span class="menu-text">删除记录</span>
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
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
                    <el-dropdown size="small" @command="handleCommand" class="action-dropdown">
                      <el-button type="primary" size="small" class="action-button">
                        <el-icon class="action-icon"><Operation /></el-icon>
                        操作
                        <el-icon class="arrow-icon"><ArrowDown /></el-icon>
                      </el-button>
                      <template #dropdown>
<!--                        :disabled="!drugstore.hsCode"-->
                        <el-dropdown-menu class="action-dropdown-menu">
                          <el-dropdown-item :command="{action: 'detail', row: drugstore}" class="dropdown-item detail-item">
                            <el-icon class="menu-icon"><Search /></el-icon>
                            <span class="menu-text">详情查看</span>
                          </el-dropdown-item>
                          <el-dropdown-item
                              :command="{action: 'update', row: drugstore}"

                              class="dropdown-item update-item">
                            <el-icon class="menu-icon"><EditPen /></el-icon>
                            <span class="menu-text">数据处理</span>
                          </el-dropdown-item>
                          <el-dropdown-item :command="{action: 'status', row: drugstore}" class="dropdown-item status-item">
                            <el-icon class="menu-icon"><Tickets /></el-icon>
                            <span class="menu-text">状态管理</span>
                          </el-dropdown-item>
                          <el-dropdown-item
                              :command="{action: 'delete', row: drugstore}"
                              divided
                              class="dropdown-item delete-item">
                            <el-icon class="menu-icon"><Delete /></el-icon>
                            <span class="menu-text">删除记录</span>
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
        <div class="fixed-pagination" v-if="drugstoreData.list?.length">
          <div class="pagination-content">
            <el-button size="small" plain :disabled="!drugstoreData.hasPreviousPage" @click="pageNumber > 1 && (pageNumber--, fetchDrugStoreData())">
              上一页
            </el-button>
            <div class="page-jumper">
              <span>跳转到</span>
              <el-input-number
                v-model="jumpPageNumber"
                :min="1"
                :max="drugstoreData.pages"
                size="small"
                controls-position="right"
                @change="handleJumpPage"
                class="page-input"
              />
              <span>页，共 {{ drugstoreData.pages }} 页 ({{ drugstoreData.total }} 条)</span>
            </div>
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


      <!-- 顶部固定操作栏 -->
      <div class="dialog-fixed-header">
        <div class="header-actions">

          <div class="header-main">
            <div class="custom-dialog-title-update">数据处理</div>
          </div>

          <el-button @click="resetForm">重置匹配</el-button>
          <el-button type="primary" :loading="isFinding" @click="findDaKuData">
            {{ isFinding ? '匹配中...' : '匹配大库' }}
          </el-button>
          <el-button type="primary" :loading="isSaving" :disabled="!currentUpdateDrugStore.hsCode"  @click="saveChanges">
            {{ isSaving ? '推送中...' : '推送更新' }}
          </el-button>

          <el-button type="primary" :loading="isPushing" @click="handleRepush">
            {{ isPushing ? '推送中...' : '重新推送' }}
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
              <el-form-item label="原始地址"><el-input v-model="currentUpdateDrugStore.originalAddress" readonly /></el-form-item>
              <el-form-item label="经销商"><el-input v-model="currentUpdateDrugStore.companyName" readonly /></el-form-item>
              <el-form-item label="豪森上传的编码"><el-input v-model="currentUpdateDrugStore.haosenCode" readonly /></el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="机构类型"  :rules="[{ required: true, message: '请选择机构类型', trigger: 'change' }]">
                <el-select v-model="currentUpdateDrugStore.orgType" @change="handleOrgTypeChange">
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
                <el-select v-model="currentUpdateDrugStore.level" :disabled="isFieldDisabled('level')">
                  <el-option label="未定级" value="未定级" />
                  <el-option label="一级" value="一级" />
                  <el-option label="二级" value="二级" />
                  <el-option label="三级" value="三级" />
                </el-select>
              </el-form-item>
              <el-form-item label="等次">
                <el-select v-model="currentUpdateDrugStore.grade" :disabled="isFieldDisabled('grade')">
                  <el-option label="未定等" value="未定等" />
                  <el-option label="甲等" value="甲等" />
                  <el-option label="乙等" value="乙等" />
                  <el-option label="丙等" value="丙等" />
                </el-select>
              </el-form-item>
              <el-form-item label="所有制">
                <el-select v-model="currentUpdateDrugStore.publicflag" :disabled="isFieldDisabled('publicflag')">
                  <el-option label="公立" value="公立" />
                  <el-option label="民营" value="民营" />
                </el-select>
              </el-form-item>
              <el-form-item label="类别"><el-input v-model="currentUpdateDrugStore.classify" /></el-form-item>
              <el-form-item label="总分院kid"><el-input v-model="currentUpdateDrugStore.generalBranchKid" :disabled="isFieldDisabled('generalBranchKid')" /></el-form-item>
              <el-form-item label="总分院名称"><el-input v-model="currentUpdateDrugStore.generalBranchName" :disabled="isFieldDisabled('generalBranchName')" /></el-form-item>
              <el-form-item label="是否军队医院">
                <el-select v-model="currentUpdateDrugStore.militaryHos" :disabled="isFieldDisabled('militaryHos')">
                  <el-option label="是" value="1" />
                  <el-option label="否" value="0" />
                </el-select>
              </el-form-item>
              <el-form-item label="登记号"><el-input v-model="currentUpdateDrugStore.regcode" :disabled="isFieldDisabled('regcode')" /></el-form-item>
              <el-form-item label="诊疗科室"><el-input v-model="currentUpdateDrugStore.subjects" :disabled="isFieldDisabled('subjects')" /></el-form-item>
              <el-form-item label="法人代表"><el-input v-model="currentUpdateDrugStore.legalperson" /></el-form-item>
              <el-form-item label="统一社会信用代码"><el-input v-model="currentUpdateDrugStore.usci" /></el-form-item>
              <el-form-item label="经营方式">
                <el-select v-model="currentUpdateDrugStore.operation" :disabled="isFieldDisabled('operation')">
                  <el-option label="零售" value="零售" />
                  <el-option label="批发" value="批发" />
                  <el-option label="连锁" value="连锁" />
                </el-select>
              </el-form-item>
              <el-form-item label="经营范围"><el-input v-model="currentUpdateDrugStore.scope" :disabled="isFieldDisabled('scope')" /></el-form-item>
              <el-form-item label="总分店kid"><el-input v-model="currentUpdateDrugStore.mainBranchKid" :disabled="isFieldDisabled('mainBranchKid')" /></el-form-item>
              <el-form-item label="总分店名称"><el-input v-model="currentUpdateDrugStore.mainBranchName" :disabled="isFieldDisabled('mainBranchName')" /></el-form-item>
              <el-form-item label="成立日期"><el-input v-model="currentUpdateDrugStore.createDate" :disabled="isFieldDisabled('createDate')" /></el-form-item>
              <el-form-item label="注册资金"><el-input v-model="currentUpdateDrugStore.registCapi" :disabled="isFieldDisabled('registCapi')" /></el-form-item>
              <el-form-item label="企业类型"><el-input v-model="currentUpdateDrugStore.econKind" :disabled="isFieldDisabled('econKind')" /></el-form-item>
              <el-form-item label="登记状态"><el-input v-model="currentUpdateDrugStore.signStatus" :disabled="isFieldDisabled('signStatus')" /></el-form-item>
              <el-form-item label="所属行业"><el-input v-model="currentUpdateDrugStore.industry" :disabled="isFieldDisabled('industry')" /></el-form-item>
              <el-form-item label="登记机关"><el-input v-model="currentUpdateDrugStore.belong" :disabled="isFieldDisabled('belong')" /></el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </el-dialog>

    <!-- 状态更新对话框 -->
    <el-dialog v-model="showStatusModal" title="更新状态" width="400px" destroy-on-close>
      <template #title>
        <div class="custom-dialog-title">更新状态</div>
      </template>

      <el-form :model="statusForm" label-width="80px">

        <el-form-item label="dataId">
          <el-input v-model="statusForm.dataId" readonly />
        </el-form-item>
        <el-form-item label="标准名称">
          <el-input v-model="statusForm.name" readonly />
        </el-form-item>

        <el-form-item label="选择状态">
          <el-select v-model="statusForm.status" placeholder="请选择状态" style="width: 100%;">
            <el-option label="正常" value="1" />
            <el-option label="作废" value="2" />
            <el-option label="无法清洗" value="3" />
            <el-option label="豪森禁用客户" value="4" />
            <el-option label="重复数据" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注信息">
          <el-input
            v-model="statusForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showStatusModal = false">取消</el-button>
        <el-button type="primary" :loading="isUpdatingStatus" @click="confirmUpdateStatus">确认</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
// ==================== 依赖导入 ====================
import '@/assets/css/dark-mode.css'
import { ref, reactive, onMounted, computed } from 'vue'
import { Grid, CopyDocument, Search, EditPen, Tickets, Delete, Operation, ArrowDown, ArrowUp } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// ==================== 视图状态管理 ====================
const viewMode = ref('table')

// ==================== 更多筛选状态 ====================
const showMoreFilters = ref(false)

// ==================== 日期范围选择器 ====================
const dateRange = ref([])
const updateDateRange = ref([])

// ==================== 数据状态管理 ====================
const drugstoreData = reactive({
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
const isSaving = ref(false)
const isFinding = ref(false)
const isPushing = ref(false)
const isUpdatingStatus = ref(false)

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
  batchCode: '',
  status: '',
  startTime: '',
  endTime: '',
  updateStartTime: '',
  updateEndTime: ''
})

// ==================== 表格列配置 ====================
const columns = ref([
  { label: 'dataId', prop: 'dataId', minWidth: 120 },
  { label: '原始编码', prop: 'dataCode', minWidth: 120 },
  { label: '原始名称', prop: 'originalName', minWidth: 150},
  { label: 'keyId', prop: 'keyid', minWidth: 120 },
  { label: '标准名称', prop: 'name', minWidth: 150},
  { label: '省份', prop: 'province', minWidth: 80},
  { label: '城市', prop: 'city', minWidth: 80 },
  { label: '区县', prop: 'area', minWidth: 80 },
  { label: '状态', prop: 'status', minWidth: 90 },
  { label: '地址', prop: 'address', minWidth: 200 },
  { label: '操作', fixed: 'right' , width: 120, resizable: false }
])

// ==================== 弹窗状态控制 ====================
const showDetailModal = ref(false)
const updateDetailModal = ref(false)
const showStatusModal = ref(false)

// ==================== 当前操作数据对象 ====================
const currentDrugStore = ref({})
const originalDrugStore = ref({})
const currentUpdateDrugStore = ref({})

// ==================== 状态更新表单 ====================
const statusForm = reactive({
  status: '',
  remark: '',
})

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
  const orgType = currentUpdateDrugStore.value.orgType
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
  batchCode: { label: '批次编码', value: d => d.batchCode, copy: true },
  dataId: { label: 'dataId', value: d => d.dataId, copy: true },
  dataCode: { label: '原始编码', value: d => d.dataCode, copy: true },
  originalName: { label: '原始名称', value: d => d.originalName, copy: true },
  originalProvince: { label: '原始省份', value: d => d.originalProvince },
  originalAddress: { label: '原始地址', value: d => d.originalAddress , copy: true },
  companyName: { label: '经销商', value: d => d.companyName },
  haosenCode: { label: '豪森上传的编码', value: d => d.haosenCode },
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
  field3: {
    label: '电子处方',
    value: h =>
        h.field3 === '1' ? '业务正常' :
            h.field3 === '2' ? '业务开通，但近7天未产生业务数据' :
                h.field3 === '3' ? '业务未开通' : ''
  },
  field4: { label: '冷链药品', value: c => c.field4 === '1' ? '是' : (c.field4 === '0' ? '否' : '') },
  remarks: { label: '备注', value: d => d.remarks },
  addtime: { label: '添加日期', value: d => formatDateTime(d.addtime) },
  updatetime: { label: '更新日期', value: d => formatDateTime(d.updatetime) },
  status: {
    label: '状态',
    value: d => getStatusText(d.status),
    type: d => getStatusType(d.status)
  }
}

// ==================== 状态映射工具函数 ====================
const getStatusText = (status) => {
  const map = { '1': '清洗成功', '2': '数据作废', '3': '无法清洗', '4': '禁用客户', '5': '数据重复' }
  return map[status] || '其他状态'
}

const getStatusType = (status) => {
  const map = { '1': 'success', '2': 'danger', '3': 'info', '4': 'danger', '5': 'warning' }
  return map[status] || 'info'
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
// 获取药店数据列表
const fetchDrugStoreData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNumber.value,
      pageSize: pageSize.value,
      ...Object.fromEntries(
          Object.entries(searchForm).filter(([, value]) => value !== '' && value !== null && value !== undefined)
      )
    }
    const { data } = await axios.get('/api/haosen/mainData/getDrugStoreData', { params })
    if (data.code === 200) {
      Object.assign(drugstoreData, data.data)
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
// 触发搜索
const handleSearch = () => {
  // 同步日期范围到 searchForm
  if (dateRange.value && dateRange.value.length === 2) {
    searchForm.startTime = dateRange.value[0]
    searchForm.endTime = dateRange.value[1]
  } else {
    searchForm.startTime = ''
    searchForm.endTime = ''
  }

  // 同步更新日期范围到 searchForm
  if (updateDateRange.value && updateDateRange.value.length === 2) {
    searchForm.updateStartTime = updateDateRange.value[0]
    searchForm.updateEndTime = updateDateRange.value[1]
  } else {
    searchForm.updateStartTime = ''
    searchForm.updateEndTime = ''
  }

  pageNumber.value = 1
  jumpPageNumber.value = 1
  fetchDrugStoreData()
}

// 跳转到指定页
const handleJumpPage = () => {
  if (jumpPageNumber.value >= 1 && jumpPageNumber.value <= drugstoreData.pages) {
    pageNumber.value = jumpPageNumber.value
    fetchDrugStoreData()
  }
}

// 重置搜索条件
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => (searchForm[key] = ''))
  dateRange.value = []
  updateDateRange.value = []
  pageNumber.value = 1
  fetchDrugStoreData()
}

// 切换更多筛选
const toggleMoreFilters = () => {
  showMoreFilters.value = !showMoreFilters.value
}

// 每页条数改变处理
const handlePageSizeChange = () => {
  pageNumber.value = 1
  fetchDrugStoreData()
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
// 导出Excel文件
const toExcel = async () => {
  if (exporting.value) return
  exporting.value = true
  try {
    // 过滤空值参数
    const params = Object.fromEntries(
        Object.entries(searchForm).filter(([, value]) => value !== '' && value !== null && value !== undefined)
    )
    const { data: jsonBlob } = await axios.get('/api/haosen/mainData/exportDrugStoreData', { params, responseType: 'blob' })
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

// ==================== 弹窗管理函数 ====================
// 显示详情弹窗
const showDetail = (row) => {
  currentDrugStore.value = { ...row }
  showDetailModal.value = true
}

// 打开数据处理弹窗
const updateDetail = (row) => {
  originalDrugStore.value = JSON.parse(JSON.stringify(row))
  currentUpdateDrugStore.value = JSON.parse(JSON.stringify(row))
  updateDetailModal.value = true
}

// 关闭数据处理弹窗
const closeUpdateModal = () => {
  updateDetailModal.value = false
  currentUpdateDrugStore.value = {}
  originalDrugStore.value = {}
}

// ==================== 数据更新处理函数 ====================
// 清除被禁用字段的值
const clearDisabledFields = () => {
  const disabledFieldNames = Object.keys(getDisabledFields.value).filter(field => getDisabledFields.value[field]);

  disabledFieldNames.forEach(field => {
    if (field in currentUpdateDrugStore.value) {
      currentUpdateDrugStore.value[field] = '';
    }
  });
};


// 机构类型改变处理
const handleOrgTypeChange = () => {
  // 机构类型改变时清除被禁用的字段的值
  clearDisabledFields();
};

// 检查字段是否应该被禁用
const isFieldDisabled = (field) => {
  return getDisabledFields.value[field];
};

// 保存更新的数据
const saveChanges = async () => {
  if (isSaving.value) return
  isSaving.value = true
  try {
    const { data } = await axios.post('/api/haosen/updateData/OneUpdateHaoSenData', currentUpdateDrugStore.value)
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

// 匹配大库数据
const findDaKuData = async () => {
  if (isFinding.value) return
  if (!currentUpdateDrugStore.value.keyid) {
    ElMessage.warning('请先输入keyid')
    return
  }
  isFinding.value = true
  try {
    const { data } = await axios.get('/api/haosen/updateData/findDaKuData', { params: { keyid: currentUpdateDrugStore.value.keyid } })
    if (data.code === 200 && data.data) {
      // 先保存原始不可编辑字段
      const protectedFields = {
        batchCode: currentUpdateDrugStore.value.batchCode,
        dataId: currentUpdateDrugStore.value.dataId,
        dataType: currentUpdateDrugStore.value.dataType,
        dataCode: currentUpdateDrugStore.value.dataCode,
        originalName: currentUpdateDrugStore.value.originalName,
        originalProvince: currentUpdateDrugStore.value.originalProvince,
        originalAddress: currentUpdateDrugStore.value.originalAddress,
        companyName: currentUpdateDrugStore.value.companyName,
        appealRemark: currentUpdateDrugStore.value.appealRemark,
        solveRemark: currentUpdateDrugStore.value.solveRemark,
        orgType: currentUpdateDrugStore.value.orgType,
        haosenCode: currentUpdateDrugStore.value.haosenCode,
        hsCode: currentUpdateDrugStore.value.hsCode
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

// 重置表单到原始数据
const resetForm = () => {
  ElMessageBox.confirm('确定要重置所有修改吗？重置后将恢复为原始数据。', '确认重置', { type: 'warning' })
      .then(() => {
        currentUpdateDrugStore.value = JSON.parse(JSON.stringify(originalDrugStore.value))
        ElMessage.success('已重置为原始数据')
      })
}

// ==================== 重新推送操作 ====================
// 重新推送数据到申诉接口
const handleRepush = async () => {
  if (isPushing.value) return
  
  // 验证必要字段
  if (!currentUpdateDrugStore.value.dataId) {
    ElMessage.warning('缺少必要的数据ID')
    return
  }

  isPushing.value = true
  try {
    const { data } = await axios.post('/api/haosen/appealData/handleAppealData', currentUpdateDrugStore.value)
    
    if (data.code === 200) {
      // const appealMessage = data.data?.appealMessage || '推送成功'
      ElMessage.success('重新推送成功')
      
      // 如果推送成功，关闭弹窗并刷新列表
      closeUpdateModal()
      fetchDrugStoreData()
    } else {
      // const errorMsg = data.data?.appealMessage || data.msg || '推送失败'
      ElMessage.error('重新推送失败')
    }
  } catch (error) {
    console.error('重新推送失败:', error)
    ElMessage.error('网络请求异常，推送失败')
  } finally {
    isPushing.value = false
  }
}

// ==================== 状态管理相关函数 ====================
// 显示状态更新对话框
const showStatusDialog = (row) => {
  currentDrugStore.value = { ...row }
  // 初始化表单数据
  statusForm.status = row.status || ''
  statusForm.remark = ''
  statusForm.dataId = row.dataId || ''
  statusForm.name = row.name || ''
  showStatusModal.value = true
}

// ==================== 下拉菜单命令处理 ====================
// 处理下拉菜单命令
const handleCommand = (command) => {
  const { action, row } = command;
  switch(action) {
    case 'detail':
      showDetail(row);
      break;
    case 'update':
      updateDetail(row);
      break;
    case 'status':
      showStatusDialog(row);
      break;
    case 'delete':
      showDeleteDialog(row);
      break;
  }
}

// ==================== 删除操作相关函数 ====================
// 显示删除确认对话框
const showDeleteDialog = (row) => {
  ElMessageBox.confirm(
    `
    <strong>药店名称：</strong>${row.name || '未知'}<br/>
    <strong>dataId：</strong>${row.dataId}<br/>
    <strong>原始编码：</strong>${row.dataCode || '无'}<br/>
    <span style="color: #f56c6c;">此操作不可恢复！</span>`,
    '删除确认',
    {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning',
      dangerouslyUseHTMLString: true,
      confirmButtonClass: 'el-button--danger',
      center: true
    }
  ).then(() => {
    confirmDelete(row)
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 执行删除操作
const confirmDelete = async (row) => {
  try {
    // 调用删除API（demo接口）
    const { data } = await axios.post('/api/haosen/updateData/deleteInstitutionData', {
      dataId: row.dataId,
      institutionType: 'drugStore'
    })
    
    if (data.code === 200) {
      ElMessage.success('删除成功')
      // 重新获取数据以刷新列表
      fetchDrugStoreData()
    } else {
      ElMessage.error(data.msg || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('网络请求异常，删除失败')
  }
}

// 确认更新状态
const confirmUpdateStatus = async () => {
  if (!statusForm.status) {
    ElMessage.warning('请选择状态')
    return
  }

  isUpdatingStatus.value = true
  try {
    // 调用API更新状态
    const payload = {
      dataId: currentDrugStore.value.dataId,
      status: statusForm.status,
      remark: statusForm.remark || null,
      institutionType: 'drugStore'
    }

    const { data } = await axios.post('/api/haosen/updateData/updateInstitutionStatus', payload)
    console.log('状态更新响应:', data)
    if (data.code === 200) {
      ElMessage.success('状态更新成功')
      showStatusModal.value = false
      // 重新获取数据以刷新列表
      fetchDrugStoreData()
    } else {
      ElMessage.error(data.msg || '状态更新失败')
    }
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('网络请求异常')
  } finally {
    isUpdatingStatus.value = false
  }
}

// ==================== 组件生命周期钩子 ====================
// 组件挂载时自动加载数据
onMounted(() => {
  fetchDrugStoreData()
})


</script>


<style scoped>
/* ==================== 页面整体布局 ==================== */
.drugstore-data-view {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: min(1600px, 95vw);
  margin: 0 auto;
  background: #ffffff;
  overflow: hidden;
  font-size: 14px;
}

/* 暗色模式支持 */
html.dark .drugstore-data-view,
.dark .drugstore-data-view {
  background: var(--el-bg-color, #1a1a2c);
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
  background: var(--el-bg-color, #ffffff);
  border-bottom: 1px solid var(--el-border-color-light, #ebeef5);
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
  background: var(--el-bg-color, #ffffff);
  flex-wrap: wrap;
  gap: 12px 8px;
  align-items: flex-end;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0 !important;
  flex: 1 1 200px;
  min-width: 200px;
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

/* 更多筛选按钮图标旋转 */
.expand-icon {
  transition: transform 0.3s ease;
}

.expand-icon.is-expanded {
  transform: rotate(180deg);
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
  display: flex;
  flex-direction: column;
}

.table-view {
  flex: 1;
  height: 100%;
  overflow: hidden;
}

:deep(.el-table) {
  height: 100%;
  width: 100%;
  font-size: 14px;
}

:deep(.el-table th.el-table__cell) {
  font-weight: 600;
}

:deep(.el-table__body-wrapper) {
  height: 100%;
  overflow: auto;
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
  background: var(--el-bg-color, #ffffff);
}

.drugstore-card :deep(.el-card__header) {
  background: var(--el-bg-color, #ffffff);
  border-bottom: 1px solid var(--el-border-color-light, #ebeef5);
}

.drugstore-card :deep(.el-card__body) {
  background: var(--el-bg-color, #ffffff);
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
  border-bottom: 1px solid var(--el-border-color-light, #ebeef5);
}

.card-title {
  font-size: 14px;
  font-weight: 600;
  max-width: 70%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--el-text-color-primary, #303133);
}

.card-body {
  flex: 1;
  margin: 12px 0;
}

.card-item {
  margin-bottom: 8px;
  line-height: 1.5;
  color: var(--el-text-color-regular, #606266);
}

.card-item .label {
  color: var(--el-text-color-secondary, #606266);
  font-weight: 600;
  margin-right: 6px;
  min-width: 60px;
  display: inline-block;
}

.card-footer {
  margin-top: auto;
  text-align: right;
  padding-top: 12px;
  border-top: 1px solid var(--el-border-color-lighter, #f0f0f0);
}

/* 分页区域 */
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

.page-info {
  font-size: 14px;
  color: var(--el-text-color-regular, #606266);
  min-width: 220px;
  text-align: center;

}

.no-data-container {
  height: 100%;
  display: flex;
  align-items: center;
  background: var(--el-bg-color, #ffffff);
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
  color: var(--el-text-color-primary, #303133);
  line-height: 1.5;
}

.detail-row span {
  flex: 1;
  word-break: break-word;
  line-height: 1.5;
  color: var(--el-text-color-regular, #606266);
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

.custom-dialog-title-update {
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
  background: var(--el-border-color, #c0c4cc);
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
.status-item .menu-icon {
  color: var(--el-color-warning);
}
.delete-item .menu-icon {
  color: var(--el-color-danger);
}

/* ==================== 暗色主题适配 ==================== */
html.dark .drugstore-data-view,
.dark .drugstore-data-view {
  background: var(--el-bg-color, #1a1a2c);
}

html.dark .integrated-container,
.dark .integrated-container {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-color: var(--el-border-color, #3a3a4a) !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
}

html.dark .fixed-search,
.dark .fixed-search {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-bottom-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark .search-form :deep(.el-form-item__label) {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

html.dark .search-form :deep(.el-input__wrapper) {
  background-color: var(--el-input-bg-color, #2a2a3a) !important;
  box-shadow: 0 0 0 1px var(--el-input-border-color, #3a3a4a) inset !important;
}

html.dark .search-form :deep(.el-picker__wrapper),
html.dark .search-form :deep(.el-date-editor .el-input__wrapper) {
  background-color: var(--el-input-bg-color, #2a2a3a) !important;
  box-shadow: 0 0 0 1px var(--el-input-border-color, #3a3a4a) inset !important;
}

html.dark :deep(.el-table) {
  background-color: var(--el-bg-color, #1a1a2c) !important;
}

html.dark :deep(.el-table__header tr),
html.dark :deep(.el-table__header tr th.el-table__cell),
html.dark :deep(.el-table thead tr th) {
  background-color: var(--el-fill-color-light, #2a2a3a) !important;
  color: var(--el-text-color-regular, #e0e0e0) !important;
  border-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark :deep(.el-table__body tr.el-table__row > td),
html.dark :deep(.el-table tbody tr td.el-table__cell) {
  background-color: var(--el-bg-color, #1a1a2c) !important;
  color: var(--el-text-color-regular, #d0d0d0) !important;
  border-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark :deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: var(--el-fill-color-lighter, #232330) !important;
}

html.dark :deep(.el-table__body tr:hover > td) {
  background-color: var(--el-fill-color-light, #2a2a3a) !important;
}

html.dark .fixed-pagination,
.dark .fixed-pagination {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-top-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark .page-info,
.dark .page-info {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

html.dark .drugstore-card,
.dark .drugstore-card {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark .drugstore-card :deep(.el-card__header),
.dark .drugstore-card :deep(.el-card__header) {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-bottom-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark .drugstore-card :deep(.el-card__body),
.dark .drugstore-card :deep(.el-card__body) {
  background: var(--el-bg-color, #1a1a2c) !important;
}

html.dark .card-header,
.dark .card-header {
  border-bottom-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark .card-title,
.dark .card-title {
  color: var(--el-text-color-regular, #e0e0e0) !important;
}

html.dark .card-item,
.dark .card-item {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

html.dark .card-item .label,
.dark .card-item .label {
  color: var(--el-text-color-secondary, #a0a0a0) !important;
}

html.dark .card-footer,
.dark .card-footer {
  border-top-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark .detail-row label,
.dark .detail-row label {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

html.dark .detail-row span,
.dark .detail-row span {
  color: var(--el-text-color-regular, #d0d0d0) !important;
}

html.dark .update-dialog :deep(.el-dialog),
.dark .update-dialog :deep(.el-dialog) {
  background: var(--el-bg-color, #1a1a2c) !important;
}

html.dark .update-dialog :deep(.el-dialog__header),
.dark .update-dialog :deep(.el-dialog__header) {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-bottom-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark .dialog-fixed-header,
.dark .dialog-fixed-header {
  background: var(--el-bg-color, #1a1a2c) !important;
  border-bottom-color: var(--el-border-color, #3a3a4a) !important;
}

html.dark .no-data-container,
.dark .no-data-container {
  background: var(--el-bg-color, #1a1a2c) !important;
}

html.dark .no-data-container :deep(.el-empty__description),
.dark .no-data-container :deep(.el-empty__description) {
  color: var(--el-text-color-secondary, #a0a0a0) !important;
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