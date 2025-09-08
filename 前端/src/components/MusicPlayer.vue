<template>
  <div class="netease-player" :class="{ 'has-song': hasSong }" v-if="hasSong">
    <!-- 进度条 -->
    <div class="progress-container">
      <div 
        class="progress-bar" 
        @click="seekTo"
        ref="progressBarRef"
      >
        <div class="progress-bg"></div>
        <div 
          class="progress-loaded" 
          :style="{ width: `${progress}%` }"
        ></div>
        <div 
          class="progress-handle" 
          :style="{ left: `${progress}%` }"
          @mousedown="startDrag"
        ></div>
      </div>
    </div>
    
    <!-- 播放器主体 -->
    <div class="player-main">
      <!-- 左侧：歌曲信息 -->
      <div class="song-info" @click="openFullscreen">
        <div class="album-cover">
          <img 
            :src="currentSong?.cover || currentSong?.albumCover || '/src/assets/1音乐.png'" 
            :alt="currentSong?.name"
            @error="handleImageError"
            @load="handleImageLoad"
          />
        </div>
        
        <div class="song-details">
          <div class="song-info-line">
            <div class="song-title">{{ currentSong?.name }}</div>
            <div class="artist-name">{{ currentSong?.artist ? '- ' + currentSong.artist : '' }}</div>
          </div>
        </div>
      </div>
      
      <!-- 中间：播放控制 -->
      <div class="controls">
        <button class="control-btn prev" @click="playPrevious" :disabled="!hasPlaylist">
          <i class="icon-prev"></i>
        </button>
        
        <button 
          class="control-btn play" 
          @click="togglePlay"
          :class="{ 'playing': isPlaying }"
        >
          <i :class="isPlaying ? 'icon-pause' : 'icon-play'"></i>
        </button>
        
        <button class="control-btn next" @click="playNext" :disabled="!hasPlaylist">
          <i class="icon-next"></i>
        </button>
      </div>
      
      <!-- 右侧：功能按钮和音量 -->
      <div class="player-tools">
        <div class="time-info" v-if="hasSong">
          <span class="current">{{ currentTimeFormatted }}</span>
          <span class="divider">/</span>
          <span class="total">{{ durationFormatted }}</span>
        </div>
        
        <button class="tool-btn love" @click="toggleLove" :class="{ 'loved': isCurrentSongLiked }" v-if="hasSong">
          <i :class="isCurrentSongLiked ? 'icon-heart-filled' : 'icon-heart'"></i>
        </button>
        
        <button class="tool-btn mode" @click="togglePlayMode" :title="playModeText" v-if="hasSong">
          <i :class="getModeIcon()"></i>
        </button>
        
        <div class="volume-control" v-if="hasSong">
          <button 
            class="tool-btn volume" 
            @click="toggleVolumePanel" 
            @mouseenter="handleVolumeButtonEnter"
            @mouseleave="handleVolumeButtonLeave"
            ref="volumeButtonRef"
          >
            <i :class="getVolumeIcon()"></i>
          </button>
        </div>
        
        <button class="tool-btn playlist" @click="openPlaylist">
          <i class="icon-list"></i>
        </button>
      </div>
    </div>
    
    <!-- 音量弹出面板 -->
    <div 
      class="volume-popup" 
      v-if="showVolumePanel" 
      :style="volumePopupStyle"
      ref="volumePopupRef"
      @mouseenter="clearVolumeHideTimer"
      @mouseleave="startVolumeHideTimer"
    >
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
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { 
  // 播放状态
  isPlaying, isPaused, isLoading, currentTime, duration, volume, isMuted,
  // 歌曲和播放列表
  currentSong, currentIndex, playlist, playMode, playModeText,
  // 进度和时间
  progress, currentTimeFormatted, durationFormatted,
  // 播放控制
  togglePlay, playNext, playPrevious, togglePlayMode, seekToProgress, setVolumeLevel, toggleMute,
  // 计算属性
  hasSong, hasPlaylist
} from '@/utils/musicPlayer.js'
import { isSongLiked, toggleSongLike } from '@/utils/favoriteManager.js'

// 组件状态
const progressBarRef = ref(null)
const volumeSliderRef = ref(null)
const volumeButtonRef = ref(null)
const volumePopupRef = ref(null) // 添加音量弹窗引用
const isDraggingProgress = ref(false)
const isDraggingVolume = ref(false)
const showVolumePanel = ref(false)
const volumeHideTimer = ref(null) // 添加延时器引用

// 计算属性
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
    zIndex: 1001
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

// 事件处理
const openFullscreen = () => window.dispatchEvent(new CustomEvent('openFullscreenPlayer'))
const openPlaylist = () => window.dispatchEvent(new CustomEvent('openPlaylist'))
const handleImageError = (event) => { 
  // 防止循环加载错误
  if (event.target.src.includes('1音乐.png')) {
    event.target.style.display = 'none' // 隐藏图片元素
    return
  }
  event.target.src = '/src/assets/1音乐.png' 
}
const handleImageLoad = (event) => {
  event.target.style.display = 'block' // 显示图片元素
}
const toggleLove = async () => {
  if (!currentSong.value) return
  
  try {
    await toggleSongLike(currentSong.value)
  } catch (error) {
    console.error('切换收藏状态失败:', error)
  }
}

// 音量面板控制
const toggleVolumePanel = () => {
  showVolumePanel.value = !showVolumePanel.value
  if (showVolumePanel.value) {
    clearVolumeHideTimer()
  }
}

// 清除音量面板隐藏定时器
const clearVolumeHideTimer = () => {
  if (volumeHideTimer.value) {
    clearTimeout(volumeHideTimer.value)
    volumeHideTimer.value = null
  }
}

// 开始音量面板隐藏定时器
const startVolumeHideTimer = () => {
  clearVolumeHideTimer()
  volumeHideTimer.value = setTimeout(() => {
    showVolumePanel.value = false
  }, 800) // 800ms 后隐藏
}

// 音量按钮鼠标进入事件
const handleVolumeButtonEnter = () => {
  if (showVolumePanel.value) {
    clearVolumeHideTimer()
  }
}

// 音量按钮鼠标离开事件
const handleVolumeButtonLeave = () => {
  if (showVolumePanel.value) {
    startVolumeHideTimer()
  }
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
  
  // 拖拽时清除隐藏定时器
  clearVolumeHideTimer()
  
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
    
    // 拖拽结束后开始延时隐藏
    startVolumeHideTimer()
  }
  
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
}

// 生命周期钩子
onMounted(() => {
  // 点击外部关闭音量面板
  const handleClickOutside = (event) => {
    if (showVolumePanel.value && 
        volumeButtonRef.value && 
        volumePopupRef.value &&
        !volumeButtonRef.value.contains(event.target) &&
        !volumePopupRef.value.contains(event.target)) {
      showVolumePanel.value = false
      clearVolumeHideTimer()
    }
  }
  
  // 鼠标移动检测，当鼠标离开音量区域时自动隐藏
  const handleMouseMove = (event) => {
    if (!showVolumePanel.value) return
    
    const isInVolumeArea = (
      (volumeButtonRef.value && volumeButtonRef.value.contains(event.target)) ||
      (volumePopupRef.value && volumePopupRef.value.contains(event.target)) ||
      event.target.closest('.volume-control') ||
      event.target.closest('.volume-popup')
    )
    
    if (!isInVolumeArea && !isDraggingVolume.value) {
      startVolumeHideTimer()
    } else {
      clearVolumeHideTimer()
    }
  }
  
  document.addEventListener('click', handleClickOutside)
  document.addEventListener('mousemove', handleMouseMove, { passive: true })
  
  onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside)
    document.removeEventListener('mousemove', handleMouseMove)
    clearVolumeHideTimer()
  })
})
</script>

<style scoped>
/* 网易云音乐风格的简洁播放器 */
.netease-player {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 80px; /* 增加高度 */
  background: #fff;
  border-top: 1px solid #e7e7e7;
  z-index: 1000;
  transform: translateY(0);
  transition: transform 0.3s ease;
}

.netease-player:not(.has-song) {
  opacity: 1; /* 修改为完全不透明 */
}

/* 进度条 */
.progress-container {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  cursor: pointer;
}

.progress-bar {
  position: relative;
  width: 100%;
  height: 100%;
}

.progress-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 100%;
  background: #e7e7e7;
}

.progress-loaded {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: #666;
  transition: width 0.1s ease;
}

.progress-handle {
  position: absolute;
  top: 50%;
  width: 12px; /* 增大拖动手柄尺寸 */
  height: 12px; /* 增大拖动手柄尺寸 */
  background: #666;
  border-radius: 50%; /* 确保是圆形 */
  transform: translate(-50%, -50%);
  opacity: 0.5; /* 提高默认可见性 */
  transition: opacity 0.2s ease, transform 0.2s ease;
  cursor: grab;
  box-shadow: 0 0 4px rgba(0, 0, 0, 0.2); /* 添加阴影效果 */
}

.progress-handle:active {
  cursor: grabbing;
  transform: translate(-50%, -50%) scale(1.2); /* 拖动时放大效果 */
}

.progress-container:hover .progress-handle {
  opacity: 1;
  box-shadow: 0 0 6px rgba(0, 0, 0, 0.3); /* 悬停时增强阴影 */
}

/* 播放器主体 */
.player-main {
  display: flex;
  align-items: center;
  height: 80px; /* 增加高度匹配播放器高度 */
  padding: 0 24px; /* 增加内边距 */
  justify-content: space-between;
}

.player-main > * {
  flex-shrink: 0;
}

.player-main .song-info {
  flex-shrink: 1;
  min-width: 0;
}

/* 歌曲信息 */
.song-info {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
  max-width: 350px; /* 增加最大宽度 */
  cursor: pointer;
  padding: 6px 8px;
  border-radius: 6px;
  transition: background-color 0.2s ease;
  /* 确保头像在最左边，歌曲信息在右边 */
  flex-direction: row;
}

.song-info:hover {
  background: #f8f8f8;
}

.album-cover {
  width: 56px; /* 增大专辑封面尺寸 */
  height: 56px; /* 增大专辑封面尺寸 */
  border-radius: 8px; /* 增大圆角 */
  overflow: hidden;
  flex-shrink: 0;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.15); /* 增强阴影效果 */
}

.album-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: relative;
  z-index: 2;
}

.song-details {
  min-width: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.song-info-line {
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
  overflow: hidden;
  width: 100%;
  text-overflow: ellipsis;
  /* 确保歌曲名和歌手名在同一行 */
  flex-wrap: nowrap;
}

.song-title {
  color: #333;
  font-size: 16px; /* 增大字体尺寸 */
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.3; /* 增大行高 */
  flex-shrink: 0;
  max-width: 60%; /* 减小最大宽度，为艺术家名称留出空间 */
}

.artist-name {
  color: #666;
  font-size: 14px; /* 增大字体尺寸 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.3; /* 增大行高 */
  opacity: 0.9;
  flex-shrink: 1;
  min-width: 0;
  max-width: 40%; /* 设置最大宽度 */
}

/* 播放控制 */
.controls {
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: center;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.control-btn {
  background: none;
  border: none;
  border-radius: 0;
  color: #333;
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
  opacity: 0.6; /* 提高禁用状态的不透明度 */
  cursor: not-allowed;
}

.control-btn:not(:disabled):hover {
  background: #f5f5f5;
}

.prev, .next {
  width: 40px; /* 增大按钮尺寸 */
  height: 40px; /* 增大按钮尺寸 */
  font-size: 16px; /* 增大图标尺寸 */
}

.love {
  width: 40px; /* 增大按钮尺寸 */
  height: 40px; /* 增大按钮尺寸 */
  font-size: 16px; /* 增大图标尺寸 */
  color: #999;
}

.love.loved {
  color: #d33a31;
}

.love:hover {
  background: #f5f5f5;
}

.play {
  width: 40px; /* 与相邻按钮保持一致 */
  height: 40px; /* 与相邻按钮保持一致 */
  font-size: 22px; /* 保持图标尺寸 */
  background: none; /* 去掉背景 */
  color: #333;
  /* 去掉所有边框和阴影效果 */
  border-radius: 0;
  box-shadow: none !important;
  -webkit-box-shadow: none !important;
  -moz-box-shadow: none !important;
}

.play:hover {
  background: #f5f5f5;
  /* 去掉阴影效果，保持与相邻按钮一致的悬停效果 */
  box-shadow: none !important;
  -webkit-box-shadow: none !important;
  -moz-box-shadow: none !important;
  transform: none; /* 去掉放大效果 */
}

/* 播放器工具 */
.player-tools {
  display: flex;
  align-items: center;
  gap: 16px; /* 增大间距 */
  justify-content: flex-end;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #999;
  font-size: 14px; /* 增大字体尺寸 */
  font-variant-numeric: tabular-nums;
}

.divider {
  opacity: 0.5;
}

.tool-btn {
  background: none;
  border: none;
  border-radius: 0;
  color: #666;
  cursor: pointer;
  width: 40px; /* 增大按钮尺寸 */
  height: 40px; /* 增大按钮尺寸 */
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  font-size: 16px; /* 增大图标尺寸 */
  position: relative;
  outline: none;
  box-shadow: none;
}

.tool-btn:focus {
  outline: none;
  box-shadow: none;
}

.tool-btn:hover {
  color: #333;
  background: #f5f5f5;
}

/* 右侧功能区域的喜欢按钮样式 */
.tool-btn.love {
  color: #999;
}

.tool-btn.love.loved {
  color: #d33a31;
}

.tool-btn.love:hover {
  background: #f5f5f5;
}



/* 音量控制 */
.volume-control {
  display: flex;
  align-items: center;
  gap: 8px;
}

.volume-slider {
  width: 80px;
  height: 3px;
  position: relative;
  cursor: pointer;
}

.volume-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 100%;
  background: #e7e7e7;
  border-radius: 2px;
}

.volume-fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: #666;
  border-radius: 2px;
  transition: width 0.1s ease;
}

.volume-handle {
  position: absolute;
  top: 50%;
  width: 8px;
  height: 8px;
  background: #666;
  transform: translate(-50%, -50%);
  opacity: 0;
  transition: opacity 0.2s ease;
  cursor: grab;
}

.volume-handle:active {
  cursor: grabbing;
}

.volume-slider:hover .volume-handle {
  opacity: 1;
}

/* 动画 */
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 图标样式 */
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
  width: 22px;
  height: 22px;
  background: url('@/assets/上一首.png') no-repeat center;
  background-size: contain;
}

.icon-next::before {
  content: '';
  display: inline-block;
  width: 22px;
  height: 22px;
  background: url('@/assets/下一首.png') no-repeat center;
  background-size: contain;
}

.icon-sequence::before {
  content: '';
  display: inline-block;
  width: 20px;
  height: 20px;
  background: url('@/assets/顺序播放.svg') no-repeat center;
  background-size: contain;
}

.icon-loop-one::before {
  content: '';
  display: inline-block;
  width: 20px;
  height: 20px;
  background: url('@/assets/循环播放.svg') no-repeat center;
  background-size: contain;
}

.icon-shuffle::before {
  content: '';
  display: inline-block;
  width: 20px;
  height: 20px;
  background: url('@/assets/随机播放.svg') no-repeat center;
  background-size: contain;
}

.icon-volume-high::before,
.icon-volume-medium::before,
.icon-volume-low::before,
.icon-volume-mute::before {
  content: '';
  display: inline-block;
  width: 18px;
  height: 18px;
  background: url('@/assets/声音.svg') no-repeat center;
  background-size: contain;
}

.icon-heart::before,
.icon-heart-fill::before {
  content: '';
  display: inline-block;
  width: 16px;
  height: 16px;
  background: url('@/assets/heart.svg') no-repeat center;
  background-size: contain;
}

.icon-heart-filled::before {
  content: '';
  display: inline-block;
  width: 16px;
  height: 16px;
  background: url('@/assets/heart-filled.svg') no-repeat center;
  background-size: contain;
}

.icon-list::before {
  content: '';
  display: inline-block;
  width: 18px;
  height: 18px;
  background: url('@/assets/播放列表.svg') no-repeat center;
  background-size: contain;
}

.icon-expand::before {
  content: '';
  display: inline-block;
  width: 18px;
  height: 18px;
  background: url('@/assets/展开.svg') no-repeat center;
  background-size: contain;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .player-main {
    padding: 0 12px;
    gap: 12px;
  }
  
  /* 保留歌曲详情显示 */
  .song-details {
    /* 不再隐藏歌曲详情 */
    max-width: 120px;
    overflow: hidden;
    /* 确保在小屏幕上也能正确显示 */
    flex-shrink: 1;
    min-width: 0;
  }
  
  .time-info {
    display: none;
  }
  
  .volume-control {
    display: none;
  }
}
/* 图标样式 - 使用assets中的图标，优化尺寸 */
.icon-play::before {
  content: '';
  display: inline-block;
  width: 24px; /* 放大图标 */
  height: 24px; /* 放大图标 */
  background: url('@/assets/star.svg') no-repeat center;
  background-size: contain;
}

.icon-pause::before {
  content: '';
  display: inline-block;
  width: 24px; /* 放大图标 */
  height: 24px; /* 放大图标 */
  background: url('@/assets/pause.svg') no-repeat center;
  background-size: contain;
}

.icon-prev::before {
  content: '';
  display: inline-block;
  width: 20px;
  height: 20px;
  background: url('@/assets/上一首.png') no-repeat center;
  background-size: contain;
}

.icon-next::before {
  content: '';
  display: inline-block;
  width: 20px;
  height: 20px;
  background: url('@/assets/下一首.png') no-repeat center;
  background-size: contain;
}

.icon-sequence::before {
  content: '';
  display: inline-block;
  width: 18px;
  height: 18px;
  background: url('@/assets/顺序播放.svg') no-repeat center;
  background-size: contain;
}

.icon-loop-one::before {
  content: '';
  display: inline-block;
  width: 18px;
  height: 18px;
  background: url('@/assets/循环播放.svg') no-repeat center;
  background-size: contain;
}

.icon-shuffle::before {
  content: '';
  display: inline-block;
  width: 18px;
  height: 18px;
  background: url('@/assets/随机播放.svg') no-repeat center;
  background-size: contain;
}

.icon-volume-high::before,
.icon-volume-medium::before,
.icon-volume-low::before,
.icon-volume-mute::before {
  content: '';
  display: inline-block;
  width: 20px;
  height: 20px;
  background: url('@/assets/声音.svg') no-repeat center;
  background-size: contain;
}

.icon-heart::before,
.icon-heart-fill::before {
  content: '';
  display: inline-block;
  width: 18px;
  height: 18px;
  background: url('@/assets/heart.svg') no-repeat center;
  background-size: contain;
}

.icon-heart-filled::before {
  content: '';
  display: inline-block;
  width: 18px;
  height: 18px;
  background: url('@/assets/已收藏.png') no-repeat center;
  background-size: contain;
}

.icon-list::before {
  content: '';
  display: inline-block;
  width: 20px;
  height: 20px;
  background: url('@/assets/播放列表.svg') no-repeat center;
  background-size: contain;
}

/* 加载动画 */
.loading-icon {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(211, 58, 49, 0.3);
  border-top: 2px solid #d33a31;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

/* 音量弹出面板 */
.volume-popup {
  position: fixed;
  z-index: 1001;
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

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>