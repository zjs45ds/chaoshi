// 音频播放器组件
<template>
  <div class="enhanced-audio-player" :class="{ 'mini': mini, 'loading': loading }">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <el-icon class="loading-icon is-loading"><Loading /></el-icon>
      <span>加载音频中...</span>
    </div>
    
    <!-- 播放器主体 -->
    <div v-else class="player-content">
      <!-- 歌曲信息 -->
      <div v-if="!mini && currentTrack" class="track-info">
        <div class="track-cover">
          <img 
            :src="currentTrack.cover || defaultCover" 
            :alt="currentTrack.title"
            @error="handleCoverError"
          />
        </div>
        <div class="track-details">
          <h3 class="track-title">{{ currentTrack.title }}</h3>
          <p class="track-artist">{{ currentTrack.artist }}</p>
        </div>
      </div>
      
      <!-- 播放控制 -->
      <div class="player-controls">
        <div class="main-controls">
          <el-button 
            :icon="isPlaying ? VideoPause : VideoPlay"
            type="primary"
            circle
            :size="mini ? 'small' : 'default'"
            @click="togglePlay"
            :disabled="!audioUrl"
          />
          
          <el-button 
            v-if="!mini"
            :icon="isMuted ? VolumeOff : VolumeDown"
            circle
            size="small"
            @click="toggleMute"
          />
          
          <div v-if="!mini" class="time-display">
            {{ formatTime(currentTime) }} / {{ formatTime(duration) }}
          </div>
        </div>
        
        <!-- 进度条 -->
        <div class="progress-container">
          <el-slider
            v-model="progressValue"
            :disabled="!audioUrl"
            :show-tooltip="false"
            @change="handleProgressChange"
            @input="handleProgressInput"
          />
        </div>
        
        <!-- 音量控制 -->
        <div v-if="!mini" class="volume-container">
          <el-slider
            v-model="volumeValue"
            :max="100"
            :show-tooltip="false"
            @change="handleVolumeChange"
            style="width: 80px;"
          />
        </div>
      </div>
      
      <!-- 错误信息 -->
      <div v-if="error" class="error-message">
        <el-alert
          :title="error"
          type="error"
          show-icon
          :closable="false"
        />
      </div>
    </div>
    
    <!-- 音频元素 -->
    <audio 
      ref="audioRef"
      preload="metadata"
      @loadstart="handleLoadStart"
      @loadedmetadata="handleLoadedMetadata"
      @timeupdate="handleTimeUpdate"
      @ended="handleEnded"
      @error="handleAudioError"
      @canplay="handleCanPlay"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  VideoPlay, 
  VideoPause, 
  VolumeDown, 
  VolumeOff,
  Loading
} from '@element-plus/icons-vue'
const props = defineProps({
  // 音频源 - 可以是URL或歌曲ID
  audioUrl: {
    type: String,
    default: ''
  },
  songId: {
    type: [String, Number],
    default: null
  },
  // 显示模式
  mini: {
    type: Boolean,
    default: false
  },
  // 自动播放
  autoplay: {
    type: Boolean,
    default: false
  },
  // 音轨信息
  track: {
    type: Object,
    default: () => ({})
  }
})

// Emits
const emit = defineEmits(['play', 'pause', 'ended', 'error', 'timeupdate', 'loadedmetadata'])

// Refs
const audioRef = ref(null)

// Reactive data
const loading = ref(false)
const isPlaying = ref(false)
const currentTime = ref(0)
const duration = ref(0)
const volume = ref(80)
const isMuted = ref(false)
const error = ref('')
const currentAudioUrl = ref('')
const currentTrack = ref({})

// 默认封面
const defaultCover = '/src/assets/1音乐.png'

// Computed
const progressValue = computed({
  get: () => duration.value ? (currentTime.value / duration.value) * 100 : 0,
  set: (value) => {
    if (audioRef.value && duration.value) {
      audioRef.value.currentTime = (value / 100) * duration.value
    }
  }
})

const volumeValue = computed({
  get: () => volume.value,
  set: (value) => {
    volume.value = value
    if (audioRef.value) {
      audioRef.value.volume = value / 100
    }
  }
})

// Methods
const formatTime = (seconds) => {
  if (!seconds || isNaN(seconds)) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const loadAudio = async () => {
  try {
    loading.value = true
    error.value = ''
    
    let audioUrl = props.audioUrl
    let trackInfo = props.track
    
    // 如果提供了歌曲ID，从API获取流媒体URL
    if (props.songId && !audioUrl) {
      console.log('🎵 从API获取音频流URL, songId:', props.songId)
      // 文件上传功能已移除，使用默认处理
      console.log('⚠️ 音频流获取功能已暂时关闭')
      const result = { success: false }
      
      if (result.success) {
        audioUrl = result.audioUrl
        trackInfo = { ...trackInfo, ...result.metadata }
        console.log('✅ 获取音频URL成功:', audioUrl)
      } else {
        throw new Error(result.error || '获取音频流失败')
      }
    }
    
    if (!audioUrl) {
      throw new Error('未提供有效的音频源')
    }
    
    // 更新状态
    currentAudioUrl.value = audioUrl
    currentTrack.value = trackInfo
    
    // 设置音频源
    if (audioRef.value) {
      audioRef.value.src = audioUrl
      audioRef.value.load()
    }
    
  } catch (err) {
    console.error('🚨 加载音频失败:', err)
    error.value = err.message || '音频加载失败'
    ElMessage.error(error.value)
    emit('error', err)
  } finally {
    loading.value = false
  }
}

const togglePlay = async () => {
  if (!audioRef.value || !currentAudioUrl.value) return
  
  try {
    if (isPlaying.value) {
      await audioRef.value.pause()
    } else {
      await audioRef.value.play()
    }
  } catch (err) {
    console.error('播放控制失败:', err)
    ElMessage.error('播放失败')
  }
}

const toggleMute = () => {
  if (audioRef.value) {
    audioRef.value.muted = !audioRef.value.muted
    isMuted.value = audioRef.value.muted
  }
}

const handleProgressChange = (value) => {
  progressValue.value = value
}

const handleProgressInput = (value) => {
  if (audioRef.value && duration.value) {
    audioRef.value.currentTime = (value / 100) * duration.value
  }
}

const handleVolumeChange = (value) => {
  volumeValue.value = value
}

// 音频事件处理
const handleLoadStart = () => {
  loading.value = true
}

const handleLoadedMetadata = () => {
  if (audioRef.value) {
    duration.value = audioRef.value.duration || 0
    emit('loadedmetadata', duration.value)
  }
  loading.value = false
  
  // 自动播放
  if (props.autoplay) {
    nextTick(() => {
      togglePlay()
    })
  }
}

const handleTimeUpdate = () => {
  if (audioRef.value) {
    currentTime.value = audioRef.value.currentTime || 0
    emit('timeupdate', currentTime.value)
  }
}

const handleEnded = () => {
  isPlaying.value = false
  emit('ended')
}

const handleAudioError = (event) => {
  loading.value = false
  isPlaying.value = false
  
  const audioError = audioRef.value?.error
  let errorMessage = '音频播放错误'
  
  if (audioError) {
    switch (audioError.code) {
      case 1:
        errorMessage = '音频加载被中止'
        break
      case 2:
        errorMessage = '网络错误，无法加载音频'
        break
      case 3:
        errorMessage = '音频解码失败'
        break
      case 4:
        errorMessage = '音频格式不支持'
        break
      default:
        errorMessage = '未知的音频错误'
    }
  }
  
  error.value = errorMessage
  console.error('🚨 音频播放错误:', errorMessage, event)
  emit('error', new Error(errorMessage))
}

const handleCanPlay = () => {
  loading.value = false
}

const handleCoverError = (event) => {
  event.target.src = defaultCover
}

// 监听器
watch([() => props.audioUrl, () => props.songId], () => {
  if (props.audioUrl || props.songId) {
    loadAudio()
  }
}, { immediate: true })

watch(() => props.track, (newTrack) => {
  currentTrack.value = { ...currentTrack.value, ...newTrack }
}, { deep: true })

// 音频事件监听
onMounted(() => {
  if (audioRef.value) {
    // 播放状态监听
    audioRef.value.addEventListener('play', () => {
      isPlaying.value = true
      emit('play')
    })
    
    audioRef.value.addEventListener('pause', () => {
      isPlaying.value = false
      emit('pause')
    })
    
    // 设置初始音量
    audioRef.value.volume = volume.value / 100
  }
})

onUnmounted(() => {
  if (audioRef.value) {
    audioRef.value.pause()
    audioRef.value.src = ''
  }
})

// 暴露方法供父组件调用
defineExpose({
  play: () => audioRef.value?.play(),
  pause: () => audioRef.value?.pause(),
  stop: () => {
    if (audioRef.value) {
      audioRef.value.pause()
      audioRef.value.currentTime = 0
    }
  },
  seek: (time) => {
    if (audioRef.value) {
      audioRef.value.currentTime = time
    }
  },
  setVolume: (vol) => {
    volume.value = Math.max(0, Math.min(100, vol))
    if (audioRef.value) {
      audioRef.value.volume = volume.value / 100
    }
  },
  reload: loadAudio
})
</script>

<style scoped>
.enhanced-audio-player {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  position: relative;
  min-height: 120px;
}

.enhanced-audio-player.mini {
  padding: 8px;
  min-height: 60px;
}

.enhanced-audio-player.loading {
  pointer-events: none;
}

/* 加载状态 */
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  z-index: 10;
  border-radius: 8px;
}

.loading-icon {
  font-size: 24px;
  color: #409eff;
}

/* 播放器内容 */
.player-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
}

/* 歌曲信息 */
.track-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.track-cover {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f5f5f5;
}

.track-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.track-details {
  flex: 1;
  min-width: 0;
}

.track-title {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.track-artist {
  margin: 0;
  font-size: 14px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 播放控制 */
.player-controls {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.main-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.time-display {
  font-size: 12px;
  color: #909399;
  font-variant-numeric: tabular-nums;
  min-width: 80px;
}

.progress-container {
  flex: 1;
}

.volume-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 错误信息 */
.error-message {
  margin-top: 8px;
}

/* Mini模式样式 */
.enhanced-audio-player.mini .player-content {
  flex-direction: row;
  align-items: center;
  gap: 12px;
}

.enhanced-audio-player.mini .player-controls {
  flex: 1;
  flex-direction: row;
  align-items: center;
  gap: 12px;
}

.enhanced-audio-player.mini .main-controls {
  gap: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .enhanced-audio-player:not(.mini) {
    padding: 12px;
  }
  
  .track-info {
    gap: 8px;
  }
  
  .track-cover {
    width: 48px;
    height: 48px;
  }
  
  .track-title {
    font-size: 14px;
  }
  
  .track-artist {
    font-size: 12px;
  }
  
  .main-controls {
    gap: 8px;
  }
  
  .volume-container {
    display: none;
  }
}

/* 动画 */
.is-loading {
  animation: rotating 2s linear infinite;
}

@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
