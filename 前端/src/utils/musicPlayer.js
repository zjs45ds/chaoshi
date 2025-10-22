/**
 * 音乐风格播放器 - 核心状态管理
 */

import { ref, computed, reactive, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getSongStreamUrl, getSongLyrics } from '@/api/song.js'

// ==================== 核心状态 ====================

// 播放器状态
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
export const currentLyricIndex = ref(-1) // 当前显示的歌词索引

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

// 魔法色彩系统 - QQ音乐特色
export const magicColors = ref({
  primary: '#ec4899',
  secondary: '#f472b6', 
  background: 'linear-gradient(135deg, #ec4899 0%, #f472b6 100%)',
  isDark: false
})

// 播放器主题模式
export const playerTheme = ref('magic') // magic, simple, dynamic

// ==================== 计算属性 ====================

// 播放进度百分比
export const progress = computed(() => {
  if (!duration.value) return 0
  return (currentTime.value / duration.value) * 100
})

// 格式化时间
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
    return currentSongLyrics.value[currentLyricIndex.value].text
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
  
  // 音频事件监听
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
        
        // 触发歌词更新事件，可用于UI更新
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
    console.error('🚨 音频播放错误:', {
      error: e,
      currentSrc: audio.src,
      song: currentSong.value?.name,
      errorCode: audio.error?.code,
      errorMessage: audio.error?.message
    })
    
    isLoading.value = false
    isPlaying.value = false
    
    // 显示用户友好的错误信息
    if (audio.error?.code === 4) {
      console.error('音频文件格式不支持')
    } else if (audio.error?.code === 3) {
      console.error('音频文件解码失败')
    } else if (audio.error?.code === 2) {
      console.error('网络错误，无法加载音频')
    } else {
      console.error('音频文件不存在或无法播放')
    }
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

/**
 * 加载歌曲 - 增强MinIO支持
 */
export const loadSong = async (song) => {
  if (!song) {
    console.log('🔇 loadSong: 歌曲对象为空')
    return false
  }
  
  // 确保音频元素已初始化
  if (!audioElement.value) {
    console.log('🔧 loadSong: 初始化音频元素')
    initAudioElement()
    
    // 如果仍然没有音频元素，无法播放
    if (!audioElement.value) {
      console.error('❌ loadSong: 无法初始化音频元素')
      return false
    }
  }
  
  try {
    // 完全重置播放状态
    console.log('🔄 loadSong: 重置播放状态')
    
    // 先暂停当前播放（如果有的话）
    if (isPlaying.value) {
      try {
        await audioElement.value.pause()
      } catch (pauseError) {
        console.warn('⚠️ loadSong: 暂停当前播放失败:', pauseError)
      }
    }
    
    // 重置播放状态变量
    isPlaying.value = false
    isPaused.value = true
    isLoading.value = true
    currentTime.value = 0
    
    // 设置当前歌曲信息
    currentSong.value = song
    currentLyricIndex.value = -1 // 重置歌词索引
    
    console.log('🎵 开始加载歌曲:', song.name, 'ID:', song.id)
    
    let audioUrl = null
    let metadata = {}
    
    // 首先尝试从API获取流媒体URL
    if (song.id) {
      try {
        console.log('🔍 从API获取音频流URL...')
        const streamResponse = await getSongStreamUrl(song.id)
        
        if (streamResponse && (streamResponse.success || streamResponse.code === 200)) {
          const responseData = streamResponse.data || streamResponse
          audioUrl = responseData.audioUrl || responseData.streamUrl || responseData.filePath
          
          // 更新歌曲元数据
          if (responseData.artist) {
            metadata.artist = responseData.artist
            currentSong.value.artist = responseData.artist
          }
          if (responseData.cover) {
            metadata.cover = responseData.cover
            currentSong.value.cover = responseData.cover
            currentSong.value.albumCover = responseData.cover
          }
          if (responseData.duration) {
            metadata.duration = responseData.duration
            currentSong.value.duration = responseData.duration
          }
          if (responseData.title || responseData.name) {
            metadata.title = responseData.title || responseData.name
            currentSong.value.name = responseData.title || responseData.name
          }
          
          console.log('✅ 从API获取音频URL成功:', audioUrl)
          console.log('📝 获取到的元数据:', metadata)
        }
      } catch (apiError) {
        console.warn('⚠️ 从API获取音频URL失败:', apiError)
        // 继续尝试其他方式
      }
    }
    
    // 尝试使用歌曲对象中的各种可能的音频URL字段，支持更多后端可能返回的字段名
    if (!audioUrl) {
      // 检查多种可能的音频URL字段名，确保能够找到后端返回的真实音频链接
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
      console.log('🔄 使用歌曲对象中的音频URL:', audioUrl)
    }
    
    // 验证音频URL的有效性
    if (audioUrl) {
      if (!isValidAudioUrl(audioUrl)) {
        console.warn('⚠️ 音频URL格式可能无效:', audioUrl)
      } else {
        console.log('✅ 音频URL格式有效')
      }
    } else {
      console.warn('⚠️ 未找到有效的音频URL，无法播放歌曲')
    }
    
    console.log('🎵 最终音频URL:', audioUrl)
    
    if (audioUrl) {
      // 设置CORS和缓存策略
      audioElement.value.crossOrigin = 'anonymous'
      audioElement.value.preload = 'metadata'
      
      // 处理MinIO URL的特殊情况
      if (audioUrl.includes('minio') || audioUrl.includes('9000')) {
        console.log('🗄️ 检测到MinIO音频源，优化加载策略')
        audioElement.value.preload = 'none' // MinIO可能需要特殊处理
      }
      
      audioElement.value.src = audioUrl
      
      // 监听加载事件
      const loadPromise = new Promise((resolve, reject) => {
        const handleLoadedData = () => {
          console.log('✅ 音频数据加载完成')
          cleanup()
          resolve()
        }
        
        const handleError = (error) => {
          console.error('❌ 音频加载错误:', error)
          cleanup()
          reject(error)
        }
        
        const handleTimeout = () => {
          console.error('⏰ 音频加载超时')
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
        
        // 设置加载超时
        const timeoutId = setTimeout(handleTimeout, 10000) // 10秒超时
      })
      
      audioElement.value.load()
      await loadPromise
      
      // 提取专辑封面色彩生成魔法色
      await extractMagicColors(currentSong.value.cover || currentSong.value.albumCover)
      
      // 触发歌曲加载完成事件
      window.dispatchEvent(new CustomEvent('song-loaded', {
        detail: { 
          song: currentSong.value, 
          audioUrl,
          metadata 
        }
      }))
      
      console.log('✅ 歌曲加载成功:', song.name)
      isLoading.value = false
      return true
    } else {
      console.error('❌ 无法获取歌曲音频URL:', song)
      ElMessage.error('歌曲音频文件不存在或无法获取')
      return false
    }
  } catch (error) {
    console.error('💥 加载歌曲失败:', error)
    
    // 根据错误类型提供更具体的错误信息
    let errorMessage = '歌曲加载失败'
    if (error.message.includes('超时')) {
      errorMessage = '音频加载超时，请检查网络连接'
    } else if (error.message.includes('CORS')) {
      errorMessage = '音频资源跨域访问受限'
    } else if (error.message.includes('网络')) {
      errorMessage = '网络错误，无法加载音频'
    }
    
    ElMessage.error(errorMessage)
    isLoading.value = false
    return false
  }
}

/**
 * 验证音频URL的有效性
 */
const isValidAudioUrl = (url) => {
  if (!url || typeof url !== 'string') return false
  
  // 检查是否是有效的URL
  try {
    new URL(url)
  } catch {
    return false
  }
  
  // 检查是否包含音频文件扩展名或是MinIO路径
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
    console.log('🔇 togglePlay: 音频元素不存在')
    return
  }
  
  console.log(`🔄 togglePlay: 当前状态: isPlaying=${isPlaying.value}, isPaused=${isPaused.value}, isLoading=${isLoading.value}`)
  
  try {
    if (isPlaying.value) {
      console.log('⏸️ 暂停播放')
      await audioElement.value.pause()
    } else {
      console.log('▶️ 开始播放')
      await audioElement.value.play()
    }
  } catch (error) {
    console.error('播放控制失败:', error)
    console.error('🚨 错误详情:', error.message)
    console.error('🚨 错误堆栈:', error.stack)
    
    // 处理浏览器自动播放策略限制
    if (error.name === 'NotAllowedError' || error.message.includes('autoplay')) {
      console.warn('⚠️ 浏览器自动播放限制，需要用户交互才能播放音频')
      
      // 手动更新播放状态
      isPlaying.value = false
      isPaused.value = true
      
      // 提示用户需要点击播放按钮
      ElMessage.warning('请点击播放按钮开始播放音乐')
    }
  }
}

/**
 * 播放歌曲
 */
export const playSong = async (song) => {
  if (!song) {
    console.log('🔇 playSong: 歌曲对象为空')
    return false
  }
  
  // 确保音频元素已初始化
  if (!audioElement.value) {
    console.log('🔧 playSong: 初始化音频元素')
    initAudioElement()
    
    // 如果仍然没有音频元素，无法播放
    if (!audioElement.value) {
      console.error('❌ playSong: 无法初始化音频元素')
      return false
    }
  }
  
  console.log(`🎯 playSong: 尝试播放歌曲 "${song.name}", 当前播放列表长度: ${playlist.value.length}`)
  console.log(`🎯 playSong: 当前状态: isPlaying=${isPlaying.value}, isPaused=${isPaused.value}, isLoading=${isLoading.value}`)
  
  const loaded = await loadSong(song)
  if (loaded) {
    console.log(`✅ playSong: 歌曲 "${song.name}" 加载成功，准备播放`)
    
    try {
      // 首先确保audio元素处于可播放状态
      if (audioElement.value.readyState >= 2) { // HAVE_CURRENT_DATA
        console.log('▶️ playSong: 音频已准备就绪，尝试播放')
        
        // 直接调用audio元素的play方法，而不是通过togglePlay
        const playPromise = audioElement.value.play()
        
        if (playPromise !== undefined) {
          playPromise.then(() => {
            console.log(`✅ playSong: 歌曲 "${song.name}" 播放成功`)
            isPlaying.value = true
            isPaused.value = false
          }).catch(playError => {
            console.error('❌ playSong: 播放失败，可能受限于浏览器自动播放策略:', playError)
            console.error('🚨 错误详情:', playError.message)
            
            // 即使播放失败，也确保状态正确更新
            isPlaying.value = false
            isPaused.value = true
          })
        }
        
        return true
      } else {
        console.warn('⚠️ playSong: 音频尚未准备就绪，等待数据加载...')
        
        // 等待音频数据加载
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
          
          // 超时处理
          setTimeout(() => {
            audioElement.value.removeEventListener('canplay', handleCanPlay)
            audioElement.value.removeEventListener('error', handleError)
            console.warn('⚠️ playSong: 音频加载超时')
            resolve(false)
          }, 5000)
        })
        
        const readyToPlay = await playWhenReady
        
        if (readyToPlay) {
          try {
            await audioElement.value.play()
            console.log(`✅ playSong: 歌曲 "${song.name}" 播放成功`)
            isPlaying.value = true
            isPaused.value = false
            return true
          } catch (playError) {
            console.error('❌ playSong: 播放失败:', playError)
            isPlaying.value = false
            isPaused.value = true
            return false
          }
        } else {
          console.error('❌ playSong: 音频无法准备就绪')
          return false
        }
      }
    } catch (playError) {
      console.error('❌ playSong: 播放过程中发生错误:', playError)
      isPlaying.value = false
      isPaused.value = true
      return false
    }
  }
  console.log(`❌ playSong: 歌曲 "${song.name}" 加载失败`)
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
  
  // 检查是否已存在
  const existingIndex = playlist.value.findIndex(item => item.id === song.id)
  if (existingIndex !== -1) {
    // 如果已存在，先移除
    playlist.value.splice(existingIndex, 1)
    // 如果移除的歌曲在当前播放位置之前，需要调整当前索引
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
  
  // 检查是否已存在
  const existingIndex = playlist.value.findIndex(item => item.id === song.id)
  
  if (existingIndex !== -1) {
    // 如果歌曲已存在
    console.log('🔄 歌曲已在播放列表中，移动到第一位:', song.name)
    
    if (existingIndex === 0) {
      // 如果已经在第一位，直接播放
      console.log('✅ 歌曲已在第一位，直接播放')
      return playByIndex(0)
    }
    
    // 移除原位置的歌曲
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
    
    console.log('✅ 歌曲已移动到第一位，其他歌曲顺序保持不变')
  } else {
    // 如果歌曲不存在，添加到第一位
    console.log('➕ 歌曲不在播放列表中，添加到第一位:', song.name)
    
    // 插入到播放列表第一位
    playlist.value.unshift(song)
    
    // 调整当前播放索引（因为插入了新歌曲）
    if (currentIndex.value >= 0) {
      currentIndex.value++
    }
    
    console.log('✅ 新歌曲已添加到第一位')
  }
  
  // 立即播放第一位的歌曲（索引为0）
  return playByIndex(0)
}

/**
 * 批量添加歌曲
 */
export const addMultipleToPlaylist = (songs, playFirst = false) => {
  if (!Array.isArray(songs) || songs.length === 0) return false
  
  songs.forEach(song => {
    if (song && song.id) {
      const existingIndex = playlist.value.findIndex(item => item.id === song.id)
      if (existingIndex === -1) {
        playlist.value.push(song)
      }
    }
  })
  
  if (playFirst && playlist.value.length > 0) {
    playByIndex(0)
  }
  
  return true
}

/**
 * 根据索引播放歌曲
 */
export const playByIndex = async (index) => {
  if (index < 0 || index >= playlist.value.length) {
    console.error(`❌ playByIndex: 索引无效 [${index}]，播放列表长度: ${playlist.value.length}`)
    return false
  }
  
  console.log(`🎯 playByIndex: 播放索引 ${index} 的歌曲`)
  
  currentIndex.value = index
  const song = playlist.value[index]
  
  try {
    const result = await playSong(song)
    
    if (result) {
      console.log(`✅ playByIndex: 成功播放歌曲 "${song.name}"`)
      
      // 检查播放状态，如果没有真正开始播放，提供一个用户交互的备用方案
      setTimeout(() => {
        if (!isPlaying.value && audioElement.value) {
          console.log('⚠️ playByIndex: 歌曲已加载但未自动播放，提示用户手动播放')
          
          // 如果有需要，可以在这里触发一个UI更新，提示用户点击播放按钮
        }
      }, 1000)
      
      return true
    } else {
      console.error(`❌ playByIndex: 播放歌曲 "${song.name}" 失败`)
      return false
    }
  } catch (error) {
    console.error('❌ playByIndex: 播放过程中发生错误:', error)
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

/**
 * 歌曲结束处理 - 增强版，确保连续播放稳定性
 */
const onSongEnded = async () => {
  console.log('🎵 歌曲播放结束，处理下一首...')
  console.log(`🎯 当前播放模式: ${playMode.value}, 当前歌曲索引: ${currentIndex.value}`)
  
  try {
    if (playMode.value === 'loop') {
      // 单曲循环
      if (audioElement.value) {
        console.log('🔄 单曲循环模式，重新开始播放当前歌曲')
        
        // 重置播放状态
        isPlaying.value = false
        isPaused.value = true
        
        // 重置播放时间
        audioElement.value.currentTime = 0
        
        try {
          // 尝试直接播放
          const playPromise = audioElement.value.play()
          
          if (playPromise !== undefined) {
            await playPromise
            console.log('✅ 单曲循环播放成功')
            isPlaying.value = true
            isPaused.value = false
          }
        } catch (loopError) {
          console.error('⚠️ 单曲循环播放失败:', loopError)
          
          // 如果单曲循环失败，尝试播放下一首
          console.log('🔄 单曲循环失败，尝试播放下一首')
          await playNextWithRetry()
        }
      }
    } else {
      // 播放下一首
      console.log('⏭️ 顺序/随机模式，播放下一首')
      await playNextWithRetry()
    }
  } catch (error) {
    console.error('❌ 歌曲结束处理失败:', error)
    
    // 确保状态正确
    isPlaying.value = false
    isPaused.value = true
  }
}

/**
 * 带重试机制的下一首播放函数
 */
const playNextWithRetry = async (retryCount = 0, maxRetries = 2) => {
  try {
    // 防止立即连续调用导致的问题
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const result = await playNext()
    
    if (result) {
      console.log('✅ 播放下一首成功')
      return true
    } else {
      console.warn('⚠️ 播放下一首失败')
      
      // 重试逻辑
      if (retryCount < maxRetries) {
        console.log(`🔄 尝试重试播放下一首 (${retryCount + 1}/${maxRetries})`)
        
        // 等待一小段时间后重试
        await new Promise(resolve => setTimeout(resolve, 300))
        return playNextWithRetry(retryCount + 1, maxRetries)
      } else {
        console.error('❌ 多次尝试播放下一首失败')
        
        // 重置播放状态
        isPlaying.value = false
        isPaused.value = true
        
        // 可以在这里添加用户提示
        ElMessage.warning('播放下一首失败，请尝试手动播放')
        return false
      }
    }
  } catch (error) {
    console.error('❌ 播放下一首时发生错误:', error)
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
          // 创建canvas提取色彩
          const canvas = document.createElement('canvas')
          const ctx = canvas.getContext('2d')
          
          canvas.width = img.width
          canvas.height = img.height
          ctx.drawImage(img, 0, 0)
          
          // 提取主色调
          const colors = extractDominantColors(ctx, canvas.width, canvas.height)
          
          // 生成魔法色
          const magic = generateMagicColors(colors)
          magicColors.value = magic
          
          resolve(magic)
        } catch (error) {
          console.error('提取色彩失败:', error)
          // 使用默认魔法色
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
    console.error('加载封面失败:', error)
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
  
  // 采样像素（每4个像素采样一次以提高性能）
  for (let i = 0; i < data.length; i += 16) {
    const r = data[i]
    const g = data[i + 1]
    const b = data[i + 2]
    const alpha = data[i + 3]
    
    if (alpha > 125) { // 忽略透明像素
      const color = `${r},${g},${b}`
      colorMap[color] = (colorMap[color] || 0) + 1
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
  
  // 转换为HSL进行色彩调整
  const primaryHsl = rgbToHsl(primaryColor.r, primaryColor.g, primaryColor.b)
  const secondaryHsl = rgbToHsl(secondaryColor.r, secondaryColor.g, secondaryColor.b)
  
  // 增强饱和度和亮度，创造QQ音乐风格的鲜艳效果
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
  // 匹配[mm:ss.xx]格式的时间标签
  const timeRegex = /\[(\d+):(\d+\.\d+)\](.+)/g
  let match
  
  while ((match = timeRegex.exec(lrcText)) !== null) {
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
  }
  
  // 按时间排序
  lyrics.sort((a, b) => a.time - b.time)
  
  return lyrics
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
    
    if (response && (response.success || response.code === 200)) {
      // 现在我们获取的是完整的歌曲对象，所以直接从response.data中获取lyrics
      const song = response.data || response
      const lrcText = song.lyrics || ''
      const parsedLyrics = parseLyrics(lrcText)
      currentSongLyrics.value = parsedLyrics
      
      // 触发歌词加载完成事件
      if (typeof window !== 'undefined') {
        window.dispatchEvent(new CustomEvent('lyrics-loaded', {
          detail: {
            songId,
            lyrics: parsedLyrics
          }
        }))
      }
      
      return parsedLyrics
    } else {
      console.warn('获取歌词失败，返回空歌词')
      currentSongLyrics.value = []
      return []
    }
  } catch (error) {
    console.error('加载歌词时发生错误:', error)
    currentSongLyrics.value = []
    return []
  } finally {
    isLoadingLyrics.value = false
  }
}

/**
 * 根据当前播放时间获取当前歌词索引
 * @param {number} currentTimeInSeconds - 当前播放时间（秒）
 * @param {Array} lyrics - 歌词数组
 * @returns {number} 当前歌词的索引
 */
export const getCurrentLyricIndex = (currentTimeInSeconds, lyrics) => {
  if (!lyrics || lyrics.length === 0) {
    return -1
  }
  
  let index = -1
  for (let i = 0; i < lyrics.length; i++) {
    if (currentTimeInSeconds >= lyrics[i].time) {
      index = i
    } else {
      break
    }
  }
  
  return index
}

// 导出函数
export { addToPlaylistFirst }