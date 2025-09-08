<template>
  <div class="fullscreen-player" :class="{ 'is-visible': visible }" v-if="visible || isTransitioning">
    <!-- 背景层 -->
    <div class="player-background" :style="backgroundStyle"></div>
    
    <!-- 播放器内容 -->
    <div class="player-content">
      <!-- 头部控制栏 -->
      <div class="player-header">
        <button class="header-btn back" @click="closePlayer">
          <i class="icon-arrow-down"></i>
        </button>
        
        <div class="header-info">
          <div class="playing-text">正在播放</div>
          <div class="source-text">{{ getSourceInfo() }}</div>
        </div>
      </div>
      
      <!-- 主要内容区域 -->
      <div class="player-main">
        <!-- 专辑封面 -->
        <div class="album-section">
          <div class="album-cover" :class="{ 'playing': isPlaying }">
            <img :src="currentSong?.cover || '/src/assets/1音乐.png'" :alt="currentSong?.name" @error="handleImageError" @load="handleImageLoad" />
          </div>
        </div>
        
        <!-- 歌曲信息 -->
        <div class="song-section">
          <div class="song-info">
            <h1 class="song-title">{{ currentSong?.name || '未知歌曲' }}</h1>
            <p class="song-artist">{{ currentSong?.artist || '未知艺术家' }}</p>
          </div>
        </div>
      </div>
      
      <!-- 播放控制区域 -->
      <div class="player-controls-section">
        <!-- 进度控制 -->
        <div class="progress-section">
          <div class="progress-bar" @click="seekTo" ref="progressBarRef">
            <div class="progress-bg"></div>
            <div class="progress-fill" :style="{ width: `${progress}%` }"></div>
            <div class="progress-handle" :style="{ left: `${progress}%` }" @mousedown="startDrag"></div>
          </div>
        </div>
        
        <!-- 单行控制按钮布局 -->
        <div class="single-control-row">
          <!-- 左侧功能按钮 -->
          <div class="left-controls">
            <button class="control-btn small mode" @click="togglePlayMode">
              <i :class="getModeIcon()"></i>
            </button>
          </div>
          
          <!-- 时间显示 -->
          <div class="time-display">
            <span class="current-time">{{ currentTimeFormatted }}</span>
            <span class="time-separator">/</span>
            <span class="total-time">{{ durationFormatted }}</span>
          </div>
          
          <!-- 中间播放控制按钮 -->
          <div class="center-controls">
            <button class="control-btn love" @click="toggleLove" :class="{ 'loved': isCurrentSongLiked }">
              <img v-if="isCurrentSongLiked" class="love-icon-img" src="/src/assets/已收藏.png" alt="已收藏" />
              <i v-else class="icon-heart"></i>
            </button>
            
            <button class="control-btn prev" @click="playPrevious" :disabled="!hasPlaylist">
              <i class="icon-prev"></i>
            </button>
            
            <button class="control-btn play" @click="togglePlay" :class="{ 'playing': isPlaying }">
              <i :class="isPlaying ? 'icon-pause' : 'icon-play'"></i>
            </button>
            
            <button class="control-btn next" @click="playNext" :disabled="!hasPlaylist">
              <i class="icon-next"></i>
            </button>
          </div>
          
          <!-- 右侧功能按钮 -->
          <div class="right-controls">
            <button class="control-btn small playlist" @click="togglePlaylist">
              <i class="icon-list"></i>
            </button>
            
            <button class="control-btn small volume" @click="toggleVolumePanel" ref="volumeButtonRef">
              <i :class="getVolumeIcon()"></i>
            </button>
          </div>
        </div>
      </div>
      
      <!-- 音量弹出面板 -->
      <div class="volume-popup" v-if="showVolumePanel" :style="volumePopupStyle">
        <div class="volume-popup-content">
          <div class="volume-slider-vertical" @click="setVolumeVertical" ref="volumeSliderRef">
            <div class="volume-bg-vertical"></div>
            <div class="volume-fill-vertical" :style="{ height: `${displayVolume * 100}%` }"></div>
            <div class="volume-handle-vertical" :style="{ bottom: `${displayVolume * 100}%` }" @mousedown="startVolumeDrag"></div>
          </div>
          
          <span class="volume-text-popup">{{ Math.round(displayVolume * 100) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { 
  isPlaying, isPaused, isLoading, currentTime, duration, volume, isMuted,
  currentSong, currentIndex, playlist, playMode, playModeText,
  progress, currentTimeFormatted, durationFormatted,
  togglePlay, playNext, playPrevious, togglePlayMode, seekToProgress, setVolumeLevel, toggleMute,
  hasSong, hasPlaylist
} from '@/utils/musicPlayer.js'
import { isSongLiked, toggleSongLike } from '@/utils/favoriteManager.js'

const props = defineProps({
  visible: { type: Boolean, default: false }
})

const emit = defineEmits(['close', 'toggle-playlist'])

// 组件状态
const progressBarRef = ref(null)
const volumeSliderRef = ref(null)
const volumeButtonRef = ref(null)
const isDraggingProgress = ref(false)
const isDraggingVolume = ref(false)
const isTransitioning = ref(false)
const showVolumePanel = ref(false)

// 计算属性
const backgroundStyle = computed(() => {
  const coverUrl = currentSong.value?.cover || '/src/assets/1音乐.png'
  return {
    backgroundImage: `linear-gradient(rgba(0, 0, 0, 0.2), rgba(0, 0, 0, 0.3)), url(${coverUrl})`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    backgroundRepeat: 'no-repeat'
  }
})

const displayVolume = computed(() => isMuted.value ? 0 : volume.value)
const isCurrentSongLiked = computed(() => {
  return currentSong.value ? isSongLiked(currentSong.value.id) : false
})

const volumePopupStyle = computed(() => {
  if (!volumeButtonRef.value) return {}
  
  const rect = volumeButtonRef.value.getBoundingClientRect()
  return {
    position: 'fixed',
    left: `${rect.left + rect.width / 2 - 20}px`,
    bottom: `${window.innerHeight - rect.top + 10}px`,
    zIndex: 100000
  }
})

// 工具函数
const getModeIcon = () => {
  const modeIcons = { sequence: 'icon-sequence', loop: 'icon-loop-one', shuffle: 'icon-shuffle' }
  return modeIcons[playMode.value] || 'icon-sequence'
}

const getVolumeIcon = () => {
  if (isMuted.value || volume.value === 0) return 'icon-volume-mute'
  if (volume.value < 0.3) return 'icon-volume-low'
  if (volume.value < 0.7) return 'icon-volume-medium'
  return 'icon-volume-high'
}

const getSourceInfo = () => {
  return currentSong.value?.album ? `来自专辑《${currentSong.value.album}》` : '我的音乐库'
}

// 事件处理
const closePlayer = () => {
  isTransitioning.value = true
  emit('close')
  setTimeout(() => { isTransitioning.value = false }, 300)
}

const togglePlaylist = () => emit('toggle-playlist')
const toggleLove = async () => {
  if (!currentSong.value) return
  
  try {
    await toggleSongLike(currentSong.value)
  } catch (error) {
    console.error('切换收藏状态失败:', error)
  }
}
const handleImageError = (event) => { 
  // 防止循环加载错误
  if (event.target.src.includes('1音乐.png')) {
    event.target.style.display = 'none'
    return
  }
  event.target.src = '/src/assets/1音乐.png' 
}
const handleImageLoad = (event) => {
  event.target.style.display = 'block'
}

// 音量面板控制
const toggleVolumePanel = () => {
  showVolumePanel.value = !showVolumePanel.value
}

const setVolumeVertical = (event) => {
  if (!volumeSliderRef.value || isDraggingVolume.value) return
  const rect = volumeSliderRef.value.getBoundingClientRect()
  // 垂直滑块，从底部开始计算
  const percentage = Math.max(0, Math.min(1, (rect.bottom - event.clientY) / rect.height))
  setVolumeLevel(percentage)
}

const seekTo = (event) => {
  if (!progressBarRef.value || isDraggingProgress.value) return
  const rect = progressBarRef.value.getBoundingClientRect()
  const percentage = Math.max(0, Math.min(1, (event.clientX - rect.left) / rect.width))
  seekToProgress(percentage)
}

const setVolume = (event) => {
  if (!volumeSliderRef.value || isDraggingVolume.value) return
  const rect = volumeSliderRef.value.getBoundingClientRect()
  const percentage = Math.max(0, Math.min(1, (event.clientX - rect.left) / rect.width))
  setVolumeLevel(percentage)
}

const startDrag = (event) => {
  isDraggingProgress.value = true
  event.preventDefault()
  
  const handleMouseMove = (e) => {
    if (!progressBarRef.value) return
    const rect = progressBarRef.value.getBoundingClientRect()
    const percentage = Math.max(0, Math.min(1, (e.clientX - rect.left) / rect.width))
    seekToProgress(percentage)
  }
  
  const handleMouseUp = () => {
    isDraggingProgress.value = false
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mouseup', handleMouseUp)
  }
  
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
}

const startVolumeDrag = (event) => {
  isDraggingVolume.value = true
  event.preventDefault()
  
  const handleMouseMove = (e) => {
    if (!volumeSliderRef.value) return
    const rect = volumeSliderRef.value.getBoundingClientRect()
    // 垂直滑块，从底部开始计算
    const percentage = Math.max(0, Math.min(1, (rect.bottom - e.clientY) / rect.height))
    setVolumeLevel(percentage)
  }
  
  const handleMouseUp = () => {
    isDraggingVolume.value = false
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mouseup', handleMouseUp)
  }
  
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
}

// 监听器和生命周期
watch(() => props.visible, (newVisible) => {
  if (newVisible) {
    isTransitioning.value = true
    // 强制禁用页面滚动和隐藏其他元素
    document.body.classList.add('fullscreen-player-active')
    document.body.style.overflow = 'hidden'
    document.documentElement.style.overflow = 'hidden'
    document.body.style.overscrollBehavior = 'none'
    document.documentElement.style.overscrollBehavior = 'none'
    document.body.style.position = 'fixed'
    document.body.style.width = '100%'
    document.body.style.height = '100%'
    nextTick(() => setTimeout(() => { isTransitioning.value = false }, 300))
  } else {
    // 恢复页面滚动和显示其他元素
    document.body.classList.remove('fullscreen-player-active')
    document.body.style.overflow = ''
    document.documentElement.style.overflow = ''
    document.body.style.overscrollBehavior = ''
    document.documentElement.style.overscrollBehavior = ''
    document.body.style.position = ''
    document.body.style.width = ''
    document.body.style.height = ''
  }
})

onMounted(() => {
  const handleKeydown = (event) => {
    if (event.key === 'Escape' && props.visible) {
      if (showVolumePanel.value) {
        showVolumePanel.value = false
      } else {
        closePlayer()
      }
    }
  }
  
  // 点击外部关闭音量面板
  const handleClickOutside = (event) => {
    if (showVolumePanel.value && volumeButtonRef.value && 
        !volumeButtonRef.value.contains(event.target) &&
        !event.target.closest('.volume-popup')) {
      showVolumePanel.value = false
    }
  }
  
  // 禁用触摸滚动
  const handleTouchMove = (event) => {
    if (props.visible) {
      event.preventDefault()
    }
  }
  
  document.addEventListener('keydown', handleKeydown)
  document.addEventListener('click', handleClickOutside)
  document.addEventListener('touchmove', handleTouchMove, { passive: false })
  
  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeydown)
    document.removeEventListener('click', handleClickOutside)
    document.removeEventListener('touchmove', handleTouchMove)
    // 组件卸载时强制恢复滚动和移除类
    document.body.classList.remove('fullscreen-player-active')
    document.body.style.overflow = ''
    document.documentElement.style.overflow = ''
    document.body.style.overscrollBehavior = ''
    document.documentElement.style.overscrollBehavior = ''
    document.body.style.position = ''
    document.body.style.width = ''
    document.body.style.height = ''
  })
})
</script>

<style scoped>
.fullscreen-player {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 99999;
  display: flex;
  flex-direction: column;
  transform: translateY(100%);
  transition: transform 0.3s ease;
  overflow: hidden;
  overscroll-behavior: none;
}

.fullscreen-player.is-visible {
  transform: translateY(0);
}

/* 背景层 */
.player-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
}

.player-content {
  position: relative;
  z-index: 10;
  display: flex;
  flex-direction: column;
  height: 100vh;
  color: #fff;
  background: none;
}

/* 头部控制栏 */
.player-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  flex-shrink: 0;
}

.header-btn {
  background: none;
  border: none;
  color: #fff;
  cursor: pointer;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  font-size: 16px;
}

.header-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.header-info {
  text-align: center;
  flex: 1;
}

.playing-text {
  font-size: 12px;
  opacity: 0.7;
  margin-bottom: 2px;
}

.source-text {
  font-size: 14px;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: 8px;
}

/* 主要内容区域 */
.player-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  min-height: 0;
}

.album-section {
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
}

.album-cover {
  width: 240px;
  height: 240px;
  overflow: hidden;
  transition: transform 0.2s ease;
  background: #333; /* 添加暗色背景 */
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.album-cover::before {
  content: '🎵'; /* 音符号作为默认占位符 */
  font-size: 48px;
  color: #666;
  position: absolute;
  z-index: 1;
}

.album-cover:hover {
  transform: scale(1.02);
}

.album-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: relative;
  z-index: 2;
}

/* 歌曲信息区域 */
.song-section {
  text-align: center;
  max-width: 500px;
  margin-bottom: 40px;
}

.song-info {
  margin-bottom: 20px;
}

.song-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
  line-height: 1.2;
}

.song-artist {
  font-size: 16px;
  opacity: 0.8;
  margin: 0;
}



/* 播放控制区域 */
.player-controls-section {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px 40px 40px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8) 0%, rgba(0, 0, 0, 0.4) 50%, transparent 100%);
  backdrop-filter: blur(20px);
}

.progress-section {
  margin-bottom: 24px;
}

.progress-bar {
  position: relative;
  width: 100%;
  height: 4px;
  cursor: pointer;
  overflow: hidden;
}

.progress-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 100%;
  background: rgba(255, 255, 255, 0.3);
}

.progress-fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: #fff;
  transition: width 0.1s ease;
}

.progress-handle {
  position: absolute;
  top: 50%;
  width: 12px;
  height: 12px;
  background: #fff;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  opacity: 0;
  transition: opacity 0.2s ease;
  cursor: grab;
}

.progress-handle:active {
  cursor: grabbing;
}

.progress-bar:hover .progress-handle {
  opacity: 1;
}

/* 单行控制按钮布局 */
.single-control-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.left-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 80px;
}

.time-display {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  font-weight: 500;
  color: #fff;
  opacity: 0.9;
  flex-shrink: 0;
}

.time-separator {
  opacity: 0.6;
  margin: 0 2px;
}

.center-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  flex: 1;
}

.right-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 80px;
  justify-content: flex-end;
}

.control-btn {
  background: none;
  border: none;
  border-radius: 50%;
  color: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  outline: none;
  box-shadow: none;
}

.control-btn:focus {
  outline: none;
  box-shadow: none;
}

.control-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.control-btn:not(:disabled):hover {
  background: rgba(255, 255, 255, 0.1);
  transform: scale(1.05);
}

/* 按钮样式 */
.control-btn {
  background: none;
  border: none;
  border-radius: 50%;
  color: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  outline: none;
  box-shadow: none;
}

.control-btn:focus {
  outline: none;
  box-shadow: none;
}

.control-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.control-btn:not(:disabled):hover {
  background: rgba(255, 255, 255, 0.1);
  transform: scale(1.05);
}

/* 小按钮样式（左右两侧）*/
.control-btn.small {
  width: 40px;
  height: 40px;
  font-size: 18px;
}

/* 主要按钮样式（中间区域）*/
.control-btn.prev,
.control-btn.next,
.control-btn.love {
  width: 44px;
  height: 44px;
  font-size: 20px;
}

.control-btn.play {
  width: 56px;
  height: 56px;
  font-size: 22px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
}

.control-btn.play:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: scale(1.1);
}

.control-btn.love {
  color: #fff;
}

.control-btn.love.loved {
  color: #ff3040;
}

.control-btn.love .love-icon-img {
  width: 22px;
  height: 22px;
  object-fit: contain;
}

.volume-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.volume-text {
  font-size: 12px;
  opacity: 0.8;
  min-width: 24px;
  text-align: center;
}

/* 音量弹出面板 */
.volume-popup {
  position: fixed;
  z-index: 100000;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  padding: 12px 8px;
  animation: volumePopupIn 0.2s ease-out;
}

@keyframes volumePopupIn {
  from {
    opacity: 0;
    transform: translateY(10px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.volume-popup-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  min-height: 80px;
}

/* 垂直音量滑块 */
.volume-slider-vertical {
  width: 4px;
  height: 80px;
  position: relative;
  cursor: pointer;
  flex: 1;
}

.volume-bg-vertical {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.3);
}

.volume-fill-vertical {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  transition: height 0.1s ease;
  min-height: 2px;
}

.volume-handle-vertical {
  position: absolute;
  left: 50%;
  width: 12px;
  height: 12px;
  background: #fff;
  transform: translate(-50%, 50%);
  opacity: 0;
  transition: opacity 0.2s ease;
  cursor: grab;
}

.volume-handle-vertical:active {
  cursor: grabbing;
}

.volume-slider-vertical:hover .volume-handle-vertical {
  opacity: 1;
}

.volume-text-popup {
  font-size: 11px;
  opacity: 0.8;
  color: #fff;
  min-width: 20px;
  text-align: center;
}

/* 动画 */
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 图标样式 - 使用assets中的SVG图标，增大尺寸 */
.icon-arrow-down::before {
  content: '';
  display: inline-block;
  width: 24px;
  height: 24px;
  background: url('@/assets/left.svg') no-repeat center;
  background-size: contain;
  transform: rotate(90deg);
}

.icon-play::before {
  content: '';
  display: inline-block;
  width: 28px;
  height: 28px;
  background: url('@/assets/star.svg') no-repeat center;
  background-size: contain;
}

.icon-pause::before {
  content: '';
  display: inline-block;
  width: 28px;
  height: 28px;
  background: url('@/assets/pause.svg') no-repeat center;
  background-size: contain;
}

.icon-prev::before {
  content: '';
  display: inline-block;
  width: 26px;
  height: 26px;
  background: url('@/assets/上一首.png') no-repeat center;
  background-size: contain;
}

.icon-next::before {
  content: '';
  display: inline-block;
  width: 26px;
  height: 26px;
  background: url('@/assets/下一首.png') no-repeat center;
  background-size: contain;
}

.icon-sequence::before {
  content: '';
  display: inline-block;
  width: 26px;
  height: 26px;
  background: url('@/assets/顺序播放.svg') no-repeat center;
  background-size: contain;
}

.icon-loop-one::before {
  content: '';
  display: inline-block;
  width: 26px;
  height: 26px;
  background: url('@/assets/循环播放.svg') no-repeat center;
  background-size: contain;
}

.icon-shuffle::before {
  content: '';
  display: inline-block;
  width: 26px;
  height: 26px;
  background: url('@/assets/随机播放.svg') no-repeat center;
  background-size: contain;
}

.icon-volume-high::before,
.icon-volume-medium::before,
.icon-volume-low::before,
.icon-volume-mute::before {
  content: '';
  display: inline-block;
  width: 26px;
  height: 26px;
  background: url('@/assets/声音.svg') no-repeat center;
  background-size: contain;
}

.icon-heart::before {
  content: '';
  display: inline-block;
  width: 18px;
  height: 18px;
  background: url('@/assets/heart.svg') no-repeat center;
  background-size: contain;
  filter: brightness(0) saturate(100%) invert(22%) sepia(98%) saturate(2098%) hue-rotate(210deg) brightness(95%) contrast(99%);
}

.icon-heart-filled::before {
  content: '';
  display: inline-block;
  width: 26px;
  height: 26px;
  background: url('@/assets/heart-filled.svg') no-repeat center;
  background-size: contain;
}

.icon-list::before {
  content: '';
  display: inline-block;
  width: 26px;
  height: 26px;
  background: url('@/assets/播放列表.svg') no-repeat center;
  background-size: contain;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .album-cover {
    width: 200px;
    height: 200px;
  }
  
  .song-title {
    font-size: 20px;
  }
  
  .player-controls-section {
    padding: 15px 20px 25px;
  }
  
  .single-control-row {
    gap: 12px;
  }
  
  .left-controls,
  .right-controls {
    gap: 8px;
    min-width: 60px;
  }
  
  .center-controls {
    gap: 16px;
  }
  
  .control-btn.small {
    width: 36px;
    height: 36px;
    font-size: 16px;
  }
  
  .control-btn.prev,
  .control-btn.next,
  .control-btn.love {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }
  
  .control-btn.play {
    width: 48px;
    height: 48px;
    font-size: 20px;
  }
  
  .time-display {
    font-size: 12px;
  }
}
/* 图标样式 - 使用assets中的SVG图标，增大尺寸 */
.icon-arrow-down::before {
  content: '';
  display: inline-block;
  width: 24px;
  height: 24px;
  background: url('@/assets/left.svg') no-repeat center;
  background-size: contain;
  transform: rotate(90deg);
}

.icon-share::before {
  content: '';
  display: inline-block;
  width: 20px;
  height: 20px;
  background: url('@/assets/展开.svg') no-repeat center;
  background-size: contain;
}

.icon-play::before {
  content: '';
  display: inline-block;
  width: 28px;
  height: 28px;
  background: url('@/assets/star.svg') no-repeat center;
  background-size: contain;
}

.icon-pause::before {
  content: '';
  display: inline-block;
  width: 28px;
  height: 28px;
  background: url('@/assets/pause.svg') no-repeat center;
  background-size: contain;
}

.icon-prev::before {
  content: '';
  display: inline-block;
  width: 26px;
  height: 26px;
  background: url('@/assets/上一首.png') no-repeat center;
  background-size: contain;
}

.icon-next::before {
  content: '';
  display: inline-block;
  width: 26px;
  height: 26px;
  background: url('@/assets/下一首.png') no-repeat center;
  background-size: contain;
}

.icon-sequence::before {
  content: '';
  display: inline-block;
  width: 26px;
  height: 26px;
  background: url('@/assets/顺序播放.svg') no-repeat center;
  background-size: contain;
}

.icon-loop-one::before {
  content: '';
  display: inline-block;
  width: 26px;
  height: 26px;
  background: url('@/assets/循环播放.svg') no-repeat center;
  background-size: contain;
}

.icon-shuffle::before {
  content: '';
  display: inline-block;
  width: 26px;
  height: 26px;
  background: url('@/assets/随机播放.svg') no-repeat center;
  background-size: contain;
}

.icon-volume-high::before,
.icon-volume-medium::before,
.icon-volume-low::before,
.icon-volume-mute::before {
  content: '';
  display: inline-block;
  width: 26px;
  height: 26px;
  background: url('@/assets/声音.svg') no-repeat center;
  background-size: contain;
}

.icon-heart::before {
  content: '';
  display: inline-block;
  width: 18px;
  height: 18px;
  background: url('@/assets/heart.svg') no-repeat center;
  background-size: contain;
  filter: brightness(0) saturate(100%) invert(22%) sepia(98%) saturate(2098%) hue-rotate(210deg) brightness(95%) contrast(99%);
}

.icon-heart-filled::before {
  content: '';
  display: inline-block;
  width: 26px;
  height: 26px;
  background: url('@/assets/heart-filled.svg') no-repeat center;
  background-size: contain;
}



.icon-list::before {
  content: '';
  display: inline-block;
  width: 26px;
  height: 26px;
  background: url('@/assets/播放列表.svg') no-repeat center;
  background-size: contain;
}


/* 全局样式：全屏播放器显示时隐藏其他元素 */
body.fullscreen-player-active {
  overflow: hidden !important;
}

/* 隐藏各种可能的回到顶部按钮 */
body.fullscreen-player-active [class*="back-to-top"],
body.fullscreen-player-active [class*="BackToTop"],
body.fullscreen-player-active [class*="scroll-top"],
body.fullscreen-player-active [class*="to-top"],
body.fullscreen-player-active [class*="backtop"],
body.fullscreen-player-active .el-backtop,
body.fullscreen-player-active .back-top,
body.fullscreen-player-active .scroll-to-top {
  display: none !important;
  visibility: hidden !important;
  opacity: 0 !important;
  pointer-events: none !important;
  z-index: -1 !important;
}

/* 隐藏其他可能的浮动元素（但保持播放器本身） */
body.fullscreen-player-active > *:not(.fullscreen-player) [style*="position: fixed"],
body.fullscreen-player-active > *:not(.fullscreen-player) [style*="position:fixed"] {
  z-index: 1 !important;
}

/* 保持全屏播放器的最高优先级 */
.fullscreen-player.is-visible {
  z-index: 99999 !important;
}
</style>