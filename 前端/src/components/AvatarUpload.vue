<template>
  <div class="avatar-upload">
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
      accept="image/jpeg,image/png,image/gif,image/webp"
      class="upload-wrapper"
    >
      <div 
        class="avatar-container" 
        :class="{ 'uploading': uploading }"
        :style="{ width: size + 'px', height: size + 'px' }"
      >
        <!-- 上传进度 -->
        <div v-if="uploading" class="upload-progress">
          <el-progress
            type="circle"
            :percentage="uploadProgress"
            :width="Math.min(size - 20, 80)"
          ></el-progress>
          <div class="progress-text">{{ uploadProgress }}%</div>
        </div>
        
        <!-- 头像预览 -->
        <div v-else-if="avatarUrl" class="avatar-preview">
          <img 
            :src="avatarUrl" 
            :alt="alt"
            class="avatar-image"
            @error="handleImageError"
          />
          <div class="avatar-overlay">
            <el-icon class="overlay-icon"><Camera /></el-icon>
            <span class="overlay-text">更换头像</span>
          </div>
        </div>
        
        <!-- 上传占位符 -->
        <div v-else class="upload-placeholder">
          <el-icon class="placeholder-icon"><Plus /></el-icon>
          <span class="placeholder-text">{{ placeholder }}</span>
        </div>
      </div>
    </el-upload>
    
    <!-- 操作按钮 -->
    <div v-if="showActions && avatarUrl && !uploading" class="avatar-actions">
      <el-button 
        type="primary" 
        size="small"
        :icon="Edit"
        @click="handleReupload"
      >
        重新上传
      </el-button>
      <el-button 
        type="danger" 
        size="small"
        :icon="Delete"
        @click="handleDelete"
      >
        删除头像
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, 
  Camera, 
  Edit, 
  Delete 
} from '@element-plus/icons-vue'
import { handleAvatarUploadToMinIO } from '@/utils/fileUpload.js'

// Props
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  userId: {
    type: [String, Number],
    default: null
  },
  size: {
    type: Number,
    default: 120
  },
  placeholder: {
    type: String,
    default: '上传头像'
  },
  alt: {
    type: String,
    default: '用户头像'
  },
  showActions: {
    type: Boolean,
    default: true
  },
  autoSave: {
    type: Boolean,
    default: true
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'success', 'error', 'change'])

// Refs
const uploadRef = ref(null)

// Reactive data
const uploading = ref(false)
const uploadProgress = ref(0)
const avatarUrl = ref(props.modelValue)

// Computed
const uploadAction = computed(() => 
  props.userId ? `/api/user/upload-avatar` : `/api/files/upload`
)

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

const uploadData = computed(() => {
  const data = { fileType: 'image' }
  if (props.userId) {
    data.userId = props.userId
  }
  return data
})

// Watch
watch(() => props.modelValue, (newVal) => {
  avatarUrl.value = newVal
})

// Methods
const beforeUpload = (file) => {
  // 验证文件类型
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    ElMessage.error('请选择支持的图片格式 (JPG, PNG, GIF, WEBP)')
    return false
  }
  
  // 验证文件大小 (2MB)
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('头像大小不能超过2MB')
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
    const newAvatarUrl = response.data
    avatarUrl.value = newAvatarUrl
    
    // 更新本地存储
    if (props.autoSave) {
      localStorage.setItem('userAvatar', newAvatarUrl)
    }
    
    // 触发事件
    emit('update:modelValue', newAvatarUrl)
    emit('success', newAvatarUrl)
    emit('change', newAvatarUrl)
    
    // 全局事件
    window.dispatchEvent(new CustomEvent('user-avatar-changed', {
      detail: { 
        url: newAvatarUrl, 
        userId: props.userId 
      }
    }))
    
    ElMessage.success('头像上传成功')
  } else {
    onError(response)
  }
}

const onError = (error) => {
  uploading.value = false
  uploadProgress.value = 0
  
  const message = error?.message || '头像上传失败'
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
    await ElMessageBox.confirm('确定要删除头像吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    avatarUrl.value = ''
    
    if (props.autoSave) {
      localStorage.removeItem('userAvatar')
    }
    
    emit('update:modelValue', '')
    emit('change', '')
    
    ElMessage.success('头像删除成功')
  } catch {
    // 用户取消删除
  }
}

const handleImageError = (event) => {
  // 头像加载失败时的处理
  console.warn('头像加载失败:', avatarUrl.value)
  event.target.style.display = 'none'
}

// 初始化
onMounted(() => {
  // 监听全局头像变更事件
  const handleAvatarChange = (event) => {
    if (event.detail.userId === props.userId || !props.userId) {
      avatarUrl.value = event.detail.url
    }
  }
  
  window.addEventListener('user-avatar-changed', handleAvatarChange)
  
  // 组件卸载时清理
  return () => {
    window.removeEventListener('user-avatar-changed', handleAvatarChange)
  }
})
</script>

<style scoped>
.avatar-upload {
  display: inline-block;
}

.upload-wrapper {
  display: block;
}

.avatar-container {
  border: 2px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
  background-color: #fafafa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-container:hover {
  border-color: #409eff;
  background-color: #f5f7fa;
}

.avatar-container.uploading {
  border-color: #409eff;
  background-color: #f0f9ff;
}

/* 上传进度 */
.upload-progress {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.progress-text {
  font-size: 12px;
  color: #666;
}

/* 头像预览 */
.avatar-preview {
  width: 100%;
  height: 100%;
  position: relative;
  border-radius: 50%;
  overflow: hidden;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: white;
  font-size: 12px;
}

.avatar-preview:hover .avatar-overlay {
  opacity: 1;
}

.overlay-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.overlay-text {
  font-size: 12px;
}

/* 上传占位符 */
.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  text-align: center;
  padding: 10px;
}

.placeholder-icon {
  font-size: 24px;
  margin-bottom: 8px;
  color: #c0c4cc;
}

.placeholder-text {
  font-size: 12px;
}

/* 操作按钮 */
.avatar-actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .avatar-actions {
    flex-direction: column;
  }
  
  .overlay-text {
    display: none;
  }
}
</style>
