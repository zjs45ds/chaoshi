/**
 * 音乐风格播放器 - 核心状态管理
 */

import { ref, computed, reactive, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getSongStreamUrl, getSongLyrics } from '@/api/song.js'
import { getArtistById } from '@/api/artist.js'
import { checkLoginForAction } from '@/utils/authCheck.js'

export const isPlaying = ref(false)
export const isPaused = ref(false)
export const isLoading = ref(false)
export const currentTime = ref(0)
export const duration = ref(0)
export const volume = ref(0.8)
export const isMuted = ref(false)

// 歌词相关状态
export const currentSongLyrics = ref([])
export const isLoadingLyrics = ref(false)
export const currentLyricIndex = ref(-1) 

// 当前歌曲信息
export const currentSong = ref(null)
export const currentIndex = ref(0)

// 播放列表
export const playlist = ref([])
export const shuffledPlaylist = ref([])

// 播放模式：sequence(顺序), loop(单曲循环), shuffle(随机)
export const playMode = ref('sequence')

// 音频元素
export const audioElement = ref(null)

// 音频分析相关
let audioContext = null
let analyser = null
let dataArray = null
let sourceNode = null
export const audioData = ref([]) // 音频频率数据，用于可视化

// 初始化音频分析器
export const initAudioAnalyser = () => {
  if (!audioElement.value || audioContext) return
  
  try {
    audioContext = new (window.AudioContext || window.webkitAudioContext)()
    analyser = audioContext.createAnalyser()
    analyser.fftSize = 256
    analyser.smoothingTimeConstant = 0.8
    
    const bufferLength = analyser.frequencyBinCount
    dataArray = new Uint8Array(bufferLength)
    
    sourceNode = audioContext.createMediaElementSource(audioElement.value)
    sourceNode.connect(analyser)
    analyser.connect(audioContext.destination)
  } catch (e) {
    console.warn('音频分析器初始化失败:', e)
  }
}

// 获取音频频率数据
export const getAudioFrequencyData = () => {
  if (!analyser || !dataArray) return []
  
  analyser.getByteFrequencyData(dataArray)
  
  // 返回简化的数据（5个频段，对应5个波浪）
  const bands = 5
  const bandSize = Math.floor(dataArray.length / bands)
  const result = []
  
  for (let i = 0; i < bands; i++) {
    let sum = 0
    for (let j = 0; j < bandSize; j++) {
      sum += dataArray[i * bandSize + j]
    }
    // 归一化到 0-1 范围
    result.push(sum / bandSize / 255)
  }
  
  return result
}

// 恢复音频上下文（用于解决自动播放限制）
export const resumeAudioContext = async () => {
  if (audioContext && audioContext.state === 'suspended') {
    await audioContext.resume()
  }
}

export const magicColors = ref({
  primary: '#ec4899',
  secondary: '#f472b6', 
  background: 'linear-gradient(135deg, #ec4899 0%, #f472b6 100%)',
  isDark: false
})

export const playerTheme = ref('magic') 

export const progress = computed(() => {
  if (!duration.value) return 0
  return (currentTime.value / duration.value) * 100
})

export const formatTime = (seconds) => {
  if (!seconds || isNaN(seconds)) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// 当前时间格式化
export const currentTimeFormatted = computed(() => formatTime(currentTime.value))

// 总时长格式化  
export const durationFormatted = computed(() => formatTime(duration.value))

// 当前显示的歌词文本
export const currentLyricText = computed(() => {
  if (currentLyricIndex.value >= 0 && currentSongLyrics.value && currentSongLyrics.value.length > 0) {
    return currentSongLyrics.value[currentLyricIndex.value]?.text || ''
  }
  return ''
})

// 播放模式文本
export const playModeText = computed(() => {
  const modeTexts = {
    sequence: '顺序播放',
    loop: '单曲循环',
    shuffle: '随机播放'
  }
  return modeTexts[playMode.value] || '顺序播放'
})

// 是否有歌曲
export const hasSong = computed(() => currentSong.value && currentSong.value.id)

// 是否有播放列表
export const hasPlaylist = computed(() => playlist.value.length > 0)

// ==================== 核心播放功能 ====================

/**
 * 初始化音频元素
 */
export const initAudioElement = () => {
  if (typeof window === 'undefined') return

  const audio = document.createElement('audio')
  audio.preload = 'metadata'
  audio.crossOrigin = 'anonymous'

  audio.addEventListener('loadstart', () => {
    isLoading.value = true
  })
  
  audio.addEventListener('loadedmetadata', () => {
    duration.value = audio.duration || 0
    isLoading.value = false
  })
  
  audio.addEventListener('timeupdate', () => {
    currentTime.value = audio.currentTime || 0
    
    // 更新当前歌词索引
    if (currentSongLyrics.value && currentSongLyrics.value.length > 0) {
      const index = getCurrentLyricIndex(currentTime.value, currentSongLyrics.value)
      if (index !== currentLyricIndex.value) {
        currentLyricIndex.value = index

        if (typeof window !== 'undefined') {
          window.dispatchEvent(new CustomEvent('lyric-updated', {
            detail: {
              currentIndex: index,
              lyrics: currentSongLyrics.value,
              currentLyric: index >= 0 ? currentSongLyrics.value[index] : null
            }
          }))
        }
      }
    }
  })
  
  audio.addEventListener('play', () => {
    isPlaying.value = true
    isPaused.value = false
  })
  
  audio.addEventListener('pause', () => {
    isPlaying.value = false
    isPaused.value = true
  })
  
  audio.addEventListener('ended', () => {
    onSongEnded()
  })
  
  audio.addEventListener('error', (e) => {
    isLoading.value = false
    isPlaying.value = false
  })

  // 音量变化
  audio.addEventListener('volumechange', () => {
    volume.value = audio.volume
    isMuted.value = audio.muted
  })
  
  audioElement.value = audio
  
  // 设置初始音量
  audio.volume = volume.value
}


export const loadSong = async (song) => {
  if (!song) {
    return false
  }
  
  // 确保音频元素已初始化
  if (!audioElement.value) {
    initAudioElement()

    if (!audioElement.value) {
      return false
    }
  }
  
  try {
    if (isPlaying.value) {
      try {
        await audioElement.value.pause()
0      } catch (pauseError) {
        // console.warn('暂停当前播放失败:', pauseError)
      }
    }
    isPlaying.value = false
    isPaused.value = true
    isLoading.value = true
    currentTime.value = 0

    currentSong.value = song

    if (currentSong.value) {
      if (!currentSong.value.artist) {
        currentSong.value.artist = currentSong.value.artistName || 
                                  currentSong.value.singer || 
                                  currentSong.value.singerName || 
                                  currentSong.value.artists?.join('、') || 
                                  '未知艺术家'
      }

      checkAndFetchArtistName(currentSong.value)
    }
    currentLyricIndex.value = -1 
    
    if (song.id) {
      loadLyrics(song.id).catch(() => {
     
      })
    }

    let audioUrl = null
    let metadata = {}
    
   
    if (song.id) {
      try {
        const streamResponse = await getSongStreamUrl(song.id)
        
        if (streamResponse && (streamResponse.success || streamResponse.code === 200)) {
          const responseData = streamResponse.data || streamResponse
          
          audioUrl = responseData.audioUrl || responseData.streamUrl || responseData.filePath || responseData.url

          const artistInfo = responseData.artist || 
                            responseData.artistName || 
                            responseData.singer || 
                            responseData.singerName || 
                            responseData.artists?.join('、')
          
          if (artistInfo) {
            metadata.artist = artistInfo
            currentSong.value.artist = artistInfo
          }
          if (responseData.cover) {
            metadata.cover = responseData.cover
            currentSong.value.cover = responseData.cover
            currentSong.value.albumCover = responseData.cover
          }

          checkAndFetchArtistName(currentSong.value)
          if (responseData.duration && !currentSong.value.duration) {
            metadata.duration = responseData.duration
            currentSong.value.duration = responseData.duration
          }
          // 只有在当前歌曲没有名称时，才使用API返回的名称
          if ((responseData.title || responseData.name) && !song.name) {
            metadata.title = responseData.title || responseData.name
            currentSong.value.name = responseData.title || responseData.name
          }

        }
      } catch (apiError) {
        // console.warn('获取音频流URL失败:', apiError)
      }
    }
    
    if (!audioUrl) {
      audioUrl = song.filePath || 
                song.audioUrl || 
                song.file_path || 
                song.url || 
                song.streamUrl ||
                song.stream_url ||
                song.audio_path ||
                song.fileUrl ||
                song.mediaUrl ||
                song.audio ||
                song.mp3Url ||
                song.mp3_url ||
                song.src ||
                song.source
    }
    
    if (audioUrl && !isValidAudioUrl(audioUrl)) {
    }
    
    if (audioUrl) {
      audioElement.value.crossOrigin = 'anonymous'
      audioElement.value.preload = 'metadata'
      if (audioUrl.includes('minio') || audioUrl.includes('9000')) {
        audioElement.value.preload = 'none' 
      }
      
      audioElement.value.src = audioUrl
      const loadPromise = new Promise((resolve, reject) => {
        const handleLoadedData = () => {
          cleanup()
          resolve()
        }
        
        const handleError = (error) => {
          cleanup()
          reject(error)
        }
        
        const handleTimeout = () => {
          cleanup()
          reject(new Error('音频加载超时'))
        }
        
        const cleanup = () => {
          audioElement.value?.removeEventListener('loadeddata', handleLoadedData)
          audioElement.value?.removeEventListener('error', handleError)
          clearTimeout(timeoutId)
        }
        
        audioElement.value.addEventListener('loadeddata', handleLoadedData)
        audioElement.value.addEventListener('error', handleError)
        
        const timeoutId = setTimeout(handleTimeout, 10000) 
      })
      
      audioElement.value.load()
      await loadPromise

      // 在后台异步提取颜色，不阻塞主流程
      if (currentSong.value.cover || currentSong.value.albumCover) {
        extractMagicColors(currentSong.value.cover || currentSong.value.albumCover).catch(() => {
          // 静默处理错误
        })
      }

      window.dispatchEvent(new CustomEvent('song-loaded', {
        detail: { 
          song: currentSong.value, 
          audioUrl,
          metadata 
        }
      }))

      isLoading.value = false
      return true
    } else {
      return false
    }
  } catch (error) {
    // console.error('加载歌曲失败:', error)
    isLoading.value = false
    return false
  }
}

/**
 * 验证音频URL的有效性
 */
const isValidAudioUrl = (url) => {
  if (!url || typeof url !== 'string') return false

  try {
    new URL(url)
  } catch {
    return false
  }

  const audioExtensions = ['.mp3', '.wav', '.ogg', '.m4a', '.flac', '.aac']
  const hasAudioExtension = audioExtensions.some(ext => url.toLowerCase().includes(ext))
  const isMinIOPath = url.includes('minio') || url.includes('/audio/')
  
  return hasAudioExtension || isMinIOPath
}

/**
 * 播放/暂停切换
 */
export const togglePlay = async () => {
  if (!audioElement.value) {
    return
  }

  try {
    if (isPlaying.value) {
      await audioElement.value.pause()
    } else {
      await audioElement.value.play()
    }
  } catch (error) {
    if (error.name === 'NotAllowedError' || error.message.includes('autoplay')) {
      isPlaying.value = false
      isPaused.value = true
      ElMessage.warning('请点击播放按钮开始播放音乐')
    }
  }
}

/**
 * 播放歌曲
 */
export const playSong = async (song) => {
  // 检查登录状态
  const isLoggedIn = await checkLoginForAction('播放歌曲')
  
  if (!isLoggedIn) {
    return false
  }

  if (!song) {
    return false
  }

  if (!audioElement.value) {
    initAudioElement()

    if (!audioElement.value) {
      return false
    }
  }

  const loaded = await loadSong(song)
  
  if (loaded) {

    try {

      if (audioElement.value.readyState >= 2) {
        const playPromise = audioElement.value.play()
        
        if (playPromise !== undefined) {
          playPromise.then(() => {
            isPlaying.value = true
            isPaused.value = false
          }).catch(playError => {
            isPlaying.value = false
            isPaused.value = true
          })
        }
        
        return true
      } else {
        const playWhenReady = new Promise((resolve) => {
          const handleCanPlay = () => {
            audioElement.value.removeEventListener('canplay', handleCanPlay)
            resolve(true)
          }
          
          const handleError = () => {
            audioElement.value.removeEventListener('error', handleError)
            resolve(false)
          }
          
          audioElement.value.addEventListener('canplay', handleCanPlay)
          audioElement.value.addEventListener('error', handleError)

          setTimeout(() => {
            audioElement.value.removeEventListener('canplay', handleCanPlay)
            audioElement.value.removeEventListener('error', handleError)
            resolve(false)
          }, 5000)
        })
        
        const readyToPlay = await playWhenReady
        
        if (readyToPlay) {
          try {
            await audioElement.value.play()
            isPlaying.value = true
            isPaused.value = false
            return true
          } catch (playError) {
              // console.warn('播放失败，尝试恢复:', playError)
              isPlaying.value = false
              isPaused.value = true
              return false
            }
        } else {
          return false
        }
      }
    } catch (playError) {
      // console.error('播放歌曲时发生错误:', playError)
      isPlaying.value = false
      isPaused.value = true
      return false
    }
  }
  return false
}

/**
 * 设置播放进度
 */
export const seekTo = (percentage) => {
  if (!audioElement.value || !duration.value) return
  
  const targetTime = (percentage / 100) * duration.value
  audioElement.value.currentTime = targetTime
  currentTime.value = targetTime
}

/**
 * 设置播放进度（百分比）
 */
export const seekToProgress = (percentage) => {
  if (!audioElement.value || !duration.value) return
  
  const targetTime = percentage * duration.value
  audioElement.value.currentTime = targetTime
  currentTime.value = targetTime
}

/**
 * 设置音量
 */
export const setVolume = (vol) => {
  if (!audioElement.value) return
  
  const newVolume = Math.max(0, Math.min(1, vol))
  audioElement.value.volume = newVolume
  volume.value = newVolume
  
  if (newVolume === 0) {
    isMuted.value = true
  } else if (isMuted.value) {
    isMuted.value = false
  }
}

/**
 * 设置音量级别（0-1）
 */
export const setVolumeLevel = (level) => {
  if (!audioElement.value) return
  
  const newVolume = Math.max(0, Math.min(1, level))
  audioElement.value.volume = newVolume
  volume.value = newVolume
  
  if (newVolume === 0) {
    isMuted.value = true
  } else if (isMuted.value) {
    isMuted.value = false
  }
}

/**
 * 静音切换
 */
export const toggleMute = () => {
  if (!audioElement.value) return
  
  audioElement.value.muted = !audioElement.value.muted
  isMuted.value = audioElement.value.muted
}

// ==================== 播放列表管理 ====================

/**
 * 添加歌曲到播放列表
 */
export const addToPlaylist = (song, playNow = false) => {
  if (!song || !song.id) return false
  
  // 检查是否已存在
  const existingIndex = playlist.value.findIndex(item => item.id === song.id)
  if (existingIndex === -1) {
    playlist.value.push(song)
  }
  
  if (playNow) {
    const targetIndex = existingIndex !== -1 ? existingIndex : playlist.value.length - 1
    playByIndex(targetIndex)
  }
  
  return true
}

/**
 * 添加歌曲到下一首播放
 */
export const addToPlayNext = (song) => {
  if (!song || !song.id) return false

  const existingIndex = playlist.value.findIndex(item => item.id === song.id)
  if (existingIndex !== -1) {
    playlist.value.splice(existingIndex, 1)
    if (existingIndex <= currentIndex.value) {
      currentIndex.value--
    }
  }
  
  // 插入到当前播放歌曲的下一首位置
  const insertIndex = currentIndex.value + 1
  playlist.value.splice(insertIndex, 0, song)
  
  return true
}

/**
 * 添加歌曲到播放列表第一位并立即播放
 * 如果歌曲已存在，则移动到第一位，保持其他歌曲顺序不变
 */
const addToPlaylistFirst = (song) => {
  if (!song || !song.id) return false

  const existingIndex = playlist.value.findIndex(item => item.id === song.id)
  
  if (existingIndex !== -1) {

    if (existingIndex === 0) {
      return playByIndex(0)
    }

    const [removedSong] = playlist.value.splice(existingIndex, 1)
    
    // 调整当前播放索引
    if (existingIndex <= currentIndex.value) {
      currentIndex.value = Math.max(0, currentIndex.value - 1)
    }
    
    // 插入到第一位
    playlist.value.unshift(removedSong)
    
    // 调整当前播放索引（因为插入了歌曲到第一位）
    if (currentIndex.value >= 0) {
      currentIndex.value++
    }
  
  } else {
    // 插入到播放列表第一位
    playlist.value.unshift(song)
    
    // 调整当前播放索引（因为插入了新歌曲）
    if (currentIndex.value >= 0) {
      currentIndex.value++
    }
  }

  return playByIndex(0)
}

/**
 * 批量添加歌曲
 */
export const addMultipleToPlaylist = async (songs, playFirst = false) => {
  if (!Array.isArray(songs) || songs.length === 0) return false
  
  // 当需要立即播放第一首时（播放全部功能），清空现有播放列表
  if (playFirst) {
    playlist.value = []
    currentIndex.value = 0
    // CONSOLE LOG REMOVED: console.log('播放全部：已清空现有播放列表')
  }
  
  // 添加新歌曲，同时预处理每首歌曲的艺术家信息
  songs.forEach(song => {
    if (song && song.id) {
      // 预处理艺术家信息
      const processedSong = { ...song }
      
      // 确保艺术家信息正确设置
      if (!processedSong.artist) {
        processedSong.artist = processedSong.artistName || 
                              processedSong.singer || 
                              processedSong.singerName || 
                              processedSong.artists?.join('、') || 
                              '未知艺术家'
      }
      
      const existingIndex = playlist.value.findIndex(item => item.id === processedSong.id)
      if (existingIndex === -1) {
        playlist.value.push(processedSong)
      }
    }
  })
  
  if (playFirst && playlist.value.length > 0) {
    const success = await playByIndex(0)
    return success
  }
  
  return true
}

/**
 * 根据索引播放歌曲
 */
export const playByIndex = async (index) => {
  if (index < 0 || index >= playlist.value.length) {
    return false
  }

  currentIndex.value = index
  const song = playlist.value[index]
  
  try {
    const result = await playSong(song)
    
    if (result) {
      setTimeout(() => {
        if (!isPlaying.value && audioElement.value) {
        }
      }, 1000)
      
      return true
    } else {
      return false
    }
  } catch (error) {
    return false
  }
}

/**
 * 上一首
 */
export const playPrevious = () => {
  if (!hasPlaylist.value) return false
  
  let prevIndex
  
  if (playMode.value === 'shuffle') {
    prevIndex = Math.floor(Math.random() * playlist.value.length)
  } else {
    prevIndex = currentIndex.value - 1
    if (prevIndex < 0) {
      prevIndex = playlist.value.length - 1
    }
  }
  
  return playByIndex(prevIndex)
}

/**
 * 下一首
 */
export const playNext = () => {
  if (!hasPlaylist.value) return false
  
  let nextIndex
  
  if (playMode.value === 'shuffle') {
    nextIndex = Math.floor(Math.random() * playlist.value.length)
  } else {
    nextIndex = currentIndex.value + 1
    if (nextIndex >= playlist.value.length) {
      nextIndex = 0
    }
  }
  
  return playByIndex(nextIndex)
}

const onSongEnded = async () => {

  try {
    if (playMode.value === 'loop') {
      if (audioElement.value) {

        isPlaying.value = false
        isPaused.value = true

        audioElement.value.currentTime = 0
        
        try {
          const playPromise = audioElement.value.play()
          
          if (playPromise !== undefined) {
            await playPromise
            isPlaying.value = true
            isPaused.value = false
          }
        } catch (loopError) {
          await playNextWithRetry()
        }
      }
    } else {

      await playNextWithRetry()
    }
  } catch (error) {
    isPlaying.value = false
    isPaused.value = true
  }
}

/**
 * 带重试机制的下一首播放函数
 */
const playNextWithRetry = async (retryCount = 0, maxRetries = 2) => {
  try {
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const result = await playNext()
    
    if (result) {
      return true
    } else {

      if (retryCount < maxRetries) {
        await new Promise(resolve => setTimeout(resolve, 300))
        return playNextWithRetry(retryCount + 1, maxRetries)
      } else {
        isPlaying.value = false
        isPaused.value = true
        return false
      }
    }
  } catch (error) {
    return false
  }
}

/**
 * 切换播放模式
 */
export const togglePlayMode = () => {
  const modes = ['sequence', 'loop', 'shuffle']
  const currentModeIndex = modes.indexOf(playMode.value)
  const nextModeIndex = (currentModeIndex + 1) % modes.length
  playMode.value = modes[nextModeIndex]
  
  return playMode.value
}

/**
 * 清空播放列表
 */
export const clearPlaylist = () => {
  playlist.value = []
  currentIndex.value = 0
  
  if (audioElement.value) {
    audioElement.value.pause()
    audioElement.value.currentTime = 0
  }
  
  currentSong.value = null
  isPlaying.value = false
}

// ==================== 魔法色彩系统 - QQ音乐特色 ====================

/**
 * 从专辑封面提取魔法色彩
 */
export const extractMagicColors = async (coverUrl) => {
  if (!coverUrl) return
  
  try {
    // 创建图片元素
    const img = new Image()
    img.crossOrigin = 'anonymous'
    
    return new Promise((resolve) => {
      img.onload = () => {
        try {
          // 创建canvas提取色彩 - 限制canvas大小以提高性能
          const canvas = document.createElement('canvas')
          const ctx = canvas.getContext('2d')
          
          // 限制canvas最大尺寸为200x200，保持宽高比
          const maxSize = 200
          let width = img.width
          let height = img.height
          
          if (width > maxSize || height > maxSize) {
            const ratio = Math.min(maxSize / width, maxSize / height)
            width = Math.floor(width * ratio)
            height = Math.floor(height * ratio)
          }
          
          canvas.width = width
          canvas.height = height
          ctx.drawImage(img, 0, 0, width, height)
          
          // 提取主色调
          const colors = extractDominantColors(ctx, width, height)
          
          // 生成魔法色
          const magic = generateMagicColors(colors)
          magicColors.value = magic
          
          resolve(magic)
        } catch (error) {
          setDefaultMagicColors()
          resolve(magicColors.value)
        }
      }
      
      img.onerror = () => {
        setDefaultMagicColors()
        resolve(magicColors.value)
      }
      
      img.src = coverUrl
    })
  } catch (error) {
    setDefaultMagicColors()
  }
}

/**
 * 提取主要颜色
 */
const extractDominantColors = (ctx, width, height) => {
  const imageData = ctx.getImageData(0, 0, width, height)
  const data = imageData.data
  const colorMap = {}
  
  // 增加采样步长以减少处理的像素数量
  const sampleStep = Math.max(4, Math.floor(Math.sqrt(width * height) / 50))
  
  for (let i = 0; i < data.length; i += sampleStep * 4) {
    const r = data[i]
    const g = data[i + 1]
    const b = data[i + 2]
    const alpha = data[i + 3]
    
    if (alpha > 125) { 
      // 对颜色进行分组，减少相似颜色的处理
      const colorKey = `${Math.floor(r / 8) * 8},${Math.floor(g / 8) * 8},${Math.floor(b / 8) * 8}`
      colorMap[colorKey] = (colorMap[colorKey] || 0) + 1
    }
  }
  
  // 找出使用最多的颜色
  const sortedColors = Object.entries(colorMap)
    .sort(([,a], [,b]) => b - a)
    .slice(0, 5)
    .map(([color]) => {
      const [r, g, b] = color.split(',').map(Number)
      return { r, g, b }
    })
  
  return sortedColors
}

/**
 * 生成魔法色彩方案
 */
const generateMagicColors = (dominantColors) => {
  if (!dominantColors || dominantColors.length === 0) {
    return setDefaultMagicColors()
  }
  
  const primaryColor = dominantColors[0]
  const secondaryColor = dominantColors[1] || primaryColor

  const primaryHsl = rgbToHsl(primaryColor.r, primaryColor.g, primaryColor.b)
  const secondaryHsl = rgbToHsl(secondaryColor.r, secondaryColor.g, secondaryColor.b)

  const enhancedPrimary = {
    h: primaryHsl.h,
    s: Math.min(100, primaryHsl.s + 20),
    l: Math.max(40, Math.min(70, primaryHsl.l))
  }
  
  const enhancedSecondary = {
    h: secondaryHsl.h,
    s: Math.min(100, secondaryHsl.s + 15),
    l: Math.max(45, Math.min(75, secondaryHsl.l))
  }
  
  const primary = hslToRgb(enhancedPrimary.h, enhancedPrimary.s, enhancedPrimary.l)
  const secondary = hslToRgb(enhancedSecondary.h, enhancedSecondary.s, enhancedSecondary.l)
  
  return {
    primary: `rgb(${primary.r}, ${primary.g}, ${primary.b})`,
    secondary: `rgb(${secondary.r}, ${secondary.g}, ${secondary.b})`,
    background: `linear-gradient(135deg, rgb(${primary.r}, ${primary.g}, ${primary.b}) 0%, rgb(${secondary.r}, ${secondary.g}, ${secondary.b}) 100%)`,
    isDark: enhancedPrimary.l < 50
  }
}

/**
 * 设置默认魔法色
 */
const setDefaultMagicColors = () => {
  magicColors.value = {
    primary: '#ec4899',
    secondary: '#f472b6',
    background: 'linear-gradient(135deg, #ec4899 0%, #f472b6 100%)',
    isDark: false
  }
  return magicColors.value
}

// 颜色转换工具函数
const rgbToHsl = (r, g, b) => {
  r /= 255; g /= 255; b /= 255
  const max = Math.max(r, g, b), min = Math.min(r, g, b)
  let h, s, l = (max + min) / 2

  if (max === min) {
    h = s = 0
  } else {
    const d = max - min
    s = l > 0.5 ? d / (2 - max - min) : d / (max + min)
    switch (max) {
      case r: h = (g - b) / d + (g < b ? 6 : 0); break
      case g: h = (b - r) / d + 2; break
      case b: h = (r - g) / d + 4; break
    }
    h /= 6
  }

  return { h: h * 360, s: s * 100, l: l * 100 }
}

const hslToRgb = (h, s, l) => {
  h /= 360; s /= 100; l /= 100
  const c = (1 - Math.abs(2 * l - 1)) * s
  const x = c * (1 - Math.abs((h * 6) % 2 - 1))
  const m = l - c / 2
  let r = 0, g = 0, b = 0

  if (0 <= h && h < 1/6) {
    r = c; g = x; b = 0
  } else if (1/6 <= h && h < 2/6) {
    r = x; g = c; b = 0
  } else if (2/6 <= h && h < 3/6) {
    r = 0; g = c; b = x
  } else if (3/6 <= h && h < 4/6) {
    r = 0; g = x; b = c
  } else if (4/6 <= h && h < 5/6) {
    r = x; g = 0; b = c
  } else if (5/6 <= h && h < 1) {
    r = c; g = 0; b = x
  }

  return {
    r: Math.round((r + m) * 255),
    g: Math.round((g + m) * 255),
    b: Math.round((b + m) * 255)
  }
}

// ==================== 初始化 ====================

/**
 * 初始化播放器
 */
export const initMusicPlayer = () => {
  if (typeof window !== 'undefined' && !audioElement.value) {
    initAudioElement()
    setDefaultMagicColors()
  }
}

// 监听播放状态变化，发送全局事件
watch([isPlaying, currentSong], () => {
  if (typeof window !== 'undefined') {
    window.dispatchEvent(new CustomEvent('musicPlayerStateChanged', {
      detail: {
        isPlaying: isPlaying.value,
        currentSong: currentSong.value,
        magicColors: magicColors.value
      }
    }))
  }
})

/**
 * 解析LRC格式歌词
 * @param {string} lrcText - LRC格式的歌词文本
 * @returns {Array} 解析后的歌词数组，每个元素包含time和text
 */
export const parseLyrics = (lrcText) => {
  if (!lrcText || typeof lrcText !== 'string') {
    return []
  }
  
  const lyrics = []
  
  try {
    const timeRegex = /\[(\d+):(\d+(?:\.\d+)?)\]([^\n\r]+)/g
    let match
    
    while ((match = timeRegex.exec(lrcText)) !== null) {
      try {
        const minutes = parseInt(match[1], 10)
        const seconds = parseFloat(match[2])
        const timeInSeconds = minutes * 60 + seconds
        const text = match[3].trim()
        
        if (text) {
          lyrics.push({
            time: timeInSeconds,
            text: text
          })
        }
      } catch (error) {
      }
    }
    
    if (lyrics.length === 0) {
      const lines = lrcText.split(/[\n\r]+/)
      
      for (const line of lines) {
        if (!line.trim()) continue

        const timeMatch = line.match(/\[(\d+):(\d+(?:\.\d+)?)\]/)
        if (timeMatch) {
          try {
            const minutes = parseInt(timeMatch[1], 10)
            const seconds = parseFloat(timeMatch[2])
            const timeInSeconds = minutes * 60 + seconds
            const text = line.replace(/\[(\d+):(\d+(?:\.\d+)?)\]/g, '').trim()
            
            if (text) {
              lyrics.push({
                time: timeInSeconds,
                text: text
              })
            }
          } catch (error) {
          }
        }
      }
    }
 
    if (lyrics.length === 0) {
    }

    if (lyrics.length > 0) {
      lyrics.sort((a, b) => a.time - b.time)
      const uniqueLyrics = []
      const seen = new Set()
      for (const lyric of lyrics) {
        const key = `${lyric.time}-${lyric.text}`
        if (!seen.has(key)) {
          seen.add(key)
          uniqueLyrics.push(lyric)
        }
      }
      
      return uniqueLyrics
    } else {
      if (lrcText.trim()) {
        return [{
          time: 0,
          text: lrcText.trim().split(/[\n\r]+/)[0] 
        }]
      }
      return []
    }
  } catch (error) {
    return []
  }
}

/**
 * 加载歌曲歌词
 * @param {string} songId - 歌曲ID
 * @returns {Promise<Array>} 解析后的歌词数组
 */
export const loadLyrics = async (songId) => {
  if (!songId) {
    currentSongLyrics.value = []
    return []
  }
  
  try {
    isLoadingLyrics.value = true
    const response = await getSongLyrics(songId)
    
    let lrcText = ''
    
    if (response && response.lyrics) {
      lrcText = response.lyrics
    } else if (response && response.data && response.data.lyrics) {
      lrcText = response.data.lyrics
    }

    const parsedLyrics = parseLyrics(lrcText)

    currentSongLyrics.value = parsedLyrics
    currentLyricIndex.value = -1 

    if (typeof window !== 'undefined') {
      window.dispatchEvent(new CustomEvent('lyrics-loaded', {
        detail: {
          songId,
          lyrics: parsedLyrics
        }
      }))
    }
    
    return parsedLyrics
  } catch (error) {
    currentSongLyrics.value = []
    currentLyricIndex.value = -1
    
    return []
  } finally {
    isLoadingLyrics.value = false
  }
}
export const getCurrentLyricIndex = (currentTimeInSeconds, lyrics) => {
  if (!lyrics || lyrics.length === 0 || 
      typeof currentTimeInSeconds !== 'number' || 
      isNaN(currentTimeInSeconds)) {
    return -1
  }
  
  let left = 0
  let right = lyrics.length - 1
  let index = -1
  
  if (lyrics.length > 50) {
    while (left <= right) {
      const mid = Math.floor((left + right) / 2)
      if (lyrics[mid] && typeof lyrics[mid].time === 'number' && 
          lyrics[mid].time <= currentTimeInSeconds) {
        index = mid
        left = mid + 1
      } else {
        right = mid - 1
      }
    }
  } else {
    for (let i = 0; i < lyrics.length; i++) {
      if (lyrics[i] && typeof lyrics[i].time === 'number' && 
          currentTimeInSeconds >= lyrics[i].time) {
        index = i
      } else {
        break
      }
    }
  }
  
  return index
}

// 检查并获取艺术家名称的函数
const checkAndFetchArtistName = (song) => {
  // 只要有artistId就尝试获取，不管当前artist是什么
  if (song.artistId) {
    fetchArtistName(song)
  }
}

// 从后端获取艺术家名称的函数
const fetchArtistName = (song) => {
  if (!song.artistId) return
  
  getArtistById(song.artistId).then(response => {
    if (response && (response.success || response.code === 200) && response.data && response.data.name) {
      const newArtistName = response.data.name
      
      // 更新当前播放歌曲（响应式更新）
      if (currentSong.value && currentSong.value.id === song.id) {
        currentSong.value = { ...currentSong.value, artist: newArtistName }
      }
      
      // 更新播放列表中的歌曲（响应式更新）
      if (playlist.value.length > 0) {
        const index = playlist.value.findIndex(item => item.id === song.id)
        if (index !== -1) {
          playlist.value[index] = { ...playlist.value[index], artist: newArtistName }
        }
      }
      
      // 同时更新传入的song对象
      song.artist = newArtistName
    }
  }).catch(() => {
    // 静默处理错误
  })
}
export { addToPlaylistFirst }