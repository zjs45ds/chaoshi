// 全屏播放器组件
<template>
  <div class="fullscreen-player" :class="{ 'is-visible': visible }" v-if="visible || isTransitioning">
    <!-- 背景层 - 纯色背景（跟随主题色） -->
    <div class="player-background">
      <!-- 纯色背景 -->
      <div class="solid-color-background"></div>
      
      <!-- 左侧半圆波纹（仅在播放时显示） -->
      <transition name="fade">
        <div class="side-ripple left-ripple" v-if="isPlaying">
          <!-- 更靠里的两条小半圈线条 -->
          <div class="ripple-circle ripple-inner-1"></div>
          <div class="ripple-circle ripple-inner-2"></div>
          <!-- 原有的五条主半圈波浪 -->
          <div class="ripple-circle ripple-1"></div>
          <div class="ripple-circle ripple-2"></div>
          <div class="ripple-circle ripple-3"></div>
          <div class="ripple-circle ripple-4"></div>
          <div class="ripple-circle ripple-5"></div>
        </div>
      </transition>
      
      <!-- 右侧半圆波纹（仅在播放时显示） -->
      <transition name="fade">
        <div class="side-ripple right-ripple" v-if="isPlaying">
          <!-- 更靠里的两条小半圈线条 -->
          <div class="ripple-circle ripple-inner-1"></div>
          <div class="ripple-circle ripple-inner-2"></div>
          <!-- 原有的五条主半圈波浪 -->
          <div class="ripple-circle ripple-1"></div>
          <div class="ripple-circle ripple-2"></div>
          <div class="ripple-circle ripple-3"></div>
          <div class="ripple-circle ripple-4"></div>
          <div class="ripple-circle ripple-5"></div>
        </div>
      </transition>
      
      <!-- 音频驱动的背景半圈波浪（仅在播放时显示） -->
      <div class="audio-wave-background left-audio-wave" v-if="isPlaying">
        <div class="audio-wave" v-for="i in 5" :key="'left-wave-' + i" :style="getLeftWaveStyle(i - 1)"></div>
      </div>
      <div class="audio-wave-background right-audio-wave" v-if="isPlaying">
        <div class="audio-wave" v-for="i in 5" :key="'right-wave-' + i" :style="getRightWaveStyle(i - 1)"></div>
      </div>
      

      
      <!-- 大歌词背景（仅在歌词模式显示） -->
      <div class="lyrics-background-layer" v-if="showLyricsBackground">
        <!-- 点击遮罩层 -->
        <!-- 第一个大字作为背景 -->
        <div class="first-char-bg">{{ (currentLyricText || '看一看自己从前到底有多疯狂')[0] }}</div>
        <!-- 完整歌词 -->
        <div class="full-lyric-text">{{ currentLyricText || '看一看自己从前到底有多疯狂' }}</div>
      </div>
    </div>
    
    <!-- 播放器内容 -->
    <div class="player-content" :class="{ 'lyrics-bg-mode': showLyricsBackground }" @mousemove="handleMouseMove" @click="handleClick">
      <!-- 头部控制栏 -->
      <transition name="fade">
        <div class="player-header" v-show="showControls" @mouseenter="showControlsTemporarily" @mousemove="showControlsTemporarily">
          <div class="header-left-buttons">
            <button class="header-btn back" @click="closePlayer">
              <svg viewBox="0 0 24 24" width="28" height="28">
                <path d="M7 10l5 5 5-5z" fill="currentColor"/>
              </svg>
            </button>
            <button class="header-btn fullscreen" @click="toggleFullscreen">
              <svg viewBox="0 0 24 24" width="28" height="28" v-if="!isFullscreen">
                <path d="M7 14H5v5h5v-2H7v-3zm-2-4h2V7h3V5H5v5zm12 7h-3v2h5v-5h-2v3zM14 5v2h3v3h2V5h-5z" fill="currentColor"/>
              </svg>
              <svg viewBox="0 0 24 24" width="28" height="28" v-else>
                <path d="M5 16h3v3h2v-5H5v2zm3-8H5v2h5V5H8v3zm6 11h2v-3h3v-2h-5v5zm2-11V5h-2v5h5V8h-3z" fill="currentColor"/>
              </svg>
            </button>
          </div>
        </div>
      </transition>
      
       <!-- 主要内容区域 -->
       <div class="player-main" :class="{ 'lyrics-mode': showLyricsBackground }">
         <!-- 经典模式 -->
         <div class="classic-layout" v-if="!showLyricsBackground">
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
       </div>
      
      <!-- 底部三行歌词显示（大歌词模式下隐藏） -->
      <div class="bottom-single-lyric" v-if="!showLyricsBackground" @click="toggleLyricsBackground">
        <div class="bottom-lyric-content" v-if="currentSongLyrics.length > 0 && currentLyricIndex >= 0">
          <!-- 上一句歌词 -->
          <div class="lyric-line prev-line" v-if="currentLyricIndex > 0">
            {{ currentSongLyrics[currentLyricIndex - 1]?.text || '' }}
          </div>
          <div class="lyric-line prev-line" v-else></div>
          
          <!-- 当前歌词 -->
          <div class="lyric-line current-lyric-line">
            {{ currentSongLyrics[currentLyricIndex]?.text || '...' }}
          </div>
          
          <!-- 下一句歌词 -->
          <div class="lyric-line next-line" v-if="currentLyricIndex < currentSongLyrics.length - 1">
            {{ currentSongLyrics[currentLyricIndex + 1]?.text || '' }}
          </div>
          <div class="lyric-line next-line" v-else></div>
        </div>
        <div class="bottom-lyric-content" v-else-if="!isLoadingLyrics">
          <div class="bottom-lyric-text">正在播放音乐...</div>
        </div>
        <div class="bottom-lyric-content" v-else>
          <div class="bottom-lyric-text">加载歌词中...</div>
        </div>
      </div>
      
      <!-- 完整歌词显示模态框 -->
      <transition name="lyrics-modal">
        <div class="full-lyrics-modal" v-if="showFullLyrics" @click.self="toggleFullLyrics">
          <div class="full-lyrics-content">
            <div class="lyrics-header">
              <h3>{{ currentSong?.name || '未知歌曲' }}</h3>
              <p>{{ currentSong?.artist || '未知艺术家' }}</p>
              <button class="close-lyrics-btn" @click="toggleFullLyrics">
                <i class="icon-close"></i>
              </button>
            </div>
            <div class="full-lyrics-container" ref="fullLyricsContainer">
              <div v-if="isLoadingLyrics" class="lyrics-loading">
                加载歌词中...
              </div>
              <div v-else-if="currentSongLyrics.length === 0" class="no-lyrics">
                <div class="lyrics-placeholder">
                  <i class="icon-music-note"></i>
                  <p>暂无歌词</p>
                </div>
              </div>
              <div v-else class="lyrics-list">
                <div 
                  v-for="(lyric, index) in currentSongLyrics" 
                  :key="index"
                  class="full-lyrics-line"
                  :class="{ 'active': index === currentLyricIndex }"
                  @click="handleLyricClick(index)"
                >
                  {{ lyric.text }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </transition>
      
      <!-- 播放控制区域 -->
      <transition name="fade">
        <div class="player-controls-section" v-show="showControls" @mouseenter="showControlsTemporarily" @mousemove="showControlsTemporarily">
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
          <!-- 左侧：时间显示 -->
          <div class="left-controls">
            <div class="time-display">
              <span class="current-time">{{ currentTimeFormatted }}</span>
              <span class="time-separator">/</span>
              <span class="total-time">{{ durationFormatted }}</span>
            </div>
          </div>
          
          <!-- 中间播放控制按钮 -->
          <div class="center-controls">
            <button class="control-btn prev" @click="playPrevious" :disabled="!hasPlaylist" title="上一首">
              <i class="icon-prev"></i>
            </button>
            
            <button class="control-btn play" @click="togglePlay" :class="{ 'playing': isPlaying }" title="播放/暂停">
              <i :class="isPlaying ? 'icon-pause' : 'icon-play'" :title="isPlaying ? '暂停' : '播放'"></i>
            </button>
            
            <button class="control-btn next" @click="playNext" :disabled="!hasPlaylist" title="下一首">
              <i class="icon-next"></i>
            </button>
          </div>
          
          <!-- 右侧功能按钮 -->
          <div class="right-controls">
            <button class="control-btn love" @click="toggleLove" :class="{ 'loved': isCurrentSongLiked }">
              <img v-if="isCurrentSongLiked" class="love-icon-img" src="/src/assets/已收藏.png" alt="已收藏" />
              <i v-else class="icon-heart"></i>
            </button>
            
            <button class="control-btn small mode" @click="togglePlayMode">
              <i :class="getModeIcon()"></i>
            </button>
            
            <button class="control-btn small volume" @click="toggleVolumePanel" ref="volumeButtonRef">
              <i :class="getVolumeIcon()"></i>
            </button>
            
            <button class="control-btn small playlist" @click="togglePlaylist">
              <i class="icon-list"></i>
            </button>
          </div>
        </div>
        </div>
      </transition>
      
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
      
      <!-- 底部触发区域 - 鼠标移动到底部显示控制栏（仅在控制栏隐藏时显示） -->
      <div class="bottom-trigger-area" v-show="!showControls" @mouseenter="handleMouseMove" @click="handleClick"></div>
      
      <!-- 顶部触发区域 - 鼠标移动到顶部显示控制栏（仅在控制栏隐藏时显示） -->
      <div class="top-trigger-area" v-show="!showControls" @mouseenter="handleMouseMove" @click="handleClick"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { 
  isPlaying, isPaused, isLoading, currentTime, duration, volume, isMuted,
  currentSong, currentIndex, playlist, playMode, playModeText, currentSongLyrics, isLoadingLyrics, currentLyricIndex,
  progress, currentTimeFormatted, durationFormatted, currentLyricText,
  togglePlay, playNext, playPrevious, togglePlayMode, seekToProgress, setVolumeLevel, toggleMute,
  hasSong, hasPlaylist, loadLyrics as loadSongLyrics, getCurrentLyricIndex,
  initAudioAnalyser, getAudioFrequencyData, resumeAudioContext
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
 const styleSelectorRef = ref(null)
 const leftVisualizerCanvas = ref(null)
 const rightVisualizerCanvas = ref(null)
 const lyricsContainer = ref(null)
 const classicLyricsContainer = ref(null)
 const isDraggingProgress = ref(false)
 const isDraggingVolume = ref(false)
 const isTransitioning = ref(false)
 const showVolumePanel = ref(false)
 const showStylePanel = ref(false)
 const showFullLyrics = ref(false) // 控制完整歌词显示
 const showLyricsBackground = ref(false) // 控制歌词背景模式
 const showControls = ref(true) // 控制控制栏显示/隐藏
 const hideControlsTimer = ref(null) // 自动隐藏定时器 

// 音频可视化相关
const rippleScales = ref([0, 0, 0, 0, 0]) // 5个波浪的缩放值
let animationFrameId = null
let audioAnalyserInitialized = false

// 更新波浪动画
const updateRippleAnimation = () => {
  if (!isPlaying.value) {
    // 停止播放时，波浪逐渐归零
    rippleScales.value = rippleScales.value.map(v => v * 0.95)
    if (rippleScales.value.some(v => v > 0.01)) {
      animationFrameId = requestAnimationFrame(updateRippleAnimation)
    }
    return
  }
  
  const frequencyData = getAudioFrequencyData()
  
  if (frequencyData.length > 0) {
    // 使用音频数据更新波浪，添加平滑过渡
    rippleScales.value = rippleScales.value.map((current, i) => {
      const target = frequencyData[i] || 0
      // 平滑过渡：当前值向目标值靠近
      return current + (target - current) * 0.3
    })
  }
  
  animationFrameId = requestAnimationFrame(updateRippleAnimation)
}

// 初始化音频分析并开始动画
const startAudioVisualization = async () => {
  if (!audioAnalyserInitialized) {
    await resumeAudioContext()
    initAudioAnalyser()
    audioAnalyserInitialized = true
  }
  
  if (!animationFrameId) {
    updateRippleAnimation()
  }
}

// 停止动画
const stopAudioVisualization = () => {
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
    animationFrameId = null
  }
}

// 计算左侧背景波浪样式
const getLeftWaveStyle = (index) => {
  const baseScale = 0.3 + index * 0.2 // 基础缩放，让外层波浪更大
  const audioScale = rippleScales.value[index] * 0.6 // 音频驱动的缩放
  const scale = baseScale + audioScale
  const opacity = 0.08 + rippleScales.value[index] * 0.2 // 背景波浪更透明
  
  return {
    transform: `translate(-50%, -50%) scale(${scale})`,
    opacity: Math.min(opacity, 0.3)
  }
}

// 计算右侧背景波浪样式
const getRightWaveStyle = (index) => {
  const baseScale = 0.3 + index * 0.2
  const audioScale = rippleScales.value[index] * 0.6
  const scale = baseScale + audioScale
  const opacity = 0.08 + rippleScales.value[index] * 0.2
  
  return {
    transform: `translate(50%, -50%) scale(${scale})`,
    opacity: Math.min(opacity, 0.3)
  }
}

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
   if (!volumeButtonRef.value) {
     // 默认位置：屏幕中下方
     return {
       position: 'fixed',
       right: '100px',
       bottom: '120px',
       zIndex: 100000
     }
   }
   
   const rect = volumeButtonRef.value.getBoundingClientRect()
   return {
     position: 'fixed',
     left: `${rect.left + rect.width / 2 - 28}px`,
     bottom: `${window.innerHeight - rect.top + 15}px`,
     zIndex: 100000
   }
 })
 
 const stylePanelStyle = computed(() => {
   if (!styleSelectorRef.value) return {}
   
   const rect = styleSelectorRef.value.getBoundingClientRect()
   return {
     position: 'fixed',
     right: `${window.innerWidth - rect.right}px`,
     top: `${rect.bottom + 10}px`,
     zIndex: 100001
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

const toggleFullLyrics = () => {
  showFullLyrics.value = !showFullLyrics.value
}

const isFullscreen = ref(false)

const toggleLyricsBackground = () => {
  showLyricsBackground.value = !showLyricsBackground.value
}

const toggleFullscreen = async () => {
  try {
    if (!document.fullscreenElement) {
      // 进入全屏
      await document.documentElement.requestFullscreen()
      isFullscreen.value = true
    } else {
      // 退出全屏
      await document.exitFullscreen()
      isFullscreen.value = false
    }
  } catch (error) {
    console.error('全屏切换失败:', error)
  }
}

// 控制栏自动隐藏逻辑
const showControlsTemporarily = () => {
  showControls.value = true
  resetHideControlsTimer()
}

const resetHideControlsTimer = () => {
  if (hideControlsTimer.value) {
    clearTimeout(hideControlsTimer.value)
  }
  // 始终自动隐藏控制栏
  hideControlsTimer.value = setTimeout(() => {
    showControls.value = false
    // 同时关闭音量面板，避免控制栏隐藏后面板位置异常
    showVolumePanel.value = false
  }, 3000) // 3秒后自动隐藏
}

const handleMouseMove = () => {
  showControlsTemporarily()
}

const handleClick = () => {
  showControlsTemporarily()
}

// 监听全屏状态变化
const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
}



const handleLyricClick = (index) => {
  if (currentSongLyrics.value[index]) {
    seekToProgress(currentSongLyrics.value[index].time / duration.value)
  }
}
 const toggleLove = async () => {
   if (!currentSong.value) return
   
   try {
     await toggleSongLike(currentSong.value)
   } catch (error) {
     // CONSOLE LOG REMOVED: console.error('切换收藏状态失败:', error)
   }
 }
 
 // 样式选择器相关方法
 const toggleStyleSelector = () => {
   showStylePanel.value = !showStylePanel.value
 }
 
 // 音效模式已移除，使用loadSongLyrics加载歌词
 
 // 音效可视化相关方法
 const initAudioVisualizer = () => {
   try {
     if (!audioContext.value) {
       audioContext.value = new (window.AudioContext || window.webkitAudioContext)()
     }
     
     if (!analyser.value) {
       analyser.value = audioContext.value.createAnalyser()
       analyser.value.fftSize = 512  // 增加分辨率
       analyser.value.smoothingTimeConstant = 0.8  // 平滑效果
       const bufferLength = analyser.value.frequencyBinCount
       dataArray.value = new Uint8Array(bufferLength)
       
       connectAudioSource()
     }
     
   } catch (error) {
     // CONSOLE LOG REMOVED: console.error('初始化音效可视化失败:', error)
     startSimulatedVisualization()
   }
 }
 
 // 连接音频源
 const connectAudioSource = () => {
   try {
     // 尝试从音频元素获取源
     const audioElements = document.querySelectorAll('audio')
     if (audioElements.length > 0) {
       const audioElement = audioElements[0]
       if (!audioElement.crossOrigin) {
         audioElement.crossOrigin = 'anonymous'
       }
       
       const source = audioContext.value.createMediaElementSource(audioElement)
       source.connect(analyser.value)
       analyser.value.connect(audioContext.value.destination)
     }
   } catch (error) {
     // CONSOLE LOG REMOVED: console.warn('无法连接真实音频源，使用模拟数据:', error)
     startSimulatedVisualization()
   }
 }
 
 // 模拟音频数据（当无法获取真实音频时）
 const startSimulatedVisualization = () => {
   if (!dataArray.value) return
   
   const simulateAudioData = () => {
     if (!isPlaying.value) return
     
     for (let i = 0; i < dataArray.value.length; i++) {
       const baseValue = Math.sin(Date.now() * 0.001 + i * 0.1) * 50 + 80
       const randomValue = Math.random() * 50
       dataArray.value[i] = Math.max(0, Math.min(255, baseValue + randomValue))
     }
     
     if (isPlaying.value) {
       setTimeout(simulateAudioData, 50) // 20 FPS
     }
   }
   
   if (isPlaying.value) {
     simulateAudioData()
   }
 }
 
 const startVisualization = () => {
   if (!leftVisualizerCanvas.value || !rightVisualizerCanvas.value) return
   
   const drawVisualizer = (canvas, isLeft = true) => {
     const ctx = canvas.getContext('2d')
     const width = canvas.width = canvas.offsetWidth * window.devicePixelRatio
     const height = canvas.height = canvas.offsetHeight * window.devicePixelRatio
     ctx.scale(window.devicePixelRatio, window.devicePixelRatio)
     
     const canvasWidth = canvas.offsetWidth
     const canvasHeight = canvas.offsetHeight
     
     const draw = () => {
       if (!analyser.value || !dataArray.value) return
       
       analyser.value.getByteFrequencyData(dataArray.value)
       
       ctx.clearRect(0, 0, canvasWidth, canvasHeight)
       
      
       const barCount = 60 // 更多的频谱条，更细腻
       const barWidth = 2.5 // 更细的条
       const barSpacing = 1.5 // 更小的间距
       const totalWidth = barCount * (barWidth + barSpacing) - barSpacing
       const startX = (canvasWidth - totalWidth) / 2
       
       for (let i = 0; i < barCount; i++) {
         const dataIndex = Math.floor((i / barCount) * dataArray.value.length)
         let barHeight = (dataArray.value[dataIndex] / 255) * canvasHeight * 0.85
         
         barHeight += Math.sin(Date.now() * 0.01 + i * 0.5) * 3
         barHeight = Math.max(2, barHeight) // 最小高度
         
         const x = startX + i * (barWidth + barSpacing)
         const y = canvasHeight - barHeight
         
         const gradient = ctx.createLinearGradient(0, canvasHeight, 0, y)
         
         if (isLeft) {
           gradient.addColorStop(0, '#00d4ff')
           gradient.addColorStop(0.3, '#0099ff')
           gradient.addColorStop(0.7, '#0066ff')
           gradient.addColorStop(1, '#0033ff')
         } else {
           gradient.addColorStop(0, '#00ff88')
           gradient.addColorStop(0.3, '#00cc99')
           gradient.addColorStop(0.7, '#0099aa')
           gradient.addColorStop(1, '#0066bb')
         }
         
         ctx.fillStyle = gradient
         
         // 轻微的发光效果
         ctx.shadowColor = isLeft ? '#00aaff' : '#00cc88'
         ctx.shadowBlur = 8
         ctx.shadowOffsetY = 0
         
         ctx.beginPath()
         if (ctx.roundRect) {
           ctx.roundRect(x, y, barWidth, barHeight, [1.5, 1.5, 0, 0])
         } else {
           const radius = 1.5
           ctx.moveTo(x + radius, y)
           ctx.lineTo(x + barWidth - radius, y)
           ctx.quadraticCurveTo(x + barWidth, y, x + barWidth, y + radius)
           ctx.lineTo(x + barWidth, y + barHeight)
           ctx.lineTo(x, y + barHeight)
           ctx.lineTo(x, y + radius)
           ctx.quadraticCurveTo(x, y, x + radius, y)
         }
         ctx.fill()
         
         if (barHeight > canvasHeight * 0.3) {
           ctx.shadowBlur = 0
           const highlightGradient = ctx.createLinearGradient(0, y, 0, y + 20)
           highlightGradient.addColorStop(0, isLeft ? 'rgba(255, 255, 255, 0.4)' : 'rgba(255, 255, 255, 0.3)')
           highlightGradient.addColorStop(1, 'rgba(255, 255, 255, 0)')
           
           ctx.fillStyle = highlightGradient
           if (ctx.roundRect) {
             ctx.fillRect(x, y, barWidth, Math.min(20, barHeight))
           }
         }
         ctx.shadowBlur = 0
       }
       
       if (isPlaying.value) {
         requestAnimationFrame(draw)
       }
     }
     
     draw()
   }
   
   drawVisualizer(leftVisualizerCanvas.value, true)
   drawVisualizer(rightVisualizerCanvas.value, false)
 }
 
 const cleanupAudioVisualizer = () => {
   if (animationId.value) {
     cancelAnimationFrame(animationId.value)
     animationId.value = null
   }
 }
 
 // 歌词相关方法
 const loadLyrics = async () => {
   if (currentSong.value?.id) {
     await loadSongLyrics(currentSong.value.id)
   }
 }
 
 const updateCurrentLyric = () => {
   if (currentSongLyrics.value.length === 0) return
   
   const currentTimeInSeconds = currentTime.value
   const newIndex = getCurrentLyricIndex(currentTimeInSeconds, currentSongLyrics.value)
   
   if (newIndex !== -1 && newIndex !== currentLyricIndex.value) {
     currentLyricIndex.value = newIndex
     
     nextTick(() => {
       if (classicLyricsContainer.value) {
         const activeElement = classicLyricsContainer.value.querySelector('.lyrics-line-classic.active')
         if (activeElement) {
           activeElement.scrollIntoView({
             behavior: 'smooth',
             block: 'center'
           })
         }
       }
     })
   }
 }
const handleImageError = (event) => { 
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
const toggleVolumePanel = async () => {
  if (!showVolumePanel.value) {
    // 打开面板时，等待 DOM 更新以确保按钮引用可用
    await nextTick()
  }
  showVolumePanel.value = !showVolumePanel.value
}

const setVolumeVertical = (event) => {
  if (!volumeSliderRef.value || isDraggingVolume.value) return
  const rect = volumeSliderRef.value.getBoundingClientRect()
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

watch(currentLyricIndex, () => {
  // 处理完整歌词模态框的滚动
  if (showFullLyrics.value && currentLyricIndex.value !== -1) {
    nextTick(() => {
      const container = document.querySelector('.full-lyrics-container')
      const activeLine = container?.querySelector('.full-lyrics-line.active')
      if (container && activeLine) {
        const containerHeight = container.clientHeight
        const lineTop = activeLine.offsetTop
        container.scrollTop = lineTop - containerHeight / 2 + activeLine.clientHeight / 2
      }
    })
  }
  
  // 底部歌词不需要滚动，只显示当前行
})

// 监听器和生命周期
 watch(() => props.visible, (newVisible) => {
   if (newVisible) {
     isTransitioning.value = true
     document.body.classList.add('fullscreen-player-active')
     document.body.style.overflow = 'hidden'
     document.documentElement.style.overflow = 'hidden'
     document.body.style.overscrollBehavior = 'none'
     document.documentElement.style.overscrollBehavior = 'none'
     document.body.style.position = 'fixed'
     document.body.style.width = '100%'
     document.body.style.height = '100%'
     nextTick(() => setTimeout(() => { isTransitioning.value = false }, 300))
     
     nextTick(() => {
       if (currentSong.value?.id) {
         loadSongLyrics(currentSong.value.id)
       }
     })
   } else {
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
 
 watch(isPlaying, (playing) => {
 })
 
 // 监听时间变化更新歌词
 watch(currentTime, () => {
   updateCurrentLyric()
 })

 // 监听歌曲变化，加载新歌词
 watch(currentSong, async (newSong) => {
   if (newSong?.id) {
     await loadSongLyrics(newSong.id)
   }
 })

// 监听大歌词模式变化
watch(showLyricsBackground, (newValue) => {
  if (newValue) {
    // 进入大歌词模式，显示控制栏并开始自动隐藏计时
    showControlsTemporarily()
  } else {
    // 退出大歌词模式，重新开始自动隐藏计时
    showControlsTemporarily()
  }
})

// 监听播放状态变化，启动/停止音频可视化
watch(isPlaying, (playing) => {
  if (playing && props.visible) {
    startAudioVisualization()
  }
})

// 监听组件可见性，启动/停止音频可视化
watch(() => props.visible, (visible) => {
  if (visible && isPlaying.value) {
    startAudioVisualization()
  } else if (!visible) {
    stopAudioVisualization()
  }
})

onMounted(() => {
  // 初始化控制栏自动隐藏
  resetHideControlsTimer()
  
  // 如果正在播放，启动音频可视化
  if (isPlaying.value && props.visible) {
    startAudioVisualization()
  }
  
  const handleKeydown = (event) => {
    if (event.key === 'Escape' && props.visible) {
      if (showLyricsBackground.value) {
        // 如果在大字模式，先退出大字模式
        toggleLyricsBackground()
      } else if (showVolumePanel.value) {
        showVolumePanel.value = false
      } else {
        closePlayer()
      }
    }
  }
  
  // 监听全屏状态变化
  document.addEventListener('fullscreenchange', handleFullscreenChange)
  
   // 点击外部关闭面板
   const handleClickOutside = (event) => {
     // 关闭音量面板
     if (showVolumePanel.value && volumeButtonRef.value && 
         !volumeButtonRef.value.contains(event.target) &&
         !event.target.closest('.volume-popup')) {
       showVolumePanel.value = false
     }
   }
  
  const handleTouchMove = (event) => {
    if (props.visible) {
      event.preventDefault()
    }
  }
  
  document.addEventListener('keydown', handleKeydown)
  document.addEventListener('click', handleClickOutside)
  document.addEventListener('touchmove', handleTouchMove, { passive: false })
  
  onUnmounted(() => {
    // 清理控制栏隐藏定时器
    if (hideControlsTimer.value) {
      clearTimeout(hideControlsTimer.value)
    }
    
    // 停止音频可视化动画
    stopAudioVisualization()
    
    document.removeEventListener('keydown', handleKeydown)
    document.removeEventListener('click', handleClickOutside)
    document.removeEventListener('touchmove', handleTouchMove)
    document.removeEventListener('fullscreenchange', handleFullscreenChange)
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
/* 淡入淡出过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 触发区域 */
.bottom-trigger-area {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100px;
  z-index: 99997;
  pointer-events: auto;
  cursor: pointer;
}

.top-trigger-area {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 80px;
  z-index: 99997;
  pointer-events: auto;
  cursor: pointer;
}

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
  overflow: hidden;
}

/* 纯色背景（始终显示，跟随主题色） */
.solid-color-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  /* 叠加一层淡黑色遮罩，使背景变暗，突出白色波纹 */
  background: linear-gradient(rgba(0, 0, 0, 0.2), rgba(0, 0, 0, 0.2)), var(--primary, #FF6B9B);
  z-index: 1;
}

/* 音频驱动的背景半圈波浪 */
.audio-wave-background {
  position: absolute;
  top: 0;
  width: 50%;
  height: 100%;
  z-index: 1;
  pointer-events: none;
  overflow: hidden;
}

.left-audio-wave {
  left: 0;
}

.right-audio-wave {
  right: 0;
}

.audio-wave {
  position: absolute;
  top: 50%;
  width: 200%;
  height: 200%;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.25) 0%, rgba(255, 255, 255, 0.1) 30%, transparent 60%);
  transition: transform 0.12s ease-out, opacity 0.15s ease-out;
}

/* 左侧背景波浪 - 圆心在左边缘 */
.left-audio-wave .audio-wave {
  left: 0;
}

/* 右侧背景波浪 - 圆心在右边缘 */
.right-audio-wave .audio-wave {
  right: 0;
}

/* 背景波动动画 */
@keyframes backgroundWave {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}



/* 左右侧半圆波浪 */
.side-wave {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 600px;
  height: 100%;
  z-index: 2;
  pointer-events: none;
  overflow: hidden;
}

.left-wave {
  left: 0;
}

.right-wave {
  right: 0;
}

.wave-circle {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, 
    rgba(255, 255, 255, 0.3) 0%, 
    rgba(255, 255, 255, 0.2) 30%, 
    rgba(255, 255, 255, 0.1) 60%,
    transparent 100%);
  filter: blur(30px);
}

.left-wave .wave-circle {
  left: -50%;
}

.right-wave .wave-circle {
  right: -50%;
}

.wave-circle-1 {
  width: 400px;
  height: 400px;
  top: 50%;
  transform: translateY(-50%) scale(0.5);
  animation: waveCirclePulseLeft 6s ease-in-out infinite;
  animation-delay: 0s;
}

.wave-circle-2 {
  width: 600px;
  height: 600px;
  top: 50%;
  transform: translateY(-50%) scale(0.5);
  animation: waveCirclePulseLeft 6s ease-in-out infinite;
  animation-delay: 1.2s;
}

.wave-circle-3 {
  width: 800px;
  height: 800px;
  top: 50%;
  transform: translateY(-50%) scale(0.5);
  animation: waveCirclePulseLeft 6s ease-in-out infinite;
  animation-delay: 2.4s;
}

.wave-circle-4 {
  width: 1000px;
  height: 1000px;
  top: 50%;
  transform: translateY(-50%) scale(0.5);
  animation: waveCirclePulseLeft 6s ease-in-out infinite;
  animation-delay: 3.6s;
}

.wave-circle-5 {
  width: 1200px;
  height: 1200px;
  top: 50%;
  transform: translateY(-50%) scale(0.5);
  animation: waveCirclePulseLeft 6s ease-in-out infinite;
  animation-delay: 4.8s;
}

@keyframes waveCirclePulseLeft {
  0% {
    transform: translateY(-50%) scale(0.5);
    opacity: 0;
  }
  15% {
    opacity: 1;
  }
  85% {
    opacity: 0.4;
  }
  100% {
    transform: translateY(-50%) scale(1.8);
    opacity: 0;
  }
}

/* 左右波浪 */
.wave-left,
.wave-right {
  position: absolute;
  top: 0;
  width: 200px;
  height: 100%;
  z-index: 2;
  pointer-events: none;
}

.wave-left {
  left: 0;
}

.wave-right {
  right: 0;
}

.wave-left svg,
.wave-right svg {
  width: 100%;
  height: 100%;
}

.wave-path {
  fill: rgba(255, 255, 255, 0.1);
}

.wave-1 {
  animation: waveMove1 8s ease-in-out infinite;
}

.wave-2 {
  animation: waveMove2 10s ease-in-out infinite;
  fill: rgba(255, 255, 255, 0.08);
}

.wave-3 {
  animation: waveMove3 12s ease-in-out infinite;
  fill: rgba(255, 255, 255, 0.06);
}

@keyframes waveMove1 {
  0%, 100% {
    d: path("M0,500 Q50,400 0,300 Q50,200 0,100 L0,0 L0,1000 L0,900 Q50,800 0,700 Q50,600 0,500 Z");
  }
  50% {
    d: path("M0,500 Q80,450 0,400 Q80,350 0,300 L0,0 L0,1000 L0,700 Q80,650 0,600 Q80,550 0,500 Z");
  }
}

@keyframes waveMove2 {
  0%, 100% {
    d: path("M0,500 Q60,420 0,340 Q60,260 0,180 L0,0 L0,1000 L0,820 Q60,740 0,660 Q60,580 0,500 Z");
  }
  50% {
    d: path("M0,500 Q90,460 0,420 Q90,380 0,340 L0,0 L0,1000 L0,660 Q90,620 0,580 Q90,540 0,500 Z");
  }
}

@keyframes waveMove3 {
  0%, 100% {
    d: path("M0,500 Q70,440 0,380 Q70,320 0,260 L0,0 L0,1000 L0,740 Q70,680 0,620 Q70,560 0,500 Z");
  }
  50% {
    d: path("M0,500 Q100,470 0,440 Q100,410 0,380 L0,0 L0,1000 L0,620 Q100,590 0,560 Q100,530 0,500 Z");
  }
}

/* 左右侧半圆波纹 - 重新设计 */
.side-ripple {
  position: absolute;
  top: 0;
  width: 50%;
  height: 100%;
  overflow: hidden;
  z-index: 2;
  pointer-events: none;
}

.left-ripple {
  left: 0;
}

.right-ripple {
  right: 0;
}

/* 半圆波纹 - 使用伪元素创建 */
.ripple-circle {
  position: absolute;
  top: 50%;
  width: 200%;
  height: 200%;
  border-radius: 50%;
  border: 3px solid rgba(255, 255, 255, 0.5);
  background: transparent;
  opacity: 0; /* 默认隐藏，等待动画开始 */
}

/* 左边半圆 - 圆心在左边缘 */
.left-ripple .ripple-circle {
  left: 0;
  transform: translate(-50%, -50%);
}

/* 右边半圆 - 圆心在右边缘 */
.right-ripple .ripple-circle {
  right: 0;
  transform: translate(50%, -50%);
}

/* 5层主波纹大小 */
.ripple-1 {
  width: 30%;
  height: 30%;
  animation: semiCirclePulse 5s ease-in-out infinite;
  animation-delay: 0s;
}

.ripple-2 {
  width: 50%;
  height: 50%;
  animation: semiCirclePulse 5s ease-in-out infinite;
  animation-delay: 1s;
}

.ripple-3 {
  width: 70%;
  height: 70%;
  animation: semiCirclePulse 5s ease-in-out infinite;
  animation-delay: 2s;
}

.ripple-4 {
  width: 90%;
  height: 90%;
  animation: semiCirclePulse 5s ease-in-out infinite;
  animation-delay: 3s;
}

.ripple-5 {
  width: 110%;
  height: 110%;
  animation: semiCirclePulse 5s ease-in-out infinite;
  animation-delay: 4s;
}

/* 内侧小半圈波浪，比 ripple-1 更小，更细、更柔和 */
.ripple-inner-1,
.ripple-inner-2 {
  border-width: 1.5px;
  border-color: rgba(255, 255, 255, 0.6);
}

/* 第一条最内侧小半圈 */
.ripple-inner-1 {
  animation: semiCirclePulseInner 4s ease-in-out infinite;
  animation-delay: 0s;
}

/* 第二条小半圈，稍微大一点、稍微滞后 */
.ripple-inner-2 {
  animation: semiCirclePulseInner 4s ease-in-out infinite;
  animation-delay: 0.8s;
}

/* 小半圈扩散动画（半径更小） */
@keyframes semiCirclePulseInner {
  0% {
    width: 10%;
    height: 10%;
    opacity: 0;
  }
  20% {
    opacity: 0.6;
  }
  80% {
    width: 45%;
    height: 45%;
    opacity: 0.2;
  }
  100% {
    width: 55%;
    height: 55%;
    opacity: 0;
  }
}

/* 半圆扩散动画 */
@keyframes semiCirclePulse {
  0% {
    width: 20%;
    height: 20%;
    opacity: 0;
  }
  20% {
    opacity: 0.7;
  }
  80% {
    opacity: 0.2;
  }
  100% {
    width: 120%;
    height: 120%;
    opacity: 0;
  }
}

/* 中心波纹 */
.center-ripple {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
  z-index: 2;
  pointer-events: none;
}

.center-ripple-circle {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  border: 3px solid rgba(255, 255, 255, 0.4);
  background: radial-gradient(circle, 
    rgba(255, 255, 255, 0.35) 0%, 
    rgba(255, 255, 255, 0.25) 30%, 
    rgba(255, 255, 255, 0.15) 50%,
    rgba(255, 255, 255, 0.05) 70%,
    transparent 100%);
  filter: blur(15px);
  box-shadow: 
    0 0 40px rgba(255, 255, 255, 0.3),
    inset 0 0 60px rgba(255, 255, 255, 0.2);
}

.center-ripple-1 {
  width: 400px;
  height: 400px;
  animation: centerRipplePulse 5s ease-in-out infinite;
}

.center-ripple-2 {
  width: 600px;
  height: 600px;
  animation: centerRipplePulse 6s ease-in-out infinite;
  animation-delay: 1.5s;
}

.center-ripple-3 {
  width: 800px;
  height: 800px;
  animation: centerRipplePulse 7s ease-in-out infinite;
  animation-delay: 3s;
}

@keyframes centerRipplePulse {
  0% {
    transform: translate(-50%, -50%) scale(0.6);
    opacity: 0;
  }
  20% {
    opacity: 0.8;
  }
  80% {
    opacity: 0.3;
  }
  100% {
    transform: translate(-50%, -50%) scale(1.8);
    opacity: 0;
  }
}

/* 歌词背景层 */
.lyrics-background-layer {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2rem;
  z-index: 100000;
  cursor: pointer;
  transition: opacity 0.3s ease;
}

.lyrics-background-layer:hover {
  opacity: 0.95;
}

/* 点击遮罩层 - 覆盖整个屏幕，确保能捕获点击 */
.lyrics-click-mask {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  width: 100% !important;
  height: 100% !important;
  z-index: 999999 !important;
  cursor: pointer !important;
  background: rgba(255, 0, 0, 0.1) !important;
  pointer-events: auto !important;
}

/* 提示文字 */
.lyrics-hint {
  position: absolute;
  bottom: 200px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  z-index: 4;
  pointer-events: none;
  animation: hintFade 2s ease-in-out infinite;
}

@keyframes hintFade {
  0%, 100% { opacity: 0.4; }
  50% { opacity: 0.8; }
}

/* 第一个大字背景 */
.first-char-bg {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 12rem;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.15);
  text-shadow: 0 0 100px rgba(0, 0, 0, 0.3);
  letter-spacing: 0;
  line-height: 1;
  z-index: 1;
  pointer-events: none;
}

/* 完整歌词文字 */
.full-lyric-text {
  position: relative;
  font-size: 4rem;
  font-weight: 700;
  color: rgba(255, 255, 255, 1);
  text-shadow: 
    0 0 80px rgba(0, 0, 0, 0.5),
    0 4px 20px rgba(0, 0, 0, 0.3);
  letter-spacing: 0.05em;
  line-height: 1.2;
  text-align: center;
  padding: 0 2rem;
  z-index: 2;
  pointer-events: none;
}

/* 退出提示 */
.exit-hint {
  position: fixed;
  bottom: 180px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  z-index: 4;
  pointer-events: none;
  animation: hintFade 2s ease-in-out infinite;
  background: rgba(0, 0, 0, 0.3);
  padding: 8px 16px;
  border-radius: 20px;
  backdrop-filter: blur(10px);
}

/* 波浪容器 */
.wave-container-bg {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 300px;
  z-index: 3;
  pointer-events: none;
}

.wave-bg {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 200%;
  height: 100%;
}

.wave-path-bg {
  fill: rgba(0, 0, 0, 0.1);
}

.wave-bg-1 {
  animation: waveBgMove1 20s linear infinite;
}

.wave-bg-1 .wave-path-bg {
  fill: rgba(0, 0, 0, 0.15);
}

.wave-bg-2 {
  animation: waveBgMove2 25s linear infinite;
  animation-delay: -5s;
}

.wave-bg-2 .wave-path-bg {
  fill: rgba(0, 0, 0, 0.1);
}

.wave-bg-3 {
  animation: waveBgMove3 30s linear infinite;
  animation-delay: -10s;
}

.wave-bg-3 .wave-path-bg {
  fill: rgba(0, 0, 0, 0.08);
}

@keyframes waveBgMove1 {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-50%);
  }
}

@keyframes waveBgMove2 {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-50%);
  }
}

@keyframes waveBgMove3 {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-50%);
  }
}

/* 音频圆环效果 */
.audio-rings-bg {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 600px;
  height: 600px;
  z-index: 1;
}

.ring-bg {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 2px solid rgba(255, 255, 255, 0.05);
  border-radius: 50%;
  animation: ringBgPulse 4s ease-in-out infinite;
}

.ring-bg-1 {
  width: 300px;
  height: 300px;
  animation-delay: 0s;
}

.ring-bg-2 {
  width: 450px;
  height: 450px;
  animation-delay: 1.3s;
}

.ring-bg-3 {
  width: 600px;
  height: 600px;
  animation-delay: 2.6s;
}

@keyframes ringBgPulse {
  0% {
    transform: translate(-50%, -50%) scale(0.8);
    opacity: 0;
  }
  50% {
    opacity: 0.3;
  }
  100% {
    transform: translate(-50%, -50%) scale(1.2);
    opacity: 0;
  }
}

/* 渐变遮罩 */
.gradient-overlay-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at center, transparent 0%, rgba(0, 0, 0, 0.15) 100%);
  z-index: 5;
  pointer-events: none;
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
  justify-content: flex-start;
  padding: 20px;
  flex-shrink: 0;
}

.header-left-buttons {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-btn {
  background: rgba(255, 255, 255, 0.1);
  border: none;
  color: #fff;
  cursor: pointer;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  border-radius: 50%;
  backdrop-filter: blur(10px);
}

.header-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

.header-btn svg {
  width: 28px;
  height: 28px;
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
 
 /* 样式选择器 */
 .style-selector {
   position: relative;
 }
 
 .style-panel {
   position: fixed;
   background: rgba(0, 0, 0, 0.7); /* 更透明 */
   border-radius: 12px;
   padding: 12px; /* 减小高度 */
   min-width: 200px;
   animation: stylePanelIn 0.2s ease-out;
 }
 
 @keyframes stylePanelIn {
   from {
     opacity: 0;
     transform: translateY(-10px) scale(0.9);
   }
   to {
     opacity: 1;
     transform: translateY(0) scale(1);
   }
 }
 
 .style-option {
   display: flex;
   align-items: center;
   gap: 12px;
   padding: 12px;
   border-radius: 8px;
   cursor: pointer;
   transition: all 0.2s ease;
   margin-bottom: 8px;
 }
 
 .style-option:last-child {
   margin-bottom: 0;
 }
 
 .style-option:hover {
   background: rgba(255, 255, 255, 0.1);
 }
 
 .style-option.active {
   background: rgba(255, 255, 255, 0.2);
 }
 
 .style-preview {
   width: 32px;
   height: 32px;
   border-radius: 6px;
   background: linear-gradient(45deg, #ff4081, #ff8a80);
   position: relative;
 }
 
 .classic-preview {
   background: linear-gradient(45deg, #667eea, #764ba2);
 }
 
 .visualizer-preview {
   background: linear-gradient(45deg, #ff0080, #ff8080);
   overflow: hidden;
 }
 
 .visualizer-preview::after {
   content: '';
   position: absolute;
   bottom: 0;
   left: 0;
   right: 0;
   height: 60%;
   background: repeating-linear-gradient(
     90deg,
     rgba(255, 255, 255, 0.3) 0px,
     rgba(255, 255, 255, 0.3) 2px,
     transparent 2px,
     transparent 4px
   );
 }
 
 .style-name {
   color: #fff;
   font-size: 14px;
   font-weight: 500;
 }

 /* 主要内容区域 */
 .player-main {
   position: absolute;
   top: 30%;
   left: 50%;
   transform: translate(-50%, -50%);
   display: flex;
   flex-direction: column;
   align-items: center;
   justify-content: center;
   width: 100%;
   opacity: 0.95;
   pointer-events: none;
 }
 
 .player-main > * {
   pointer-events: auto;
 }
 
 .player-main.visualizer-mode {
   padding: 20px;
 }
 
 /* 经典布局 */
 .classic-layout {
   display: flex;
   flex-direction: column;
   align-items: center;
   justify-content: flex-start;
   width: 100%;
   height: 100%;
 }
 
 /* 音效模式布局 */
 .visualizer-layout {
   display: flex;
   align-items: center;
   width: 100%;
   height: 100%;
   gap: 30px; /* 减小间距 */
 }
 
 /* 音效可视化器 */
 .audio-visualizer {
   flex: 1;
   height: 140px; /* 减小高度 */
   background: linear-gradient(135deg, rgba(0, 0, 0, 0.1) 0%, rgba(0, 30, 60, 0.08) 100%); /* 更透明 */
   border-radius: 16px;
   overflow: hidden;
   position: relative;
   border: 1px solid rgba(255, 255, 255, 0.1);
   box-shadow: 
     0 8px 32px rgba(0, 0, 0, 0.3),
     inset 0 1px 0 rgba(255, 255, 255, 0.1),
     0 0 40px rgba(0, 150, 255, 0.1);
 }
 
 .visualizer-canvas {
   width: 100%;
   height: 100%;
   display: block;
 }
 
 /* 歌词区域 */
 .lyrics-section {
   flex: 2;
   max-width: 480px;
   height: 100%;
   display: flex;
   flex-direction: column;
   align-items: center;
   background: linear-gradient(135deg, rgba(255, 255, 255, 0.05) 0%, rgba(255, 255, 255, 0.02) 100%); /* 更透明 */
   border-radius: 20px;
   padding: 20px; /* 减小内边距 */
   border: 1px solid rgba(255, 255, 255, 0.12);
   box-shadow: 
     0 8px 32px rgba(0, 0, 0, 0.3),
     inset 0 1px 0 rgba(255, 255, 255, 0.15);
 }
 
 /* 经典模式下的歌词区域 */
 .classic-lyrics-section {
   margin-top: 20px; /* 减小顶部边距 */
   width: 100%;
   max-height: 250px; /* 减小最大高度 */
   display: flex;
   flex-direction: column;
   justify-content: center;
   align-items: center;
   opacity: 0.9; /* 设置透明度 */
 }
 
 /* 单行歌词显示样式 */
 .single-lyric-display {
   width: 100%;
   margin-bottom: 20px;
   text-align: center;
   display: flex;
   justify-content: center;
   align-items: center;
 }
 
 .single-lyric-text {
   font-size: 24px;
   font-weight: 600;
   color: #ffffff;
   line-height: 1.5;
   padding: 15px 30px;
   border-radius: 16px;
   background: linear-gradient(135deg, rgba(0, 212, 255, 0.2) 0%, rgba(0, 153, 255, 0.1) 100%);
   border: 1px solid rgba(0, 212, 255, 0.3);
   text-shadow: 0 0 15px rgba(0, 212, 255, 0.7);
   transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
   max-width: 80%;
   white-space: nowrap;
   overflow: hidden;
   text-overflow: ellipsis;
   box-shadow: 0 8px 32px rgba(0, 212, 255, 0.15);
 }
 
 .lyrics-container-classic {
   display: flex;
   flex-direction: column;
   align-items: center;
   justify-content: center;
   width: 100%;
   max-height: 100%;
   overflow-y: auto;
   padding: 10px;
   scroll-behavior: smooth;
 }
 
 .lyrics-container-classic::-webkit-scrollbar {
   width: 4px;
 }
 
 .lyrics-container-classic::-webkit-scrollbar-track {
   background: rgba(255, 255, 255, 0.1);
   border-radius: 2px;
 }
 
 .lyrics-container-classic::-webkit-scrollbar-thumb {
   background: rgba(255, 255, 255, 0.3);
   border-radius: 2px;
 }
 
 .lyrics-line-classic {
   text-align: center;
   font-size: 16px;
   line-height: 2.5;
   color: rgba(255, 255, 255, 0.65);
   transition: all 0.3s ease;
   margin: 4px 0;
   padding: 4px 20px;
   border-radius: 8px;
   font-weight: 400;
   letter-spacing: 0.2px;
   opacity: 0.8;
 }
 
 .lyrics-line-classic.active {
   color: #00d4ff;
   font-size: 18px;
   font-weight: 600;
   opacity: 1;
   text-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
   background: linear-gradient(135deg, rgba(0, 212, 255, 0.1) 0%, rgba(0, 153, 255, 0.05) 100%);
   border: 1px solid rgba(0, 212, 255, 0.2);
 }
 
 .no-lyrics-classic {
   text-align: center;
   color: rgba(255, 255, 255, 0.5);
   font-size: 14px;
   padding: 20px;
 }
 
 /* 歌词加载状态 */
 .lyrics-loading {
   display: flex;
   flex-direction: column;
   align-items: center;
   justify-content: center;
   color: rgba(255, 255, 255, 0.6);
   font-size: 14px;
   padding: 20px;
 }
 
 .loading-spinner {
   width: 20px;
   height: 20px;
   border: 2px solid rgba(255, 255, 255, 0.3);
   border-top: 2px solid #00d4ff;
   border-radius: 50%;
   animation: spin 1s linear infinite;
   margin-bottom: 10px;
 }
 
 @keyframes spin {
   0% { transform: rotate(0deg); }
   100% { transform: rotate(360deg); }
 }
 
 .song-info-mini {
   text-align: center;
   margin-bottom: 30px;
 }
 
 .song-title-mini {
   font-size: 20px;
   font-weight: 600;
   margin: 0 0 8px 0;
   color: #fff;
   letter-spacing: 0.3px;
 }
 
 .song-artist-mini {
   font-size: 14px;
   opacity: 0.7;
   margin: 0;
   color: #fff;
   font-weight: 400;
 }
 
 .lyrics-display {
   flex: 1;
   width: 100%;
   overflow: hidden;
   position: relative;
   border-radius: 12px;
   background: rgba(0, 0, 0, 0.1);
 }
 
 .lyrics-container {
   height: 100%;
   overflow-y: auto;
   padding: 20px;
   display: flex;
   flex-direction: column;
   justify-content: center;
   scroll-behavior: smooth;
 }
 
 .lyrics-container::-webkit-scrollbar {
   width: 3px;
 }
 
 .lyrics-container::-webkit-scrollbar-track {
   background: rgba(255, 255, 255, 0.05);
   border-radius: 2px;
 }
 
 .lyrics-container::-webkit-scrollbar-thumb {
   background: rgba(255, 255, 255, 0.2);
   border-radius: 2px;
 }
 
 .lyrics-line {
   text-align: center;
   font-size: 16px;
   line-height: 2.8;
   color: rgba(255, 255, 255, 0.65);
   transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
   margin: 8px 0;
   padding: 8px 20px;
   border-radius: 12px;
   font-weight: 400;
   letter-spacing: 0.3px;
 }
 
 .lyrics-line.active {
   color: #00d4ff;
   font-size: 20px;
   font-weight: 600;
   transform: scale(1.05);
   background: linear-gradient(135deg, rgba(0, 212, 255, 0.15) 0%, rgba(0, 153, 255, 0.1) 100%);
   text-shadow: 0 0 20px rgba(0, 212, 255, 0.5);
   border: 1px solid rgba(0, 212, 255, 0.3);
 }
 
 .no-lyrics {
   display: flex;
   align-items: center;
   justify-content: center;
   height: 100%;
 }
 
 .lyrics-placeholder {
   text-align: center;
   color: rgba(255, 255, 255, 0.5);
 }
 
 .lyrics-placeholder i {
   font-size: 48px;
   margin-bottom: 16px;
   display: block;
 }
 
 .lyrics-placeholder p {
   margin: 8px 0;
   font-size: 16px;
 }
 
 .lyrics-hint {
   font-size: 14px !important;
   opacity: 0.7;
 }

.album-section {
  display: flex;
  justify-content: center;
  margin: 0;
}

/* 调整歌曲信息和封面的整体布局 */
.classic-layout {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
  height: auto;
  padding: 0;
  gap: 40px;
}

.album-cover {
  width: 220px; /* 减小宽度 */
  height: 220px; /* 减小高度 */
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
  width: 100%;
  max-width: 500px;
  margin: 0 auto;
  position: relative;
  z-index: 10;
  opacity: 1;
  visibility: visible;
  display: flex;
  justify-content: center;
}

.song-info {
  margin: 0;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.song-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
  line-height: 1.2;
  color: #ffffff;
  text-align: center;
  width: 100%;
}

.song-artist {
  font-size: 16px;
  opacity: 0.8;
  margin: 0;
  color: #ffffff;
  text-align: center;
  width: 100%;
}



/* 播放控制区域 */
.player-controls-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 15px 12px; /* 进一步减小高度 */
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7) 0%, rgba(0, 0, 0, 0.4) 50%, transparent 100%); /* 更透明 */
  z-index: 99999;
}

/* 大字模式下，播放控制区域降低层级 */
.player-content.lyrics-bg-mode .player-controls-section {
  z-index: 99998;
}

/* 底部歌词显示 */
.bottom-single-lyric {
  position: fixed;
  bottom: 220px;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  max-width: 600px;
  z-index: 99998;
  display: flex;
  justify-content: center;
  align-items: center;
  pointer-events: auto;
  cursor: pointer;
  transition: all 0.3s ease;
}

.bottom-single-lyric:hover {
  transform: translateX(-50%) scale(1.02);
}

.bottom-lyric-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: auto;
  padding: 0;
  background: transparent;
  pointer-events: auto;
  gap: 8px;
}

/* 歌词行通用样式 */
.lyric-line {
  text-align: center;
  padding: 4px 20px;
  white-space: normal;
  word-wrap: break-word;
  max-width: 90%;
  transition: all 0.3s ease;
  min-height: 24px;
}

/* 上一句歌词 */
.prev-line {
  font-size: 14px;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.4);
  line-height: 1.4;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.4);
}

/* 当前歌词行样式 */
.current-lyric-line {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  line-height: 1.6;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.6);
  animation: lyricFadeIn 0.3s ease;
}

/* 下一句歌词 */
.next-line {
  font-size: 14px;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.4);
  line-height: 1.4;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.4);
}

@keyframes lyricFadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.bottom-lyric-text {
  font-size: 14px;
  font-weight: 500;
  color: #ffffff;
  line-height: 1.1;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 8px 16px;
  border-radius: 8px;
}

.bottom-lyric-text:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: scale(1.05);
}

.progress-section {
  margin-bottom: 18px; /* 减小底部边距 */
  opacity: 1; /* 完全不透明 */
}

.progress-bar {
  position: relative;
  width: 100%;
  height: 4px; /* 减小高度使进度条更细 */
  cursor: pointer;
  overflow: hidden;
  border-radius: 2px;
  background: rgba(0, 0, 0, 0.2); /* 添加轻微背景 */
}

.progress-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 100%;
  background: rgba(255, 255, 255, 0.3); /* 调整背景色 */
  border-radius: 2px;
}

.progress-fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: linear-gradient(90deg, var(--primary) 0%, var(--primary-light) 100%); /* 使用主题色 */
  transition: width 0.1s ease;
  border-radius: 2px;
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.9);
  border: 1px solid var(--primary-light); /* 使用主题色浅色 */
}

.progress-handle {
  position: absolute;
  top: 50%;
  width: 12px;
  height: 12px;
  background: var(--primary); /* 使用主题色 */
  border-radius: 50%;
  transform: translate(-50%, -50%);
  opacity: 1; /* 默认显示手柄 */
  transition: all 0.2s ease;
  cursor: grab;
  box-shadow: 0 0 12px var(--primary); /* 使用主题色 */
  border: 2px solid rgba(255, 255, 255, 0.8);
}

.progress-handle:active {
  cursor: grabbing;
  transform: translate(-50%, -50%) scale(1.1);
}

.progress-bar:hover .progress-handle {
  opacity: 1;
  transform: translate(-50%, -50%) scale(1.1);
}

/* 单行控制按钮布局 */
.single-control-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  opacity: 0.9; /* 设置透明度 */
  position: relative;
  height: 60px;
}

.left-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  position: absolute;
  left: 20px;
  justify-content: flex-start;
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
  gap: 25px;
  flex-shrink: 0;
}

.right-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  position: absolute;
  right: 20px;
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
  background: transparent !important;
  transform: scale(1.05);
}

.control-btn.play:hover {
  background: transparent !important;
  transform: scale(1.1);
}

/* 按钮样式 */
.control-btn {
  background: transparent !important;
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
  box-shadow: none !important;
}

.control-btn:focus {
  outline: none;
  box-shadow: none !important;
}

.control-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

/* 悬停效果已在上方统一设置 */

/* 小按钮样式（左右两侧）*/
.control-btn.small {
  width: 36px;
  height: 36px;
  font-size: 16px;
}

/* 主要按钮样式（中间区域）*/
.control-btn.prev,
.control-btn.next,
.control-btn.love {
  width: 40px;
  height: 40px;
  font-size: 18px;
  transition: all 0.3s ease, transform 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.control-btn.prev:hover:not(:disabled),
.control-btn.next:hover:not(:disabled) {
  transform: scale(1.15) translateY(-2px);
  background: rgba(255, 255, 255, 0.08) !important;
  box-shadow: 0 4px 12px rgba(255, 255, 255, 0.15) !important;
}

.control-btn.play {
  width: 44px;
  height: 44px;
  font-size: 20px; /* 减小图标大小 */
  background: transparent !important; /* 强制透明背景 */
  box-shadow: none !important; /* 强制移除任何阴影 */
}

.control-btn.play:hover {
  background: transparent !important;
  transform: scale(1.1);
}

.control-btn.love {
  color: #fff;
  background: transparent !important;
}

.control-btn.love.loved {
  color: #ff3040;
  background: transparent !important;
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
    width: 180px;
    height: 180px;
  }
  
  .song-title {
    font-size: 20px;
  }
  
  .player-controls-section {
    padding: 15px 20px 15px;
    bottom: 0;
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
    font-size: 24px; /* 移动端也放大图标 */
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
  width: 32px;
  height: 32px;
  background: url('@/assets/star.svg') no-repeat center;
  background-size: contain;
}

.icon-pause::before {
  content: '';
  display: inline-block;
  width: 32px;
  height: 32px;
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

/* 保持全屏播放器的最高优先级，但低于播放列表面板 */
 .fullscreen-player.is-visible {
   z-index: 99998 !important;
 }
 
 .icon-style::before {
   content: '';
   display: inline-block;
   width: 24px;
   height: 24px;
   background: url('@/assets/展开.svg') no-repeat center;
   background-size: contain;
   transform: rotate(45deg);
 }
 
 .icon-music-note::before {
   content: '';
   display: inline-block;
   width: 48px;
   height: 48px;
   background: url('@/assets/star.svg') no-repeat center;
   background-size: contain;
   opacity: 0.5;
 }
 
 /* 响应式设计 - 音效模式 */
 @media (max-width: 768px) {
   .visualizer-layout {
     flex-direction: column;
     gap: 12px; /* 减小间距 */
     padding: 12px; /* 减小内边距 */
     height: calc(100vh - 120px); /* 减小高度 */
   }
   
   .audio-visualizers {
     gap: 12px;
     height: 160px;
     order: 2;
   }
   
   .audio-visualizer {
     height: 160px;
     border-radius: 16px;
   }
   
   .lyrics-section {
     order: 1;
     max-width: none;
     height: auto;
     min-height: 200px; /* 减小最小高度 */
     padding: 15px; /* 减小内边距 */
     border-radius: 16px;
     opacity: 0.9; /* 设置透明度 */
   }
   
   .song-info-mini {
     margin-bottom: 20px;
   }
   
   .song-title-mini {
     font-size: 18px;
   }
   
   .song-artist-mini {
     font-size: 13px;
   }
   
   .lyrics-line {
     font-size: 15px;
     line-height: 2.4;
     margin: 5px 0;
     padding: 5px 12px;
   }
   
   .lyrics-line.active {
     font-size: 17px;
 }
}

/* 黑色主题下的全屏播放器样式 - 不使用background简写，避免覆盖子元素的背景图片 */
[data-theme="black"] .fullscreen-player {
  background-color: rgba(0, 0, 0, 0.0) !important;
}

/* 移除黑色主题下的背景色设置，让背景图片完全显示 */
[data-theme="black"] .player-background {
  background-color: transparent !important;
  /* 移除inherit设置，允许组件计算属性中的背景图片正常显示 */
}

[data-theme="black"] .player-header {
  background: rgba(0, 0, 0, 0.8) !important;
}

[data-theme="black"] .header-btn {
  color: white !important;
}

[data-theme="black"] .header-btn:hover {
  background: #1a1a1a !important;
}

[data-theme="black"] .header-info .playing-text {
  color: white !important;
}

[data-theme="black"] .header-info .source-text {
  color: #cccccc !important;
}

[data-theme="black"] .song-title {
  color: white !important;
}

[data-theme="black"] .song-artist {
  color: #cccccc !important;
}

[data-theme="black"] .solid-color-background {
  background: #000000 !important;
  background-size: 100% 100% !important;
  opacity: 1 !important;
}

/* 黑色主题下的音频背景波浪 */
[data-theme="black"] .audio-wave {
  background: radial-gradient(ellipse at center, rgba(255, 255, 255, 0.15) 0%, rgba(255, 255, 255, 0.05) 40%, transparent 70%) !important;
}

[data-theme="black"] .gradient-overlay-bg {
  display: none !important;
}

/* 完整歌词模态框样式 */
.full-lyrics-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100002;
  backdrop-filter: blur(10px);
}

.full-lyrics-content {
  background: rgba(0, 0, 0, 0.9);
  border-radius: 20px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.5);
  overflow: hidden;
  animation: lyricsFadeIn 0.3s ease;
}

.lyrics-header {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
}

.lyrics-header h3 {
  font-size: 18px;
  margin: 0 0 5px 0;
  font-weight: 600;
}

.lyrics-header p {
  font-size: 14px;
  margin: 0;
  opacity: 0.8;
}

.close-lyrics-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  background: none;
  border: none;
  color: #fff;
  font-size: 20px;
  cursor: pointer;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.close-lyrics-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: scale(1.1);
}

.full-lyrics-container {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  overflow-x: hidden;
}

.lyrics-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.full-lyrics-line {
  padding: 8px 20px;
  margin: 4px 0;
  font-size: 16px;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.7);
  text-align: center;
  width: 100%;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 10px;
}

.full-lyrics-line:hover {
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.05);
  transform: translateX(5px);
}

.full-lyrics-line.active {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  transform: scale(1.05);
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 2px 10px rgba(255, 255, 255, 0.1);
}

/* 歌词加载状态 */
.lyrics-loading {
  text-align: center;
  padding: 40px 0;
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}

/* 无歌词状态 */
.no-lyrics .lyrics-placeholder {
  text-align: center;
  padding: 60px 0;
  color: rgba(255, 255, 255, 0.5);
}

.no-lyrics .lyrics-placeholder i {
  font-size: 48px;
  display: block;
  margin-bottom: 15px;
  opacity: 0.3;
}

.no-lyrics .lyrics-placeholder p {
  font-size: 16px;
  margin: 0;
}

/* 过渡动画 */
.lyrics-modal-enter-active,
.lyrics-modal-leave-active {
  transition: opacity 0.3s ease;
}

.lyrics-modal-enter-from,
.lyrics-modal-leave-to {
  opacity: 0;
}

.lyrics-modal-enter-active .full-lyrics-content,
.lyrics-modal-leave-active .full-lyrics-content {
  transition: transform 0.3s ease;
}

.lyrics-modal-enter-from .full-lyrics-content {
  transform: scale(0.9) translateY(20px);
}

.lyrics-modal-leave-to .full-lyrics-content {
  transform: scale(0.9) translateY(20px);
}

@keyframes lyricsFadeIn {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

/* 自定义滚动条 */
.full-lyrics-container::-webkit-scrollbar {
  width: 6px;
}

.full-lyrics-container::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

.full-lyrics-container::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.full-lyrics-container::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .full-lyrics-content {
    width: 95%;
    max-height: 90vh;
  }
  
  .lyrics-header {
    padding: 15px;
  }
  
  .lyrics-header h3 {
    font-size: 16px;
  }
  
  .lyrics-header p {
    font-size: 13px;
  }
  
  .full-lyrics-container {
    padding: 15px;
  }
  
  .full-lyrics-line {
    font-size: 15px;
    padding: 6px 15px;
  }
  
  .full-lyrics-line.active {
    font-size: 17px;
  }
}

/* 黑色主题适配 */
[data-theme="black"] .full-lyrics-modal {
  background: rgba(0, 0, 0, 0.9);
}

[data-theme="black"] .full-lyrics-content {
  background: rgba(0, 0, 0, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

[data-theme="black"] .lyrics-header {
  border-bottom-color: rgba(255, 255, 255, 0.1);
}

[data-theme="black"] .full-lyrics-line {
  color: #cccccc;
}

[data-theme="black"] .full-lyrics-line:hover {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.05);
}

[data-theme="black"] .full-lyrics-line.active {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.1);
}

[data-theme="black"] .progress-bar {
  background: #333333 !important;
}

[data-theme="black"] .progress-fill {
  background: #cccccc !important;
}

[data-theme="black"] .progress-handle {
  background: white !important;
}

[data-theme="black"] .time-display {
  color: #cccccc !important;
}

[data-theme="black"] .control-button {
  color: white !important;
}

[data-theme="black"] .control-button:hover {
  background: #1a1a1a !important;
}

[data-theme="black"] .volume-control {
  background: #1a1a1a !important;
}

[data-theme="black"] .style-panel {
  background: #1a1a1a !important;
  border: 1px solid #333333 !important;
}

[data-theme="black"] .style-option {
  color: #cccccc !important;
}

[data-theme="black"] .style-option:hover {
  background: #333333 !important;
}

[data-theme="black"] .style-option.active {
  background: #2a2a2a !important;
  color: white !important;
}

[data-theme="black"] .audio-visualizer {
  background: #1a1a1a !important;
  border: 1px solid #333333 !important;
}

[data-theme="black"] .lyrics-section {
  background: rgba(0, 0, 0, 0.8) !important;
}

[data-theme="black"] .lyrics-content {
  color: #cccccc !important;
}

[data-theme="black"] .lyrics-line.active {
  color: white !important;
}
</style>
