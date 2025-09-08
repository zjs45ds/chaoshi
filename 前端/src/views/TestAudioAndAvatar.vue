<template>
  <div class="test-page">
    <div class="page-header">
      <h1>音频播放和头像更新测试</h1>
      <p>测试MinIO文件上传、音频播放和头像更新功能</p>
    </div>
    
    <div class="test-sections">
      <!-- 头像上传测试 -->
      <el-card class="test-card">
        <template #header>
          <div class="card-header">
            <span>头像上传测试</span>
            <el-button type="text" @click="clearAvatar">清除头像</el-button>
          </div>
        </template>
        
        <div class="avatar-test">
          <div class="avatar-row">
            <div class="avatar-item">
              <h4>小尺寸头像 (80px)</h4>
              <AvatarUpload
                v-model="avatar1"
                :user-id="currentUserId"
                :size="80"
                placeholder="小头像"
                @success="handleAvatarSuccess"
                @error="handleAvatarError"
              />
            </div>
            
            <div class="avatar-item">
              <h4>中等尺寸头像 (120px)</h4>
              <AvatarUpload
                v-model="avatar2"
                :user-id="currentUserId"
                :size="120"
                placeholder="中头像"
                @success="handleAvatarSuccess"
                @error="handleAvatarError"
              />
            </div>
            
            <div class="avatar-item">
              <h4>大尺寸头像 (160px)</h4>
              <AvatarUpload
                v-model="avatar3"
                :user-id="currentUserId"
                :size="160"
                placeholder="大头像"
                @success="handleAvatarSuccess"
                @error="handleAvatarError"
              />
            </div>
          </div>
          
          <div class="avatar-info">
            <h4>当前头像URL:</h4>
            <el-input
              v-model="displayAvatarUrl"
              readonly
              placeholder="头像URL将在这里显示"
              class="avatar-url-input"
            />
          </div>
        </div>
      </el-card>
      
      <!-- 音频上传测试 -->
      <el-card class="test-card">
        <template #header>
          <span>音频上传测试</span>
        </template>
        
        <div class="audio-upload-test">
          <AudioUpload
            v-model="audioUrl"
            placeholder="上传音频文件进行测试"
            @success="handleAudioUploadSuccess"
            @error="handleAudioUploadError"
            @metadata="handleAudioMetadata"
          />
          
          <div v-if="audioMetadata" class="audio-metadata">
            <h4>音频文件信息:</h4>
            <ul>
              <li><strong>文件名:</strong> {{ audioMetadata.name }}</li>
              <li><strong>文件大小:</strong> {{ formatFileSize(audioMetadata.size) }}</li>
              <li><strong>文件URL:</strong> {{ audioUrl }}</li>
            </ul>
          </div>
        </div>
      </el-card>
      
      <!-- 音频播放测试 -->
      <el-card class="test-card">
        <template #header>
          <div class="card-header">
            <span>音频播放测试</span>
            <el-button-group>
              <el-button size="small" @click="loadTestSong(1)">测试歌曲1</el-button>
              <el-button size="small" @click="loadTestSong(2)">测试歌曲2</el-button>
              <el-button size="small" @click="loadCurrentAudio">播放上传的音频</el-button>
            </el-button-group>
          </div>
        </template>
        
        <div class="audio-player-test">
          <!-- 完整播放器 -->
          <div class="player-section">
            <h4>完整音频播放器</h4>
            <EnhancedAudioPlayer
              ref="fullPlayerRef"
              :audio-url="currentAudioUrl"
              :song-id="currentSongId"
              :track="currentTrack"
              @play="handlePlayerPlay"
              @pause="handlePlayerPause"
              @ended="handlePlayerEnded"
              @error="handlePlayerError"
            />
          </div>
          
          <!-- 迷你播放器 -->
          <div class="player-section">
            <h4>迷你音频播放器</h4>
            <EnhancedAudioPlayer
              ref="miniPlayerRef"
              :audio-url="currentAudioUrl"
              :song-id="currentSongId"
              :track="currentTrack"
              mini
              @play="handlePlayerPlay"
              @pause="handlePlayerPause"
              @ended="handlePlayerEnded"
              @error="handlePlayerError"
            />
          </div>
        </div>
      </el-card>
      
      <!-- 集成测试 -->
      <el-card class="test-card">
        <template #header>
          <span>API集成测试</span>
        </template>
        
        <div class="api-test">
          <el-button-group>
            <el-button @click="testGetSongList">获取歌曲列表</el-button>
            <el-button @click="testGetStreamUrl">获取流媒体URL</el-button>
            <el-button @click="testUserInfo">获取用户信息</el-button>
          </el-button-group>
          
          <div v-if="apiTestResult" class="api-result">
            <h4>API测试结果:</h4>
            <pre>{{ JSON.stringify(apiTestResult, null, 2) }}</pre>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AvatarUpload from '@/components/AvatarUpload.vue'
import AudioUpload from '@/components/AudioUpload.vue'
import EnhancedAudioPlayer from '@/components/EnhancedAudioPlayer.vue'
import { formatFileSize } from '@/utils/fileUpload.js'
import { getSongList, getSongStreamUrl } from '@/api/song.js'
import { getUserInfo } from '@/api/user.js'

// 响应式数据
const avatar1 = ref('')
const avatar2 = ref('')
const avatar3 = ref('')
const audioUrl = ref('')
const audioMetadata = ref(null)
const currentAudioUrl = ref('')
const currentSongId = ref(null)
const currentTrack = ref({})
const apiTestResult = ref(null)
const currentUserId = ref(1) // 测试用户ID

// 播放器引用
const fullPlayerRef = ref(null)
const miniPlayerRef = ref(null)

// 计算属性
const displayAvatarUrl = computed(() => {
  return avatar1.value || avatar2.value || avatar3.value || '暂无头像'
})

// 头像处理函数
const handleAvatarSuccess = (url) => {
  ElMessage.success('头像上传成功')
  console.log('头像上传成功:', url)
}

const handleAvatarError = (error) => {
  ElMessage.error('头像上传失败: ' + error.message)
  console.error('头像上传失败:', error)
}

const clearAvatar = () => {
  avatar1.value = ''
  avatar2.value = ''
  avatar3.value = ''
  localStorage.removeItem('userAvatar')
  ElMessage.info('头像已清除')
}

// 音频上传处理函数
const handleAudioUploadSuccess = (url) => {
  ElMessage.success('音频上传成功')
  console.log('音频上传成功:', url)
}

const handleAudioUploadError = (error) => {
  ElMessage.error('音频上传失败: ' + error.message)
  console.error('音频上传失败:', error)
}

const handleAudioMetadata = (metadata) => {
  audioMetadata.value = metadata
  console.log('音频元数据:', metadata)
}

// 播放器控制函数
const loadTestSong = async (songId) => {
  try {
    currentSongId.value = songId
    currentAudioUrl.value = '' // 清除直接URL，使用songId获取
    currentTrack.value = {
      title: `测试歌曲 ${songId}`,
      artist: '测试歌手',
      cover: '/src/assets/1音乐.png'
    }
    
    ElMessage.info(`加载测试歌曲 ${songId}`)
  } catch (error) {
    ElMessage.error('加载测试歌曲失败: ' + error.message)
  }
}

const loadCurrentAudio = () => {
  if (!audioUrl.value) {
    ElMessage.warning('请先上传音频文件')
    return
  }
  
  currentSongId.value = null
  currentAudioUrl.value = audioUrl.value
  currentTrack.value = {
    title: audioMetadata.value?.name || '上传的音频',
    artist: '未知艺术家',
    cover: '/src/assets/1音乐.png'
  }
  
  ElMessage.info('加载上传的音频文件')
}

// 播放器事件处理
const handlePlayerPlay = () => {
  console.log('播放器开始播放')
}

const handlePlayerPause = () => {
  console.log('播放器暂停')
}

const handlePlayerEnded = () => {
  console.log('播放器播放结束')
  ElMessage.info('音频播放完毕')
}

const handlePlayerError = (error) => {
  console.error('播放器错误:', error)
  ElMessage.error('播放器错误: ' + error.message)
}

// API测试函数
const testGetSongList = async () => {
  try {
    const result = await getSongList(0, 5)
    apiTestResult.value = result
    ElMessage.success('获取歌曲列表成功')
  } catch (error) {
    apiTestResult.value = { error: error.message }
    ElMessage.error('获取歌曲列表失败: ' + error.message)
  }
}

const testGetStreamUrl = async () => {
  try {
    const result = await getSongStreamUrl(1)
    apiTestResult.value = result
    ElMessage.success('获取流媒体URL成功')
  } catch (error) {
    apiTestResult.value = { error: error.message }
    ElMessage.error('获取流媒体URL失败: ' + error.message)
  }
}

const testUserInfo = async () => {
  try {
    const result = await getUserInfo(currentUserId.value)
    apiTestResult.value = result
    ElMessage.success('获取用户信息成功')
  } catch (error) {
    apiTestResult.value = { error: error.message }
    ElMessage.error('获取用户信息失败: ' + error.message)
  }
}

// 生命周期
onMounted(() => {
  // 加载已保存的头像
  const savedAvatar = localStorage.getItem('userAvatar')
  if (savedAvatar) {
    avatar1.value = savedAvatar
    avatar2.value = savedAvatar
    avatar3.value = savedAvatar
  }
  
  // 监听全局头像变更事件
  const handleAvatarChange = (event) => {
    const { url } = event.detail
    avatar1.value = url
    avatar2.value = url
    avatar3.value = url
  }
  
  window.addEventListener('user-avatar-changed', handleAvatarChange)
  
  // 组件卸载时清理
  return () => {
    window.removeEventListener('user-avatar-changed', handleAvatarChange)
  }
})
</script>

<style scoped>
.test-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  color: #303133;
  margin-bottom: 10px;
}

.page-header p {
  color: #909399;
  font-size: 14px;
}

.test-sections {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.test-card {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 头像测试样式 */
.avatar-test {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.avatar-row {
  display: flex;
  gap: 30px;
  justify-content: center;
  flex-wrap: wrap;
}

.avatar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.avatar-item h4 {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.avatar-info {
  margin-top: 20px;
}

.avatar-info h4 {
  margin-bottom: 10px;
  color: #606266;
}

.avatar-url-input {
  max-width: 600px;
}

/* 音频测试样式 */
.audio-upload-test {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.audio-metadata {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 6px;
}

.audio-metadata h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #606266;
}

.audio-metadata ul {
  margin: 0;
  padding-left: 20px;
}

.audio-metadata li {
  margin-bottom: 5px;
  color: #303133;
}

/* 播放器测试样式 */
.audio-player-test {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.player-section h4 {
  margin-bottom: 15px;
  color: #606266;
  font-size: 16px;
}

/* API测试样式 */
.api-test {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.api-result {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 6px;
  max-height: 300px;
  overflow-y: auto;
}

.api-result h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #606266;
}

.api-result pre {
  margin: 0;
  font-size: 12px;
  color: #303133;
  white-space: pre-wrap;
  word-break: break-all;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .test-page {
    padding: 10px;
  }
  
  .avatar-row {
    flex-direction: column;
    align-items: center;
    gap: 20px;
  }
  
  .card-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .avatar-url-input {
    max-width: 100%;
  }
}
</style>
