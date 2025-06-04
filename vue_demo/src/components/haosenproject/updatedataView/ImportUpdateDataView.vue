<template>
  <div class="appeal-management-view">
    <!-- 文件上传区域 -->
    <div class="upload-container">
      <div class="upload-card">
        <h3 class="upload-title">上传更新数据</h3>
        <div
            class="upload-area"
            @dragover.prevent="dragOver"
            @dragleave="dragLeave"
            @drop.prevent="handleDrop"
            @click="handleAreaClick"
        >
          <input
              type="file"
              id="file-upload"
              ref="fileInput"
              @change="handleFileSelect"
              accept=".xlsx,.xls"
              style="display: none;"
          >
          <div class="upload-icon" :class="{ 'drag-active': isDragActive }">
            <font-awesome-icon :icon="['fas', 'file-upload']" />
          </div>
          <p class="upload-text">
            拖拽文件到此处或
            <button class="upload-btn" @click.stop="triggerFileInput">点击上传</button>
          </p>
          <p class="upload-hint">支持格式：.xlsx, .xls </p>
          <div v-if="selectedFile" class="file-info" @click.stop>
            <span class="file-name">{{ selectedFile.name }}</span>
            <span class="file-size">({{ formatFileSize(selectedFile.size) }})</span>
            <button class="file-remove" @click="removeFile">&times;</button>
          </div>
        </div>
        <div class="upload-actions">
          <button
              class="submit-btn"
              @click="submitFile"
              :disabled="!selectedFile || uploading"
              :class="{ 'disabled': !selectedFile || uploading }"
          >
            {{ uploading ? '上传中...' : '提交更新' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 上传结果弹窗 -->
    <div v-if="showResultModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content" :class="uploadResult.success ? 'success' : 'error'">
        <div class="modal-header">
          <h3>{{ uploadResult.success ? '推送成功' : '推送失败' }}</h3>
          <button class="modal-close" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <p>{{ uploadResult.message }}</p>
          <p v-if="uploadResult.details" class="result-details">{{ uploadResult.details }}</p>
        </div>
        <div class="modal-footer">
          <button class="modal-btn" @click="closeModal">确定</button>
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
      pendingCount: 1245,
      isDragActive: false,
      selectedFile: null,
      uploading: false,
      uploadResult: null,
      showResultModal: false
    }
  },
  methods: {
    handleAreaClick(e) {
      if (e.target.closest('.file-info') || e.target.classList.contains('upload-btn')) {
        return;
      }
      this.triggerFileInput();
    },

    triggerFileInput() {
      this.$refs.fileInput.click();
    },

    handleFileSelect(event) {
      const file = event.target.files[0];
      if (file) {
        this.validateAndSetFile(file);
      }
    },

    handleDrop(event) {
      this.isDragActive = false;
      const file = event.dataTransfer.files[0];
      if (file) {
        this.validateAndSetFile(file);
      }
    },

    dragOver() {
      this.isDragActive = true;
    },

    dragLeave() {
      this.isDragActive = false;
    },

    validateAndSetFile(file) {
      const validTypes = ['application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'text/csv'];
      const extension = file.name.split('.').pop().toLowerCase();

      if (validTypes.includes(file.type) || ['xls', 'xlsx', 'csv'].includes(extension)) {
        this.selectedFile = file;
      } else {
        this.uploadResult = {
          success: false,
          message: '文件格式不支持，请上传Excel或CSV文件'
        };
        this.showResultModal = true;
      }
    },

    removeFile() {
      this.selectedFile = null;
      this.$refs.fileInput.value = '';
    },

    formatFileSize(bytes) {
      if (bytes === 0) return '0 Bytes';
      const k = 1024;
      const sizes = ['Bytes', 'KB', 'MB', 'GB'];
      const i = Math.floor(Math.log(bytes) / Math.log(k));
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    },

    closeModal() {
      this.showResultModal = false;
    },

    async submitFile() {
      if (!this.selectedFile || this.uploading) return;

      this.uploading = true;

      try {
        // 创建FormData对象
        const formData = new FormData();
        formData.append('file', this.selectedFile);

        // 调用API接口
        const response = await axios.post('/api/updateData/importUpdateData', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          },
        });

        if (response.data.data.message === 'success') {
          this.uploadResult = {
            success: true,
            message: response.data.data.appealMessage || '更新数据推送失败',
            details: `已成功处理${response.data.data.processedCount || 0}条更新数据`
          };

          // 更新待处理计数
          this.pendingCount += response.data.data.processedCount || 0;

          // 清空已选文件
          this.selectedFile = null;
          this.$refs.fileInput.value = '';
        } else {
          throw new Error(response.data.data.message || '更新数据推送失败');
        }

      } catch (error) {
        console.error(error);
        this.uploadResult = {
          success: false,
          // message: '更新数据推送失败',
          details: error.response?.data?.message || error.message || '请检查文件格式并重试'
        };
      } finally {
        this.showResultModal = true;
        this.uploading = false;
      }
    }
  }

}
</script>

<style scoped>
.appeal-management-view {
  padding: 12px;
  background: white;
  font-size: 13px;
}

/* 上传区域样式 */
.upload-container {
  margin-bottom: 15px;
}

.upload-card {
  background: white;
  border-radius: 3px;
  padding: 12px;
  box-shadow: 0 1px 4px 0 rgba(0, 0, 0, 0.05);
  border-left: 3px solid #9478cc;
}

.upload-title {
  margin-top: 0;
  margin-bottom: 12px;
  color: #333;
  font-size: 17px;
  font-weight: bold;
}

.upload-area {
  border: 1px dashed #dcdfe6;
  border-radius: 3px;
  padding: 20px 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 12px;
  background: white;
}

.upload-area:hover {
  border-color: #c0c4cc;
}

.drag-active {
  border-color: #9478cc;
  background-color: rgba(148, 120, 204, 0.05);
}

.upload-icon {
  font-size: 29px;
  color: #9478cc;
  margin-bottom: 8px;
  transition: all 0.3s;
}

.upload-text {
  margin: 0 0 6px;
  color: #606266;
  font-size: 15px;
}

.upload-hint {
  margin: 0;
  color: #909399;
  font-size: 12px;
}

.upload-btn {
  background: none;
  border: none;
  color: #9478cc;
  cursor: pointer;
  padding: 0;
  margin-left: 4px;
  font-size: inherit;
  font-weight: 500;
}

.upload-btn:hover {
  text-decoration: underline;
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
  background: none;
  border: none;
  color: #f56c6c;
  cursor: pointer;
  font-size: 15px;
  line-height: 1;
  padding: 0 4px;
}

.file-remove:hover {
  color: #f78989;
}

.upload-actions {
  text-align: right;
}

.submit-btn {
  padding: 5px 10px;
  background: #9478cc;
  color: white;
  border: none;
  border-radius: 3px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s;
}

.submit-btn:hover {
  background: #af96e6;
}

.submit-btn.disabled {
  background: #c0c4cc;
  cursor: not-allowed;
  opacity: 0.7;
}

/* 弹窗样式 */
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

.modal-header {
  padding: 10px 12px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 15px;
  font-weight: bold;
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

.modal-body {
  padding: 12px;
}

.modal-body p {
  margin: 0 0 8px;
  font-size: 13px;
}

.result-details {
  color: #909399;
  font-size: 12px;
}

.modal-footer {
  padding: 8px 12px;
  border-top: 1px solid #ebeef5;
  text-align: right;
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

/* 响应式调整 */
@media (max-width: 768px) {
  .upload-area {
    padding: 15px 10px;
  }

  .upload-text, .upload-hint {
    font-size: 12px;
  }
}
</style>


