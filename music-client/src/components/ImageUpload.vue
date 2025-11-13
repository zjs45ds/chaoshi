// 图片上传组件
<template>
  <div class="image-upload">
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
      accept="image/*"
      class="upload-wrapper"
    >
      <div class="upload-area" :class="{ 'uploading': uploading }">
        <div v-if="!imageUrl && !uploading" class="upload-placeholder">
          <el-icon class="upload-icon"><Plus /></el-icon>
          <div class="upload-text">{{ placeholder }}</div>
        </div>
        
        <div v-else-if="uploading" class="upload-progress">
          <el-progress
            type="circle"
            :percentage="uploadProgress"
            :width="60"
          ></el-progress>
          <div class="upload-progress-text">上传中...</div>
        </div>
        
        <div v-else class="upload-preview">
          <img :src="imageUrl" :alt="alt" class="preview-image" />
          <div class="upload-overlay">
            <el-button 
              type="primary" 
              :icon="Edit" 
              circle 
              size="small"
              @click.stop="handleReupload"
            ></el-button>
            <el-button 
              type="danger" 
              :icon="Delete" 
              circle 
              size="small"
              @click.stop="handleDelete"
            ></el-button>
          </div>
        </div>
      </div>
    </el-upload>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

// Props
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '点击上传图片'
  },
  alt: {
    type: String,
    default: '图片'
  },
  maxSize: {
    type: Number,
    default: 10 * 1024 * 1024 // 10MB
  },
  width: {
    type: [Number, String],
    default: 150
  },
  height: {
    type: [Number, String],
    default: 150
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'success', 'error'])

// Refs
const uploadRef = ref(null)

// Reactive data
const uploading = ref(false)
const uploadProgress = ref(0)
const imageUrl = ref(props.modelValue)

// Computed
const uploadAction = computed(() => '/api/files/upload')
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})
const uploadData = computed(() => ({
  fileType: 'image'
}))

// Watch
watch(() => props.modelValue, (newVal) => {
  imageUrl.value = newVal
})

// Methods
const beforeUpload = (file) => {
  // 验证文件类型和大小
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return false
  }
  
  if (file.size > props.maxSize) {
    ElMessage.error('文件大小不能超过10MB')
    return false
  }
  
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
    imageUrl.value = response.data
    emit('update:modelValue', response.data)
    emit('success', response.data)
    ElMessage.success('图片上传成功')
  } else {
    onError(response)
  }
}

const onError = (error) => {
  uploading.value = false
  uploadProgress.value = 0
  
  const message = error?.message || '图片上传失败'
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
    await ElMessageBox.confirm('确定要删除这张图片吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    imageUrl.value = ''
    emit('update:modelValue', '')
    ElMessage.success('图片删除成功')
  } catch {
    // 用户取消删除
  }
}
</script>

<style scoped>
.image-upload {
  display: inline-block;
}

.upload-wrapper {
  display: block;
}

.upload-area {
  width: v-bind(width + 'px');
  height: v-bind(height + 'px');
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-area:hover {
  border-color: #409eff;
}

.upload-area.uploading {
  border-color: #409eff;
  background-color: #f5f7fa;
}

.upload-placeholder {
  text-align: center;
  color: #999;
}

.upload-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
}

.upload-progress {
  text-align: center;
}

.upload-progress-text {
  margin-top: 10px;
  font-size: 12px;
  color: #666;
}

.upload-preview {
  width: 100%;
  height: 100%;
  position: relative;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  opacity: 0;
  transition: opacity 0.3s;
}

.upload-preview:hover .upload-overlay {
  opacity: 1;
}
</style>