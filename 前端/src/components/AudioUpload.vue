// 音频上传组件
<template>
  <div class="audio-upload">
    <el-upload
      ref="uploadRef"
      :action="uploadAction"
      :headers="uploadHeaders"
      :data="uploadData"
      :show-file-list="false"
      :before-upload="beforeUpload"
      :on-success="onSuccess"
      :on-error="onError"
      :on-progress="onProgress"
      :disabled="uploading"
      accept="audio/*"
      class="upload-wrapper"
    >
      <div class="upload-area" :class="{ 'uploading': uploading, 'has-audio': audioUrl }">
        <div v-if="!audioUrl && !uploading" class="upload-placeholder">
          <el-icon class="upload-icon"><UploadFilled /></el-icon>
          <div class="upload-text">{{ placeholder }}</div>
          <div class="upload-hint">支持 MP3、WAV、FLAC 等格式</div>
        </div>
        
        <div v-else-if="uploading" class="upload-progress">
          <el-progress
            type="circle"
            :percentage="uploadProgress"
            :width="80"
          ></el-progress>
          <div class="upload-progress-text">上传中... {{ uploadProgress }}%</div>
        </div>
        
        <div v-else class="audio-preview">
          <div class="audio-info">
            <el-icon class="audio-icon"><Headphone /></el-icon>
            <div class="audio-name">{{ audioName }}</div>
            <div class="audio-size">{{ formatFileSize(audioSize) }}</div>
          </div>
          
          <div class="audio-actions">
            <el-button 
              type="primary" 
              :icon="Edit" 
              size="small"
              @click.stop="handleReupload"
            >重新上传</el-button>
            <el-button 
              type="danger" 
              :icon="Delete" 
              size="small"
              @click.stop="handleDelete"
            >删除</el-button>
          </div>
        </div>
      </div>
    </el-upload>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  UploadFilled, 
  Headphone, 
  Edit, 
  Delete 
} from '@element-plus/icons-vue'

// Props
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '点击上传音频文件'
  },
  maxSize: {
    type: Number,
    default: 50 * 1024 * 1024 // 50MB
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'success', 'error', 'metadata'])

// Refs
const uploadRef = ref(null)

// Reactive data
const uploading = ref(false)
const uploadProgress = ref(0)
const audioUrl = ref(props.modelValue)
const audioName = ref('')
const audioSize = ref(0)

// Computed
const uploadAction = computed(() => '/api/files/upload')
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})
const uploadData = computed(() => ({
  fileType: 'audio'
}))

// Watch
watch(() => props.modelValue, (newVal) => {
  audioUrl.value = newVal
})

// Methods
const beforeUpload = (file) => {
  // 验证文件类型和大小
  if (!file.type.startsWith('audio/')) {
    ElMessage.error('请选择音频文件')
    return false
  }
  
  if (file.size > props.maxSize) {
    ElMessage.error('文件大小不能超过50MB')
    return false
  }
  
  // 保存文件信息
  audioName.value = file.name
  audioSize.value = file.size
  
  uploading.value = true
  uploadProgress.value = 0
  return true
}

const onProgress = (event) => {
  uploadProgress.value = Math.round(event.percent)
}

const onSuccess = (response) => {
  uploading.value = false
  uploadProgress.value = 0
  
  if (response && response.code === 200) {
    audioUrl.value = response.data
    emit('update:modelValue', response.data)
    emit('success', response.data)
    emit('metadata', {
      name: audioName.value,
      size: audioSize.value,
      url: response.data
    })
    ElMessage.success('音频上传成功')
  } else {
    onError(response)
  }
}

const onError = (error) => {
  uploading.value = false
  uploadProgress.value = 0
  
  const message = error?.message || '音频上传失败'
  ElMessage.error(message)
  emit('error', error)
}

const handleReupload = () => {
  uploadRef.value?.clearFiles()
  const input = uploadRef.value?.$el.querySelector('input[type="file"]')
  if (input) {
    input.click()
  }
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这个音频文件吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    audioUrl.value = ''
    audioName.value = ''
    audioSize.value = 0
    emit('update:modelValue', '')
    ElMessage.success('音频删除成功')
  } catch {
    // 用户取消删除
  }
}

const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i]
}
</script>

<style scoped>
.audio-upload {
  width: 100%;
}

.upload-wrapper {
  display: block;
}

.upload-area {
  width: 100%;
  min-height: 150px;
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.upload-area:hover {
  border-color: #409eff;
}

.upload-area.uploading {
  border-color: #409eff;
  background-color: #f5f7fa;
}

.upload-area.has-audio {
  border-style: solid;
  border-color: #409eff;
  background-color: #f0f9ff;
  align-items: flex-start;
  justify-content: flex-start;
  flex-direction: column;
}

.upload-placeholder {
  text-align: center;
  color: #999;
}

.upload-icon {
  font-size: 48px;
  margin-bottom: 12px;
  color: #c0c4cc;
}

.upload-text {
  font-size: 16px;
  margin-bottom: 8px;
}

.upload-hint {
  font-size: 12px;
  color: #999;
}

.upload-progress {
  text-align: center;
}

.upload-progress-text {
  margin-top: 15px;
  font-size: 14px;
  color: #666;
}

.audio-preview {
  width: 100%;
}

.audio-info {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.audio-icon {
  font-size: 24px;
  color: #409eff;
  margin-right: 12px;
}

.audio-name {
  flex: 1;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-right: 12px;
  word-break: break-all;
}

.audio-size {
  font-size: 12px;
  color: #909399;
}

.audio-actions {
  display: flex;
  gap: 10px;
}
</style>