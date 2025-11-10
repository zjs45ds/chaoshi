// 全屏播放器组件
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
         <!-- 经典模式 -->
         <div class="classic-layout">
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
      
      <!-- 底部单行歌词显示 -->
      <div class="bottom-single-lyric">
        <div class="bottom-lyric-content">
          <div class="bottom-lyric-text" v-if="currentLyricText" @click="toggleFullLyrics">
            {{ currentLyricText }}
          </div>
          <div class="bottom-lyric-text" v-else-if="!isLoadingLyrics">
            正在播放音乐...
          </div>
          <div class="bottom-lyric-text" v-else>
            加载歌词中...
          </div>
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
  currentSong, currentIndex, playlist, playMode, playModeText, currentSongLyrics, isLoadingLyrics, currentLyricIndex,
  progress, currentTimeFormatted, durationFormatted, currentLyricText,
  togglePlay, playNext, playPrevious, togglePlayMode, seekToProgress, setVolumeLevel, toggleMute,
  hasSong, hasPlaylist, loadLyrics as loadSongLyrics, getCurrentLyricIndex
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
     console.error('切换收藏状态失败:', error)
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
     console.error('初始化音效可视化失败:', error)
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
     console.warn('无法连接真实音频源，使用模拟数据:', error)
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
const toggleVolumePanel = () => {
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
       loadSongLyrics()
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
    document.removeEventListener('keydown', handleKeydown)
    document.removeEventListener('click', handleClickOutside)
    document.removeEventListener('touchmove', handleTouchMove)
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
   flex: 1;
   display: flex;
   flex-direction: column;
   align-items: center;
   justify-content: center;
   padding: 30px 15px; /* 减小内边距 */
   min-height: 0;
   opacity: 0.95; /* 设置透明度 */
 }
 
 .player-main.visualizer-mode {
   padding: 20px;
 }
 
 /* 经典布局 */
 .classic-layout {
   display: flex;
   flex-direction: column;
   align-items: center;
   justify-content: center;
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
  margin-bottom: 20px; /* 减少下边距，使封面照片上移 */
}

/* 调整歌曲信息和封面的整体布局 */
.classic-layout {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
  height: 100%;
  padding-top: 30px; /* 减小顶部内边距 */
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
  max-width: 500px;
  margin-bottom: 40px;
  position: relative;
  z-index: 10;
  opacity: 1;
  visibility: visible;
}

.song-info {
  margin-bottom: 20px;
}

.song-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
  line-height: 1.2;
  color: #ffffff;
}

.song-artist {
  font-size: 16px;
  opacity: 0.8;
  margin: 0;
  color: #ffffff;
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

/* 底部单行歌词显示 */
.bottom-single-lyric {
  position: fixed;
  bottom: 180px; /* 调整底部距离，确保歌词在播放器下方 */
  left: 0;
  right: 0;
  text-align: center;
  z-index: 99998;
  opacity: 0.8; /* 设置透明度 */
  cursor: pointer;
  transition: all 0.2s ease;
}

.bottom-single-lyric:hover {
  opacity: 1;
  transform: scale(1.02);
}

.bottom-lyric-content {
  display: inline-block;
  background: transparent; /* 设置为透明背景 */
  padding: 3px 15px; /* 进一步减小内边距，降低高度 */
  border-radius: 20px;
  /* 移除模糊效果 */
  box-shadow: none; /* 移除阴影 */
  border: none; /* 移除边框 */
  max-width: 80%;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.bottom-lyric-text {
  font-size: 14px; /* 减小字体大小 */
  font-weight: 500;
  color: #ffffff;
  line-height: 1.1;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.5); /* 轻微阴影增加可读性 */
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
  background: rgba(0, 0, 0, 0.6); /* 更透明 */
  padding: 8px 6px; /* 减小高度 */
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
  gap: 6px; /* 减小间距 */
  min-height: 60px; /* 减小最小高度 */
}

/* 垂直音量滑块 */
.volume-slider-vertical {
  width: 4px;
  height: 60px; /* 减小高度 */
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
