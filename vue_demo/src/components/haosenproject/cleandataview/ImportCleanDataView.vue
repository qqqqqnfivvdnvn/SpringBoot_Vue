<template>
  <div class="appeal-management-view">
    <!-- 文件上传区域 -->
    <div class="upload-container">
      <div class="upload-card">
        <div class="upload-title">上传申诉数据</div>

        <el-upload
            class="custom-upload"
            drag
            :limit="1"
            :file-list="fileList"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleRemove"
            accept=".xlsx"
            :http-request="() => {}"
        >
          <el-icon class="upload-icon">
            <UploadFilled />
          </el-icon>
          <div class="upload-text">
            拖拽文件到此处或
            <span class="upload-btn">点击上传</span>
          </div>
          <div class="upload-hint">支持格式：.xlsx</div>

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
            {{ uploading ? '上传中...' : '提交清洗' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 上传结果弹窗（紧凑版，使用 ElResult） -->
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
import { ref, nextTick } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { UploadFilled, CircleCloseFilled } from '@element-plus/icons-vue'

const pendingCount = ref(1245)
const fileList = ref([])
const selectedFile = ref(null)
const uploading = ref(false)
const uploadResult = ref(null)
const showResultModal = ref(false)
const resultKey = ref(0)  // 用于强制 el-result 重新渲染

// 文件选择/拖拽处理
const handleFileChange = (file, files) => {
  // 只保留最新一个文件
  if (files.length > 1) {
    files.splice(0, files.length - 1)
  }

  const validTypes = [
    'application/vnd.ms-excel',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  ]
  const ext = file.name.split('.').pop()?.toLowerCase()

  if (validTypes.includes(file.raw.type) || ['xlsx'].includes(ext)) {
    selectedFile.value = file.raw
    fileList.value = [file]
  } else {
    ElMessage.error('文件格式不支持，请上传Excel文件')
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

// 关闭弹窗（关键修复）
const closeModal = () => {
  showResultModal.value = false

  // 延迟清空 uploadResult，确保 dialog 完全关闭并销毁后再重置
  // 同时更新 key 强制下次渲染全新 el-result
  setTimeout(() => {
    uploadResult.value = null
    resultKey.value += 1
  }, 300)  // 300ms 足够覆盖 Element Plus 的关闭过渡动画
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

    const response = await axios.post('/api/cleanData/importCleanData', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })

    if (response.data.data.message === 'success') {
      uploadResult.value = {
        success: true,
        message: response.data.data.appealMessage || '清洗数据推送成功',
        details: `已成功处理${response.data.data.processedCount || 0}条清洗数据`,
      }
      pendingCount.value += response.data.data.processedCount || 0
      handleRemove()
    } else {
      throw new Error(response.data.data.message || '清洗数据推送失败')
    }
  } catch (error) {
    console.error(error)
    uploadResult.value = {
      success: false,
      message: '清洗数据推送失败',
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
  background: var(--bg-secondary, #ffffff);;
  font-size: 13px;
}

.upload-container {
  margin-bottom: 15px;
}

.upload-card {
  background: var(--bg-secondary, #ffffff);;
  border-radius: 3px;
  padding: 12px;
  box-shadow: 0 1px 4px 0 rgba(0, 0, 0, 0.05);
  border-left: 3px solid #9478cc;
}

.upload-title {
  margin: 0 0 12px;
  font-size: 17px;
  font-weight: bold;
  text-align: center;
}

.custom-upload :deep(.el-upload-dragger) {
  border: 1px dashed #dcdfe6;
  border-radius: 3px;
  padding: 20px 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background: var(--bg-secondary, #ffffff);;
  width: 100%;
  height: auto;
}

.custom-upload :deep(.el-upload-dragger:hover) {
  border-color: #c0c4cc;
}

.custom-upload :deep(.el-upload-dragger.is-dragover) {
  border-color: #9478cc;
  background-color: rgba(148, 120, 204, 0.05);
}

.upload-icon {
  font-size: 29px;
  color: #9478cc;
  margin-bottom: 8px;
}

.upload-text {
  margin: 0 0 6px;
  color: #606266;
  font-size: 15px;
}

.upload-btn {
  color: #9478cc;
  cursor: pointer;
  margin-left: 4px;
  font-weight: 500;
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

/* 微调 ElResult 内部间距 */
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
</style>