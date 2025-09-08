<template>
  <div class="detail-page">
    <div class="loading" v-if="loading">
      <i class="el-icon-loading"></i>
      <span>正在加载歌曲详情...</span>
    </div>
    
    <div v-else-if="song" class="song-detail-container">
      <!-- 左侧专辑封面 -->
      <div class="album-cover-section">
        <div class="album-cover">
          <img :src="song.cover || '/src/assets/1音乐.png'" :alt="song.name" class="cover-image" />
        </div>
      </div>
      
      <!-- 右侧歌曲信息 -->
      <div class="song-info-section">
        <div class="song-header">
          <h1 class="song-title">{{ song.name }}</h1>
          <span v-if="song.vip" class="vip-badge">VIP</span>
        </div>
        
        <div class="song-meta">
          <div class="meta-item" v-if="artist">
            <i class="artist-icon">👤</i>
            <span class="meta-label">歌手：</span>
            <span class="meta-value" @click="goToArtist(song.artistId)" style="cursor: pointer; color: var(--primary);">{{ artist.name }}</span>
          </div>
          
          <div class="meta-item" v-if="album">
            <span class="meta-label">专辑：</span>
            <span class="meta-value" @click="goToAlbum(song.albumId)" style="cursor: pointer; color: var(--primary);">{{ album.name }}</span>
          </div>
          
          <div class="meta-item" v-if="song.duration">
            <span class="meta-label">时长：</span>
            <span class="meta-value">{{ formatDuration(song.duration) }}</span>
          </div>
          
          <div class="meta-item">
            <span class="meta-label">语种：</span>
            <span class="meta-value">国语</span>
          </div>
        </div>
        
        <div class="action-buttons">
          <button class="play-btn" @click="playSong">
            <svg class="play-icon-svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
              <path d="M955.733333 512L68.266667 1024V0z" fill="currentColor"></path>
            </svg>
            播放
          </button>
          <button class="action-btn" @click="toggleFavorite" :class="{ 'is-favorited': isFavorited }">
            <img v-if="isFavorited" class="favorite-icon-img" src="/src/assets/已收藏.png" alt="已收藏" />
            <i v-else class="heart-icon">♡</i>
            {{ isFavorited ? '已收藏' : '收藏' }}
          </button>
        </div>
      </div>
    </div>
    
    <div v-else class="error-message">
      <i class="el-icon-warning"></i>
      <span>歌曲不存在或已被删除</span>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted, computed, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSongById } from '@/api/song.js'
import { getArtistById } from '@/api/artist.js'
import { getAlbumById } from '@/api/album.js'
import { ElMessage } from 'element-plus'
import { addToPlaylist } from '@/utils/musicPlayer.js'
import { isSongLiked, toggleSongLike, checkSongLikeStatus } from '@/utils/favoriteManager.js'
import { isSongFavorited } from '@/api/song.js'

const route = useRoute()
const router = useRouter()
const song = ref(null)
const artist = ref(null)
const album = ref(null)
const loading = ref(true)

// 收藏状态计算属性
const isFavorited = computed(() => {
  return song.value ? isSongLiked(song.value.id) : false
})

// 监听收藏状态变化
let songLikeListener = null

// 获取歌曲详情
const fetchSongDetail = async () => {
  try {
    loading.value = true
    const songId = route.params.id
    
    // 获取歌曲信息
    const songResponse = await getSongById(songId)
    if (songResponse && songResponse.code === 200) {
      song.value = songResponse.data
      
      // 获取歌手信息
      if (song.value.artistId) {
        try {
          const artistResponse = await getArtistById(song.value.artistId)
          if (artistResponse && artistResponse.code === 200) {
            artist.value = artistResponse.data
          }
        } catch (error) {
          // 无法获取歌手信息
        }
      }
      
      // 获取专辑信息
      if (song.value.albumId) {
        try {
          const albumResponse = await getAlbumById(song.value.albumId)
          if (albumResponse && albumResponse.code === 200) {
            album.value = albumResponse.data
          }
        } catch (error) {
          // 无法获取专辑信息
        }
      }
      
      console.log('🎵 歌曲详情加载完成:')
      console.log('歌曲信息:', song.value)
      console.log('歌手信息:', artist.value)
      console.log('专辑信息:', album.value)
      console.log('🎵 音频字段检查:', {
        audioUrl: song.value.audioUrl,
        filePath: song.value.filePath,
        file_path: song.value.file_path
      })
    } else {
      ElMessage.error('歌曲不存在')
    }
  } catch (error) {
    ElMessage.error('获取歌曲详情失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 格式化时长
const formatDuration = (seconds) => {
  if (!seconds) return ''
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
}

// 播放歌曲
const playSong = () => {
  if (!song.value) {
    ElMessage.warning('歌曲信息不存在')
    return
  }

  // 检查音频链接 - 优先使用后端返回的字段
  const audioUrl = song.value.filePath || song.value.audioUrl || song.value.file_path || song.value.url
  
  console.log('🎵 歌曲音频信息检查:', {
    filePath: song.value.filePath,
    audioUrl: song.value.audioUrl,
    file_path: song.value.file_path,
    url: song.value.url,
    finalAudioUrl: audioUrl
  })
  
  if (!audioUrl) {
    ElMessage.error('歌曲音频文件不存在，无法播放')
    console.error('❌ 歌曲数据缺少音频链接:', song.value)
    return
  }

  // 构建播放数据
  const playData = {
    id: song.value.id,
    name: song.value.name,
    artist: song.value.artistName || (artist.value?.name) || '未知歌手',
    album: song.value.albumName || (album.value?.name) || '未知专辑',
    duration: song.value.duration || 0,
    cover: song.value.cover || song.value.coverUrl || '/src/assets/1音乐.png',
    audioUrl: audioUrl,
    artistId: song.value.artistId,
    albumId: song.value.albumId
  }
  
  console.log('尝试播放歌曲:', playData)
  
  // 添加到播放列表并立即播放
  const success = addToPlaylist(playData, true)
  
  if (success) {
    ElMessage.success('开始播放: ' + song.value.name)
  } else {
    ElMessage.error('播放失败，请稍后重试')
  }
}

// 切换收藏状态
const toggleFavorite = async () => {
  if (!song.value) {
    ElMessage.warning('歌曲信息不存在')
    return
  }

  try {
    // 构建歌曲数据用于收藏
    const songData = {
      id: song.value.id,
      name: song.value.name,
      artist: song.value.artistName || (artist.value?.name) || '未知歌手',
      album: song.value.albumName || (album.value?.name) || '未知专辑',
      duration: song.value.duration || 0,
      cover: song.value.cover || song.value.coverUrl || '/src/assets/1音乐.png',
      audioUrl: song.value.filePath || song.value.audioUrl || song.value.file_path || '',
      artistId: song.value.artistId,
      albumId: song.value.albumId
    }

    const newLikeStatus = await toggleSongLike(songData)
    
    if (newLikeStatus) {
      ElMessage.success('已添加到我喜欢')
    } else {
      ElMessage.success('已从我喜欢中移除')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error('收藏操作失败，请稍后重试')
  }
}

// 跳转到歌手详情
const goToArtist = (artistId) => {
  if (artistId) {
    const targetPath = `/artist/${artistId}`
    if (route.path !== targetPath) {
      router.push(targetPath)
    }
  }
}

// 跳转到专辑详情
const goToAlbum = (albumId) => {
  if (albumId) {
    const targetPath = `/album/${albumId}`
    if (route.path !== targetPath) {
      router.push(targetPath)
    }
  }
}

onMounted(async () => {
  await fetchSongDetail()
  
  // 歌曲详情加载完成后，检查收藏状态
  if (song.value) {
    await checkSongLikeStatus(song.value.id)
  }
  
  // 监听收藏状态变化
  songLikeListener = (event) => {
    // 如果是当前歌曲，强制更新组件
    if (song.value && event.detail.songId === song.value.id) {
      // 触发响应式更新
      nextTick(() => {
        // 计算属性会自动重新计算
      })
    }
  }
  window.addEventListener('songLikeChanged', songLikeListener)
})

onUnmounted(() => {
  if (songLikeListener) {
    window.removeEventListener('songLikeChanged', songLikeListener)
  }
})
</script>
<style scoped>
.detail-page { 
  width: 1280px; 
  margin: 0 auto; 
  background: var(--background); 
  min-height: 400px; 
  padding: 32px; 
  animation: fadein 0.5s; 
  color: var(--text-primary);
}

/* 黑色主题下的SongDetail页面样式 */
[data-theme="black"] .detail-page {
  background: var(--background) !important;
}

@keyframes fadein { 
  from { opacity: 0; } 
  to { opacity: 1; } 
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: var(--text-secondary);
  font-size: 16px;
  gap: 16px;
}

.loading i {
  font-size: 24px;
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.error-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: var(--text-secondary);
  font-size: 16px;
  gap: 16px;
}

.error-message i {
  font-size: 48px;
  color: var(--warning);
}

.song-detail-container {
  display: flex;
  gap: 40px;
  margin-bottom: 40px;
  background: var(--background-card);
  padding: 24px;
  border-radius: var(--border-radius-lg);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.album-cover-section {
  flex: 0 0 300px;
}

.album-cover {
  position: relative;
  width: 300px;
  height: 300px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.song-info-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.song-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
}

.song-title {
  font-size: 36px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0;
}

.vip-badge {
  background: #22c55e;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.song-meta {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  line-height: 1.5;
}

.artist-icon {
  font-size: 16px;
  margin-right: 4px;
}

.meta-label {
  color: var(--text-secondary);
  font-weight: 500;
  min-width: 80px;
}

.meta-value {
  color: var(--text-primary);
  font-weight: 600;
  transition: color var(--transition-normal);
}

.meta-value:hover {
  color: var(--primary);
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.play-btn {
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 12px 20px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.play-btn:hover {
  background: var(--primary-dark);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.3);
}

.play-icon-svg {
  width: 14px;
  height: 14px;
  margin-right: 6px;
  transition: all 0.3s ease;
}

.play-btn:hover .play-icon-svg {
  transform: scale(1.1);
}

.action-btn {
  background: var(--background);
  color: var(--text-primary);
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 12px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.action-btn:hover {
  background: var(--background-hover);
  border-color: var(--primary);
  color: var(--primary);
}

/* 收藏按钮的PNG图片样式 */
.favorite-icon-img {
  width: 16px;
  height: 16px;
  object-fit: contain;
  flex-shrink: 0;
}

/* 收藏按钮的红色爱心状态 */
.action-btn .heart-icon {
  transition: color 0.2s ease;
}

.action-btn.is-favorited .heart-icon {
  color: #d33a31 !important; /* 收藏成功的红色 */
}

/* 响应式设计 */
@media (max-width: 768px) {
  .detail-page {
    width: 100%;
    padding: 16px;
  }
  
  .song-detail-container {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }
  
  .album-cover-section {
    flex: none;
    align-self: center;
  }
  
  .album-cover {
    width: 200px;
    height: 200px;
  }
  
  .song-title {
    font-size: 28px;
  }
  
  .action-buttons {
    justify-content: center;
  }
}
</style>