<template>
  <div class="appeal-management-view" :style="themeStyle">
    <!-- 文件上传区域 -->
    <div class="upload-container">
      <div class="upload-card">
        <div class="upload-title">上传豪森确认数据</div>

        <el-upload
            class="custom-upload"
            drag
            :limit="1"
            :file-list="fileList"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleRemove"
            accept=".xlsx,.xls"
            :http-request="() => {}"
        >
          <el-icon class="upload-icon">
            <UploadFilled />
          </el-icon>
          <div class="upload-text">
            拖拽文件到此处或
            <span class="upload-btn">点击上传</span>
          </div>
          <div class="upload-hint">支持格式：.xlsx 或 .xls</div>

          <!-- 自定义文件显示 -->
          <template #file="{ file }">
            <div class="file-info">
              <span class="file-name">{{ file.name }}</span>
              <span class="file-size">({{ formatFileSize(file.size) }})</span>
              <el-icon class="file-remove" @click.stop="handleRemove">
                <CircleCloseFilled />
              </el-icon>
            </div>
          </template>
        </el-upload>

        <div class="upload-actions">
          <el-button
              type="primary"
              :loading="uploading"
              :disabled="!selectedFile || uploading"
              @click="submitFile"
          >
            {{ uploading ? '上传中...' : '提交上传' }}
          </el-button>
        </div>

        <div class="template-section">
          <h4>文件格式要求：</h4>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="必需字段">
              机构类型、dataId、原始名称、原始编码、keyid、省份、标准名称、历史名称、地址、豪森编码、添加时间、是否需易联禁用这条对应关系、登记状态、备注
            </el-descriptions-item>
            <el-descriptions-item label="文件格式">
              .xlsx 或 .xls
            </el-descriptions-item>
            <el-descriptions-item label="处理说明">
              文件上传后将自动处理并禁用相关数据
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </div>

    <!-- 上传结果弹窗 -->
    <el-dialog
        v-model="showResultModal"
        width="360px"
        custom-class="custom-result-dialog"
        :close-on-click-modal="false"
        :show-close="true"
        :show-header="false"
        center
        :destroy-on-close="true"
    >
      <el-result
          :key="resultKey"
          :icon="uploadResult?.success ? 'success' : 'error'"
          :title="uploadResult?.message"
          :sub-title="uploadResult?.details"
      >
        <template #extra>
          <el-button type="primary" @click="closeModal">
            确定
          </el-button>
        </template>
      </el-result>
    </el-dialog>
  </div>
</template>

<script setup>
import '@/assets/css/dark-mode.css'
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { UploadFilled, CircleCloseFilled } from '@element-plus/icons-vue'

// 主题颜色配置
const themeColors = ref({
  primary: '#9478cc',
  dark: '#7d60b8',
  light: '#b89ff0'
})

// 动态主题样式
const themeStyle = computed(() => {
  const { primary, dark, light } = themeColors.value
  return {
    '--theme-primary': primary,
    '--theme-dark': dark,
    '--theme-light': light,
  }
})

// 调整颜色亮度
function adjustColor(hex, percent) {
  const num = parseInt(hex, 16)
  const r = Math.max(0, Math.min(255, (num >> 16) + percent))
  const g = Math.max(0, Math.min(255, ((num >> 8) & 0x00FF) + percent))
  const b = Math.max(0, Math.min(255, (num & 0x0000FF) + percent))
  return `#${(1 << 24 | r << 16 | g << 8 | b).toString(16).slice(1)}`
}

// 加载主题颜色
function loadThemeColor() {
  const savedColor = sessionStorage.getItem('themeColor_HaoSenHome')
  if (savedColor) {
    const hex = savedColor.replace('#', '')
    themeColors.value = {
      primary: `#${hex}`,
      dark: adjustColor(hex, -20),
      light: adjustColor(hex, 20)
    }
  } else {
    // 默认颜色：紫色
    themeColors.value = {
      primary: '#9478cc',
      dark: '#7d60b8',
      light: '#b89ff0'
    }
  }
}

onMounted(() => {
  loadThemeColor()
})

const fileList = ref([])
const selectedFile = ref(null)
const uploading = ref(false)
const uploadResult = ref(null)
const showResultModal = ref(false)
const resultKey = ref(0)

// 文件选择/拖拽处理
const handleFileChange = (file, files) => {
  if (files.length > 1) {
    files.splice(0, files.length - 1)
  }

  const validTypes = [
    'application/vnd.ms-excel',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  ]
  const ext = file.name.split('.').pop()?.toLowerCase()

  if (validTypes.includes(file.raw.type) || ['xlsx', 'xls'].includes(ext)) {
    selectedFile.value = file.raw
    fileList.value = [file]
  } else {
    ElMessage.error('文件格式不支持，请上传 Excel 文件')
    fileList.value = []
    selectedFile.value = null
  }
}

// 删除文件
const handleRemove = () => {
  selectedFile.value = null
  fileList.value = []
}

// 文件大小格式化
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 关闭弹窗
const closeModal = () => {
  showResultModal.value = false

  setTimeout(() => {
    uploadResult.value = null
    resultKey.value += 1
  }, 300)
}

// 提交上传
const submitFile = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请选择要上传的文件')
    return
  }

  if (uploading.value) return

  uploading.value = true

  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)

    const {data} = await axios.post('/api/haosen/duplicatedata/uploadduplicatedata', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })

    if (data.code === 200) {
      uploadResult.value = {
        success: true,
        message: '数据上传成功',
        details: `已成功导入 ${data.data.result || 0} 条数据`,
      }
      handleRemove()
    } else {
      throw new Error(data.msg || '数据上传失败')
    }
  } catch (error) {
    console.error(error)
    uploadResult.value = {
      success: false,
      message: '数据上传失败',
      details: error.response?.data?.message || error.message || '请检查文件格式并重试',
    }
  } finally {
    showResultModal.value = true
    uploading.value = false
  }
}
</script>

<style scoped>
.appeal-management-view {
  padding: 12px;
  background: var(--bg-secondary, #ffffff);
  font-size: 13px;
  max-width: min(1200px, 95vw);
  margin: 0 auto;
}

/* ==================== 响应式布局适配 ==================== */
/* 2K 屏幕优化 */
@media (min-width: 2000px) and (max-width: 2600px) {
  .appeal-management-view {
    max-width: min(1800px, 90vw);
  }
}

/* 超宽屏幕 */
@media (min-width: 2600px) {
  .appeal-management-view {
    max-width: min(2200px, 95vw);
  }
}

.upload-container {
  margin-bottom: 15px;
}

.upload-card {
  background: var(--bg-secondary, #ffffff);
  border-radius: 3px;
  padding: 12px;
  box-shadow: 0 1px 4px 0 rgba(0, 0, 0, 0.05);
  border-left: 3px solid var(--theme-primary);
}

html.dark .upload-card {
  background: var(--bg-secondary, #1a1a2c);
  border-left-color: #a488dc;
}

.upload-title {
  margin: 0 0 12px;
  font-size: 17px;
  font-weight: bold;
  text-align: center;
}

.custom-upload :deep(.el-upload-dragger) {
  border: 1px dashed var(--theme-primary);
  border-radius: 3px;
  padding: 20px 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background: var(--bg-secondary, #ffffff);
  width: 100%;
  height: auto;
}

html.dark .custom-upload :deep(.el-upload-dragger) {
  background: #121212 !important;
  border-color: #333333 !important;
}

html.dark .custom-upload :deep(.el-upload-dragger:hover),
html.dark .custom-upload :deep(.el-upload:focus .el-upload-dragger) {
  border-color: var(--theme-dark);
}

.custom-upload :deep(.el-upload-dragger:hover),
.custom-upload :deep(.el-upload:focus .el-upload-dragger) {
  border-color: var(--theme-dark);
}

.custom-upload :deep(.el-upload-dragger.is-dragover) {
  border-color: var(--theme-dark);
  background-color: rgba(148, 120, 204, 0.05);
}

html.dark .custom-upload :deep(.el-upload-dragger.is-dragover) {
  border-color: var(--theme-dark);
  background-color: rgba(148, 120, 204, 0.1);
}

.upload-icon {
  font-size: 29px;
  color: var(--theme-primary);
  margin-bottom: 8px;
}

html.dark .upload-icon {
  color: #9ca3af;
}

.upload-text {
  margin: 0 0 6px;
  color: #606266;
  font-size: 15px;
}

html.dark .upload-text {
  color: #9ca3af;
}

.upload-btn {
  color: var(--theme-primary);
  cursor: pointer;
  margin-left: 4px;
  font-weight: 500;
}

html.dark .upload-btn {
  color: #9ca3af;
}

.upload-btn:hover {
  text-decoration: underline;
}

.upload-hint {
  margin: 0;
  color: #909399;
  font-size: 12px;
}

.custom-upload :deep(.el-upload-list) {
  margin-top: 12px;
  margin-bottom: 0;
}

.custom-upload :deep(.el-upload-list__item) {
  transition: none;
  margin: 0;
  padding: 0;
  border: none;
  background: transparent;
}

.file-info {
  margin-top: 12px;
  padding: 6px;
  background: #f5f7fa;
  border-radius: 3px;
  display: inline-flex;
  align-items: center;
}

html.dark .file-info {
  background: #2a2a3a;
}

.file-name {
  font-weight: bold;
  margin-right: 4px;
  font-size: 13px;
}

.file-size {
  color: #909399;
  margin-right: 8px;
  font-size: 13px;
}

.file-remove {
  color: #f56c6c;
  cursor: pointer;
  font-size: 15px;
}

.file-remove:hover {
  color: #f78989;
}

.upload-actions {
  text-align: right;
  margin-top: 12px;
}

.template-section {
  margin-top: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 3px;
}

html.dark .template-section {
  background: #121212;
}

.template-section h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 14px;
}

html.dark .template-section h4 {
  color: #e0e0e0;
}

/* 结果弹窗紧凑样式 */
:deep(.custom-result-dialog) {
  border-radius: 8px;
  overflow: hidden;
  max-width: 90vw;
}

:deep(.custom-result-dialog .el-dialog__body) {
  padding: 20px 24px 30px;
  text-align: center;
}

:deep(.el-result__icon) {
  margin-bottom: 12px;
}

:deep(.el-result__title) {
  margin-bottom: 8px !important;
  font-size: 17px;
  font-weight: 600;
}

:deep(.el-result__subtitle) {
  font-size: 13px;
  color: #909399;
  margin-bottom: 20px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .custom-upload :deep(.el-upload-dragger) {
    padding: 15px 10px;
  }

  .upload-text,
  .upload-hint {
    font-size: 12px;
  }

  :deep(.custom-result-dialog) {
    width: 90% !important;
  }

  :deep(.custom-result-dialog .el-dialog__body) {
    padding: 16px 20px 24px;
  }
}

/* 暗色模式下表格样式覆盖 */
html.dark .template-section :deep(.el-descriptions) {
  --el-descriptions-header-bg-color: #121212 !important;
  --el-descriptions-content-bg-color: #121212 !important;
  --el-descriptions-header-label-bg-color: #1a1a1a !important;
  --el-descriptions-item-bg-color: #121212 !important;
}

html.dark .template-section :deep(.el-descriptions__label) {
  background-color: #1a1a1a !important;
  color: #e0e0e0 !important;
}

html.dark .template-section :deep(.el-descriptions__content) {
  background-color: #121212 !important;
  color: #d0d0d0 !important;
}

html.dark .template-section :deep(.el-descriptions__cell) {
  border-color: #333333 !important;
}
</style>
