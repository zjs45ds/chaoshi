// 音乐播放器组件
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
    <div class="player-main" @click="handlePlayerMainClick">
      <!-- 左侧：歌曲信息 -->
      <div class="song-info" @click.stop="openFullscreen">
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
      <div class="controls" @click.stop>
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
      <div class="player-tools" @click.stop>
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

// 处理播放器主体区域点击事件
const handlePlayerMainClick = (event) => {
  // 检查点击的目标元素是否是按钮或其子元素
  const clickedElement = event.target
  
  // 如果点击的是按钮、输入控件或其子元素，则不执行跳转
  if (
    clickedElement.closest('button') ||
    clickedElement.closest('.controls') ||
    clickedElement.closest('.player-tools') ||
    clickedElement.closest('.song-info') ||
    clickedElement.closest('.progress-container') ||
    clickedElement.classList.contains('progress-bar') ||
    clickedElement.classList.contains('progress-handle')
  ) {
    return // 不执行跳转
  }
  
  // 如果点击的是空白区域，跳转到全屏播放器
  openFullscreen()
}
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
    // CONSOLE LOG REMOVED: console.error('切换收藏状态失败:', error)
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
  transition: transform 0.3s ease, background 0.3s ease, border-color 0.3s ease;
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
  padding: 6px 8px;
  border-radius: 6px;
  /* 确保头像在最左边，歌曲信息在右边 */
  flex-direction: row;
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
  backdrop-filter: blur(20px);
  padding: 16px 12px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
  animation: volumePopupIn 0.3s cubic-bezier(0.4, 0, 0.2, 1);
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
  gap: 12px;
  min-height: 100px;
}

/* 垂直音量滑块 */
.volume-slider-vertical {
  width: 6px;
  height: 100px;
  position: relative;
  cursor: pointer;
  flex: 1;
  border-radius: 3px;
  overflow: hidden;
}

.volume-bg-vertical {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.volume-fill-vertical {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, #60a5fa, #3b82f6);
  transition: height 0.1s ease;
  min-height: 3px;
  border-radius: 3px;
  box-shadow: 0 0 10px rgba(96, 165, 250, 0.5);
}

.volume-handle-vertical {
  position: absolute;
  left: 50%;
  width: 16px;
  height: 16px;
  background: white;
  border-radius: 50%;
  transform: translate(-50%, 50%);
  opacity: 0;
  transition: all 0.2s ease;
  cursor: grab;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.volume-handle-vertical:active {
  cursor: grabbing;
  transform: translate(-50%, 50%) scale(1.2);
}

.volume-slider-vertical:hover .volume-handle-vertical {
  opacity: 1;
}

.volume-text-popup {
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  min-width: 30px;
  text-align: center;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 黑色主题下的播放器样式 */
[data-theme="black"] .netease-player {
  background: #000000 !important;
  border-top: 1px solid #333333 !important;
}


[data-theme="black"] .album-cover {
  background: #1a1a1a !important;
}

[data-theme="black"] .song-title {
  color: white !important;
}

[data-theme="black"] .artist-name {
  color: #cccccc !important;
}

[data-theme="black"] .control-btn {
  color: white !important;
}

[data-theme="black"] .control-btn:not(:disabled):hover {
  background: #1a1a1a !important;
}

[data-theme="black"] .play:hover {
  background: #1a1a1a !important;
}

[data-theme="black"] .time-info {
  color: #cccccc !important;
}

[data-theme="black"] .tool-btn {
  color: #cccccc !important;
}

[data-theme="black"] .tool-btn:hover {
  color: white !important;
  background: #1a1a1a !important;
}

[data-theme="black"] .tool-btn.love {
  color: #cccccc !important;
}

/* 黑色主题下的爱心图标 - 使用 filter 反转颜色使其可见 */
[data-theme="black"] .tool-btn.love .icon-heart::before {
  filter: invert(1) brightness(0.8) !important;
}

[data-theme="black"] .tool-btn.love.loved {
  color: #dc2626 !important;
}

/* 已收藏状态保持红色 */
[data-theme="black"] .tool-btn.love.loved .icon-heart-filled::before {
  filter: none !important;
}

[data-theme="black"] .tool-btn.love:hover {
  background: #1a1a1a !important;
}

[data-theme="black"] .progress-bg {
  background: #333333 !important;
}

[data-theme="black"] .progress-loaded {
  background: #cccccc !important;
}

[data-theme="black"] .progress-handle {
  background: #cccccc !important;
}

[data-theme="black"] .volume-bg {
  background: #333333 !important;
}

[data-theme="black"] .volume-fill {
  background: #cccccc !important;
}

[data-theme="black"] .volume-handle {
  background: #cccccc !important;
}

[data-theme="black"] .volume-popup {
  background: rgba(0, 0, 0, 0.9) !important;
}

[data-theme="black"] .volume-bg-vertical {
  background: rgba(255, 255, 255, 0.2) !important;
}

[data-theme="black"] .volume-fill-vertical {
  background: #cccccc !important;
}

[data-theme="black"] .volume-handle-vertical {
  background: #cccccc !important;
}

[data-theme="black"] .volume-text-popup {
  color: #cccccc !important;
}

/* 粉色主题下的播放器样式 */
[data-theme="pink"] .netease-player {
  background: #f9a8d4 !important;
  border-top: 1px solid #ec4899 !important;
  box-shadow: 0 -2px 10px rgba(236, 72, 153, 0.1) !important;
}

[data-theme="pink"] .album-cover {
  background: #fce7f3 !important;
}

[data-theme="pink"] .song-title {
  color: #831843 !important;
}

[data-theme="pink"] .artist-name {
  color: #be185d !important;
}

[data-theme="pink"] .control-btn {
  color: #be185d !important;
}

[data-theme="pink"] .control-btn:not(:disabled):hover {
  background: #fce7f3 !important;
}

[data-theme="pink"] .play:hover {
  background: #fce7f3 !important;
}

[data-theme="pink"] .time-info {
  color: #be185d !important;
}

[data-theme="pink"] .tool-btn {
  color: #be185d !important;
}

[data-theme="pink"] .tool-btn:hover {
  color: #831843 !important;
  background: #fce7f3 !important;
}

[data-theme="pink"] .tool-btn.love {
  color: #be185d !important;
}

[data-theme="pink"] .tool-btn.love.loved {
  color: #dc2626 !important;
}

[data-theme="pink"] .tool-btn.love:hover {
  background: #fce7f3 !important;
}

[data-theme="pink"] .progress-bg {
  background: #f9a8d4 !important;
}

[data-theme="pink"] .progress-loaded {
  background: #f66e85 !important;
}

[data-theme="pink"] .progress-handle {
  background: #f66e85 !important;
}

[data-theme="pink"] .volume-bg {
  background: #f9a8d4 !important;
}

[data-theme="pink"] .volume-fill {
  background: #f66e85 !important;
}

[data-theme="pink"] .volume-handle {
  background: #be185d !important;
}

[data-theme="pink"] .volume-popup {
  background: rgba(244, 114, 182, 0.9) !important;
}

[data-theme="pink"] .volume-bg-vertical {
  background: rgba(255, 255, 255, 0.3) !important;
}

[data-theme="pink"] .volume-fill-vertical {
  background: #ffffff !important;
}

[data-theme="pink"] .volume-handle-vertical {
  background: #ffffff !important;
}

[data-theme="pink"] .volume-text-popup {
  color: #ffffff !important;
}

/* 浅粉色主题下的播放器样式 */
[data-theme="lightPink"] .netease-player.netease-player {
  background: #f7b9c8 !important;
  border-top: 1px solid #ff8eaa !important;
  box-shadow: 0 -2px 10px rgba(247, 185, 200, 0.15) !important;
}

[data-theme="lightPink"] .album-cover {
  background: #fa7da0 !important;
}

[data-theme="lightPink"] .song-title {
  color: #4a1e2b !important;
}

[data-theme="lightPink"] .artist-name {
  color: #7d4a5a !important;
}

[data-theme="lightPink"] .control-btn {
  color: #7d4a5a !important;
}

[data-theme="lightPink"] .control-btn:not(:disabled):hover {
  background: #fce7ed !important;
}

[data-theme="lightPink"] .play:hover {
  background: #fce7ed !important;
}

[data-theme="lightPink"] .time-info {
  color: #7d4a5a !important;
}

[data-theme="lightPink"] .tool-btn {
  color: #7d4a5a !important;
}

[data-theme="lightPink"] .tool-btn:hover {
  color: #4a1e2b !important;
  background: #fce7ed !important;
}

[data-theme="lightPink"] .tool-btn.love {
  color: #7d4a5a !important;
}

[data-theme="lightPink"] .tool-btn.love.loved {
  color: #dc2626 !important;
}

[data-theme="lightPink"] .tool-btn.love:hover {
  background: #fce7ed !important;
}

[data-theme="lightPink"] .progress-bg {
  background: #f7d4dd !important;
}

[data-theme="lightPink"] .progress-loaded {
  background: #f66e85 !important;
}

[data-theme="lightPink"] .progress-handle {
  background: #f66e85 !important;
}

[data-theme="lightPink"] .volume-bg {
  background: #f7d4dd !important;
}

[data-theme="lightPink"] .volume-fill {
  background: #f66e85 !important;
}

[data-theme="lightPink"] .volume-handle {
  background: #f7b9c8 !important;
}

[data-theme="lightPink"] .volume-popup {
  background: rgba(247, 185, 200, 0.9) !important;
}

[data-theme="lightPink"] .volume-bg-vertical {
  background: rgba(255, 255, 255, 0.3) !important;
}

[data-theme="lightPink"] .volume-fill-vertical {
  background: #ffffff !important;
}

[data-theme="lightPink"] .volume-handle-vertical {
  background: #ffffff !important;
}

[data-theme="lightPink"] .volume-text-popup {
  color: #ffffff !important;
}

/* 蓝色主题下的播放器样式 */
[data-theme="blue"] .netease-player {
  background: #93c5fd !important;
  border-top: 1px solid #3b82f6 !important;
  box-shadow: 0 -2px 10px rgba(59, 130, 246, 0.1) !important;
}

[data-theme="blue"] .album-cover {
  background: #dbeafe !important;
}

[data-theme="blue"] .song-title {
  color: #1e3a8a !important;
}

[data-theme="blue"] .artist-name {
  color: #2563eb !important;
}

[data-theme="blue"] .control-btn {
  color: #2563eb !important;
}

[data-theme="blue"] .control-btn:not(:disabled):hover {
  background: #dbeafe !important;
}

[data-theme="blue"] .play:hover {
  background: #dbeafe !important;
}

[data-theme="blue"] .time-info {
  color: #2563eb !important;
}

[data-theme="blue"] .tool-btn {
  color: #2563eb !important;
}

[data-theme="blue"] .tool-btn:hover {
  color: #1e3a8a !important;
  background: #dbeafe !important;
}

[data-theme="blue"] .tool-btn.love {
  color: #2563eb !important;
}

[data-theme="blue"] .tool-btn.love.loved {
  color: #dc2626 !important;
}

[data-theme="blue"] .tool-btn.love:hover {
  background: #dbeafe !important;
}

[data-theme="blue"] .progress-bg {
  background: #93c5fd !important;
}

[data-theme="blue"] .progress-loaded {
  background: #2563eb !important;
}

[data-theme="blue"] .progress-handle {
  background: #2563eb !important;
}

[data-theme="blue"] .volume-bg {
  background: #93c5fd !important;
}

[data-theme="blue"] .volume-fill {
  background: #2563eb !important;
}

[data-theme="blue"] .volume-handle {
  background: #2563eb !important;
}

[data-theme="blue"] .volume-popup {
  background: rgba(59, 130, 246, 0.9) !important;
}

[data-theme="blue"] .volume-bg-vertical {
  background: rgba(255, 255, 255, 0.3) !important;
}

[data-theme="blue"] .volume-fill-vertical {
  background: #ffffff !important;
}

[data-theme="blue"] .volume-handle-vertical {
  background: #ffffff !important;
}

[data-theme="blue"] .volume-text-popup {
  color: #ffffff !important;
}

/* 绿色主题下的播放器样式 */
[data-theme="green"] .netease-player {
  background: #8dffb6 !important;
  border-top: 1px solid #10b981 !important;
  box-shadow: 0 -2px 10px rgba(16, 185, 129, 0.1) !important;
}

[data-theme="green"] .album-cover {
  background: #dcfce7 !important;
}

[data-theme="green"] .song-title {
  color: #064e3b !important;
}

[data-theme="green"] .artist-name {
  color: #059669 !important;
}

[data-theme="green"] .control-btn {
  color: #059669 !important;
}

[data-theme="green"] .control-btn:not(:disabled):hover {
  background: #dcfce7 !important;
}

[data-theme="green"] .play:hover {
  background: #dcfce7 !important;
}

[data-theme="green"] .time-info {
  color: #059669 !important;
}

[data-theme="green"] .tool-btn {
  color: #059669 !important;
}

[data-theme="green"] .tool-btn:hover {
  color: #064e3b !important;
  background: #dcfce7 !important;
}

[data-theme="green"] .tool-btn.love {
  color: #059669 !important;
}

[data-theme="green"] .tool-btn.love.loved {
  color: #dc2626 !important;
}

[data-theme="green"] .tool-btn.love:hover {
  background: #dcfce7 !important;
}

[data-theme="green"] .progress-bg {
  background: #86efac !important;
}

[data-theme="green"] .progress-loaded {
  background: #059669 !important;
}

[data-theme="green"] .progress-handle {
  background: #059669 !important;
}

[data-theme="green"] .volume-bg {
  background: #86efac !important;
}

[data-theme="green"] .volume-fill {
  background: #059669 !important;
}

[data-theme="green"] .volume-handle {
  background: #059669 !important;
}

[data-theme="green"] .volume-popup {
  background: rgba(16, 185, 129, 0.9) !important;
}

[data-theme="green"] .volume-bg-vertical {
  background: rgba(255, 255, 255, 0.3) !important;
}

[data-theme="green"] .volume-fill-vertical {
  background: #ffffff !important;
}

[data-theme="green"] .volume-handle-vertical {
  background: #ffffff !important;
}

[data-theme="green"] .volume-text-popup {
  color: #ffffff !important;
}

/* 紫色主题下的播放器样式 */
[data-theme="purple"] .netease-player {
  background: #c2a0d9 !important;
  border-top: 1px solid #8b5cf6 !important;
  box-shadow: 0 -2px 10px rgba(139, 92, 246, 0.1) !important;
}

[data-theme="purple"] .album-cover {
  background: #f3e8ff !important;
}

[data-theme="purple"] .song-title {
  color: #4c1d95 !important;
}

[data-theme="purple"] .artist-name {
  color: #7c3aed !important;
}

[data-theme="purple"] .control-btn {
  color: #7c3aed !important;
}

[data-theme="purple"] .control-btn:not(:disabled):hover {
  background: #f3e8ff !important;
}

[data-theme="purple"] .play:hover {
  background: #f3e8ff !important;
}

[data-theme="purple"] .time-info {
  color: #7c3aed !important;
}

[data-theme="purple"] .tool-btn {
  color: #7c3aed !important;
}

[data-theme="purple"] .tool-btn:hover {
  color: #4c1d95 !important;
  background: #f3e8ff !important;
}

[data-theme="purple"] .tool-btn.love {
  color: #7c3aed !important;
}

[data-theme="purple"] .tool-btn.love.loved {
  color: #dc2626 !important;
}

[data-theme="purple"] .tool-btn.love:hover {
  background: #f3e8ff !important;
}

[data-theme="purple"] .progress-bg {
  background: #c4b5fd !important;
}

[data-theme="purple"] .progress-loaded {
  background: #7c3aed !important;
}

[data-theme="purple"] .progress-handle {
  background: #7c3aed !important;
}

[data-theme="purple"] .volume-bg {
  background: #c4b5fd !important;
}

[data-theme="purple"] .volume-fill {
  background: #7c3aed !important;
}

[data-theme="purple"] .volume-handle {
  background: #7c3aed !important;
}

[data-theme="purple"] .volume-popup {
  background: rgba(139, 92, 246, 0.9) !important;
}

[data-theme="purple"] .volume-bg-vertical {
  background: rgba(255, 255, 255, 0.3) !important;
}

[data-theme="purple"] .volume-fill-vertical {
  background: #ffffff !important;
}

[data-theme="purple"] .volume-handle-vertical {
  background: #ffffff !important;
}

[data-theme="purple"] .volume-text-popup {
  color: #ffffff !important;
}

/* 橙色主题下的播放器样式 */
[data-theme="orange"] .netease-player {
  background: #fdba74 !important;
  border-top: 1px solid #f97316 !important;
  box-shadow: 0 -2px 10px rgba(249, 115, 22, 0.1) !important;
}

[data-theme="orange"] .album-cover {
  background: #fed7aa !important;
}

[data-theme="orange"] .song-title {
  color: #7c2d12 !important;
}

[data-theme="orange"] .artist-name {
  color: #ea580c !important;
}

[data-theme="orange"] .control-btn {
  color: #ea580c !important;
}

[data-theme="orange"] .control-btn:not(:disabled):hover {
  background: #fed7aa !important;
}

[data-theme="orange"] .play:hover {
  background: #fed7aa !important;
}

[data-theme="orange"] .time-info {
  color: #ea580c !important;
}

[data-theme="orange"] .tool-btn {
  color: #ea580c !important;
}

[data-theme="orange"] .tool-btn:hover {
  color: #7c2d12 !important;
  background: #fed7aa !important;
}

[data-theme="orange"] .tool-btn.love {
  color: #ea580c !important;
}

[data-theme="orange"] .tool-btn.love.loved {
  color: #dc2626 !important;
}

[data-theme="orange"] .tool-btn.love:hover {
  background: #fed7aa !important;
}

[data-theme="orange"] .progress-bg {
  background: #fdba74 !important;
}

[data-theme="orange"] .progress-loaded {
  background: #ea580c !important;
}

[data-theme="orange"] .progress-handle {
  background: #ea580c !important;
}

[data-theme="orange"] .volume-bg {
  background: #fdba74 !important;
}

[data-theme="orange"] .volume-fill {
  background: #ea580c !important;
}

[data-theme="orange"] .volume-handle {
  background: #ea580c !important;
}

[data-theme="orange"] .volume-popup {
  background: rgba(249, 115, 22, 0.9) !important;
}

[data-theme="orange"] .volume-bg-vertical {
  background: rgba(255, 255, 255, 0.3) !important;
}

[data-theme="orange"] .volume-fill-vertical {
  background: #ffffff !important;
}

[data-theme="orange"] .volume-handle-vertical {
  background: #ffffff !important;
}

[data-theme="orange"] .volume-text-popup {
  color: #ffffff !important;
}

/* 红色主题下的播放器样式 */
[data-theme="red"] .netease-player {
  background: var(--background-light) !important;
  border-top: 1px solid var(--border) !important;
  box-shadow: 0 2px 16px rgba(255, 44, 44, 0.211) !important;
}

[data-theme="red"] .album-cover {
  background: #ff5454 !important;
  border: 2px solid transparent !important;
  box-shadow: 0 6px 16px rgba(255, 79, 79, 0.15) !important;
  transition: all 0.3s ease !important;
}

[data-theme="red"] .album-cover:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 24px rgba(255, 79, 79, 0.2) !important;
}

[data-theme="red"] .song-title {
  color: var(--text-primary) !important;
  font-weight: 600 !important;
  letter-spacing: 0.3px !important;
}

[data-theme="red"] .artist-name {
  color: var(--text-secondary) !important;
  font-weight: 400 !important;
}

[data-theme="red"] .control-btn {
  color: var(--text-secondary) !important;
  background: transparent !important;
  border-radius: 4px !important;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

[data-theme="red"] .control-btn:not(:disabled):hover {
  color: var(--primary) !important;
  background: rgba(255, 79, 79, 0.1) !important;
  transform: scale(1.08) !important;
}

[data-theme="red"] .play {
  color: var(--text-secondary) !important;
  background: transparent !important;
  box-shadow: none !important;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1) !important;
  border-radius: 4px !important;
}

[data-theme="red"] .play:hover {
  color: var(--primary) !important;
  background: rgba(255, 79, 79, 0.1) !important;
  transform: scale(1.08) !important;
  box-shadow: none !important;
}

[data-theme="red"] .time-info {
  color: var(--text-tertiary) !important;
  font-size: 12px !important;
  font-weight: 500 !important;
}

[data-theme="red"] .tool-btn {
  color: var(--text-secondary) !important;
  border-radius: 4px !important;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1) !important;
  background: transparent !important;
}

[data-theme="red"] .tool-btn:hover {
  color: var(--primary) !important;
  background: rgba(255, 79, 79, 0.1) !important;
  transform: scale(1.05) !important;
}

[data-theme="red"] .tool-btn.love {
  color: var(--primary) !important;
}

[data-theme="red"] .tool-btn.love.loved {
  color: var(--primary) !important;
  animation: heartBeat 0.5s ease-in-out;
  background: rgba(255, 79, 79, 0.1) !important;
}

/* 红色主题下的进度条样式 */
[data-theme="red"] .progress-bg {
  background-color: rgba(0, 0, 0, 0.06) !important;
  height: 4px !important;
  border-radius: 2px !important;
}

[data-theme="red"] .progress-loaded {
  background-color: rgba(255, 79, 79, 0.3) !important;
  border-radius: 2px !important;
}

[data-theme="red"] .progress-handle {
  background-color: var(--primary) !important;
  border: 3px solid #ffffff !important;
  box-shadow: 0 2px 8px rgba(255, 79, 79, 0.3) !important;
  transform: scale(1.1) !important;
  transition: all 0.2s ease !important;
}

[data-theme="red"] .progress-handle:hover {
  transform: scale(1.2) !important;
  box-shadow: 0 2px 12px rgba(255, 79, 79, 0.4) !important;
}

/* 红色主题下的音量控制样式 */
[data-theme="red"] .volume-bg {
  background-color: rgba(0, 0, 0, 0.06) !important;
  height: 4px !important;
  border-radius: 2px !important;
}

[data-theme="red"] .volume-fill {
  background-color: var(--primary) !important;
}

[data-theme="red"] .volume-handle {
  background-color: var(--primary) !important;
  border: 3px solid #ffffff !important;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15) !important;
}

[data-theme="red"] .volume-popup {
  background-color: var(--background-card) !important;
  border: 1px solid var(--border) !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}

[data-theme="red"] .volume-bg-vertical {
  background-color: rgba(0, 0, 0, 0.08) !important;
}

[data-theme="red"] .volume-fill-vertical {
  background-color: var(--primary) !important;
}

[data-theme="red"] .volume-handle-vertical {
  background-color: var(--primary) !important;
  border: 3px solid #ffffff !important;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15) !important;
}

[data-theme="red"] .volume-text-popup {
  color: var(--text-primary) !important;
}

/* 红色主题下的额外精致效果 */
[data-theme="red"] .netease-player {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

[data-theme="red"] .album-cover {
  transition: all 0.3s ease;
}

[data-theme="red"] .album-cover:hover {
  transform: rotate(2deg) scale(1.02);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12) !important;
}

[data-theme="red"] .progress-handle,
[data-theme="red"] .volume-handle,
[data-theme="red"] .volume-handle-vertical {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

[data-theme="red"] .progress-handle:hover,
[data-theme="red"] .volume-handle:hover,
[data-theme="red"] .volume-handle-vertical:hover {
  transform: scale(1.2);
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.2) !important;
}

@keyframes heartBeat {
  0% { transform: scale(1); }
  14% { transform: scale(1.2); }
  28% { transform: scale(1); }
  42% { transform: scale(1.2); }
  70% { transform: scale(1); }
}

[data-theme="red"] .tool-btn.love.loved {
  animation: heartBeat 0.5s ease-in-out;
}

/* 黄色主题下的播放器样式 */
[data-theme="yellow"] .netease-player {
  background: #fefce8 !important;
  border-top: 1px solid #eab308 !important;
  box-shadow: 0 -2px 10px rgba(234, 179, 8, 0.1) !important;
}

[data-theme="yellow"] .album-cover {
  background: #fef3c7 !important;
}

[data-theme="yellow"] .song-title {
  color: #713f12 !important;
}

[data-theme="yellow"] .artist-name {
  color: #a16207 !important;
}

[data-theme="yellow"] .control-btn {
  color: #a16207 !important;
}

[data-theme="yellow"] .control-btn:not(:disabled):hover {
  background: #fef3c7 !important;
}

[data-theme="yellow"] .play:hover {
  background: #fef3c7 !important;
}

[data-theme="yellow"] .time-info {
  color: #a16207 !important;
}

[data-theme="yellow"] .tool-btn {
  color: #a16207 !important;
}

[data-theme="yellow"] .tool-btn:hover {
  color: #713f12 !important;
  background: #fef3c7 !important;
}

[data-theme="yellow"] .tool-btn.love {
  color: #a16207 !important;
}

[data-theme="yellow"] .tool-btn.love.loved {
  color: #dc2626 !important;
}

[data-theme="yellow"] .tool-btn.love:hover {
  background: #fef3c7 !important;
}

[data-theme="yellow"] .progress-bg {
  background: #fde047 !important;
}

[data-theme="yellow"] .progress-loaded {
  background: #a16207 !important;
}

[data-theme="yellow"] .progress-handle {
  background: #a16207 !important;
}

[data-theme="yellow"] .volume-bg {
  background: #fde047 !important;
}

[data-theme="yellow"] .volume-fill {
  background: #a16207 !important;
}

[data-theme="yellow"] .volume-handle {
  background: #a16207 !important;
}

[data-theme="yellow"] .volume-popup {
  background: rgba(234, 179, 8, 0.9) !important;
}

[data-theme="yellow"] .volume-bg-vertical {
  background: rgba(0, 0, 0, 0.3) !important;
}

[data-theme="yellow"] .volume-fill-vertical {
  background: #000000 !important;
}

[data-theme="yellow"] .volume-handle-vertical {
  background: #000000 !important;
}

[data-theme="yellow"] .volume-text-popup {
  color: #000000 !important;
}

/* 白色主题下的播放器样式 */
[data-theme="white"] .netease-player {
  background: #ffffff !important;
  border-top: 1px solid #e5e7eb !important;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1) !important;
}

[data-theme="white"] .album-cover {
  background: #f9fafb !important;
}

[data-theme="white"] .song-title {
  color: #111827 !important;
}

[data-theme="white"] .artist-name {
  color: #374151 !important;
}

[data-theme="white"] .control-btn {
  color: #374151 !important;
}

[data-theme="white"] .control-btn:not(:disabled):hover {
  background: #f3f4f6 !important;
}

[data-theme="white"] .play:hover {
  background: #f3f4f6 !important;
}

[data-theme="white"] .time-info {
  color: #6b7280 !important;
}

[data-theme="white"] .tool-btn {
  color: #6b7280 !important;
}

[data-theme="white"] .tool-btn:hover {
  color: #374151 !important;
  background: #f3f4f6 !important;
}

[data-theme="white"] .tool-btn.love {
  color: #6b7280 !important;
}

[data-theme="white"] .tool-btn.love.loved {
  color: #dc2626 !important;
}

[data-theme="white"] .tool-btn.love:hover {
  background: #f3f4f6 !important;
}

[data-theme="white"] .progress-bg {
  background: #e5e7eb !important;
}

[data-theme="white"] .progress-loaded {
  background: #374151 !important;
}

[data-theme="white"] .progress-handle {
  background: #374151 !important;
}

[data-theme="white"] .volume-bg {
  background: #e5e7eb !important;
}

[data-theme="white"] .volume-fill {
  background: #374151 !important;
}

[data-theme="white"] .volume-handle {
  background: #374151 !important;
}

[data-theme="white"] .volume-popup {
  background: rgba(0, 0, 0, 0.8) !important;
}

[data-theme="white"] .volume-bg-vertical {
  background: rgba(255, 255, 255, 0.3) !important;
}

[data-theme="white"] .volume-fill-vertical {
  background: #ffffff !important;
}

[data-theme="white"] .volume-handle-vertical {
  background: #ffffff !important;
}

[data-theme="white"] .volume-text-popup {
  color: #ffffff !important;
}
</style>