<template>
  <div class="upload-test-page">
    <div class="container">
      <h1>文件上传测试页面</h1>
      
      <el-row :gutter="20">
        <!-- 图片上传测试 -->
        <el-col :span="12">
          <el-card class="upload-card">
            <template #header>
              <div class="card-header">
                <span>图片上传测试</span>
              </div>
            </template>
            
            <div class="upload-section">
              <h3>普通图片上传</h3>
              <ImageUpload
                v-model="imageUrl1"
                placeholder="上传专辑封面"
                :width="200"
                :height="200"
                @success="onImageUploadSuccess"
                @error="onUploadError"
              />
              
              <div v-if="imageUrl1" class="result-display">
                <p><strong>上传结果：</strong></p>
                <p>{{ imageUrl1 }}</p>
              </div>
            </div>
            
            <el-divider />
            
            <div class="upload-section">
              <h3>带裁剪的图片上传</h3>
              <ImageUpload
                v-model="imageUrl2"
                placeholder="上传头像"
                :width="150"
                :height="150"
                :enable-crop="true"
                :crop-aspect-ratio="1"
                @success="onImageUploadSuccess"
                @error="onUploadError"
              />
              
              <div v-if="imageUrl2" class="result-display">
                <p><strong>上传结果：</strong></p>
                <p>{{ imageUrl2 }}</p>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <!-- 音频上传测试 -->
        <el-col :span="12">
          <el-card class="upload-card">
            <template #header>
              <div class="card-header">
                <span>音频上传测试</span>
              </div>
            </template>
            
            <div class="upload-section">
              <h3>音频文件上传</h3>
              <AudioUpload
                v-model="audioUrl"
                placeholder="上传音频文件"
                :auto-play="false"
                @success="onAudioUploadSuccess"
                @error="onUploadError"
                @metadata="onAudioMetadata"
              />
              
              <div v-if="audioMetadata" class="result-display">
                <p><strong>音频信息：</strong></p>
                <p>文件名: {{ audioMetadata.name }}</p>
                <p>大小: {{ formatFileSize(audioMetadata.size) }}</p>
                <p>时长: {{ formatDuration(audioMetadata.duration) }}</p>
                <p>URL: {{ audioMetadata.url }}</p>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- API测试区域 -->
      <el-row style="margin-top: 20px;">
        <el-col :span="24">
          <el-card class="api-test-card">
            <template #header>
              <div class="card-header">
                <span>API连接测试</span>
              </div>
            </template>
            
            <div class="api-test-section">
              <el-space wrap>
                <el-button type="primary" @click="testGetSongs">测试获取歌曲列表</el-button>
                <el-button type="primary" @click="testGetAlbums">测试获取专辑列表</el-button>
                <el-button type="primary" @click="testGetArtists">测试获取歌手列表</el-button>
                <el-button type="warning" @click="clearResults">清空结果</el-button>
              </el-space>
              
              <div v-if="apiResults.length > 0" class="api-results">
                <h4>API测试结果：</h4>
                <div v-for="(result, index) in apiResults" :key="index" class="api-result-item">
                  <el-tag :type="result.success ? 'success' : 'danger'">
                    {{ result.api }}
                  </el-tag>
                  <span class="result-message">{{ result.message }}</span>
                  <div v-if="result.data" class="result-data">
                    <pre>{{ JSON.stringify(result.data, null, 2) }}</pre>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import ImageUpload from '@/components/ImageUpload.vue'
import AudioUpload from '@/components/AudioUpload.vue'
import { getSongList } from '@/api/song.js'
import { getAlbumList } from '@/api/album.js'
import { request } from '@/utils/httpUtils.js'

// 响应式数据
const imageUrl1 = ref('')
const imageUrl2 = ref('')
const audioUrl = ref('')
const audioMetadata = ref(null)
const apiResults = ref([])

// 事件处理
const onImageUploadSuccess = (url) => {
  ElMessage.success('图片上传成功！')
}

const onAudioUploadSuccess = (url) => {
  ElMessage.success('音频上传成功！')
}

const onUploadError = (error) => {
  ElMessage.error('上传失败：' + error.message)
}

const onAudioMetadata = (metadata) => {
  audioMetadata.value = metadata
}

// API测试方法
const testGetSongs = async () => {
  try {
    const data = await getSongList(1, 5)
    apiResults.value.push({
      api: '获取歌曲列表',
      success: true,
      message: '成功获取歌曲数据',
      data: data
    })
    ElMessage.success('歌曲列表API测试成功')
  } catch (error) {
    apiResults.value.push({
      api: '获取歌曲列表',
      success: false,
      message: error.message || '请求失败',
      data: null
    })
    ElMessage.error('歌曲列表API测试失败')
  }
}

const testGetAlbums = async () => {
  try {
    const data = await getAlbumList(1, 5)
    apiResults.value.push({
      api: '获取专辑列表',
      success: true,
      message: '成功获取专辑数据',
      data: data
    })
    ElMessage.success('专辑列表API测试成功')
  } catch (error) {
    apiResults.value.push({
      api: '获取专辑列表',
      success: false,
      message: error.message || '请求失败',
      data: null
    })
    ElMessage.error('专辑列表API测试失败')
  }
}

const testGetArtists = async () => {
  try {
    const data = await request.get('/api/artists', { page: 1, size: 5 })
    apiResults.value.push({
      api: '获取歌手列表',
      success: true,
      message: '成功获取歌手数据',
      data: data
    })
    ElMessage.success('歌手列表API测试成功')
  } catch (error) {
    apiResults.value.push({
      api: '获取歌手列表',
      success: false,
      message: error.message || '请求失败',
      data: null
    })
    ElMessage.error('歌手列表API测试失败')
  }
}

const clearResults = () => {
  apiResults.value = []
  ElMessage.info('已清空测试结果')
}

// 工具方法
const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i]
}

const formatDuration = (seconds) => {
  if (!seconds || isNaN(seconds)) return '00:00'
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = Math.floor(seconds % 60)
  return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
}
</script>

<style scoped>
.upload-test-page {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  color: #303133;
  margin-bottom: 30px;
}

.upload-card, .api-test-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #303133;
}

.upload-section {
  margin-bottom: 20px;
}

.upload-section h3 {
  margin-bottom: 15px;
  color: #606266;
  font-size: 16px;
}

.result-display {
  margin-top: 15px;
  padding: 10px;
  background-color: #f0f9ff;
  border-radius: 4px;
  border-left: 4px solid #409eff;
}

.result-display p {
  margin: 5px 0;
  font-size: 14px;
  color: #303133;
}

.api-test-section {
  padding: 10px 0;
}

.api-results {
  margin-top: 20px;
  max-height: 400px;
  overflow-y: auto;
}

.api-result-item {
  margin-bottom: 15px;
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #fff;
}

.result-message {
  margin-left: 10px;
  color: #606266;
}

.result-data {
  margin-top: 10px;
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
}

.result-data pre {
  margin: 0;
  font-size: 12px;
  white-space: pre-wrap;
  word-wrap: break-word;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style>