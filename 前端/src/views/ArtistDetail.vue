<template>
  <div class="detail-page">
    <div class="loading" v-if="loading">
      <i class="el-icon-loading"></i>
      <span>正在加载歌手详情...</span>
    </div>
    
    <div v-else-if="artist" class="artist-detail">
      <div class="artist-header">
        <img :src="artist.avatar || '/default-avatar.png'" :alt="artist.name" class="artist-avatar" />
        <div class="artist-info">
          <div class="artist-name">{{ artist.name }}</div>
          <div class="artist-desc">{{ artist.description || '暂无介绍' }}</div>
        </div>
      </div>
      
      <div class="artist-albums" v-if="albums && albums.length > 0">
        <div class="section-title">专辑</div>
        <ul>
          <li v-for="album in albums" :key="album.id" @click="goToAlbum(album.id)" class="album-item">
            <img :src="album.cover || '/src/assets/1音乐.png'" :alt="album.name" class="album-cover-mini" />
            <span class="album-name">{{ album.name }}</span>
            <span class="album-date" v-if="album.releaseDate">({{ album.releaseDate }})</span>
          </li>
        </ul>
      </div>
      
      <div class="artist-songs" v-if="songs && songs.length > 0">
        <div class="section-header">
          <div class="section-title">热门歌曲</div>
          <button class="play-all-btn" @click="playAllSongs" :title="`播放全部 ${songs.length} 首歌曲`">
            <svg class="play-icon-svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
              <path d="M955.733333 512L68.266667 1024V0z" fill="currentColor"></path>
            </svg>
            播放全部
          </button>
        </div>
        <ul>
          <li v-for="song in songs" :key="song.id" class="song-item">
            <div class="song-content" @click="goToSong(song.id)">
              <span class="song-name">{{ song.name }}</span>
              <span class="song-album" v-if="song.albumName">· {{ song.albumName }}</span>
            </div>
            <div class="song-actions">
              <button class="play-next-btn" @click.stop="addSongToPlayNext(song)" :title="`下一首播放 ${song.name}`">
                <svg class="play-next-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M19 13H13V19H11V13H5V11H11V5H13V11H19V13Z" fill="currentColor"/>
                  <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="1.5"/>
                </svg>
              </button>
              <button class="play-btn" @click.stop="playCurrentSong(song)" :title="`播放 ${song.name}`">
                <img src="/src/assets/开始.svg" alt="播放" class="play-icon-img" />
              </button>
            </div>
          </li>
        </ul>
      </div>
      
      <div v-if="(!albums || albums.length === 0) && (!songs || songs.length === 0)" class="no-content">
        <i class="el-icon-info"></i>
        <span>暂无相关内容</span>
      </div>
    </div>
    
    <div v-else class="error-message">
      <i class="el-icon-warning"></i>
      <span>歌手不存在或已被删除</span>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArtistById, getAlbumsByArtist, getSongsByArtist } from '@/api/artist.js'
import { ElMessage } from 'element-plus'
import { playSong, addToPlaylist, addMultipleToPlaylist, addToPlayNext, playlist } from '@/utils/musicPlayer.js'

const route = useRoute()
const router = useRouter()
const artist = ref(null)
const albums = ref([])
const songs = ref([])
const loading = ref(true)

// 获取歌手详情
const fetchArtistDetail = async () => {
  try {
    loading.value = true
    const artistId = route.params.id
    
    // 并行获取歌手信息、专辑和歌曲
    const [artistResponse, albumsResponse, songsResponse] = await Promise.all([
      getArtistById(artistId).catch(() => null),
      getAlbumsByArtist(artistId).catch(() => null),
      getSongsByArtist(artistId).catch(() => null)
    ])
    
    // 按照前端数据处理规范，正确处理API响应
    if (artistResponse && artistResponse.code === 200) {
      artist.value = artistResponse.data
    }
    
    if (albumsResponse && albumsResponse.code === 200) {
      if (albumsResponse.data && albumsResponse.data.content) {
        albums.value = albumsResponse.data.content
      } else if (albumsResponse.data && Array.isArray(albumsResponse.data)) {
        albums.value = albumsResponse.data
      }
    }
    
    if (songsResponse && songsResponse.code === 200) {
      if (songsResponse.data && songsResponse.data.content) {
        songs.value = songsResponse.data.content
      } else if (songsResponse.data && Array.isArray(songsResponse.data)) {
        songs.value = songsResponse.data
      }
    }
    
    console.log('🎤 歌手详情加载完成:')
    console.log('歌手信息:', artist.value)
    console.log('专辑数量:', albums.value?.length || 0)
    console.log('歌曲数量:', songs.value?.length || 0)
    
    if (!artist.value) {
      ElMessage.error('歌手不存在')
    }
  } catch (error) {
    console.error('获取歌手详情失败:', error)
    ElMessage.error('获取歌手详情失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 跳转到专辑详情
const goToAlbum = (albumId) => {
  const targetPath = `/album/${albumId}`
  if (route.path !== targetPath) {
    router.push(targetPath)
  }
}

// 跳转到歌曲详情
const goToSong = (songId) => {
  const targetPath = `/song/${songId}`
  if (route.path !== targetPath) {
    router.push(targetPath)
  }
}

// 播放歌曲
const playCurrentSong = async (song) => {
  try {
    console.log('🎵 准备播放歌曲:', song.name)
    
    // 先添加到播放列表
    const added = addToPlaylist(song, true) // playNow = true 表示立即播放
    
    if (added) {
      console.log('✅ 歌曲已添加到播放列表:', song.name)
      console.log('📋 当前播放列表长度:', playlist.value.length)
      ElMessage.success(`开始播放: ${song.name}`)
    } else {
      console.warn('⚠️ 歌曲添加到播放列表失败')
      ElMessage.error('添加到播放列表失败')
    }
  } catch (error) {
    console.error('播放歌曲失败:', error)
    ElMessage.error('播放失败，请稍后重试')
  }
}

// 播放全部歌曲
const playAllSongs = async () => {
  try {
    if (!songs.value || songs.value.length === 0) {
      ElMessage.warning('没有可播放的歌曲')
      return
    }
    
    console.log('🎵 准备播放全部歌曲:', songs.value.length, '首')
    
    // 将所有歌曲添加到播放列表并播放第一首
    const added = addMultipleToPlaylist(songs.value, true) // playFirst = true
    
    if (added) {
      console.log('✅ 所有歌曲已添加到播放列表')
      console.log('📋 当前播放列表长度:', playlist.value.length)
      ElMessage.success(`开始播放全部 ${songs.value.length} 首歌曲`)
    } else {
      console.warn('⚠️ 歌曲添加到播放列表失败')
      ElMessage.error('添加到播放列表失败')
    }
  } catch (error) {
    console.error('播放全部歌曲失败:', error)
    ElMessage.error('播放失败，请稍后重试')
  }
}

// 添加歌曲到下一首播放
const addSongToPlayNext = async (song) => {
  try {
    console.log('⏭️ 准备将歌曲添加到下一首播放:', song.name)
    
    const added = addToPlayNext(song)
    
    if (added) {
      console.log('✅ 歌曲已添加到下一首播放:', song.name)
      console.log('📋 当前播放列表长度:', playlist.value.length)
      ElMessage.success(`${song.name} 已添加到下一首播放`)
    } else {
      console.warn('⚠️ 歌曲添加到下一首播放失败')
      ElMessage.error('添加失败')
    }
  } catch (error) {
    console.error('添加歌曲到下一首播放失败:', error)
    ElMessage.error('添加失败，请稍后重试')
  }
}

onMounted(() => {
  fetchArtistDetail()
})
</script>
<style scoped>
.detail-page {
  width: 1280px;
  margin: 0 auto;
  background: var(--background);
  min-height: 400px;
  padding: 32px;
  color: var(--text-primary);
}

/* 黑色主题背景样式增强 */
[data-theme="black"] .detail-page {
  background: var(--background) !important;
  transition: background 0.5s ease-in-out !important;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #000000;
  font-size: 20px;
  font-weight: 700;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
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

.artist-detail {
  max-width: 900px;
  margin: 0 auto;
}

.artist-header {
  display: flex;
  align-items: center;
  gap: 32px;
  margin-bottom: 32px;
  background: var(--background-card);
  padding: 24px;
  border-radius: var(--border-radius-lg);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.artist-avatar {
  width: 160px;
  height: 160px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--primary-light);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.artist-info {
  flex: 1;
}

.artist-name {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 12px;
  color: var(--text-primary);
}

.artist-desc {
  color: var(--text-secondary);
  font-size: 16px;
  line-height: 1.6;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 24px 0 16px 0;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-primary);
  border-left: 4px solid var(--primary);
  padding-left: 12px;
  margin: 0;
}

.play-all-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  border-radius: 20px;
  background-color: var(--primary);
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.play-all-btn:hover {
  background-color: var(--primary-dark);
  transform: translateY(-1px);
}

.play-all-btn .play-icon-svg {
  width: 12px;
  height: 12px;
}

ul {
  padding-left: 0;
  list-style: none;
  background: var(--background-card);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.album-item,
.song-item {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border);
  font-size: 16px;
  color: var(--text-primary);
  transition: all var(--transition-normal);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.song-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  cursor: pointer;
}

.album-item:hover {
  background: var(--background-hover);
  color: var(--primary);
  transform: translateX(4px);
}

.song-content:hover {
  color: var(--primary);
}

.song-item:hover {
  background: var(--background-hover);
}

.album-item:last-child,
.song-item:last-child {
  border-bottom: none;
}

.album-cover-mini {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  object-fit: cover;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.album-name,
.song-name {
  font-weight: 600;
  color: var(--text-primary);
  flex: 1;
}

.album-date {
  color: var(--text-secondary);
  font-size: 14px;
}

.song-album {
  color: var(--text-secondary);
  font-size: 14px;
}

.song-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  opacity: 0;
  transform: translateX(10px);
  transition: all 0.3s ease;
}

.song-item:hover .song-actions {
  opacity: 1;
  transform: translateX(0);
}

.play-next-btn,
.play-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
}

.play-next-btn {
  background-color: var(--background-card);
  color: var(--text-secondary);
  border: 1px solid var(--border);
}

.play-next-btn:hover {
  background-color: var(--primary);
  color: white;
  border-color: var(--primary);
  transform: scale(1.1);
}

.play-btn {
  background-color: var(--primary);
  color: white;
}

.play-btn:hover {
  background-color: var(--primary-dark);
  transform: scale(1.1);
}

.play-next-icon {
  width: 14px;
  height: 14px;
}

.play-icon-img {
  width: 14px;
  height: 14px;
  filter: brightness(0) invert(1); /* 将SVG变为白色 */
}

.no-content,
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

.no-content i {
  font-size: 48px;
  color: var(--text-secondary);
}

/* 黑色主题下的文字颜色增强 */
[data-theme="black"] .artist-name,
[data-theme="black"] .section-title {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5) !important;
}

/* 黑色主题下的加载文字样式 */
[data-theme="black"] .loading {
  color: #ffffff !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8) !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .detail-page {
    width: 100%;
    padding: 16px;
  }
  
  .artist-detail {
    max-width: 100%;
  }
  
  .artist-header {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }
  
  .artist-avatar {
    width: 120px;
    height: 120px;
  }
  
  .artist-name {
    font-size: 24px;
  }
  
  .album-item,
  .song-item {
    padding: 12px 16px;
  }
}
</style>